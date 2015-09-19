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
import javafx.util.Duration;

import java.nio.file.Path;
import java.nio.file.Paths;

/****************************************************
 * The Two Way Algorithm Animation
 ***************************************************/
public class TwoWayAlgo extends Visualisable {

    private ListView maxSufList,periodList, isSuffixList, tableView;
    protected static final int CELL_A_WIDTH = 100;
    private Pane backPane;

    private int[] limits1;
    private int[] limits2;

    /****************************************************
     * Constructor
     *
     * @param textRectA    - search text Rectangle (trailing)
     * @param patternRectA - pattern Rectangle (trailing)
     * @param textRectB    - search text Rectangle (leading)
     * @param patternRectB - pattern Rectangle (leading)
     * @param patternLabel - the pattern label
     * @param codeView
     * @param infoView
     ***************************************************/
    public TwoWayAlgo(Rectangle textRectA, Rectangle patternRectA, Rectangle textRectB, Rectangle patternRectB, Label patternLabel, ListView codeView, ListView infoView, ListView maxSufList, ListView periodList, ListView isSuffixList, Pane backPane, ListView tableInfoView) {
        super(textRectA, patternRectA, textRectB, patternRectB, patternLabel, codeView, infoView);


        this.maxSufList = maxSufList;
        this.periodList = periodList;
        this.isSuffixList = isSuffixList;
        this.backPane = backPane;


        this.tableView = tableInfoView;
        loadAlgoData();

//        // Populate ListViews with the appropriate data
//        loadAlgoData(TW_TXT);
//        // Populate the tableInfo ListView
//        Path tableInfoPath = Paths.get(DATA_ADDRESS + TW_TXT + "TableInfo.txt");
//        tableInfoView.setItems(getData(tableInfoPath));
//        // Disable User selection
//        tableInfoView.setFocusModel(null);
    }


    public SequentialTransition getAnimation(){

        // The centralAnimation which will store all sub-Animations
        SequentialTransition animSequence = new SequentialTransition();
        // Initialize X-coordinates of the Rectangles
        this.setXCoordinates();
        // length of pattern (m) and text (n)
        int m = pattern.length, n = text.length;
        // Reset result
        result = -1;
        if (m == 0 || n == 0) // If there is nothing to search for
            return animSequence;

        int sp = 0; // starting position in text
        int j; // position in pattern
        // find the maximum suffix and period
        int l,period;
        // choose max suffix from the two orderings
        if (limits1[0] > limits2[0]){
            l = limits1[0]; period = limits1[1];
        } else {
            l = limits2[0]; period = limits2[1];
        }

        // next check if first part of pattern (up to l-1) is a suffix of the second
        boolean isSuffix = false;

        if ( l < (n/2) ) { // need first part to be smaller
            int k = l; // then check if it is a suffix
            while ( k >= 0 && pattern[k] == pattern[ ( m-1 ) - ( l-k) ] ) { k--; }
            if ( k < 0 ) isSuffix = true; // is a suffix
        }

        // Move the orange rectangles at the beginning of the suffix
        animSequence.getChildren().addAll(highlightCodeRow(HALF_SEC, 25), new ParallelTransition(moveRectsA(rectWidth * (l + 1), Duration.ONE), moveRectsB(rectWidth * (l + 1), Duration.ONE)), new PauseTransition(ONE_SEC));

        if (isSuffix)
        { // first part of pattern is suffix of second
            int s = -1; // memory used for new starting position
            // while we have not reached the end of text and a result hasn't been found
            while ( sp <= n-m && result < 0 ) {
                animSequence.getChildren().add(highlightCodeRow( HALF_SEC, 27,28 ));
                //--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->SEARCH FORWARDS
                j = Math.max(l,s) + 1; // starting position in pattern
                while ( j < m && pattern[j] == text[sp+j] ) { // check current chars
                    // Hide the orange rectangles
                    animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 30,31 ), hideOrangeRect() );
                    // Show the other green Rectangles
                    if (j == (Math.max(l,s) + 1))
                        animSequence.getChildren().add( showGreenRect() );
                    // else increase the size to highlight one more character
                    else animSequence.getChildren().add( incrementRectBWidth() );
                    // Pause for half a second
                    animSequence.getChildren().add( new PauseTransition( ONE_SEC ) );
                    j++; // move to next position in pattern

                    if ( j < m )
                        animSequence.getChildren().addAll(
                                highlightCodeRow(HALF_SEC,32),
                                moveOrangeRect(rectWidth),
                                new PauseTransition(HALF_SEC)
                        );
                } //--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->SEARCH FORWARDS

                animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 33,34 ) );

                // if end of pattern reached
                if (j >= m) {
// <-----<-----<-----<-----<-----<-----<-----<------<------<-----<------<------<-----<------<------<-----SEARCH BACKWARDS
                    j = l; // starting position in pattern

                    animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 35), moveOrangeRect( ( l - (m-1) ) * rectWidth ), new PauseTransition( HALF_SEC ) );

                    while ( j > s && pattern[j] == text[sp+j]) { // check current chars
                        animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 36,37 ), hideOrangeRect(), setRectBWidth( m - j ), moveRectsB(-rectWidth, Duration.ONE), new PauseTransition( HALF_SEC ) );

                        if ( j != 0 && j < m ) // if match not found and text not finished
                        animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 38 ),moveOrangeRect(-rectWidth), new PauseTransition( HALF_SEC ) );

                        j--; // move to previous position in pattern
                    }
