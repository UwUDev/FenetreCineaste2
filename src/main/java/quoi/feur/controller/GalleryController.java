package quoi.feur.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import quoi.feur.Main;
import quoi.feur.manager.ImageManager;
import quoi.feur.struct.ImageData;
import quoi.feur.utils.CryptoUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static quoi.feur.Main.primaryStage;

public class GalleryController implements Initializable {
    @SuppressWarnings("DataFlowIssue")
    private static final Image lockedImageIcon = new Image(Main.class.getResourceAsStream("lock.png"));


    public ScrollPane scrollPane;
    VBox mainPane = new VBox();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Opened gallery");


        @SuppressWarnings("DataFlowIssue")
        String css = Main.class.getResource("gallery.css").toExternalForm(); //import css from resources
        scrollPane.getStylesheets().add(css); // apply css


        JFXTextField searchBar = new JFXTextField();
        searchBar.setPromptText("Search (use # to search tag)");
        searchBar.setLayoutY(200);
        mainPane.getChildren().add(searchBar);
        searchBar.setOnKeyReleased(event -> updateGrid(searchBar.getText()));

        updateGrid(null);
    }

    @SuppressWarnings({"DataFlowIssue", "ResultOfMethodCallIgnored"})
    public void updateGrid(String filter) {
        List<ImageData> images = ImageManager.getInstance().getImages().stream().filter(imageData -> {
            if (filter == null || filter.isEmpty())
                return true;
            if (filter.startsWith("#")) {
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

        GridPane gridPane = new GridPane();
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
                pane.setPrefWidth(800f / columnCount);

                Image image;
                if (images.get(index).isLocked())
                    image = lockedImageIcon;
                else image = new Image(new File("images/" + images.get(index).getFilename()).toURI().toString());


                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(150);
                imageView.setFitWidth(800f / columnCount);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setCache(true);
                pane.getChildren().add(imageView);

                Label label;
                if (images.get(index).isLocked())
                    label = new Label("🔒");
                else label = new Label(images.get(index).getName());
                label.setLayoutY(150);
                label.setPrefWidth(800f / columnCount);
                label.setAlignment(javafx.geometry.Pos.CENTER);

                pane.getChildren().add(label);

                if (images.get(index).isLocked()) {
                    int finalIndex = index;
                    pane.setOnMouseClicked(event -> {
                        Stage dialog = new Stage();

                        Parent root;
                        try {
                            root = FXMLLoader.load(Main.class.getResource("decrypt_popup.fxml"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(primaryStage);
                        Scene dialogScene = new Scene(root, 300, 150);
                        dialog.setResizable(false);
                        dialog.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
                        dialog.setTitle("Gallery");
                        dialog.setScene(dialogScene);

                        Label invalid = (Label) root.lookup("#invalid");
                        JFXPasswordField passwordField = (JFXPasswordField) root.lookup("#password");
                        JFXButton decryptBtn = (JFXButton) root.lookup("#decrypt");

                        decryptBtn.setOnMouseClicked(e -> {
                            if (CryptoUtils.isValidPassword(passwordField.getText(), images.get(finalIndex).getHashedPassword())) {
                                File inputFile = new File("images/" + images.get(finalIndex).getFilename());
                                File outputFile = new File("images/" + images.get(finalIndex).getFilename().replace(".locked", ""));

                                try {
                                    CryptoUtils.decryptFile(passwordField.getText(), inputFile, outputFile);
                                    //noinspection ResultOfMethodCallIgnored
                                    inputFile.delete();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    System.err.println("Error while decrypting file :(");
                                }

                                images.get(finalIndex).setHashedPassword(null);
                                images.get(finalIndex).setFilename(images.get(finalIndex).getFilename().replace(".locked", ""));
                                ImageManager.getInstance().save();

                                updateGrid(filter);
                                dialog.close();
                            } else invalid.setVisible(true);
                        });

                        dialog.show();
                    });
                } else {
                    int finalIndex = index;
                    pane.setOnMouseClicked(event -> {
                        Stage dialog = new Stage();

                        Parent root;
                        try {
                            root = FXMLLoader.load(Main.class.getResource("file_actions.fxml"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(primaryStage);
                        Scene dialogScene = new Scene(root, 300, 200);
                        dialog.setResizable(false);
                        dialog.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
                        dialog.setTitle("Gallery");
                        dialog.setScene(dialogScene);

                        JFXButton openBtn = (JFXButton) root.lookup("#open");
                        JFXButton deleteBtn = (JFXButton) root.lookup("#delete");

                        JFXPasswordField passwordField = (JFXPasswordField) root.lookup("#password");

                        JFXButton encryptBtn = (JFXButton) root.lookup("#encrypt");


                        openBtn.setOnMouseClicked(e -> {
                            MainController.instance.exportButton.setDisable(false);
                            System.out.println("File selected : " + images.get(finalIndex).getFilename());
                            MainController.instance.imageView.setImage(new javafx.scene.image.Image(new File("images/" + images.get(finalIndex).getFilename()).toURI().toString()));

                            dialog.close();
                            MainController.instance.dialog.close();
                        });

                        deleteBtn.setOnMouseClicked(e -> {
                            File file = new File("images/" + images.get(finalIndex).getFilename());
                            file.delete();
                            ImageManager.getInstance().getImages().remove(images.get(finalIndex));
                            updateGrid(filter);
                            dialog.close();

                            ImageManager.getInstance().save();
                        });

                        encryptBtn.setOnMouseClicked(e -> {
                            if (passwordField.getText().isEmpty()) {
                                System.err.println("Password is empty");
                                return;
                            }

                            File inputFile = new File("images/" + images.get(finalIndex).getFilename());
                            File outputFile = new File("images/" + images.get(finalIndex).getFilename() + ".locked");

                            try {
                                CryptoUtils.encryptFile(passwordField.getText(), inputFile, outputFile);
                                inputFile.delete();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                System.err.println("Error while encrypting file :(");
                            }

                            images.get(finalIndex).setHashedPassword(CryptoUtils.hashSha256(passwordField.getText()));
                            images.get(finalIndex).setFilename(images.get(finalIndex).getFilename() + ".locked");
                            ImageManager.getInstance().save();

                            updateGrid(filter);
                            dialog.close();
                        });
                        dialog.show();
                    });
                }

                gridPane.add(pane, j, i);
                index++;
            }
        }

        mainPane.getChildren().removeIf(node -> node instanceof GridPane);
        mainPane.getChildren().add(gridPane);

        scrollPane.setContent(mainPane);
    }
}
