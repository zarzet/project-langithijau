@echo off
echo ========================================
echo Build Aplikasi dengan Credential Bundle
echo ========================================
echo.

REM Check if credentials.json exists
if not exist "src\main\resources\credentials.json" (
    echo ERROR: credentials.json tidak ditemukan!
    echo Silakan copy credentials.json ke src\main\resources\
    pause
    exit /b 1
)

echo [1/3] Building Fat JAR dengan Maven...
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build gagal!
    pause
    exit /b 1
)

echo.
echo [2/3] JAR berhasil dibuat!
echo File: target\adaptive-study-planner-0.1.4-ALPHA.jar
echo.
echo CATATAN: Credential JSON sudah ter-bundle di dalam JAR
echo Aplikasi bisa langsung dijalankan tanpa setup credential
echo.

echo [3/3] Membuat launcher script...
(
echo @echo off
echo java -jar adaptive-study-planner-0.1.4-ALPHA.jar
echo pause
) > target\run.bat

echo.
echo ========================================
echo BUILD SELESAI!
echo ========================================
echo.
echo File yang dibuat:
echo - target\adaptive-study-planner-0.1.4-ALPHA.jar
echo - target\run.bat
echo.
echo Cara distribusi:
echo 1. Copy kedua file di atas ke folder baru
echo 2. Zip folder tersebut
echo 3. Bagikan ZIP file
echo.
echo User tinggal:
echo 1. Extract ZIP
echo 2. Double-click run.bat
echo 3. Login dengan Google account mereka
echo.
pause
