package main.java.model.algocontrol;

import main.java.model.MikeSimLogger;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

public class SimpleScalperAlgo extends BaseAlgo {

    private MikePosOrders posOrders;
    private int entryTargetPrice = 0;
    private int exitTargetPrice = 1;
    private int orderAmount = 0;

    private long lowOrderId;
    private long highOrderId;

    private MikeOrder.MikeOrderType entryOrderType;
    private MikeOrder.MikeOrderType exitOrderType;
    private MikeOrder.MikeOrderType returnOrderType;

    private enum Status{
        CREATED,
        RUNNING,
        LOWERFILLED,
        HIGHERFILLED,
        CANCELLED
    }

    private Status status;

    /**
     * Constructor enforces that selling price is always higher than buying price regardless of provided parameters
     * If caller makes scalper to buy at a higher price than it sells, it will sell at buyingprice +1
     * @param posOrders
     * @param entryTargetPrice
     * @param exitTargetPrice
     * @param orderAmount
     * @param entryOrderType
     */
    protected SimpleScalperAlgo(MikePosOrders posOrders, int entryTargetPrice, int exitTargetPrice, int orderAmount, MikeOrder.MikeOrderType entryOrderType) {

        //If entry is BUYLMT or BUYSTP then interval has to be a positive value
        int interval =  exitTargetPrice - entryTargetPrice;
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
        exitTargetPrice = entryTargetPrice + interval;


        if (entryOrderType == MikeOrder.MikeOrderType.BUYLMT || entryOrderType == MikeOrder.MikeOrderType.BUYSTP) {
            exitOrderType = MikeOrder.MikeOrderType.SELLLMT;
            returnOrderType = MikeOrder.MikeOrderType.BUYLMT;
            //make sure the scalper buys at a lower price than it sells:
            if(entryTargetPrice >= exitTargetPrice) exitTargetPrice = entryTargetPrice +1;
        } else if (entryOrderType == MikeOrder.MikeOrderType.SELLLMT || entryOrderType == MikeOrder.MikeOrderType.SELLSTP) {
            exitOrderType = MikeOrder.MikeOrderType.BUYLMT;
            returnOrderType = MikeOrder.MikeOrderType.SELLLMT;
            //make sure the scalper sells at a higher price than it buys:
            if(entryTargetPrice <= exitTargetPrice) exitTargetPrice = entryTargetPrice -1;
        } else {
            MikeSimLogger.addLogEvent("ERROR CREATING SIMPLESCALPERALGO");
            status = Status.CANCELLED;
        }

        this.posOrders = posOrders;
        this.entryTargetPrice = entryTargetPrice;
        this.exitTargetPrice = exitTargetPrice;
        this.orderAmount = orderAmount;
        this.entryOrderType = entryOrderType;
        status = Status.CREATED;

    }

    @Override
    public synchronized void process() {
        //1. if status is 'CREATED' then create first orders
        if (status == Status.CREATED) {
            lowOrderId = posOrders.placeNewOrder(entryOrderType, entryTargetPrice, entryTargetPrice, orderAmount);
            status = Status.RUNNING;
            return;
        }
        if (status == Status.RUNNING) {
            //if lowOrderId is filled, create the highOrder, change status to LOWERFILLED
            //assigning order to entryTargetPrice position to avoid problems with incorrectly calculating openPL.
            if (posOrders.checkIfOrderFilled(lowOrderId)) {
                highOrderId = posOrders.placeNewOrder(exitOrderType, entryTargetPrice, exitTargetPrice, orderAmount);
                status = Status.LOWERFILLED;
                return;
            }
        }
        if (status == Status.LOWERFILLED) {
            //check if exitTargetPrice has been filled:
            if (posOrders.checkIfOrderFilled(highOrderId)) {
                lowOrderId = posOrders.placeNewOrder(returnOrderType, entryTargetPrice, entryTargetPrice, orderAmount);
                status = Status.RUNNING;
                return;
            }
        }

        if (status == Status.CANCELLED) {
            //algo has been cancelled, do nothing and return
            return;
        }

    }

    @Override
    public synchronized void cancel() {
        //1. Cancel all orders
        posOrders.cancelOrder(lowOrderId);
        posOrders.cancelOrder(highOrderId);
        //2. Set status to CANCELLED
        status = Status.CANCELLED;
    }

    @Override
    public int getEntryPrice() {
        return entryTargetPrice;
    }

    @Override
    public MikePosOrders monitoredMikePosOrders() {
        return posOrders;
    }

    public int getEntryTargetPrice() {
        return entryTargetPrice;
    }


    @Override
    public String toString() {
        String description = "SimpleScalperAlgo" + entryOrderType + " at price: ";
        description = description + ("" + entryTargetPrice + " on: " + posOrders.toString());
        description = description + (" status: " + status);
        return description;
    }
}
