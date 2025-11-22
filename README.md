# Perencana Belajar Adaptif

Aplikasi desktop untuk membantu mahasiswa mengelola jadwal belajar dengan teknik **Spaced Repetition (SM-2)** dan sistem pembelajaran adaptif.

## 🚀 Quick Start

### Cara Tercepat (Windows)
1. **Double-click** file `run.bat`
2. Pilih menu yang diinginkan
3. Selesai!

### Menu yang Tersedia

```
[1] Aplikasi Utama              → Login & Dashboard
[2] Aplikasi Utama (Fast)       → Tanpa compile (lebih cepat)
[3] Inspektur Database          → Lihat data database
[4] Inspektur Database (Fast)   → Tanpa compile (lebih cepat)
[5] Build Project               → Compile saja
[0] Keluar
```

### Manual Build & Run
```bash
# Build project
mvn clean install

# Run aplikasi utama
mvn exec:java -Dexec.mainClass="com.studyplanner.AplikasiUtama"

# Run inspektur database
mvn exec:java -Dexec.mainClass="com.studyplanner.AplikasiInspekturDB"
```

## 📋 Requirements

- **Java Development Kit (JDK) 25+**
- **Maven 3.9+**
- **JavaFX 25**

## ✨ Key Features

### 🔐 Login & Authentication
- Login dengan Google OAuth 2.0
- Login dengan Username & Password
- Register akun baru
- Password encryption (SHA-256)
- Session persistence

### 📊 Dashboard
- Widget runtutan belajar (streak counter)
- Widget waktu belajar hari ini
- Widget ulasan berikutnya
- Widget tugas mendatang
- Sidebar collapsible dengan animasi smooth
- Dark mode / Light mode toggle

### 📚 Mata Kuliah
- Tambah, edit, hapus mata kuliah
- Kelola topik per mata kuliah
- Sistem spaced repetition otomatis
- Tracking progress per topik

### 📅 Jadwal
- View jadwal mingguan
- Calendar picker dengan navigasi
- Timeline schedule cards
- Auto-generate jadwal belajar dengan algoritma SM-2

### ⚙️ Settings
- Toggle dark mode
- Durasi belajar default
- Pengingat belajar
- Backup & export data
- About aplikasi

### 🔍 Inspektur Database
- View semua tabel
- Lihat struktur & data
- Query SQL custom
- Export data
- View username & password (ter-hash)

## 🎨 Design System

Aplikasi ini menggunakan **Material Design 3** dengan:
- ✨ Modern card-based layouts
- 🎭 Smooth animations & transitions
- 📐 Elevation & shadows untuk depth
- 🎨 Color tokens untuk konsistensi visual
- 📝 Responsive typography dengan Google Sans
- 🌙 Full dark mode support
- 🖼️ Custom window decorations

## 🗄️ Database

**Database**: `data/study_planner.db` (SQLite)

### Tables
- `users` - User accounts (username/password & Google OAuth)
- `mata_kuliah` - Courses/subjects
- `topik` - Topics per course
- `jadwal_ujian` - Exam schedules
- `sesi_belajar` - Study sessions with spaced repetition

## 📂 Project Structure

```
src/
├── main/
│   ├── java/com/studyplanner/
│   │   ├── AplikasiUtama.java              → Main app entry point
│   │   ├── AplikasiInspekturDB.java        → DB inspector tool
│   │   ├── basisdata/                      → Database layer
│   │   │   └── ManajerBasisData.java       → DB manager
│   │   ├── kontroler/                      → MVC Controllers
│   │   │   ├── KontrolerLogin.java         → Login controller
│   │   │   ├── KontrolerUtama.java         → Main dashboard
│   │   │   ├── KontrolerMataKuliah.java    → Course management
│   │   │   └── KontrolerTampilanJadwal.java → Schedule view
│   │   ├── model/                          → Data models
│   │   │   ├── MataKuliah.java
│   │   │   ├── Topik.java
│   │   │   └── JadwalUjian.java
│   │   ├── tampilan/                       → Custom UI components
│   │   │   ├── Widget*.java                → Dashboard widgets
│   │   │   └── DekoratorJendelaKustom.java → Custom window
│   │   └── utilitas/                       → Utilities
│   │       ├── ManajerOtentikasi.java      → OAuth & auth manager
│   │       ├── PembuatIkon.java            → Material icons
│   │       └── PembuatDialogMD3.java       → MD3 dialogs
│   └── resources/
│       ├── css/
│       │   └── style.css                   → MD3 styling (2300+ lines)
│       ├── fxml/
│       │   ├── LoginView.fxml              → Modern login UI
│       │   ├── MainView.fxml               → Dashboard layout
│       │   ├── CourseView.fxml             → Course management
│       │   └── ScheduleView.fxml           → Weekly schedule
│       ├── fonts/
│       │   └── GoogleSans-*.ttf            → Google Sans Variable
│       └── credentials.json                → Google OAuth config
├── run.bat                                 → Interactive run menu
├── CARA_MENJALANKAN.txt                   → User guide (Indonesian)
└── pom.xml                                 → Maven configuration
```

## 🔐 Security

- ✅ Password di-hash dengan SHA-256
- ✅ Google OAuth 2.0 untuk authentication
- ✅ Session tokens untuk persistence
- ✅ Validasi input di semua form
- ✅ SQL injection protection dengan PreparedStatement

## 🛠️ Technology Stack

- **Java 25** - Latest LTS
- **JavaFX 25** - Modern UI framework
- **SQLite 3.47** - Embedded database
- **Maven** - Build & dependency management
- **Google OAuth 2.0** - Authentication
- **Ikonli Material Design 2** - Material icons
- **Google Sans Flex** - Variable font

## 📖 Documentation

Dokumentasi lengkap tersedia di:
- `CARA_MENJALANKAN.txt` - Panduan lengkap menjalankan aplikasi
- `docs/` - Dokumentasi teknis proyek

## 💡 Development Tips

1. **Pertama kali**: Gunakan `run.bat` pilih `[1]` (dengan compile)
2. **Development**: Gunakan pilihan `[2]` (fast mode - lebih cepat)
3. **Setelah pull**: Pilih `[5]` untuk build ulang
4. **Debug database**: Gunakan `[3]` untuk inspektur database
5. **Lihat users**: Buka Inspektur DB → Pilih tabel `users`

## 🐛 Troubleshooting

### IDE Error (Red Underlines)
Jika IDE menunjukkan error tapi build success:
- Reload Window (VS Code)
- Clean Java Language Server Workspace
- Jalankan `mvn clean install`

### Database Locked
Jika database terkunci:
- Tutup semua instance aplikasi
- Tutup Inspektur Database
- Restart aplikasi

## 📝 License

© 2025 Perencana Belajar Adaptif - Educational Project

---

**Dibuat dengan ❤️ menggunakan Material Design 3**
