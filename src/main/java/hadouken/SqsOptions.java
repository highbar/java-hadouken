package hadouken;

import java.util.Optional;
import java.util.function.Predicate;

import io.reactivex.functions.Function;

/**
 * Encapsulates options used to connect to SQS.
 */
public class SqsOptions {
  private int _maxMessages;
  private int _visibilityTimeout;
  private int _waitTime;
  private Predicate<SimpleMessage> _filter;
  private String _queueName;

  public SqsOptions() {
    _maxMessages = 10;
    _visibilityTimeout = 30;
    _waitTime = 0;
  }

  public int getMaxMessages() {
    return _maxMessages;
  }

  public SqsOptions setMaxMessages(final int maxMessages) {
    if (maxMessages < 1) {
      throw new IllegalArgumentException("maxMessages must be greater than zero");
    }

    _maxMessages = maxMessages;

    return this;
  }

  public String getQueueName() {
    return _queueName;
  }

  public SqsOptions setQueueName(final String queueName) {
    _queueName = queueName;

    return this;
  }

  public int getVisibilityTimeout() {
    return _visibilityTimeout;
  }

  public SqsOptions setVisibilityTimeout(final int visibilityTimeout) {
    if (visibilityTimeout <= 0) {
      throw new IllegalArgumentException("visibilityTimeout must be greater than zero");
    }

    _visibilityTimeout = visibilityTimeout;

    return this;
  }

  public int getWaitTime() {
    return _waitTime;
  }

  public SqsOptions setWaitTime(final int waitTime) {
    if (waitTime <= 0) {
      throw new IllegalArgumentException("waitTime must be greater than zero");
    }

    _waitTime = waitTime;

    return this;
  }

  public Predicate<SimpleMessage> getFilter() {
    return Optional.ofNullable(_filter)
      .orElse(Filters.HAS_CONTENT);
  }

  public SqsOptions setFilter(final Predicate<SimpleMessage> filter) {
    _filter = filter;

    return this;
  }
}
