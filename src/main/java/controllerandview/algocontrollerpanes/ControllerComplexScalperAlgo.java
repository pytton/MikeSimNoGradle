package main.java.controllerandview.algocontrollerpanes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.MainModelThread;
import main.java.model.orderserver.MikeOrder;
import main.java.model.positionsorders.MikePosOrders;

public class ControllerComplexScalperAlgo extends AlgoController {

    public ToggleGroup orderTypeToggleGroup;
    public RadioButton buyLimit;
    public RadioButton buyStop;
    public RadioButton sellLimit;
    public RadioButton sellStop;
    public RadioButton cancel;

    public TextField orderAmount;
    public TextField targetInterval;
    public TextField scalperCount;
    private MikeOrder.MikeOrderType orderType = MikeOrder.MikeOrderType.BUYLMT;

    private final String descriptionRow1 = "CPLX SCP1";
    private String descriptionRow2 = "B LMT";

    public TextField getOrderAmount() {
        return orderAmount;
    }

    @FXML
    public void initialize() {
//        System.out.println("ControllerSimpleScalperAlgo created.");


        //setup the kind of order passed to algoManager depending on which radiobutton is pressed:
        orderTypeToggleGroup.selectedToggleProperty().addListener(
                new ChangeListener<Toggle>() {
                    @Override
                    public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                        if (orderTypeToggleGroup.getSelectedToggle() == buyLimit) {orderType = MikeOrder.MikeOrderType.BUYLMT; descriptionRow2 = "B LMT"; }
                        if (orderTypeToggleGroup.getSelectedToggle() == buyStop) {orderType = MikeOrder.MikeOrderType.BUYSTP; descriptionRow2 = "B STP"; }
                        if (orderTypeToggleGroup.getSelectedToggle() == sellLimit) {orderType = MikeOrder.MikeOrderType.SELLLMT; descriptionRow2 = "S LMT";}
                        if (orderTypeToggleGroup.getSelectedToggle() == sellStop) {orderType = MikeOrder.MikeOrderType.SELLSTP; descriptionRow2 = "S STP";}
                        if (orderTypeToggleGroup.getSelectedToggle() == cancel) {orderType = MikeOrder.MikeOrderType.CANCEL; descriptionRow2 = "CANCEL";}
                    } } );

    }


//    @Override
//    public boolean launch(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
//        return false;
//    }

    @Override
    public boolean cancel(int entryPrice, MainModelThread model, MikePosOrders posOrders) {
        return false;
    }

    @Override
    public void mikeGridPaneButtonPressed(int pricePressed, MainModelThread model, MikePosOrders posOrders) {


        if (orderType != MikeOrder.MikeOrderType.CANCEL) {
            model.algoManager.createComplexScalperAlgoUp1(posOrders, pricePressed, getInterval(), getScCount(), getAmount(), orderType);
        } else {
            model.algoManager.cancelAllComplexScalperAlgo1sAtPrice(pricePressed, posOrders);
        }

        System.out.println("ControllerComplexScalperAlgo. Price clicked: " + pricePressed);
    }

    @Override
    public String getSimpleDescriptionRow1() {
        return descriptionRow1;
    }

    @Override
    public String getSimpleDescriptionRow2() {
        return descriptionRow2;
    }

    @Override
    public String getSimpleDescriptionRow3() {
        return orderAmount.getText();
    }

    private int getAmount() {
        return Integer.parseInt(orderAmount.getText());
    }

    private int getInterval() {
        return Integer.parseInt(targetInterval.getText());
    }

    private int getScCount() {
        return Integer.parseInt(scalperCount.getText());
    }

    public void oAmtBtnPressed(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
//        System.out.println("Pressed: " + button.getText());
        orderAmount.setText(button.getText());
    }

    public void intervalBtnPressed(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        targetInterval.setText(button.getText());
    }
}
