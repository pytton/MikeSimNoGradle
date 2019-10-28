package main.java.model.priceserver;


import main.java.model.livemarketdata.OutsideTradingSoftwareAPIConnection;

/**
 * Provides priceserver bid/ask volumes for a single instrument.
 * Prices can be one of three: live, manual or historical
 * One PriceServer per one instrument to be traded.
 * So one for SPY, another one for DIA etc.
 */
public class PriceServer {

    public String instrumentName;

    public enum PriceType{
        MANUAL,
        LIVEMARKET,
        HISTORICAL
    }

    private OutsideTradingSoftwareAPIConnection outsideTradingSoftwareAPIConnection = null;

    //simulatad manual prices and volumes:
    private int bidPrice = 27100;
    private int askPrice = 27101;
    private int bidVolume = -5;
    private int askVolume = -5;

    //livemarket prices and volumes:
    private int liveBidPrice = 27100;
    private int liveAskPrice = 27101;
    private int liveBidVolume = -5;
    private int liveAskVolume = -5;

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
            case LIVEMARKET: return liveBidPrice;
            default: return bidPrice;
        }
    }

    synchronized public void setBidPrice(int bidPrice) {
        this.bidPrice = bidPrice;
//        System.out.println("Bid price set to: " + bidPrice);
    }

    //TODO: finish this. returns only manual prices now
    public int getAskPrice() {
        return askPrice;
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

    public void setRealTimeDataSource(OutsideTradingSoftwareAPIConnection outsideTradingSoftwareAPIConnection) {
        this.outsideTradingSoftwareAPIConnection = outsideTradingSoftwareAPIConnection;
    }

    public double getRealTimeBidPrice(){
        if(!(outsideTradingSoftwareAPIConnection == null)){
            return outsideTradingSoftwareAPIConnection.getBidPrice();
        }
        return -5;
    }

    public double getRealTimeAskPrice(){
        if(!(outsideTradingSoftwareAPIConnection == null)){
            return outsideTradingSoftwareAPIConnection.getAskPrice();
        }
        return -5;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }
}
