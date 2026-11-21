package com.valek.controller.AuthControllers;

import com.valek.service.mail.PasswordResetService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final PasswordResetService passwordResetService;
    private final ConfigurableApplicationContext context;

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;


    @FXML
    private void handleSendCode() {

        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            messageLabel.setText("Введите электронную почту.");
            return;
        }

        try {
            passwordResetService.sendResetCode(email);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/AuthPages/reset-password.fxml")
            );

            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            ResetPasswordController controller = loader.getController();
            controller.setEmail(email);

            Stage stage = (Stage) emailField.getScene().getWindow();

            boolean wasMaximized = stage.isMaximized();

            stage.setScene(new Scene(root));
            stage.setTitle("Unolingo");

            if (wasMaximized) {
                stage.setMaximized(false);
            }

            stage.setMaximized(true);

        } catch (IOException e) {
            messageLabel.setText("Не удалось открыть экран.");
            e.printStackTrace();

        } catch (Exception e) {
            messageLabel.setText("Не удалось отправить код. Попробуйте позже.");
            e.printStackTrace();
        }
    }


    @FXML
    private void handleBackToLogin() {
        openWindow("/fxml/AuthPages/login.fxml", emailField);
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
