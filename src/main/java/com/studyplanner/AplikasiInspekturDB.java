package com.studyplanner;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.kontroler.KontrolerInspekturBasisData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AplikasiInspekturDB extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InspekturBasisData.fxml"));
        Parent root = loader.load();

        ManajerBasisData manajerBasisData = new ManajerBasisData();

        KontrolerInspekturBasisData controller = loader.getController();
        controller.setManajerBasisData(manajerBasisData);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        
        DekoratorJendelaKustom.dekorasi(stage, "Inspektur Basis Data (Standalone)", false);
        
        stage.setOnCloseRequest(e -> {
            manajerBasisData.tutup();
            System.exit(0);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
