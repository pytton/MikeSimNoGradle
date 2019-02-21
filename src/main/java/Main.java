package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../resources/SceneBuilder/PriceControlPanel.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.setX(1200);
        primaryStage.setY(50);

        Parent root2 = FXMLLoader.load(getClass().getResource("../resources/SceneBuilder/PositionsWindow.fxml"));

        Stage secondStage = new Stage();
        secondStage.setX(0);
        secondStage.setY(50);
        secondStage.setScene(new Scene(root2));

        secondStage.show();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
