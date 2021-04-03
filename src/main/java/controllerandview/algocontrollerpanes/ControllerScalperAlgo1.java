package main.java.controllerandview.algocontrollerpanes;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;


public class ControllerScalperAlgo1 extends AlgoController {



    @FXML
    TextField orderAmount;

    public boolean launch(int entryPrice, MainModelThread model, MikePosOrders posOrders) {

        model.algoManager.createScalperAlgo1(posOrders, entryPrice, entryPrice + 5, 100, MikeOrder.MikeOrderType.BUYLMT);

        return true;
    }

    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {


        return true;
    }



    @Override
    public void mikeGridPaneButtonPressed(int pricePressed, MainModelThread model, MikePosOrders posOrders) {
        MikeSimLogger.addLogEvent("SimpleScalperAlgo: mikeGridPaneButtonPressed. Price: " + pricePressed);
    }

    @Override
    public String getSimpleDescriptionRow1() {
        return null;
    }

    @Override
    public String getSimpleDescriptionRow2() {
        return null;
    }

    @Override
    public String getSimpleDescriptionRow3() {
        return null;
    }

    public int getAmount() {
        Integer amount = Integer.parseInt(orderAmount.getText());

        return amount;
    }
}
