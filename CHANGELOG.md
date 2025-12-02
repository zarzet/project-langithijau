# Changelog

## [Unreleased] - 2 Desember 2025

### Dark Mode Easter Egg

#### Fitur Tersembunyi
- **Dark mode** sekarang menjadi Easter egg yang harus di-unlock
- Klik foto profil **10 kali** di halaman Pengaturan untuk membuka fitur
- Hint muncul pada klik ke-7, 8, 9: "* X klik lagi..."
- Status unlock tersimpan permanen di preferensi

#### Perubahan File
- **`PreferensiPengguna.java`** — Method baru `isDarkModeUnlocked()` dan `setDarkModeUnlocked()`
- **`KontrolerUtama.java`** — Logic Easter egg di `buatSectionProfil()`

---

### Toast Notification

#### Sistem Toast Baru
- **`UtilUI.java`** — Method `tampilkanToast()` untuk notifikasi non-blocking
  - Overload tanpa parameter Window (auto-detect)
  - Muncul di tengah bawah layar, hilang otomatis setelah 2 detik
  - Mendukung tema terang dan gelap

#### Dialog Info Diganti Toast
Semua notifikasi sukses CRUD diganti dari modal dialog ke toast:
- **`KontrolerManajemenMataKuliah.java`** — Mata kuliah, topik, jadwal ujian
- **`KontrolerUtama.java`** — Sesi selesai, generate jadwal
- **`KontrolerLogin.java`** — Pendaftaran berhasil

---

### SPA-Style Dialog Overlay

#### Infrastruktur Overlay
- **`MainView.fxml`** — Root diganti ke StackPane dengan overlay layer
- **`dialogs.css`** — CSS untuk `.dialog-overlay` dan `.dialog-container`
- **`KontrolerUtama.java`** — Method overlay:
  - `tampilkanDialogOverlay(Node konten)` — Tampilkan konten di overlay
  - `tutupDialogOverlay()` — Tutup dengan animasi fade
  - `tampilkanKonfirmasi()` — Dialog konfirmasi SPA-style

#### Dialog Keluar
- Menggunakan SPA overlay (bukan window terpisah)
- Klik di luar dialog untuk menutup

---

### Perbaikan Bug Dark Mode

#### Fix Sidebar Icons
- **`sidebar.css`** — Warna ikon unselected: `#c2c7cf` (terang)
- Hover: `#e1e2e9` (lebih terang)
- Hapus shadow pada tombol sidebar selected di dark mode

#### Fix Dialog Background
- **`dialogs.css`** — CSS selector diperbaiki dari `.dark-mode .md3-dialog-pane` ke `.dark-mode.md3-dialog-pane`
- Semua elemen form (text-area, spinner, combo-box) mendapat styling dark mode
- Hapus shadow (`-fx-effect: null`)

#### Fix Logout Error
- **`KontrolerUtama.java`** — Method `getStage()` untuk mendapatkan Stage dari berbagai node
- Mencegah NullPointerException saat `labelSelamatDatang.getScene()` null

---

### Peningkatan Test Coverage (51%)

#### Target Coverage Tercapai
- **Coverage**: 42% → **51%** (dari 49 kelas)
- **Tests**: 225 → **271 tests** (semua passed)
- **Metode**: Tanpa exclude kelas non-UI

#### Test Files Baru
| File | Deskripsi |
|------|-----------|
| `LayananTopikTest.java` | Integration tests untuk cascade delete, CRUD, dan spaced repetition |
| `LayananSesiBelajarTest.java` | Tests untuk CRUD sesi dan validasi |
| `LayananJadwalUjianTest.java` | Tests untuk CRUD ujian, deadline calculation |
| `LayananMataKuliahTest.java` | Tests untuk CRUD dan cascade delete |
| `DAOJadwalUjianTest.java` | Integration tests untuk persistence ujian |
| `DAOPenggunaTest.java` | Tests untuk user management |
| `DAOSesiBelajarTest.java` | Tests untuk sesi belajar DAO |
| `DAOTopikTest.java` | Extended tests untuk topik DAO |
| `KonfigurasiWidgetTest.java` | Unit tests untuk model widget |
| `KonfigurasiJadwalTest.java` | Tests untuk konfigurasi jadwal |
| `PengulanganBerjarakTest.java` | Tests untuk algoritma spaced repetition |
| `PembuatJadwalTest.java` | Tests untuk jadwal generator |
| `EksepsiTest.java` | Tests untuk custom exception classes |
| `KonfigurasiAplikasiTest.java` | Tests untuk singleton config |
| `PreferensiPenggunaTest.java` | Tests untuk user preferences |
| `PencatatLogTest.java` | Tests untuk logging utility |

