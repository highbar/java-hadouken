package hadouken;

import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.sqs.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action1;

/**
 * Proxies access to Amazon SQS.
 */
public class SqsProxy {
  private static final Logger _log = LoggerFactory.getLogger(SqsProxy.class);
  private final SqsOptions _options;
  private final ClientFacade _client;

  public SqsProxy(SqsOptions options, ClientFacade client) {
    _options = options;
    _client = client;
  }

  public List<SimpleMessage> getMessages(Action1<Exception> errorHandler) {
    return _client.getMessages().stream()
      .map(message -> new SimpleMessage(message, _client).setBody(mapBody(message, errorHandler)))
      .filter(this::filterMessage)
      .collect(Collectors.toList());
  }

  private boolean filterMessage(SimpleMessage message) {
    boolean valid = _options.getFilter().test(message);

    if (!valid) {
      _log.warn("Ignoring message {} (filtered)", message.getId());
    }

    return valid;
  }

  private String mapBody(Message message, Action1<Exception> errorHandler) {
    String result = null;

    try {
      result = _options.getTransformer().transform(message.getBody());
    } catch (Exception error) {
      errorHandler.call(error);
    }

    return result;
  }
}
