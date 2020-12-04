package main.java.model.orderserver;

import main.java.model.MikeSimLogger;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to simulate placing and filling orders internally.
 * checkSimulatedFills has to be checked as often as possible to make the
 * simulated order filling works accurately - it works by comparing
 * current bid/ask price with orders to decide if they were filled or not
 */
public class OrderServerSimulatedInternal extends OrderServer {


    /**
     * Call this to check for fills of orders being simulated by monitoring bid/ask price
     *                 for buy orders, the amount will be positive
     *                 for sell orders, the amount has to be negative!
     *                 a negative filled amount means the position is short
     */
    synchronized public void checkFills(PriceServer priceServer){

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

                MikeSimLogger.addLogEvent("Order Filled! Fill Price: " + order.getFilledPrice());
            }
            if(order.getOrderType()== MikeOrder.MikeOrderType.BUYSTP && askPrice >= order.getPrice() && !order.isCancelled()){
                order.setFilled(true);
                order.setFilledPrice(askPrice);
                order.setFilledAmount(order.getAmount());
                filledOrderIDs.add(orderId);

                MikeSimLogger.addLogEvent("Order Filled! Fill Price: " + order.getFilledPrice());
            }
            if(order.getOrderType()== MikeOrder.MikeOrderType.SELLLMT && bidPrice >= order.getPrice() && !order.isCancelled()){
                order.setFilled(true);
                order.setFilledPrice(bidPrice);
                //this is a sell order so the amount must be negative!
                order.setFilledAmount(order.getAmount() * -1);
                filledOrderIDs.add(orderId);

                MikeSimLogger.addLogEvent("Order Filled! Fill Price: " + order.getFilledPrice());
            }
            if(order.getOrderType()== MikeOrder.MikeOrderType.SELLSTP && bidPrice <= order.getPrice() && !order.isCancelled()){
                order.setFilled(true);
                order.setFilledPrice(bidPrice);
                //this is a sell order so the amount must be negative!
                order.setFilledAmount(order.getAmount() * -1);
                filledOrderIDs.add(orderId);

                MikeSimLogger.addLogEvent("Order Filled! Fill Price: " + order.getFilledPrice());
            }
        }

        //notify mikePosOrders about orders that have been filled and
        //remove all the filled orderIDs from the activeOrdersList and add them to the filledOrdersList:
        for(long orderId : filledOrderIDs){
            (allOrdersMap.get(orderId)).getPosOrders().notifyAboutFill(orderId);
            getActiveOrdersList().remove(orderId);
            getFilledOrdersList().add(orderId);
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


    /**
     * This only works with simulated orders!
     * If order was sent to outside trading software it will not be cancelled
     * todo: think of a way to cancel the order if it was sent to outside trading software
     * @param orderID
     */
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


}