#### Coverage Per Package
| Package | Coverage |
|---------|----------|
| `model` | 90% |
| `dao` | 65% |
| `layanan` | 63% |
| `algoritma` | 62% |
| `eksepsi` | 43% |
| `utilitas` | 15% (UI-related) |
| `basisdata` | 14% |

#### Konfigurasi JaCoCo
- **Version**: 0.8.14
- **Excluded**: `kontroler/**`, `tampilan/**`, `AplikasiUtama`, `AplikasiInspekturDB`, `Launcher`
- **Included**: Semua kelas non-UI termasuk `utilitas`

---

### Refaktor Navigasi Sidebar

#### Pindah Tombol "Buat Jadwal" ke Pengaturan
- **`MainView.fxml`** — Hapus tombol "Buat Jadwal" dari sidebar
- **`KontrolerUtama.java`** — Tombol dipindah ke halaman Pengaturan sebagai "Generate Jadwal Manual"
  - Override manual untuk generate ulang jadwal 7 hari
  - Validasi: cek mata kuliah dan topik sebelum generate

#### Tombol Pengaturan di Sidebar
- **`MainView.fxml`** — Tambah `tombolPengaturan` di atas "Keluar"
- **`KontrolerUtama.java`** — Setup icon dan action untuk navigasi ke pengaturan
  - Navigasi berfungsi dari halaman manapun (kembali ke dashboard dulu jika perlu)
  - Tombol ter-highlight saat halaman pengaturan aktif

---

### Perbaikan Bug

#### Fix Navigasi ke Pengaturan
- **`KontrolerUtama.java`** — `tampilkanPengaturan()` sekarang bisa dipanggil dari halaman manapun
  - Jika bukan di dashboard, otomatis kembali dulu lalu buka pengaturan

#### Fix Spacing Profil di Pengaturan
- **`KontrolerUtama.java`** — `buatSectionProfil()` hanya tampilkan email jika ada
  - Kurangi VBox spacing dari 4px ke 2px
  - Tidak ada space kosong jika email tidak tersedia

#### Fix Today Indicator di Kalender
- **`KontrolerTampilanJadwal.java`** — Gunakan StackPane untuk overlay titik hari ini
  - Titik tidak lagi mendorong tanggal ke bawah
  - Semua kartu hari sejajar rata

#### Fix Konsistensi Tombol Kembali
- **`KontrolerUtama.java`** — Samakan padding wrapper Jadwal Belajar dengan Kelola Mata Kuliah
  - Kedua halaman: `VBox(16)` + `Insets(24)`

#### Fix Warna Sidebar Selected State
- **`sidebar.css`** — Samakan warna selected dengan hover
  - Gunakan `-color-primary` untuk keduanya (bukan `#1a7ab3`)

#### Cascade Delete Sesi Belajar
- **`DAOSesiBelajar.java`** — Method baru untuk cascade delete
  - `hapusBerdasarkanTopikId(int topikId)` — Hapus semua sesi berdasarkan ID topik
  - `hapusBerdasarkanMataKuliahId(int mataKuliahId)` — Hapus semua sesi berdasarkan ID mata kuliah
  - Ubah `LEFT JOIN` ke `INNER JOIN` untuk otomatis filter sesi orphan

