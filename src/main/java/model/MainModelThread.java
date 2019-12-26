package main.java.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.controllerandview.MainGUIClass;
import main.java.model.livemarketdata.InteractiveBrokersAPI;
import main.java.model.livemarketdata.OutsideTradingSoftwareAPIConnection;
import main.java.model.mikealgos.ScalperAlgo1;
import main.java.model.mikealgos.MikeAlgo;
import main.java.model.orderserver.OrderServer;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    int refreshGUIInMiliseconds = 100; //set this to change GUI refresh rate
    long mainLoopTurnaroundTime = 0; //for monitoring performance

    public MainModelThread(MainGUIClass mainGUIClass, Map<Integer, TradedInstrument> tradedInstrumentMap){
        this.mainGUIClass = mainGUIClass;
        //set up connection to outside trading software for market data, orders, etc:
        marketConnection = new InteractiveBrokersAPI(tradedInstrumentMap);
        //this stores a priceserver, orderserver and a list of MikePosOrders for each traded instrument:
        posOrdersManager = new PosOrdersManager(tradedInstrumentMap);

        algoManager = new AlgoManager(this);

    }

    /**
     * This is the main loop of the program
     */
    @Override
    public void run() {

        //Hangle GUI updates in seperate thread:
        long timeOfLastGUIUpdate = System.currentTimeMillis();
        myGUIUpdateDispatcher = new GUIUpdateDispatcher();
        myGUIUpdateDispatcher.setMainGUIClass(mainGUIClass);

        while (!interrupted) {
            try {
                long timeStartLoop = System.currentTimeMillis();

                processOrdersAndCalculatePL();
                processAlgos();

                //Update the GUI:
                if (System.currentTimeMillis() > timeOfLastGUIUpdate + refreshGUIInMiliseconds) {
                    if(myGUIUpdateDispatcher.isReady()){
                    Platform.runLater(myGUIUpdateDispatcher);
                    timeOfLastGUIUpdate = System.currentTimeMillis();
                        count++;}
                }

                Thread.sleep(1);
                mainLoopTurnaroundTime = System.currentTimeMillis() - timeStartLoop;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDownMikeSim(){
        interrupted = true;
    }

    private void processAlgos(){
        algoManager.processAllAlgos();
    }

    private void processOrdersAndCalculatePL(){
        for(PosOrdersManager.Data data: posOrdersManager.dataMap.values()){
            //check for fills in orderserver of every traded instrument:
            data.getOrderServer().checkSimulatedFills(data.getPriceServer());

            //go through all the MikePosOrders, process filled orders and recalculate the PL for each of them:
            for(MikePosOrders positions : data.getPosOrdersObservableList()){
                positions.processFilledOrders();
                positions.recalcutlatePL();
            }
        }
    }

    synchronized public void connectOutsideData(){
        marketConnection.connect();
    }

    /**
     * This class is responsible for creating and managing new PosOrders and assigning them the correct
     * PriceServer and OrderServer depending on which instrument the PosOrders is supposed to trade.
     * PosOrders needs a PriceServer and OrderServer. There is only one PriceServer and one OrderServer
     * for every instrument traded. The traded instruments are identified by their tickerId,
     * just like in the InterActive Brokers API.
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
                orderServer = new OrderServer();
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
                //TODO: finish this so that you set the correct orderServer based on the instrument
                posOrders.setName("" + tradedInstrumentName + " " + mikePosOrdersNumber++);
                posOrdersObservableList.add(posOrders);
                return posOrders;
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

        public ObservableList<MikePosOrders> getPosOrdersObservableList(int tickerId) {
            return dataMap.get(tickerId).getPosOrdersObservableList();
        }

        public AlgoManager getAlgoManager(){return algoManager;}
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
                if (count%10 == 0) System.out.println("Mainloop count: " + count + " MainLoop turnaround in miliseconds: " + mainLoopTurnaroundTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean isReady() {
            return isReady;
        }
    }
}

