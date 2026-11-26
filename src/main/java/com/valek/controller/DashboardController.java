
package com.valek.controller;

import com.valek.dto.learningdto.ExerciseCatalogDto;
import com.valek.dto.learningdto.ExerciseDto;
import com.valek.dto.progressdto.ProgressSummaryDto;
import com.valek.model.User;
import com.valek.model.attempts.LessonAttempt;
import com.valek.model.learningmodels.Course;
import com.valek.model.learningmodels.Lesson;
import com.valek.service.learningservices.ExerciseService;
import com.valek.service.learningservices.LessonService;
import com.valek.service.progress.LessonProgressService;
import com.valek.service.progress.ProgressService;
import com.valek.view.dashboard.*;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private User user;

    private final HomeView homeView;
    private final CourseCatalogView courseCatalogView;
    private final CourseDetailsView courseDetailsView;
    private final LessonView lessonView;
    private final ExerciseView exerciseView;
    private final ExerciseCatalogView exerciseCatalogView;

    private final ExerciseService exerciseService;
    private final LessonService lessonService;
    private final LessonProgressService lessonProgressService;
    private final ProgressService progressService;
    private final ProgressView progressView;

    private List<ExerciseDto> currentExercises;
    private int currentExerciseIndex;

    private Lesson currentLesson;
    private Course currentCourse;
    private LessonAttempt currentLessonAttempt;

    // true — урок запущен из вкладки «Упражнения»
    private boolean practiceMode;

    @FXML
    private Button homeButton;

    @FXML
    private Button learningButton;

    @FXML
    private Button exercisesButton;

    @FXML
    private VBox contentBox;

    @FXML
    private Button progressButton;

    @FXML
    private void handleHome() {
        setActiveButton(homeButton);
        showHome();
    }

    @FXML
    private void handleLearning() {
        setActiveButton(learningButton);
        showLearning();
    }

    @FXML
    private void handleExercises() {
        setActiveButton(exercisesButton);
        showExercises();
    }

    @FXML
    private void handleProgress() {
        setActiveButton(progressButton);

        ProgressSummaryDto summary =
                progressService.getProgressSummary(user);

        contentBox.getChildren().setAll(
                progressView.create(summary)
        );
    }

    private void setActiveButton(Button button) {
        homeButton.getStyleClass().remove("active");
        learningButton.getStyleClass().remove("active");
        exercisesButton.getStyleClass().remove("active");
        progressButton.getStyleClass().remove("active");

        button.getStyleClass().add("active");
    }

    public void setUser(User user) {
        this.user = user;
        showHome();
    }

    private void showHome() {
        contentBox.getChildren().setAll(
                homeView.create(
                        user,
                        progressService.getCompletedLessons(user)
                )
        );
    }

    private void showLearning() {
        contentBox.getChildren().setAll(
                courseCatalogView.create(this::showCourseDetails)
        );
    }

    private void showExercises() {
        contentBox.getChildren().setAll(exerciseCatalogView.create(this::startPracticeLesson));
    }

    private void showCourseDetails(Course course) {
        currentCourse = course;
        practiceMode = false;

        Set<Long> completedLessonIds = progressService.getCompletedLessonIds(user);

        contentBox.getChildren().setAll(
                courseDetailsView.create(
                        course,
                        completedLessonIds,
                        this::showLearning,
                        this::showLesson
                )
        );
    }


    private boolean isLessonUnlocked(Lesson targetLesson) {
        Set<Long> completedLessonIds = progressService.getCompletedLessonIds(user);

        return lessonService.isLessonUnlocked(targetLesson.getId(), completedLessonIds);
    }


    private void showLockedLessonMessage() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Урок заблокирован");
        alert.setHeaderText("Этот урок пока недоступен");

        alert.setContentText(
                "Чтобы открыть этот урок, сначала заверши предыдущий урок курса."
        );

        alert.showAndWait();
    }

    private void showLesson(Lesson lesson) {

        if (!isLessonUnlocked(lesson)) {
            showLockedLessonMessage();
            return;
        }

        currentLesson = lesson;
        practiceMode = false;

        contentBox.getChildren().setAll(
                lessonView.create(
                        lesson,
                        () -> showCourseDetails(currentCourse),
                        this::startLesson
                )
        );
    }

    // Запуск урока из каталога упражнений

    private void startPracticeLesson(ExerciseCatalogDto selectedLesson) {

        currentLesson = lessonService.getPublishedLessonById(
                selectedLesson.lessonId()
        );

        currentCourse = null;

        practiceMode = true;

        startLesson();
    }

    private void showExercise() {

        if (currentExercises == null || currentExercises.isEmpty()) {
            return;
        }

        ExerciseDto exercise = currentExercises.get(currentExerciseIndex);

        Runnable onBack = practiceMode
                ? this::showExercises
                : () -> showLesson(currentLesson);

        contentBox.getChildren().setAll(
                exerciseView.create(
                        exercise,
                        onBack,
                        answer -> {

                            boolean correct = exerciseService.checkAnswer(
                                    exercise.id(),
                                    answer
                            );

                            lessonProgressService.recordExerciseAttempt(
                                    currentLessonAttempt,
                                    exercise.id(),
                                    answer,
                                    correct
                            );

                            return correct;
                        },
                        this::nextExercise
                )
        );
    }


    public void startLesson() {


        if (!practiceMode && !isLessonUnlocked(currentLesson)) {
            showLockedLessonMessage();
            showCourseDetails(currentCourse);
            return;
        }

        currentExercises = exerciseService.getExercisesByLesson(currentLesson.getId());

        if (currentExercises.isEmpty()) {

            if (practiceMode) {
                showExercises();
            }

            return;
        }

        currentExerciseIndex = 0;

        currentLessonAttempt = lessonProgressService.startLessonAttempt(user, currentLesson, currentExercises.size());

        showExercise();
    }


    private void nextExercise() {

        currentExerciseIndex++;

        if (currentExerciseIndex < currentExercises.size()) {

            showExercise();

        } else {

            lessonProgressService.completeLesson(currentLessonAttempt);

            currentLessonAttempt = null;

            if (practiceMode) {
                showExercises();
            } else {
                showCourseDetails(currentCourse);
            }
        }
    }
}