- **`LayananTopik.java`** — Integrasi cascade delete
  - Tambah `DAOSesiBelajar` sebagai dependency
  - Panggil `daoSesiBelajar.hapusBerdasarkanTopikId()` di method `hapus()`

- **`LayananMataKuliah.java`** — Integrasi cascade delete
  - Tambah `DAOSesiBelajar` sebagai dependency
  - Panggil `daoSesiBelajar.hapusBerdasarkanMataKuliahId()` di method `hapus()`

---

### Perbaikan UI/UX

#### Sidebar Selection State
- **`sidebar.css`** — Styling untuk tombol sidebar yang aktif
  - `.sidebar-btn-selected` dengan warna `#1a7ab3` (lebih terang dari primary)
  - `.sidebar-btn-selected .ikonli-font-icon` untuk warna icon putih
  - Hover state dengan shadow lebih prominent

- **`KontrolerUtama.java`** — Logic untuk manage selection state
  - `updateSidebarSelection(Button selected)` — Update state selected pada sidebar
  - `resetSidebarButton(Button button)` — Reset tombol ke state normal
  - Icon color diubah programmatically: putih saat selected, `#42474e` saat normal

#### Konsistensi Header SPA
- **`KontrolerUtama.java`** — Jadwal Belajar menggunakan header konsisten
  - `bukaLihatJadwal()` menggunakan `buatHeaderDenganTombolKembali("Jadwal Belajar")`

- **`ScheduleView.fxml`** — Layout adjustment
  - Hapus label judul duplikat "Jadwal Belajar"
  - Alignment date picker row ke kanan (`CENTER_RIGHT`)

#### Konsistensi Ukuran Tombol Navigasi
- **`buttons.css`** — Fixed size untuk `.nav-arrow-btn`
  - Width: 40px (min, pref, max)
  - Height: 40px (min, pref, max)
  - Font size: 18px
  - Padding: 0

#### Konsistensi Warna Surface (Hapus Tint Biru)
- **`variables.css`** — Warna surface yang netral
  - `-color-background`: `#f8f9ff` → `#f8f9fa`
  - `-color-surface-container`: `#f2f3fa` → `#f5f6f7`
  - `-color-surface-variant`: `#e1e2e9` → `#e8e9eb`

#### Styling Form Elements di Dialog
- **`dialogs.css`** — Styling komprehensif untuk form elements
  - TextField dengan focus state (border primary, padding adjustment)
  - TextArea dengan background dan border radius
  - Spinner dengan styling custom
  - ComboBox dengan border styling
  - DatePicker dengan modern look
  - Full dark mode support untuk semua elements

---

### Fitur Baru

#### Sistem Widget Kustomisasi
- **`KonfigurasiWidget.java`** — Model untuk konfigurasi widget dashboard
  - Enum `JenisWidget`: `RUNTUTAN_BELAJAR`, `JAM_ANALOG`, `WAKTU_BELAJAR`, `ULASAN_BERIKUTNYA`, `TUGAS_MENDATANG`
  - Method serialisasi/deserialisasi: `keString()`, `dariString()`
  - Method untuk mengelola widget: `tambahWidget()`, `hapusWidget()`, `pindahWidget()`

- **`DialogPemilihWidget.java`** — Dialog untuk memilih widget yang ditampilkan
  - Checkbox untuk setiap jenis widget
  - Preview ikon untuk setiap widget
  - Tombol Simpan/Batal dengan callback

- **`WadahWidgetDraggable.java`** — Container dengan drag & drop support
  - Drag handle di pojok kanan atas (overlay, tidak mempengaruhi layout)
  - Tombol hapus widget (×)
  - Empty state dengan tombol "Tambah Widget"
  - Reorder widget via drag & drop

- **`PreferensiPengguna.java`** — Update untuk menyimpan konfigurasi widget
  - Key baru: `widget.config.<userId>`
  - Method: `getWidgetConfig()`, `setWidgetConfig()`

- **`KontrolerUtama.java`** — Integrasi sistem widget
  - Method `siapkanSistemWidget()` untuk inisialisasi
  - Method `buatWidgetDariJenis()` factory untuk membuat widget
  - Method `simpanKonfigurasiWidget()` untuk persistensi
  - Method `tampilkanDialogPemilihWidget()` untuk buka dialog

