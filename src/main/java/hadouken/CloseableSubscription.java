package hadouken;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;

/**
 * Provides a Reactive Subscription wrapper that can be declared in a try-with-resources block.
 */
public class CloseableSubscription implements Closeable {
  private static final Logger _log = LoggerFactory.getLogger(CloseableSubscription.class);
  private final Subscription _subscription;
  private final ExecutorService _executor;

  public CloseableSubscription(Subscription subscription, ExecutorService executor) {
    _subscription = subscription;
    _executor = executor;
  }

  @Override
  public void close() throws IOException {
    try {
      _subscription.unsubscribe();
      _executor.shutdown();
      _executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException error) {
      _log.error("Thread interrupted while detaching from SQS", error);
    }
  }
}
