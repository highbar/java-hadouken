package hadouken;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

/**
 * Provides the default implementation of ClientFacade.
 */
public class SqsClientFacade implements ClientFacade {
  private final SqsOptions _options;
  private final String _queueUrl;
  private final ReceiveMessageRequest _receiveRequest;
  private final AmazonSQSClient _client;

  public SqsClientFacade(SqsOptions options) {
    _options = options;
    _client = new AmazonSQSClient();
    _queueUrl = _client.getQueueUrl(_options.getQueueName()).getQueueUrl();
    _receiveRequest = buildReceiveRequest();
  }

  @Override
  public List<Message> getMessages() {
    ReceiveMessageResult result = _client.receiveMessage(_receiveRequest);
    return result != null ? result.getMessages() : new ArrayList<>();
  }

  @Override
  public void deleteMessage(Message message) {
    _client.deleteMessage(_queueUrl, message.getReceiptHandle());
  }

  private ReceiveMessageRequest buildReceiveRequest() {
    ReceiveMessageRequest request = new ReceiveMessageRequest();
    request.setMaxNumberOfMessages(_options.getMaxMessages());
    request.setQueueUrl(_queueUrl);
    request.setVisibilityTimeout(_options.getVisibilityTimeout());
    request.setWaitTimeSeconds(_options.getWaitTime());

    return request;
  }
}
