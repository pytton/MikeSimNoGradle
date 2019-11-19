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

    public double getBidPrice(int tickerID);
    public double getAskPrice(int tickerID);
    public double getBidSize(int tickerID);
    public double getAskSize(int tickerID);

    /**
     * connects to realtime market data and sets up default contracts
     * with their tickerIDs:
     *      * Use tickerID later to access data of contracts.
     *      * Currently:
     *      * TickerID 0 = SPY
     *      * TickerID 1 = DIA
     *      * TickerID 2 = IWM
     *      * TickerID 3 = QQQ
     *      * TickerID 4 = EUR (FOREX)
     *
     */
     boolean connect();
    public void consolePrintRealTimeData();
}
