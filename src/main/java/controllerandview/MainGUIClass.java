package main.java.controllerandview;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controllerandview.mainGUIwindow.controller.ControllerPrimaryGUIWindow;
import main.java.controllerandview.positionswindow.controller.ControllerPositionsWindow;
import main.java.controllerandview.positionswindow.view.MikePositionsWindowCreator;
import main.java.controllerandview.pricecontrolwindow.controller.ControllerPriceControlPanel;
import main.java.model.MainModelThread;
import main.java.model.priceserver.PriceServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainGUIClass {

    private long count =0;
    public MainModelThread mainModelThread;

    private List<ControllerPriceControlPanel> priceControlPanelControllerList = new ArrayList<>();
    private List<ControllerPositionsWindow> posWindowControllerList = new ArrayList<>();

    //called by Mainloop. Updates all GUI windows
    public void updateGUI(){

        for(ControllerPositionsWindow controller :posWindowControllerList){
            if (controller != null) {
                controller.updateGUI();
            }
        }

        for(ControllerPriceControlPanel controller :priceControlPanelControllerList){
            if (controller != null) {
                controller.updateGUI();
            }
        }

        try {
            //below for testing only:
            ControllerPositionsWindow posWindowController = posWindowControllerList.get(0);
            for (int i = 0; i < 20; i++) {
                posWindowController.setSpecificButtonInMikeGridPane(i, 0, "" + ((Math.sqrt((count * 79 + count))) * 1) % 567);
                posWindowController.setSpecificButtonInMikeGridPane(i, 1, "" + count);
                posWindowController.setSpecificButtonInMikeGridPane(i, 2, "" + Math.sqrt((count * 79 + i + count)) % 7);
                posWindowController.setSpecificButtonInMikeGridPane(i, 3, "" + Math.sqrt((count * 79 + i + count)) % 56);
                posWindowController.setSpecificButtonInMikeGridPane(i, 4, "" + Math.sqrt((count * 73 + i + count)) % 74);
                posWindowController.setSpecificButtonInMikeGridPane(i, 5, "" + Math.sqrt((count * 987 + i + count)) % 34);
                posWindowController.setSpecificButtonInMikeGridPane(i, 6, "" + Math.sqrt((count * 453 + i + count)) % 9);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        count++;
    }

    public void initializeGUI(Stage initialStage, /*PriceServer priceServer,*/ MainModelThread mainModelThread) throws Exception{


        this.mainModelThread = mainModelThread;

        //create PrimaryGUIWindow

        FXMLLoader primaryGUIWindowLoader = new FXMLLoader(getClass().getResource("/MainGUIWindow.fxml"));
        Parent primaryGUIRoot = primaryGUIWindowLoader.load();
        ControllerPrimaryGUIWindow primaryGUIWindow = (ControllerPrimaryGUIWindow) primaryGUIWindowLoader.getController();
        primaryGUIWindow.setMainGUIClass(this);

        initialStage.setScene(new Scene(primaryGUIRoot));
        initialStage.setTitle("MikeSimulator prototype 0.1");

        initialStage.show();

    }

    public void createPosWindow(){
        //create Positions Window:
        //we need to add custom MikeGridPane not defined in FXML:
        MikePositionsWindowCreator posWindow = null;
        try {
            posWindow = new MikePositionsWindowCreator(getMainModelThread().getPriceServer());
        } catch (IOException e) {
            System.out.println("Exception in createPosWindow");
            e.printStackTrace();
        }
        ControllerPositionsWindow posWindowController = posWindow.getPositionsWindowController();
        posWindowController.setModel(mainModelThread);

        //add the controller to the list of controllers:
        posWindowControllerList.add(posWindowController);



        //create the window:
        Stage secondStage = new Stage();
        secondStage.setX(0);
        secondStage.setY(0);
        secondStage.setScene(new Scene(posWindow.getPositionsWindowRoot()));
        //display the window:
        secondStage.show();

        //name the window:
        String name = ("PositionsWindow " + posWindowControllerList.size());
        secondStage.setTitle(name);


    }

    public void createPriceControlWindow(){
        //create Price Control window:
        FXMLLoader priceControlPanelLoader = new FXMLLoader(getClass().getResource("/PriceControlPanel.fxml"));
        Parent pricePanelRoot = null; //FXMLLoader.load(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));
        try {
            pricePanelRoot = priceControlPanelLoader.load();
        } catch (IOException e) {
            System.out.println("Exception in createPriceControlWindow");
            e.printStackTrace();
        }

        //get the controller class:
        ControllerPriceControlPanel priceControlPanel = (ControllerPriceControlPanel) priceControlPanelLoader.getController();
        priceControlPanel.setPriceServer(getMainModelThread().getPriceServer());

        //add the controller to the list of controllers:
        priceControlPanelControllerList.add(priceControlPanel);

        //create the window:
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Price Control");
        primaryStage.setScene(new Scene(pricePanelRoot));
        primaryStage.setX(850);
        primaryStage.setY(0);
        //display the window:
        primaryStage.show();

        //name the window:
        String name = ("Price Control " + priceControlPanelControllerList.size());
        primaryStage.setTitle(name);

    }


    public MainModelThread getMainModelThread() {
        return mainModelThread;
    }
}