package io.github.keymaster65.timecharts.application;

import io.github.keymaster65.timecharts.api.BucketAggregationMethod;
import io.github.keymaster65.timecharts.api.Config;
import io.github.keymaster65.timecharts.api.StdInEventHandlerFactory;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;


public class JavaFxMain extends Application {

    private static final AtomicReference<JavaFxMonitor> monitor = new AtomicReference<>();

    private static final Logger LOG = LoggerFactory.getLogger(JavaFxMain.class);

    private static final AtomicInteger maxY = new AtomicInteger(0);
    private static String title;

    public static void main(String[] args) throws ParseException {
        final CommandLine commandLine = CommandLineFactory.createCommandLine(args);

        if (commandLine.hasOption("--help")) {
            return;
        }

        final int bucketDuration =
                Integer.parseInt(getOption("bd", "4", commandLine));
        title = getOption("t", "time-charts", commandLine);
        final String chartMaxY = getOption("cmy", null, commandLine);
        if (chartMaxY != null) {
            maxY.set(Integer.parseInt(chartMaxY));
        }

        Map<String, BucketAggregationMethod> seriesBucketAggregationMethod =
                createSeriesBucketAggregationMethod(commandLine.getOptionProperties("bam"));

        final Thread javaFxThread = new Thread(() -> Application.launch(args), "JavaFX application");
        javaFxThread.start();

        LOG.debug("Wait for JavaFX to start.");
        while (monitor.get() == null) {
            LockSupport.parkNanos(Duration.ofMillis(100).toNanos());
        }

        StdInEventHandlerFactory.create(
                monitor.get(),
                new Config(
                        Duration.ofSeconds(bucketDuration),
                        seriesBucketAggregationMethod
                )
        ).start();
    }

    private static Map<String, BucketAggregationMethod> createSeriesBucketAggregationMethod(
            final Properties bucketAggregationMethod
    ) {
        Map<String, BucketAggregationMethod> result = new HashMap<>();
        bucketAggregationMethod
                .stringPropertyNames()
                .forEach(name -> result.put(name, BucketAggregationMethod.valueOf(bucketAggregationMethod.getProperty(name))));
        return result;
    }


    @Override
    public void start(final Stage stage) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = getYAxis(maxY.get());

        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        final Scene scene = new Scene(lineChart);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());

        stage.setTitle(title);
        lineChart.setTitle(title);
        monitor.set(new JavaFxMonitor(lineChart));
        stage.setScene(scene);
        stage.show();
    }

    private static String getOption(
            final String optionName,
            final String defaultValue,
            final CommandLine commandLine
    ) {
        final String optionValue = commandLine.getOptionValue(optionName);
        if (optionValue == null || optionValue.isEmpty()) {
            return defaultValue;
        }
        return optionValue;
    }

    private NumberAxis getYAxis(final int maxY) {
        if (maxY != 0) {
            return new NumberAxis(0, maxY, maxY / 10F);
        }
        final NumberAxis numberAxis = new NumberAxis();
        numberAxis.autosize();
        return numberAxis;
    }

}
