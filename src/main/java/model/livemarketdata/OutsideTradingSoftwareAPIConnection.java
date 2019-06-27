package main.java.model.livemarketdata;

public interface OutsideTradingSoftwareAPIConnection {
    /**
     * returns bid price of default contract
     */
    double getBidPrice();

    /**
     * returns ask price of default contract
     */
    double getAskPrice();

    /**
     * connects to realtime market data and sets up default contracts
     */
     boolean connect();
}
