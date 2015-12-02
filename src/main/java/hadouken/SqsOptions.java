package hadouken;

import java.util.function.Predicate;

/**
 * Encapsulates options used to connect to SQS.
 */
public class SqsOptions {
  private int _maxMessages;
  private String _queueName;
  private int _visibilityTimeout;
  private int _waitTime;
  private VolatileTransformer _transformer;
  private boolean _autoAcknowledge;
  private Predicate<SimpleMessage> _filter;

  public SqsOptions() {
    _autoAcknowledge = true;
    _maxMessages = 10;
    _visibilityTimeout = 30;
    _waitTime = 0;
  }

  public int getMaxMessages() {
    return _maxMessages;
  }

  public SqsOptions setMaxMessages(int maxMessages) {
    _maxMessages = maxMessages;
    return this;
  }

  public String getQueueName() {
    return _queueName;
  }

  public SqsOptions setQueueName(String queueName) {
    _queueName = queueName;
    return this;
  }

  public int getVisibilityTimeout() {
    return _visibilityTimeout;
  }

  public SqsOptions setVisibilityTimeout(int visibilityTimeout) {
    _visibilityTimeout = visibilityTimeout;
    return this;
  }

  public int getWaitTime() {
    return _waitTime;
  }

  public SqsOptions setWaitTime(int waitTime) {
    _waitTime = waitTime;
    return this;
  }

  public VolatileTransformer getTransformer() {
    return _transformer != null ? _transformer : Transformers.NONE;
  }

  public SqsOptions setTransformer(VolatileTransformer transformer) {
    _transformer = transformer;
    return this;
  }

  public boolean shouldAutoAcknowledge() {
    return _autoAcknowledge;
  }

  public SqsOptions setAutoAcknowledge(boolean autoAcknowledge) {
    _autoAcknowledge = autoAcknowledge;
    return this;
  }

  public Predicate<SimpleMessage> getFilter() {
    return _filter != null ? _filter : Filters.HAS_CONTENT;
  }

  public SqsOptions setFilter(Predicate<SimpleMessage> filter) {
    _filter = filter;
    return this;
  }
}
