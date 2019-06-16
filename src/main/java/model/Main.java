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

private PriceServer priceServer = new PriceServer();


    @Override
    public void start(Stage primaryStage) throws Exception{

        //TODO: experimenting here:

        RealTimeData data = new TWSRealTimeData();
        data.connect();

        Thread.sleep(3000);

        System.out.println("EUR Bid price: " + data.getBidPrice());

        //create Price Control window:
        FXMLLoader priceControlPanelLoader = new FXMLLoader(getClass().getResource("../../resources/PriceControlPanel.fxml"));
        Parent pricePanelRoot =  priceControlPanelLoader.load(); //FXMLLoader.load(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));
        ControllerPriceControlPanel priceControlPanel = (ControllerPriceControlPanel) priceControlPanelLoader.getController();
        priceControlPanel.setPriceServer(priceServer);
        primaryStage.setTitle("Price Control");
        primaryStage.setScene(new Scene(pricePanelRoot));
        primaryStage.setX(1200);
        primaryStage.setY(50);

        //create Positions Window:

        MikePositionsWindowCreator posWindow = new MikePositionsWindowCreator(priceServer);
        ControllerPostitionsWindow posWindowController = posWindow.getPositionsWindowController();

        Stage secondStage = new Stage();
        secondStage.setX(0);
        secondStage.setY(50);

        Scene positions1 = new Scene(posWindow.getPositionsWindowRoot());

        secondStage.setScene(positions1);
        secondStage.show();
        primaryStage.show();

        MainLoop ct = new MainLoop(posWindow.getPositionsWindowController().getMikeGridPane());
        ct.setPriceServer(priceServer);

        ct.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
