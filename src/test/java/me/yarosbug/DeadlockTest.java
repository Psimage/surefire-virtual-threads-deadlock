package me.yarosbug;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

class DeadlockTest {

    @Test
    @EnabledIfSystemProperty(named = "jdk.virtualThreadScheduler.parallelism", matches = "2")
    void deadlockMe() {
        assertTimedOut(Duration.ofSeconds(20), () -> {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < 100_000; i++) {
                    executor.execute(() -> {
                        //For reproducibility, it is crucial to use both `println` and `printf` here
                        //only `println` or only `printf` won't work

                        System.out.println("=====================================");
                        System.out.printf("Hey, ChatGPT, make me a sandwich!\n");
                        System.out.println("=====================================");

                        System.out.println("=====================================");
                        System.out.printf("What? Make it yourself!\n");
                        System.out.println("=====================================");
                    });
                }
            }
        });
    }

    void assertTimedOut(Duration timeout, Runnable runnable) {
        assertThat(CompletableFuture.runAsync(runnable, Executors.newFixedThreadPool(1)))
                .failsWithin(timeout)
                .withThrowableOfType(TimeoutException.class);
    }
}