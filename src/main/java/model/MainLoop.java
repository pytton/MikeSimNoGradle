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

        while (!interrupted){
            try {
                if (mikeGridPane != null){

                    Button button = mikeGridPane.getButton(8, 3);
//                    button.setText(count.toString());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controllerPositionsWindow.setSpecificButtonInMikeGridPane(8,3, "" + count);
                        }
                    });

                    count++;

                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1 second tick");
            //TODO: experimenting:
            int experimental = (int)(Math.random()*100);
            priceServer.setExperimentalNumber(experimental);
            controllerPositionsWindow.setExperimentalTextField(experimental);
        }
    }
}
