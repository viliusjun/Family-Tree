package tree;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.text.pdf.BaseFont;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import com.itextpdf.layout.Document;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class TreeController {

    static boolean newTree;
    static File fileName;
    boolean flag = false;

    // Hashtable who's key is the ID and the value is a Node class object
    static Hashtable<Integer, Node> People = new Hashtable<>(1);
    static Node rootPersonNode;

    int newOptionType;
    int chosenNodeID;

    @FXML MenuBar menuBar;
    @FXML TreeView<String> treeView;
    @FXML ScrollPane treeViewScrollPane;

    @FXML AnchorPane mainPane;
    @FXML AnchorPane treePane;
    @FXML AnchorPane infoPaneWithButtons;

    @FXML Pane infoPane;
    @FXML TextField textFieldName;
    @FXML TextField textFieldSurname;
    @FXML DatePicker datePickerBirthday;
    @FXML TextField textFieldBirthPlace;

    @FXML Button saveBtn;
    @FXML Button exitBtn;



    /*
    Initialize and change Stage methods
     */

    // Initialize method
    public static void initialize(boolean newTreeBool, File file) {
        newTree = newTreeBool;
        fileName = file;
    }

    // method for closing the current stage
    @FXML private void close() throws Exception {
        Stage currentStage = (Stage) mainPane.getScene().getWindow();
        backToTitle(currentStage);
        People.clear();
        Person.setTotal(0);
        rootPersonNode = null;
    }

    // Method for showing the titleScene
    void backToTitle(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../main/TitleScreen.fxml"));
        Scene treeScene = new Scene(root, 800, 474);
        stage.setScene(treeScene);
        stage.show();
    }



    /*
    Load tree from file methods
     */

    // We update the scroll pane for the list of people when load is pressed or the method is called
    @FXML void load() {
        // we we chose to load tree from file, then we go to the readFromCSV method
        if (!newTree && !flag) {
            readFromCSV(fileName);
            flag = true;
        }

        reloadNodes();
        placeNodesOnPane();

        // We create tree items
        TreeItem<String> root, parents, children, grandparents, greatGrandparents, spouse, person, grandchildren, greatGrandchildren;
        root = new TreeItem<>();
        root.setExpanded(true);

        // We run through all the people
        Enumeration<Node> keys = People.elements();
        while (keys.hasMoreElements()) {
            int key = keys.nextElement().ID;
            // create branch for person (name + surname + ID)
            person = makeBranch(People.get(key).name + " " + People.get(key).surname + " " + People.get(key).ID, root);

            // if the person has a spouse create a branch spouse and put the person's info under the spouse branch
            if (People.get(key).Spouse != 0) {
                spouse = makeBranch("Spouse", person);
                makeBranch(People.get(People.get(key).Spouse).name + " " + People.get(People.get(key).Spouse).surname + " " + People.get(People.get(key).Spouse).ID, spouse);
            }

            // If the person has parents, then create a branch parents and put the info of the parents under the parents branch
            if (People.get(key).Parents.size() != 0) {
                parents = makeBranch("Parents", person);
                makeRelationshipBranches(parents, People.get(key).Parents);
            }

            if (People.get(key).Children.size() != 0) {
                children = makeBranch("Children", person);
                makeRelationshipBranches(children, People.get(key).Children);
            }

            if (People.get(key).Grandparents.size() != 0) {
                grandparents = makeBranch("Grandparents", person);
                makeRelationshipBranches(grandparents, People.get(key).Grandparents);
            }

            if (People.get(key).GreatGrandparents.size() != 0) {
                greatGrandparents = makeBranch("Great grandparents", person);
                makeRelationshipBranches(greatGrandparents, People.get(key).GreatGrandparents);
            }

            if (People.get(key).GrandChildren.size() != 0) {
                grandchildren = makeBranch("Grandchildren", person);
                makeRelationshipBranches(grandchildren, People.get(key).GrandChildren);
            }

            if (People.get(key).GreatGrandChildren.size() != 0) {
                greatGrandchildren = makeBranch("Great grandchildren", person);
                makeRelationshipBranches(greatGrandchildren, People.get(key).GreatGrandChildren);
            }
        }

        treeView.setStyle("-fx-font: 14px Verdana;");
        treeView.setRoot(root);
        treeView.setShowRoot(false);
    }

    // Method that reads the information from the CSV file and creates Nodes of the received data
    void readFromCSV(File file) {
        try {
            int counterID = 0;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String row;
            row = bufferedReader.readLine();

            // if there is no row with the required elements we alert
            if (row == null || !row.equals("ID;Name;Surname;Birthday;BirthPlace;Parents;Children;Grandparents;Great grandparents;Grandchildren;Great grandchildren;Spouse")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Tree not found");
                alert.setHeaderText(null);
                alert.setContentText("No tree in file " + file.getPath() + " could be found.");
                alert.showAndWait();
            }
            else {
                while ((row = bufferedReader.readLine()) != null) {
                    String[] data = row.split(";");
                    Node personNode = new Node(data[1], data[2], LocalDate.parse(data[3]), data[4]);
                    personNode.ID = Integer.parseInt(data[0]);
                    String[] ids;


                    // We look for the biggest ID
                    if (personNode.ID > counterID) {
                        counterID = personNode.ID;
                    }

                    // Parents
                    // If not empty we read the numbers provided (split by space) and memorize (put) them in the parents array
                    if (!data[5].equals("")) {
                        ids = data[5].split(" ");
                        for (String id : ids) {
                            personNode.Parents.add(Integer.parseInt(id));
                        }
                    }

                    // Children
                    if (!data[6].equals("")) {
                        ids = data[6].split(" ");
                        for (String id : ids) personNode.Children.add(Integer.parseInt(id));
                    }

                    // Grandparents
                    if (!data[7].equals("")) {
                        ids = data[7].split(" ");
                        for (String id : ids) personNode.Grandparents.add(Integer.parseInt(id));
                    }

                    // Great grandparents
                    if (!data[8].equals("")) {
                        ids = data[8].split(" ");
                        for (String id : ids) personNode.GreatGrandparents.add(Integer.parseInt(id));
                    }

                    // Grandchildren
                    if (!data[9].equals("")) {
                        ids = data[9].split(" ");
                        for (String id : ids) personNode.GrandChildren.add(Integer.parseInt(id));
                    }

                    // Great grandchildren
                    if (!data[10].equals("")) {
                        ids = data[10].split(" ");
                        for (String id : ids) personNode.GreatGrandChildren.add(Integer.parseInt(id));
                    }

                    personNode.Spouse = Integer.parseInt(data[11]);
                    setPersonNodeMenuButtons(personNode);
                    People.put(personNode.ID, personNode);
                }
            }
            Person.setTotal(counterID);
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Could not open file");
            alert.setHeaderText(null);
            alert.setContentText("File " + file.getPath() + " could not be opened.");
            alert.showAndWait();
        }

    }

    // Method that runs through all the people in the hashtable with their ID number and calls another method to set the ellipses information (editNode())
    void reloadNodes() {
        // Enumeration - generates a series of elements of the provided object
        Enumeration<Node> personNodeEnumeration = People.elements();

        while(personNodeEnumeration.hasMoreElements()) {
            int ID = personNodeEnumeration.nextElement().ID;
            People.get(ID).editNode();
        }
    }



    /*
    Place nodes, filter, set menu buttons
     */

    // Method that places the Nodes on the main Pane so they are visible
    void placeNodesOnPane() {
        int startX;
        int startY = 20;
        ArrayList<Integer> arrayList = new ArrayList<>();

        // We remove all the nodes from the pane
        treePane.getChildren().clear();
        // We add the infoPaneWithButtons
        treePane.getChildren().add(infoPaneWithButtons);

        // if a rootPersonNode is created
        if (rootPersonNode != null) {
            arrayList.add(rootPersonNode.ID);

            // while the arrayList is not empty do the following
            int level = 0;
            while (!arrayList.isEmpty()) {
                level++;
                startX = 1000;

                int amountOfPeople = arrayList.size();
                startX = startX - (amountOfPeople * 420 / 2) + 420;

                for (int i = 0; i < amountOfPeople; i++) {
                    Line spouseLine = new Line();
                    spouseLine.strokeProperty().setValue(Paint.valueOf("BLACK"));
                    People.get(arrayList.get(0)).level = level;
                    arrayList.addAll(People.get(arrayList.get(0)).Children);
                    arrayList = removeDuplicates(arrayList);
                    People.get(arrayList.get(0)).createNode(treePane, startX, startY);

                    // if the person has a spouse, create spouse node and connect lines
                    if (People.get(arrayList.get(0)).Spouse != 0) {
                        People.get(People.get(arrayList.get(0)).Spouse).isSpouse = true;
                        People.get(People.get(arrayList.get(0)).Spouse).level = level;
                        People.get(People.get(arrayList.get(0)).Spouse).createNode(treePane, startX + 210, startY);
                        spouseLine.setStartX(startX + 155);
                        spouseLine.setStartY(startY + 70);
                        spouseLine.setEndX(startX + 195);
                        spouseLine.setEndY(startY + 70);
                        treePane.getChildren().add(spouseLine);
                    }

                    // if the person has parents
                    if (People.get(arrayList.get(0)).Parents.size() != 0) {
                        for (int j = 0; j < People.get(arrayList.get(0)).Parents.size(); j++) {
                            Line parentLine = new Line();
                            parentLine.strokeProperty().setValue(Paint.valueOf("BLACK"));
                            if (People.get(People.get(arrayList.get(0)).Parents.get(j)).x != 0) {
                                parentLine.setStartX(startX + 70);
                                parentLine.setStartY(startY);
                                parentLine.setEndX(People.get(People.get(arrayList.get(0)).Parents.get(j)).x + 70);
                                parentLine.setEndY(People.get(People.get(arrayList.get(0)).Parents.get(j)).y + 140);
                                treePane.getChildren().add(parentLine);
                            }
                        }
                    }
                    startX += 420;
                    // we remove the first element, so we repeat the same with the new first element
                    arrayList.remove(0);
                }
                startY += 140 + 70;
            }
        }
    }

    // If filter button is pressed and multipleSelectionModel is not null (a person is marked), then by the ID number we make that person a root node
    //      and call the load() method that sets the scroll treeView person info pane and reloads the nodes in the node pane
    @FXML
    void makeRoot() {
        MultipleSelectionModel<TreeItem<String>> multipleSelectionModel = treeView.getSelectionModel();

        if (multipleSelectionModel.getSelectedItem() != null) {
            String text = multipleSelectionModel.getSelectedItem().getValue();

            // call method that returns ID by the (name + surname + ID) text
            int ID = stringToID(text);
            if (ID != 0) {
                rootPersonNode = People.get(ID);
            }
            load();
        }
        treeViewScrollPane.setHvalue(0.4);
        treeViewScrollPane.setVvalue(0);
    }


    // Method that calls a method, which lets you edit the information of a person, if a menuButton is pressed
    void setPersonNodeMenuButtons(Node node) {
        node.edit.setOnAction(event -> makeInfoPaneVisible(1, node.ID));
        node.addParent.setOnAction(event -> makeInfoPaneVisible(2, node.ID));
        node.addChild.setOnAction(event -> makeInfoPaneVisible(3, node.ID));
        node.addSpouse.setOnAction(event -> makeInfoPaneVisible(4, node.ID));
        node.removePerson.setOnAction(event -> deleteNode(node.ID));
    }



    /*
    Methods for making the infoPane visible and invisible
     */

    // Method that makes the infoPane visible
    void makeInfoPaneVisible(int type, int ID) {
        infoPaneWithButtons.setLayoutX(People.get(ID).x);
        infoPaneWithButtons.setLayoutY(People.get(ID).y);
        infoPaneWithButtons.toFront();
        datePickerBirthday.setValue(LocalDate.now());

        // Edit
        if (type == 1) {
            textFieldName.setText(People.get(ID).name);
            textFieldSurname.setText(People.get(ID).surname);
            datePickerBirthday.setValue(People.get(ID).birthDate);
            textFieldBirthPlace.setText(People.get(ID).birthPlace);

            //when save is pressed we update the node
            saveBtn.setOnAction(event -> updateNode(textFieldName.getText(), textFieldSurname.getText(), datePickerBirthday.getValue(), ID));
        } // Parent
        else if (type == 2) {
            saveBtn.setOnAction(event -> addNode(textFieldName.getText(), textFieldSurname.getText(), datePickerBirthday.getValue(), textFieldBirthPlace.getText(), People.get(ID), -1));
            newOptionType = type;
            chosenNodeID = ID;
        } // Child
        else if (type == 3) {
            saveBtn.setOnAction(event -> addNode(textFieldName.getText(), textFieldSurname.getText(), datePickerBirthday.getValue(), textFieldBirthPlace.getText(), People.get(ID), 1));
            newOptionType = type;
            chosenNodeID = ID;
        } // Spouse
        else  if (type == 4) {
            saveBtn.setOnAction(event -> addNode(textFieldName.getText(), textFieldSurname.getText(), datePickerBirthday.getValue(), textFieldBirthPlace.getText(), People.get(ID), 0));
        }

        infoPaneWithButtons.setVisible(true);
    }

    @FXML void makeInfoPaneInvisible() {
        textFieldName.setText("Name");
        textFieldSurname.setText("Surname");
        datePickerBirthday.setValue(LocalDate.now());
        infoPaneWithButtons.setVisible(false);
    }



    /*
    Methods for updating, deleting, adding Nodes
     */

    // Node is updated with the information provided and we update the scroll pane
    void updateNode(String name, String surname, LocalDate birthDate, int ID) {
        People.get(ID).name = name;
        People.get(ID).surname = surname;
        People.get(ID).birthDate = birthDate;
        People.get(ID).editNode();
        load();
        makeInfoPaneInvisible();
    }

    // Method that deletes a node
    void deleteNode(int ID) {
        People.forEach((k, v) -> {
            if (!v.Parents.isEmpty() && v.Parents.contains(ID)) v.Parents.remove((Object)ID);
            if (!v.Children.isEmpty() && v.Children.contains(ID)) v.Children.remove((Object)ID);
            if (v.Spouse == ID) v.Spouse = 0;
            if (!v.Grandparents.isEmpty() && v.Grandparents.contains(ID)) v.Grandparents.remove((Object)ID);
            if (!v.GreatGrandparents.isEmpty() && v.GreatGrandparents.contains(ID)) v.GreatGrandparents.remove((Object)ID);
            if (!v.GrandChildren.isEmpty() && v.GrandChildren.contains(ID)) v.GrandChildren.remove((Object)ID);
            if (!v.GreatGrandChildren.isEmpty() && v.GreatGrandChildren.contains(ID)) v.GreatGrandChildren.remove((Object)ID);
        });
        if (rootPersonNode == People.get(ID)) {
            if (!rootPersonNode.Children.isEmpty()) {
                rootPersonNode = People.get(rootPersonNode.Children.get(0));
            }
            else if (rootPersonNode.Spouse != 0) rootPersonNode = People.get(rootPersonNode.Spouse);
            else rootPersonNode = null;
        }
        People.remove(ID);
        load();
    }

    // Method that adds a new Node and makes relations with other nodes
    void addNode(String name, String surname, LocalDate birthDate, String birthPlace, Node chosenNode, int level) {
        Node node = new Node(name, surname, birthDate, birthPlace);
        setPersonNodeMenuButtons(node);

        // We put the ID and the node into the hashtable
        People.put(node.ID, node);

        // Add parent
        if (level == -1) {
            // we store the newNodes ID in the chosenNodes Parents' array list and vise versa in the Children's array list
            chosenNode.addRelation(chosenNode.Parents, node.ID);
            node.addRelation(node.Children, chosenNode.ID);

            for (int i = 0; i < chosenNode.Children.size(); i++) {
                // we make all of the chosen node's children save the ID of the new nodes (their grandparent's) ID
                // vise versa we store the grandchildren ID
                People.get(chosenNode.Children.get(i)).Grandparents.add(node.ID);
                node.addRelation(node.GrandChildren, chosenNode.Children.get(i));

                // we irritate through the current child's (i) children (j)
                for (int j = 0; j < People.get(chosenNode.Children.get(i)).Children.size(); j++) {
                    // the same way store greatGrandParents' and greatGrandChildren's ID
                    People.get(People.get(chosenNode.Children.get(i)).Children.get(j)).GreatGrandparents.add(node.ID);
                    node.addRelation(node.GreatGrandChildren, People.get(chosenNode.Children.get(i)).Children.get(j));
                }
            }
            // and we make the parent the root node
            rootPersonNode = node;
        }
        // Add spouse
        else if (level == 0) {
            if (chosenNode.Spouse != 0) {
                People.get(chosenNode.Spouse).Spouse = 0;
            }
            chosenNode.Spouse = node.ID;
            node.Spouse = chosenNode.ID;
        }
        // Add child
        else {
            chosenNode.addRelation(chosenNode.Children, node.ID);
            node.addRelation(node.Parents, chosenNode.ID);

            for (int i = 0; i < chosenNode.Parents.size(); i++) {
                People.get(chosenNode.Parents.get(i)).GrandChildren.add(node.ID);
                node.addRelation(node.Grandparents, chosenNode.Parents.get(i));
            }

            for (int i = 0; i < chosenNode.Grandparents.size(); i++) {
                People.get(chosenNode.Grandparents.get(i)).GreatGrandChildren.add(node.ID);
                node.addRelation(node.GreatGrandparents, chosenNode.Grandparents.get(i));
            }
        }

        load();
        makeInfoPaneInvisible();
    }



    /*
    Methods for creating a new tree root node, saving the tree in a CSV file, saving in PDF
     */

    // when create new tree button is pressed, we create a new node and make it the rootnode.
    @FXML void createNewTree() {
        Node start = new Node("Name", "Surname", LocalDate.now(), "Birth Place");
        People.put(start.ID, start);
        setPersonNodeMenuButtons(start);
        rootPersonNode = start;
        load();
    }

    // Method that saves the tree's data into a csv file
    @FXML void saveTree() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
        if (file != null) {
            try {
                PrintWriter pw = new PrintWriter(file);
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append("ID;Name;Surname;Birthday;BirthPlace;Parents;Children;Grandparents;Great grandparents;Grandchildren;Great grandchildren;Spouse\n");

                People.forEach((k, v)->{
                    stringBuilder.append(v.ID).append(";").append(v.name).append(";").append(v.surname).append(";");

                    if (v.birthDate!=null) stringBuilder.append(v.birthDate.toString()).append(";");
                    else stringBuilder.append(";;");

                    stringBuilder.append(v.birthPlace).append(";");

                    v.Parents.forEach((i) -> stringBuilder.append(i).append(" "));
                    stringBuilder.append(";");

                    v.Children.forEach((i) -> stringBuilder.append(i).append(" "));
                    stringBuilder.append(";");

                    v.Grandparents.forEach((i) -> stringBuilder.append(i).append(" "));
                    stringBuilder.append(";");

                    v.GreatGrandparents.forEach((i) -> stringBuilder.append(i).append(" "));
                    stringBuilder.append(";");

                    v.GrandChildren.forEach((i) -> stringBuilder.append(i).append(" "));
                    stringBuilder.append(";");

                    v.GreatGrandChildren.forEach((i) -> stringBuilder.append(i).append(" "));
                    stringBuilder.append(";");

                    stringBuilder.append(v.Spouse);

                    stringBuilder.append("\n");
                });
                pw.write(stringBuilder.toString());
                pw.close();

                pw = new PrintWriter("StartSettings.txt");
                pw.write(file.getAbsolutePath());
                pw.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Method that captures the family tree pane image and puts in into pdf together with a table of the data
    @FXML void saveToPDF() throws IOException {

        // We find the path to the program file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());

        // we capture an image of the tree
        WritableImage image =  treePane.snapshot( new SnapshotParameters(), null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", byteArrayOutputStream);
        Image imageOfFamilyTree = new Image(ImageDataFactory.create(byteArrayOutputStream.toByteArray()));

        // we create the pdf
        PdfWriter pdfWriter = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDoc, PageSize.A1);

        //Add image to pdf
        document.add(imageOfFamilyTree);

        float n = 150F;
        float[] pointColumnWidths = {n, n, n, n, n, n, n, n, n, n, n, n};
        Table table = new Table(pointColumnWidths);

        //Column names
        table.addCell(new Cell().add(new Paragraph("ID")));
        table.addCell(new Cell().add(new Paragraph("Name")));
        table.addCell(new Cell().add(new Paragraph("Surname")));
        table.addCell(new Cell().add(new Paragraph("Birthday")));
        table.addCell(new Cell().add(new Paragraph("BirthPlace")));
        table.addCell(new Cell().add(new Paragraph("Spouse")));
        table.addCell(new Cell().add(new Paragraph("Children")));
        table.addCell(new Cell().add(new Paragraph("Parents")));
        table.addCell(new Cell().add(new Paragraph("Grandparents")));
        table.addCell(new Cell().add(new Paragraph("Grandchildren")));
        table.addCell(new Cell().add(new Paragraph("Great grandparents")));
        table.addCell(new Cell().add(new Paragraph("Great grandchildren")));

        //Rows
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(rootPersonNode.ID);
        while (!arrayList.isEmpty()) {
            Node person = People.get(arrayList.get(0));
            arrayList.addAll(person.Children);
            arrayList.remove(0);

            addPersonInfoCells(table, person);
            if (person.Spouse != 0){
                addPersonInfoCells(table, People.get(person.Spouse));
            }
        }

        document.add(table);
        document.close();
    }


    /*
    Other methods
     */

    // We create a table for the pdf file
    void addPersonInfoCells(Table table,Node person) throws IOException {
        PdfFont font = PdfFontFactory.createFont("tree/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        table.addCell(new Cell().add(new Paragraph(String.valueOf(person.ID))));
        table.addCell(new Cell().add(new Paragraph(person.name).setFont(font)));
        table.addCell(new Cell().add(new Paragraph(person.surname).setFont(font)));
        table.addCell(new Cell().add(new Paragraph(person.birthDate.toString())));
        table.addCell(new Cell().add(new Paragraph(person.birthPlace).setFont(font)));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(person.Spouse))));

        String ids =  Arrays.toString(person.Children.toArray());
        table.addCell(new Cell().add(new Paragraph(ids)));

        ids =  Arrays.toString(person.Parents.toArray());
        table.addCell(new Cell().add(new Paragraph(ids)));

        ids =  Arrays.toString(person.Grandparents.toArray());
        table.addCell(new Cell().add(new Paragraph(ids)));

        ids =  Arrays.toString(person.GrandChildren.toArray());
        table.addCell(new Cell().add(new Paragraph(ids)));

        ids =  Arrays.toString(person.GreatGrandparents.toArray());
        table.addCell(new Cell().add(new Paragraph(ids)));

        ids =  Arrays.toString(person.GreatGrandChildren.toArray());
        table.addCell(new Cell().add(new Paragraph(ids)));
    }


    // Method that receives a string and a parent treeItem and creates children for the parent with the string received
    private TreeItem<String> makeBranch(String title, TreeItem<String> parent) {
        TreeItem<String> item = new TreeItem<>(title);
        parent.getChildren().add(item);
        return item;
    }

    // we run through all the arrayList and calling makeBranch method for each element
    private void makeRelationshipBranches(TreeItem<String> root, ArrayList<Integer> arrayList) {
        for (Integer integer : arrayList) {
            makeBranch(People.get(integer).name + " " + People.get(integer).surname + " " + People.get(integer).ID, root);
        }
    }

    // Method that runs through all the nodes returning the ID of the person who's name + surname + ID = text (received in function)
    int stringToID(String text) {
        Enumeration<Node> keys = People.elements();

        while(keys.hasMoreElements()) {
            int ID = keys.nextElement().ID;
            if (text.equals(People.get(ID).name + " " + People.get(ID).surname + " " + People.get(ID).ID)){
                return ID;
            }
        }
        return 0;
    }

    // Method that creates a new arrayList and adds only the values that don't repeat (doesn't add values that repeat) and returns the new list
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<T>();
        for (T element : list) {
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }
        return newList;
    }
}