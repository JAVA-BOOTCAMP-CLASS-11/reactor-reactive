package aws.reactive;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.stream.Stream;

@Slf4j
public class ColdVsHot {
    private static final String KEY = ColdVsHot.class.getSimpleName() + " ->";

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

    private int getDataToBePublished(){
        logInfo("getDataToBePublished was called");
        return 1;
    }

    private Stream<String> getMovie(){
        logInfo("Got the movie streaming request");
        return Stream.of(
                "scene 1",
                "scene 2",
                "scene 3",
                "scene 4",
                "scene 5"
        );
    }

    private void publishers() {
        // Un publisher no publica nada hasta tener al menos 1 subscriber
/*
        Mono.fromSupplier(this::getDataToBePublished);

        // Si nos subscribimos
        Mono.fromSupplier(this::getDataToBePublished)
                .subscribe(i -> logInfo("Observer :: {}", i));

        Flux<String> netFlux = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(2));

        netFlux.subscribe(s -> logInfo("[COLD] Observer 1 :: {}", s));

        sleep(5000);

        netFlux.subscribe(s -> logInfo("[COLD] Observer 2 :: {}", s));

        sleep(12000);

        // Como se pudo ver, cada observer vio su pelicula a su tiempo,  se generó una publicación para cada observer.
        // Esto sería COLD Publisher

        logInfo("----------------------------------------------------------------------");

        // Ahora supongamos que estamos en una sala de cine, no todos llegan al mismo momento a ver la pelicula, pero los
        // que llegan tarde no podrán ver todas las escenas desde el principio.  En el modelo de COLD Publisher hay un solo
        // emisor.

        Flux<String> movieCine = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(2))
                .share();

        movieCine.subscribe(s -> logInfo("[HOT] Observer 1 :: {}", s));

        sleep(5000);

        movieCine.subscribe(s -> logInfo("[HOT] Observer 2 :: {}", s));

        sleep(6000);

        logInfo("----------------------------------------------------------------------");

        // Ahora supongamos el caso anterior pero, el observer 2 llega cuando la pelicula ya terminó

        Flux<String> movieCine2 = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(2))
                .share();

        movieCine2.subscribe(s -> logInfo("[HOT - rePublish] Observer 1 :: {}", s));

        sleep(12000);

        movieCine2.subscribe(s -> logInfo("[HOT - rePublish] Observer 2 :: {}", s));

        sleep(12000);
*/
        // Para evitar que el publisher se vuelva a ejecutar podemos utilizar CACHE, de esta manera el observer 2 podrá
        // utilizar el historial de datos emitidos previamente por el publisher

        Flux<String> movieCine3 = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(2))
                .cache();

        movieCine3.subscribe(s -> {
            sleep(1000);
            logInfo("[HOT - cache] Observer 1 :: {}", s);
        });


        movieCine3.subscribe(s -> {
            sleep(500);
            logInfo("[HOT - cache] Observer 2 :: {}", s);
        });

        sleep(16000);
    }

    public static void main(String[] args) {
        ColdVsHot cvh = new ColdVsHot();

        cvh.publishers();
    }
}
