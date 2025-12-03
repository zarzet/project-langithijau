package com.studyplanner.layanan;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAODosen;
import com.studyplanner.dao.DAOMahasiswa;
import com.studyplanner.dao.DAOPengguna;
import com.studyplanner.model.Dosen;
import com.studyplanner.model.Mahasiswa;
import com.studyplanner.model.RolePengguna;
import com.studyplanner.model.StatusPengguna;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Layanan untuk mengelola pengguna sistem (UC-25).
 * Termasuk CRUD user, role management, dan status management.
 */
public class LayananPengguna {

    private final DAOPengguna daoPengguna;
    private final DAODosen daoDosen;
    private final DAOMahasiswa daoMahasiswa;

    public LayananPengguna(ManajerBasisData manajerDB) {
        this.daoPengguna = new DAOPengguna(manajerDB);
        this.daoDosen = new DAODosen(manajerDB);
        this.daoMahasiswa = new DAOMahasiswa(manajerDB);
    }

    /**
     * Membuat pengguna baru dengan role tertentu.
     *
     * @param nama Nama lengkap
     * @param email Email
     * @param username Username (untuk local auth)
     * @param password Password yang sudah di-hash
     * @param role Role pengguna
     * @return ID pengguna baru
     * @throws SQLException jika terjadi kesalahan database
     * @throws IllegalArgumentException jika data tidak valid
     */
    public int buatPengguna(String nama, String email, String username, 
                            String password, RolePengguna role) throws SQLException {
        validasiDataPengguna(nama, email, username);

        int userId = daoPengguna.simpanPenggunaLokal(username, password, email, nama);

        daoPengguna.perbaruiRole(userId, role.getKode());

        switch (role) {
            case MAHASISWA -> {
                Mahasiswa mhs = new Mahasiswa();
                mhs.setUserId(userId);
                daoMahasiswa.simpan(mhs);
            }
            case DOSEN -> {
                Dosen dosen = new Dosen();
                dosen.setUserId(userId);
                daoDosen.simpan(dosen);
            }
            case ADMIN -> {
            }
        }

        return userId;
    }

    /**
     * Membuat pengguna dosen baru dengan NIP.
     *
     * @param nama Nama lengkap
     * @param email Email
     * @param username Username
     * @param password Password yang sudah di-hash
     * @param nip NIP dosen
     * @param maxMahasiswa Kuota maksimal mahasiswa
     * @return ID pengguna baru
     * @throws SQLException jika terjadi kesalahan database
     */
    public int buatDosen(String nama, String email, String username, 
                         String password, String nip, int maxMahasiswa) throws SQLException {
        validasiDataPengguna(nama, email, username);

        if (nip != null && daoDosen.nipSudahAda(nip, 0)) {
            throw new IllegalArgumentException("NIP sudah digunakan");
        }

        int userId = daoPengguna.simpanPenggunaLokal(username, password, email, nama);
        daoPengguna.perbaruiRole(userId, RolePengguna.DOSEN.getKode());

        Dosen dosen = new Dosen();
        dosen.setUserId(userId);
        dosen.setNip(nip);
        dosen.setMaxMahasiswa(maxMahasiswa > 0 ? maxMahasiswa : 30);
        daoDosen.simpan(dosen);

        return userId;
    }

    /**
     * Membuat pengguna mahasiswa baru dengan NIM.
     *
     * @param nama Nama lengkap
     * @param email Email
     * @param username Username
     * @param password Password yang sudah di-hash
     * @param nim NIM mahasiswa
     * @param semester Semester saat ini
     * @return ID pengguna baru
     * @throws SQLException jika terjadi kesalahan database
     */
    public int buatMahasiswa(String nama, String email, String username, 
                             String password, String nim, int semester) throws SQLException {
        validasiDataPengguna(nama, email, username);

        if (nim != null && daoMahasiswa.nimSudahAda(nim, 0)) {
            throw new IllegalArgumentException("NIM sudah digunakan");
        }

        int userId = daoPengguna.simpanPenggunaLokal(username, password, email, nama);
        daoPengguna.perbaruiRole(userId, RolePengguna.MAHASISWA.getKode());

        Mahasiswa mhs = new Mahasiswa();
        mhs.setUserId(userId);
        mhs.setNim(nim);
        mhs.setSemester(semester > 0 ? semester : 1);
        daoMahasiswa.simpan(mhs);

        return userId;
    }

