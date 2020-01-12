package main.java.model.mikealgos;

import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

public class SimpleStepperAlgo extends BaseAlgo {

    private MikeOrder.MikeOrderType entryOrderType;
    private MikeOrder.MikeOrderType firstExitOrderType;
    private MikeOrder.MikeOrderType secondExitOrderType;
    private MikeOrder.MikeOrderType returnOrderType;

    private MikePosOrders posOrders;
    private int entryTargetPrice = 0;
    private int interval = 1;
    private int amount = 0;

    private long startOrderId;
    private long targetOrderId;
    private long exitOrderId;
//    private long reEntryOrderId;


    private enum Status{
        CREATED,
        RUNNING,
        STARTFILLED,
        TARGETFILLED,
        EXITFILLED,
        CANCELLED
    }

    private Status status;

    public SimpleStepperAlgo(MikePosOrders posOrders, int entryTargetPrice, int interval, int amount, MikeOrder.MikeOrderType entryOrderType) {

        status = Status.CREATED;

        if (entryOrderType == MikeOrder.MikeOrderType.BUYLMT || entryOrderType == MikeOrder.MikeOrderType.BUYSTP) {
            firstExitOrderType = MikeOrder.MikeOrderType.SELLLMT;
            secondExitOrderType = MikeOrder.MikeOrderType.SELLSTP;
            returnOrderType = MikeOrder.MikeOrderType.BUYSTP;
        } else if (entryOrderType == MikeOrder.MikeOrderType.SELLLMT || entryOrderType == MikeOrder.MikeOrderType.SELLSTP) {
            firstExitOrderType = MikeOrder.MikeOrderType.BUYLMT;
            secondExitOrderType = MikeOrder.MikeOrderType.BUYSTP;
            returnOrderType = MikeOrder.MikeOrderType.SELLSTP;
        } else {
            System.out.println("ERROR CREATING STEPPERALGO1");
            status = Status.CANCELLED;
        }

        //If entry is BUYLMT or BUYSTP then interval has to be a positive value
        if (entryOrderType == MikeOrder.MikeOrderType.BUYLMT || entryOrderType == MikeOrder.MikeOrderType.BUYSTP){
            if (interval < 0) {
                interval = interval * -1;
            }
            if (interval == 0) interval = 1;
        }

        //If entry is SELLLMT or SELLSTP then interval has to be negative.
        if (entryOrderType == MikeOrder.MikeOrderType.SELLLMT || entryOrderType == MikeOrder.MikeOrderType.SELLSTP){
            if (interval > 0) {
                interval = interval * -1;
            }
            if (interval == 0) interval = -1;
        }

        this.posOrders = posOrders;
        this.entryTargetPrice = entryTargetPrice;
        this.interval = interval;
        this.amount = amount;
        this.entryOrderType = entryOrderType;
    }

    @Override
    public synchronized void process() {
        //todo: write this

        //PSEUDOCODE:

        if (status == Status.CREATED) {
            //create the first buy order:
            startOrderId = posOrders.placeNewOrder(entryOrderType, entryTargetPrice, entryTargetPrice, (amount /2)*2);
            status = Status.RUNNING;
            return;
        }
        if (status == Status.RUNNING) {
            //check if first order was filled. If it has, place the target order:
            //target order is half the amount of startOrder
            if (posOrders.checkIfOrderFilled(startOrderId)) {
                targetOrderId = posOrders.placeNewOrder(firstExitOrderType, entryTargetPrice, (entryTargetPrice + interval), (amount / 2));
                status = Status.STARTFILLED;
                return;
            }
        }
        if (status == Status.STARTFILLED) {
            //check if the target has been filled. If it has, create the exit order:
            //exit order half of firstOrder - should make position 0
            if (posOrders.checkIfOrderFilled(targetOrderId)) {
                exitOrderId = posOrders.placeNewOrder(secondExitOrderType, entryTargetPrice, (entryTargetPrice - interval), (amount / 2));
                status = Status.TARGETFILLED;
                return;
            }
        }
        if (status == Status.TARGETFILLED) {
            //if target was filled then exit order was placed.
            //check if it was filled. If it has, then the poosition should be flat now,
            // so make a STOP BUY order back at the initial entry price:
            if (posOrders.checkIfOrderFilled(exitOrderId)) {
                startOrderId = posOrders.placeNewOrder(returnOrderType, entryTargetPrice, entryTargetPrice, (amount /2)*2);
                status = Status.RUNNING;
                return;
            }
        }
    }

    @Override
    public synchronized void cancel() {
        posOrders.cancelOrder(startOrderId);
        posOrders.cancelOrder(targetOrderId);
        posOrders.cancelOrder(exitOrderId);
        status = Status.CANCELLED;
    }

    @Override
    public MikePosOrders getMikePosOrders() {
        return posOrders;
    }

    public int getEntryTargetPrice() {
        return entryTargetPrice;
    }


}
