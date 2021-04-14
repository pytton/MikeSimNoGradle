package main.java.model.algocontrol;

import main.java.model.MikeSimLogger;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

public class TrailingStopAlgo extends BaseAlgo{

    private Status status = Status.SUSPENDED;
    private int lastBidPrice;
    private int lastAskPrice;

    private MikeOrder.MikeOrderType orderType;
    private int trailingDistance = 10;
    private int orderAmount = 0;
    private MikePosOrders posOrders;
    private long trailingStopOrderNumber;

    protected TrailingStopAlgo(MikeOrder.MikeOrderType orderType, int orderAmount, int trailingDistance, MikePosOrders posOrders) throws Exception {
        if( !( (orderType == MikeOrder.MikeOrderType.BUYSTP) || (orderType == MikeOrder.MikeOrderType.SELLSTP) ) ){
            orderType = MikeOrder.MikeOrderType.SELLSTP;
            MikeSimLogger.addLogEvent("WARNING! Order submitted to TrailingStopAlgo is not a stop order! Setting it to SELL STOP!!!");
        }
        if(trailingDistance < 0){
            trailingDistance = trailingDistance * -1;
            MikeSimLogger.addLogEvent("WARNING! Negative trailing distance submitted to TrailingStopAlgo!" +
                    "Setting it to positive!");
        }
        if(orderAmount < 0){
            orderAmount = orderAmount * -1;
            MikeSimLogger.addLogEvent("WARNING! Negative order amount submitted to TrailingStopAlgo!" +
                    "Setting it to positive!");
        }
        if(posOrders == null){
            MikeSimLogger.addLogEvent("Cannot create TrailingStopAlgo supplying a null MikePosOrders! Aborting!");
            throw new Exception();
        }

        this.orderType = orderType;
        this.orderAmount = orderAmount;
        this.trailingDistance = trailingDistance;
        this.posOrders = posOrders;

        status = Status.CREATED;
    }

    @Override
    public void process() throws Exception {
        if((status == Status.STOPPED)
                || (status == Status.SUSPENDED)
        || (status == Status.CANCELLED) ) return;
        //create the first order:
        if(status == Status.CREATED){
            if(orderType == MikeOrder.MikeOrderType.SELLSTP){
                placeSELLorder();
                return;
            }
            if(orderType == MikeOrder.MikeOrderType.BUYSTP){
                placeBUYorder();
                return;
            }
        }
        if(status == Status.RUNNING){
            //if the order was filled, the algo is done and can be stopped:
            if(posOrders.checkIfOrderFilled(trailingStopOrderNumber)){
                MikeSimLogger.addLogEvent("Trailing Stop order filled. Cancelling Trailing Stop Algo in "
                + posOrders.getName());
                status = Status.CANCELLED;
                return;
            }

            //if it has not been filled, check if the bid/ask price has moved and if the order needs updating:
            if(orderType == MikeOrder.MikeOrderType.SELLSTP){
                if(posOrders.getBidPrice() > lastBidPrice){
                    posOrders.cancelOrder(trailingStopOrderNumber);
                    placeSELLorder();
                }
            }
            if(orderType == MikeOrder.MikeOrderType.BUYSTP){
                if(posOrders.getAskPrice() < lastAskPrice){
                    posOrders.cancelOrder(trailingStopOrderNumber);
                    placeBUYorder();
                }
            }
        }

        //finally, if order has been cancelled externally, cancel the algo and let user know:
        if(posOrders.getOrderServer().getMikeOrder(trailingStopOrderNumber).isCancelled()){
            MikeSimLogger.addLogEvent("Order cancelled outside of algo! Cancelling Trailing Stop Algo!");
            status = Status.CANCELLED;
        }

    }

    private void placeBUYorder() {
        trailingStopOrderNumber = posOrders.placeNewOrder(orderType, posOrders.getAskPrice() + trailingDistance,
                posOrders.getAskPrice() + trailingDistance, orderAmount );
        lastBidPrice = posOrders.getBidPrice();
        lastAskPrice = posOrders.getAskPrice();
        status = Status.RUNNING;
    }

    private void placeSELLorder() {
        trailingStopOrderNumber = posOrders.placeNewOrder(orderType, posOrders.getBidPrice() - trailingDistance,
                posOrders.getBidPrice() - trailingDistance, orderAmount );
        lastBidPrice = posOrders.getBidPrice();
        lastAskPrice = posOrders.getAskPrice();
        status = Status.RUNNING;
        return;
    }

    @Override
    public void cancel() {
        posOrders.cancelOrder(trailingStopOrderNumber);
        status = Status.CANCELLED;
        MikeSimLogger.addLogEvent("Trailing Stop Algo cancelled.");
    }

    @Override
    public int getEntryPrice() {
        return lastAskPrice;
    }

    @Override
    public MikePosOrders monitoredMikePosOrders() {
        return posOrders;
    }
}
