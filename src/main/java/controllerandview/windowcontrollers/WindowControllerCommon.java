package main.java.controllerandview.windowcontrollers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import main.java.controllerandview.algocontrollerpanes.ControllerPlainOrder;
import main.java.controllerandview.algocontrollerpanes.ControllerSimpleScalperAlgo;
import main.java.controllerandview.algocontrollerpanes.ControllerSimpleStepperAlgo;
import main.java.controllerandview.algocontrollerpanes.ControllerTransferAndCancel;
import main.java.model.MikeSimLogger;

import java.io.IOException;

/**
 * Holds static methods used by other Window Controllers
 */
public class WindowControllerCommon {

    /**
     * Can be called by onitialize() - sets up the default actions of the columns.
     * User can later change what each column does.
     * @param controllerPositionsWindow the window in which we want to setup the default column actions
     */
    public static void setupDefaultColumnActionsInControllerPositionsWindow(ControllerPositionsWindow controllerPositionsWindow) {
        MikeSimLogger.addLogEvent("setting initial column actions in Positionswindow");
        try {
            ControllerPlainOrder controllerPlainOrder;// = loader.getController();
            ControllerTransferAndCancel transferAndCancel;

            //setup first column:
            transferAndCancel = setupTransferAndCancelInColumn(controllerPositionsWindow, 1);
            transferAndCancel.cancelBelowOrAbove.fire();

            //setup second column:
            controllerPlainOrder = setupPlainOrderInColumn(controllerPositionsWindow, 2);
            controllerPlainOrder.buyLimit.fire();

            //setup third column:
            controllerPlainOrder = setupPlainOrderInColumn(controllerPositionsWindow, 3);
            controllerPlainOrder.buyStop.fire();

            //setup 4th column:
            controllerPlainOrder = setupPlainOrderInColumn(controllerPositionsWindow, 4);
            controllerPlainOrder.cancel.fire();

            //setup 5th column:
            controllerPlainOrder = setupPlainOrderInColumn(controllerPositionsWindow, 5);
            controllerPlainOrder.sellStop.fire();

            //setup 6th column:
            controllerPlainOrder = setupPlainOrderInColumn(controllerPositionsWindow, 6);
            controllerPlainOrder.sellLimit.fire();

            //setup 7th column:
            transferAndCancel = setupTransferAndCancelInColumn(controllerPositionsWindow, 7);
            transferAndCancel.transferBelowOrAbove.fire();

            MikeSimLogger.addLogEvent("Experiment in setting initial column controller  successful");
        } catch (Exception e) {
            MikeSimLogger.addLogEvent("Exception in setupControllerInsidePane()");
        }
    }

