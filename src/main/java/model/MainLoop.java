package main.java.model;

import javafx.scene.control.Button;
import main.java.model.prices.PriceServer;
import main.java.controllerandview.positionswindow.view.MikeGridPane;

public class MainLoop extends Thread {

    MikeGridPane mikeGridPane;
    PriceServer priceServer;

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

    public MainLoop(PriceServer priceServer) {
        this.priceServer = priceServer;
    }

    public void run()  {

        Integer count = 0;
        while (true){
            try {
                if (mikeGridPane != null){

                    Button button = mikeGridPane.getButton(8, 3);
//                    button.setText(count.toString());
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

        }
    }
}
