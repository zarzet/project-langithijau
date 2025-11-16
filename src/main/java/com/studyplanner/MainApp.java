package com.studyplanner;

import com.studyplanner.component.CustomWindowDecorator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Application Class - Entry Point
 */
public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/MainView.fxml")
        );
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);
        scene
            .getStylesheets()
            .add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        // Apply custom window decoration
        CustomWindowDecorator.decorate(stage, "Adaptive Study Planner", false);

        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
