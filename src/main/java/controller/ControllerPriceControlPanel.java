package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import main.java.model.PriceServer;

public class ControllerPriceControlPanel {

    @FXML
    public TextField maxPriceTextField;
    @FXML
    public TextField minPriceTextField;
    @FXML
    public Slider priceSlider;
    @FXML
    public TextField bidPriceTextField;
    @FXML
    public TextField askPriceTextField;
    public PriceServer priceServer;
    @FXML
    private Integer minSliderValue = 27000;
    @FXML
    private Integer maxSliderValue = 27300;

    @FXML
    public void initialize() {

        System.out.println("ControllerPriceControlPanel created");
        priceSlider.setMin((double)minSliderValue);
        priceSlider.setMax((double)maxSliderValue);

        System.out.println(priceSlider.getMax());
        System.out.println(priceSlider.getValue());

        priceSlider.setValue((double)(maxSliderValue - (maxSliderValue-minSliderValue)/2));
//        priceSlider.setValue(27150.0);
        maxPriceTextField.setText(maxSliderValue.toString());
        minPriceTextField.setText(minSliderValue.toString());

        System.out.println(""+ priceSlider.getValue());

//        priceSlider.notifyAll();
    }

    public PriceServer getPriceServer() {
        return priceServer;
    }

    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }



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
//            System.out.println((int)priceSlider.getValue());
//            System.out.println("send this to PriceServer!");
            priceServer.setBidPrice((int)priceSlider.getValue());
            priceServer.setAskPrice((int)(priceSlider.getValue() + 1)); // TODO: 1 is the bid-ask spread, make this changable by user


        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Exception in onMouseDragged");
        }

    }

    @FXML
    /**
     * sets the maximum value returned by the slider
     */
    private void setMaxPrice() {

        int maxvalue = maxSliderValue.intValue();
        try {
            maxvalue = Integer.parseInt(maxPriceTextField.getText());
            if (maxvalue > minSliderValue.intValue()) {
                priceSlider.setMax((double) maxvalue);
                maxPriceTextField.setText(""+maxvalue);
                maxSliderValue = maxvalue;
            }else {maxPriceTextField.setText(maxSliderValue.toString());}
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void setMinPrice() {

        int minvalue = minSliderValue.intValue();
        try {
            minvalue = Integer.parseInt(minPriceTextField.getText());
//            System.out.println("minvalue: " + minvalue + "minSliderValue: " + minSliderValue.intValue());
            if (minvalue < maxSliderValue.intValue()) {
//                System.out.println("setting slider");
                priceSlider.setMin((double) minvalue);
                minPriceTextField.setText(""+minvalue);
                minSliderValue = minvalue;
            }else{minPriceTextField.setText(minSliderValue.toString());}
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
