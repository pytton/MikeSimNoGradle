package main.java.model.orderserver;

public class OrderServer {

    private int mikeSimmOrderNumber = 0;

    /**
     * Use this to place a new order.
     * @return
     */
    public int newOrder(){

        return mikeSimmOrderNumber++;
    }

    private boolean addOrderToQueue(){
        return true;
    }



}
