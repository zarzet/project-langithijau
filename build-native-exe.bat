@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul

echo ============================================================
echo   Adaptive Study Planner - Native EXE Builder
echo   Version: 0.1.4-ALPHA (Production Build)
echo ============================================================
echo.

:: Check if credentials.json exists
if not exist "src\main\resources\credentials.json" (
    echo [ERROR] File credentials.json tidak ditemukan!
    echo.
    echo Pastikan file credentials.json ada di:
    echo   src\main\resources\credentials.json
    echo.
    echo Anda bisa menyalin dari credentials.json.example dan
    echo mengisinya dengan kredensial Google OAuth Anda.
    echo.
    pause
    exit /b 1
)

echo [INFO] Credentials.json ditemukan. Melanjutkan build...
echo.

:: Step 1: Clean previous build
echo ============================================================
echo [STEP 1/5] Membersihkan build sebelumnya...
echo ============================================================
call mvn clean -q
if errorlevel 1 (
    echo [ERROR] Maven clean gagal!
    pause
    exit /b 1
)
echo [OK] Build sebelumnya dibersihkan.
echo.

:: Step 2: Build Fat JAR with bundled credentials
echo ============================================================
echo [STEP 2/5] Building Fat JAR dengan kredensial terbundle...
echo ============================================================
echo [INFO] Kredensial akan di-embed dalam JAR file (terobfuskasi)
echo.
call mvn package -DskipTests -q
if errorlevel 1 (
    echo [ERROR] Maven package gagal!
    echo.
    echo Kemungkinan penyebab:
    echo   - JDK 25 tidak terinstall atau tidak di PATH
    echo   - Dependency tidak bisa didownload
    echo   - Error kompilasi di source code
    echo.
    echo Jalankan: mvn package -DskipTests
    echo untuk melihat detail error.
    pause
    exit /b 1
)

:: Check if JAR was created
set "JAR_FILE=target\adaptive-study-planner-0.1.4-ALPHA.jar"
if not exist "%JAR_FILE%" (
    echo [ERROR] Fat JAR tidak ditemukan di: %JAR_FILE%
    pause
    exit /b 1
)
echo [OK] Fat JAR berhasil dibuat: %JAR_FILE%
echo.

:: Step 3: Create installer directory
echo ============================================================
echo [STEP 3/5] Menyiapkan direktori installer...
echo ============================================================
if exist "target\installer" rmdir /s /q "target\installer"
mkdir "target\installer"
echo [OK] Direktori installer siap.
echo.

:: Step 4: Create native EXE using jpackage (app-image mode)
echo ============================================================
echo [STEP 4/5] Membuat native Windows EXE dengan jpackage...
echo ============================================================
echo [INFO] Proses ini membutuhkan waktu beberapa menit...
echo [INFO] EXE akan include bundled JRE (tidak perlu Java terinstall)
echo.

:: Try to find jpackage
set "JPACKAGE_CMD=jpackage"
where jpackage >nul 2>&1
if errorlevel 1 (
    :: Try common JDK paths
    if exist "C:\Program Files\Java\jdk-25\bin\jpackage.exe" (
        set "JPACKAGE_CMD=C:\Program Files\Java\jdk-25\bin\jpackage.exe"
    ) else if exist "C:\Program Files\Java\jdk-21\bin\jpackage.exe" (
        set "JPACKAGE_CMD=C:\Program Files\Java\jdk-21\bin\jpackage.exe"
    ) else (
        echo [ERROR] jpackage tidak ditemukan!
        echo.
        echo Pastikan JDK 14+ terinstall. Lokasi yang dicek:
        echo   - PATH system
        echo   - C:\Program Files\Java\jdk-25\bin\
        echo   - C:\Program Files\Java\jdk-21\bin\
        echo.
        pause
        exit /b 1
    )
)

echo [INFO] Menggunakan: %JPACKAGE_CMD%
echo.

:: Run jpackage to create portable app-image (no WiX required)
"%JPACKAGE_CMD%" ^
    --type app-image ^
    --name "AdaptiveStudyPlanner" ^
    --app-version "0.1.4" ^
    --vendor "Adaptive Study Planner" ^
    --description "Aplikasi Perencana Belajar Adaptif dengan Spaced Repetition" ^
    --copyright "2024 Adaptive Study Planner Team" ^
    --input target ^
    --main-jar adaptive-study-planner-0.1.4-ALPHA.jar ^
    --dest target\installer ^
    --java-options "--add-opens java.base/java.lang=ALL-UNNAMED" ^
    --java-options "--add-opens java.base/java.util=ALL-UNNAMED" ^
    --java-options "-Dfile.encoding=UTF-8"

if errorlevel 1 (
    echo.
    echo [ERROR] jpackage gagal membuat EXE!
    echo.
    echo Kemungkinan penyebab:
    echo   - JDK tidak terinstall dengan benar
    echo   - Tidak ada cukup ruang disk
    echo   - Antivirus memblokir proses
    echo.
    pause
    exit /b 1
)

:: Step 5: Copy to dist folder for GitHub release
echo.
echo ============================================================
echo [STEP 5/5] Menyalin installer ke folder dist/...
echo ============================================================
if not exist "dist" mkdir "dist"
copy "target\installer\AdaptiveStudyPlanner-0.1.4.exe" "dist\" /Y

if errorlevel 1 (
    echo [WARNING] Gagal menyalin ke folder dist.
) else (
    echo [OK] Installer disalin ke: dist\AdaptiveStudyPlanner-0.1.4.exe
)

echo.
echo ============================================================
echo   BUILD BERHASIL!
echo ============================================================
echo.
echo [OUTPUT] Installer tersedia di:
echo          dist\AdaptiveStudyPlanner-0.1.4.exe
echo.
echo [INFO] Cara distribusi:
echo   1. Commit dan push ke GitHub:
echo      git add dist/AdaptiveStudyPlanner-0.1.4.exe
echo      git commit -m "Add installer v0.1.4"
echo      git push
echo.
echo   2. Buat tag untuk release:
echo      git tag v0.1.4
echo      git push origin v0.1.4
echo.
echo   3. GitHub Actions akan otomatis membuat release
echo.
echo [KEAMANAN] Kredensial terobfuskasi dalam:
echo   - JAR file (binary bytecode)
echo   - Native EXE installer dengan bundled JRE
echo.
echo ============================================================
echo.

:: Show file size
for %%A in ("dist\AdaptiveStudyPlanner-0.1.4.exe") do (
    set /a "SIZE_MB=%%~zA / 1048576"
    echo [INFO] Ukuran installer: !SIZE_MB! MB
)
echo.

:: Ask if user wants to open dist folder
set /p "OPEN_FOLDER=Buka folder dist? (Y/N): "
if /i "%OPEN_FOLDER%"=="Y" (
    explorer "dist"
)

echo.
echo Terima kasih telah menggunakan Adaptive Study Planner Builder!
echo.
pause
