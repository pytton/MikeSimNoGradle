package main.java.controllerandview.positionswindow.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import main.java.model.priceserver.PriceServer;
import main.java.controllerandview.positionswindow.view.MikeGridPane;

import java.util.List;

public class ControllerPositionsWindow {

    @FXML
    public BorderPane mainBorderPane;
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


    private MikeGridPane mikeGridPane = null;
    private PriceServer priceServer;

    //private ObservableList<List<Integer>> pricelist;


    @FXML
    private void testThreeButtonClicked(){
        //TODO: experimenting here:
        System.out.println("Clicked");
//        String exper = priceServer.getExperimentalNumber().toString();
//        askVolumeTextField.setText(exper);


        //display realtime bid ask priceserver:
        askPriceTextField.setText(((Double)priceServer.getRealTimeAskPrice()).toString());
        bidPriceTextField.setText(((Double)priceServer.getRealTimeBidPrice()).toString());

        //experiment:
//        mikeGridPane.getButton(3,7).setPrefWidth(120);

    }

    public void updateGUI(){

//        String exper = priceServer.getExperimentalNumber().toString();
//        experimentalTextField.setText(exper);


        //display realtime bid ask priceserver:
        askPriceTextField.setText("" + (int)priceServer.getAskPrice());
        bidPriceTextField.setText("" + (int)priceServer.getBidPrice());

//        System.out.println("Window updated");
    }

    public void setSpecificButtonInMikeGridPane(int row, int col, String text) {
        mikeGridPane.getButton(row, col).setText(text);
    }

    public void otherButtonClicked(ActionEvent actionEvent) {
        //experiment:
//        mikeGridPane.getButton(3,7).setPrefWidth(30);
    }

    public void testOneButtonClicked(ActionEvent actionEvent) {
    }

    public void testTwoButtonClicked(ActionEvent actionEvent) {
    }

    public void buyLimitButtonClicked(ActionEvent actionEvent) {
    }

    public void sellLimitButtonClicked(ActionEvent actionEvent) {
    }

    public void buyStopButtonClicked(ActionEvent actionEvent) {
    }

    public void sellStopButtonClicked(ActionEvent actionEvent) {
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

}
