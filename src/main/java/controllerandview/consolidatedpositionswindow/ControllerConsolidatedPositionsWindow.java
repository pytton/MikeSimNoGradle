package main.java.controllerandview.consolidatedpositionswindow;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import main.java.controllerandview.MainGUIClass;
import main.java.controllerandview.MikeGridPane;
import main.java.model.MainModelThread;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.positionsorders.MikePosition;
import main.java.model.priceserver.PriceServer;

public class ControllerConsolidatedPositionsWindow implements MikeGridPane.MikeButtonHandler, MainGUIClass.Updatable {

    @FXML
    public BorderPane mainBorderPane;
    public ListView positionsList;
    public ListView instrumentsList;
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
    private PriceServer priceServer;
    private MainModelThread model;
    private MikePosOrders mikePosOrders;

    //private ObservableList<List<Integer>> pricelist;

    public void setInstrumentList(ObservableList<PriceServer> instrumentNamesList) {
        instrumentsList.setItems(instrumentNamesList);
    }

    @FXML
    public void initialize(){
        //this handles changing the instrument PositionsWindow refers to based on what the user
        //selected in in ListView instrumentlist:
        class MyInstrumentChangeListener implements ChangeListener {
            ControllerConsolidatedPositionsWindow controllerPositionsWindow;
            MyInstrumentChangeListener(ControllerConsolidatedPositionsWindow controllerPositionsWindow){
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
            ControllerConsolidatedPositionsWindow controllerPositionsWindow;
            MyPosOrdersChangeListener(ControllerConsolidatedPositionsWindow controllerPositionsWindow){
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
    }

    public void mikeGridPaneButtonClicked(ActionEvent event) {

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



                //print "BID" in the row of the bid price in third column:
                if (topRowPrice - row == priceServer.getBidPrice()) {
                    setSpecificButtonInMikeGridPane(row, 2, "BID");
                }else{
                    setSpecificButtonInMikeGridPane(row, 2, "");
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

    public void setSpecificButtonInMikeGridPane(int row, int col, String text) {
        mikeGridPane.getButton(row, col).setText(text);
    }

    public void otherButtonClicked(ActionEvent actionEvent) {
        //experiment:
//        mikeGridPane.getButton(3,7).setPrefWidth(30);
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
    }

    @FXML
    private void testThreeButtonClicked(){

        model.marketConnection.consolePrintRealTimeData();

        System.out.println("Clicked");
//        String exper = priceServer.getExperimentalNumber().toString();
//        askVolumeTextField.setText(exper);




        //display realtime bid ask priceserver:
//        askPriceTextField.setText(((Double)priceServer.getRealTimeAskPrice()).toString());
//        bidPriceTextField.setText(((Double)priceServer.getRealTimeBidPrice()).toString());

        //experiment:
//        mikeGridPane.getButton(3,7).setPrefWidth(120);

    }

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

    private int getPriceOfRow(int rowClicked) {
        return topRowPrice - rowClicked;
    }

    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    public MikeGridPane getMikeGridPane(){
        return mikeGridPane;
    }

    public void setMikeGridPane(MikeGridPane mikeGridPane) {
        this.mikeGridPane = mikeGridPane;
    }

    public void setAskPriceTextField(Integer price){
        askPriceTextField.setText(price.toString());
    }

    public void setExperimentalTextField(int num) {
        experimentalTextField.setText("" + num);
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


    @Override
    public void handleMikeButtonClicked(MikeGridPane.MikeButton button) {

        System.out.println("MikeButton clicked. column: " +button.getColOfButton());
        System.out.println("Price clicked: " + getPriceOfRow(button.getRowOfButton()));


        //todo: testing:
        int price = getPriceOfRow(button.getRowOfButton());
        if (button.getColOfButton() == 0) {
            System.out.println("Testing. Creating SimpleScalperAlgo. LowTarget: " + getPriceOfRow(button.getRowOfButton())
            + " HighTarget: LowTarget +5 (hardcoded now), amount: 100.");
            model.algoManager.createScalperAlgo1(mikePosOrders, price, price + 5, 100, MikeOrder.MikeOrderType.BUYLMT);
        }
        if (button.getColOfButton() == 1) {
            System.out.println("Testing. Creating StepperAlgoUp1. ");
            model.algoManager.createStepperAlgoUp1(mikePosOrders, price, 5, 100);
        }
        if (button.getColOfButton() == 2) {
            System.out.println("Testing Creating ComplexScalperAlgoUp1");
            model.algoManager.createComplexScalperAlgoUp1(mikePosOrders, price, 1, 10, 100, MikeOrder.MikeOrderType.BUYLMT);

        }
        if (button.getColOfButton() == 3) {
            System.out.println("Testing Creating ComplexScalperAlgoUp1");
            model.algoManager.createComplexScalperAlgoUp1(mikePosOrders, price, 1, 10, 100, MikeOrder.MikeOrderType.BUYSTP);
        }
        if (button.getColOfButton() == 4) {
            System.out.println("Testing Creating ComplexScalperAlgoUp1");
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
}
