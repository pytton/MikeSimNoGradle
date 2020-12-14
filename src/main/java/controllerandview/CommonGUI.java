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
     * Gets the PriceServer from ICommonGUI and sets MikePosOrders inside class implementing ICommonGUI
     * based on user choice in dialog
     * @param setMikePosOrdersTarget
     * @param model
     */
    synchronized static public void setMikePos(ICommonGUI setMikePosOrdersTarget, MainModelThread model){
        System.out.println("Attempting to set MikePosOrders");

        if (setMikePosOrdersTarget.getPriceServer() == null) {
            System.out.println("Price server not available! Unable to set MikePosOrders!");
            return;
        }


        try {
            //get the list of available MikePosOrders from the model:
            List<MikePosOrders> dialogData = model.posOrdersManager.getMikePosOrdersList(
                    setMikePosOrdersTarget.getPriceServer().getTickerID());

            //make sure we have something to choose from:
            if(dialogData.isEmpty()) return;
            MikePosOrders selected = dialogData.get(0);

            //create and display the dialog:
            ChoiceDialog dialog = new ChoiceDialog(dialogData.get(0), dialogData);
            dialog.setTitle("Select MikePosOrders");
            dialog.setHeaderText("MikePosOrders available for " +
                    setMikePosOrdersTarget.getPriceServer().toString());
            Optional<MikePosOrders> result = dialog.showAndWait();

            //handle user selection:
            if (result.isPresent()) {
                selected = result.get();
                System.out.println("Selection: " + selected.toString() + " Current bid price: " + selected.getBidPrice());
                setMikePosOrdersTarget.setMikePosOrders(selected);
            } else {
                System.out.println("Selection Cancelled!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in setMikePos dialog");
        }


    }

    /**
     * Sets the PriceServer in setPriceServerTarget according to user choice in a JavaFX Dialog pop-up window.
     *
     * @param setPriceServerTarget
     * @param model
     */
    synchronized static public void setPriceServer(ICommonGUI setPriceServerTarget, MainModelThread model){

        System.out.println("Attempting to set PriceServer");

        //get the list of available PriceServers from the model:
        List<PriceServer> dialogData = model.posOrdersManager.getPriceServerObservableList();

        //make sure we have something to choose from:
        if(dialogData.isEmpty()) return;
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
