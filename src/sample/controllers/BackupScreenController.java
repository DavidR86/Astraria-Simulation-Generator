package sample.controllers;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 4/12/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import sample.Config;
import sample.Main;

import java.io.File;

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
    private FileChooser fileChooser;

    @FXML
    public void initialize(){
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "seconds",
                        "minutes",
                        "hours"
                );
        backupDurationComboBox.setItems(options);

        fileChooser = new FileChooser();
    }

    public void setBackupSimPath(ActionEvent actionEvent) {
        fileChooser.setTitle("Select simulation file");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.nbd (Nbody data file)", "*.nbd"));
        File file = fileChooser.showOpenDialog(mainClass.getPrimaryStage());
        if (file != null) {
            backupSimPath.setText(file.getAbsolutePath());
        }
    }

    public void setOutputFilePath(ActionEvent actionEvent) {
        fileChooser.setTitle("Select backup file");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.bck (Backup file)", "*.bck"));
        File file = fileChooser.showOpenDialog(mainClass.getPrimaryStage());
        if (file != null) {
            backupFilePath.setText(file.getAbsolutePath());
        }
    }

    public void goBack(ActionEvent actionEvent) {
        mainClass.ChangeToTitleScreen();
    }

    public void startSimulation(ActionEvent actionEvent) {
        if (validate()){
            Config.backup=true;
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "One or more required fields have not been correctly filled.");
            alert.showAndWait();
        }
    }

    private boolean validate(){

        Config.outputFile=new File(backupSimPath.getText());
        Config.inputFile=new File(backupFilePath.getText());

        if (!Config.outputFile.exists()){
            backupSimPath.setStyle("-fx-border-color: red;");
            return false;
        }else {
            backupSimPath.setStyle("");
        }

        if (!Config.inputFile.exists()){
            backupSimPath.setStyle("-fx-border-color: red;");
        }else {
            backupSimPath.setStyle("");
            return false;
        }

        double mult;
        //System.out.println(durationComboBox.getValue());
        if (backupDurationComboBox.getValue()==null){
            backupDurationComboBox.setValue("seconds");
        }
        if ( backupDurationComboBox.getValue().equals("seconds")){
            mult=1;
            //System.out.println("mult: 1");
        }else if (backupDurationComboBox.getValue().toString().equals("minutes")){
            mult=60;
            //System.out.println("mult: 60");
        }else{
            mult=3600;
            //System.out.println("mult: 3600");
        }

        try {
            backupDurationField.setStyle("");
            Config.simDuration=mult*Double.parseDouble(backupDurationField.getText());
        }catch (Exception e){
            //System.out.println("Wrong input");
            backupDurationField.setStyle("-fx-border-color: red;");
            return false;
        }



        return true;
    }

    public void setMain (Main main){
        mainClass = main;
    }
}
