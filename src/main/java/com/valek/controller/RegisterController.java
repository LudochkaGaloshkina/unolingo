
package com.valek.controller;

import com.valek.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final AuthService authService;
    private final ConfigurableApplicationContext context;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> levelComboBox;

    @FXML
    private CheckBox termsCheckBox;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        levelComboBox.getItems().addAll(
                "A1", "A2", "B1", "B2", "C1", "C2"
        );

        levelComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleRegister() {

        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String level = levelComboBox.getValue();

        if (username.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Заполните все поля.");
            return;
        }

        if (password.length() < 8) {
            showError("Пароль должен содержать минимум 8 символов.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Пароли не совпадают.");
            return;
        }

        if (level == null) {
            showError("Выберите уровень английского.");
            return;
        }

        if (!termsCheckBox.isSelected()) {
            showError("Необходимо принять условия использования.");
            return;
        }

        try {
            authService.register(username, email, password, level);

            messageLabel.setStyle("-fx-text-fill: #218838;");
            messageLabel.setText("Регистрация успешна! Теперь войдите.");

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());

        } catch (DataAccessException e) {
            showError(
                    "Не удалось зарегистрироваться. Проверьте подключение к БД."
            );
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenLogin() {
        openWindow("/fxml/login.fxml", usernameField);
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: #d64545;");
        messageLabel.setText(message);
    }

    private void openWindow(String fxmlPath, Node node) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );

            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("English Learning Platform");
            stage.centerOnScreen();

        } catch (IOException e) {
            showError("Не удалось открыть экран.");
            e.printStackTrace();
        }
    }
}
