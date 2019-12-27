package main.java.model.mikealgos;

import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

import java.util.HashSet;
import java.util.Set;

public class ComplexScalperAlgoUp1 extends BaseAlgo {

    private Set<ScalperAlgoUp1> algoSet;



    public ComplexScalperAlgoUp1(MikePosOrders posOrders, int lowerTarget, int interval, int howManyScalpers, int amount) {
        algoSet = new HashSet<>();

        for (int i = 0; i<howManyScalpers;i++ ) {
            ScalperAlgoUp1 algo = new ScalperAlgoUp1(posOrders, lowerTarget, (lowerTarget +interval+(i*interval)), (amount / howManyScalpers));
            algoSet.add(algo);
        }

    }

    @Override
    public void process() {
        //todo: write this

        for (ScalperAlgoUp1 algo :algoSet) {
            algo.process();
        }
    }

    @Override
    public void cancel() {
        //todo: write this

        for (ScalperAlgoUp1 algo :algoSet) {
            algo.cancel();
        }
    }

}
