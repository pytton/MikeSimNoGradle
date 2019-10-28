package main.java.model;

import javafx.application.Application;
import javafx.stage.Stage;
import main.java.controllerandview.MainGUIClass;
import main.java.model.livemarketdata.InteractiveBrokersAPI;
import main.java.model.livemarketdata.OutsideTradingSoftwareAPIConnection;
import main.java.model.priceserver.PriceServer;

public class Main extends Application {

    //All logic handled here:
    public MainModelThread mainModelThread;

    //This class handles the GUI part:
    public MainGUIClass mainGUIClass;

    @Override
    public void start(Stage primaryStage) throws Exception{

        //set up the GUI:
        mainGUIClass = new MainGUIClass();

        //create the main model thread:
        mainModelThread = new MainModelThread(mainGUIClass);

        //initialize the GUI and show the primary window:
        mainGUIClass.initializeGUI(primaryStage, mainModelThread);

        //start the main model thread:
        mainModelThread.start();
    }

    @Override
    public void stop(){
        System.out.println("Shutting down.");
        MainModelThread.interrupted = true;
    }

    public static void main(String[] args) {
        //JavaFX application reguires this line. It does JavaFX stuff and then calls start(Stage primaryStage)
        launch(args);
    }
}
