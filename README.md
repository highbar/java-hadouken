# Hadouken
> An RxJava-based AWS SQS message observation library. Inspired by [Shoryuken] Ruby gem.

![hadouken][img]

Hadouken for Java combines [Reactive Extensions for Java][rx] with the [AWS SDK for Java][aws-sdk] to create an observable sequence of SQS messages. Resulting application code is short and to the point.

- Transform message contents before the handler fires (comes with support for SNS, but other transformers are possible by extending `ClientFacade`)
- Handle transform and/or handler errors with custom logic

### Getting Started

- [Configure your environment for AWS][aws-config]
- Add a dependency to this library: `compile 'com.highbar:hadouken:x.y.z'`

### Usage

Assuming your program's parameters are pulled from environment variables, and your queue is populated through SNS with payloads like `{ "myField": "important data" }`, you can now write something like:

```java
import hadouken.ObservableSqs;
import hadouken.SimpleMessage;
import hadouken.SqsOptions;
import hadouken.Transformers;
import io.reactivex.disposables.Disposable;

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

    ObservableSqs sqs = ObservableSqs.fromOptions(options);

    Disposable ignored = sqs.readMessages().subscribe(Program::handler)

    try {
      System.out.println("Press Enter / Return to Stop");
      scanner.nextLine();
    } catch (IOException error) {
      error.printStackTrace();
    } finally {
      ignored.dispose();
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
[img]: docs/img/hadouken.png
[rx]: https://github.com/ReactiveX/RxJava
[Shoryuken]: https://github.com/phstc/shoryuken
