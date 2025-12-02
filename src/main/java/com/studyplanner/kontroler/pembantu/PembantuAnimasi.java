package com.studyplanner.kontroler.pembantu;

import com.studyplanner.utilitas.AnimasiUtil;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Pembantu untuk mengelola animasi UI.
 * Memisahkan logika animasi dari KontrolerUtama untuk SRP.
 */
public class PembantuAnimasi {

    /**
     * Terapkan animasi masuk untuk semua elemen dashboard.
     *
     * @param sidebar sidebar container
     * @param welcomeParent parent dari label selamat datang
     * @param statsGrid grid statistik
     * @param activitySection section aktivitas
     * @param mainContentGrid grid konten utama
     */
    public void terapkanAnimasiMasuk(
            VBox sidebar,
            Node welcomeParent,
            GridPane statsGrid,
            VBox activitySection,
            GridPane mainContentGrid) {

        // Sidebar: slide in dari kiri dengan spring physics
        if (sidebar != null) {
            sidebar.setOpacity(0);
            sidebar.setTranslateX(-50);

            FadeTransition fadeSidebar = new FadeTransition(Duration.millis(400), sidebar);
            fadeSidebar.setFromValue(0.0);
            fadeSidebar.setToValue(1.0);
            fadeSidebar.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            TranslateTransition slideSidebar = new TranslateTransition(Duration.millis(500), sidebar);
            slideSidebar.setFromX(-50);
            slideSidebar.setToX(0);
            slideSidebar.setInterpolator(AnimasiUtil.SPRING_DEFAULT);

            ParallelTransition sidebarAnim = new ParallelTransition(fadeSidebar, slideSidebar);
            sidebarAnim.play();
        }

        // Welcome section: fade in dengan easing
        if (welcomeParent != null) {
            welcomeParent.setOpacity(0);
            FadeTransition fadeWelcome = new FadeTransition(Duration.millis(500), welcomeParent);
            fadeWelcome.setFromValue(0.0);
            fadeWelcome.setToValue(1.0);
            fadeWelcome.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);
            fadeWelcome.setDelay(Duration.millis(100));
            fadeWelcome.play();
        }

        // Stats grid: slide up dengan spring
        if (statsGrid != null) {
            statsGrid.setOpacity(0);
            statsGrid.setTranslateY(40);

            FadeTransition fadeStats = new FadeTransition(Duration.millis(400), statsGrid);
            fadeStats.setFromValue(0.0);
            fadeStats.setToValue(1.0);
            fadeStats.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            TranslateTransition slideStats = new TranslateTransition(Duration.millis(600), statsGrid);
            slideStats.setFromY(40);
            slideStats.setToY(0);
            slideStats.setInterpolator(AnimasiUtil.SPRING_DEFAULT);

            ParallelTransition statsAnim = new ParallelTransition(fadeStats, slideStats);
            statsAnim.setDelay(Duration.millis(150));
            statsAnim.play();
        }

        // Activity section: fade in
        if (activitySection != null) {
            activitySection.setOpacity(0);
            FadeTransition fadeActivity = new FadeTransition(Duration.millis(500), activitySection);
            fadeActivity.setFromValue(0.0);
            fadeActivity.setToValue(1.0);
            fadeActivity.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);
            fadeActivity.setDelay(Duration.millis(300));
            fadeActivity.play();
        }

        // Main content: slide up dengan spring
        if (mainContentGrid != null) {
            mainContentGrid.setOpacity(0);
            mainContentGrid.setTranslateY(40);

            FadeTransition fadeContent = new FadeTransition(Duration.millis(400), mainContentGrid);
            fadeContent.setFromValue(0.0);
            fadeContent.setToValue(1.0);
            fadeContent.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            TranslateTransition slideContent = new TranslateTransition(Duration.millis(600), mainContentGrid);
            slideContent.setFromY(40);
            slideContent.setToY(0);
            slideContent.setInterpolator(AnimasiUtil.SPRING_DEFAULT);

            ParallelTransition contentAnim = new ParallelTransition(fadeContent, slideContent);
            contentAnim.setDelay(Duration.millis(400));
            contentAnim.play();
        }
    }

    /**
     * Terapkan animasi hover pada tombol sidebar.
     *
     * @param button tombol yang akan diberi animasi
     */
    public void terapkanAnimasiHoverSidebarButton(Button button) {
        if (button == null) return;

        button.setOnMouseEntered(_ -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
            scaleUp.setToX(1.03);
            scaleUp.setToY(1.03);
            scaleUp.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            TranslateTransition slideRight = new TranslateTransition(Duration.millis(200), button);
            slideRight.setToX(6);
            slideRight.setInterpolator(AnimasiUtil.EASE_OUT_BACK);

            ParallelTransition hoverIn = new ParallelTransition(scaleUp, slideRight);
            hoverIn.play();
        });

        button.setOnMouseExited(_ -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(250), button);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.setInterpolator(AnimasiUtil.SPRING_SNAPPY);

            TranslateTransition slideBack = new TranslateTransition(Duration.millis(250), button);
            slideBack.setToX(0);
            slideBack.setInterpolator(AnimasiUtil.SPRING_SNAPPY);

            ParallelTransition hoverOut = new ParallelTransition(scaleDown, slideBack);
            hoverOut.play();
        });

        // Press animation
        button.setOnMousePressed(_ -> {
            ScaleTransition press = new ScaleTransition(Duration.millis(80), button);
            press.setToX(0.97);
            press.setToY(0.97);
            press.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);
            press.play();
        });

        button.setOnMouseReleased(_ -> {
            ScaleTransition release = new ScaleTransition(Duration.millis(200), button);
            release.setToX(1.03);
            release.setToY(1.03);
            release.setInterpolator(AnimasiUtil.SPRING_BOUNCY);
            release.play();
        });
    }

    /**
     * Toggle sidebar dengan animasi.
     *
     * @param sidebar sidebar container
     * @param show true untuk menampilkan, false untuk menyembunyikan
     */
    public void toggleSidebar(VBox sidebar, boolean show) {
        if (sidebar == null) return;

        if (show) {
            sidebar.setManaged(true);
            sidebar.setVisible(true);
            sidebar.setTranslateX(-240);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), sidebar);
            slideIn.setFromX(-240);
            slideIn.setToX(0);
            slideIn.setInterpolator(AnimasiUtil.SPRING_DEFAULT);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), sidebar);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            ParallelTransition showAnim = new ParallelTransition(slideIn, fadeIn);
            showAnim.play();
        } else {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), sidebar);
            slideOut.setFromX(0);
            slideOut.setToX(-240);
            slideOut.setInterpolator(AnimasiUtil.EASE_IN_OUT_CUBIC);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(250), sidebar);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            ParallelTransition hideAnim = new ParallelTransition(slideOut, fadeOut);
            hideAnim.setOnFinished(_ -> {
                sidebar.setManaged(false);
                sidebar.setVisible(false);
            });
            hideAnim.play();
        }
    }
}
