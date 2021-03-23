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

                button.setPrefWidth(60);
//                if (col == 3) button.setPrefWidth(90);
                button.setMinHeight(20);
                button.setMaxHeight(20);
                button.setPrefHeight(20);


//                button.setMaxHeight(5);
//                button.setStyle("-fx-background-color: lightgrey;-fx-border-color: black; -fx-border-width: 1px");
//                button.setStyle("-fx-background-color: white");
//                System.out.println(button.getLabelPadding());
//                System.out.println(button.getLayoutBounds());



                //add the button to the GridPane. Yes col, row is the correct order
                this.add(button, col, row);
            }

//            this.getRowConstraints().get(row).setPrefHeight(6);
        }
        this.setAlignment(CENTER);


        System.out.println("MikeGridPane generated");
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
                    System.out.println("MikeGridPane MikeButton clicked with RIGHT MOUSE BUTTON. This message" +
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
