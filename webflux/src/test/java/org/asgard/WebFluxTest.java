package org.asgard;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Duration;
import java.util.function.Consumer;

public class WebFluxTest {

  @Test
  public void generateInfiniteStreamOfStrings() {
    Flux<String> messagesProducer = Flux.generate(sink -> sink.next("hello"));

    messagesProducer.timeout(Duration.ofMillis(1000)).subscribe(System.out::println);
  }

  @Test
  public void handleEventsInStreamUsingErrorAndCompleteCallbacks() {
    Flux<String> messageProducer = Flux.just("This", "is", "java", "world", "dude");
    Flux<String> exceptionFlux = Flux.error(new RuntimeException("error mtfk"));
    messageProducer.concatWith(exceptionFlux)
                   .filter(s -> s.length() > 3)
                   .timeout(Duration.ofMillis(1000))
                   .subscribe(System.out::println, ex -> System.out.println(ex.getMessage()), () -> System.out.println("completed"));

    System.out.println("main thread exit!");
  }

  @Test
  public void performingFluxLogicInDifferentThread_NonBlockingMainThread() {
    Flux<String> messageProducer = Flux.just("This", "is", "java", "world", "dude");
    messageProducer.publishOn(Schedulers.single())
                   .timeout(Duration.ofSeconds(1000))
                   .subscribe(System.out::println, ex -> System.out.println(ex.getMessage()), () -> System.out.println("completed"));

    System.out.println("main thread exit!");
  }

  @Test
  public void createStreamOfBytesFromTheFile() {
    Flux<Byte> messageProducer = Flux.create(fileBytesStreamConsumer());
    messageProducer
//       publishOn performs streaming processing in different thread
        .publishOn(Schedulers.single())
        .subscribe(System.out::println, ex -> System.out.println(ex.getMessage()), () -> System.out.println("completed"));

    System.out.println("End main thread!");
  }

  private Consumer<FluxSink<Byte>> fileBytesStreamConsumer() {
    return sink -> {
      ByteBuffer buffer = ByteBuffer.allocate(24);

      try (RandomAccessFile file = new RandomAccessFile("src/test/resources/test.txt", "r");
           FileChannel channel = file.getChannel()) {

        while (buffer.hasRemaining()) {
          channel.read(buffer);
          buffer.flip();

          for (byte b : buffer.array()) {
            sink.next(b);
          }
        }
        sink.complete();
      } catch (IOException e) {
        sink.error(e);
      }
    };
  }
}
