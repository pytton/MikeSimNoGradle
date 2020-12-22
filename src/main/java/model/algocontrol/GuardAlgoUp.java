package main.java.model.algocontrol;

import main.java.model.MikeSimLogger;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

public class GuardAlgoUp extends GuardAlgo {

    protected GuardAlgoUp(MikePosOrders monitored, MikePosOrders orderTarget, int guardBuffer) {

        this.guardBuffer = 45;
        this.monitored = monitored;
        this.orderTarget = orderTarget;
        if (guardBuffer > 0) this.guardBuffer = guardBuffer;
        status = Status.CREATED;
    }

    @Override
    protected void processCREATED() {
        if(monitored.getTotalOpenAmount() == 0) return; //if position is zero algo can't work
        //When the bid/ask price gets far enough from the zeroProfitPoint
        //send the first order and change status to RUNNING
        if(checkDistance() < guardBuffer +1){
            //create first order and pass it to the target
            processGuardOrder();
            status = Status.RUNNING;
            MikeSimLogger.addLogEvent("GuardAlgoDown monitoring " + monitored.toString() + " changing status to RUNNING");
        }
    }

    @Override
    protected boolean checkLongPositionFailed() {
        //check if failed
        if(monitored.getAskPrice() < monitored.getZeroProfitPoint().intValue()) {
            status = Status.FAILED;
            return true;}
        return false;
    }

    @Override
    protected boolean checkShortPositionFailed() {
        //check if failed
        if(monitored.getBidPrice() > monitored.getZeroProfitPoint().intValue()) {
            status = Status.FAILED;
            return true;}
        return false;
    }

    @Override
    protected void processGuardOrder() {
        if(monitored == null) return;
        //order type and order amount depends on whether position is long or short
        MikeOrder.MikeOrderType ordertype = MikeOrder.MikeOrderType.BUYSTP;
        //todo: do the math and implement it
        // for testing just make the order 1/5 of current position:
        int orderAmount = monitored.getTotalOpenAmount() / 10;
        int zeroProfitPoint = orderTarget.getZeroProfitPoint().intValue();
        int price = zeroProfitPoint + guardBuffer;

        //things change if position is short instead of long:
        if (monitored.getTotalOpenAmount() < 0) {
            ordertype = MikeOrder.MikeOrderType.SELLSTP;
            orderAmount = (monitored.getTotalOpenAmount() / 10) * -1; //TotalOpenAmount negative for short positions
            price = zeroProfitPoint - guardBuffer;
        }
        //cancel existing guardOrder:
        orderTarget.cancelOrder(guardOrderId);

        if (orderAmount < 1) {
            status = Status.FAILED;
            return;
        }

        //send the order:
        guardOrderId = orderTarget.placeNewOrder(ordertype, price, price, orderAmount);
        previousZeroPP = zeroProfitPoint;

    }

    @Override
    protected void processFAILED() {
        //if for some reason, after the first order is placed, the distance between bid/ask
        //price and zeroProfitPoint gets below a certain margin, wait until it changes
        //and switch the status to RUNNING
        if (checkDistance() > minimumOrderFireDistance) status = Status.RUNNING;
    }
}
