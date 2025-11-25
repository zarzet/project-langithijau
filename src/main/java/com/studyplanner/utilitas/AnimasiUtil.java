package com.studyplanner.utilitas;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Utility class untuk animasi fluid dan modern.
 * Menyediakan interpolator custom (easing curves, spring physics)
 * dan helper method untuk animasi umum.
 */
public final class AnimasiUtil {

    private AnimasiUtil() {
        // Utility class
    }

    // ============================================
    // CUSTOM INTERPOLATORS - Easing Curves
    // ============================================

    /**
     * Ease Out Cubic - Mulai cepat, melambat di akhir.
     * Ideal untuk elemen yang "mendarat" dengan lembut.
     */
    public static final Interpolator EASE_OUT_CUBIC = new Interpolator() {
        @Override
        protected double curve(double t) {
            return 1 - Math.pow(1 - t, 3);
        }
    };

    /**
     * Ease In Out Cubic - Smooth di awal dan akhir.
     * Ideal untuk transisi halaman atau panel.
     */
    public static final Interpolator EASE_IN_OUT_CUBIC = new Interpolator() {
        @Override
        protected double curve(double t) {
            return t < 0.5
                    ? 4 * t * t * t
                    : 1 - Math.pow(-2 * t + 2, 3) / 2;
        }
    };

    /**
     * Ease Out Quart - Lebih dramatis dari cubic.
     * Ideal untuk elemen yang "menabrak" posisi akhir.
     */
    public static final Interpolator EASE_OUT_QUART = new Interpolator() {
        @Override
        protected double curve(double t) {
            return 1 - Math.pow(1 - t, 4);
        }
    };

    /**
     * Ease Out Back - Sedikit melewati target lalu kembali.
     * Memberikan efek "bouncy" yang subtle.
     */
    public static final Interpolator EASE_OUT_BACK = new Interpolator() {
        private static final double C1 = 1.70158;
        private static final double C3 = C1 + 1;

        @Override
        protected double curve(double t) {
            return 1 + C3 * Math.pow(t - 1, 3) + C1 * Math.pow(t - 1, 2);
        }
    };

