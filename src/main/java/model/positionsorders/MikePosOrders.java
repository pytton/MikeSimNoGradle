package main.java.model.positionsorders;

import main.java.model.orderserver.MikeOrder;
import main.java.model.orderserver.OrderServer;
import main.java.model.priceserver.PriceServer;

import java.util.*;

public class MikePosOrders {

    /**
     * The name of this MikePosOrders:
     */
    private String name;

    private OrderServer orderServer;
    private PriceServer priceServer; //we need this to calculate Profit/Loss (PL)

    private Map<Integer, MikePosition> positionsMap = new HashMap<>();
    private SortedSet<Long> activeOrdersSet = new TreeSet<>();
    private Set<Long> filledOrdersToBeProcessed = new HashSet<>();

    public MikePosOrders(OrderServer orderServer, PriceServer priceServer) {
        this.orderServer = orderServer;
        this.priceServer = priceServer;
    }

    public class OrdersAtPrice{

        public int getOpenOrdersAtPrice(int price) {

            //TODO: finish this:
            return 0;
        }
    }

    public MikePosition getMikePositionAtPrice(int price){
        return positionsMap.get(price);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Use this to place a new order. Passes the order to the OrderServer
     * @return
     */
    synchronized public long placeNewOrder(MikeOrder.MikeOrderType orderType, int assignedToPos, int price, int amount){

        System.out.println("Placing new order in " + getName());
        //send the order to orderserver and get the order number:
        long orderNumber = orderServer.placeNewOrder(this, orderType, assignedToPos, price, amount);
        //add the order number to the list of active orders:
        activeOrdersSet.add(orderNumber);
        return orderNumber;
    }

    /**
     * Once order gets filled in OrderServer, OrderServer uses this method to notify MikePosOrders about it, so that
     * MikePosOrders can process the filled order
     */
    synchronized public void notifyAboutFill(long OrderId){
        filledOrdersToBeProcessed.add(OrderId);
    }

    /**
     * This needs to be called in a loop. This processes all the filled orders and updates the positions with
     * the amounts that have been filled
     */
    synchronized public void processFilledOrders(){

//        System.out.println("Processing orders");

        //if there are no orders to process then do nothing:
        if(filledOrdersToBeProcessed.isEmpty()) return;

        MikeOrder order;
        for (long orderId : filledOrdersToBeProcessed) {

            order = orderServer.getAllOrdersMap().get(orderId);

            //first find out if positionsMap already has a position at the price of the order. If not, then create it:
            if(!positionsMap.containsKey(order.getAssignedToPosition())){
                positionsMap.put(order.getAssignedToPosition(), new MikePosition(order.getAssignedToPosition()));
            }
            MikePosition position = positionsMap.get(order.getAssignedToPosition());

            //fill the position:
            position.fill(order.getFilledPrice(), order.getFilledAmount());

            //recalculate the PL for the position:
            position.calculatePL(priceServer.getBidPrice(), priceServer.getAskPrice());

            //order has been filled so is no longer active:
            activeOrdersSet.remove(orderId);
        }

        //TODO: anything else I need to do? Recalculate PL? Recalculate average position? OrdersAtPrice? etc?

        //make sure filled orders are only processed once:
        filledOrdersToBeProcessed.clear();
    }

    /**
     * Cancel an order if you know its orderId
     * @param orderId
     */
    public void cancelOrder(long orderId) {
        orderServer.cancelOrder(orderId);
        activeOrdersSet.remove(orderId);
    }

    /**
     * For testing purposes
     */
    public void printPositionsToConsole(){

        System.out.println("Printing positions!");
        for (MikePosition position : positionsMap.values()) {
            System.out.println("Position price: " + position.getPrice()+
                    " Position amount: " + position.getOpen_amount()+
                    " Position total PL: " + position.getTotal_pl());
        }
    }

    /**
     * For testing purposes
     */
    public void printActiveOrdersToConsole(){

        for (long orderId : activeOrdersSet){
            //print out active orders to console:
            System.out.println("Active order ID: " + getOrderServer().getAllOrdersMap().get(orderId).getMikeOrderNumber() + " Order price: "
                    + getOrderServer().getAllOrdersMap().get(orderId).getPrice());
        }
    }

    public OrderServer getOrderServer() {
        return orderServer;
    }

    public void setOrderServer(OrderServer orderServer) {
        this.orderServer = orderServer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}