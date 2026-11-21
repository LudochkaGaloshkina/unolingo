
package com.valek.view.dashboard;

import com.valek.model.learningmodels.Course;
import com.valek.model.learningmodels.CourseUnit;
import com.valek.model.learningmodels.Lesson;
import com.valek.service.learningservices.CourseUnitService;
import com.valek.service.learningservices.LessonService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseDetailsView {

    private final CourseUnitService courseUnitService;
    private final LessonService lessonService;

    public VBox create(Course course, Runnable onBack) {

        // Основной контейнер
        VBox root = new VBox(22);
        root.setPadding(new Insets(25));
        root.getStyleClass().add("course-details");

        // Подключение CSS
        root.getStylesheets().add(
                getClass()
                        .getResource("/css/course-details.css")
                        .toExternalForm()
        );

        // Кнопка возврата к каталогу курсов
        Button backButton = new Button("←  Все курсы");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(event -> onBack.run());

        // Название курса
        Label title = new Label(course.getTitle());
        title.getStyleClass().add("course-title");

        // Описание курса
        Label description = new Label(course.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add("course-description");

        VBox courseHeader = new VBox(8);
        courseHeader.getChildren().addAll(title, description);

        root.getChildren().addAll(backButton, courseHeader);

        // Получаем разделы курса
        List<CourseUnit> units =
                courseUnitService.getUnitsByCourse(course.getId());

        // Создаем карточку для каждого раздела
        for (CourseUnit unit : units) {

            // Получаем опубликованные уроки раздела
            List<Lesson> lessons =
                    lessonService.getPublishedLessonsByUnit(unit.getId());

            // Контейнер со списком уроков
            VBox lessonList = new VBox(8);
            lessonList.setPadding(new Insets(12, 8, 12, 8));

            if (lessons.isEmpty()) {
                Label emptyLabel = new Label("Уроки пока не добавлены");
                emptyLabel.setStyle(
                        "-fx-text-fill: #94A3B8;" +
                                "-fx-font-size: 13px;"
                );

                lessonList.getChildren().add(emptyLabel);
            }

            // Создаем строки уроков
            int lessonNumber = 1;

            for (Lesson lesson : lessons) {

                HBox lessonRow = new HBox(12);
                lessonRow.setAlignment(Pos.CENTER_LEFT);
                lessonRow.getStyleClass().add("lesson-row");

                // Номер урока
                Label number = new Label(
                        String.format("%02d", lessonNumber++)
                );
                number.getStyleClass().add("lesson-number");

                // Название урока
                Label lessonTitle = new Label(lesson.getTitle());
                lessonTitle.getStyleClass().add("lesson-title");

                lessonRow.getChildren().addAll(number, lessonTitle);

                lessonList.getChildren().add(lessonRow);
            }

            // Название раздела
            Label unitTitle = new Label(
                    unit.getPosition() + ". " + unit.getTitle()
            );
            unitTitle.setStyle(
                    "-fx-font-size: 16px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #1E293B;"
            );

            // Количество уроков
            Label lessonCount = new Label(
                    lessons.size() + " уроков"
            );
            lessonCount.getStyleClass().add("lesson-count");

            // Заголовок карточки
            VBox headerText = new VBox(4);
            headerText.getChildren().addAll(unitTitle, lessonCount);

            HBox header = new HBox(headerText);
            header.setAlignment(Pos.CENTER_LEFT);

            // Раскрывающаяся карточка раздела
            TitledPane unitPane = new TitledPane();

            unitPane.setText("");
            unitPane.setGraphic(header);
            unitPane.setContent(lessonList);
            unitPane.setExpanded(false);
            unitPane.setAnimated(true);
            unitPane.setMaxWidth(Double.MAX_VALUE);

            unitPane.getStyleClass().add("unit-pane");

            root.getChildren().add(unitPane);
        }

        return root;
    }
}
