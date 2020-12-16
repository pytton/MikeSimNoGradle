/**
 * Sample Skeleton for 'GuardAlgoPane1.fxml' Controller Class
 */

package main.java.controllerandview.algocontrollers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import main.java.controllerandview.CommonGUI;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.mikealgos.MikeAlgo;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

public class ControllerGuardAlgoPane1 extends AlgoController implements CommonGUI.ICommonGUI {

    private MainModelThread model;
    private MikePosOrders monitoredPosOrders = null; //this is the Positions that are being watched by the algo
    private MikePosOrders orderTargetPosOrders = null; //this is where the orders are being sent to. it can be the same as monitoredPosOrders

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="guardedPosition"
    private Button guardedPosition; // Value injected by FXMLLoader

    @FXML // fx:id="ordersSentTo"
    private Button ordersSentTo; // Value injected by FXMLLoader

    /**
     * if this is checked, orders will be sent to the same MikePosOrders as the ones being monitored
     */
    @FXML
    private CheckBox targetSameAsMonitoredAuto;

    @FXML // fx:id="bufferDistanceUp"
    private TextField bufferDistanceUp; // Value injected by FXMLLoader

    @FXML // fx:id="bufferSetUp"
    private Button bufferSetUp; // Value injected by FXMLLoader

    @FXML // fx:id="activateBtn"
    private Button activateBtn; // Value injected by FXMLLoader

    @FXML // fx:id="suspendBtn"
    private Button suspendBtn; // Value injected by FXMLLoader

    @FXML // fx:id="statusBtn"
    private Button statusBtn; // Value injected by FXMLLoader

    @FXML // fx:id="bufferDistanceDown"
    private TextField bufferDistanceDown; // Value injected by FXMLLoader

    @FXML // fx:id="bufferSetDown"
    private Button bufferSetDown; // Value injected by FXMLLoader

    @FXML // fx:id="activateDownBtn"
    private Button activateDownBtn; // Value injected by FXMLLoader

    @FXML // fx:id="suspendDownBtn"
    private Button suspendDownBtn; // Value injected by FXMLLoader

    @FXML // fx:id="statusDownBtn"
    private Button statusDownBtn; // Value injected by FXMLLoader

    @FXML
    void activateDownBtnPressed(ActionEvent event) {

    }

    @FXML
    void activateUpBtnPressed(ActionEvent event) {

    }

    @FXML
    void bufferSetDownBtnPressed(ActionEvent event) {

    }

    @FXML
    void bufferSetUpBtnPressed(ActionEvent event) {

    }

    @FXML
    void guardedPositionBtnPressed(ActionEvent event) {
        MikeSimLogger.addLogEvent("Guarded position button pressed");
        //we need to choose the instrument traded:
        PriceServer priceServer = CommonGUI.setPriceServer(this, model);

        //set the MikePosOrders being guarded:
        monitoredPosOrders = CommonGUI.setMikePos(this, model, priceServer);

        //display the name of the MikePosOrders being guarded:
        guardedPosition.setText(monitoredPosOrders.getName());

        //if the checkbox is selected, change the MikePosOrders orders will be sent to automatically:
        if(targetSameAsMonitoredAuto.isSelected()) orderTargetPosOrders = monitoredPosOrders;
        if (orderTargetPosOrders != null) {
            ordersSentTo.setText(orderTargetPosOrders.getName());
        }

        //todo:
        //if the algo has been created, update the algo with the new values:


    }

    @FXML
    void ordersSentToBtnPressed(ActionEvent event) {
        MikeSimLogger.addLogEvent("Orders target button pressed");

        //we are selecting target MikePosOrders different than monitored ones. do not autoselect in future:
        targetSameAsMonitoredAuto.setSelected(false);
        //get the instrument traded:
        PriceServer priceServer = CommonGUI.setPriceServer(this, model);

        //set the MikePosOrders where the algo will send its orders
        orderTargetPosOrders = CommonGUI.setMikePos(this, model, priceServer);

        if (orderTargetPosOrders != null) {
            ordersSentTo.setText(orderTargetPosOrders.getName());
        }


//        MikeAlgo.status status = MikeAlgo.status.CREATED;

    }

    @FXML
    void statusDownBtnPressed(ActionEvent event) {

    }

    @FXML
    void statusUpBtnPressed(ActionEvent event) {

    }

    @FXML
    void suspendDownBtnPressed(ActionEvent event) {

    }

    @FXML
    void suspendUpBtnPressed(ActionEvent event) {

    }

    public void setModel(MainModelThread model) {
        this.model = model;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert guardedPosition != null : "fx:id=\"guardedPosition\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert ordersSentTo != null : "fx:id=\"ordersSentTo\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferDistanceUp != null : "fx:id=\"bufferDistanceUp\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferSetUp != null : "fx:id=\"bufferSetUp\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert activateBtn != null : "fx:id=\"activateBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert suspendBtn != null : "fx:id=\"suspendBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert statusBtn != null : "fx:id=\"statusBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferDistanceDown != null : "fx:id=\"bufferDistanceDown\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferSetDown != null : "fx:id=\"bufferSetDown\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert activateDownBtn != null : "fx:id=\"activateDownBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert suspendDownBtn != null : "fx:id=\"suspendDownBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert statusDownBtn != null : "fx:id=\"statusDownBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";

    }

    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        //todo: finish this:
        MikeSimLogger.addLogEvent("Not implemented!");
        return false;
    }

    @Override
    public void mikeGridPaneButtonPressed(int pricePressed, MainModelThread model, MikePosOrders posOrders) {
        MikeSimLogger.addLogEvent("Not implemented!");
    }

    @Override
    public String getSimpleDescriptionRow1() {
        MikeSimLogger.addLogEvent("Not implemented!");
        return null;
    }

    @Override
    public String getSimpleDescriptionRow2() {
        MikeSimLogger.addLogEvent("Not implemented!");
        return null;
    }

    @Override
    public String getSimpleDescriptionRow3() {
        MikeSimLogger.addLogEvent("Not implemented!");
        return null;
    }

    @Override
    public void setPriceServer(PriceServer priceServer) {

    }

    @Override
    public PriceServer getPriceServer() {
        return null;
    }

    @Override
    public void setMikePosOrders(MikePosOrders posOrders) {

    }
}
