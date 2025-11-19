package com.studyplanner.kontroler;

import com.google.api.services.oauth2.model.Userinfo;
import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PencatatLog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class KontrolerLogin {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView gambarLatar;

    @FXML
    private Button googleLoginBtn;

    @FXML
    public void initialize() {
        googleLoginBtn.setOnAction(_ -> handleLogin());

        try {
            var imgUrl = getClass().getResource("/images/google_logo.jpg");
            if (imgUrl != null) {
                ImageView icon = new ImageView(new Image(imgUrl.toExternalForm()));
                icon.setFitHeight(20);
                icon.setFitWidth(20);
                icon.setPreserveRatio(true);
                googleLoginBtn.setGraphic(icon);
            }
        } catch (Exception e) {
        }
        
        siapkanPratinjauLatar();
        
        if (rootPane != null) {
             rootPane.getStyleClass().add("light-mode");
        }
        
        Platform.runLater(() -> {
            if (rootPane != null && rootPane.getScene() != null) {
                rootPane.getScene().setFill(Color.TRANSPARENT);
            }
        });
    }

    private void siapkanPratinjauLatar() {
        Image gambarPratinjau = null;
        try {
            var imgUrl = getClass().getResource("/images/app_preview.png");
            if (imgUrl == null) {
                 imgUrl = getClass().getResource("/images/app_preview.jpg");
            }
            
            if (imgUrl != null) {
                gambarPratinjau = new Image(imgUrl.toExternalForm());
            }
        } catch (Exception e) {
            PencatatLog.error("Gagal memuat gambar preview: " + e.getMessage());
        }

        if (gambarPratinjau != null && gambarLatar != null) {
            gambarLatar.setImage(gambarPratinjau);
            gambarLatar.setEffect(new BoxBlur(20, 20, 3));
            gambarLatar.fitWidthProperty().bind(rootPane.widthProperty());
            gambarLatar.fitHeightProperty().bind(rootPane.heightProperty());
        }
    }

    private void handleLogin() {
        googleLoginBtn.setDisable(true);
        googleLoginBtn.setText("Menghubungkan ke Google...");

        new Thread(() -> {
            try {
                ManajerOtentikasi auth = ManajerOtentikasi.getInstance();
                Userinfo user = auth.login();

                Platform.runLater(() -> {
                    if (user != null) {
                        PencatatLog.info("Login Berhasil: " + user.getName());
                        bukaAplikasiUtama();
                    } else {
                        resetButton();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    PencatatLog.error("Gagal login: " + e.getMessage());
                    googleLoginBtn.setText("Failed. Try Again.");
                    googleLoginBtn.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void resetButton() {
        googleLoginBtn.setDisable(false);
        googleLoginBtn.setText("Sign up with Google");
    }

    private void bukaAplikasiUtama() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) googleLoginBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            stage.setScene(scene);
            stage.centerOnScreen();
            
            DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", false);
            
        } catch (Exception e) {
            PencatatLog.error("Gagal membuka MainView: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
