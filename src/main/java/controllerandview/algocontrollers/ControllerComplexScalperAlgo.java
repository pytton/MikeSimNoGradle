package main.java.controllerandview.algocontrollers;

import javafx.scene.control.TextField;
import main.java.model.MainModelThread;
import main.java.model.positionsorders.MikePosOrders;

public class ControllerComplexScalperAlgo extends AlgoController {


    public TextField orderAmount;

    public TextField getOrderAmount() {
        return orderAmount;
    }

    @Override
    public boolean launch(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }

    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }

    @Override
    public void mikeGridPaneButtonPressed(int pricePressed, MainModelThread model, MikePosOrders posOrders) {

    }

    @Override
    public int getAmount() {
        Integer amount = Integer.parseInt(orderAmount.getText());

        return amount;
    }
}
