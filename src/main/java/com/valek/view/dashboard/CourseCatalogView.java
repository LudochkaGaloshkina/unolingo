package com.valek.view.dashboard;

import com.valek.model.Course;
import com.valek.service.CourseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseCatalogView {
    private final CourseService courseService;

    private VBox createCourseCard(Course course) {

        VBox card = new VBox(12);
        card.setPrefWidth(310);
        card.setMinHeight(190);
        card.setPrefHeight(190);
        card.setPadding(new Insets(20));

        card.getStyleClass().add("course-card");

        Label level = new Label("LEVEL " + course.getLevel());
        level.getStyleClass().add("course-level");

        Label title = new Label(course.getTitle());
        title.getStyleClass().add("course-title");

        Label description = new Label(course.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add("course-description");

        VBox.setVgrow(description, Priority.ALWAYS);

        Button startButton = new Button("Начать обучение →");
        startButton.setMaxWidth(Double.MAX_VALUE);
        startButton.getStyleClass().add("course-button");

        card.getChildren().addAll(
                level,
                title,
                description,
                startButton
        );

        return card;
    }

    public VBox create() {

        VBox root = new VBox(15);

        Label title = new Label("Доступные курсы");
        title.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Label subtitle = new Label(
                "Выберите курс и начните изучение английского языка."
        );
        subtitle.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #64748b;"
        );

        FlowPane coursesContainer = new FlowPane();
        coursesContainer.setHgap(20);
        coursesContainer.setVgap(20);
        coursesContainer.setPadding(new Insets(10));
        coursesContainer.setAlignment(Pos.TOP_LEFT);

        List<Course> courses = courseService.getPublishedCourses();

        for (Course course : courses) {
            VBox card = createCourseCard(course);
            coursesContainer.getChildren().add(card);
        }

        ScrollPane scrollPane = new ScrollPane(coursesContainer);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;"
        );

        root.getChildren().addAll(
                title,
                subtitle,
                scrollPane
        );

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return root;
    }
}
