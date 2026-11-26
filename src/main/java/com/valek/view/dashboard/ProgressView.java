package com.valek.view.dashboard;

import com.valek.dto.progressdto.DailyXpDto;
import com.valek.dto.progressdto.ProgressSummaryDto;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ProgressView {

    public VBox create(ProgressSummaryDto summary) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20, 30, 20, 30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Label title = new Label("My Progress");
        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        // Левая часть — график
        BarChart<String, Number> weeklyChart = createWeeklyChart(summary);
        weeklyChart.setPrefHeight(350);
        weeklyChart.setMinWidth(400);

        // Правая часть — XP и статистика
        VBox rightPanel = new VBox(15);
        rightPanel.setPrefWidth(450);
        rightPanel.setMinWidth(380);

        VBox xpCard = createXpCard(summary);

        GridPane statistics = new GridPane();
        statistics.setHgap(15);
        statistics.setVgap(15);
        statistics.setAlignment(Pos.CENTER);

        statistics.add(createStatCard("📚", "Completed Lessons",
                        String.valueOf(summary.getCompletedLessons()), "#DCFCE7", "#15803D"),
                0, 0
        );

        statistics.add(
                createStatCard("🎯", "Total Attempts",
                        String.valueOf(summary.getTotalAttempts()), "#FCE7F3", "#BE185D"),
                1, 0
        );

        statistics.add(
                createStatCard("🏆", "Accuracy",
                        String.format("%.1f%%", summary.getAverageAccuracy()), "#EDE9FE", "#6D28D9"),
                0, 1
        );

        statistics.add(
                createStatCard("🔥", "Weekly XP",
                        String.valueOf(
                                summary.getWeeklyActivity().stream()
                                        .mapToInt(DailyXpDto::getXpEarned)
                                        .sum()
                        ), "#FEF3C7", "#B45309"),
                1, 1
        );

        rightPanel.getChildren().addAll(xpCard, statistics);

        // Общая горизонтальная компоновка
        HBox content = new HBox(25);
        content.setAlignment(Pos.CENTER);
        content.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox.setHgrow(weeklyChart, Priority.ALWAYS);

        content.getChildren().addAll(weeklyChart, rightPanel);

        VBox.setVgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(title, content);

        return root;
    }

    private VBox createStatCard(String icon, String name, String value, String backgroundColor, String valueColor) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefSize(190, 115);
        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle(
                "-fx-background-color: " + backgroundColor + ";" +
                        "-fx-background-radius: 15;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 25px;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #64748B;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
                "-fx-font-size: 23px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + valueColor + ";"
        );

        card.getChildren().addAll(iconLabel, nameLabel, valueLabel);

        return card;
    }

    private BarChart<String, Number> createWeeklyChart(ProgressSummaryDto summary) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Day");
        yAxis.setLabel("XP Earned");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Weekly Activity");
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCategoryGap(20);
        chart.setPrefHeight(300);
        chart.setMaxWidth(Double.MAX_VALUE);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        for (DailyXpDto dailyXp : summary.getWeeklyActivity()) {
            String date = dailyXp.getActivityDate().format(formatter);

            series.getData().add(
                    new XYChart.Data<>(date, dailyXp.getXpEarned())
            );
        }

        chart.getData().add(series);

        return chart;
    }

    private VBox createXpCard(ProgressSummaryDto summary) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle(
                "-fx-background-color: linear-gradient(to right, #F59E0B, #F97316);" +
                        "-fx-background-radius: 18;"
        );

        Label title = new Label("⚡ Your Experience");
        title.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: white;"
        );

        Label xpValue = new Label(summary.getTotalXp() + " XP");
        xpValue.setStyle(
                "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );

        Label level = new Label("LEVEL " + summary.getLevel());
        level.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );

        double progress = (double) summary.getXpInCurrentLevel()
                / summary.getXpForNextLevel();

        // Фоновая дорожка
        StackPane progressTrack = new StackPane();
        progressTrack.setMinHeight(13);
        progressTrack.setPrefHeight(13);
        progressTrack.setMaxWidth(Double.MAX_VALUE);

        progressTrack.setStyle(
                "-fx-background-color: rgba(255,255,255,0.35);" +
                        "-fx-background-radius: 10;"
        );

        // Заполненная часть полосы
        Region progressFill = new Region();
        progressFill.setMinWidth(0);
        progressFill.setMaxWidth(Double.MAX_VALUE);
        progressFill.setPrefHeight(13);

        progressFill.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;"
        );

        progressFill.prefWidthProperty().bind(
                progressTrack.widthProperty().multiply(progress)
        );

        StackPane.setAlignment(progressFill, Pos.CENTER_LEFT);
        progressTrack.getChildren().add(progressFill);

        Label progressText = new Label(
                summary.getXpInCurrentLevel() + " / " +
                        summary.getXpForNextLevel() + " XP to next level"
        );

        progressText.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: white;"
        );

        card.getChildren().addAll(
                title, xpValue, level, progressTrack, progressText
        );

        return card;
    }
}