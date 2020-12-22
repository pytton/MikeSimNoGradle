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
import main.java.controllerandview.MainGUIClass;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.algocontrol.GuardAlgoDown;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

public class ControllerGuardAlgoPane1 extends AlgoController implements CommonGUI.ICommonGUI, MainGUIClass.Updatable {

    private MainModelThread model;
    private MikePosOrders monitoredPosOrders = null; //this is the Positions that are being watched by the algo
    private MikePosOrders orderTargetPosOrders = null; //this is where the orders are being sent to. it can be the same as monitoredPosOrders
    private GuardAlgoDown guardAlgoDOWNThatIsControlled = null;
    private GuardAlgoDown guardAlgoUPThatIsControlled = null;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private Button guardedPosition; // Value injected by FXMLLoader

    @FXML
    private Button ordersSentTo; // Value injected by FXMLLoader

    /**
     * if this is checked, orders will be sent to the same MikePosOrders as the ones being monitored
     */
    @FXML
    private CheckBox targetSameAsMonitoredAuto;

    @FXML
    private TextField bufferDistanceUp; // Value injected by FXMLLoader

    @FXML
    private Button bufferSetUp; // Value injected by FXMLLoader

    @FXML
    private Button restartUpBtn; // Value injected by FXMLLoader

    @FXML
    private Button suspendUpBtn; // Value injected by FXMLLoader

    @FXML
    private Button statusAndCreateUpBtn; // Value injected by FXMLLoader

    @FXML
    private TextField bufferDistanceDown; // Value injected by FXMLLoader

    @FXML
    private Button bufferSetDown; // Value injected by FXMLLoader

    @FXML
    private Button restartDownBtn; // Value injected by FXMLLoader

    @FXML
    private Button suspendDownBtn; // Value injected by FXMLLoader

    @FXML
    private Button statusAndCreateDownBtn; // Value injected by FXMLLoader

    @FXML
    void restartDownBtnPressed(ActionEvent event) {
        if(guardAlgoDOWNThatIsControlled != null) guardAlgoDOWNThatIsControlled.cancelOrdersAndRestart();
    }

    @FXML
    void restartUpBtnPressed(ActionEvent event) {

    }

    @FXML
    void bufferSetDownBtnPressed(ActionEvent event) {
        MikeSimLogger.addLogEvent("Not implemented");
    }

    @FXML
    void bufferSetUpBtnPressed(ActionEvent event) {
        MikeSimLogger.addLogEvent("Not implemented");
    }


    /**
     * This must be called in a loop by someone to update all the information displayed
     */
    @Override
    public void updateGUI() {
        //todo: finish this
        //check the status of the algo and print it out ot the buttons:


        colorButtonAccordingToStatus();


    }

    private void colorButtonAccordingToStatus() {
        if(guardAlgoDOWNThatIsControlled == null) {
            if(monitoredPosOrders != null) {
                statusAndCreateDownBtn.setText("Click to create new");
                statusAndCreateDownBtn.setStyle("-fx-background-color: grey");
            }
            return;
        }

        String status = guardAlgoDOWNThatIsControlled.getStatus();
        statusAndCreateDownBtn.setText(status);
        if (status.contentEquals("CREATED")) statusAndCreateDownBtn.setStyle("-fx-background-color: lightblue");
        if (status.contentEquals("RUNNING")) statusAndCreateDownBtn.setStyle("-fx-background-color: green");
        if (status.contentEquals("FAILED")) statusAndCreateDownBtn.setStyle("-fx-background-color: red");
        if (status.contentEquals("SUSPENDED")) statusAndCreateDownBtn.setStyle("-fx-background-color: yellow");
    }

    /**
     * Pressing this button will chose the MikePosOrders being monitored by the algo
     * @param event
     */
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

