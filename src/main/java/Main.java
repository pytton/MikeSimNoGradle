package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.model.MainLoop;
import main.java.view.MikePositionsWindowCreator;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{

        //create Price Control window:
        FXMLLoader priceControlPanelLoader = new FXMLLoader(getClass().getResource("view/PriceControlPanel.fxml"));
        Parent pricePanelRoot =  priceControlPanelLoader.load(); //FXMLLoader.load(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));
        primaryStage.setTitle("Price Control");
        primaryStage.setScene(new Scene(pricePanelRoot));
        primaryStage.setX(1200);
        primaryStage.setY(50);



//
//        //load the FXML file
//        FXMLLoader posWindowLoader = new FXMLLoader(getClass().getResource("view/PositionsWindow.fxml"));
//
//
//        Parent positionsWindowRoot = posWindowLoader.load();
//        //access the controller of the FXML file:
//        ControllerPostitionsWindow positionsWindowController = (ControllerPostitionsWindow)posWindowLoader.getController();
//        //set something inside the controller:
//        positionsWindowController.setAskPriceTextField(145);
//
//        MikeGridPane buttonTable = new MikeGridPane();
//
//        positionsWindowController.getMainBorderPane().setCenter(buttonTable);
//
//        Button button = buttonTable.getButton(3,1);
//
//        button.setText("Hello!");
//        button.setStyle("-fx-background-color: red");
//
//        buttonTable.getButton(3,2).setText("");


        //create Positions Window:
        MikePositionsWindowCreator posWindow = new MikePositionsWindowCreator();


        Stage secondStage = new Stage();
        secondStage.setX(0);
        secondStage.setY(50);

        Scene positions1 = new Scene(posWindow.getPositionsWindowRoot());

        secondStage.setScene(positions1);
        secondStage.show();
        primaryStage.show();

        MainLoop ct = new MainLoop(posWindow.getPositionsWindowController().getMikeGridPane());
        ct.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
