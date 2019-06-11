package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import main.java.positions.view.MikeGridPane;

public class ControllerPostitionsWindow {

    public TextField askPriceTextField;
    public TextField bidPriceTextField;
    public BorderPane mainBorderPane;
    private MikeGridPane mikeGridPane = null;


    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    public MikeGridPane getMikeGridPane(){
        return mikeGridPane;
    }

    public void setMikeGridPane(MikeGridPane mikeGridPane) {
        this.mikeGridPane = mikeGridPane;
    }

    @FXML
    private void buttonClicked(){
        System.out.println("Clicked");
    }

    public void setAskPriceTextField(Integer price){
        askPriceTextField.setText(price.toString());
    }
}
