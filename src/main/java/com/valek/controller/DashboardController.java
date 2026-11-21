package com.valek.controller;

import com.valek.model.User;
import com.valek.model.learningmodels.Course;
import com.valek.view.dashboard.CourseCatalogView;
import com.valek.view.dashboard.CourseDetailsView;
import com.valek.view.dashboard.HomeView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class DashboardController {

    private User user;
    private final HomeView homeView;
    private final CourseCatalogView courseCatalogView;
    private final CourseDetailsView courseDetailsView;

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
        contentBox.getChildren().setAll(homeView.create(user));
    }

    private void showCourseDetails(Course course) {
        contentBox.getChildren().setAll(
                courseDetailsView.create(course, this::showLearning)
        );
    }
}