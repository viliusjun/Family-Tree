package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import tree.TreeController;

import java.io.File;


public class TitleScreenController {
    @FXML private Button createYourOwnTreeBtn;
    @FXML private Button representTreeFromFileBtn;
    @FXML private Button exitTheApplicationBtn;

    @FXML
    private void createTree(ActionEvent event) throws Exception {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        showTreeScene(stage);
        TreeController.initialize(true, null);
    }

    @FXML
    private void loadTree(ActionEvent event) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fileChooser.showOpenDialog(createYourOwnTreeBtn.getScene().getWindow());
        if(file != null) {
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            showTreeScene(stage);
            TreeController.initialize(false, file);
        }
    }

    @FXML
    private void stop(ActionEvent event) throws Exception {
        Stage stage = (Stage) exitTheApplicationBtn.getScene().getWindow();
        stage.close();
    }


    private void showTreeScene(Stage stage) throws Exception {
        Parent root =  FXMLLoader.load(getClass().getResource("../tree/Tree.fxml"));
        Scene treeScene = new Scene(root);
        stage.setScene(treeScene);
        stage.show();
    }
}
