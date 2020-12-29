package main.java.model.positionsorders;

public class PosOrdersManager {

    /**
     * Use this to take profits or move positions between different MikePosOrder objects.
     * Takes a given amount of traded instruments and closed profit/loss
     * from source MikePosOrders to target MikePosOrders
     * returns false in case of error in parameters
     * @param source take from this MikePosOrders
     * @param target put into this MikePosOrders
     * @param amountOpenPositionToTransfer negative number for short position, positive for long
     * @param closedPLToTransfer
     */
    public static synchronized boolean transferAmountAndClosedPL(MikePosOrders source, MikePosOrders target,
                                          int amountOpenPositionToTransfer,
                                          int closedPLToTransfer){
        //check for valid parameters:
        if(source == null || target == null) return false;
        if(source == target) return false;

        //create two symmetrical MikePositions and add them to souce and target MikePosOrders at current bid price:

        int price = source.getBidPrice();
        int open_amount = amountOpenPositionToTransfer;
        int closed_pl = closedPLToTransfer;int total_pl = 0;

        MikePosition putIntoThis = new MikePosition (price, open_amount, 0, closed_pl, 0);
        MikePosition takeFromThis = new MikePosition(price, (open_amount) * -1, 0, (closed_pl) * -1, 0 );

        source.addToMikePosition(takeFromThis);
        target.addToMikePosition(putIntoThis);

        return true;
    }
}
