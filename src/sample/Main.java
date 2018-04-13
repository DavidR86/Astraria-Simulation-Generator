package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controllers.*;

public class Main extends Application {

    private Stage primaryStage;
    private FXMLLoader loader;
    private Scene newSimulation;
    private Scene titleScreen;
    private Scene initScreen;
    private Scene generationScreen;
    private Scene backupScreen;

    private Thread thread;

    private GenerationController controller2;

    private InitController initController;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Thread.currentThread().setName("mainThread");

        loader = new FXMLLoader(getClass().getResource("gui/TitleScreen.fxml"));
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("gui/NewSimulation.fxml"));
        FXMLLoader loader3 = new FXMLLoader(getClass().getResource("gui/InitializeWindow.fxml"));
        FXMLLoader loader4 = new FXMLLoader(getClass().getResource("gui/GenerationWindow.fxml"));
        FXMLLoader loader5 = new FXMLLoader(getClass().getResource("gui/OpenExistingSimulation.fxml"));

        newSimulation = new Scene(loader2.load());
        Parent root = loader.load();
        titleScreen = new Scene(root);

        initScreen = new Scene(loader3.load());
        generationScreen = new Scene(loader4.load());
        backupScreen = new Scene(loader5.load());

        primaryStage.setTitle("Astraria Generator Tool");
        primaryStage.setResizable(false);
        primaryStage.setScene(titleScreen);
        this.primaryStage = primaryStage;



        TitleScreenController controller = loader.getController();
        controller.setMain(this);
        NewSimController controller1 = loader2.getController();
        controller1.setMain(this);

        controller2 = loader4.getController();
        controller2.setMain(this);

        initController = loader3.getController();
        initController.setMain(this);

        BackupScreenController backupScreenController = loader5.getController();
        backupScreenController.setMain(this);


        primaryStage.show();



    }


    public static void main(String[] args) {
        launch(args);
    }

    public void ChangeToNewSimulation(){
        try {
            primaryStage.setScene(newSimulation);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ChangeToTitleScreen(){
        try {
            primaryStage.setScene(titleScreen);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public GenerationController ChangeToGenerationScreen(){
        try {
            primaryStage.setScene(generationScreen);
            return controller2;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void ChangeToInitScreen(){
        try {
            primaryStage.setScene(initScreen);

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    initController.start();
                    Thread.currentThread().interrupt();
                }
            });
            thread.setName("accelInit");
            thread.setDaemon(true);

            thread.start();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void changeToBackupScreen(){
        try {
            primaryStage.setScene(backupScreen);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage(){
        return primaryStage;
    }

    public Thread getThread(){
        return thread;
    }
}
