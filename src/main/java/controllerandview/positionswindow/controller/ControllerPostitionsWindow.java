package main.java.controllerandview.positionswindow.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import main.java.model.prices.PriceServer;
import main.java.controllerandview.positionswindow.view.MikeGridPane;

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
        //TODO: experimenting here:
        System.out.println("Clicked");
        String exper = priceServer.getExperimentalNumber().toString();
        experimentalTextField.setText(exper);


        //display realtime bid ask prices:
        askPriceTextField.setText(((Double)priceServer.getRealTimeAskPrice()).toString());
        bidPriceTextField.setText(((Double)priceServer.getRealTimeBidPrice()).toString());
    }


    public void setAskPriceTextField(Integer price){
        askPriceTextField.setText(price.toString());
    }
}
