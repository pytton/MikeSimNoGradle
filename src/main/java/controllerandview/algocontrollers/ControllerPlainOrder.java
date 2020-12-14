package main.java.controllerandview.algocontrollers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import main.java.controllerandview.windowcontrollers.ControllerPositionsWindow;
import main.java.model.MainModelThread;
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

    public TextField orderAmount;
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
        return descriptionRow1;
    }

    @Override
    public String getSimpleDescriptionRow2() {
        return descriptionRow2;
    }

    @Override
    public String getSimpleDescriptionRow3() {
        return orderAmount.getText();
    }

    @Override
    public void mikeGridPaneButtonPressed(int pricePressed, MainModelThread model, MikePosOrders posOrders) {
        if (orderType == MikeOrder.MikeOrderType.TRANSFER && controllerPositionsWindow.targetPositionsList.getSelectionModel().getSelectedItem() != null){
            System.out.println("Attempting transfer");
            posOrders.movePositionToDifferentMikePosOrders(pricePressed, (MikePosOrders) controllerPositionsWindow.targetPositionsList.getSelectionModel().getSelectedItem());
            return;
        }

        //cancel orders at price if CANCEL selected
        if (orderType == MikeOrder.MikeOrderType.CANCEL) {
            posOrders.cancelAllOrdersAtPrice(pricePressed);
        }

        //place new order if CANCEL not selected
        if (orderType != MikeOrder.MikeOrderType.CANCEL) {
            posOrders.placeNewOrder(orderType, pricePressed, pricePressed, getAmount());
            return;
        }
    }

    private int getAmount() {
        Integer amount = Integer.parseInt(orderAmount.getText());
        return amount;
    }

    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }

    public void setControllerPositionsWindow(ControllerPositionsWindow controllerPositionsWindow) {
        this.controllerPositionsWindow = controllerPositionsWindow;
    }
}
