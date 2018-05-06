package sample.controllers;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 3/15/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import sample.Config;
import sample.Main;
import sample.algorithms.MultiThreadAlgorithm;
import sample.fileManagement.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

import static sample.Config.outputFile;

public class InitController {
    public Text msg1;
    public Text msg2;
    public Text msg3;
    public Text msg4;
    public Text msg5;
    public Text done1;
    public Text done3;
    public Text done2;
    public Text done4;
    public Text msg6;
    public Text done5;
    public Button cancelButton;
    public Button startButton;
    public ProgressBar progressBar;
    public ImageView loadingImage;

    private String order;



    private Main main;
    private IniReader txtReader;

    private float[] x;
    private float[] y;
    private float[] z;
    private float[] vx;
    private float[] vy;
    private float[] vz;
    private float[] m;

    private Thread writerThread;
    private MultiThreadAlgorithm multiThreadAlgorithm;

    private BinWriter writer;
    private boolean doneGenerating;

    private AccelerationPatcher patcher;

    public InitController(){
        doneGenerating=false;
    }

    private ScheduledExecutorService exec;

    public void goBack(ActionEvent actionEvent) {

        /*
        if (thread!=null){
            System.out.println(thread.getName());
            thread.interrupt();

        }

        if (main.getThread()!=null){
            System.out.println(main.getThread().getName());
            main.getThread().interrupt();
        }
        */


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?.");
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> exit());
    }

    public void exit(){
        Platform.exit();
    }

    public void startSimulation(ActionEvent actionEvent) {


        GenerationController controller = main.ChangeToGenerationScreen();
        controller.setThreadAlgorithm(multiThreadAlgorithm);
        controller.setWriter(writer);
        controller.setWriterThread(writerThread);
        //Timer generationStatusUpdate = new Timer("generationStatusUpdate", true);
        /*8
        Task<Void> algorithmThread = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                controller.stat3.setText("(1/2) : Simulation generation");
                multiThreadAlgorithm.run();
                patcher = new AccelerationPatcher(writer.getAvgAcceleration(), Config.outputFile);
                    writer.terminate();

                    synchronized (writer.getLock()){
                        writerThread.interrupt();

                    }
                doneGenerating=true;
                controller.timeRemainingLabel.setText("Progress :");
                controller.stat3.setText("(2/2) : Automatic particle colorization");

                try {
                    patcher.start();

                    generationStatusUpdate.cancel();
                    controller.progressBar.setProgress(1);
                    controller.stat5.setText((patcher.getTotalBytes()/4)+" / "+patcher.getTotalBytes()/4+" accelerations");
                    this.done();

                }catch (Exception e){
                    e.printStackTrace();
                   this.cancel();
                }

                return null;
            }
        };
        */
        Thread algorithmThreadParent = new Thread(new Runnable() {
            @Override
            public void run() {
                controller.stat3.setText("(1/3) : Simulation generation");
                multiThreadAlgorithm.run();
                controller.stat3.setText("(2/3) : Writing remaining data");
                writer.terminate();

                synchronized (writer.getLock()){
                    writerThread.interrupt();

                }
                doneGenerating=true;
                patcher = new AccelerationPatcher(writer.getAvgAcceleration(), Config.outputFile);
                controller.timeRemainingLabel.setText("Progress :");
                controller.stat3.setText("(3/3) : Automatic particle colorization");

                try {
                    patcher.start();

                    //generationStatusUpdate.cancel();
                    exec.shutdownNow();
                    controller.progressBar.setProgress(1);
                    controller.stat5.setText((patcher.getTotalBytes()/4)+" / "+patcher.getTotalBytes()/4+" accelerations");
                    Thread.currentThread().interrupt();
                    Platform.exit();

                }catch (Exception e){
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });
        algorithmThreadParent.setDaemon(true);
        algorithmThreadParent.setName("algortihmThread");
        writerThread.start();
        algorithmThreadParent.start();
        /*
        Thread isEnded = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    algorithmThreadParent.join();
                    exit();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        isEnded.setDaemon(true);
        isEnded.setName("");
        isEnded.start();
        */

        /*
        Thread algorithmThread = new Thread(new Runnable() {
            @Override
            public void run() {
                controller.stat3.setText("1: Simulation generation");
                multiThreadAlgorithm.run();
                doneGenerating=true;
                controller.timeRemainingLabel.setText("Progress :");
                controller.stat3.setText("2: Automatic particle colorization");
                patcher = new AccelerationPatcher(writer.getAvgAcceleration(), Config.outputFile);

                try {
                    patcher.start();

                    generationStatusUpdate.cancel();
                    controller.progressBar.setProgress(1);
                    controller.stat5.setText((patcher.getTotalBytes()/4)+" / "+patcher.getTotalBytes()/4+" accelerations");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Simulation finished. Output file path: \n"+Config.outputFile.getPath());
                    alert.showAndWait();
                    exit();

                }catch (Exception e){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Could not locate simulation output file. It is either missing or corrupt.");
                    alert.showAndWait();
                    exit();
                }

            }
        });

        algorithmThread.setOnFailed(e -> {
            //Alert alert = new Alert(Alert.AlertType.ERROR, "Could not locate simulation output file. It is either missing or corrupt.");
            //alert.showAndWait();
            exit();
                });

        algorithmThread.setOnSucceeded(a -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Simulation finished. Output file path: \n"+Config.outputFile.getPath());
            alert.showAndWait();
            exit();
        });
        */

        exec = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        });
        exec.scheduleAtFixedRate(new Runnable() {

            double progressPercent;
            float secondsRemaining;
            float convertedTimeLeft;
            float convertedTime;
            String units2;
            String units;

            Integer secondsElapsed = 0;

            @Override
            public void run() {

                if (multiThreadAlgorithm.isPaused.get()) {
                    controller.stat4.setText("GENERATION PAUSED");
                    controller.stat5.setText("GENERATION PAUSED");
                    try {
                        multiThreadAlgorithm.pauseLatch.await();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                } else if (doneGenerating) {
                    controller.progressBar.setProgress((patcher.getTotalBytes() - patcher.getRemainingBytes()) / patcher.getTotalBytes());
                    controller.stat5.setText((patcher.getTotalBytes() - patcher.getRemainingBytes() / 4) + " / " + patcher.getTotalBytes() / 4 + " accelerations");
                } else {

                    //defining a series
                    XYChart.Series series = new XYChart.Series();
                    series.setName("My portfolio");
                    //populating the series with data

                    series.getData().add(new XYChart.Data(1, 23));
                    series.getData().add(new XYChart.Data(2, 14));
                    series.getData().add(new XYChart.Data(3, 15));
                    series.getData().add(new XYChart.Data(4, 24));
                    series.getData().add(new XYChart.Data(5, 34));
                    series.getData().add(new XYChart.Data(6, 36));
                    series.getData().add(new XYChart.Data(7, 22));
                    series.getData().add(new XYChart.Data(8, 45));
                    series.getData().add(new XYChart.Data(9, 43));
                    series.getData().add(new XYChart.Data(10, 17));
                    series.getData().add(new XYChart.Data(11, 29));
                    series.getData().add(new XYChart.Data(12, 25));
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.lineChart.getData().add(series);
                        }
                    });
                    controller.stat2.setText(multiThreadAlgorithm.timePerCycle() + " seconds");
                    System.out.println(multiThreadAlgorithm.timePerCycle());

                    progressPercent = multiThreadAlgorithm.getL() / (Config.simDuration * 60D);
                    controller.progressBar.setProgress((progressPercent));
                    controller.stat1.setText(multiThreadAlgorithm.avgCPF() + " cycles");
                    units2 = "seconds";
                    convertedTimeLeft = secondsElapsed;
                    if (convertedTimeLeft >= 60) {
                        convertedTimeLeft = convertedTimeLeft / 60;
                        units2 = "minutes";
                        if (convertedTimeLeft >= 60) {
                            convertedTimeLeft = convertedTimeLeft / 60;
                            units2 = "hours";
                            if (convertedTimeLeft >= 24) {
                                convertedTimeLeft = convertedTimeLeft / 24;
                                units2 = "days";
                            }
                        }
                    }


                    controller.stat4.setText(convertedTimeLeft + " " + units2);
                    secondsRemaining = (int) (((1D / progressPercent) * (double) secondsElapsed) - (double) secondsElapsed);
                    units = "seconds";

                    if (secondsRemaining == Integer.MAX_VALUE) {
                        controller.stat5.setText("NaN");
                    } else {
                        convertedTime = secondsRemaining;
                        if (convertedTime >= 60) {
                            convertedTime = convertedTime / 60;
                            units = "minutes";
                            if (convertedTime >= 60) {
                                convertedTime = convertedTime / 60;
                                units = "hours";
                                if (convertedTime >= 24) {
                                    convertedTime = convertedTime / 24;
                                    units = "days";
                                }
                            }
                        }


                        controller.stat5.setText(convertedTime + " " + units);
                    }
                }

                if (secondsElapsed % Config.autoSaveInterval == 0 && (secondsElapsed != 0)) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.backupProcedure();
                        }
                    });
                }

                secondsElapsed++;
            }

        }, 1, 1, TimeUnit.SECONDS);}

        /*
        generationStatusUpdate.schedule(new TimerTask() {

            Integer secondsElapsed = 0;

            @Override
            public void run() {

                if (multiThreadAlgorithm.isPaused.get()){
                    controller.stat4.setText("GENERATION PAUSED");
                    controller.stat5.setText("GENERATION PAUSED");
                    try {
                        multiThreadAlgorithm.pauseLatch.await();
                    }catch (Exception e){

                        e.printStackTrace();
                    }
                }else if (doneGenerating){
                    controller.progressBar.setProgress((patcher.getTotalBytes()-patcher.getRemainingBytes())/patcher.getTotalBytes());
                    controller.stat5.setText((patcher.getTotalBytes()-patcher.getRemainingBytes()/4)+" / "+patcher.getTotalBytes()/4+" accelerations");
                }else {

                    controller.stat2.setText(multiThreadAlgorithm.timePerCycle()+" seconds");

                    double progressPercent = multiThreadAlgorithm.getL()  /  (Config.simDuration*60D);
                    controller.progressBar.setProgress((progressPercent));
                    controller.stat1.setText(multiThreadAlgorithm.avgCPF()+" cycles");
                    String units2 = "seconds";
                    float convertedTimeLeft= secondsElapsed;
                    if (convertedTimeLeft>=60){
                        convertedTimeLeft=convertedTimeLeft/60;
                        units2="minutes";
                        if (convertedTimeLeft>=60){
                            convertedTimeLeft=convertedTimeLeft/60;
                            units2="hours";
                            if (convertedTimeLeft>=24){
                                convertedTimeLeft=convertedTimeLeft/24;
                                units2="days";
                            }
                        }
                    }


                    controller.stat4.setText(convertedTimeLeft+" "+units2);
                    float secondsRemaining = (int) (((1D/ progressPercent)* (double) secondsElapsed)-(double) secondsElapsed);
                    String units = "seconds";

                    if (secondsRemaining==Integer.MAX_VALUE){
                        controller.stat5.setText("NaN");
                    }else {
                        float convertedTime = secondsRemaining;
                        if (convertedTime>=60){
                            convertedTime=convertedTime/60;
                            units="minutes";
                            if (convertedTime>=60){
                                convertedTime=convertedTime/60;
                                units="hours";
                                if (convertedTime>=24){
                                    convertedTime=convertedTime/24;
                                    units="days";
                                }
                            }
                        }




                        controller.stat5.setText(convertedTime + " "+units);
                    }
                }

                if (secondsElapsed%Config.autoSaveInterval==0&&(secondsElapsed!=0)){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.backupProcedure();
                        }
                    });
                }

                secondsElapsed++;
            }
        }, 1000, 1000);
    }
    */

    public void setMain(Main main){
        this.main=main;
    }

    public void start(){
        stage1();
        stage2();
        stage3();
        stage4();
        stage5();
        Thread.currentThread().interrupt();
    }

    public void setOrder(String order){
        this.order=order;
    }


    private void stage1(){

        if (!Config.backup){
            txtReader = new TxtReader(Config.inputFile);

        }else {
            txtReader = Config.txtReader;
        }


        try {
           order=txtReader.read();

           while (order==null||order.isEmpty()){
               final CountDownLatch latch = new CountDownLatch(1);

               Platform.runLater(new Runnable() {
                   @Override
                   public void run() {
                       TextInputDialog dialog = new TextInputDialog();
                       dialog.setHeaderText("The order of variables are not specified in the input file.\n Please specify them below.");
                       dialog.setContentText("Use 'x', 'y', 'z', 'vx', 'vy', 'vz' and 'm' for the values. \n Use 'i' for irrelevant data. Eg. \"i m x y z vx vy vz\"");
                       dialog.showAndWait();
                       setOrder(dialog.getEditor().getText());
                       latch.countDown();
                   }
               });
               latch.await();
               //System.out.println(order);
           }

           done1.setVisible(true);
           progressBar.setProgress(0.1);
        }catch (Exception e){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Could not read the selected text file : "+e.getMessage()+"\nRun in console to get full stack trace.");
                    alert.showAndWait();
                    main.getPrimaryStage().close();
                }
            });

        }

    }

    private void stage2(){
        msg1.setText("      {File contains "+txtReader.getBodyCount(order)+" bodies}");
        msg1.setVisible(true);
        msg2.setVisible(true);

        done2.setVisible(true);
        msg3.setVisible(true);
        progressBar.setProgress(0.2);
    }

    private void stage3(){

        try {
            //readData();
            int bodyCount = txtReader.getBodyCount(order);

            x = new float[bodyCount];
            y = new float[bodyCount];
            z = new float[bodyCount];
            vx = new float[bodyCount];
            vy = new float[bodyCount];
            vz = new float[bodyCount];
            m = new float[1];
            m[0]=0.00002283f;

            txtReader.sort(x,y,z,vx,vy,vz, m);

            /*
            for (int i = 0; i<x.length; i++){
                System.out.println("x: "+x[i]);
                System.out.println("y: "+y[i]);
                System.out.println("z: "+z[i]);
            }
            System.out.println(m[0]);
            */

            done3.setVisible(true);
            msg4.setVisible(true);
            msg5.setVisible(true);
            progressBar.setProgress(0.3);
        }catch (Exception e){
           Platform.runLater(new Runnable() {
               @Override
               public void run() {
                   e.printStackTrace();
                   Alert alert = new Alert(Alert.AlertType.ERROR, "An exception occurred while attempting to decompose the data into individual numbers: "+e.getMessage()+"\nRun in console to get full stack trace.");
                   alert.showAndWait();
                   main.getPrimaryStage().close();
               }
           });
        }


    }

    private void stage4(){
        initializeAcceleration();
        done4.setVisible(true);
        msg6.setVisible(true);
    }

    private void stage5(){
        if (initializeThread()){
            done5.setVisible(true);
            progressBar.setProgress(1);
            loadingImage.setVisible(false);
            startButton.setDisable(false);
        }else {
           Platform.runLater(new Runnable() {
               @Override
               public void run() {
                   Alert alert = new Alert(Alert.AlertType.ERROR, "An exception occurred while attempting to initialize the writer thread."+"\nRun in console to get full stack trace.");
                   alert.showAndWait();
                   main.getPrimaryStage().close();
               }
           });
        }

    }

    private boolean initializeThread(){
        writerThread = new Thread(writer);
        writerThread.setName("writerThread");
        //threadIsOn = true;

        return writer.setFile(outputFile);
    }

    private void initializeAcceleration(){
        writer = new BinWriter((short) 1, x.length, Config.bodyScale, multiThreadAlgorithm);

        int k = x.length;
        Config.bodyCount=k;

        Timer initProgressUpdate = new Timer("initProgressUpdate", true);
        initProgressUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                msg5.setText("      { "+MultiThreadAlgorithm.getProgress()+" / "+k+" bodies initialized }");
                progressBar.setProgress((MultiThreadAlgorithm.getProgress() /  (double) (k*2))+0.3);
            }
        }, 0, 1000);
        /*
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (MultiThreadAlgorithm.getProgress().get()!=k&&!thread.isInterrupted()){
                    msg5.setText("      { "+MultiThreadAlgorithm.getProgress()+" / "+k+" bodies initialized }");
                    progressBar.setProgress((MultiThreadAlgorithm.getProgress().get() /  (double) (k*2))+0.3);
                    try {
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                        msg5.setText("      { "+k+" / "+k+" bodies initialized }");
                        Thread.currentThread().interrupt();

                    }
                }
                //msg5.setText("      { "+k+" / "+k+" bodies initialized }");
                //Thread.currentThread().interrupt();
            }
        });
        thread.setName("initProgressUpdate");
        thread.setDaemon(true);
        thread.start();
        */

        //double t = System.nanoTime();
        multiThreadAlgorithm = new MultiThreadAlgorithm( x,y,z,vx,vy,vz,m[0],Config.simSpeed, writer, Config.simDuration, Config.constantCPS, Config.cpsPerFrame, Config.smoothingConstant);
        //System.out.println(System.nanoTime()-t);
        //thread.interrupt();
        initProgressUpdate.cancel();
        msg5.setText("      { "+k+" / "+k+" bodies initialized }");

    }

    /*
    private void readData() throws Exception{
        ArrayList<Float> data = txtReader.getData();

        int yot = 0;
        int s = 0;


        int k = 0;


        if (txtReader.getV()==1||txtReader.getV()==2){
            if (txtReader.getV()==1){
                yot = 0;
                s = 8;
            }else if (txtReader.getV()==2){
                yot = 1;
                s = 7;
            }

            x = new float[data.size()/s];
            y = new float[data.size()/s];
            z = new float[data.size()/s];
            vx = new float[data.size()/s];
            vy = new float[data.size()/s];
            vz = new float[data.size()/s];

            for (int i = 0; i<data.size(); i=i+s){

                x[k] = data.get(i+2-yot);
                y[k] = data.get(i+3-yot);
                z[k] = data.get(i+4-yot);
                vx[k] = data.get(i+5-yot);
                vy[k] = data.get(i+6-yot);
                vz[k] = data.get(i+7-yot);

                k++;
            }

            ///m = data.get(1-yot);
        }else if (txtReader.getV()==3){
            x = new float[data.size()/11];
            y = new float[data.size()/11];
            z = new float[data.size()/11];
            vx = new float[data.size()/11];
            vy = new float[data.size()/11];
            vz = new float[data.size()/11];

            //m = data.get(15);

            for (int i = 0; i<data.size(); i=i+11){

                x[k] = data.get(i+5-yot);
                y[k] = data.get(i+6-yot);
                z[k] = data.get(i+7-yot);
                vx[k] = data.get(i+8-yot);
                vy[k] = data.get(i+9-yot);
                vz[k] = data.get(i+10-yot);


                k++;
            }
        }else {
            x = new float[data.size()/7];
            y = new float[data.size()/7];
            z = new float[data.size()/7];
            vx = new float[data.size()/7];
            vy = new float[data.size()/7];
            vz = new float[data.size()/7];

            //m=data.get(0);

            for (int i = 0; i<data.size(); i=i+7){

                x[k] = data.get(i+1);
                y[k] = data.get(i+2);
                z[k] = data.get(i+3);
                vx[k] = data.get(i+4);
                vy[k] = data.get(i+5);
                vz[k] = data.get(i+6);


                k++;
            }

        }
        data=null;
    }
    */
}
