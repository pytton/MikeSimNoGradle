package main.java.model.mikealgos;

import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

public class ScalperAlgo1 extends BaseAlgo {

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
        HIGHERFILLED
    }

    private Status status;

    /**
     * Constructor enforces that selling price is always higher than buying price regardless of provided parameters
     * If caller makes scalper to buy at a higher price than it sells, it will sell at buyingprice +1
     * @param posOrders
     * @param entryTargetPrice
     * @param exitTargetPrice
     * @param orderAmount
     * @param entry
     */
    public ScalperAlgo1(MikePosOrders posOrders, int entryTargetPrice, int exitTargetPrice, int orderAmount, MikeOrder.MikeOrderType entry) {
        this.posOrders = posOrders;
        this.entryTargetPrice = entryTargetPrice;
        this.exitTargetPrice = exitTargetPrice;
        this.orderAmount = orderAmount;
        this.entryOrderType = entry;

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

        }


        status = Status.CREATED;
    }

    @Override
    public void process() {
        //todo: write this

        //PSEUDOCODE:

        //1. it status is 'CREATED' then create first orders

        if (status == Status.CREATED) {
            lowOrderId = posOrders.placeNewOrder(entryOrderType, entryTargetPrice, entryTargetPrice, orderAmount);
            status = Status.RUNNING;
            return;
        }
        if (status == Status.RUNNING) {
            //if lowOrderId is filled, create the highOrder, change status to LOWERFILLED
            //assigning order to entryTargetPrice position to avoid problems with incorrectly calculating openPL.
            if (posOrders.getOrderServer().getMikeOrder(lowOrderId).isFilled()) {
                highOrderId = posOrders.placeNewOrder(exitOrderType, entryTargetPrice, exitTargetPrice, orderAmount);
                status = Status.LOWERFILLED;
                return;
            }
        }
        if (status == Status.LOWERFILLED) {
            //check if exitTargetPrice has been filled:
            if (posOrders.getOrderServer().getMikeOrder(highOrderId).isFilled()) {
                lowOrderId = posOrders.placeNewOrder(returnOrderType, entryTargetPrice, entryTargetPrice, orderAmount);
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
