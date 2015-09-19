package mainApp;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import model.*;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

/*****************************************************************
 * The Controller for the Algorithms Screen Panel.
 ****************************************************************/
public class AlgoController implements Initializable {

    //Constants
    private final String RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Chars for generating random text
    private final int TEXT_LENGTH = 25; // length of random search text
    private final int PATTERN_LENGTH = 6; // length of random pattern text
    private final int MAX_P_TXT_LENGTH = 10; // max length of pattern text
    private final int MAX_S_TXT_LENGTH = 53; // max length of search text
    private final int INVISIBLE = 0;
    private final int VISIBLE = 1;
    private final int SHOW_X = -550; // Coordinates for panel transitions
    private final int FONT_SIZE = 12; // Default listView fontSize
    private final double ANIM_SPEED = 1.5; // Default animation speed
    private static final String WARNING_BACKGROUND = "-fx-background-color: #C20E14";
    private static final String WHITE_BACKGROUND = "-fx-background-color: white";
    private static final Duration WARNING_DURATION = Duration.seconds(0.2);
    private static final Duration ANIM_DURATION = Duration.seconds(0.7); // Pane transition duration

    // Booleans to track settings panel, Info and Code ListViews visibility
    private boolean settingsVisible, codeVisible, infoVisible, tableInfoVisible;
    private SequentialTransition animSequence; // Animation Sequence
    private Visualisable visualisable;

    @FXML private Button playBtn, pauseBtn, nextBtn, previousBtn, settingsBtn, resetBtn, clearTextBtn ,randomBtn,doneBtn, tableInfoBtn;
    @FXML private Rectangle searchRectA, searchRectB, patternRectA, patternRectB;
    @FXML private Slider fontSizeSlider, animSpeedSlider;
    @FXML private Label searchLabel,patternLabel, resultLabel;
    @FXML private TextField searchTextInput, patternTextInput;
    @FXML private ListView<String> codeView, infoView, algoTableA, algoTableB, algoTableC, tableInfoView;
    @FXML private Pane settingsPane, backPane, tableInfoPane;

    /**************************************************************************
     * Method that runs as soon as the View loads
     *************************************************************************/
    @Override public void initialize(URL location, ResourceBundle resources) {

        // Load the appropriate algorithm according to the choice
        switch (MainController.algoChoice) {
            case MainController.BRUTE_FORCE:
                visualisable = new BruteForceAlgo(searchRectA, patternRectA, searchRectB, patternRectB, patternLabel, codeView, infoView); break;
            case MainController.BOYER_MOORE:
                visualisable = new BoyerMooreAlgo(searchRectA, patternRectA, searchRectB, patternRectB, patternLabel, codeView, infoView, algoTableA, algoTableB, backPane, tableInfoView);break;
            case MainController.KNUTH_MORRIS_PRATT:
                visualisable = new KMPAlgo(searchRectA, patternRectA, searchRectB, patternRectB, patternLabel, codeView, infoView, algoTableA, algoTableB, algoTableC, backPane, tableInfoView); break;
            case MainController.QUICK_SEARCH:
                visualisable = new QuickSearchAlgo(searchRectA, patternRectA, searchRectB, patternRectB, patternLabel, codeView, infoView, algoTableA, algoTableB, backPane, tableInfoView); break;
            case MainController.TWO_WAY: // two way
                visualisable = new TwoWayAlgo(searchRectA, patternRectA, searchRectB, patternRectB, patternLabel, codeView, infoView, algoTableA, algoTableB, algoTableC, backPane, tableInfoView); break;
            case MainController.NOT_SO_NAIVE:// not so naive
                visualisable = new NotSoNaiveAlgo(searchRectA, patternRectA, searchRectB, patternRectB, patternLabel, codeView, infoView,algoTableA, algoTableB, backPane); break;
            case MainController.SMITH:
                visualisable = new SmithAlgo(searchRectA, patternRectA, searchRectB, patternRectB, patternLabel, codeView, infoView, algoTableA, algoTableB, algoTableC, backPane, tableInfoView); break;
            case MainController.HORSPOOL:
                visualisable = new HorspoolAlgo(searchRectA, patternRectA, searchRectB, patternRectB, patternLabel, codeView, infoView, algoTableA, algoTableB, backPane, tableInfoView);break;
        }

        // Adjust the table info pane position or remove the table button if not required
        switch (MainController.algoChoice) {
            case MainController.BRUTE_FORCE:
            case MainController.NOT_SO_NAIVE:
                tableInfoBtn.setVisible(false);
                break;
            case MainController.BOYER_MOORE:
            case MainController.QUICK_SEARCH :
            case MainController.HORSPOOL:
                tableInfoPane.setLayoutY(tableInfoPane.getLayoutY() - 35);
                break;
        }

        // Add tooltips to buttons and input-fields
        this.addTooltips();

        // Add listeners to Settings
        this.addSettingsListeners();

        // Set the Default text, fontSize and animSpeed
        searchTextInput.setText( "AGCALGHGAACGAGSGCAGAGAG" );
        patternTextInput.setText( "GCAGAGAG" );
        fontSizeSlider.setValue( FONT_SIZE );
        animSpeedSlider.setValue(ANIM_SPEED);

        // Disable the action buttons
        this.setDisableActionBtns(true);
    }

