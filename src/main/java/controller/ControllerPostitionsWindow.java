package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import main.java.model.PriceServer;
import main.java.positions.view.MikeGridPane;

public class ControllerPostitionsWindow {

    public TextField askPriceTextField;
    public TextField bidPriceTextField;
    public BorderPane mainBorderPane;
    public TextField experimentalTextField;
    private MikeGridPane mikeGridPane = null;
    private PriceServer priceServer;

    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

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
//        experimentalTextField.setText(((Double)(Math.random())).toString());
        String exper = priceServer.getExperimentalNumber().toString();
        experimentalTextField.setText(exper);
        askPriceTextField.setText(priceServer.getAskPrice().toString());


    }


    public void setAskPriceTextField(Integer price){
        askPriceTextField.setText(price.toString());
    }
}
