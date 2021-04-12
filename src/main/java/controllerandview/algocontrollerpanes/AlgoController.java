package main.java.controllerandview.algocontrollerpanes;

import javafx.scene.input.MouseEvent;
import main.java.controllerandview.MikeGridPane;
import main.java.controllerandview.windowcontrollers.ControllerPositionsWindow;
import main.java.model.MainModelThread;
import main.java.model.positionsorders.MikePosOrders;

public abstract class AlgoController {

    /**
     * Starts the Algo. Returns true if launch successful.
     * @param entryPrice
     * @return
     */
//    public abstract boolean launch(int entryPrice, MainModelThread model, MikePosOrders posOrders);

    public abstract boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders);

    public abstract void mikeGridPaneButtonPressed(ControllerPositionsWindow contrPoswindow, int pricePressed, MainModelThread model, MikePosOrders posOrders,
                                                   MikeGridPane.MikeButton button, MouseEvent event);
    public abstract void setControllerPositionsWindow(ControllerPositionsWindow controllerPositionsWindow);

    public abstract String getSimpleDescriptionRow1();
    public abstract String getSimpleDescriptionRow2();
    public abstract String getSimpleDescriptionRow3();
    /* {
        return null;
    }*/

//    public abstract int getAmount();
}
