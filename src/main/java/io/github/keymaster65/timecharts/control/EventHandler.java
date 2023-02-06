package io.github.keymaster65.timecharts.control;

import io.github.keymaster65.timecharts.api.Config;
import io.github.keymaster65.timecharts.api.Monitor;
import io.github.keymaster65.timecharts.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(EventHandler.class.getName());

    private final InputStream in;

    public final EventCollector eventCollector;

    public EventHandler(
            final InputStream in,
            final Monitor monitor,
            final Config config
    ) {
        this.in = in;
        eventCollector = new EventCollector(
                LocalDateTime.now(),
                config,
                monitor
        );
    }

    @Override
    public void run() {
        LOG.info("Starting");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            for (String line = reader.readLine(); !".".equals(line); line = reader.readLine()) {
                handleString(line);
            }
        } catch (IOException e) {
            LOG.error( "Exception while processing line.", e);
        }


    }

    private void handleString(final String line) {
        if (line == null) {
            LOG.trace( "Ignore line null.");
            return;
        }
        final String[] eventArgs = line.split(" ");
        if (eventArgs.length != 3) {
            LOG.warn( "Ignore line {}.", line);
            return;
        }
        LOG.info( "Handle line {}.", line);

        final String today =
        DateTimeFormatter
                .ofPattern("yyyy-MM-dd ")
                .format(LocalDateTime.now());

        final Event event = new Event(
                eventArgs[0],
                LocalDateTime.parse(today + eventArgs[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                Float.valueOf(eventArgs[2])

        );
        eventCollector.add(event);
    }
}
