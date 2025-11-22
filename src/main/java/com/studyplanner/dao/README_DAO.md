# Struktur DAO (Data Access Object)

## 📁 Struktur Folder DAO

```
src/main/java/com/studyplanner/dao/
├── DAOBase.java           # Interface dasar untuk semua DAO
├── DAOPengguna.java       # DAO untuk manajemen users
├── DAOMataKuliah.java     # DAO untuk manajemen courses
├── DAOTopik.java          # DAO untuk manajemen topics
├── DAOJadwalUjian.java    # DAO untuk manajemen exam schedules
├── DAOSesiBelajar.java    # DAO untuk manajemen study sessions
└── README_DAO.md          # Dokumentasi ini
```

## 🎯 Tujuan Struktur DAO

Struktur DAO dibuat untuk:

1. **Separation of Concerns** - Memisahkan logika akses data dari business logic
2. **Maintainability** - Setiap DAO fokus pada satu entitas saja
3. **Testability** - Lebih mudah untuk membuat unit test
4. **Reusability** - Kode lebih mudah digunakan kembali
5. **Clean Architecture** - Mengikuti best practice software engineering

## 📋 Interface DAOBase

Interface dasar yang mendefinisikan operasi CRUD standar:

```java
public interface DAOBase<T, ID> {
    ID simpan(T entitas);
    T ambilBerdasarkanId(ID id);
    List<T> ambilSemua();
    boolean perbarui(T entitas);
    boolean hapus(ID id);
    int hitungTotal();
}
```

## ✅ Status Implementasi

### Selesai Dibuat:
- ✅ **DAOBase.java** - Interface dasar dengan method CRUD standar
- ✅ **DAOPengguna.java** - DAO untuk users dengan fitur:
  - Login lokal (username/password)
  - Login Google OAuth
  - Manajemen profil user
- ✅ **DAOMataKuliah.java** - DAO untuk mata kuliah dengan fitur:
  - CRUD mata kuliah
  - Filter berdasarkan semester
  - Pencarian berdasarkan nama/kode
- ✅ **DAOTopik.java** - DAO untuk topik dengan fitur:
  - CRUD topik
  - Spaced repetition (FSRS)
  - Filter berdasarkan kesulitan

- ✅ **DAOJadwalUjian.java** - DAO untuk jadwal ujian dengan fitur:
  - CRUD jadwal ujian
  - Query ujian mendatang
  - Filter berdasarkan rentang tanggal
  - Tandai ujian selesai
- ✅ **DAOSesiBelajar.java** - DAO untuk sesi belajar dengan fitur:
  - CRUD sesi belajar
  - Query sesi hari ini dan mendatang
  - Filter berdasarkan topik/mata kuliah
  - Tracking performa dan durasi

## ⚠️ Catatan Penting

### 1. Dependency pada ManajerBasisData

DAO classes saat ini membutuhkan method `bukaKoneksi()` di ManajerBasisData:

```java
public Connection bukaKoneksi() {
    return this.koneksi;
}
```

**Tambahkan method ini di ManajerBasisData.java** untuk mengaktifkan DAO pattern.

### 2. Sinkronisasi Model dengan Database Schema

Beberapa field di database schema belum ada di model classes:

**MataKuliah.java** perlu menambahkan:
```java
private int userId;          // Untuk multi-user support
private String warna;        // Warna kategori mata kuliah
private String semester;     // Semester mata kuliah
private Timestamp dibuatPada; // Tanggal dibuat
```

**Topik.java** perlu menambahkan:
```java
private int mataKuliahId;              // Foreign key
private int estimasiDurasiMenit;       // Estimasi waktu belajar
private int intervalUlasan;            // Interval rekomendasi algoritma
private LocalDate tanggalUlasanBerikutnya; // Tanggal ulasan berikutnya
private int jumlahPengulangan;         // Jumlah review yang sudah dilakukan
private Timestamp dibuatPada;          // Tanggal dibuat
```

## 🚀 Cara Menggunakan DAO (Setelah Setup)

### Contoh 1: DAOPengguna

```java
// Inisialisasi
ManajerBasisData manajerDB = new ManajerBasisData();
DAOPengguna daoUser = new DAOPengguna(manajerDB);

// Membuat user baru (lokal)
int userId = daoUser.simpanPenggunaLokal(
    "username123",
    hashedPassword,
    "email@example.com",
    "Nama Lengkap"
);

// Mencari user berdasarkan username
Map<String, Object> user = daoUser.cariBerdasarkanUsername("username123");

// Mengambil semua users
List<Map<String, Object>> semuaUser = daoUser.ambilSemua();

// Menghitung total users
int totalUsers = daoUser.hitungTotal();
```

### Contoh 2: DAOMataKuliah

```java
// Inisialisasi
ManajerBasisData manajerDB = new ManajerBasisData();
DAOMataKuliah daoMK = new DAOMataKuliah(manajerDB);

// Membuat mata kuliah baru
MataKuliah mk = new MataKuliah();
mk.setNama("Pemrograman Berorientasi Objek");
mk.setKode("IF-201");
mk.setDeskripsi("Belajar konsep OOP");

int idMK = daoMK.simpan(mk);

// Mengambil berdasarkan ID
MataKuliah mkDariDB = daoMK.ambilBerdasarkanId(idMK);

// Mencari berdasarkan kode
MataKuliah mkByKode = daoMK.ambilBerdasarkanKode("IF-201", userId);

// Update
mk.setDeskripsi("Deskripsi baru");
daoMK.perbarui(mk);

// Delete
daoMK.hapus(idMK);
```

