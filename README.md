# 📚 Adaptive Study Planner

**Aplikasi Desktop Perencana Studi Adaptif Berbasis Teknik Spaced Repetition dan Interleaving**

---

## 📖 Deskripsi

Adaptive Study Planner adalah aplikasi desktop yang dirancang khusus untuk membantu mahasiswa mengelola jadwal belajar mereka dengan lebih efektif. Aplikasi ini menggunakan dua teknik pembelajaran yang telah terbukti secara ilmiah:

1. **Spaced Repetition (Pengulangan Berjarak)**: Mengulang materi pada interval waktu yang optimal untuk meningkatkan retensi memori jangka panjang.
2. **Interleaving (Pembelajaran Bergantian)**: Menjadwalkan berbagai topik dari mata kuliah yang berbeda dalam satu hari untuk meningkatkan pemahaman.

Aplikasi ini menghilangkan beban kognitif dalam merencanakan "kapan dan apa yang harus dipelajari" dengan cara mengotomatiskan pembuatan jadwal belajar berdasarkan prioritas, tingkat kesulitan, dan kedekatan dengan jadwal ujian.

---

## ✨ Fitur Utama

### 1. **Manajemen Mata Kuliah & Topik**
- Tambah, edit, dan hapus mata kuliah
- Kelola topik-topik dalam setiap mata kuliah
- Set prioritas dan tingkat kesulitan untuk setiap topik

### 2. **Jadwal Ujian & Tenggat Waktu**
- Input jadwal ujian (UTS, UAS, Kuis, Tugas)
- Sistem otomatis memprioritaskan materi berdasarkan kedekatan ujian

### 3. **Generator Jadwal Otomatis**
- Generate jadwal belajar otomatis untuk 7 hari ke depan
- Implementasi algoritma Spaced Repetition (SM-2)
- Implementasi Interleaving untuk variasi pembelajaran

### 4. **Dashboard Tugas Harian**
- Tampilan tugas belajar hari ini
- Checklist untuk menandai tugas yang sudah selesai
- Rating performa setelah sesi belajar

### 5. **Pelacakan Progress**
- Progress keseluruhan penguasaan materi
- Progress harian
- Statistik topik yang sudah dikuasai

---

## 🛠️ Teknologi yang Digunakan

- **Java 25** - Bahasa pemrograman (latest version)
- **JavaFX 25** - Framework GUI (latest version)
- **SQLite 3.47** - Database lokal
- **Maven** - Build tool & dependency management
- **SM-2 Algorithm** - Algoritma Spaced Repetition

---

## 📋 Persyaratan Sistem

- **Java Development Kit (JDK)** 25 atau lebih tinggi
- **Maven** 3.6 atau lebih tinggi
- **RAM** minimal 512 MB
- **Ruang Disk** minimal 100 MB
- **OS**: Windows 10/11, macOS, atau Linux

---

## 🚀 Cara Instalasi & Menjalankan

### 1. Clone atau Download Repository

```bash
cd C:\Experiment\Note
```

### 2. Kompilasi Proyek dengan Maven

```bash
mvn clean install
```

### 3. Jalankan Aplikasi

```bash
mvn javafx:run
```

**Alternatif**: Jika Anda menggunakan IDE seperti IntelliJ IDEA atau Eclipse:
1. Import project sebagai Maven project
2. Jalankan class `com.studyplanner.MainApp`

---

## 📚 Cara Penggunaan

### **Langkah 1: Tambah Mata Kuliah**
1. Klik tombol **"🎓 Kelola Mata Kuliah"**
2. Klik **"➕ Tambah"** di bagian Mata Kuliah
3. Isi data mata kuliah (Kode, Nama, Deskripsi)
4. Klik **"Simpan"**

### **Langkah 2: Tambah Topik Materi**
1. Pilih mata kuliah yang sudah ditambahkan
2. Di bagian **"Topik Materi"**, klik **"➕ Tambah"**
3. Isi nama topik, deskripsi, prioritas (1-5), dan tingkat kesulitan (1-5)
4. Klik **"Simpan"**

### **Langkah 3: Tambah Jadwal Ujian**
1. Pilih mata kuliah
2. Di bagian **"Jadwal Ujian"**, klik **"➕ Tambah"**
3. Isi judul, tipe (UTS/UAS/Kuis/Tugas), dan tanggal ujian
4. Klik **"Simpan"**

### **Langkah 4: Generate Jadwal Belajar**
1. Kembali ke Dashboard utama
2. Klik tombol **"⚡ Generate Schedule"**
3. Sistem akan otomatis membuat jadwal belajar untuk 7 hari ke depan

