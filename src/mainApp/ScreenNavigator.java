package mainApp;

import javafx.fxml.FXMLLoader;
import java.io.IOException;

/****************************************************************************************
 * REFERENCE: https://github.com/andrehk93/Fellesprosjekt/blob/824998ae67f4f4fb463806248d71b80ef8db1be3/Prosjekt/src/klient/ScreenNavigator.java
 * Utility class for controlling navigation between screens.
 * All methods on the navigator are static to facilitate
 * simple access from anywhere in the application.
 ***************************************************************************************/
public class ScreenNavigator {
    // Convenience constants for fxml layouts managed by the navigator.
    public static final String MAIN = "/fxmlFolder/main.fxml";
    public static final String ALGO_SCREEN = "/fxmlFolder/algoScreen.fxml";
    public static final String HOME_SCREEN = "/fxmlFolder/homeScreen.fxml";
    public static final String TUTORIAL_SCREEN = "/fxmlFolder/tutorialScreen.fxml";
    // The main application layout controller.
    private static MainController mainController;

    /*************************************************************
     * Stores the main controller for later use in navigation tasks.
     * @param mainController the main application layout controller.
     ************************************************************/
    public static void setMainController(MainController mainController) {
        ScreenNavigator.mainController = mainController;
    }

    /*************************************************************
     * Loads the screen specified by the fxml file into the
     * screenHolder pane of the main application layout.
     * @param fxml - the fxml file to be loaded.
     *************************************************************/
    public static void loadScreen(String fxml) {
        try {
            mainController.setScreen(
                    FXMLLoader.load( ScreenNavigator.class.getResource( fxml ) )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}