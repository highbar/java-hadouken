package hadouken;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides common message body transformers.
 */
public class Transformers {
  private static final ObjectMapper _jsonMapper = new ObjectMapper();

  public static final VolatileTransformer FROM_SNS = body -> _jsonMapper.readTree(body).get("Message").asText();
  public static final VolatileTransformer NONE = body -> body;
}
