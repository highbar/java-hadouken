# Hadouken
> An AWS SQS message processor for Java. Inspired by [Shoryuken] Ruby gem.

![hadouken][img]

![master][master-status]
![dev][dev-status]

Hadouken for Java combines [Reactive Extensions for Java][rx] with the [AWS SDK for Java][aws-sdk] to create an observable sequence of SQS messages. Resulting application code is short and to the point.

- Transform message contents before the handler fires (optional; comes with support for SNS; can be any `String` &rarr; `String` lambda)
- Handle transform and/or handler errors with custom logic
- Automatically acknowledge messages after handler finishes successfully (can be disabled: `options.setAutoAcknowledge(false)`)

### Getting Started

- [Configure your environment for AWS][aws-config]
- Add a dependency to this library: `compile 'com.ica-carealign:hadouken:0.0.0'`

### Usage

Assuming your program's parameters are pulled from environment variables, and your queue is populated through SNS with payloads like `{ "myField": "important data" }`, you can now write something like:

```java
import hadouken.CloseableSubscription;
import hadouken.ObservableSqs;
import hadouken.SimpleMessage;
import hadouken.SqsOptions;
import hadouken.Transformers;

public class Program {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Press Enter / Return to Start");
    scanner.nextLine();

    SqsOptions options = new SqsOptions()
      .setMaxMessages(Integer.parseInt(System.getenv("MAX_MESSAGES")))
      .setQueueName(System.getenv("QUEUE_NAME")) // required
      .setTransformer(Transformers.FROM_SNS)
      .setVisibilityTimeout(Integer.parseInt(System.getenv("VISIBILITY")))
      .setWaitTime(Integer.parseInt(System.getenv("WAIT")));

    ObservableSqs sqs = ObservableSqs.fromOptions(options)
      .setApplicationErrorHandler(error -> {
        CustomLogger.logException(error); // for example
        error.printStackTrace();
      });

    try (CloseableSubscription ignored = sqs.subscribe(Program::handler)) {
      System.out.println("Press Enter / Return to Stop");
      scanner.nextLine();
    } catch (IOException error) {
      error.printStackTrace();
    }
  }

  private static void handler(SimpleMessage message) throws Exception {
    String value = message.getBodyAsJson().get("myField").asText();
    System.out.println(String.format("message received: %s", value));
    System.out.println();

    new Worker(value).run(); // for example
  }
}
```

[aws-config]: http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
[aws-sdk]: https://aws.amazon.com/sdk-for-java/
[dev-status]: https://travis-ci.org/ica-carealign/java-hadouken.svg?branch=dev
[img]: docs/img/hadouken.png
[master-status]: https://travis-ci.org/ica-carealign/java-hadouken.svg?branch=master
[rx]: https://github.com/ReactiveX/RxJava
[Shoryuken]: https://github.com/phstc/shoryuken
