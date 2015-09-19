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
import javafx.util.Duration;

import java.nio.file.Path;
import java.nio.file.Paths;

/****************************************************
 * The Horspool Algorithm Animation
 ***************************************************/
public class HorspoolAlgo extends Visualisable {
    private ListView indexList, patternList, tableView;
    protected static final int CELL_A_WIDTH = 100;
    private Pane backPane;
    private int[] hp;


    /****************************************************
     * Default Constructor
     ***************************************************/
    public HorspoolAlgo( Rectangle tA, Rectangle pA, Rectangle tB, Rectangle pB, Label label, ListView codeView, ListView infoView,
                           ListView indexList, ListView patternList, Pane backPane, ListView tableInfoView) {
        super(tA, pA, tB, pB, label, codeView, infoView);

        this.indexList = indexList;
        this.patternList = patternList;
        this.backPane = backPane;

        tableView = tableInfoView;
        loadAlgoData();
//        // Populate ListViews with the appropriate data
//        loadAlgoData(HP_TXT);
//        // Populate the tableInfo ListView
//        Path tableInfoPath = Paths.get(DATA_ADDRESS + HP_TXT + "TableInfo.txt");
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
        int i = m-1, j = m-1;
        int sp = 0; // current starting position of string in text
        // Reset result
        result = -1;
        if (m == 0 || n == 0) // If there is nothing to search for
            return animSequence;

        // Move the orange rectangles at the end of the string
        animSequence.getChildren().addAll(
                new ParallelTransition(
                        moveRectsA(rectWidth * i, Duration.ONE),
                        moveRectsB(rectWidth * i, Duration.ONE)
                ),
                highlightCodeRow(ZERO, 8,9),
                new PauseTransition(ONE_SEC)
        );

        while (sp <= n-m && j >= 0) {

            if ( text[i] == pattern[j] ) { // current characters match

                animSequence.getChildren().addAll(
                        highlightCodeRow( HALF_SEC, 10 ),
                        highlightCodeRow( HALF_SEC, 11,12 ),
                        hideOrangeRect()
                );

                if ( j == ( m - 1 ) ) // Show the green Rectangles and pause for half indexList second
                    animSequence.getChildren().addAll(
                            setRectBWidth( 1 ),
                            showGreenRect(),
                            new PauseTransition( HALF_SEC )
                    );
                else if ( j < ( m - 1 ) )  // else double the size
                    animSequence.getChildren().addAll(
                            setRectBWidth( m - j ),
                            moveRectsB(-rectWidth, Duration.ONE),
                            new PauseTransition( HALF_SEC )
                    );

                if ( j != 0 && j < n ) // if match not found and text not finished
                    animSequence.getChildren().addAll(
                            moveOrangeRect( -rectWidth ),
                            new PauseTransition( HALF_SEC )
                    );

                i--; // move back in text
                j--; // move back in string
            }
            else { // current characters do not match

                int spIncrement = hp[text[sp + (m-1)]];

                animSequence.getChildren().addAll(
                        highlightCodeRow( HALF_SEC, 14 ),
                        highlightCodeRow( HALF_SEC, 15,16,17,18 ),
                        mismatchTransition(), // Make Orange Rect Red for 0.5s, and make it orange again
                        new ParallelTransition(
                                moveRectsBasedLabel( (spIncrement + m-1) * rectWidth, HALF_SEC ),
                                moveLabel( spIncrement * rectWidth, ONE_SEC )
                        ),
                        setRectBWidth(1)
                );

                sp += spIncrement;
                i += spIncrement + m - 1 - j;
                j = m-1; // return to end of string
            }

            animSequence.getChildren().addAll( highlightCodeRow( ZERO, 8,9 ), new PauseTransition( HALF_SEC ) );
        }

        animSequence.getChildren().add(highlightCodeRow(HALF_SEC, 21));
        if (j < 0) { // occurrence found yes/no
            animSequence.getChildren().add( highlightCodeRow( HALF_SEC, 21,22 ) );
            result = sp;
        }
        else {
            animSequence.getChildren().add( highlightCodeRow( HALF_SEC, 23,24 ) );
            result = -1;
        }

        return animSequence;
    }

    /*****************************************************************************************
     * If c is in P, hp(c) is the index of the hp (right-most) occurence of c in (P-1).
     * Otherwise, we can conventionally define hp(c) = pattern.length
     ****************************************************************************************/
    private void createHPArray() {

        hp = new int[256];
        int m = pattern.length;

        for (int i = 0; i < 256; i++)
            hp[i] = m;

        for (int i = 0; i < m - 1; i++)
            hp[ pattern[i] ] = m - i - 1;
    }


