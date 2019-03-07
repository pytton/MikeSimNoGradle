package main.java.model;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ClockTicker extends Thread {

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    GridPane gridPane;


    public void run()  {

        Integer count = 0;
        while (true){
            try {
                if (gridPane != null){
                    Button button = (Button)gridPane.getChildren().get(1);
                    button.setText(count.toString());
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
