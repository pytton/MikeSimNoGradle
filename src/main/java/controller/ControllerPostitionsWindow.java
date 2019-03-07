package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class ControllerPostitionsWindow {

    public TextField askPriceTextField;
    public TextField bidPriceTextField;
    public BorderPane mainBorderPane;

    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }





    @FXML
    private void buttonClicked(){
        System.out.println("Clicked");
    }

    public void setAskPriceTextField(Integer price){
        askPriceTextField.setText(price.toString());
    }
}
