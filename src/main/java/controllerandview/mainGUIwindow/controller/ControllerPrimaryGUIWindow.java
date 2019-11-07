package main.java.controllerandview.mainGUIwindow.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import main.java.controllerandview.MainGUIClass;

public class ControllerPrimaryGUIWindow {

    private MainGUIClass mainGUIClass;



//    @FXML
//    private Button createPosWindowButton;

    public void setMainGUIClass(MainGUIClass controller){
        mainGUIClass = controller;
    }

    @FXML
    public void createPosWindowsButtonClicked(){
        System.out.println("Primary GUI window clicked!");
        mainGUIClass.createPosWindow();
    }

    @FXML
    public void createPriceContrWinClicked(ActionEvent actionEvent) {
        mainGUIClass.createPriceControlWindow();
    }

    @FXML
    public void connectOutsideData(){
        mainGUIClass.getMainModelThread().connectOutsideData();
    }

}
