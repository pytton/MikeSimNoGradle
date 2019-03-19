package main.java.controller;

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
//    public PriceServer priceServer;
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

//        priceSlider.setValue((double)(maxSliderValue - (maxSliderValue-minSliderValue)/2));
        priceSlider.setValue(27150.0);
        maxPriceTextField.setText(maxSliderValue.toString());
        minPriceTextField.setText(minSliderValue.toString());

        System.out.println(""+ priceSlider.getValue());

//        priceSlider.notifyAll();
    }

//    public PriceServer getPriceServer() {
//        return priceServer;
//    }

//    public void setPriceServer(PriceServer priceServer) {
//        this.priceServer = priceServer;
//    }



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

//            int value = Integer.parseInt(minPriceTextField.getText());
//
////            int value = 117;
//
//            System.out.println(value);

//            double value = priceSlider.getValue();
//
//            minSliderValue = Integer.parseInt(minPriceTextField.getText());
//            maxSliderValue = Integer.parseInt(maxPriceTextField.getText());
//            if(minSliderValue < maxSliderValue)priceSlider.setMin((double)minSliderValue);
//            if(maxSliderValue > minSliderValue)priceSlider.setMax((double)maxSliderValue);



//            priceServer.setBidPrice((int) priceSlider.getValue());
//            priceServer.setAskPrice((int)priceSlider.getValue()+1);
//            bidPriceTextField.setText(""+priceServer.getBidPrice());
//            askPriceTextField.setText(""+priceServer.getAskPrice());

        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Exception in onMouseDragged");
        }
        System.out.println((int)priceSlider.getValue());


       // System.out.println("Slider onMouseDragged");
    }

    @FXML
    private void setMaxPrice(InputMethodEvent inputMethodEvent) {
        String text = maxPriceTextField.getText();

        System.out.println("setMaxPrice called");
        Integer maxvalue = Integer.parseInt(text);
        int value = (int)maxvalue.intValue();
        System.out.println((double)value);

        if (maxvalue > minSliderValue) priceSlider.setMax((double)maxvalue);
    }
}
