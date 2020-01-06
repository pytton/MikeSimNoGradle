package main.java.model.mikealgos;

import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

import java.util.HashSet;
import java.util.Set;

public class ComplexScalperAlgoUp1 extends BaseAlgo {

    private Set<SimpleScalperAlgo> algoSet;


    /**
     * If entry is BUYLMT or BUYSTP then interval has to be a positive value
     * If entry is SELLLMT or SELLSTP then interval has to be negative.
     * Otherwise this creates a neverending monster
     * Interval cannot be zero
     * Above enforced by constructor
     * @param posOrders
     * @param lowerTarget
     * @param interval
     * @param howManyScalpers
     * @param amount
     * @param entry
     */
    public ComplexScalperAlgoUp1(MikePosOrders posOrders, int lowerTarget, int interval, int howManyScalpers, int amount, MikeOrder.MikeOrderType entry) {
        algoSet = new HashSet<>();

        //If entry is BUYLMT or BUYSTP then interval has to be a positive value
        if (entry == MikeOrder.MikeOrderType.BUYLMT || entry == MikeOrder.MikeOrderType.BUYSTP){
            if (interval < 0) {
                interval = interval * -1;
            }
            if (interval == 0) interval = 1;
        }

        //If entry is SELLLMT or SELLSTP then interval has to be negative.
        if (entry == MikeOrder.MikeOrderType.SELLLMT || entry == MikeOrder.MikeOrderType.SELLSTP){
            if (interval > 0) {
                interval = interval * -1;
            }
            if (interval == 0) interval = -1;
        }

        for (int i = 0; i<howManyScalpers;i++ ) {
            SimpleScalperAlgo algo = new SimpleScalperAlgo(posOrders, lowerTarget, (lowerTarget +interval+(i*interval)), (amount / howManyScalpers), entry);
            algoSet.add(algo);
        }

    }

    @Override
    public void process() {
        //todo: write this

        for (SimpleScalperAlgo algo :algoSet) {
            algo.process();
        }
    }

    @Override
    public void cancel() {
        //todo: write this

        for (SimpleScalperAlgo algo :algoSet) {
            algo.cancel();
        }
    }

}
