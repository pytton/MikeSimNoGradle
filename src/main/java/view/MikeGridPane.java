package main.java.view;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import static javafx.geometry.Pos.CENTER;

/**
 *Custom GridPane with buttons that can be accessed with getButton function
 */
public class MikeGridPane extends GridPane {

    public int getHowManyRows() {
        int rows = howManyRows;
        return rows;
    }

    public int getHowManyCols() {
        return howManyCols;
    }

    private int howManyRows = 20;
    private int howManyCols = 6;

    /**
     * Buttons in this GridPane. Access via getButton(row,column)
     */
    Button[][] buttons = new Button[howManyRows][howManyCols];

    public Button getButton(int row, int col) {
        if(row<= howManyRows && col<=howManyCols) return buttons[row][col];
        else return null;
    }



    public MikeGridPane() {
        super();
        this.setVgap(1);
        this.setHgap(1);



        //add buttons to grid:
        for (int row = 0; row < howManyRows; row++) {
            for (int col = 0; col < howManyCols; col++) {
                Button button = new Button();
//                button.setPrefWidth(50);
                button.setText(""+(row+col));
                this.add(button, col, row);
                buttons[row][col] = button;
                button.setPrefWidth(70);
                button.setPrefHeight(10);
                button.setMaxHeight(5);
                button.setStyle("-fx-background-color: lightgrey;-fx-border-color: black; -fx-border-width: 1px");
//                button.setStyle("-fx-background-color: white");
            }
        }
        this.setAlignment(CENTER);


        System.out.println("MikeGridPane generated");
    }
}
