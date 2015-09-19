package model;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.shape.Rectangle;

/****************************************************
 * The Brute Force Algorithm Animation
 ***************************************************/
public class BruteForceAlgo extends Visualisable {

    /****************************************************
     * Default Constructor
     ***************************************************/
    public BruteForceAlgo(Rectangle textRectA, Rectangle patternRectA, Rectangle textRectB, Rectangle patternRectB,
                          Label patternLabel, ListView codeView, ListView infoView) {
        super(textRectA, patternRectA, textRectB, patternRectB, patternLabel, codeView, infoView);


        loadAlgoData();
//        // Populate ListViews with the appropriate data
//        loadAlgoData(BF_TXT);
    }

    /*****************************************************************************************
     * Create the animation
     * @return SequentialTransition the sequence of animations
     ****************************************************************************************/
    @Override public SequentialTransition getAnimation() {

        // The centralAnimation which will store all sub-Animations
        SequentialTransition animSequence = new SequentialTransition();
        // Initialize X-coordinates of the Rectangles
        this.setXCoordinates();
        // Store the searchText and patternText length
        int m = pattern.length, n = text.length;
        // Initialize all indexes at 0
        int searchIndex = 0, i = 0, j = 0;

        // While pattern hasn't been found and not reached end of text/string
        while ( searchIndex <= n-m && j < m ) {

            animSequence.getChildren().addAll(
                    new PauseTransition( HALF_SEC ),
                    highlightCodeRow( ZERO,6,7 )
            );

            if (text[i] == pattern[j]) { // if chars match

                animSequence.getChildren().addAll(
                        highlightCodeRow( HALF_SEC, 8 ),
                        highlightCodeRow( HALF_SEC , 9,10 ),
                        hideOrangeRect()
                );

                // Show the other green Rectangles
                if (j == 0) animSequence.getChildren().add( showGreenRect() );
                // else increase the size to highlight one more character
                else animSequence.getChildren().add( setRectBWidth( j + 1 ) );
                // Pause for half a second
                animSequence.getChildren().add( new PauseTransition( ONE_SEC ) );

                i++; j++; // move on in text and pattern

                if (searchIndex <= n - m && j < m)
                    animSequence.getChildren().addAll(
                            moveOrangeRect(rectWidth),
                            new PauseTransition(HALF_SEC)
                    );
            }
            else { // if there is a mismatch
                // Add the transitions to the general sequence
                animSequence.getChildren().addAll(
                        highlightCodeRow( HALF_SEC, 11 ),
                        highlightCodeRow( HALF_SEC, 12,13,14 ),
                        mismatchTransition() );
                if (j > 0) animSequence.getChildren().addAll(moveOrangeRect(-j * rectWidth),setRectBWidth( 1 ));
                animSequence.getChildren().addAll(
                        new ParallelTransition(
                                moveRectsA( rectWidth, ONE_SEC ),
                                moveRectsB( rectWidth, ONE_SEC ),
                                moveLabel( rectWidth, ONE_SEC ) )
                        );

                j = 0; // start again in string
                searchIndex++; // advance search index
                i = searchIndex; // back up in text to new search index
            }
        }

        // Highlight the lines determining the result
        animSequence.getChildren().add(highlightCodeRow( HALF_SEC, 16 ));
        if (j == m ) animSequence.getChildren().add( highlightCodeRow( HALF_SEC, 16, 17) );
        else animSequence.getChildren().add( highlightCodeRow( HALF_SEC, 18, 19) );

        // Store the result

        if (j == m && m > 0)
            result = searchIndex;
        else
            result = -1;

        return animSequence;
    }

    /**********************************************************************
     * Loads the algorithm data in the listViews (JAR FILE CHANGE)
     *********************************************************************/
    private void loadAlgoData(){
        ObservableList<String> algoData = FXCollections.observableArrayList(
        "- also known as exhaustive search (tests all positions)",
        "- set the current starting position in the text to be zero",
        "  compare text and string characters left to right until the",
        "  entire string is matched or a character mismatches",
        "- in the case of a mismatch advance the starting",
        "  position of the string in the text by 1 and repeat",
        "- continue until a match is found or the text is exhausted",
        "",
        "Time Complexity: O(mn)",
        "- m size of string and n size of text"
        );

        ObservableList<String> algoCode = FXCollections.observableArrayList(
        "// length of string (m) and text (n)",
        "int m = string.length, n = text.length;",
        "// starting position in text t",
        "int sp = 0;",
        "// current position in text (i) and string (j)",
        "int i = 0, j = 0; ",
        "// while we have not reached the end of text or string",
        "while ( ( sp <= n-m ) && ( j < m ) )",
        "   if (t[i] == s[j]){ // chars in text and string match",
        "     i++;     // move on in text ",
        "     j++;     // move on in string",
        "   } else { // a mismatch between text and string",
        "     j = 0;   // start again in string",
        "     sp++;    // advance starting position in text",
        "     i = sp;  // start in text from new starting position",
        "   }",
        "if (j == m) // reached the end of string",
        "   return sp; // occurrence of string found starting from sp ",
        "else // reached the end of text",
        "   return -1; // no occurrence of string found"
        );

        codeView.setItems(algoCode);
        infoView.setItems(algoData);
    }
}