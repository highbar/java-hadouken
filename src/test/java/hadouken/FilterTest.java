package hadouken;

import static org.junit.Assert.*;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class FilterTest {
  private final SimpleMessage _testInvalidBodyMessage = new SimpleMessage();
  private final SimpleMessage _testValidBodyMessage = new SimpleMessage().setBody("body");

  public class DescribeHasContentFilter {
    private boolean _actualValue;
    private boolean _expectedValue;

    public class WithValidMessageBody {
      @Before
      public void before() {
        _expectedValue = true;
        _actualValue = Filters.HAS_CONTENT.test(_testValidBodyMessage);
      }

      @Test
      public void itShouldReturnTrue() {
        assertEquals(_expectedValue, _actualValue);
      }
    }

    public class WithInvalidMessageBody {
      @Before
      public void before() {
        _expectedValue = false;
        _actualValue = Filters.HAS_CONTENT.test(_testInvalidBodyMessage);
      }

      @Test
      public void itShouldReturnFalse() {
        assertEquals(_expectedValue, _actualValue);
      }
    }
  }

  public class DescribeNoneFilter {
    private boolean _actualValue;
    private boolean _expectedValue;

    public class WithValidMessageBody {
      @Before
      public void before() {
        _expectedValue = true;
        _actualValue = Filters.NONE.test(_testValidBodyMessage);
      }

      @Test
      public void itShouldReturnTrue() {
        assertEquals(_expectedValue, _actualValue);
      }
    }

    public class WithInvalidMessageBody {
      @Before
      public void before() {
        _expectedValue = true;
        _actualValue = Filters.NONE.test(_testInvalidBodyMessage);
      }

      @Test
      public void itShouldReturnTrue() {
        assertEquals(_expectedValue, _actualValue);
      }
    }
  }
}
