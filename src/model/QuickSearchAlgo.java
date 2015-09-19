package model;

import com.google.common.base.CharMatcher;
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

import java.nio.file.Path;
import java.nio.file.Paths;

/****************************************************
 * The Quick Search Algorithm Animation
 ***************************************************/
public class QuickSearchAlgo extends Visualisable {
    private ListView indexList, patternList, tableView;
    protected static final int CELL_A_WIDTH = 100;
    private Pane backPane;
    private int[] last;


    /****************************************************
     * Default Constructor
     ***************************************************/
    public QuickSearchAlgo( Rectangle tA, Rectangle pA, Rectangle tB, Rectangle pB, Label label, ListView codeView, ListView infoView,
                           ListView indexList, ListView patternList, Pane backPane, ListView tableInfoView) {
        super(tA, pA, tB, pB, label, codeView, infoView);

        this.indexList = indexList;
        this.patternList = patternList;
        this.backPane = backPane;

        this.tableView = tableInfoView;
        loadAlgoData();
//
//        // Populate ListViews with the appropriate data
//        loadAlgoData(QS_TXT);
//        // Populate the tableInfo ListView
//        Path tableInfoPath = Paths.get(DATA_ADDRESS + QS_TXT + "TableInfo.txt");
//        tableInfoView.setItems(getData(tableInfoPath));
//        // Disable User selection
//        tableInfoView.setFocusModel(null);
    }

    public SequentialTransition getAnimation() {

        // The centralAnimation which will store all sub-Animations
        SequentialTransition animSequence = new SequentialTransition();
        // Initialize X-coordinates of the Rectangles
        this.setXCoordinates();
        // Store the searchText and patternText length
        int m = pattern.length, n = text.length;
        // Initialize all indexes at 0
        int searchIndex = 0, i = 0, j = 0;
        // Reset result
        result = -1;
        if (m == 0 || n == 0) // If there is nothing to search for
            return animSequence;

        // While pattern hasn't been found and not reached end of text/string
        while ( searchIndex <= n-m && j < m ) {

            animSequence.getChildren().addAll(
                    new PauseTransition( HALF_SEC ),
                    highlightCodeRow( ZERO,8,9 ),
                    new PauseTransition( HALF_SEC ),
                    highlightCodeRow( ZERO,10 )
            );

            if (text[i] == pattern[j]) { // if chars match

                animSequence.getChildren().addAll(
                        highlightCodeRow( HALF_SEC , 11,12 ),
                        hideOrangeRect()
                );

                // Show the other Rectangles
                if (j == 0) animSequence.getChildren().add( showGreenRect() );
                    // else increase the size to highlight one more character
                else animSequence.getChildren().add( setRectBWidth( j + 1 ) );
                // Pause for half a second
                animSequence.getChildren().add( new PauseTransition( ONE_SEC ) );

                i++; j++; // move on in text and pattern

                if (searchIndex <= n - m && j < m) {
                    animSequence.getChildren().addAll(
                            moveOrangeRect(rectWidth),
                            new PauseTransition(HALF_SEC)
                    );
                } else {
                    animSequence.getChildren().add(highlightCodeRow( HALF_SEC, 20 ));
                    if (j == m ) animSequence.getChildren().add( highlightCodeRow( HALF_SEC, 20, 21) );
                    else animSequence.getChildren().add( highlightCodeRow( HALF_SEC, 22, 23) );
                }

            }
            else { // if there is a mismatch
                // Add the transitions to the general sequence
                animSequence.getChildren().addAll(
                        highlightCodeRow( HALF_SEC, 14 ),
                        highlightCodeRow( HALF_SEC, 15, 16 ,17 ),
                        mismatchTransition() );
                if (j > 0) animSequence.getChildren().addAll(moveOrangeRect(-j * rectWidth));

                int jump;
                if ( searchIndex + m >= text.length){ // check that there won't be an indexOutOfBounds Exception
                    jump = m + 1;
                } else {
                    jump = last[ text[ searchIndex + m ] ];
                }

                animSequence.getChildren().addAll(
                        new ParallelTransition(
                                moveRectsA( rectWidth * jump, ONE_SEC ),
                                moveRectsB( rectWidth * jump, ONE_SEC ),
                                moveLabel( rectWidth * jump, ONE_SEC ) ),
                        setRectBWidth( 1 ) );

                j = 0; // start again in string
                searchIndex += jump; // advance search index
                i = searchIndex; // back up in text to new search index
            }
        }
        // Store the result
        result = ( j == m ? searchIndex : -1 );

        return animSequence;
    }

    /*****************************************************************************************
     * If c is in P, last(c) is equal to the patternLength minus the index of the
     * last (right-most) occurrence of c in P.
     * Otherwise, we can conventionally define last(c) = patternLength + 1
     ****************************************************************************************/
    private void createLastCharArray() {

        int m = pattern.length;

        last = new int[256];

        // If a character doesn't exist in P
        for (int i = 0; i < 256; i++)
            last[i] = m + 1;
        // If it does, calculate its last occurrence
        for (int i = 0; i < m; i++)
            last[ pattern[i] ] = m - i;
    }


