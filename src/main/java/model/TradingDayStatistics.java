package main.java.model;

import javafx.collections.ObservableList;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.positionsorders.PosOrdersManager;

/**
 * This is used for calculating current and highest:
 * profit, loss, open long positions, open short positions
 */
public class TradingDayStatistics {

    private int tickerId;
    private int currentGlobalOpenPos;
    private int currentGlobalPL;
    private int maxLoss;
    private int maxProfit;
    private int highestOpenLongPosition;
    private int highestOpenShortPosition;
    private boolean initialized = false;

    TradingDayStatistics(int tickerId){
        this.tickerId = tickerId;
    }

    public void calculate(PosOrdersManager posOrdersManager){
        if(!initialized) doInitialization(posOrdersManager);
        calculateCurrent(posOrdersManager.getMikePosOrdersList(tickerId));
        calculateMaxValues();
    }

    private void calculateMaxValues() {
        if(currentGlobalPL > maxProfit) maxProfit = currentGlobalPL;
        if(currentGlobalPL < maxLoss) maxLoss = currentGlobalPL;
        if(currentGlobalOpenPos > highestOpenLongPosition) highestOpenLongPosition = currentGlobalOpenPos;
        if(currentGlobalOpenPos < highestOpenShortPosition) highestOpenShortPosition = currentGlobalOpenPos;
    }

    private void doInitialization(PosOrdersManager posOrdersManager){
        currentGlobalOpenPos = 0;
        ObservableList<MikePosOrders> positionsList = posOrdersManager.getMikePosOrdersList(tickerId);
        calculateCurrent(positionsList);
        if(currentGlobalPL >= 0){
            maxProfit = currentGlobalPL;
            maxLoss = 0;
        }else {
            maxProfit = 0;
            maxLoss = currentGlobalPL;
        }

        if(currentGlobalOpenPos >=0){
            highestOpenLongPosition = currentGlobalOpenPos;
            highestOpenShortPosition = 0;
        }else {
            highestOpenShortPosition = 0;
            highestOpenShortPosition = currentGlobalOpenPos;
        }
        initialized = true;
    }

    private void calculateCurrent(ObservableList<MikePosOrders> positionsList) {
        currentGlobalOpenPos = 0;
        currentGlobalPL = 0;
        for(MikePosOrders posOrders : positionsList){
            currentGlobalOpenPos = currentGlobalOpenPos + posOrders.getTotalOpenAmount();
            currentGlobalPL = currentGlobalPL + posOrders.getTotalPL();
        }
    }

    public int getTickerId() {
        return tickerId;
    }

    public int getCurrentGlobalOpenPos() {
        return currentGlobalOpenPos;
    }

    public int getCurrentGlobalPL() {
        return currentGlobalPL;
    }

    public int getMaxLoss() {
        return maxLoss;
    }

    public int getMaxProfit() {
        return maxProfit;
    }

    public int getHighestOpenLongPosition() {
        return highestOpenLongPosition;
    }

    public int getHighestOpenShortPosition() {
        return highestOpenShortPosition;
    }
}
