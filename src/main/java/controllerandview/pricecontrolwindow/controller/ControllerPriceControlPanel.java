package main.java.controllerandview.pricecontrolwindow.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.controllerandview.MainGUIClass;
import main.java.model.priceserver.PriceServer;

public class ControllerPriceControlPanel implements MainGUIClass.Updatable {

    public TextField histPrcDate;
    public TextField tempoTextField;
    private PriceServer priceServer;
    private PriceServer.PriceType priceType = PriceServer.PriceType.MANUAL;
//    private ObservableList<String> instrumentNamesList;


//    public void setInstrumentsList(ListView instrumentsList) {
//        this.instrumentsList = instrumentsList;
//    }

    @FXML
    public ListView instrumentsList;

    public void addToInstrumentList(ObservableList<PriceServer> instrumentNamesList){
        instrumentsList.getItems().addAll(instrumentNamesList);
    }

    public void setInstrumentList(ObservableList<PriceServer> instrumentNamesList) {
        instrumentsList.setItems(instrumentNamesList);
    }


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

    @FXML
    public ToggleGroup priceSourceToggleGroup;
    public RadioButton historicalRadioButton;
    public RadioButton manualRadioButton;
    public RadioButton liveRadioButton;
    public TextField experimentalTextField1;
    @FXML
    private Integer minSliderValue = 27100;
    @FXML
    private Integer maxSliderValue = 27200;

    @FXML
    public void initialize() {
        priceSlider.setMin((double) minSliderValue);
        priceSlider.setMax((double) maxSliderValue);
        priceSlider.setValue((double) (maxSliderValue - (maxSliderValue - minSliderValue) / 2));
        maxPriceTextField.setText(maxSliderValue.toString());
        minPriceTextField.setText(minSliderValue.toString());

        //this handles changing the instrument PriceControlPanel refers to based on what the user
        //selected in in ListView instrumentlist:
        class MyChangeListener implements ChangeListener{
            ControllerPriceControlPanel controllerPriceControlPanel;
            MyChangeListener(ControllerPriceControlPanel controllerPriceControlPanel){
                this.controllerPriceControlPanel = controllerPriceControlPanel;
            }

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                controllerPriceControlPanel.priceServer = (PriceServer) instrumentsList.getSelectionModel().getSelectedItem();
                System.out.println("Chosen: " + controllerPriceControlPanel.priceServer.toString());
            }
        }
        MyChangeListener listener = new MyChangeListener(this);
        instrumentsList.getSelectionModel().selectedItemProperty().addListener( listener );

        //assign a listener to the radiobuttons. pressing the radiobutton selects the price source for the priceserver:
        priceSourceToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                    if (priceSourceToggleGroup.getSelectedToggle() == manualRadioButton) {
                        experimentalTextField1.setText("Manual");
                        getPriceServer().setPriceType(PriceServer.PriceType.MANUAL);
                    }
                    if (priceSourceToggleGroup.getSelectedToggle() == liveRadioButton) {
                        experimentalTextField1.setText("Live");
                        getPriceServer().setPriceType(PriceServer.PriceType.LIVEMARKET);

                        System.out.println("Live prices selected");
                        System.out.println("Live ask price: " + getPriceServer().getRealTimeAskPrice());
                    }
                    if (priceSourceToggleGroup.getSelectedToggle() == historicalRadioButton) {
                        experimentalTextField1.setText("Historical");
                        getPriceServer().setPriceType(PriceServer.PriceType.HISTORICAL);
                                                                            }
                                                                        }
                                                                    }
        );
    }

    public void updateGUI(){
        askPriceTextField.setText(""+priceServer.getAskPrice());
        bidPriceTextField.setText(""+priceServer.getBidPrice());
        histPrcDate.setText("" + priceServer.getHistoricalPriceDate());
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

    public void testButtonClicked(ActionEvent actionEvent) {

        System.out.println("Test button clicked");

        System.out.println("Ask price: " + getPriceServer().getAskPrice());

        System.out.println("Realtime ask price: " + getPriceServer().getRealTimeAskPrice());


//        instrumentsList.getItems().add("Hello");
    }

    public PriceServer getPriceServer() {
        return priceServer;
    }

    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

    public void tempoNaturalBtnClicked(ActionEvent actionEvent) {
        priceServer.setHistoricalTempo(1.0);
    }


    public void startFromBeginningBtnClicked(ActionEvent actionEvent) {
        priceServer.startHistoricalDataFromBeginning();
    }


    public void setTempoBtnClicked(ActionEvent actionEvent) {

        Double tempo = 1.0;

        try {
            String tempoText = tempoTextField.getText();
            tempo = Double.parseDouble(tempoText);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            tempo = 1.0;
        }

        priceServer.setHistoricalTempo(tempo);
    }
}