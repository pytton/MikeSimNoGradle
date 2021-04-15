package main.java.model;

import javafx.application.Platform;
import main.java.controllerandview.MainGUIClass;
import main.java.model.livemarketdata.InteractiveBrokersAPI;
import main.java.model.livemarketdata.OutsideTradingSoftwareAPIConnection;
import main.java.model.algocontrol.AlgoManager;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.positionsorders.PosOrdersManager;

import java.util.Map;

public class MainModelThread extends Thread {

    public MainGUIClass mainGUIClass;
    public PosOrdersManager posOrdersManager;
    public TradingDayStatistics tradingStatistics;

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
        posOrdersManager = new PosOrdersManager(this, tradedInstrumentMap /*this*/);
        algoManager = new AlgoManager(this);
        tradingStatistics = new TradingDayStatistics(0);
        setupInitialPosOrders();
    }

    /**
     * Convenience method
     * Creates a standard set of MikePositionsOrders for trading
     */
    private void setupInitialPosOrders() {
        MikePosOrders posOrders = posOrdersManager.createMikePosorders(0);
        posOrders.setName("Long Orders");
        posOrders = posOrdersManager.createMikePosorders(0);
        posOrders.setName("Long Scalper");
        posOrders = posOrdersManager.createMikePosorders(0);
        posOrders.setName("Long Bank");
        posOrders = posOrdersManager.createMikePosorders(0);
        posOrders.setName("Short Orders");
        posOrders = posOrdersManager.createMikePosorders(0);
        posOrders.setName("Short Scalper");
        posOrders = posOrdersManager.createMikePosorders(0);
        posOrders.setName("Short Bank");

        posOrders = posOrdersManager.createMikePosorders(0);
        posOrders.setName("Booked Profits");
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
                    tradingStatistics.calculate(posOrdersManager);
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
        for(PosOrdersManager.Data data: posOrdersManager.getDataMap().values()){
            //check for fills in orderserver of every traded instrument:
            data.getOrderServer().checkFills(data.getPriceServer());

            //go through all the MikePosOrders, process filled orders and recalculate the PL for each of them:
            for(MikePosOrders positions : data.getPosOrdersObservableList()){
                positions.processFilledOrders();
                positions.recalcutlatePL();
            }
            //calulate global current P/L, highest P/L and highest loss, open long/short positions

            //process historical data:
            data.getPriceServer().processHistoricalData();
        }
    }

    synchronized public void connectOutsideData(){
        marketConnection.connect();
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
                if (count%10 == 0) {

                    mainGUIClass.controllerPrimaryGUIWindow.mainloopCountTextField.setText("" + count);
                    mainGUIClass.controllerPrimaryGUIWindow.mainloopSpeedTextField.setText("" + mainLoopTurnaroundTime);
                    if(count%600 == 0) MikeSimLogger.printTradingStatistics(tradingStatistics);



//                    MikeSimLogger.addLogEvent("Mainloop count: " + count + " MainLoop turnaround in miliseconds: " + mainLoopTurnaroundTime);

                }

//                    MikeSimLogger.addLogEvent("Mainloop count: " + count + " MainLoop turnaround in miliseconds: " + mainLoopTurnaroundTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean isReady() {
            return isReady;
        }
    }
}

