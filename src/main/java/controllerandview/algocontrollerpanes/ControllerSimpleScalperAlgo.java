package main.java.controllerandview.algocontrollerpanes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import main.java.controllerandview.CommonGUI;
import main.java.controllerandview.MikeGridPane;
import main.java.controllerandview.windowcontrollers.ControllerPositionsWindow;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.algocontrol.AlgoManager;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

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
    public CheckBox multipleCheckBox;
    public TextField multipleAmount;
    public TextField multipleDistance;
    public CheckBox addPlainOrderCheckbox;
    private MikeOrder.MikeOrderType orderType = MikeOrder.MikeOrderType.BUYLMT;

    private final String descriptionRow1 = "SCLP1";
    private String descriptionRow2 = "B LMT";

    /**
     * Used for placing multiple algos with one click
     */
    class MultipleOrderCommand implements CommonGUI.GUICommandMultipleOrder {

        AlgoManager algoManager;
        MikePosOrders posOrdersToSendTo;
        MikeOrder.MikeOrderType orderType;
        int priceToSend;
        int orderAmount;
        int interval;
        boolean addPlainOrder = false;

        public MultipleOrderCommand(AlgoManager algoManager, MikePosOrders posOrdersToSendTo,
                                    MikeOrder.MikeOrderType orderType, int priceToSend, int orderAmount, int interval, boolean addPlainOrder) {

            this.posOrdersToSendTo = posOrdersToSendTo;
            this.orderType = orderType;
            this.priceToSend = priceToSend;
            this.orderAmount = orderAmount;
            this.algoManager = algoManager;
            this.interval = interval;
            this.addPlainOrder = addPlainOrder;
        }

        @Override
        public void setPriceToSend(int priceToSend) {
            this.priceToSend = priceToSend;
        }

        /**
         * This is the reason for this class.
         * This gets called multiple times in main.java.controllerandview.CommonGUI#placeMultipleOrder
         * to place multiple orders/algos
         * @return
         */
        @Override
        public boolean command() {

//            model.algoManager.createScalperAlgo1(posOrders, pricePressed, pricePressed + getInterval(), getAmount(), orderType);

            algoManager.createScalperAlgo1(posOrdersToSendTo, priceToSend, priceToSend + getInterval(), getAmount(), orderType);

            //handle adding a plain order if selected by user:
            if(addPlainOrder) posOrdersToSendTo.placeNewOrder(orderType, priceToSend, priceToSend, getAmount());

//            algoManager.createSimpleStepperAlgo(posOrdersToSendTo, priceToSend, getInterval(), getAmount(), orderType,
//                    smTrailingStopCheckBox.isSelected(), fixedTrailingStopCheckBox.isSelected());
            return true;
        }
    }



    @FXML
    public void initialize() {
//        MikeSimLogger.addLogEvent("ControllerSimpleScalperAlgo created.");


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

    @Override
    public String getSimpleDescriptionRow3() {
        if(multipleCheckBox.isSelected()) return ("M " + orderAmount.getText());
        return orderAmount.getText();
    }


//    @Override
//    public boolean launch(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
//
//        model.algoManager.createScalperAlgo1(posOrders, entryPrice, getInterval(), 100, orderType);
//
//        return true;
//    }


    @Override
    public void mikeGridPaneButtonPressed(ControllerPositionsWindow controllerPositionsWindow,
                                          int pricePressed, MainModelThread model, MikePosOrders posOrders,
                                          MikeGridPane.MikeButton button,
                                          MouseEvent event) {
//        MikeSimLogger.addLogEvent("SimpleScalperAlgo: mikeGridPaneButtonPressed. Price: " + pricePressed
//        + " target price: " + (pricePressed + getInterval()) + " Amount: " + (getAmount())  );
        if (orderType != MikeOrder.MikeOrderType.CANCEL) {

            //handle multiple alogos created with one click if checkbox clicked:
            if(multipleCheckBox.isSelected()){
                MikeSimLogger.addLogEvent("Attempting multiple");

                ControllerSimpleScalperAlgo.MultipleOrderCommand command = new ControllerSimpleScalperAlgo.MultipleOrderCommand(model.algoManager,
                        posOrders, orderType, pricePressed, getAmount(), getInterval(), addPlainOrderCheckbox.isSelected());

                CommonGUI.placeMultipleOrder(command, multipleAmount, multipleDistance, orderType, pricePressed);

                return;
            }

            model.algoManager.createScalperAlgo1(posOrders, pricePressed, pricePressed + getInterval(), getAmount(), orderType);
            if (addPlainOrderCheckbox.isSelected()) {
                posOrders.placeNewOrder(orderType, pricePressed, pricePressed, getAmount());
            }

        } else {
            model.algoManager.cancelAllSimpleScalperAlgosAtPrice(pricePressed, posOrders);
        }
    }

    @Override
    public void setCntrlParentWindow(ControllerPositionsWindow cntrlParentWindow) {
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED!");
    }

    private int getAmount() {
        return Integer.parseInt(orderAmount.getText());
    }

    private int getInterval() {
        return Integer.parseInt(targetInterval.getText());
    }


    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }

}
