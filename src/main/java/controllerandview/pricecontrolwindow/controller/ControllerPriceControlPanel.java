package main.java.controllerandview.pricecontrolwindow.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import main.java.model.priceserver.PriceServer;

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
    public ListView instrumentsList;
    @FXML
    private Integer minSliderValue = 27000;
    @FXML
    private Integer maxSliderValue = 27300;

    private ObservableList<String> instrumentNamesList;

    @FXML
    public void initialize() {
        priceSlider.setMin((double)minSliderValue);
        priceSlider.setMax((double)maxSliderValue);
        priceSlider.setValue((double)(maxSliderValue - (maxSliderValue-minSliderValue)/2));
        maxPriceTextField.setText(maxSliderValue.toString());
        minPriceTextField.setText(minSliderValue.toString());

        instrumentNamesList = FXCollections.<String>observableArrayList("SPY", "DIA", "IWM", "EUR");
        instrumentsList.getItems().addAll(instrumentNamesList);

//        System.out.println("ControllerPriceControlPanel created");
//        System.out.println(priceSlider.getMax());
//        System.out.println(priceSlider.getValue());
//        System.out.println(""+ priceSlider.getValue());
    }

    public PriceServer getPriceServer() {
        return priceServer;
    }

    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

    @FXML
    /**
     * triggered when user moves the slider
     */
    private void onMouseDragged() {
        int bidAskSpread = 1;
        //change the bid & ask priceserver in priceServer:
        try {
            priceServer.setBidPrice((Double) priceSlider.getValue());
            priceServer.setAskPrice((Double) (priceSlider.getValue() + bidAskSpread));

            //TODO: finish this

            //get bid price from Priceserver and update bid price textfield

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

    @FXML
    /**
     * sets the minimum value returned by the slider
     */
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
