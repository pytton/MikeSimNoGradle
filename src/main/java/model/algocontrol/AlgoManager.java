package main.java.model.algocontrol;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
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

    public ObservableList<BaseAlgo> algoSet;

    Set<BaseAlgo> cancelledAlgoSet;
    //we need this if we want to cancel algos of specific type:
    Set<SimpleScalperAlgo> simpleScalperAlgoSet;
    Set<ComplexScalperAlgo1> complexScalperAlgo1Set;
    Set<SimpleStepperAlgo> simpleStepperAlgoSet;
    Set<GuardAlgoDown> guardAlgoDownSet;
    private Set<GuardAlgoUp> guardAlgoUpSet;


    public AlgoManager(MainModelThread model) {
        this.model = model;
        algoSet = FXCollections.observableArrayList();
        cancelledAlgoSet = FXCollections.observableSet();
        simpleScalperAlgoSet = FXCollections.observableSet();
        complexScalperAlgo1Set = FXCollections.observableSet();
        simpleStepperAlgoSet = FXCollections.observableSet();
        guardAlgoDownSet = FXCollections.observableSet();
        guardAlgoUpSet = FXCollections.observableSet();
    }

    //todo:
    //this doesn't look right. eg if algo is a SimpleStepperAlgo it will not get removed
    //from     Set<SimpleScalperAlgo> simpleScalperAlgoSet; - is this supposed to be that way?
    public synchronized void cancelAlgo(BaseAlgo algoToCancel) {

        //cancelling GuardAlgos is problematic - can suspend them instead:
        if(algoToCancel instanceof GuardAlgo){
            algoToCancel.cancel();
            return;
        }

        MikeSimLogger.addLogEvent("Cancelling " + algoToCancel);

        algoToCancel.cancel();
        algoSet.remove(algoToCancel);
        cancelledAlgoSet.add(algoToCancel);

    }

    /**
     * This will cancel all working algos irrespective of their MikePosOrders or price:
     */
    synchronized public void cancelAllAlgosGlobally() {
        for (BaseAlgo algo : algoSet) {
            cancelAlgo(algo);
        }
    }

    /**
     * Cancels all algos operating on a given MikePosOrders
     */
    synchronized public void cancelAllAlgosInMikePosOrders(MikePosOrders posOrders) {
        Set<BaseAlgo> algosToCancel = new HashSet<>();
        for (BaseAlgo algo : algoSet) {
            if (algo.monitoredMikePosOrders() == posOrders) {
                algosToCancel.add(algo);
            }
        }

        for (BaseAlgo algo : algosToCancel) cancelAlgo(algo);
    }

    /**
     * not finished
     */
    synchronized public GuardAlgoUp createGuardAlgoUp(MikePosOrders monitoredPosOrders, MikePosOrders orderTargetPosOrders, int guardBuffer){
        GuardAlgoUp guardAlgoUp = null;

        try {
            guardAlgoUp = getGuardAlgoUPforMikePosOrders(monitoredPosOrders);

        } catch (Exception e) {
            MikeSimLogger.addLogEvent("Cannot create new GuardAlgoDown!");
            e.printStackTrace();
            return guardAlgoUp;
        }

        if(guardAlgoUp == null){
            guardAlgoUp = new GuardAlgoUp(monitoredPosOrders, orderTargetPosOrders, guardBuffer);

            algoSet.add(guardAlgoUp);
            guardAlgoUpSet.add(guardAlgoUp);}

        return guardAlgoUp;
    }

    /**
     * enforces creating only one GuardAlgoDown per one MikePosOrders
     * @param monitoredPosOrders
     * @param orderTargetPosOrders
     * @param guardBuffer
     * @return
     */
    synchronized public GuardAlgoDown createGuardAlgoDown(MikePosOrders monitoredPosOrders, MikePosOrders orderTargetPosOrders, int guardBuffer){
        //todo: finish this:

        GuardAlgoDown guardAlgoDown = null;

        try {
            guardAlgoDown = getGuardAlgoDOWNforMikePosOrders(monitoredPosOrders);

        } catch (Exception e) {
            MikeSimLogger.addLogEvent("Cannot create new GuardAlgoDown!");
            e.printStackTrace();
            return guardAlgoDown;
        }

        if(guardAlgoDown == null){
        guardAlgoDown = new GuardAlgoDown(monitoredPosOrders, orderTargetPosOrders, guardBuffer);

        algoSet.add(guardAlgoDown);
        guardAlgoDownSet.add(guardAlgoDown);}

        return guardAlgoDown;
    }

    /**
     * PROBLEM RELATED TO CANCELLING ALGOS
     * returns a GuardAlgoDown that is monitoring provided MikePosOrders.
     * if there is more than one, throws an Exception
     * @param posOrders
     * @return
     * @throws Exception
     */
    synchronized public GuardAlgoDown getGuardAlgoDOWNforMikePosOrders(MikePosOrders posOrders) throws Exception {
        GuardAlgoDown guardAlgoDownMonitoringPosOrders = null;

            int guardCount = 0;
            for (GuardAlgoDown guardAlgoDown : guardAlgoDownSet){
                if(guardAlgoDown.monitoredMikePosOrders() == posOrders) {
                    guardAlgoDownMonitoringPosOrders = guardAlgoDown;
                    guardCount++;
                }
                if(guardCount > 1) {
                    MikeSimLogger.addLogEvent("EXCEPTION in getGuardAlgoForMikePosOrders - more than one algo controlling " + posOrders.getName());
                    throw new Exception();
                }
            }

        return guardAlgoDownMonitoringPosOrders;
    }

    synchronized public GuardAlgoUp getGuardAlgoUPforMikePosOrders(MikePosOrders posOrders) throws Exception {
        GuardAlgoUp guardAlgoUpMonitoringPosOrders = null;

        int guardCount = 0;
        for (GuardAlgoUp guardAlgoUp : guardAlgoUpSet){
            if(guardAlgoUp.monitoredMikePosOrders() == posOrders) {
                guardAlgoUpMonitoringPosOrders = guardAlgoUp;
                guardCount++;
            }
            if(guardCount > 1) {
                MikeSimLogger.addLogEvent("EXCEPTION in getGuardAlgoForMikePosOrders - more than one algo controlling " + posOrders.getName());
                throw new Exception();
            }
        }

        return guardAlgoUpMonitoringPosOrders;
    }

    synchronized public void createScalperAlgo1(MikePosOrders posOrders, int entryPrice, int targetPrice, int orderAmount, MikeOrder.MikeOrderType entry){

//       // experimenting
//        System.out.println("Experimenting. Creating Simple Scalper Algo in child of MikePosOrders:");
//
//        MikePosOrders child = posOrders.createChildPosOrders();
//        SimpleScalperAlgo algo = new SimpleScalperAlgo(child, entryPrice, targetPrice, orderAmount, entry );


        MikeSimLogger.addLogEvent("Creating SimpleScalperAlgo for MikePosOrders " + posOrders.getName());
        SimpleScalperAlgo algo = new SimpleScalperAlgo(posOrders, entryPrice, targetPrice, orderAmount, entry );

        algoSet.add(algo);
        simpleScalperAlgoSet.add(algo);
    }

    /**
     * This will cancel all SimpleScalperAlgos that have been initialized with entryPrice
     * @param entryPrice
     */
    synchronized public void cancelAllSimpleScalperAlgosAtPrice(int entryPrice, MikePosOrders posOrders) {
        Set<BaseAlgo> algosToCancel = new HashSet<>();
        //find all algos initialized with entryPrice and cancel them:
        for (SimpleScalperAlgo algo : simpleScalperAlgoSet) {
            if (algo.getEntryTargetPrice() == entryPrice && algo.monitoredMikePosOrders() == posOrders) {
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
        Set<BaseAlgo> algosToCancel = new HashSet<>();
        //find all algos initialized with entryPrice and cancel them:
        for (ComplexScalperAlgo1 algo : complexScalperAlgo1Set) {
            if (algo.getEntryTargetPrice() == entryPrice && algo.monitoredMikePosOrders() == posOrders) {
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

        Set<BaseAlgo> algosToCancel = new HashSet<>();
        //find all algos initialized with entryPrice and cancel them:
        for (SimpleStepperAlgo algo : simpleStepperAlgoSet) {
            if (algo.getEntryTargetPrice() == entryPrice && algo.monitoredMikePosOrders() == posOrders) {
                algo.cancel();
                algosToCancel.add(algo);
            }
        }

        //housekeeping - remove cancelled algos from active algos and add them to the set of cancelled algos:
        algoSet.removeAll(algosToCancel);
        cancelledAlgoSet.addAll(algosToCancel);
    }


    public synchronized void processAllAlgos() throws Exception {
        for (BaseAlgo algo : algoSet) {
            algo.process();
        }
    }


    public synchronized void sortAlgosbyPrice() {

        //explanation here:
        //https://stackoverflow.com/questions/36228664/sorting-observablelistclass-by-value-in-ascending-and-descending-order-javaf
        // assuming there is a instance method Class.getScore that returns int
        // (other implementations for comparator could be used too, of course)
        Comparator<BaseAlgo> comparator = Comparator.comparingInt(BaseAlgo::getEntryPrice);

        comparator = comparator.reversed();
        FXCollections.sort(algoSet, comparator);

    }


    public ObservableList<BaseAlgo> getAlgoSet() {
        return algoSet;
    }
}
