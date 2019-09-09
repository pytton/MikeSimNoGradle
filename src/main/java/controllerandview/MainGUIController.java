package main.java.controllerandview;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controllerandview.positionswindow.controller.ControllerPositionsWindow;
import main.java.controllerandview.positionswindow.view.MikePositionsWindowCreator;
import main.java.controllerandview.pricecontrolwindow.controller.ControllerPriceControlPanel;
import main.java.model.MainLoop;
import main.java.model.priceserver.PriceServer;

public class MainGUIController {
    private ControllerPriceControlPanel priceControlPanel;
    private ControllerPositionsWindow posWindowController;


    //called by Mainloop. Updates all GUI windows
    public void updateGUI(){
        if (posWindowController != null) posWindowController.updateGUI();
    }
    public void updateGUI(MainLoop mainLoop){
        updateGUI();
        System.out.println("Gui updated");
    }





    public ControllerPositionsWindow getPosWindowController() {
        return posWindowController;
    }

    public void initializeGUI(Stage primaryStage, PriceServer priceServer) throws Exception{
        //create Price Control window:
        FXMLLoader priceControlPanelLoader = new FXMLLoader(getClass().getResource("/PriceControlPanel.fxml"));
        Parent pricePanelRoot =  priceControlPanelLoader.load(); //FXMLLoader.load(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));

        //get the controller class:
        priceControlPanel = (ControllerPriceControlPanel) priceControlPanelLoader.getController();
        priceControlPanel.setPriceServer(priceServer);

        //create the window:
        primaryStage.setTitle("Price Control");
        primaryStage.setScene(new Scene(pricePanelRoot));
        primaryStage.setX(850);
        primaryStage.setY(0);

        //create Positions Window:
        //we need to add custom MikeGridPane not defined in FXML:
        MikePositionsWindowCreator posWindow = new MikePositionsWindowCreator(priceServer);
        posWindowController = posWindow.getPositionsWindowController();

        //create the window:
        Stage secondStage = new Stage();
        secondStage.setX(0);
        secondStage.setY(0);
//        Scene positions1 = ;
        secondStage.setScene(new Scene(posWindow.getPositionsWindowRoot()));

//        Scene positions1 = new Scene(posWindow.getPositionsWindowRoot());
//        secondStage.setScene(positions1);

        //display the windows:
        secondStage.show();
        primaryStage.show();
    }



}
