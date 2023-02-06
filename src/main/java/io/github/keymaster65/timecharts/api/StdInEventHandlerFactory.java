package io.github.keymaster65.timecharts.api;


public final class StdInEventHandlerFactory {
    public static EventHandler create(final Monitor monitor, final Config config) {
        return new StdInEventHandler(
                monitor,
                config
        );
    }

    private StdInEventHandlerFactory() {}
}
