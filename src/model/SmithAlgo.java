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
 * The Smith Algorithm Animation
 ***************************************************/
public class SmithAlgo extends Visualisable {
    private ListView indexList,failList, patternList, tableView;
    protected static final int CELL_A_WIDTH = 100;
    private Pane backPane;
    private int[] qsTable, bmTable;


    /****************************************************
     * Default Constructor
     ***************************************************/
    public SmithAlgo( Rectangle textRectA, Rectangle patternRectA, Rectangle textRectB, Rectangle patternRectB,
                      Label patternLabel, ListView codeView, ListView infoView, ListView indexList, ListView patternList, ListView failList, Pane backPane, ListView tableInfoView ) {
        super(textRectA, patternRectA, textRectB, patternRectB, patternLabel, codeView, infoView);

        this.indexList = indexList;
        this.patternList = patternList;
        this.failList = failList;
        this.backPane = backPane;

        this.tableView = tableInfoView;
        loadAlgoData();

//        // Populate ListViews with the appropriate data
//        loadAlgoData(SM_TXT);
//        // Populate the tableInfo ListView
//        Path tableInfoPath = Paths.get(DATA_ADDRESS + SM_TXT + "TableInfo.txt");
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
        int sp = 0, i = 0, j = 0;
        // Reset result
        result = -1;
        if (m == 0 || n == 0) // If there is nothing to search for
            return animSequence;

        // While pattern hasn't been found and not reached end of text/string
        while ( sp <= n-m && j < m ) {

            animSequence.getChildren().add( highlightCodeRow( HALF_SEC, 14 ) );

            if (text[i] == pattern[j]) { // if chars match

                animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC , 15,16 ), hideOrangeRect() );

                // Show the other Rectangles
                if (j == 0) animSequence.getChildren().add( showGreenRect() );
                // else increase the size to highlight one more character
                else animSequence.getChildren().add( setRectBWidth( j + 1 ) );
                // Pause for half a second
                animSequence.getChildren().add( new PauseTransition( ONE_SEC ) );

                i++; j++; // move on in text and pattern

                if (sp <= n - m && j < m) {
                    animSequence.getChildren().addAll(
                            highlightCodeRow( HALF_SEC,12,13 ),
                            moveOrangeRect(rectWidth),
                            new PauseTransition(HALF_SEC)
                    );
                } else {
                    animSequence.getChildren().add(highlightCodeRow( HALF_SEC, 26 ));
                    if (j == m ) animSequence.getChildren().add( highlightCodeRow( HALF_SEC, 26, 27) );
                    else animSequence.getChildren().add( highlightCodeRow( HALF_SEC, 28, 29) );
                }
            }
            else { // if there is a mismatch
                // Add the transitions to the general sequence
                animSequence.getChildren().addAll(
                        highlightCodeRow( HALF_SEC, 18 ),
                        highlightCodeRow( HALF_SEC, 19,20,21,22,23),
                        mismatchTransition() );
                if (j > 0) animSequence.getChildren().addAll(moveOrangeRect(-j * rectWidth));

                int jump;
                if ( sp + m >= text.length){ // check that there won't be an indexOutOfBounds Exception
                    jump = bmTable[ text[sp + m - 1] ];
                } else {
                    jump = Math.max(qsTable[ text[ sp + m ] ], bmTable[ text[sp + m - 1] ] );
                }

                animSequence.getChildren().addAll(
                        new ParallelTransition(
                                highlightCodeRow( HALF_SEC,12,13 ),
                                moveRectsA( rectWidth * jump, ONE_SEC ),
                                moveRectsB( rectWidth * jump, ONE_SEC ),
                                moveLabel( rectWidth * jump, ONE_SEC ) ),
                        setRectBWidth( 1 ) );

                j = 0; // start again in string
                sp += jump; // advance search index
                i = sp; // back up in text to new search index
            }
        }
        // Store the result
        result = ( j == m ? sp : -1 );

        return animSequence;
    }

    /*****************************************************************************************
     * If c is in P, qsTable(c) is equal to the patternLength minus the index of the
     * qsTable (right-most) occurrence of c in P.
     * Otherwise, we can conventionally define qsTable(c) = patternLength + 1
     ****************************************************************************************/
    private void createQSArray() {

        int m = pattern.length;
        qsTable = new int[256];

        // If a character doesn't exist in P
        for (int i = 0; i < 256; i++)
            qsTable[i] = m + 1;
        // If it does, calculate its qsTable occurrence
        for (int i = 0; i < m; i++)
            qsTable[ pattern[i] ] = m - i;
    }

    /*****************************************************************************************
     * Horspool shift function
     ****************************************************************************************/
    private void createHPArray() {

        bmTable = new int[256];
        int m = pattern.length;

        // If a character doesn't exist in P
        for (int i = 0; i < 256; i++)
            bmTable[i] = m;
        for (int i = 0; i < m - 1; i++)
            bmTable[ pattern[i] ] = m - i - 1;
    }


    /*************************************************************************
     * Populate the lists
     ************************************************************************/
    private void populateLists() {
        ObservableList<String> charRow = FXCollections.observableArrayList(" Char c ");
        ObservableList<String> quickSRow = FXCollections.observableArrayList(" QS[c]  ");
        ObservableList<String> horspoolRow = FXCollections.observableArrayList(" HP[c]  ");

        int m = pattern.length;
        for ( int i = 0; i < 256; i++ ) {
            if ( !(qsTable[i] > m) ) {
                charRow.add((char) (i) + "");
                quickSRow.add(qsTable[i] + "");
                horspoolRow.add(bmTable[i] + "");
            }
        }

        // For the rest qsTable(c) = patternLength + 1
        charRow.add("REST");
        quickSRow.add(" " + (m + 1) + " ");
        horspoolRow.add(" " + m + " ");


        // Populate lists
        indexList.setItems(charRow);
        patternList.setItems(quickSRow);
        failList.setItems(horspoolRow);
        // Disable user Selection/Focus
        indexList.setFocusModel(null);
        patternList.setFocusModel(null);
        failList.setFocusModel(null);

        int cellsWidth = 20 + CELL_A_WIDTH + ( CELL_WIDTH * charRow.size() );
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
     * @return boolean - succesful or not
     ***************************************************/
    @Override public boolean setPatternText( String pattern ) {

        // Ensure that only ASCII Characters are used
        if (!CharMatcher.ASCII.matchesAllOf(pattern) ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setHeaderText("Error - Non-ASCII character identified: " + pattern);
            alert.setContentText("Smith's algorithm works with a " +
                    "specific charset and currently accepts only ASCII characters");
            alert.showAndWait();
            return false;
        }

        super.setPatternText(pattern);

        if ( !pattern.isEmpty() ){
            this.createQSArray();
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
                "- Smith noticed in Quick Search when computing a shift",
                "  the text character just next to the rightmost text",
                "  character of the current comparison window can give",
                "  a shorter shift than using the rightmost text character",
                "- for this reason the algorithm takes the maximum of",
                "  the two shifts",
                "- therefore the last text character currently being",
                "  compared with the string and the character following",
                "  this in the text are used to decide the next comparison",
                "- the algorithm takes the maximum shift cased by these",
                "  two characters according to the Horspool (and BM)",
                "  shift function (right most char) and the Quick Search",
                "  shift function (char following rightmost)",
                "  ",
                "- the algorithm involves pre-processing the string",
                "  to construct two arrays lastOccur and lastOccurQs",
                "- the alphabet must be fixed in advance",
                "",
                "Time Complexity",
                "- m size of string and n size of text",
                "- pre-processing is O(m)",
                "- main algorithm is O(mn)\n"
        );

        ObservableList<String> algoCode = FXCollections.observableArrayList(
            "// length of string (m) and text (n)",
            "int m = string.length, n = text.length; ",
            "// starting position in text t",
            "int sp = 0; ",
            "// current position in text(i) and string(j)",
            "int i = 0, j = 0;",
            "// preprocessing",
            "// set up Bad Character Shift Array",
            "int [] lastOccur = createHPArray(string);",
            "// set up Last Occurrence Array",
            "int [] lastOccurQs = createQSArray(string);",
            "",
            "// while we have not reached the end of text or string",
            "while ( sp <= n-m && j<m ) {",
            "    if (text[i] == string[j]) { // chars in text and string match",
            "      i++; // move on in text",
            "      j++; // move on in string",
            "    }",
            "    else { // if there is a mismatch",
            "        j = 0; // start again in string",
            "        // advance starting position ",
            "        sp += Math.max(lastOccur[ text[ sp+m-1 ] ],",
            "                        lastOccurQs[ text[ sp+m ] ]);",
            "        i = sp; // back up in text to new search index",
            "    }",
            "}",
            "if ( j==m ) // reached the end of string",
            "   return sp; // occurrence of string found starting from sp",
            "else // reached the end of text",
            "   return -1; // no occurrence of string found"
        );

        ObservableList<String> algoTable = FXCollections.observableArrayList(
            "lastOccur - last occurence array (from BM and Horspoool)",
            "- we assume an array called lastOccur indexed by",
            "  characters of the underlying alphabet of the text",
            "- lastOccur[c] records the position in the string",
            "  of the last occurrence of character c",
            "- if the character c is absent from the",
            "  string s, then lastOccur[c] = -1",
            "",
            "lastOccurQs - Last Occurrence Array for Quick Search algorithm",
            "- we assume an array called QS indexed by characters of the ",
            "  underlying alphabet of the text",
            "- lastOccurQs[c] records the position in the string",
            "  of the last occurrence of char c ",
            "- if the character c is absent from the string s,",
            "  then lastOccurQs[c] = m+1 where m is the length",
            "  of the string"
        );


        tableView.setItems(algoTable);
        // Disable User selection
        tableView.setFocusModel(null);
        codeView.setItems(algoCode);
        infoView.setItems(algoData);
    }
}
