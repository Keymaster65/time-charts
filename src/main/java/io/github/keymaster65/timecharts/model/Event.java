package io.github.keymaster65.timecharts.model;

import java.time.LocalDateTime;

public record Event(
        String seriesName,
        LocalDateTime eventTime,
        Number value
) {
}
