
package com.valek.view.dashboard;

import com.valek.model.learningmodels.Course;
import com.valek.service.learningservices.CourseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class CourseCatalogView {

    private final CourseService courseService;

    private VBox createCourseCard(
            Course course,
            Consumer<Course> onCourseSelected
    ) {
        VBox card = new VBox(13);

        card.setPrefWidth(320);
        card.setMinWidth(320);
        card.setMaxWidth(320);
        card.setPrefHeight(225);
        card.setPadding(new Insets(20));

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: #D6EAFB;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 1 1 4 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(33,150,243,0.10), 12, 0, 0, 4);"
        );

        // Верхняя часть карточки

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label level = new Label("LEVEL " + course.getLevel());
        level.setStyle(
                "-fx-background-color: #E3F2FD;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 10;" +
                        "-fx-text-fill: #1976D2;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
        );

        Label icon = new Label("📘");
        icon.setStyle("-fx-font-size: 25px;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        top.getChildren().addAll(level, spacer, icon);

        // Название курса

        Label title = new Label(course.getTitle());
        title.setWrapText(true);
        title.setStyle(
                "-fx-font-size: 21px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        // Описание

        Label description = new Label(course.getDescription());
        description.setWrapText(true);
        description.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #647F9F;"
        );

        VBox.setVgrow(description, Priority.ALWAYS);

        // Кнопка

        Button startButton = new Button("Начать обучение →");
        startButton.setMaxWidth(Double.MAX_VALUE);
        startButton.setStyle(
                "-fx-background-color: #2196F3;" +
                        "-fx-background-radius: 12;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: #1976D2;" +
                        "-fx-border-width: 0 0 4 0;" +
                        "-fx-border-radius: 12;"
        );

        startButton.setOnMouseEntered(event ->
                startButton.setStyle(
                        "-fx-background-color: #42A5F5;" +
                                "-fx-background-radius: 12;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 12;" +
                                "-fx-cursor: hand;"
                )
        );

        startButton.setOnMouseExited(event ->
                startButton.setStyle(
                        "-fx-background-color: #2196F3;" +
                                "-fx-background-radius: 12;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 12;" +
                                "-fx-cursor: hand;" +
                                "-fx-border-color: #1976D2;" +
                                "-fx-border-width: 0 0 4 0;" +
                                "-fx-border-radius: 12;"
                )
        );

        startButton.setOnAction(event ->
                onCourseSelected.accept(course)
        );

        card.getChildren().addAll(
                top,
                title,
                description,
                startButton
        );

        return card;
    }

    public VBox create(Consumer<Course> onCourseSelected) {

        VBox root = new VBox(22);
        root.setPadding(new Insets(10));

        // Приветственный блок

        VBox welcomeCard = new VBox(12);
        welcomeCard.setPadding(new Insets(25));

        welcomeCard.setStyle(
                "-fx-background-color: linear-gradient(to right, #BBDEFB, #E3F2FD);" +
                        "-fx-background-radius: 22;"
        );

        Label welcomeLabel = new Label("ТВОЁ ОБУЧЕНИЕ");
        welcomeLabel.setStyle(
                "-fx-text-fill: #1976D2;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
        );

        Label welcomeTitle = new Label(
                "Твой путь к английскому начинается здесь! 🚀"
        );
        welcomeTitle.setWrapText(true);
        welcomeTitle.setStyle(
                "-fx-font-size: 25px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Label welcomeDescription = new Label(
                "Выбирай курсы, изучай новые темы и постепенно повышай свой уровень английского."
        );
        welcomeDescription.setWrapText(true);
        welcomeDescription.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #456B99;"
        );

        welcomeCard.getChildren().addAll(
                welcomeLabel,
                welcomeTitle,
                welcomeDescription
        );

        // Заголовок каталога

        Label catalogTitle = new Label("Выбери свой курс");
        catalogTitle.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Label catalogSubtitle = new Label(
                "Каждый курс — ещё один шаг к свободному английскому."
        );
        catalogSubtitle.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #7893B0;"
        );

        // Карточки курсов

        FlowPane coursesContainer = new FlowPane();
        coursesContainer.setHgap(20);
        coursesContainer.setVgap(20);
        coursesContainer.setAlignment(Pos.TOP_LEFT);
        coursesContainer.setPadding(new Insets(5));

        List<Course> courses = courseService.getPublishedCourses();

        for (Course course : courses) {
            coursesContainer.getChildren().add(
                    createCourseCard(course, onCourseSelected)
            );
        }

        if (courses.isEmpty()) {
            Label emptyLabel = new Label(
                    "Пока нет доступных курсов. Загляни сюда немного позже!"
            );
            emptyLabel.setWrapText(true);
            emptyLabel.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-text-fill: #647F9F;" +
                            "-fx-padding: 20;"
            );

            coursesContainer.getChildren().add(emptyLabel);
        }

        ScrollPane scrollPane = new ScrollPane(coursesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;"
        );

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(
                welcomeCard,
                catalogTitle,
                catalogSubtitle,
                scrollPane
        );

        return root;
    }
}
