package quoi.feur.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import quoi.feur.Main;
import quoi.feur.manager.ImageManager;
import quoi.feur.struct.ImageData;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GalleryController implements Initializable {


    public ScrollPane scrollPane;
    private GridPane gridPane = new GridPane();
    VBox mainPane = new VBox();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Opened gallery");

        //import css from resources
        String css = Main.class.getResource("gallery.css").toExternalForm();
        // apply css
        scrollPane.getStylesheets().add(css);


        JFXTextField searchBar = new JFXTextField();
        searchBar.setPromptText("Search (use # to search tag)");
        searchBar.setLayoutY(200);
        mainPane.getChildren().add(searchBar);
        searchBar.setOnKeyReleased(event -> updateGrid(searchBar.getText()));

        updateGrid(null);
    }

    public void updateGrid(String filter) {
        List<ImageData> images = ImageManager.getInstance().getImages().stream().filter(imageData -> {
            if (filter == null || filter.isEmpty())
                return true;
            if(filter.startsWith("#")) {
                for (String tag : imageData.getTags()) {
                    if (tag.toLowerCase().contains(filter.substring(1).toLowerCase()))
                        return true;
                }
                return false;
            }
            return imageData.getName().toLowerCase().contains(filter.toLowerCase());
        }).collect(Collectors.toList());

        int itemCount = images.size();
        System.out.println(images.size());
        int columnCount = 5;
        int rowCount = itemCount / columnCount + 1;

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setStyle("-fx-background-color: #FB8092");
        gridPane.prefWidthProperty().bind(scrollPane.widthProperty());
        gridPane.setMinWidth(800);
        gridPane.setMinHeight(510);
        gridPane.getStyleClass().add("mon-grid-pane");



        int index = 0;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (index >= images.size())
                    break;
                Pane pane = new Pane();  // tuile de l'image et texte
                pane.setPrefHeight(200);
                pane.setStyle("-fx-background-color: #FB8092");
                pane.setPrefWidth(800f/columnCount);

                Image image = new Image(new File("images/" + images.get(index).getFilename()).toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(150);
                imageView.setFitWidth(800f/columnCount);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setCache(true);
                pane.getChildren().add(imageView);

                Label label = new Label(images.get(index).getName());
                label.setLayoutY(150);
                label.setPrefWidth(800f/columnCount);
                label.setAlignment(javafx.geometry.Pos.CENTER);

                pane.getChildren().add(label);

                gridPane.add(pane, j, i);
                index++;
            }
        }

        mainPane.getChildren().removeIf(node -> node instanceof GridPane);
        mainPane.getChildren().add(gridPane);

        scrollPane.setContent(mainPane);
    }
}
