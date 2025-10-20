# 📊 USE CASE DIAGRAM - ADAPTIVE STUDY PLANNER

## Aktor dalam Sistem

### **1. Mahasiswa (Student)** 👨‍🎓
**Deskripsi:** Pengguna utama dan satu-satunya dalam aplikasi. Mahasiswa menggunakan aplikasi untuk mengelola pembelajaran personal mereka.

**Karakteristik:**
- Mahasiswa S1/S2/S3 dari berbagai jurusan
- Memiliki multiple mata kuliah dalam satu semester
- Ingin meningkatkan efektivitas pembelajaran
- Membutuhkan bantuan dalam mengatur jadwal belajar

---

## Use Case Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ADAPTIVE STUDY PLANNER SYSTEM                        │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                    MANAJEMEN DATA                                │  │
│  │                                                                  │  │
│  │  • UC-01: Tambah Mata Kuliah                                    │  │
│  │  • UC-02: Edit Mata Kuliah                                      │  │
│  │  • UC-03: Hapus Mata Kuliah                                     │  │
│  │  • UC-04: Lihat Daftar Mata Kuliah                             │  │
│  │                                                                  │  │
│  │  • UC-05: Tambah Topik                                          │  │
│  │  • UC-06: Edit Topik                                            │  │
│  │  • UC-07: Hapus Topik                                           │  │
│  │  • UC-08: Set Prioritas & Kesulitan Topik                      │  │
│  │                                                                  │  │
│  │  • UC-09: Tambah Jadwal Ujian                                   │  │
│  │  • UC-10: Edit Jadwal Ujian                                     │  │
│  │  • UC-11: Hapus Jadwal Ujian                                    │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                              ▲                                          │
│                              │                                          │
│                              │                                          │
│                     ┌────────┴────────┐                                │
│                     │                 │                                 │
│                     │   MAHASISWA     │                                 │
│                     │   (Student)     │                                 │
│                     │                 │                                 │
│                     └────────┬────────┘                                │
│                              │                                          │
│                              │                                          │
│                              ▼                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                 MANAJEMEN PEMBELAJARAN                           │  │
│  │                                                                  │  │
│  │  • UC-12: Generate Jadwal Otomatis                              │  │
│  │  • UC-13: Lihat Tugas Harian                                    │  │
│  │  • UC-14: Tandai Tugas Selesai                                  │  │
│  │  • UC-15: Beri Rating Performa                                  │  │
│  │  • UC-16: Lihat Jadwal per Tanggal                             │  │
│  │  • UC-17: Lihat Progress Keseluruhan                           │  │
│  │  • UC-18: Lihat Ujian Mendatang                                │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘

                              SISTEM BACKEND
                    (Tidak terlihat oleh pengguna)
                    
                    • Algoritma Spaced Repetition (SM-2)
                    • Algoritma Interleaving
                    • Priority Matrix Calculation
                    • Database Management (SQLite)
