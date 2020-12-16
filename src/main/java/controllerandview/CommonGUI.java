package main.java.controllerandview;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import main.java.model.MainModelThread;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

import java.util.List;
import java.util.Optional;

public class CommonGUI {

    public interface ICommonGUI {
        void setPriceServer(PriceServer priceServer);
        PriceServer getPriceServer();
        void setMikePosOrders(MikePosOrders posOrders);
    }

    /**
     *
     * Sets MikePosOrders inside class implementing ICommonGUI
     *
     * if priceServer is supplied - MikePosOrders available for choosing will be for the instrument traded by that priceserver
     * if it is null - attempts to get the priceserver from the class implementing the ICommonGUI interface
     * based on user choice in dialog
     * @param setMikePosOrdersTarget
     * @param model
     * @param priceServer
     */
    synchronized static public MikePosOrders setMikePos(ICommonGUI setMikePosOrdersTarget, MainModelThread model, PriceServer priceServer){
        System.out.println("Attempting to set MikePosOrders");


        //use priceServer supplied by call to method or try to get it if null
        if (priceServer == null) {
            if (setMikePosOrdersTarget.getPriceServer() == null) {
                System.out.println("Price server not available! Unable to set MikePosOrders!");
                return null;
            }
            else priceServer = setMikePosOrdersTarget.getPriceServer();
        }



        try {
            //get the list of available MikePosOrders from the model:
            List<MikePosOrders> dialogData = model.posOrdersManager.getMikePosOrdersList(
                    priceServer.getTickerID());

            //make sure we have something to choose from:
            if(dialogData.isEmpty()) return null;
            MikePosOrders selected = dialogData.get(0);

            //create and display the dialog:
            ChoiceDialog dialog = new ChoiceDialog(dialogData.get(0), dialogData);
            dialog.setTitle("Select MikePosOrders");
            dialog.setHeaderText("MikePosOrders available for " +
                    priceServer.toString());
            Optional<MikePosOrders> result = dialog.showAndWait();

            //handle user selection:
            if (result.isPresent()) {
                selected = result.get();
                System.out.println("Selection: " + selected.toString() + " Current bid price: " + selected.getBidPrice());
                setMikePosOrdersTarget.setMikePosOrders(selected);
                return selected;
            } else {
                System.out.println("Selection Cancelled!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in setMikePos dialog");
        }


        return null;
    }

    /**
     * Sets the PriceServer in setPriceServerTarget according to user choice in a JavaFX Dialog pop-up window.
     *  @param setPriceServerTarget
     * @param model
     * @return
     */
    synchronized static public PriceServer setPriceServer(ICommonGUI setPriceServerTarget, MainModelThread model){

        System.out.println("Attempting to set PriceServer");

        //get the list of available PriceServers from the model:
        List<PriceServer> dialogData = model.posOrdersManager.getPriceServerObservableList();

        //make sure we have something to choose from:
        if(dialogData.isEmpty()) return null;
        PriceServer selected = dialogData.get(0);

        //create and display the dialog:
        ChoiceDialog dialog = new ChoiceDialog(dialogData.get(0), dialogData);
        dialog.setTitle("Trading Instrument Selection");
        dialog.setHeaderText("Select your choice");
        Optional<PriceServer> result = dialog.showAndWait();

        //handle user selection:
        if (result.isPresent()) {
            selected = result.get();
            System.out.println("Selection: " + selected.toString() + " Current bid price: " + selected.getBidPrice());
            setPriceServerTarget.setPriceServer(selected);
        } else {
            System.out.println("Selection Cancelled!");
        }

        return selected;
    }

    /**
     * Not tested and not sure if working:
     * @param text
     * @return
     */
    static private boolean showConfirmationDialog(String text){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();

        if ((result.isPresent()) && (result.get() == ButtonType.OK)) return true;
        else return false;
    }

}