- **`MainView.fxml`** — Update layout dashboard
  - Mengganti multiple widget containers dengan single `widgetContainer` HBox
  - ScrollPane untuk horizontal scrolling jika banyak widget

#### Filter Data by User ID
- **`DAOTopik.java`** — Method baru untuk filter by userId (JOIN dengan mata_kuliah)
  - `ambilSemuaByUserId(int userId)`
  - `ambilTopikUntukDiulangByUserId(int userId, int mataKuliahId, LocalDate tanggal)`
  - `hitungByUserId(int userId)`
  - `hitungDikuasaiByUserId(int userId)`

- **`LayananTopik.java`** — Method wrapper untuk filter by userId
  - `ambilSemuaByUserId(int userId)`
  - `ambilTopikUntukDiulangByUserId(int userId, int idMataKuliah)`
  - `hitungByUserId(int userId)`
  - `hitungDikuasaiByUserId(int userId)`

#### Sistem Animasi Fluid
- **`AnimasiUtil.java`** — Utility class baru untuk animasi modern
  - Custom Interpolators: `EASE_OUT_CUBIC`, `EASE_IN_OUT_CUBIC`, `EASE_OUT_QUART`, `EASE_OUT_BACK`, `EASE_OUT_ELASTIC`
  - Spring Physics: `SPRING_DEFAULT`, `SPRING_SNAPPY`, `SPRING_BOUNCY`
  - Bezier Curves: `MATERIAL_STANDARD`, `MATERIAL_DECELERATE`, `MATERIAL_ACCELERATE`
  - Helper methods: `fadeIn()`, `fadeOut()`, `slideIn()`, `popIn()`, `shake()`, `pulse()`, `staggeredFadeIn()`

#### Ikon Navigasi
- **`PembuatIkon.java`** — Menambahkan method ikon panah
  - `ikonPanahKiri()` dan `ikonPanahKiri(int ukuran)`
  - `ikonPanahKanan()` dan `ikonPanahKanan(int ukuran)`
  - Menggunakan Material Design `CHEVRON_LEFT` dan `CHEVRON_RIGHT`

---

### Perbaikan UI/UX

#### Window Rounded Corners
- **`DekoratorJendelaKustom.java`** — Window dengan sudut melengkung
  - `StageStyle.TRANSPARENT` untuk background transparan
  - Radius sudut 12px pada window container
  - Drop shadow effect untuk depth
  - Clip rectangle untuk memastikan konten tidak keluar dari rounded corners
  - Title bar dengan rounded corners di bagian atas

#### Jam Analog Smooth
- **`JamAnalog.java`** — Animasi jam 60 FPS
  - Menggunakan `AnimationTimer` menggantikan `Timeline`
  - Interpolasi nanoseconds untuk gerakan jarum yang smooth
  - Jarum detik, menit, dan jam bergerak fluid tanpa "melompat"
  - Method `setSmoothSecond(boolean)` untuk toggle mode smooth
  - Method `stop()` untuk cleanup animasi

#### Animasi Dashboard
- **`KontrolerUtama.java`** — Animasi masuk dengan spring physics
  - Sidebar slide dengan `SPRING_DEFAULT`
  - Welcome section dengan `EASE_OUT_CUBIC`
  - Stats grid dengan staggered animation dan `EASE_OUT_BACK`
  - Button hover dengan scale dan translate animations
  - Button press dengan immediate feedback
  - Toggle sidebar dengan smooth spring animation

#### Dialog Onboarding
- **`DialogPengenalan.java`** — Transisi halaman yang fluid
  - Kombinasi fade + slide + scale untuk entrance
  - Spring physics untuk efek natural
  - `EASE_OUT_BACK` untuk bounce effect

#### Jadwal Belajar Navigation
- **`KontrolerTampilanJadwal.java`** — Tombol navigasi dengan ikon
  - Ikon panah Material Design menggantikan Unicode characters
  - Styling konsisten dengan tema aplikasi
