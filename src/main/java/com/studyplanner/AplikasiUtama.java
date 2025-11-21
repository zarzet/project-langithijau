package com.studyplanner;

import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.layanan.LayananPelatihanFSRS;
import com.studyplanner.utilitas.ManajerOtentikasi;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AplikasiUtama extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        ManajerOtentikasi auth = ManajerOtentikasi.getInstance();
        auth.cobaPulihkanSesi();

        boolean isLoggedIn = auth.isLoggedIn();
        String fxmlFile = isLoggedIn ? "/fxml/MainView.fxml" : "/fxml/LoginView.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", false);

        stage.show();

        // Latihan FSRS otomatis di background (non-blocking UI).
        Platform.runLater(this::latihFsrsOtomatis);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void latihFsrsOtomatis() {
        new Thread(() -> {
            try {
                ManajerBasisData manajerBasisData = new ManajerBasisData();
                LayananPelatihanFSRS layananPelatihanFSRS = new LayananPelatihanFSRS(manajerBasisData);
                LayananPelatihanFSRS.HasilLatihOtomatis hasil = layananPelatihanFSRS.latihJikaPerlu();

                if (hasil.dilakukan()) {
                    System.out.println("[FSRS] Latihan otomatis selesai. Loss=" + hasil.loss() +
                            ", Akurasi=" + (hasil.akurasi() * 100.0) + "%, data=" + hasil.jumlahItem() + " item");
                } else {
                    System.out.println("[FSRS] Latihan otomatis dilewati: " + hasil.alasan());
                }
            } catch (Exception e) {
                System.err.println("[FSRS] Latihan otomatis gagal: " + e.getMessage());
            }
        }, "fsrs-auto-train").start();
    }
}
