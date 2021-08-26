package org.asgard;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

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
  }

  @Test
  public void performingFluxLogicInDifferentThread_NonBlockingMainThread() {

  }
}
