package hadouken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SingleMessageClientFacade extends ClientFacade<SimpleMessage> {
  private boolean _hasReturned = false;

  @Override
  protected SimpleMessage transform(SimpleMessage input, Consumer<Throwable> errorHandler) {
    return input;
  }

  @Override
  protected List<SimpleMessage> getInputs() {
    if (!_hasReturned) {
      _hasReturned = true;

      return Collections.singletonList(new SimpleMessage().setBody("Body"));
    }

    return new ArrayList<>();
  }
}
