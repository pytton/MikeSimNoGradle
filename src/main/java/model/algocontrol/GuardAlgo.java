package main.java.model.algocontrol;

import main.java.model.MikeSimLogger;
import main.java.model.positionsorders.MikePosOrders;

abstract class GuardAlgo extends BaseAlgo {
    //this defines minimum distance between zeroprofitpoint and bid/ask price that is to be maintained by this algo
    protected int guardBuffer = 10;

    //if current bid/ask price gets this close or less to zeroProfitPoint, algo will stop and enter FAILED status
    protected int minimumOrderFireDistance = 4;
    MikePosOrders monitored; //these are the ones being monitored
    MikePosOrders orderTarget; //this is where orders are sent to - can be same as guardedPosOrders
    Status status;
    protected int previousZeroPP = 0;
    protected long orderNeverCreatedCode = -5; //orderIds have positive numbers
    protected long guardOrderId = orderNeverCreatedCode;
//    private boolean hasDirectionChanged = false; //used by process()

//    protected enum Status {
//        CREATED,
//        RUNNING,
//        FAILED,
//        SUSPENDED
//    }

    protected abstract void processCREATED();

    /**
     * if position is long and bid price falls below zeroProfitPoint, algo has failed
     * @return
     */
    protected abstract boolean checkLongPositionFailed();

    /**
     * if position is short and ask price goes above zeroProfitPoint, algo has failed
     * @return
     */
    protected abstract boolean checkShortPositionFailed();

    /**
     *  send a stop order for an amount that if executed will move the ZeroProfitPoint to
     *  be at a distance defined by guardBuffer
     */
    protected abstract void processGuardOrder();

    @Override
    synchronized public void process() throws Exception {
        try {
            if (status == Status.CREATED) processCREATED();
            if (status == Status.RUNNING) processRUNNING();
            if (status == Status.FAILED) processFAILED();
            if (status == Status.SUSPENDED) processSUSPENDED();
        } catch (Exception e) {
            MikeSimLogger.addLogEvent("Exception in GuardAlgoDown");
            e.printStackTrace();
        }
    }

    synchronized public void suspend(){
        orderTarget.cancelOrder(guardOrderId);
        status = Status.SUSPENDED;
    }

    public void cancelOrdersAndRestart() {
        orderTarget.cancelOrder(guardOrderId);
        status = Status.CREATED;
    }

    protected void processRUNNING() {
        //todo: check if direction of monitored has changed:

        //check if position is empty:
        if (monitored.getTotalOpenAmount() == 0) {
            cancelOrdersAndRestart();
            return;
        }

        //if position is long check for changes and send orders:
        if (monitored.getTotalOpenAmount() > 0) {
            if (!checkLongPositionFailed()) commonProcessLongAndShort();
        }

        //if position is short check for changes and send orders
        if(monitored.getTotalOpenAmount() < 0){
            if (!checkShortPositionFailed()) commonProcessLongAndShort();
        }
    }

    protected abstract void processFAILED();

    protected void processSUSPENDED() {

    }

    //TODO: FINISH THIS
    protected void processPositionDirectionChanged() throws Exception {

        MikeSimLogger.addLogEvent("Change of direction in GuardAlgoDown NOT IMPLEMENTED!!");
        MikeSimLogger.addLogEvent("Change of direction in GuardAlgoDown NOT IMPLEMENTED!!");
        MikeSimLogger.addLogEvent("Change of direction in GuardAlgoDown NOT IMPLEMENTED!!");
        MikeSimLogger.addLogEvent("Change of direction in GuardAlgoDown NOT IMPLEMENTED!!");
        MikeSimLogger.addLogEvent("Change of direction in GuardAlgoDown NOT IMPLEMENTED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        throw new Exception("Error in GuardAlgoDown!");
    }


    /**
     * Returns distance of bid price from zeroProfitPoint if position long
     * or ask price from zeroProfitPoint if position short.
     * Assumes distance is measured in cents, returns -5 if zeroProfitPoint is null
     * or position is flat (neither long nor short)
     * @return
     */
    protected Integer checkDistance(){
        int distance = -5;
        try {
            Double zeroProfitPoint = monitored.getZeroProfitPoint();
            if (zeroProfitPoint == null) return distance;

            //if position is LONG do this:
            if(monitored.getTotalOpenAmount() > 0){
                int bidPrice = monitored.getBidPrice();
                //assumes distance is measured in cents
                distance = (bidPrice - zeroProfitPoint.intValue());

    //            MikeSimLogger.addLogEvent("ZeroPP is: " + zeroProfitPoint.intValue() + " bid price: " + bidPrice + " Distance is: " + distance);
            }

            //if position is SHORT do this:
            if(monitored.getTotalOpenAmount() < 0){
                int askPrice = monitored.getAskPrice();
                distance =  zeroProfitPoint.intValue() - askPrice;
            }
        } catch (Exception e) {
            MikeSimLogger.addLogEvent("Exception in GuardAlgo.checkDistance!");
            e.printStackTrace();
        }

        return distance;
    }

    protected boolean isGuardOrderFilled(){
        try {
//            MikeSimLogger.addLogEvent("Checking if orders filled");
            if (guardOrderId == orderNeverCreatedCode) return false;
            if(orderTarget.checkIfOrderFilled(guardOrderId)) {
//                MikeSimLogger.addLogEvent("order has been filled");
                return true;}
        } catch (Exception e) {
//            MikeSimLogger.addLogEvent("Exception in isGuardOrderfilled");
            e.printStackTrace();
        }
        return false;
    }

    public String getStatus() {
        if(status != null) return status.toString();
        return "Unknown";
    }

    @Override
    synchronized public void cancel() {
        MikeSimLogger.addLogEvent("Cancelling GuardAlgo NOT IMPLEMENTED! SUSPENDING IT INSTEAD!");
        cancelOrdersAndRestart();
        status = Status.SUSPENDED;
    }

    @Override
    public int getEntryPrice() {
        return 0;
    }

    /**
     * CAUTION! This algo operates on two different MikePosOrders - monitored and orderTarget
     * this returns the one that is being monitored. orders might be sent here or to a different one!
     *
     * @return MikePosOrders being monitored by algo
     */
    @Override
    public MikePosOrders monitoredMikePosOrders() {
        return monitored;
    }

    @Override
    public String toString() {
        String description = "GuardAlgo monitoring " + monitored;
        description = description + (" distance set: " + guardBuffer);
        description = description + (" status: " + status);
        return description;
    }

    protected void commonProcessLongAndShort() {
        //check if order has been filled, if it has, send a new one:
        if (isGuardOrderFilled()) {
            MikeSimLogger.addLogEvent("Guard order filled");
            processGuardOrder();
            return;
        }

        //check if zeroProfitPoint has changed
        if (monitored.getZeroProfitPoint().intValue() != previousZeroPP) {
            //cancel the guard order and send a new one:
            orderTarget.cancelOrder(guardOrderId);
            processGuardOrder();
        }
    }
}
