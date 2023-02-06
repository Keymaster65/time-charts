package io.github.keymaster65.timecharts.api;

import java.time.Duration;
import java.util.Map;

public record Config(
        Duration bucketDuration,
        Map<String, BucketAggregationMethod> seriesBucketAggregationMethod
) {
    public Config(Duration bucketDuration){
        this(bucketDuration, Map.of());
    }
}