package main.java.model.mikealgos;

import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

public class StepperAlgoUp1 extends BaseAlgo {

    private MikePosOrders posOrders;
    private int startPrice = 0;
    private int interval = 1;
    private int amount = 0;

    private long startOrderId;
    private long targetOrderId;
    private long exitOrderId;
    private long reEntryOrderId;


    private enum Status{
        CREATED,
        RUNNING,
        STARTFILLED,
        TARGETFILLED,
        EXITFILLED
    }

    private Status status;

    public StepperAlgoUp1(MikePosOrders posOrders, int startPrice, int interval, int amount) {
        this.posOrders = posOrders;
        this.startPrice = startPrice;
        this.interval = interval;
        this.amount = amount;

        status = Status.CREATED;
    }

    @Override
    public void process() {
        //todo: write this

        //PSEUDOCODE:

        if (status == Status.CREATED) {
            //create the first buy order:
            startOrderId = posOrders.placeNewOrder(MikeOrder.MikeOrderType.BUYLMT, startPrice, startPrice, amount);
            status = Status.RUNNING;
            return;
        }
        if (status == Status.RUNNING) {
            //check if first order was filled. If it has, place the target order:
            //target order is half the amount of startOrder
            if (posOrders.getOrderServer().getMikeOrder(startOrderId).isFilled()) {
                targetOrderId = posOrders.placeNewOrder(MikeOrder.MikeOrderType.SELLLMT, (startPrice + interval), (startPrice + interval), (amount / 2));
                status = Status.STARTFILLED;
                return;
            }
        }
        if (status == Status.STARTFILLED) {
            //check if the target has been filled. If it has, create the exit order:
            //exit order half of firstOrder - should make position 0
            if (posOrders.getOrderServer().getMikeOrder(targetOrderId).isFilled()) {
                exitOrderId = posOrders.placeNewOrder(MikeOrder.MikeOrderType.SELLSTP, (startPrice - interval), (startPrice - interval), (amount / 2));
                status = Status.TARGETFILLED;
                return;
            }
        }
        if (status == Status.TARGETFILLED) {
            //if target was filled then exit order was placed.
            //check if it was filled. If it has, then the poosition should be flat now,
            // so make a STOP BUY order back at the initial entry price:
            if (posOrders.getOrderServer().getMikeOrder(exitOrderId).isFilled()) {
                startOrderId = posOrders.placeNewOrder(MikeOrder.MikeOrderType.BUYSTP, startPrice, startPrice, amount);
                status = Status.RUNNING;
                return;
            }
        }
    }

    @Override
    public void cancel() {
        //todo: write this
    }

}
