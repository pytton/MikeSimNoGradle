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
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

/**
 *
 */
public class ControllerPlainOrder extends AlgoController {

    @FXML
    public ToggleGroup orderTypeToggleGroup;
    public RadioButton buyLimit;
    public RadioButton buyStop;
    public RadioButton sellLimit;
    public RadioButton sellStop;
    public RadioButton cancel;
    public RadioButton transfer;

    public RadioButton size1of8;
    public RadioButton size1of4;
    public RadioButton size1of2;
    public RadioButton size1of1;
    public RadioButton defaultSize;
    public RadioButton manualSize;

    public TextField orderAmount;
    public CheckBox multipleCheckBox;
    public TextField multipleAmount;
    public TextField multipleDistance;
    public CheckBox trailingStopCheckbox;
    public RadioButton thisBookRadioBtn;
    public RadioButton targetBookRadioBtn;

    private MikeOrder.MikeOrderType orderType = MikeOrder.MikeOrderType.BUYLMT;
    private ControllerPositionsWindow controllerPositionsWindow;

    private String descriptionRow1 = "ORDER:";
    private String descriptionRow2 = "B LMT";

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
                if (orderTypeToggleGroup.getSelectedToggle() == transfer) {orderType = MikeOrder.MikeOrderType.TRANSFER; descriptionRow2 = "TRANSFER";}
             } } );
    }

    @Override
    public String getSimpleDescriptionRow1() {

//        if(multipleCheckBox.isSelected()) return "MULT ORD";
        return descriptionRow1;
    }

    @Override
    public String getSimpleDescriptionRow2() {
        return descriptionRow2;
    }

    @Override
    public String getSimpleDescriptionRow3() {

        if(multipleCheckBox.isSelected()) return ("M " + getAmount());
        return ("" + getAmount());
    }

    @Override
    public void mikeGridPaneButtonPressed(ControllerPositionsWindow controllerPositionsWindow,
                                          int pricePressed, MainModelThread model, MikePosOrders posOrders,
                                          MikeGridPane.MikeButton button,
                                          MouseEvent event) {

        this.controllerPositionsWindow = controllerPositionsWindow;

        if (orderType == MikeOrder.MikeOrderType.TRANSFER && controllerPositionsWindow.targetPositionsList.getSelectionModel().getSelectedItem() != null){
            MikeSimLogger.addLogEvent("Attempting transfer");
            posOrders.movePositionToDifferentMikePosOrders(pricePressed, (MikePosOrders) controllerPositionsWindow.targetPositionsList.getSelectionModel().getSelectedItem());
            return;
        }

        //cancel orders at price if CANCEL selected
        if (orderType == MikeOrder.MikeOrderType.CANCEL) {
            posOrders.cancelAllOrdersAtPrice(pricePressed);
            return;
        }

        //place new order if CANCEL not selected
        if (orderType != MikeOrder.MikeOrderType.CANCEL) {
            //handle multiple orders if choicebox selected:
            if(multipleCheckBox.isSelected()){
                //send a multipleAmount of orders with a distance of multipleDistance between their prices:
                MultipleOrderCommand command = new MultipleOrderCommand(posOrders, orderType, pricePressed, getAmount());
                CommonGUI.placeMultipleOrder(command, multipleAmount, multipleDistance, orderType, pricePressed);
                return;
            }

            //handle placing trailing stop orders:
            if(trailingStopCheckbox.isSelected()){
                //do we want this behaviour?
                if(multipleCheckBox.isSelected()){
                    MikeSimLogger.addLogEvent("Unable to place multiple trailing stop algos! Uncheck \"Multiple\" checkbox to place trailing" +
                            "stop algos!");
                    return;
                }

                int distanceFromBidOrAsk = 0;
                if(orderType == MikeOrder.MikeOrderType.BUYSTP){
                    //distance from ask is used in trailing stop algo to place buy stop orders. calculate it:
                    distanceFromBidOrAsk = pricePressed - posOrders.getAskPrice();
                    //we don't want buy stop orders placed below the ask price?:
                    if(distanceFromBidOrAsk < 0) distanceFromBidOrAsk = 0;
                    MikeSimLogger.addLogEvent("Placing trailing stop buy order");
                }
                if(orderType == MikeOrder.MikeOrderType.SELLSTP){
                    //distance from bid is used in trailing stop algo to place sell stop orders. calculate it:
                    distanceFromBidOrAsk = posOrders.getBidPrice() - pricePressed;
                    //we don't want sell stop orders placed above the bid price?:
                    if(distanceFromBidOrAsk < 0) distanceFromBidOrAsk = 0;
                    MikeSimLogger.addLogEvent("Placing trailing stop sell order");
                }
                    model.algoManager.createTrailingStopAlgo(orderType, getAmount(), distanceFromBidOrAsk, posOrders);
                    return;
            }

            //otherwise just place an order:
            posOrders.placeNewOrder(orderType, pricePressed, pricePressed, getAmount());
            return;
        }
    }

    //experimenting:
    class MultipleOrderCommand implements CommonGUI.GUICommandMultipleOrder {

        MikePosOrders posOrdersToSendTo;
        MikeOrder.MikeOrderType orderType;
        int priceToSend;
        int orderAmount;

        public MultipleOrderCommand(MikePosOrders posOrdersToSendTo, MikeOrder.MikeOrderType orderType, int priceToSend, int orderAmount) {
            this.posOrdersToSendTo = posOrdersToSendTo;
            this.orderType = orderType;
            this.priceToSend = priceToSend;
            this.orderAmount = orderAmount;
        }

        @Override
        public void setPriceToSend(int priceToSend) {
            this.priceToSend = priceToSend;
        }

        /**
         * This is the reason for this class.
         * This gets called multiple times in main.java.controllerandview.CommonGUI#placeMultipleOrder
         * to place multiple orders
         * @return
         */
        @Override
        public boolean command() {

            posOrdersToSendTo.placeNewOrder(orderType, priceToSend, priceToSend, orderAmount);

            return true;
        }
    }

    /**
     * gives the amount of the order depending on which radiobutton is selected:
     * @return
     */
    private int getAmount() {

        Integer amount = 1; //Integer.parseInt(orderAmount.getText());

        if (manualSize.isSelected()) amount = Integer.parseInt(orderAmount.getText());

        if (controllerPositionsWindow == null) {MikeSimLogger.addLogEvent("null pointer in ControllerPlainOrder.getAmount()");
        return amount; }

        //implement ability to get amount based on open position in posOrders or targetPosOrders that
        //the ControllerPositionsWindow has selected:
        MikePosOrders amountSource = controllerPositionsWindow.getMikePosOrders();
        if(targetBookRadioBtn.isSelected()) amountSource = controllerPositionsWindow.getTargetMikePosOrders();


        if (size1of1.isSelected()) amount = amountSource.getTotalOpenAmount();
        if (size1of2.isSelected()) amount = (int) (0.5 * amountSource.getTotalOpenAmount());
        if (size1of4.isSelected()) amount = (int) (0.25 * amountSource.getTotalOpenAmount());
        if (size1of8.isSelected()) amount = (int) (0.125 * amountSource.getTotalOpenAmount());
        if(defaultSize.isSelected()) MikeSimLogger.addLogEvent("NOT IMPLEMENTED!");


        //if position is short it is negative, therefore we need to make sure order amount is positive:
        if(amount < 0) amount = amount *-1;
        return amount;
    }

    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }

    /**
     * this doesn't get called unless user clicks on a choicebox inside PositionsWindow
     * @param controllerPositionsWindow
     */
    public void setControllerPositionsWindow(ControllerPositionsWindow controllerPositionsWindow) {
        this.controllerPositionsWindow = controllerPositionsWindow;
    }
}
