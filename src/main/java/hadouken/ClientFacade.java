package hadouken;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates retrieving messages from a source and transforming them into {@link SimpleMessage} objects.
 *
 * @param <T> The pre-transformation type.
 */
public abstract class ClientFacade<T> {
  private static final Logger _log = LoggerFactory.getLogger(ClientFacade.class);

  private Predicate<SimpleMessage> _filter = Filters.NONE;
  private Consumer<Throwable> _transformErrorHandler;

  private static void defaultTransformErrorHandler(final Throwable error) {
    _log.error("Body transformation error", error);
  }

  public List<SimpleMessage> getMessages() {
    return getInputs().stream()
      .map(input -> transform(input, getTransformErrorHandler()))
      .filter(this::filterMessage)
      .collect(Collectors.toList());
  }

  public Consumer<Throwable> getTransformErrorHandler() {
    return Optional.ofNullable(_transformErrorHandler)
      .orElse(ClientFacade::defaultTransformErrorHandler);
  }

  public ClientFacade<T> setTransformErrorHandler(final Consumer<Throwable> transformErrorHandler) {
    Objects.requireNonNull(transformErrorHandler);

    _transformErrorHandler = transformErrorHandler;

    return this;
  }

  public Predicate<SimpleMessage> getSimpleMessageFilter() {
    return _filter;
  }

  public ClientFacade<T> setSimpleMessageFilter(final Predicate<SimpleMessage> filter) {
    Objects.requireNonNull(filter);

    _filter = filter;

    return this;
  }

  private boolean filterMessage(final SimpleMessage message) {
    boolean valid = _filter.test(message);

    if (!valid) {
      _log.warn("Ignoring message {} (filtered)", message.getId());
    }

    return valid;
  }

  abstract SimpleMessage transform(final T input, final Consumer<Throwable> errorHandler);
  abstract List<T> getInputs();
}
