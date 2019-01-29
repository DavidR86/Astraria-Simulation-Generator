package sample.controllers;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 3/16/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sample.Config;
import sample.Main;
import sample.algorithms.MultiThreadAlgorithm;
import sample.fileManagement.BackupToolWriter;
import sample.fileManagement.BinWriter;

import java.io.File;

public class GenerationController {

    public ProgressBar progressBar;
    public Button cancelButton;
    public ImageView loadingImage;
    public Text stat1;
    public Text stat2;
    public Text stat3;
    public Text stat4;
    public Text stat5;
    public Text timeRemainingLabel;
    public Button backupButton;
    public Button PauseButton;
    public TitledPane showGraphsTitledPane;
    public HBox graphHBox;
    public VBox mainBox;
    public AreaChart areaChart;
    public LineChart lineChart;

    private Main main;

    private BinWriter writer;
    private Thread writerThread;
    private MultiThreadAlgorithm multiThreadAlgorithm;
    private boolean dec = true;

    @FXML
    public void initialize(){
        showGraphsTitledPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            mainBox.getScene().getWindow().sizeToScene();
            //if (dec){
            //    mainBox.setPrefHeight(412);
            //}else {
            //    mainBox.setPrefHeight(512);
            //}
            //dec=!dec;
        });
        //showGraphsTitledPane.expandedProperty().addListener((obs, oldHeight, newHeight) -> {
        //    mainBox.getScene().getWindow().sizeToScene();
            //mainBox.setPrefHeight(612);

        //});

        lineChart.setTitle("Time per frame");

    }


    public void setMain(Main main){
    this.main=main;
    }

    public void goBack(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to interrupt the generation process? Any current progress may be lost.");
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> exit());

    }

    public void setThreadAlgorithm(MultiThreadAlgorithm multiThreadAlgorithm){
        this.multiThreadAlgorithm=multiThreadAlgorithm;
    }


    public void exit(){

        writer.terminate();

        synchronized (writer.getLock()){
            writerThread.interrupt();
        }
        multiThreadAlgorithm.endThreads();
        Platform.exit();
    }

    public void setWriter(BinWriter writer){
        this.writer=writer;
    }

    public void setWriterThread(Thread writerThread){
        this.writerThread=writerThread;
    }

    public void doBackup(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Doing a backup may take some time on larger simulations. Do you want to proceed?");
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    //backup
                //    backup();
                    backupProcedure();
                });
    }

    public void backupProcedure(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                multiThreadAlgorithm.pauseAndResumeAlgorithm();
                synchronized (multiThreadAlgorithm.getLock()){
                    try {
                        BackupToolWriter backupWriter = new BackupToolWriter(new File(Config.outputFile.getParent()+"/"+Config.outputFile.getName().substring(0,Config.outputFile.getName().length()-4)+".bck"), multiThreadAlgorithm);
                        System.out.println("Backup file path: "+Config.outputFile.getParent()+"/"+Config.outputFile.getName().substring(0,Config.outputFile.getName().length()-4)+".bck");

                        backupWriter.writeData();
                    }catch (Exception e){
                        e.printStackTrace();

                        exit();
                    }

                }
                multiThreadAlgorithm.pauseAndResumeAlgorithm();
            }
        });
        thread.setDaemon(true);
        thread.setName("bakcupProcedureThread");
        thread.start();
    }

    public void pause(ActionEvent actionEvent) {
        multiThreadAlgorithm.pauseAndResumeAlgorithm();
    }

    public void expand(MouseEvent mouseEvent) {
        //mainBox.setPrefHeight(mainBox.getHeight()+50);
        //showGraphsTitledPane.setPrefHeight(50);
    }
}