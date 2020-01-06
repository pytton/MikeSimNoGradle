package main.java.controllerandview.positionswindow.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.java.controllerandview.MainGUIClass;
import main.java.controllerandview.algocontrollers.AlgoController;
import main.java.controllerandview.algocontrollers.ControllerComplexScalperAlgo;
import main.java.controllerandview.algocontrollers.ControllerSimpleScalperAlgo;
import main.java.model.MainModelThread;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.positionsorders.MikePosition;
import main.java.model.priceserver.PriceServer;
import main.java.controllerandview.MikeGridPane;

import java.io.IOException;

public class ControllerPositionsWindow implements MikeGridPane.MikeButtonHandler, MainGUIClass.Updatable {

    @FXML
    public BorderPane mainBorderPane;
    public ListView positionsList;
    public ListView instrumentsList;


    //todo: experimenting:
//    public ChoiceBox choiceBoxColSel;
//    public Pane algoChoicePane;
//    public AnchorPane columnActionsAnchorPane;
//    public VBox algoChoiceVbox;

    public AlgoController algoController;
    public ChoiceBox choiceBoxCol1;
    public ChoiceBox choiceBoxCol2;
    public ChoiceBox choiceBoxCol3;
    public AnchorPane anPaneCol1;
    public AnchorPane anPaneCol2;
    public AnchorPane anPaneCol3;
    public ChoiceBox choiceBoxCol4;
    public AnchorPane anPaneCol4;
    public ChoiceBox choiceBoxCol5;
    public AnchorPane anPaneCol5;
    public ChoiceBox choiceBoxCol6;
    public AnchorPane anPaneCol6;
    public ChoiceBox choiceBoxCol7;
    public AnchorPane anPaneCol7;

    public AlgoController controllerCol1 = null;
    public AlgoController controllerCol2 = null;
    public AlgoController controllerCol3= null;
    public AlgoController controllerCol4= null;
    public AlgoController controllerCol5= null;
    public AlgoController controllerCol6= null;
    public AlgoController controllerCol7= null;


    private ControllerComplexScalperAlgo controllerComplexScalperAlgo;

    private ControllerSimpleScalperAlgo controllerSimpleScalperAlgo;


    //todo: erase this after experiment finished:
    int selection = 0;

    @FXML
    private TextField TopRowPriceTextField;
    @FXML
    private TextField askPriceTextField;
    @FXML
    private TextField bidPriceTextField;
    private TextField experimentalTextField;
    @FXML
    private TextField askVolumeTextField;
    @FXML
    private TextField bidVolumeTextField;
    @FXML
    private TextField totalOpenPosTextField;
    @FXML
    private TextField weighedAveragePriceTextField;
    @FXML
    private TextField zeroProfitPointTextField;
    @FXML
    private TextField totalPLTextField;
    @FXML
    private TextField totalOpenPLTextField;
    @FXML
    private TextField totalClosedPLTextField;
    @FXML
    private TextField orderSizeTextField;
    @FXML
    private TextField orderPriceTextField;
    @FXML
    private Button buyLimitButton;
    @FXML
    private Button sellLimitButton;
    @FXML
    private Button buyStopButton;



    private int topRowPrice = 27150; //used with MikeGridPane and UpdateGUI

    private int tickerId = 0;
    private MikeGridPane mikeGridPane = null;

    public MikeGridPane topMikeGridPane = null;
    public MikeGridPane bottomMikeGridPane = null;

    private PriceServer priceServer;
    private MainModelThread model;
    private MikePosOrders mikePosOrders;

    //private ObservableList<List<Integer>> pricelist;

    public void setInstrumentList(ObservableList<PriceServer> instrumentNamesList) {
        instrumentsList.setItems(instrumentNamesList);
    }

    @FXML
    public void initialize(){

        setUpChangeListeners();


//        instrumentsList.getSelectionModel().selectFirst();
//        positionsList.getSelectionModel().selectFirst();

//        if (!instrumentsList.getItems().isEmpty()) {
//            instrumentsList.getSelectionModel().select(0);
//        }
//
//
//        if (!positionsList.getItems().isEmpty()) {
//            positionsList.getSelectionModel().select(0);
//        }



    }

