# Penggunaan Ikon Material Design dengan Ikonli

## Tentang Ikonli

Ikonli adalah library icon untuk JavaFX yang mirip dengan React Icons. Library ini mendukung berbagai icon pack seperti:
- Material Design Icons (yang kita gunakan)
- FontAwesome
- Bootstrap Icons
- Dan banyak lagi

## Cara Menggunakan

### 1. Melalui Helper Class `PembuatIkon`

Cara termudah adalah menggunakan helper class yang sudah disediakan:

```java
import com.studyplanner.utilitas.PembuatIkon;

// Contoh penggunaan di kontroler
Button myButton = new Button("Kelola Mata Kuliah");
myButton.setGraphic(PembuatIkon.ikonMataKuliah());
myButton.setGraphicTextGap(8);
```

### 2. Ikon yang Tersedia

#### Ikon Navigasi
- `PembuatIkon.ikonMataKuliah()` - Ikon buku untuk kelola mata kuliah
- `PembuatIkon.ikonJadwal()` - Ikon kalender untuk jadwal
- `PembuatIkon.ikonBuatJadwal()` - Ikon schedule untuk buat jadwal

#### Ikon User & Menu
- `PembuatIkon.ikonProfil()` - Ikon profil pengguna
- `PembuatIkon.ikonPengaturan()` - Ikon pengaturan (gear)
- `PembuatIkon.ikonKeluar()` - Ikon logout
- `PembuatIkon.ikonMenu()` - Ikon hamburger menu

#### Ikon Action Buttons
- `PembuatIkon.ikonTambah()` - Ikon + untuk tombol tambah
- `PembuatIkon.ikonEdit()` - Ikon pencil untuk tombol edit
- `PembuatIkon.ikonHapus()` - Ikon trash untuk tombol hapus
- `PembuatIkon.ikonTutup()` - Ikon X untuk tombol close

#### Ikon Status & Alert
- `PembuatIkon.ikonSelesai()` - Ikon check circle
- `PembuatIkon.ikonPeringatan()` - Ikon warning
- `PembuatIkon.ikonError()` - Ikon error
- `PembuatIkon.ikonInfo()` - Ikon info

#### Ikon Fitur
- `PembuatIkon.ikonBelajar()` - Ikon school/graduation
- `PembuatIkon.ikonStatistik()` - Ikon bar chart
- `PembuatIkon.ikonTimer()` - Ikon stopwatch
- `PembuatIkon.ikonStreak()` - Ikon fire untuk runtutan
- `PembuatIkon.ikonReview()` - Ikon review
- `PembuatIkon.ikonTugas()` - Ikon assignment

#### Ikon Dynamic
- `PembuatIkon.ikonModeGelap(boolean)` - Ikon yang berubah sesuai mode

### 3. Membuat Ikon Custom

Jika ingin membuat ikon dengan ukuran atau warna custom:

```java
import org.kordamp.ikonli.material2.Material2OutlinedAL;

// Ikon dengan ukuran custom
FontIcon icon = PembuatIkon.buat(Material2OutlinedAL.HOME, 24);

// Ikon dengan ukuran dan warna custom
FontIcon icon = PembuatIkon.buat(Material2OutlinedAL.HOME, 24, "#006495");

// Ikon dengan style CSS
FontIcon icon = PembuatIkon.buatDenganStyle(
    Material2OutlinedAL.HOME,
    24,
    "-fx-icon-color: red; -fx-icon-size: 32px;"
);
```

### 4. Mencari Ikon Material Design

Untuk mencari ikon Material Design lainnya:

1. Kunjungi: https://fonts.google.com/icons
2. Cari ikon yang diinginkan
3. Lihat nama ikon (contoh: "calendar_today")
4. Konversi ke format Java:
   - Huruf besar untuk setiap kata
   - Ganti underscore dengan huruf besar
   - Contoh: `calendar_today` → `CALENDAR_TODAY`

5. Import class yang sesuai:
   - Huruf A-L: `Material2OutlinedAL`
   - Huruf M-Z: `Material2OutlinedMZ`

```java
import org.kordamp.ikonli.material2.Material2OutlinedAL;

// Contoh ikon "calendar_today"
FontIcon icon = PembuatIkon.buat(Material2OutlinedAL.CALENDAR_TODAY, 20);
```

### 5. Menggunakan FontAwesome

Jika ingin menggunakan ikon FontAwesome:

```java
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

FontIcon icon = new FontIcon(FontAwesomeSolid.USER);
icon.setIconSize(20);
button.setGraphic(icon);
```

## Keuntungan Menggunakan Ikonli

1. **Scalable** - Ikon berbasis vector, tidak pecah saat diperbesar
2. **Customizable** - Bisa ubah ukuran dan warna dengan mudah
3. **Konsisten** - Ikon mengikuti desain Material Design
4. **Performance** - Lebih ringan dari gambar PNG/SVG
5. **Banyak Pilihan** - Ribuan ikon tersedia dari berbagai icon pack

## Styling Ikon dengan CSS

Anda juga bisa styling ikon melalui CSS:

```css
.ikonli-font-icon {
    -fx-icon-color: #006495;
    -fx-icon-size: 24px;
}

.ikonli-font-icon:hover {
    -fx-icon-color: #004a70;
}
```

## Contoh Implementasi Lengkap

```java
// Di kontroler
@FXML
private Button saveButton;

@FXML
private Button cancelButton;

@Override
public void initialize(URL location, ResourceBundle resources) {
    // Setup ikon save
    saveButton.setGraphic(PembuatIkon.ikonSelesai());
    saveButton.setGraphicTextGap(8);
    saveButton.setText("Simpan");

    // Setup ikon cancel
    cancelButton.setGraphic(PembuatIkon.ikonTutup());
    cancelButton.setGraphicTextGap(8);
    cancelButton.setText("Batal");
}
```

## Dependencies Maven

```xml
<properties>
    <ikonli.version>12.3.1</ikonli.version>
</properties>

<dependencies>
    <!-- Ikonli Core -->
    <dependency>
        <groupId>org.kordamp.ikonli</groupId>
        <artifactId>ikonli-javafx</artifactId>
        <version>${ikonli.version}</version>
    </dependency>

    <!-- Material Design Icons -->
    <dependency>
        <groupId>org.kordamp.ikonli</groupId>
        <artifactId>ikonli-material2-pack</artifactId>
        <version>${ikonli.version}</version>
    </dependency>

    <!-- FontAwesome (Optional) -->
    <dependency>
        <groupId>org.kordamp.ikonli</groupId>
        <artifactId>ikonli-fontawesome5-pack</artifactId>
        <version>${ikonli.version}</version>
    </dependency>
</dependencies>
```

## Resources

- Ikonli GitHub: https://github.com/kordamp/ikonli
- Material Icons: https://fonts.google.com/icons
- FontAwesome: https://fontawesome.com/icons
