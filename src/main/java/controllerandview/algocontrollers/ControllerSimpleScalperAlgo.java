package main.java.controllerandview.algocontrollers;

import javafx.scene.control.TextField;
import main.java.model.MainModelThread;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

public class ControllerSimpleScalperAlgo extends AlgoController {


    public TextField orderAmount;

    public TextField getOrderAmount() {
        return orderAmount;
    }

    @Override
    public boolean launch(int entryPrice, MainModelThread model, MikePosOrders posOrders) {

        model.algoManager.createScalperAlgo1(posOrders, entryPrice, entryPrice + 5, 100, MikeOrder.MikeOrderType.BUYLMT);

        return true;
    }


    @Override
    public void mikeGridPaneButtonPressed(int pricePressed, MainModelThread model, MikePosOrders posOrders) {
        System.out.println("ScalperAlgo1: mikeGridPaneButtonPressed. Price: " + pricePressed);
    }

    @Override
    public int getAmount() {
        Integer amount = Integer.parseInt(orderAmount.getText());


        return amount;
    }


    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }

}
