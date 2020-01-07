package main.java.model;

import javafx.collections.FXCollections;
import main.java.model.mikealgos.ComplexScalperAlgo1;
import main.java.model.mikealgos.MikeAlgo;
import main.java.model.mikealgos.SimpleScalperAlgo;
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
    Set<MikeAlgo> cancelledAlgoSet;
    //we need this if we want to cancel algos of specific type:
    Set<SimpleScalperAlgo> simpleScalperAlgoSet;
    Set<ComplexScalperAlgo1> complexScalperAlgo1Set;

    public AlgoManager(MainModelThread model) {
        this.model = model;
        //todo: does this work?
        algoSet = FXCollections.observableSet();
        cancelledAlgoSet = FXCollections.observableSet();
        simpleScalperAlgoSet = FXCollections.observableSet();
        complexScalperAlgo1Set = FXCollections.observableSet();
    }

    synchronized public void createScalperAlgo1(MikePosOrders posOrders, int entryPrice, int targetPrice, int orderAmount, MikeOrder.MikeOrderType entry){
        System.out.println("Creating SimpleScalperAlgo for MikePosOrders " + posOrders.getName());
        SimpleScalperAlgo algo = new SimpleScalperAlgo(posOrders, entryPrice, targetPrice, orderAmount, entry );
        algoSet.add(algo);
        simpleScalperAlgoSet.add(algo);
    }

    /**
     * This will cancel all SimpleScalperAlgos that have been initialized with entryPrice
     * @param entryPrice
     */
    synchronized public void cancelAllSimpleScalperAlgosAtPrice(int entryPrice) {
        Set<MikeAlgo> algosToCancel = new HashSet<>();
        //find all algos initialized with entryPrice and cancel them:
        for (SimpleScalperAlgo algo : simpleScalperAlgoSet) {
            if (algo.getEntryTargetPrice() == entryPrice) {
                algo.cancel();
                algosToCancel.add(algo);
            }
        }

        //housekeeping - remove cancelled algos from active algos and add them to the set of cancelled algos:
        algoSet.removeAll(algosToCancel);
        cancelledAlgoSet.addAll(algosToCancel);
    }

    synchronized public void cancelAllComplexScalperAlgo1sAtPrice(int entryPrice) {
        Set<MikeAlgo> algosToCancel = new HashSet<>();
        //find all algos initialized with entryPrice and cancel them:
        for (ComplexScalperAlgo1 algo : complexScalperAlgo1Set) {
            if (algo.getEntryTargetPrice() == entryPrice) {
                algo.cancel();
                algosToCancel.add(algo);
            }
        }

        //housekeeping - remove cancelled algos from active algos and add them to the set of cancelled algos:
        algoSet.removeAll(algosToCancel);
        cancelledAlgoSet.addAll(algosToCancel);
    }

    synchronized public void createStepperAlgoUp1(MikePosOrders posOrders, int startPrice, int interval, int amount, MikeOrder.MikeOrderType orderType) {
        StepperAlgoUp1 algo = new StepperAlgoUp1(posOrders, startPrice, interval, amount, orderType);
        algoSet.add(algo);
    }

    public void createComplexScalperAlgoUp1(MikePosOrders posOrders, int lowerTarget, int interval, int howManyScalpers, int amount, MikeOrder.MikeOrderType entry) {
        ComplexScalperAlgo1 algo = new ComplexScalperAlgo1(posOrders, lowerTarget, interval, howManyScalpers, amount, entry);
        algoSet.add(algo);
        complexScalperAlgo1Set.add(algo);
    }

    public void processAllAlgos(){
        for (MikeAlgo algo : algoSet) {
            algo.process();
        }
    }

    public void cancelAlgo(MikeAlgo algo) {

    }

}