### **Langkah 5: Mulai Belajar!**
1. Lihat daftar tugas di **"📝 Tugas Hari Ini"**
2. Setelah menyelesaikan satu sesi, centang checkbox
3. Berikan rating performa (1-5):
   - **1-2**: Sangat sulit/lupa (interval direset)
   - **3**: Cukup
   - **4**: Baik
   - **5**: Sangat mudah
4. Sistem akan otomatis menghitung kapan Anda perlu mengulang topik tersebut

---

## 🧠 Bagaimana Algoritma Bekerja?

### **Spaced Repetition (SM-2)**

Aplikasi menggunakan algoritma SuperMemo 2 (SM-2) untuk menentukan interval pengulangan:

1. **Review Pertama**: 1 hari setelah belajar pertama kali
2. **Review Kedua**: 6 hari setelah review pertama
3. **Review Selanjutnya**: Interval dikalikan dengan Easiness Factor (EF)

**Easiness Factor** dihitung berdasarkan rating performa:
- Rating tinggi → Interval semakin panjang (materi mudah)
- Rating rendah → Interval direset (perlu dipelajari lagi)

### **Interleaving**

Sistem menjadwalkan topik dari berbagai mata kuliah dalam satu hari:
- Mencegah pembelajaran monoton
- Meningkatkan kemampuan otak membedakan konsep
- Memperdalam pemahaman

### **Prioritas Otomatis**

Sistem menghitung prioritas berdasarkan:
1. **Prioritas pengguna** (30%)
2. **Tingkat kesulitan** (20%)
3. **Kedekatan dengan ujian** (30%)
4. **Frekuensi review** (20%)

---

## 📊 Contoh Skenario Penggunaan

### **Kasus: Mahasiswa dengan 3 Mata Kuliah**

**Mata Kuliah:**
- CS101 - Pemrograman Dasar (Ujian: 15 hari lagi)
- MATH201 - Kalkulus II (Ujian: 7 hari lagi)
- PHY101 - Fisika Dasar (Ujian: 20 hari lagi)

**Topik yang Ditambahkan:**
- CS101: Variables, Functions, Arrays, OOP
- MATH201: Integral, Turunan, Limit
- PHY101: Gerak, Gaya, Energi

**Hasil Generate Schedule (Hari 1):**
1. 📚 Belajar Pertama: Integral (MATH201) - 45 menit
2. 📚 Belajar Pertama: Variables (CS101) - 45 menit
3. 📚 Belajar Pertama: Gerak (PHY101) - 45 menit

**Sistem akan otomatis:**
- Memprioritaskan MATH201 karena ujian lebih dekat
- Menerapkan interleaving dengan mencampur 3 mata kuliah
- Menjadwalkan review sesuai performa pengguna

---

## 🗂️ Struktur Database

Aplikasi menggunakan SQLite dengan 4 tabel utama:

1. **courses** - Data mata kuliah
2. **topics** - Topik-topik dalam mata kuliah
3. **exam_schedules** - Jadwal ujian
4. **study_sessions** - Sesi belajar yang dijadwalkan

File database: `study_planner.db` (otomatis dibuat saat pertama kali dijalankan)

---

## 🎨 Screenshot

### Dashboard Utama
Menampilkan:
- Progress keseluruhan dan harian
- Tugas hari ini dengan checklist
- Ujian yang akan datang

### Manajemen Mata Kuliah
- Daftar mata kuliah
- Topik-topik per mata kuliah
- Jadwal ujian

### Jadwal Belajar
- Kalender dengan jadwal belajar
- Detail sesi per hari

---

## 🔧 Troubleshooting

### **Error: "JavaFX runtime components are missing"**
**Solusi**: Pastikan JavaFX sudah terinstall atau jalankan dengan Maven:
```bash
mvn javafx:run
```

### **Database tidak terbuat**
**Solusi**: Pastikan aplikasi memiliki permission untuk membuat file di direktori aplikasi.

### **Aplikasi crash saat membuka window**
**Solusi**: 
1. Periksa file FXML di folder `src/main/resources/fxml/`
2. Jalankan dengan `mvn clean install` terlebih dahulu

---

## 📝 Lisensi

Proyek ini dibuat untuk tujuan edukasi.

---

## 👨‍💻 Pengembang

Dikembangkan sebagai solusi untuk masalah manajemen waktu belajar mahasiswa menggunakan pendekatan berbasis sains kognitif.

---

## 🙏 Kontribusi

Kontribusi, issues, dan feature requests sangat diterima!

---

## 📞 Kontak & Support

Jika ada pertanyaan atau masalah, silakan buat issue di repository ini.

---

**Selamat Belajar! 🎓📚**

*"The secret to getting ahead is getting started." - Mark Twain*

