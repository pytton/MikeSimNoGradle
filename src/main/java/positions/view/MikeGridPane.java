package main.java.positions.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;

import java.util.ArrayList;

import static javafx.geometry.Pos.CENTER;

/**
 *Custom GridPane with buttons that can be accessed with getButton function
 */
public class MikeGridPane extends GridPane {


    /**
     *
     * Button which can tell which row and column it occupies. Has to be set after constructing it.
     */
    public class MikeButton extends Button{
        public int getRowOfButton() {
            return rowOfButton;
        }

        public void setRowOfButton(int rowOfButton) {
            this.rowOfButton = rowOfButton;
        }

        public int getColOfButton() {
            return colOfButton;
        }

        public void setColOfButton(int colOfButton) {
            this.colOfButton = colOfButton;
        }

        int rowOfButton = 0;
        int colOfButton = 0;
        public MikeButton() {
            super();
        }
        public MikeButton(int rowPosition, int colPosition){
            this();
            rowOfButton = rowPosition;
            colOfButton = colPosition;
        }
    }

    public int getHowManyRows() {
        int rows = howManyRows;
        return rows;
    }

    public int getHowManyCols() {
        return howManyCols;
    }

    private int howManyRows = 20;
    private int howManyCols = 7;

    /**
     * Buttons in this GridPane. Access via getButton(row,column)
     */
    Button[][] buttons = new Button[howManyRows][howManyCols];

    ArrayList<ArrayList<MikeButton>> buttonList = new ArrayList<>();

    public Button getButton(int row, int col) {
        if(row<= howManyRows && col<=howManyCols) return buttons[row][col];
        else return null;
    }



    public MikeGridPane() {
        super();
        this.setVgap(0);
        this.setHgap(1);
//        this.setPrefHeight(10);

        //Set the row height:
        RowConstraints rowConstraints = new RowConstraints(20);

        //add buttons to grid:
        for (int row = 0; row < howManyRows; row++) {
//            RowConstraints rowConstr = new RowConstraints(7);
            this.getRowConstraints().add(rowConstraints);

            buttonList.add(new ArrayList<MikeButton>());


            for (int col = 0; col < howManyCols; col++) {
                MikeButton button = new MikeButton(row, col);
//                button.setPrefWidth(50);

                int number = col + (row + col);
                button.setText(""+number);
                button.setFont(Font.font(15));
                button.setPadding(new Insets(0, 0, 0, 0));


                buttons[row][col] = button;
                buttonList.get(row).add(button);


                button.setPrefWidth(45);
                button.setMinHeight(20);
                button.setMaxHeight(20);
                button.setPrefHeight(20);


//                button.setMaxHeight(5);
//                button.setStyle("-fx-background-color: lightgrey;-fx-border-color: black; -fx-border-width: 1px");
//                button.setStyle("-fx-background-color: white");
//                System.out.println(button.getLabelPadding());
//                System.out.println(button.getLayoutBounds());


                this.add(button, col, row);

            }




//            this.getRowConstraints().get(row).setPrefHeight(6);
        }
        this.setAlignment(CENTER);


        System.out.println("MikeGridPane generated");
    }
}