    /**
     * Mengambil semua pengguna.
     *
     * @return List pengguna
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Map<String, Object>> ambilSemuaPengguna() throws SQLException {
        return daoPengguna.ambilSemua();
    }

    /**
     * Mengambil pengguna berdasarkan role.
     *
     * @param role Role yang dicari
     * @return List pengguna
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Map<String, Object>> ambilPenggunaBerdasarkanRole(RolePengguna role) throws SQLException {
        return daoPengguna.ambilBerdasarkanRole(role.getKode());
    }

    /**
     * Mengambil pengguna berdasarkan ID.
     *
     * @param id ID pengguna
     * @return Data pengguna atau null
     * @throws SQLException jika terjadi kesalahan database
     */
    public Map<String, Object> ambilPengguna(int id) throws SQLException {
        return daoPengguna.ambilBerdasarkanId(id);
    }

    /**
     * Memperbarui role pengguna.
     * Jika mengubah dari/ke mahasiswa/dosen, akan membuat/hapus entry di tabel terkait.
     *
     * @param userId ID pengguna
     * @param roleBaru Role baru
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean ubahRole(int userId, RolePengguna roleBaru) throws SQLException {
        Map<String, Object> user = daoPengguna.ambilBerdasarkanId(userId);
        if (user == null) {
            throw new IllegalArgumentException("Pengguna tidak ditemukan");
        }

        String roleLama = (String) user.get("role");
        if (roleLama == null) roleLama = "mahasiswa";

        boolean berhasil = daoPengguna.perbaruiRole(userId, roleBaru.getKode());

        if (berhasil) {
            RolePengguna enumRoleLama = RolePengguna.dariKode(roleLama);
            if (enumRoleLama == RolePengguna.MAHASISWA && roleBaru != RolePengguna.MAHASISWA) {
                Mahasiswa mhs = daoMahasiswa.ambilBerdasarkanUserId(userId);
                if (mhs != null) {
                    daoMahasiswa.hapus(mhs.getId());
                }
            } else if (enumRoleLama == RolePengguna.DOSEN && roleBaru != RolePengguna.DOSEN) {
                Dosen dosen = daoDosen.ambilBerdasarkanUserId(userId);
                if (dosen != null) {
                    daoDosen.hapus(dosen.getId());
                }
            }

            if (roleBaru == RolePengguna.MAHASISWA && enumRoleLama != RolePengguna.MAHASISWA) {
                Mahasiswa mhs = new Mahasiswa();
                mhs.setUserId(userId);
                daoMahasiswa.simpan(mhs);
            } else if (roleBaru == RolePengguna.DOSEN && enumRoleLama != RolePengguna.DOSEN) {
                Dosen dosen = new Dosen();
                dosen.setUserId(userId);
                daoDosen.simpan(dosen);
            }
        }

        return berhasil;
    }

    /**
     * Memperbarui status pengguna (active/inactive/suspended).
     *
     * @param userId ID pengguna
     * @param status Status baru
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean ubahStatus(int userId, StatusPengguna status) throws SQLException {
        return daoPengguna.perbaruiStatus(userId, status.getKode());
    }

    /**
     * Menonaktifkan pengguna (soft delete).
     *
     * @param userId ID pengguna
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean nonaktifkanPengguna(int userId) throws SQLException {
        return daoPengguna.perbaruiStatus(userId, StatusPengguna.INACTIVE.getKode());
    }

    /**
     * Mengaktifkan kembali pengguna.
     *
     * @param userId ID pengguna
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean aktifkanPengguna(int userId) throws SQLException {
        return daoPengguna.perbaruiStatus(userId, StatusPengguna.ACTIVE.getKode());
    }

    /**
     * Menangguhkan pengguna (suspend).
     *
     * @param userId ID pengguna
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean tangguhkanPengguna(int userId) throws SQLException {
        return daoPengguna.perbaruiStatus(userId, StatusPengguna.SUSPENDED.getKode());
    }

    /**
     * Menghitung statistik pengguna.
     *
     * @return Map berisi statistik
     * @throws SQLException jika terjadi kesalahan database
     */
    public Map<String, Integer> hitungStatistikPengguna() throws SQLException {
        return Map.of(
            "total", daoPengguna.hitungTotal(),
            "mahasiswa", daoPengguna.hitungBerdasarkanRole("mahasiswa"),
            "dosen", daoPengguna.hitungBerdasarkanRole("dosen"),
            "admin", daoPengguna.hitungBerdasarkanRole("admin")
        );
    }

    private void validasiDataPengguna(String nama, String email, String username) throws SQLException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username minimal 3 karakter");
        }

        Map<String, Object> existing = daoPengguna.cariBerdasarkanUsername(username);
        if (existing != null) {
            throw new IllegalArgumentException("Username sudah digunakan");
        }
    }
}
