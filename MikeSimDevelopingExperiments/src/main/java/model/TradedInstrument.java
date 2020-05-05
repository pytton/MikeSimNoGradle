package main.java.model;

import main.java.model.orderserver.OrderServer;
import main.java.model.priceserver.PriceServer;

public class TradedInstrument {

    private int tickerId = 0;
    private String symbol = "SPY";
    private String exchange = "SMART";
    private String secType = "STK";
    private String currency = "USD";

//    //TODO: DO I NEED THIS? CAN I USE IT THIS WAY?
//    private OrderServer orderServer;
//    private PriceServer priceServer;

    public TradedInstrument(int tickerId, String symbol, String exchange, String secType, String currency) {
        this.tickerId = tickerId;
        this.symbol = symbol;
        this.exchange = exchange;
        this.secType = secType;
        this.currency = currency;
    }

/*    private double bidPrice = -5;
    private double askPrice = -5;
    private double bidSize = -5;
    private double askSize = -5;

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public double getBidSize() {
        return bidSize;
    }

    public void setBidSize(double bidSize) {
        this.bidSize = bidSize;
    }

    public double getAskSize() {
        return askSize;
    }

    public void setAskSize(double askSize) {
        this.askSize = askSize;
    }*/

//    public OrderServer getOrderServer() {
//        return orderServer;
//    }
//
//    public PriceServer getPriceServer() {
//        return priceServer;
//    }

    public int getTickerId() {
        return tickerId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public String getSecType() {
        return secType;
    }

    public String getCurrency() {
        return currency;
    }
}
