package com.studyplanner.kontroler.pembantu;

import com.studyplanner.utilitas.PembuatIkon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Pembantu untuk mengelola navigasi SPA-style.
 * Mengelola perpindahan halaman tanpa membuka window baru.
 */
public class PembantuNavigasi {

    /**
     * Enum untuk halaman yang tersedia.
     */
    public enum Halaman {
        DASHBOARD,
        PENGATURAN,
        MANAJEMEN_MATKUL,
        LIHAT_JADWAL
    }

    private final ScrollPane scrollPaneUtama;
    private Node kontenDashboardAsli;
    private Halaman halamanAktif;
    private final Runnable onKembaliKeDashboard;

    // Referensi tombol sidebar untuk update selection
    private Button tombolKelolaMataKuliah;
    private Button tombolLihatJadwal;
    private Button tombolPengaturan;

    /**
     * Konstruktor PembantuNavigasi.
     *
     * @param scrollPaneUtama ScrollPane utama untuk swap konten
     * @param onKembaliKeDashboard callback saat kembali ke dashboard
     */
    public PembantuNavigasi(ScrollPane scrollPaneUtama, Runnable onKembaliKeDashboard) {
        this.scrollPaneUtama = scrollPaneUtama;
        this.halamanAktif = Halaman.DASHBOARD;
        this.onKembaliKeDashboard = onKembaliKeDashboard;
    }

    /**
     * Set referensi tombol sidebar untuk update selection.
     */
    public void setTombolSidebar(Button kelolaMatkul, Button lihatJadwal, Button pengaturan) {
        this.tombolKelolaMataKuliah = kelolaMatkul;
        this.tombolLihatJadwal = lihatJadwal;
        this.tombolPengaturan = pengaturan;
    }

    /**
     * Dapatkan halaman yang sedang aktif.
     */
    public Halaman getHalamanAktif() {
        return halamanAktif;
    }

    /**
     * Simpan konten dashboard asli untuk dikembalikan nanti.
     */
    public void simpanKontenDashboard() {
        if (kontenDashboardAsli == null && scrollPaneUtama != null) {
            kontenDashboardAsli = scrollPaneUtama.getContent();
        }
    }

    /**
     * Navigasi ke halaman tertentu dengan konten yang diberikan.
     *
     * @param halaman halaman tujuan
     * @param konten konten yang akan ditampilkan
     */
    public void navigasiKe(Halaman halaman, Node konten) {
        // Jika bukan di dashboard, kembali dulu
        if (halamanAktif != Halaman.DASHBOARD) {
            kembaliKeDashboard();
        }

        simpanKontenDashboard();
        halamanAktif = halaman;

        // Update sidebar selection
        updateSidebarSelection(halaman);

        // Swap konten
        if (scrollPaneUtama != null) {
            scrollPaneUtama.setContent(konten);
            scrollPaneUtama.setVvalue(0);
        }
    }

    /**
     * Kembali ke dashboard.
     */
    public void kembaliKeDashboard() {
        if (halamanAktif == Halaman.DASHBOARD || kontenDashboardAsli == null) return;

        // Clear sidebar selection
        updateSidebarSelection((Button) null);

        if (scrollPaneUtama != null) {
            scrollPaneUtama.setContent(kontenDashboardAsli);
            scrollPaneUtama.setVvalue(0);
        }

        halamanAktif = Halaman.DASHBOARD;

        // Callback untuk refresh data
        if (onKembaliKeDashboard != null) {
            onKembaliKeDashboard.run();
        }
    }

    /**
     * Update state selected pada tombol sidebar berdasarkan Halaman.
     */
    private void updateSidebarSelection(Halaman halaman) {
        Button selected = switch (halaman) {
            case MANAJEMEN_MATKUL -> tombolKelolaMataKuliah;
            case LIHAT_JADWAL -> tombolLihatJadwal;
            case PENGATURAN -> tombolPengaturan;
            default -> null;
        };
        updateSidebarSelection(selected);
    }

    /**
     * Update state selected pada tombol sidebar berdasarkan Button.
     * Public method untuk digunakan oleh KontrolerUtama.
     */
    public void updateSidebarSelection(Button selected) {
        // Reset semua tombol
        resetSidebarButton(tombolKelolaMataKuliah);
        resetSidebarButton(tombolLihatJadwal);
        resetSidebarButton(tombolPengaturan);

        // Tambah selected ke tombol yang aktif
        if (selected != null) {
            selected.getStyleClass().add("sidebar-btn-selected");
            if (selected.getGraphic() instanceof FontIcon icon) {
                icon.setIconColor(Color.WHITE);
            }
        }
    }

    /**
     * Reset sidebar button ke state normal.
     */
    private void resetSidebarButton(Button button) {
        if (button == null) return;
        button.getStyleClass().remove("sidebar-btn-selected");
        // Reset icon ke warna default
        if (button.getGraphic() instanceof FontIcon icon) {
            icon.setIconColor(Color.web("#42474e"));
        }
    }

    /**
     * Buat wrapper dengan header dan tombol kembali.
     *
     * @param judul judul halaman
     * @param konten konten halaman
     * @return VBox wrapper dengan header
     */
    public VBox buatWrapperDenganHeader(String judul, Node konten) {
        VBox wrapper = new VBox(16);
        wrapper.setPadding(new Insets(24));

        HBox header = buatHeaderDenganTombolKembali(judul);

        VBox.setVgrow(konten, Priority.ALWAYS);
        wrapper.getChildren().addAll(header, konten);

        return wrapper;
    }

    /**
     * Buat header dengan tombol kembali.
     *
     * @param judul judul halaman
     * @return HBox header
     */
    public HBox buatHeaderDenganTombolKembali(String judul) {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        Button tombolKembali = new Button();
        tombolKembali.setGraphic(PembuatIkon.ikonKembali());
        tombolKembali.getStyleClass().add("btn-icon");
        tombolKembali.setOnAction(_ -> kembaliKeDashboard());

        Label labelJudul = new Label(judul);
        labelJudul.getStyleClass().add("section-title");
        labelJudul.setStyle("-fx-font-size: 24px;");

        header.getChildren().addAll(tombolKembali, labelJudul);
        return header;
    }
}
