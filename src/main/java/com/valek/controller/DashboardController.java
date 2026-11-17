package com.valek.controller;

import com.valek.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.stereotype.Controller;

@Controller
public class DashboardController {

    private User user;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label levelLabel;

    @FXML
    private Label sectionLabel;

    @FXML
    private Button homeButton;

    @FXML
    private Button learningButton;

    @FXML
    private Button exercisesButton;

    @FXML
    private Button progressButton;

    @FXML
    private void handleHome() {
        sectionLabel.setText("Главная");
        setActiveButton(homeButton);
    }

    @FXML
    private void handleLearning() {
        sectionLabel.setText("Обучение");
        setActiveButton(learningButton);
    }

    @FXML
    private void handleExercises() {
        sectionLabel.setText("Упражнения");
        setActiveButton(exercisesButton);
    }

    @FXML
    private void handleProgress() {
        sectionLabel.setText("Мой прогресс");
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

        welcomeLabel.setText(
                "Добро пожаловать, " + user.getUsername() + "!"
        );

        levelLabel.setText(user.getLevel());
    }
}