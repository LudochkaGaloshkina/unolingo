
package com.valek.view.dashboard;

import com.valek.dto.learningdto.ExerciseCatalogDto;
import com.valek.service.learningservices.LessonService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ExerciseCatalogView {

    private final LessonService lessonService;

    private static final String[] COLORS = {
            "#E3F2FD",
            "#E0F7FA",
            "#FFF3E0",
            "#F3E5F5",
            "#E8F5E9"
    };

    private static final String[] ACCENTS = {
            "#2196F3",
            "#00ACC1",
            "#FB8C00",
            "#8E65C5",
            "#43A047"
    };

    private static final String[] ICONS = {
            "📘", "💬", "🧩", "✍️", "🌍"
    };

    public VBox create(
            Consumer<ExerciseCatalogDto> onLessonSelected
    ) {

        VBox root = new VBox(25);
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color: #F0F7FF;");

        // Игровой приветственный блок

        HBox hero = new HBox(20);
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setPadding(new Insets(27));

        hero.setStyle(
                "-fx-background-color: linear-gradient(to right, #1976D2, #00BCD4);" +
                        "-fx-background-radius: 24;"
        );

        VBox heroText = new VBox(10);
        HBox.setHgrow(heroText, Priority.ALWAYS);

        Label heroBadge = new Label("YOUR PRACTICE ZONE");

        heroBadge.setStyle(
                "-fx-background-color: rgba(255,255,255,0.2);" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 10;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;"
        );

        Label heroTitle = new Label(
                "Прокачай свой английский! 🚀"
        );

        heroTitle.setWrapText(true);

        heroTitle.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );

        Label heroDescription = new Label(
                "Выбирай любимые темы, выполняй задания " +
                        "и закрепляй то, что уже изучил."
        );

        heroDescription.setWrapText(true);

        heroDescription.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #EAF8FF;"
        );

        heroText.getChildren().addAll(
                heroBadge,
                heroTitle,
                heroDescription
        );

        Label heroIcon = new Label("🎯");
        heroIcon.setStyle("-fx-font-size: 58px;");

        hero.getChildren().addAll(heroText, heroIcon);

        // Заголовок практики

        VBox heading = new VBox(5);

        Label headingTitle = new Label("Выбери свою тренировку");

        headingTitle.setStyle(
                "-fx-font-size: 23px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Label headingSubtitle = new Label(
                "Каждый урок — новая возможность стать лучше."
        );

        headingSubtitle.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #7893B0;"
        );

        heading.getChildren().addAll(
                headingTitle,
                headingSubtitle
        );

        // Получение уроков

        List<ExerciseCatalogDto> lessons =
                lessonService.getAllPublishedLessons();

        VBox coursesContainer = new VBox(28);
        coursesContainer.setPadding(new Insets(5, 3, 25, 3));

        // Группировка: курс -> раздел -> уроки

        Map<String, Map<String, List<ExerciseCatalogDto>>> grouped =
                new LinkedHashMap<>();

        for (ExerciseCatalogDto lesson : lessons) {

            grouped
                    .computeIfAbsent(
                            lesson.courseTitle(),
                            key -> new LinkedHashMap<>()
                    )
                    .computeIfAbsent(
                            lesson.unitTitle(),
                            key -> new ArrayList<>()
                    )
                    .add(lesson);
        }

        if (lessons.isEmpty()) {

            VBox emptyCard = new VBox(12);
            emptyCard.setAlignment(Pos.CENTER);
            emptyCard.setPadding(new Insets(35));

            emptyCard.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 22;"
            );

            Label emptyIcon = new Label("📚");
            emptyIcon.setStyle("-fx-font-size: 42px;");

            Label emptyTitle = new Label(
                    "Пока нет доступных тренировок"
            );

            emptyTitle.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #163779;"
            );

            Label emptyDescription = new Label(
                    "Опубликованные уроки появятся здесь."
            );

            emptyDescription.setStyle(
                    "-fx-font-size: 13px;" +
                            "-fx-text-fill: #7893B0;"
            );

            emptyCard.getChildren().addAll(
                    emptyIcon,
                    emptyTitle,
                    emptyDescription
            );

            coursesContainer.getChildren().add(emptyCard);

        } else {

            for (Map.Entry<String, Map<String, List<ExerciseCatalogDto>>>
                    courseEntry : grouped.entrySet()) {

                VBox courseBlock = createCourseBlock(
                        courseEntry.getKey(),
                        courseEntry.getValue(),
                        onLessonSelected
                );

                coursesContainer.getChildren().add(courseBlock);
            }
        }

        // Прокрутка

        ScrollPane scrollPane = new ScrollPane(coursesContainer);

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
                hero,
                heading,
                scrollPane
        );

        return root;
    }

    // Отдельный курс

    private VBox createCourseBlock(
            String courseTitle,
            Map<String, List<ExerciseCatalogDto>> units,
            Consumer<ExerciseCatalogDto> onLessonSelected
    ) {

        VBox courseBlock = new VBox(18);

        // Заголовок курса

        ExerciseCatalogDto firstLesson =
                units.values().iterator().next().get(0);

        HBox courseHeader = new HBox(12);
        courseHeader.setAlignment(Pos.CENTER_LEFT);

        Label courseIcon = new Label("📖");
        courseIcon.setStyle("-fx-font-size: 28px;");

        VBox courseInfo = new VBox(4);

        Label courseName = new Label(courseTitle);

        courseName.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        Label courseLevel = new Label(
                "ENGLISH LEVEL " + firstLesson.courseLevel()
        );

        courseLevel.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2196F3;"
        );

        courseInfo.getChildren().addAll(
                courseName,
                courseLevel
        );

        courseHeader.getChildren().addAll(
                courseIcon,
                courseInfo
        );

        courseBlock.getChildren().add(courseHeader);

        // Разделы курса

        for (Map.Entry<String, List<ExerciseCatalogDto>> unitEntry
                : units.entrySet()) {

            VBox unitBlock = createUnitBlock(
                    unitEntry.getKey(),
                    unitEntry.getValue(),
                    onLessonSelected
            );

            courseBlock.getChildren().add(unitBlock);
        }

        return courseBlock;
    }

    // Отдельный учебный раздел

    private VBox createUnitBlock(
            String unitTitle,
            List<ExerciseCatalogDto> lessons,
            Consumer<ExerciseCatalogDto> onLessonSelected
    ) {

        VBox unitBlock = new VBox(13);

        HBox unitHeader = new HBox(10);
        unitHeader.setAlignment(Pos.CENTER_LEFT);

        Region line = new Region();
        line.setPrefSize(5, 20);
        line.setMinWidth(5);

        line.setStyle(
                "-fx-background-color: #00BCD4;" +
                        "-fx-background-radius: 5;"
        );

        Label title = new Label(unitTitle);

        title.setWrapText(true);

        title.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #456B99;"
        );

        unitHeader.getChildren().addAll(line, title);

        // Сетка игровых карточек

        TilePane lessonGrid = new TilePane();

        lessonGrid.setHgap(17);
        lessonGrid.setVgap(17);
        lessonGrid.setAlignment(Pos.TOP_LEFT);

        lessonGrid.setPrefColumns(2);
        lessonGrid.setPrefTileWidth(300);
        lessonGrid.setPrefTileHeight(225);

        lessonGrid.setPadding(new Insets(3));

        for (int i = 0; i < lessons.size(); i++) {

            VBox card = createLessonCard(
                    lessons.get(i),
                    i,
                    onLessonSelected
            );

            lessonGrid.getChildren().add(card);
        }

        unitBlock.getChildren().addAll(
                unitHeader,
                lessonGrid
        );

        return unitBlock;
    }

    // Игровая карточка урока

    private VBox createLessonCard(
            ExerciseCatalogDto lesson,
            int index,
            Consumer<ExerciseCatalogDto> onLessonSelected
    ) {

        int colorIndex = index % COLORS.length;

        String background = COLORS[colorIndex];
        String accent = ACCENTS[colorIndex];
        String icon = ICONS[colorIndex];

        VBox card = new VBox(12);

        card.setPrefWidth(300);
        card.setMinWidth(300);
        card.setMaxWidth(300);

        card.setPrefHeight(225);
        card.setMinHeight(225);

        card.setPadding(new Insets(17));

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: #DCEBFA;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 1 1 4 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(22,55,121,0.09), 12, 0, 0, 4);"
        );

        // Верх карточки

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label level = new Label(
                "LEVEL " + lesson.courseLevel()
        );

        level.setStyle(
                "-fx-background-color: " + background + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 10;" +
                        "-fx-text-fill: " + accent + ";" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label category = new Label("PRACTICE");

        category.setStyle(
                "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #90A4B8;"
        );

        top.getChildren().addAll(
                level,
                spacer,
                category
        );

        // Иллюстрация

        HBox iconBox = new HBox();
        iconBox.setAlignment(Pos.CENTER_LEFT);

        Label lessonIcon = new Label(icon);

        lessonIcon.setStyle(
                "-fx-font-size: 31px;" +
                        "-fx-background-color: " + background + ";" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 9;"
        );

        iconBox.getChildren().add(lessonIcon);

        // Название

        Label title = new Label(lesson.lessonTitle());

        title.setWrapText(true);

        title.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #163779;"
        );

        // Описание

        Label description = new Label(
                lesson.lessonDescription()
        );

        description.setWrapText(true);
        description.setMaxHeight(40);

        description.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #7893B0;"
        );

        VBox.setVgrow(description, Priority.ALWAYS);

        // Кнопка

        Button button = new Button("Начать тренировку  →");

        button.setMaxWidth(Double.MAX_VALUE);

        button.setStyle(
                "-fx-background-color: " + accent + ";" +
                        "-fx-background-radius: 11;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 11;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent transparent " + accent + " transparent;"
        );

        button.setOnAction(event ->
                onLessonSelected.accept(lesson)
        );

        card.getChildren().addAll(
                top,
                iconBox,
                title,
                description,
                button
        );

        return card;
    }
}
