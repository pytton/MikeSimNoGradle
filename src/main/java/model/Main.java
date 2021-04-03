package main.java.model;

import javafx.application.Application;
import javafx.stage.Stage;
import main.java.controllerandview.MainGUIClass;
import main.java.model.orderserver.OrderServer;
import main.java.prototypes.DoubleEqualTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Main classes and their responsibilities:
 *
 * MainGUIclass - this runs in its own thread and is responsible for creating and managing
 * all of the GUI windows
 *
 * MainModelThread - runs in a seperate thread than MainGUIClass and is responsible for all
 * of the logic
 *
 * MikePosOrders - a single "book" used for trading. One MikePosOrders
 * holds a Map<Integer, MikePosition> - MikePosition stores the open amount,
 * open, closed Profit/Loss for a single price level in cents. MikePosOrders then sums
 * all the single MikePositions up for a total of open amount and profit/loss for all the MikePositions.
 * All orders have to be placed/cancelled from MikePosOrders.
 *
 * AlgoManager - all algos have to be created and cancelled using this. Keeps a tally of all active algos
 *
 * MainModelThread.PosOrdersManager - this is responsible for creating new MikePosOrders and linking them
 * with the correct OrderServer and PriceServer
 *
 * InteractiveBrokersAPI implements OutsideTradingSoftwareAPIConnection - this is a link to an API
 * of a trading software from a broker. Currently using Interactive Brokers but write everything so
 * that it is easy to replace this class with a different one from a different broker
 */
public class Main extends Application {
    //All logic handled here:
    public MainModelThread mainModelThread;

    //This class handles the GUI part:
    public MainGUIClass mainGUIClass;

    //TODO: this not currently used to set traded contracts in InterActiveBrokers API
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
        MikeSimLogger.addLogEvent("Shutting down.");
//        MikeSimLogger.addLogEvent("Shutting down.");
        mainModelThread.shutDownMikeSim();
//        MainModelThread.interrupted = true;
    }

    public static void main(String[] args) {

        //create the logger:
        MikeSimLogger logger = MikeSimLogger.getInstance();

        //testing:
//        DoubleEqualTest test = new DoubleEqualTest();

//        test.runTest();

        //JavaFX application reguires this line. It does JavaFX stuff and then calls start(Stage primaryStage)
        launch(args);

        MikeSimLogger.addLogEvent("Ending program and writing to MikeSimLog.txt");
//        MikeSimLogger.addLogEvent("Ending program and writing to MikeSimLog.txt");
        MikeSimLogger.printLogToFile();

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

