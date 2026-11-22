
package com.valek.view.dashboard;

import com.valek.dto.learningdto.ExerciseDto;
import com.valek.model.learningmodels.Lesson;

import com.valek.service.learningservices.ExerciseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LessonView {

    private final ExerciseService exerciseService;

    public VBox create(Lesson lesson, Runnable onBack, Runnable onStart) {

        // Основной контейнер
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_LEFT);
        root.getStyleClass().add("course-details");

        var css = getClass().getResource("/css/course-details.css");

        if (css == null) {
            throw new IllegalStateException(
                    "Не найден файл /css/course-details.css"
            );
        }

        root.getStylesheets().add(css.toExternalForm());

        // Кнопка возврата
        Button backButton = new Button("←  Назад к курсу");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(event -> onBack.run());

        // Название урока
        Label title = new Label(lesson.getTitle());
        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E293B;"
        );

        // Описание урока
        Label description = new Label(
                lesson.getDescription() != null
                        ? lesson.getDescription()
                        : "Описание урока пока отсутствует."
        );
        description.setWrapText(true);
        description.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );

        // Количество упражнений
        List<ExerciseDto> exercises =
                exerciseService.getExercisesByLesson(lesson.getId());

        int exerciseCount = exercises.size();

        Label exercisesLabel = new Label(
                "Упражнений в уроке: " + exerciseCount
        );
        exercisesLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #475569;"
        );

        // Кнопка начала урока
        Button startButton = new Button("Начать урок");
        startButton.setOnAction(event -> onStart.run());
        startButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 12 25 12 25;" +
                        "-fx-cursor: hand;"
        );

        VBox lessonInfo = new VBox(12);
        lessonInfo.getChildren().addAll(
                title,
                description,
                exercisesLabel,
                startButton
        );

        root.getChildren().addAll(backButton, lessonInfo);

        return root;
    }
}
