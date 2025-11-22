package com.studyplanner.basisdata;

import com.studyplanner.utilitas.PencatatLog;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Platform;

public class PencatatQuery {
    private static PencatatQuery instance;
    private List<Consumer<String>> listeners = new ArrayList<>();

    private PencatatQuery() {}

    public static synchronized PencatatQuery getInstance() {
        if (instance == null) {
            instance = new PencatatQuery();
        }
        return instance;
    }

    public void catat(String query) {
        PencatatLog.db(query);
        
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String logMessage = "[" + timestamp + "] " + query;

        for (Consumer<String> listener : listeners) {
            Platform.runLater(() -> listener.accept(logMessage));
        }
    }

    public void tambahPendengar(Consumer<String> listener) {
        listeners.add(listener);
    }

    public void hapusPendengar(Consumer<String> listener) {
        listeners.remove(listener);
    }
}
