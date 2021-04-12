package main.java.controllerandview.windowcontrollers;

import com.ib.client.Contract;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import main.java.controllerandview.MainGUIClass;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.priceserver.PriceServer;

public class ControllerMainGUIWindow {

    public ListView instrumentsList;
    public TextField histDataDuration;
    public TextField histDataDate;
    public TextField histDataTime;
    public TextField mainloopCountTextField;
    public TextField mainloopSpeedTextField;
    private MainGUIClass mainGUIClass;
    private MainModelThread model;

    public void setModel(MainModelThread model) {
        this.model = model;
    }

    public void setMainGUIClass(MainGUIClass controller){
        mainGUIClass = controller;
    }

    @FXML
    public void createPosWindowsButtonClicked(){
        MikeSimLogger.addLogEvent("Creating new PositionsWindow!");
        int xPos, yPos;
        xPos = 0;
        yPos = 0;
        mainGUIClass.createPosWindow(xPos, yPos);
    }

    @FXML
    public void createPriceContrWinClicked(ActionEvent actionEvent) {
        mainGUIClass.createPriceControlWindow();
    }

    @FXML
    public void connectOutsideDataClicked(){
        mainGUIClass.getMainModelThread().connectOutsideData();
    }

    /**
     * Creates a new MikePosOrders for trading the instrument selected from instrumentList
     * @param event
     */
    public void createPositionOrdersClicked(ActionEvent event) {

        int tickerId = 0;

        if (instrumentsList.getSelectionModel().getSelectedItem() != null) {
            tickerId =  ((PriceServer) instrumentsList.getSelectionModel().getSelectedItem())
                    .getTickerID();
        }

        model.posOrdersManager.createMikePosorders(tickerId);
    }

    public void createAlgoManagerWindow(ActionEvent actionEvent) {
        mainGUIClass.createAlgoManagerWindow();
    }

    /**
     * Requests historical data from TWS API. TWS limits "duraion" parameter to no more than 3000.
     * duration is currently hardcoded as expressed in number of seconds.
     * Look into docs for reqHistoricalData() in TWS API for details
     * @param actionEvent
     */
    public void reqHistDataClicked(ActionEvent actionEvent) {
        //todo:
        //TWS limits "duration" to 3000. think of a way to split requests over 3000 to smaller
        //consecutive ones of 3000 each

        Contract contract = new Contract();

        contract.m_symbol = "SPY";
        contract.m_exchange = "SMART";
        contract.m_secType = "STK";
        contract.m_currency = "USD";

        String endDateTime = "";
        endDateTime += (histDataDate.getText() + " ");
        endDateTime += (histDataTime.getText() + " GMT");

        String duration = "";
        duration += histDataDuration.getText();
        duration += " S";

        model.marketConnection.getEClientSocket().reqHistoricalData(0, contract, endDateTime,  duration, "1 secs", "BID", 1, 1);
    }

    public void createGuardAlgoClicked(ActionEvent actionEvent) {
        mainGUIClass.createGuardAlgoWindow();
    }

    public void createMultipleStepperAlgoWindowClicked(ActionEvent actionEvent) {
        mainGUIClass.createMultipleStepperAlgoWindow();
    }


    /**
     * Centers all the MikeGridPanes in all MikeposWindows and current ask/bid
     * @param actionEvent
     */
    public void ask50everywherePressed(ActionEvent actionEvent) {
        for (ControllerPositionsWindow controller : mainGUIClass.controllerPositionsWindowList){
            controller.ask50Clicked(actionEvent);
        }
    }

    public void create6PosWinOn4KClicked(ActionEvent actionEvent) {
        MikeSimLogger.addLogEvent("Creating 6 PositionsWindows");

        //indexes are defined in main.java.model.MainModelThread.setupInitialPosOrders()

        ControllerPositionsWindow controller = null;

        controller = mainGUIClass.createPosWindow((640*0),0);
        controller.positionsList.getSelectionModel().clearAndSelect(1);
        controller.targetPositionsList.getSelectionModel().clearAndSelect(3);

        controller = mainGUIClass.createPosWindow((640*1),0);
        controller.positionsList.getSelectionModel().clearAndSelect(2);
        controller.targetPositionsList.getSelectionModel().clearAndSelect(3);

        controller =  mainGUIClass.createPosWindow((640*2),0);
        controller.positionsList.getSelectionModel().clearAndSelect(3);
        controller.targetPositionsList.getSelectionModel().clearAndSelect(7);
//
//        mainGUIClass.createPosWindow((640*3),0);
//        mainGUIClass.createPosWindow((640*4),0);
//        mainGUIClass.createPosWindow((640*5),0);

        controller = mainGUIClass.createPosWindow((640*0),1000);
        controller.positionsList.getSelectionModel().clearAndSelect(4);
        controller.targetPositionsList.getSelectionModel().clearAndSelect(6);

        controller = mainGUIClass.createPosWindow((640*1),1000);
        controller.positionsList.getSelectionModel().clearAndSelect(5);
        controller.targetPositionsList.getSelectionModel().clearAndSelect(6);

        controller =  mainGUIClass.createPosWindow((640*2),1000);
        controller.positionsList.getSelectionModel().clearAndSelect(6);
        controller.targetPositionsList.getSelectionModel().clearAndSelect(7);

//        mainGUIClass.createPosWindow((640*3),1000);
//        mainGUIClass.createPosWindow((640*4),1000);
//        mainGUIClass.createPosWindow((640*5),1000);
    }
}