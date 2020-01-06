package main.java.controllerandview.algocontrollers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.MainModelThread;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

/**
 *
 */
public class ControllerSimpleScalperAlgo extends AlgoController {

    public ToggleGroup orderTypeToggleGroup;
    public RadioButton buyLimit;
    public RadioButton buyStop;
    public RadioButton sellLimit;
    public RadioButton sellStop;
    public RadioButton cancel;

    public TextField orderAmount;
    public TextField targetInterval;
    private MikeOrder.MikeOrderType orderType = MikeOrder.MikeOrderType.BUYLMT;

    private String descriptionRow1 = "SCLP1";
    private String descriptionRow2 = "B LMT";




    @FXML
    public void initialize() {
//        System.out.println("ControllerSimpleScalperAlgo created.");


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
    public String getSimpleDescriptionRow1() {
        return descriptionRow1;
    }

    @Override
    public String getSimpleDescriptionRow2() {
        return descriptionRow2;
    }


//    @Override
//    public boolean launch(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
//
//        model.algoManager.createScalperAlgo1(posOrders, entryPrice, getInterval(), 100, orderType);
//
//        return true;
//    }


    @Override
    public void mikeGridPaneButtonPressed(int pricePressed, MainModelThread model, MikePosOrders posOrders) {
//        System.out.println("SimpleScalperAlgo: mikeGridPaneButtonPressed. Price: " + pricePressed
//        + " target price: " + (pricePressed + getInterval()) + " Amount: " + (getAmount())  );
        if (orderType != MikeOrder.MikeOrderType.CANCEL) {
            model.algoManager.createScalperAlgo1(posOrders, pricePressed, pricePressed + getInterval(), getAmount(), orderType);
        } else {
            System.out.println("Cancelling all SimpleScalperAlgos at price: " + pricePressed + " NOT IMPLEMENTED!");
            //todo: cancel all algos at pricePressed
        }
    }

    private int getAmount() {
        Integer amount = Integer.parseInt(orderAmount.getText());
        return amount;
    }

    private int getInterval() {
        Integer interval = Integer.parseInt(targetInterval.getText());
        return interval;
    }


    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }

}
