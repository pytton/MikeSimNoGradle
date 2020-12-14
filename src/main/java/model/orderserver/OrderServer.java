package main.java.model.orderserver;

import main.java.model.MikeSimLogger;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

import java.util.*;

/**
 * One OrderServer for one instrument type traded.
 * OrderServer can be implemented as:
 * OrderServerRealExternal - sends oreder to OutsideTradingSoftwareAPIConnection to be executed
 * by the real market through a broker; or:
 * OrderServerSimulatedInternal - this checks prices continuously to simulate fills internally
 *
 * Decision has to be made at start of trading and cannot be changed while program is running
 *
 */
abstract public class OrderServer {

    protected String name = "default";

    protected Map<Long, MikeOrder> allOrdersMap = new HashMap<>();
    protected SortedSet<Long> activeOrdersList = new TreeSet<>();
    protected SortedSet<Long> filledOrdersList = new TreeSet<>();
    protected SortedSet<Long> cancelledOrdersList = new TreeSet<>();
    protected long mikeSimOrderNumber = 0;

    /**
     * Call this to check for fills of orders being simulated by monitoring bid/ask price
     *                 for buy orders, the amount will be positive
     *                 for sell orders, the amount has to be negative!
     *                 a negative filled amount means the position is short
     */
    abstract public void checkFills(PriceServer priceServer);

    /**
     * Use this to place a new order.
     * @return
     */
    abstract public long placeNewOrder(MikePosOrders posOrders/*which MikePosOrders is this order assigned to?*/,
                                           MikeOrder.MikeOrderType orderType/*Buy limit? Sell stop etc*/,
                                           int assignedToPos/*what price within MikePosOrders is this assigned to?*/,
                                           int price/*price of the order*/,
                                           int amount/*size of the order*/);

    /**
     * This only works with simulated orders!
     * If order was sent to outside trading software it will not be cancelled
     * todo: think of a way to cancel the order if it was sent to outside trading software
     * @param orderID
     */
    abstract public void cancelOrder(long orderID);

    /**
     * The name of the traded instrument serviced by this OrderServer
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Long, MikeOrder> getAllOrdersMap() {
        return allOrdersMap;
    }

//    public void setAllOrdersMap(Map<Long, MikeOrder> allOrdersMap) {
//        this.allOrdersMap = allOrdersMap;
//    }

    public SortedSet<Long> getActiveOrdersList() {
        return activeOrdersList;
    }

//    public void setActiveOrdersList(SortedSet<Long> activeOrdersList) {
//        this.activeOrdersList = activeOrdersList;
//    }

    public SortedSet<Long> getFilledOrdersList() {
        return filledOrdersList;
    }

//    public void setFilledOrdersList(SortedSet<Long> filledOrdersList) {
//        this.filledOrdersList = filledOrdersList;
//    }

    public SortedSet<Long> getCancelledOrdersList() {
        return cancelledOrdersList;
    }

//    public void setCancelledOrdersList(SortedSet<Long> cancelledOrdersList) {
//        this.cancelledOrdersList = cancelledOrdersList;
//    }

    public MikeOrder getMikeOrder(long orderId) {
        return allOrdersMap.get(orderId);
    }


    /**
     * For testing
     */
    public void printAllOrdersToConsole(){
        System.out.println("Printing all orders");
        for(MikeOrder order : getAllOrdersMap().values()){
            System.out.println("Order id: " + order.getMikeOrderNumber() +
                    " Order price: " + order.getPrice() +
                    " Order amount: " + order.getAmount());
        }

    }

    /**
     * for testing
     */
    public void printActiveOrdersToConsole(){
        System.out.println("Printing active orders in OrderServer");

        for(long orderID : getActiveOrdersList()){
            //print out active orders to console:
            System.out.println("Active order ID: " + getAllOrdersMap().get(orderID).getMikeOrderNumber() + " Order price: "
                    + getAllOrdersMap().get(orderID).getPrice());
        }




    }

}