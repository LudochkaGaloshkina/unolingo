
package com.valek.controller;

import com.valek.dto.learningdto.ExerciseDto;
import com.valek.dto.progressdto.ProgressSummaryDto;
import com.valek.model.User;
import com.valek.model.attempts.LessonAttempt;
import com.valek.model.learningmodels.Course;

import com.valek.model.learningmodels.Lesson;

import com.valek.service.learningservices.ExerciseService;
import com.valek.service.progress.LessonProgressService;
import com.valek.service.progress.ProgressService;
import com.valek.view.dashboard.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private User user;

    private final HomeView homeView;
    private final CourseCatalogView courseCatalogView;
    private final CourseDetailsView courseDetailsView;
    private final LessonView lessonView;
    private final ExerciseView exerciseView;
    private final ExerciseService exerciseService;
    private List<ExerciseDto> currentExercises;
    private int currentExerciseIndex;
    private Lesson currentLesson;
    private Course currentCourse;
    private final LessonProgressService lessonProgressService;
    private LessonAttempt currentLessonAttempt;
    private final ProgressService progressService;
    private final ProgressView progressView;

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
    }

    @FXML
    private void handleProgress() {
        setActiveButton(progressButton);

        ProgressSummaryDto summary = progressService.getProgressSummary(user);

        VBox progressContent = progressView.create(summary);

        contentBox.getChildren().setAll(progressContent);
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

    private void showLearning() {
        contentBox.getChildren().setAll(
                courseCatalogView.create(this::showCourseDetails)
        );
    }

    private void showHome() {
        contentBox.getChildren().setAll(homeView.create(user, progressService.getCompletedLessons(user)));
    }

    private void showCourseDetails(Course course) {
        currentCourse = course;

        contentBox.getChildren().setAll(courseDetailsView.create(course, this::showLearning, this::showLesson));
    }

    private void showLesson(Lesson lesson) {
        currentLesson = lesson;

        contentBox.getChildren().setAll(lessonView.create(lesson, () -> showCourseDetails(currentCourse), this::startLesson));
    }

    private void showExercise() {

        if (currentExercises == null || currentExercises.isEmpty()) {
            return;
        }

        ExerciseDto exercise = currentExercises.get(currentExerciseIndex);

        contentBox.getChildren().setAll(exerciseView.create(exercise, () -> showLesson(currentLesson),
                answer -> {
                    boolean correct = exerciseService.checkAnswer(exercise.id(), answer);

                    lessonProgressService.recordExerciseAttempt(
                            currentLessonAttempt,
                            exercise.id(),
                            answer,
                            correct
                    );

                    return correct;
                }, this::nextExercise));
    }

    public void startLesson() {
        currentExercises = exerciseService.getExercisesByLesson(currentLesson.getId());

        if (currentExercises.isEmpty()) {
            return;
        }

        currentExerciseIndex = 0;

        currentLessonAttempt = lessonProgressService.startLessonAttempt(
                user,
                currentLesson,
                currentExercises.size()
        );

        showExercise();
    }

    private void nextExercise() {
        currentExerciseIndex++;

        if (currentExerciseIndex < currentExercises.size()) {
            showExercise();
        } else {
            lessonProgressService.completeLesson(currentLessonAttempt);

            currentLessonAttempt = null;

            showCourseDetails(currentCourse);
        }
    }
}
