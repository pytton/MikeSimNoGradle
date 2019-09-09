package main.java.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controllerandview.MainGUIController;
import main.java.controllerandview.positionswindow.controller.ControllerPositionsWindow;
import main.java.controllerandview.pricecontrolwindow.controller.ControllerPriceControlPanel;
import main.java.model.livemarketdata.InteractiveBrokersAPI;
import main.java.model.livemarketdata.OutsideTradingSoftwareAPIConnection;
import main.java.model.priceserver.PriceServer;
import main.java.controllerandview.positionswindow.view.MikePositionsWindowCreator;

public class Main extends Application {
    //working on this:
    //This class handles the GUI part:
    private MainGUIController mainGUIController;


    //GUI controllers:
    private ControllerPriceControlPanel priceControlPanel;
    private ControllerPositionsWindow posWindowController;

    //priceServer handles priceserver for single instrument:
    private PriceServer priceServer;

    //OutsideTradingSoftwareAPIConnection interfaces with outside trading software API for market data and orders:
    OutsideTradingSoftwareAPIConnection data = null;

    @Override
    public void start(Stage primaryStage) throws Exception{

        //provides prices for the program:
        priceServer = new PriceServer();

        //new code here:
        mainGUIController = new MainGUIController();

        mainGUIController.initializeGUI(primaryStage, priceServer);
        posWindowController = mainGUIController.getPosWindowController();



        //set up initial display:
//        initializeGUI(primaryStage);

        //TODO: experimenting here:

        //set up connection to outside trading software for market data, orders, etc:
        data = new InteractiveBrokersAPI();
//        data.connect();
        Thread.sleep(1000);
        //small test to see if connected to realtime data
//        System.out.println("EUR Bid price: " + data.getBidPrice());

        //let priceserver know about real time market data:
        priceServer.setRealTimeDataSource(data);


        //start the main loop:
        MainLoop mainLoop = new MainLoop(priceServer, mainGUIController);
        mainLoop.setPriceServer(priceServer);
        mainLoop.start();
    }

    public void initializeGUI(Stage primaryStage) throws Exception{
        //create Price Control window:
        FXMLLoader priceControlPanelLoader = new FXMLLoader(getClass().getResource("/PriceControlPanel.fxml"));
        Parent pricePanelRoot =  priceControlPanelLoader.load(); //FXMLLoader.load(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));

        //get the controller class:
        priceControlPanel = (ControllerPriceControlPanel) priceControlPanelLoader.getController();
        priceControlPanel.setPriceServer(priceServer);

        //create the window:
        primaryStage.setTitle("Price Control");
        primaryStage.setScene(new Scene(pricePanelRoot));
        primaryStage.setX(850);
        primaryStage.setY(0);

        //create Positions Window:
        //we need to add custom MikeGridPane not defined in FXML:
        MikePositionsWindowCreator posWindow = new MikePositionsWindowCreator(priceServer);
        posWindowController = posWindow.getPositionsWindowController();

        //create the window:
        Stage secondStage = new Stage();
        secondStage.setX(0);
        secondStage.setY(0);
//        Scene positions1 = ;
        secondStage.setScene(new Scene(posWindow.getPositionsWindowRoot()));

//        Scene positions1 = new Scene(posWindow.getPositionsWindowRoot());
//        secondStage.setScene(positions1);

        //display the windows:
        secondStage.show();
        primaryStage.show();
    }

    @Override
    public void stop(){
        MainLoop.interrupted = true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
