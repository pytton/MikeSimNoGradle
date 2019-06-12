package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controller.ControllerPostitionsWindow;
import main.java.controller.ControllerPriceControlPanel;
import main.java.model.MainLoop;
import main.java.model.PriceServer;
import main.java.positions.view.MikePositionsWindowCreator;

public class Main extends Application {

private PriceServer priceServer = new PriceServer();


    @Override
    public void start(Stage primaryStage) throws Exception{

        //create Price Control window:
        FXMLLoader priceControlPanelLoader = new FXMLLoader(getClass().getResource("../resources/PriceControlPanel.fxml"));
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
