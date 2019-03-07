package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controller.ControllerPostitionsWindow;
import main.java.model.ClockTicker;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader priceControlPanelLoader = new FXMLLoader(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));
        Parent pricePanelRoot =  priceControlPanelLoader.load(); //FXMLLoader.load(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));
        primaryStage.setTitle("Hello World");



        primaryStage.setScene(new Scene(pricePanelRoot));
        primaryStage.setX(1200);
        primaryStage.setY(50);

        //load the FXML file
        FXMLLoader posWindowLoader = new FXMLLoader(getClass().getResource("view/SceneBuilder/PositionsWindow.fxml"));
        Parent positionsWindowRoot = posWindowLoader.load();
        //access the controller of the FXML file:
        ControllerPostitionsWindow positionsWindowController = (ControllerPostitionsWindow)posWindowLoader.getController();
        //set something inside the controller:
        positionsWindowController.setAskPriceTextField(145);

        Stage secondStage = new Stage();
        secondStage.setX(0);
        secondStage.setY(50);

        Scene positions1 = new Scene(positionsWindowRoot);



        secondStage.setScene(positions1);
        secondStage.show();
        primaryStage.show();



        ClockTicker ct = new ClockTicker();
        ct.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
