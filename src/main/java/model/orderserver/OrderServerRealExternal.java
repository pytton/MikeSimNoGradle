package main.java.model.orderserver;

import main.java.model.MikeSimLogger;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

/**
 * This is used for placing orders and checking fills using an external trading
 * software using its API.
 */
public class OrderServerRealExternal extends OrderServer {
    /**
     * NOT IMPLEMENTED
     * @param priceServer priceServer used to check current bid/ask price
     */
    @Override
    synchronized public void checkFills(PriceServer priceServer) {
        MikeSimLogger.addLogEvent("Checking fills on external trading platform has NOT BEEN IMPLEMENTED YET!");
    }

    /**
     * NOT IMPLEMENTED
     * @param posOrders
     * @param orderType
     * @param assignedToPos
     * @param price
     * @param amount
     * @return
     */
    @Override
    public long placeNewOrder(MikePosOrders posOrders, MikeOrder.MikeOrderType orderType, int assignedToPos, int price, int amount) {
        MikeSimLogger.addLogEvent("Placing new orders on external trading platform has NOT BEEN IMPLEMENTED YET!");
        return 0;
    }

    /**
     * NOT IMPLEMENTED
     * @param orderID
     */
    @Override
    public void cancelOrder(long orderID) {
        MikeSimLogger.addLogEvent("Cancelling orders on external trading platform has NOT BEEN IMPLEMENTED YET!");
    }
}