    /**
     * Convenience method for setting up columns in ControllerPositionsWindow.
     * Sets up a SimpleScalperAlgo in the column specified and returns the controller of the algo
     * @param controllerPositionsWindow
     * @param columnNumber the column in MikeGridPane where we want the algo setup. first column in number 1 not zero!
     * @return
     */
    public static ControllerSimpleScalperAlgo setupSimpleScalperInColumn(ControllerPositionsWindow controllerPositionsWindow, int columnNumber){
        try {
            AnchorPane anPaneOfColumn = controllerPositionsWindow.getAnchorPaneOfColumn(columnNumber);
            FXMLLoader loader = new FXMLLoader(controllerPositionsWindow.getClass().getResource("/PositionsWindow/algoControllers/SimpleScalperAlgoControlPanel.fxml"));
            anPaneOfColumn.getChildren().setAll((Parent) loader.load());
            ControllerSimpleScalperAlgo controllerSimpleScalperAlgo = loader.getController();
            //MikeGridPane inside controllerPositionsWindow will call this controller whenever a button inside MikeGridPane is pressed:
            controllerPositionsWindow.setAlgoController(columnNumber, controllerSimpleScalperAlgo);
            controllerSimpleScalperAlgo.buyLimit.fire();
            controllerSimpleScalperAlgo.setCntrlParentWindow(controllerPositionsWindow);

            return controllerSimpleScalperAlgo;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convenience method for setting up columns in ControllerPositionsWindow.
     * Sets up a Plain Order contorller in the column specified and returns the controller of the algo
     * @param controllerPositionsWindow
     * @param columnNumber the column in MikeGridPane where we want the algo setup. first column in number 1 not zero!
     * @return
     */
    public static ControllerPlainOrder setupPlainOrderInColumn(ControllerPositionsWindow controllerPositionsWindow, int columnNumber){
        try {
            AnchorPane anPaneOfColumn = controllerPositionsWindow.getAnchorPaneOfColumn(columnNumber);
            FXMLLoader loader = new FXMLLoader(ControllerPositionsWindow.class.getResource("/PositionsWindow/algoControllers/PlainOrderControlPanel.fxml"));
            anPaneOfColumn.getChildren().setAll((Parent) loader.load());
            ControllerPlainOrder controllerPlainOrder = loader.getController();
            //MikeGridPane inside controllerPositionsWindow will call this controller whenever a button inside MikeGridPane is pressed:
            controllerPositionsWindow.setAlgoController(columnNumber, controllerPlainOrder);
            //set default action:
            controllerPlainOrder.buyLimit.fire();
            //this is required for controllerPlainOrder to work:
            controllerPlainOrder.setCntrlParentWindow(controllerPositionsWindow);

            return controllerPlainOrder;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convenience method for setting up columns in ControllerPositionsWindow.
     * Sets up a SimpleStepper Algo contorller in the column specified and returns the controller of the algo
     * @param controllerPositionsWindow
     * @param columnNumber the column in MikeGridPane where we want the algo setup. first column in number 1 not zero!
     * @return
     */
    public static ControllerSimpleStepperAlgo setupSimpleStepperAlgoInColumn(ControllerPositionsWindow controllerPositionsWindow, int columnNumber){
        try {
            AnchorPane anPaneOfColumn = controllerPositionsWindow.getAnchorPaneOfColumn(columnNumber);
            FXMLLoader loader = new FXMLLoader(ControllerPositionsWindow.class.getResource("/PositionsWindow/algoControllers/SimpleStepperControlPanel.fxml"));
            anPaneOfColumn.getChildren().setAll((Parent) loader.load());
            ControllerSimpleStepperAlgo controllerStepperAlgo = loader.getController();
            //MikeGridPane inside controllerPositionsWindow will call this controller whenever a button inside MikeGridPane is pressed:
            controllerPositionsWindow.setAlgoController(columnNumber, controllerStepperAlgo);
            //set default action:
            controllerStepperAlgo.buyStop.fire();
            //this is required for controllerStepperAlgo to work:
            controllerStepperAlgo.setCntrlParentWindow(controllerPositionsWindow);

            return controllerStepperAlgo;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convenience method for setting up columns in ControllerPositionsWindow.
     * Sets up a SimpleStepper Algo contorller in the column specified and returns the controller of the algo
     * @param controllerPositionsWindow
     * @param columnNumber the column in MikeGridPane where we want the algo setup. first column in number 1 not zero!
     * @return
     */
    public static ControllerTransferAndCancel setupTransferAndCancelInColumn(ControllerPositionsWindow controllerPositionsWindow, int columnNumber){
        try {
            AnchorPane anPaneOfColumn = controllerPositionsWindow.getAnchorPaneOfColumn(columnNumber);
            FXMLLoader loader = new FXMLLoader(ControllerPositionsWindow.class.getResource("/PositionsWindow/algoControllers/TransferAndCancelControlPanel.fxml"));
            anPaneOfColumn.getChildren().setAll((Parent) loader.load());
            ControllerTransferAndCancel controllerTransferAndCancel = loader.getController();
            //MikeGridPane inside controllerPositionsWindow will call this controller whenever a button inside MikeGridPane is pressed:
            controllerPositionsWindow.setAlgoController(columnNumber, controllerTransferAndCancel);
            //set default action:
            controllerTransferAndCancel.transferBelowOrAbove.fire();
            //this is required for controllerTransferAndCancel to work:
            controllerTransferAndCancel.setCntrlParentWindow(controllerPositionsWindow);

            return controllerTransferAndCancel;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
