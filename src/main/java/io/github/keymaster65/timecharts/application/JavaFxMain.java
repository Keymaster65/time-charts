package io.github.keymaster65.timecharts.application;

import io.github.keymaster65.timecharts.api.StdInEventHandlerFactory;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;


public class JavaFxMain extends Application {

    private static final AtomicReference<JavaFxMonitor> monitor = new AtomicReference<>();

    private static final AtomicInteger maxY = new AtomicInteger(0);
    private static String title;

    public static void main(String[] args) {
        int bucketDuration = 4;
        if (args != null && args.length > 0) {
            if ("-h".equals(args[0]) || "--help".equals(args[0])) {

                System.out.println( // NOSONAR I want STDIO
                        "Usage: time-charts " +
                                "[-h|--help] " +
                                "[bucketDuration ms(default=4)[title(default=Monitor)]] " +
                                "[maxY(default=auto)]"
                );
                System.exit(1);
            }
            bucketDuration = Integer.parseInt(args[0]);
        }

        if (args != null && args.length > 1) {
            title = args[1];
        }

        if (args != null && args.length > 2) {
            maxY.set(Integer.parseInt(args[2]));
        }

        new Thread(() -> Application.launch(args), "javaFX").start();

        while (monitor.get() == null) {
            LockSupport.parkNanos(Duration.ofMillis(100).toNanos());
        }

        StdInEventHandlerFactory.create(
                monitor.get(),
                Duration.ofSeconds(bucketDuration)
        ).start();
    }

    @Override
    public void start(Stage stage) {
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

    private NumberAxis getYAxis(final int maxY) {
        if (maxY != 0) {
            return new NumberAxis(0, maxY, maxY / 10F);
        }
        final NumberAxis numberAxis = new NumberAxis();
        numberAxis.autosize();
        return numberAxis;
    }

}
