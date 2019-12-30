package main.java.controllerandview;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.controllerandview.mainGUIwindow.controller.ControllerMainGUIWindow;
import main.java.controllerandview.positionswindow.controller.ControllerPositionsWindow;
import main.java.controllerandview.pricecontrolwindow.controller.ControllerPriceControlPanel;
import main.java.model.MainModelThread;
import main.java.model.priceserver.PriceServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainGUIClass {



    //Windows can choose which instrument they refer to. this sets the default instrument
    int defaultTickerId = 0;
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
        ControllerPositionsWindow posWindowController = posWindow.getPositionsWindowController();
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

//    public void createConsolidatedPosWindow(){
//        //create Positions Window:
//        //we need to add custom MikeGridPane not defined in FXML:
//        MikePositionsWindowCreator consolidatedPosWindow = null;
//
//        try {
//            consolidatedPosWindow = new MikePositionsWindowCreator(getMainModelThread().posOrdersManager.getPriceServer(defaultTickerId));
//        } catch (IOException e) {
//            System.out.println("Exception in createPosWindow");
//            e.printStackTrace();
//        }
//        ControllerPositionsWindow posWindowController = consolidatedPosWindow.getPositionsWindowController();
//        posWindowController.setModel(mainModelThread);
//
//        //set the default instrument
//        posWindowController.setInstrumentList(mainModelThread.posOrdersManager.getPriceServerObservableList());
////        posWindowController.instrumentsList.setItems(mainModelThread.posOrdersManager.getPriceServer(defaultTickerId));
//
//        posWindowController.setMikePosOrders(mainModelThread.posOrdersManager.getMikePosOrders(defaultTickerId, 0));
//
//        //add the controller to the list of controllers (for updateGUI):
//        posWindowControllerList.add(posWindowController);
//
//        //populate the ListView that allows choosing PosOrders
//        posWindowController.positionsList.setItems(mainModelThread.posOrdersManager.getPosOrdersObservableList(defaultTickerId));
//
//        //create the window:
//        Stage secondStage = new Stage();
//        secondStage.setX(0);
//        secondStage.setY(0);
//        secondStage.setScene(new Scene(consolidatedPosWindow.getPositionsWindowRoot()));
//        //display the window:
//        secondStage.show();
//
//        //name the window:
//        String name = ("PositionsWindow " + posWindowControllerList.size());
//        secondStage.setTitle(name);
//    }

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

    public static class MikePositionsWindowCreator {

        private FXMLLoader posWindowLoader;// = new FXMLLoader(getClass().getResource("PositionsWindow.fxml"));
        private Parent positionsWindowRoot;
        public ControllerPositionsWindow positionsWindowController;
        private MikeGridPane buttonTable;


        public MikePositionsWindowCreator() throws IOException {
            //load FXML file

            // ../../../../resources

            posWindowLoader = new FXMLLoader(getClass().getResource("/PositionsWindow.fxml"));
            //this needed by JavaFX Scene constructor:
            positionsWindowRoot = posWindowLoader.load(); //this might throw IOException
            //this is used to access elements of MikePositionsWindowCreator:
            positionsWindowController = (ControllerPositionsWindow)posWindowLoader.getController();
            //this adds a custom table of buttons to the scene
            buttonTable = new MikeGridPane(100,7, positionsWindowController);




            //todo: check this works:
            //experimenting with modifing this window:

            VBox topVbox = new VBox();
            MikeGridPane topGridPane = new MikeGridPane(1,7, new MikeGridPane.EmptyMikeButtonHandler());
            MikeGridPane bottomGridPane = new MikeGridPane(1, 7, new MikeGridPane.EmptyMikeButtonHandler());


            topGridPane.setPadding( new Insets(0, 15, 0, 0));
            bottomGridPane.setPadding( new Insets(0, 15, 0, 0));
            topVbox.getChildren().add(topGridPane);

            ScrollPane sp = new ScrollPane();
            sp.setContent(buttonTable);
            sp.setFitToWidth(true);
    //        sp.setMaxWidth(450);
            topVbox.getChildren().add(sp);
            topVbox.getChildren().add(bottomGridPane);

            //todo: this changed. works?
            positionsWindowController.getMainBorderPane().setLeft(topVbox);
    //        positionsWindowController.getMainBorderPane().setMinWidth(850);

    //        positionsWindowController.getMainBorderPane().setCenter(sp);
            positionsWindowController.setMikeGridPane(buttonTable);

    //        Button button = buttonTable.getButton(3,1);
    //
    //        button.setText("Hello!");
    //        button.setStyle("-fx-background-color: red");
    //
    //        buttonTable.getButton(3,2).setText("");
    //        buttonTable.getButton(3,3).setStyle("-fx-text-fill: green");
    //        buttonTable.getButton(4,0).setStyle("-fx-background-color: blue");
    //        buttonTable.getButton(4,1).setStyle("-fx-background-color: grey;-fx-border-color: black; -fx-border-width: 1px");
    //        buttonTable.getButton(5,1).setStyle("-fx-background-color: blue;-fx-border-color: black; -fx-border-width: 1px");
    //        buttonTable.getButton(5,1).setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        }

        public MikePositionsWindowCreator(PriceServer priceServer) throws IOException{
            this();
            positionsWindowController.setPriceServer(priceServer);
        }

        public Parent getPositionsWindowRoot() {
            return positionsWindowRoot;
        }

        public ControllerPositionsWindow getPositionsWindowController() {
            return positionsWindowController;
        }
    }
}