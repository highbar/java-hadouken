package hadouken;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Exposes an Amazon SQS queue as an observable sequence.
 */
public class ObservableSqs {
  private static final Logger _log = LoggerFactory.getLogger(ObservableSqs.class);
  private final SqsOptions _options;
  private final SqsProxy _proxy;
  private final ExecutorService _executor;
  private Action1<Exception> _transformErrorHandler;
  private Action1<Exception> _applicationErrorHandler;

  public static ObservableSqs fromOptions(SqsOptions options) {
    ClientFacade client = new SqsClientFacade(options);
    return new ObservableSqs(options, new SqsProxy(options, client), Executors.newCachedThreadPool());
  }

  public ObservableSqs(SqsOptions options, SqsProxy proxy, ExecutorService executor) {
    _options = options;
    _proxy = proxy;
    _executor = executor;
  }

  public Action1<Exception> getTransformErrorHandler() {
    return _transformErrorHandler != null ? _transformErrorHandler : ObservableSqs::defaultTransformErrorHandler;
  }

  public ObservableSqs setTransformErrorHandler(Action1<Exception> onTransformError) {
    _transformErrorHandler = onTransformError;
    return this;
  }

  public Action1<Exception> getApplicationErrorHandler() {
    return _applicationErrorHandler != null ? _applicationErrorHandler : ObservableSqs::defaultApplicationErrorHandler;
  }

  public ObservableSqs setApplicationErrorHandler(Action1<Exception> onApplicationError) {
    _applicationErrorHandler = onApplicationError;
    return this;
  }

  public CloseableSubscription subscribe(VolatileHandler<SimpleMessage> handler) {
    return new CloseableSubscription(Observable.create(new Observable.OnSubscribe<SimpleMessage>() {
      @Override
      public void call(Subscriber<? super SimpleMessage> observer) {
        _executor.execute(() -> dequeueAndTransmit(observer));
      }
    }).subscribe(message -> handleMessage(message, handler)), _executor);
  }

  private void dequeueAndTransmit(Subscriber<? super SimpleMessage> observer) {
    while (!_executor.isShutdown()) {
      _proxy.getMessages(getTransformErrorHandler()).stream().forEach(observer::onNext);
    }
  }

  private void handleMessage(SimpleMessage message, VolatileHandler<SimpleMessage> handler) {
    boolean acknowledge = _options.shouldAutoAcknowledge();

    try {
      handler.apply(message);
    } catch (Exception error) {
      acknowledge = false;
      getApplicationErrorHandler().call(error);
    } finally {
      if (acknowledge) {
        message.acknowledge();
      }
    }
  }

  private static void defaultTransformErrorHandler(Exception error) {
    _log.error("Body transformation error", error);
  }

  private static void defaultApplicationErrorHandler(Exception error) {
    _log.error("Application error", error);
  }
}
