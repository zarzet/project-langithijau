@echo off
setlocal enabledelayedexpansion

echo ============================================================
echo Build Native Windows EXE dengan Credential Ter-Bundle
echo ============================================================
echo.

REM Check if credentials.json exists
if not exist "src\main\resources\credentials.json" (
    echo ERROR: credentials.json tidak ditemukan!
    echo.
    echo Silakan pastikan file credentials.json ada di:
    echo src\main\resources\credentials.json
    echo.
    pause
    exit /b 1
)

echo [1/5] Cleaning previous builds...
call mvn clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Clean gagal!
    pause
    exit /b 1
)

echo.
echo [2/5] Building Fat JAR dengan credential ter-bundle...
call mvn package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build JAR gagal!
    pause
    exit /b 1
)

echo.
echo [3/5] Skipping jlink, using system Java runtime...
echo (jpackage will create runtime automatically)

echo.
echo [4/5] Creating Windows installer dengan jpackage...
echo This may take 5-10 minutes...
echo.

REM Try with icon first
jpackage --type exe ^
         --name "AdaptiveStudyPlanner" ^
         --app-version "0.1.4" ^
         --vendor "Adaptive Study Planner" ^
         --description "Aplikasi Perencana Belajar Adaptif dengan Spaced Repetition" ^
         --input target ^
         --main-jar adaptive-study-planner-0.1.4-ALPHA.jar ^
         --dest target\installer ^
         --win-dir-chooser ^
         --win-menu ^
         --win-shortcut ^
         --java-options "--add-opens" ^
         --java-options "java.base/java.lang=ALL-UNNAMED" ^
         --java-options "--add-opens" ^
         --java-options "java.base/java.util=ALL-UNNAMED"

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: jpackage gagal!
    echo.
    echo Troubleshooting:
    echo 1. Pastikan menggunakan JDK 25 (bukan JRE)
    echo 2. Pastikan JAVA_HOME sudah di-set: %JAVA_HOME%
    echo 3. Pastikan jpackage ada: %JAVA_HOME%\bin\jpackage.exe
    echo.
    pause
    exit /b 1
)

echo.
echo [5/5] Cleaning up temporary files...
REM Keep the installer, remove runtime-image to save space
REM rmdir /s /q target\runtime-image

echo.
echo ============================================================
echo BUILD NATIVE EXE SELESAI!
echo ============================================================
echo.
echo File installer dibuat:
echo target\installer\AdaptiveStudyPlanner-0.1.4.exe
echo.
echo Ukuran: ~100-150 MB (includes Java runtime)
echo.
echo FITUR:
echo - Native Windows executable
echo - Tidak perlu install Java
echo - Credential ter-bundle dan ter-obfuscate
echo - Auto-create Start Menu shortcut
echo - Auto-create Desktop shortcut
echo - Professional installer
echo.
echo CARA DISTRIBUSI:
echo 1. Upload AdaptiveStudyPlanner-0.1.4.exe ke cloud storage
echo 2. Bagikan link download
echo 3. User download dan install
echo 4. User login dengan Google account mereka
echo.
echo KEAMANAN:
echo - Credential ter-obfuscate di dalam EXE
echo - Sulit di-extract atau di-decompile
echo - Aman untuk distribusi publik
echo.
pause
