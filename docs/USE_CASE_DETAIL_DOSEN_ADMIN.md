# 📋 USE CASE DETAIL - DOSEN & ADMINISTRATOR

## Detail Use Case untuk Dosen Pembimbing dan Administrator

---

## C. MONITORING & PEMBIMBINGAN (UC-19 s/d UC-24)
**Aktor:** Dosen Pembimbing

### **UC-19: Lihat Daftar Mahasiswa Bimbingan**
- **Aktor:** Dosen Pembimbing
- **Deskripsi:** Dosen dapat melihat daftar semua mahasiswa yang menjadi bimbingannya
- **Precondition:** Dosen sudah login, sudah di-assign mahasiswa oleh admin
- **Postcondition:** Daftar mahasiswa ditampilkan dengan status pembelajaran
- **Main Flow:**
  1. Dosen login ke sistem
  2. Dosen klik menu "Mahasiswa Bimbingan"
  3. Sistem query database untuk daftar mahasiswa dengan advisor_id = dosen.id
  4. Sistem tampilkan tabel dengan kolom:
     - Nama Mahasiswa
     - NIM
     - Semester
     - Jumlah Mata Kuliah Aktif
     - Overall Progress (%)
     - Last Activity Date
     - Status (Active/Inactive)
  5. Dosen dapat sort dan filter berdasarkan kriteria
  6. Dosen dapat klik mahasiswa untuk lihat detail (UC-20)

---

### **UC-20: Lihat Progress Mahasiswa**
- **Aktor:** Dosen Pembimbing
- **Deskripsi:** Dosen dapat melihat progress pembelajaran detail seorang mahasiswa
- **Precondition:** Dosen sudah memilih mahasiswa dari daftar (UC-19)
- **Postcondition:** Dashboard progress mahasiswa ditampilkan
- **Main Flow:**
  1. Dosen klik nama mahasiswa dari daftar
  2. Sistem load data pembelajaran mahasiswa tersebut
  3. Sistem tampilkan dashboard yang berisi:
     - **Overall Statistics:**
       - Total Topics: X
       - Mastered Topics: Y (Z%)
       - Active Courses: N
       - Upcoming Exams: M
     - **Study Consistency:**
       - Current Streak: X days
       - Study Sessions This Week: Y
       - Completion Rate: Z%
     - **Performance Trends:**
       - Chart: Progress over time (line graph)
       - Chart: Session completion rate per week (bar chart)
       - Average Performance Rating: X.X/5.0
     - **Challenging Topics:**
       - List topik dengan rating rendah
       - List topik dengan review count tinggi tapi masih belum mastered
     - **Recent Activity:**
       - Last 10 study sessions dengan timestamp dan rating
  4. Dosen dapat export data ini sebagai PDF (UC-23)
  5. Dosen dapat memberikan rekomendasi (UC-22)

**Business Rules:**
- Dosen hanya bisa melihat data mahasiswa bimbingannya sendiri
- Data yang ditampilkan adalah read-only (dosen tidak bisa edit)
- Real-time update jika mahasiswa sedang aktif belajar

---

### **UC-21: Lihat Study Pattern Mahasiswa**
- **Aktor:** Dosen Pembimbing
- **Deskripsi:** Dosen dapat menganalisis pola belajar mahasiswa
- **Precondition:** Sudah ada data study sessions minimal 2 minggu
- **Main Flow:**
  1. Dari dashboard mahasiswa (UC-20), dosen klik tab "Study Pattern"
  2. Sistem analisis data study_sessions mahasiswa
  3. Sistem tampilkan:
     - **Time Pattern:**
       - Heatmap: Jam berapa mahasiswa paling sering belajar
       - Chart: Distribusi durasi belajar per hari
       - Peak productivity hours
     - **Subject Focus:**
       - Pie chart: Persentase waktu per mata kuliah
       - Bar chart: Study sessions count per course
       - Identifikasi course yang terabaikan
     - **Consistency Analysis:**
       - Calendar heatmap (GitHub-style): Hari mana saja belajar
       - Longest streak vs current streak
       - Days inactive
     - **Performance Pattern:**
       - Correlation: Waktu belajar vs performance rating
       - Trend: Apakah performance meningkat dari waktu ke waktu
       - Success rate by session type (Initial/Review/Practice)
  4. Sistem berikan insights otomatis:
     - "Mahasiswa cenderung belajar di malam hari"
     - "Course X kurang mendapat perhatian (hanya 5% total waktu)"
     - "Performance rating meningkat 15% dalam 2 minggu terakhir"

---

