package model;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.nio.file.Path;
import java.nio.file.Paths;

/****************************************************
 * The KMP Algorithm Animation
 ***************************************************/
public class KMPAlgo extends Visualisable {

    private ListView indexList, patternList, failList, tableView;
    protected static final int CELL_A_WIDTH = 75;
    private Pane backPane;
    private int [] failArray;

    /****************************************************
     * Default Constructor
     ***************************************************/
    public KMPAlgo( Rectangle textRectA, Rectangle patternRectA, Rectangle textRectB, Rectangle patternRectB,
                    Label patternLabel, ListView codeView, ListView infoView, ListView indexList, ListView patternList, ListView failList, Pane backPane, ListView tableInfoView ) {
        super(textRectA, patternRectA, textRectB, patternRectB, patternLabel, codeView, infoView);

        this.indexList = indexList;
        this.patternList = patternList;
        this.failList = failList;
        this.backPane = backPane;

        this.tableView = tableInfoView;
        loadAlgoData();

//        // Populate ListViews with the appropriate data
//        loadAlgoData(KMP_TXT);
//        // Populate the tableInfo ListView
//        Path tableInfoPath = Paths.get(DATA_ADDRESS + KMP_TXT + "TableInfo.txt");
//        tableInfoView.setItems(getData(tableInfoPath));
//        // Disable User selection
//        tableInfoView.setFocusModel(null);
    }

    public SequentialTransition getAnimation() {
        // The centralAnimation which will store all sub-Animations
        SequentialTransition animSequence = new SequentialTransition();
        // Initialize X-coordinates of the Rectangles
        this.setXCoordinates();
        // length of pattern and text
        int m = pattern.length, n = text.length;
        // current position in text and pattern
        int i = 0, j = 0;
        // Reset result
        result = -1;
        if (m == 0 || n == 0) // If there is nothing to search for
            return animSequence;

        while ( i < n && !(i + (m - j) > n) ) {

            animSequence.getChildren().addAll( highlightCodeRow( ZERO, 6,7 ), new PauseTransition( HALF_SEC ) );

            if (text[i] == pattern[j]) { // if chars match

                animSequence.getChildren().addAll(
                    highlightCodeRow( HALF_SEC, 8 ),
                    highlightCodeRow( HALF_SEC, 9, 10, 11 ),
                    hideOrangeRect()
                );

                if (j == 0) // Show the green Rectangles and pause for half indexList second
                    animSequence.getChildren().addAll(
                           setRectBWidth( 1 ),
                           showGreenRect(),
                           new PauseTransition( HALF_SEC )
                    );
                else if (j < m)  // else double the size
                    animSequence.getChildren().addAll(
                           setRectBWidth( j + 1 ),
                           new PauseTransition( HALF_SEC )
                    );

                if (j != ( m - 1 ) && j < n) // if match not found and text not finished
                    animSequence.getChildren().addAll(
                           moveOrangeRect( rectWidth ),
                           new PauseTransition( HALF_SEC )
                    );

                if ( j == ( m - 1 ) ) {
                    animSequence.getChildren().add( highlightCodeRow( HALF_SEC, 12 ) );
                    result = i - m + 1;
                    break;
                }

                i++; // move on in text
                j++; // move on in string
            }
            else  {

                animSequence.getChildren().addAll(
                        highlightCodeRow( HALF_SEC, 13 ),
                        highlightCodeRow(HALF_SEC, 14)
                );
                if( j > 0 ) { // no match, but we have advance in the pattern

                    double step = ( j - failArray[j-1] );
                    animSequence.getChildren().addAll(
                            highlightCodeRow( HALF_SEC, 14, 15 ),
                            highlightCodeRow( HALF_SEC, 16,17 ),
                            mismatchTransition(),
                            setRectBWidth(failArray[j - 1]),
                            new ParallelTransition( moveRectsB(step * rectWidth, ONE_SEC),
                                    moveLabel(step * rectWidth, ONE_SEC ) ),
                            showGreenRect(),
                            new PauseTransition( HALF_SEC ) );

                    j = failArray[j-1];
                }
                else {
                    animSequence.getChildren().addAll(
                            highlightCodeRow( HALF_SEC, 14, 18 ),
                            highlightCodeRow( HALF_SEC, 19,20 ),
                            new ParallelTransition(
                                    mismatchTransition(),
                                    moveRectsA(rectWidth, ONE_SEC),
                                    moveRectsB(rectWidth, ONE_SEC),
                                    moveLabel( rectWidth, ONE_SEC )
                            ),
                            setRectBWidth(1)
                    );
                    i++;
                }
            }
        }
        if( result < 0 ) animSequence.getChildren().add( highlightCodeRow( HALF_SEC,23 ) );

        return animSequence;
    }

