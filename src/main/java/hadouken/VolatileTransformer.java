package hadouken;

/**
 * Defines a lambda-replaceable String transformer that may throw an exception.
 */
public interface VolatileTransformer {
  String transform(String content) throws Exception;
}
