
package com.valek.service.learningservices;

import com.valek.dto.learningdto.ExerciseDto;
import com.valek.dto.learningdto.ExerciseOptionDto;
import com.valek.model.learningmodels.Exercise;
import com.valek.model.learningmodels.ExerciseAcceptedAnswer;
import com.valek.model.learningmodels.ExerciseOption;
import com.valek.repository.learningreps.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    // Получение упражнений урока
    @Transactional(readOnly = true)
    public List<ExerciseDto> getExercisesByLesson(Long lessonId) {

        List<Exercise> exercises =
                exerciseRepository.findByLesson_IdOrderByPositionAsc(lessonId);

        return exercises.stream()
                .map(exercise -> new ExerciseDto(
                        exercise.getId(),
                        exercise.getType(),
                        exercise.getQuestion(),
                        exercise.getExplanation(),
                        exercise.getPoints(),
                        exercise.getPosition(),
                        exercise.getOptions().stream()
                                .map(option -> new ExerciseOptionDto(
                                        option.getId(),
                                        option.getOptionText()
                                ))
                                .toList()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean checkAnswer(Long exerciseId, String userAnswer) {

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Упражнение не найдено")
                );

        if (userAnswer == null || userAnswer.isBlank()) {
            return false;
        }

        switch (exercise.getType()) {

            case "TRANSLATION":
                return checkTextAnswer(exercise, userAnswer);

            case "FILL_IN_BLANK":
            case "MULTIPLE_CHOICE":
                return checkMultipleChoice(exercise, userAnswer);

            case "WORD_ORDER":
                return checkWordOrder(exercise, userAnswer);

            default:
                return false;
        }
    }

    private boolean checkTextAnswer(Exercise exercise, String userAnswer) {

        String normalizedAnswer = normalize(userAnswer);

        for (ExerciseAcceptedAnswer answer : exercise.getAcceptedAnswers()) {

            String correctAnswer = normalize(answer.getAnswerText());

            if (normalizedAnswer.equals(correctAnswer)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkMultipleChoice(Exercise exercise, String userAnswer) {

        Long selectedId;

        try {
            selectedId = Long.parseLong(userAnswer);
        } catch (NumberFormatException exception) {
            return false;
        }

        for (ExerciseOption option : exercise.getOptions()) {

            if (option.getId().equals(selectedId) && option.isCorrect()) {
                return true;
            }
        }

        return false;
    }

    private boolean checkWordOrder(Exercise exercise, String userAnswer) {

        String[] selectedWords = userAnswer.split(",");

        List<ExerciseOption> correctOptions =
                new ArrayList<>(exercise.getOptions());

        correctOptions.sort(
                Comparator.comparingInt(ExerciseOption::getPosition)
        );

        if (selectedWords.length != correctOptions.size()) {
            return false;
        }

        for (int i = 0; i < selectedWords.length; i++) {

            Long selectedId;

            try {
                selectedId = Long.parseLong(selectedWords[i].trim());
            } catch (NumberFormatException exception) {
                return false;
            }

            if (!correctOptions.get(i).getId().equals(selectedId)) {
                return false;
            }
        }

        return true;
    }

    private String normalize(String answer) {
        return answer.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
