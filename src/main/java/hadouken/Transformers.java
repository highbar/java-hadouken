package hadouken;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.functions.Function;

/**
 * Provides common message body transformers.
 */
public class Transformers {
  private static final ObjectMapper _jsonMapper = new ObjectMapper();

  public static final Function<String, String> FROM_SNS = body -> _jsonMapper.readTree(body).get("Message").asText();
  public static final Function<String, String> NONE = body -> body;
}
