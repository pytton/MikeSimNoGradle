package main.java.model;

public class PriceServer {

    private int bidPrice = 27100;
    private int askPrice = 27101;
    private int bidVolume = 10;
    private int askVolume = 10;

    //add timestamp variable


    public int getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(int bidPrice) {
        this.bidPrice = bidPrice;
        System.out.println("Bid price set to: " + bidPrice);
    }

    public int getAskPrice() {
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
