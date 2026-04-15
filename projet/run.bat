@echo off
setlocal enabledelayedexpansion

set "PROJECT_DIR=%~dp0"
set "JAVAFX_LIB=C:\javafx\lib"
set "LIB_DIR=%PROJECT_DIR%lib"
set "SRC_DIR=%PROJECT_DIR%src\billeterie"
set "BIN_DIR=%PROJECT_DIR%bin"

if exist "%BIN_DIR%" rmdir /s /q "%BIN_DIR%"
mkdir "%BIN_DIR%"

echo ======================
echo Compilation...
echo ======================

set "SOURCES="
for /r "%SRC_DIR%\model" %%f in (*.java) do call set SOURCES=%%SOURCES%% "%%f"
for /r "%SRC_DIR%\controller" %%f in (*.java) do call set SOURCES=%%SOURCES%% "%%f"
for /r "%SRC_DIR%\utils" %%f in (*.java) do call set SOURCES=%%SOURCES%% "%%f"
for /r "%SRC_DIR%\view" %%f in (*.java) do call set SOURCES=%%SOURCES%% "%%f"

javac --release 21 --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%LIB_DIR%\*" -d "%BIN_DIR%" %SOURCES%

if errorlevel 1 (
    echo ERREUR COMPILATION
    pause
    exit /b 1
)

echo ======================
echo Lancement...
echo ======================

java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%BIN_DIR%;%LIB_DIR%\*" billeterie.view.App

if errorlevel 1 (
    echo ERREUR EXECUTION
    pause
    exit /b 1
)

pause
endlocal
