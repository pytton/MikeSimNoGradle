package main.java.positions.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import main.java.controller.ControllerPostitionsWindow;
import main.java.positions.view.MikeGridPane;

import java.io.IOException;

public class MikePositionsWindowCreator {

    private FXMLLoader posWindowLoader;// = new FXMLLoader(getClass().getResource("PositionsWindow.fxml"));
    public Parent positionsWindowRoot;
    public ControllerPostitionsWindow positionsWindowController;
    private MikeGridPane buttonTable;


    public MikePositionsWindowCreator() throws IOException {
        //load FXML file
        posWindowLoader = new FXMLLoader(getClass().getResource("../../../resources/PositionsWindow.fxml"));
        //this needed by JavaFX Scene constructor:
        positionsWindowRoot = posWindowLoader.load(); //this might throw IOException
        //this is used to access elements of MikePositionsWindowCreator:
        positionsWindowController = (ControllerPostitionsWindow)posWindowLoader.getController();
        //this adds a custom table of buttons to the scene
        buttonTable = new MikeGridPane();

        //experimenting with modifing this window:
        positionsWindowController.getMainBorderPane().setCenter(buttonTable);
        positionsWindowController.setMikeGridPane(buttonTable);

        Button button = buttonTable.getButton(3,1);

        button.setText("Hello!");
        button.setStyle("-fx-background-color: red");

        buttonTable.getButton(3,2).setText("");
        buttonTable.getButton(3,3).setStyle("-fx-text-fill: green");
        buttonTable.getButton(4,0).setStyle("-fx-background-color: blue");
        buttonTable.getButton(4,1).setStyle("-fx-background-color: grey;-fx-border-color: black; -fx-border-width: 1px");
        buttonTable.getButton(5,1).setStyle("-fx-background-color: blue;-fx-border-color: black; -fx-border-width: 1px");
//        buttonTable.getButton(5,1).setStyle("-fx-border-color: black; -fx-border-width: 1px;");



    }

    public Parent getPositionsWindowRoot() {
        return positionsWindowRoot;
    }

    public ControllerPostitionsWindow getPositionsWindowController() {
        return positionsWindowController;
    }
}
