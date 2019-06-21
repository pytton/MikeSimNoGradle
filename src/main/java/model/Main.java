package main.java.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controllerandview.positionswindow.controller.ControllerPostitionsWindow;
import main.java.controllerandview.pricecontrolwindow.controller.ControllerPriceControlPanel;
import main.java.model.livemarketdata.RealTimeData;
import main.java.model.livemarketdata.TWSRealTimeData;
import main.java.model.prices.PriceServer;
import main.java.controllerandview.positionswindow.view.MikePositionsWindowCreator;

public class Main extends Application {
    //GUI controllers:
    private ControllerPriceControlPanel priceControlPanel = null;
    private ControllerPostitionsWindow posWindowController = null;


    //priceServer handles prices for single instrument:
    private PriceServer priceServer = null;

    //RealTimeData interfaces with outside trading software API for market data and orders:
    RealTimeData data = null;

    @Override
    public void start(Stage primaryStage) throws Exception{

        priceServer = new PriceServer();

        initializeGUI(primaryStage);

        //TODO: experimenting here:

        data = new TWSRealTimeData();
        data.connect();
        Thread.sleep(2000);
        //small test to see if connected to realtime data
        System.out.println("EUR Bid price: " + data.getBidPrice());

        priceServer.setRealTimeDataSource(data);

        MainLoop ct = new MainLoop(priceServer);
        ct.setPriceServer(priceServer);
        ct.start();

    }

    public void initializeGUI(Stage primaryStage) throws Exception{
        //create Price Control window:

        //../../resources/

        FXMLLoader priceControlPanelLoader = new FXMLLoader(getClass().getResource("/PriceControlPanel.fxml"));
        Parent pricePanelRoot =  priceControlPanelLoader.load(); //FXMLLoader.load(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));
        priceControlPanel = (ControllerPriceControlPanel) priceControlPanelLoader.getController();
        priceControlPanel.setPriceServer(priceServer);
        primaryStage.setTitle("Price Control");
        primaryStage.setScene(new Scene(pricePanelRoot));
        primaryStage.setX(1200);
        primaryStage.setY(50);

        //create Positions Window:
        MikePositionsWindowCreator posWindow = new MikePositionsWindowCreator(priceServer);
        posWindowController = posWindow.getPositionsWindowController();
        Stage secondStage = new Stage();
        secondStage.setX(0);
        secondStage.setY(50);
        Scene positions1 = new Scene(posWindow.getPositionsWindowRoot());
        secondStage.setScene(positions1);
        secondStage.show();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