    /************************************************************************************************
     *
     *                  Methods to handle Settings buttons (Clear and Generate Random text)
     *
    /************************************************************************************************
     * Clear Text button - clears the input fields
     **************************************************/
    @FXML private void handleClearBtn() {
        searchTextInput.setText("");
        patternTextInput.setText("");
    }

    /****************************************************************
     * Generate random button - new random search and pattern text
     ***************************************************************/
    @FXML private void generateRandomText() {

        //Generate a random searchText
        char[] text = new char[TEXT_LENGTH];
        for (int i = 0; i < TEXT_LENGTH; i++)
            text[i] = RANDOM_CHARS.charAt(new Random().nextInt(RANDOM_CHARS.length()));

        // Generate a pattern text as a substring of the searchText
        int substring = new Random().nextInt( TEXT_LENGTH - PATTERN_LENGTH );
        String patternText = new String( text, substring, PATTERN_LENGTH);

        searchTextInput.setText(new String(text)); // Set the new searchText
        patternTextInput.setText(patternText); // Set the new patternText
    }

    /************************************************************************************************
     *
     *                      Methods to handle Action buttons (Play, Pause, etc.)
     *
    /************************************************************************************************
     * Handles the Play Button based on the animation's state
     ************************************************************/
    @FXML private void handlePlayBtn() {
        if (!patternTextInput.getText().isEmpty() && !searchTextInput.getText().isEmpty()){
            if(animSequence == null ) {
                // Get the animation
                animSequence = visualisable.getAnimation();
                // When the animation finishes
                animSequence.setOnFinished(event -> this.printResult());
                // Enable the action buttons
                this.setDisableActionBtns(false);
                // Set the animation speed according to the settings slider
                animSequence.setRate(animSpeedSlider.getValue());
            }

            if(animSequence.getStatus().equals(Animation.Status.STOPPED)){
                // Reset the objects to initial positions, color, width
                visualisable.resetAnimation();
                // Empty the result label
                resultLabel.setText("");
            }

            animSequence.play();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setHeaderText("No text to search for");
            alert.setContentText("Please enter some text to search for");
            alert.showAndWait();
        }
    }

    /*************************************************************
     * Handles the Pause button - pauses the animation
     ************************************************************/
    @FXML private void handlePauseBtn() {  animSequence.pause(); }

    /*************************************************************
     * Handles the Next and Previous button when pressed
     ************************************************************/
    @FXML private void handleSpeedBtnPressed() {
        // Increase or Reverse and increase the current animation rate
        if (nextBtn.isPressed())
            animSequence.setRate(animSequence.getCurrentRate() * 4);
        else if (previousBtn.isPressed())
            animSequence.setRate(-animSequence.getCurrentRate() * 4);
    }

