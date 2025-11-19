package com.studyplanner.tampilan;

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

public class DekoratorJendelaKustom {

    private static final double TINGGI_TITLE_BAR = 40;
    private double xOffset = 0;
    private double yOffset = 0;

    public static void dekorasi(Stage stage, String judul, boolean modeGelap) {
        new DekoratorJendelaKustom().terapkanDekorasi(stage, judul, modeGelap);
    }

    private void terapkanDekorasi(Stage stage, String judul, boolean modeGelap) {
        try {
            stage.initStyle(StageStyle.UNDECORATED);
        } catch (IllegalStateException e) {
            // Sudah diinisialisasi, abaikan
        }

        Scene scene = stage.getScene();
        javafx.scene.Parent originalRoot = null;
        double lebar = 1200;
        double tinggi = 800;

        if (scene != null) {
            originalRoot = scene.getRoot();
            lebar = scene.getWidth();
            tinggi = scene.getHeight();
            // Cek jika root asli memiliki dark-mode
            if (originalRoot.getStyleClass().contains("dark-mode")) {
                modeGelap = true;
            }
        }

        BorderPane root = new BorderPane();
        root.getStyleClass().add("custom-window");

        HBox titleBar = buatTitleBar(stage, judul, modeGelap);
        root.setTop(titleBar);

        if (originalRoot != null) {
            root.setCenter(originalRoot);
        }

        Scene sceneBaru = new Scene(root, lebar, tinggi);

        if (scene != null && !scene.getStylesheets().isEmpty()) {
            sceneBaru.getStylesheets().addAll(scene.getStylesheets());
        }

        if (modeGelap) {
            sceneBaru.getRoot().getStyleClass().add("dark-mode");
        }

        stage.setScene(sceneBaru);

        aktifkanGeser(titleBar, stage);
        aktifkanUbahUkuran(root, stage);
    }

