package com.valek.service.progress;

import com.valek.model.User;
import com.valek.model.UserDailyStatsId;
import com.valek.model.attempts.ExerciseAttempt;
import com.valek.model.attempts.LessonAttempt;
import com.valek.model.attempts.UserLessonProgress;
import com.valek.model.learningmodels.Exercise;
import com.valek.model.learningmodels.ExerciseOption;
import com.valek.model.learningmodels.Lesson;
import com.valek.model.UserDailyStats;
import com.valek.repository.UserDailyStatsRepository;
import com.valek.repository.attemptreps.ExerciseAttemptRepository;
import com.valek.repository.attemptreps.LessonAttemptRepository;
import com.valek.repository.attemptreps.UserLessonProgressRepository;
import com.valek.repository.learningreps.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class LessonProgressService {

    private final LessonAttemptRepository lessonAttemptRepository;
    private final ExerciseAttemptRepository exerciseAttemptRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final UserDailyStatsRepository userDailyStatsRepository;

    @Transactional
    public LessonAttempt startLessonAttempt(User user, Lesson lesson, int totalCount) {
        LessonAttempt attempt = new LessonAttempt();

        attempt.setUser(user);
        attempt.setLesson(lesson);
        attempt.setStartedAt(OffsetDateTime.now());

        attempt.setScore(0);
        attempt.setCorrectCount(0);
        attempt.setTotalCount(totalCount);

        return lessonAttemptRepository.save(attempt);
    }

    @Transactional
    public void recordExerciseAttempt(LessonAttempt lessonAttempt, Long exerciseId, String userAnswer, boolean correct) {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() -> new IllegalArgumentException("Упражнение не найдено"));
        if (!exercise.getLesson().getId().equals(lessonAttempt.getLesson().getId())) {
            throw new IllegalArgumentException("Упражнение не принадлежит текущему уроку");
        }
        if (lessonAttempt.getFinishedAt() != null) {
            throw new IllegalStateException("Попытка прохождения уже завершена");
        }

        ExerciseAttempt exerciseAttempt = new ExerciseAttempt();

        exerciseAttempt.setLessonAttempt(lessonAttempt);
        exerciseAttempt.setExercise(exercise);
        exerciseAttempt.setCorrect(correct);
        exerciseAttempt.setAnsweredAt(OffsetDateTime.now());

        int earnedPoints = correct ? exercise.getPoints() : 0;
        exerciseAttempt.setEarnedPoints(earnedPoints);

        switch (exercise.getType()) {
            case "MULTIPLE_CHOICE":
            case "FILL_IN_BLANK":
                Long selectedOptionId;

                try {
                    selectedOptionId = Long.parseLong(userAnswer);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException("Некорректный вариант ответа");
                }

                ExerciseOption selectedOption = exercise.getOptions().stream()
                        .filter(option -> option.getId().equals(selectedOptionId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Вариант ответа не найден"));

                exerciseAttempt.setSelectedOption(selectedOption);
                break;

            case "TRANSLATION":
            case "WORD_ORDER":
                exerciseAttempt.setUserAnswer(userAnswer);
                break;

            default:
                throw new IllegalArgumentException("Неизвестный тип упражнения");
        }

        exerciseAttemptRepository.save(exerciseAttempt);

        lessonAttempt.setScore(lessonAttempt.getScore() + earnedPoints);

        if (correct) {
            lessonAttempt.setCorrectCount(lessonAttempt.getCorrectCount() + 1);
        }

        lessonAttemptRepository.save(lessonAttempt);
    }

    @Transactional
    public void finishLessonAttempt(LessonAttempt lessonAttempt) {
        lessonAttempt.setFinishedAt(OffsetDateTime.now());

        lessonAttemptRepository.save(lessonAttempt);
    }

    @Transactional
    public void updateLessonProgress(LessonAttempt lessonAttempt) {
        UserLessonProgress progress = userLessonProgressRepository.findByUser_IdAndLesson_Id(
                        lessonAttempt.getUser().getId(),
                        lessonAttempt.getLesson().getId()
                ).orElseGet(UserLessonProgress::new);

        progress.setUser(lessonAttempt.getUser());
        progress.setLesson(lessonAttempt.getLesson());
        progress.setStatus("COMPLETED");

        if (lessonAttempt.getScore() > progress.getBestScore()) {
            progress.setBestScore(lessonAttempt.getScore());
        }

        progress.setCompletedAt(OffsetDateTime.now());

        userLessonProgressRepository.save(progress);
    }

    @Transactional
    public void updateDailyStats(LessonAttempt lessonAttempt) {
        Long userId = lessonAttempt.getUser().getId();
        LocalDate today = LocalDate.now();

        UserDailyStatsId statsId = new UserDailyStatsId();
        statsId.setUserId(userId);
        statsId.setActivityDate(today);

        UserDailyStats stats = userDailyStatsRepository.findById(statsId)
                .orElseGet(() -> {
                    UserDailyStats newStats = new UserDailyStats();
                    newStats.setId(statsId);
                    newStats.setUser(lessonAttempt.getUser());
                    newStats.setXpEarned(0);
                    newStats.setLessonsCompleted(0);
                    return newStats;
                });

        stats.setXpEarned(stats.getXpEarned() + lessonAttempt.getScore());
        stats.setLessonsCompleted(stats.getLessonsCompleted() + 1);

        userDailyStatsRepository.save(stats);
    }

    @Transactional
    public void completeLesson(LessonAttempt lessonAttempt) {
        if (lessonAttempt.getFinishedAt() != null) {
            return;
        }

        finishLessonAttempt(lessonAttempt);
        updateLessonProgress(lessonAttempt);
        updateDailyStats(lessonAttempt);
    }
}
