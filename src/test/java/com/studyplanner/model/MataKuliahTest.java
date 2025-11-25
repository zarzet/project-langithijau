package com.studyplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test untuk model MataKuliah.
 */
@DisplayName("MataKuliah Model Tests")
class MataKuliahTest {

    private MataKuliah mataKuliah;

    @BeforeEach
    void setUp() {
        mataKuliah = new MataKuliah();
    }

    @Nested
    @DisplayName("Konstruktor Tests")
    class KonstruktorTests {

        @Test
        @DisplayName("Konstruktor default menginisialisasi daftarTopik kosong")
        void konstruktorDefault_daftarTopikKosong() {
            MataKuliah mk = new MataKuliah();
            
            assertNotNull(mk.getDaftarTopik(), "Daftar topik tidak boleh null");
            assertTrue(mk.getDaftarTopik().isEmpty(), "Daftar topik harus kosong");
        }

        @Test
        @DisplayName("Konstruktor dengan parameter menginisialisasi dengan benar")
        void konstruktorDenganParameter_inisialisasiBenar() {
            MataKuliah mk = new MataKuliah(1, "Struktur Data", "IF2040", "Mata kuliah dasar");

            assertEquals(1, mk.getId());
            assertEquals("Struktur Data", mk.getNama());
            assertEquals("IF2040", mk.getKode());
            assertEquals("Mata kuliah dasar", mk.getDeskripsi());
            assertNotNull(mk.getDaftarTopik());
            assertTrue(mk.getDaftarTopik().isEmpty());
        }
    }

    @Nested
    @DisplayName("Getter dan Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("setId dan getId bekerja dengan benar")
        void setIdDanGetId() {
            mataKuliah.setId(10);
            assertEquals(10, mataKuliah.getId());
        }

        @Test
        @DisplayName("setNama dan getNama bekerja dengan benar")
        void setNamaDanGetNama() {
            mataKuliah.setNama("Algoritma dan Pemrograman");
            assertEquals("Algoritma dan Pemrograman", mataKuliah.getNama());
        }

        @Test
        @DisplayName("setKode dan getKode bekerja dengan benar")
        void setKodeDanGetKode() {
            mataKuliah.setKode("IF1010");
            assertEquals("IF1010", mataKuliah.getKode());
        }

        @Test
        @DisplayName("setDeskripsi dan getDeskripsi bekerja dengan benar")
        void setDeskripsiDanGetDeskripsi() {
            mataKuliah.setDeskripsi("Pengenalan pemrograman dasar");
            assertEquals("Pengenalan pemrograman dasar", mataKuliah.getDeskripsi());
        }

        @Test
        @DisplayName("setDaftarTopik dan getDaftarTopik bekerja dengan benar")
        void setDaftarTopikDanGetDaftarTopik() {
            List<Topik> daftarBaru = new ArrayList<>();
            daftarBaru.add(new Topik(1, 1, "Topik 1", "Deskripsi 1"));
            daftarBaru.add(new Topik(2, 1, "Topik 2", "Deskripsi 2"));
            
            mataKuliah.setDaftarTopik(daftarBaru);
            
            assertEquals(2, mataKuliah.getDaftarTopik().size());
            assertEquals("Topik 1", mataKuliah.getDaftarTopik().get(0).getNama());
        }
    }

    @Nested
    @DisplayName("Operasi Topik Tests")
    class OperasiTopikTests {

        @Test
        @DisplayName("tambahTopik menambahkan topik ke daftar")
        void tambahTopik_menambahkanTopik() {
            Topik topik1 = new Topik(1, 1, "Array", "Struktur data array");
            Topik topik2 = new Topik(2, 1, "Linked List", "Struktur data linked list");

            mataKuliah.tambahTopik(topik1);
            assertEquals(1, mataKuliah.getDaftarTopik().size());

            mataKuliah.tambahTopik(topik2);
            assertEquals(2, mataKuliah.getDaftarTopik().size());

            assertTrue(mataKuliah.getDaftarTopik().contains(topik1));
            assertTrue(mataKuliah.getDaftarTopik().contains(topik2));
        }

        @Test
        @DisplayName("tambahTopik dapat menambahkan topik null")
        void tambahTopik_bisaMenambahkanNull() {
            mataKuliah.tambahTopik(null);
            assertEquals(1, mataKuliah.getDaftarTopik().size());
            assertNull(mataKuliah.getDaftarTopik().get(0));
        }

        @Test
        @DisplayName("Daftar topik bisa dimodifikasi langsung")
        void daftarTopikBisaDimodifikasi() {
            Topik topik = new Topik(1, 1, "Test", "Test");
            mataKuliah.getDaftarTopik().add(topik);
            
            assertEquals(1, mataKuliah.getDaftarTopik().size());
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("toString mengembalikan format 'kode - nama'")
        void toStringMengembalikanFormatBenar() {
            mataKuliah.setKode("IF2040");
            mataKuliah.setNama("Struktur Data");
            
            assertEquals("IF2040 - Struktur Data", mataKuliah.toString());
        }

        @Test
        @DisplayName("toString dengan nilai null")
        void toStringDenganNilaiNull() {
            String result = mataKuliah.toString();
            assertEquals("null - null", result);
        }
    }
}
