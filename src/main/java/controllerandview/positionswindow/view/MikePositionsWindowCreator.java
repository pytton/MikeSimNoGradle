package main.java.controllerandview.positionswindow.view;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import main.java.controllerandview.MikeGridPane;
import main.java.controllerandview.positionswindow.controller.ControllerConsolidatedPositionsWindow;
import main.java.model.priceserver.PriceServer;

import java.io.IOException;

public class MikePositionsWindowCreator {

    private FXMLLoader posWindowLoader;// = new FXMLLoader(getClass().getResource("PositionsWindow.fxml"));
    private Parent positionsWindowRoot;
    public ControllerConsolidatedPositionsWindow positionsWindowController;
    private MikeGridPane buttonTable;


    public MikePositionsWindowCreator() throws IOException {
        //load FXML file

        // ../../../../resources

        posWindowLoader = new FXMLLoader(getClass().getResource("/PositionsWindow.fxml"));
        //this needed by JavaFX Scene constructor:
        positionsWindowRoot = posWindowLoader.load(); //this might throw IOException
        //this is used to access elements of MikePositionsWindowCreator:
        positionsWindowController = (ControllerConsolidatedPositionsWindow)posWindowLoader.getController();
        //this adds a custom table of buttons to the scene
        buttonTable = new MikeGridPane(100,7, positionsWindowController);




        //todo: check this works:
        //experimenting with modifing this window:

        VBox topVbox = new VBox();
        MikeGridPane topGridPane = new MikeGridPane(1,7, new MikeGridPane.EmptyMikeButtonHandler());
        MikeGridPane bottomGridPane = new MikeGridPane(1, 7, new MikeGridPane.EmptyMikeButtonHandler());


        topGridPane.setPadding( new Insets(0, 15, 0, 0));
        bottomGridPane.setPadding( new Insets(0, 15, 0, 0));
        topVbox.getChildren().add(topGridPane);

        ScrollPane sp = new ScrollPane();
        sp.setContent(buttonTable);
        sp.setFitToWidth(true);
//        sp.setMaxWidth(450);
        topVbox.getChildren().add(sp);
        topVbox.getChildren().add(bottomGridPane);

        //todo: this changed. works?
        positionsWindowController.getMainBorderPane().setLeft(topVbox);
//        positionsWindowController.getMainBorderPane().setMinWidth(850);

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

    public ControllerConsolidatedPositionsWindow getPositionsWindowController() {
        return positionsWindowController;
    }
}
