package model;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/****************************************************
 * The Not-So-Naive Algorithm Animation
 ***************************************************/
public class NotSoNaiveAlgo extends Visualisable {

    /****************************************************
     * Default Constructor
     ***************************************************/
    public NotSoNaiveAlgo( Rectangle tA, Rectangle pA, Rectangle tB, Rectangle pB, Label label, ListView codeView, ListView infoView,
                           ListView listOne, ListView listTwo, Pane backPane) {
        super(tA, pA, tB, pB, label, codeView, infoView);

        loadAlgoData();
//        // Populate ListViews with the appropriate data
//        loadAlgoData(NSN_TXT);
    }

    /*****************************************************************************************
     * Create the animation
     * @return SequentialTransition the sequence of animations
     ****************************************************************************************/
    @Override public SequentialTransition getAnimation()
    {

        // The centralAnimation which will store all sub-Animations
        SequentialTransition animSequence = new SequentialTransition();
        // Initialize X-coordinates of the Rectangles
        this.setXCoordinates();

        // length of string (m) and text (n)
        int m = pattern.length, n = text.length;
        // current position in text(i)  and string (j)
        int j = 2, i = 1;
        // starting position in text
        int sp = 0;

        // Preprocessing
        int k = (pattern[0] == pattern[1]? 2 : 1);
        int l = (pattern[0] == pattern[1]? 1 : 2);

        // Reset result
        result = -1;

        // Move the orange rectangles at the second char of the string
        animSequence.getChildren().addAll(
                new ParallelTransition(
                        moveRectsA(rectWidth, Duration.ONE),
                        moveRectsB(rectWidth, Duration.ONE)
                ),
                highlightCodeRow(ZERO, 7,8),
                new PauseTransition(ONE_SEC)
        );

        // while we have not reached the end of text and a result hasn't been found
        while ( sp <= n - m && result < 0 )
        {
            animSequence.getChildren().addAll(
                    highlightCodeRow(ZERO, 9,10),
                    new PauseTransition(HALF_SEC),
                    highlightCodeRow(ZERO, 12)
            );

            // If there is a match
            if ( pattern[1] == text[i] )
            {
                animSequence.getChildren().addAll(
                        highlightCodeRow(HALF_SEC,13,14),
                        hideOrangeRect(),
                        showGreenRect(),
                        new PauseTransition(ONE_SEC) );

                if (m > 2) // move to the next char on the right - otherwise the first char must be checked
                animSequence.getChildren().addAll( moveOrangeRect(rectWidth), new PauseTransition(HALF_SEC) );


                // Check the rest of the characters till the end of the pattern
                while ( (j < m) && pattern[j] == text[i + 1] ) {
                    animSequence.getChildren().addAll(
                            highlightCodeRow(HALF_SEC,13,14),
                            hideOrangeRect(),
                            setRectBWidth(j), // Highlight one more character
                            new PauseTransition( HALF_SEC ) // Pause for half a second
                    );
                    j++; i++; // move on in text and pattern

                    if (j < m){
                        animSequence.getChildren().addAll(
                                highlightCodeRow(HALF_SEC,15,16),
                                moveOrangeRect(rectWidth),
                                new PauseTransition(HALF_SEC)
                        );
                    }
                }

                animSequence.getChildren().addAll(highlightCodeRow(HALF_SEC, 18, 19,20) );
                // if we compared all chars till the end of the pattern
                // and the first characters of the pattern and text match
                if ( j == m ) {

                    animSequence.getChildren().addAll(
                            new PauseTransition( ONE_SEC ), // Pause for half a second
                            moveOrangeRect((1 - m) * rectWidth),
                            new PauseTransition(HALF_SEC)
                    );

                    if( pattern[0] == text[sp] ){
                        animSequence.getChildren().addAll(
                                highlightCodeRow(HALF_SEC,21),
                                hideOrangeRect(),
                                setRectBWidth( m ),
                                moveRectsB(-rectWidth, Duration.ONE),
                                new PauseTransition( HALF_SEC )
                        );
                        result = sp; // result found
                    } else
                    {
                        animSequence.getChildren().addAll(
                                highlightCodeRow( HALF_SEC, 23,24,25 ),
                                mismatchTransition(),
                                new ParallelTransition(
                                        moveRectsA(rectWidth * ( l+1), ONE_SEC),
                                        moveRectsB(rectWidth * l, ONE_SEC),
                                        moveLabel(rectWidth * l, ONE_SEC)
                                ),
                                setRectBWidth( 1 )
                        );

                        j = 2; //reset pattern index
                        sp += l; // move starting position of text
                        i = sp + 1; // reset text index
                    }
                } else {
                    animSequence.getChildren().addAll(
                            highlightCodeRow( HALF_SEC, 23,24,25 ),
                            mismatchTransition(),
                            new ParallelTransition(
                                    moveRectsA(rectWidth * (l-j+1), ONE_SEC),
                                    moveRectsB(rectWidth * l, ONE_SEC),
                                    moveLabel(rectWidth * l, ONE_SEC)
                            ),
                            setRectBWidth( 1 )
                    );

                    j = 2; //reset pattern index
                    sp += l; // move starting position of text
                    i = sp + 1; // reset text index
                }
            }
            else // if there is a mismatch
            {
                sp += k; // move starting position of text
                i += k; // move on in text

                animSequence.getChildren().addAll(
                        highlightCodeRow( HALF_SEC, 26 ),
                        highlightCodeRow( HALF_SEC, 27,28 ),
                        mismatchTransition(),
                        new ParallelTransition(
                                moveRectsA(rectWidth * k, ONE_SEC),
                                moveRectsB(rectWidth * k, ONE_SEC),
                                moveLabel(rectWidth * k, ONE_SEC )
                        )
                );
            }
        }
        return animSequence;
    }

