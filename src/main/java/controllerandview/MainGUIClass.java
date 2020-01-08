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

    //this used so MainModelThread can periodically send signals to windows to update their data from the model
    public interface Updatable {
        public void updateGUI();
    }


    //Windows can choose which instrument they refer to. this sets the default instrument set at startup
    int defaultTickerId = 0;
    private long count = 0;
    public MainModelThread mainModelThread;

    private List<Updatable> updatableWindowsList = new ArrayList<>();

    //   private List<ControllerPriceControlPanel> priceControlPanelControllerList = new ArrayList<>();
    //   private List<ControllerPositionsWindow> posWindowControllerList = new ArrayList<>();

    //called by Mainloop. Updates all GUI windows
    public void updateGUI() {

        for (Updatable controller : updatableWindowsList) {
            if (controller != null) {
                controller.updateGUI();
            }
        }

//        for(ControllerPositionsWindow controller :posWindowControllerList){
//            if (controller != null) {
//                controller.updateGUI();
//            }
//        }
//
//        for(ControllerPriceControlPanel controller :priceControlPanelControllerList){
//            if (controller != null) {
//                controller.updateGUI();
//            }
//        }

        count++;
    }

    public void initializeGUI(Stage initialStage, /*PriceServer priceServer,*/ MainModelThread mainModelThread) throws Exception {


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

    public void createPosWindow() {
        //create Positions Window:
        //we need to add custom MikeGridPane not defined in FXML:
        MikePositionsWindowCreator creator = null;

        try {
            creator = new MikePositionsWindowCreator(getMainModelThread().posOrdersManager.getPriceServer(defaultTickerId));
        } catch (IOException e) {
            System.out.println("Exception in createPosWindow");
            e.printStackTrace();
        }

        //add the controller to the list of controllers (for updateGUI):
        updatableWindowsList.add(creator.getController());

        //create the window:
        Stage stage = new Stage();
        stage.setX(0);
        stage.setY(0);
        stage.setScene(new Scene(creator.getPositionsWindowRoot()));
        //display the window:
        stage.show();

        //name the window:
        String name = ("PositionsWindow " + updatableWindowsList.size());
        stage.setTitle(name);
    }

    public void createConsolidatedPosWindow() {
//        //TODO: WORK IN PROGRESS:
//        //create Positions Window:
//        //we need to add custom MikeGridPane not defined in FXML:
//        ConsolidatedPositionsWindowCreator creator = null;
//
//        try {
//            creator = new ConsolidatedPositionsWindowCreator(getMainModelThread().posOrdersManager.getPriceServer(defaultTickerId));
//        } catch (IOException e) {
//            System.out.println("Exception in createPosWindow");
//            e.printStackTrace();
//        }
//
//        //add the controller to the list of controllers (for updateGUI):
//        updatableWindowsList.add(creator.getController());
//
//        //create the window:
//        Stage stage = new Stage();
//        stage.setX(0);
//        stage.setY(0);
//        stage.setScene(new Scene(creator.getPositionsWindowRoot()));
//        //display the window:
//        stage.show();
//
//        //name the window:
//        String name = ("ConsolidatedPositions " + updatableWindowsList.size());
//        stage.setTitle(name);
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

    public void createPriceControlWindow() {
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
        updatableWindowsList.add(priceControlPanel);
        //create the window:
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Price Control");
        primaryStage.setScene(new Scene(pricePanelRoot));
        primaryStage.setX(850);
        primaryStage.setY(0);
        //display the window:
        primaryStage.show();

        //name the window:
        String name = ("Price Control " + updatableWindowsList.size());
        primaryStage.setTitle(name);
    }

    public MainModelThread getMainModelThread() {
        return mainModelThread;
    }

    //Used to create and setup MikePositionsWindow.
    class MikePositionsWindowCreator {

        private FXMLLoader loader;// = new FXMLLoader(getClass().getResource("PositionsWindow.fxml"));
        private Parent positionsWindowRoot;
        public ControllerPositionsWindow controller;
        private MikeGridPane buttonTable;

        public MikePositionsWindowCreator(PriceServer priceServer) throws IOException {
            //load FXML file
            loader = new FXMLLoader(getClass().getResource("/PositionsWindow.fxml"));
//            loader = new FXMLLoader(getClass().getResource("/ConsolidatedPositionsWindow.fxml"));
            //this needed by JavaFX Scene constructor:
            positionsWindowRoot = loader.load(); //this might throw IOException
            //this is used to access elements of MikePositionsWindowCreator:
            controller = (ControllerPositionsWindow) loader.getController();
            //this adds a custom table of buttons to the scene
            buttonTable = new MikeGridPane(100, 7, controller);

            VBox topVbox = new VBox();
            MikeGridPane topGridPane = new MikeGridPane(3, 7, new MikeGridPane.EmptyMikeButtonHandler());
            MikeGridPane bottomGridPane = new MikeGridPane(1, 7, new MikeGridPane.EmptyMikeButtonHandler());

            topGridPane.setPadding(new Insets(0, 15, 0, 0));
            bottomGridPane.setPadding(new Insets(0, 15, 0, 0));
            topVbox.getChildren().add(topGridPane);

            ScrollPane sp = new ScrollPane();
            sp.setContent(buttonTable);
            sp.setFitToWidth(true);
            topVbox.getChildren().add(sp);
            topVbox.getChildren().add(bottomGridPane);

            controller.getMainBorderPane().setLeft(topVbox);

            controller.setMikeGridPane(buttonTable);
            controller.topMikeGridPane = topGridPane;
            controller.bottomMikeGridPane = bottomGridPane;

            controller.setModel(mainModelThread);


            //setup the initial traded instrument and posOrders this window refers to:
            //set the default instrument
            controller.setInstrumentList(mainModelThread.posOrdersManager.getPriceServerObservableList());

            controller.setMikePosOrders(mainModelThread.posOrdersManager.getMikePosOrders(defaultTickerId, 0));

            //populate the ListView that allows choosing PosOrders
            controller.positionsList.setItems(mainModelThread.posOrdersManager.getPosOrdersObservableList(defaultTickerId));

            //set the initial priceServer(this can later be changed by user in the window)
            controller.setPriceServer(priceServer);
        }


        public Parent getPositionsWindowRoot() {
            return positionsWindowRoot;
        }

        public ControllerPositionsWindow getController() {
            return controller;
        }
    }
}



    //Used to create and setup MikePositionsWindow.
//    class ConsolidatedPositionsWindowCreator {

//        private FXMLLoader loader;// = new FXMLLoader(getClass().getResource("PositionsWindow.fxml"));
//        private Parent positionsWindowRoot;
//        public ControllerPositionsWindow controller;
//        private MikeGridPane buttonTable;
//
//        public ConsolidatedPositionsWindowCreator(PriceServer priceServer) throws IOException {
//            //load FXML file
////            loader = new FXMLLoader(getClass().getResource("/PositionsWindow.fxml"));
//            loader = new FXMLLoader(getClass().getResource("/ConsolidatedPositionsWindow.fxml"));
//            //this needed by JavaFX Scene constructor:
//            positionsWindowRoot = loader.load(); //this might throw IOException
//            //this is used to access elements of MikePositionsWindowCreator:
//            controller = (ControllerPositionsWindow) loader.getController();
//            //this adds a custom table of buttons to the scene
//            buttonTable = new MikeGridPane(100,7, controller);
//
//            VBox topVbox = new VBox();
//            MikeGridPane topGridPane = new MikeGridPane(1,7, new MikeGridPane.EmptyMikeButtonHandler());
//            MikeGridPane bottomGridPane = new MikeGridPane(1, 7, new MikeGridPane.EmptyMikeButtonHandler());
//
//            topGridPane.setPadding( new Insets(0, 15, 0, 0));
//            bottomGridPane.setPadding( new Insets(0, 15, 0, 0));
//            topVbox.getChildren().add(topGridPane);
//
//            ScrollPane sp = new ScrollPane();
//            sp.setContent(buttonTable);
//            sp.setFitToWidth(true);
//            topVbox.getChildren().add(sp);
//            topVbox.getChildren().add(bottomGridPane);
//
//            controller.getMainBorderPane().setLeft(topVbox);
//
//            controller.setMikeGridPane(buttonTable);
//
//            controller.setModel(mainModelThread);
//
//            //set the default instrument
//            controller.setInstrumentList(mainModelThread.posOrdersManager.getPriceServerObservableList());
//
//            controller.setMikePosOrders(mainModelThread.posOrdersManager.getMikePosOrders(defaultTickerId, 0));
//
//            //populate the ListView that allows choosing PosOrders
//            controller.positionsList.setItems(mainModelThread.posOrdersManager.getPosOrdersObservableList(defaultTickerId));
//
//            //set the initial priceServer(this can later be changed by user in the window)
//            controller.setPriceServer(priceServer);
//        }
//
//        public Parent getPositionsWindowRoot() {
//            return positionsWindowRoot;
//        }
//
//        public ControllerPositionsWindow getController() {
//            return controller;
//        }
//    }


//}