```

---

## Detail Use Case

### **A. MANAJEMEN DATA**

#### **UC-01: Tambah Mata Kuliah**
- **Aktor:** Mahasiswa
- **Deskripsi:** Mahasiswa menambahkan mata kuliah baru dengan kode, nama, dan deskripsi
- **Precondition:** Aplikasi terbuka, berada di menu Course Management
- **Postcondition:** Mata kuliah baru tersimpan di database
- **Main Flow:**
  1. Mahasiswa klik tombol "➕ Tambah" di bagian Mata Kuliah
  2. Sistem menampilkan dialog input
  3. Mahasiswa input: Kode (contoh: CS101), Nama, Deskripsi
  4. Mahasiswa klik "Simpan"
  5. Sistem validasi data
  6. Sistem simpan ke database
  7. Sistem refresh tabel mata kuliah

#### **UC-02: Edit Mata Kuliah**
- **Aktor:** Mahasiswa
- **Precondition:** Ada mata kuliah di database
- **Main Flow:**
  1. Mahasiswa pilih mata kuliah dari tabel
  2. Mahasiswa klik tombol "✏️ Edit"
  3. Sistem tampilkan dialog dengan data existing
  4. Mahasiswa ubah data yang diperlukan
  5. Mahasiswa klik "Simpan"
  6. Sistem update database
  7. Sistem refresh tabel

#### **UC-03: Hapus Mata Kuliah**
- **Aktor:** Mahasiswa
- **Precondition:** Ada mata kuliah di database
- **Main Flow:**
  1. Mahasiswa pilih mata kuliah dari tabel
  2. Mahasiswa klik tombol "🗑️ Hapus"
  3. Sistem tampilkan dialog konfirmasi
  4. Mahasiswa konfirmasi hapus
  5. Sistem hapus mata kuliah dan semua data terkait (CASCADE)
  6. Sistem refresh tabel

#### **UC-05: Tambah Topik**
- **Aktor:** Mahasiswa
- **Precondition:** Minimal ada 1 mata kuliah di database
- **Main Flow:**
  1. Mahasiswa pilih mata kuliah dari tabel
  2. Mahasiswa klik "➕ Tambah" di bagian Topik
  3. Sistem tampilkan dialog input
  4. Mahasiswa input: Nama, Deskripsi, Prioritas (1-5), Kesulitan (1-5)
  5. Mahasiswa klik "Simpan"
  6. Sistem simpan topik dengan course_id yang dipilih
  7. Sistem refresh tabel topik

#### **UC-09: Tambah Jadwal Ujian**
- **Aktor:** Mahasiswa
- **Precondition:** Minimal ada 1 mata kuliah di database
- **Main Flow:**
  1. Mahasiswa pilih mata kuliah
  2. Mahasiswa klik "➕ Tambah" di bagian Jadwal Ujian
  3. Sistem tampilkan dialog input
  4. Mahasiswa pilih tipe ujian (UTS/UAS/Kuis/Tugas)
  5. Mahasiswa input judul dan tanggal
  6. Mahasiswa klik "Simpan"
  7. Sistem simpan jadwal ujian
  8. Sistem refresh tabel ujian

---

### **B. MANAJEMEN PEMBELAJARAN**

#### **UC-12: Generate Jadwal Otomatis**
- **Aktor:** Mahasiswa
- **Deskripsi:** Sistem membuat jadwal belajar otomatis untuk 7 hari ke depan
- **Precondition:** Ada minimal 1 topik di database
- **Postcondition:** Jadwal belajar ter-generate dan tersimpan
- **Main Flow:**
  1. Mahasiswa klik tombol "⚡ Generate Schedule" di dashboard
  2. Sistem ambil semua data: courses, topics, exams
  3. Sistem hitung prioritas setiap topik menggunakan Priority Matrix
  4. Sistem apply algoritma Interleaving untuk mix topics
  5. Sistem apply algoritma Spaced Repetition untuk tentukan review timing
  6. Sistem generate 3-6 sesi belajar per hari untuk 7 hari ke depan
  7. Sistem simpan study sessions ke database
  8. Sistem tampilkan notifikasi sukses
  9. Sistem refresh dashboard

**Business Rules (UC-12):**
- **Priority Calculation:**
  - User Priority (30%)
  - Difficulty Level (20%)
  - Exam Proximity (30%)
  - Review Frequency (20%)
  - Needs Review Today Bonus (+15%)
  - Mastered Penalty (-20%)

- **Interleaving Rules:**
  - Round 1: Pilih 1 topik prioritas tertinggi dari setiap course
  - Round 2: Fill hingga 3-6 sesi dengan topik prioritas tinggi lainnya
  - Shuffle final list untuk variasi

- **Session Types:**
  - INITIAL_STUDY: Topik belum pernah dipelajari (45 menit)
  - REVIEW: Pengulangan sesuai interval (30 menit)
  - PRACTICE: Latihan soal (30 menit)

#### **UC-13: Lihat Tugas Harian**
- **Aktor:** Mahasiswa
- **Precondition:** Jadwal sudah di-generate
- **Main Flow:**
  1. Mahasiswa buka aplikasi
  2. Sistem otomatis load dashboard
  3. Sistem query study_sessions untuk hari ini
  4. Sistem tampilkan daftar tugas dengan:
     - Checkbox (completed/not)
     - Nama topik
     - Mata kuliah
     - Tipe sesi (icon)
     - Durasi estimasi
  5. Mahasiswa lihat daftar tugas

#### **UC-14: Tandai Tugas Selesai**
- **Aktor:** Mahasiswa
- **Precondition:** Ada tugas hari ini
- **Main Flow:**
  1. Mahasiswa centang checkbox di task card
  2. Sistem update study_session.completed = true
  3. Sistem catat completed_at = current_timestamp
  4. Sistem tampilkan rating dialog (UC-15)

#### **UC-15: Beri Rating Performa**
- **Aktor:** Mahasiswa
- **Deskripsi:** Mahasiswa memberikan feedback tentang pemahaman materi
- **Precondition:** Tugas baru saja ditandai selesai
- **Postcondition:** Algorithm SM-2 update interval review berikutnya
- **Main Flow:**
  1. Sistem tampilkan dialog rating
  2. Mahasiswa pilih rating 1-5:
     - 1: Sangat Sulit
     - 2: Sulit
     - 3: Cukup
     - 4: Baik
     - 5: Sangat Mudah
  3. Mahasiswa klik "OK"
  4. Sistem simpan performance_rating ke study_session
  5. Sistem trigger SM-2 algorithm:
     - Hitung easiness factor baru
     - Hitung interval review berikutnya
     - Update topic.easiness_factor
     - Update topic.interval
     - Update topic.review_count
     - Update topic.last_review_date
     - Check mastery condition
  6. Sistem tampilkan notifikasi: "Review berikutnya: [tanggal]"
  7. Sistem refresh dashboard

**Algorithm Flow (UC-15):**
```
if rating < 3:
    interval = 1 day
    review_count = 0  (RESET)
