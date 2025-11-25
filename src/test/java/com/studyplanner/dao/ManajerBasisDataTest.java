package com.studyplanner.dao;

import com.studyplanner.basisdata.ManajerBasisData;
import java.sql.Connection;
import java.sql.SQLException;

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
    public Connection bukaKoneksi() throws SQLException {
        return testHelper.getKoneksi();
    }
}
