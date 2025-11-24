package com.studyplanner;

/**
 * Launcher class untuk menjalankan aplikasi JavaFX dari Fat JAR.
 *
 * Ini diperlukan karena JavaFX memerlukan class yang TIDAK extend Application
 * sebagai entry point ketika dijalankan dari Fat JAR/native EXE.
 *
 * Tanpa ini, akan muncul error:
 * "Error: JavaFX runtime components are missing"
 */
public class Launcher {

    public static void main(String[] args) {
        // Panggil main method dari AplikasiUtama
        AplikasiUtama.main(args);
    }
}
