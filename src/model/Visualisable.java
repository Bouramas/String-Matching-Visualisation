package model;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/****************************************************
 * The Visualisable Abstract Class
 * Defines a series of methods that assists each
 * class that extends it to visualise an algorithm
 ***************************************************/
public abstract class Visualisable {

    // Private Constants
    private static final String COLOR_RED = "-fx-fill: #ff333e";
    private static final String COLOR_ORANGE = "-fx-fill: darkorange";
    // Protected Constants
    protected static final int CELL_WIDTH = 25;
    protected static final int PANE_PADDING = 25 ;
    protected static final String VISIBLE = "-fx-opacity: 100%";
    protected static final String SEMI_VISIBLE = "-fx-opacity: 50%";
    protected static final String INVISIBLE = "-fx-opacity: 0%";
    protected static final Duration ZERO = Duration.ZERO;
    protected static final Duration ONE_SEC = Duration.seconds(1);
    protected static final Duration HALF_SEC = Duration.seconds(0.5);
    protected static final String BF_TXT = "bruteForce";
    protected static final String BM_TXT = "boyerMoore";
    protected static final String KMP_TXT = "knuthMorrisPratt";
    protected static final String QS_TXT = "quickSearch";
    protected static final String SM_TXT = "smith";
    protected static final String TW_TXT = "twoWay";
    protected static final String NSN_TXT = "notSoNaive";
    protected static final String HP_TXT = "horspool";
    protected static final String DATA_ADDRESS = "src/resources/algoData/";

    // Objects that will be moving during the animation
    private Rectangle textRectA, textRectB, patternRectA, patternRectB;
    protected ListView codeView, infoView;
    private Label patternLabel;
    // Other variables
    private double textRectAX, textRectBX, patternRectAX, patternRectBX, patternLabelX; // X-Coordinates of objects
    protected double rectWidth;// The initial rectangle width
    protected char[] text, pattern; // Search text and pattern to search for
    protected int result = -1; // stores the result of the search - default ( -1 = no occurence)


