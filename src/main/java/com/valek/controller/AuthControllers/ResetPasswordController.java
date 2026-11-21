
package com.valek.controller.AuthControllers;

import com.valek.service.mail.PasswordResetService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class ResetPasswordController {

    private final PasswordResetService passwordResetService;
    private final ConfigurableApplicationContext context;

    @FXML
    private TextField codeField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    private void handleResetPassword() {

        String code = codeField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (code.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {
            messageLabel.setText("Заполните все поля.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Пароли не совпадают.");
            return;
        }

        try {
            passwordResetService.resetPassword(
                    email,
                    code,
                    password
            );

            messageLabel.setStyle("-fx-text-fill: #218838;");
            messageLabel.setText("Пароль успешно изменён!");

        } catch (IllegalArgumentException e) {
            messageLabel.setStyle("-fx-text-fill: #d64545;");
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        openWindow("/fxml/AuthPages/login.fxml", codeField);
    }

    private void openWindow(String fxmlPath, Node node) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );

            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            Stage stage = (Stage) node.getScene().getWindow();

            boolean wasMaximized = stage.isMaximized();

            stage.setScene(new Scene(root));
            stage.setTitle("Unolingo");

            if (wasMaximized) {
                stage.setMaximized(false);
            }

            stage.setMaximized(true);

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Не удалось открыть экран.");
        }
    }
}
