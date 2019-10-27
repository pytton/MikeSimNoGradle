package main.java.controllerandview.pricecontrolwindow.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.priceserver.PriceServer;

public class ControllerPriceControlPanel {

    public PriceServer priceServer;
    private PriceServer.PriceType priceType = PriceServer.PriceType.MANUAL;
    private ObservableList<String> instrumentNamesList;


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
    public ListView instrumentsList;
    @FXML
    public ToggleGroup priceSourceToggleGroup;
    public RadioButton historicalRadioButton;
    public RadioButton manualRadioButton;
    public RadioButton liveRadioButton;
    public TextField experimentalTextField1;
    @FXML
    private Integer minSliderValue = 27000;
    @FXML
    private Integer maxSliderValue = 27300;

    @FXML
    public void initialize() {
        priceSlider.setMin((double) minSliderValue);
        priceSlider.setMax((double) maxSliderValue);
        priceSlider.setValue((double) (maxSliderValue - (maxSliderValue - minSliderValue) / 2));
        maxPriceTextField.setText(maxSliderValue.toString());
        minPriceTextField.setText(minSliderValue.toString());

        instrumentNamesList = FXCollections.<String>observableArrayList("SPY", "DIA", "IWM", "EUR");
        instrumentsList.getItems().addAll(instrumentNamesList);

//        System.out.println("ControllerPriceControlPanel created");
//        System.out.println(priceSlider.getMax());
//        System.out.println(priceSlider.getValue());
//        System.out.println(""+ priceSlider.getValue());

        //assign a listener to the radiobuttons. pressing the radiobutton selects the price source for the priceserver:
        priceSourceToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                                                                        @Override
                                                                        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                                                                            if (priceSourceToggleGroup.getSelectedToggle() == manualRadioButton) {
                                                                                experimentalTextField1.setText("Manual");
                                                                                priceServer.setPriceType(PriceServer.PriceType.MANUAL);
                                                                            }
                                                                            if (priceSourceToggleGroup.getSelectedToggle() == liveRadioButton) {
                                                                                experimentalTextField1.setText("Live");
                                                                                priceServer.setPriceType(PriceServer.PriceType.LIVEMARKET);
                                                                            }
                                                                            if (priceSourceToggleGroup.getSelectedToggle() == historicalRadioButton) {
                                                                                experimentalTextField1.setText("Live");
                                                                                priceServer.setPriceType(PriceServer.PriceType.HISTORICAL);
                                                                            }
                                                                        }
                                                                    }
        );


    }

    @FXML
    /**
     * triggered when user moves the slider
     */
    private void onMouseDragged() {
        int bidAskSpread = 1;
        //change the bid & ask priceserver in priceServer:
        try {
            priceServer.setBidPrice((int) priceSlider.getValue());
            priceServer.setAskPrice((int) (priceSlider.getValue() + bidAskSpread));

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
                maxPriceTextField.setText("" + maxvalue);
                maxSliderValue = maxvalue;
            } else {
                maxPriceTextField.setText(maxSliderValue.toString());
            }
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
                minPriceTextField.setText("" + minvalue);
                minSliderValue = minvalue;
            } else {
                minPriceTextField.setText(minSliderValue.toString());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void connectRealTimeData(ActionEvent actionEvent) {
        //TODO:
        //connect to real-time data
    }

    public PriceServer getPriceServer() {
        return priceServer;
    }

    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }
}