package com.studyplanner.dao;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.eksepsi.EksepsiKoneksiBasisData;
import java.sql.Connection;

/**
 * Subclass dari ManajerBasisData untuk testing.
 * Menggunakan koneksi dari TestDatabaseHelper (in-memory database).
 */
public class ManajerBasisDataTest extends ManajerBasisData {

    private final TestDatabaseHelper testHelper;

    public ManajerBasisDataTest(TestDatabaseHelper testHelper) {
        this.testHelper = testHelper;
    }

    @Override
    public Connection bukaKoneksi() {
        try {
            return testHelper.getKoneksi();
        } catch (Exception e) {
            throw new EksepsiKoneksiBasisData("Gagal mendapatkan koneksi test", e);
        }
    }
}
