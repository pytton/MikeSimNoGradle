package main.java.controllerandview.algocontrollers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.MainModelThread;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

public class ControllerSimpleStepperAlgo extends AlgoController {

    public ToggleGroup orderTypeToggleGroup;
    public RadioButton buyLimit;
    public RadioButton buyStop;
    public RadioButton sellLimit;
    public RadioButton sellStop;
    public RadioButton cancel;

    public TextField orderAmount;
    public TextField targetInterval;
    public CheckBox smTrailingStopCheckBox;
    public CheckBox fixedTrailingStopCheckBox;
    //    public TextField scalperCount;
    private MikeOrder.MikeOrderType orderType = MikeOrder.MikeOrderType.BUYLMT;

    private String descriptionRow1 = "STEPPER1";
    private String descriptionRow2 = "B LMT";

    public TextField getOrderAmount() {
        return orderAmount;
    }

    @FXML
    public void initialize() {

        //setup the kind of order passed to algoManager depending on which radiobutton is pressed:
        orderTypeToggleGroup.selectedToggleProperty().addListener(
                new ChangeListener<Toggle>() {
                    @Override
                    public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                        if (orderTypeToggleGroup.getSelectedToggle() == buyLimit) {orderType = MikeOrder.MikeOrderType.BUYLMT; descriptionRow2 = "B LMT"; }
                        if (orderTypeToggleGroup.getSelectedToggle() == buyStop) {orderType = MikeOrder.MikeOrderType.BUYSTP; descriptionRow2 = "B STP"; }
                        if (orderTypeToggleGroup.getSelectedToggle() == sellLimit) {orderType = MikeOrder.MikeOrderType.SELLLMT; descriptionRow2 = "S LMT";}
                        if (orderTypeToggleGroup.getSelectedToggle() == sellStop) {orderType = MikeOrder.MikeOrderType.SELLSTP; descriptionRow2 = "S STP";}
                        if (orderTypeToggleGroup.getSelectedToggle() == cancel) {orderType = MikeOrder.MikeOrderType.CANCEL; descriptionRow2 = "CANCEL";}
                    } } );

    }

    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }

    @Override
    public void mikeGridPaneButtonPressed(int pricePressed, MainModelThread model, MikePosOrders posOrders) {
        if (orderType != MikeOrder.MikeOrderType.CANCEL) {
            model.algoManager.createSimpleStepperAlgo(posOrders, pricePressed, getInterval(), getAmount(), orderType, smTrailingStopCheckBox.isSelected(), fixedTrailingStopCheckBox.isSelected());
        } else {
            model.algoManager.cancelAllSimpleStepperAlgosAtPrice(pricePressed, posOrders);
        }
        System.out.println("ControllerSimpleStepperAlgo. Price clicked: " + pricePressed);
    }

    @Override
    public String getSimpleDescriptionRow1() {
        return descriptionRow1;
    }

    @Override
    public String getSimpleDescriptionRow2() {
        return descriptionRow2;
    }

    private int getAmount() {
        Integer amount = Integer.parseInt(orderAmount.getText());
        return amount;
    }

    private int getInterval() {
        Integer interval = Integer.parseInt(targetInterval.getText());
        return interval;
    }

//    private int getScCount() {
//        Integer scCount = Integer.parseInt(scalperCount.getText());
//        return scCount;
//    }

    public void oAmtBtnPressed(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        orderAmount.setText(button.getText());
    }

    public void intervalBtnPressed(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        targetInterval.setText(button.getText());
    }
}
