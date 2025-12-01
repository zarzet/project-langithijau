package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.basisdata.PencatatQuery;
import com.studyplanner.utilitas.PembuatIkon;
import java.net.URL;
import com.studyplanner.eksepsi.EksepsiAksesBasisData;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class KontrolerInspekturBasisData implements Initializable {

    @FXML private HBox headerIconBox;
    @FXML private VBox statCard1;
    @FXML private VBox statCard2;
    @FXML private VBox statCard3;

    @FXML private Label totalTablesLabel;
    @FXML private Label totalQueriesLabel;

    @FXML private Tab logTab;
    @FXML private Tab tableTab;
    @FXML private Tab sqlTab;

    @FXML private Button clearLogBtn;
    @FXML private TextArea logArea;
    @FXML private HBox infoFooter;

    @FXML private TextField searchTableField;
    @FXML private ListView<String> tableList;
    @FXML private Button refreshTablesBtn;
    @FXML private Label selectedTableLabel;
    @FXML private Label rowCountLabel;
    @FXML private TableView<Map<String, Object>> dataTableView;
    @FXML private VBox dataPlaceholder;

    @FXML private TextArea queryInput;
    @FXML private Button executeBtn;
    @FXML private Label statusLabel;
    @FXML private Label resultCountLabel;
    @FXML private TableView<Map<String, Object>> resultTableView;
    @FXML private VBox resultPlaceholder;

    private ManajerBasisData manajerBasisData;
    private List<String> semuaTabel;
    private int jumlahKueriLog = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupIkon();

        totalTablesLabel.setText("0");
        totalQueriesLabel.setText("0");

        PencatatQuery.getInstance().tambahPendengar(this::tambahLog);
        clearLogBtn.setOnAction(e -> {
            logArea.clear();
            jumlahKueriLog = 0;
            perbaruiLabelJumlahKueri();
        });

        refreshTablesBtn.setOnAction(e -> muatDaftarTabel());
        tableList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                muatDataTabel(newVal);
            }
        });

        if (searchTableField != null) {
            searchTableField.textProperty().addListener((observable, oldValue, newValue) -> {
                saringTabel(newValue);
            });
        }

        executeBtn.setOnAction(e -> jalankanKueri());
        queryInput.setOnKeyPressed(this::tanganiInputKeyKueri);

        logArea.textProperty().addListener((observable, oldValue, newValue) -> {
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    public void setManajerBasisData(ManajerBasisData mb) {
        this.manajerBasisData = mb;
        muatDaftarTabel();
    }

    private void setupIkon() {
        FontIcon headerIcon = new FontIcon(FontAwesomeSolid.DATABASE);
        headerIcon.setIconSize(32);
        headerIcon.getStyleClass().add("header-icon");
        headerIconBox.getChildren().add(0, headerIcon);

        HBox statIcon1 = new HBox(8);
        statIcon1.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        FontIcon icon1 = PembuatIkon.buat(Material2OutlinedMZ.TABLE_CHART, 20);
        icon1.getStyleClass().add("stat-icon");
        Label label1 = new Label("Total Tabel");
        label1.getStyleClass().add("stat-label");
        statIcon1.getChildren().addAll(icon1, label1);
        statCard1.getChildren().set(0, statIcon1);

        HBox statIcon2 = new HBox(8);
        statIcon2.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        FontIcon icon2 = PembuatIkon.buat(Material2OutlinedAL.DESCRIPTION, 20);
        icon2.getStyleClass().add("stat-icon");
        Label label2 = new Label("Log Queries");
        label2.getStyleClass().add("stat-label");
        statIcon2.getChildren().addAll(icon2, label2);
        statCard2.getChildren().set(0, statIcon2);

        HBox statIcon3 = new HBox(8);
        statIcon3.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        FontIcon icon3 = PembuatIkon.buat(Material2OutlinedAL.CHECK_CIRCLE, 20);
        icon3.getStyleClass().add("stat-icon");
        Label label3 = new Label("Status");
        label3.getStyleClass().add("stat-label");
        statIcon3.getChildren().addAll(icon3, label3);
        statCard3.getChildren().set(0, statIcon3);

        logTab.setGraphic(PembuatIkon.buat(Material2OutlinedAL.LIST_ALT, 18));
        tableTab.setGraphic(PembuatIkon.buat(Material2OutlinedMZ.TABLE_VIEW, 18));
        sqlTab.setGraphic(PembuatIkon.buat(Material2OutlinedAL.CODE, 18));

        HBox clearBtnContent = new HBox(8);
        clearBtnContent.setAlignment(javafx.geometry.Pos.CENTER);
        Label clearLabel = new Label("Bersihkan Log");
        clearLabel.setStyle("-fx-text-fill: inherit;");
        clearBtnContent.getChildren().addAll(
            PembuatIkon.buat(Material2OutlinedAL.DELETE, 16),
            clearLabel
        );
        clearLogBtn.setGraphic(clearBtnContent);
        clearLogBtn.setText("");

        HBox refreshBtnContent = new HBox(8);
        refreshBtnContent.setAlignment(javafx.geometry.Pos.CENTER);
        Label refreshLabel = new Label("Segarkan");
        refreshLabel.setStyle("-fx-text-fill: white;");
        refreshBtnContent.getChildren().addAll(
            PembuatIkon.buat(Material2OutlinedMZ.REFRESH, 16),
            refreshLabel
        );
        refreshTablesBtn.setGraphic(refreshBtnContent);
        refreshTablesBtn.setText("");

        HBox executeBtnContent = new HBox(8);
        executeBtnContent.setAlignment(javafx.geometry.Pos.CENTER);
        Label executeLabel = new Label("Jalankan Query");
        executeLabel.setStyle("-fx-text-fill: white;");
        executeBtnContent.getChildren().addAll(
            PembuatIkon.buat(Material2OutlinedMZ.PLAY_ARROW, 18),
            executeLabel
        );
        executeBtn.setGraphic(executeBtnContent);
        executeBtn.setText("");

        infoFooter.getChildren().add(0, PembuatIkon.buat(Material2OutlinedAL.INFO, 14));

        if (dataPlaceholder != null) {
            dataPlaceholder.getChildren().add(0, PembuatIkon.buat(Material2OutlinedAL.INBOX, 48));
        }
        if (resultPlaceholder != null) {
            resultPlaceholder.getChildren().add(0, PembuatIkon.buat(Material2OutlinedAL.CODE, 48));
        }
    }

    private void tambahLog(String pesan) {
        logArea.appendText(pesan + "\n");
        jumlahKueriLog++;
        perbaruiLabelJumlahKueri();
    }

    private void perbaruiLabelJumlahKueri() {
        if (totalQueriesLabel != null) {
            totalQueriesLabel.setText(String.valueOf(jumlahKueriLog));
        }
    }

    private void muatDaftarTabel() {
        if (manajerBasisData == null) return;
        try {
            semuaTabel = manajerBasisData.ambilDaftarTabel();
            tableList.setItems(FXCollections.observableArrayList(semuaTabel));

            if (totalTablesLabel != null) {
                totalTablesLabel.setText(String.valueOf(semuaTabel.size()));
            }

            if (searchTableField != null && !searchTableField.getText().isEmpty()) {
                saringTabel(searchTableField.getText());
            }
        } catch (EksepsiAksesBasisData e) {
            if (statusLabel != null) statusLabel.setText("Gagal memuat tabel: " + e.getMessage());
        }
    }

    private void saringTabel(String tekstCari) {
        if (semuaTabel == null) return;

        if (tekstCari == null || tekstCari.trim().isEmpty()) {
            tableList.setItems(FXCollections.observableArrayList(semuaTabel));
        } else {
            String cariLowerCase = tekstCari.toLowerCase();
            List<String> tersaring = semuaTabel.stream()
                    .filter(tabel -> tabel.toLowerCase().contains(cariLowerCase))
                    .collect(Collectors.toList());
            tableList.setItems(FXCollections.observableArrayList(tersaring));
        }
    }

    private void muatDataTabel(String namaTabel) {
        selectedTableLabel.setText("Tabel: " + namaTabel);
        jalankanSelectKeTabel("SELECT * FROM " + namaTabel, dataTableView, true);
    }

    private void tanganiInputKeyKueri(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
            jalankanKueri();
        }
    }

    private void jalankanKueri() {
        if (manajerBasisData == null) return;
        String sql = queryInput.getText().trim();
        if (sql.isEmpty()) return;

        statusLabel.setText("Menjalankan...");
        statusLabel.setStyle("-fx-text-fill: #72777f;");

        try {
            if (sql.toUpperCase().startsWith("SELECT")) {
                jalankanSelectKeTabel(sql, resultTableView, false);
                statusLabel.setText("Sukses: " + java.time.LocalTime.now());
                statusLabel.setStyle("-fx-text-fill: #10b981;");
            } else {
                manajerBasisData.jalankanKueriUpdate(sql);
                statusLabel.setText("Pembaruan Berhasil: " + java.time.LocalTime.now());
                statusLabel.setStyle("-fx-text-fill: #10b981;");
                muatDaftarTabel();
            }
        } catch (EksepsiAksesBasisData e) {
            statusLabel.setText("Kesalahan: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #ba1a1a;");
        }
    }

    private void jalankanSelectKeTabel(String sql, TableView<Map<String, Object>> tabelTarget, boolean apakahTabelData) {
        try {
            List<Map<String, Object>> barisBaris = manajerBasisData.jalankanKueriSelect(sql);

            tabelTarget.getColumns().clear();
            if (!barisBaris.isEmpty()) {
                Map<String, Object> barisPertama = barisBaris.get(0);
                for (String namaKolom : barisPertama.keySet()) {
                    TableColumn<Map<String, Object>, String> kolom = new TableColumn<>(namaKolom);
                    kolom.setCellValueFactory(param -> {
                        Object nilai = param.getValue().get(namaKolom);
                        return new SimpleStringProperty(nilai == null ? "NULL" : nilai.toString());
                    });
                    tabelTarget.getColumns().add(kolom);
                }
            }

            tabelTarget.setItems(FXCollections.observableArrayList(barisBaris));

            if (apakahTabelData && rowCountLabel != null) {
                rowCountLabel.setText(barisBaris.size() + " baris");
            } else if (!apakahTabelData && resultCountLabel != null) {
                resultCountLabel.setText(barisBaris.size() + " hasil");
            }

        } catch (EksepsiAksesBasisData e) {
            if (statusLabel != null) {
                statusLabel.setText("Kesalahan Query: " + e.getMessage());
                statusLabel.setStyle("-fx-text-fill: #ba1a1a;");
            }
        }
    }
}
