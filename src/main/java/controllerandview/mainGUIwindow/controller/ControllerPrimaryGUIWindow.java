package main.java.controllerandview.mainGUIwindow.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.controllerandview.MainGUIController;
import main.java.controllerandview.positionswindow.controller.ControllerPositionsWindow;
import main.java.controllerandview.pricecontrolwindow.controller.ControllerPriceControlPanel;

public class ControllerPrimaryGUIWindow {

    private MainGUIController mainGUIController;



//    @FXML
//    private Button createPosWindowButton;

    public void setMainGUIController(MainGUIController controller){
        mainGUIController = controller;
    }

    @FXML
    public void createPosWindowsButtonClicked(){
        System.out.println("Primary GUI window clicked!");
        mainGUIController.createPosWindow();

    }

    public void createPriceContrWinClicked(ActionEvent actionEvent) {
        mainGUIController.createPriceControlWindow();
    }
}
