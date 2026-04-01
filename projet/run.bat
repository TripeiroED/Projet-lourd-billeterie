@echo off
setlocal enabledelayedexpansion

rem ---------- CONFIG ----------
set "PROJECT_DIR=%~dp0"
set "JAVAFX_LIB=C:\javafx\lib"
set "LIB_DIR=%PROJECT_DIR%lib"

rem ---------- CLEAN / BUILD ----------
if exist "%PROJECT_DIR%bin" rmdir /s /q "%PROJECT_DIR%bin"
mkdir "%PROJECT_DIR%bin"

echo Recherche des fichiers sources...
set "SOURCES="
for /r "%PROJECT_DIR%src\billeterie\model" %%f in (*.java) do set "SOURCES=!SOURCES! "%%f""
for /r "%PROJECT_DIR%src\billeterie\controller" %%f in (*.java) do set "SOURCES=!SOURCES! "%%f""
for /r "%PROJECT_DIR%src\billeterie\utils" %%f in (*.java) do set "SOURCES=!SOURCES! "%%f""
for /r "%PROJECT_DIR%src\billeterie\view" %%f in (*.java) do set "SOURCES=!SOURCES! "%%f""

echo Compilation des sources...
javac --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%LIB_DIR%\*" -d "%PROJECT_DIR%bin" %SOURCES%
if errorlevel 1 (
    echo Erreur de compilation. Verifie le chemin JavaFX, les jars dans lib et ta version de JDK.
    pause
    endlocal
    exit /b 1
)

echo Lancement de l'application...
java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%PROJECT_DIR%bin;%PROJECT_DIR%;%LIB_DIR%\*" billeterie.view.App
if errorlevel 1 (
    echo Erreur d'execution. Verifie les jars dans "%LIB_DIR%".
    pause
    endlocal
    exit /b 1
)

pause
endlocal
