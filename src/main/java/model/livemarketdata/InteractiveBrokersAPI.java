package main.java.model.livemarketdata;

// InteractiveBrokersAPI.java
// Version 1.0
// 20141028
// R. Holowczak
//http://holowczak.com/ib-api-java-realtime/7/

//https://interactivebrokers.github.io/tws-api/interfaceIBApi_1_1EWrapper.html


import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.CommissionReport;
import com.ib.client.UnderComp;
import main.java.model.MikeSimLogger;
import main.java.model.TradedInstrument;
import main.java.model.helpers.DoubleCompare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Experimental class used to familiarize with IB TWS API
 */
public class InteractiveBrokersAPI implements EWrapper, OutsideTradingSoftwareAPIConnection {

    private static InteractiveBrokersAPI instance = null;

    /**
     * Make sure only one instance of this class exists
     * @param tradedInstrumentMap
     * @return
     */
    public static InteractiveBrokersAPI getInstance(Map<Integer, TradedInstrument> tradedInstrumentMap) {
        if(instance == null){
            synchronized (InteractiveBrokersAPI.class) {
                if(instance == null){
                    instance = new InteractiveBrokersAPI(tradedInstrumentMap);
                }
            }
        }
        return instance;
    }

    private InteractiveBrokersAPI(Map<Integer, TradedInstrument> tradedInstrumentMap) {
        this.tradedInstrumentMap= tradedInstrumentMap;

        for(TradedInstrument instrument : tradedInstrumentMap.values()){

            boolean isFOREX = false;

            if(instrument.isThisInstrumentFOREX) isFOREX =true;

            //setup realtime data:
            priceDataMap.put(instrument.getTickerId(), new PriceData(
                    instrument.getTickerId(),
                    instrument.getSymbol(),
                    instrument.getExchange(),
                    instrument.getSecType(),
                    instrument.getCurrency(),
                    instrument,
                    isFOREX
            ));



            //setup historical data:
            List<PriceData> historicalPriceData = new ArrayList();
            historicalPriceDataMap.put(instrument.getTickerId(), historicalPriceData);

        }
    }

    // Keep track of the next Order ID
    private int nextOrderID = 0;
    // The IB API Client Socket object
    private EClientSocket client = null;
    @Override
    public EClientSocket getEClientSocket() {
        return client;
    }

    private boolean connectedToTWS = false;
    private boolean contractsAlreadySetupFlag = false;

    //TickerID for development experiments set here:
    private final int defaulTickerID = 0;
    //for default contract (without need for tickerID):
    private double bidPrice = -5;
    private double askPrice = -5;
    private double bidSize = -5;
    private double askSize = -5;

//    this stores all the live market data for each tickerID
//     * Currently(defined in tradedInstrumentsMap):
//     * TickerID 0 = SPY
//     * TickerID 1 = DIA
//     * TickerID 2 = IWM
//     * TickerID 3 = QQQ
//     * TickerID 4 = EUR (FOREX)
    private Map<Integer /*tickerID*/, PriceData /*TradedInstrument*/> priceDataMap = new HashMap<>();

    //Instruments available for trading defined here:
    private Map<Integer /*tickerID*/, TradedInstrument> tradedInstrumentMap;

    public List<PriceData> historicalPriceData = new ArrayList();

    public Map<Integer/*tickerID*/, List<PriceData>> historicalPriceDataMap = new HashMap<>();

    public Map<Integer, List<PriceData>> getHistoricalPriceDataMap() {
        return historicalPriceDataMap;
    }

    public class PriceData {

        protected TradedInstrument instrument;

        int tickerId = 0;
        private String symbol = "SPY";
        private String exchange = "SMART";
        private String secType = "STK";
        private String currency = "USD";

        public String date = "20200121 10:00:00";
        public double bidPrice = -5;
        public double askPrice = -5;
        public double bidSize = -5;
        public double askSize = -5;

        //todo: this might cause issues in historical data:
        private PriceData() {
        }

        private PriceData(TradedInstrument instrument){

            this.tickerId = instrument.getTickerId();// tickerId;
            this.symbol = instrument.getSymbol();// symbol;
            this.exchange = instrument.getExchange();// exchange;
            this.secType = instrument.getSecType();// secType;
            this.currency = instrument.getCurrency();// currency;
            this.instrument = instrument;

        }