    /*************************************************************
     * Handles the Next and Previous buttons when released
     ************************************************************/
    @FXML private void handleSpeedBtnReleased() {
        // Reset the animation rate according to the slider speed
        animSequence.setRate(animSpeedSlider.getValue());
    }

    /*************************************************************
     * Handles the Reset button - resets the animation
     ************************************************************/
    @FXML private void handleResetBtn() { this.resetAnimation(); }

    /************************************************************************************************
     *
     *                      Methods to handle Pane Transitions (Setting, Info and Code panes)
     *
    /************************************************************************************************
     * Methods to Show and hide the Settings Pane through a Fade
     * transition which disables/enables the functionality accordingly
     *********************************************************************/
    @FXML private void toggleSettingsPane() {
        FadeTransition transition = new FadeTransition(ANIM_DURATION, settingsPane);
        transition.setFromValue((settingsVisible ? VISIBLE : INVISIBLE));
        transition.setToValue((settingsVisible ? INVISIBLE : VISIBLE));
        transition.setOnFinished(event -> {
            animSpeedSlider.setDisable(!settingsVisible);
            fontSizeSlider.setDisable(!settingsVisible);
            patternTextInput.setDisable(!settingsVisible);
            searchTextInput.setDisable(!settingsVisible);
            clearTextBtn.setDisable(!settingsVisible);
            randomBtn.setDisable(!settingsVisible);
            doneBtn.setDisable(!settingsVisible);
        });
        transition.play();
        settingsVisible ^= true;
    }
    /*********************************************************************
     * Methods to Show and hide the Table Info Pane through a Fade
     * transition
     ********************************************************************/
    @FXML private void toggleTableInfoPane() {
        FadeTransition transitionA = new FadeTransition(ANIM_DURATION, backPane);
        transitionA.setFromValue((tableInfoVisible ? VISIBLE : INVISIBLE));
        transitionA.setToValue((tableInfoVisible ? INVISIBLE : VISIBLE));

        FadeTransition transitionB = new FadeTransition(ANIM_DURATION, tableInfoPane);
        transitionB.setFromValue((tableInfoVisible ? VISIBLE : INVISIBLE));
        transitionB.setToValue((tableInfoVisible ? INVISIBLE : VISIBLE));

        new ParallelTransition(transitionA,transitionB).play();
        tableInfoVisible ^= true;
    }

    /*************************************************************
     * Methods to Show and hide the Info ListView
     ************************************************************/
    @FXML private void toggleInfoPane() {
        this.getTransition(infoView, infoVisible).play();
        infoVisible ^= true;
    }

    /*************************************************************
     * Methods to Show and hide the Code ListView
     ************************************************************/
    @FXML private void toggleCodePane() {
        this.getTransition(codeView, codeVisible).play();
        codeVisible ^= true;
    }

    /************************************************************************************************
     *
     *                                  Private Helper Methods
     *
    /************************************************************************************************
     * Stops and deletes the current animation and disables the buttons
     *************************************************************************/
    private void resetAnimation() {
        if (animSequence != null) {
            resultLabel.setText(""); // Clear the result label
            animSequence.stop(); // Stop the currentAnimation
            this.setDisableActionBtns(true); // Disable the action buttons
            animSequence = null; // Delete the previous animation
            // Reset the objects to initial positions, color, width
            visualisable.resetAnimation();
        }
    }

    /**********************************************************************
     * Methods to disabled/enable Action buttons (Play, Pause, etc.)
     * @param disabled - true for disabled and vice versa
     *********************************************************************/
    private void setDisableActionBtns(boolean disabled) {
        pauseBtn.setDisable(disabled);
        nextBtn.setDisable(disabled);
        previousBtn.setDisable(disabled);
        resetBtn.setDisable(disabled);
    }