    /*****************************************************************************
     * Creates the failure function f for the pattern, which maps j to
     * the length of the longest prefix of P that is indexList suffix of P[1..j]
     * @return int[] f - the failure function
     ****************************************************************************/
    private void createFailureFucntion() {
        failArray = new int[pattern.length];
        failArray[0] = 0;
        int i = 1, j = 0;

        while ( i < pattern.length )
            if ( pattern[j] == pattern[i] ){ // we have matched j+1 characters
                failArray[i] = j+1;
                i++;
                j++;
            } else if( j > 0 ) {
                j = failArray[j-1];
            } else {
                failArray[i] = 0;
                i++;
            }
    }

    /*************************************************************************
     * Populate the list with the KMP algorithm's failure function
     ************************************************************************/
    private void populateLists() {
        ObservableList<String> indexRow = FXCollections.observableArrayList("   j  ");
        ObservableList<String> patternRow = FXCollections.observableArrayList(" P[j] ");
        ObservableList<String> failRow = FXCollections.observableArrayList(" b[j] ");

        for ( int i = 0; i < pattern.length; i++ ) {
            indexRow.add( i + "" );
            patternRow.add( pattern[i] + "" );
            failRow.add( failArray[i] + "" );
        }

        // Populate lists
        indexList.setItems(indexRow);
        patternList.setItems(patternRow);
        failList.setItems(failRow);
        // Disable user Selection/Focus
        indexList.setFocusModel(null);
        patternList.setFocusModel(null);
        failList.setFocusModel(null);

        int cellsWidth = CELL_A_WIDTH + ( CELL_WIDTH * pattern.length );
        // Adjust the width of the lists based on the length of the pattern
        indexList.setPrefWidth( cellsWidth );
        patternList.setPrefWidth( cellsWidth );
        failList.setPrefWidth( cellsWidth );
        backPane.setPrefWidth( PANE_PADDING + cellsWidth );

        indexList.setStyle( VISIBLE );
        patternList.setStyle( VISIBLE );
        failList.setStyle( VISIBLE );
    }

    /****************************************************
     * Set the pattern text and populate the list
     * @param pattern - the search text
     ***************************************************/
    @Override public boolean setPatternText( String pattern ) {
        super.setPatternText(pattern);
        if ( !pattern.isEmpty() ){
            this.createFailureFucntion();
            this.populateLists();
        }
        return true;
    }

    /**********************************************************************
     * Loads the algorithm data in the listViews (JAR FILE CHANGE)
     *********************************************************************/
    private void loadAlgoData(){
        ObservableList<String> algoData = FXCollections.observableArrayList(
            "- KMP is an on-line algorithm",
            "- no need to back-up in the text",
            "- involves pre-processing the string",
            "  to build a border table",
            "",
            "Time Complexity: O(m + n)",
            "- m size of string",
            "- n size of text",
            "- pre-processing is O(m)",
            "- main algorithm is O(n)"
        );

        ObservableList<String> algoCode = FXCollections.observableArrayList(
            "// length of string (m) and text (n)",
            "int m = string.length(), n = text.length(); ",
            "// current position in text (i) and string (j)",
            "int i = 0, j = 0;",
            "// create and set up the border table of string",
            "int [] borderTable = createBorderTable(string); ",
            "// while we have not reached the end of text or string",
            "while ( i <= n )",
            "   if ( text[i] == string[j] ){ // chars in text and string match",
            "      i++; // move on in text",
            "      j++; // move on in string",
            "      if ( j == m ) // reached end of string",
            "          return i - j; // occurrence of string found starting from i-j",
            "   } else { // a mismatch between text and string",
            "      // adjust string position using the border table",
            "      if ( borderTable[j] > 0 ) { // border exists in string",
            "          // change position in string using longest border",
            "          j = borderTable[j]; ",
            "      } else { // no border in string",
            "          i++;   // advance text position",
            "          j = 0; // start again in string",
            "      }",
            "   }",
            "return -1; // no occurrence of string found"
        );

        ObservableList<String> algoTable = FXCollections.observableArrayList(
            "The border table",
            "- a border of a string is a substring that is both",
            "  a prefix and a suffix and cannot be the string itself",
            "- the border table borderTable is an array which has",
            "  the same size as the string and borderTable[j] equals",
            "  the length of the longest border of the substring string[0..j-1]",
            "- if we get a mismatch at position j in the string",
            "  we remain on the current text character and the border",
            "  table tells us which string character should next be",
            "  compared with the current text character"
        );


        tableView.setItems(algoTable);
        // Disable User selection
        tableView.setFocusModel(null);
        codeView.setItems(algoCode);
        infoView.setItems(algoData);
    }
}
