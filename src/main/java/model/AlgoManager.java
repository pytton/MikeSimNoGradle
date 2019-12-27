package main.java.model;

import main.java.model.mikealgos.ComplexScalperAlgoUp1;
import main.java.model.mikealgos.MikeAlgo;
import main.java.model.mikealgos.ScalperAlgoUp1;
import main.java.model.mikealgos.StepperAlgoUp1;
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

    public void createScalperAlgo1(MikePosOrders posOrders, int lowPrice, int highPrice, int amount){
        System.out.println("Creating ScalperAlgoUp1 for MikePosOrders " + posOrders.getName());
        ScalperAlgoUp1 algo = new ScalperAlgoUp1(posOrders, lowPrice, highPrice, amount );
        algoSet.add(algo);
    }

    public void createStepperAlgoUp1(MikePosOrders posOrders, int startPrice, int interval, int amount) {


        StepperAlgoUp1 algo = new StepperAlgoUp1(posOrders, startPrice, interval, amount);
        algoSet.add(algo);

    }

    public void createComplexScalperAlgoUp1(MikePosOrders posOrders, int lowerTarget, int interval, int howManyScalpers, int amount) {
        ComplexScalperAlgoUp1 algo = new ComplexScalperAlgoUp1(posOrders, lowerTarget, interval, howManyScalpers, amount);
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
