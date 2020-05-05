package main.java.controllerandview.algomanagerwindow;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import main.java.model.MainModelThread;
import main.java.model.mikealgos.MikeAlgo;

import java.util.ArrayList;
import java.util.List;

public class ControllerAlgoManagerPanel {


    private MainModelThread model;


    @FXML
    private ListView algoList;



    @FXML
    public void initialize() {
        System.out.println("AlgoManagerPanel created");
        algoList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }




    public void setAlgoList(ObservableList<MikeAlgo> algoList) {
        System.out.println("Attempting ");
        this.algoList.setItems(algoList);
    }

    public void setModel(MainModelThread model) {
        this.model = model;
    }

    public void cancelSelectedAlgoBtnPressed(ActionEvent actionEvent) {
        if (algoList.getSelectionModel().getSelectedItem() != null) {

            List<MikeAlgo> algosToCancel = new ArrayList<>();
            algosToCancel.addAll(algoList.getSelectionModel().getSelectedItems());

            for(MikeAlgo algo : algosToCancel) {
                model.algoManager.cancelAlgo(algo);
            }
        }
    }

    public void sortAlgoListBtnPressed(ActionEvent actionEvent) {
        model.algoManager.sortAlgosbyPrice();
    }
}
