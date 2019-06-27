package main.java.model.orderserver;

public class OrderServer {

    private int mikeSimOrderNumber = 0;

    /**
     * Use this to place a new order.
     * @return
     */
    public int newOrder(){

        return mikeSimOrderNumber++;
    }

    private boolean addOrderToQueue(){
        return true;
    }



}
