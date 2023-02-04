package io.github.keymaster65.timecharts.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

final class StdInEventHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(StdInEventHandler.class);

    private static final String THREAD_NAME = "EventHandler";
    private final Monitor monitor;
    private final Duration bucketDuration;

    StdInEventHandler(
            final Monitor monitor,
            final Duration bucketDuration
    ) {

        this.monitor = monitor;
        this.bucketDuration = bucketDuration;
    }

    @Override
    public void start() {
        final Thread eventHandlerDeamon = new Thread(
                new io.github.keymaster65.timecharts.control.EventHandler(
                        System.in,
                        monitor,
                        bucketDuration
                ),
                THREAD_NAME
        );
        eventHandlerDeamon.setDaemon(true);
        LOG.info("Starting deamon thread {}.", THREAD_NAME);
        eventHandlerDeamon.start();
    }
}
