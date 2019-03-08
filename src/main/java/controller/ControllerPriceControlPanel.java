package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import main.java.model.PriceServer;

import java.awt.*;

public class ControllerPriceControlPanel {

    public TextField bidPriceTextField;
    public TextField askPriceTextField;
    private PriceServer priceServer;
    private Integer minSliderValue = 27300;
    private Integer maxSliderValue = 27000;

    public PriceServer getPriceServer() {
        return priceServer;
    }

    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

    @FXML
    public TextField maxPriceTextField;
    @FXML
    public TextField minPriceTextField;

    @FXML
    public Slider priceSlider;

//    @FXML
//    private void dragDetected() {
//        System.out.println("dragDetected");
//    }
//
//    @FXML
//    private void dragDone() {
//        System.out.println("dragDone");
//    }
//
//    @FXML
//    private void dragExited() {
//        System.out.println("Slider dragExited");
//    }
//
//    @FXML
//    private void dragOver() {
//        System.out.println("Slider dragOver");
//    }
//
//    @FXML
//    private void dragDropped() {
//        System.out.println("Slider dragDropped");
//    }
//
//    @FXML
//    private void onScroll() {
//        System.out.println("Slider onScroll");
//    }
//
//    @FXML
//    private void onScrollFinished() {
//        System.out.println("Slider onScrollFinished");
//    }
//
//    @FXML
//    private void onScrollStarted() {
//        System.out.println("Slider onScrollStarted");
//    }

    @FXML
    private void onMouseDragged() {


        try {
            minSliderValue = Integer.parseInt(minPriceTextField.getText());
            maxSliderValue = Integer.parseInt(maxPriceTextField.getText());
            if(minSliderValue < maxSliderValue)priceSlider.setMin(minSliderValue);
            if(maxSliderValue > minSliderValue)priceSlider.setMax(maxSliderValue);

            priceServer.setBidPrice((int) priceSlider.getValue());
            priceServer.setAskPrice((int)priceSlider.getValue()+1);
            bidPriceTextField.setText(""+priceServer.getBidPrice());
            askPriceTextField.setText(""+priceServer.getAskPrice());

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        System.out.println((int)priceSlider.getValue());


       // System.out.println("Slider onMouseDragged");
    }




}
