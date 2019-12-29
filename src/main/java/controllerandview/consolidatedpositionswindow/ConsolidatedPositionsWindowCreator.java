package main.java.controllerandview.consolidatedpositionswindow;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import main.java.controllerandview.MikeGridPane;
import main.java.controllerandview.positionswindow.controller.ControllerConsolidatedPositionsWindow;
import main.java.model.priceserver.PriceServer;

import java.io.IOException;

public class ConsolidatedPositionsWindowCreator {

    private FXMLLoader consolidatedPosWindowLoader;// = new FXMLLoader(getClass().getResource("PositionsWindow.fxml"));
    private Parent consolidatedPositionsWindowRoot;
    public ControllerConsolidatedPositionsWindow controllerConsolidatedPositionsWindow;
    private MikeGridPane buttonTable;


    public ConsolidatedPositionsWindowCreator() throws IOException {
        //load FXML file

        // ../../../../resources

        consolidatedPosWindowLoader = new FXMLLoader(getClass().getResource("/ConsolidatedPositionsWindow.fxml"));
        //this needed by JavaFX Scene constructor:
        consolidatedPositionsWindowRoot = consolidatedPosWindowLoader.load(); //this might throw IOException
        //this is used to access elements of MikePositionsWindowCreator:
        controllerConsolidatedPositionsWindow = (ControllerConsolidatedPositionsWindow) consolidatedPosWindowLoader.getController();
        //this adds a custom table of buttons to the scene
        buttonTable = new MikeGridPane(100,7, controllerConsolidatedPositionsWindow);

        //
        VBox topVbox = new VBox();
        MikeGridPane topGridPane = new MikeGridPane(1,7, new MikeGridPane.EmptyMikeButtonHandler());
        MikeGridPane bottomGridPane = new MikeGridPane(1, 7, new MikeGridPane.EmptyMikeButtonHandler());


        topGridPane.setPadding( new Insets(0, 15, 0, 0));
        bottomGridPane.setPadding( new Insets(0, 15, 0, 0));
        topVbox.getChildren().add(topGridPane);

        ScrollPane sp = new ScrollPane();
        sp.setContent(buttonTable);
        sp.setFitToWidth(true);
        topVbox.getChildren().add(sp);
        topVbox.getChildren().add(bottomGridPane);

        controllerConsolidatedPositionsWindow.getMainBorderPane().setLeft(topVbox);

        controllerConsolidatedPositionsWindow.setMikeGridPane(buttonTable);
    }

    public ConsolidatedPositionsWindowCreator(PriceServer priceServer) throws IOException{
        this();
        controllerConsolidatedPositionsWindow.setPriceServer(priceServer);
    }

    public Parent getConsolidatedPositionsWindowRoot() {
        return consolidatedPositionsWindowRoot;
    }

    public ControllerConsolidatedPositionsWindow getControllerConsolidatedPositionsWindow() {
        return controllerConsolidatedPositionsWindow;
    }
}
