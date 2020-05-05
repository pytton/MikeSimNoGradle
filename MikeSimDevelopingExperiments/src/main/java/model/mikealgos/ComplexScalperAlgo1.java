package main.java.model.mikealgos;

import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

import java.util.HashSet;
import java.util.Set;

public class ComplexScalperAlgo1 extends BaseAlgo {

    private Set<SimpleScalperAlgo> algoSet;
    private int entryTargetPrice;
    private MikePosOrders posOrders;
    private MikeOrder.MikeOrderType entryOrderType;


    /**
     * If entry is BUYLMT or BUYSTP then interval has to be a positive value
     * If entry is SELLLMT or SELLSTP then interval has to be negative.
     * Otherwise this creates a neverending monster
     * Interval cannot be zero
     * Above enforced by constructor
     * @param posOrders
     * @param entryTarget
     * @param interval
     * @param howManyScalpers
     * @param amount
     * @param entry
     */
    public ComplexScalperAlgo1(MikePosOrders posOrders, int entryTarget, int interval, int howManyScalpers, int amount, MikeOrder.MikeOrderType entry) {
        algoSet = new HashSet<>();
        this.entryTargetPrice = entryTarget;
        this.posOrders = posOrders;
        this.entryOrderType = entry;


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
            SimpleScalperAlgo algo = new SimpleScalperAlgo(posOrders, entryTarget, (entryTarget +interval+(i*interval)), (amount / howManyScalpers), entry);
            algoSet.add(algo);
        }
    }

    @Override
    public synchronized void process() {
        for (SimpleScalperAlgo algo :algoSet) {
            algo.process();
        }
    }

    @Override
    public synchronized void cancel() {
        for (SimpleScalperAlgo algo :algoSet) {
            algo.cancel();
        }
        //we can do this because everything in algoSet has been cancelled anyway and no need to process it anymore:
        algoSet.clear();
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
        String description = "ComplexSclprAlgo1" + entryOrderType + " at price: ";
        description = description + ("" + entryTargetPrice + " on: " + posOrders.toString());
        return description;
    }
}