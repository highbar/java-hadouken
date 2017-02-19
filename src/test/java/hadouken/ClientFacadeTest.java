package hadouken;

import static org.junit.Assert.*;

import java.util.function.Consumer;
import java.util.function.Predicate;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class ClientFacadeTest {
  private final ClientFacade _clientFacade = new SingleMessageClientFacade();

  public class DescribeSetTransformHandler {
    private Consumer<Throwable> _expectedHandler = throwable -> {};

    @Before
    public void before() {
      _clientFacade.setTransformErrorHandler(_expectedHandler);
    }

    @Test
    public void itShouldReturnTheErrorHandler() {
      assertEquals(_expectedHandler, _clientFacade.getTransformErrorHandler());
    }
  }

  public class DescribeSetSimpleMessageFilter {
    private Predicate<SimpleMessage> _expectedFilter = simpleMessage -> true;

    @Before
    public void before() {
      _clientFacade.setSimpleMessageFilter(_expectedFilter);
    }

    @Test
    public void itShouldReturnTheFilter() {
      assertEquals(_expectedFilter, _clientFacade.getSimpleMessageFilter());
    }
  }

  public class DescribeFiltering {
    @Before
    public void before() {
      _clientFacade.setSimpleMessageFilter(simpleMessage -> false);
    }

    @Test
    public void itShouldReturnNoMessages() {
      final int expectedCount = 0;
      final int actualCount = _clientFacade.getMessages().size();

      assertEquals(expectedCount, actualCount);
    }
  }
}
