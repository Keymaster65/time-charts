package io.github.keymaster65.timecharts.model;

import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class BucketTest {

    @Example
    void create() {
        final Bucket bucket = new Bucket(LocalDateTime.now(), Duration.ofSeconds(1));

        Assertions.assertThat(bucket).isNotNull();
    }

    @Example
    void acceptStartTime() {
        final LocalDateTime now = LocalDateTime.now();
        final Bucket bucket = new Bucket(now, Duration.ofSeconds(1));
        final Event event = new Event(
                "ignored",
                now,
                1
        );

        Assertions.assertThat(bucket.isFor(event)).isTrue();
    }

    @Example
    void notAcceptEndTime() {
        final LocalDateTime now = LocalDateTime.now();
        final Bucket bucket = new Bucket(now, Duration.ofSeconds(1));
        final Event event = new Event(
                "ignored",
                now.plusSeconds(1),
                1
        );

        Assertions.assertThat(bucket.isFor(event)).isFalse();
    }

    @Example
    void notAcceptAfterEndTime() {
        final LocalDateTime now = LocalDateTime.now();
        final Bucket bucket = new Bucket(now, Duration.ofSeconds(1));
        final Event event = new Event(
                "ignored",
                now.plusNanos(Duration.ofSeconds(1).toNanos() + 1),
                1
        );

        Assertions.assertThat(bucket.isFor(event)).isFalse();
    }

    @Example
    void notAcceptBeforeStartTime() {
        final LocalDateTime now = LocalDateTime.now();
        final Bucket bucket = new Bucket(now, Duration.ofSeconds(1));
        final Event event = new Event(
                "ignored",
                now.minusNanos(1),
                1
        );

        Assertions.assertThat(bucket.isFor(event)).isFalse();
    }

    @Example
    void getName() {
        final LocalDateTime time = LocalDateTime.parse("2011-12-03T13:59:55", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        final Bucket bucket = new Bucket(time, Duration.ofSeconds(1));

        Assertions.assertThat(bucket.getName()).isEqualTo("13:59:55");
    }

    @Example
    void getAverage() {
        final LocalDateTime now = LocalDateTime.now();
        final Bucket bucket = new Bucket(now, Duration.ofSeconds(1));
        final Event event = new Event(
                "ignored",
                now,
                1
        );
        final Event event2 = new Event(
                "ignored",
                now,
                3
        );
        bucket.add(event);
        bucket.add(event2);

        Assertions.assertThat(bucket.getAverage()).isEqualTo(2F);
    }

    @Example
    void bucketIsAfterEvent() {
        final LocalDateTime now = LocalDateTime.now();
        final Bucket bucket = new Bucket(now, Duration.ofSeconds(1));
        final Event event = new Event(
                "ignored",
                now.minusNanos(1),
                1
        );

        Assertions.assertThat(bucket.isAfter(event)).isTrue();
    }
    @Example
    void bucketIsNotAfterEvent() {
        final LocalDateTime now = LocalDateTime.now();
        final Bucket bucket = new Bucket(now, Duration.ofSeconds(1));
        final Event event = new Event(
                "ignored",
                now,
                1
        );

        Assertions.assertThat(bucket.isAfter(event)).isFalse();
    }
}