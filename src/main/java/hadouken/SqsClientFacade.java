package hadouken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import io.reactivex.functions.Function;

/**
 *
 */
public class SqsClientFacade extends ClientFacade<Message> {
  private final AmazonSQSClient _sqsClient;
  private final ReceiveMessageRequest _receiveRequest;
  private final Function<String, String> _transformer = Transformers.FROM_SNS;
  private final String _queueUrl;

  public SqsClientFacade(final SqsOptions options) {
    _sqsClient = new AmazonSQSClient();
    _queueUrl = _sqsClient.getQueueUrl(options.getQueueName()).getQueueUrl();
    _receiveRequest = buildReceiveRequest(options);

    setSimpleMessageFilter(options.getFilter());
  }

  @Override
  SimpleMessage transform(final Message message, final Consumer<Throwable> errorHandler) {
    Objects.requireNonNull(message);
    Objects.requireNonNull(errorHandler);

    try {
      return new SimpleMessage(message, () -> _sqsClient.deleteMessage(_queueUrl, message.getReceiptHandle()))
        .setBody(_transformer.apply(message.getBody()));
    } catch (Exception error) {
      errorHandler.accept(error);
    }

    return null;
  }

  @Override
  List<Message> getInputs() {
    return Optional.ofNullable(_sqsClient.receiveMessage(_receiveRequest))
      .map(ReceiveMessageResult::getMessages)
      .orElse(new ArrayList<>());
  }

  private ReceiveMessageRequest buildReceiveRequest(final SqsOptions options) {
    Objects.requireNonNull(options);

    final ReceiveMessageRequest request = new ReceiveMessageRequest();

    request.setMaxNumberOfMessages(options.getMaxMessages());
    request.setQueueUrl(_queueUrl);
    request.setVisibilityTimeout(options.getVisibilityTimeout());
    request.setWaitTimeSeconds(options.getWaitTime());

    return request;
  }
}
