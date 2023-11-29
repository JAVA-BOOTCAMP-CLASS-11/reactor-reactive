package aws.reactive;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.SynchronousSink;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

@Slf4j
public class CreateVsGenerate {
    private static final String KEY = CreateVsGenerate.class.getSimpleName() + " ->";

    private void logInfo(String message) {
        log.info("{} " + message, KEY);
    }

    private <T> void logInfo(String message, T value) {
        log.info("{} " + message, KEY, value);
    }

    private void sleep(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void create() {
        Flux<Integer> integerFlux = Flux.create((FluxSink<Integer> sink) -> {
            logInfo("Preparando info a emitir");
            sleep(5000);
            List.of(1, 2, 3, 4, 5, 6, 7, 8).forEach(sink::next);
        });

        //First observer. takes 1 ms to process each element
        integerFlux.delayElements(Duration.ofMillis(1)).subscribe(i -> logInfo("First :: {}", i));

        //Second observer. takes 2 ms to process each element
        integerFlux.delayElements(Duration.ofMillis(2)).subscribe(i -> logInfo("Second:: {}", i));

        sleep(1000);

    }

    private void generate() {
        logInfo("-------------------------------------------------------------------");

        AtomicInteger atomicInteger = new AtomicInteger();

        Flux<Integer> integerFlux = Flux.generate((SynchronousSink<Integer> synchronousSink) -> {
            logInfo("Flux generate");
            synchronousSink.next(atomicInteger.getAndIncrement());
        });

        Flux<Integer> integerFlux2 = Flux.generate(() -> 0, (estado, sink) -> {
            logInfo("Flux generate 2");

            sink.next(estado);

            return estado + 1;
        });


        Disposable subscriber = integerFlux2.delayElements(Duration.ofMillis(50))
                                           .subscribe(i -> logInfo("First consumed :: {}", i));

        sleep(1000);

        subscriber.dispose();

        logInfo("-------------------------------------------------------------------");
/*
        Callable<Integer> initialState = () -> 65;

        //BiFunction que consume un estado, genera un nuevo valor y reemplaza el etado
        BiFunction<Integer, SynchronousSink<Character>, Integer> generator = (state, sink) -> {
            char value = (char) state.intValue();
            sink.next(value);
            if (value == 'Z') {
                sink.complete();
            }
            return state + 1;
        };

       Flux<Character> charFlux = Flux.generate(initialState, generator);

        charFlux.delayElements(Duration.ofMillis(50))
                .subscribe(i -> logInfo("Consumed :: {}", i));
*/
        sleep(2000);
    }

    public static void main(String[] args) {
        CreateVsGenerate cvg = new CreateVsGenerate();

        //cvg.create();

        cvg.generate();
    }
}
