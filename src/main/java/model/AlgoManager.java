package main.java.model;

import main.java.model.mikealgos.MikeAlgo;
import main.java.model.mikealgos.ScalperAlgo1;
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
        System.out.println("Creating ScalperAlgo1 for MikePosOrders " + posOrders.getName());
        ScalperAlgo1 algo = new ScalperAlgo1(posOrders, lowPrice, highPrice, amount );
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