else:
    if review_count == 0:
        interval = 1 day
    elif review_count == 1:
        interval = 6 days
    else:
        interval = previous_interval × easiness_factor
    
    review_count += 1

if review_count >= 5 AND rating >= 4 AND interval >= 30:
    mastered = true
```

#### **UC-16: Lihat Jadwal per Tanggal**
- **Aktor:** Mahasiswa
- **Main Flow:**
  1. Mahasiswa klik "📅 Lihat Jadwal"
  2. Sistem buka Schedule View window
  3. Mahasiswa pilih tanggal di date picker
  4. Sistem query study_sessions untuk tanggal tersebut
  5. Sistem tampilkan semua sesi dengan detail lengkap

#### **UC-17: Lihat Progress Keseluruhan**
- **Aktor:** Mahasiswa
- **Main Flow:**
  1. Mahasiswa buka dashboard (otomatis saat start)
  2. Sistem hitung:
     - Total Topics
     - Mastered Topics (review_count >= 5, interval >= 30)
     - Overall Progress % = (mastered / total) × 100
     - Today Total Tasks
     - Today Completed Tasks
     - Today Progress % = (completed / total) × 100
  3. Sistem tampilkan di statistics cards dengan progress bars

#### **UC-18: Lihat Ujian Mendatang**
- **Aktor:** Mahasiswa
- **Main Flow:**
  1. Mahasiswa lihat widget "Ujian Mendatang" di dashboard
  2. Sistem query exam_schedules WHERE exam_date >= today AND completed = false
  3. Sistem urutkan berdasarkan exam_date ASC
  4. Sistem hitung days until exam
  5. Sistem tampilkan dengan color coding:
     - Merah: ≤ 3 hari (urgent)
     - Kuning: 4-7 hari (soon)
     - Normal: > 7 hari

---

## Use Case Priority Matrix

| Priority | Use Cases | Keterangan |
|----------|-----------|------------|
| **HIGH** | UC-01, UC-05, UC-09, UC-12, UC-13, UC-14, UC-15 | Core functionality - critical untuk operasional aplikasi |
| **MEDIUM** | UC-02, UC-06, UC-10, UC-16, UC-17, UC-18 | Important features - meningkatkan user experience |
| **LOW** | UC-03, UC-07, UC-11 | Support features - administrative |

---

## Non-Functional Requirements

### **Performance**
- Dashboard harus load dalam < 2 detik
- Schedule generation untuk 7 hari harus selesai dalam < 5 detik
- Database query response time < 100ms

### **Usability**
- Aplikasi harus intuitif, minimal onboarding diperlukan
- Semua dialog harus self-explanatory
- Error messages harus jelas dan actionable

### **Reliability**
- Data integrity harus terjaga (foreign key constraints)
- Aplikasi harus auto-save, tidak ada data loss
- Database harus auto-create jika belum ada

### **Scalability**
- Aplikasi harus bisa handle 20+ courses per semester
- Aplikasi harus bisa handle 200+ topics
- Database query harus tetap fast dengan data besar

### **Security**
- Data disimpan lokal di device pengguna
- Tidak ada network communication (offline-first)
- Database tidak encrypted (single-user, personal use)

---

## Future Extensions (Planned)

### **Version 2.0 - Multi-User Consideration**

Jika di masa depan aplikasi akan dikembangkan menjadi multi-user atau collaborative, maka akan ada aktor tambahan:

#### **2. Dosen/Instruktur (Future)** 👨‍🏫
- Bisa melihat aggregate statistics dari mahasiswa (anonim)
- Bisa memberikan recommended study materials
- Bisa set default priorities untuk topik tertentu

#### **3. Administrator (Future)** 👨‍💼
- Manage user accounts
- View system-wide analytics
- Export data untuk research purposes

#### **4. Study Group Member (Future)** 👥
- Berbagi study schedule dengan grup
- Collaborative study sessions
- Group progress tracking

---

## Kesimpulan

Saat ini (**Version 1.0**), aplikasi **Adaptive Study Planner** memiliki:

✅ **1 Aktor Utama:** Mahasiswa (Student)  
✅ **18 Use Cases** yang mencakup manajemen data dan pembelajaran  
✅ **Single-user, offline-first application**  
✅ **Personal study management tool**  

Desain ini dipilih untuk:
- **Simplicity**: Fokus pada core value proposition
- **Privacy**: Data fully controlled oleh pengguna
- **Performance**: Tidak ada network overhead
- **Accessibility**: Tidak memerlukan internet connection

Untuk future versions, sistem dapat dikembangkan menjadi collaborative/multi-user jika diperlukan.

---

**Document Version:** 1.0  
**Last Updated:** October 20, 2025  
**Status:** Production Ready ✅