        private PriceData(int tickerId, String symbol, String exchange, String secType, String currency,
                          TradedInstrument instrument, boolean isForex ) {
            this.tickerId = tickerId;
            this.symbol = symbol;
            this.exchange = exchange;
            this.secType = secType;
            this.currency = currency;
            this.instrument = instrument;
        }

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
        }

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

        public String getDate() { return date;}

        public void setDate(String date) { this.date = date;}
    }

    @Override
    public double getBidPrice(int tickerID) {
        try {
            return priceDataMap.get(tickerID).getBidPrice();
        } catch (Exception e) {
            return -55;
        }
    }

    @Override
    public double getAskPrice(int tickerID) {
        try {
            return priceDataMap.get(tickerID).getAskPrice();
        } catch (Exception e) {
            e.printStackTrace();
            return -55;
        }
    }

    @Override
    public double getBidSize(int tickerID) {
        try {
            return priceDataMap.get(tickerID).getBidSize();
        } catch (Exception e) {
            e.printStackTrace();
            return -55;
        }
    }

    @Override
    public double getAskSize(int tickerID) {
        try {
            return priceDataMap.get(tickerID).getAskSize();
        } catch (Exception e) {
            e.printStackTrace();
            return -55;
        }
    }

    @Override
    public double getBidPrice() {
        return bidPrice;
    }

    @Override
    public double getAskPrice() {
        return askPrice;
    }

    @Override
    synchronized public boolean connect() {
        //return true is connection attempt and setting up contracts successful:
        if (connectToTWS()) {
            return setUpContracts();
        }
        return false;
    }

    @Override
    public void disconnect() {
        MikeSimLogger.addLogEvent("Attempting to disconnect from Outside Trading Software API");
        if(client != null){
            try {
                client.eDisconnect();
            } catch (Exception e) {
                MikeSimLogger.addLogEvent("Error while attempting to disconnect");
                e.printStackTrace();
            }
            MikeSimLogger.addLogEvent("Disconnected from Outside Trading API");
        }

    }


    private boolean connectToTWS() {
        //check if already connected:
        if (connectedToTWS) {
            MikeSimLogger.addLogEvent("OutsideTradingSoftwareAPIConnection already connected to TWS!");
            return true;
        } else {
            // Create a new EClientSocket object
            client = new EClientSocket(this);

            try {
                // Connect to the TWS or IB Gateway application
                // Leave null for localhost
                // Port Number (should match TWS/IB Gateway configuration
                client.eConnect(null, 7496, 0);
                int connectionAttempts = 5;
                MikeSimLogger.addLogEvent("Attempting to connect to InterActiveBrokers TWS API");

                for (int i = 0; i < connectionAttempts; i++) {
                    if (client.isConnected()) {
                        connectedToTWS = true;
                        MikeSimLogger.addLogEvent("Connection attempt successful!");
                        return true;
                    }
                    MikeSimLogger.addLogEvent("Failed to connect. Retrying in 1000 ms");
                    // Pause here for connection to complete
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                MikeSimLogger.addLogEvent("Error connecting to TWS!");
                return false;
            }

        }
        return false;
    }

    /**
     * Define tickerIDs of contracts here.
     * Use tickerID later to access data of contracts.
     * Currently:
     * TickerID 0 = SPY
     * TickerID 1 = DIA
     * TickerID 2 = IWM
     * TickerID 3 = QQQ
     * TickerID 4 = EUR (FOREX)
     */
    private boolean setUpContracts() {

        //check if connected to data:
        if (!connectToTWS()) return false;

        //if contracts had been previously set up, return true:
        if (contractsAlreadySetupFlag) {
            MikeSimLogger.addLogEvent("Contracts have already been set up!");
            return true;
        }

        try {

            //Procedure for setting up a new contract:
            // Create a new contract
            Contract contract = new Contract();

            for(TradedInstrument instrument : tradedInstrumentMap.values())
            {
                contract.m_symbol = instrument.getSymbol();
                contract.m_exchange = instrument.getExchange();
                contract.m_secType = instrument.getSecType();
                contract.m_currency = instrument.getCurrency();
                // Create a TagValue list
                // Vector<TagValue> mktDataOptions = new Vector<TagValue>();
                // Make a call to reqMktData to start off data retrieval with parameters:
                // ConID    - Connection Identifier.
                // Contract - The financial instrument we are requesting data on
                // Ticks    - Any custom tick values we are looking for (null in this case)
                // Snapshot - false give us streaming data, true gives one data snapshot
                // MarketDataOptions - tagValue list of additional options (API 9.71 and newer)

                client.reqMktData(instrument.getTickerId(), contract, null, false);
                priceDataMap.put(instrument.getTickerId(), new PriceData(instrument)); //priceDataMap stores market data for each tickerId

                // For API Version 9.73 and higher, add one more parameter: regulatory snapshot
                // client.reqMktData(0, contract, null, false, false, mktDataOptions);

                // At this point our call is done and any market data events
                // will be returned via the tickPrice method

                //set up remaining contracts with respective TickerIDs:
            }
//
////            contract = new Contract();
//            contract.m_symbol = "DIA";
//            contract.m_exchange = "SMART";
//            contract.m_secType = "STK";
//            contract.m_currency = "USD";
//            client.reqMktData(1, contract, null, false);
//            priceDataMap.put(1, new PriceData()); //priceDataMap stores market data for each tickerId
//
////            contract = new Contract();
//            contract.m_symbol = "IWM";
//            contract.m_exchange = "SMART";
//            contract.m_secType = "STK";
//            contract.m_currency = "USD";
//            client.reqMktData(2, contract, null, false);
//            priceDataMap.put(2, new PriceData()); //priceDataMap stores market data for each tickerId
//
////            contract = new Contract();
//            contract.m_symbol = "QQQ";
//            contract.m_exchange = "SMART";
//            contract.m_secType = "STK";
//            contract.m_currency = "USD";
//            client.reqMktData(3, contract, null, false);
//            priceDataMap.put(3, new PriceData()); //priceDataMap stores market data for each tickerId
//
////            contract = new Contract();
//            contract.m_symbol = "EUR";
//            contract.m_exchange = "IDEALPRO";
//            contract.m_secType = "CASH";
//            contract.m_currency = "USD";
//            client.reqMktData(4, contract, null, false);
//            priceDataMap.put(4, new PriceData()); //priceDataMap stores market data for each tickerId

        } catch (Exception e) {
            MikeSimLogger.addLogEvent("Exception in InterActiveBrokersAPI setUpContracts!");
            e.printStackTrace();
            contractsAlreadySetupFlag = false;
            return false;
        }

        //if everything successful, set the flag:
        MikeSimLogger.addLogEvent("Setting up contracts successful.");
        contractsAlreadySetupFlag = true;
        return true;
    }

    public void consolePrintRealTimeData() {

        if (!connectedToTWS) {
            MikeSimLogger.addLogEvent("Not connected to live data!");
            return;
        }
        MikeSimLogger.addLogEvent("Bid Price: " + getBidPrice());
        MikeSimLogger.addLogEvent("Ask Price: " + getAskPrice());

        for (int tickerID : priceDataMap.keySet()) {
            MikeSimLogger.addLogEvent("Prices: " + priceDataMap.get(tickerID).getAskPrice());
        }
    }

    /**
     * This is called by InterActiveBrokers TWS API periodically whenever the API wants to communicate changes in price
     * Since this is used for filling simulated orders, if bid price passed by API would be equal to or higher than ask price,
     * this ensures ask price is always at least minBidAskSpread cent higher than bid price
     * @param tickerId
     * @param field
     * @param price
     * @param canAutoExecute
     */
    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {

        //if TWS API passes price as -1.00 or 0 - it means price is not available:
        if(price == -1.00 || price == 0) {
            MikeSimLogger.addLogEvent("EMPTY PRICE sent by TWS API");
            return;
        }

        try {
            // Print out the current price.
            // field will provide the price type:
            // 1 = bid,  2 = ask, 4 = last
            // 6 = high, 7 = low, 9 = close
            // 3 = ask size, 0 = bid size

            //https://interactivebrokers.github.io/tws-api/tick_types.html

            //priceDataMap contains prices for all tickerIds
            PriceData priceData = priceDataMap.get(tickerId);



            //make sure that Bid price is always at least minBidAskSpread lower than ask price
            //before updating prices in priceData

            //todo: issues with forex here:
            //The minimum enforced difference between the bid price and ask price:
            double minBidAskSpread = 0.01d;

            if (priceData.instrument.isThisInstrumentFOREX) minBidAskSpread = 0.00001d;


            switch (field) {
                case 1: //"field" defines what "price" is - if price provided by API is BID price this happens
                    //if bid price will be higher or equal to ask price, make sure ask price is
                    //at least minBidAskSpread cent higher than bid price
                    if(DoubleCompare.equals(price , priceData.getAskPrice()) || DoubleCompare.greaterThan(price , priceData.getAskPrice())
                    ) priceData.setAskPrice(price + minBidAskSpread);
                    //set the current bid price:
                    priceData.setBidPrice(price);
                break;
                case 2: //if price provided by API is ASK price this happens
                    //ensure ask price is always higher than bid price:
                    if(DoubleCompare.equals(price , priceData.getAskPrice()) || DoubleCompare.lessThan(price , priceData.getAskPrice())
                    ) priceData.setBidPrice(price - minBidAskSpread);
                    //set the current ask price:
                    priceData.setAskPrice(price);
                break;
            }

            if (tickerId == defaulTickerID) {

                if (field == 1) bidPrice = price;
                if (field == 2) askPrice = price;
            }
        } catch (Exception e) {
            MikeSimLogger.addLogEvent("ERROR IN InteractiveBrokersAPI tickPrice method!");
            e.printStackTrace();
        }
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {

        //look in tickPrice for comments
        // field: 3 = ask size, 0 = bid size

        try {
            PriceData priceData = priceDataMap.get(tickerId);

            switch (field) {
                case 0: priceData.setBidSize(size);
                case 3: priceData.setAskSize(size);
            }

            if (tickerId == defaulTickerID) {
                if (field == 0) bidSize = size;
                if (field == 3) askSize = size;
            }
        } catch (Exception e) {
            MikeSimLogger.addLogEvent("Error in InteractiveBrokersAPI.java.tickSize");
            e.printStackTrace();
        }
    }

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {

        // Display Historical data
        try {
            MikeSimLogger.addLogEvent("historicalData: " + reqId + ", date: " + date + ", open: " +
                    open + " ,high: " + high + ", low: " + low + ", close: " + close + ", vol: " +
                    volume);


            //IB returns -1.0 at the end of data:
            if(open == -1.0) return;
            List<PriceData> list = historicalPriceDataMap.get(reqId);
            PriceData priceData = new PriceData();
            priceData.setBidPrice(open);
            priceData.setAskPrice(open + 0.01);
            priceData.setDate(date);
            list.add(priceData);

            MikeSimLogger.addLogEvent("Setting historical price: " + open);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {

    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {

    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {

    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry) {

    }

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {

    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {

    }

    @Override
    public void openOrderEnd() {

    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {

    }

    @Override
    public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {

    }

    @Override
    public void updateAccountTime(String timeStamp) {

    }

    @Override
    public void accountDownloadEnd(String accountName) {

    }

    @Override
    public void nextValidId(int orderId) {
        // Return the next valid OrderID
        nextOrderID = orderId;
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {

    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {

    }

    @Override
    public void contractDetailsEnd(int reqId) {

    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {

    }

    @Override
    public void execDetailsEnd(int reqId) {

    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {

    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {

    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {

    }

    @Override
    public void managedAccounts(String accountsList) {

    }

    @Override
    public void receiveFA(int faDataType, String xml) {

    }

    @Override
    public void scannerParameters(String xml) {

    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {

    }

    @Override
    public void scannerDataEnd(int reqId) {

    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {

    }

    @Override
    public void currentTime(long time) {

    }

    @Override
    public void fundamentalData(int reqId, String data) {

    }

    @Override
    public void deltaNeutralValidation(int reqId, UnderComp underComp) {

    }

    @Override
    public void tickSnapshotEnd(int reqId) {

    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {

    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {

    }

    @Override
    public void position(String account, Contract contract, int pos, double avgCost) {

    }

    @Override
    public void positionEnd() {

    }

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {

    }

    @Override
    public void accountSummaryEnd(int reqId) {

    }

    @Override
    public void error(Exception e) {

    }

    @Override
    public void error(String str) {
        // Print out the error message
        System.err.println(str);

    }

    @Override
    public void error(int id, int errorCode, String errorMsg) {
        // Overloaded error event (from IB) with their own error
        // codes and messages
        System.err.println("error: " + id + "," + errorCode + "," + errorMsg);
    }

    @Override
    public void connectionClosed() {

    }
}


class InteractiveBrokersAPITestDrive{

}