    /*************************************************************************
     * Populate the list with the BoyerMoore algorithm's lastChar array
     ************************************************************************/
    private void populateLists() {
        ObservableList<String> charRow = FXCollections.observableArrayList("  Char c ");
        ObservableList<String> lastIndexRow = FXCollections.observableArrayList( " last[c] " );

        int m = pattern.length;

        for ( int i = 0; i < 256; i++ ) {
            if ( !(last[i] > m) ) {
                charRow.add((char) (i) + "");
                lastIndexRow.add(last[i] + "");
            }
        }

        // For the rest last(c) = patternLength + 1
        charRow.add("REST");
        lastIndexRow.add(" " + ( m+1 ) + " ");

        // Populate lists
        indexList.setItems(charRow);
        patternList.setItems(lastIndexRow);
        // Disable user Selection/Focus
        indexList.setFocusModel(null);
        patternList.setFocusModel(null);

        int cellsWidth = 20 + CELL_A_WIDTH + ( CELL_WIDTH * charRow.size() );
        // Adjust the width of the lists based on the length of the pattern
        indexList.setPrefWidth(cellsWidth );
        patternList.setPrefWidth( cellsWidth );

        backPane.setPrefWidth( PANE_PADDING + cellsWidth );
        backPane.setPrefHeight(100);

        indexList.setStyle(VISIBLE);
        patternList.setStyle( VISIBLE );
    }

    /****************************************************
     * Set the pattern text and populate the list
     * @param pattern - the search text
     * @return boolean - successful or not
     ***************************************************/
    @Override public boolean setPatternText( String pattern ) {

        // Ensure that only ASCII Characters are used
        if (!CharMatcher.ASCII.matchesAllOf(pattern) ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setHeaderText("Error - Non-ASCII character identified: " + pattern);
            alert.setContentText("The Quick Search algorithm works with a " +
                    "specific charset and currently accepts only ASCII characters");
            alert.showAndWait();
            return false;
        }

        super.setPatternText(pattern);

        if ( !pattern.isEmpty() ){
            this.createLastCharArray();
            this.populateLists();
        }

        return true;
    }

    /**********************************************************************
     * Loads the algorithm data in the listViews (JAR FILE CHANGE)
     *********************************************************************/
    private void loadAlgoData(){
        ObservableList<String> algoData = FXCollections.observableArrayList(
            "- simplification of the Boyer-Moore algorithm (easy to implement)",
            "- uses only the bad-character shift",
            "- bases shift on the text character just following ",
            "  the rightmost text character of the current comparison window",
            "- involves pre-processing the string to record the position",
            "  of the last occurrence of each character c in the alphabet",
            "- therefore the alphabet must be fixed in advance",
            "",
            "Time Complexity: O(mn)",
            "- m size of string and n size of text",
            "- pre-processing is O(m)",
            "- main algorithm is O(mn)",
            "",
            "Very fast in practice for short patterns and large alphabets"
        );

        ObservableList<String> algoCode = FXCollections.observableArrayList(
                "// length of string (m) and text (n)",
                "int m = string.length, n = text.length;",
                "// starting position in text t",
                "int sp = 0;",
                "// current position in text (i) and string (j)",
                "int i = 0, j = 0;",
                "// create and set up the last occurrence array of string",
                "int [] lastOccurQs = createLastOccurArrayQS(string);",
                "// while we have not reached the end of text or string",
                "while ( ( sp <= n-m ) && ( j < m ) ) {",
                "   if (text[i] == string[j]){ // chars in text and string match",
                "     i++;     // move on in text ",
                "     j++;     // move on in string",
                "   } ",
                "   else { // a mismatch between text and string",
                "     j = 0;   // start again in string",
                "     sp += lastOccurQs[ t[sp + m] ]; // advance starting position in text",
                "     i = sp;  // start in text from new starting position",
                "   }",
                "}",
                "if (j == m) // reached the end of string",
                "   return sp; // occurrence of string found starting from sp ",
                "else // reached the end of text",
                "   return -1; // no occurrence of string found"
        );

        ObservableList<String> algoTable = FXCollections.observableArrayList(
            "Last Occurrence Array for Quick Search Algorithm",
            "- we assume an array called lastOccurQs indexed by characters ",
            "  of the underlying alphabet of the text",
            "  of the text lastOccurQs[c] records the position in the string",
            "  of the last occurrence of char c if the character c",
            "  is absent from the string s, then let lastOccurQs[c] = m + 1",
            "- on finding a mismatch on char c of the pattern,",
            "  there is a jump step in the algorithm of lastOccurQs[c]"
        );


        tableView.setItems(algoTable);
        // Disable User selection
        tableView.setFocusModel(null);
        codeView.setItems(algoCode);
        infoView.setItems(algoData);
    }
}
