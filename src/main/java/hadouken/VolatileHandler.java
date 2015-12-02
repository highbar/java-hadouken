package hadouken;

/**
 * Defines a lambda-replaceable message handler that may throw an exception.
 * @param <Input> The input type.
 */
public interface VolatileHandler<Input> {
  void apply(Input input) throws Exception;
}
