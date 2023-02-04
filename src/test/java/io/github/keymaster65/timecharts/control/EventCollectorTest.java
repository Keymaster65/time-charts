package io.github.keymaster65.timecharts.control;

import io.github.keymaster65.timecharts.application.JavaFxMonitor;
import io.github.keymaster65.timecharts.model.Event;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;

class EventCollectorTest {

    @Example
    void createEventCollector() {
        final Duration bucketDuration = Duration.ofSeconds(1);
        final JavaFxMonitor monitor = Mockito.mock(JavaFxMonitor.class);


        final EventCollector eventCollector = new EventCollector(
                LocalDateTime.now(),
                bucketDuration,
                monitor

        );


        Assertions.assertThat(eventCollector.bucketDuration).isEqualTo(bucketDuration);
        Mockito.verifyNoInteractions(monitor);
    }

    @Example
    void addInitialEvent() {
        final Duration bucketDuration = Duration.ofSeconds(1);
        final JavaFxMonitor monitor = Mockito.mock(JavaFxMonitor.class);
        final LocalDateTime now = LocalDateTime.now();
        final EventCollector eventCollector = new EventCollector(
                now,
                bucketDuration,
                monitor
        );


        final String seriesName = "seriesName";
        eventCollector.add(
                new Event(
                        seriesName,
                        now,
                        1
                )
        );


        Assertions.assertThat(eventCollector.size(seriesName)).isEqualTo(1);
        Assertions.assertThat(eventCollector.size("notExistingSeriesName")).isZero();
        Mockito.verifyNoInteractions(monitor);
    }

    @Example
    void addEventsSameBucket() {
        final Duration bucketDuration = Duration.ofSeconds(1);
        final JavaFxMonitor monitor = Mockito.mock(JavaFxMonitor.class);
        final LocalDateTime now = LocalDateTime.now();
        final EventCollector eventCollector = new EventCollector(
                now,
                bucketDuration,
                monitor

        );
        final String seriesName = "seriesName";


        final Event event = new Event(
                seriesName,
                now,
                1
        );
        eventCollector.add(
                event
        );
        eventCollector.add(
                event
        );


        Assertions.assertThat(eventCollector.size(seriesName)).isEqualTo(2);
        Assertions.assertThat(eventCollector.size("notExistingSeriesName")).isZero();
        Mockito.verifyNoInteractions(monitor);
    }

    @Example
    void addEventsNextBucket() {
        final Duration bucketDuration = Duration.ofSeconds(1);
        final JavaFxMonitor monitor = Mockito.mock(JavaFxMonitor.class);
        final LocalDateTime now = LocalDateTime.now();
        final EventCollector eventCollector = new EventCollector(
                now,
                bucketDuration,
                monitor

        );
        final String seriesName = "seriesName";


        final Event event = new Event(
                seriesName,
                now,
                1
        );
        final Event nextBucketEvent = new Event(
                seriesName,
                now.plusSeconds(1),
                2
        );
        eventCollector.add(
                event
        );
        eventCollector.add(
                nextBucketEvent
        );


        Assertions.assertThat(eventCollector.size(seriesName)).isEqualTo(1);
        Assertions.assertThat(eventCollector.size("notExistingSeriesName")).isZero();
        Mockito.verify(monitor).addBucket(
                Mockito.eq(seriesName),
                Mockito.anyString(),
                Mockito.eq(1F)
        );
        Mockito.verifyNoMoreInteractions(monitor);
    }

    @Example
    void addEventsFutureBucket() {
        final Duration bucketDuration = Duration.ofSeconds(1);
        final JavaFxMonitor monitor = Mockito.mock(JavaFxMonitor.class);
        final LocalDateTime now = LocalDateTime.now();
        final EventCollector eventCollector = new EventCollector(
                now,
                bucketDuration,
                monitor

        );
        final String seriesName = "seriesName";


        final Event event = new Event(
                seriesName,
                now,
                1
        );
        final int seconds = 10;
        final Event futureBucketEvent = new Event(
                seriesName,
                now.plusSeconds(seconds),
                2
        );
        eventCollector.add(
                event
        );
        eventCollector.add(
                futureBucketEvent
        );


        Assertions.assertThat(eventCollector.size(seriesName)).isEqualTo(1);
        Assertions.assertThat(eventCollector.size("notExistingSeriesName")).isZero();
        Mockito.verify(monitor).addBucket(
                Mockito.eq(seriesName),
                Mockito.anyString(),
                Mockito.eq(1F)
        );
        Mockito.verify(monitor, Mockito.times(seconds - 1)).addBucket(
                Mockito.eq(seriesName),
                Mockito.anyString(),
                Mockito.eq(0F)
        );
        Mockito.verifyNoMoreInteractions(monitor);
    }

}