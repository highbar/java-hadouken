package hadouken;

import static org.junit.Assert.*;

import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class SqsOptionsTest {
  private final SqsOptions _sqsOptions = new SqsOptions();
  private final PrimitiveIterator.OfInt _intStream = new Random().ints(1, Integer.MAX_VALUE).iterator();

  public class DescribeMaxMessages {
    public class WithGoodValue {
      private final int _expectedValue = _intStream.nextInt();

      @Before
      public void before() {
        _sqsOptions.setMaxMessages(_expectedValue);
      }

      @Test
      public void itShouldReturnTheMaxMessages() {
        assertEquals(_expectedValue, _sqsOptions.getMaxMessages());
      }
    }

    public class WithIllegalValue {
      @Test(expected = IllegalArgumentException.class)
      public void itShouldThrowWithAnIllegalValue() {
        _sqsOptions.setMaxMessages(0);
      }
    }
  }

  public class DescribeQueueName {
    private final String _expectedValue = UUID.randomUUID().toString();

    @Before
    public void before() {
      _sqsOptions.setQueueName(_expectedValue);
    }

    @Test
    public void itShouldReturnTheQueueName() {
      assertEquals(_expectedValue, _sqsOptions.getQueueName());
    }
  }

  public class DescribeVisibilityTimeout {
    public class WithValidValue {
      private final int _expectedValue = _intStream.nextInt();

      @Before
      public void before() {
        _sqsOptions.setVisibilityTimeout(_expectedValue);
      }

      @Test
      public void itShouldReturnTheVisibilityTimeout() {
        assertEquals(_expectedValue, _sqsOptions.getVisibilityTimeout());
      }
    }

    public class WithInvalidValue {
      @Test(expected = IllegalArgumentException.class)
      public void itShouldThrowWithAnIllegalValue() {
        _sqsOptions.setVisibilityTimeout(0);
      }
    }
  }

  public class DescribeWaitTime {
    public class WithValidValue {
      private final int _expectedValue = _intStream.nextInt();

      @Before
      public void before() {
        _sqsOptions.setWaitTime(_expectedValue);
      }

      @Test
      public void itShouldReturnTheWaitTime() {
        assertEquals(_expectedValue, _sqsOptions.getWaitTime());
      }
    }

    public class WithInvalidValue {
      @Test(expected = IllegalArgumentException.class)
      public void itShouldThrowWithAnIllegalValue() {
        _sqsOptions.setWaitTime(0);
      }
    }
  }

  public class DescribeFilter {
    private final Predicate<SimpleMessage> _expectedValue = Objects::nonNull;

    @Before
    public void before() {
      _sqsOptions.setFilter(_expectedValue);
    }

    @Test
    public void itShouldReturnTheFilter() {
      assertEquals(_expectedValue, _sqsOptions.getFilter());
    }
  }
}
