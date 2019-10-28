package main.java.model;

import javafx.application.Platform;
import main.java.controllerandview.MainGUIClass;
import main.java.model.livemarketdata.InteractiveBrokersAPI;
import main.java.model.livemarketdata.OutsideTradingSoftwareAPIConnection;
import main.java.model.orderserver.OrderServer;
import main.java.model.priceserver.PriceServer;

public class MainModelThread extends Thread {

    public MainGUIClass mainGUIClass;

    //provides prices for the program:
    public PriceServer priceServer;

    //set up connection to outside trading software for market data, orders, etc:
    public OutsideTradingSoftwareAPIConnection marketConnection;

    //handles all orders, checking for order fills:
    private OrderServer orderServer;

    private GUIUpdateDispatcher myGUIUpdateDispatcher;
    private long count = 0;
    public static boolean interrupted;
    int refreshGUIInMiliseconds = 200;
    long mainLoopTurnaroundTime = 0;


//    PriceServerManager priceServerManager;

    public void shutDownMikeSim(){
        interrupted = true;
    }

    public MainModelThread(MainGUIClass mainGUIClass){
        this.mainGUIClass = mainGUIClass;

        //provides prices for the program:
        priceServer = new PriceServer();

        //TODO: experimenting here:
        //set up connection to outside trading software for market data, orders, etc:
        marketConnection = new InteractiveBrokersAPI();

        //let priceserver know about real time market data:
        priceServer.setRealTimeDataSource(marketConnection);

        orderServer = new OrderServer();
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
                orderServer.checkSimulatedFills(priceServer);

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
    private void processOrders(){}

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
