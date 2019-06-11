package main.java.model;

import javafx.scene.control.Button;
import main.java.positions.view.MikeGridPane;

public class MainLoop extends Thread {

    MikeGridPane mikeGridPane;

    public MainLoop(MikeGridPane mikeGridPane) {
        this.mikeGridPane = mikeGridPane;
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

        }
    }
}
