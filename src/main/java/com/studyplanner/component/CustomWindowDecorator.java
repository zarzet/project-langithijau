package com.studyplanner.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomWindowDecorator {

    private static final double TITLE_BAR_HEIGHT = 40;
    private double xOffset = 0;
    private double yOffset = 0;

    public static void decorate(Stage stage, String title, boolean darkMode) {
        new CustomWindowDecorator().applyDecoration(stage, title, darkMode);
    }

    private void applyDecoration(Stage stage, String title, boolean darkMode) {
        try {
            stage.initStyle(StageStyle.UNDECORATED);
        } catch (IllegalStateException e) {
            // Already initialized, ignore
        }

        Scene scene = stage.getScene();
        javafx.scene.Parent originalRoot = null;
        double width = 1200;
        double height = 800;

        if (scene != null) {
            originalRoot = scene.getRoot();
            width = scene.getWidth();
            height = scene.getHeight();
            // Check if original root has dark-mode
            if (originalRoot.getStyleClass().contains("dark-mode")) {
                darkMode = true;
            }
        }

        BorderPane root = new BorderPane();
        root.getStyleClass().add("custom-window");

        HBox titleBar = createTitleBar(stage, title, darkMode);
        root.setTop(titleBar);

        if (originalRoot != null) {
            root.setCenter(originalRoot);
        }

        Scene newScene = new Scene(root, width, height);

        if (scene != null && !scene.getStylesheets().isEmpty()) {
            newScene.getStylesheets().addAll(scene.getStylesheets());
        }

        if (darkMode) {
            newScene.getRoot().getStyleClass().add("dark-mode");
        }

        stage.setScene(newScene);

        enableDragging(titleBar, stage);
        enableResizing(root, stage);
    }

    private HBox createTitleBar(Stage stage, String title, boolean darkMode) {
        HBox titleBar = new HBox();
        titleBar.getStyleClass().add("custom-title-bar");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPrefHeight(TITLE_BAR_HEIGHT);
        titleBar.setMinHeight(TITLE_BAR_HEIGHT);
        titleBar.setMaxHeight(TITLE_BAR_HEIGHT);
        titleBar.setPadding(new Insets(0, 0, 0, 20));
        titleBar.setSpacing(0);

        String bgColor = darkMode ? "#0f172a" : "#ffffff";
        String textColor = darkMode ? "#f8fafc" : "#0f172a";
        String borderColor = darkMode ? "#1e293b" : "#e2e8f0";

        titleBar.setStyle(
            "-fx-background-color: " +
                bgColor +
                ";" +
                "-fx-border-color: transparent transparent " +
                borderColor +
                " transparent;" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: " +
                textColor +
                ";"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonBox = new HBox(0);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPrefHeight(TITLE_BAR_HEIGHT);
        buttonBox.setMinHeight(TITLE_BAR_HEIGHT);
        buttonBox.setMaxHeight(TITLE_BAR_HEIGHT);

        Button minimizeBtn = createWindowButton("−", darkMode);
        minimizeBtn.setOnAction(e -> stage.setIconified(true));

        Button maximizeBtn = createWindowButton("□", darkMode);
        String maxStyle =
            maximizeBtn.getStyle() +
            "-fx-font-size: 14px; -fx-translate-y: -1;";
        maximizeBtn.setStyle(maxStyle);

        // Fix hover to preserve translate-y
        String maxHoverBg = darkMode ? "#1e293b" : "#f1f5f9";
        maximizeBtn.setOnMouseEntered(e ->
            maximizeBtn.setStyle(
                maxStyle + "-fx-background-color: " + maxHoverBg + ";"
            )
        );
        maximizeBtn.setOnMouseExited(e -> maximizeBtn.setStyle(maxStyle));

        maximizeBtn.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));

        Button closeBtn = createCloseButton(darkMode);
        closeBtn.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(minimizeBtn, maximizeBtn, closeBtn);

        titleBar.getChildren().addAll(titleLabel, spacer, buttonBox);

        return titleBar;
    }

    private Button createWindowButton(String text, boolean darkMode) {
        Button button = new Button(text);

        String btnTextColor = darkMode ? "#cbd5e1" : "#475569";
        String hoverBg = darkMode ? "#1e293b" : "#f1f5f9";

        button.setMinSize(46, TITLE_BAR_HEIGHT);
        button.setPrefSize(46, TITLE_BAR_HEIGHT);
        button.setMaxSize(46, TITLE_BAR_HEIGHT);

        String baseStyle =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " +
            btnTextColor +
            ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-family: 'Segoe UI', sans-serif;" +
            "-fx-border-width: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0;";

        button.setStyle(baseStyle);
        button.setAlignment(Pos.CENTER);

        button.setOnMouseEntered(e ->
            button.setStyle(
                baseStyle + "-fx-background-color: " + hoverBg + ";"
            )
        );

        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    private Button createCloseButton(boolean darkMode) {
        Button button = new Button("×");

        String btnTextColor = "#ef4444";
        String hoverBg = "#ef4444";

        button.setMinSize(46, TITLE_BAR_HEIGHT);
        button.setPrefSize(46, TITLE_BAR_HEIGHT);
        button.setMaxSize(46, TITLE_BAR_HEIGHT);

        String baseStyle =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " +
            btnTextColor +
            ";" +
            "-fx-font-size: 18px;" +
            "-fx-font-family: 'Segoe UI', sans-serif;" +
            "-fx-border-width: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0;";

        button.setStyle(baseStyle);
        button.setAlignment(Pos.CENTER);

        button.setOnMouseEntered(e ->
            button.setStyle(
                baseStyle +
                    "-fx-background-color: " +
                    hoverBg +
                    ";" +
                    "-fx-text-fill: white;"
            )
        );

        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    private void enableDragging(HBox titleBar, Stage stage) {
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            if (!stage.isMaximized()) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        titleBar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
            }
        });
    }

    private void enableResizing(BorderPane root, Stage stage) {
        final double RESIZE_MARGIN = 5;

        root.setOnMouseMoved(event -> {
            if (stage.isMaximized()) {
                root.setCursor(Cursor.DEFAULT);
                return;
            }

            double x = event.getX();
            double y = event.getY();
            double width = root.getWidth();
            double height = root.getHeight();

            if (x < RESIZE_MARGIN && y < RESIZE_MARGIN) {
                root.setCursor(Cursor.NW_RESIZE);
            } else if (x > width - RESIZE_MARGIN && y < RESIZE_MARGIN) {
                root.setCursor(Cursor.NE_RESIZE);
            } else if (x < RESIZE_MARGIN && y > height - RESIZE_MARGIN) {
                root.setCursor(Cursor.SW_RESIZE);
            } else if (
                x > width - RESIZE_MARGIN && y > height - RESIZE_MARGIN
            ) {
                root.setCursor(Cursor.SE_RESIZE);
            } else if (x < RESIZE_MARGIN) {
                root.setCursor(Cursor.W_RESIZE);
            } else if (x > width - RESIZE_MARGIN) {
                root.setCursor(Cursor.E_RESIZE);
            } else if (y < RESIZE_MARGIN) {
                root.setCursor(Cursor.N_RESIZE);
            } else if (y > height - RESIZE_MARGIN) {
                root.setCursor(Cursor.S_RESIZE);
            } else {
                root.setCursor(Cursor.DEFAULT);
            }
        });

        root.setOnMouseDragged(event -> {
            if (stage.isMaximized()) return;

            Cursor cursor = root.getCursor();
            double deltaX = event.getScreenX() - stage.getX();
            double deltaY = event.getScreenY() - stage.getY();

            if (cursor == Cursor.E_RESIZE) {
                stage.setWidth(deltaX);
            } else if (cursor == Cursor.W_RESIZE) {
                double newWidth =
                    stage.getWidth() -
                    deltaX +
                    stage.getX() -
                    event.getScreenX();
                if (newWidth > stage.getMinWidth()) {
                    stage.setWidth(newWidth);
                    stage.setX(event.getScreenX());
                }
            } else if (cursor == Cursor.S_RESIZE) {
                stage.setHeight(deltaY);
            } else if (cursor == Cursor.N_RESIZE) {
                double newHeight =
                    stage.getHeight() -
                    deltaY +
                    stage.getY() -
                    event.getScreenY();
                if (newHeight > stage.getMinHeight()) {
                    stage.setHeight(newHeight);
                    stage.setY(event.getScreenY());
                }
            } else if (cursor == Cursor.SE_RESIZE) {
                stage.setWidth(deltaX);
                stage.setHeight(deltaY);
            } else if (cursor == Cursor.SW_RESIZE) {
                double newWidth =
                    stage.getWidth() -
                    deltaX +
                    stage.getX() -
                    event.getScreenX();
                if (newWidth > stage.getMinWidth()) {
                    stage.setWidth(newWidth);
                    stage.setX(event.getScreenX());
                }
                stage.setHeight(deltaY);
            } else if (cursor == Cursor.NE_RESIZE) {
                stage.setWidth(deltaX);
                double newHeight =
                    stage.getHeight() -
                    deltaY +
                    stage.getY() -
                    event.getScreenY();
                if (newHeight > stage.getMinHeight()) {
                    stage.setHeight(newHeight);
                    stage.setY(event.getScreenY());
                }
            } else if (cursor == Cursor.NW_RESIZE) {
                double newWidth =
                    stage.getWidth() -
                    deltaX +
                    stage.getX() -
                    event.getScreenX();
                double newHeight =
                    stage.getHeight() -
                    deltaY +
                    stage.getY() -
                    event.getScreenY();
                if (newWidth > stage.getMinWidth()) {
                    stage.setWidth(newWidth);
                    stage.setX(event.getScreenX());
                }
                if (newHeight > stage.getMinHeight()) {
                    stage.setHeight(newHeight);
                    stage.setY(event.getScreenY());
                }
            }
        });
    }
}
