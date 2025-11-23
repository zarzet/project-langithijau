# Perencana Belajar Adaptif

Aplikasi desktop untuk membantu mahasiswa mengelola jadwal belajar dengan teknik **Spaced Repetition (SM-2)** dan sistem pembelajaran adaptif.

## Quick Start

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

## Requirements

- **Java Development Kit (JDK) 25+**
- **Maven 3.9+**
- **JavaFX 25**
- **Google OAuth 2.0 Credentials** (untuk login Google)

## Setup Google OAuth (Wajib untuk Developer)

Sebelum menjalankan aplikasi, Anda perlu setup Google OAuth credentials:

1. **Buat Google Cloud Project** di [Google Cloud Console](https://console.cloud.google.com/)
2. **Enable Google+ API**
3. **Buat OAuth 2.0 Client ID** (Desktop application)
4. **Download credential JSON**
5. **Copy credential:**
   ```bash
   cp src/main/resources/credentials.json.example src/main/resources/credentials.json
   # Lalu isi dengan credential dari Google Cloud Console
   ```

**Panduan lengkap**: Lihat [DEPLOYMENT.md](DEPLOYMENT.md)

**PENTING**: File `credentials.json` tidak di-commit ke Git untuk keamanan!

## Key Features

### Login & Authentication
- Login dengan Google OAuth 2.0
- Login dengan Username & Password
- Register akun baru
- Password encryption (SHA-256)
- Session persistence

### Dashboard
- Widget runtutan belajar (streak counter)
- Widget waktu belajar hari ini
- Widget ulasan berikutnya
- Widget tugas mendatang
- Sidebar collapsible dengan animasi smooth
- Dark mode / Light mode toggle

### Mata Kuliah
- Tambah, edit, hapus mata kuliah
- Kelola topik per mata kuliah
- Sistem spaced repetition otomatis
- Tracking progress per topik

### Jadwal
- View jadwal mingguan
- Calendar picker dengan navigasi
- Timeline schedule cards
- Auto-generate jadwal belajar dengan algoritma SM-2

### Settings
- Toggle dark mode
- Durasi belajar default
- Pengingat belajar
- Backup & export data
- About aplikasi

### Inspektur Database
- View semua tabel
- Lihat struktur & data
- Query SQL custom
- Export data
- View username & password (ter-hash)

## Design System

Aplikasi ini menggunakan **Material Design 3** dengan:
- Modern card-based layouts
- Smooth animations & transitions
- Elevation & shadows untuk depth
- Color tokens untuk konsistensi visual
- Responsive typography dengan Google Sans
- Full dark mode support
- Custom window decorations

## Database

**Database**: `data/study_planner.db` (SQLite)

### Tables
- `users` - User accounts (username/password & Google OAuth)
- `mata_kuliah` - Courses/subjects
- `topik` - Topics per course
- `jadwal_ujian` - Exam schedules
- `sesi_belajar` - Study sessions with spaced repetition

## Project Structure

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

## Security

- Password di-hash dengan SHA-256
- Google OAuth 2.0 untuk authentication
- Session tokens untuk persistence
- Validasi input di semua form
- SQL injection protection dengan PreparedStatement

## Technology Stack

- **Java 25** - Latest LTS
- **JavaFX 25** - Modern UI framework
- **SQLite 3.47** - Embedded database
- **Maven** - Build & dependency management
- **Google OAuth 2.0** - Authentication
- **Ikonli Material Design 2** - Material icons
- **Google Sans Flex** - Variable font

## Documentation

Dokumentasi lengkap tersedia di:
- `CARA_MENJALANKAN.txt` - Panduan lengkap menjalankan aplikasi
- `docs/` - Dokumentasi teknis proyek

## Development Tips

1. **Pertama kali**: Gunakan `run.bat` pilih `[1]` (dengan compile)
2. **Development**: Gunakan pilihan `[2]` (fast mode - lebih cepat)
3. **Setelah pull**: Pilih `[5]` untuk build ulang
4. **Debug database**: Gunakan `[3]` untuk inspektur database
5. **Lihat users**: Buka Inspektur DB → Pilih tabel `users`

## Troubleshooting

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

### Credential Error
Jika error saat login Google:
- Pastikan `credentials.json` ada di `src/main/resources/`
- Pastikan credential valid dari Google Cloud Console
- Pastikan Google+ API sudah enabled

## Deployment & Distribution

### Build Fat JAR (Single File Executable)

```bash
# Build aplikasi menjadi single JAR file
mvn clean package

# Hasil: target/adaptive-study-planner-1.0.0.jar
# Jalankan dengan:
java -jar target/adaptive-study-planner-1.0.0.jar
```

### Build Windows Installer

```bash
# Build installer Windows (.exe)
mvn clean package
mvn jpackage:jpackage

# Hasil: target/dist/AdaptiveStudyPlanner-1.0.0.exe
```

### Untuk Demonstrasi

**Option 1: Video Demo**
- Record aplikasi saat digunakan
- Upload ke YouTube
- Link di README

**Option 2: GitHub Releases**
- Build Fat JAR atau Installer
- Upload ke GitHub Releases
- User download & jalankan

**Panduan lengkap**: Lihat [DEPLOYMENT.md](DEPLOYMENT.md)

## License

Copyright 2025 Perencana Belajar Adaptif - Educational Project

---

Built with Material Design 3
