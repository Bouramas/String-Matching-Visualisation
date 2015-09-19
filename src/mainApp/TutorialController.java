package mainApp;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

/****************************************************
 * The Tutorial Screen Controller
 * Responsible for handling user events
 ***************************************************/
public class TutorialController implements Initializable {


    // Constants
    private final int INVISIBLE = 0;
    private final int VISIBLE = 1;
    private final Duration FADE_DURATION = Duration.seconds(0.5);

    @FXML private Button startBtn, nextBtnA, nextBtnB, nextBtnC, nextBtnD, endBtn;
    @FXML private TextField searchTextInput, patternTextInput;
    @FXML private Pane settingsPane, backPane, startPane, helpPaneA, helpPaneB, helpPaneC, helpPaneD, endPane;

    /**************************************************************************
     * Method that runs as soon as the View loads
     *************************************************************************/
    @Override public void initialize(URL location, ResourceBundle resources) {

        // Set the Default text, fontSize and animSpeed
        searchTextInput.setText("Search Text");
        patternTextInput.setText("Pattern Text");

    }


    /**************************************************************************
     * Handles startBtn
     *************************************************************************/
    @FXML private void handleStartBtn() {
        new ParallelTransition(
                getFadeTransition(startPane, startBtn, true),
                getFadeTransition(helpPaneA, nextBtnA, false)
        ).play();
    }

    /**************************************************************************
     * Handles nextBtnA
     *************************************************************************/
    @FXML
    private void handleNextBtnA() {
        new ParallelTransition(
                getFadeTransition(helpPaneA, nextBtnA, true),
                getFadeTransition(helpPaneB, nextBtnB, false)
        ).play();
    }

    /**************************************************************************
     * Handles nextBtnB
     *************************************************************************/
    @FXML private void handleNextBtnB() {
        new ParallelTransition(
                getFadeTransition(helpPaneB, nextBtnB, true),
                getFadeTransition(helpPaneC, nextBtnC, false)
        ).play();
    }

    /**************************************************************************
     * Handles nextBtnC
     *************************************************************************/
    @FXML private void handleNextBtnC() {
        FadeTransition transition = new FadeTransition(FADE_DURATION, settingsPane);
        transition.setFromValue( INVISIBLE );
        transition.setToValue( VISIBLE );
        new ParallelTransition(
                getFadeTransition(helpPaneC, nextBtnC, true),
                getFadeTransition(helpPaneD, nextBtnD, false),
                transition
        ).play();
    }

    /**************************************************************************
     * Handles nextBtnD
     *************************************************************************/
    @FXML private void handleNextBtnD() {
        FadeTransition transition = new FadeTransition(FADE_DURATION, settingsPane);
        transition.setFromValue( VISIBLE );
        transition.setToValue( INVISIBLE );
        new ParallelTransition(
                getFadeTransition(helpPaneD, nextBtnD, true),
                getFadeTransition(endPane, endBtn, false),
                transition
        ).play();
    }

    /**************************************************************************
     * Handles nextBtnD
     *************************************************************************/
    @FXML private void handleEndBtn() {
        // Load the Home Screen
        ScreenNavigator.loadScreen(ScreenNavigator.HOME_SCREEN);
    }

    /**************************************************************************************
     * Method to hide or show a panel and enable or disable its button
     * @param pane - the pane to show/hide
     * @param button - the button to enable/disable
     * @param hide - boolean (hide-true / show-false)
     * @return FadeTransition - Animation doing the changes
     *************************************************************************************/
    private FadeTransition getFadeTransition(Pane pane, Button button, boolean hide) {
        FadeTransition transition = new FadeTransition(FADE_DURATION, pane);
        transition.setFromValue((hide ? VISIBLE : INVISIBLE));
        transition.setToValue((hide ? INVISIBLE : VISIBLE));
        transition.setOnFinished(event -> button.setDisable(hide));
        return transition;
    }

}
