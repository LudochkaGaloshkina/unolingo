
package com.valek.view.dashboard;

import com.valek.dto.learningdto.ExerciseDto;
import com.valek.dto.learningdto.ExerciseOptionDto;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class ExerciseView {

    public VBox create(ExerciseDto exercise, Runnable onBack, Function<String, Boolean> onCheck, Runnable onNext) {

        VBox root = new VBox(25);
        root.setPadding(new Insets(35));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("exercise-root");

        URL css = getClass().getResource("/css/course-details.css");

        if (css == null) {
            throw new IllegalStateException(
                    "Не найден файл /css/course-details.css"
            );
        }

        root.getStylesheets().add(css.toExternalForm());

        // Верхняя часть экрана
        HBox header = createHeader(onBack);

        // Карточка с заданием
        VBox exerciseCard = new VBox(20);
        exerciseCard.setPadding(new Insets(30));
        exerciseCard.setMaxWidth(650);
        exerciseCard.setAlignment(Pos.TOP_LEFT);
        exerciseCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 18;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.07), 15, 0, 0, 4);"
        );

        VBox questionHeader = createQuestionHeader(exercise);

        AnswerControls answerControls = createAnswerBox(exercise);

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);
        resultLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );

        Button checkButton = createButton(
                "Проверить ответ",
                "#2563EB"
        );

        Button nextButton = createButton(
                "Следующее задание →",
                "#16A34A"
        );

        nextButton.setVisible(false);
        nextButton.setManaged(false);

        checkButton.setMaxWidth(Double.MAX_VALUE);
        nextButton.setMaxWidth(Double.MAX_VALUE);

        checkButton.setOnAction(event -> {

            String userAnswer = answerControls.getAnswer().get();

            if (userAnswer == null || userAnswer.isBlank()) {
                resultLabel.setText("Сначала введи или выбери ответ.");
                resultLabel.setStyle(
                        "-fx-text-fill: #D97706;" +
                                "-fx-font-size: 14px;"
                );
                return;
            }

            boolean correct = onCheck.apply(userAnswer);

            showResult(
                    resultLabel,
                    exercise,
                    correct
            );

            checkButton.setDisable(true);
            answerControls.getBox().setDisable(true);

            nextButton.setVisible(true);
            nextButton.setManaged(true);
        });

        nextButton.setOnAction(event -> onNext.run());

        exerciseCard.getChildren().addAll(
                questionHeader,
                answerControls.getBox(),
                resultLabel,
                checkButton,
                nextButton
        );

        root.getChildren().addAll(
                header,
                exerciseCard
        );

        return root;
    }

    private HBox createHeader(Runnable onBack) {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMaxWidth(650);

        Button backButton = new Button("←  Назад к уроку");
        backButton.getStyleClass().add("back-button");

        backButton.setOnAction(event -> onBack.run());

        header.getChildren().add(backButton);

        return header;
    }

    private VBox createQuestionHeader(ExerciseDto exercise) {

        VBox header = new VBox(12);

        Label typeLabel = new Label(getTypeName(exercise.type()));
        typeLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2563EB;" +
                        "-fx-background-color: #EFF6FF;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 7 13;"
        );

        Label questionLabel = new Label(exercise.question());
        questionLabel.setWrapText(true);
        questionLabel.setStyle(
                "-fx-font-size: 23px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E293B;"
        );

        Label pointsLabel = new Label(
                "★  " + exercise.points() + " баллов"
        );
        pointsLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #64748B;"
        );

        header.getChildren().addAll(
                typeLabel,
                questionLabel,
                pointsLabel
        );

        return header;
    }


    private AnswerControls createAnswerBox(ExerciseDto exercise) {

        VBox answerBox = new VBox(12);
        answerBox.setPadding(new Insets(10, 0, 10, 0));

        switch (exercise.type()) {

            case "TRANSLATION":
                return createTextAnswer(answerBox);

            case "MULTIPLE_CHOICE":
                return createMultipleChoiceAnswer(
                        exercise,
                        answerBox
                );

            case "FILL_IN_BLANK":
                return createFillBlankAnswer(
                        exercise,
                        answerBox
                );

            case "WORD_ORDER":
                return createWordOrderAnswer(
                        exercise,
                        answerBox
                );

            default:
                Label errorLabel = new Label(
                        "Неизвестный тип упражнения."
                );

                answerBox.getChildren().add(errorLabel);

                return new AnswerControls(
                        answerBox,
                        () -> ""
                );
        }
    }

    private AnswerControls createFillBlankAnswer(
            ExerciseDto exercise,
            VBox answerBox
    ) {

        Label instructionLabel = new Label(
                "Выбери слово, которое подходит по смыслу:"
        );

        instructionLabel.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #475569;"
        );

        answerBox.getChildren().add(instructionLabel);

        return createMultipleChoiceAnswer(
                exercise,
                answerBox
        );
    }

    private AnswerControls createTextAnswer(VBox answerBox) {

        TextField answerField = new TextField();
        answerField.setPromptText("Введи свой ответ...");
        answerField.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-padding: 13;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-background-color: #F8FAFC;"
        );

        answerBox.getChildren().add(answerField);

        return new AnswerControls(
                answerBox,
                answerField::getText
        );
    }

    private AnswerControls createMultipleChoiceAnswer(ExerciseDto exercise, VBox answerBox) {

        ToggleGroup answerGroup = new ToggleGroup();

        for (ExerciseOptionDto option : exercise.options()) {

            RadioButton optionButton =
                    new RadioButton(option.optionText());

            optionButton.setUserData(option.id());
            optionButton.setToggleGroup(answerGroup);
            optionButton.setMaxWidth(Double.MAX_VALUE);

            optionButton.setStyle(
                    "-fx-font-size: 15px;" +
                            "-fx-padding: 12;" +
                            "-fx-cursor: hand;" +
                            "-fx-background-color: #F8FAFC;" +
                            "-fx-background-radius: 10;"
            );

            answerBox.getChildren().add(optionButton);
        }

        return new AnswerControls(
                answerBox,
                () -> {

                    if (answerGroup.getSelectedToggle() == null) {
                        return "";
                    }

                    return answerGroup.getSelectedToggle()
                            .getUserData().toString();
                }
        );
    }

    private AnswerControls createWordOrderAnswer(ExerciseDto exercise, VBox answerBox) {

        List<Long> selectedWordIds = new ArrayList<>();

        Label selectedWordsLabel = new Label(
                "Выбранные слова появятся здесь..."
        );

        selectedWordsLabel.setWrapText(true);
        selectedWordsLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E293B;" +
                        "-fx-padding: 12;" +
                        "-fx-background-color: #EFF6FF;" +
                        "-fx-background-radius: 10;"
        );

        HBox wordsBox = new HBox(10);
        wordsBox.setAlignment(Pos.CENTER_LEFT);
        wordsBox.setPadding(new Insets(10, 0, 10, 0));

        List<ExerciseOptionDto> shuffledOptions = new ArrayList<>(exercise.options());

        java.util.Collections.shuffle(shuffledOptions);


        for (ExerciseOptionDto option : shuffledOptions) {

            Button wordButton = new Button(option.optionText());

            wordButton.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-padding: 10 15;" +
                            "-fx-background-color: #E2E8F0;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );

            wordButton.setOnAction(event -> {

                selectedWordIds.add(option.id());

                updateSelectedWords(
                        selectedWordIds,
                        exercise.options(),
                        selectedWordsLabel
                );

                wordButton.setDisable(true);
            });

            wordsBox.getChildren().add(wordButton);
        }

        answerBox.getChildren().addAll(
                new Label("Составь предложение:"),
                selectedWordsLabel,
                wordsBox
        );

        return new AnswerControls(
                answerBox,
                () -> {

                    StringBuilder answer = new StringBuilder();

                    for (Long id : selectedWordIds) {

                        if (!answer.isEmpty()) {
                            answer.append(",");
                        }

                        answer.append(id);
                    }

                    return answer.toString();
                }
        );
    }

    private void updateSelectedWords(
            List<Long> selectedWordIds,
            List<ExerciseOptionDto> options,
            Label selectedWordsLabel
    ) {

        StringBuilder selectedWords = new StringBuilder();

        for (Long wordId : selectedWordIds) {

            for (ExerciseOptionDto option : options) {

                if (option.id().equals(wordId)) {

                    if (!selectedWords.isEmpty()) {
                        selectedWords.append(" ");
                    }

                    selectedWords.append(option.optionText());
                }
            }
        }

        selectedWordsLabel.setText(selectedWords.toString());
    }

    private void showResult(Label resultLabel, ExerciseDto exercise, boolean correct) {

        if (correct) {

            resultLabel.setText(
                    "✓  Правильно! Отличная работа!"
            );

            resultLabel.setStyle(
                    "-fx-text-fill: #16A34A;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: #DCFCE7;" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 13;"
            );

        } else {

            String explanation = exercise.explanation();

            if (explanation == null || explanation.isBlank()) {
                explanation = "Попробуй внимательнее в следующий раз.";
            }

            resultLabel.setText(
                    "✗  Неправильно.\n" + explanation
            );

            resultLabel.setStyle(
                    "-fx-text-fill: #DC2626;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: #FEE2E2;" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 13;"
            );
        }
    }

    private String getTypeName(String type) {

        switch (type) {

            case "TRANSLATION":
                return "ПЕРЕВОД";

            case "MULTIPLE_CHOICE":
                return "ВЫБОР ОТВЕТА";

            case "FILL_IN_BLANK":
                return "ЗАПОЛНИ ПРОПУСК";

            case "WORD_ORDER":
                return "ПОРЯДОК СЛОВ";

            default:
                return type;
        }
    }

    private Button createButton(String text, String color) {

        Button button = new Button(text);

        button.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 13 20;" +
                        "-fx-cursor: hand;"
        );

        return button;
    }

    private static class AnswerControls {

        private final VBox box;
        private final Supplier<String> answer;

        public AnswerControls(VBox box, Supplier<String> answer) {
            this.box = box;
            this.answer = answer;
        }

        public VBox getBox() {
            return box;
        }

        public Supplier<String> getAnswer() {
            return answer;
        }
    }
}
