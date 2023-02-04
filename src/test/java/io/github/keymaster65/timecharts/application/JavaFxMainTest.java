package io.github.keymaster65.timecharts.application;


import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

class JavaFxMainTest extends JavaFxMain {

    @Example
    void main() {

        System.setIn(new ByteArrayInputStream(
                """
                        value1 12:33:00 0
                        value1 12:34:00 10
                        value1 12:35:00 20
                        value1 12:40:00 30
                        value2 12:33:10 10
                        value2 12:34:10 5
                        value2 12:35:10 15
                        value2 12:40:10 10
                        """
                        .getBytes(StandardCharsets.UTF_8)));
        Assertions
                .assertThatCode(() -> main(new String[]{"60", "Test", "100"})).doesNotThrowAnyException();

        // give human user 3 seconds to check displayed diagram
        LockSupport.parkNanos(Duration.ofSeconds(3).toNanos());
    }
}