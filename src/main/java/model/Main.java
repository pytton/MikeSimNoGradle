package main.java.model;

import javafx.application.Application;
import javafx.stage.Stage;
import main.java.controllerandview.MainGUIController;
import main.java.model.livemarketdata.InteractiveBrokersAPI;
import main.java.model.livemarketdata.OutsideTradingSoftwareAPIConnection;
import main.java.model.priceserver.PriceServer;

public class Main extends Application {
    //working on this:
    //This class handles the GUI part:
    private MainGUIController mainGUIController;

    //priceServer handles priceserver for single instrument:
    private PriceServer priceServer;

    //OutsideTradingSoftwareAPIConnection interfaces with outside trading software API for market data and orders:
    OutsideTradingSoftwareAPIConnection data = null;

    @Override
    public void start(Stage primaryStage) throws Exception{

        //provides prices for the program:
        priceServer = new PriceServer();


        //TODO: experimenting here:
        //set up connection to outside trading software for market data, orders, etc:
        data = new InteractiveBrokersAPI();
//        data.connect();
        Thread.sleep(1000);
        //small test to see if connected to realtime data
        //System.out.println("EUR Bid price: " + data.getBidPrice());

        //let priceserver know about real time market data:
        priceServer.setRealTimeDataSource(data);

        //set up the GUI:
        mainGUIController = new MainGUIController();

        //create the main model thread:
        MainModelThread mainModelThread = new MainModelThread(mainGUIController);

        //initialize the GUI and show the windows:
        mainGUIController.initializeGUI(primaryStage, priceServer);

        //start the main model thread:
        mainModelThread.start();
    }

    @Override
    public void stop(){
        MainModelThread.interrupted = true;
    }

    public static void main(String[] args) {
        //JavaFX application reguires this line. It does JavaFX stuff and then calls start(Stage primaryStage)
        launch(args);
    }
}
