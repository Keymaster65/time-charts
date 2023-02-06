package io.github.keymaster65.timecharts.application;


import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

class JavaFxMainTest extends JavaFxMain {

    public static final byte[] TEST_EVENTS =
            """
                    value1 12:33:00 0
                    value1 12:34:00 10
                    value1 12:35:00 20
                    value1 12:40:00 30
                    value2 12:33:10 10
                    value2 12:34:10 5
                    value2 12:35:10 15
                    value2 12:40:10 10
                    count 12:33:10 1000
                    count 12:33:10 1000
                    count 12:33:10 1000
                    count 12:33:10 1000
                    count 12:33:10 0
                    count 12:34:10 5
                    count 12:35:10 15
                    count 12:40:10 10
                    """
                    .getBytes(StandardCharsets.UTF_8);

    @Example
    void mainOptions() {
        System.setIn(new ByteArrayInputStream((TEST_EVENTS)));
        Assertions
                .assertThatCode(() -> main(new String[]{
                        "-bd", "60",
                        "-title", "Test with options",
                        "-cmy", "100",
                        "-bam", "count=COUNT", "value2=AVERAGE"
                }))
                .doesNotThrowAnyException();


        // give human user 3 seconds to check displayed diagram
        LockSupport.parkNanos(Duration.ofSeconds(3).toNanos());
    }

    @Example
    void help() {
        Assertions
                .assertThatCode(() -> main(new String[]{
                        "--help"
                }))
                .doesNotThrowAnyException();
    }
    @Example
    void defaults() {
        System.setIn(new ByteArrayInputStream(TEST_EVENTS));
        Assertions
                .assertThatCode(() -> main(new String[]{}))
                .doesNotThrowAnyException();

        // give human user 3 seconds to check displayed diagram
        LockSupport.parkNanos(Duration.ofSeconds(3).toNanos());
    }
}