### **UC-22: Berikan Rekomendasi Topik**
- **Aktor:** Dosen Pembimbing
- **Deskripsi:** Dosen dapat memberikan rekomendasi topik tambahan atau sumber belajar
- **Precondition:** Dosen sedang melihat progress mahasiswa (UC-20)
- **Main Flow:**
  1. Dari dashboard mahasiswa, dosen klik tombol "Tambah Rekomendasi"
  2. Sistem tampilkan form:
     - Pilih Mata Kuliah (dropdown dari courses mahasiswa)
     - Nama Topik Baru (text input)
     - Deskripsi/Catatan (textarea)
     - Suggested Priority (1-5)
     - Suggested Difficulty (1-5)
     - Link/Sumber Belajar (optional, URL)
  3. Dosen isi form dan klik "Kirim Rekomendasi"
  4. Sistem simpan rekomendasi ke tabel `recommendations`
  5. Sistem kirim notifikasi ke mahasiswa
  6. Mahasiswa dapat:
     - Accept → otomatis tambah topik ke courses
     - Decline → rekomendasi ditandai sebagai declined
     - Ignore → tetap di pending

**Alternative Flow:**
- Dosen dapat memberikan catatan umum tanpa topik spesifik
- Dosen dapat attach file (PDF, link artikel, video)

---

### **UC-23: Export Laporan Progress**
- **Aktor:** Dosen Pembimbing
- **Deskripsi:** Dosen dapat export laporan progress mahasiswa untuk dokumentasi
- **Precondition:** Dosen sedang melihat progress mahasiswa (UC-20)
- **Main Flow:**
  1. Dari dashboard mahasiswa, dosen klik tombol "Export Laporan"
  2. Sistem tampilkan dialog export options:
     - Format: PDF / Excel / CSV
     - Periode: Last 7 days / Last 30 days / This Semester / All Time
     - Include: 
       ☑ Study Statistics
       ☑ Performance Trends (charts)
       ☑ Session Details
       ☑ Study Pattern Analysis
       ☑ Recommendations History
  3. Dosen pilih options dan klik "Generate"
  4. Sistem generate laporan dengan format yang dipilih
  5. Sistem tampilkan preview laporan
  6. Dosen klik "Download"
  7. File tersimpan di komputer dosen

**Laporan PDF berisi:**
- Header: Nama mahasiswa, NIM, Periode, Tanggal generate
- Executive Summary (1 halaman)
- Detailed Statistics (2-3 halaman)
- Charts & Visualizations
- Recommendations & Notes
- Footer: Nama dosen, signature digital

---

### **UC-24: Lihat Statistik Aggregat**
- **Aktor:** Dosen Pembimbing
- **Deskripsi:** Dosen dapat melihat statistik gabungan dari semua mahasiswa bimbingan
- **Precondition:** Dosen memiliki minimal 3 mahasiswa bimbingan
- **Main Flow:**
  1. Dari menu utama, dosen klik "Statistik Aggregat"
  2. Sistem aggregate data dari semua mahasiswa bimbingan dosen
  3. Sistem tampilkan dashboard:
     - **Overview:**
       - Total Mahasiswa Bimbingan: N
       - Average Overall Progress: X%
       - Total Study Sessions This Week: Y
       - Average Completion Rate: Z%
     - **Leaderboard (Anonim/Optional):**
       - Top 5 Most Consistent Students (by streak)
       - Top 5 Best Performance (by rating)
       - Top 5 Most Active (by session count)
     - **Aggregate Trends:**
       - Chart: Average progress all students over time
       - Chart: Total study hours per week (aggregate)
     - **Alerts:**
       - List mahasiswa dengan activity < 3 days (perlu perhatian)
       - List mahasiswa dengan completion rate < 50%
       - List mahasiswa dengan exam dalam 3 hari tapi progress < 60%
  4. Dosen dapat drill-down ke mahasiswa spesifik dengan klik

**Privacy Note:**
- Leaderboard dapat di-toggle antara "show names" vs "anonymous"
- Data aggregat tidak menampilkan identitas spesifik tanpa consent

---

## D. ADMINISTRASI SISTEM (UC-25 s/d UC-30)
**Aktor:** Administrator

### **UC-25: Manage User Accounts**
- **Aktor:** Administrator
- **Deskripsi:** Admin dapat membuat, mengedit, dan menonaktifkan akun pengguna
- **Precondition:** Admin sudah login dengan role administrator
- **Postcondition:** User account ter-manage dengan benar
- **Main Flow:**
  1. Admin klik menu "User Management"
  2. Sistem tampilkan tabel semua users:
     - User ID
     - Name
     - Email
     - Role (Mahasiswa/Dosen/Admin)
     - Status (Active/Inactive/Suspended)
     - Created Date
     - Last Login
  3. **Untuk Create New User:**
     - Admin klik "Add User"
     - Admin isi form: Name, Email, Password, Role
     - Admin klik "Create"
     - Sistem generate user ID dan simpan ke database
     - Sistem kirim email aktivasi ke user
  4. **Untuk Edit User:**
     - Admin pilih user dari tabel
     - Admin klik "Edit"
     - Admin ubah data (nama, email, role, status)
     - Admin klik "Save"
     - Sistem update database
  5. **Untuk Deactivate User:**
     - Admin pilih user
     - Admin klik "Deactivate"
     - Sistem konfirmasi
     - Sistem set status = inactive
     - User tidak bisa login lagi

