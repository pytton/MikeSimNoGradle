package main.java.controllerandview;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controllerandview.mainGUIwindow.controller.ControllerMainGUIWindow;
import main.java.controllerandview.positionswindow.controller.ControllerConsolidatedPositionsWindow;
import main.java.controllerandview.positionswindow.view.MikePositionsWindowCreator;
import main.java.controllerandview.pricecontrolwindow.controller.ControllerPriceControlPanel;
import main.java.model.MainModelThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainGUIClass {



    //Windows can choose which instrument they refer to. this sets the default instrument
    int defaultTickerId = 0;
    private long count =0;
    public MainModelThread mainModelThread;

    private List<ControllerPriceControlPanel> priceControlPanelControllerList = new ArrayList<>();
    private List<ControllerConsolidatedPositionsWindow> posWindowControllerList = new ArrayList<>();

    //called by Mainloop. Updates all GUI windows
    public void updateGUI(){

        for(ControllerConsolidatedPositionsWindow controller :posWindowControllerList){
            if (controller != null) {
                controller.updateGUI();
            }
        }

        for(ControllerPriceControlPanel controller :priceControlPanelControllerList){
            if (controller != null) {
                controller.updateGUI();
            }
        }

//        try {
//            //below for testing only:
//            ControllerPositionsWindow posWindowController = null;
//            if (!posWindowControllerList.isEmpty()) {
//                posWindowController = posWindowControllerList.get(0);
//            }
//            if (posWindowController != null) {
//                for (int i = 0; i < 20; i++) {
//                    posWindowController.setSpecificButtonInMikeGridPane(i, 0, "" + ((Math.sqrt((count * 79 + count))) * 1) % 567);
//                    posWindowController.setSpecificButtonInMikeGridPane(i, 1, "" + count);
//                    posWindowController.setSpecificButtonInMikeGridPane(i, 2, "" + Math.sqrt((count * 79 + i + count)) % 7);
//                    posWindowController.setSpecificButtonInMikeGridPane(i, 3, "" + Math.sqrt((count * 79 + i + count)) % 56);
//                    posWindowController.setSpecificButtonInMikeGridPane(i, 4, "" + Math.sqrt((count * 73 + i + count)) % 74);
//                    posWindowController.setSpecificButtonInMikeGridPane(i, 5, "" + Math.sqrt((count * 987 + i + count)) % 34);
//                    posWindowController.setSpecificButtonInMikeGridPane(i, 6, "" + Math.sqrt((count * 453 + i + count)) % 9);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        count++;
    }

    public void initializeGUI(Stage initialStage, /*PriceServer priceServer,*/ MainModelThread mainModelThread) throws Exception{


        this.mainModelThread = mainModelThread;

        //create PrimaryGUIWindow

        FXMLLoader primaryGUIWindowLoader = new FXMLLoader(getClass().getResource("/MainGUIWindow.fxml"));
        Parent primaryGUIRoot = primaryGUIWindowLoader.load();
        ControllerMainGUIWindow primaryGUIWindow = (ControllerMainGUIWindow) primaryGUIWindowLoader.getController();
        primaryGUIWindow.setMainGUIClass(this);
        primaryGUIWindow.setModel(mainModelThread);
        primaryGUIWindow.instrumentsList.setItems(mainModelThread.posOrdersManager.getPriceServerObservableList());

        initialStage.setScene(new Scene(primaryGUIRoot));
        initialStage.setTitle("MikeSimulator prototype 0.1");

        initialStage.show();


    }

    public void createPosWindow(){
        //create Positions Window:
        //we need to add custom MikeGridPane not defined in FXML:
        MikePositionsWindowCreator posWindow = null;

        try {
            posWindow = new MikePositionsWindowCreator(getMainModelThread().posOrdersManager.getPriceServer(defaultTickerId));
        } catch (IOException e) {
            System.out.println("Exception in createPosWindow");
            e.printStackTrace();
        }
        ControllerConsolidatedPositionsWindow posWindowController = posWindow.getPositionsWindowController();
        posWindowController.setModel(mainModelThread);

        //set the default instrument
        posWindowController.setInstrumentList(mainModelThread.posOrdersManager.getPriceServerObservableList());
//        posWindowController.instrumentsList.setItems(mainModelThread.posOrdersManager.getPriceServer(defaultTickerId));

        //todo: currently only defaultTickerId = 0 . chenge this later
        posWindowController.setMikePosOrders(mainModelThread.posOrdersManager.getMikePosOrders(defaultTickerId, 0));

        //add the controller to the list of controllers (for updateGUI):
        posWindowControllerList.add(posWindowController);

        //populate the ListView that allows choosing PosOrders
        posWindowController.positionsList.setItems(mainModelThread.posOrdersManager.getPosOrdersObservableList(defaultTickerId));

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

    public void createConsolidatedPosWindow(){
        //create Positions Window:
        //we need to add custom MikeGridPane not defined in FXML:
        MikePositionsWindowCreator consolidatedPosWindow = null;

        try {
            consolidatedPosWindow = new MikePositionsWindowCreator(getMainModelThread().posOrdersManager.getPriceServer(defaultTickerId));
        } catch (IOException e) {
            System.out.println("Exception in createPosWindow");
            e.printStackTrace();
        }
        ControllerConsolidatedPositionsWindow posWindowController = consolidatedPosWindow.getPositionsWindowController();
        posWindowController.setModel(mainModelThread);

        //set the default instrument
        posWindowController.setInstrumentList(mainModelThread.posOrdersManager.getPriceServerObservableList());
//        posWindowController.instrumentsList.setItems(mainModelThread.posOrdersManager.getPriceServer(defaultTickerId));

        posWindowController.setMikePosOrders(mainModelThread.posOrdersManager.getMikePosOrders(defaultTickerId, 0));

        //add the controller to the list of controllers (for updateGUI):
        posWindowControllerList.add(posWindowController);

        //populate the ListView that allows choosing PosOrders
        posWindowController.positionsList.setItems(mainModelThread.posOrdersManager.getPosOrdersObservableList(defaultTickerId));

        //create the window:
        Stage secondStage = new Stage();
        secondStage.setX(0);
        secondStage.setY(0);
        secondStage.setScene(new Scene(consolidatedPosWindow.getPositionsWindowRoot()));
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
        //set the priceserver chosen at the beginning:
        priceControlPanel.setPriceServer(getMainModelThread().posOrdersManager.getPriceServer(defaultTickerId));
        //adding list of priceservers:
        priceControlPanel.setInstrumentList(mainModelThread.posOrdersManager.getPriceServerObservableList());
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