        //if a GuardAlgoDown monitoring selected MikePosOrders already exists, update it here:
        try {
            guardAlgoDOWNThatIsControlled = model.algoManager.getGuardAlgoDownForMikePosOrders(monitoredPosOrders);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Pressing this button will choose the MikePosOrders the algo will send algos to.
     * If "auto" checkbox is chosen it will be the same as the monitored MikePosOrders.
     * "Auto" is enabled by default.
     * @param event
     */
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
    }

    @FXML
    void statusAndCreateDownBtnPressed(ActionEvent event) {

        if(monitoredPosOrders == null) {
            statusAndCreateDownBtn.setText("Choose PosOrders first!");
            return;
        }
        MikeSimLogger.addLogEvent("Create Down GuardAlgoDown button pressed");

        //IF THERE IS A GUARD ALGO MONITORING CHOSEN MIKEPOSORDERS ALREADY - CHOOSE IT INSTEAD OF CREATING NEW ONE
        GuardAlgoDown algo = null;
        try {
            //this will return null if algo controlling monitoredPosOrders doesn't exist
            algo = model.algoManager.getGuardAlgoDownForMikePosOrders(monitoredPosOrders);
        } catch (Exception e) {
            e.printStackTrace();
        }
        guardAlgoDOWNThatIsControlled = algo;

        if(guardAlgoDOWNThatIsControlled == null) {
            createAlgo();
        }
        else {
            MikeSimLogger.addLogEvent("GuardAlgoDown controlling " + monitoredPosOrders.getName() + " already exists");
        }
    }

    /**
     * creates a new algo and gives this controller access to it.
     * if userinput is missing, broken or negative sets the guardbuffer to an arbitrary value
     */
    private void createAlgo() {
        if(monitoredPosOrders == null) return;
        if(orderTargetPosOrders == null) return;
        MikeSimLogger.addLogEvent("Creating GuardAlgoDown");
        int guardBuffer = 15; //arbitrary default value
        int userInput;
        try {
            userInput = Integer.parseInt(bufferDistanceDown.getText());
            if(userInput > 0) guardBuffer = userInput;
        } catch (NumberFormatException e) {
            MikeSimLogger.addLogEvent("Exeption in ControllerGuardAlgoPane1.createAlgo");
            e.printStackTrace();
        }

        //create the algo
        guardAlgoDOWNThatIsControlled = model.algoManager.createGuardAlgoDown(monitoredPosOrders, orderTargetPosOrders, guardBuffer);

    }

    @FXML
    void statusAndCreateUpBtnPressed(ActionEvent event) {
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED");
    }

    @FXML
    void suspendDownBtnPressed(ActionEvent event) {
        if(guardAlgoDOWNThatIsControlled != null) guardAlgoDOWNThatIsControlled.suspend();
    }

    @FXML
    void suspendUpBtnPressed(ActionEvent event) {
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED");
    }

    public void setModel(MainModelThread model) {
        this.model = model;
    }

    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        guardAlgoDOWNThatIsControlled.cancel();
        guardAlgoUPThatIsControlled.cancel();
        return true;
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
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED");

    }

    @Override
    public PriceServer getPriceServer() {
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED");
        return null;
    }

    @Override
    public void setMikePosOrders(MikePosOrders posOrders) {
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert guardedPosition != null : "fx:id=\"guardedPosition\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert ordersSentTo != null : "fx:id=\"ordersSentTo\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferDistanceUp != null : "fx:id=\"bufferDistanceUp\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferSetUp != null : "fx:id=\"bufferSetUp\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert restartUpBtn != null : "fx:id=\"activateBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert suspendUpBtn != null : "fx:id=\"suspendBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert statusAndCreateUpBtn != null : "fx:id=\"statusBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferDistanceDown != null : "fx:id=\"bufferDistanceDown\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferSetDown != null : "fx:id=\"bufferSetDown\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert restartDownBtn != null : "fx:id=\"activateDownBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert suspendDownBtn != null : "fx:id=\"suspendDownBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert statusAndCreateDownBtn != null : "fx:id=\"statusDownBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";

    }

}
