package main.java.controllerandview.positionswindow.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.java.controllerandview.MainGUIClass;
import main.java.controllerandview.algocontrollers.*;
import main.java.model.MainModelThread;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.AggregatedPosOrders;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;
import main.java.controllerandview.MikeGridPane;
import main.java.controllerandview.commonviewelements.CommonGUI;

import java.util.*;

/**
 * To create this window, you need to call createPosWindow() in MainGUIClass.java
 */
public class ControllerPositionsWindow
        implements MikeGridPane.MikeButtonHandler,
        MainGUIClass.Updatable,
        CommonGUI.ICommonGUI {


    private int topRowPrice = 27150; //used with MikeGridPane and UpdateGUI
    private int bottomRowPrice = 27100;
    private int tickerId = 0;

    private MikeGridPane mikeGridPane = null;
    public MikeGridPane topMikeGridPane = null;
    public MikeGridPane bottomMikeGridPane = null;

    private PriceServer priceServer;
    private MainModelThread model;
    protected MikePosOrders mikePosOrders;
    private AggregatedPosOrders aggregatedPosOrders = new AggregatedPosOrders();
    @FXML
    public BorderPane mainBorderPane;
    @FXML //this is where all the orders are routed to
    private ListView positionsList;
    @FXML //this holds the instruments available for trading
    private ListView instrumentsList;
    @FXML
    private CheckBox aggregatedCheckBox;
    @FXML //this is where secondary actions are routed to
    public ListView targetPositionsList;

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

    public ChoiceBox choiceBoxCol1;
    public ChoiceBox choiceBoxCol2;
    public ChoiceBox choiceBoxCol3;
    public ChoiceBox choiceBoxCol4;
    public ChoiceBox choiceBoxCol5;
    public ChoiceBox choiceBoxCol6;
    public ChoiceBox choiceBoxCol7;

    public AnchorPane anPaneCol1;
    public AnchorPane anPaneCol2;
    public AnchorPane anPaneCol3;
    public AnchorPane anPaneCol4;
    public AnchorPane anPaneCol5;
    public AnchorPane anPaneCol6;
    public AnchorPane anPaneCol7;

    public AlgoController controllerCol1 = null;
    public AlgoController controllerCol2 = null;
    public AlgoController controllerCol3= null;
    public AlgoController controllerCol4= null;
    public AlgoController controllerCol5= null;
    public AlgoController controllerCol6= null;
    public AlgoController controllerCol7= null;

    public void setInstrumentList(ObservableList<PriceServer> instrumentNamesList) {
        instrumentsList.setItems(instrumentNamesList);
    }

    @FXML
    public void initialize(){
        setUpChangeListeners();
        positionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
                    //and also the targetPosOrders:
                    targetPositionsList.setItems(model.posOrdersManager.getPosOrdersObservableList(tickerId));

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

                    //rename the window:
                    Stage stage = (Stage) controllerPositionsWindow.getMainBorderPane().getScene().getWindow();
                    stage.setTitle(((MikePosOrders) positionsList.getSelectionModel().getSelectedItem()).getName());

                    //notify aggregatedPosOrders about all the selected MikePosOrders:
                    aggregatedPosOrders.setPosOrdersList(positionsList.getSelectionModel().getSelectedItems());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        MyPosOrdersChangeListener posListener = new MyPosOrdersChangeListener(this);
        positionsList.getSelectionModel().selectedItemProperty().addListener(posListener);

        //setup the column action tabs. define algo controllers here :
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

                if(cb.getSelectionModel().getSelectedItem() == "SimpleStepperAlgo"){
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/algoControllers/SimpleStepperControlPanel.fxml"));

                        Parent anchorPaneParent = loader.load();
                        anchorPane.getChildren().setAll(anchorPaneParent);
                        ControllerSimpleStepperAlgo controller = loader.getController();

                        controllerPositionsWindow.setAlgoController(colNumber, controller);

                        System.out.println("Choicebox algo setting successful");
                    } catch (Exception e) {
                        System.out.println("Exception in Choicebox algo setting");
                    }
                }

                if(cb.getSelectionModel().getSelectedItem() == "PlainOrder"){
                    try {
                        //and set the anchorPane according to that file, and set the controller for it:
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/algoControllers/PlainOrderControlPanel.fxml"));
                        Parent anchorPaneParent = loader.load();
                        //this will make the anchorPane display what was in the fxml file:
                        anchorPane.getChildren().setAll(anchorPaneParent);
                        ControllerPlainOrder controller = loader.getController();
                        //let Plain Order Controller know where to find targetPosOrders for transferring positions:
                        controller.setControllerPositionsWindow(controllerPositionsWindow);
                        //MikeGridPane will call this controller whenever a button inside MikeGridPane is pressed:
                        controllerPositionsWindow.setAlgoController(colNumber, controller);

                        System.out.println("Choicebox algo setting successful");
                    } catch (Exception e) {
                        System.out.println("Exception in Choicebox algo setting");
                    }
                }

            }
        }

        List<String> algosAvailable = Arrays.asList("SimpleScalper1", "ComplexScalper1", "SimpleStepperAlgo", "PlainOrder");

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
        if(aggregatedCheckBox.isSelected() == true){
            aggregatedPosOrders.recalcutlatePL();
            printout(aggregatedPosOrders);}
        else printout(mikePosOrders);
    }

    @Override
    /**
     * This is called whenever a button in MikeGridPane is clicked
     */
    public void handleMikeButtonClicked(MikeGridPane.MikeButton button) {

        int price = getPriceOfRow(button.getRowOfButton());

        switch (button.getColOfButton()) {
            case 0: if(controllerCol1 != null) controllerCol1.mikeGridPaneButtonPressed(price, model, mikePosOrders); break;
            case 1: if(controllerCol2 != null)controllerCol2.mikeGridPaneButtonPressed(price, model, mikePosOrders); break;
            case 2: if(controllerCol3 != null)controllerCol3.mikeGridPaneButtonPressed(price, model, mikePosOrders); break;
            case 3: if(controllerCol4 != null)controllerCol4.mikeGridPaneButtonPressed(price, model, mikePosOrders); break;
            case 4: if(controllerCol5 != null)controllerCol5.mikeGridPaneButtonPressed(price, model, mikePosOrders); break;
            case 5: if(controllerCol6 != null)controllerCol6.mikeGridPaneButtonPressed(price, model, mikePosOrders); break;
            case 6: if(controllerCol7 != null)controllerCol7.mikeGridPaneButtonPressed(price, model, mikePosOrders); break;
        }
    }

    @FXML
    public void testOneButtonClicked(ActionEvent actionEvent) {
//        model.getOrderServer().checkSimulatedFills(model.getPriceServer());

//        Button button = (Button)(actionEvent.getSource());
//        button.setText("Print Positions");
//        //printout positions to console:
//        mikePosOrders.printPositionsToConsole();

        System.out.println("Transferring all positions to target PosOrders");

        MikePosOrders targetPosOrders = mikePosOrders;
        if (targetPositionsList.getSelectionModel().getSelectedItem() != null) {
            targetPosOrders = (MikePosOrders)targetPositionsList.getSelectionModel().getSelectedItem();
        }

        Set<Integer> positionPricesToMove = new HashSet<>();

        positionPricesToMove.addAll(mikePosOrders.getPositionPricesSet());

        for (int price : positionPricesToMove) {
            mikePosOrders.movePositionToDifferentMikePosOrders(price, targetPosOrders);
        }




    }

    @FXML
    public void testTwoButtonClicked(ActionEvent actionEvent) {

        //testing: set mikePosOrders using dialog:


        System.out.println("PriceServer before change: " + priceServer.toString());

        CommonGUI.setPriceServer(this, model);


        System.out.println("PriceServer after change: " + priceServer.toString());

//        //testing: generate new MikeGridPane with more columns:
//
//        mikeGridPane = new MikeGridPane(30, 12, this);
//
//        VBox topVbox = new VBox();
//        topMikeGridPane = new MikeGridPane(3, 12, new MikeGridPane.EmptyMikeButtonHandler());
//        bottomMikeGridPane = new MikeGridPane(1, 12, new MikeGridPane.EmptyMikeButtonHandler());
//
//        topMikeGridPane.setPadding(new Insets(0, 15, 0, 0));
//        bottomMikeGridPane.setPadding(new Insets(0, 15, 0, 0));
//        topVbox.getChildren().add(topMikeGridPane);
//
//        ScrollPane sp = new ScrollPane();
//        sp.setContent(mikeGridPane);
//        sp.setFitToWidth(true);
//        topVbox.getChildren().add(sp);
//        topVbox.getChildren().add(bottomMikeGridPane);
//
//        getMainBorderPane().setLeft(topVbox);



    }

    @FXML
    private void testThreeButtonClicked(){

        System.out.println("Test three clicked");

        List<PriceServer> dialogData;

        dialogData = model.posOrdersManager.getPriceServerObservableList();

                //Arrays.asList(arrayData);

        ChoiceDialog dialog = new ChoiceDialog(dialogData.get(0), dialogData);
        dialog.setTitle("Trading Instrument Selection");
        dialog.setHeaderText("Select your choice");

        Optional<PriceServer> result = dialog.showAndWait();
        PriceServer selected = null;// = "cancelled.";

        if (result.isPresent()) {

            selected = result.get();
            System.out.println("Selection: " + selected.toString() + " Current bid price: " + selected.getBidPrice());




        }






//        System.out.println("Size: " + aggregatedPosOrders.posOrdersList.size());
//
//        Set<MikePosOrders> posOrders = new HashSet<>();
//
//        posOrders.addAll( positionsList.getSelectionModel().getSelectedItems());
//
//        for (MikePosOrders positions : posOrders) {
//            System.out.println("Selected: " + positions.getName());
//        }


    }

    /**
     * Used for choosing what price range is displayed in MikeGridPane.
     * @param actionEvent
     */
    public void setTopRowPriceBtnClicked(ActionEvent actionEvent) {
        Integer topRowPriceToBeSet = Integer.parseInt(TopRowPriceTextField.getText());
//        topRowPrice = topRowPriceToBeSet;
        setTopRowPrice(topRowPriceToBeSet);
    }


    ScrollPane sp = null;
    public void setSp(ScrollPane sp) {
        this.sp = sp;
    }
    /**
     * set the current top row price to ask price + 50
     */
    public void ask50Clicked(ActionEvent event) {
//        topRowPrice = priceServer.getAskPrice() + 50;
//        bottomRowPrice = topRowPrice - mikeGridPane.getHowManyRows();
        setTopRowPrice(priceServer.getAskPrice() + 50);
        sp.setVvalue(0.5);
    }

    private void setTopRowPrice(int topRowPrice) {
        this.topRowPrice = topRowPrice;
        this.bottomRowPrice = topRowPrice - mikeGridPane.getHowManyRows();
    }

    public void buyLimitButtonClicked(ActionEvent actionEvent) {
        Integer price = Integer.parseInt(orderPriceTextField.getText());
        Integer amount = Integer.parseInt(orderSizeTextField.getText());

        System.out.println("Buy limit pressed. Order price: " + price + " Order size: " + amount);

        mikePosOrders.placeNewOrder(MikeOrder.MikeOrderType.BUYLMT, price, price, amount);
//        model.getOrderServer().placeNewOrder(MikeOrder.MikeOrderType.BUYLMT, price, price, amount);
    }


    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

    @Override
    public PriceServer getPriceServer() {
        return priceServer;
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

    /**
     * Sets the primary MikePosOrders that this Window is operating on
     * @param mikePosOrders
     */
    @Override
    public void setMikePosOrders(MikePosOrders mikePosOrders) {
        this.mikePosOrders = mikePosOrders;
    }

    /**
     * Defines MikePosOrders available to be selected in this window.
     * This needs to be called when changing the instrument traded
     * @param positionsList
     */
    public void setPositionsList(ObservableList positionsList) { this.positionsList.setItems(positionsList); }

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


    public void testThreeMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            System.out.println("Test Three RIGHT BUTTON CLICKED");
        }
    }

    public void cancelAlgosThisBookBtnPressed(ActionEvent actionEvent) {
        model.algoManager.cancelAllAlgosInMikePosOrders(mikePosOrders);
        System.out.println("Cancelling all algos in this Book!");
    }

    public void cancelAlgosGloballyBtnPressed(ActionEvent actionEvent) {
        model.algoManager.cancelAllAlgosGlobally();
        System.out.println("Cancelling all algos GLOBALLY!!!");
    }

    /**
     * Prints out all the data in the window. Ask/Bid price, open pos, total PL etc;
     * also prints out everthing in MikeGridPane.
     * If you want to print aggregated data from multiple positions, pass in aggregatedPosOrders as argument;
     * For a single MikePosOrders, pass in mikePosOrders
     */
    private synchronized void printout(MikePosOrders mikePosOrders) {
        try {

            int totalOpenAmount = mikePosOrders.getTotalOpenAmount();
            Double averagePrice = 0.0;
            if (mikePosOrders.getAveragePrice() != null) {
                averagePrice = mikePosOrders.getAveragePrice();
            }

            Double zeroProfitPoint = 0.0;
            if(mikePosOrders.getZeroProfitPoint() != null) {
                zeroProfitPoint = mikePosOrders.getZeroProfitPoint();
            }

            int totalPL = mikePosOrders.getTotalPL();


            //display realtime bid ask priceserver:
            askPriceTextField.setText("" + (int)priceServer.getAskPrice());
            bidPriceTextField.setText("" + (int)priceServer.getBidPrice());
            //display the total open position:

            totalOpenPosTextField.setText("" + totalOpenAmount);
            //set the color depending on position long/short
            if (totalOpenAmount >= 0) {
                totalOpenPosTextField.setStyle("-fx-background-color: lightblue; -fx-text-fill: blue; -fx-font-weight: bolder; -fx-border-color : black");
            } else {
                totalOpenPosTextField.setStyle("-fx-background-color: salmon; -fx-text-fill: crimson; -fx-font-weight: bolder; -fx-border-color : black");
            }
            //display the average price:

            weighedAveragePriceTextField.setText("" + averagePrice);
            zeroProfitPointTextField.setText(""+zeroProfitPoint);
            //display the PL:
            totalOpenPLTextField.setText("" + mikePosOrders.getOpenPL());
            totalClosedPLTextField.setText("" + mikePosOrders.getClosedPL());
            totalPLTextField.setText("" + totalPL);
            if (totalPL >= 0) {
                totalPLTextField.setStyle("-fx-background-color: lightblue; -fx-text-fill: blue; -fx-font-weight: bolder; -fx-border-color : black");
            } else {
                totalPLTextField.setStyle("-fx-background-color: salmon; -fx-text-fill: crimson; -fx-font-weight: bolder; -fx-border-color : black");
            }

            //printout data in MikeGridPane:
            int openLongPositionsCol = 0;
            int activeBuyOrderCol = 1;
            int bidPrintoutCol = 2;
            int zeroProfitPointCol = 3;
            int pricePrintoutCol = 3;
            int askPrintoutCol = 4;
            int activeSellOrderCol = 5;
            int openShortPositionsCol = 6;

            //update the top column description depending on what has been set:
            for (int col = 0; col < 7; col++) {

                AlgoController controller = getAlgoControllerOfColumn(col);

                //clear the topMikeGridPane:
                for (int rowToPrint = 0; rowToPrint < topMikeGridPane.getHowManyRows(); rowToPrint++) {
                    topMikeGridPane.getButton(rowToPrint, col).setFont(Font.font(11));
                    topMikeGridPane.getButton(rowToPrint, col).setStyle("-fx-background-color: lightgrey  ; -fx-font-weight: bold;-fx-border-color : black");
                    topMikeGridPane.getButton(rowToPrint, col).setText("");
                }
                if (null != controller) {
                    //Display short description on top:
                    topMikeGridPane.getButton(0, (col)).setText(getAlgoControllerOfColumn((col)).getSimpleDescriptionRow1());
                    topMikeGridPane.getButton(1, (col)).setText(getAlgoControllerOfColumn((col)).getSimpleDescriptionRow2());
                }

                //clear the BottomMikeGridPane
                bottomMikeGridPane.getButton(0, col).setStyle("-fx-background-color: grey ; -fx-font-weight: bold; -fx-border-color : black");
                bottomMikeGridPane.getButton(0, col).setText("");
            }


            for(int row = 0 ; row < mikeGridPane.getHowManyRows() ; row++){

                int priceToPrint = topRowPrice - row;
                int openPosAtPrice = mikePosOrders.getOpenAmountAtPrice(priceToPrint);

                //print open long and short positions:
                if (openPosAtPrice == 0) {
                    setSpecificButtonInMikeGridPane(row, openLongPositionsCol, "" );
                    setSpecificButtonInMikeGridPane(row, openShortPositionsCol, "" );
                    mikeGridPane.getButton(row, openLongPositionsCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
                    mikeGridPane.getButton(row, openShortPositionsCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
                } else {
                    if (openPosAtPrice > 0) {
                        mikeGridPane.getButton(row, openLongPositionsCol).setStyle("" +
                                "-fx-background-color: lightblue  ; -fx-text-fill: blue; -fx-font-weight: bolder;-fx-border-color : black");
                        setSpecificButtonInMikeGridPane(row, openLongPositionsCol, "" + openPosAtPrice);
                    } else if(openPosAtPrice < 0){
                        mikeGridPane.getButton(row, openShortPositionsCol).setStyle("" +
                                "-fx-background-color: lightred; -fx-text-fill: red; -fx-font-weight: bolder; -fx-border-color : black");
                        setSpecificButtonInMikeGridPane(row, openShortPositionsCol, "" + openPosAtPrice);
                    }
                }


                //print active buy orders for the given price:
                if(mikePosOrders.getOpenBuyOrdersAtPrice(priceToPrint) != 0){
                    setSpecificButtonInMikeGridPane(row, activeBuyOrderCol,"" + mikePosOrders.getOpenBuyOrdersAtPrice(priceToPrint));
                    mikeGridPane.getButton(row, activeBuyOrderCol).setStyle("-fx-background-color: lightblue; -fx-text-fill: blue; -fx-font-weight: bolder; -fx-border-color : black");
                } else {
                    setSpecificButtonInMikeGridPane(row, activeBuyOrderCol, "");
                    mikeGridPane.getButton(row, activeBuyOrderCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
                }


                //print "BID" in the row of the bid price:
                MikeGridPane.MikeButton button = mikeGridPane.getButton(row, 2);
                if (topRowPrice - row == priceServer.getBidPrice()) {
                    setSpecificButtonInMikeGridPane(row, bidPrintoutCol, "BID");
                    button.setStyle("-fx-background-color: yellow; -fx-text-fill: blue; -fx-font-weight: bolder; -fx-border-color : black");
                }else{
                    setSpecificButtonInMikeGridPane(row, bidPrintoutCol, "");
                    mikeGridPane.getButton(row, bidPrintoutCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");

                    //if it is outside mikeGridPane, print it in the top or bottom gridPanes:
                    if (priceServer.getBidPrice() >topRowPrice) {
                        topMikeGridPane.getButton(2, bidPrintoutCol).setText("BID");
                        topMikeGridPane.getButton(2, bidPrintoutCol).setStyle("-fx-background-color: yellow; -fx-text-fill: blue; -fx-font-weight: bolder; -fx-border-color : black");
                    }
                    if (priceServer.getBidPrice() <bottomRowPrice) {
                        bottomMikeGridPane.getButton(0, bidPrintoutCol).setText("BID");
                        bottomMikeGridPane.getButton(0, bidPrintoutCol).setStyle("-fx-background-color: yellow; -fx-text-fill: blue; -fx-font-weight: bolder; -fx-border-color : black");
                    }
                }

                //print prices in the fourth column of mikeGridPane:
                button = mikeGridPane.getButton(row, pricePrintoutCol);
                setSpecificButtonInMikeGridPane( row, pricePrintoutCol, "" +(topRowPrice - row));
                button.setStyle("-fx-background-color: lightgrey; -fx-text-fill: black; -fx-font-weight: bold");

                //print "ASK" in the row of the ask price in fifth column:
                button = mikeGridPane.getButton(row, askPrintoutCol);
                if (topRowPrice - row == priceServer.getAskPrice()) {
                    setSpecificButtonInMikeGridPane(row, askPrintoutCol, "ASK");
                    button.setStyle("-fx-background-color: yellow; -fx-text-fill: red; -fx-font-weight: bolder; -fx-border-color : black");
                }else{
                    setSpecificButtonInMikeGridPane(row, askPrintoutCol, "");
                    mikeGridPane.getButton(row, askPrintoutCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");

                    //if it is outside mikeGridPane, print it in the top or bottom gridPanes:
                    if (priceServer.getAskPrice() >topRowPrice) {
                        topMikeGridPane.getButton(2, askPrintoutCol).setText("ASK");
                        topMikeGridPane.getButton(2, askPrintoutCol).setStyle("-fx-background-color: yellow; -fx-text-fill: red; -fx-font-weight: bolder; -fx-border-color : black");
                    }
                    if (priceServer.getAskPrice() <bottomRowPrice) {
                        bottomMikeGridPane.getButton(0, askPrintoutCol).setText("ASK");
                        bottomMikeGridPane.getButton(0, askPrintoutCol).setStyle("-fx-background-color: yellow; -fx-text-fill: red; -fx-font-weight: bolder; -fx-border-color : black");
                    }
                }

                //print active sell orders for the given price:
                if(mikePosOrders.getOpenSellOrdersAtPrice(priceToPrint) != 0){
                    setSpecificButtonInMikeGridPane(row, activeSellOrderCol,"" + mikePosOrders.getOpenSellOrdersAtPrice(priceToPrint));
                    mikeGridPane.getButton(row, activeSellOrderCol).setStyle("-fx-background-color: salmon; -fx-text-fill: crimson; -fx-font-weight: bolder; -fx-border-color : black");
                } else {
                    setSpecificButtonInMikeGridPane(row, activeSellOrderCol, "");
                    mikeGridPane.getButton(row, activeSellOrderCol).setStyle("-fx-background-color: lightyellow  ; -fx-border-color : black");
                }

                //print the zero profit point
                if( priceToPrint == zeroProfitPoint.intValue()){

                    setSpecificButtonInMikeGridPane(row, zeroProfitPointCol,"" + totalOpenAmount);
                    //color the button according to the position being long/short:
                    if(totalOpenAmount>0) mikeGridPane.getButton(row, zeroProfitPointCol).setStyle("-fx-text-fill: blue; -fx-font-weight: bolder");
                    if(totalOpenAmount<0) mikeGridPane.getButton(row, zeroProfitPointCol).setStyle("-fx-text-fill: red; -fx-font-weight: bolder");
                    if(totalOpenAmount==0) mikeGridPane.getButton(row, zeroProfitPointCol).setStyle("-fx-background-color: white");

                } else {

                    //if it is outside mikeGridPane, print it in the top or bottom gridPanes:
                    if (zeroProfitPoint>topRowPrice) {
                        topMikeGridPane.getButton(0, zeroProfitPointCol).setText("" + totalOpenAmount);
                        //color the button according to the position being long/short:
                        if(totalOpenAmount>0) topMikeGridPane.getButton(0, zeroProfitPointCol).setStyle("-fx-text-fill: blue; -fx-font-weight: bolder; -fx-font-size: 14");
                        if(totalOpenAmount<0) topMikeGridPane.getButton(0, zeroProfitPointCol).setStyle("-fx-text-fill: red; -fx-font-weight: bolder; -fx-font-size: 14");
                        if(totalOpenAmount==0) topMikeGridPane.getButton(0, zeroProfitPointCol).setStyle("-fx-background-color: white");
                    }
                    if (zeroProfitPoint<bottomRowPrice) {
                        bottomMikeGridPane.getButton(0, zeroProfitPointCol).setText("" + totalOpenAmount);
//                        setSpecificButtonInMikeGridPane(row, zeroProfitPointCol,"" + totalOpenAmount);
                        //color the button according to the position being long/short:
                        if(totalOpenAmount>0) bottomMikeGridPane.getButton(0, zeroProfitPointCol).setStyle("-fx-text-fill: blue; -fx-font-weight: bolder");
                        if(totalOpenAmount<0) bottomMikeGridPane.getButton(0, zeroProfitPointCol).setStyle("-fx-text-fill: red; -fx-font-weight: bolder");
                        if(totalOpenAmount==0) bottomMikeGridPane.getButton(0, zeroProfitPointCol).setStyle("-fx-background-color: white");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION IN POSITIONSWINDOW UPDATE GUI");
            e.printStackTrace();
        }
    }

    private void setSpecificButtonInMikeGridPane(int row, int col, String text) {
        mikeGridPane.getButton(row, col).setText(text);
    }

    public void sellStopButtonClicked(ActionEvent actionEvent) {
        Integer price = Integer.parseInt(orderPriceTextField.getText());
        Integer amount = Integer.parseInt(orderSizeTextField.getText());

        System.out.println("Sell stop pressed. Order price: " + price + " Order size: " + amount);

        mikePosOrders.placeNewOrder(MikeOrder.MikeOrderType.SELLSTP, price, price, amount);
//        model.getOrderServer().placeNewOrder(MikeOrder.MikeOrderType.SELLSTP, price, price, amount);
    }

    private int getPriceOfRow(int rowClicked) {
        return topRowPrice - rowClicked;
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
