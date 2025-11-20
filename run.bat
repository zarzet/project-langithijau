@echo off
chcp 65001 >nul
cls

:menu
echo ========================================
echo   Perencana Belajar Adaptif
echo ========================================
echo.
echo Pilih aplikasi yang ingin dijalankan:
echo.
echo [1] Aplikasi Utama
echo [2] Aplikasi Utama (Fast - tanpa compile)
echo.
echo [3] Inspektur Database
echo [4] Inspektur Database (Fast - tanpa compile)
echo.
echo [5] Build Project (compile saja)
echo [0] Keluar
echo.
echo ========================================

set /p choice="Pilihan Anda (0-5): "

if "%choice%"=="1" goto app
if "%choice%"=="2" goto app-fast
if "%choice%"=="3" goto inspektur
if "%choice%"=="4" goto inspektur-fast
if "%choice%"=="5" goto build
if "%choice%"=="0" goto end

echo.
echo Pilihan tidak valid! Silakan pilih 0-5.
timeout /t 2 >nul
cls
goto menu

:app
cls
echo ========================================
echo   Menjalankan Aplikasi Utama
echo ========================================
echo.
echo Melakukan compile dan menjalankan aplikasi...
echo.
mvn clean compile exec:java -Dexec.mainClass="com.studyplanner.AplikasiUtama"
echo.
echo Aplikasi telah ditutup.
echo.
pause
cls
goto menu

:app-fast
cls
echo ========================================
echo   Menjalankan Aplikasi Utama (Fast)
echo ========================================
echo.
echo Menjalankan aplikasi tanpa compile...
echo Pastikan sudah di-compile sebelumnya!
echo.
mvn exec:java -Dexec.mainClass="com.studyplanner.AplikasiUtama"
echo.
echo Aplikasi telah ditutup.
echo.
pause
cls
goto menu

:inspektur
cls
echo ========================================
echo   Menjalankan Inspektur Database
echo ========================================
echo.
echo Melakukan compile dan menjalankan inspektur...
echo.
mvn clean compile exec:java -Dexec.mainClass="com.studyplanner.AplikasiInspekturDB"
echo.
echo Inspektur Database telah ditutup.
echo.
pause
cls
goto menu

:inspektur-fast
cls
echo ========================================
echo   Menjalankan Inspektur Database (Fast)
echo ========================================
echo.
echo Menjalankan inspektur tanpa compile...
echo Pastikan sudah di-compile sebelumnya!
echo.
mvn exec:java -Dexec.mainClass="com.studyplanner.AplikasiInspekturDB"
echo.
echo Inspektur Database telah ditutup.
echo.
pause
cls
goto menu

:build
cls
echo ========================================
echo   Build Project
echo ========================================
echo.
echo Melakukan compile project...
echo.
mvn clean compile
echo.
echo Build selesai!
echo.
pause
cls
goto menu

:end
cls
echo.
echo Terima kasih telah menggunakan Perencana Belajar Adaptif!
echo.
timeout /t 2 >nul
exit
