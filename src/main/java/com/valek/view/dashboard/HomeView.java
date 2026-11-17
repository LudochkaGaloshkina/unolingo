
package com.valek.view.dashboard;

import com.valek.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

@Component
public class HomeView {

    public VBox create(User user) {

        VBox root = new VBox(25);

        // Приветствие
        Label welcome = new Label(
                "Добро пожаловать, " + user.getUsername() + "!"
        );
        welcome.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Label description = new Label(
                "Продолжайте изучать английский язык и достигайте новых целей."
        );
        description.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748b;"
        );

        // Заголовок раздела
        Label section = new Label("Главная");
        section.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        // Карточки статистики
        HBox statistics = new HBox(20);

        VBox levelCard = new VBox(12);
        levelCard.setPadding(new Insets(20));
        levelCard.setPrefWidth(220);
        levelCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;"
        );

        Label levelTitle = new Label("Ваш уровень");
        levelTitle.setStyle(
                "-fx-text-fill: #64748b; -fx-font-size: 15px;"
        );

        Label levelValue = new Label(user.getLevel());
        levelValue.setStyle(
                "-fx-font-size: 21px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        levelCard.getChildren().addAll(levelTitle, levelValue);

        VBox lessonsCard = new VBox(12);
        lessonsCard.setPadding(new Insets(20));
        lessonsCard.setPrefWidth(220);
        lessonsCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;"
        );

        Label lessonsTitle = new Label("Пройдено уроков");
        lessonsTitle.setStyle(
                "-fx-text-fill: #64748b; -fx-font-size: 15px;"
        );

        Label lessonsValue = new Label("0");
        lessonsValue.setStyle(
                "-fx-font-size: 21px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        lessonsCard.getChildren().addAll(lessonsTitle, lessonsValue);

        statistics.getChildren().addAll(levelCard, lessonsCard);

        VBox continueCard = new VBox(12);
        continueCard.setPadding(new Insets(25));
        continueCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;"
        );

        Label continueTitle = new Label("Продолжить обучение");
        continueTitle.setStyle(
                "-fx-font-size: 21px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Label continueDescription = new Label(
                "Здесь будут отображаться доступные уроки."
        );
        continueDescription.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #64748b;"
        );

        continueCard.getChildren().addAll(
                continueTitle,
                continueDescription
        );

        // Собираем главную страницу
        root.getChildren().addAll(
                welcome,
                description,
                section,
                statistics,
                continueCard
        );

        return root;
    }
}
