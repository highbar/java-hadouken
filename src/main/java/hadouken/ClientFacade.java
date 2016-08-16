package hadouken;

import java.util.List;

import com.amazonaws.services.sqs.model.Message;

/**
 * Defines a contract for SQS clients that masks all SDK details except for the Message model itself.
 */
public interface ClientFacade {
  List<Message> getMessages();
  void deleteMessage(Message message);
}
