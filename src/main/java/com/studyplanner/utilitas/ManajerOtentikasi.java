package com.studyplanner.utilitas;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class ManajerOtentikasi {
    private static final String APPLICATION_NAME = "Adaptive Study Planner";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    // Menggunakan scope profil dan email
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/userinfo.profile");
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static ManajerOtentikasi instance;
    private Oauth2 oauth2Service;
    private Userinfo currentUser;
    private java.util.Map<String, Object> currentLocalUser;

    private ManajerOtentikasi() {}

    public static synchronized ManajerOtentikasi getInstance() {
        if (instance == null) {
            instance = new ManajerOtentikasi();
        }
        return instance;
    }

    public Userinfo loginGoogle() throws IOException, GeneralSecurityException {
        // 1. Bangun Flow
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleAuthorizationCodeFlow flow = buatFlow(httpTransport);

        // 2. Otorisasi (Membuka Browser)
        // Port 8888 harus ditambahkan di Google Cloud Console > Credentials > Authorized Redirect URIs
        // http://localhost:8888/Callback
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        // 3. Dapatkan Info Pengguna
        return siapkanLayananDanPengguna(httpTransport, credential);
    }

    public boolean cobaPulihkanSesi() {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleAuthorizationCodeFlow flow = buatFlow(httpTransport);
            Credential credential = flow.loadCredential("user");

            if (credential != null && (credential.getRefreshToken() != null || 
                    credential.getExpiresInSeconds() == null || credential.getExpiresInSeconds() > 60)) {
                
                // Coba refresh jika perlu (library biasanya menangani ini otomatis saat request)
                // Tapi kita cek validitasnya dengan mencoba fetch user info
                siapkanLayananDanPengguna(httpTransport, credential);
                return true;
            }
        } catch (Exception e) {
            PencatatLog.error("Gagal memulihkan sesi: " + e.getMessage());
        }
        return false;
    }

    private GoogleAuthorizationCodeFlow buatFlow(HttpTransport httpTransport) throws IOException {
        InputStream in = ManajerOtentikasi.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new IOException("File credentials.json tidak ditemukan di folder resources!");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        
        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
    }

    private Userinfo siapkanLayananDanPengguna(HttpTransport httpTransport, Credential credential) throws IOException {
        oauth2Service = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        currentUser = oauth2Service.userinfo().get().execute();
        PencatatLog.info("Login/Pemulihan Sesi Berhasil: " + currentUser.getName());
        return currentUser;
    }

    public void logout() {
        try {
            File tokens = new File(TOKENS_DIRECTORY_PATH);
            if (tokens.exists()) {
                deleteDirectory(tokens);
            }
            currentUser = null;
            currentLocalUser = null;
            oauth2Service = null;
            PencatatLog.info("Logout berhasil.");
        } catch (Exception e) {
            PencatatLog.error("Gagal logout: " + e.getMessage());
        }
    }

    public Userinfo getCurrentUser() {
        return currentUser;
    }

    public java.util.Map<String, Object> getCurrentLocalUser() {
        return currentLocalUser;
    }

    public void setCurrentLocalUser(java.util.Map<String, Object> user) {
        this.currentLocalUser = user;
        this.currentUser = null; // Clear Google user jika ada
    }

    public boolean isLoggedIn() {
        return currentUser != null || currentLocalUser != null;
    }

    public String getCurrentUserName() {
        if (currentUser != null) {
            return currentUser.getName();
        } else if (currentLocalUser != null) {
            return (String) currentLocalUser.get("nama");
        }
        return null;
    }

    public String getCurrentUserProvider() {
        if (currentUser != null) {
            return "google";
        } else if (currentLocalUser != null) {
            return (String) currentLocalUser.get("provider");
        }
        return null;
    }

    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }
}
