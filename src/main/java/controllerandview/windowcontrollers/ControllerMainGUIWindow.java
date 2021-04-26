package main.java.controllerandview.windowcontrollers;

import com.ib.client.Contract;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import main.java.controllerandview.MainGUIClass;
import main.java.controllerandview.algocontrollerpanes.ControllerPlainOrder;
import main.java.controllerandview.algocontrollerpanes.ControllerSimpleScalperAlgo;
import main.java.controllerandview.algocontrollerpanes.ControllerSimpleStepperAlgo;
import main.java.controllerandview.algocontrollerpanes.ControllerTransferAndCancel;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.priceserver.PriceServer;

public class ControllerMainGUIWindow implements MainGUIClass.Updatable {

    public ListView instrumentsList;
    public TextField histDataDuration;
    public TextField histDataDate;
    public TextField histDataTime;
    public TextField mainloopCountTextField;
    public TextField mainloopSpeedTextField;
    public Label openPosLabel;
    public Label currentPLLabel;
    public Label longMaxLabel;
    public Label shortMaxLabel;
    public Label profitMaxLabel;
    public Label lossMaxLabel;
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
        mainGUIClass.createPosWindow(xPos, yPos, true);
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

        ControllerPositionsWindow windowController = null;
        ControllerPlainOrder plainOrder = null;
        ControllerSimpleStepperAlgo stepper = null;
        ControllerSimpleScalperAlgo scalper = null;
        ControllerTransferAndCancel transferAndCancel = null;


        //create the first window and setup its column actions for stepper algo long:
        windowController = mainGUIClass.createPosWindow((640*0),0);
        windowController.positionsList.getSelectionModel().clearAndSelect(1);
        windowController.targetPositionsList.getSelectionModel().clearAndSelect(4);
        stepper = WindowControllerCommon.setupSimpleStepperAlgoInColumn(windowController, 1);
        stepper.buyStop.fire();
        plainOrder = WindowControllerCommon.setupPlainOrderInColumn(windowController, 2);
        plainOrder.multipleCheckBox.setSelected(false);
        plainOrder.trailingStopCheckbox.setSelected(true);
        plainOrder.sellStop.fire();
        plainOrder.size1of8.fire();
        transferAndCancel = WindowControllerCommon.setupTransferAndCancelInColumn(windowController, 4);
        transferAndCancel.cancelBelowOrAbove.fire();
        transferAndCancel = WindowControllerCommon.setupTransferAndCancelInColumn(windowController, 7);
        transferAndCancel.transferBelowOrAbove.fire();

        //create second window for scalper short:
        windowController = mainGUIClass.createPosWindow((640*1),0);
        windowController.positionsList.getSelectionModel().clearAndSelect(2);
        windowController.targetPositionsList.getSelectionModel().clearAndSelect(1);
        scalper = WindowControllerCommon.setupSimpleScalperInColumn(windowController, 1);
        scalper.sellLimit.fire();
        transferAndCancel = WindowControllerCommon.setupTransferAndCancelInColumn(windowController, 4);
        transferAndCancel.cancelBelowOrAbove.fire();
        transferAndCancel = WindowControllerCommon.setupTransferAndCancelInColumn(windowController, 7);
        transferAndCancel.transferBelowOrAbove.fire();

        //create third window with default actions:
        windowController =  mainGUIClass.createPosWindow((640*2),0);
        windowController.positionsList.getSelectionModel().clearAndSelect(3);
        windowController.targetPositionsList.getSelectionModel().clearAndSelect(7);
        WindowControllerCommon.setupDefaultColumnActionsInControllerPositionsWindow(windowController);

        //fourth window stepper algo short:
        windowController = mainGUIClass.createPosWindow((640*0),1050);
        windowController.positionsList.getSelectionModel().clearAndSelect(4);
        windowController.targetPositionsList.getSelectionModel().clearAndSelect(1);
        stepper = WindowControllerCommon.setupSimpleStepperAlgoInColumn(windowController, 1);
        stepper.sellStop.fire();
        plainOrder = WindowControllerCommon.setupPlainOrderInColumn(windowController, 2);
        plainOrder.multipleCheckBox.setSelected(false);
        plainOrder.trailingStopCheckbox.setSelected(true);
        plainOrder.buyStop.fire();
        plainOrder.size1of8.fire();
        transferAndCancel = WindowControllerCommon.setupTransferAndCancelInColumn(windowController, 4);
        transferAndCancel.cancelBelowOrAbove.fire();
        transferAndCancel = WindowControllerCommon.setupTransferAndCancelInColumn(windowController, 7);
        transferAndCancel.transferBelowOrAbove.fire();

        //fifth window for scalper long:
        windowController = mainGUIClass.createPosWindow((640*1),1050);
        windowController.positionsList.getSelectionModel().clearAndSelect(5);
        windowController.targetPositionsList.getSelectionModel().clearAndSelect(1);
        scalper = WindowControllerCommon.setupSimpleScalperInColumn(windowController, 1);
        scalper.buyLimit.fire();
        transferAndCancel = WindowControllerCommon.setupTransferAndCancelInColumn(windowController, 4);
        transferAndCancel.cancelBelowOrAbove.fire();
        transferAndCancel = WindowControllerCommon.setupTransferAndCancelInColumn(windowController, 7);
        transferAndCancel.transferBelowOrAbove.fire();

        //sixth window with default actions:
        windowController = mainGUIClass.createPosWindow((640*2),1050);
        windowController.positionsList.getSelectionModel().clearAndSelect(6);
        windowController.targetPositionsList.getSelectionModel().clearAndSelect(7);
        WindowControllerCommon.setupDefaultColumnActionsInControllerPositionsWindow(windowController);


//        mainGUIClass.createPosWindow((640*3),1050);
//        mainGUIClass.createPosWindow((640*4),1050);
//        mainGUIClass.createPosWindow((640*5),1050);
    }

    @Override
    public void updateGUI() {
        openPosLabel.setText("" + model.tradingStatistics.getCurrentGlobalOpenPos());
        currentPLLabel.setText("" + model.tradingStatistics.getCurrentGlobalPL());
        longMaxLabel.setText("" + model.tradingStatistics.getHighestOpenLongPosition());
        shortMaxLabel.setText("" + model.tradingStatistics.getHighestOpenShortPosition());
        profitMaxLabel.setText("" + model.tradingStatistics.getMaxProfit());
        lossMaxLabel.setText("" + model.tradingStatistics.getMaxLoss());
    }
}