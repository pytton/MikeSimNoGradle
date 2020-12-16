/**
 * Sample Skeleton for 'GuardAlgoPane1.fxml' Controller Class
 */

package main.java.controllerandview.algocontrollers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ControllerGuardAlgoPane1 {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="guardedPosition"
    private Button guardedPosition; // Value injected by FXMLLoader

    @FXML // fx:id="ordersSentTo"
    private Button ordersSentTo; // Value injected by FXMLLoader

    @FXML // fx:id="bufferDistanceUp"
    private TextField bufferDistanceUp; // Value injected by FXMLLoader

    @FXML // fx:id="bufferSetUp"
    private Button bufferSetUp; // Value injected by FXMLLoader

    @FXML // fx:id="activateBtn"
    private Button activateBtn; // Value injected by FXMLLoader

    @FXML // fx:id="suspendBtn"
    private Button suspendBtn; // Value injected by FXMLLoader

    @FXML // fx:id="statusBtn"
    private Button statusBtn; // Value injected by FXMLLoader

    @FXML // fx:id="bufferDistanceDown"
    private TextField bufferDistanceDown; // Value injected by FXMLLoader

    @FXML // fx:id="bufferSetDown"
    private Button bufferSetDown; // Value injected by FXMLLoader

    @FXML // fx:id="activateDownBtn"
    private Button activateDownBtn; // Value injected by FXMLLoader

    @FXML // fx:id="suspendDownBtn"
    private Button suspendDownBtn; // Value injected by FXMLLoader

    @FXML // fx:id="statusDownBtn"
    private Button statusDownBtn; // Value injected by FXMLLoader

    @FXML
    void activateDownBtnPressed(ActionEvent event) {

    }

    @FXML
    void activateUpBtnPressed(ActionEvent event) {

    }

    @FXML
    void bufferSetDownBtnPressed(ActionEvent event) {

    }

    @FXML
    void bufferSetUpBtnPressed(ActionEvent event) {

    }

    @FXML
    void guardedPositionBtnPressed(ActionEvent event) {

    }

    @FXML
    void ordersSentToBtnPressed(ActionEvent event) {

    }

    @FXML
    void statusDownBtnPressed(ActionEvent event) {

    }

    @FXML
    void statusUpBtnPressed(ActionEvent event) {

    }

    @FXML
    void suspendDownBtnPressed(ActionEvent event) {

    }

    @FXML
    void suspendUpBtnPressed(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert guardedPosition != null : "fx:id=\"guardedPosition\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert ordersSentTo != null : "fx:id=\"ordersSentTo\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferDistanceUp != null : "fx:id=\"bufferDistanceUp\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferSetUp != null : "fx:id=\"bufferSetUp\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert activateBtn != null : "fx:id=\"activateBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert suspendBtn != null : "fx:id=\"suspendBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert statusBtn != null : "fx:id=\"statusBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferDistanceDown != null : "fx:id=\"bufferDistanceDown\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert bufferSetDown != null : "fx:id=\"bufferSetDown\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert activateDownBtn != null : "fx:id=\"activateDownBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert suspendDownBtn != null : "fx:id=\"suspendDownBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";
        assert statusDownBtn != null : "fx:id=\"statusDownBtn\" was not injected: check your FXML file 'GuardAlgoPane1.fxml'.";

    }
}
