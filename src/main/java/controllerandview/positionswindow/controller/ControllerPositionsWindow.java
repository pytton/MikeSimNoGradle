package main.java.controllerandview.positionswindow.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import main.java.model.priceserver.PriceServer;
import main.java.controllerandview.positionswindow.view.MikeGridPane;

import java.util.List;

public class ControllerPositionsWindow {

    public TextField askPriceTextField;
    public TextField bidPriceTextField;
    public BorderPane mainBorderPane;
    public TextField experimentalTextField;
    private MikeGridPane mikeGridPane = null;
    private PriceServer priceServer;

    private ObservableList<List<Integer>> pricelist;



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


        //display realtime bid ask priceserver:
        askPriceTextField.setText(((Double)priceServer.getRealTimeAskPrice()).toString());
        bidPriceTextField.setText(((Double)priceServer.getRealTimeBidPrice()).toString());
    }

    public void updateGUI(){



        String exper = priceServer.getExperimentalNumber().toString();
        experimentalTextField.setText(exper);


        //display realtime bid ask priceserver:
        askPriceTextField.setText(((Double)priceServer.getRealTimeAskPrice()).toString());
        bidPriceTextField.setText(((Double)priceServer.getRealTimeBidPrice()).toString());

        System.out.println("Window updated");
    }


    public void setAskPriceTextField(Integer price){
        askPriceTextField.setText(price.toString());
    }

    public void setExperimentalTextField(int num) {
        experimentalTextField.setText("" + num);
    }

    public void setSpecificButtonInMikeGridPane(int row, int col, String text) {
        mikeGridPane.getButton(row, col).setText(text);
    }
}
