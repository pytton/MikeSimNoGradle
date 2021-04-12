package main.java.controllerandview;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.controllerandview.algocontrollerpanes.ControllerGuardAlgoPane1;
import main.java.controllerandview.windowcontrollers.*;
import main.java.model.MainModelThread;
import main.java.model.MikeSimLogger;
import main.java.model.priceserver.PriceServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * All windows created here.
 * At start of program, creates and displays the first window.
 * updateGUI has to be called by the main loop all the time to update windows with new data
 * from model
 */
public class MainGUIClass {

    public ControllerMainGUIWindow controllerPrimaryGUIWindow;

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

    public Stage getInitialStage() {
        return initialStage;
    }

    private Stage initialStage;

    //this stores windows which will be called by updateGUI method:
    private List<Updatable> updatableWindowsList = new ArrayList<>();

    //this stores all the PositionWindow controllers:
    public List<ControllerPositionsWindow> controllerPositionsWindowList = new ArrayList<>();

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
        this.initialStage = initialStage;
        //create PrimaryGUIWindow
        FXMLLoader primaryGUIWindowLoader = new FXMLLoader(getClass().getResource("/MainGUIWindow.fxml"));
        Parent primaryGUIRoot = primaryGUIWindowLoader.load();
        /*ControllerMainGUIWindow */ controllerPrimaryGUIWindow = (ControllerMainGUIWindow) primaryGUIWindowLoader.getController();
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
     * Creates and displays a new Positions window.
     * Default size is 640x1000 pixels
     * @param xPos
     * @param yPos
     */
    public ControllerPositionsWindow createPosWindow(int xPos, int yPos) {
        //create Positions Window:
        //we need to add custom MikeGridPane not defined in FXML:
        MikePositionsWindowCreator creator = null;

        try {
            creator = new MikePositionsWindowCreator(getMainModelThread().posOrdersManager.getPriceServer(defaultTickerId));
        } catch (IOException e) {
            MikeSimLogger.addLogEvent("Exception in createPosWindow\nPoswindow not created!");
            e.printStackTrace();
            return null;
        }

        //create the window:
        Stage stage = new Stage();
//        stage.setX(xPos);
//        stage.setY(yPos);
        stage.setScene(new Scene(creator.getPositionsWindowRoot()));
        //display the window:
        stage.show();

        //move to same monitor as Main GUI Window:
        CommonGUI.placeOnSameMonitor(getInitialStage(), stage, xPos,yPos);

        //name the window:
        String name = ("PositionsWindow " + updatableWindowsList.size());
        stage.setTitle(name);

        //resize it:
        stage.setHeight(1000);

        creator.getController().initialSetupAfterCreation( mainModelThread,
                getMainModelThread().posOrdersManager.getPriceServer(defaultTickerId),
                defaultTickerId );


        //add the controller to the list of controllers (for updateGUI):
        updatableWindowsList.add(creator.getController());
        controllerPositionsWindowList.add(creator.getController());

        return creator.getController();


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
            MikeSimLogger.addLogEvent("Exception in createAlgoManagerWindow");
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

        //move to same monitor as Main GUI Window:
        CommonGUI.placeOnSameMonitor(getInitialStage(), stage, 50,50);

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
            MikeSimLogger.addLogEvent("Exception in createPriceControlWindow");
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

        //move to same monitor as Main GUI Window:
        CommonGUI.placeOnSameMonitor(getInitialStage(), primaryStage, 300,0);

        //name the window:
        String name = ("Price Control " + updatableWindowsList.size());
        primaryStage.setTitle(name);
    }

    /**
     * Creates and displays a new GuardAlgoDown window
     */
    public void createGuardAlgoWindow(){
        FXMLLoader guardAlgoPanelLoader = new FXMLLoader(getClass().getResource("/PositionsWindow/algoControllers/GuardAlgoPane1.fxml"));
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
        stage.setTitle("GuardAlgoDown");
        stage.setScene(new Scene(guardAlgoPaneRoot));

        stage.show();

        //move to same monitor as Main GUI Window:
        CommonGUI.placeOnSameMonitor(getInitialStage(), stage, 50,50);

        updatableWindowsList.add(controllerGuardAlgoPane1);
    }

    public void createMultipleStepperAlgoWindow(){
        FXMLLoader standAloneAlgoWindowLoader = new FXMLLoader(getClass().getResource("/MultipleStepperAlgoWindow.fxml"));
        Parent paneRoot = null; //FXMLLoader.load(getClass().getResource("view/SceneBuilder/PriceControlPanel.fxml"));
        try {
            paneRoot = standAloneAlgoWindowLoader.load();
        } catch (IOException e) {
            MikeSimLogger.addLogEvent("Exception in createStandAloneAlgoWindow");
            e.printStackTrace();
            return;
        }
        MikeSimLogger.addLogEvent("createStandAloneAlgoWindow called");
        //get the controller class:
        ControllerMultipleStepperAlgoWindow controllerWindow = (ControllerMultipleStepperAlgoWindow) standAloneAlgoWindowLoader.getController();
        //todo: add the controller to the list of updatable controllers:

        //set the model:
//        controllerWindow.setModel(mainModelThread);

        //this adds a custom table of buttons to the scene
        MikeGridPane buttonTable = new MikeGridPane(30, 2, controllerWindow);

        ScrollPane sp = new ScrollPane();
        sp.setContent(buttonTable);
        sp.setFitToWidth(true);

        controllerWindow.getMainBorderPane().setLeft(sp);


        //create the window:
        Stage stage = new Stage();
        stage.setTitle("Multiple Stepper Algo Control");
        stage.setScene(new Scene(paneRoot));

        stage.setHeight(750);

        stage.show();


        //I want to display the newly created window on the same screen that the MainGUI is currently displayed at.
        //This does the magic:
        Rectangle2D bounds = CommonGUI.getScreenForStage(getInitialStage()).getVisualBounds();
        stage.setX(bounds.getMinX() +50);
        stage.setY(bounds.getMinY() +50);



//        updatableWindowsList.add(controllerWindow);

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
            loader = new FXMLLoader(getClass().getResource("/PositionsWindow/PositionsWindow.fxml"));
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


//            controller.initialSetupAfterCreation( mainModelThread,  priceServer, defaultTickerId );

//            controller.setModel(mainModelThread);
//
//
//            //setup the initial traded instrument and posOrders this window refers to:
//            //set the default instrument
//            controller.setInstrumentList(mainModelThread.posOrdersManager.getPriceServerObservableList());
//
//            controller.setMikePosOrders(mainModelThread.posOrdersManager.getMikePosOrders(defaultTickerId, 0));
//
//            //populate the ListView that allows choosing PosOrders
////            controller.positionsList.setItems(mainModelThread.posOrdersManager.getPosOrdersObservableList(defaultTickerId));
//
//            controller.setPositionsList(mainModelThread.posOrdersManager.getPosOrdersObservableList(defaultTickerId));
//
//            //set the initial priceServer(this can later be changed by user in the window)
//            controller.setPriceServer(priceServer);
//
//            //set the initial instrument, posOrders and targetPosOrders:

        }



        public Parent getPositionsWindowRoot() {
            return positionsWindowRoot;
        }

        public ControllerPositionsWindow getController() {
            return controller;
        }
    }
}