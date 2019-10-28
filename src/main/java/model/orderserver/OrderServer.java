package main.java.model.orderserver;

import com.sun.deploy.util.OrderedHashSet;
import main.java.model.priceserver.PriceServer;

import java.util.*;

public class OrderServer {

    public Map<Long, MikeOrder> allOrdersMap = new HashMap<>();
    public SortedSet<Long> activeOrdersList = new TreeSet<>();
    public SortedSet<Long> filledOrdersList = new TreeSet<>();
    private long mikeSimOrderNumber = 0;

    /**
     * Call this to check for fills of orders being simulated by monitoring bid/ask price
     */
    public void checkSimulatedFills(PriceServer priceServer){

        int bidPrice = priceServer.getBidPrice();
        int askPrice = priceServer.getAskPrice();

        List<Long> filledOrderIDs = new ArrayList<>();
        //TODO: finish this. currently this fills the whole order based on bid/ask prices
        //check all the active orders
        for(long orderID : activeOrdersList){
            MikeOrder order = allOrdersMap.get(orderID);

            if(order.getOrderType()== MikeOrder.MikeOrderType.BUYLMT && askPrice <= order.getPrice()){
                order.setFilled(true);
                order.setFilledAmount(order.getAmount());
                filledOrderIDs.add(orderID);

                System.out.println("Order Filled!");
            }
        }

        //remove all the filled orderIDs from the activeOrdersList and add them to the filledOrdersList:
        for(long orderID : filledOrderIDs){
            activeOrdersList.remove(orderID);
            filledOrdersList.add(orderID);
        }

        //TODO: think of something to notify PosOrders class what placed the order that it has been filled.
    }

    public void printActiveOrdersToConsole(){
        for(long orderID : activeOrdersList){
            //print out active orders to console:
            System.out.println("Active order ID: " + allOrdersMap.get(orderID).getMikeOrderNumber() + " Order price: "
                    + allOrdersMap.get(orderID).getPrice());
        }
    }

    /**
     * Use this to place a new order.
     * @return
     */
    public long placeNewOrder(MikeOrder.MikeOrderType orderType, int assignedToPos, int price, int amount){
        //create the order:
        MikeOrder order = new MikeOrder(orderType, assignedToPos, price, amount);
        //set a unigue order number:
        order.setMikeOrderNumber(mikeSimOrderNumber);
        //add the order to the map of all orders:
        allOrdersMap.put(mikeSimOrderNumber, order);
        //this is a new order so add it to activeOrders:
        activeOrdersList.add(mikeSimOrderNumber);

        //TODO: erase this once everthing is working:
        //printout to console:


        return mikeSimOrderNumber++;
    }

//    private boolean addOrderToQueue(){
//        return true;
//    }
//
}
