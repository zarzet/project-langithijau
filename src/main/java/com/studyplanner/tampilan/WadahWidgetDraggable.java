package com.studyplanner.tampilan;

import com.studyplanner.model.KonfigurasiWidget;
import com.studyplanner.model.KonfigurasiWidget.JenisWidget;
import com.studyplanner.utilitas.PembuatIkon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Container untuk widget dengan kemampuan drag & drop untuk reorder.
 */
@SuppressWarnings("this-escape")
public class WadahWidgetDraggable extends HBox {

    private final List<JenisWidget> widgetList;
    private final Consumer<KonfigurasiWidget> onConfigChanged;
    private Consumer<Void> onTambahWidgetClicked;
    private WidgetFactory widgetFactory;

    /**
     * Factory untuk membuat instance widget berdasarkan jenisnya.
     */
    public interface WidgetFactory {
        Node buatWidget(JenisWidget jenis);
    }

    public WadahWidgetDraggable(KonfigurasiWidget konfigurasi, 
                                 WidgetFactory factory,
                                 Consumer<KonfigurasiWidget> onConfigChanged) {
        this.widgetList = new ArrayList<>(konfigurasi.getWidgetAktif());
        this.widgetFactory = factory;
        this.onConfigChanged = onConfigChanged;

        setSpacing(16);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(8, 0, 8, 0));

        refreshWidgets();
    }

    public void setOnTambahWidgetClicked(Consumer<Void> handler) {
        this.onTambahWidgetClicked = handler;
    }

    /**
     * Refresh tampilan widget berdasarkan list saat ini.
     */
    public void refreshWidgets() {
        getChildren().clear();

        if (widgetList.isEmpty()) {
            // Tampilan kosong dengan tombol tambah
            getChildren().add(buatTampilanKosong());
        } else {
            // Tambah setiap widget dengan wrapper draggable
            for (int i = 0; i < widgetList.size(); i++) {
                JenisWidget jenis = widgetList.get(i);
                Node widgetWrapper = buatWidgetWrapper(jenis, i);
                getChildren().add(widgetWrapper);
            }
            
            // Tombol tambah widget di akhir
            getChildren().add(buatTombolTambahKecil());
        }
    }

    private VBox buatTampilanKosong() {
        VBox container = new VBox(6);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(12, 30, 12, 30));
        container.getStyleClass().add("widget-empty-state");

        var ikon = PembuatIkon.ikonTambahWidget();
        ikon.setStyle("-fx-font-size: 24px;");

        Label judul = new Label("Belum ada widget");
        judul.getStyleClass().add("widget-empty-title");
        judul.setStyle("-fx-font-size: 13px;");

        Button btnTambah = new Button("Tambah");
        btnTambah.getStyleClass().add("btn-primary");
        btnTambah.setStyle("-fx-padding: 6 16; -fx-font-size: 12px;");
        btnTambah.setOnAction(_ -> {
            if (onTambahWidgetClicked != null) {
                onTambahWidgetClicked.accept(null);
            }
        });

        container.getChildren().addAll(ikon, judul, btnTambah);

        // Klik container juga bisa
        container.setOnMouseClicked(_ -> {
            if (onTambahWidgetClicked != null) {
                onTambahWidgetClicked.accept(null);
            }
        });
        container.setCursor(javafx.scene.Cursor.HAND);

        return container;
    }

    private javafx.scene.layout.StackPane buatWidgetWrapper(JenisWidget jenis, int index) {
        javafx.scene.layout.StackPane wrapper = new javafx.scene.layout.StackPane();
        wrapper.getStyleClass().add("widget-draggable-wrapper");

        // Widget content (di bawah)
        Node widgetContent = widgetFactory.buatWidget(jenis);

        // Header overlay (di atas, hanya muncul saat hover)
        HBox header = new HBox(4);
        header.setAlignment(Pos.TOP_RIGHT);
        header.getStyleClass().add("widget-drag-header");
        header.setPadding(new Insets(4));
        header.setPickOnBounds(false); // Agar click bisa tembus ke widget

        var dragHandle = PembuatIkon.ikonDragHandle();
        dragHandle.getStyleClass().add("drag-handle");
        dragHandle.setCursor(javafx.scene.Cursor.MOVE);

        Button btnHapus = new Button("×");
        btnHapus.getStyleClass().add("widget-remove-btn");
        btnHapus.setOnAction(_ -> hapusWidget(index));

        header.getChildren().addAll(dragHandle, btnHapus);

        // Stack: widget di bawah, header di atas
        wrapper.getChildren().addAll(widgetContent, header);
        javafx.scene.layout.StackPane.setAlignment(header, Pos.TOP_RIGHT);

        // Setup drag and drop
        setupDragAndDrop(wrapper, index);

        return wrapper;
    }

    private void setupDragAndDrop(javafx.scene.layout.StackPane wrapper, int index) {
        // Drag detection
        wrapper.setOnDragDetected(event -> {
            Dragboard db = wrapper.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(index));
            db.setContent(content);
            
            wrapper.getStyleClass().add("widget-dragging");
            event.consume();
        });

        // Drag over
        wrapper.setOnDragOver(event -> {
            if (event.getGestureSource() != wrapper && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                wrapper.getStyleClass().add("widget-drag-over");
            }
            event.consume();
        });

        // Drag exited
        wrapper.setOnDragExited(event -> {
            wrapper.getStyleClass().remove("widget-drag-over");
            event.consume();
        });

        // Drop
        wrapper.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                int sourceIndex = Integer.parseInt(db.getString());
                int targetIndex = index;

                if (sourceIndex != targetIndex) {
                    // Pindahkan widget
                    JenisWidget widget = widgetList.remove(sourceIndex);
                    widgetList.add(targetIndex, widget);
                    
                    // Refresh tampilan
                    refreshWidgets();
                    
                    // Notify perubahan
                    notifyConfigChanged();
                    
                    success = true;
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });

        // Drag done
        wrapper.setOnDragDone(event -> {
            wrapper.getStyleClass().remove("widget-dragging");
            event.consume();
        });
    }

    private Button buatTombolTambahKecil() {
        Button btn = new Button();
        btn.setGraphic(PembuatIkon.ikonTambahWidget());
        btn.getStyleClass().add("widget-add-btn-small");
        btn.setOnAction(_ -> {
            if (onTambahWidgetClicked != null) {
                onTambahWidgetClicked.accept(null);
            }
        });
        return btn;
    }

    private void hapusWidget(int index) {
        if (index >= 0 && index < widgetList.size()) {
            widgetList.remove(index);
            refreshWidgets();
            notifyConfigChanged();
        }
    }

    private void notifyConfigChanged() {
        if (onConfigChanged != null) {
            KonfigurasiWidget newConfig = new KonfigurasiWidget(widgetList);
            onConfigChanged.accept(newConfig);
        }
    }

    /**
     * Update konfigurasi widget (dipanggil setelah dialog pemilih).
     */
    public void updateKonfigurasi(KonfigurasiWidget konfigurasi) {
        widgetList.clear();
        widgetList.addAll(konfigurasi.getWidgetAktif());
        refreshWidgets();
    }

    public KonfigurasiWidget getKonfigurasiSaatIni() {
        return new KonfigurasiWidget(widgetList);
    }
}
