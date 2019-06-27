package main.java.controllerandview.positionswindow.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import main.java.controllerandview.positionswindow.controller.ControllerPositionsWindow;
import main.java.model.priceserver.PriceServer;

import java.io.IOException;

public class MikePositionsWindowCreator {

    private FXMLLoader posWindowLoader;// = new FXMLLoader(getClass().getResource("PositionsWindow.fxml"));
    public Parent positionsWindowRoot;
    public ControllerPositionsWindow positionsWindowController;
    private MikeGridPane buttonTable;


    public MikePositionsWindowCreator() throws IOException {
        //load FXML file

        // ../../../../resources

        posWindowLoader = new FXMLLoader(getClass().getResource("/PositionsWindow.fxml"));
        //this needed by JavaFX Scene constructor:
        positionsWindowRoot = posWindowLoader.load(); //this might throw IOException
        //this is used to access elements of MikePositionsWindowCreator:
        positionsWindowController = (ControllerPositionsWindow)posWindowLoader.getController();
        //this adds a custom table of buttons to the scene
        buttonTable = new MikeGridPane(9,100);

        //experimenting with modifing this window:
        ScrollPane sp = new ScrollPane();
        sp.setContent(buttonTable);
        sp.setFitToWidth(true);
        sp.setMaxWidth(450);

        positionsWindowController.getMainBorderPane().setLeft(sp);
        positionsWindowController.getMainBorderPane().setMinWidth(850);

//        positionsWindowController.getMainBorderPane().setCenter(sp);
        positionsWindowController.setMikeGridPane(buttonTable);

//        Button button = buttonTable.getButton(3,1);
//
//        button.setText("Hello!");
//        button.setStyle("-fx-background-color: red");
//
//        buttonTable.getButton(3,2).setText("");
//        buttonTable.getButton(3,3).setStyle("-fx-text-fill: green");
//        buttonTable.getButton(4,0).setStyle("-fx-background-color: blue");
//        buttonTable.getButton(4,1).setStyle("-fx-background-color: grey;-fx-border-color: black; -fx-border-width: 1px");
//        buttonTable.getButton(5,1).setStyle("-fx-background-color: blue;-fx-border-color: black; -fx-border-width: 1px");
//        buttonTable.getButton(5,1).setStyle("-fx-border-color: black; -fx-border-width: 1px;");
    }

    public MikePositionsWindowCreator(PriceServer priceServer) throws IOException{
        this();
        positionsWindowController.setPriceServer(priceServer);
    }

    public Parent getPositionsWindowRoot() {
        return positionsWindowRoot;
    }

    public ControllerPositionsWindow getPositionsWindowController() {
        return positionsWindowController;
    }
}
