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

@SuppressWarnings("DataFlowIssue")
public class GalleryController implements Initializable {
    private static final Image lockedImageIcon = new Image(Main.class.getResourceAsStream("lock.png")); // image de l'œil avec le cadenas


    public ScrollPane scrollPane;
    VBox mainPane = new VBox(); // la popup

    @Override
    public void initialize(URL location, ResourceBundle resources) { // fonction appelée à l'ouverture du popup
        System.out.println("Opened gallery");


        String css = Main.class.getResource("gallery.css").toExternalForm(); //import la feuille de style css
        scrollPane.getStylesheets().add(css); // applique le css


        JFXTextField searchBar = new JFXTextField(); // barre de recherche
        searchBar.setPromptText("Search (use # to search tag)"); // definition du texte d'indice
        searchBar.setLayoutY(200); // positionnement de la barre de recherche
        mainPane.getChildren().add(searchBar); // ajout de la barre de recherche a le popup
        searchBar.setOnKeyReleased(event -> updateGrid(searchBar.getText())); // appel de la fonction updateGrid à chaque fois que l'utilisateur tape quelque chose dans la barre de recherche

        updateGrid(null); // appel de la fonction updateGrid avec un filtre null (pas de filtrage)
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void updateGrid(String filter) {  // fonction qui met à jour la grille d'images
        List<ImageData> images = ImageManager.getInstance().getImages().stream().filter(imageData -> { // filtre les images en fonction du filtre
            if (filter == null || filter.isEmpty()) // si le filtre est null ou vide, on retourne true
                return true;
            if (filter.startsWith("#")) { // si le filtre commence par un #, on filtre par tag
                for (String tag : imageData.getTags()) { // pour chaque tag de l'image
                    if (tag.toLowerCase().contains(filter.substring(1).toLowerCase())) // si le tag contient le filtre, on retourne true
                        return true;
                }
                return false;
            }
            return imageData.getName().toLowerCase().contains(filter.toLowerCase()); // si le filtre n'est pas un tag, on filtre par nom en ignorant la casse
        }).collect(Collectors.toList()); // on collecte les images filtrées dans une liste

        int itemCount = images.size(); // nombre d'images
        System.out.println(images.size());
        int columnCount = 5; // nombre de colonnes
        int rowCount = itemCount / columnCount + 1; // nombre de lignes

        GridPane gridPane = new GridPane(); // grille d'images
        gridPane.setHgap(10); // espace entre les images horizontal
        gridPane.setVgap(10); // espace entre les images vertical
        gridPane.setStyle("-fx-background-color: #FB8092"); // couleur de fond de la grille d'images
        gridPane.prefWidthProperty().bind(scrollPane.widthProperty()); // largeur de la grille d'images
        gridPane.setMinWidth(800);
        gridPane.setMinHeight(510);
        gridPane.getStyleClass().add("mon-grid-pane"); // on applique le css à la grille d'images


        int index = 0; // index de l'image
        for (int i = 0; i < rowCount; i++) { // pour chaque ligne
            for (int j = 0; j < columnCount; j++) { // pour chaque colonne
                if (index >= images.size()) // si l'index est supérieur au nombre d'images, on sort de la boucle
                    break;
                Pane pane = new Pane();  // tuile de l'image et texte
                pane.setPrefHeight(200); // hauteur de la tuile
                pane.setStyle("-fx-background-color: #FB8092"); // couleur de fond de la tuile
                pane.setPrefWidth(800f / columnCount); // largeur de la tuile calculée en fonction du nombre de colonnes

                Image image;
                if (images.get(index).isLocked()) // si l'image est verrouillée, on affiche l'icône du cadenas
                    image = lockedImageIcon;
                else image = new Image(new File("images/" + images.get(index).getFilename()).toURI().toString()); // sinon on affiche l'image


                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(150); // hauteur de l'image
                imageView.setFitWidth(800f / columnCount); // largeur de l'image calculée en fonction du nombre de colonnes
                imageView.setPreserveRatio(true);  // on conserve le ratio de l'image
                imageView.setSmooth(true); // on applique un filtre pour lisser l'image
                imageView.setCache(true); // on met en cache l'image (pour recharger plus vite l'image à la prochaine réouverture de la galerie)
                pane.getChildren().add(imageView); // on ajoute l'image à la tuile

                Label label; // texte de la tuile
                if (images.get(index).isLocked()) // si l'image est verrouillée, on affiche le cadenas
                    label = new Label("🔒");
                else label = new Label(images.get(index).getName()); // sinon, on affiche le nom de l'image
                label.setLayoutY(150); // positionnement du texte
                label.setPrefWidth(800f / columnCount); // largeur du texte calculée en fonction du nombre de colonnes
                label.setAlignment(javafx.geometry.Pos.CENTER); // on centre le texte

                pane.getChildren().add(label); // on ajoute le texte à la tuile

                if (images.get(index).isLocked()) { // si l'image est verrouillée, on ajoute un évènement au clic sur la tuile
                    int finalIndex = index; // on crée une variable final pour pouvoir l'utiliser dans l'évènement
                    pane.setOnMouseClicked(event -> { // on ajoute un évènement au clic sur la tuile
                        Stage dialog = new Stage(); // popup de déchiffrement

                        Parent root;
                        try {
                            root = FXMLLoader.load(Main.class.getResource("decrypt_popup.fxml")); // on charge le fichier fxml du popup de déchiffrement
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        dialog.initModality(Modality.APPLICATION_MODAL); // on rend le popup modal
                        dialog.initOwner(primaryStage); // on définit le propriétaire du popup
                        Scene dialogScene = new Scene(root, 300, 150); // on définit la scène du popup ainsi que sa taille
                        dialog.setResizable(false); // on empêche le redimensionnement du popup
                        dialog.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png"))); // on définit l'icône du popup
                        dialog.setTitle("Gallery"); // on définit le titre du popup
                        dialog.setScene(dialogScene); // on définit la scène du popup

                        Label invalid = (Label) root.lookup("#invalid"); // on récupère le label d'erreur du popup
                        JFXPasswordField passwordField = (JFXPasswordField) root.lookup("#password"); // on récupère le champ de mot de passe du popup
                        JFXButton decryptBtn = (JFXButton) root.lookup("#decrypt"); // on récupère le bouton de déchiffrement du popup

                        decryptBtn.setOnMouseClicked(e -> { // on ajoute un évènement au clic sur le bouton de déchiffrement
                            if (CryptoUtils.isValidPassword(passwordField.getText(), images.get(finalIndex).getHashedPassword())) { // si le mot de passe est valide
                                File inputFile = new File("images/" + images.get(finalIndex).getFilename()); // on récupère le fichier à déchiffrer
                                File outputFile = new File("images/" + images.get(finalIndex).getFilename().replace(".locked", "")); // on récupère le fichier déchiffré

                                try {
                                    CryptoUtils.decryptFile(passwordField.getText(), inputFile, outputFile); // on déchiffre le fichier
                                    //noinspection ResultOfMethodCallIgnored
                                    inputFile.delete(); // on supprime le fichier chiffré
                                    ImageManager.getInstance().deleteImage(images.get(finalIndex).getFilename()); // on supprime de la DB

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    System.err.println("Error while decrypting file :(");
                                }

                                images.get(finalIndex).setHashedPassword(null); // on supprime le mot de passe de l'image
                                images.get(finalIndex).setFilename(images.get(finalIndex).getFilename().replace(".locked", "")); // on supprime le .locked du nom de fichier
                                ImageManager.getInstance().save(); // on sauvegarde les images

                                updateGrid(filter); // on met à jour la grille
                                dialog.close(); // on ferme le popup
                            } else invalid.setVisible(true); // sinon, on affiche le label d'erreur
                        });

                        dialog.show(); // on affiche le popup
                    });
                } else { // sinon on ajoute un évènement au clic sur la tuile
                    int finalIndex = index; // on crée une variable final pour pouvoir l'utiliser dans l'évènement
                    pane.setOnMouseClicked(event -> { // on ajoute un évènement au clic sur la tuile
                        Stage dialog = new Stage(); // popup de déchiffrement

                        Parent root;
                        try {
                            root = FXMLLoader.load(Main.class.getResource("file_actions.fxml")); // on charge le fichier fxml du popup de déchiffrement
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        dialog.initModality(Modality.APPLICATION_MODAL); // on rend le popup modal
                        dialog.initOwner(primaryStage); // on définit le propriétaire du popup
                        Scene dialogScene = new Scene(root, 300, 200); // on définit la scène du popup ainsi que sa taille
                        dialog.setResizable(false); // on empêche le redimensionnement du popup
                        dialog.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png"))); // on définit l'icône du popup
                        dialog.setTitle("Gallery"); // on définit le titre du popup
                        dialog.setScene(dialogScene); // on définit la scène du popup

                        JFXButton openBtn = (JFXButton) root.lookup("#open"); // on récupère le bouton d'ouverture du popup
                        JFXButton deleteBtn = (JFXButton) root.lookup("#delete"); // on récupère le bouton de suppression du popup

                        JFXPasswordField passwordField = (JFXPasswordField) root.lookup("#password"); // on récupère le champ de mot de passe du popup

                        JFXButton encryptBtn = (JFXButton) root.lookup("#encrypt"); // on récupère le bouton de chiffrement du popup


                        openBtn.setOnMouseClicked(e -> { // on ajoute un évènement au clic sur le bouton d'ouverture
                            MainController.instance.exportButton.setDisable(false); // on active le bouton d'exportation
                            System.out.println("File selected : " + images.get(finalIndex).getFilename());
                            MainController.instance.imageView.setImage(new javafx.scene.image.Image(new File("images/" + images.get(finalIndex).getFilename()).toURI().toString())); // on affiche l'image dans l'imageView

                            dialog.close(); // on ferme le popup
                            MainController.instance.dialog.close(); // on ferme le popup principal
                        });

                        deleteBtn.setOnMouseClicked(e -> { // on ajoute un évènement au clic sur le bouton de suppression
                            File file = new File("images/" + images.get(finalIndex).getFilename()); // on récupère le fichier à supprimer
                            file.delete(); // on supprime le fichier
                            ImageManager.getInstance().getImages().remove(images.get(finalIndex)); // on supprime l'image de la liste
                            ImageManager.getInstance().deleteImage(images.get(finalIndex).getFilename()); // on supprime de la DB
                            updateGrid(filter); // on met à jour la grille
                            dialog.close(); // on ferme le popup

                            ImageManager.getInstance().save(); // on sauvegarde les images
                        });

                        encryptBtn.setOnMouseClicked(e -> { // on ajoute un évènement au clic sur le bouton de chiffrement
                            if (passwordField.getText().isEmpty()) { // si le mot de passe est vide
                                System.err.println("Password is empty"); // on affiche une erreur
                                return; // on arrête l'exécution de la méthode
                            }

                            File inputFile = new File("images/" + images.get(finalIndex).getFilename()); // on récupère le fichier à chiffrer
                            File outputFile = new File("images/" + images.get(finalIndex).getFilename() + ".locked"); // on crée le fichier chiffré

                            try {
                                CryptoUtils.encryptFile(passwordField.getText(), inputFile, outputFile); // on chiffre le fichier
                                ImageManager.getInstance().deleteImage(images.get(finalIndex).getFilename()); // on supprime de la DB
                                inputFile.delete(); // on supprime le fichier non chiffré
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                System.err.println("Error while encrypting file :(");
                            }

                            images.get(finalIndex).setHashedPassword(CryptoUtils.hashSha256(passwordField.getText())); // on ajoute le mot de passe chiffré à l'image
                            images.get(finalIndex).setFilename(images.get(finalIndex).getFilename() + ".locked"); // on ajoute le .locked au nom de fichier
                            ImageManager.getInstance().save(); // on sauvegarde les images

                            updateGrid(filter); // on met à jour la grille
                            dialog.close(); // on ferme le popup
                        });
                        dialog.show(); // on affiche le popup
                    });
                }

                gridPane.add(pane, j, i); // on ajoute la tuile à la grille
                index++; // on incrémente l'index
            }
        }

        mainPane.getChildren().removeIf(node -> node instanceof GridPane); // on supprime la grille précédente
        mainPane.getChildren().add(gridPane); // on ajoute la nouvelle grille

        scrollPane.setContent(mainPane); // on ajoute le pane principal à la scrollPane
    }
}
