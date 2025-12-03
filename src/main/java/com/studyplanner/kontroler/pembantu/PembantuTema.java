package com.studyplanner.kontroler.pembantu;

import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.utilitas.PembuatIkon;
import com.studyplanner.utilitas.PreferensiPengguna;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Pembantu untuk mengelola tema (dark mode) aplikasi.
 * Memisahkan logika dark mode dari KontrolerUtama untuk SRP.
 */
public class PembantuTema {

    private boolean isDarkMode;
    private final Runnable onTemaChanged;

    /**
     * Konstruktor PembantuTema.
     *
     * @param onTemaChanged callback saat tema berubah
     */
    public PembantuTema(Runnable onTemaChanged) {
        this.isDarkMode = PreferensiPengguna.getInstance().isDarkMode();
        this.onTemaChanged = onTemaChanged;
    }

    /**
     * Cek apakah dark mode aktif.
     */
    public boolean isDarkMode() {
        return isDarkMode;
    }

    /**
     * Alihkan dark mode (toggle).
     *
     * @param tombolAlihTema tombol untuk update icon
     * @param rootNode node root untuk update style class
     * @param stage stage untuk update dekorasi window
     */
    public void alihkanModaGelap(Button tombolAlihTema, Node rootNode, Stage stage) {
        isDarkMode = !isDarkMode;
        PreferensiPengguna.getInstance().setDarkMode(isDarkMode);

        if (tombolAlihTema != null) {
            tombolAlihTema.setGraphic(PembuatIkon.ikonModeGelap(isDarkMode));
            tombolAlihTema.setText(isDarkMode ? "Terang" : "Gelap");
        }

        if (rootNode != null) {
            if (isDarkMode) {
                rootNode.getStyleClass().add("dark-mode");
            } else {
                rootNode.getStyleClass().remove("dark-mode");
            }
        }

        if (stage != null) {
            DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", isDarkMode);
        }

        if (onTemaChanged != null) {
            onTemaChanged.run();
        }
    }

    /**
     * Terapkan dark mode pada startup (jika sudah tersimpan di preferensi).
     *
     * @param rootNode node root untuk update style class
     * @param stage stage untuk update dekorasi window
     */
    public void terapkanPadaStartup(Node rootNode, Stage stage) {
        if (!isDarkMode) return;

        if (rootNode != null) {
            rootNode.getStyleClass().add("dark-mode");
        }

        if (stage != null) {
            DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", isDarkMode);
        }
    }

    /**
     * Terapkan dark mode dengan Scene dan ManajerWidgetDashboard.
     */
    public void terapkanModaGelap(Scene scene, ManajerWidgetDashboard manajerWidget) {
        if (scene == null) return;
        
        scene.getRoot().getStyleClass().add("dark-mode");
        
        Stage stage = (Stage) scene.getWindow();
        if (stage != null) {
            DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", isDarkMode);
        }
        
        if (manajerWidget != null) {
            manajerWidget.aturModeGelap(isDarkMode);
        }
    }

    /**
     * Alihkan dark mode dengan Scene dan ManajerWidgetDashboard.
     */
    public void alihkanModaGelap(Button tombolAlihTema, Scene scene, ManajerWidgetDashboard manajerWidget) {
        isDarkMode = !isDarkMode;
        PreferensiPengguna.getInstance().setDarkMode(isDarkMode);

        if (tombolAlihTema != null) {
            tombolAlihTema.setGraphic(PembuatIkon.ikonModeGelap(isDarkMode));
            tombolAlihTema.setText(isDarkMode ? "Terang" : "Gelap");
        }

        if (manajerWidget != null) {
            manajerWidget.aturModeGelap(isDarkMode);
            manajerWidget.setDarkMode(isDarkMode);
        }

        if (scene != null) {
            if (isDarkMode) {
                scene.getRoot().getStyleClass().add("dark-mode");
            } else {
                scene.getRoot().getStyleClass().remove("dark-mode");
            }

            Stage stage = (Stage) scene.getWindow();
            if (stage != null) {
                DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", isDarkMode);
            }
        }

        if (onTemaChanged != null) {
            onTemaChanged.run();
        }
    }
}
