package main.java.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.controllerandview.MainGUIClass;
import main.java.model.livemarketdata.InteractiveBrokersAPI;
import main.java.model.livemarketdata.OutsideTradingSoftwareAPIConnection;
import main.java.model.orderserver.OrderServer;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

import java.util.Map;

public class MainModelThread extends Thread {

    public MainGUIClass mainGUIClass;
    public PosOrdersManager posOrdersManager; // = new PosOrdersManager();

    //set up connection to outside trading software for market data, orders, etc:
    public OutsideTradingSoftwareAPIConnection marketConnection;

    //used for unique names:
    private long mikePosOrdersNumber = 0;

    private GUIUpdateDispatcher myGUIUpdateDispatcher;
    private long count = 0;
    public static boolean interrupted;
    int refreshGUIInMiliseconds = 100;//set this to change GUI refresh rate
    long mainLoopTurnaroundTime = 0;//for monitoring performance


    public void shutDownMikeSim(){
        interrupted = true;
    }

    public MainModelThread(MainGUIClass mainGUIClass){
        this.mainGUIClass = mainGUIClass;

        //set up connection to outside trading software for market data, orders, etc:
        marketConnection = new InteractiveBrokersAPI();

        posOrdersManager = new PosOrdersManager();

    }

    @Override
    public void run() {

        //Hangle GUI updates in seperate thread:
        long timeOfLastGUIUpdate = System.currentTimeMillis();
        myGUIUpdateDispatcher = new GUIUpdateDispatcher();
        myGUIUpdateDispatcher.setMainGUIClass(mainGUIClass);



        while (!interrupted) {
            try {
                long timeStartLoop = System.currentTimeMillis();



                processOrders();
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

    private void processAlgos(){}
    private void processOrders(){
        posOrdersManager.orderServer.checkSimulatedFills(posOrdersManager.priceServer);

        if (posOrdersManager.getMikePosOrders(0, this) != null) {
            posOrdersManager.getMikePosOrders(0, this).processFilledOrders();
        }
    }

    synchronized public void connectOutsideData(){
        marketConnection.connect();
    }

    synchronized public PriceServer getPriceServer(){return posOrdersManager.priceServer;}

    public OrderServer getOrderServer() {
        return posOrdersManager.orderServer;
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

        private class Data{
            

        }

        private Map<Integer, Data> dataMap;

        private Map<Integer, String> tickerIdMap;

        //handles all orders, checking for order fills:
        private OrderServer orderServer;

        //provides prices for the program:
        private PriceServer priceServer;

        //MikePosOrders - this is used to have multiple separate 'books' of orders and positions - to manage trading.
        // TODO: for now this is just one instance. Modify to be able to have as many as you want.
        private ObservableList<MikePosOrders> posOrdersObservableList;// = FXCollections.observableArrayList();

        //provides prices for the program:
        //one priceserver for each tickerID
        private ObservableList<PriceServer> priceServerObservableList;// = FXCollections.observableArrayList();

        private ObservableList<OrderServer> orderServerObservableList = FXCollections.observableArrayList();

        public PosOrdersManager() {

            posOrdersObservableList = FXCollections.observableArrayList();

            priceServerObservableList = FXCollections.observableArrayList();

            /*posOrdersManager.*/
            priceServer = new PriceServer(0, "SPY", marketConnection);
            /*posOrdersManager.*/
            priceServerObservableList.add(priceServer);
            /*posOrdersManager.*/
            priceServerObservableList.add(new PriceServer(1, "DIA", marketConnection));
            /*posOrdersManager.*/
            priceServerObservableList.add(new PriceServer(2, "IWM", marketConnection));
            /*posOrdersManager.*/
            priceServerObservableList.add(new PriceServer(3, "QQQ", marketConnection));
            /*posOrdersManager.*/
            priceServerObservableList.add(new PriceServer(4, "EUR FOREX", marketConnection));

            /*posOrdersManager.*/
            orderServer = new OrderServer();

            createMikePosorders(0);

        }

        public MikePosOrders getMikePosOrders(int mikePosOrdersNumber, MainModelThread mainModelThread) {
            return posOrdersObservableList.get(mikePosOrdersNumber);
        }

        /**
         * Create an instance of MikePosOrders and add it to the list
         */
        synchronized public MikePosOrders createMikePosorders(Integer tickerId) {
            MikePosOrders posOrders = new MikePosOrders(orderServer, priceServer);
            //TODO: finish this so that you set the correct orderServer based on the instrument

            posOrders.setName("Positions number " + /*mainModelThread.*/mikePosOrdersNumber++);
            posOrdersObservableList.add(posOrders);

            return posOrders;
        }

        public ObservableList<PriceServer> getPriceServerObservableList(/*MainModelThread mainModelThread*/) {
            return /*mainModelThread.posOrdersManager.*/priceServerObservableList;
        }

        public ObservableList<MikePosOrders> getPosOrdersObservableList(MainModelThread mainModelThread) {
            return posOrdersObservableList;
        }
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