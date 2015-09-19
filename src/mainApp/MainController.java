package mainApp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

/*************************************************************************
 * The Controller for the Main Panel which contains the main
 * toolbar/menu and handles navigation between the programs screens
 ************************************************************************/
public class MainController {

    // CONSTANTS
    public static final int BRUTE_FORCE = 0;
    public static final int BOYER_MOORE = 1;
    public static final int KNUTH_MORRIS_PRATT = 2;
    public static final int QUICK_SEARCH = 3;
    public static final int NOT_SO_NAIVE = 5;
    public static final int SMITH = 6;
    public static final int TWO_WAY = 4;
    public static final int HORSPOOL = 7;


    // Holder of a switchable screen.
    @FXML private StackPane screenHolder;
    @FXML private Button exitBtn;
    @FXML private MenuItem bruteForceBtn, kmpAlgoBtn, boyerMAlgoBtn,
            quickSAlgoBtn, notSoNaiveAlgoBtn, smithAlgoBtn, twoWayAlgoBtn, horspoolAlgoBtn;
    @FXML private MenuButton algoButton;
    @FXML private Label currentAlgoLabel;
    public static int algoChoice = 0;

//    /*****************************************************************************
//     * Method that runs as soon as the view loads
//     ****************************************************************************/
//    @Override public void initialize(URL location, ResourceBundle resources) {
//
////        EventHandler<ActionEvent> eventHandler = handleMenuItem();
//
////        int keyCode = 1;
////        for (MenuItem menuItem : algoButton.getItems()){
////            menuItem.setOnAction(eventHandler);
////            menuItem.setAccelerator(new KeyCodeCombination(KeyCode.getKeyCode(keyCode + "")));
////            keyCode++;
////        }
//    }

    /*****************************************************************************
     * Replaces the screen displayed in the screen holder with a new screen.
     * @param node the screen node to be swapped in.
     ****************************************************************************/
    public void setScreen(Node node) {
        screenHolder.getChildren().setAll(node);
    }

    /************************************************************************************************
     *
     *                  Methods to handle MenuBar buttons (Home, Algorithms, Exit)
     *
    /************************************************************************************************
     * Handle the Home button - Loads the home screen
     **********************************************************/
    @FXML private void handleHomeBtn() {
        currentAlgoLabel.setText("");
        ScreenNavigator.loadScreen(ScreenNavigator.HOME_SCREEN);
    }

    /***********************************************************
     * Handle the Brute Force MenuItem
     **********************************************************/
    @FXML private EventHandler handleMenuItem(){
        return new EventHandler<ActionEvent>(){

            public void handle(ActionEvent event){
                // Set the algoChoice variable which is used to decide
                // which data/animation should be loaded
                if (event.getSource().equals(bruteForceBtn)){
                    algoChoice = BRUTE_FORCE;
                    currentAlgoLabel.setText("Brute-Force");
                } else if (event.getSource().equals(boyerMAlgoBtn)){
                    algoChoice = BOYER_MOORE;
                    currentAlgoLabel.setText("Boyer-Moore");
                }else if (event.getSource().equals(kmpAlgoBtn)){
                    algoChoice = KNUTH_MORRIS_PRATT;
                    currentAlgoLabel.setText("Knuth-Morris-Pratt");
                }else if (event.getSource().equals(quickSAlgoBtn)){
                    algoChoice = QUICK_SEARCH;
                    currentAlgoLabel.setText("Quick-Search");
                }else if (event.getSource().equals(horspoolAlgoBtn)){
                    algoChoice = HORSPOOL;
                    currentAlgoLabel.setText("Horspool");
                }else if (event.getSource().equals(twoWayAlgoBtn)){
                    algoChoice = TWO_WAY;
                    currentAlgoLabel.setText("Two-Way");
                }else if (event.getSource().equals(notSoNaiveAlgoBtn)){
                    algoChoice = NOT_SO_NAIVE;
                    currentAlgoLabel.setText("Not-So-Naive");
                }else if (event.getSource().equals(smithAlgoBtn)){
                    algoChoice = SMITH;
                    currentAlgoLabel.setText("Smith");
                }
                // Load the Algorithms Screen
                ScreenNavigator.loadScreen(ScreenNavigator.ALGO_SCREEN);
            }
        };
    }

    /**********************************************************
     * Handle the BF button - Loads the algo screen
     **********************************************************/
    @FXML private void handleBFBtn() {
        algoChoice = BRUTE_FORCE;
        currentAlgoLabel.setText("Brute-Force");
        ScreenNavigator.loadScreen(ScreenNavigator.ALGO_SCREEN);
    }
    /**********************************************************
     * Handle the BM button - Loads the algo screen
     **********************************************************/
    @FXML private void handleBMBtn() {
        algoChoice = BOYER_MOORE;
        currentAlgoLabel.setText("Boyer-Moore");
        ScreenNavigator.loadScreen(ScreenNavigator.ALGO_SCREEN);
    }
    /**********************************************************
     * Handle the KMP button - Loads the algo screen
     **********************************************************/
    @FXML private void handleKMPBtn() {
        algoChoice = KNUTH_MORRIS_PRATT;
        currentAlgoLabel.setText("Knuth-Morris-Pratt");
        ScreenNavigator.loadScreen(ScreenNavigator.ALGO_SCREEN);
    }
    /**********************************************************
     * Handle the QS button - Loads the algo screen
     **********************************************************/
    @FXML private void handleQSBtn() {
        algoChoice = QUICK_SEARCH;
        currentAlgoLabel.setText("Quick-Search");
        ScreenNavigator.loadScreen(ScreenNavigator.ALGO_SCREEN);
    }

    /***********************************************************
     * Handle the Horspool button - Loads the algo screen
     **********************************************************/
    @FXML private void handleHPBtn() {
        algoChoice = HORSPOOL;
        currentAlgoLabel.setText("Horspool");
        ScreenNavigator.loadScreen(ScreenNavigator.ALGO_SCREEN);
    }

    /***********************************************************
     * Handle the TwoWay button - Loads the algo screen
     **********************************************************/
    @FXML private void handleTWBtn() {
        algoChoice = TWO_WAY;
        currentAlgoLabel.setText("Two-Way");
        ScreenNavigator.loadScreen(ScreenNavigator.ALGO_SCREEN);
    }
    /**********************************************************
     * Handle the NSN button - Loads the algo screen
     **********************************************************/
    @FXML private void handleNSNBtn() {
        algoChoice = NOT_SO_NAIVE;
        currentAlgoLabel.setText("Not-So-Naive");
        ScreenNavigator.loadScreen(ScreenNavigator.ALGO_SCREEN);
    }
    /**********************************************************
     * Handle the SM button - Loads the algo screen
     **********************************************************/
    @FXML private void handleSMBtn() {
        algoChoice = SMITH;
        currentAlgoLabel.setText("Smith");
        ScreenNavigator.loadScreen(ScreenNavigator.ALGO_SCREEN);
    }

    /***********************************************************
     * Handle the exit button - Closes the application
     **********************************************************/
    @FXML private void handleExitBtn() {
        Platform.exit();
    }
}