    /**********************************************************************
     * Adds tooltips to action buttons and Setting input-fields
     *********************************************************************/
    private void addTooltips() {
        playBtn.setTooltip(new Tooltip("Play Animation"));
        pauseBtn.setTooltip(new Tooltip("Pause Animation"));
        nextBtn.setTooltip(new Tooltip("Fast Forward"));
        previousBtn.setTooltip(new Tooltip("Rewind"));
        resetBtn.setTooltip(new Tooltip("Reset Animation"));
        settingsBtn.setTooltip(new Tooltip("Settings"));
        tableInfoBtn.setTooltip(new Tooltip("Table Information"));
        searchTextInput.setTooltip(new Tooltip("Maximum Characters: 53"));
        patternTextInput.setTooltip(new Tooltip("Maximum Characters: 10"));
    }

    /**************************************************************************
     * Add Listeners to all options available in the Settings pane
     * Note: The input text fields are bound to the labels (Done in FXML)
     *************************************************************************/
    private void addSettingsListeners() {
        //Change the font size of the code/info listView according to the value of the slider
        fontSizeSlider.valueProperty().addListener((arg0, arg1, arg2) -> {
            double fontSize = fontSizeSlider.getValue();
            codeView.setStyle("-fx-font-size: " + fontSize);
            infoView.setStyle("-fx-font-size: " + fontSize);
            tableInfoView.setStyle("-fx-font-size: " + fontSize);
        });
        //Change the animation speed according to the value of the slider
        animSpeedSlider.valueProperty().addListener((arg0, arg1, arg2) -> {
            if ( animSequence != null)
                animSequence.setRate(animSpeedSlider.getValue());
        });
        // Auto-change the search and pattern text when changed
        searchTextInput.textProperty().addListener((arg0, arg1, arg2) -> {
            if ( checkInputText( searchTextInput, MAX_S_TXT_LENGTH)) {
                visualisable.setSearchText(searchLabel.getText());
                this.resetAnimation();
            }
        });
        patternTextInput.textProperty().addListener((arg0, arg1, arg2) -> {
            if ( checkInputText( patternTextInput, MAX_P_TXT_LENGTH ) ) {
                String text = patternLabel.getText();
                if( visualisable.setPatternText(text) ){
                    this.resetAnimation();
                } else {
                    patternTextInput.setText(text.substring(0, text.length() - 1));
                }
            }
        });
    }

    /*********************************************************************
     * Methods that check if the input text is of the required length
     * and shows an indication if its not.
     * @param textField - the textfield to check
     * @param maxLength - the required Length
     * @return boolean - if the input was valid or not
     ********************************************************************/
    private boolean checkInputText( TextField textField, int maxLength ) {

        String text = textField.getText();
        if (text.length() > maxLength ) {
            // Set the text till the appropriate limit
            textField.setText(text.substring(0, maxLength));
            // Show indication of invalid input
            textField.setStyle( WARNING_BACKGROUND );
            PauseTransition pause = new PauseTransition( WARNING_DURATION );
            pause.setOnFinished(event -> textField.setStyle( WHITE_BACKGROUND ) );
            pause.play();
            return false;
        }
        return true;
    }

    /**************************************************************************
     * Print the result of the search on the screen (i.e. match found or not)
     *************************************************************************/
    private void printResult() {
        resultLabel.setText( visualisable.getResult() < 0 ? "No match found":
                "Match found at index: " + visualisable.getResult() );
    }

    /*************************************************************
     * Method to Show and hide the given Node by relocating it
     * @param node - the node to be relocated
     * @param visible - the current visibility of the node
     * @return an Animation doing the transition
     ************************************************************/
    private TranslateTransition getTransition(Node node, boolean visible) {
        TranslateTransition transition = new TranslateTransition(ANIM_DURATION, node);
        transition.setFromX((visible ? SHOW_X : INVISIBLE));
        transition.setToX((visible ? INVISIBLE : SHOW_X));
        return transition;
    }
}