package main.java.model.mikealgos;

import main.java.model.positionsorders.MikePosOrders;

public interface MikeAlgo {
    /**
     * This has to be called in a loop from the outside to make the algo work
     */
    public void process();
    public void cancel();

    /**
     * Returns the MikePosOrders on which the algo is operating(submitting orders)
     * @return
     */
    public MikePosOrders getMikePosOrders();
}
