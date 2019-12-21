package main.java.model;

import javafx.application.Application;
import javafx.stage.Stage;
import main.java.controllerandview.MainGUIClass;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    //All logic handled here:
    public MainModelThread mainModelThread;

    //This class handles the GUI part:
    public MainGUIClass mainGUIClass;

    //TODO: this not currently used. change everything so that it is used by the rest of the program
    //Instruments available for trading defined here:
    private Map<Integer /*tickerID*/, TradedInstrument> tradedInstrumentMap = new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception{

        //set up the GUI:
        mainGUIClass = new MainGUIClass();

        //define instruments available for trading - tradedInstrumetMap:
        setupContracts();
        //create the main model thread:
        mainModelThread = new MainModelThread(mainGUIClass, tradedInstrumentMap);

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

    //this defines which instruments (contracts) will be available for trading
    private void setupContracts() {
        tradedInstrumentMap.put(0, new TradedInstrument(0, "SPY", "SMART", "STK", "USD"));
        tradedInstrumentMap.put(1, new TradedInstrument(1, "DIA", "SMART", "STK", "USD"));
        tradedInstrumentMap.put(2, new TradedInstrument(2, "IWM", "SMART", "STK", "USD"));
        tradedInstrumentMap.put(3, new TradedInstrument(3, "QQQ", "SMART", "STK", "USD"));
        tradedInstrumentMap.put(4, new TradedInstrument(4, "EUR", "IDEALPRO", "CASH", "USD"));
    }


}

