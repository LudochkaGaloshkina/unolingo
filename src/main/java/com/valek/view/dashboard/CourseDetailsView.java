
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class CourseDetailsView {

    private final CourseUnitService courseUnitService;
    private final LessonService lessonService;

    public VBox create(
            Course course,
            Set<Long> completedLessonIds,
            Runnable onBack,
            Consumer<Lesson> onLessonSelected
    ) {

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F8FF;");

        // Кнопка возврата

        Button backButton = new Button("←  Все курсы");

        backButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #1976D2;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 5 0 5 0;"
        );

        backButton.setOnAction(event -> onBack.run());

        // Получение разделов и уроков

        List<CourseUnit> units =
                courseUnitService.getUnitsByCourse(course.getId());

        Map<Long, List<Lesson>> lessonsByUnit = new HashMap<>();
        Map<Long, Boolean> unlockedLessonIds = new HashMap<>();

        List<Lesson> allLessons = new ArrayList<>();

        int completedLessons = 0;

        for (CourseUnit unit : units) {

            List<Lesson> lessons =
                    lessonService.getPublishedLessonsByUnit(unit.getId());

            lessonsByUnit.put(unit.getId(), lessons);

            allLessons.addAll(lessons);

            for (Lesson lesson : lessons) {

                if (completedLessonIds.contains(lesson.getId())) {
                    completedLessons++;
                }
            }
        }

        int totalLessons = allLessons.size();

        // Определение доступности уроков

        boolean previousLessonsCompleted = true;

        for (Lesson lesson : allLessons) {

            boolean isCompleted =
                    completedLessonIds.contains(lesson.getId());

            boolean isUnlocked =
                    previousLessonsCompleted || isCompleted;

            unlockedLessonIds.put(lesson.getId(), isUnlocked);

            previousLessonsCompleted =
                    previousLessonsCompleted && isCompleted;
        }

        // Расчёт прогресса

        double progress = totalLessons == 0
                ? 0
                : (double) completedLessons / totalLessons;

        int progressPercent = (int) Math.round(progress * 100);

        // Шапка курса

        VBox header = new VBox(12);
        header.setPadding(new Insets(25));

        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #BBDEFB, #E3F2FD);" +
                        "-fx-background-radius: 22;"
        );

        Label level = new Label(
                "LEARNING PATH  •  LEVEL " + course.getLevel()
        );

        level.setStyle(
                "-fx-text-fill: #1976D2;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
        );

        Label title = new Label(course.getTitle());
        title.setWrapText(true);

        title.setStyle(
                "-fx-font-size: 27px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Label description = new Label(course.getDescription());
        description.setWrapText(true);

        description.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #456B99;"
        );

        // Информация о прогрессе

        HBox progressHeader = new HBox();
        progressHeader.setAlignment(Pos.CENTER_LEFT);

        Label progressTitle = new Label("Твой прогресс");

        progressTitle.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label progressValue = new Label(progressPercent + "%");

        progressValue.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1976D2;"
        );

        progressHeader.getChildren().addAll(
                progressTitle,
                spacer,
                progressValue
        );

        ProgressBar progressBar = new ProgressBar(progress);

        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(12);

        progressBar.setStyle("-fx-accent: #00BCD4;");

        Label lessonStatistics = new Label(
                completedLessons + " из " + totalLessons +
                        " уроков завершено"
        );

        lessonStatistics.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #456B99;"
        );

        header.getChildren().addAll(
                level,
                title,
                description,
                progressHeader,
                progressBar,
                lessonStatistics
        );

        // Заголовок карты

        VBox mapHeader = new VBox(5);

        Label mapTitle = new Label("Твой путь обучения");

        mapTitle.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Label mapSubtitle = new Label(
                "Завершай уроки, открывай новые уровни и двигайся вперёд!"
        );

        mapSubtitle.setWrapText(true);

        mapSubtitle.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #7893B0;"
        );

        mapHeader.getChildren().addAll(
                mapTitle,
                mapSubtitle
        );

        // Контейнер разделов

        VBox unitsContainer = new VBox(25);
        unitsContainer.setPadding(new Insets(5, 5, 20, 5));

        if (units.isEmpty()) {

            Label emptyLabel = new Label(
                    "В этом курсе пока нет учебных разделов."
            );

            emptyLabel.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-text-fill: #7893B0;" +
                            "-fx-padding: 20;"
            );

            unitsContainer.getChildren().add(emptyLabel);
        }

        // Создание разделов

        for (CourseUnit unit : units) {

            List<Lesson> lessons = lessonsByUnit.get(unit.getId());

            VBox unitCard = createUnitCard(
                    unit,
                    lessons,
                    completedLessonIds,
                    unlockedLessonIds,
                    onLessonSelected
            );

            unitsContainer.getChildren().add(unitCard);
        }

        // Прокручиваемая область

        ScrollPane scrollPane = new ScrollPane(unitsContainer);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;" +
                        "-fx-border-color: transparent;"
        );

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(
                backButton,
                header,
                mapHeader,
                scrollPane
        );

        return root;
    }

    // Создание карточки раздела

    private VBox createUnitCard(
            CourseUnit unit,
            List<Lesson> lessons,
            Set<Long> completedLessonIds,
            Map<Long, Boolean> unlockedLessonIds,
            Consumer<Lesson> onLessonSelected
    ) {

        VBox unitCard = new VBox(18);
        unitCard.setPadding(new Insets(20));

        unitCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: #D6EAFB;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 1;"
        );

        // Заголовок раздела

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox headerText = new VBox(5);

        Label unitNumber = new Label(
                "РАЗДЕЛ " + unit.getPosition()
        );

        unitNumber.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2196F3;"
        );

        Label unitTitle = new Label(unit.getTitle());
        unitTitle.setWrapText(true);

        unitTitle.setStyle(
                "-fx-font-size: 19px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        long completedInUnit = lessons.stream()
                .filter(lesson -> completedLessonIds.contains(lesson.getId()))
                .count();

        Label lessonCount = new Label(
                completedInUnit + " / " + lessons.size() + " уроков"
        );

        lessonCount.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #7893B0;"
        );

        headerText.getChildren().addAll(
                unitNumber,
                unitTitle,
                lessonCount
        );

        header.getChildren().add(headerText);

        unitCard.getChildren().add(header);

        // Дорожка уроков

        VBox lessonPath = new VBox(0);
        lessonPath.setPadding(new Insets(5, 0, 5, 0));

        if (lessons.isEmpty()) {

            Label emptyLabel = new Label(
                    "Уроки пока не добавлены"
            );

            emptyLabel.setStyle(
                    "-fx-font-size: 13px;" +
                            "-fx-text-fill: #94A3B8;" +
                            "-fx-padding: 10 0 5 0;"
            );

            lessonPath.getChildren().add(emptyLabel);

        } else {

            for (int i = 0; i < lessons.size(); i++) {

                Lesson lesson = lessons.get(i);

                boolean isLast = i == lessons.size() - 1;

                boolean isCompleted =
                        completedLessonIds.contains(lesson.getId());

                boolean isUnlocked =
                        unlockedLessonIds.getOrDefault(lesson.getId(), false);

                HBox lessonRow = createLessonRow(
                        lesson,
                        i + 1,
                        !isLast,
                        isCompleted,
                        isUnlocked,
                        onLessonSelected
                );

                lessonPath.getChildren().add(lessonRow);
            }
        }

        unitCard.getChildren().add(lessonPath);

        return unitCard;
    }

    // Создание отдельного урока на дорожке

    private HBox createLessonRow(
            Lesson lesson,
            int lessonNumber,
            boolean hasNext,
            boolean isCompleted,
            boolean isUnlocked,
            Consumer<Lesson> onLessonSelected
    ) {

        HBox row = new HBox(15);
        row.setAlignment(Pos.TOP_LEFT);

        // Левая часть: номер урока и соединительная линия

        VBox pathColumn = new VBox(0);
        pathColumn.setAlignment(Pos.TOP_CENTER);
        pathColumn.setMinWidth(48);
        pathColumn.setPrefWidth(48);
        pathColumn.setMaxWidth(48);

        String buttonText;

        if (isCompleted) {
            buttonText = "✓";
        } else if (isUnlocked) {
            buttonText = String.valueOf(lessonNumber);
        } else {
            buttonText = "🔒";
        }

        Button numberButton = new Button(buttonText);

        numberButton.setMinSize(44, 44);
        numberButton.setPrefSize(44, 44);
        numberButton.setMaxSize(44, 44);

        String circleColor;
        String borderColor;

        if (isCompleted) {
            circleColor = "#26B99A";
            borderColor = "#159B80";
        } else if (isUnlocked) {
            circleColor = "#2196F3";
            borderColor = "#1976D2";
        } else {
            circleColor = "#B0BEC5";
            borderColor = "#90A4AE";
        }

        numberButton.setStyle(
                "-fx-background-color: " + circleColor + ";" +
                        "-fx-background-radius: 50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: " + (isUnlocked ? "hand" : "default") + ";" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-radius: 50;" +
                        "-fx-border-width: 0 0 4 0;"
        );

        numberButton.setDisable(!isUnlocked);

        numberButton.setOnAction(event ->
                onLessonSelected.accept(lesson)
        );

        pathColumn.getChildren().add(numberButton);

        if (hasNext) {

            Region connector = new Region();

            connector.setMinSize(4, 28);
            connector.setPrefSize(4, 28);
            connector.setMaxSize(4, 28);

            connector.setStyle(
                    "-fx-background-color: " +
                            (isCompleted ? "#80CBC4" : "#CFD8DC") + ";" +
                            "-fx-background-radius: 5;"
            );

            pathColumn.getChildren().add(connector);
        }

        // Правая часть: информация об уроке

        VBox lessonInfo = new VBox(7);

        Label lessonTitle = new Label(lesson.getTitle());
        lessonTitle.setWrapText(true);

        lessonTitle.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " +
                        (isUnlocked ? "#163779" : "#90A4AE") + ";"
        );

        String descriptionText;

        if (isCompleted) {
            descriptionText = "✓ Урок завершён";
        } else if (isUnlocked) {
            descriptionText = "Урок " + lessonNumber;
        } else {
            descriptionText = "Сначала заверши предыдущий урок";
        }

        Label lessonDescription = new Label(descriptionText);

        lessonDescription.setWrapText(true);

        lessonDescription.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: " +
                        (isCompleted ? "#159B80" :
                                isUnlocked ? "#7893B0" : "#90A4AE") + ";"
        );

        String startButtonText;

        if (isCompleted) {
            startButtonText = "Повторить ↻";
        } else if (isUnlocked) {
            startButtonText = "Начать →";
        } else {
            startButtonText = "Заблокировано";
        }

        Button startButton = new Button(startButtonText);

        startButton.setDisable(!isUnlocked);

        startButton.setStyle(
                "-fx-background-color: " +
                        (isCompleted ? "#E0F5EF" :
                                isUnlocked ? "#E3F2FD" : "#ECEFF1") + ";" +
                        "-fx-background-radius: 9;" +
                        "-fx-text-fill: " +
                        (isCompleted ? "#159B80" :
                                isUnlocked ? "#1976D2" : "#90A4AE") + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 7 13;" +
                        "-fx-cursor: " + (isUnlocked ? "hand" : "default") + ";"
        );

        startButton.setOnAction(event ->
                onLessonSelected.accept(lesson)
        );

        lessonInfo.getChildren().addAll(
                lessonTitle,
                lessonDescription,
                startButton
        );

        HBox.setHgrow(lessonInfo, Priority.ALWAYS);

        // Карточка урока

        VBox lessonCard = new VBox(5);
        lessonCard.setPadding(new Insets(13));
        lessonCard.setMaxWidth(Double.MAX_VALUE);

        String cardBackground;
        String cardBorder;

        if (isCompleted) {
            cardBackground = "#F0FAF7";
            cardBorder = "#B8E8D9";
        } else if (isUnlocked) {
            cardBackground = "#F5FAFF";
            cardBorder = "#E3F2FD";
        } else {
            cardBackground = "#F5F6F7";
            cardBorder = "#E0E4E7";
        }

        lessonCard.setStyle(
                "-fx-background-color: " + cardBackground + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + cardBorder + ";" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;"
        );

        lessonCard.getChildren().add(lessonInfo);

        HBox.setHgrow(lessonCard, Priority.ALWAYS);

        row.getChildren().addAll(
                pathColumn,
                lessonCard
        );

        return row;
    }
}