- **`ScheduleView.fxml`** — Menghapus Unicode arrow characters (`◀`, `▶`)

---

### Styling CSS

#### DatePicker Modern
- **`forms.css`** — Styling DatePicker komprehensif
  - Popup dengan rounded corners 16px
  - Day cells berbentuk lingkaran dengan hover effect
  - Today ditandai dengan border primary
  - Selected dengan background primary
  - Drop shadow untuk depth
  - Dark mode support lengkap

#### Navigation Buttons
- **`buttons.css`** — Styling tombol navigasi
  - `.nav-arrow-btn .ikonli-font-icon` styling
  - Hover state dengan perubahan warna
  - Dark mode support untuk ikon

---

### Dialog System

#### Material Design 3 Dialogs
- **`PembuatDialogMD3.java`** — Rewrite lengkap untuk MD3 styling
  - `StageStyle.TRANSPARENT` dengan scene fill transparan
  - DialogPane dengan rounded corners 24px dan shadow
  - Recursive styling untuk semua form elements:
    - TextField dengan focus state
    - TextArea dengan rounded corners
    - Spinner dengan custom styling
    - ComboBox dengan border styling
    - DatePicker dengan modern look
  - Button styling dengan hover dan press effects:
    - Primary button (Simpan): background `#006495`, teks putih
    - Secondary button (Cancel): background `#d2e5f5`
  - Custom Material Design icons untuk Alert:
    - Information: `INFO` icon dengan background `#e3f2fd`
    - Warning: `WARNING` icon dengan background `#fff3e0`
    - Error: `ERROR` icon dengan background `#ffebee`
    - Confirmation: `HELP` icon dengan background `#e8f5e9`

---

### File yang Diubah

| File | Tipe | Deskripsi |
|------|------|-----------|
| `KonfigurasiWidget.java` | Baru | Model konfigurasi widget dashboard |
| `DialogPemilihWidget.java` | Baru | Dialog untuk memilih widget |
| `WadahWidgetDraggable.java` | Baru | Container drag & drop widget |
| `DAOTopik.java` | Diubah | Method filter by userId |
| `DAOSesiBelajar.java` | Diubah | Cascade delete + INNER JOIN |
| `LayananTopik.java` | Diubah | Method filter by userId + cascade delete |
| `LayananMataKuliah.java` | Diubah | Cascade delete sesi belajar |
| `PreferensiPengguna.java` | Diubah | Simpan konfigurasi widget |
| `MainView.fxml` | Diubah | Single widget container |
| `widgets.css` | Diubah | Styling widget picker & draggable |
| `AnimasiUtil.java` | Baru | Utility class untuk animasi fluid |
| `JamAnalog.java` | Diubah | AnimationTimer untuk 60 FPS |
| `KontrolerUtama.java` | Diubah | Integrasi widget + sidebar selection |
| `DialogPengenalan.java` | Diubah | Spring physics animations |
| `PembuatIkon.java` | Diubah | Menambah ikon widget & panah |
| `KontrolerTampilanJadwal.java` | Diubah | Ikon navigasi |
| `ScheduleView.fxml` | Diubah | Hapus Unicode arrows + label duplikat |
| `DekoratorJendelaKustom.java` | Diubah | Rounded window corners |
| `PembuatDialogMD3.java` | Diubah | MD3 dialog styling |
| `forms.css` | Diubah | DatePicker styling |
| `buttons.css` | Diubah | Nav button sizing & icon styling |
| `sidebar.css` | Diubah | Sidebar selection state styling |
| `dialogs.css` | Diubah | Form elements styling (TextField, TextArea, etc.) |
| `variables.css` | Diubah | Warna surface netral (hapus tint biru) |

---

### Warna Utama

| Mode | Primary | Background | Surface Container |
|------|---------|------------|-------------------|
| Light | `#006495` | `#f8f9fa` | `#f5f6f7` |
| Dark | `#8dcdff` | `#0f1419` | `#171c21` |
