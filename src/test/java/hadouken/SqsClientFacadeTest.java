package hadouken;

import static org.mockito.Mockito.*;

import java.util.UUID;
import java.util.function.Consumer;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class SqsClientFacadeTest {
  private static final String _tempQueueName = buildTemporaryQueueName();
  private static final AmazonSQSClient _sqsClient = new AmazonSQSClient();
  private static final SqsOptions _sqsOptions = new SqsOptions().setQueueName(_tempQueueName);
  private static SqsClientFacade _sqsClientFacade;

  private static String buildTemporaryQueueName() {
    Package classPackage = SqsClientFacadeTest.class.getPackage();

    return String.format("test-%s-%s-%s",
      classPackage.getImplementationTitle(),
      classPackage.getImplementationVersion(),
      UUID.randomUUID().toString());
  }

  @BeforeClass
  public static void beforeAll() throws Exception {
    _sqsClient.createQueue(_tempQueueName);

    _sqsClientFacade = new SqsClientFacade(_sqsOptions);
  }

  private Consumer<Throwable> _mockErrorHandler;

  @Before
  @SuppressWarnings("unchecked")
  public void before() {
    _mockErrorHandler = mock(Consumer.class);

    _sqsClientFacade.transform(new Message(), _mockErrorHandler);
  }

  @Test
  public void itShouldCallTheErrorHandlerWhenTransforming() {
    final int expectedInvocationCount = 1;

    verify(_mockErrorHandler, times(expectedInvocationCount)).accept(any());
  }

  @AfterClass
  public static void afterAll() {
    try {
      _sqsClient.deleteQueue(_sqsClient.getQueueUrl(_tempQueueName).getQueueUrl());
    } catch (Exception exception) {
      System.err.println(String.format("Error when deleting queue: %s", exception.getMessage()));
    }
  }
}
