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
    private long count =0;

    //called by Mainloop. Updates all GUI windows
    public void updateGUI(){
        if (posWindowController != null) posWindowController.updateGUI();
//        for (int i = 0; i < 20; i++) {
//            posWindowController.setSpecificButtonInMikeGridPane(i, 0, "" + ((Math.sqrt((count * 79 + count))) * 1) % 567);
//            posWindowController.setSpecificButtonInMikeGridPane(i, 1, "" + count);
//            posWindowController.setSpecificButtonInMikeGridPane(i, 2, "" + Math.sqrt((count * 79 + i + count)) % 7);
//            posWindowController.setSpecificButtonInMikeGridPane(i, 3, "" + Math.sqrt((count * 79 + i + count)) % 56);
//            posWindowController.setSpecificButtonInMikeGridPane(i, 4, "" + Math.sqrt((count * 73 + i + count)) % 74);
//            posWindowController.setSpecificButtonInMikeGridPane(i, 5, "" + Math.sqrt((count * 987 + i + count)) % 34);
//            posWindowController.setSpecificButtonInMikeGridPane(i, 6, "" + Math.sqrt((count * 453 + i + count)) % 9);
//        }
        count++;
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