    /****************************************************
     * Set the pattern text and populate the list
     * @param pattern - the search text
     * @return boolean - succesful or not
     ***************************************************/
    @Override public boolean setPatternText( String pattern ) {
        int patternLength = pattern.length();
        // Ensure that only ASCII Characters are used
        if (patternLength < 2) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setHeaderText("Minimum character length is 2.");
            alert.setContentText("The Not So Naive algorithm works only for" +
                    "patterns greater than 2 characters. Blank spaces were automatically added.");
            alert.showAndWait();
            // Add extra spaces accordingly
            pattern += (patternLength == 0 ? "  " : " ");
        }

        super.setPatternText(pattern);

        return true;
    }


    /**********************************************************************
     * Loads the algorithm data in the listViews (JAR FILE CHANGE)
     *********************************************************************/
    private void loadAlgoData(){
        ObservableList<String> algoData = FXCollections.observableArrayList(
                "- a simple variation of the Naive (BF) algorithm",
                "- can be quite efficient in some practical cases",
                "- searches the string in the ordering 1,2,...,m-1,1",
                "- identifies the following two cases in which at",
                " a mismatch the string can be advanced by two",
                " positions (rather than by one in BF)",
                "  * if string[0] does not equal string[1], string[0]",
                "    equals text[sp] and string[1] equals text[sp+1],",
                "    then at a mismatch we can move the string two places",
                "  * if string[0] equals string[1], string[0] equals",
                "    text[sp] and string[1] does not equal text[sp+1],",
                "    then at a mismatch we can move the string two places",
                "    to the right",
                "- works only for strings/patterns greater than 2 characters",
                "",
                "Time Complexity O(mn)",
                "- m size of string and n size of text"
        );

        ObservableList<String> algoCode = FXCollections.observableArrayList(
            "// length of string (m) and text (n)",
            "int m = string.length, n = text.length;",
            "// current position in text(i) and string(j)",
            "int j = 2, i = 1;",
            "// starting position in text",
            "int sp = 0;",
            "// Preprocessing",
            "int k = (string[0] == string[1]? 2 : 1);",
            "int l = (string[0] == string[1]? 1 : 2);",
            "// while we have not reached the end of text",
            "while (sp <= n - m && result < 0)",
            "{",
            "    if ( string[1] == text[i] ) { // If there is a match",
            "        // Check the rest of the characters till the end of the pattern",
            "        while ( (j < m) && string[j] == text[i + 1] ) {",
            "            j++; // move on in pattern",
            "            i++; // move on in text",
            "        }",
            "        // if we compared all chars till the end of the pattern",
            "        // and the first characters of the pattern and text match",
            "        if ( j == m && string[0] == text[sp]) {",
            "            return sp; // result found",
            "        }",
            "        j = 2; //reset pattern index",
            "        sp += l; // move starting position of text",
            "        i = sp + 1; // reset text index",
            "    } else { // if there is a mismatch",
            "        sp += k; // move starting position of text",
            "        i += k; // move on in text",
            "    }",
            "}",
            "return -1;"
        );

        codeView.setItems(algoCode);
        infoView.setItems(algoData);
    }

}
