package main.java.model.mikealgos;

import main.java.model.MikeSimLogger;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

/**
 * NOT IMPLEMENTED YET.
 * Creates a 'guard' for a given MikePosOrders:
 * If the current ZEROPROFITPOINT is below the current bid price - then it will submit a SELLSTP order
 * in an amount that if filled will move the ZEROPROFITPOINT lower so that it is never touched.
 * The distance between the ZEROPROFITPOINT and the bid price is specified as GUARDBUFFER
 */
public class GuardAlgo extends BaseAlgo {
    //this defines minimum distance between zeroprofitpoint and bid/ask price that is to be maintained by this algo
    private int guardBuffer = 15;

    //if current bid/ask price gets this close or less to zeroProfitPoint, algo will stop and enter FAILED status
    private int minimumOrderFireDistance = 4;
    MikePosOrders monitored; //these are the ones being monitored
    MikePosOrders orderTarget; //this is where orders are sent to - can be same as guardedPosOrders
    Status status;
    private long orderNeverCreatedCode = -5; //orderIds have positive numbers
    private long guardOrderId = orderNeverCreatedCode;
//    private boolean hasDirectionChanged = false; //used by process()

    private enum Status {
        CREATED,
        RUNNING,
        FAILED,
        SUSPENDED
    }

    protected GuardAlgo(MikePosOrders monitored, MikePosOrders orderTarget, int guardBuffer) {
        this.monitored = monitored;
        this.orderTarget = orderTarget;
        if (guardBuffer > 0) this.guardBuffer = guardBuffer;
        status = Status.CREATED;
    }

    @Override
    synchronized public void process() throws Exception {
        try {
            if (status == Status.CREATED) processCREATED();
            if (status == Status.RUNNING) processRUNNING();
            if (status == Status.FAILED) processCFAILED();
            if (status == Status.SUSPENDED) processSUSPENDED();
        } catch (Exception e) {
            MikeSimLogger.addLogEvent("Exception in GuardAlgo");
            e.printStackTrace();
        }
    }

    synchronized public void restart(){
        if (status != Status.RUNNING) cancelOrdersAndRestart();
    }

    synchronized public void suspend(){
        orderTarget.cancelOrder(guardOrderId);
        status = Status.SUSPENDED;
    }


    private void processCREATED(){
        if(monitored.getTotalOpenAmount() == 0) return; //if position is zero algo can't work
        //When the bid/ask price gets far enough from the zeroProfitPointk
        //send the first order and change status to RUNNING
        if(checkDistance() > guardBuffer +1){
            //create first order and pass it to the target
            sendNewGuardOrder();
            status = Status.RUNNING;
            MikeSimLogger.addLogEvent("GuardAlgo monitoring " + monitored.toString() + " changing status to runnong");
        }
    }


    private int previousZeroPP = 0;
    private void processRUNNING() {
        //todo: check if direction of monitored has changed:

        //check if position is empty:
        if (monitored.getTotalOpenAmount() == 0) {
            cancelOrdersAndRestart();
            return;
        }

        //check if position is long:
        if (monitored.getTotalOpenAmount() > 0) {
            processLongPosition();
        }

        //check if position is short:
        if(monitored.getTotalOpenAmount() < 0){
            processShortPosition();
        }

        return;
    }

    private void cancelOrdersAndRestart() {
        orderTarget.cancelOrder(guardOrderId);
        status = Status.CREATED;
    }

    private void processCFAILED() {
        //if for some reason, after the first order is placed, the distance between bid/ask
        //price and zeroProfitPoint gets below a certain margin, wait until it changes
        //and switch the status to RUNNING

        if (checkDistance() > minimumOrderFireDistance) status = Status.RUNNING;



    }

    private void processSUSPENDED() {

    }

    private void processLongPosition(){
        //check if failed
        if(monitored.getBidPrice() < monitored.getZeroProfitPoint().intValue()) {
            status = Status.FAILED;
            return;}

        //check if order has been filled, if it has, send a new one:
        if(isGuardOrderFilled()){
            MikeSimLogger.addLogEvent("Guard order filled");
            sendNewGuardOrder();
            return;
        }

        //check if zeroProfitPoint has changed
        if(monitored.getZeroProfitPoint().intValue() != previousZeroPP){
            //cancel the guard order and send a new one:
            orderTarget.cancelOrder(guardOrderId);
            sendNewGuardOrder();
        }
    }

    private void processShortPosition(){
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED");
    }

    //TODO: FINISH THIS
    private void processPositionDirectionChanged() throws Exception {

        MikeSimLogger.addLogEvent("Change of direction in GuardAlgo NOT IMPLEMENTED!!");
        MikeSimLogger.addLogEvent("Change of direction in GuardAlgo NOT IMPLEMENTED!!");
        MikeSimLogger.addLogEvent("Change of direction in GuardAlgo NOT IMPLEMENTED!!");
        MikeSimLogger.addLogEvent("Change of direction in GuardAlgo NOT IMPLEMENTED!!");
        MikeSimLogger.addLogEvent("Change of direction in GuardAlgo NOT IMPLEMENTED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        throw new Exception("Error in GuardAlgo!");



    }


    /**
     * Assumes distance is measured in cents,
     * Returns -5 if zeroProfitPoint is null
     * or position is flat (neither long nor short)
     * @return
     */
    private Integer checkDistance(){
        int distance = -5;
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

        return distance;
    }

    private boolean isGuardOrderFilled(){
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

    private void sendNewGuardOrder(){
        //send a stop order for an amount that if executed will move the ZeroProfitPoint to
        //be at a distance defined by guardBuffer

        //todo: do the math and implement it for testing just make the order 1/5 of current position:

        //cancel existing guardOrder:
        orderTarget.cancelOrder(guardOrderId);

        int zeroProfitPoint =  orderTarget.getZeroProfitPoint().intValue();   // (int)(orderTarget.getZeroProfitPoint() *100);

        int price = zeroProfitPoint + guardBuffer;
        int orderAmount = monitored.getTotalOpenAmount() / 5 ;

        if(orderAmount < 1){
            status = Status.FAILED;
            return;
        }

        MikeOrder.MikeOrderType ordertype = MikeOrder.MikeOrderType.SELLSTP;

        guardOrderId = orderTarget.placeNewOrder(ordertype, price, price, orderAmount);
        previousZeroPP = zeroProfitPoint;
    }

    public String getStatus() {
        if(status != null) return status.toString();
        return "Unknown";
    }

    @Override
    public void cancel() {
        MikeSimLogger.addLogEvent("cancel for GuardAlgo NOT IMPLEMENTED!");
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
}
