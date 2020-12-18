package main.java.controllerandview;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.controllerandview.algocontrollers.ControllerGuardAlgoPane1;
import main.java.controllerandview.windowcontrollers.ControllerAlgoManagerPanel;
import main.java.controllerandview.windowcontrollers.ControllerMainGUIWindow;
import main.java.controllerandview.windowcontrollers.ControllerPositionsWindow;
import main.java.controllerandview.windowcontrollers.ControllerPriceControlWindow;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.priceserver.PriceServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * All windows created here.
 * At start of program, creates and displays the first window.
 * updateGUI has to be called by the main loop all the time to update windows with new data
 * from model
 */
public class MainGUIClass {

    //this used so MainModelThread can periodically send signals to windows to update their data from the model
    public interface Updatable {
        /**
         * called by Mainloop. Updates all GUI windows
         */
        public void updateGUI();
    }


    //Windows can choose which instrument they refer to. this sets the default instrument set at startup
    int defaultTickerId = 0;
//    private long count = 0;
    private MainModelThread mainModelThread;

    //this stores windows which will be called by updateGUI method:
    private List<Updatable> updatableWindowsList = new ArrayList<>();

    //   private List<ControllerPriceControlWindow> priceControlPanelControllerList = new ArrayList<>();
    //   private List<ControllerPositionsWindow> posWindowControllerList = new ArrayList<>();


    /**
     * called by Mainloop. Updates all GUI windows
     */
    public void updateGUI() {

        for (Updatable controller : updatableWindowsList) {
            if (controller != null) {
                controller.updateGUI();
            }
        }

//        count++;
    }

    /**
     * This called at start of program to set things up and create and
     * display the first GUI window
     * @param initialStage
     * @param mainModelThread
     * @throws Exception
     */
    public void initializeGUI(Stage initialStage, /*PriceServer priceServer,*/ MainModelThread mainModelThread) throws Exception {
        this.mainModelThread = mainModelThread;
        //create PrimaryGUIWindow
        FXMLLoader primaryGUIWindowLoader = new FXMLLoader(getClass().getResource("/MainGUIWindow.fxml"));
        Parent primaryGUIRoot = primaryGUIWindowLoader.load();
        ControllerMainGUIWindow controllerPrimaryGUIWindow = (ControllerMainGUIWindow) primaryGUIWindowLoader.getController();
        controllerPrimaryGUIWindow.setMainGUIClass(this);
        controllerPrimaryGUIWindow.setModel(mainModelThread);
        controllerPrimaryGUIWindow.instrumentsList.setItems(mainModelThread.posOrdersManager.getPriceServerObservableList());

        initialStage.setScene(new Scene(primaryGUIRoot));
        initialStage.setTitle("MikeSimulator prototype 0.1");

        //display the initial window:
        initialStage.show();

    }

    public MainModelThread getMainModelThread() {
        return mainModelThread;
    }

    /**
     * Creates and displays a new Positions window
     */
    public void createPosWindow() {
        //create Positions Window:
        //we need to add custom MikeGridPane not defined in FXML:
        MikePositionsWindowCreator creator = null;

        try {
            creator = new MikePositionsWindowCreator(getMainModelThread().posOrdersManager.getPriceServer(defaultTickerId));
        } catch (IOException e) {
            System.out.println("Exception in createPosWindow\nPoswindow not created!");
            e.printStackTrace();
            return;
        }

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

        //add the controller to the list of controllers (for updateGUI):
        updatableWindowsList.add(creator.getController());

    }

    /**
     * Creates and displays a new AlgoManager window
     */
    public void createAlgoManagerWindow() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AlgoManagerPanel.fxml"));
        Parent root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            System.out.println("Exception in createAlgoManagerWindow");
            e.printStackTrace();
        }

        ControllerAlgoManagerPanel controller = (ControllerAlgoManagerPanel) loader.getController();

        controller.setModel(mainModelThread);
        controller.setAlgoList(mainModelThread.algoManager.getAlgoSet());

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setX(0);
        stage.setY(0);
        stage.show();

    }

    /**
     * Creates and displays a new Price Control window
     */
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
        ControllerPriceControlWindow controllerPriceControlPanel = (ControllerPriceControlWindow) priceControlPanelLoader.getController();
        //set the priceserver chosen at the beginning:
        controllerPriceControlPanel.setPriceServer(getMainModelThread().posOrdersManager.getPriceServer(defaultTickerId));
        //adding list of priceservers:
        controllerPriceControlPanel.setInstrumentList(mainModelThread.posOrdersManager.getPriceServerObservableList());
        //add the controller to the list of controllers:
        updatableWindowsList.add(controllerPriceControlPanel);
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

    /**
     * Creates and displays a new GuardAlgo window
     */
    public void createGuardAlgoWindow(){
        FXMLLoader guardAlgoPanelLoader = new FXMLLoader(getClass().getResource("/algoControllers/GuardAlgoPane1.fxml"));
        Parent guardAlgoPaneRoot = null; //FXMLLoader.load(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));
        try {
            guardAlgoPaneRoot = guardAlgoPanelLoader.load();
        } catch (IOException e) {
            MikeSimLogger.addLogEvent("Exception in createGuardAlgoWindow");
            e.printStackTrace();
        }
        MikeSimLogger.addLogEvent("createGuardAlgoWindow called");
        //get the controller class:
        ControllerGuardAlgoPane1 controllerGuardAlgoPane1 = (ControllerGuardAlgoPane1) guardAlgoPanelLoader.getController();
        //todo: add the controller to the list of updatable controllers:

        //set the model:
        controllerGuardAlgoPane1.setModel(mainModelThread);

        //create the window:
        Stage stage = new Stage();
        stage.setTitle("GuardAlgo");
        stage.setScene(new Scene(guardAlgoPaneRoot));

        stage.show();

        updatableWindowsList.add(controllerGuardAlgoPane1);
    }

    /**
     * Used to create and setup MikePositionsWindow.
     */
    private class MikePositionsWindowCreator {

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
            controller.setSp(sp); //enables the scrollpane of buttonTable to be accessed from the controller
            controller.topMikeGridPane = topGridPane;
            controller.bottomMikeGridPane = bottomGridPane;

            controller.setModel(mainModelThread);


            //setup the initial traded instrument and posOrders this window refers to:
            //set the default instrument
            controller.setInstrumentList(mainModelThread.posOrdersManager.getPriceServerObservableList());

            controller.setMikePosOrders(mainModelThread.posOrdersManager.getMikePosOrders(defaultTickerId, 0));

            //populate the ListView that allows choosing PosOrders
//            controller.positionsList.setItems(mainModelThread.posOrdersManager.getPosOrdersObservableList(defaultTickerId));

            controller.setPositionsList(mainModelThread.posOrdersManager.getPosOrdersObservableList(defaultTickerId));

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