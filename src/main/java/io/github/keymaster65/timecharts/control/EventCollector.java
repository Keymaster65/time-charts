package io.github.keymaster65.timecharts.control;

import io.github.keymaster65.timecharts.api.BucketAggregationMethod;
import io.github.keymaster65.timecharts.api.Config;
import io.github.keymaster65.timecharts.api.Monitor;
import io.github.keymaster65.timecharts.model.Bucket;
import io.github.keymaster65.timecharts.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class EventCollector {

    private static final Logger LOG = LoggerFactory.getLogger(EventCollector.class.getName());
    public final Config config;
    private final Monitor monitor;
    public final LocalDateTime testStartTime;
    public final AtomicReference<LocalDateTime> bucketStartTimeRef = new AtomicReference<>();

    private final Map<String, Bucket> currentBuckets = new ConcurrentHashMap<>();

    public EventCollector(
            final LocalDateTime testStartTime,
            final Config config,
            final Monitor monitor
    ) {
        this.testStartTime = testStartTime;
        this.config = config;
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
            final Bucket newBucket = new Bucket(bucketStartTime, config.bucketDuration());
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
                        getAggregation(
                                bucket,
                                Optional.ofNullable(config.seriesBucketAggregationMethod().get(event.seriesName()))
                        )
                );
                bucket = Bucket.createSuccessor(bucket);
            }
            bucket.add(event);
            currentBuckets.put(event.seriesName(), bucket);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Number getAggregation(final Bucket bucket, final Optional<BucketAggregationMethod> bucketAggregationMethodOptional) {
        if (bucketAggregationMethodOptional.isEmpty()) {
            return bucket.getAverage();
        }
        final BucketAggregationMethod bucketAggregationMethod = bucketAggregationMethodOptional.get();
        //noinspection SwitchStatementWithTooFewBranches
        return switch (bucketAggregationMethod) {
            case COUNT -> bucket.size();
            default -> bucket.getAverage();
        };
    }

    public int size(final String seriesName) {
        final Bucket existingBucket = currentBuckets.get(seriesName);
        if (existingBucket == null) {
            return 0;
        }

        return existingBucket.size();
    }
}