**Business Rules:**
- Email harus unik per user
- Password minimal 8 karakter
- Tidak bisa delete user, hanya deactivate (data retention)
- Hanya Super Admin yang bisa create/edit Admin role

---

### **UC-26: Assign Dosen ke Mahasiswa**
- **Aktor:** Administrator
- **Deskripsi:** Admin dapat meng-assign dosen pembimbing ke mahasiswa
- **Precondition:** Ada user dengan role Dosen dan Mahasiswa di sistem
- **Main Flow:**
  1. Admin klik menu "Advisor Assignment"
  2. Sistem tampilkan interface dengan 2 kolom:
     - **Kiri:** List semua Dosen (dengan jumlah mahasiswa bimbingan masing-masing)
     - **Kanan:** List semua Mahasiswa (dengan status: assigned/unassigned)
  3. **Option 1: Assign dari Mahasiswa**
     - Admin pilih mahasiswa yang unassigned
     - Admin klik "Assign Advisor"
     - Admin pilih dosen dari dropdown
     - Admin klik "Save"
  4. **Option 2: Bulk Assign**
     - Admin select multiple mahasiswa (checkbox)
     - Admin pilih 1 dosen
     - Admin klik "Bulk Assign"
     - Sistem assign semua mahasiswa terpilih ke dosen tersebut
  5. Sistem update tabel `students` dengan advisor_id
  6. Sistem kirim notifikasi ke dosen dan mahasiswa

**Business Rules:**
- 1 mahasiswa hanya bisa punya 1 dosen pembimbing utama
- 1 dosen bisa membimbing max 30 mahasiswa (configurable)
- Admin bisa reassign jika diperlukan

---

### **UC-27: Backup & Restore Data**
- **Aktor:** Administrator
- **Deskripsi:** Admin dapat backup database dan restore jika diperlukan
- **Precondition:** Admin memiliki access ke server
- **Main Flow:**
  
  **BACKUP:**
  1. Admin klik menu "System Maintenance" → "Backup"
  2. Sistem tampilkan backup options:
     - Backup Type: Full / Incremental
     - Include: Database / Files / Both
     - Schedule: Now / Scheduled (daily/weekly)
  3. Admin pilih "Full Backup" + "Now"
  4. Admin klik "Start Backup"
  5. Sistem:
     - Export database ke SQL file
     - Compress menjadi .zip dengan encryption
     - Save ke backup directory dengan naming: `backup_YYYYMMDD_HHMMSS.zip`
     - Generate checksum untuk integrity check
  6. Sistem tampilkan summary:
     - Backup Size: X MB
     - Duration: Y seconds
     - Status: Success
     - Location: /backups/backup_20251020_150000.zip
  7. Admin dapat download backup file

  **RESTORE:**
  1. Admin klik "Restore"
  2. Sistem tampilkan list backup files yang available
  3. Admin pilih backup file yang ingin direstore
  4. Sistem tampilkan preview: tanggal, size, checksum
  5. Admin confirm dengan warning: "Current data will be overwritten"
  6. Sistem:
     - Stop all active connections
     - Restore database from backup
     - Verify integrity dengan checksum
     - Restart database connection
  7. Sistem tampilkan hasil: Success/Failed

**Business Rules:**
- Auto backup setiap malam jam 02:00
- Retention: Keep backups for 30 days
- Critical: Backup sebelum major system update

---

### **UC-28: View System Analytics**
- **Aktor:** Administrator
- **Deskripsi:** Admin dapat melihat analytics system-wide
- **Main Flow:**
  1. Admin klik menu "System Analytics"
  2. Sistem tampilkan dashboard:
     
     **User Statistics:**
     - Total Users: N (breakdown by role)
     - Active Users (last 7 days): X
     - New Registrations (this month): Y
     - User Growth Chart (monthly)
     
     **System Usage:**
     - Total Study Sessions: X
     - Total Study Hours: Y hours
     - Average Sessions per User: Z
     - Peak Usage Hours (heatmap)
     
     **Database Metrics:**
     - Total Courses: N
     - Total Topics: M
     - Total Exams Scheduled: P
     - Database Size: X GB
     - Growth Rate: +Y MB/month
     
     **Performance Metrics:**
     - Average Response Time: X ms
     - Uptime: 99.X%
     - Error Rate: Y%
     - Active Connections: Z
     
  3. Admin dapat filter by date range
  4. Admin dapat export analytics report

