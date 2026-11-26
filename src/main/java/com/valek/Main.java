package com.valek;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class Main extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(SpringBootApp.class).run();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Image applicationIcon = new Image(getClass().getResourceAsStream("/images/shark-logo.png"));

        stage.getIcons().add(applicationIcon);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/AuthPages/login.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Scene scene = new Scene(loader.load());

        stage.setTitle("Unolingo");
        stage.setScene(scene);

        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
