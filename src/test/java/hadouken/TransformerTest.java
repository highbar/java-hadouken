package hadouken;

import static org.junit.Assert.*;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class TransformerTest {
  private final String _testInnerMessage = "message;";
  private final String _testMessage = String.format("{\"Message\": \"%s\"}", _testInnerMessage);

  public class DescribeFromSnsTransformer {
    private String _actualMessage;

    @Before
    public void before() throws Exception {
      _actualMessage = Transformers.FROM_SNS.apply(_testMessage);
    }

    @Test
    public void itShouldReturnTheInnerMessage() {
      assertEquals(_testInnerMessage, _actualMessage);
    }
  }

  public class DescribeNonTransformer {
    private String _actualMessage;

    @Before
    public void before() throws Exception {
      _actualMessage = Transformers.NONE.apply(_testMessage);
    }

    @Test
    public void itShouldReturnTheMessage() {
      assertEquals(_testMessage, _actualMessage);
    }
  }
}
