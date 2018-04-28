package sample.controllers;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 4/12/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import sample.Config;
import sample.Main;
import sample.fileManagement.BackupFileReader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

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

    long elapsedFrames;
    int bodyCount;

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
            startButton.setDisable(true);
            Config.backup=true;
            int mult = 1;
            if(backupDurationComboBox.getValue()!=null&&backupDurationComboBox.getValue()!="seconds"){
                if (backupDurationComboBox.getValue()=="minutes"){
                    mult=60;
                }else {
                    mult=3600;
                }
            }
            Config.simDuration= Float.parseFloat(backupDurationField.getText())*mult;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    setBackupData();
                }
            });
            thread.start();

        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "One or more required fields have not been correctly filled.");
            alert.showAndWait();
        }
    }

    private void setBackupData(){
        try {
            FileInputStream ifStream=new FileInputStream(Config.inputFile);
            DataInputStream stream=new DataInputStream(ifStream);

            String statusMessage="";
            Config.constantCPS=stream.readBoolean();
            Config.cpsPerFrame=stream.readInt();
            Config.autoSaveInterval=stream.readInt();
            Config.grav=stream.readFloat();
            Config.smoothingConstant=stream.readFloat();
            Config.simSpeed=stream.readFloat();

            bodyCount=stream.readInt();
            elapsedFrames=stream.readLong();

            ifStream.close();
            stream.close();

            statusMessage+=("Constant CPS: "+Config.constantCPS+
                    "\nCPS per frame: "+Config.cpsPerFrame+
                    "\nSave interval (seconds): "+Config.autoSaveInterval+
                    "\nGrav. constant: "+Config.grav+
                    "\nSmoothing constant: "+Config.smoothingConstant+"" +
                    "\nSim. Speed: "+Config.simSpeed)+
                    "\n"+
                    "\nCopying old simulation data (This may take a long time) ...";

            backupStatusField.setText(backupStatusField.getText()+statusMessage);

            BackupFileReader backupFileReader = new BackupFileReader(Config.outputFile, Config.inputFile, bodyCount, elapsedFrames);

            Config.outputFile=backupFileReader.copyFileContent();
            Config.txtReader=backupFileReader;

            statusMessage+=("  [DONE]");

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Old simulation file content successfully copied. Click continue to begin generation.");
                    alert.showAndWait();
                    mainClass.ChangeToInitScreen();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
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
            return false;
        }else {
            backupSimPath.setStyle("");
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
            e.printStackTrace();
            return false;
        }



        return true;
    }

    public void setMain (Main main){
        mainClass = main;
    }
}