// <-----<-----<-----<-----<-----<-----<-----<------<------<-----<------<------<-----<------<------<-----SEARCH BACKWARDS
                    animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 39 ));
                    if (j<=s) { // if match found
                        animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 40 ));
                        result = sp;
                        break;
                    }
// ----x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-------MISMATCH BACKWARDS
                    animSequence.getChildren().addAll( highlightCodeRow(HALF_SEC, 41,42,43 ), mismatchTransition(), new ParallelTransition(moveLabel(period * rectWidth, ONE_SEC ), moveRectsBasedLabel((l + 1) * rectWidth, ONE_SEC ) ), setRectBWidth(1));
                    sp = sp+period; // new starting position in text
                    s = m-period-1; // update memory
                }
                else { // ----x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-------MISMATCH FORWARDS

                    int jump = (j-l);
                    animSequence.getChildren().addAll(highlightCodeRow( HALF_SEC, 45 ),highlightCodeRow( HALF_SEC, 45,46,47 ),mismatchTransition());
                    if ( j > (l+1) )
                        animSequence.getChildren().addAll( new ParallelTransition( moveLabel(jump * rectWidth, ONE_SEC), moveRectsBasedLabel( rectWidth * (l+1), ONE_SEC) ), setRectBWidth(1) );
                    else
                        animSequence.getChildren().addAll( new ParallelTransition( moveRectsA( rectWidth * jump, ONE_SEC ), moveRectsB( rectWidth * jump, ONE_SEC ), moveLabel( rectWidth * jump, ONE_SEC ) ), setRectBWidth(1) );

                    sp = sp + jump; // new starting position in text
                    s = -1; // update memory
                } // end if

            } // end of while loop
            animSequence.getChildren().addAll(highlightCodeRow( HALF_SEC, 74 ));
        } else { // first part of pattern is not a suffix
// ----------------------------------------------------------------------------------------------------------------------------------------------
// ----------------------------------------------------------------------------------------------------------------------------------------------
            period = Math.max(l+1, m-(l+1)) + 1; // value of period
            animSequence.getChildren().add(highlightCodeRow(HALF_SEC, 51));

            // while we have not reached the end of text and a result hasn't been found
            while ( sp <= n-m && result < 0 ) {
                animSequence.getChildren().add(highlightCodeRow( HALF_SEC, 53,54 ));
                // search forwards...--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->
                j = l+1; // start from position l+1 in pattern
                while ( j < m && pattern[j] == text[sp+j]) { // check current chars
                    animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 56,57 ), hideOrangeRect() );
                    if (j == (l + 1)) animSequence.getChildren().add( showGreenRect() );
                    else animSequence.getChildren().add( incrementRectBWidth() );
                    animSequence.getChildren().add( new PauseTransition( ONE_SEC ) );

                    j++; // move to next position in pattern

                    if ( j < m ) animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 58 ),moveOrangeRect(rectWidth), new PauseTransition(HALF_SEC) );
                } // finished forwards search...--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->--->

                animSequence.getChildren().addAll(
                        highlightCodeRow( HALF_SEC, 59,60 )
                );

                if (j >= m)
                { // reached end of pattern so search backwards
                    // search backwards...<-----<------<------<-----<------<------<-----<------<------<-----<------
                    j = l; // starting position in pattern


                    animSequence.getChildren().addAll(
                            highlightCodeRow( HALF_SEC, 61 ),
                            moveOrangeRect( ( l - (m-1) ) * rectWidth ),
                            new PauseTransition( HALF_SEC )
                    );

                    while ( j >= 0 && pattern[j] == text[sp+j]) { // check current chars
                        animSequence.getChildren().addAll(highlightCodeRow( HALF_SEC, 62,63 ), hideOrangeRect(), moveRectsB(-rectWidth, Duration.ONE), setRectBWidth( m - j ), new PauseTransition( HALF_SEC ) );

                        if ( j != 0 && j < m ) // if match not found and text not finished
                            animSequence.getChildren().addAll(
                                    highlightCodeRow( HALF_SEC, 64 ),
                                    moveOrangeRect(-rectWidth),
                                    new PauseTransition( HALF_SEC )
                            );

                        j--; // move to previous position in pattern
                    }// finished searching backwards...<-----<------<------<-----<------<------<-----<------<------

                    animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 65 ));
                    if ( j < 0 ) { // reached start of pattern so a match
                        animSequence.getChildren().addAll( highlightCodeRow( HALF_SEC, 65,66 ));
                        result = sp; break;
                    }
