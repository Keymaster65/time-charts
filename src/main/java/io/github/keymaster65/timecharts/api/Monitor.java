package io.github.keymaster65.timecharts.api;

public interface Monitor {
    void addBucket(
            final String seriesName,
            final String bucketName,
            final Number value
    );
}
