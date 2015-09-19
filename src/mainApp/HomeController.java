package mainApp;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/*****************************************************************
 * The Controller for the Home Screen Panel.
 ****************************************************************/
public class HomeController implements Initializable{

    @FXML private ListView<String> refView;
    private boolean refVisible;
    private final int INVISIBLE = 0;
    private static final Duration ANIM_DURATION = Duration.seconds(0.7); // Pane transition duration


    /**************************************************************************
     * Method that runs as soon as the View loads
     *************************************************************************/
    @Override public void initialize(URL location, ResourceBundle resources){
        // Load the references to the list
        this.loadReferences();
    }

    /*********************************************************************************
     * Methods to load the tutorial screen when the button is pressed
     ********************************************************************************/
    @FXML private void loadTutorial(){
        ScreenNavigator.loadScreen(ScreenNavigator.TUTORIAL_SCREEN);
    }

    /*********************************************************************************
     * Methods to Show and hide the References ListView when the button is pressed
     ********************************************************************************/
    @FXML private void toggleRefPane() {
        TranslateTransition transition = new TranslateTransition(ANIM_DURATION, refView);
        transition.setFromX((refVisible ? -550 : INVISIBLE));
        transition.setToX((refVisible ? INVISIBLE : -550));
        transition.play();
        refVisible ^= true;
    }

    /*****************************************************************
     * Loads the references data to the listView
     ****************************************************************/
    private void loadReferences(){
//        Path refPath = Paths.get("src/resources/algoData/references.txt");

            ObservableList<String> refData = FXCollections.observableArrayList(
        " Boyer, R. & Moore, J. (1977) “A fast string searching algorithm”, ACM.",
        "",
        " Charras, C., & Lecroq, T. (2004). Handbook of exact string matching",
        " algorithms (pp. 1-17). London, UK: King's College Publications.",
        "",
        " Crochemore, M. & Perrin, D. (1991) “Two-way string-matching”,",
        " Journal of the ACM (JACM), vol. 38, no. 3, pp. 650-674.",
        "",
        " Horspool, R.N. (1980) “Practical fast searching in strings”,",
        " Software: Practice and Experience, vol. 10, no. 6, pp. 501-506.",
        "",
        " Knuth, D.E., James H. Morris, J. & Pratt, V.R. (1977) “Fast Pattern",
        " Matching in Strings”, SIAM Journal on Computing, vol. 6, no. 2, pp. 323.",
        "",
        " Smith, P.D. (1991) “Experiments with a very fast substring search",
        " algorithm”, Software: Practice and Experience, vol. 21, no. 10, pp. 1065-1074.",
        "",
        " Sunday, D. (1990) “A very fast substring search algorithm”,",
        " Communications of the ACM. vol.33 no. 8 pp. 132-142, New York."
            );



        refView.setItems(refData);
//        refView.setItems(getData(refPath));
    }

    /******************************************************************
     * Reads a file and stores each line in an ObservableList
     * @param path - the path of the file to be opened
     * @return an Observable list containing the contents of the file
     ******************************************************************/
    private ObservableList<String> getData(Path path){
        ObservableList<String> list = FXCollections.observableArrayList();

        try ( BufferedReader br = Files.newBufferedReader(path) ) {
            for( String line; ( line = br.readLine() ) != null; )
                list.add(line);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setHeaderText("IOException - Filename was not found");
            alert.setContentText("Could not find file: " + path.toString());
            alert.showAndWait();
        }

        return list;
    }

}