// ----x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-------MISMATCH BACKWARDS
                    animSequence.getChildren().addAll(highlightCodeRow( HALF_SEC, 67,68 ),mismatchTransition(), new ParallelTransition( moveLabel( period * rectWidth, ONE_SEC ), moveRectsBasedLabel( (l+1) * rectWidth, ONE_SEC ) ), setRectBWidth(1));
                    sp = sp + period; // new starting position in text
                }
                else { // ----x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-x-------MISMATCH FORWARDS
                    int jump = (j-l);
                    animSequence.getChildren().addAll(highlightCodeRow( HALF_SEC, 70,71 ),mismatchTransition());
                    if (j > (l+1))
                        animSequence.getChildren().addAll( new ParallelTransition( moveLabel( jump * rectWidth, ONE_SEC ), moveRectsBasedLabel( rectWidth * (l+1), ONE_SEC) ), setRectBWidth(1) );
                    else
                        animSequence.getChildren().addAll( new ParallelTransition( moveRectsA( rectWidth * jump, ONE_SEC ), moveRectsB( rectWidth * jump, ONE_SEC ), moveLabel( rectWidth * jump, ONE_SEC ) ), setRectBWidth(1) );
                    sp = sp + jump; // new starting position in text
                }
            } // end while
            animSequence.getChildren().addAll(highlightCodeRow( HALF_SEC, 74 ));
        } // end else (first part not suffix

        return animSequence;
    }

    /**************************************************************
     * Returns max suffix and period for => ordering
     *************************************************************/
    private int[] maxSuf2() {
        int i = -1, j = 0, k = 1, p = 1;
        char a, b;

        int m = this.pattern.length;

        while ( (j+k) <= m-1 ) {
            b = pattern[i+k];
            a = pattern[j+k];

            // case 1
            if (a > b){
                j = j+k;
                k = 1;
                p = j-i;
            }
            // case 2
            if (a == b){
                if (k == p) {
                    j = j+p;
                    k = 1;
                } else {
                    k = k + 1;
                }
            }
            // case 3
            if (a < b) {
                i = j;
                j = i + 1;
                k = 1;
                p = 1;
            }
        } // end while

        int[] limits = new int[2];
        limits[0] = i;
        limits[1] = p;

        return limits;
    }

    /***************************************************************
     * Returns max suffix and period for <= ordering
     **************************************************************/
    private int[] maxSuf1() {
        int i = -1, j = 0, k = 1, p = 1;
        char a, b;

        int m = this.pattern.length;

        while ( (j+k) <= m-1 ) {

            b = pattern[i+k];
            a = pattern[j+k];

            // case 1
            if (a < b){
                j = j+k;
                k = 1;
                p = j-i;
            }
            // case 2
            if (a == b){
                if (k == p) {
                    j = j+p;
                    k = 1;
                } else {
                    k = k + 1;
                }
            }
            // case 3
            if (a > b) {
                i = j;
                j = i + 1;
                k = 1;
                p = 1;
            }
        } // end while

        int[] limits = new int[2];
        limits[0] = i;
        limits[1] = p;

        return limits;
    }

    /****************************************************
     * Set the pattern text and populate the list
     * @param pattern - the search text
     * @return boolean - successful or not
     ***************************************************/
    @Override public boolean setPatternText( String pattern ) {

        super.setPatternText(pattern);

        if ( !pattern.isEmpty() ){
            limits1 = maxSuf1(); // max suffix for <= ordering
            limits2 = maxSuf2(); // max suffix for => ordering
            this.populateLists();
        }

        return true;
    }

    /*************************************************************************
     * Populate the lists
     ************************************************************************/
    private void populateLists() {
        ObservableList<String> maxSufRow = FXCollections.observableArrayList(" maxSuffix ");
        ObservableList<String> periodRow = FXCollections.observableArrayList("   period  ");
        ObservableList<String> isSuffixRow = FXCollections.observableArrayList("  isSuffix ");


        // find the maximum suffix and period
        int l,period;
        // choose max suffix from the two orderings
        if (limits1[0] > limits2[0]){
            l = limits1[0]; period = limits1[1];
        } else {
            l = limits2[0]; period = limits2[1];
        }

        maxSufRow.add("  "+l);
        periodRow.add("  "+period);

        // next check if first part of pattern (up to l-1) is a suffix of the second
        boolean isSuffix = false;
        int n = text.length; int m = pattern.length;
        if ( l < (n/2) ) { // need first part to be smaller
            int k = l; // then check if it is a suffix
            while ( k >= 0 && pattern[k] == pattern[ ( m-1 ) - ( l-k) ] ) { k--; }
            if ( k < 0 ) isSuffix = true; // is a suffix
        }

        isSuffixRow.add("" + (isSuffix? "true": "false"));

        // Populate lists
        maxSufList.setItems(maxSufRow);
        periodList.setItems(periodRow);
        isSuffixList.setItems(isSuffixRow);
        // Disable user Selection/Focus
        maxSufList.setFocusModel(null);
        periodList.setFocusModel(null);
        isSuffixList.setFocusModel(null);

        int cellsWidth = CELL_A_WIDTH + 4 * CELL_WIDTH;
        // Adjust the width of the lists based on the length of the pattern
        maxSufList.setPrefWidth( cellsWidth );
        periodList.setPrefWidth( cellsWidth );
        isSuffixList.setPrefWidth( cellsWidth );
        backPane.setPrefWidth( PANE_PADDING + cellsWidth );

        maxSufList.setStyle( VISIBLE );
        periodList.setStyle( VISIBLE );
        isSuffixList.setStyle( VISIBLE );
    }

    /**********************************************************************
     * Loads the algorithm data in the listViews (JAR FILE CHANGE)
     *********************************************************************/
    private void loadAlgoData(){
        ObservableList<String> algoData = FXCollections.observableArrayList(
"- can be viewed as an intermediate between the algorithms",
        "  of Knuth, Morris and Pratt and Boyer and Moore",
        "- requires an ordered alphabet",
        "- the pattern is factorized into two parts left and right",
        "- the searching phase consists of comparing",
        "  the characters of the right part from left to right (KMP)",
        "  and then, if no mismatch occurs, in comparing the",
        "  characters of the left part from right to left (BM)",
        "- the most important part is the pre-processing",
        "  phase in choosing a good factorization",
        "- the factorisation is based on finding the",
        "  maximal suffix of the string",
        "",
        "Time Complexity: O(m+n)",
        "- m size of string and n size of text",
        "- pre-processing is O(m)",
        "- main algorithm is O(n)",
        "",
        "Performs 2n-m text character comparisons in the worst case"
        );

        ObservableList<String> algoCode = FXCollections.observableArrayList(
"// length of string (m) and text (n)",
        "int m = string.length, n = text.length;",
        "// find the maximim suffix and period",
        "int l,period;",
        "int[] limits1 = maxSuf1(string,m); // max suffix for <= ordering",
        "int[] limits2 = maxSuf2(string,m); // max suffix for => ordering",
        "",
        "// choose max suffix from the two orderings (gives critical factorisation)",
        "if (limits1[0] > limits2[0]) {",
        "    l = limits1[0]; period = limits1[1];",
        "} else {",
        "    l = limits2[0]; period = limits2[1];",
        "}",
        "// next check if first part of string (up to l-1) is a suffix of the second",
        "boolean isSuffix = false;",
        "",
        "if ( l < ( n/2 ) ) { // need first part to be smaller",
        "    int k = l; // then check if it is a suffix",
        "    while ( k>=0 && string[k]==string[(m-1)-(l-k)] ) {  k--; }",
        "    if (k<0) isSuffix = true; // is a suffix",
        "}",
        "        ",
        "int sp=0; // starting position in text",
        "int j; // position in string",
        "",
        "if (isSuffix) { // first part of string is suffix of second",
        "    int s = -1; // memory used for new starting position",
        "    // while we have not reached the end of text    ",
        "    while (sp <= n-m) {",
        "        j = Math.max(l,s) + 1; // starting position in string",
        "        // search forwards",
        "        while (j<m && string[j] == text[sp+j])",
        "            j++;",
        "        // If end of string reached",
        "        if (j>=m) {",
        "            j = l; // starting position in string",
        "            // Search backwards",
        "            while (j>s && string[j] == text[sp+j])",
        "                j--;",
        "            if (j<=s) // there is a match",
        "                return sp;",
        "            // mismatch found during backwards search",
        "            sp = sp+period; // new starting position in text",
        "            s = m-period-1; // update memory",
        "        }",
        "        else { // mismatch found during forwards search",
        "            sp = sp+(j-l); // new starting position in text",
        "            s = -1; // update memory",
        "        }",
        "    }",
        "}",
        "else { // first part of string is not a suffix",
        "    period = Math.max(l+1, m-(l+1)) + 1;",
        "    // while we have not reached the end of text",
        "    while (sp <= n-m) {",
        "        j = l+1; // start from position l+1 in string",
        "        // search forwards",
        "        while (j<m && string[j] == text[sp+j])",
        "            j++;",
        "        // If end of string reached",
        "        if (j >= m) {",
        "            j = l; // starting position in string",
        "            // search backwards",
        "            while (j>=0 && string[j] == text[sp+j])",
        "                j--;",
        "            // if start of string reached",
        "            if (j<0) return sp; // there is a match",
        "            // mismatch found during backwards search",
        "            sp = sp+period; // new starting position in text",
        "        }",
        "        else // mismatch found during forwards search",
        "            sp = sp+(j-l); // new starting position in text",
        "    }",
        "}",
        "return -1; // no occurrence of string found"
        );

        ObservableList<String> algoTable = FXCollections.observableArrayList(
"- finds maximal suffix of the string with",
        "  respect to two orderings",
        "- the orderings are the standard lexicographic",
        "  ordering and the dual ordering",
        "- this is used to factorise the string into two compositions ",
        "  (for forwards and backwards comparisons)",
        "- the functions return the position (l) of the maximal suffix ",
        "  and the period of this suffix",
        "   ",
        "Definition of maximal suffix:",
        "- for a given ordering <= and string, the maximal",
        "  suffix w.r.t <= is the suffix w of the string",
        "  such that w' <= w for all other suffixes w'",
        "  (including the string its self)",
        "",
        "Definition of critical factorisation:",
        "",
        "- a period of a string is a positive integer",
        "  such that string[i] = string[i+p] whenever",
        "  both sides are defined",
        "- the smallest period of a string is denoted per(string)",
        "",
        "- let \"u v\" be a factorization of the string",
        "- a repetition in the factorisation is a pattern w",
        "  such that it appears on both sides of the factorisation",
        "  with possible overflows, that is:",
        "  * w is a suffix of u or u is a suffix of w",
        "  * w is a prefix of v of v is a prefix of w",
        "",
        "- the length of a repetition is called a local period",
        "- the length of the smallest repetition is denoted by r(u,v)",
        "- each factorization has at least one repetition and",
        "  1 <= r(u,v) <=  m where m is the size of the string",
        "- a factorisation (u,v) such that r(u,v)=per(string)",
        "  is called a critical factorisation",
        "",
        "- the Two Way algorithm chooses the critical",
        "  factorization \"u v\" such that |u| < per(string)",
        "  and |u| is minimal based on the following theorem",
        "",
        "Theorem:",
        "- let <= be an alphabetical ordering and => the reverse ordering of <=",
        "- let v be the alphabetically maximal suffix of string w.r.t. <=",
        "- let v' be the alphabetically maximal suffix of string w.r.t. =>",
        "- if |v| <= |v'|, then \"u v\" is a critical factorization of string,",
        "  otherwise \"u' v'\" is a critical factorization of string",
        "- moreover, |u| < per(string) and |u'| < per(string)",
        ""
        );


        tableView.setItems(algoTable);
        // Disable User selection
        tableView.setFocusModel(null);
        codeView.setItems(algoCode);
        infoView.setItems(algoData);
    }

}
