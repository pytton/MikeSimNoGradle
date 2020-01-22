package main.java.model.priceserver;


import main.java.model.livemarketdata.InteractiveBrokersAPI;
import main.java.model.livemarketdata.OutsideTradingSoftwareAPIConnection;

import java.util.List;

/**
 * Provides priceserver bid/ask prices and volumes for a single instrument.
 * Prices can be one of three: live, manual or historical
 * One PriceServer per one instrument to be traded.
 * So one for SPY, another one for DIA etc.
 */
public class PriceServer {

    //    private Integer tickerId;
    final private int tickerID;
    final public String TradedInstrumentName;
    private OutsideTradingSoftwareAPIConnection outsideTradingSoftwareAPIConnection = null;


    @Override
    public String toString() {
        return TradedInstrumentName;
    }

    public PriceServer(int tickerID, String TradedInstrumentName, OutsideTradingSoftwareAPIConnection marketConnection){
        this.tickerID = tickerID;
        this.TradedInstrumentName = TradedInstrumentName;
        setRealTimeDataSource(marketConnection);
    }


    //used by processHistoricalData:
    long histDataLastUpdateTimeInMilisecs = System.currentTimeMillis();
    int lastHistoricalPos = 0;
//    double histDataUpadateSpeedPercentage = 1.00;

    /**
     * This called in a loop from MainModelThread:
     */
    public void processHistoricalData() {
            //check if it is time to update the data:
            if (System.currentTimeMillis() > (histDataLastUpdateTimeInMilisecs + 1000)) {

                //if historical prices not selected, do nothing:
                if(priceType != PriceType.HISTORICAL) return;

                histDataLastUpdateTimeInMilisecs = System.currentTimeMillis();
                //if historical data is available, update it in this priceserver.
                // tickerID used to access the correct intrument:
                if (!outsideTradingSoftwareAPIConnection.getHistoricalPriceDataMap().get(tickerID).isEmpty()
                && lastHistoricalPos < outsideTradingSoftwareAPIConnection.getHistoricalPriceDataMap().get(tickerID).size()) {
                    try {
                        System.out.println("Inside processHistoricalData");
                        List<InteractiveBrokersAPI.PriceData> historicalPriceDataList = outsideTradingSoftwareAPIConnection.getHistoricalPriceDataMap().get(tickerID);
                        historicalBidPrice = ((int)(historicalPriceDataList.get(lastHistoricalPos).getBidPrice() * 100));
                        historicalAskPrice = ((int)(historicalPriceDataList.get(lastHistoricalPos).getAskPrice() * 100));
                        lastHistoricalPos++;

                    } catch (Exception e) {
                        System.out.println("Exception in historical Data");
                        e.printStackTrace();
                    }
                }


            }
    }

    public String getHistoricalPriceDate(){

        try {
            if (!outsideTradingSoftwareAPIConnection.getHistoricalPriceDataMap().get(tickerID).isEmpty()) return outsideTradingSoftwareAPIConnection.getHistoricalPriceDataMap().get(tickerID).get(lastHistoricalPos).getDate();
            return "No data";
        } catch (Exception e) {
            e.printStackTrace();
            return "No data";
        }


    }

    public enum PriceType{
        MANUAL,
        LIVEMARKET,
        HISTORICAL

    }

    //simulatad manual prices and volumes:
    private int bidPrice = 27100;
    private int askPrice = 27101;
    private int bidVolume = -5;
    private int askVolume = -5;

    //livemarket prices and volumes:
//    private int liveBidPrice = 27100;
//    private int liveAskPrice = 27101;
//    private int liveBidVolume = -5;
//    private int liveAskVolume = -5;

    //historical prices and volumes:
    private int historicalBidPrice = 27100;
    private int historicalAskPrice = 27101;
    private int historicalBidVolume = -5;
    private int historicalAskVolume = -5;

    private PriceType priceType = PriceType.MANUAL;

    public int getBidPrice() {
        switch (priceType){
            case MANUAL: return bidPrice;
            case HISTORICAL: return historicalBidPrice;
            case LIVEMARKET: return getRealTimeBidPrice();
            default: return bidPrice;
        }
    }

    synchronized public void setBidPrice(int bidPrice) {
        this.bidPrice = bidPrice;
//        System.out.println("Bid price set to: " + bidPrice);
    }

    public int getAskPrice() {
        switch (priceType){
            case MANUAL: return askPrice;
            case HISTORICAL: return historicalAskPrice;
            case LIVEMARKET: return getRealTimeAskPrice();
            default: return bidPrice;
        }
    }

    synchronized public void setAskPrice(int askPrice) {
        this.askPrice = askPrice;
//        System.out.println("Ask price set to: " + askPrice);
    }

    public int getBidVolume() {
        return bidVolume;
    }

    public void setBidVolume(int bidVolume) {
        this.bidVolume = bidVolume;
    }

    public int getAskVolume() {
        return askVolume;
    }

    public void setAskVolume(int askVolume) {
        this.askVolume = askVolume;
    }

    //Use below methods to get real market data from outside trading software API:

    public void setRealTimeDataSource (OutsideTradingSoftwareAPIConnection outsideTradingSoftwareAPIConnection) {
        this.outsideTradingSoftwareAPIConnection = outsideTradingSoftwareAPIConnection;
    }

    public int getRealTimeBidPrice(){
        if(!(outsideTradingSoftwareAPIConnection == null)){
            return (int)(100*outsideTradingSoftwareAPIConnection.getBidPrice(tickerID));
        }
        return -5;
    }

    public int getRealTimeAskPrice(){
        if(!(outsideTradingSoftwareAPIConnection == null)){
            return(int)(100* outsideTradingSoftwareAPIConnection.getAskPrice(tickerID));
        }
        return -5;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public int getTickerID() {
        return tickerID;
    }

}