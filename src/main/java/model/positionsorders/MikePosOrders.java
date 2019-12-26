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
    /**
     * positive totalOpenAmount means the total position is long
     * negative means it is short
     */
    private int totalOpenAmount = 0;
    private int openPL = 0;
    private int closedPL = 0;
    private int totalPL = 0;
    private double averagePrice = 0;

    private Map<Integer, MikePosition> positionsMap = new HashMap<>();
    private SortedSet<Long> activeOrdersSet = new TreeSet<>();
    private Set<Long> filledOrdersToBeProcessed = new HashSet<>();
    public OrdersAtPrice ordersAtPrice;
    private OrderServer orderServer;
    private PriceServer priceServer; //we need this to calculate Profit/Loss (PL)

    public MikePosOrders(OrderServer orderServer, PriceServer priceServer) {
        this.orderServer = orderServer;
        this.priceServer = priceServer;
        ordersAtPrice = new OrdersAtPrice();
    }

    /**
     * ask this class for the amount of open buy and sell orders at a given price
     */
    public class OrdersAtPrice{

        private Map <Integer, Integer> buyOrdersAtPrice = new TreeMap<>();
        private Map <Integer, Integer> sellOrdersAtPrice = new TreeMap<>();

        /**
         * for a given price, returns the total amount of open buy (limit and stop) orders for that price
         * @param price
         * @return
         */
        public int getOpenBuyOrdersAtPrice(int price) {

            if(buyOrdersAtPrice.containsKey(price)) return buyOrdersAtPrice.get(price);
            else return 0;
        }

        public int getOpenSellOrdersAtPrice(int price) {
            if(sellOrdersAtPrice.containsKey(price)) return sellOrdersAtPrice.get(price);
            else return 0;
        }

        /**
         * When an order is placed, filled, modified or cancelled, this needs to be called
         * Goes through all the open orders and updates buyOrdersAtPrice and sellOrdersAtPrice
         */
        synchronized public void recalculate() {
            buyOrdersAtPrice.clear();
            sellOrdersAtPrice.clear();

            MikeOrder order;
            int currentOpenOrdersAmount =0;
            for (long orderId : activeOrdersSet) {
                 order = orderServer.getAllOrdersMap().get(orderId);


                 if (order.getOrderType() == MikeOrder.MikeOrderType.BUYLMT
                 || order.getOrderType() == MikeOrder.MikeOrderType.BUYSTP)
                 {

                     //if there already is an entry in the map, add to it
                     if(buyOrdersAtPrice.containsKey(order.getPrice()))
                     {   currentOpenOrdersAmount = buyOrdersAtPrice.get(order.getPrice());
                         currentOpenOrdersAmount += order.getAmount();
                         buyOrdersAtPrice.put(order.getPrice(), currentOpenOrdersAmount);
                     } else{
                         //otherwise create a new entry
                         buyOrdersAtPrice.put(order.getPrice(), order.getAmount());}
                 }

                if (order.getOrderType() == MikeOrder.MikeOrderType.SELLLMT
                        || order.getOrderType() == MikeOrder.MikeOrderType.SELLSTP)
                {
                    //if there already is an entry in the map, add to it
                    if(sellOrdersAtPrice.containsKey(order.getPrice()))
                    {   currentOpenOrdersAmount = sellOrdersAtPrice.get(order.getPrice());
                        currentOpenOrdersAmount += order.getAmount();
                        sellOrdersAtPrice.put(order.getPrice(), currentOpenOrdersAmount);
                    } else{
                        //otherwise create a new entry
                        sellOrdersAtPrice.put(order.getPrice(), order.getAmount());}
                }
            }
        }
    }

    public synchronized void recalcutlatePL(){
        openPL = 0; closedPL = 0; totalPL = 0; totalOpenAmount = 0;
        averagePrice = 0;
        double averagePriceCalculator = 0;
        for (MikePosition position : positionsMap.values()) {
            position.calculatePL(priceServer.getBidPrice(), priceServer.getAskPrice());
            openPL += position.getOpen_pl();
            closedPL += position.getClosed_pl();
            totalPL += position.getTotal_pl();
            totalOpenAmount += position.getOpen_amount();
            averagePriceCalculator += (position.getOpen_amount() * position.getPrice());
        }
        averagePrice = averagePriceCalculator / totalOpenAmount;
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
        //UPDATE OPEN ORDERS BY PRICE
        ordersAtPrice.recalculate();
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

        //RECALCULATE ORDERS AT PRICE
        ordersAtPrice.recalculate();
    }

    /**
     * Cancel an order if you know its orderId
     * @param orderId
     */
    public void cancelOrder(long orderId) {
        orderServer.cancelOrder(orderId);
        activeOrdersSet.remove(orderId);
        //RECALCULATE ACTIVE ORDERS BY PRICE
        ordersAtPrice.recalculate();
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

    public int getOpenPL() {
        return openPL;
    }

    public int getClosedPL() {
        return closedPL;
    }

    public int getTotalPL() {
        return totalPL;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public int getTotalOpenAmount() {
        return totalOpenAmount;
    }

    public int getTickerId(){return priceServer.getTickerID();}
}