### Contoh 3: DAOTopik

```java
// Inisialisasi
ManajerBasisData manajerDB = new ManajerBasisData();
DAOTopik daoTopik = new DAOTopik(manajerDB);

// Membuat topik baru
Topik topik = new Topik();
topik.setNama("Inheritance");
topik.setDeskripsi("Konsep pewarisan di OOP");
topik.setTingkatKesulitan("medium");

int idTopik = daoTopik.simpan(topik);

// Mengambil topik untuk diulang hari ini
LocalDate today = LocalDate.now();
List<Topik> topikReview = daoTopik.ambilTopikUntukDiulang(-1, today);

// Update spaced repetition data
daoTopik.perbaruiDataSpacedRepetition(
    idTopik,
    3,  // interval baru (hari)
    LocalDate.now().plusDays(3),  // tanggal review berikutnya
    2.6,  // faktor kemudahan baru
    2   // jumlah pengulangan
);
```

### Contoh 4: DAOJadwalUjian

```java
// Inisialisasi
ManajerBasisData manajerDB = new ManajerBasisData();
DAOJadwalUjian daoUjian = new DAOJadwalUjian(manajerDB);

// Membuat jadwal ujian baru
JadwalUjian ujian = new JadwalUjian();
ujian.setIdMataKuliah(1);
ujian.setTipeUjian("UTS");
ujian.setJudul("Ujian Tengah Semester OOP");
ujian.setTanggalUjian(LocalDate.of(2025, 12, 15));
ujian.setWaktuUjian(LocalTime.of(10, 0));
ujian.setLokasi("Gedung A Ruang 301");
ujian.setCatatan("Bawa kalkulator");

int idUjian = daoUjian.simpan(ujian);

// Mengambil ujian mendatang
List<JadwalUjian> ujianMendatang = daoUjian.ambilUjianMendatang();

// Mengambil ujian dalam rentang tanggal
LocalDate mulai = LocalDate.now();
LocalDate akhir = LocalDate.now().plusMonths(1);
List<JadwalUjian> ujianBulanIni = daoUjian.ambilBerdasarkanRentangTanggal(mulai, akhir);

// Tandai ujian selesai
daoUjian.tandaiSelesai(idUjian);
```

### Contoh 5: DAOSesiBelajar

```java
// Inisialisasi
ManajerBasisData manajerDB = new ManajerBasisData();
DAOSesiBelajar daoSesi = new DAOSesiBelajar(manajerDB);

// Membuat sesi belajar baru
SesiBelajar sesi = new SesiBelajar();
sesi.setIdTopik(1);
sesi.setIdMataKuliah(1);
sesi.setTanggalJadwal(LocalDate.now());
sesi.setTipeSesi("Review");
sesi.setDurasiMenit(45);

int idSesi = daoSesi.simpan(sesi);

// Mengambil sesi hari ini
List<SesiBelajar> sesiHariIni = daoSesi.ambilSesiHariIni();

// Mengambil sesi mendatang yang belum selesai
List<SesiBelajar> sesiMendatang = daoSesi.ambilSesiMendatang();

// Tandai sesi selesai dengan rating dan catatan
daoSesi.tandaiSelesai(idSesi, 4, "Sesi produktif, berhasil memahami konsep");

// Hitung sesi selesai hari ini
int jumlahSelesai = daoSesi.hitungSesiSelesaiHariIni();
```

## 🔄 Migrasi dari ManajerBasisData ke DAO

### Before (Menggunakan ManajerBasisData langsung):
```java
ManajerBasisData db = new ManajerBasisData();
MataKuliah mk = new MataKuliah(0, "OOP", "IF-201", "Deskripsi");
int id = db.tambahMataKuliah(mk);
```

### After (Menggunakan DAO Pattern):
```java
ManajerBasisData manajerDB = new ManajerBasisData();
DAOMataKuliah dao = new DAOMataKuliah(manajerDB);
MataKuliah mk = new MataKuliah(0, "OOP", "IF-201", "Deskripsi");
int id = dao.simpan(mk);
```

## 🎯 Keuntungan Menggunakan DAO

1. **Kode Lebih Terorganisir**: Setiap DAO fokus pada satu entitas
2. **Mudah di-Test**: Bisa mock DAO untuk unit testing
3. **Mudah di-Maintain**: Perubahan struktur data hanya perlu update 1 file
4. **Query Terpusat**: Semua query untuk satu entitas ada di satu tempat
5. **Konsisten**: Semua DAO mengikuti interface yang sama

## 📝 TODO untuk Implementasi Penuh

1. ✅ Buat interface DAOBase
2. ✅ Implementasi DAOPengguna
3. ✅ Implementasi DAOMataKuliah
4. ✅ Implementasi DAOTopik
5. ✅ Tambahkan method `bukaKoneksi()` di ManajerBasisData
6. ✅ Buat DAOJadwalUjian
7. ✅ Buat DAOSesiBelajar
8. ⏳ Refactor controllers untuk menggunakan DAO instead of ManajerBasisData
9. ⏳ Buat unit tests untuk setiap DAO

## 🔗 Referensi

- [Data Access Object Pattern](https://www.oracle.com/java/technologies/dataaccessobject.html)
- [DAO Design Pattern in Java](https://www.baeldung.com/java-dao-pattern)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

**Dibuat**: 2025-11-20
**Tujuan**: Meningkatkan arsitektur kode dengan separation of concerns
