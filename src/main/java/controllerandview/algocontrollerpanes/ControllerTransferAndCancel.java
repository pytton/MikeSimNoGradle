package main.java.controllerandview.algocontrollerpanes;

import com.sun.org.apache.bcel.internal.generic.RET;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.java.controllerandview.CommonGUI;
import main.java.controllerandview.MikeGridPane;
import main.java.controllerandview.windowcontrollers.ControllerPositionsWindow;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

/**
 *
 */
public class ControllerTransferAndCancel extends AlgoController {

    @FXML
    public ToggleGroup actionTypeToggleGroup;
    public RadioButton movePosDownOrUp;
    public RadioButton transferBelowOrAbove;
    public RadioButton cancelBelowOrAbove;
    private ControllerPositionsWindow controllerPositionsWindow;

    private String descriptionRow1 = "TRNSF:";
    private String descriptionRow2 = "DN / UP";

    @FXML
    public void initialize() {
        //setup the kind of order passed to algoManager depending on which radiobutton is pressed:
        actionTypeToggleGroup.selectedToggleProperty().addListener(
                new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (actionTypeToggleGroup.getSelectedToggle() == movePosDownOrUp) { descriptionRow1 = "MOVE"; }
                if (actionTypeToggleGroup.getSelectedToggle() == transferBelowOrAbove) { descriptionRow1 = "TRNSF"; }
                if (actionTypeToggleGroup.getSelectedToggle() == cancelBelowOrAbove) { descriptionRow1 = "CX DW/UP"; }
             } } );
    }

    @Override
    public String getSimpleDescriptionRow1() {

//        if(multipleCheckBox.isSelected()) return "MULT ORD";
        return descriptionRow1;
    }

    @Override
    public String getSimpleDescriptionRow2() {
        //todo: need to add a method to ControllerPositionsWindow which returns the target PosOrders to make this nice
        return descriptionRow2;
    }

    @Override
    public String getSimpleDescriptionRow3() {
        return " ";
    }

    /**
     * This method handles clicks inside MikeGridPane
     * @param pricePressed
     * @param model
     * @param posOrders
     * @param button
     * @param event
     */
    @Override
    public void mikeGridPaneButtonPressed(int pricePressed, MainModelThread model, MikePosOrders posOrders,
                                          MikeGridPane.MikeButton button,
                                          MouseEvent event) {

        MikeSimLogger.addLogEvent("Inside ControllerTransferAndCancel");

        if (actionTypeToggleGroup.getSelectedToggle() == movePosDownOrUp) processMovePosClicked(pricePressed, model,
                posOrders, button, event);
        if (actionTypeToggleGroup.getSelectedToggle() == transferBelowOrAbove) processTransferClicked(pricePressed, model,
                posOrders, button, event);
        if (actionTypeToggleGroup.getSelectedToggle() == cancelBelowOrAbove) processCancelBelowOrAboveClicked(pricePressed, model,
                posOrders, button, event);




        if (event.getButton() == MouseButton.SECONDARY) {
            MikeSimLogger.addLogEvent("right button recognized in ControllerTransferAndCancel");
        }

    }

    private void processCancelBelowOrAboveClicked(int pricePressed, MainModelThread model, MikePosOrders posOrders, MikeGridPane.MikeButton button, MouseEvent event) {

        //if left button was clicked, cancel all orders at or below the price that was pressed
        if (event.getButton() == MouseButton.PRIMARY) {
                MikeSimLogger.addLogEvent("cancelling all orders BELOW " + pricePressed + " not implemented");
        }

        //if right was clicked, cancel all orders above pricePressed
        if (event.getButton() == MouseButton.SECONDARY) {
            MikeSimLogger.addLogEvent("cancelling all orders ABOVE " + pricePressed + " not implemented");
        }

    }

    private void processTransferClicked(int pricePressed, MainModelThread model, MikePosOrders posOrders, MikeGridPane.MikeButton button, MouseEvent event) {
        //if left button was clicked, transfer all MikePositions at or below the price that was pressed
        if (event.getButton() == MouseButton.PRIMARY) {
            MikeSimLogger.addLogEvent("transferring all positions BELOW " + pricePressed + " from: " + posOrders +  " not implemented");
        }

        //transfer all MikePositions above if right button clicked
        if (event.getButton() == MouseButton.SECONDARY) {
            MikeSimLogger.addLogEvent("transferring all positions ABOVE " + pricePressed + " from: " + posOrders +  " not implemented");
        }
    }

    private void processMovePosClicked(int pricePressed, MainModelThread model, MikePosOrders posOrders, MikeGridPane.MikeButton button, MouseEvent event) {

        //if left button was clicked move the single MikePosition at that price to the next MikePosition at a lower price, or if none exists, one cent lower
        if (event.getButton() == MouseButton.PRIMARY) {
            MikeSimLogger.addLogEvent("Moving single MikePosition at price " + pricePressed + " lower not implemented");
        }

        //move higher if right button clicked
        if (event.getButton() == MouseButton.SECONDARY) {
            MikeSimLogger.addLogEvent("Moving single MikePosition at price " + pricePressed + " higher not implemented");
        }


    }


//    private int getAmount() {
//    return 0;
//    }

    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }

    //todo:
    public void setControllerPositionsWindow(ControllerPositionsWindow controllerPositionsWindow) {
        this.controllerPositionsWindow = controllerPositionsWindow;
    }
}
