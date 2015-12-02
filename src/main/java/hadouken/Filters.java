package hadouken;

import java.util.function.Predicate;

/**
 * Provides common filters.
 */
public class Filters {
  public static final Predicate<SimpleMessage> HAS_CONTENT =
    message -> message.getBody() != null && !message.getBody().isEmpty();
  public static final Predicate<SimpleMessage> NONE = message -> true;
}
