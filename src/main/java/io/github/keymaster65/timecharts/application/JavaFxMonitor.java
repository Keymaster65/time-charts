package io.github.keymaster65.timecharts.application;

import io.github.keymaster65.timecharts.api.Monitor;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JavaFxMonitor implements Monitor {
    private final LineChart<String, Number> lineChart;

    private final Map<String, XYChart.Series<String, Number>> seriesMap = new ConcurrentHashMap<>();

    public JavaFxMonitor(final LineChart<String, Number> lineChart) {
        this.lineChart = lineChart;
    }

    public void addBucket(
            final String seriesName,
            final String bucketName,
            final Number value
    ) {
        XYChart.Series<String, Number> series = seriesMap.get(seriesName);
        if (series == null) {
            final XYChart.Series<String, Number> newSeries = new XYChart.Series<>();
            final XYChart.Series<String, Number> putSeries = seriesMap.put(seriesName, newSeries);
            final XYChart.Series<String, Number> useSeries = (
                    putSeries == null
                            ? newSeries
                            : putSeries
            );
            if (putSeries == null) {
                useSeries.setName(seriesName);
                Platform.runLater(() -> lineChart.getData().add(useSeries));
            }
            Platform.runLater(() -> useSeries.getData().add(new XYChart.Data<>(bucketName, value)));
        } else {
            Platform.runLater(() -> series.getData().add(new XYChart.Data<>(bucketName, value)));
        }
    }
}
