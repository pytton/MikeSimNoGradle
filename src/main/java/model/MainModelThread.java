package main.java.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.controllerandview.MainGUIClass;
import main.java.model.livemarketdata.InteractiveBrokersAPI;
import main.java.model.livemarketdata.OutsideTradingSoftwareAPIConnection;
import main.java.model.mikealgos.AlgoManager;
import main.java.model.orderserver.OrderServer;
import main.java.model.orderserver.OrderServerSimulatedInternal;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.positionsorders.MikePosition;
import main.java.model.priceserver.PriceServer;

import java.util.Map;
import java.util.TreeMap;

public class MainModelThread extends Thread {

    public MainGUIClass mainGUIClass;
    public PosOrdersManager posOrdersManager;

    //set up connection to outside trading software for market data, orders, etc:
    public OutsideTradingSoftwareAPIConnection marketConnection;
    public AlgoManager algoManager;
    private GUIUpdateDispatcher myGUIUpdateDispatcher;
    private long count = 0; //used for printing program 'heartbeat'
    public static boolean interrupted; //used for shutting down the program
    private int mainLoopThrottleSetting = 10; //main loop will wait this miliseconds between iterations
    int refreshGUIInMiliseconds = 100; //set this to change GUI refresh rate
    long mainLoopTurnaroundTime = 0; //for monitoring performance

    public MainModelThread(MainGUIClass mainGUIClass, Map<Integer, TradedInstrument> tradedInstrumentMap){
        this.mainGUIClass = mainGUIClass;
        //set up connection to outside trading software for market data, orders, etc:
        marketConnection = InteractiveBrokersAPI.getInstance(tradedInstrumentMap);
        //this stores a priceserver, orderserver and a list of MikePosOrders for each traded instrument:
        posOrdersManager = new PosOrdersManager(tradedInstrumentMap);
        algoManager = new AlgoManager(this);
    }

