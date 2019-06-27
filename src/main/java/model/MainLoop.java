package main.java.model;

import javafx.application.Platform;
import javafx.scene.control.Button;
import main.java.controllerandview.positionswindow.controller.ControllerPositionsWindow;
import main.java.model.priceserver.PriceServer;
import main.java.controllerandview.positionswindow.view.MikeGridPane;

public class MainLoop extends Thread {

    public void setMikeGridPane(MikeGridPane mikeGridPane) {
        this.mikeGridPane = mikeGridPane;
    }

    MikeGridPane mikeGridPane;
    PriceServer priceServer;
    ControllerPositionsWindow controllerPositionsWindow;

    TimedRunnable myTimedRunnable;


    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

//    public MainLoop(MikeGridPane mikeGridPane) {
//        this.mikeGridPane = mikeGridPane;
//    }

//    public MainLoop(MikeGridPane mikeGridPane, PriceServer priceServer) {
//        this.mikeGridPane = mikeGridPane;
//        this.priceServer = priceServer;
//    }

    public MainLoop(PriceServer priceServer, ControllerPositionsWindow controllerPositionsWindow) {
        this.priceServer = priceServer;
        this.controllerPositionsWindow = controllerPositionsWindow;
    }


    private Integer count = 0;
    public static boolean interrupted;

    public void run() {

        myTimedRunnable = new TimedRunnable();

        while (!interrupted) {
            try {
                if (mikeGridPane != null) {

                    Button button = mikeGridPane.getButton(8, 3);
//                    button.setText(count.toString());

//                    if(myTimedRunnable.isReady()){
//                        myTimedRunnable.setControllerPositionsWindow(controllerPositionsWindow);
//                        myTimedRunnable.setPriceServer(priceServer);
//                        Platform.runLater(myTimedRunnable);
//                    }
                }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controllerPositionsWindow.updateGUI();
                        }
                    });

//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            controllerPositionsWindow.setSpecificButtonInMikeGridPane(8, 3, "" + count);
//
//                            for (int i = 0; i < 20; i++) {
//                                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 0, "" + ((Math.sqrt((count * 79 + count))) * 1) % 567);
//                                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 1, "" + count);
//                                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 2, "" + Math.sqrt((count * 79 + i + count)) % 7);
//                                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 3, "" + Math.sqrt((count * 79 + i + count)) % 56);
//                                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 4, "" + Math.sqrt((count * 73 + i + count)) % 74);
//                                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 5, "" + Math.sqrt((count * 987 + i + count)) % 34);
//                                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i, 6, "" + Math.sqrt((count * 453 + i + count)) % 9);
//                            }
//                            controllerPositionsWindow.askPriceTextField.setText(((Double) priceServer.getRealTimeAskPrice()).toString());
//                            controllerPositionsWindow.bidPriceTextField.setText(((Double) priceServer.getRealTimeBidPrice()).toString());
//
//
//                        }
//                    });

                    count++;


                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1 second tick");
            //TODO: experimenting:
            int experimental = (int) (Math.random() * 100);
//            priceServer.setExperimentalNumber(experimental);
//            controllerPositionsWindow.setExperimentalTextField(experimental);
        }
    }
}
