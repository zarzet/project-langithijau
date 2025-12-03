package com.studyplanner;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.utilitas.ManajerOtentikasi;
import javafx.application.Application;
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
        boolean sesiDipulihkan = auth.cobaPulihkanSesi();
        
        // Jika sesi dipulihkan (Google), load data user dari DB untuk dapat role
        if (sesiDipulihkan && auth.getCurrentUser() != null) {
            try {
                ManajerBasisData db = new ManajerBasisData();
                var userData = db.cariUserBerdasarkanGoogleId(auth.getCurrentUser().getId());
                if (userData != null) {
                    auth.setCurrentLocalUser(userData);
                }
            } catch (Exception e) {
                System.err.println("Gagal load user data: " + e.getMessage());
            }
        }

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
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