    /**
     * This is the main loop of the program
     */
    @Override
    public void run() {

        //Handle GUI updates in seperate thread:
        long timeOfLastGUIUpdate = System.currentTimeMillis();
        long timeOfLastModelUpdate = System.currentTimeMillis();
        myGUIUpdateDispatcher = new GUIUpdateDispatcher();
        myGUIUpdateDispatcher.setMainGUIClass(mainGUIClass);

        while (!interrupted) {
                long timeStartLoop = System.currentTimeMillis();

            try {

                if (System.currentTimeMillis() > timeOfLastModelUpdate + mainLoopThrottleSetting) {
                    processOrdersAndCalculatePL();
                    processAlgos();
                    mainLoopTurnaroundTime = System.currentTimeMillis() - timeOfLastModelUpdate;
                    timeOfLastModelUpdate = System.currentTimeMillis();
                    count++;
                }

                //Update the GUI:
                if (System.currentTimeMillis() > timeOfLastGUIUpdate + refreshGUIInMiliseconds) {
                    if(myGUIUpdateDispatcher.isReady()){
                    Platform.runLater(myGUIUpdateDispatcher);
                    timeOfLastGUIUpdate = System.currentTimeMillis();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This is called if all JavaFX windows are closed
     */
    public void shutDownMikeSim(){

        //disconnect from OutSideTradingSoftware if connected:
        try{
            MikeSimLogger.addLogEvent("shutDownMikeSim called. Attempting to disconnect from TWS API");
            marketConnection.disconnect();
        }
        catch (Exception e){MikeSimLogger.addLogEvent("Exception while disconnecting from TWS API");}

        //stop the main loop:
        interrupted = true;
    }

    private void processAlgos() throws Exception {
        algoManager.processAllAlgos();
    }

    private void processOrdersAndCalculatePL(){
        for(PosOrdersManager.Data data: posOrdersManager.dataMap.values()){
            //check for fills in orderserver of every traded instrument:
            data.getOrderServer().checkFills(data.getPriceServer());

            //go through all the MikePosOrders, process filled orders and recalculate the PL for each of them:
            for(MikePosOrders positions : data.getPosOrdersObservableList()){
                positions.processFilledOrders();
                positions.recalcutlatePL();
            }

            //process historical data:
            data.priceServer.processHistoricalData();
        }
    }

    synchronized public void connectOutsideData(){
        marketConnection.connect();
    }

    /**
     * This class is responsible for creating and managing new PosOrders and assigning them the correct
     * PriceServer and OrderServer depending on which instrument the PosOrders is supposed to trade.
     * PosOrders needs a PriceServer and OrderServer.
     *
     * There is only one PriceServer and one OrderServer for every instrument traded.
     * The traded instruments are identified by their tickerId, just like in the InterActive Brokers API.
     * There can be many PosOrders for one traded instrument.
     * Once a PosOrders is created, it can only be used to trade the instrument it was
     * created for trading initially
     */
    public class PosOrdersManager {

        private Map<Integer, Data> dataMap;
        private ObservableList<PriceServer> priceServerObservableList = FXCollections.observableArrayList();

        public PosOrdersManager(Map<Integer, TradedInstrument> tradedInstrumentMap) {
            dataMap = new TreeMap<>();
            for (TradedInstrument instrument : tradedInstrumentMap.values()) {
                //create an orderserver, priceserver and PosOrdersObservable list based on the instruments that can be traded:
                Data data = new Data(instrument.getTickerId(), instrument.getSymbol());
                dataMap.put(instrument.getTickerId(), data);
                priceServerObservableList.add(data.getPriceServer());
            }
        }

        /**
         * One Data object per instrument traded. The key is tickerId of the instrument.
         * Data holds one OrderServer, one priceServer and
         * Java FX ObservableList of MikePosOrders
         */
        private class Data{
            //one tickerId per one instrument traded
            private int tickerId;
            private String tradedInstrumentName;

            //handles all orders, checking for order fills:
            private OrderServer orderServer;

            //provides prices for the program:
            private PriceServer priceServer;

            //MikePosOrders - this is used to have multiple separate 'books' of orders and positions - to manage trading.
            private ObservableList<MikePosOrders> posOrdersObservableList;

            //used for unique names:
            private long mikePosOrdersNumber = 0;

            public Data(int tickerId, String tradedInstrumentName) {
                this.tickerId = tickerId;
                this.tradedInstrumentName = tradedInstrumentName;
                posOrdersObservableList = FXCollections.observableArrayList();
                MikeSimLogger.addLogEvent("Creating simulated OrderServer - implement OrderServerRealExternal to send orders to real market");
                orderServer = new OrderServerSimulatedInternal();
//                orderServer = new OrderServerRealExternal();
                //marketConnection taken from outer class:
                priceServer = new PriceServer(tickerId, tradedInstrumentName, marketConnection);

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
             * @param singlePosition
             * @param sourcePosOrders
             * @param targetPosOrders
             */
            public synchronized void transferMikePosition(MikePosition singlePosition, MikePosOrders sourcePosOrders, MikePosOrders targetPosOrders) {
                sourcePosOrders.movePositionToDifferentMikePosOrders(singlePosition.getPrice(), targetPosOrders);
                System.out.println("Moving positon at price " + singlePosition.getPrice() + " from " + sourcePosOrders.getName() +
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
         * @param tickerId
         * @param mikePosOrdersNumber
         * @return
         */
        public MikePosOrders getMikePosOrders(int tickerId,  int mikePosOrdersNumber) {
            return dataMap.get(tickerId).getPosOrdersObservableList().get(mikePosOrdersNumber);
        }

        /**
         * Create an instance of MikePosOrders and add it to the list
         */
        synchronized public MikePosOrders createMikePosorders(Integer tickerId) {
            return dataMap.get(tickerId).createMikePosorders();
        }

        public PriceServer getPriceServer (int tickerId){
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
         * @param tickerID
         * @return
         */
        public ObservableList<MikePosOrders> getMikePosOrdersList(int tickerID){
            return dataMap.get(tickerID).getPosOrdersObservableList();
        }

        public ObservableList<MikePosOrders> getPosOrdersObservableList(int tickerId) {
            return dataMap.get(tickerId).getPosOrdersObservableList();
        }

//        public AlgoManager getAlgoManager(){return algoManager;}
    }

    /**
     * This is used to make sure GUI is updated in a separate thread
     * only once every refreshGUIInMiliseconds
     */
    private class GUIUpdateDispatcher implements Runnable {

        MainGUIClass mainGUIClass;
        boolean isReady = true;
        long count = 0;

        public void setMainGUIClass(MainGUIClass mainGUIClass) {
            this.mainGUIClass = mainGUIClass;
        }

        @Override
        public void run() {
            try {
                isReady = false;
                mainGUIClass.updateGUI();
                count++;
//                Thread.sleep(refreshGUIInMiliseconds);
                isReady = true;
                if (count%10 == 0) MikeSimLogger.addLogEvent("Mainloop count: " + count + " MainLoop turnaround in miliseconds: " + mainLoopTurnaroundTime);


//                    System.out.println("Mainloop count: " + count + " MainLoop turnaround in miliseconds: " + mainLoopTurnaroundTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean isReady() {
            return isReady;
        }
    }
}

