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
    private long trailingStopOrderId;

    private int startOrderAmount;
    private int targetOrderAmount;
    private int exitOrderAmount;
    private int exitOrderPrice;
    private int trailingStopOrderAmount;
    private int trailingStopPrice;
//    private int trailingStopTriggerPrice;

    private boolean trailingStopTriggered = false;
    private boolean fixedTrailingStop = false;


    private enum Status{
        CREATED,
        RUNNING,
        STARTFILLED,
        TARGETFILLED,
        EXITFILLED,
        CANCELLED
    }

    private Status status;

    public SimpleStepperAlgo(MikePosOrders posOrders, int entryTargetPrice, int interval, int amount,
                             MikeOrder.MikeOrderType entryOrderType, boolean smTrailingStop, boolean fixedTrailingStop) {

        status = Status.CREATED;
        //set the order amount depending on whether trailing stop option selected:
        if(smTrailingStop == true){
            startOrderAmount = (amount/4) *4;
            targetOrderAmount = (amount/4) *2;
            exitOrderAmount = (amount /4);
            trailingStopOrderAmount = (amount / 4);
        }

        if (smTrailingStop == false) {
            startOrderAmount = (amount/2) *2;
            targetOrderAmount = amount/2;
            exitOrderAmount = amount/2;
            trailingStopOrderAmount = 0;
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


        exitOrderPrice = entryTargetPrice - interval;
        System.out.println("Creating SimpleStepperAlgo with exitOrder Price: " + exitOrderPrice);
        trailingStopPrice = exitOrderPrice;

        //setup the order types and trailing stop trigger price:
        if (entryOrderType == MikeOrder.MikeOrderType.BUYLMT || entryOrderType == MikeOrder.MikeOrderType.BUYSTP) {
            firstExitOrderType = MikeOrder.MikeOrderType.SELLLMT;
            secondExitOrderType = MikeOrder.MikeOrderType.SELLSTP;
            returnOrderType = MikeOrder.MikeOrderType.BUYSTP;
//            trailingStopTriggerPrice = entryTargetPrice + (interval * 2);
        } else if (entryOrderType == MikeOrder.MikeOrderType.SELLLMT || entryOrderType == MikeOrder.MikeOrderType.SELLSTP) {
            firstExitOrderType = MikeOrder.MikeOrderType.BUYLMT;
            secondExitOrderType = MikeOrder.MikeOrderType.BUYSTP;
            returnOrderType = MikeOrder.MikeOrderType.SELLSTP;
        } else {
            System.out.println("ERROR CREATING STEPPERALGO1");
            status = Status.CANCELLED;
        }



        this.posOrders = posOrders;
        this.entryTargetPrice = entryTargetPrice;
        this.interval = interval;
        this.amount = amount;
        this.entryOrderType = entryOrderType;
        this.fixedTrailingStop = fixedTrailingStop;
    }

    @Override
    public synchronized void process() {

        if (status == Status.CREATED) {
            //create the first buy order:
            startOrderId = posOrders.placeNewOrder(entryOrderType, entryTargetPrice, entryTargetPrice, startOrderAmount);
            status = Status.RUNNING;
            return;
        }
        if (status == Status.RUNNING) {
            //check if first order was filled. If it has, place the target order:
            //target order is half the amount of startOrder
            if (posOrders.checkIfOrderFilled(startOrderId)) {
                targetOrderId = posOrders.placeNewOrder(firstExitOrderType, entryTargetPrice, (entryTargetPrice + interval), targetOrderAmount);
                status = Status.STARTFILLED;
                //if the second exit has been filled, so the trailing stop must have been filled too because it is always closer
                //to the bid/ask than the second exit - we need to re-enable setting the trailing stop:
                trailingStopTriggered = false;
                trailingStopPrice = exitOrderPrice;
                return;
            }
        }
        if (status == Status.STARTFILLED) {
            //check if the target has been filled. If it has, create the exit order:
            //exit order half of firstOrder - should make position 0
            if (posOrders.checkIfOrderFilled(targetOrderId)) {
                exitOrderId = posOrders.placeNewOrder(secondExitOrderType, entryTargetPrice, exitOrderPrice, exitOrderAmount);
                status = Status.TARGETFILLED;
                return;
            }
        }
        if (status == Status.TARGETFILLED) {
            //the target has been filled, so process the trailing stop:
            processTrailingStop();
            //if target was filled then exit order was placed.
            //check if it was filled. If it has, then the poosition should be flat now,
            // so make a STOP BUY order back at the initial entry price:
            if (posOrders.checkIfOrderFilled(exitOrderId)) {
                startOrderId = posOrders.placeNewOrder(returnOrderType, entryTargetPrice, entryTargetPrice, startOrderAmount);

                status = Status.RUNNING;
                return;
            }
        }
    }

    private synchronized void processTrailingStop() {
        if(trailingStopOrderAmount == 0) return;

        //send the first trailing stop order if it hasn't been done yet:
        //trailingStopPrice has been set to exitOrderPrice in constructor
        if(!trailingStopTriggered){
            System.out.println("Placing first trailing stop");
            trailingStopOrderId = posOrders.placeNewOrder(secondExitOrderType, entryTargetPrice, trailingStopPrice, trailingStopOrderAmount);
            trailingStopTriggered = true;
        }

        //if the trailing stop has been filled, return:
        if (posOrders.checkIfOrderFilled(trailingStopOrderId)) {
            return;
        }

        // dynamically determine the price for the trailing stop - halfway between current bid/ask and exitprice
        //if it needs to be adjusted, cancel the old trailing stop order and place a new one:

            //If entry is BUYLMT or BUYSTP then trailing stop above entryPrice - interval
            if (entryOrderType == MikeOrder.MikeOrderType.BUYLMT || entryOrderType == MikeOrder.MikeOrderType.BUYSTP){

                int trailingStopShouldBe = ((posOrders.priceServer.getBidPrice() + exitOrderPrice - (interval/2))/2);
                if(fixedTrailingStop) trailingStopShouldBe = (posOrders.priceServer.getBidPrice() - (interval*2));

                if(trailingStopShouldBe > trailingStopPrice){
                    posOrders.cancelOrder(trailingStopOrderId);
                    trailingStopOrderId = posOrders.placeNewOrder(secondExitOrderType, entryTargetPrice, trailingStopShouldBe, trailingStopOrderAmount);
                    trailingStopPrice = trailingStopShouldBe;
                }
            }

            //If entry is SELLLMT or SELLSTP then trailing stop below entryPrice + interval
            if (entryOrderType == MikeOrder.MikeOrderType.SELLLMT || entryOrderType == MikeOrder.MikeOrderType.SELLSTP){

                int trailingStopShouldBe = ((posOrders.priceServer.getAskPrice() + exitOrderPrice)/2) - interval;//interval negative for sell entry orders
                if(fixedTrailingStop) trailingStopShouldBe = (posOrders.priceServer.getAskPrice() - (interval*2));

                if(trailingStopShouldBe < trailingStopPrice){
                    posOrders.cancelOrder(trailingStopOrderId);
                    trailingStopOrderId = posOrders.placeNewOrder(secondExitOrderType, entryTargetPrice, trailingStopShouldBe, trailingStopOrderAmount);
                    trailingStopPrice = trailingStopShouldBe;
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
    public int getEntryPrice() {
        return entryTargetPrice;
    }

    @Override
    public MikePosOrders getMikePosOrders() {
        return posOrders;
    }

    public int getEntryTargetPrice() {
        return entryTargetPrice;
    }


    @Override
    public String toString() {
        String description = "SimpleStepperAlgo" + entryOrderType + " at price: ";
        description = description + ("" + entryTargetPrice + " on: " + posOrders.toString());
        description = description + (" status: " + status);
        return description;
    }


}
