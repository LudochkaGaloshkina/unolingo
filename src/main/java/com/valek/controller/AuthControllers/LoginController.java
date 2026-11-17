package com.valek.controller.AuthControllers;

import com.valek.controller.DashboardController;
import com.valek.model.User;
import com.valek.service.AuthService;
import com.valek.service.mail.EmailService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;
    private final ConfigurableApplicationContext context;
    private final EmailService emailService;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Заполните все поля.");
            return;
        }

        try {
            User user = authService.login(email, password);

            if (user == null) {
                messageLabel.setText("Неверный логин или пароль.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/dashboard.fxml")
            );

            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Unolingo");
            stage.centerOnScreen();

        } catch (DataAccessException e) {
            messageLabel.setStyle("-fx-text-fill: #d64545;");
            messageLabel.setText(
                    "Не удалось выполнить вход. Проверьте подключение к БД."
            );
            e.printStackTrace();

        } catch (IOException e) {
            messageLabel.setText("Не удалось открыть главный экран.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenRegister() {
        openWindow("/fxml/AuthPages/register.fxml", emailField);
    }

    @FXML
    private void handleForgotPassword() {
        openWindow("/fxml/AuthPages/forgot-password.fxml", emailField);
    }

    @FXML
    private void handleGuest() {
        messageLabel.setText(
                "Гостевой режим пока не реализован."
        );
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

        } catch (IOException | NullPointerException e) {
            messageLabel.setText("Не удалось открыть экран.");
            e.printStackTrace();
        }
    }
}
