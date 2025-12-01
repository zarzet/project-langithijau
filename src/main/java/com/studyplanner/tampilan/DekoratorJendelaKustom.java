package com.studyplanner.tampilan;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DekoratorJendelaKustom {

    private static final double TINGGI_TITLE_BAR = 40;
    private static final double RADIUS_SUDUT = 12;
    private static final double PADDING_BAYANGAN = 15;
    private double xOffset = 0;
    private double yOffset = 0;

    public static void dekorasi(Stage stage, String judul, boolean modeGelap) {
        new DekoratorJendelaKustom().terapkanDekorasi(stage, judul, modeGelap);
    }

    private void terapkanDekorasi(Stage stage, String judul, boolean modeGelap) {
        try {
            stage.initStyle(StageStyle.TRANSPARENT);
        } catch (IllegalStateException e) {
            // Stage sudah ditampilkan, abaikan
        }

        Scene scene = stage.getScene();
        javafx.scene.Parent originalRoot = null;
        double lebar = 1200;
        double tinggi = 800;

        if (scene != null) {
            originalRoot = scene.getRoot();
            lebar = scene.getWidth();
            tinggi = scene.getHeight();

            // Cek apakah sudah di-wrap sebelumnya
            if (originalRoot instanceof StackPane) {
                StackPane sp = (StackPane) originalRoot;
                if (sp.getStyleClass().contains("window-shadow-container") && !sp.getChildren().isEmpty()) {
                    javafx.scene.Node child = sp.getChildren().get(0);
                    if (child instanceof BorderPane) {
                        BorderPane bp = (BorderPane) child;
                        if (bp.getStyleClass().contains("custom-window") && bp.getCenter() instanceof javafx.scene.Parent) {
                            originalRoot = (javafx.scene.Parent) bp.getCenter();
                        }
                    }
                }
            } else if (originalRoot instanceof BorderPane) {
                BorderPane bp = (BorderPane) originalRoot;
                if (bp.getStyleClass().contains("custom-window") && bp.getCenter() instanceof javafx.scene.Parent) {
                    originalRoot = (javafx.scene.Parent) bp.getCenter();
                }
            }

            if (originalRoot != null) {
                originalRoot.getStyleClass().remove("dark-mode");
                originalRoot.getStyleClass().remove("light-mode");
            }
        }

        // Container utama dengan rounded corners
        BorderPane windowPane = new BorderPane();
        windowPane.getStyleClass().add("custom-window");

        String bgColor = modeGelap ? "#0f1419" : "#f8f9ff";
        windowPane.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: " + RADIUS_SUDUT + ";" +
            "-fx-border-radius: " + RADIUS_SUDUT + ";" +
            "-fx-border-color: " + (modeGelap ? "#2a3038" : "#d0d5dd") + ";" +
            "-fx-border-width: 1;"
        );

        // Clip untuk memastikan konten tidak keluar dari rounded corners
        Rectangle clip = new Rectangle();
        clip.setArcWidth(RADIUS_SUDUT * 2);
        clip.setArcHeight(RADIUS_SUDUT * 2);
        windowPane.setClip(clip);
        
        // Update clip size ketika window berubah ukuran
        windowPane.layoutBoundsProperty().addListener((_, _, bounds) -> {
            clip.setWidth(bounds.getWidth());
            clip.setHeight(bounds.getHeight());
        });

        HBox titleBar = buatTitleBar(stage, judul, modeGelap);
        windowPane.setTop(titleBar);

        if (originalRoot != null) {
            windowPane.setCenter(originalRoot);
        }

        // Wrapper dengan shadow
        StackPane shadowContainer = new StackPane(windowPane);
        shadowContainer.getStyleClass().add("window-shadow-container");
        shadowContainer.setPadding(new Insets(PADDING_BAYANGAN));
        shadowContainer.setStyle("-fx-background-color: transparent;");

        // Drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(12);
        shadow.setOffsetX(0);
        shadow.setOffsetY(4);
        shadow.setColor(Color.rgb(0, 0, 0, 0.25));
        windowPane.setEffect(shadow);

        Scene sceneBaru = new Scene(shadowContainer, lebar + PADDING_BAYANGAN * 2, tinggi + PADDING_BAYANGAN * 2);
        sceneBaru.setFill(Color.TRANSPARENT);

        if (scene != null && !scene.getStylesheets().isEmpty()) {
            sceneBaru.getStylesheets().addAll(scene.getStylesheets());
        }

        if (modeGelap) {
            windowPane.getStyleClass().add("dark-mode");
        } else {
            windowPane.getStyleClass().remove("dark-mode");
            windowPane.getStyleClass().add("light-mode");
        }

        stage.setScene(sceneBaru);
        
        // Handler untuk maximize - hilangkan shadow dan rounded corners
        stage.maximizedProperty().addListener((_, _, isMaximized) -> {
            if (isMaximized) {
                // Saat maximize: hapus padding, shadow, dan rounded corners
                shadowContainer.setPadding(Insets.EMPTY);
                windowPane.setEffect(null);
                windowPane.setClip(null);
                windowPane.setStyle(
                    "-fx-background-color: " + bgColor + ";" +
                    "-fx-background-radius: 0;" +
                    "-fx-border-radius: 0;" +
                    "-fx-border-width: 0;"
                );
            } else {
                // Saat restore: kembalikan padding, shadow, dan rounded corners
                shadowContainer.setPadding(new Insets(PADDING_BAYANGAN));
                windowPane.setEffect(shadow);
                windowPane.setClip(clip);
                windowPane.setStyle(
                    "-fx-background-color: " + bgColor + ";" +
                    "-fx-background-radius: " + RADIUS_SUDUT + ";" +
                    "-fx-border-radius: " + RADIUS_SUDUT + ";" +
                    "-fx-border-color: " + (modeGelap ? "#2a3038" : "#d0d5dd") + ";" +
                    "-fx-border-width: 1;"
                );
            }
        });

        aktifkanGeser(titleBar, stage);
        aktifkanUbahUkuran(shadowContainer, windowPane, stage);
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

        String warnaLatar = modeGelap ? "#171c21" : "#f2f3fa";
        String warnaTeks = modeGelap ? "#e1e2e9" : "#191c20";
        String warnaBorder = modeGelap ? "#42474e" : "#c2c7cf";

        // Title bar dengan rounded corners di atas
        titleBar.setStyle(
                "-fx-background-color: " + warnaLatar + ";" +
                "-fx-background-radius: " + RADIUS_SUDUT + " " + RADIUS_SUDUT + " 0 0;" +
                "-fx-border-color: transparent transparent " + warnaBorder + " transparent;" +
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

        String maxHoverBg = modeGelap ? "#1b2025" : "#ecedf4";
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

        String warnaTeksTombol = modeGelap ? "#c2c7cf" : "#42474e";
        String hoverBg = modeGelap ? "#1b2025" : "#ecedf4";

        tombol.setMinSize(46, TINGGI_TITLE_BAR);
        tombol.setPrefSize(46, TINGGI_TITLE_BAR);
        tombol.setMaxSize(46, TINGGI_TITLE_BAR);

        String gayaDasar = "-fx-background-color: transparent;" +
                "-fx-text-fill: " +
                warnaTeksTombol +
                ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-family: 'Google Sans', 'Segoe UI', sans-serif;" +
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

        String warnaTeksBtn = modeGelap ? "#ffb4ab" : "#ba1a1a";
        String hoverBg = modeGelap ? "#93000a" : "#ba1a1a";

        tombol.setMinSize(46, TINGGI_TITLE_BAR);
        tombol.setPrefSize(46, TINGGI_TITLE_BAR);
        tombol.setMaxSize(46, TINGGI_TITLE_BAR);

        String gayaDasar = "-fx-background-color: transparent;" +
                "-fx-text-fill: " +
                warnaTeksBtn +
                ";" +
                "-fx-font-size: 18px;" +
                "-fx-font-family: 'Google Sans', 'Segoe UI', sans-serif;" +
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

    private void aktifkanUbahUkuran(StackPane shadowContainer, BorderPane windowPane, Stage stage) {
        final double MARGIN_UBAH_UKURAN = 8;

        shadowContainer.setOnMouseMoved(event -> {
            if (stage.isMaximized()) {
                shadowContainer.setCursor(Cursor.DEFAULT);
                return;
            }

            double x = event.getX();
            double y = event.getY();
            double lebar = shadowContainer.getWidth();
            double tinggi = shadowContainer.getHeight();

            if (x < MARGIN_UBAH_UKURAN && y < MARGIN_UBAH_UKURAN) {
                shadowContainer.setCursor(Cursor.NW_RESIZE);
            } else if (x > lebar - MARGIN_UBAH_UKURAN && y < MARGIN_UBAH_UKURAN) {
                shadowContainer.setCursor(Cursor.NE_RESIZE);
            } else if (x < MARGIN_UBAH_UKURAN && y > tinggi - MARGIN_UBAH_UKURAN) {
                shadowContainer.setCursor(Cursor.SW_RESIZE);
            } else if (x > lebar - MARGIN_UBAH_UKURAN && y > tinggi - MARGIN_UBAH_UKURAN) {
                shadowContainer.setCursor(Cursor.SE_RESIZE);
            } else if (x < MARGIN_UBAH_UKURAN) {
                shadowContainer.setCursor(Cursor.W_RESIZE);
            } else if (x > lebar - MARGIN_UBAH_UKURAN) {
                shadowContainer.setCursor(Cursor.E_RESIZE);
            } else if (y < MARGIN_UBAH_UKURAN) {
                shadowContainer.setCursor(Cursor.N_RESIZE);
            } else if (y > tinggi - MARGIN_UBAH_UKURAN) {
                shadowContainer.setCursor(Cursor.S_RESIZE);
            } else {
                shadowContainer.setCursor(Cursor.DEFAULT);
            }
        });

        shadowContainer.setOnMouseDragged(event -> {
            if (stage.isMaximized())
                return;

            Cursor kursor = shadowContainer.getCursor();
            double deltaX = event.getScreenX() - stage.getX();
            double deltaY = event.getScreenY() - stage.getY();

            double minWidth = stage.getMinWidth() + PADDING_BAYANGAN * 2;
            double minHeight = stage.getMinHeight() + PADDING_BAYANGAN * 2;

            if (kursor == Cursor.E_RESIZE) {
                stage.setWidth(Math.max(deltaX, minWidth));
            } else if (kursor == Cursor.W_RESIZE) {
                double lebarBaru = stage.getWidth() - deltaX + stage.getX() - event.getScreenX();
                if (lebarBaru > minWidth) {
                    stage.setWidth(lebarBaru);
                    stage.setX(event.getScreenX());
                }
            } else if (kursor == Cursor.S_RESIZE) {
                stage.setHeight(Math.max(deltaY, minHeight));
            } else if (kursor == Cursor.N_RESIZE) {
                double tinggiBaru = stage.getHeight() - deltaY + stage.getY() - event.getScreenY();
                if (tinggiBaru > minHeight) {
                    stage.setHeight(tinggiBaru);
                    stage.setY(event.getScreenY());
                }
            } else if (kursor == Cursor.SE_RESIZE) {
                stage.setWidth(Math.max(deltaX, minWidth));
                stage.setHeight(Math.max(deltaY, minHeight));
            } else if (kursor == Cursor.SW_RESIZE) {
                double lebarBaru = stage.getWidth() - deltaX + stage.getX() - event.getScreenX();
                if (lebarBaru > minWidth) {
                    stage.setWidth(lebarBaru);
                    stage.setX(event.getScreenX());
                }
                stage.setHeight(Math.max(deltaY, minHeight));
            } else if (kursor == Cursor.NE_RESIZE) {
                stage.setWidth(Math.max(deltaX, minWidth));
                double tinggiBaru = stage.getHeight() - deltaY + stage.getY() - event.getScreenY();
                if (tinggiBaru > minHeight) {
                    stage.setHeight(tinggiBaru);
                    stage.setY(event.getScreenY());
                }
            } else if (kursor == Cursor.NW_RESIZE) {
                double lebarBaru = stage.getWidth() - deltaX + stage.getX() - event.getScreenX();
                double tinggiBaru = stage.getHeight() - deltaY + stage.getY() - event.getScreenY();
                if (lebarBaru > minWidth) {
                    stage.setWidth(lebarBaru);
                    stage.setX(event.getScreenX());
                }
                if (tinggiBaru > minHeight) {
                    stage.setHeight(tinggiBaru);
                    stage.setY(event.getScreenY());
                }
            }
        });
    }
}
