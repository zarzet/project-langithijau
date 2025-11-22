package com.studyplanner.utilitas;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

public class KonfigurasiAplikasi {
    private static KonfigurasiAplikasi instance;
    private ConfigData config;

    private KonfigurasiAplikasi() {
        loadConfig();
    }

    public static KonfigurasiAplikasi getInstance() {
        if (instance == null) {
            instance = new KonfigurasiAplikasi();
        }
        return instance;
    }

    private void loadConfig() {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/config.json")))) {
            Gson gson = new Gson();
            config = gson.fromJson(reader, ConfigData.class);
        } catch (Exception e) {
            System.err.println("Gagal memuat config.json, menggunakan default.");
            e.printStackTrace();
            config = new ConfigData(); // Default fallback
        }
    }

    public boolean isDbLogEnabled() {
        return config != null && config.logging != null && config.logging.enable_db_log;
    }

    public boolean isAppLogEnabled() {
        return config != null && config.logging != null && config.logging.enable_app_log;
    }

    public String getAppVersion() {
        return (config != null && config.app != null) ? config.app.version : "1.0.0";
    }

    public String getAppLanguage() {
        return (config != null && config.app != null) ? config.app.language : "id";
    }

    private static class ConfigData {
        LoggingConfig logging = new LoggingConfig();
        AppConfig app = new AppConfig();
    }

    private static class LoggingConfig {
        boolean enable_db_log = true;
        boolean enable_app_log = true;
    }

    private static class AppConfig {
        String version = "1.0.0";
        String language = "id";
    }
}
