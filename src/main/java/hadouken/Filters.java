package hadouken;

import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

/**
 * Provides common filters.
 */
public class Filters {
  public static final Predicate<SimpleMessage> HAS_CONTENT = message -> Optional.ofNullable(message)
    .map(SimpleMessage::getBody)
    .map(StringUtils::isNotBlank)
    .orElse(false);
  public static final Predicate<SimpleMessage> NONE = message -> true;
}
