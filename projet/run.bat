@echo off
setlocal enabledelayedexpansion

rem ---------- CONFIG ----------
set "PROJECT_DIR=%~dp0"
set "JAVAFX_LIB=C:\javafx\lib"
set "JDBC_JAR=lib\mysql-connector-j-9.5.0.jar"

rem ---------- CLEAN / BUILD ----------
if exist "%PROJECT_DIR%bin" rmdir /s /q "%PROJECT_DIR%bin"
mkdir "%PROJECT_DIR%bin"

echo Recherche des fichiers sources...
set "SOURCES="
for /r "%PROJECT_DIR%src\billeterie\model" %%f in (*.java) do set "SOURCES=!SOURCES! "%%f""
for /r "%PROJECT_DIR%src\billeterie\controller" %%f in (*.java) do set "SOURCES=!SOURCES! "%%f""
for /r "%PROJECT_DIR%src\billeterie\view" %%f in (*.java) do set "SOURCES=!SOURCES! "%%f""

echo Compilation des sources...
javac --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -d "%PROJECT_DIR%bin" %SOURCES%
if errorlevel 1 (
    echo Erreur de compilation. Verifie le chemin JavaFX et ta version de JDK.
    pause
    endlocal
    exit /b 1
)

echo Lancement de l'application...
java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%PROJECT_DIR%bin;%PROJECT_DIR%%JDBC_JAR%" billeterie.view.App
if errorlevel 1 (
    echo Erreur d'execution. As-tu mis le driver JDBC en "%PROJECT_DIR%%JDBC_JAR%" ?
    pause
    endlocal
    exit /b 1
)

pause
endlocal