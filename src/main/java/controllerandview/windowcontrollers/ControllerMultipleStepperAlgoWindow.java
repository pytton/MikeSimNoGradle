


/**
 * Sample Skeleton for 'MultipleStepperAlgoWindow.fxml' Controller Class
 */

package main.java.controllerandview.windowcontrollers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import main.java.controllerandview.CommonGUI;
import main.java.controllerandview.MainGUIClass;
import main.java.controllerandview.MikeGridPane;
import main.java.model.MikeSimLogger;
import main.java.model.positionsorders.MikePosOrders;
import main.java.model.priceserver.PriceServer;

public class ControllerMultipleStepperAlgoWindow implements
        MikeGridPane.MikeButtonHandler,
        MainGUIClass.Updatable,
        CommonGUI.ICommonGUI {

    public TextField orderAmount;
    public TextField targetInterval;
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="mainBorderPane"
    private BorderPane mainBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="askPriceTextField"
    private TextField askPriceTextField; // Value injected by FXMLLoader

    @FXML // fx:id="bidPriceTextField"
    private TextField bidPriceTextField; // Value injected by FXMLLoader

    @FXML // fx:id="totalOpenPosTextField"
    private TextField totalOpenPosTextField; // Value injected by FXMLLoader

    @FXML // fx:id="weighedAveragePriceTextField"
    private TextField weighedAveragePriceTextField; // Value injected by FXMLLoader

    @FXML // fx:id="zeroProfitPointTextField"
    private TextField zeroProfitPointTextField; // Value injected by FXMLLoader

    @FXML // fx:id="totalOpenPLTextField"
    private TextField totalOpenPLTextField; // Value injected by FXMLLoader

    @FXML // fx:id="totalClosedPLTextField"
    private TextField totalClosedPLTextField; // Value injected by FXMLLoader

    @FXML // fx:id="totalPLTextField"
    private TextField totalPLTextField; // Value injected by FXMLLoader

    @FXML // fx:id="cancelAlgosThisBookBtn"
    private Button cancelAlgosThisBookBtn; // Value injected by FXMLLoader

    @FXML // fx:id="testOneButton"
    private Button testOneButton; // Value injected by FXMLLoader

    @FXML // fx:id="testTwoButton"
    private Button testTwoButton; // Value injected by FXMLLoader

    @FXML // fx:id="testThreeButton"
    private Button testThreeButton; // Value injected by FXMLLoader

    @FXML // fx:id="setTopRowPriceButton"
    private Button setTopRowPriceButton; // Value injected by FXMLLoader

    @FXML // fx:id="TopRowPriceTextField"
    private TextField TopRowPriceTextField; // Value injected by FXMLLoader

    @FXML // fx:id="ask50"
    private Button ask50; // Value injected by FXMLLoader

    @FXML
    void ask50Clicked(ActionEvent event) {

    }

    @FXML
    void cancelAlgosThisBookBtnPressed(ActionEvent event) {

    }

    @FXML
    void setTopRowPriceBtnClicked(ActionEvent event) {

    }

    @FXML
    void testOneButtonClicked(ActionEvent event) {

    }

    @FXML
    void testThreeButtonClicked(ActionEvent event) {

    }

    @FXML
    void testThreeMouseClicked(MouseEvent event) {

    }

    @FXML
    void testTwoButtonClicked(ActionEvent event) {

    }

    public void oAmtBtnPressed(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        orderAmount.setText(button.getText());
    }

    public void intervalBtnPressed(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        targetInterval.setText(button.getText());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert mainBorderPane != null : "fx:id=\"mainBorderPane\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert askPriceTextField != null : "fx:id=\"askPriceTextField\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert bidPriceTextField != null : "fx:id=\"bidPriceTextField\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert totalOpenPosTextField != null : "fx:id=\"totalOpenPosTextField\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert weighedAveragePriceTextField != null : "fx:id=\"weighedAveragePriceTextField\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert zeroProfitPointTextField != null : "fx:id=\"zeroProfitPointTextField\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert totalOpenPLTextField != null : "fx:id=\"totalOpenPLTextField\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert totalClosedPLTextField != null : "fx:id=\"totalClosedPLTextField\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert totalPLTextField != null : "fx:id=\"totalPLTextField\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert cancelAlgosThisBookBtn != null : "fx:id=\"cancelAlgosThisBookBtn\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert testOneButton != null : "fx:id=\"testOneButton\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert testTwoButton != null : "fx:id=\"testTwoButton\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert testThreeButton != null : "fx:id=\"testThreeButton\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert setTopRowPriceButton != null : "fx:id=\"setTopRowPriceButton\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert TopRowPriceTextField != null : "fx:id=\"TopRowPriceTextField\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";
        assert ask50 != null : "fx:id=\"ask50\" was not injected: check your FXML file 'MultipleStepperAlgoWindow.fxml'.";

    }

    @Override
    public void setPriceServer(PriceServer priceServer) {
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED!");
        
    }

    @Override
    public PriceServer getPriceServer() {
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED!");
        return null;
    }

    @Override
    public void setMikePosOrders(MikePosOrders posOrders) {
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED!");
    }

    @Override
    public void updateGUI() {
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED!");
    }


    @Override
    /**
     * This handles what happens when a button in MikeGridPane is clicked
     */
    public void handleMikeButtonClicked(MikeGridPane.MikeButton event) {
        MikeSimLogger.addLogEvent("NOT IMPLEMENTED!");
//        MikeSimLogger.addLogEvent(event.);
    }


    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }
}