    /**
     * Watch for off by 1 errors!
     * Returns the algoController assigned to the column specified.
     * colNumber = 0 is the first column and is controlled by controllerCol1
     * @param colNumber 0 is the first column
     * @return
     */
    private AlgoController getAlgoControllerOfColumn(int colNumber) {
        switch (colNumber) {
            case 0: if (controllerCol1 != null) {return controllerCol1;}
            break;
            case 1: if (controllerCol2 != null) {return controllerCol2;}
            break;
            case 2: if (controllerCol3 != null) {return controllerCol3;}
            break;
            case 3: if (controllerCol4 != null) {return controllerCol4;}
            break;
            case 4: if (controllerCol5 != null) {return controllerCol5;}
            break;
            case 5: if (controllerCol6 != null) {return controllerCol6;}
            break;
            case 6: if (controllerCol7 != null) {return controllerCol7;}
            break;

//            default: return null;
        }
        return null;
    }




    /**
     * This sets up the controllers for each of the columns in MikeGridPane
     * Used by ChoiceBoxChangeListener
     * @param columnNumber
     * @param algoContr
     */
    protected void setAlgoController(int columnNumber, AlgoController algoContr) {
        switch (columnNumber) {
            case 1:
                controllerCol1 = algoContr;
                break;
            case 2:
                controllerCol2 = algoContr;
                break;
            case 3:
                controllerCol3 = algoContr;
                break;
            case 4:
                controllerCol4 = algoContr;
                break;
            case 5:
                controllerCol5 = algoContr;
                break;
            case 6:
                controllerCol6 = algoContr;
                break;
            case 7:
                controllerCol7 = algoContr;
                break;
        }



    }