    /**
     * Ease Out Elastic - Efek elastis seperti pegas.
     * Ideal untuk notifikasi atau elemen yang muncul.
     */
    public static final Interpolator EASE_OUT_ELASTIC = new Interpolator() {
        private static final double C4 = (2 * Math.PI) / 3;

        @Override
        protected double curve(double t) {
            if (t == 0) return 0;
            if (t == 1) return 1;
            return Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * C4) + 1;
        }
    };

    // ============================================
    // SPRING PHYSICS INTERPOLATOR
    // ============================================

    /**
     * Membuat interpolator dengan fisika pegas.
     *
     * @param stiffness Kekakuan pegas (100-500 untuk hasil natural)
     * @param damping   Redaman (10-30 untuk hasil natural)
     * @return Interpolator dengan efek spring
     */
    public static Interpolator spring(double stiffness, double damping) {
        return new SpringInterpolator(stiffness, damping);
    }

    /**
     * Spring default - smooth dan bouncy.
     */
    public static final Interpolator SPRING_DEFAULT = spring(200, 20);

    /**
     * Spring snappy - lebih cepat settle.
     */
    public static final Interpolator SPRING_SNAPPY = spring(400, 30);

    /**
     * Spring bouncy - lebih banyak bounce.
     */
    public static final Interpolator SPRING_BOUNCY = spring(150, 12);

    private static class SpringInterpolator extends Interpolator {
        private final double stiffness;
        private final double damping;
        private final double mass = 1.0;

        SpringInterpolator(double stiffness, double damping) {
            this.stiffness = stiffness;
            this.damping = damping;
        }

        @Override
        protected double curve(double t) {
            // Simplified spring simulation
            double omega = Math.sqrt(stiffness / mass);
            double zeta = damping / (2 * Math.sqrt(stiffness * mass));

            if (zeta < 1) {
                // Underdamped
                double omegaD = omega * Math.sqrt(1 - zeta * zeta);
                return 1 - Math.exp(-zeta * omega * t) *
                        (Math.cos(omegaD * t) + (zeta * omega / omegaD) * Math.sin(omegaD * t));
            } else {
                // Critically/overdamped
                return 1 - (1 + omega * t) * Math.exp(-omega * t);
            }
        }
    }

    // ============================================
    // BEZIER CURVE INTERPOLATOR
    // ============================================

    /**
     * Membuat interpolator dari cubic bezier curve (seperti CSS).
     *
     * @param x1 Control point 1 X (0-1)
     * @param y1 Control point 1 Y
     * @param x2 Control point 2 X (0-1)
     * @param y2 Control point 2 Y
     * @return Interpolator dengan kurva bezier
     */
    public static Interpolator bezier(double x1, double y1, double x2, double y2) {
        return new CubicBezierInterpolator(x1, y1, x2, y2);
    }

    /**
     * Material Design standard easing.
     */
    public static final Interpolator MATERIAL_STANDARD = bezier(0.4, 0.0, 0.2, 1.0);

    /**
     * Material Design decelerate easing.
     */
    public static final Interpolator MATERIAL_DECELERATE = bezier(0.0, 0.0, 0.2, 1.0);

    /**
     * Material Design accelerate easing.
     */
    public static final Interpolator MATERIAL_ACCELERATE = bezier(0.4, 0.0, 1.0, 1.0);

    private static class CubicBezierInterpolator extends Interpolator {
        private final double x1, y1, x2, y2;

        CubicBezierInterpolator(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        protected double curve(double t) {
            // Newton-Raphson iteration untuk solve X, lalu evaluate Y
            double x = t;
            for (int i = 0; i < 8; i++) {
                double currentX = bezierX(x) - t;
                if (Math.abs(currentX) < 0.0001) break;
                double dx = bezierDX(x);
                if (Math.abs(dx) < 0.0001) break;
                x -= currentX / dx;
            }
            return bezierY(x);
        }

        private double bezierX(double t) {
            return 3 * (1 - t) * (1 - t) * t * x1 +
                    3 * (1 - t) * t * t * x2 +
                    t * t * t;
        }

        private double bezierY(double t) {
            return 3 * (1 - t) * (1 - t) * t * y1 +
                    3 * (1 - t) * t * t * y2 +
                    t * t * t;
        }

        private double bezierDX(double t) {
            return 3 * (1 - t) * (1 - t) * x1 +
                    6 * (1 - t) * t * (x2 - x1) +
                    3 * t * t * (1 - x2);
        }
    }

    // ============================================
    // ANIMATION HELPERS
    // ============================================

    /**
     * Animasi fade in dengan easing.
     */
    public static FadeTransition fadeIn(Node node, double durationMs) {
        FadeTransition fade = new FadeTransition(Duration.millis(durationMs), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(EASE_OUT_CUBIC);
        return fade;
    }

    /**
     * Animasi fade out dengan easing.
     */
    public static FadeTransition fadeOut(Node node, double durationMs) {
        FadeTransition fade = new FadeTransition(Duration.millis(durationMs), node);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setInterpolator(EASE_OUT_CUBIC);
        return fade;
    }

    /**
     * Animasi slide in dari arah tertentu dengan spring physics.
     */
    public static ParallelTransition slideIn(Node node, double durationMs,
                                              double fromX, double fromY) {
        node.setTranslateX(fromX);
        node.setTranslateY(fromY);
        node.setOpacity(0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(durationMs), node);
        slide.setToX(0);
        slide.setToY(0);
        slide.setInterpolator(SPRING_DEFAULT);

        FadeTransition fade = new FadeTransition(Duration.millis(durationMs * 0.6), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(EASE_OUT_CUBIC);

        return new ParallelTransition(slide, fade);
    }

    /**
     * Animasi slide dari kiri.
     */
    public static ParallelTransition slideInFromLeft(Node node, double durationMs) {
        return slideIn(node, durationMs, -50, 0);
    }

    /**
     * Animasi slide dari bawah.
     */
    public static ParallelTransition slideInFromBottom(Node node, double durationMs) {
        return slideIn(node, durationMs, 0, 30);
    }

    /**
     * Animasi scale dengan bounce effect.
     */
    public static ScaleTransition scaleWithBounce(Node node, double durationMs,
                                                   double fromScale, double toScale) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(durationMs), node);
        scale.setFromX(fromScale);
        scale.setFromY(fromScale);
        scale.setToX(toScale);
        scale.setToY(toScale);
        scale.setInterpolator(EASE_OUT_BACK);
        return scale;
    }

    /**
     * Animasi pop in - scale dari kecil ke normal dengan bounce.
     */
    public static ScaleTransition popIn(Node node, double durationMs) {
        node.setScaleX(0.8);
        node.setScaleY(0.8);
        return scaleWithBounce(node, durationMs, 0.8, 1.0);
    }

    /**
     * Animasi hover scale untuk button/card.
     */
    public static void setupHoverAnimation(Node node, double scaleUp) {
        node.setOnMouseEntered(_ -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), node);
            scale.setToX(scaleUp);
            scale.setToY(scaleUp);
            scale.setInterpolator(EASE_OUT_CUBIC);
            scale.play();
        });

        node.setOnMouseExited(_ -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), node);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setInterpolator(EASE_OUT_CUBIC);
            scale.play();
        });
    }

    /**
     * Animasi press effect untuk button.
     */
    public static void setupPressAnimation(Node node) {
        node.setOnMousePressed(_ -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), node);
            scale.setToX(0.95);
            scale.setToY(0.95);
            scale.setInterpolator(EASE_OUT_CUBIC);
            scale.play();
        });

        node.setOnMouseReleased(_ -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setInterpolator(SPRING_SNAPPY);
            scale.play();
        });
    }

    /**
     * Animasi shake untuk error feedback.
     */
    public static TranslateTransition shake(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(_ -> node.setTranslateX(0));
        return shake;
    }

    /**
     * Animasi pulse untuk menarik perhatian.
     */
    public static ScaleTransition pulse(Node node) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(300), node);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        pulse.setInterpolator(EASE_IN_OUT_CUBIC);
        return pulse;
    }

    /**
     * Membuat staggered animation untuk list items.
     *
     * @param nodes         List of nodes untuk di-animate
     * @param delayPerItem  Delay antar item dalam ms
     * @param durationMs    Durasi animasi per item
     */
    public static void staggeredFadeIn(java.util.List<? extends Node> nodes,
                                       double delayPerItem, double durationMs) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            node.setOpacity(0);
            node.setTranslateY(20);

            ParallelTransition anim = slideInFromBottom(node, durationMs);
            anim.setDelay(Duration.millis(i * delayPerItem));
            anim.play();
        }
    }

    /**
     * Smooth rotation untuk jam analog.
     */
    public static double smoothSecondRotation(int second, int millis) {
        // Interpolasi halus antara detik
        return (second + millis / 1000.0) * 6;
    }
}
