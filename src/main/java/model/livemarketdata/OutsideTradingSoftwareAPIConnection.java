package main.java.model.livemarketdata;


import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;

import java.util.List;
import java.util.Map;

/**
 * This allows connecting to an API of a trading software to get realtime market data,
 * send orders etc.
 */
public interface OutsideTradingSoftwareAPIConnection {
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

    void disconnect();

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
    public void consolePrintRealTimeData();
    public EClientSocket getEClientSocket();
    public Map<Integer, List<InteractiveBrokersAPI.PriceData>> getHistoricalPriceDataMap();
}
