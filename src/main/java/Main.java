package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.model.ClockTicker;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));
        primaryStage.setTitle("Hello World");



        primaryStage.setScene(new Scene(root));
        primaryStage.setX(1200);
        primaryStage.setY(50);

        Parent root2 = FXMLLoader.load(getClass().getResource("view/SceneBuilder/PositionsWindow.fxml"));

        Stage secondStage = new Stage();
        secondStage.setX(0);
        secondStage.setY(50);

        Scene positions1 = new Scene(root2);
        
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
