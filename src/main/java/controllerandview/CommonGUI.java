package main.java.controllerandview;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CommonGUI {

    public interface ICommonGUI {
        void setPriceServer(PriceServer priceServer);
        PriceServer getPriceServer();
        void setMikePosOrders(MikePosOrders posOrders);
    }


    /**
     * If you want to fire off different algos or orders on consecutive prices you can use this.
     * Create a class that implements GUICommandMultipleOrder
     * with the parameters you want and implement command() inside GUICommandMultipleOrder however you wish.
     * command() gets called multiple times by depending on what is entered into TextField multipleAmount, TextField multipleDistance
     */
    public static void placeMultipleOrder(GUICommandMultipleOrder command, TextField multipleAmount, TextField multipleDistance, MikeOrder.MikeOrderType orderType,
                                          int pricePressed){

        MikeSimLogger.addLogEvent("Placing multiple order");

        //how many orders do we want?
        Integer amountOfOrders = 1;
        try{ amountOfOrders =  Integer.parseInt(multipleAmount.getText());
            if (amountOfOrders < 0) amountOfOrders = 1;
        }catch (Exception e){
            amountOfOrders = 1;
        }

        //distance in cents between orders?
        Integer distanceBetweenOrders = 1;
        try{ distanceBetweenOrders =  Integer.parseInt(multipleDistance.getText());
            if (distanceBetweenOrders < 0) distanceBetweenOrders = 1;
        }catch (Exception e){
            distanceBetweenOrders = 1;
        }

        int direction = 1;
        //place orders going down in price for buy lmt and sell stp
        if(orderType == MikeOrder.MikeOrderType.BUYLMT || orderType == MikeOrder.MikeOrderType.SELLSTP) direction = -1;
        //going up in price for buy stop and sell limit
        if(orderType == MikeOrder.MikeOrderType.BUYSTP || orderType == MikeOrder.MikeOrderType.SELLLMT) direction = 1;

        MikeSimLogger.addLogEvent("Amount of orders: " + amountOfOrders + " distance: " + distanceBetweenOrders
        + " direction: " + direction);

        int priceToSend = pricePressed;
        for(int i = 0; i < amountOfOrders; i++){
            //place the order in the right direction and distance from previous one:

            command.command();
            priceToSend = priceToSend + (distanceBetweenOrders * direction);
            command.setPriceToSend(priceToSend);

//            posOrders.placeNewOrder(orderType, priceToSend, priceToSend, orderAmount);
//            priceToSend = priceToSend + (distanceBetweenOrders * direction);
        }
    }

    /**
     * If you want to fire off different algos or orders on consecutive prices you can use this.
     * Create a class with the parameters you want and implement command however you wish.
     * command() gets called multiple times by main.java.controllerandview.CommonGUI#placeMultipleOrder
     */
    public interface GUICommandMultipleOrder {
        /**
         * This is the reason for this class.
         * This gets called multiple times in main.java.controllerandview.CommonGUI#placeMultipleOrder
         * to place multiple orders
         * @return
         */
        boolean command();

        /**
         * This gets called inside main.java.controllerandview.CommonGUI#placeMultipleOrder
         * To change the price of the next order that will be sent by command();
         * @param priceToSend
         */
        void setPriceToSend(int priceToSend);
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

    /**
     * Returns the screen for the specified stage.
     *
     * @param stage the stage
     * @return the screen for the specified stage
     */
    @Nonnull
    public static Screen getScreenForStage(@Nonnull Stage stage) {
        for (Screen screen : Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1., 1.)) {
            return screen;
        }
        throw new NoSuchElementException("Cannot determine screen for stage.");
    }

}
