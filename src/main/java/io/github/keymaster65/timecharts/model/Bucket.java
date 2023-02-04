package io.github.keymaster65.timecharts.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class Bucket {
    private final List<Event> events = new LinkedList<>();

    private final String name ;

    private final LocalDateTime startTimeInclusive;


    private final Duration bucketDuration;

    public Bucket(
            final LocalDateTime startTimeInclusive,
            final Duration bucketDuration
    ) {
        this.name = DateTimeFormatter.ofPattern("HH:mm:ss").format(startTimeInclusive);
        this.startTimeInclusive = startTimeInclusive;
        this.bucketDuration = bucketDuration;
    }

    public static Bucket createSuccessor(final Bucket previousBucket) {
        final LocalDateTime successorStartTime = previousBucket.startTimeInclusive.plus(previousBucket.bucketDuration);
        return new Bucket(
                successorStartTime,
                previousBucket.bucketDuration
        );
    }

    public void add(Event event) {
        synchronized (events) {
            events.add(event);
        }

    }

    public int size() {
        synchronized (events) {
            return events.size();
        }
    }

    public String getName() {
        return this.name;
    }

    public float getAverage() {
        float sum = 0F;
        synchronized (events) {
            if (events.size() == 0L) {
                return sum;
            }
            for (Event event : events) {
                sum = sum + event.value().floatValue();
            }
            return sum / size();
        }
    }

    public boolean isFor(Event event) {
        return event.eventTime().isBefore(startTimeInclusive.plus(bucketDuration))
                &&
                (
                        event.eventTime().isAfter(startTimeInclusive)
                                || event.eventTime().isEqual(startTimeInclusive)
                );
    }

    public boolean isAfter(final Event event) {
        return event.eventTime().isBefore(startTimeInclusive);
    }
}
