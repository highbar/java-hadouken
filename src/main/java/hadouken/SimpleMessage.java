package hadouken;

import java.io.IOException;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.xml.XmlMapper;

import io.reactivex.functions.Action;

/**
 * A simplified SQS Message.
 */
public class SimpleMessage {
  private static final ObjectMapper _jsonMapper = new ObjectMapper();
  private static final XmlMapper _xmlMapper = new XmlMapper();

  private String _id;
  private String _body;
  private Action _acknowledgement;
  private Message _rawMessage;

  SimpleMessage() {}

  public SimpleMessage(Message message) {
    setId(message.getMessageId());
    setBody(message.getBody());
    setRawMessage(message);
  }

  SimpleMessage(Message message, Action acknowledgementAction) {
    this(message);
    setAcknowledgement(acknowledgementAction);
  }

  public String getId() {
    return _id;
  }

  public SimpleMessage setId(String id) {
    _id = id;

    return this;
  }

  public String getBody() {
    return _body;
  }

  public JsonNode getJsonBody() throws IOException {
    return _jsonMapper.readTree(_body);
  }

  public <Output> Output getJsonBodyAs(Class<Output> outputType) throws IOException {
    return _jsonMapper.readValue(_body, outputType);
  }

  public org.codehaus.jackson.JsonNode getXmlBody() throws IOException {
    // NOTE(justin.morgan): Why do we have two implementations of jackson.JsonNode? o_O
    return _xmlMapper.readTree(_body);
  }

  public <Output> Output getXmlBodyAs(Class<Output> outputType) throws IOException {
    return _xmlMapper.readValue(_body, outputType);
  }

  public SimpleMessage setBody(String body) {
    _body = body;

    return this;
  }

  public SimpleMessage setAcknowledgement(Action acknowledgement) {
    _acknowledgement = acknowledgement;

    return this;
  }

  public void acknowledge() throws Exception {
    if (_acknowledgement != null) {
      _acknowledgement.run();
    }
  }

  public SimpleMessage setRawMessage(Message message) {
    _rawMessage = message;

    return this;
  }

  public Message getRawMessage() {
    return _rawMessage;
  }
}
