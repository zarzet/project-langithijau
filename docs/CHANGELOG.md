# Changelog

## [1.1.0] - 2025-10-20

### 🔄 Updates
- **Upgraded to Java 25**: Full compatibility with latest JDK (September 2025)
- **JavaFX 25**: Updated to latest JavaFX version (September 2025 release)
- **SQLite 3.47.1**: Updated JDBC driver to latest version
- **Gson 2.11.0**: Latest JSON processing library
- **Maven Compiler 3.13.0**: Latest compiler plugin
- **Performance improvements** from Java 25 and JavaFX 25 optimizations
- **Better rendering** and UI performance with JavaFX 25

### 🛠️ Technical Changes
- Updated `pom.xml` for Java 25 compatibility
- Updated all dependencies to latest stable versions
- Enhanced build configuration for modern JDK

---

## [1.0.0] - 2025-10-20

### ✨ Fitur Utama
- **Manajemen Mata Kuliah & Topik**: Tambah, edit, hapus mata kuliah dan topik pembelajaran
- **Jadwal Ujian**: Input dan kelola jadwal ujian, kuis, dan assignment
- **Generator Jadwal Otomatis**: Sistem cerdas yang membuat jadwal belajar untuk 7 hari ke depan
- **Spaced Repetition Algorithm**: Implementasi SM-2 untuk pengulangan optimal
- **Interleaving**: Pembelajaran bergantian dari berbagai mata kuliah
- **Dashboard Interaktif**: Progress tracking, tugas harian, dan upcoming exams
- **Rating Performa**: Sistem feedback untuk menyesuaikan interval review

### 🎨 User Interface
- Dashboard utama dengan statistik lengkap
- Window manajemen mata kuliah dengan 3 panel
- Schedule viewer dengan date picker
- Modern dan responsive design dengan JavaFX
- Custom CSS styling untuk UX yang lebih baik

### 🗄️ Database
- SQLite database untuk penyimpanan lokal
- 4 tabel utama: courses, topics, exam_schedules, study_sessions
- Auto-create database saat first run
- Relational integrity dengan foreign keys

### 📊 Algoritma
- **SM-2 Spaced Repetition**: 
  - Review pertama: 1 hari
  - Review kedua: 6 hari
  - Review selanjutnya: interval × easiness factor
- **Prioritas Otomatis**:
  - Berdasarkan prioritas pengguna (30%)
  - Tingkat kesulitan (20%)
  - Kedekatan ujian (30%)
  - Frekuensi review (20%)
- **Interleaving**: Mix topik dari berbagai mata kuliah per hari

### 📚 Dokumentasi
- README.md lengkap dengan instalasi dan usage
- PANDUAN_PENGGUNA.md untuk end-user
- Inline code comments untuk developers
- Contoh skenario penggunaan

### 🛠️ Teknologi
- Java 17
- JavaFX 21
- SQLite JDBC 3.44.1
- Maven build system
- Gson untuk JSON processing

### 🎯 Target Pengguna
- Mahasiswa dari berbagai jurusan
- Siapa saja yang ingin belajar lebih efektif dengan metode ilmiah

---

## Rencana Update Selanjutnya

### [1.1.0] - Planned
- [ ] Export/Import data (JSON/CSV)
- [ ] Dark mode theme
- [ ] Statistik dan analytics dashboard
- [ ] Reminder notifications
- [ ] Multiple study plan profiles

### [1.2.0] - Planned
- [ ] Cloud sync (optional)
- [ ] Mobile companion app
- [ ] Pomodoro timer integration
- [ ] Study notes per topic
- [ ] PDF export untuk jadwal

### [2.0.0] - Future
- [ ] AI-powered difficulty prediction
- [ ] Collaborative study groups
- [ ] Gamification (badges, streaks)
- [ ] Integration dengan calendar apps
- [ ] Voice commands

---

*Catatan: Aplikasi ini masih dalam development aktif. Feedback dan suggestions sangat diterima!*

