package main.java.controllerandview.positionswindow.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import main.java.model.TimedRunnable;
import main.java.model.priceserver.PriceServer;
import main.java.controllerandview.positionswindow.view.MikeGridPane;

import java.util.List;

public class ControllerPositionsWindow {

    public TextField askPriceTextField;
    public TextField bidPriceTextField;
    public BorderPane mainBorderPane;
    public TextField experimentalTextField;
    private MikeGridPane mikeGridPane = null;
    private PriceServer priceServer;

//    private TimedRunnable myTimedRunnable;

    private ObservableList<List<Integer>> pricelist;



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

//    @FXML
//    public void initialize(){
////        myTimedRunnable = new TimedRunnable();
////        myTimedRunnable.setControllerPositionsWindow(this);
////        myTimedRunnable.setPriceServer(priceServer);
//    }


    @FXML
    private void buttonClicked(){
        //TODO: experimenting here:
        System.out.println("Clicked");

//        if(myTimedRunnable.isReady()){
//            myTimedRunnable.setControllerPositionsWindow(this);
//            myTimedRunnable.setPriceServer(priceServer);
////            myTimedRunnable.start();
//
//        }else {
//
//            myTimedRunnable.stopLooping();
//        }






        String exper = priceServer.getExperimentalNumber().toString();
        experimentalTextField.setText(exper);


        //display realtime bid ask priceserver:
        askPriceTextField.setText(((Double)priceServer.getRealTimeAskPrice()).toString());
        bidPriceTextField.setText(((Double)priceServer.getRealTimeBidPrice()).toString());
    }



    int count = 0;
    private boolean isReady = true;

//    synchronized public void updateGUI(){
//
//
//            long started, finished, elapsed;
//
//            started = System.currentTimeMillis();
//
//            for (int i = 0; i < 20; i++) {
//                setSpecificButtonInMikeGridPane(i,0, "" + ((Math.sqrt((count*79+count)))*1)%567);
//                setSpecificButtonInMikeGridPane(i,1, "" + count);
//                setSpecificButtonInMikeGridPane(i,2, "" + Math.sqrt((count*79+i+count))%7);
//                setSpecificButtonInMikeGridPane(i,3, "" + Math.sqrt((count*79+i+count))%56);
//                setSpecificButtonInMikeGridPane(i,4, "" + Math.sqrt((count*73+i+count))%74);
//                setSpecificButtonInMikeGridPane(i,5, "" + Math.sqrt((count*987+i+count))%34);
//                setSpecificButtonInMikeGridPane(i,6, "" + Math.sqrt((count*453+i+count))%9);
//            }
//            askPriceTextField.setText(((Double)priceServer.getRealTimeAskPrice()).toString());
//            bidPriceTextField.setText(((Double)priceServer.getRealTimeBidPrice()).toString());
//
//
//        try {
//            Thread.sleep(50);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//            //check how long it takes to print out
//            finished = System.currentTimeMillis();
//            elapsed = finished - started;
//            System.out.println("Printout took: "+elapsed + " ms");
//
//            count++;
//
//
//    }


    public void setAskPriceTextField(Integer price){
        askPriceTextField.setText(price.toString());
    }

    public void setExperimentalTextField(int num) {
        experimentalTextField.setText("" + num);
    }

    public void setSpecificButtonInMikeGridPane(int row, int col, String text) {
        mikeGridPane.getButton(row, col).setText(text);
    }
}
