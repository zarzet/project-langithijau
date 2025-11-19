package com.studyplanner.utilitas;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PencatatLog {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void db(String message) {
        if (KonfigurasiAplikasi.getInstance().isDbLogEnabled()) {
            System.out.println(formatLog("DB", message));
        }
    }

    public static void app(String message) {
        if (KonfigurasiAplikasi.getInstance().isAppLogEnabled()) {
            System.out.println(formatLog("APP", message));
        }
    }

    public static void info(String message) {
        if (KonfigurasiAplikasi.getInstance().isAppLogEnabled()) {
            System.out.println(formatLog("INFO", message));
        }
    }

    public static void error(String message) {
        System.err.println(formatLog("ERROR", message));
    }

    private static String formatLog(String type, String message) {
        return String.format("[%s] [%s] %s",
                LocalTime.now().format(TIME_FORMATTER),
                type,
                message);
    }
}
