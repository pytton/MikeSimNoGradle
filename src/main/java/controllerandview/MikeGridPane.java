package main.java.controllerandview;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import main.java.model.MikeSimLogger;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

import java.util.ArrayList;

import static javafx.geometry.Pos.CENTER;




/**
 *Custom GridPane with buttons that can be accessed with getButton function
 */
public class MikeGridPane extends GridPane {

    public static void setTextForButtonInMikeGridPane(MikeGridPane mikeGridPane, int row, int col, String text) {
        assert (row <= mikeGridPane.getHowManyRows() && row >=0 ) : "setTextForButtonInMikeGridPane: row outside of bounds";
        assert (col <= mikeGridPane.getHowManyCols() && col >=0 ) : "setTextForButtonInMikeGridPane: col outside of bounds";
        mikeGridPane.getButton(row, col).setText(text);
    }

    public static void printPositionsAtRow(MikeGridPane mikeGridPane, MikePosOrders mikePosOrders, int openLongPositionsCol, int openShortPositionsCol,
                                           int row, int priceToPrintAtRow) {
        int openPosAtPrice = mikePosOrders.getOpenAmountAtPrice(priceToPrintAtRow);

        if (openPosAtPrice == 0) {
            setTextForButtonInMikeGridPane(mikeGridPane, row, openLongPositionsCol, "" );
            setTextForButtonInMikeGridPane(mikeGridPane, row, openShortPositionsCol, "" );
            mikeGridPane.getButton(row, openLongPositionsCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
            mikeGridPane.getButton(row, openShortPositionsCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
        } else {
            if (openPosAtPrice > 0) {
                mikeGridPane.getButton(row, openLongPositionsCol).setStyle("" +
                        "-fx-background-color: lightblue  ; -fx-text-fill: blue; -fx-font-weight: bolder;-fx-border-color : black");
                setTextForButtonInMikeGridPane(mikeGridPane, row, openLongPositionsCol, "" + openPosAtPrice);
            } else if(openPosAtPrice < 0){
                mikeGridPane.getButton(row, openShortPositionsCol).setStyle("" +
                        "-fx-background-color: lightred; -fx-text-fill: red; -fx-font-weight: bolder; -fx-border-color : black");
                setTextForButtonInMikeGridPane(mikeGridPane, row, openShortPositionsCol, "" + openPosAtPrice);
            }
        }
    }

    public static void printPositions(MikeGridPane mikeGridPane, MikePosOrders mikePosOrders, int openLongPositionsColumn, int openShortPositionsCol,
                                      int topRowPrice){
        //check parameters:
        if((openLongPositionsColumn < 0 || openLongPositionsColumn > (mikeGridPane.getHowManyCols()))
        || (openShortPositionsCol < 0 || openShortPositionsCol > mikeGridPane.getHowManyCols())){
            MikeSimLogger.addLogEvent("INVALID PARAMETERS IN main.java.controllerandview.MikeGridPane.printPositions");
            return;
        }

        int priceToPrintAtRow;
        for (int row = 0; row < mikeGridPane.getHowManyRows(); row++){
            priceToPrintAtRow = topRowPrice - row;

            int openPosAtPrice = mikePosOrders.getOpenAmountAtPrice(priceToPrintAtRow);

            if (openPosAtPrice == 0) {
                setTextForButtonInMikeGridPane(mikeGridPane, row, openLongPositionsColumn, "" );
                setTextForButtonInMikeGridPane(mikeGridPane, row, openShortPositionsCol, "" );
                mikeGridPane.getButton(row, openLongPositionsColumn).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
                mikeGridPane.getButton(row, openShortPositionsCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
            } else {
                if (openPosAtPrice > 0) {
                    mikeGridPane.getButton(row, openLongPositionsColumn).setStyle("" +
                            "-fx-background-color: lightblue  ; -fx-text-fill: blue; -fx-font-weight: bolder;-fx-border-color : black");
                    setTextForButtonInMikeGridPane(mikeGridPane, row, openLongPositionsColumn, "" + openPosAtPrice);
                } else if(openPosAtPrice < 0){
                    mikeGridPane.getButton(row, openShortPositionsCol).setStyle("" +
                            "-fx-background-color: lightred; -fx-text-fill: red; -fx-font-weight: bolder; -fx-border-color : black");
                    setTextForButtonInMikeGridPane(mikeGridPane, row, openShortPositionsCol, "" + openPosAtPrice);
                }
            }

        }

    }

    public static void printBuyOrders(MikeGridPane mikeGridPane, MikePosOrders mikePosOrders, int activeBuyOrderCol,
                                      int topRowPrice){
        //check parameters:
        if(activeBuyOrderCol < 0 || activeBuyOrderCol > (mikeGridPane.getHowManyCols())){
            MikeSimLogger.addLogEvent("INVALID PARAMETERS IN main.java.controllerandview.MikeGridPane.printBuyOrders");
            return;
        }

        int priceToPrint;
        for(int row = 0 ; row < mikeGridPane.getHowManyRows() ; row++) {

            priceToPrint = topRowPrice - row;

            //print active buy orders for the given price:
            if (mikePosOrders.getOpenBuyOrdersAtPrice(priceToPrint) != 0) {
                MikeGridPane.setTextForButtonInMikeGridPane(mikeGridPane, row, activeBuyOrderCol, "" + mikePosOrders.getOpenBuyOrdersAtPrice(priceToPrint));
                mikeGridPane.getButton(row, activeBuyOrderCol).setStyle("-fx-background-color: lightblue; -fx-text-fill: blue; -fx-font-weight: bolder; -fx-border-color : black");
            } else {
                MikeGridPane.setTextForButtonInMikeGridPane(mikeGridPane, row, activeBuyOrderCol, "");
                mikeGridPane.getButton(row, activeBuyOrderCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
            }
        }
    }

    public static void printBidInMikeGridPane(MikeGridPane mikeGridPane, PriceServer priceServer, int bidPrintoutCol, int topRowPrice){
        //check parameters:
        if(bidPrintoutCol < 0 || bidPrintoutCol > (mikeGridPane.getHowManyCols())){
            MikeSimLogger.addLogEvent("INVALID PARAMETERS IN main.java.controllerandview.MikeGridPane.printBidInMikeGridPane");
            return;
        }

        int priceToPrint;
        for(int row = 0 ; row < mikeGridPane.getHowManyRows() ; row++) {
            priceToPrint = topRowPrice - row;

            //print "BID" in the row of the bid price:
            MikeGridPane.MikeButton button = mikeGridPane.getButton(row, bidPrintoutCol);
            if (topRowPrice - row == priceServer.getBidPrice()) {
                MikeGridPane.setTextForButtonInMikeGridPane(mikeGridPane, row, bidPrintoutCol, "BID");
                button.setStyle("-fx-background-color: yellow; -fx-text-fill: blue; -fx-font-weight: bolder; -fx-border-color : black");
            } else {
                MikeGridPane.setTextForButtonInMikeGridPane(mikeGridPane, row, bidPrintoutCol, "");
                mikeGridPane.getButton(row, bidPrintoutCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
            }
        }
    }

    public static void printPricesInMikeGridPane(MikeGridPane mikeGridPane, int pricePrintoutCol, int topRowPrice){
        //check parameters:
        if(pricePrintoutCol < 0 || pricePrintoutCol > (mikeGridPane.getHowManyCols())){
            MikeSimLogger.addLogEvent("INVALID PARAMETERS IN main.java.controllerandview.MikeGridPane.printPricesInMikeGridPane");
            return;
        }

        for(int row = 0 ; row < mikeGridPane.getHowManyRows() ; row++) {

            //print prices in mikeGridPane:
            MikeGridPane.MikeButton button = mikeGridPane.getButton(row, pricePrintoutCol);
            MikeGridPane.setTextForButtonInMikeGridPane(mikeGridPane, row, pricePrintoutCol, "" + (topRowPrice - row));
            button.setStyle("-fx-background-color: lightgrey; -fx-text-fill: black; -fx-font-weight: bold");
        }
    }

    public static void printAskInMikeGridPane(MikeGridPane mikeGridPane, PriceServer priceServer, int askPrintoutCol, int topRowPrice){
        //check parameters:
        if(askPrintoutCol < 0 || askPrintoutCol > (mikeGridPane.getHowManyCols())){
            MikeSimLogger.addLogEvent("INVALID PARAMETERS IN main.java.controllerandview.MikeGridPane.printAskInMikeGridPane");
            return;
        }
        for(int row = 0 ; row < mikeGridPane.getHowManyRows() ; row++) {
            //print "ASK" in the row of the ask price in fifth column:
            MikeGridPane.MikeButton button = mikeGridPane.getButton(row, askPrintoutCol);
            if (topRowPrice - row == priceServer.getAskPrice()) {
                MikeGridPane.setTextForButtonInMikeGridPane(mikeGridPane, row, askPrintoutCol, "ASK");
                button.setStyle("-fx-background-color: yellow; -fx-text-fill: red; -fx-font-weight: bolder; -fx-border-color : black");
            } else {
                MikeGridPane.setTextForButtonInMikeGridPane(mikeGridPane, row, askPrintoutCol, "");
                mikeGridPane.getButton(row, askPrintoutCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
            }
        }
    }

    public static void printSellOrders(MikeGridPane mikeGridPane, MikePosOrders mikePosOrders, int activeSellOrderCol, int topRowPrice){
        //check parameters:
        if(activeSellOrderCol < 0 || activeSellOrderCol > (mikeGridPane.getHowManyCols())){
            MikeSimLogger.addLogEvent("INVALID PARAMETERS IN main.java.controllerandview.MikeGridPane.printSellOrders");
            return;
        }
        int priceToPrint;
        for(int row = 0 ; row < mikeGridPane.getHowManyRows() ; row++) {
            priceToPrint = topRowPrice - row;
            //print active sell orders for the given price:
            if (mikePosOrders.getOpenSellOrdersAtPrice(priceToPrint) != 0) {
                MikeGridPane.setTextForButtonInMikeGridPane(mikeGridPane, row, activeSellOrderCol, "" + mikePosOrders.getOpenSellOrdersAtPrice(priceToPrint));
                mikeGridPane.getButton(row, activeSellOrderCol).setStyle("-fx-background-color: salmon; -fx-text-fill: crimson; -fx-font-weight: bolder; -fx-border-color : black");
            } else {
                MikeGridPane.setTextForButtonInMikeGridPane(mikeGridPane, row, activeSellOrderCol, "");
                mikeGridPane.getButton(row, activeSellOrderCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
            }
        }
    }

    public static void printZeroProfitPoint(MikeGridPane mikeGridPane, int zeroProfitPointCol, int totalOpenAmount, int topRowPrice, Double zeroProfitPoint){
        //check parameters:
        if(zeroProfitPointCol < 0 || zeroProfitPointCol > (mikeGridPane.getHowManyCols())){
            MikeSimLogger.addLogEvent("INVALID PARAMETERS IN main.java.controllerandview.MikeGridPane.printZeroProfitPoint");
            return;
        }
        int priceToPrint;
        for(int row = 0 ; row < mikeGridPane.getHowManyRows() ; row++) {
            priceToPrint = topRowPrice - row;
            //print the zero profit point
            if (priceToPrint == zeroProfitPoint.intValue()) {
                MikeGridPane.setTextForButtonInMikeGridPane(mikeGridPane, row, zeroProfitPointCol, "" + totalOpenAmount);
                //color the button according to the position being long/short:
                if (totalOpenAmount > 0)
                    mikeGridPane.getButton(row, zeroProfitPointCol).setStyle("-fx-text-fill: blue; -fx-font-weight: bolder");
                if (totalOpenAmount < 0)
                    mikeGridPane.getButton(row, zeroProfitPointCol).setStyle("-fx-text-fill: red; -fx-font-weight: bolder");
                if (totalOpenAmount == 0)
                    mikeGridPane.getButton(row, zeroProfitPointCol).setStyle("-fx-background-color: white");
            }
        }
    }

    /**
     * This interface gets an event when a button within the MikeGridPane is clicked
     */
    public interface MikeButtonHandler{
        void handleMikeButtonClicked(MikeGridPane.MikeButton event);
//        public ActionEvent handleMikeButtonClicked(ActionEvent event);
    }

    public static class EmptyMikeButtonHandler implements MikeButtonHandler {
        @Override
        public void handleMikeButtonClicked(MikeButton event) {

        }
    }


    private int howManyRows = 20;
    private int howManyCols = 7;

    /**
     * Interface that handles clicks on MikeButtons within the MikeGridPane.
     * Must have handleMikeButtonClicked(ActionEvent event) implemented
     */
    private  MikeButtonHandler handler;

//    private ControllerPositionsWindow positionsWindow;
    /**
     * Buttons in this GridPane. Access via getButton(row,column)
     */
    private MikeButton[][] buttons;

    ArrayList<ArrayList<MikeButton>> buttonList = new ArrayList<>();

    public MikeGridPane(int rowsToCreate, int colsToCreate, MikeButtonHandler handler) {
        super();

        //set the handler for clicking buttons inside MikeGridPane
        this.handler = handler;
        howManyCols = colsToCreate;
        howManyRows = rowsToCreate;

        buttons = new MikeButton [howManyRows][howManyCols];

        this.setVgap(0);
        this.setHgap(1);

        //Set the row height:
        RowConstraints rowConstraints = new RowConstraints(20);

        //add buttons to grid:
        for (int row = 0; row < howManyRows; row++) {
            this.getRowConstraints().add(rowConstraints);
            buttonList.add(new ArrayList<MikeButton>());

            for (int col = 0; col < howManyCols; col++) {
                MikeButton button = new MikeButton(row, col, this.getHandler());
//                button.setPrefWidth(90);//not working

                button.setButtonClickHandler(this.getHandler());

//                button.setOnAction();

                int number = col + (row + col);
                button.setText(""+number);
                button.setFont(Font.font(15));
                button.setPadding(new Insets(0, 0, 0, 0));

                buttons[row][col] = button;
                buttonList.get(row).add(button);

                button.setPrefWidth(58);
//                if (col == 3) button.setPrefWidth(90);
                button.setMinHeight(20);
                button.setMaxHeight(20);
                button.setPrefHeight(20);


//                button.setMaxHeight(5);
//                button.setStyle("-fx-background-color: lightgrey;-fx-border-color: black; -fx-border-width: 1px");
//                button.setStyle("-fx-background-color: white");
//                MikeSimLogger.addLogEvent(button.getLabelPadding());
//                MikeSimLogger.addLogEvent(button.getLayoutBounds());



                //add the button to the GridPane. Yes col, row is the correct order
                this.add(button, col, row);
            }

//            this.getRowConstraints().get(row).setPrefHeight(6);
        }
        this.setAlignment(CENTER);


        MikeSimLogger.addLogEvent("MikeGridPane generated");
    }

    public MikeButton getButton(int row, int col) {
        if(row<= howManyRows && col<=howManyCols) return  buttons[row][col];
        else return null;
    }
    /**
     * Button which can tell which row and column it occupies. Has to be set after constructing it.
     */
    public class MikeButton extends Button{
        int rowOfButton = 0;
        int colOfButton = 0;

        //interface which is called when MikeButton is pressed
        public MikeButtonHandler buttonClickHandler;

        //handler for pressing the button. This gets called when the button is pressed
        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //pass the MikeButton which was pressed to the interface which handles it:
                getButtonClickHandler().handleMikeButtonClicked((MikeButton)event.getSource());
            }

        };

        EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //todo: experimenting:

                //pass the MikeButton which was pressed to the interface which handles it:
                if (event.getButton() == MouseButton.PRIMARY) {
                    getButtonClickHandler().handleMikeButtonClicked((MikeButton)event.getSource());
                }


                if (event.getButton() == MouseButton.SECONDARY) {
                    MikeSimLogger.addLogEvent("MikeGridPane MikeButton clicked with RIGHT MOUSE BUTTON. This message" +
                            " generated in MikeButton class");
                }
            }
        };



        public MikeButton(int rowPosition, int colPosition, MikeButtonHandler handler){
            rowOfButton = rowPosition;
            colOfButton = colPosition;
            //set where will an event be sent once the button is clicked
            this.setButtonClickHandler(handler);
            //add the handler method to the button. this is what is called when the button is pressed
//            this.setOnAction(buttonHandler);
            this.setOnMouseClicked(mouseEventEventHandler);
        }

        public int getRowOfButton() {
            return rowOfButton;
        }

        public void setRowOfButton(int rowOfButton) {
            this.rowOfButton = rowOfButton;
        }

        public int getColOfButton() {
            return colOfButton;
        }

        public void setColOfButton(int colOfButton) {
            this.colOfButton = colOfButton;
        }

        public void setButtonClickHandler(MikeButtonHandler buttonClickHandler) {
            this.buttonClickHandler = buttonClickHandler;
        }

        public MikeButtonHandler getButtonClickHandler() {
            return buttonClickHandler;
        }
    }

    public int getHowManyRows() {
        int rows = howManyRows;
        return rows;
    }

    /**
     * one column returns 1
     * 10 columns returns 10
     * WARNING! FIRST COLUMN NUMBER IS 0 AND THIS METHOD RETURNS 1 IF ONLY COLMUN IN MIKEGRIDPANE IS COLUMN 0!
     * @return
     */
    public int getHowManyCols() {
        return howManyCols;
    }

    public MikeButtonHandler getHandler() {
        return handler;
    }

    /**
     * WARNING THIS DOESN'T SEEM TO WORK CORRECTLY!
     * @param handler
     */
    public void setHandler(MikeButtonHandler handler) {

        //WARNING THIS IS NOT WORKING
        //this should go through all the MikeButtons inside     private MikeButton[][] buttons;
        //and set the handlers there. it will do nothing here

        MikeSimLogger.addLogEvent("setHandler in MikeGridPane called. NOT SURE IF THIS WORKS CORRECTLY!");
        this.handler = handler;
    }

}
