package hadouken;

import static org.mockito.Mockito.*;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import io.reactivex.functions.Action;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class SimpleMessageTest {
  private final SimpleMessage _simpleMessage = new SimpleMessage();
  private Action _acknowledgementAction;

  @Before
  public void before() throws Exception {
    _acknowledgementAction = mock(Action.class);

    _simpleMessage.setAcknowledgement(_acknowledgementAction);

    _simpleMessage.acknowledge();
  }

  @Test
  public void itShouldCallAcknowledge() throws Exception {
    final int expectedInvocationCount = 1;

    verify(_acknowledgementAction, times(expectedInvocationCount)).run();
  }
}
