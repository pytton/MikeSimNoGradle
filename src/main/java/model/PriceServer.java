package main.java.model;

public class PriceServer {

    private int bidPrice = 27100;
    private Integer askPrice = 27101;
    private int bidVolume = 10;
    private int askVolume = 10;

    private Integer experimentalNumber = 0;

    public Integer getExperimentalNumber() {
        return experimentalNumber;
    }

    public void setExperimentalNumber(int experimentalNumber) {
        this.experimentalNumber = experimentalNumber;
    }
//add timestamp variable


    public int getBidPrice() {
        return bidPrice;
    }

    synchronized public void setBidPrice(int bidPrice) {
        this.bidPrice = bidPrice;
        System.out.println("Bid price set to: " + bidPrice);
    }

    public Integer getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(int askPrice) {
        this.askPrice = askPrice;
        System.out.println("Ask price set to: " + askPrice);
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
}
