package main.java.model.mikealgos;

import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

public class ScalperAlgo1 extends BaseAlgo {

    private MikePosOrders posOrders;
    private int lowerTarget = 0;
    private int upperTarget = 1;
    private int amount = 0;

    private long lowOrderId;
    private long highOrderId;


    private enum Status{
        CREATED,
        RUNNING,
        LOWERFILLED,
        HIGHERFILLED
    }

    private Status status;

    public ScalperAlgo1(MikePosOrders posOrders, int lowerTarget, int upperTarget, int amount) {
        this.posOrders = posOrders;
        this.lowerTarget = lowerTarget;
        this.upperTarget = upperTarget;
        this.amount = amount;

        status = Status.CREATED;
    }

    @Override
    public void process() {
        //todo: write this

        //PSEUDOCODE:

        //1. it status is 'CREATED' then create first orders

        if (status == Status.CREATED) {
            lowOrderId = posOrders.placeNewOrder(MikeOrder.MikeOrderType.BUYLMT, lowerTarget, lowerTarget, amount);
            status = Status.RUNNING;
            return;
        }
        if (status == Status.RUNNING) {
            //if lowOrderId is filled, create the highOrder, change status to LOWERFILLED
            if (posOrders.getOrderServer().getMikeOrder(lowOrderId).isFilled()) {
                highOrderId = posOrders.placeNewOrder(MikeOrder.MikeOrderType.SELLLMT, upperTarget, upperTarget, amount);
                status = Status.LOWERFILLED;
                return;
            }
        }
        if (status == Status.LOWERFILLED) {
            //check if upperTarget has been filled:
            if (posOrders.getOrderServer().getMikeOrder(highOrderId).isFilled()) {
                lowOrderId = posOrders.placeNewOrder(MikeOrder.MikeOrderType.BUYLMT, lowerTarget, lowerTarget, amount);
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
