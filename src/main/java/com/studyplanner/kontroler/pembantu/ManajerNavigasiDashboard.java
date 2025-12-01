package com.studyplanner.kontroler.pembantu;

import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.utilitas.UtilUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Mengelola navigasi ke berbagai tampilan dari dashboard.
 * Mengekstrak logika pembukaan jendela dari KontrolerUtama.
 */
public class ManajerNavigasiDashboard {

    private final Class<?> kelasReferensi;
    private boolean isDarkMode;
    private Runnable onWindowClosed;

    public ManajerNavigasiDashboard(Class<?> kelasReferensi, boolean isDarkMode) {
        this.kelasReferensi = kelasReferensi;
        this.isDarkMode = isDarkMode;
    }

    /**
     * Set callback yang dipanggil setelah window ditutup.
     */
    public void setOnWindowClosed(Runnable callback) {
        this.onWindowClosed = callback;
    }

    /**
     * Update status dark mode.
     */
    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

    /**
     * Buka tampilan manajemen mata kuliah.
     */
    public void bukaManajemenMataKuliah(Object kontrolerUtama) {
        try {
            FXMLLoader loader = new FXMLLoader(
                kelasReferensi.getResource("/fxml/CourseManagement.fxml"));
            Parent root = loader.load();

            Stage stage = buatStage("Manajemen Mata Kuliah & Topik", root, 1000, 700);
            
            // Set referensi ke kontroler utama jika diperlukan
            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass()
                        .getMethod("aturKontrolerUtama", kontrolerUtama.getClass())
                        .invoke(controller, kontrolerUtama);
                } catch (Exception e) {
                    // Method tidak ada atau gagal, abaikan
                }
            }

            stage.showAndWait();
            panggilCallback();
        } catch (IOException e) {
            UtilUI.tampilkanKesalahan("Gagal membuka manajemen mata kuliah: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Buka tampilan jadwal belajar.
     */
    public void bukaLihatJadwal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                kelasReferensi.getResource("/fxml/ScheduleView.fxml"));
            Parent root = loader.load();

            Stage stage = buatStage("Jadwal Belajar", root, 900, 700);
            stage.showAndWait();
        } catch (IOException e) {
            UtilUI.tampilkanKesalahan("Gagal membuka tampilan jadwal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Buka inspektor basis data.
     */
    public void bukaInspekturBasisData() {
        try {
            FXMLLoader loader = new FXMLLoader(
                kelasReferensi.getResource("/fxml/DatabaseInspector.fxml"));
            Parent root = loader.load();

            Stage stage = buatStage("Inspektor Basis Data", root, 1100, 750);
            stage.showAndWait();
        } catch (IOException e) {
            UtilUI.tampilkanKesalahan("Gagal membuka inspektor basis data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper untuk membuat Stage dengan konfigurasi standar.
     */
    private Stage buatStage(String judul, Parent root, int lebar, int tinggi) {
        Stage stage = new Stage();
        stage.setTitle(judul);
        
        Scene scene = new Scene(root, lebar, tinggi);
        scene.getStylesheets().add(
            kelasReferensi.getResource("/css/style.css").toExternalForm()
        );
        
        if (isDarkMode) {
            scene.getRoot().getStyleClass().add("dark-mode");
        }
        
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        DekoratorJendelaKustom.dekorasi(stage, judul, isDarkMode);
        
        return stage;
    }

    private void panggilCallback() {
        if (onWindowClosed != null) {
            onWindowClosed.run();
        }
    }
}
