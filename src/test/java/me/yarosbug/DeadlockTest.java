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
                        System.out.println("=====================================");
                        System.out.printf("Start: " + Thread.currentThread() + "%n");
                        System.out.println("=====================================");
                        /* Do nothing */
                        System.out.println("=====================================");
                        System.out.printf("End: " + Thread.currentThread() + "%n");
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