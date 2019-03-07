package main.java.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import main.java.controller.ControllerPostitionsWindow;

import java.io.IOException;

public class MikePositionsWindow {

    private FXMLLoader posWindowLoader;// = new FXMLLoader(getClass().getResource("PositionsWindow.fxml"));
    private Parent positionsWindowRoot;
    private ControllerPostitionsWindow positionsWindowController;
    private MikeGridPane buttonTable;


    public MikePositionsWindow() throws IOException {
        //load FXML file
        posWindowLoader = new FXMLLoader(getClass().getResource("PositionsWindow.fxml"));
        //this needed by JavaFX Scene constructor:
        positionsWindowRoot = posWindowLoader.load(); //this might throw IOException
        //this is used to access elements of MikePositionsWindow:
        positionsWindowController = (ControllerPostitionsWindow)posWindowLoader.getController();
        //this adds a custom table of buttons to the scene
        buttonTable = new MikeGridPane();

        //experimenting with modifing this window:
        positionsWindowController.getMainBorderPane().setCenter(buttonTable);

        Button button = buttonTable.getButton(3,1);

        button.setText("Hello!");
        button.setStyle("-fx-background-color: red");

        buttonTable.getButton(3,2).setText("");
        buttonTable.getButton(3,3).setStyle("-fx-text-fill: green");
        buttonTable.getButton(4,0).setStyle("-fx-background-color: blue");
        buttonTable.getButton(4,1).setStyle("-fx-background-color: light-grey;-fx-border-color: black; -fx-border-width: 1px");
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
