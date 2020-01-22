package main.java.controllerandview.mainGUIwindow.controller;

import com.ib.client.Contract;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import main.java.controllerandview.MainGUIClass;
import main.java.model.MainModelThread;
import main.java.model.priceserver.PriceServer;

public class ControllerMainGUIWindow {

    public ListView instrumentsList;
    public TextField histDataDuration;
    public TextField histDataDate;
    public TextField histDataTime;
    private MainGUIClass mainGUIClass;

    public void setModel(MainModelThread model) {
        this.model = model;
    }

    private MainModelThread model;



//    @FXML
//    private Button createPosWindowButton;

    public void setMainGUIClass(MainGUIClass controller){
        mainGUIClass = controller;
    }

    @FXML
    public void createPosWindowsButtonClicked(){
        System.out.println("Primary GUI window clicked!");
        mainGUIClass.createPosWindow();
    }

    @FXML
    public void createPriceContrWinClicked(ActionEvent actionEvent) {
        mainGUIClass.createPriceControlWindow();
    }

    @FXML
    public void connectOutsideDataClicked(){
        mainGUIClass.getMainModelThread().connectOutsideData();
    }

    /**
     * Creates a new MikePosOrders for trading the instrument selected from instrumentList
     * @param event
     */
    public void createPositionOrdersClicked(ActionEvent event) {

        int tickerId = 0;

        if (instrumentsList.getSelectionModel().getSelectedItem() != null) {
            tickerId =  ((PriceServer) instrumentsList.getSelectionModel().getSelectedItem())
                    .getTickerID();
        }

        model.posOrdersManager.createMikePosorders(tickerId);
    }

    public void createAlgoManagerWindow(ActionEvent actionEvent) {
        mainGUIClass.createAlgoManagerWindow();
    }

    public void reqHistDataClicked(ActionEvent actionEvent) {
        //todo: experimenting:

        Contract contract = new Contract();

        contract.m_symbol = "SPY";
        contract.m_exchange = "SMART";
        contract.m_secType = "STK";
        contract.m_currency = "USD";

        String endDateTime = "";
        endDateTime += (histDataDate.getText() + " ");
        endDateTime += (histDataTime.getText() + " GMT");

        String duration = "";
        duration += histDataDuration.getText();
        duration += " S";


        model.marketConnection.getEClientSocket().reqHistoricalData(0, contract, endDateTime,  duration, "1 secs", "BID", 1, 1);


    }
}