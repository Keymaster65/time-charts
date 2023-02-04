package io.github.keymaster65.timecharts.model;

import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

import java.time.LocalDateTime;

class EventTest {

    @Example
    void createEvent() {
        Number value = 1;
        final String seriesName = "seriesName";
        final LocalDateTime now = LocalDateTime.now();


        final Event event = new Event(
                seriesName,
                now,
                value

        );

        Assertions.assertThat(event.seriesName()).isEqualTo(seriesName);
        Assertions.assertThat(event.eventTime()).isEqualTo(now);
        Assertions.assertThat(event.value()).isEqualTo(value);
    }

}