    private HBox buatTitleBar(Stage stage, String judul, boolean modeGelap) {
        HBox titleBar = new HBox();
        titleBar.getStyleClass().add("custom-title-bar");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPrefHeight(TINGGI_TITLE_BAR);
        titleBar.setMinHeight(TINGGI_TITLE_BAR);
        titleBar.setMaxHeight(TINGGI_TITLE_BAR);
        titleBar.setPadding(new Insets(0, 0, 0, 20));
        titleBar.setSpacing(0);

        String warnaLatar = modeGelap ? "#0f172a" : "#ffffff";
        String warnaTeks = modeGelap ? "#f8fafc" : "#0f172a";
        String warnaBorder = modeGelap ? "#1e293b" : "#e2e8f0";

        titleBar.setStyle(
                "-fx-background-color: " +
                        warnaLatar +
                        ";" +
                        "-fx-border-color: transparent transparent " +
                        warnaBorder +
                        " transparent;" +
                        "-fx-border-width: 0 0 1 0;");

        Label labelJudul = new Label(judul);
        labelJudul.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-text-fill: " +
                        warnaTeks +
                        ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox kotakTombol = new HBox(0);
        kotakTombol.setAlignment(Pos.CENTER);
        kotakTombol.setPrefHeight(TINGGI_TITLE_BAR);
        kotakTombol.setMinHeight(TINGGI_TITLE_BAR);
        kotakTombol.setMaxHeight(TINGGI_TITLE_BAR);

        Button tombolMinimize = buatTombolJendela("−", modeGelap);
        tombolMinimize.setOnAction(e -> stage.setIconified(true));

        Button tombolMaximize = buatTombolJendela("□", modeGelap);
        String gayaMax = tombolMaximize.getStyle() +
                "-fx-font-size: 14px; -fx-translate-y: -1;";
        tombolMaximize.setStyle(gayaMax);

        // Perbaiki hover untuk mempertahankan translate-y
        String maxHoverBg = modeGelap ? "#1e293b" : "#f1f5f9";
        tombolMaximize.setOnMouseEntered(e -> tombolMaximize.setStyle(
                gayaMax + "-fx-background-color: " + maxHoverBg + ";"));
        tombolMaximize.setOnMouseExited(e -> tombolMaximize.setStyle(gayaMax));

        tombolMaximize.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));

        Button tombolTutup = buatTombolTutup(modeGelap);
        tombolTutup.setOnAction(e -> stage.close());

        kotakTombol.getChildren().addAll(tombolMinimize, tombolMaximize, tombolTutup);

        titleBar.getChildren().addAll(labelJudul, spacer, kotakTombol);

        return titleBar;
    }

    private Button buatTombolJendela(String teks, boolean modeGelap) {
        Button tombol = new Button(teks);

        String warnaTeksTombol = modeGelap ? "#cbd5e1" : "#475569";
        String hoverBg = modeGelap ? "#1e293b" : "#f1f5f9";

        tombol.setMinSize(46, TINGGI_TITLE_BAR);
        tombol.setPrefSize(46, TINGGI_TITLE_BAR);
        tombol.setMaxSize(46, TINGGI_TITLE_BAR);

        String gayaDasar = "-fx-background-color: transparent;" +
                "-fx-text-fill: " +
                warnaTeksTombol +
                ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-family: 'Segoe UI', sans-serif;" +
                "-fx-border-width: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0;";

        tombol.setStyle(gayaDasar);
        tombol.setAlignment(Pos.CENTER);

        tombol.setOnMouseEntered(e -> tombol.setStyle(
                gayaDasar + "-fx-background-color: " + hoverBg + ";"));

        tombol.setOnMouseExited(e -> tombol.setStyle(gayaDasar));

        return tombol;
    }

    private Button buatTombolTutup(boolean modeGelap) {
        Button tombol = new Button("×");

        String warnaTeksBtn = "#ef4444";
        String hoverBg = "#ef4444";

        tombol.setMinSize(46, TINGGI_TITLE_BAR);
        tombol.setPrefSize(46, TINGGI_TITLE_BAR);
        tombol.setMaxSize(46, TINGGI_TITLE_BAR);

        String gayaDasar = "-fx-background-color: transparent;" +
                "-fx-text-fill: " +
                warnaTeksBtn +
                ";" +
                "-fx-font-size: 18px;" +
                "-fx-font-family: 'Segoe UI', sans-serif;" +
                "-fx-border-width: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0;";

        tombol.setStyle(gayaDasar);
        tombol.setAlignment(Pos.CENTER);

        tombol.setOnMouseEntered(e -> tombol.setStyle(
                gayaDasar +
                        "-fx-background-color: " +
                        hoverBg +
                        ";" +
                        "-fx-text-fill: white;"));

        tombol.setOnMouseExited(e -> tombol.setStyle(gayaDasar));

        return tombol;
    }

    private void aktifkanGeser(HBox titleBar, Stage stage) {
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

    private void aktifkanUbahUkuran(BorderPane root, Stage stage) {
        final double MARGIN_UBAH_UKURAN = 5;

        root.setOnMouseMoved(event -> {
            if (stage.isMaximized()) {
                root.setCursor(Cursor.DEFAULT);
                return;
            }

            double x = event.getX();
            double y = event.getY();
            double lebar = root.getWidth();
            double tinggi = root.getHeight();

            if (x < MARGIN_UBAH_UKURAN && y < MARGIN_UBAH_UKURAN) {
                root.setCursor(Cursor.NW_RESIZE);
            } else if (x > lebar - MARGIN_UBAH_UKURAN && y < MARGIN_UBAH_UKURAN) {
                root.setCursor(Cursor.NE_RESIZE);
            } else if (x < MARGIN_UBAH_UKURAN && y > tinggi - MARGIN_UBAH_UKURAN) {
                root.setCursor(Cursor.SW_RESIZE);
            } else if (x > lebar - MARGIN_UBAH_UKURAN && y > tinggi - MARGIN_UBAH_UKURAN) {
                root.setCursor(Cursor.SE_RESIZE);
            } else if (x < MARGIN_UBAH_UKURAN) {
                root.setCursor(Cursor.W_RESIZE);
            } else if (x > lebar - MARGIN_UBAH_UKURAN) {
                root.setCursor(Cursor.E_RESIZE);
            } else if (y < MARGIN_UBAH_UKURAN) {
                root.setCursor(Cursor.N_RESIZE);
            } else if (y > tinggi - MARGIN_UBAH_UKURAN) {
                root.setCursor(Cursor.S_RESIZE);
            } else {
                root.setCursor(Cursor.DEFAULT);
            }
        });

        root.setOnMouseDragged(event -> {
            if (stage.isMaximized())
                return;

            Cursor kursor = root.getCursor();
            double deltaX = event.getScreenX() - stage.getX();
            double deltaY = event.getScreenY() - stage.getY();

            if (kursor == Cursor.E_RESIZE) {
                stage.setWidth(deltaX);
            } else if (kursor == Cursor.W_RESIZE) {
                double lebarBaru = stage.getWidth() -
                        deltaX +
                        stage.getX() -
                        event.getScreenX();
                if (lebarBaru > stage.getMinWidth()) {
                    stage.setWidth(lebarBaru);
                    stage.setX(event.getScreenX());
                }
            } else if (kursor == Cursor.S_RESIZE) {
                stage.setHeight(deltaY);
            } else if (kursor == Cursor.N_RESIZE) {
                double tinggiBaru = stage.getHeight() -
                        deltaY +
                        stage.getY() -
                        event.getScreenY();
                if (tinggiBaru > stage.getMinHeight()) {
                    stage.setHeight(tinggiBaru);
                    stage.setY(event.getScreenY());
                }
            } else if (kursor == Cursor.SE_RESIZE) {
                stage.setWidth(deltaX);
                stage.setHeight(deltaY);
            } else if (kursor == Cursor.SW_RESIZE) {
                double lebarBaru = stage.getWidth() -
                        deltaX +
                        stage.getX() -
                        event.getScreenX();
                if (lebarBaru > stage.getMinWidth()) {
                    stage.setWidth(lebarBaru);
                    stage.setX(event.getScreenX());
                }
                stage.setHeight(deltaY);
            } else if (kursor == Cursor.NE_RESIZE) {
                stage.setWidth(deltaX);
                double tinggiBaru = stage.getHeight() -
                        deltaY +
                        stage.getY() -
                        event.getScreenY();
                if (tinggiBaru > stage.getMinHeight()) {
                    stage.setHeight(tinggiBaru);
                    stage.setY(event.getScreenY());
                }
            } else if (kursor == Cursor.NW_RESIZE) {
                double lebarBaru = stage.getWidth() -
                        deltaX +
                        stage.getX() -
                        event.getScreenX();
                double tinggiBaru = stage.getHeight() -
                        deltaY +
                        stage.getY() -
                        event.getScreenY();
                if (lebarBaru > stage.getMinWidth()) {
                    stage.setWidth(lebarBaru);
                    stage.setX(event.getScreenX());
                }
                if (tinggiBaru > stage.getMinHeight()) {
                    stage.setHeight(tinggiBaru);
                    stage.setY(event.getScreenY());
                }
            }
        });
    }
}
