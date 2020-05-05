package main.java.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import main.java.model.mikealgos.ComplexScalperAlgo1;
import main.java.model.mikealgos.MikeAlgo;
import main.java.model.mikealgos.SimpleScalperAlgo;
import main.java.model.mikealgos.SimpleStepperAlgo;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Creates, stores and manages all the trading algos available
 */
public class AlgoManager{

    MainModelThread model;


//    public ObservableSet<MikeAlgo> algoSet;

    public ObservableList<MikeAlgo> algoSet;

//    public Set<MikeAlgo> algoSet;
    Set<MikeAlgo> cancelledAlgoSet;
    //we need this if we want to cancel algos of specific type:
    Set<SimpleScalperAlgo> simpleScalperAlgoSet;
    Set<ComplexScalperAlgo1> complexScalperAlgo1Set;
    Set<SimpleStepperAlgo> simpleStepperAlgoSet;


    public AlgoManager(MainModelThread model) {
        this.model = model;
        algoSet = FXCollections.observableArrayList();
        cancelledAlgoSet = FXCollections.observableSet();
        simpleScalperAlgoSet = FXCollections.observableSet();
        complexScalperAlgo1Set = FXCollections.observableSet();
        simpleStepperAlgoSet = FXCollections.observableSet();
    }

    /**
     * This will cancel all working algos irrespective of their MikePosOrders or price:
     */
    synchronized public void cancelAllAlgosGlobally() {
        for (MikeAlgo algo : algoSet) {
            algo.cancel();
        }
        cancelledAlgoSet.addAll(algoSet);
        algoSet.clear();
    }

    /**
     * Cancels all algos operating on a given MikePosOrders
     */
    synchronized public void cancelAllAlgosInMikePosOrders(MikePosOrders posOrders) {
        Set<MikeAlgo> algosToCancel = new HashSet<>();
        for (MikeAlgo algo : algoSet) {
            if (algo.getMikePosOrders() == posOrders) {
                algo.cancel();
                algosToCancel.add(algo);
            }
        }
        algoSet.removeAll(algosToCancel);
        cancelledAlgoSet.addAll(algosToCancel);

    }

    synchronized public void createScalperAlgo1(MikePosOrders posOrders, int entryPrice, int targetPrice, int orderAmount, MikeOrder.MikeOrderType entry){

        //todo: experimenting
        System.out.println("Experimenting. Creating Simple Scalper Algo in child of MikePosOrders:");

        MikePosOrders child = posOrders.createChildPosOrders();
        SimpleScalperAlgo algo = new SimpleScalperAlgo(child, entryPrice, targetPrice, orderAmount, entry );


//        System.out.println("Creating SimpleScalperAlgo for MikePosOrders " + posOrders.getName());
//        SimpleScalperAlgo algo = new SimpleScalperAlgo(posOrders, entryPrice, targetPrice, orderAmount, entry );

        algoSet.add(algo);
        simpleScalperAlgoSet.add(algo);
    }

    /**
     * This will cancel all SimpleScalperAlgos that have been initialized with entryPrice
     * @param entryPrice
     */
    synchronized public void cancelAllSimpleScalperAlgosAtPrice(int entryPrice, MikePosOrders posOrders) {
        Set<MikeAlgo> algosToCancel = new HashSet<>();
        //find all algos initialized with entryPrice and cancel them:
        for (SimpleScalperAlgo algo : simpleScalperAlgoSet) {
            if (algo.getEntryTargetPrice() == entryPrice && algo.getMikePosOrders() == posOrders) {
                algo.cancel();
                algosToCancel.add(algo);
            }
        }

        //housekeeping - remove cancelled algos from active algos and add them to the set of cancelled algos:
        algoSet.removeAll(algosToCancel);
        cancelledAlgoSet.addAll(algosToCancel);
    }


    synchronized public void createComplexScalperAlgoUp1(MikePosOrders posOrders, int lowerTarget, int interval, int howManyScalpers, int amount, MikeOrder.MikeOrderType entry) {
        ComplexScalperAlgo1 algo = new ComplexScalperAlgo1(posOrders, lowerTarget, interval, howManyScalpers, amount, entry);
        algoSet.add(algo);
        complexScalperAlgo1Set.add(algo);
    }

    synchronized public void cancelAllComplexScalperAlgo1sAtPrice(int entryPrice, MikePosOrders posOrders) {
        Set<MikeAlgo> algosToCancel = new HashSet<>();
        //find all algos initialized with entryPrice and cancel them:
        for (ComplexScalperAlgo1 algo : complexScalperAlgo1Set) {
            if (algo.getEntryTargetPrice() == entryPrice && algo.getMikePosOrders() == posOrders) {
                algo.cancel();
                algosToCancel.add(algo);
            }
        }

        //housekeeping - remove cancelled algos from active algos and add them to the set of cancelled algos:
        algoSet.removeAll(algosToCancel);
        cancelledAlgoSet.addAll(algosToCancel);
    }

    synchronized public void createSimpleStepperAlgo(MikePosOrders posOrders, int startPrice, int interval, int amount,
                                                     MikeOrder.MikeOrderType orderType, boolean smTrailingStop,
                                                     boolean fixedTrailingStop) {
        SimpleStepperAlgo algo = new SimpleStepperAlgo(posOrders, startPrice, interval, amount, orderType, smTrailingStop, fixedTrailingStop);
        algoSet.add(algo);
        simpleStepperAlgoSet.add(algo);
    }

    synchronized public void cancelAllSimpleStepperAlgosAtPrice(int entryPrice, MikePosOrders posOrders) {

        Set<MikeAlgo> algosToCancel = new HashSet<>();
        //find all algos initialized with entryPrice and cancel them:
        for (SimpleStepperAlgo algo : simpleStepperAlgoSet) {
            if (algo.getEntryTargetPrice() == entryPrice && algo.getMikePosOrders() == posOrders) {
                algo.cancel();
                algosToCancel.add(algo);
            }
        }

        //housekeeping - remove cancelled algos from active algos and add them to the set of cancelled algos:
        algoSet.removeAll(algosToCancel);
        cancelledAlgoSet.addAll(algosToCancel);
    }


    public synchronized void processAllAlgos(){
        for (MikeAlgo algo : algoSet) {
            algo.process();
        }
    }

    public synchronized void cancelAlgo(MikeAlgo algoToCancel) {

        algoToCancel.cancel();
        algoSet.remove(algoToCancel);
        cancelledAlgoSet.add(algoToCancel);

    }

    public synchronized void sortAlgosbyPrice() {

        //explanation here:
        //https://stackoverflow.com/questions/36228664/sorting-observablelistclass-by-value-in-ascending-and-descending-order-javaf
        // assuming there is a instance method Class.getScore that returns int
        // (other implementations for comparator could be used too, of course)
        Comparator<MikeAlgo> comparator = Comparator.comparingInt(MikeAlgo::getEntryPrice);

        comparator = comparator.reversed();
        FXCollections.sort(algoSet, comparator);

    }


    public ObservableList<MikeAlgo> getAlgoSet() {
        return algoSet;
    }
}
