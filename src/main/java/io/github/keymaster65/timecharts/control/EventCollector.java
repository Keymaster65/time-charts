package io.github.keymaster65.timecharts.control;

import io.github.keymaster65.timecharts.api.Monitor;
import io.github.keymaster65.timecharts.model.Bucket;
import io.github.keymaster65.timecharts.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class EventCollector {

    private static final Logger LOG = LoggerFactory.getLogger(EventCollector.class.getName());
    public final Duration bucketDuration;
    private final Monitor monitor;
    public final LocalDateTime testStartTime;
    public final AtomicReference<LocalDateTime> bucketStartTimeRef = new AtomicReference<>();

    private final Map<String, Bucket> currentBuckets = new ConcurrentHashMap<>();

    public EventCollector(
            final LocalDateTime testStartTime,
            final Duration bucketDuration,
            final Monitor monitor
    ) {
        this.testStartTime = testStartTime;
        this.bucketDuration = bucketDuration;
        this.monitor = monitor;
    }

    public void add(
            final Event event
    ) {
        final Bucket bucket = currentBuckets.get(event.seriesName());
        if (bucket == null) {
            LocalDateTime bucketStartTime = this.bucketStartTimeRef.get();
            if (bucketStartTime == null) {
                this.bucketStartTimeRef.compareAndSet(null, event.eventTime());
                bucketStartTime = this.bucketStartTimeRef.get();
                LOG.info("Use bucketStartTime {}.", bucketStartTime);
            }
            final Bucket newBucket = new Bucket(bucketStartTime, bucketDuration);
            final Bucket concurrentBucket = currentBuckets.putIfAbsent(event.seriesName(), newBucket);
            final Bucket useBucket =
                    concurrentBucket == null
                            ? newBucket
                            : concurrentBucket;
            handleEvent(event, useBucket);
        } else {
            handleEvent(event, bucket);
        }
    }

    private void handleEvent(final Event event, final Bucket useBucket) {
        if (
                useBucket.isFor(event)
        ) {
            useBucket.add(event);
        } else if (useBucket.isAfter(event)) {
            LOG.warn("Ingore old event {}", event);
        } else {
            Bucket bucket = useBucket;
            while (!bucket.isFor(event)) {
                monitor.addBucket(
                        event.seriesName(),
                        bucket.getName(),
                        bucket.getAverage()
                );
                bucket = Bucket.createSuccessor(bucket);
            }
            bucket.add(event);
            currentBuckets.put(event.seriesName(), bucket);
        }
    }

    public int size(final String seriesName) {
        final Bucket existingBucket = currentBuckets.get(seriesName);
        if (existingBucket == null) {
            return 0;
        }

        return existingBucket.size();
    }
}
