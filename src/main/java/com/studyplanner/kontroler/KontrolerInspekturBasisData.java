package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.basisdata.PencatatQuery;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KontrolerInspekturBasisData implements Initializable {

    @FXML private Button clearLogBtn;
    @FXML private TextArea logArea;
    
    @FXML private ListView<String> tableList;
    @FXML private Button refreshTablesBtn;
    @FXML private Label selectedTableLabel;
    @FXML private TableView<Map<String, Object>> dataTableView;
    
    @FXML private TextArea queryInput;
    @FXML private Button executeBtn;
    @FXML private Label statusLabel;
    @FXML private TableView<Map<String, Object>> resultTableView;

    private ManajerBasisData manajerBasisData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup Log
        PencatatQuery.getInstance().tambahPendengar(this::tambahLog);
        clearLogBtn.setOnAction(e -> logArea.clear());
        
        // Setup Tables
        refreshTablesBtn.setOnAction(e -> muatDaftarTabel());
        tableList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                muatDataTabel(newVal);
            }
        });

        // Setup Query Executor
        executeBtn.setOnAction(e -> jalankanKueri());
        queryInput.setOnKeyPressed(this::handleQueryInputKey);

        // Auto scroll log
        logArea.textProperty().addListener((observable, oldValue, newValue) -> {
            logArea.setScrollTop(Double.MAX_VALUE); 
        });
    }

    public void setManajerBasisData(ManajerBasisData mb) {
        this.manajerBasisData = mb;
        muatDaftarTabel();
    }

    private void tambahLog(String msg) {
        logArea.appendText(msg + "\n");
    }

    private void muatDaftarTabel() {
        if (manajerBasisData == null) return;
        try {
            List<String> tables = manajerBasisData.ambilDaftarTabel();
            tableList.setItems(FXCollections.observableArrayList(tables));
        } catch (SQLException e) {
            if (statusLabel != null) statusLabel.setText("Gagal memuat tabel: " + e.getMessage());
        }
    }

    private void muatDataTabel(String tableName) {
        selectedTableLabel.setText("Tabel: " + tableName);
        jalankanSelectKeTabel("SELECT * FROM " + tableName, dataTableView);
    }

    private void handleQueryInputKey(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
            jalankanKueri();
        }
    }

    private void jalankanKueri() {
        if (manajerBasisData == null) return;
        String sql = queryInput.getText().trim();
        if (sql.isEmpty()) return;

        statusLabel.setText("Menjalankan...");
        statusLabel.setStyle("-fx-text-fill: black;");

        try {
            if (sql.toUpperCase().startsWith("SELECT")) {
                jalankanSelectKeTabel(sql, resultTableView);
                statusLabel.setText("Sukses: " + java.time.LocalTime.now());
                statusLabel.setStyle("-fx-text-fill: green;");
            } else {
                manajerBasisData.jalankanQueryUpdate(sql);
                statusLabel.setText("Update Sukses: " + java.time.LocalTime.now());
                statusLabel.setStyle("-fx-text-fill: green;");
                // Refresh table list just in case CREATE/DROP
                muatDaftarTabel();
            }
        } catch (SQLException e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void jalankanSelectKeTabel(String sql, TableView<Map<String, Object>> targetTable) {
        try {
            List<Map<String, Object>> rows = manajerBasisData.jalankanQuerySelect(sql);
            
            targetTable.getColumns().clear();
            if (!rows.isEmpty()) {
                Map<String, Object> firstRow = rows.get(0);
                for (String colName : firstRow.keySet()) {
                    TableColumn<Map<String, Object>, String> col = new TableColumn<>(colName);
                    col.setCellValueFactory(param -> {
                        Object val = param.getValue().get(colName);
                        return new SimpleStringProperty(val == null ? "NULL" : val.toString());
                    });
                    targetTable.getColumns().add(col);
                }
            }
            
            targetTable.setItems(FXCollections.observableArrayList(rows));
            
        } catch (SQLException e) {
            statusLabel.setText("Error Select: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
