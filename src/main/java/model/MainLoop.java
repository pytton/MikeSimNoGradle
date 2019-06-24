package main.java.model;

import javafx.application.Platform;
import javafx.scene.control.Button;
import main.java.controllerandview.positionswindow.controller.ControllerPositionsWindow;
import main.java.model.priceserver.PriceServer;
import main.java.controllerandview.positionswindow.view.MikeGridPane;

import java.util.Date;

public class MainLoop extends Thread {

    public void setMikeGridPane(MikeGridPane mikeGridPane) {
        this.mikeGridPane = mikeGridPane;
    }

    MikeGridPane mikeGridPane;
    PriceServer priceServer;
    private ControllerPositionsWindow controllerPositionsWindow;

    public void setPriceServer(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

    public MainLoop(MikeGridPane mikeGridPane) {
        this.mikeGridPane = mikeGridPane;
    }

    public MainLoop(MikeGridPane mikeGridPane, PriceServer priceServer) {
        this.mikeGridPane = mikeGridPane;
        this.priceServer = priceServer;
    }

    public MainLoop(PriceServer priceServer, ControllerPositionsWindow controllerPositionsWindow) {
        this.priceServer = priceServer;
        this.controllerPositionsWindow = controllerPositionsWindow;
    }



    private Integer count = 0;
    public static boolean interrupted;
    public void run()  {

        TimedRunnable myTimedRunnable = new TimedRunnable();
        myTimedRunnable.setControllerPositionsWindow(controllerPositionsWindow);
        myTimedRunnable.setPriceServer(priceServer);

        while (!interrupted){


            try {
                if (mikeGridPane != null){


                    Button button = mikeGridPane.getButton(8, 3);

                    if(myTimedRunnable.isReady()){
                        Platform.runLater(myTimedRunnable);
                    }




//                    button.setText(count.toString());
//                    Platform.runLater(new Runnable() {
//
//                        Date timeStarted = new Date();
//                        Date timeFinished = new Date();
//                        long started;
//                        long finished;
//                        long elapsed;
//
//                        @Override
//                        public void run() {
//                            started = timeStarted.getTime();
//                            for (int i = 0; i < 10; i++) {
//                                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i,3, "" + (count + i));
//                                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i+5,4, "" + (count * i));
//                                controllerPositionsWindow.setSpecificButtonInMikeGridPane(i+10,5, "" + (count + i));
//                            }
//                            controllerPositionsWindow.askPriceTextField.setText(((Double)priceServer.getRealTimeAskPrice()).toString());
//                            controllerPositionsWindow.bidPriceTextField.setText(((Double)priceServer.getRealTimeBidPrice()).toString());
//
//                            //check how long it takes to print out
//                            finished = timeFinished.getTime();
//                            elapsed = started - finished;
//                            System.out.println("Printout took: "+elapsed + " ms");
//
//                        }
//                    });

                    count++;


                    System.out.println("1 second tick");
                    //TODO: experimenting:
                    int experimental = (int)(Math.random()*100);
//            priceServer.setExperimentalNumber(experimental);
                    controllerPositionsWindow.setExperimentalTextField(experimental);




                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
