package main.java.controllerandview.positionswindow.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import main.java.model.MainModelThread;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.positionsorders.MikePosition;
import main.java.model.priceserver.PriceServer;
import main.java.controllerandview.positionswindow.view.MikeGridPane;

public class ControllerPositionsWindow implements MikeGridPane.MikeButtonHandler {

    @FXML
    public BorderPane mainBorderPane;
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
    private TextField zeroProfitPoint;
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

    private MikeGridPane mikeGridPane = null;
    private PriceServer priceServer;
    private MainModelThread model;
    private MikePosOrders mikePosOrders;

    //private ObservableList<List<Integer>> pricelist;


    public void mikeGridPaneButtonClicked(ActionEvent event) {

    }

    public void updateGUI(){

//        String exper = priceServer.getExperimentalNumber().toString();
//        experimentalTextField.setText(exper);


        //display realtime bid ask priceserver:
        askPriceTextField.setText("" + (int)priceServer.getAskPrice());
        bidPriceTextField.setText("" + (int)priceServer.getBidPrice());

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

            //print prices in the third column of mikeGridPane:
            setSpecificButtonInMikeGridPane( row,2, "" +(topRowPrice - row)
            );
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
        model.getOrderServer().printActiveOrdersToConsole();
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


    }
}
