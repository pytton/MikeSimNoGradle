package main.java.model;

import javafx.application.Platform;
import main.java.controllerandview.positionswindow.controller.ControllerPositionsWindow;
import main.java.model.priceserver.PriceServer;

import java.util.Date;

public class TimedRunnable implements Runnable {

    Date timeStarted = new Date();
    Date timeFinished = new Date();
    long started;
    long finished;
    long elapsed;
    int count = 0;
    private boolean isReady = true;
    private PriceServer priceServer = null;
    private ControllerPositionsWindow controllerPositionsWindow = null;

    public void stopLooping() {
        isReady = false;
    }

    public boolean isReady() {
        return isReady;
    }


    @Override
    public void run() {


//        started = timeStarted.getTime();

        while (isReady) {

            started = System.currentTimeMillis();


            for (int i = 0; i < 20; i++) {
                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 0, "" + ((Math.sqrt((count * 79 + count))) * 1) % 567);
                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 1, "" + count);
                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 2, "" + Math.sqrt((count * 79 + i + count)) % 7);
                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 3, "" + Math.sqrt((count * 79 + i + count)) % 56);
                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 4, "" + Math.sqrt((count * 73 + i + count)) % 74);
                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 5, "" + Math.sqrt((count * 987 + i + count)) % 34);
                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 6, "" + Math.sqrt((count * 453 + i + count)) % 9);
            }
            controllerPositionsWindow.askPriceTextField.setText(((Double) priceServer.getRealTimeAskPrice()).toString());
            controllerPositionsWindow.bidPriceTextField.setText(((Double) priceServer.getRealTimeBidPrice()).toString());


            //check how long it takes to print out
            finished = System.currentTimeMillis();
            elapsed = finished - started;
            System.out.println("Printout took: " + elapsed + " ms");

            count++;
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isReady = true;
    }

    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

    public void setControllerPositionsWindow(ControllerPositionsWindow controllerPositionsWindow) {
        this.controllerPositionsWindow = controllerPositionsWindow;
    }

}
