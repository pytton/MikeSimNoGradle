package main.java.model.mikealgos;

import main.java.model.positionsorders.MikePosOrders;

public abstract class BaseAlgo {
    /**
     * This has to be called in a loop from the outside to make the algo work
     */
    abstract public void process();
    abstract public void cancel();
    abstract public int getEntryPrice();

    /**
     * Returns the MikePosOrders on which the algo is operating(submitting orders)
     * @return
     */
    abstract public MikePosOrders getMikePosOrders();

    public enum status{
        CREATED,
        RUNNING,
        STOPPED
    }
}
