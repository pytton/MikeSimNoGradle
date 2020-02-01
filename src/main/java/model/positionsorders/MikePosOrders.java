package main.java.model.positionsorders;

import com.ib.client.Order;
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
    protected int totalOpenAmount = 0;
    protected int openPL = 0;
    protected int closedPL = 0;
    protected int totalPL = 0;
    protected Double averagePrice = null;
    protected Double zeroProfitPoint = null;

    private Map<Integer, MikePosition> positionsMap = new HashMap<>();
    private SortedSet<Long> activeOrdersSet = new TreeSet<>();
    private Set<Long> filledOrdersToBeProcessed = new HashSet<>();
    public OrdersAtPrice ordersAtPrice;

    private MikePosOrders parent = null;

    public void setParent(MikePosOrders posOrders) {
        parent = posOrders;
    }

    private Set<MikePosOrders> childPosOrders = new HashSet<>();
    private OrderServer orderServer;
    public PriceServer priceServer; //we need this to calculate Profit/Loss (PL)

    private boolean recalculateChildren = true;

    public void setRecalculateChildren(boolean setting) {
        recalculateChildren = setting;
    }


    public MikePosOrders() {

    }

    public MikePosOrders(OrderServer orderServer, PriceServer priceServer) {
        this.orderServer = orderServer;
        this.priceServer = priceServer;
        ordersAtPrice = new OrdersAtPrice();
    }

    /**
     * For use by algos
     *
     * @return
     */
    public MikePosOrders createChildPosOrders() {
        MikePosOrders child = new MikePosOrders(orderServer, priceServer);
        child.setName("" + name + " child" + (childPosOrders.size() + 1));
        //this is needed for recalcualteOrdersAtPrice methond:
        child.setParent(this);
        childPosOrders.add(child);
        return child;
    }

    /**
     * Returns the total amount of all open BUY orders in this MikePosOrders.
     * If there are no open orders at that price, returns zero
     *
     * @param price the price point of open orders
     * @return
     */
    public int getOpenBuyOrdersAtPrice(int price) {
        if (ordersAtPrice.buyOrdersAtPrice.containsKey(price)) return ordersAtPrice.buyOrdersAtPrice.get(price);
        else return 0;
    }

    /**
     * Returns a Set of prices for which there are any open BUY orders
     *
     * @return
     */
    public Set<Integer> getOpenBUYOrdersPriceSet() {
        return ordersAtPrice.buyOrdersAtPrice.keySet();
    }

    /**
     * Returns the total amount of all open SELL orders in this MikePosOrders.
     * If there are no open orders at that price, returns zero
     *
     * @param price the price point of open orders
     * @return
     */
    public int getOpenSellOrdersAtPrice(int price) {
        if (ordersAtPrice.sellOrdersAtPrice.containsKey(price)) return ordersAtPrice.sellOrdersAtPrice.get(price);
        else return 0;
    }

    /**
     * Returns a Set of prices for which there are any open SELL orders
     *
     * @return
     */
    public Set<Integer> getOpenSELLOrdersPriceSet() {
        return ordersAtPrice.sellOrdersAtPrice.keySet();
    }

    /**
     * Returns the size of the open position at a given price. Positive value means position is long.
     * Negative value means position is short. Returns 0 if there is no MikePosition at given price.
     *
     * @param price the price of the individual MikePosition
     * @return
     */
    public int getOpenAmountAtPrice(int price) {
        if (positionsMap.get(price) != null) {
            return positionsMap.get(price).getOpen_amount();
        } else return 0;
        //Todo: add handling of child PosOrders
    }


    /**
     * ask this class for the amount of open buy and sell orders at a given price
     */
    private class OrdersAtPrice {

        private Map<Integer, Integer> buyOrdersAtPrice = new TreeMap<>();
        private Map<Integer, Integer> sellOrdersAtPrice = new TreeMap<>();

        /**
         * for a given price, returns the total amount of open buy (limit and stop) orders for that price
         *
         * @param price
         * @return
         */
        public int getOpenBUYOrdersAtPrice(int price) {

            if (buyOrdersAtPrice.containsKey(price)) return buyOrdersAtPrice.get(price);
            else return 0;
        }

        /**
         * for a given price, returns the total amount of open sell (limit and stop) orders for that price
         *
         * @param price
         * @return
         */
        public int getOpenSELLOrdersAtPrice(int price) {
            if (sellOrdersAtPrice.containsKey(price)) return sellOrdersAtPrice.get(price);
            else return 0;
        }


        synchronized private OrdersAtPrice recalculate(OrdersAtPrice ordersAtPrice) {
            return ordersAtPrice;
        }




        synchronized private OrdersAtPrice recalc(OrdersAtPrice ordersAtPrice) {

            buyOrdersAtPrice.clear();
            sellOrdersAtPrice.clear();

            int currentOpenOrdersAmount = 0;
            MikeOrder order;
            for (long orderId : activeOrdersSet) {
                order = orderServer.getAllOrdersMap().get(orderId);

                if (order.getOrderType() == MikeOrder.MikeOrderType.BUYLMT
                        || order.getOrderType() == MikeOrder.MikeOrderType.BUYSTP) {
                    //if there already is an entry in the map, add to it
                    if (buyOrdersAtPrice.containsKey(order.getPrice())) {
                        currentOpenOrdersAmount = buyOrdersAtPrice.get(order.getPrice());
                        currentOpenOrdersAmount += order.getAmount();
                        buyOrdersAtPrice.put(order.getPrice(), currentOpenOrdersAmount);
                    } else {
                        //otherwise create a new entry
                        buyOrdersAtPrice.put(order.getPrice(), order.getAmount());
                    }
                }

                if (order.getOrderType() == MikeOrder.MikeOrderType.SELLLMT
                        || order.getOrderType() == MikeOrder.MikeOrderType.SELLSTP) {
                    //if there already is an entry in the map, add to it
                    if (sellOrdersAtPrice.containsKey(order.getPrice())) {
                        currentOpenOrdersAmount = sellOrdersAtPrice.get(order.getPrice());
                        currentOpenOrdersAmount += order.getAmount();
                        sellOrdersAtPrice.put(order.getPrice(), currentOpenOrdersAmount);
                    } else {
                        //otherwise create a new entry
                        sellOrdersAtPrice.put(order.getPrice(), order.getAmount());
                    }
                }
            }

            //add all open orders in child MikePosOrders to this MikePosOrders
            if (true) {
                for (MikePosOrders child : childPosOrders) {
                    //recalculate the childs OrdersAtPrice:
                    child.ordersAtPrice.recalc(this);
                    //add all the open BUY orders in the children to this MikePosOrders:
                    for (Integer price : child.getOpenBUYOrdersPriceSet()) {
                        //if there already is an entry in the map, add to it
                        if (buyOrdersAtPrice.containsKey(price)) {
                            currentOpenOrdersAmount = buyOrdersAtPrice.get(price);
                            currentOpenOrdersAmount += child.getOpenBuyOrdersAtPrice(price);
                            buyOrdersAtPrice.put(price, currentOpenOrdersAmount);
                        } else {
                            //otherwise create a new entry
                            buyOrdersAtPrice.put(price, child.getOpenBuyOrdersAtPrice(price));
                        }
                    }

                    //add all the open SELL orders in the children to this MikePosOrders:
                    for (Integer price : child.getOpenSELLOrdersPriceSet()) {
                        //if there already is an entry in the map, add to it
                        if (sellOrdersAtPrice.containsKey(price)) {
                            currentOpenOrdersAmount = sellOrdersAtPrice.get(price);
                            currentOpenOrdersAmount += child.getOpenSellOrdersAtPrice(price);
                            sellOrdersAtPrice.put(price, currentOpenOrdersAmount);
                        } else {
                            //otherwise create a new entry
                            sellOrdersAtPrice.put(price, child.getOpenSellOrdersAtPrice(price));
                        }
                    }
                }
            }

            return new OrdersAtPrice();
        }


        /**
         * When an order is placed, filled, modified or cancelled, this needs to be called
         * Goes through all the open orders and updates buyOrdersAtPrice and sellOrdersAtPrice
         */
        private void recalculateOpenOrdersAtPrice() {

            if(parent == null) {
                recalc(this);
                return;
            }

            parent.ordersAtPrice.recalculateOpenOrdersAtPrice();

        }







    }


    public synchronized void recalcutlatePL() {

        //calculate values for this MikePosOrders:
        openPL = 0;
        closedPL = 0;
        totalPL = 0;
        totalOpenAmount = 0;
        averagePrice = 0.0;
        double averagePriceCalculator = 0;
        for (MikePosition position : positionsMap.values()) {
            position.calculatePL(priceServer.getBidPrice(), priceServer.getAskPrice());
            openPL += position.getOpen_pl();
            closedPL += position.getClosed_pl();
            totalPL += position.getTotal_pl();
            totalOpenAmount += position.getOpen_amount();
            averagePriceCalculator += (position.getOpen_amount() * position.getPrice());
        }

        //caclulate values for all children and add to this MikePosOrders values:
        for (MikePosOrders child : childPosOrders) {
            child.recalcutlatePL();
            openPL += child.getOpenPL();
            closedPL += child.getClosedPL();
            totalPL += child.getTotalPL();
            totalOpenAmount += child.getTotalOpenAmount();
            if (child.getAveragePrice() != null) {
                averagePriceCalculator += (child.getTotalOpenAmount() * child.getAveragePrice());
            }
        }


        if (totalOpenAmount == 0) {
            openPL = 0;
            closedPL = totalPL;
        }

        if (totalOpenAmount != 0) {
            averagePrice = averagePriceCalculator / totalOpenAmount;
        } else averagePrice = null;
        if (totalOpenAmount != 0) zeroProfitPoint = (averagePrice) - (closedPL / totalOpenAmount);
        else if (averagePrice != null) zeroProfitPoint = averagePrice;
        else zeroProfitPoint = null;
    }

    private MikePosition getMikePositionAtPrice(int price) {
        return positionsMap.get(price);
    }

    /**
     * Takes a position from this MikePosOrders and moves it to a different one:
     *
     * @param price           price of the MikePosition to be moved
     * @param targetPosOrders the place where the MikePosition should be moved to
     */
    public synchronized void movePositionToDifferentMikePosOrders(int price, MikePosOrders targetPosOrders) {
        //if there is no position at the price, do nothing:
        if (positionsMap.get(price) == null) return;

        //if target doesn't exist, do nothing:
        if (targetPosOrders == null) return;

        //get the position to move:
        MikePosition positionToMove = positionsMap.get(price);

        //add it to the target MikePosOrders:
        targetPosOrders.addToMikePosition(positionToMove);

        //remove it from this MikePosOrders:
        positionsMap.remove(price);

        //recalculate the PL:
        recalcutlatePL();
        targetPosOrders.recalcutlatePL();

        //todo: currently not moving children

    }

    /**
     * Use this for transferring a single position from one MikePosOrders to a different one
     *
     * @param positionToAdd
     */
    private synchronized void addToMikePosition(MikePosition positionToAdd) {
        if (positionToAdd == null) return;

        //Create a new empty position at the price of the position to add:
        MikePosition existingPosition = new MikePosition(positionToAdd.getPrice());

        //check if there is already a MikePosition at that price in this book.
        //if there is, use the data in that position instead of the empty position\
        if (getMikePositionAtPrice(positionToAdd.getPrice()) != null) {
            existingPosition = getMikePositionAtPrice(positionToAdd.getPrice());
        }

        //Combine the two positions in one new one:
        MikePosition newPosition = new MikePosition(
                (positionToAdd.getPrice()),
                (existingPosition.getOpen_amount() + positionToAdd.getOpen_amount()),
                (existingPosition.getOpen_pl() + positionToAdd.getOpen_pl()),
                (existingPosition.getClosed_pl() + positionToAdd.getClosed_pl()),
                (existingPosition.getTotal_pl() + positionToAdd.getTotal_pl()));

        //remove the old position from this book and replace it with the new one:
        positionsMap.put(newPosition.getPrice(), newPosition);

        //recalculate the profit/loss:
        recalcutlatePL();

        //todo: currently not moving children
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Use this to place a new order. Passes the order to the OrderServer
     *
     * @return
     */
    synchronized public long placeNewOrder(MikeOrder.MikeOrderType orderType, int assignedToPos, int price, int amount) {

        System.out.println("Placing new order in " + getName());
        //send the order to orderserver and get the order number:
        long orderNumber = orderServer.placeNewOrder(this, orderType, assignedToPos, price, amount);
        //add the order number to the list of active orders:
        activeOrdersSet.add(orderNumber);
        //UPDATE OPEN ORDERS BY PRICE
        ordersAtPrice.recalculateOpenOrdersAtPrice();
        return orderNumber;
    }

    /**
     * Once order gets filled in OrderServer, OrderServer uses this method to notify MikePosOrders about it, so that
     * MikePosOrders can process the filled order
     */
    synchronized public void notifyAboutFill(long OrderId) {
        filledOrdersToBeProcessed.add(OrderId);
    }

    /**
     * This needs to be called in a loop. This processes all the filled orders and updates the positions with
     * the amounts that have been filled
     */
    synchronized public void processFilledOrders() {

        //process filled orders for all the child MikePosOrders:
        for (MikePosOrders child : childPosOrders) {
            child.processFilledOrders();
        }

        //if there are no orders to process then do nothing:
        if (filledOrdersToBeProcessed.isEmpty()) return;

        MikeOrder order;
        for (long orderId : filledOrdersToBeProcessed) {

            order = orderServer.getAllOrdersMap().get(orderId);

            //first find out if positionsMap already has a position at the price of the order. If not, then create it:
            if (!positionsMap.containsKey(order.getAssignedToPosition())) {
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
        ordersAtPrice.recalculateOpenOrdersAtPrice();
    }

    /**
     * Cancel an order if you know its orderId
     *
     * @param orderId
     */
    public synchronized void cancelOrder(long orderId) {
        orderServer.cancelOrder(orderId);
        activeOrdersSet.remove(orderId);
        //RECALCULATE ACTIVE ORDERS BY PRICE
        ordersAtPrice.recalculateOpenOrdersAtPrice();
    }

    public synchronized void cancelAllOrdersAtPrice(int price) {
        Set<Long> orderIdToCancelSet = new HashSet<>();

        for (Long orderId : activeOrdersSet) {
            if (orderServer.getMikeOrder(orderId).getPrice() == price) {
                orderIdToCancelSet.add(orderId);
            }
        }
        for (Long orderId : orderIdToCancelSet) {
            orderServer.cancelOrder(orderId);
            activeOrdersSet.remove(orderId);
        }
        ordersAtPrice.recalculateOpenOrdersAtPrice();

        System.out.println("Cancelling all orders at price " + price + " in " + getName());
    }

    /**
     * For testing purposes
     */
    public void printPositionsToConsole() {

        System.out.println("Printing positions!");
        for (MikePosition position : positionsMap.values()) {
            System.out.println("Position price: " + position.getPrice() +
                    " Position amount: " + position.getOpen_amount() +
                    " Position total PL: " + position.getTotal_pl());
        }
    }

    /**
     * For testing purposes
     */
    public void printActiveOrdersToConsole() {

        for (long orderId : activeOrdersSet) {
            //print out active orders to console:
            System.out.println("Active order ID: " + getOrderServer().getAllOrdersMap().get(orderId).getMikeOrderNumber() + " Order price: "
                    + getOrderServer().getAllOrdersMap().get(orderId).getPrice());
        }
    }

    public Set<Integer> getPositionPricesSet() {
        return positionsMap.keySet();
    }

    private OrderServer getOrderServer() {
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

    public Double getAveragePrice() {
        return averagePrice;
    }

    public int getTotalOpenAmount() {
        return totalOpenAmount;
    }

    public int getTickerId() {
        return priceServer.getTickerID();
    }

    public Double getZeroProfitPoint() {
        return zeroProfitPoint;
    }

    public boolean checkIfOrderFilled(long orderId) {
        return getOrderServer().getMikeOrder(orderId).isFilled();
    }
}