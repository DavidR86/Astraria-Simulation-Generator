package sample.controllers;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 3/15/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sample.Config;
import sample.Main;

import java.io.File;

public class NewSimController {
    public Button cancelButton;
    public Button startButton;


    public TextField inputPath;
    public TextField outputPath;
    public CheckBox cyclesCheck;
    public TextField durationField;
    public ComboBox durationComboBox;
    public TextField cpsField;
    public TextField gravField;
    public TextField smoothingField;
    public TextField speedField;
    public ComboBox afterComboBox;
    private Main main;

    FileChooser fileChooser;

    @FXML
    public void initialize() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Input file");

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "seconds",
                        "minutes",
                        "hours"
                );
        durationComboBox.setItems(options);

        ObservableList<String> options2 =
                FXCollections.observableArrayList(
                        "show dialog",
                        "launch simulation",
                        "show statistics"
                );
        afterComboBox.setItems(options2);
    }

    public void setInputFilePath(ActionEvent actionEvent) {
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.txt (Initial conditions file)", "*.txt"));
        File file = fileChooser.showOpenDialog(main.getPrimaryStage());
        if (file != null) {
            inputPath.setText(file.getAbsolutePath());
        }
    }

    public void setOutputFilePath(ActionEvent actionEvent) {
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.nbd (Nbody data file)", "*.nbd"));
        File file = fileChooser.showSaveDialog(main.getPrimaryStage());
        if (file != null) {
            outputPath.setText(file.getAbsolutePath());
        }
    }

    public void cyclesField(ActionEvent actionEvent) {
        if (cyclesCheck.isSelected()){
            cpsField.setDisable(false);
        }else {
            cpsField.clear();
            cpsField.setDisable(true);
        }
    }

    public void goBack(ActionEvent actionEvent) {
        main.ChangeToTitleScreen();
    }

    public void setMain(Main main){
        this.main = main;
    }

    public void startSimulation(ActionEvent actionEvent) {
        if (validate()){
            main.ChangeToInitScreen();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "One or more required fields have not been correctly filled.");
            alert.showAndWait();
        }

    }

    private boolean validate(){
        Config.inputFile=new File(inputPath.getText());
        Config.outputFile=new File(outputPath.getText());

        if (!Config.inputFile.exists()){
            inputPath.setStyle("-fx-border-color: red;");
        }else {
            inputPath.setStyle("");
        }

        if (Config.outputFile!=null&&!Config.outputFile.getPath().equals("")){
            //System.out.println(Config.outputFile);
            if (!Config.outputFile.getParentFile().exists()){
                outputPath.setStyle("-fx-border-color: red;");
            }else {
                outputPath.setStyle("");
            }
        }else {
            outputPath.setStyle("-fx-border-color: red;");
        }


        Config.constantCPS=cyclesCheck.isSelected();

        double mult;
        //System.out.println(durationComboBox.getValue());
        if (durationComboBox.getValue()==null){
            durationComboBox.setValue("seconds");
        }
        if ( durationComboBox.getValue().equals("seconds")){
            mult=1;
            //System.out.println("mult: 1");
        }else if (durationComboBox.getValue().toString().equals("minutes")){
            mult=60;
            //System.out.println("mult: 60");
        }else{
            mult=3600;
            //System.out.println("mult: 3600");
        }

        try {
            durationField.setStyle("");
            Config.simDuration=mult*Double.parseDouble(durationField.getText());
            Config.durationSet=true;
        }catch (Exception e){
            //System.out.println("Wrong input");
            durationField.setStyle("-fx-border-color: red;");
            Config.durationSet=false;
        }

        if (cyclesCheck.isSelected()){
            try {
                Config.cpsPerFrame=Integer.parseInt(cpsField.getText());
                Config.cpsSet=true;
                cpsField.setStyle("");
            }catch (Exception e){
                Config.cpsSet=false;
                cpsField.setStyle("-fx-border-color: red;");
            }
        }else {
            Config.cpsSet=true;
            cpsField.setStyle("");
        }

        try {
            if (gravField.getText().isEmpty()){
                Config.grav=1;
            }else {
                Config.grav=Float.parseFloat(gravField.getText());
            }
        }catch (Exception e){
            Config.grav=1;
            gravField.clear();
        }

        try {
            if (smoothingField.getText().isEmpty()){
                Config.smoothingConstant=0;
            }else {
                Config.smoothingConstant=Float.parseFloat(smoothingField.getText());
            }
        }catch (Exception e){
            Config.smoothingConstant=0;
            smoothingField.clear();
        }

        try {
            if (speedField.getText().isEmpty()){
                Config.simSpeed=1;
            }else {
                Config.simSpeed=Float.parseFloat(speedField.getText());
            }
        }catch (Exception e){
            Config.simSpeed=1;
            speedField.clear();
        }

        if (Config.allSet()){
            System.out.println("All set!");
            return true;
        }else {
            System.out.println("Missing info");
            return false;
        }


    }
}
