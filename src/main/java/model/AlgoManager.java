package main.java.model;

import main.java.model.mikealgos.ComplexScalperAlgoUp1;
import main.java.model.mikealgos.MikeAlgo;
import main.java.model.mikealgos.ScalperAlgo1;
import main.java.model.mikealgos.StepperAlgoUp1;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

import java.util.HashSet;
import java.util.Set;

/**
 * Creates, stores and manages all the trading algos available
 */
public class AlgoManager{

    MainModelThread model;
    Set<MikeAlgo> algoSet;

    public AlgoManager(MainModelThread model) {
        this.model = model;
        algoSet = new HashSet<>();
    }

    public void createScalperAlgo1(MikePosOrders posOrders, int entryPrice, int targetPrice, int orderAmount, MikeOrder.MikeOrderType entry){
        System.out.println("Creating ScalperAlgo1 for MikePosOrders " + posOrders.getName());
        ScalperAlgo1 algo = new ScalperAlgo1(posOrders, entryPrice, targetPrice, orderAmount, entry );
        algoSet.add(algo);
    }

    public void createStepperAlgoUp1(MikePosOrders posOrders, int startPrice, int interval, int amount) {


        StepperAlgoUp1 algo = new StepperAlgoUp1(posOrders, startPrice, interval, amount);
        algoSet.add(algo);

    }

    public void createComplexScalperAlgoUp1(MikePosOrders posOrders, int lowerTarget, int interval, int howManyScalpers, int amount, MikeOrder.MikeOrderType entry) {
        ComplexScalperAlgoUp1 algo = new ComplexScalperAlgoUp1(posOrders, lowerTarget, interval, howManyScalpers, amount, entry);
        algoSet.add(algo);
    }

    public void processAllAlgos(){
        for (MikeAlgo algo : algoSet) {
            algo.process();
        }
    }

    public void cancelAlgo(MikeAlgo algo) {

    }

}
