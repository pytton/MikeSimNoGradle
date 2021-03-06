package main.java.model.positionsorders;

import com.ib.client.Order;
import javafx.collections.transformation.SortedList;
import main.java.model.MikeSimLogger;
import main.java.model.orderserver.MikeOrder;
import main.java.model.orderserver.OrderServer;
import main.java.model.priceserver.PriceServer;

import java.util.*;

/**
 *
 */
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
    /**
     * This is the closedPL of all of the MikePositions contained in positionsMap
     */
    protected int closedPL = 0;

    /**
     * This is an independent Closed Profit Loss not assigned to any MikePosition but to this whole MikePosOrders
     * separately - it does not get moved when moving individual MikePositions from one MikePosOrders to another.
     * It is included in calculating the totalPL and zeroProfitPoint. You can move it to another MikePosOrders using
     * a dedicated method: moveInternalClosedPL
     */
    protected int internalClosedPL = 0;

    public int getInternalClosedPL(){return internalClosedPL;}
    protected void setInternalClosedPL(int amount){internalClosedPL = amount;}

    /**
     * Reduces this MikePosOrders InternalClosedPL by amount and increases it by the same amount in
     * the targetMikePosOrders
     * @param amount amount of PL to be removed from this MikePosOrders
     * @param targetPosOrders the MikePosOrders to transfer the PL to
     */
    public synchronized void moveInternalClosedPL(int amount, MikePosOrders targetPosOrders){
        if(targetPosOrders == null) return;
        internalClosedPL = internalClosedPL - amount;
        int targetInterClosedPL = targetPosOrders.getInternalClosedPL();
        targetPosOrders.setInternalClosedPL(targetInterClosedPL + amount );
    }

    /**
     * This moves all the closed PL from individual MikePositions in this MikePosOrders to
     * InternalClosedPL
     */
    public synchronized void transferClosedPLFromPositionsToInternal(){
        int closedPLToAdd = 0;
        int bidPrice = priceServer.getBidPrice();
        int askPrice = priceServer.getAskPrice();
        for(MikePosition position : positionsMap.values()){
            closedPLToAdd += position.getClosed_pl();
            position.setClosedPL(0);
//            position.calculatePL(bidPrice, askPrice);
        }
        internalClosedPL = internalClosedPL + closedPLToAdd;
    }

    protected int totalPL = 0;
    protected Double averagePrice = null;
    protected Double zeroProfitPoint = null;

    private Map<Integer, MikePosition> positionsMap = new HashMap<>();
    private SortedSet<Long> activeOrdersSet = new TreeSet<>();
    private Set<Long> filledOrdersToBeProcessed = new HashSet<>();
    public OrdersAtPrice ordersAtPrice;

    private MikePosOrders parent = null;

    private Set<MikePosOrders> childPosOrders = new HashSet<>();

    private OrderServer orderServer;
    public PriceServer priceServer; //we need this to calculate Profit/Loss (PL)
    protected MikePosOrders() {

    }

    public MikePosOrders(OrderServer orderServer, PriceServer priceServer) {
        this.orderServer = orderServer;
        this.priceServer = priceServer;
        ordersAtPrice = new OrdersAtPrice();
    }

    /**
     * Use this to place a new order. Passes the order to the OrderServer.
     * returns the orderID obtained from orderServer.
     * Watch out for negative order amounts!
     * @return
     */
    synchronized public long placeNewOrder(MikeOrder.MikeOrderType orderType, int assignedToPos, int price, int amount) {

        MikeSimLogger.addLogEvent("Placing new order in " + getName());
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

    /**
     * this doesn't cancel the orders at this price in child MikePosOrders
     * @param price
     */
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

        MikeSimLogger.addLogEvent("Cancelling all orders at price. NOT CANCELLING IN CHILDREN! " + price + " in " + getName());
    }

    /**
     * This makes the position flat by submitting stop orders 5 pricepoints below bid/ask for instant fill
     * @param percentage the percent by which we want to reduce it. 1 is all, 0.25 is by 25%, 0.7 is by 70%
     */
    public synchronized void flattenThisPosition(float percentage){
        int safetyAmount = 5; //this is how far from the bid/ask we want to place the stop orders
        int openAmount = getTotalOpenAmount();
        if(openAmount > 0) placeNewOrder(MikeOrder.MikeOrderType.SELLSTP, getAskPrice(), (getAskPrice() + safetyAmount), (int) (openAmount * percentage));
        if(openAmount < 0) placeNewOrder(MikeOrder.MikeOrderType.BUYSTP, getBidPrice(), (getBidPrice() - safetyAmount), (int) (openAmount * percentage));
    }

    /**
     * Cancels all the orders in this PosOrders
     */
    public synchronized void cancelAllOrders(){
        Set<Long> ordersToCancel = new TreeSet<>();
        //cancelling orders includes removing them from this set so we need a copy
        for(Long orderId : activeOrdersSet){
            ordersToCancel.add(orderId);
        }
        for (Long orderId : ordersToCancel){
            cancelOrder(orderId);
        }
    }

    public synchronized void cancelAllOrdersAtOrABOVEPrice(int price){
        Set<Long> ordersToCancel = new TreeSet<>();

        //find all the orders with the specified price and if it is higher than price passed as parameter,
        //add it to the list to be cancelled
        for(Long orderId : activeOrdersSet){
            if(getOrderServer().getMikeOrder(orderId).getPrice() >= price) ordersToCancel.add(orderId);
        }

        for (Long orderId : ordersToCancel){
            cancelOrder(orderId);
        }

    }

    public synchronized void cancelAllOrdersAtOrBELOWPrice(int price){
        Set<Long> ordersToCancel = new TreeSet<>();

        //find all the orders with the specified price and if it is higher than price passed as parameter,
        //add it to the list to be cancelled
        for(Long orderId : activeOrdersSet){
            if(getOrderServer().getMikeOrder(orderId).getPrice() <= price) ordersToCancel.add(orderId);
        }

        for (Long orderId : ordersToCancel){
            cancelOrder(orderId);
        }

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
    }


    /**
     * ask this class for the amount of open buy and sell orders at a given price
     */
    public class OrdersAtPrice {

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


    /**
     * This calculates: openPL, closedPL, totalPL, totalOpenAmount, averagePrice
     * and zeroProfitPoint
     */
    public synchronized void recalcutlatePL() {

        //calculate values for this MikePosOrders:
        openPL = 0;
        closedPL = 0;
        totalPL = 0;
        totalOpenAmount = 0;
        averagePrice = 0.0;
        int bidPrice = priceServer.getBidPrice();
        int askPrice = priceServer.getAskPrice();
        double averagePriceCalculator = 0;
        for (MikePosition position : positionsMap.values()) {
            position.calculatePL(bidPrice, askPrice);
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

        //internaClosedPL belongs to this MikePosOrders, it is independent from all individual MikePositions
        closedPL = closedPL + internalClosedPL;
        totalPL = openPL + closedPL;
//        totalPL = totalPL + internalClosedPL;

        //this is suspicious
        if (totalOpenAmount == 0) {
            //chenging stuff here - test this:
            openPL = 0;
            closedPL = totalPL;
//            zeroProfitPoint = null;
//            averagePrice = null;
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
     * returns false if error in parameters or true if successful
     *
     * @param price           price of the MikePosition to be moved
     * @param targetPosOrders the place where the MikePosition should be moved to
     */
    public synchronized boolean movePositionToDifferentMikePosOrders(int price, MikePosOrders targetPosOrders) {
        //you cannot move a MikePosition from a MikePosOrders to a MikePosOrders that is the same MikePosOrders!
        if(targetPosOrders == this){
            MikeSimLogger.addLogEvent("Attempting to move MikePosition into the same target MikePosOrders as the source one! Aborting!");
            return false;
        }

        //if there is no position at the price, do nothing:
        if (positionsMap.get(price) == null) return false;

        //if target doesn't exist, do nothing:
        if (targetPosOrders == null) return false;

        //get the position to move:
        MikePosition positionToMove = positionsMap.get(price);

        //add it to the target MikePosOrders:
        targetPosOrders.addToMikePosition(positionToMove);

        //remove it from this MikePosOrders:
        positionsMap.remove(price);

        //recalculate the PL:
        recalcutlatePL();
        targetPosOrders.recalcutlatePL();

        return true;

        //currently not moving children

    }

    /**
     * Use this for transferring a single position from one MikePosOrders to a different one
     *
     * @param positionToAdd
     */
    protected synchronized void addToMikePosition(MikePosition positionToAdd) {
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

        MikeSimLogger.addLogEvent("newPosition open amount: " +newPosition.getOpen_amount());

        //remove the old position from this book and replace it with the new one:

        positionsMap.put(newPosition.getPrice(), newPosition);

        //recalculate the profit/loss:
        recalcutlatePL();

        //currently not moving children
    }

    /**
     * This takes a single MikePosition and moves it to a different price level while keeping the totalPL for
     * it unchanged by altering its closedPL
     * @param originalPrice
     * @param newPrice
     */
    public synchronized void moveSinglePosToNewPrice(int originalPrice, int newPrice){
        //IT IS POSSIBLE THAT originalPrice = newPrice which will CAUSE BUGS!!!!!
        if(originalPrice == newPrice) return;
        if( (originalPrice <0) || (newPrice <0) ) return;
        MikePosition mikePosition = positionsMap.get(originalPrice);
        if(mikePosition == null) return;

        int differenceInPrice = newPrice - originalPrice;
        //we alter the closed PL of the position so that the total PL at the new price level is still the same:
        int openAmount = mikePosition.getOpen_amount();

        int closedPLAdjustment = openAmount * differenceInPrice;
        MikePosition newPosition = new MikePosition(newPrice, mikePosition.getOpen_amount(), mikePosition.getOpen_pl(),
                mikePosition.getClosed_pl() + closedPLAdjustment,
                mikePosition.getTotal_pl() + closedPLAdjustment
                );
        //add the position with adjusted PL to this MikePosOrders:
        addToMikePosition(newPosition);

        //set everything in the old one to zero so it doesn't get double counted:
        mikePosition.zeroOut();
        //remove it from this MikePosOrders:
        positionsMap.remove(mikePosition.getPrice());

    }

    /**
     * Returns the price of the first MikePosition above or below the submitted price. If nothing found returns
     * the price entered or price entered +1
     * @param priceEntered
     * @param above returns above for true, below for negative
     * @return
     */
    public Integer findFirstPosAboveOrBelowPrice(int priceEntered, boolean above){
        int minimumPriceInterval = 1;
        //create a list of all the prices:
        NavigableSet<Integer> priceList = new TreeSet<>();
        priceList.addAll(positionsMap.keySet());
        if(priceList.isEmpty()) return null;
        //https://stackoverflow.com/questions/19613650/java-find-closest-number-in-some-collection
        Integer found = priceEntered;
        if(above) found = priceList.higher(priceEntered);
        if(!above) found = priceList.lower(priceEntered);

        if (found == null) MikeSimLogger.addLogEvent("Nothing above/below! Returning null!");


        return found;

//        for(Integer members : priceList){
//            MikeSimLogger.addLogEvent("Position at: " + members);
//        }
//        MikeSimLogger.addLogEvent("First above found: " + found);
//        MikeSimLogger.addLogEvent("lower: " + priceList.lower(priceEntered));
//        MikeSimLogger.addLogEvent("floor: " + priceList.floor(priceEntered));
//        MikeSimLogger.addLogEvent("ceiling: " + priceList.ceiling(priceEntered));
    }

    /**
     * If there are many MikePositions at different price levels, this moves all of them to one preserving the
     * total PL by altering their closed PL
     * @param price of the new single MikePosition
     */
    public void consolidatePositions(int price){
        if(price < 0){
            MikeSimLogger.addLogEvent("Moving to a negative price not implemented! Aborting!");
            return;
        }
        Set<Integer> pricesToMove = new TreeSet<>(); //contains prices of all the individual MikePositions
        pricesToMove.addAll(positionsMap.keySet());

        //move them all to provided price
        for (Integer oldPrice : pricesToMove){
            moveSinglePosToNewPrice(oldPrice, price);
        }
    }

    @Override
    public String toString() {
        return name;
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
     *
     * @param currentPrice
     * @param newPrice
     */
    synchronized public void moveMikePosorders(int currentPrice, int newPrice){
        //TODO: FINISH THIS
    }


    /**
     * For testing purposes
     */
    public void printPositionsToConsole() {

        MikeSimLogger.addLogEvent("Printing positions!");
        for (MikePosition position : positionsMap.values()) {
            MikeSimLogger.addLogEvent("Position price: " + position.getPrice() +
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
            MikeSimLogger.addLogEvent("Active order ID: " + getOrderServer().getAllOrdersMap().get(orderId).getMikeOrderNumber() + " Order price: "
                    + getOrderServer().getAllOrdersMap().get(orderId).getPrice());
        }
    }

    public Set<Integer> getPositionPricesSet() {
        return positionsMap.keySet();
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

    public Double getAveragePrice() {
        return averagePrice;
    }

    public int getTotalOpenAmount() {
        return totalOpenAmount;
    }

    public int getTickerId() {
        return priceServer.getTickerID();
    }

    /**
     * CAUTION! this return a DOUBLE IN CENTS!
     * use Double.asInt() to get the zeroPP in cents for comparisons
     * this returns null if zeroProfitPoint does not exist
     * @return
     */
    public Double getZeroProfitPoint() {
        return zeroProfitPoint;
    }

    /**
     * returns true if order exists and has been filled
     * if order does not exist or has not been filled, returns false
     * @param orderId
     * @return
     */
    public boolean checkIfOrderFilled(long orderId) {
        boolean isfilled = false;
        try {
            if (getOrderServer().getMikeOrder(orderId).isFilled()) {
                isfilled = true;
            }
        } catch (Exception e) {
            MikeSimLogger.addLogEvent("Exception in MikePosOrders.checkIfOrderFilled");
            e.printStackTrace();
        }
        return isfilled;
    }

    public int getBidPrice(){
        return priceServer.getBidPrice();
    }

    public int getAskPrice(){
        return priceServer.getAskPrice();
    }

    private boolean recalculateChildren = true;

    /**
     * old failed experiment
     * @param setting
     */
    private void setRecalculateChildren(boolean setting) {
        recalculateChildren = setting;
    }

    /**
     * Old failed experiment
     * @return
     */
    private MikePosOrders createChildPosOrders() {
        MikePosOrders child = new MikePosOrders(orderServer, priceServer);
        child.setName("" + name + " child" + (childPosOrders.size() + 1));
        //this is needed for recalcualteOrdersAtPrice methond:
        child.setParent(this);
        childPosOrders.add(child);
        return child;
    }

    public void setParent(MikePosOrders posOrders) {
        parent = posOrders;
    }

}