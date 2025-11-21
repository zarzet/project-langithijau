package com.studyplanner.kontroler;

import com.google.api.services.oauth2.model.Userinfo;
import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PembuatDialogMD3;
import com.studyplanner.utilitas.PencatatLog;
import com.studyplanner.utilitas.UtilUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.util.Map;

public class KontrolerLogin {

    @FXML
    private StackPane rootPane;

    @FXML
    private Button googleLoginBtn;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginBtn;

    @FXML
    private Label showRegisterBtn;

    @FXML
    private VBox loginForm;

    @FXML
    private VBox registerForm;

    @FXML
    private TextField namaRegField;

    @FXML
    private TextField usernameRegField;

    @FXML
    private TextField emailRegField;

    @FXML
    private PasswordField passwordRegField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerBtn;

    @FXML
    private Label backToLoginBtn;

    private ManajerBasisData manajerBasisData;

    @FXML
    public void initialize() {
        manajerBasisData = new ManajerBasisData();

        googleLoginBtn.setOnAction(_ -> handleGoogleLogin());

        try {
            var imgUrl = getClass().getResource("/images/google_logo.jpg");
            if (imgUrl != null) {
                ImageView icon = new ImageView(new Image(imgUrl.toExternalForm()));
                icon.setFitHeight(20);
                icon.setFitWidth(20);
                icon.setPreserveRatio(true);
                googleLoginBtn.setGraphic(icon);
            }
        } catch (Exception e) {
        }

        loginBtn.setOnAction(_ -> handleUsernameLogin());

        showRegisterBtn.setOnMouseClicked(_ -> {
            loginForm.setVisible(false);
            loginForm.setManaged(false);
            registerForm.setVisible(true);
            registerForm.setManaged(true);
        });

        registerBtn.setOnAction(_ -> handleRegister());

        backToLoginBtn.setOnMouseClicked(_ -> {
            registerForm.setVisible(false);
            registerForm.setManaged(false);
            loginForm.setVisible(true);
            loginForm.setManaged(true);
        });

        if (rootPane != null) {
            rootPane.getStyleClass().add("light-mode");
        }

        Platform.runLater(() -> {
            if (rootPane != null && rootPane.getScene() != null) {
                rootPane.getScene().setFill(Color.TRANSPARENT);
            }
        });
    }

    private void handleGoogleLogin() {
        googleLoginBtn.setDisable(true);
        googleLoginBtn.setText("Menghubungkan ke Google...");

        new Thread(() -> {
            try {
                ManajerOtentikasi auth = ManajerOtentikasi.getInstance();
                Userinfo user = auth.loginGoogle();

                Platform.runLater(() -> {
                    if (user != null) {
                        try {
                            Map<String, Object> existingUser = manajerBasisData.cariUserByGoogleId(user.getId());
                            if (existingUser == null) {
                                manajerBasisData.tambahUserGoogle(user.getId(), user.getEmail(),
                                        user.getName(), user.getPicture());
                            }
                        } catch (Exception e) {
                            PencatatLog.error("Gagal menyimpan user Google: " + e.getMessage());
                        }

                        PencatatLog.info("Login Google Berhasil: " + user.getName());
                        bukaAplikasiUtama();
                    } else {
                        resetGoogleButton();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    PencatatLog.error("Gagal login Google: " + e.getMessage());
                    googleLoginBtn.setText("Gagal. Coba Lagi.");
                    googleLoginBtn.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void handleUsernameLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            UtilUI.tampilkanKesalahan("Username dan password harus diisi!");
            return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Memproses...");

        new Thread(() -> {
            try {
                Map<String, Object> user = manajerBasisData.cariUserByUsername(username);

                Platform.runLater(() -> {
                    if (user != null) {
                        String storedPassword = (String) user.get("password");
                        String hashedPassword = hashPassword(password);

                        if (storedPassword.equals(hashedPassword)) {
                            ManajerOtentikasi.getInstance().setCurrentLocalUser(user);
                            PencatatLog.info("Login Berhasil: " + username);
                            bukaAplikasiUtama();
                        } else {
                            UtilUI.tampilkanKesalahan("Password salah!");
                            resetLoginButton();
                        }
                    } else {
                        UtilUI.tampilkanKesalahan("Username tidak ditemukan!");
                        resetLoginButton();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    UtilUI.tampilkanKesalahan("Gagal login: " + e.getMessage());
                    resetLoginButton();
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void handleRegister() {
        String nama = namaRegField.getText().trim();
        String username = usernameRegField.getText().trim();
        String email = emailRegField.getText().trim();
        String password = passwordRegField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (nama.isEmpty() || username.isEmpty() || password.isEmpty()) {
            UtilUI.tampilkanKesalahan("Nama, username, dan password harus diisi!");
            return;
        }

        if (username.length() < 3) {
            UtilUI.tampilkanKesalahan("Username minimal 3 karakter!");
            return;
        }

        if (password.length() < 6) {
            UtilUI.tampilkanKesalahan("Password minimal 6 karakter!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            UtilUI.tampilkanKesalahan("Password dan konfirmasi password tidak sama!");
            return;
        }

        registerBtn.setDisable(true);
        registerBtn.setText("Mendaftar...");

        new Thread(() -> {
            try {
                Map<String, Object> existingUser = manajerBasisData.cariUserByUsername(username);

                Platform.runLater(() -> {
                    if (existingUser != null) {
                        UtilUI.tampilkanKesalahan("Username sudah digunakan! Pilih username lain.");
                        resetRegisterButton();
                    } else {
                        try {
                            String hashedPassword = hashPassword(password);

                            int userId = manajerBasisData.tambahUser(username, hashedPassword,
                                    email.isEmpty() ? null : email, nama, "local");

                            if (userId > 0) {
                                UtilUI.tampilkanInfo("Pendaftaran berhasil! Silakan login.");

                                registerForm.setVisible(false);
                                registerForm.setManaged(false);
                                loginForm.setVisible(true);
                                loginForm.setManaged(true);

                                namaRegField.clear();
                                usernameRegField.clear();
                                emailRegField.clear();
                                passwordRegField.clear();
                                confirmPasswordField.clear();

                                usernameField.setText(username);
                                passwordField.clear();

                                resetRegisterButton();
                            } else {
                                UtilUI.tampilkanKesalahan("Gagal mendaftar! Coba lagi.");
                                resetRegisterButton();
                            }
                        } catch (Exception e) {
                            UtilUI.tampilkanKesalahan("Gagal mendaftar: " + e.getMessage());
                            resetRegisterButton();
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    UtilUI.tampilkanKesalahan("Gagal mendaftar: " + e.getMessage());
                    resetRegisterButton();
                });
                e.printStackTrace();
            }
        }).start();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Gagal hash password", e);
        }
    }

    private void resetGoogleButton() {
        googleLoginBtn.setDisable(false);
        googleLoginBtn.setText("Masuk dengan Google");
    }

    private void resetLoginButton() {
        loginBtn.setDisable(false);
        loginBtn.setText("Masuk");
    }

    private void resetRegisterButton() {
        registerBtn.setDisable(false);
        registerBtn.setText("Daftar");
    }

    private void bukaAplikasiUtama() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) googleLoginBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.centerOnScreen();

            DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", false);

        } catch (Exception e) {
            PencatatLog.error("Gagal membuka MainView: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
