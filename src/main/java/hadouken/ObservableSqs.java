package hadouken;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

/**
 * Exposes an Amazon SQS queue as an observable sequence.
 */
public class ObservableSqs {
  private final ClientFacade _clientFacade;
  private Flowable<SimpleMessage> _messageFlowable;

  public static ObservableSqs fromOptions(final SqsOptions options) {
    Objects.requireNonNull(options);

    return new ObservableSqs(new SqsClientFacade(options));
  }

  public ObservableSqs(final ClientFacade clientFacade) {
    _clientFacade = clientFacade;

    _messageFlowable = Flowable.generate(this::dequeueAndTransmit).flatMapIterable(message -> message);
  }

  public Flowable<SimpleMessage> readMessages() {
    return _messageFlowable;
  }

  private void dequeueAndTransmit(final Emitter<? super List<SimpleMessage>> emitter) {
    emitter.onNext(readSimpleMessageList());
  }

  @SuppressWarnings("unchecked")
  private List<SimpleMessage> readSimpleMessageList() {
    return _clientFacade.getMessages();
  }
}
