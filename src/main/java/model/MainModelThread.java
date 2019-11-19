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

public class MainModelThread extends Thread {

    public ObservableList<PriceServer> priceServers;

    public MainGUIClass mainGUIClass;

    //provides prices for the program:
    public PriceServer priceServer;

    //set up connection to outside trading software for market data, orders, etc:
    public OutsideTradingSoftwareAPIConnection marketConnection;

    //handles all orders, checking for order fills:
    private OrderServer orderServer;

    //MikePosOrders - this is used to have multiple separate 'books' of orders and positions - to manage trading.
    // TODO: for now this is just one instance. Modify to be able to have as many as you want.
    public MikePosOrders mikePosOrders;
    public ObservableList<MikePosOrders> posOrdersObservableList = FXCollections.observableArrayList();
    private long mikePosOrdersNumber = 0;

    private GUIUpdateDispatcher myGUIUpdateDispatcher;
    private long count = 0;
    public static boolean interrupted;
    int refreshGUIInMiliseconds = 100;//set this to change GUI refresh rate
    long mainLoopTurnaroundTime = 0;//for monitoring performance


//    PriceServerManager priceServerManager;

    public void shutDownMikeSim(){
        interrupted = true;
    }

    public MainModelThread(MainGUIClass mainGUIClass){
        this.mainGUIClass = mainGUIClass;

        //set up connection to outside trading software for market data, orders, etc:
        marketConnection = new InteractiveBrokersAPI();

        //provides prices for the program:
        //one priceserver for each tickerID
        priceServers = FXCollections.observableArrayList();
        priceServer = new PriceServer(0, "SPY", marketConnection);
        priceServers.add(priceServer);
        priceServers.add(new PriceServer(1, "DIA", marketConnection));
        priceServers.add(new PriceServer(2, "IWM", marketConnection));
        priceServers.add(new PriceServer(3, "QQQ", marketConnection));
        priceServers.add(new PriceServer(4, "EUR FOREX", marketConnection));

        orderServer = new OrderServer();

        createMikePosorders();
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
        orderServer.checkSimulatedFills(priceServer);

        if (getMikePosOrders(0) != null) {
            getMikePosOrders(0).processFilledOrders();
        }
    }

    /**
     * Create an instance of MikePosOrders and add it to the list
     * @return
     */
    synchronized public MikePosOrders createMikePosorders(){
        MikePosOrders posOrders = new MikePosOrders(orderServer, priceServer);
        //TODO: finish this so that you set the correct orderServer based on the instrument



        posOrders.setName("Positions number " + mikePosOrdersNumber++);
        posOrdersObservableList.add(posOrders);

        return posOrders;
    }

    public MikePosOrders getMikePosOrders(int mikePosOrdersNumber) {
        return posOrdersObservableList.get(mikePosOrdersNumber);
    }

    synchronized public void connectOutsideData(){
        marketConnection.connect();
    }

    synchronized public PriceServer getPriceServer(){return priceServer;}

    public OrderServer getOrderServer() {
        return orderServer;
    }

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
