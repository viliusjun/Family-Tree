package tree;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.StrokeType;

import java.time.LocalDate;

public class Node extends Person implements NodeMethods {
    public Pane ellipsePane;
    public Label nameText;
    public Label surnameText;
    public Label birthDateText;
    public Ellipse ellipse;
    public MenuButton actions;
    public MenuItem edit;
    public MenuItem addParent;
    public MenuItem addChild;
    public MenuItem addSpouse;
    public MenuItem removePerson;
    public double x;
    public double y;
    public int level;
    public boolean isSpouse = false;

    public Node(String name, String surname, LocalDate birthDate, String birthPlace) {
        // we save the information in the Person class
        super(name, surname, birthDate, birthPlace);

        // we create a new ellipse with the required fields
        ellipsePane = new Pane();
        ellipsePane.setPrefSize(140, 140);

        ellipse = new Ellipse(100, 74);
        ellipse.setFill(Paint.valueOf("#DFCFBE"));
        ellipse.setLayoutX(70);
        ellipse.setCenterY(70);
        ellipse.setStroke(javafx.scene.paint.Color.BLACK);
        ellipse.setStrokeType(StrokeType.INSIDE);

        nameText = new Label(name);
        nameText.setLayoutX(0.0);
        nameText.setLayoutY(25.0);
        nameText.setAlignment(Pos.CENTER);
        nameText.setPrefWidth(140.0);
        nameText.setFont(javafx.scene.text.Font.font(18));

        surnameText = new Label(surname);
        surnameText.setLayoutX(0.0);
        surnameText.setLayoutY(60.0);
        surnameText.setAlignment(Pos.CENTER);
        surnameText.setPrefWidth(140.0);
        surnameText.setFont(javafx.scene.text.Font.font(18));

        birthDateText = new Label(birthDate.toString());
        birthDateText.setLayoutX(0.0);
        birthDateText.setLayoutY(100.0);
        birthDateText.setAlignment(Pos.CENTER);
        birthDateText.setPrefWidth(140.0);
        birthDateText.setFont(javafx.scene.text.Font.font(15));

        actions = new MenuButton("");
        actions.setLayoutX(50);
        actions.setLayoutY(114);
        edit = new MenuItem("Edit information");
        addParent = new MenuItem("Add parent");
        addChild = new MenuItem("Add child");
        addSpouse = new MenuItem("Add spouse");
        removePerson = new MenuItem("Remove person");
        actions.getItems().addAll(edit, addParent, addChild, addSpouse, removePerson);
        actions.setStyle("-fx-background-color: TRANSPARENT;");

        ellipsePane.getChildren().addAll(ellipse, nameText, surnameText, birthDateText, actions);
    }

    // Method that creates the Node by adding the created ellipse to the pane
    @Override
    public void createNode(Pane pane, double x, double y) {
        this.ellipsePane.setLayoutX(x);
        this.ellipsePane.setLayoutY(y);
        this.x = x;
        this.y = y;

        if(!pane.getChildren().contains(this.ellipsePane)) {
            pane.getChildren().add(this.ellipsePane);
        }
    }

    // Method that sets the text into the Nodes (ellipses) and and initializes its coordinates, level and spouse information
    @Override
    public void editNode() {
        nameText.setText(this.name);
        surnameText.setText(this.surname);
        birthDateText.setText(this.birthDate.toString());
        isSpouse = false;
        level = 0;
        x = 0;
        y = 0;
    }

}
