/**
@author Vilius Junevičius, programų sistemos, 1 kursas, 4 grupė, 1 pogrupis
vilius.junevicius@mid.stud.vu.lt

 A program that creates a visual family tree structure, which you can edit, save as CVS/PDF, load as CSV.
 */


package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("TitleScreen.fxml"));
        primaryStage.setScene(new Scene(root, 800, 474));

        primaryStage.setTitle("Family tree");
        primaryStage.show();
    }
}
