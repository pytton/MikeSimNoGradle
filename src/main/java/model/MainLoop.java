package main.java.model;

import javafx.application.Platform;
import main.java.controllerandview.MainGUIController;
import main.java.model.priceserver.PriceServer;
import main.java.controllerandview.positionswindow.view.MikeGridPane;

public class MainLoop extends Thread {

    private long count = 0;
    public static boolean interrupted;
    int refreshInMiliseconds = 100;
    MikeGridPane mikeGridPane;
    MainGUIController mainGUIController;
    GUIUpdateDispatcher myGUIUpdateDispatcher;

    public void setMikeGridPane(MikeGridPane mikeGridPane) {
        this.mikeGridPane = mikeGridPane;
    }

    public MainLoop(MainGUIController mainGUIController){
        this.mainGUIController = mainGUIController;
    }
    @Override
    public void run() {

        myGUIUpdateDispatcher = new GUIUpdateDispatcher();
        myGUIUpdateDispatcher.setMainGUIController(mainGUIController);

        while (!interrupted) {
            try {
                if(myGUIUpdateDispatcher.isReady()){
                Platform.runLater(myGUIUpdateDispatcher);}
                else {
                    System.out.println("myGUIUpdateDispatcher not ready");
                }

                if (count%10 == 0) System.out.println("Mainloop count: " + count);
                count++;
                Thread.sleep(refreshInMiliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class GUIUpdateDispatcher implements Runnable {

        MainGUIController mainGUIController;
        boolean isReady = true;
        long count = 0;

        public void setMainGUIController(MainGUIController mainGUIController) {
            this.mainGUIController = mainGUIController;
        }

        @Override
        public void run() {
            isReady = false;
            mainGUIController.updateGUI();
            count++;
            isReady = true;
        }

        public boolean isReady() {
            return isReady;
        }
    }
}
