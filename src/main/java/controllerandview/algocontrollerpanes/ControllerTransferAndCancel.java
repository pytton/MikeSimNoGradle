package main.java.controllerandview.algocontrollerpanes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.java.controllerandview.MikeGridPane;
import main.java.controllerandview.windowcontrollers.ControllerPositionsWindow;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.positionsorders.MikePosOrders;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ControllerTransferAndCancel extends AlgoController {

    @FXML
    public ToggleGroup actionTypeToggleGroup;
    public RadioButton movePosDownOrUp;
    public RadioButton transferBelowOrAbove;
    public RadioButton cancelBelowOrAbove;
    public Label trnsfTargetName;
    private ControllerPositionsWindow controllerPositionsWindow;

    private String descriptionRow1 = "TRNSF:";
    private String descriptionRow2 = "DN / UP";
    private String descriptionRow3 = "TRANSFER";

    @FXML
    public void initialize() {
//        movePosDownOrUp.setText("\u1F5D");
        //setup the kind of order passed to algoManager depending on which radiobutton is pressed:
        actionTypeToggleGroup.selectedToggleProperty().addListener(
                new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                try {
                    if (actionTypeToggleGroup.getSelectedToggle() == movePosDownOrUp) { descriptionRow1 = "MOVE"; descriptionRow3 = "MOVE";}
                    if (actionTypeToggleGroup.getSelectedToggle() == transferBelowOrAbove) { descriptionRow1 = "TRNSF"; descriptionRow3 = controllerPositionsWindow.getTargetMikePosOrders().getName();}
                    if (actionTypeToggleGroup.getSelectedToggle() == cancelBelowOrAbove) { descriptionRow1 = "CX DW/UP"; descriptionRow3 = "CANCEL";}
                } catch (Exception e) {
                    MikeSimLogger.addLogEvent("Exception in main.java.controllerandview.algocontrollerpanes.ControllerTransferAndCancel.initialize");
                }
            } } );
    }

    @Override
    public String getSimpleDescriptionRow1() {

        //update the label here in this controller:
        trnsfTargetName.setText(controllerPositionsWindow.getTargetMikePosOrders().getName());

        //and give them what they want
        return descriptionRow1;
    }

    @Override
    public String getSimpleDescriptionRow2() {
        //todo: need to add a method to ControllerPositionsWindow which returns the target PosOrders to make this nice
        return descriptionRow2;
    }

    @Override
    public String getSimpleDescriptionRow3() {
        return descriptionRow3;
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
    public void mikeGridPaneButtonPressed(ControllerPositionsWindow controllerPositionsWindow,
                                          int pricePressed, MainModelThread model, MikePosOrders posOrders,
                                          MikeGridPane.MikeButton button,
                                          MouseEvent event) {

//        MikeSimLogger.addLogEvent("Inside ControllerTransferAndCancel");

        if (actionTypeToggleGroup.getSelectedToggle() == movePosDownOrUp) processMovePosClicked(pricePressed, model,
                posOrders, button, event);
        if (actionTypeToggleGroup.getSelectedToggle() == transferBelowOrAbove) processTransferClicked(pricePressed, model,
                posOrders, button, event);
        if (actionTypeToggleGroup.getSelectedToggle() == cancelBelowOrAbove) processCancelBelowOrAboveClicked(pricePressed, model,
                posOrders, button, event);




        if (event.getButton() == MouseButton.SECONDARY) {
//            MikeSimLogger.addLogEvent("right button recognized in ControllerTransferAndCancel");
        }

    }

    private void processCancelBelowOrAboveClicked(int pricePressed, MainModelThread model, MikePosOrders posOrders, MikeGridPane.MikeButton button, MouseEvent event) {

        //if left button was clicked, cancel all orders at or below the price that was pressed
        if (event.getButton() == MouseButton.PRIMARY) {
                MikeSimLogger.addLogEvent("cancelling all orders BELOW " + pricePressed );
                posOrders.cancelAllOrdersAtOrBELOWPrice(pricePressed);
        }

        //if right was clicked, cancel all orders above pricePressed
        if (event.getButton() == MouseButton.SECONDARY) {
            MikeSimLogger.addLogEvent("cancelling all orders ABOVE " + pricePressed );
            posOrders.cancelAllOrdersAtOrABOVEPrice(pricePressed);
        }

    }

    private void processTransferClicked(int pricePressed, MainModelThread model, MikePosOrders posOrders, MikeGridPane.MikeButton button, MouseEvent event) {


        MikePosOrders targetPosOrders = controllerPositionsWindow.getTargetMikePosOrders();

        //this will store all the prices of the MikePositions that we want to move:
        Set<Integer> positionPricesToMove = new HashSet<>();

        //if left button was clicked, transfer all MikePositions at or below the price that was pressed
        if (event.getButton() == MouseButton.PRIMARY) {

            //find all the MikePositions at or below the price that has been specified and add them to the transfer list
            for (int positionPrice : posOrders.getPositionPricesSet()){
                if(positionPrice <= pricePressed) positionPricesToMove.add(positionPrice);
            }

            MikeSimLogger.addLogEvent("transferring all positions BELOW " + pricePressed + " from: " + posOrders +
                    " to " + targetPosOrders);
        }




        //if right button clicked - transfer all MikePositions at or above the price that was pressed
        if (event.getButton() == MouseButton.SECONDARY) {
            //find all the MikePositions at or above the price that has been specified and add them to the transfer list
            for (int positionPrice : posOrders.getPositionPricesSet()){
                if(positionPrice >= pricePressed) positionPricesToMove.add(positionPrice);
            }
            MikeSimLogger.addLogEvent("transferring all positions ABOVE " + pricePressed + " from: " + posOrders +
                    " to " + targetPosOrders);
        }


        //make the transfers at the prices found:
        for (int price : positionPricesToMove) {
            MikeSimLogger.addLogEvent("Attempting transfer of postion at price: " + price
                    + " to MikePosOrders: " + targetPosOrders.getName());
            posOrders.movePositionToDifferentMikePosOrders(price, targetPosOrders);
        }



    }

    private void processMovePosClicked(int pricePressed, MainModelThread model, MikePosOrders posOrders, MikeGridPane.MikeButton button, MouseEvent event) {

        //if left button was clicked move the single MikePosition at that price to the next MikePosition at a lower price, or if none exists, one cent lower
        if (event.getButton() == MouseButton.PRIMARY) {
            MikeSimLogger.addLogEvent("Moving single MikePosition at price " + pricePressed + " lower NOT IMPLEMENTED!");
//            MikeSimLogger.addLogEvent("TargetPosOrders is: " + controllerPositionsWindow.getTargetMikePosOrders());
        }

        //move higher if right button clicked
        if (event.getButton() == MouseButton.SECONDARY) {
            MikeSimLogger.addLogEvent("Moving single MikePosition at price " + pricePressed + " higher NOT IMPLEMENTED!");
//            MikeSimLogger.addLogEvent("TargetPosOrders is: " + controllerPositionsWindow.getTargetMikePosOrders());
        }


    }


//    private int getAmount() {
//    return 0;
//    }

    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }


    public void setControllerPositionsWindow(ControllerPositionsWindow controllerPositionsWindow) {
        this.controllerPositionsWindow = controllerPositionsWindow;
    }

    public void transferAllClosedPLClicked(ActionEvent actionEvent) {
        transferClosedPL(1f);
    }

    public void transferHalfClosedPLClicked(ActionEvent actionEvent) {
        transferClosedPL(0.5f);
    }

    private void transferClosedPL(float percentage) {
        MikePosOrders mikePosOrders = controllerPositionsWindow.getMikePosOrders();
        MikePosOrders targetPosOrders = controllerPositionsWindow.getTargetMikePosOrders();
        MikeSimLogger.addLogEvent("Transferring all ClosedPL from positions to internalClosedPL and moving half" +
                "\nof it to targerPosOrders");
        MikeSimLogger.addLogEvent("Internal PL before: " + mikePosOrders.getInternalClosedPL());
        mikePosOrders.transferClosedPLFromPositionsToInternal();
        MikeSimLogger.addLogEvent("Internal PL after: " + mikePosOrders.getInternalClosedPL());

        mikePosOrders.moveInternalClosedPL((int) (mikePosOrders.getInternalClosedPL() * percentage), targetPosOrders );

        MikeSimLogger.addLogEvent("Internal Closed PL after moving half: " + mikePosOrders.getInternalClosedPL());
        MikeSimLogger.addLogEvent("Internal PL in target: " + targetPosOrders.getInternalClosedPL());
    }

    public void consolidateAllMikePosClicked(ActionEvent actionEvent) {
        MikeSimLogger.addLogEvent("Not implemented!");
    }

    public void reducePosByQuarterClicked(ActionEvent actionEvent) {
        MikeSimLogger.addLogEvent("Reducing " +
                controllerPositionsWindow.getMikePosOrders().getName() +
                " by 25%");
        controllerPositionsWindow.getMikePosOrders().flattenThisPosition(0.25f);
    }

    public void reducePosHalfClicked(ActionEvent actionEvent) {
        MikeSimLogger.addLogEvent("Reducing " +
                controllerPositionsWindow.getMikePosOrders().getName() +
                " by 50%");
        controllerPositionsWindow.getMikePosOrders().flattenThisPosition(0.5f);
    }

    public void flattenPosClicked(ActionEvent actionEvent) {
        MikeSimLogger.addLogEvent("Flattening " +
                controllerPositionsWindow.getMikePosOrders().getName() );
        controllerPositionsWindow.getMikePosOrders().flattenThisPosition(1);
    }
}
