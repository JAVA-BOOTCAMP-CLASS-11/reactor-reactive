package aws.reactive;


import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class MonoAndFlux {

    private static final String KEY = MonoAndFlux.class.getSimpleName() + " ->";

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

    private void pruebaMono() {
        logInfo("Prueba MONO");

        Mono<Integer> monoInteger = Mono.just(1);

        monoInteger.subscribe(v -> logInfo("Just -> {}", v));


        Mono.fromCallable(() -> 1)
                .subscribe(v -> logInfo("From Callable -> {}", v));

        Mono.fromSupplier(() -> "HOLA")
                .subscribe(v -> logInfo("From Supplier -> {}", v));

        // Sabemos que un Runnable no recibe parametros NI entrega un resultado
        Mono.fromRunnable(() -> logInfo("Ejecucion de runnable!"))
                .subscribe(i -> logInfo("Received :: {}", i));


        Mono.fromRunnable(() -> {
                    logInfo("Init Ejecucion de runnable2!");
                    int x = 10 / 0;
                    logInfo("Fin Ejecucion de runnable2!");
                })
                .subscribe(
                        i -> logInfo("Received :: {}", i),
                        err -> logInfo("Error :: {}", err.getMessage()),
                        () -> logInfo("Completed Successful"));

    }

    private void pruebaFlux() {
 /*       Flux.just(1, 2, 3, 4, 5)
                .map(i -> i * 2)
                //.delayElements(Duration.ofSeconds(2))
                .subscribe(v -> logInfo("Flux just -> {}", v));


        // Genero 2 subscribers que procesaran a diferente tiempo

        logInfo("---------------------- START -------------------");

        Flux<Integer> fInt = Flux.just(1, 2, 3, 4, 5)
                                 .log()
                                 .delayElements(Duration.ofSeconds(1));

        fInt.subscribe(i -> {
            sleep(1000);
            logInfo("Observer 1 :: {}", i);
        });

        fInt.subscribe(i -> logInfo("Observer 2 :: {}", i));

        sleep(12000);
        logInfo("---------------------- END -------------------");


        Flux.just(1, 2, 3)
                .map(i -> 10 / i)
                .subscribe(
                        i -> logInfo("Received :: {}", i),
                        err -> logInfo("Error :: {}", err),
                        () -> logInfo("Successfully completed"));



        Flux.just(1, 2, 3)
                .map(i -> i / (i - 3))
                .subscribe(
                        i -> logInfo("Received :: {}", i),
                        err -> logInfo("Error :: {}", err.getMessage()),
                        () -> logInfo("Successfully completed"));


        Flux.just(1.0, 2.0, 3.0)
                .map(i -> i / (i - 3.0))
                .subscribe(
                        i -> logInfo("Received :: {}", i),
                        err -> logInfo("Error :: {}", err.getMessage()),
                        () -> logInfo("Successfully completed"));



        String[] arr = {"Hi", "Hello", "How are you"};

        Flux.fromArray(arr)
                .filter(s -> s.length() > 2)
                .subscribe(i -> logInfo("FromArray -> Received : {}", i));

        List<String> list = Arrays.asList(arr);
        Flux.fromIterable(list)
                .map(String::toUpperCase)
                .subscribe(i -> logInfo("FromIterable -> Received : {}", i));

        Flux<Integer> fStream = Flux.fromStream(Stream.of(1, 2, 3, 4, 5));

        fStream.subscribe(i -> logInfo("FromStream -> Receive : {}", i));


        // Pero, ¿que pasa si pongo otro subscriber?
        fStream.subscribe(i -> logInfo("[Subscriber 2] FromStream -> Receive : {}", i));

        sleep(12000);

 */
        Flux<Integer> fStreamSupplier = Flux.fromStream(() -> Stream.of(1, 2, 3, 4, 5));

        fStreamSupplier.subscribe(i -> logInfo("[Observer 1] FromStreamSupplier -> Receive : {}", i));
        fStreamSupplier.subscribe(i -> logInfo("[Observer 2] FromStreamSupplier -> Receive : {}", i));
        fStreamSupplier.subscribe(i -> logInfo("[Observer 3] FromStreamSupplier -> Receive : {}", i));

        Flux.range(10, 5)
                .subscribe(i -> logInfo("Range Receive : {}", i));
        sleep(12000);
    }

    public static void main(String[] args) {
        MonoAndFlux monoAndFlux = new MonoAndFlux();

        //
        //monoAndFlux.pruebaMono();
       monoAndFlux.pruebaFlux();
/*
        Integer variable = 10;

        Mono<Integer> monoInt = Mono.justOrEmpty(variable)
                .filter(v -> v % 2 != 0)
                .map(v -> v * 2)
                .switchIfEmpty(Mono.defer(() -> Mono.fromCallable(() -> {
                    System.out.println("SWITCH IF EMPTY");
                    return 0;
                })));

        monoInt.subscribe(System.out::println);

 */
    }
}