    /*************************************************************************
     * Populate the list with the BoyerMoore algorithm's lastChar array
     ************************************************************************/
    private void populateLists() {
        ObservableList<String> charRow = FXCollections.observableArrayList("  Char c ");
        ObservableList<String> lastIndexRow = FXCollections.observableArrayList( "  HP[c]  " );

        int m = pattern.length;

        for ( int i = 0; i < 256; i++ ) {
            if ( hp[i] != m ) {
                charRow.add((char) (i) + "");
                lastIndexRow.add(hp[i] + "");
            }
        }

        // For the rest hp(c) = -1
        charRow.add("REST");
        lastIndexRow.add(" " + m + " ");

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

        backPane.setPrefWidth(PANE_PADDING + cellsWidth);
        backPane.setPrefHeight(100);

        indexList.setStyle(VISIBLE);
        patternList.setStyle( VISIBLE );
    }

    /****************************************************************************
     * Set the pattern text and populate the list if text is allowed
     * @param pattern - the search text
     * @return boolean - succesful or not
     ***************************************************************************/
    @Override public boolean setPatternText( String pattern ) {

        // Ensure that only ASCII Characters are used
        if (!CharMatcher.ASCII.matchesAllOf(pattern) ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setHeaderText("Error - Non-ASCII character identified: " + pattern);
            alert.setContentText("The Horspool algorithm works with a " +
                    "specific charset and currently accepts only ASCII characters");
            alert.showAndWait();
            return false;
        }

        super.setPatternText(pattern);

        if ( !pattern.isEmpty() ){
            this.createHPArray();
            this.populateLists();
        }

        return true;
    }

    /**********************************************************************
     * Loads the algorithm data in the listViews (JAR FILE CHANGE)
     *********************************************************************/
    private void loadAlgoData(){
        ObservableList<String> algoData = FXCollections.observableArrayList(
"- simplification of the Boyer-Moore algorithm",
        "- as for the BM algorithm, the string and text",
        "  are scanned right-to-left",
        "- the last text character currently being compared",
        "  with the string is used to decide the next",
        "  comparison (this is the difference from Boyer Moore",
        "  which uses the last character of the text involved",
        "  in a mismatch)",
        "- involves pre-processing the string to record the position",
        "  of the last occurrence of each character in the alphabet",
        "- therefore the alphabet must be fixed in advance",
        "",
        "Time Complexity:  O(mn)",
        "- m size of string and n size of text",
        "- pre-processing is O(m+n)",
        "- main algorithm is O(mn)"
        );

        ObservableList<String> algoCode = FXCollections.observableArrayList(
"// length of string (m) and text (n)",
        "int m = string.length, n = text.length; ",
        "// starting position in text t",
        "int sp = 0; ",
        "// current position in text (i) and string (j)",
        "int i = m - 1, j = m - 1;",
        "// create and set up the horspool shift array of string",
        "int [] hp = lastOccur(string);",
        "// while we have not reached the end of text or string",
        "while ( sp <= n-m && j >= 0 ) {",
        "   if ( t[i] == s[j] ){ // chars in text and string match",
        "       i--; // move back in text",
        "       j--; // move back in string ",
        "   } ",
        "   else { // a mismatch between text and string ",
        "       // adjust string position using the horspool shift array",
        "       sp += lastOccur[ text[sp + (m-1)] ]; // update starting position",
        "       i += lastOccur[ text[sp + (m-1)] ] + m - 1 - j; // update position in text",
        "       j = m - 1;   // return to the end of the string ",
        "   }",
        "}",
        "if ( j < 0 ) // reached the end of string",
        "   return sp; // occurrence of string found starting from sp",
        "else // reached the end of text",
        "   return -1; // no occurrence of string found"
        );

        ObservableList<String> algoTable = FXCollections.observableArrayList(
"Last Occurrence Array (as in Boyer Moore)",
        "- we assume an array called lastOccur indexed by",
        "  characters of the underlying alphabet of the text",
        "- lastOccur[c] records the position in the string",
        "  of the last occurrence of character c",
        "- if the character c is absent from the string s,",
        "  then lastOccur[c] = -1",
        "- on finding a mismatch on char c of the string,",
        "  there is a jump step in the algorithm",
        "- the algorithm ‘slides the string along’ so that",
        "  the last character in text currently being compared",
        "  with the string (i.e. character sp+m of the text)",
        "  matches the last position of this character in the string",
        "- if this moves the string in the ‘wrong direction’,",
        "  instead the string is moved one position right",
        "- if the character does not appear in the string, instead ",
        "  move the string passed the current character in the text"
        );


        tableView.setItems(algoTable);
        // Disable User selection
        tableView.setFocusModel(null);
        codeView.setItems(algoCode);
        infoView.setItems(algoData);
    }
}
