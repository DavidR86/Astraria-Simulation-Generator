package sample.controllers;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 3/16/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import sample.Main;
import sample.algorithms.MultiThreadAlgorithm;
import sample.fileManagement.BinWriter;

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

    private Main main;

    private BinWriter writer;
    private Thread writerThread;
    private MultiThreadAlgorithm multiThreadAlgorithm;

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

}
