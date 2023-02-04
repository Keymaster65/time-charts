package io.github.keymaster65.timecharts.api;

import java.time.Duration;

public final class StdInEventHandlerFactory {
    public static EventHandler create(final Monitor monitor, final Duration bucketDuration) {
        return new StdInEventHandler(
                monitor,
                bucketDuration
        );
    }

    private StdInEventHandlerFactory() {}
}
