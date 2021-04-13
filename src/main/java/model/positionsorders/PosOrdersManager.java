package main.java.model.positionsorders;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.TradedInstrument;
import main.java.model.orderserver.OrderServer;
import main.java.model.orderserver.OrderServerSimulatedInternal;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.positionsorders.MikePosition;
import main.java.model.priceserver.PriceServer;

import java.util.Map;
import java.util.TreeMap;

/**
 * This class is responsible for creating and managing new PosOrders and assigning them the correct
 * PriceServer and OrderServer depending on which instrument the PosOrders is supposed to trade.
 * PosOrders needs a PriceServer and OrderServer.
 * <p>
 * There is only one PriceServer and one OrderServer for every instrument traded.
 * The traded instruments are identified by their tickerId, just like in the InterActive Brokers API.
 * There can be many PosOrders for one traded instrument.
 * Once a PosOrders is created, it can only be used to trade the instrument it was
 * created for trading initially
 */
public class PosOrdersManager {

    private final MainModelThread mainModelThread;
//    private MainModelThread mainModelThread;
    private Map<Integer, Data> dataMap;
    private ObservableList<PriceServer> priceServerObservableList = FXCollections.observableArrayList();

    public PosOrdersManager(MainModelThread mainModelThread, Map<Integer, TradedInstrument> tradedInstrumentMap /*MainModelThread mainModelThread*/) {
//        this.mainModelThread = mainModelThread;
        this.mainModelThread = mainModelThread;
        dataMap = new TreeMap<>();
        for (TradedInstrument instrument : tradedInstrumentMap.values()) {
            //create an orderserver, priceserver and PosOrdersObservable list based on the instruments that can be traded:
            Data data = new Data(instrument.getTickerId(), instrument.getSymbol(), instrument);
            dataMap.put(instrument.getTickerId(), data);
            priceServerObservableList.add(data.getPriceServer());
        }
    }

    /**
     * Use this to take profits or move positions between different MikePosOrder objects.
     * Takes a given amount of traded instruments and closed profit/loss
     * from source MikePosOrders to target MikePosOrders
     * returns false in case of error in parameters
     * @param source take from this MikePosOrders
     * @param target put into this MikePosOrders
     * @param amountOpenPositionToTransfer negative number for short position, positive for long
     * @param closedPLToTransfer
     */
    public static synchronized boolean transferAmountAndClosedPL(MikePosOrders source, MikePosOrders target,
                                          int amountOpenPositionToTransfer,
                                          int closedPLToTransfer){
        //check for valid parameters:
        if(source == null || target == null) return false;
        if(source == target) return false;

        //create two symmetrical MikePositions and add them to souce and target MikePosOrders at current bid price:

        int price = source.getBidPrice();
        int open_amount = amountOpenPositionToTransfer;
        int closed_pl = closedPLToTransfer;int total_pl = 0;

        MikePosition putIntoThis = new MikePosition (price, open_amount, 0, closed_pl, 0);
        MikePosition takeFromThis = new MikePosition(price, (open_amount) * -1, 0, (closed_pl) * -1, 0 );

        source.addToMikePosition(takeFromThis);
        target.addToMikePosition(putIntoThis);

        return true;
    }

    public Map<Integer, Data> getDataMap() {
        return dataMap;
    }

    /**
     * One Data object per instrument traded. The key is tickerId of the instrument.
     * Data holds one OrderServer, one priceServer and
     * Java FX ObservableList of MikePosOrders
     */
    public class Data {
        //one tickerId per one instrument traded
        private int tickerId;
        private String tradedInstrumentName;
        public TradedInstrument tradedInstrument;

        //handles all orders, checking for order fills:
        private OrderServer orderServer;

        //provides prices for the program:
        protected PriceServer priceServer;

        //MikePosOrders - this is used to have multiple separate 'books' of orders and positions - to manage trading.
        private ObservableList<MikePosOrders> posOrdersObservableList;

        //used for unique names:
        private long mikePosOrdersNumber = 0;

        public Data(int tickerId, String tradedInstrumentName, TradedInstrument instrument) {
            this.tradedInstrument = instrument;
            this.tickerId = tickerId;
            this.tradedInstrumentName = tradedInstrumentName;
            posOrdersObservableList = FXCollections.observableArrayList();
            MikeSimLogger.addLogEvent("Creating simulated OrderServer - implement OrderServerRealExternal to send orders to real market");
            orderServer = new OrderServerSimulatedInternal();
//                orderServer = new OrderServerRealExternal();
            //marketConnection taken from outer class:
            priceServer = new PriceServer(tickerId, tradedInstrumentName, mainModelThread.marketConnection, instrument);

            //create at least one MikePosOrders for each instrument traded:
            createMikePosorders();
        }

        /**
         * Create an instance of MikePosOrders and add it to the list
         */
        synchronized public MikePosOrders createMikePosorders() {
            MikePosOrders posOrders = new MikePosOrders(orderServer, priceServer);
            posOrders.setName("" + tradedInstrumentName + " " + mikePosOrdersNumber++);
            posOrdersObservableList.add(posOrders);
            return posOrders;
        }

        /**
         * Transfers a single position from one MikePosOrders to another one.
         * Erases it from the source and adds it to the target
         *
         * @param singlePosition
         * @param sourcePosOrders
         * @param targetPosOrders
         */
        public synchronized void transferMikePosition(MikePosition singlePosition, MikePosOrders sourcePosOrders, MikePosOrders targetPosOrders) {
            sourcePosOrders.movePositionToDifferentMikePosOrders(singlePosition.getPrice(), targetPosOrders);
            MikeSimLogger.addLogEvent("Moving positon at price " + singlePosition.getPrice() + " from " + sourcePosOrders.getName() +
                    " to " + targetPosOrders.getName());
        }

        public int getTickerId() {
            return tickerId;
        }

        public OrderServer getOrderServer() {
            return orderServer;
        }

        public PriceServer getPriceServer() {
            return priceServer;
        }

        public ObservableList<MikePosOrders> getPosOrdersObservableList() {
            return posOrdersObservableList;
        }
    }


    /**
     * returns a single MikePosOrders. tickerID defines the instrument.
     *
     * @param tickerId
     * @param mikePosOrdersNumber
     * @return
     */
    public MikePosOrders getMikePosOrders(int tickerId, int mikePosOrdersNumber) {
        return dataMap.get(tickerId).getPosOrdersObservableList().get(mikePosOrdersNumber);
    }

    /**
     * Create an instance of MikePosOrders and add it to the list
     */
    synchronized public MikePosOrders createMikePosorders(Integer tickerId) {
        return dataMap.get(tickerId).createMikePosorders();
    }

    public PriceServer getPriceServer(int tickerId) {
        return dataMap.get(tickerId).getPriceServer();
    }

    public OrderServer getOrderServer(int tickerId) {
        return dataMap.get(tickerId).getOrderServer();
    }

    public ObservableList<PriceServer> getPriceServerObservableList() {
        return priceServerObservableList;
    }

    /**
     * returns the list of all MikePosOrders for the instrument defined by TickerID
     *
     * @param tickerID
     * @return
     */
    public ObservableList<MikePosOrders> getMikePosOrdersList(int tickerID) {
        return dataMap.get(tickerID).getPosOrdersObservableList();
    }

    public ObservableList<MikePosOrders> getPosOrdersObservableList(int tickerId) {
        return dataMap.get(tickerId).getPosOrdersObservableList();
    }

//        public AlgoManager getAlgoManager(){return algoManager;}
}
