package main.java.model.mikealgos;

import main.java.model.positionsorders.MikePosOrders;

/**
 * NOT IMPLEMENTED YET.
 * Creates a 'guard' for a given MikePosOrders:
 * If the current ZEROPROFITPOINT is below the current bid price - then it will submit a SELLSTP order
 * in an amount that if filled will move the ZEROPROFITPOINT lower so that it is never touched.
 * The distance between the ZEROPROFITPOINT and the bid price is specified as GUARDBUFFER
 */
public class GuardAlgo extends BaseAlgo {
    private int guardBuffer = 5;
    MikePosOrders guardedPosOrders;
    Status status;
    private long guardOrderId;
    private enum Status{
        CREATED,
        WAITING,
        ARMED,
        ORDERFILLED,
    }
    public GuardAlgo(MikePosOrders posOrders, int guardBuffer) {
        guardedPosOrders = posOrders;
        if(guardBuffer > 0) this.guardBuffer = guardBuffer;
        status = Status.CREATED;
    }

    @Override
    public void process() {
        if(status == Status.CREATED){
            //check if the current bid price is above the zeroprofitpoint of the MikePosOrders that is being guarded:
            // if it is NULL it means MikePosOrders has a flat position so there is nothing to guard
            if(guardedPosOrders.getZeroProfitPoint() == null) return;
            else  {
                //if guardedPosOrders is not flat make sure the position is long:
                if(guardedPosOrders.getTotalOpenAmount() < 0) {status = Status.CREATED; return;}
                if(guardedPosOrders.getZeroProfitPoint() > (guardedPosOrders.getBidPrice() + guardBuffer)){

                }
            }
        }

    }

    @Override
    public void cancel() {

    }

    @Override
    public int getEntryPrice() {
        return 0;
    }

    @Override
    public MikePosOrders getMikePosOrders() {
        return null;
    }
}
