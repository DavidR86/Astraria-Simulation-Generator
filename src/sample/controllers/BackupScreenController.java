package sample.controllers;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 4/12/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.Main;

public class BackupScreenController {
    public TextField backupSimPath;
    public TextField backupFilePath;
    public Button backupSimPathButton;
    public Button backupFilePathButton;
    public TextField backupDurationField;
    public ComboBox backupDurationComboBox;
    public TextArea backupStatusField;
    public Button cancelButton;
    public Button startButton;
    public Main mainClass;

    public void setBackupSimPath(ActionEvent actionEvent) {
    }

    public void setOutputFilePath(ActionEvent actionEvent) {
    }

    public void goBack(ActionEvent actionEvent) {
        mainClass.ChangeToTitleScreen();
    }

    public void startSimulation(ActionEvent actionEvent) {

    }

    public void setMain (Main main){
        mainClass = main;
    }
}
