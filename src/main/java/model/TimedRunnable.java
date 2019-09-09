package main.java.model;


import main.java.controllerandview.MainGUIController;
import main.java.controllerandview.positionswindow.controller.ControllerPositionsWindow;
import main.java.model.priceserver.PriceServer;

class TimedRunnable implements Runnable {

    PriceServer priceServer;
    ControllerPositionsWindow controllerPositionsWindow;

    public void setMainGUIController(MainGUIController mainGUIController) {
        this.mainGUIController = mainGUIController;
    }

    MainGUIController mainGUIController;

    boolean isReady = true;
    int count = 0;

    @Override
    public void run() {
        isReady = false;


//        controllerPositionsWindow.updateGUI();

        mainGUIController.updateGUI();

        System.out.println("myTimedRunnable called.");

//
//
//        for (int i = 0; i < 20; i++) {
//            controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 0, "" + ((Math.sqrt((count * 79 + count))) * 1) % 567);
//            controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 1, "" + count);
//            controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 2, "" + Math.sqrt((count * 79 + i + count)) % 7);
//            controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 3, "" + Math.sqrt((count * 79 + i + count)) % 56);
//            controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 4, "" + Math.sqrt((count * 73 + i + count)) % 74);
//            controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 5, "" + Math.sqrt((count * 987 + i + count)) % 34);
//            controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 6, "" + Math.sqrt((count * 453 + i + count)) % 9);
//        }
//
//        controllerPositionsWindow.askPriceTextField.setText(((Double) priceServer.getRealTimeAskPrice()).toString());
//        controllerPositionsWindow.bidPriceTextField.setText(((Double) priceServer.getRealTimeBidPrice()).toString());

        System.out.println("Timedrunnable count: " + count);

        count++;

        try {
            Thread.sleep(600);
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

    public boolean isReady() {
        return isReady;
    }
}