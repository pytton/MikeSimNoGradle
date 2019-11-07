package main.java.model.orderserver;

import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

import java.util.*;

/**
 * One OrderServer for one instrument type traded
 */
public class OrderServer {

    private String name = "default";

    private Map<Long, MikeOrder> allOrdersMap = new HashMap<>();
    private SortedSet<Long> activeOrdersList = new TreeSet<>();
    private SortedSet<Long> filledOrdersList = new TreeSet<>();
    private SortedSet<Long> cancelledOrdersList = new TreeSet<>();
    private long mikeSimOrderNumber = 0;

    /**
     * Call this to check for fills of orders being simulated by monitoring bid/ask price
     */
    synchronized public void checkSimulatedFills(PriceServer priceServer){

        int bidPrice = priceServer.getBidPrice();
        int askPrice = priceServer.getAskPrice();

        List<Long> filledOrderIDs = new ArrayList<>();

        //This fills the whole order based on bid/ask prices
        //check all the active orders
        for(long orderId : getActiveOrdersList()){
            MikeOrder order = getAllOrdersMap().get(orderId);

            if(order.getOrderType()== MikeOrder.MikeOrderType.BUYLMT && askPrice <= order.getPrice() && !order.isCancelled()){
                order.setFilled(true);
                order.setFilledPrice(askPrice);
                order.setFilledAmount(order.getAmount());
                filledOrderIDs.add(orderId);

                System.out.println("Order Filled! Fill Price: " + order.getFilledPrice());
            }
            if(order.getOrderType()== MikeOrder.MikeOrderType.BUYSTP && askPrice >= order.getPrice() && !order.isCancelled()){
                order.setFilled(true);
                order.setFilledPrice(askPrice);
                order.setFilledAmount(order.getAmount());
                filledOrderIDs.add(orderId);

                System.out.println("Order Filled! Fill Price: " + order.getFilledPrice());
            }
            if(order.getOrderType()== MikeOrder.MikeOrderType.SELLLMT && bidPrice >= order.getPrice() && !order.isCancelled()){
                order.setFilled(true);
                order.setFilledPrice(bidPrice);
                order.setFilledAmount(order.getAmount());
                filledOrderIDs.add(orderId);

                System.out.println("Order Filled! Fill Price: " + order.getFilledPrice());
            }
            if(order.getOrderType()== MikeOrder.MikeOrderType.SELLSTP && bidPrice <= order.getPrice() && !order.isCancelled()){
                order.setFilled(true);
                order.setFilledPrice(bidPrice);
                order.setFilledAmount(order.getAmount());
                filledOrderIDs.add(orderId);

                System.out.println("Order Filled! Fill Price: " + order.getFilledPrice());
            }
        }

        //notify mikePosOrders about orders that have been filled and
        //remove all the filled orderIDs from the activeOrdersList and add them to the filledOrdersList:
        for(long orderId : filledOrderIDs){
//            (allOrdersMap.get(orderId)).getPosOrders()
            getActiveOrdersList().remove(orderId);
            getFilledOrdersList().add(orderId);
        }

        //TODO: think of something to notify PosOrders class what placed the order that it has been filled.
    }

    public void printActiveOrdersToConsole(){
        for(long orderID : getActiveOrdersList()){
            //print out active orders to console:
            System.out.println("Active order ID: " + getAllOrdersMap().get(orderID).getMikeOrderNumber() + " Order price: "
                    + getAllOrdersMap().get(orderID).getPrice());
        }
    }

    /**
     * Use this to place a new order.
     * @return
     */
    synchronized public long placeNewOrder(MikePosOrders posOrders/*which MikePosOrders is this order assigned to?*/,
                                           MikeOrder.MikeOrderType orderType/*Buy limit? Sell stop etc*/,
                                           int assignedToPos/*what price within MikePosOrders is this assigned to?*/,
                                           int price/*price of the order*/,
                                           int amount/*size of the order*/){
        //create the order:
        MikeOrder order = new MikeOrder(orderType, assignedToPos, price, amount);
        //tell the order which MikePosOrders it is assigned to - to notify it once the order gets filled
        order.setPosOrders(posOrders);
        //set a unigue order number:
        order.setMikeOrderNumber(mikeSimOrderNumber);
        //add the order to the map of all orders:
        getAllOrdersMap().put(mikeSimOrderNumber, order);
        //this is a new order so add it to activeOrders:
        getActiveOrdersList().add(mikeSimOrderNumber);

        return mikeSimOrderNumber++;
    }

    synchronized public void cancelOrder(long orderID){
        try {
            MikeOrder order = getAllOrdersMap().get(orderID);
            if (order != null) {
                order.setCancelled(true);
                getCancelledOrdersList().add(orderID);
            }
        } catch (Exception e) {
            System.out.println("Exception in cancelOrder!");
            e.printStackTrace();
        }

        try {
            getActiveOrdersList().remove(orderID);
        } catch (Exception e) {
            System.out.println("Exception in cancelOrder!");
            e.printStackTrace();
        }
    }

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
}
