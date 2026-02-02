@echo off

echo Suppression du dossier bin...
rmdir /s /q bin

mkdir bin

echo Création de la liste des sources...
dir /b /s projet\src\billeterie\*.java > sources.txt

echo Compilation en cours...
javac --module-path C:/javafx/lib --add-modules javafx.controls,javafx.fxml -d bin -cp "projet/lib/mysql-connector-j-9.5.0.jar" @sources.txt

IF ERRORLEVEL 1 (
    echo Erreur lors de la compilation. Arrêt du script.
    pause
    exit /b 1
)

echo Copie des ressources...
xcopy /e /i /y projet\resources bin\resources

echo Lancement de l'application...
java --module-path C:/javafx/lib --add-modules javafx.controls,javafx.fxml -cp "bin;projet/lib/mysql-connector-j-9.5.0.jar" billeterie.App

pause