    /****************************************************
     * Constructor
     * @param textRectA - search text Rectangle (trailing)
     * @param patternRectA - pattern Rectangle (trailing)
     * @param textRectB - search text Rectangle (leading)
     * @param patternRectB - pattern Rectangle (leading)
     * @param patternLabel - the pattern label
     ***************************************************/
    public Visualisable(Rectangle textRectA, Rectangle patternRectA, Rectangle textRectB, Rectangle patternRectB,
                        Label patternLabel, ListView codeView, ListView infoView){
        // Initialize all objects to be animated
        this.textRectA = textRectA;
        this.textRectB = textRectB;
        this.patternRectA = patternRectA;
        this.patternRectB = patternRectB;
        this.patternLabel = patternLabel;
        this.codeView = codeView;
        this.infoView = infoView;
        // Initialize the rectangle's width
        this.rectWidth = this.textRectB.getWidth();


        // Fix ListViews
        infoView.setFocusModel(null);
        codeView.setFocusModel(null);

        infoView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        codeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    /************************************************************************************************
     *
     *                                      Public Methods
     *
     /************************************************************************************************
     * Creates and returns all the animations in a sequence
     * @return a Sequential Transition containing all the animations
     ***************************************************************/
    public SequentialTransition getAnimation(){
        return new SequentialTransition();
    }

    /****************************************************
     * Set the search text
     * @param text - the search text
     ***************************************************/
    public void setSearchText( String text ) { this.text = text.toCharArray();  }

    /****************************************************
     * Set the pattern text
     * @param pattern - the search text
     * @return boolean - succesful or not
     ***************************************************/
    public boolean setPatternText( String pattern ) {
        this.pattern = pattern.toCharArray();
        return true;
    }

    /******************************************************************
     * Accesor method for the result of the search
     * @return int result -1 if not found / or the index if found
     *****************************************************************/
    public int getResult() { return result;  }

    /********************************************************************
     * Reset position, width and visibility of animated objects
     *******************************************************************/
    public void resetAnimation( ) {
        textRectA.setTranslateX(0);
        textRectB.setTranslateX(0);
        patternRectA.setTranslateX(0);
        patternRectB.setTranslateX(0);
        patternLabel.setTranslateX(0);
        patternRectB.setWidth(rectWidth);
        textRectB.setWidth(rectWidth);
        this.setVisible(true, patternRectA, textRectA);
        this.setVisible(false, patternRectB, textRectB);
    }

    /************************************************************************************************
     *
     *                                      Protected Methods
     *
     /************************************************************************************************
     * Loads the algorithm data (code and info) in the listViews
     * @param filename - the name of the file that contains the data
     *********************************************************************/
    protected void loadAlgoData(String filename){
        Path codePath = Paths.get(DATA_ADDRESS + filename + ".txt");
        Path infoPath = Paths.get(DATA_ADDRESS + filename + "Info.txt");

        codeView.setItems(getData(codePath));
        infoView.setItems(getData(infoPath));
    }

    /********************************************************************************
     * Highlights a specific row in the Code ListView in a PauseTransition
     * @param duration - the pause period after the animation
     * @param rows - the rows to be highlighted
     * @return a Pause Transition doing the changes
     *******************************************************************************/
    protected PauseTransition highlightCodeRow(Duration duration, int row, int...rows){
            PauseTransition animation = new PauseTransition( duration );
            animation.setOnFinished(event -> {
                codeView.getSelectionModel().clearSelection();
                codeView.getSelectionModel().selectIndices(row, rows);
                // Note that if the following method is used there is an error,
                // while if each method is called seperately it works fine
                // codeView.getSelectionModel().clearAndSelect(row);
            });
            return animation;
    }

    /***********************************************************************
     * Change the x-coordinates of the patternLabel
     * @param step - the step to be made (double)
     * @param duration - the duration of the animation returned
     * @return TranslateTransition - Animation doing the transitions
     **********************************************************************/
    protected TranslateTransition moveLabel( double step, Duration duration ) {
        TranslateTransition transition = new TranslateTransition( duration, patternLabel );
        transition.setFromX( patternLabelX );
        patternLabelX += step;
        transition.setToX(patternLabelX);
        return transition;
    }

    /***********************************************************************
     * Change the x-coordinates of the text and pattern rect-A
     * @param step - the step to be made (double)
     * @param duration - the duration of the animation returned
     * @return ParallelTransition - Animation doing the transitions
     **********************************************************************/
    protected ParallelTransition moveRectsA(double step, Duration duration) {
        return new ParallelTransition(
                movePrectA(patternRectAX + step, duration),
                moveTrectA(textRectAX + step, duration) );
    }

    /********************************************************************************
     * Change the x-coordinates of the text and pattern rect-B
     * @param step - the step to be made (double)
     * @param duration - the duration of the animation returned
     * @return ParallelTransition - Animation doing the transitions
     *******************************************************************************/
    protected ParallelTransition moveRectsB(double step, Duration duration){
        return new ParallelTransition(
                movePrectB(patternRectBX + step, duration),
                moveTrectB(textRectBX + step, duration) );
    }

    /********************************************************************************
     * Change the x-coordinates of the text and pattern rects A and B
     * @param step - the step to be made (double)
     * @param duration - the duration of the animation returned
     * @return ParallelTransition - Animation doing the transitions
     *******************************************************************************/
    protected ParallelTransition moveRectsBasedLabel( double step, Duration duration ) {
        return new ParallelTransition(
                movePrectA( patternLabelX + step, duration ),
                moveTrectA( patternLabelX + step, duration ),
                movePrectB( patternLabelX + step, duration ),
                moveTrectB( patternLabelX + step, duration ) );
    }

    /*****************************************************************************
     * Make Orange Rectangle Red for half Sec, on finished make it orange again
     * and make invisible the the other (green Rectangles)
     * @return SequentialTransition - Animation doing the transitions
     ****************************************************************************/
    protected SequentialTransition mismatchTransition(){
        PauseTransition transitionA = new PauseTransition( Duration.ONE );
        transitionA.setOnFinished(event -> {
            patternRectA.setStyle( COLOR_RED );
            textRectA.setStyle( COLOR_RED );
        });

        PauseTransition transitionB = new PauseTransition( HALF_SEC );
        transitionB.setOnFinished(event -> {
            patternRectA.setStyle(COLOR_ORANGE);
            textRectA.setStyle(COLOR_ORANGE);
            this.setVisible(false, patternRectB, textRectB);
        });

        return new SequentialTransition(transitionA, transitionB);
    }

    /*****************************************************************************
     * Modifies the visibility of the Green rectangles to true
     * @return PauseTransition - Transition doing the changes
     *****************************************************************************/
    protected PauseTransition showGreenRect(){
        return this.toggleVisibility( true, patternRectB, textRectB );
    }

    /*****************************************************************************
     * Modifies the visibility of the Orange rectangles to false
     * @return PauseTransition - Transition doing the changes
     *****************************************************************************/
    protected PauseTransition hideOrangeRect(){
        return this.toggleVisibility( false, patternRectA, textRectA );
    }

    /*****************************************************************************
     * Set the width of the B rectangles
     * @param size - the size in terms of the original rect width
     * @return PauseTransition - Animation doing the changes
     ****************************************************************************/
    protected PauseTransition setRectBWidth(int size) {
        PauseTransition animation = new PauseTransition( ZERO );
        animation.setOnFinished(event -> {
            textRectB.setWidth((size * rectWidth));
            patternRectB.setWidth((size * rectWidth));
        });
        return animation;
    }

    /*****************************************************************************
     * Increase the width of the B rectangles incrementally
     * @return PauseTransition - Animation doing the changes
     ****************************************************************************/
    protected PauseTransition incrementRectBWidth() {
        PauseTransition animation = new PauseTransition( ZERO );
        animation.setOnFinished(event -> {
            textRectB.setWidth( textRectB.getWidth() + rectWidth );
            patternRectB.setWidth( patternRectB.getWidth() + rectWidth );
        });
        return animation;
    }

    /********************************************************************************
     * Initializes or resets the X-coordinates of the Rectangles and the label
     *******************************************************************************/
    protected void setXCoordinates() {
        this.textRectAX = textRectA.getTranslateX();
        this.textRectBX = textRectB.getTranslateX();
        this.patternRectAX = patternRectA.getTranslateX();
        this.patternRectBX = patternRectB.getTranslateX();
        this.patternLabelX = patternLabel.getTranslateX();
    }

    /********************************************************************************
     * Move the orange rectangles to the appropriate positions and make them visible
     * @return ParallelTransition - One MilliSecond Transition doing the transitions
     *******************************************************************************/
    protected ParallelTransition moveOrangeRect( double step ) {
        ParallelTransition parallelTransition = new ParallelTransition(
                moveTrectA( textRectAX + step , Duration.ONE ),
                movePrectA( patternRectAX + step, Duration.ONE ) );
        parallelTransition.setOnFinished(event -> this.setVisible(true, textRectA, patternRectA));
        return parallelTransition;
    }

    /******************************************************************
     * Reads a file and stores each line in an ObservableList
     * @param path - the path of the file to be opened
     * @return an Observable list containing the contents of the file
     ******************************************************************/
    protected ObservableList<String> getData(Path path){
        ObservableList<String> list = FXCollections.observableArrayList();

        try ( BufferedReader br = Files.newBufferedReader( path ) ) {
            for( String line; ( line = br.readLine() ) != null; )
                list.add(line);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setHeaderText("IOException - Filename was not found");
            alert.setContentText("IOException message: " + e.getMessage());
            alert.showAndWait();
        }

        return list;
    }

    /************************************************************************************************
     *
     *                                  Private Helper Methods
     *
    /************************************************************************************************
     * Modifies the visibility of the rectangles given
     * @param visible - true for visible and vice versa
     * @param rectA - the rectangle to be modified
     * @param rectB - the rectangle to be modified
     ******************************************************************/
    private void setVisible( boolean visible, Rectangle rectA, Rectangle rectB ) {
        if (visible) {
            rectA.setStyle( SEMI_VISIBLE );
            rectB.setStyle( SEMI_VISIBLE );
        } else {
            rectA.setStyle( INVISIBLE );
            rectB.setStyle( INVISIBLE );
        }
    }

    /*****************************************************************************
     * Modifies the visibility of the rectangles given and returns an Animation
     * @param visible - true for visible and vice versa
     * @param rectA - the rectangle to be modified
     * @param rectB - the rectangle to be modified
     * @return PauseTransition - Transition doing the changes
     *****************************************************************************/
    private PauseTransition toggleVisibility( boolean visible, Rectangle rectA, Rectangle rectB ) {
        PauseTransition animation = new PauseTransition( ZERO );
        animation.setOnFinished(event -> this.setVisible(visible, rectA, rectB));
        return animation;
    }

    /********************************************************************
     * Change the x-coordinates of pattern rect A
     * @param newPosition - the step to be made (double)
     * @param duration - the duration of the animation returned
     * @return ParallelTransition - Animation doing the transition
     *******************************************************************/
    private TranslateTransition movePrectA(double newPosition, Duration duration) {
        TranslateTransition transition = new TranslateTransition(duration, patternRectA);
        transition.setFromX(patternRectAX);
        patternRectAX = newPosition;
        transition.setToX(patternRectAX);
        return transition;
    }

    /********************************************************************
     * Change the x-coordinates of text rect A
     * @param newPosition - the step to be made (double)
     * @param duration - the duration of the animation returned
     * @return ParallelTransition - Animation doing the transition
     *******************************************************************/
    private TranslateTransition moveTrectA(double newPosition, Duration duration) {
        TranslateTransition transition = new TranslateTransition(duration, textRectA);
        transition.setFromX(textRectAX);
        textRectAX = newPosition;
        transition.setToX(textRectAX);
        return transition;
    }

    /********************************************************************
     * Change the x-coordinates of pattern rect B
     * @param newPosition - the step to be made (double)
     * @param duration - the duration of the animation returned
     * @return ParallelTransition - Animation doing the transition
     *******************************************************************/
    private TranslateTransition movePrectB(double newPosition, Duration duration) {
        TranslateTransition transition = new TranslateTransition( duration, patternRectB );
        transition.setFromX(patternRectBX);
        patternRectBX = newPosition;
        transition.setToX(patternRectBX);
        return transition;
    }

    /********************************************************************
     * Change the x-coordinates of text rect B
     * @param newPosition - the step to be made (double)
     * @param duration - the duration of the animation returned
     * @return ParallelTransition - Animation doing the transition
     *******************************************************************/
    private TranslateTransition moveTrectB(double newPosition, Duration duration) {
        TranslateTransition transition = new TranslateTransition( duration, textRectB );
        transition.setFromX(textRectBX);
        textRectBX = newPosition;
        transition.setToX(textRectBX);
        return transition;
    }
}