    /**
     * Internal function used to set up all the change listeners for choosing instrument traded, posOrders window refers to,
     * and what pressing buttons in MikeGridPane does
     */
    private void setUpChangeListeners(){


        //this handles changing the instrument PositionsWindow refers to based on what the user
        //selected in in ListView instrumentlist:
        class MyInstrumentChangeListener implements ChangeListener {
            ControllerPositionsWindow controllerPositionsWindow;
            MyInstrumentChangeListener(ControllerPositionsWindow controllerPositionsWindow){
                this.controllerPositionsWindow = controllerPositionsWindow;
            }
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                try {
                    //set the priceserver to the one chosen:
                    controllerPositionsWindow.priceServer = (PriceServer) instrumentsList.getSelectionModel().getSelectedItem();
                    //set the tickerId:
                    tickerId = ((PriceServer) instrumentsList.getSelectionModel().getSelectedItem()).getTickerID();
                    //we changed the instrument, change the PosOrders available to be chosen to those for that insturment:
                    positionsList.setItems(model.posOrdersManager.getPosOrdersObservableList(tickerId));
                    System.out.println("Chosen: " + controllerPositionsWindow.priceServer.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        MyInstrumentChangeListener listener = new MyInstrumentChangeListener(this);
        instrumentsList.getSelectionModel().selectedItemProperty().addListener( listener );

        //this handles changing the MikePosOrders this window controls/displays
        class MyPosOrdersChangeListener implements ChangeListener {
            ControllerPositionsWindow controllerPositionsWindow;
            MyPosOrdersChangeListener(ControllerPositionsWindow controllerPositionsWindow){
                this.controllerPositionsWindow = controllerPositionsWindow;
            }
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                try {
                    //set the MikePosOrders to the one selected:
                    controllerPositionsWindow.mikePosOrders = (MikePosOrders) positionsList.getSelectionModel().getSelectedItem();
                    System.out.println("Chosen: " + ((MikePosOrders) positionsList.getSelectionModel().getSelectedItem()).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        MyPosOrdersChangeListener posListener = new MyPosOrdersChangeListener(this);
        positionsList.getSelectionModel().selectedItemProperty().addListener(posListener);


        //setup the column action tabs:
        class ChoiceBoxChangeListener implements ChangeListener {
            ControllerPositionsWindow controllerPositionsWindow;
            ChoiceBox cb;
            int colNumber;
            AnchorPane anchorPane;

            public ChoiceBoxChangeListener(ControllerPositionsWindow controllerPositionsWindow, ChoiceBox choiceBox, AnchorPane anchorPaneToSet, int colNumber) {
                this.controllerPositionsWindow = controllerPositionsWindow;
                this.cb = choiceBox;
                this.colNumber = colNumber;
                this.anchorPane = anchorPaneToSet;
            }

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                System.out.println("Chosen: " + cb.getSelectionModel().getSelectedItem());

                //THIS IS WHERE YOU SET THE ACTIONS:
                if(cb.getSelectionModel().getSelectedItem() == "SimpleScalper1"){
                    try {
                        //LOAD A DIFFERENT FXML FILE DEPENDING ON THE SELECTION OF THE CHOICEBOX
                        //and set the anchorPane according to that file, and set the controller for it:
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/algoControllers/SimpleScalperAlgoControlPanel.fxml"));
                        Parent anchorPaneParent = loader.load();
                        //this will make the anchorPane display what was in the fxml file:
                        anchorPane.getChildren().setAll(anchorPaneParent);
                        ControllerSimpleScalperAlgo controllerSimpleScalperAlgo = loader.getController();
                        //MikeGridPane will call this controller whenever a button inside MikeGridPane is pressed:


                        controllerPositionsWindow.setAlgoController(colNumber, controllerSimpleScalperAlgo);


                        System.out.println("Choicebox algo setting successful");
                    } catch (Exception e) {
                        System.out.println("Exception in Choicebox algo setting");
                    }
                }
                if(cb.getSelectionModel().getSelectedItem() == "ComplexScalper1"){
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/algoControllers/ComplexScalperControlPanel.fxml"));

                        Parent anchorPaneParent = loader.load();
                        anchorPane.getChildren().setAll(anchorPaneParent);
                        ControllerComplexScalperAlgo controllerSimpleScalperAlgo = loader.getController();

                        controllerPositionsWindow.setAlgoController(colNumber, controllerSimpleScalperAlgo);

                        System.out.println("Choicebox algo setting successful");
                    } catch (Exception e) {
                        System.out.println("Exception in Choicebox algo setting");
                    }
                }
            }
        }

//        choiceBoxColSel.getItems().addAll("Column1", "Column2", "Column3");


        String[] algosAvailable = {"SimpleScalper1", "ComplexScalper1", "New One!"};

        choiceBoxCol1.getItems().addAll(algosAvailable);
        choiceBoxCol2.getItems().addAll(algosAvailable);
        choiceBoxCol3.getItems().addAll(algosAvailable);
        choiceBoxCol4.getItems().addAll(algosAvailable);
        choiceBoxCol5.getItems().addAll(algosAvailable);
        choiceBoxCol6.getItems().addAll(algosAvailable);
        choiceBoxCol7.getItems().addAll(algosAvailable);


        ChoiceBoxChangeListener listenerCol1 = new ChoiceBoxChangeListener(this, choiceBoxCol1, anPaneCol1,  1);
        choiceBoxCol1.getSelectionModel().selectedItemProperty().addListener(listenerCol1);

        ChoiceBoxChangeListener listenerCol2 = new ChoiceBoxChangeListener(this, choiceBoxCol2, anPaneCol2,  2);
        choiceBoxCol2.getSelectionModel().selectedItemProperty().addListener(listenerCol2);

        ChoiceBoxChangeListener listenerCol3 = new ChoiceBoxChangeListener(this, choiceBoxCol3, anPaneCol3, 3);
        choiceBoxCol3.getSelectionModel().selectedItemProperty().addListener(listenerCol3);

        ChoiceBoxChangeListener listenerCol4 = new ChoiceBoxChangeListener(this, choiceBoxCol4, anPaneCol4, 4);
        choiceBoxCol4.getSelectionModel().selectedItemProperty().addListener(listenerCol4);

        ChoiceBoxChangeListener listenerCol5 = new ChoiceBoxChangeListener(this, choiceBoxCol5, anPaneCol5, 5);
        choiceBoxCol5.getSelectionModel().selectedItemProperty().addListener(listenerCol5);

        ChoiceBoxChangeListener listenerCol6 = new ChoiceBoxChangeListener(this, choiceBoxCol6, anPaneCol6, 6);
        choiceBoxCol6.getSelectionModel().selectedItemProperty().addListener(listenerCol6);

        ChoiceBoxChangeListener listenerCol7 = new ChoiceBoxChangeListener(this, choiceBoxCol7, anPaneCol7, 7);
        choiceBoxCol7.getSelectionModel().selectedItemProperty().addListener(listenerCol7);

    }

    public void updateGUI(){

//        String exper = priceServer.getExperimentalNumber().toString();
//        experimentalTextField.setText(exper);


        try {
            //display realtime bid ask priceserver:
            askPriceTextField.setText("" + (int)priceServer.getAskPrice());
            bidPriceTextField.setText("" + (int)priceServer.getBidPrice());

            //display the open amount:
            totalOpenPosTextField.setText("" + (int)mikePosOrders.getTotalOpenAmount());

            //display the average price:
            double averagePrice = (double)mikePosOrders.getAveragePrice();
            weighedAveragePriceTextField.setText("" + averagePrice);
            double zeroProfitPoint = mikePosOrders.getZeroProfitPoint();
            zeroProfitPointTextField.setText(""+zeroProfitPoint);


            //display the PL:
            totalOpenPLTextField.setText("" + mikePosOrders.getOpenPL());
            totalClosedPLTextField.setText("" + mikePosOrders.getClosedPL());
            totalPLTextField.setText("" + mikePosOrders.getTotalPL());

            //update the top column description depending on what has been set:
            for (int col = 0; col < 7; col++) {

                AlgoController controller = getAlgoControllerOfColumn(col);

                    if (null != controller) {
                        //Display short description on top:

                        topMikeGridPane.getButton(0, (col)).setText(getAlgoControllerOfColumn((col)).getSimpleDescriptionRow1());
                        topMikeGridPane.getButton(1, (col)).setText(getAlgoControllerOfColumn((col)).getSimpleDescriptionRow2());

////                        System.out.println(" testing: " + getAlgoControllerOfColumn(col).getSimpleDescriptionRow1());
//
//                        String toPrint = controller.getSimpleDescriptionRow1();
//
                        MikeGridPane.MikeButton buttonToPrint = topMikeGridPane.getButton(0, col);
                        buttonToPrint.setFont(Font.font(11));

//                        System.out.println("" + buttonToPrint.getColOfButton());
//                        buttonToPrint.setText("Hello!");

//                        topMikeGridPane.getButton(0, col).setText(toPrint);

                    }


            }

            //printout data in MikeGridPane:
            for(int row = 0 ; row < mikeGridPane.getHowManyRows() ; row++){
                int priceToPrint = topRowPrice - row;
                MikePosition position = mikePosOrders.getMikePositionAtPrice(priceToPrint);

                //print open positions in first column:
                if (position == null) {
                    setSpecificButtonInMikeGridPane( row,0, "" );
                } else {
                    setSpecificButtonInMikeGridPane(row, 0, ""+ position.getOpen_amount());
                }

                //print active buy orders for the given price in the second column:
                if(mikePosOrders.ordersAtPrice.getOpenBuyOrdersAtPrice(priceToPrint) != 0){
                    setSpecificButtonInMikeGridPane(row, 1,
                            "" + mikePosOrders.ordersAtPrice.getOpenBuyOrdersAtPrice(priceToPrint));
                } else {
                    setSpecificButtonInMikeGridPane(row, 1, "");
                }


                //todo: testing:
                //print "BID" in the row of the bid price in third column:
                //and experiment to play with the colors:
                MikeGridPane.MikeButton button = mikeGridPane.getButton(row, 2);
                if (topRowPrice - row == priceServer.getBidPrice()) {
                    setSpecificButtonInMikeGridPane(row, 2, "BID");
                    button.setStyle("-fx-background-color: blue; -fx-text-fill: red; -fx-font-weight: bolder; -fx-border-color : black");
                    button.setBorder(new Border(new BorderStroke(Color.BLACK,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                }else{
                    setSpecificButtonInMikeGridPane(row, 2, "");
                    button.setStyle("-fx-background-color: white");
                    button.setBorder(new Border(new BorderStroke(Color.BLACK,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                }


                //print prices in the fourth column of mikeGridPane:
                setSpecificButtonInMikeGridPane( row,3, "" +(topRowPrice - row)
                );

                //print "ASK" in the row of the ask price in fifth column:
                if (topRowPrice - row == priceServer.getAskPrice()) {
                    setSpecificButtonInMikeGridPane(row, 4, "ASK");
                }else{
                    setSpecificButtonInMikeGridPane(row, 4, "");
                }

                //print active sell orders for the given price in the sixth column:
                if(mikePosOrders.ordersAtPrice.getOpenSellOrdersAtPrice(priceToPrint) != 0){
                    setSpecificButtonInMikeGridPane(row, 5,
                            "" + mikePosOrders.ordersAtPrice.getOpenSellOrdersAtPrice(priceToPrint));
                } else {
                    setSpecificButtonInMikeGridPane(row, 5, "");
                }

                //print the total open amount in the row who's price equals to the average weighed position in seventh column.
                //todo: print out the zeroprofit point
                if( priceToPrint == (int)zeroProfitPoint){
                    setSpecificButtonInMikeGridPane(row, 6,
                            "" + (int)mikePosOrders.getTotalOpenAmount());
                } else {
                    setSpecificButtonInMikeGridPane(row, 6, "");
                }





            }
        } catch (Exception e) {
            System.out.println("EXCEPTION IN POSITIONSWINDOW UPDATE GUI");
            e.printStackTrace();
        }

    }

    @Override
    public void handleMikeButtonClicked(MikeGridPane.MikeButton button) {

        System.out.println("MikeButton clicked. column: " +button.getColOfButton());
        System.out.println("Price clicked: " + getPriceOfRow(button.getRowOfButton()));


        //todo: testing:
        int price = getPriceOfRow(button.getRowOfButton());
        if (button.getColOfButton() == 0) {

            //todo: testing:
            System.out.println("Testing algoController. ");

            controllerCol1.mikeGridPaneButtonPressed(price, model, mikePosOrders);
//            algoController.mikeGridPaneButtonPressed(price, model, mikePosOrders);



//            System.out.println("Testing. Creating SimpleScalperAlgo. LowTarget: " + getPriceOfRow(button.getRowOfButton())
//            + " HighTarget: LowTarget +5 (hardcoded now), amount: 100.");
//            model.algoManager.createScalperAlgo1(mikePosOrders, price, price + 5, 100, MikeOrder.MikeOrderType.BUYLMT);
        }
        if (button.getColOfButton() == 1) {


            controllerCol2.mikeGridPaneButtonPressed(price, model, mikePosOrders);

//            System.out.println("Testing. Creating StepperAlgoUp1. ");
//            model.algoManager.createStepperAlgoUp1(mikePosOrders, price, 5, 100);
        }
        if (button.getColOfButton() == 2) {

            controllerCol3.mikeGridPaneButtonPressed(price, model, mikePosOrders);

//            System.out.println("Testing Creating ComplexScalperAlgo1");
//            model.algoManager.createComplexScalperAlgoUp1(mikePosOrders, price, 1, 10, 100, MikeOrder.MikeOrderType.BUYLMT);

        }
        if (button.getColOfButton() == 3) {
            System.out.println("Testing Creating ComplexScalperAlgo1");
            model.algoManager.createComplexScalperAlgoUp1(mikePosOrders, price, 1, 10, 100, MikeOrder.MikeOrderType.BUYSTP);
        }
        if (button.getColOfButton() == 4) {
            System.out.println("Testing Creating ComplexScalperAlgo1");
            model.algoManager.createComplexScalperAlgoUp1(mikePosOrders, price, -1, 10, 100, MikeOrder.MikeOrderType.SELLLMT);
        }
//        if (button.getColOfButton() == 3) {
//            System.out.println("Testing. Creating SimpleScalperAlgo. LowTarget: " + getPriceOfRow(button.getRowOfButton())
//                    + " HighTarget: LowTarget +5 (hardcoded now), amount: 100.");
//            model.algoManager.createScalperAlgo1(mikePosOrders, price, price + 5, 100, MikeOrder.MikeOrderType.BUYSTP);
//        }
//
//        if (button.getColOfButton() == 4) {
//            System.out.println("Testing. Creating SimpleScalperAlgo. LowTarget: " + getPriceOfRow(button.getRowOfButton())
//                    + " HighTarget: LowTarget +5 (hardcoded now), amount: 100.");
//            model.algoManager.createScalperAlgo1(mikePosOrders, price, price - 5, 100, MikeOrder.MikeOrderType.SELLLMT);
//        }

        if (button.getColOfButton() == 5) {
            System.out.println("Testing. Creating SimpleScalperAlgo. LowTarget: " + getPriceOfRow(button.getRowOfButton())
                    + " HighTarget: LowTarget +5 (hardcoded now), amount: 100.");
            model.algoManager.createScalperAlgo1(mikePosOrders, price, price - 5, 100, MikeOrder.MikeOrderType.SELLSTP);
        }



    }

    public void setSpecificButtonInMikeGridPane(int row, int col, String text) {
        mikeGridPane.getButton(row, col).setText(text);
    }

    @FXML
    public void testOneButtonClicked(ActionEvent actionEvent) {
//        model.getOrderServer().checkSimulatedFills(model.getPriceServer());

        Button button = (Button)(actionEvent.getSource());
        button.setText("Print Positions");
        //printout positions to console:
        mikePosOrders.printPositionsToConsole();
    }

    @FXML
    public void testTwoButtonClicked(ActionEvent actionEvent) {
//        model.getOrderServer().printActiveOrdersToConsole();

//        //todo: testing:
//        System.out.println("Column Selected: " + choiceBoxColSel.getSelectionModel().getSelectedItem());
//
//
//        //experimenting:
//        //can I change the contents of the anchorpane?
//
//
//        if (selection == 0/*set to 0 at startup of program*/) {
//            try {
//
//                //todo: experimenting:
//
//                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/algoControllers/SimpleScalperAlgoControlPanel.fxml"));
//                Parent anchorPaneParent = loader2.load();
//                columnActionsAnchorPane.getChildren().setAll(anchorPaneParent);
//                ControllerSimpleScalperAlgo controllerSimpleScalperAlgo = loader2.getController();
//                algoController = controllerSimpleScalperAlgo;
//
//                System.out.println("Test two part 1 OK");
//            } catch (Exception e) {
//                System.out.println("Exception in test two part 1");
//            }
//            selection = 1;
//        } else {
//            try {
//
//                FXMLLoader loader2;
//
//                //try a different FXML file:
//                loader2 = new FXMLLoader(getClass().getResource("/algoControllers/ComplexScalperControlPanel.fxml"));
//                Parent anchorPaneParent = loader2.load();
//                System.out.println(" Second Anchorpane loaded");
//
//                columnActionsAnchorPane.getChildren().setAll(anchorPaneParent);
//
//                controllerComplexScalperAlgo = loader2.getController();
//
//                algoController = controllerComplexScalperAlgo;
//
//
//                System.out.println("Second Anchorpane set");
//            } catch (IOException e) {
//                System.out.println("Exception in part 2");
//                e.printStackTrace();
//            }
//
//            selection = 0;
//        }


    }

    @FXML
    private void testThreeButtonClicked(){


//        System.out.println("Amount: " + controllerCol1.getAmount());


    }

    /**
     * This used for choosing what price range is displayed in MikeGridPane.
     * @param actionEvent
     */
    public void setTopRowPriceBtnClicked(ActionEvent actionEvent) {
        Integer topRowPriceToBeSet = Integer.parseInt(TopRowPriceTextField.getText());
        topRowPrice = topRowPriceToBeSet;
    }

    /**
     * set the current top row price to ask price + 50
     */
    public void ask50Clicked(ActionEvent event) {
        topRowPrice = priceServer.getAskPrice() + 50;
    }

    public void buyLimitButtonClicked(ActionEvent actionEvent) {
        Integer price = Integer.parseInt(orderPriceTextField.getText());
        Integer amount = Integer.parseInt(orderSizeTextField.getText());

        System.out.println("Buy limit pressed. Order price: " + price + " Order size: " + amount);

        mikePosOrders.placeNewOrder(MikeOrder.MikeOrderType.BUYLMT, price, price, amount);
//        model.getOrderServer().placeNewOrder(MikeOrder.MikeOrderType.BUYLMT, price, price, amount);
    }

    private int getPriceOfRow(int rowClicked) {
        return topRowPrice - rowClicked;
    }

    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    public void setMikeGridPane(MikeGridPane mikeGridPane) {
        this.mikeGridPane = mikeGridPane;
    }

    public void setModel(MainModelThread model) {
        this.model = model;
    }

    public MikePosOrders getMikePosOrders() {
        return mikePosOrders;
    }

    public void setMikePosOrders(MikePosOrders mikePosOrders) {
        this.mikePosOrders = mikePosOrders;
    }

    public void sellLimitButtonClicked(ActionEvent actionEvent) {
        Integer price = Integer.parseInt(orderPriceTextField.getText());
        Integer amount = Integer.parseInt(orderSizeTextField.getText());

        System.out.println("Sell limit pressed. Order price: " + price + " Order size: " + amount);

        mikePosOrders.placeNewOrder(MikeOrder.MikeOrderType.SELLLMT, price, price, amount);
//        model.getOrderServer().placeNewOrder(MikeOrder.MikeOrderType.SELLLMT, price, price, amount);
    }

    public void buyStopButtonClicked(ActionEvent actionEvent) {

        Integer price = Integer.parseInt(orderPriceTextField.getText());
        Integer amount = Integer.parseInt(orderSizeTextField.getText());

        System.out.println("Buy stop pressed. Order price: " + price + " Order size: " + amount);

        mikePosOrders.placeNewOrder(MikeOrder.MikeOrderType.BUYSTP, price, price, amount);
//        model.getOrderServer().placeNewOrder(MikeOrder.MikeOrderType.BUYSTP, price, price, amount);
    }

    public void sellStopButtonClicked(ActionEvent actionEvent) {
        Integer price = Integer.parseInt(orderPriceTextField.getText());
        Integer amount = Integer.parseInt(orderSizeTextField.getText());

        System.out.println("Sell stop pressed. Order price: " + price + " Order size: " + amount);

        mikePosOrders.placeNewOrder(MikeOrder.MikeOrderType.SELLSTP, price, price, amount);
//        model.getOrderServer().placeNewOrder(MikeOrder.MikeOrderType.SELLSTP, price, price, amount);
    }

//    public void mikeGridPaneButtonClicked(ActionEvent event) {
//
//    }
//    public void setAskPriceTextField(Integer price){
//        askPriceTextField.setText(price.toString());
//    }
//    public void setExperimentalTextField(int num) {
//        experimentalTextField.setText("" + num);
//    }
//    public MikeGridPane getMikeGridPane(){
//        return mikeGridPane;
//    }
}