---

### **UC-29: Manage System Settings**
- **Aktor:** Administrator
- **Deskripsi:** Admin dapat configure system settings
- **Main Flow:**
  1. Admin klik menu "Settings"
  2. Sistem tampilkan settings categories:
     
     **General Settings:**
     - Application Name
     - Institution Name
     - Academic Year
     - Semester (Ganjil/Genap)
     
     **Algorithm Configuration:**
     - Spaced Repetition: Enable/Disable
     - SM-2 Initial Easiness Factor (default: 2.5)
     - Min Interval (days): 1
     - Max Interval (days): 365
     - Interleaving: Enable/Disable
     - Min Sessions per Day: 3
     - Max Sessions per Day: 6
     
     **User Limits:**
     - Max Courses per Student: 10
     - Max Topics per Course: 50
     - Max Students per Advisor: 30
     
     **Security:**
     - Password Min Length: 8
     - Session Timeout (minutes): 60
     - Max Login Attempts: 5
     - Enable Two-Factor Auth: Yes/No
     
     **Notifications:**
     - Email Notifications: Enable/Disable
     - SMTP Server, Port, Username, Password
     
  3. Admin edit settings yang diperlukan
  4. Admin klik "Save Changes"
  5. Sistem validate settings
  6. Sistem apply settings (some require restart)

---

### **UC-30: Generate System Reports**
- **Aktor:** Administrator
- **Deskripsi:** Admin dapat generate berbagai jenis laporan untuk institusi
- **Main Flow:**
  1. Admin klik menu "Reports"
  2. Sistem tampilkan report templates:
     - **Monthly Activity Report**
     - **Semester Summary Report**
     - **User Engagement Report**
     - **Academic Performance Report**
     - **System Health Report**
  3. Admin pilih template, misalnya "Semester Summary Report"
  4. Sistem tampilkan parameters:
     - Semester: Ganjil/Genap
     - Academic Year: 2024/2025
     - Include: All Users / Specific Department
  5. Admin set parameters dan klik "Generate"
  6. Sistem compile data dan create report
  7. Sistem tampilkan preview report
  8. Admin dapat:
     - Download as PDF
     - Download as Excel
     - Email to recipients
     - Schedule untuk auto-generate monthly

**Sample Report Content (Semester Summary):**
- Total Active Students: N
- Total Active Advisors: M
- Total Study Sessions: X
- Total Study Hours: Y
- Average Overall Progress: Z%
- Top Performing Students
- Most Active Courses
- System Usage Trends
- Recommendations for Next Semester

---

## Access Control Matrix

| Use Case | Mahasiswa | Dosen | Administrator |
|----------|-----------|-------|---------------|
| UC-01 s/d UC-18 | ✅ Full Access | ❌ | ❌ |
| UC-19 | ❌ | ✅ | ✅ (View All) |
| UC-20 | ✅ (Own Only) | ✅ (Students Only) | ✅ (All) |
| UC-21 | ✅ (Own Only) | ✅ (Students Only) | ✅ (All) |
| UC-22 | 👀 (Receive Only) | ✅ | ❌ |
| UC-23 | ✅ (Own Only) | ✅ (Students Only) | ✅ (All) |
| UC-24 | ❌ | ✅ | ✅ |
| UC-25 s/d UC-30 | ❌ | ❌ | ✅ |

**Legend:**
- ✅ = Full Access
- 👀 = Read Only / Receive
- ❌ = No Access

---

## Database Schema Extension (untuk Multi-User)

```sql
-- Tabel Users (extends existing)
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL, -- 'mahasiswa', 'dosen', 'admin'
    status TEXT DEFAULT 'active', -- 'active', 'inactive', 'suspended'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Tabel Students (extends existing courses, topics, etc)
CREATE TABLE students (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    nim TEXT NOT NULL UNIQUE,
    advisor_id INTEGER, -- dosen pembimbing
    semester INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (advisor_id) REFERENCES advisors(id)
);

-- Tabel Advisors
CREATE TABLE advisors (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    nip TEXT NOT NULL UNIQUE,
    max_students INTEGER DEFAULT 30,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabel Recommendations
CREATE TABLE recommendations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    advisor_id INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    course_id INTEGER,
    topic_name TEXT NOT NULL,
    description TEXT,
    suggested_priority INTEGER,
    suggested_difficulty INTEGER,
    resource_url TEXT,
    status TEXT DEFAULT 'pending', -- 'pending', 'accepted', 'declined'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (advisor_id) REFERENCES advisors(id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);
```

---

**Document Version:** 2.0  
**Last Updated:** October 20, 2025  
**Status:** Extended for Multi-User Support ✅

