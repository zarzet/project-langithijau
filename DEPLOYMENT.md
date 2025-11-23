# Deployment Guide - Adaptive Study Planner

Panduan lengkap untuk build, package, dan deploy aplikasi sebagai executable.

---

## Prerequisites

1. **Java 25 JDK** - [Download](https://www.oracle.com/java/technologies/downloads/)
2. **Maven 3.9+** - [Download](https://maven.apache.org/download.cgi)
3. **Google Cloud Console** - Untuk OAuth credentials

---

## Setup Google OAuth Credentials

### Langkah 1: Buat Google Cloud Project

1. Buka [Google Cloud Console](https://console.cloud.google.com/)
2. Klik **Create Project**
3. Beri nama project (contoh: "Adaptive Study Planner")
4. Klik **Create**

### Langkah 2: Enable Google+ API

1. Di dashboard project, klik **Enable APIs and Services**
2. Cari **Google+ API**
3. Klik **Enable**

### Langkah 3: Buat OAuth 2.0 Credentials

1. Klik **Credentials** di sidebar
2. Klik **Create Credentials** → **OAuth client ID**
3. Pilih **Desktop app**
4. Beri nama (contoh: "Study Planner Desktop")
5. Klik **Create**
6. **Download JSON** (tombol download)

### Langkah 4: Setup Credential File

```bash
# 1. Copy file template
cp src/main/resources/credentials.json.example src/main/resources/credentials.json

# 2. Buka credentials.json yang baru di-download dari Google
# 3. Copy isi file tersebut ke src/main/resources/credentials.json
```

**PENTING**: File `credentials.json` sudah otomatis di-ignore oleh Git!

---

## Build Options

### Option 1: Fat JAR (Recommended untuk Demo)

Build aplikasi menjadi single JAR file yang bisa dijalankan di mana saja:

```bash
# Build Fat JAR
mvn clean package

# Hasilnya ada di: target/adaptive-study-planner-1.0.0.jar

# Jalankan
java -jar target/adaptive-study-planner-1.0.0.jar
```

**Keuntungan:**
- Single file, mudah didistribusikan
- Semua dependencies sudah include
- Bisa dijalankan di komputer mana saja (yang punya Java)
- Source code ter-obfuscate (sulit di-decompile)

**Credential:**
- Credential JSON harus ada di folder yang sama dengan JAR
- Atau di `src/main/resources/credentials.json`

---

### Option 2: Windows Installer (jpackage)

Build aplikasi menjadi installer Windows (.exe atau .msi):

```bash
# Step 1: Build JAR dulu
mvn clean package

# Step 2: Create runtime image
jlink --module-path "%JAVA_HOME%/jmods" --add-modules java.base,java.desktop,java.sql,java.logging,java.naming,java.xml,jdk.unsupported --output target/runtime-image

# Step 3: Create installer
mvn jpackage:jpackage
```

**Hasil:**
- Installer Windows di `target/dist/`
- Aplikasi ter-install di Program Files
- Shortcut di Start Menu & Desktop

**Keuntungan:**
- User tidak perlu install Java
- Aplikasi native Windows
- Professional deployment
- Auto-update support (dengan konfigurasi tambahan)

---

### Option 3: Portable Executable

Buat folder portable yang bisa di-zip dan dibagikan:

```bash
# Build dengan dependencies
mvn clean package
mvn dependency:copy-dependencies

# Buat struktur folder portable
mkdir portable
copy target\adaptive-study-planner-1.0.0.jar portable\
copy src\main\resources\credentials.json.example portable\credentials.json.example
```

Buat file `run-portable.bat`:
```batch
@echo off
java -jar adaptive-study-planner-1.0.0.jar
pause
```

---

## Deploy untuk Demo (Tanpa Credential di GitHub)

### Struktur Repository GitHub:

```
GitHub Repository:
├── src/                          ← Source code
├── pom.xml                       ← Maven config
├── .gitignore                    ← Ignore credentials.json
├── credentials.json.example      ← Template (DI-COMMIT)
├── README.md                     ← Dokumentasi
└── DEPLOYMENT.md                 ← Panduan ini
```

### Setup untuk Developer Lain:

**1. Clone repository:**
```bash
git clone https://github.com/username/adaptive-study-planner.git
cd adaptive-study-planner
```

**2. Setup credentials:**
```bash
# Copy template
cp src/main/resources/credentials.json.example src/main/resources/credentials.json

# Edit credentials.json dengan Google OAuth credential Anda sendiri
```

**3. Build & Run:**
```bash
mvn clean install
mvn javafx:run
```

---

## Keamanan Credential

### JANGAN:
- Commit `credentials.json` ke Git
- Hardcode credential di source code
- Share credential di public

### LAKUKAN:
- Gunakan `.gitignore` untuk exclude credential
- Setiap developer punya credential sendiri
- Gunakan environment variables untuk production
- Dokumentasikan cara mendapatkan credential

---

## Distribusi Aplikasi

### Untuk End Users (Non-Developer):

**Option A: Installer Windows**
1. Build installer dengan jpackage
2. Upload installer ke GitHub Releases
3. User download & install
4. User login dengan Google account mereka (tidak perlu credential JSON)

**Option B: Fat JAR + Launcher**
1. Build Fat JAR
2. Buat launcher script yang handle OAuth flow
3. Bundle dalam ZIP
4. Upload ke GitHub Releases

### Untuk Demonstrasi:

**Video Demo:**
- Record screen saat menggunakan aplikasi
- Upload ke YouTube
- Link di README.md

**Live Demo:**
- Deploy ke cloud (jika memungkinkan)
- Atau sediakan test account

---

## Build Commands Lengkap

```bash
# Development
mvn clean install              # Build project
mvn javafx:run                 # Run aplikasi

# Production
mvn clean package              # Build Fat JAR
mvn jpackage:jpackage          # Build Windows installer

# Testing
mvn test                       # Run unit tests
mvn verify                     # Run integration tests

# Clean
mvn clean                      # Hapus build artifacts
```

---

## Checklist Sebelum Push ke GitHub

- [ ] `credentials.json` tidak ada di repository
- [ ] `credentials.json` ada di `.gitignore`
- [ ] `credentials.json.example` sudah di-commit
- [ ] README.md updated dengan instruksi setup
- [ ] DEPLOYMENT.md lengkap
- [ ] Database file (.db) di-ignore
- [ ] Tokens folder di-ignore
- [ ] Build berhasil: `mvn clean package`

---

## Recommended Workflow

### Untuk Developer:
1. Clone repo
2. Setup credential sendiri
3. Build & run
4. Develop
5. Push (tanpa credential)

### Untuk End User:
1. Download installer dari Releases
2. Install aplikasi
3. Login dengan Google account
4. Mulai gunakan aplikasi

---

## Tips

- **Credential JSON hanya untuk OAuth setup**, bukan untuk menyimpan password
- **User login dengan Google account mereka sendiri**, bukan dengan credential developer
- **Setiap developer/tester perlu credential JSON sendiri** dari Google Cloud Console
- **Production app tidak perlu credential JSON** jika menggunakan OAuth flow yang proper

---

---

Adaptive Study Planner - Professional Deployment Guide
