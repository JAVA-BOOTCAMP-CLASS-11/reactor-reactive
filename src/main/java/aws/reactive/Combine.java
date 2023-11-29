package aws.reactive;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class Combine {
    private static final String KEY = Combine.class.getSimpleName() + " ->";

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

    private void combine() {
        Sinks.Many<String> source1 = Sinks.many().multicast().directBestEffort();
        Sinks.Many<String> source2 = Sinks.many().multicast().directBestEffort();

        //emitting data through source 1
        Runnable r1 = () -> {
            for (int i = 0; i < 5; i++) {
                int idle = ThreadLocalRandom.current().nextInt(100, 1000);
                sleep(idle);
                source1.emitNext("source1 - " + i, Sinks.EmitFailureHandler.FAIL_FAST);
            }
            source1.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);
        };

        //emitting data through source 2
        Runnable r2 = () -> {
            for (int i = 0; i < 10; i++) {
                int idle = ThreadLocalRandom.current().nextInt(100, 1000);
                sleep(idle);
                source2.emitNext("source2 - " + i, Sinks.EmitFailureHandler.FAIL_FAST);
            }
            source2.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);
        };

        //lets run via in separate threads
        new Thread(r1).start();
        new Thread(r2).start();

        Flux<String> flux1 = source1.asFlux();
        Flux<String> flux2 = source2.asFlux();

        /*
        flux1.subscribe(this::logInfo);
        flux2.subscribe(this::logInfo);
        */

        /*
        Flux.concat(flux1, flux2).subscribe(conc -> logInfo("Concat :: {}", conc));
        */

        /*
        Flux.combineLatest(flux1, flux2, (a, b) -> a + " :: " + b)
                .subscribe(latest -> logInfo("Conbine Latest -> {}", latest));
        */

        /*
        Flux.merge(flux1, flux2)
                .subscribe(merge -> logInfo("Merge :: {}", merge));
        */

        Flux.zip(flux1, flux2)
                .subscribe(t -> logInfo("[" + t.getT1() + " - " + t.getT2() + "]"));
    }

    public static void main(String[] args) {
        Combine c = new Combine();

        c.combine();
    }
}
