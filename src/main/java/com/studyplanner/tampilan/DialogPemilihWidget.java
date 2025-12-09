package com.studyplanner.tampilan;

import com.studyplanner.model.KonfigurasiWidget;
import com.studyplanner.model.KonfigurasiWidget.JenisWidget;
import com.studyplanner.utilitas.PembuatIkon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.HashMap;
import java.util.Map;

/**
 * Dialog untuk memilih widget yang akan ditampilkan di dashboard.
 */
public class DialogPemilihWidget {

    private final Stage stage;
    private final KonfigurasiWidget konfigurasi;
    private final Map<JenisWidget, CheckBox> checkBoxMap;
    private final PemilihWidgetCallback callback;

    public interface PemilihWidgetCallback {
        void onKonfigurasiDisimpan(KonfigurasiWidget konfigurasi);
    }

    public DialogPemilihWidget(Stage parent, KonfigurasiWidget konfigurasiAwal, 
                               boolean isDarkMode, PemilihWidgetCallback callback) {
        this.konfigurasi = new KonfigurasiWidget(konfigurasiAwal.getWidgetAktif());
        this.checkBoxMap = new HashMap<>();
        this.callback = callback;

        stage = new Stage();
        stage.initOwner(parent);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Pilih Widget");

        VBox root = buatKonten();
        
        Scene scene = new Scene(root, 450, 500);
        scene.getStylesheets().add(
            getClass().getResource("/css/style.css").toExternalForm()
        );
        
        if (isDarkMode) {
            root.getStyleClass().add("dark-mode");
        }

        stage.setScene(scene);
        DekoratorJendelaKustom.dekorasi(stage, "Pilih Widget", isDarkMode);
    }

    private VBox buatKonten() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("widget-picker-dialog");

        // Header
        Label judulLabel = new Label("Pilih Widget");
        judulLabel.getStyleClass().add("dialog-title");

        Label deskripsiLabel = new Label("Centang widget yang ingin ditampilkan di dashboard Anda");
        deskripsiLabel.getStyleClass().add("dialog-subtitle");
        deskripsiLabel.setWrapText(true);

        VBox header = new VBox(8, judulLabel, deskripsiLabel);

        // Daftar widget
        VBox daftarWidget = new VBox(12);
        daftarWidget.setPadding(new Insets(8));

        for (JenisWidget jenis : JenisWidget.values()) {
            HBox item = buatItemWidget(jenis);
            daftarWidget.getChildren().add(item);
        }

        ScrollPane scrollPane = new ScrollPane(daftarWidget);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Tombol aksi
        HBox tombolBox = new HBox(12);
        tombolBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("btn-secondary");
        btnBatal.setOnAction(_ -> stage.close());

        Button btnSimpan = new Button("Simpan");
        btnSimpan.getStyleClass().add("btn-primary");
        btnSimpan.setGraphic(PembuatIkon.ikonSimpan());
        btnSimpan.setOnAction(_ -> simpan());

        tombolBox.getChildren().addAll(btnBatal, btnSimpan);

        root.getChildren().addAll(header, scrollPane, tombolBox);
        return root;
    }

    private HBox buatItemWidget(JenisWidget jenis) {
        HBox item = new HBox(16);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.getStyleClass().add("widget-picker-item");

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(konfigurasi.isWidgetAktif(jenis));
        checkBoxMap.put(jenis, checkBox);

        VBox infoBox = new VBox(4);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label namaLabel = new Label(jenis.getNamaDisplay());
        namaLabel.getStyleClass().add("widget-picker-item-title");

        Label deskripsiLabel = new Label(jenis.getDeskripsi());
        deskripsiLabel.getStyleClass().add("widget-picker-item-desc");
        deskripsiLabel.setWrapText(true);

        infoBox.getChildren().addAll(namaLabel, deskripsiLabel);

        // Ikon widget
        var ikon = getIkonUntukWidget(jenis);

        item.getChildren().addAll(checkBox, ikon, infoBox);
        
        // Klik seluruh item untuk toggle checkbox
        item.setOnMouseClicked(_ -> checkBox.setSelected(!checkBox.isSelected()));

        return item;
    }

    private javafx.scene.Node getIkonUntukWidget(JenisWidget jenis) {
        return switch (jenis) {
            case RUNTUTAN_BELAJAR -> PembuatIkon.ikonRuntutan();
            case JAM_ANALOG -> PembuatIkon.ikonJam();
            case WAKTU_BELAJAR -> PembuatIkon.ikonWaktuBelajar();
            case ULASAN_BERIKUTNYA -> PembuatIkon.ikonUlasan();
            case TUGAS_MENDATANG -> PembuatIkon.ikonTugasMendatang();
        };
    }

    private void simpan() {
        // Update konfigurasi berdasarkan checkbox
        // Buat list baru untuk menghindari bug clear() pada copy
        java.util.List<JenisWidget> widgetBaru = new java.util.ArrayList<>();
        
        // Iterasi sesuai urutan enum untuk konsistensi
        for (JenisWidget jenis : JenisWidget.values()) {
            CheckBox cb = checkBoxMap.get(jenis);
            if (cb != null && cb.isSelected()) {
                widgetBaru.add(jenis);
            }
        }
        
        // Set langsung ke konfigurasi (bukan clear + tambah satu-satu)
        konfigurasi.setWidgetAktif(widgetBaru);

        if (callback != null) {
            callback.onKonfigurasiDisimpan(konfigurasi);
        }
        stage.close();
    }

    public void tampilkan() {
        stage.showAndWait();
    }
}
