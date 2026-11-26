
package com.valek.view.dashboard;

import com.valek.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

@Component
public class HomeView {

    public VBox create(User user, long completedLessons) {

        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_LEFT);

        // Приветствие
        HBox welcomeSection = new HBox(20);
        welcomeSection.setAlignment(Pos.CENTER_LEFT);

        VBox welcomeText = new VBox(8);

        Label welcome = new Label(
                "Привет, " + user.getUsername() + "! 👋"
        );
        welcome.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Label description = new Label(
                "Готов продолжить изучение английского?"
        );
        description.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );

        welcomeText.getChildren().addAll(welcome, description);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Image mascotImage = new Image(
                getClass().getResourceAsStream("/images/shark-logo.png")
        );

        ImageView mascot = new ImageView(mascotImage);
        mascot.setFitWidth(110);
        mascot.setFitHeight(110);
        mascot.setPreserveRatio(true);

        welcomeSection.getChildren().addAll(
                welcomeText, spacer, mascot
        );

        // Заголовок статистики
        Label section = new Label("Твоя статистика");
        section.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        // Карточки статистики
        HBox statistics = new HBox(20);
        statistics.setAlignment(Pos.CENTER_LEFT);

        VBox levelCard = createStatCard(
                "🎓",
                "Твой уровень",
                user.getLevel(),
                "#E3F2FD",
                "#1976D2"
        );

        VBox lessonsCard = createStatCard(
                "📚",
                "Пройдено уроков",
                String.valueOf(completedLessons),
                "#E0F7FA",
                "#0097A7"
        );

        HBox.setHgrow(levelCard, Priority.ALWAYS);
        HBox.setHgrow(lessonsCard, Priority.ALWAYS);

        statistics.getChildren().addAll(
                levelCard, lessonsCard
        );

        // Карточка продолжения обучения
        VBox continueCard = createContinueCard();

        root.getChildren().addAll(
                welcomeSection,
                section,
                statistics,
                continueCard
        );

        return root;
    }

    private VBox createStatCard(
            String icon,
            String title,
            String value,
            String backgroundColor,
            String valueColor
    ) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(22));
        card.setPrefHeight(145);
        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle(
                "-fx-background-color: " + backgroundColor + ";" +
                        "-fx-background-radius: 18;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 30px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #64748B;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
                "-fx-font-size: 23px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + valueColor + ";"
        );

        card.getChildren().addAll(
                iconLabel,
                titleLabel,
                valueLabel
        );

        return card;
    }

    private VBox createContinueCard() {

        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle(
                "-fx-background-color: linear-gradient(to right, #42A5F5, #1976D2);" +
                        "-fx-background-radius: 20;"
        );

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox textContent = new VBox(10);

        Label title = new Label("Продолжим обучение?");
        title.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );

        Label description = new Label(
                "Каждый урок приближает тебя к свободному " +
                        "владению английским языком."
        );
        description.setWrapText(true);
        description.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: white;"
        );

        textContent.getChildren().addAll(
                title,
                description
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label rocket = new Label("🚀");
        rocket.setStyle("-fx-font-size: 48px;");

        header.getChildren().addAll(
                textContent,
                spacer,
                rocket
        );

        Label hint = new Label(
                "Твой следующий шаг — выбрать урок!"
        );
        hint.setPadding(new Insets(10, 15, 10, 15));
        hint.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-text-fill: #1976D2;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        card.getChildren().addAll(
                header,
                hint
        );

        return card;
    }
}
