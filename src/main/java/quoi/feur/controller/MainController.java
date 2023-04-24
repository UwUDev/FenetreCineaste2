package quoi.feur.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import quoi.feur.Main;
import quoi.feur.manager.ImageManager;
import quoi.feur.struct.ImageData;
import quoi.feur.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.Function;

import static quoi.feur.Main.primaryStage;

@SuppressWarnings("DataFlowIssue")
public class MainController implements Initializable {
    public static MainController instance;

    public ImageView imageView;
    public JFXButton exportButton;
    public Stage dialog = new Stage();

    @Override
    public void initialize(URL location, ResourceBundle resources) { // fontion appelée au lancement de l'application après le chargement du FXML
        instance = this; // on stocke l'instance de la classe pour pouvoir y accéder depuis d'autres classes
        System.out.println("Loaded :)");
    }

    private void processImage(Function<BufferedImage, BufferedImage> operation) { // fonction qui permet de traiter l'image
        Image image = imageView.getImage(); // on récupère l'image dans l'ImageView que l'on manipule actuellement
        if (image == null) { // si l'image est null, on ne fait rien
            return;
        }
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image); // on convertit l'image en BufferedImage
        ImageUtils.addToHistory(bufferedImage); // on ajoute l'image à l'historique
        bufferedImage = operation.apply(bufferedImage); // on applique l'opération sur l'image
        imageView.setImage(ImageUtils.toImage(bufferedImage)); // on convertit l'image en Image et on l'affiche dans l'ImageView
    }

    public void importImage() { // fonction qui permet d'importer une image
        System.out.println("Importing image");
        FileChooser fileChooser = new FileChooser(); // on crée un FileChooser
        fileChooser.setInitialDirectory(new File("images/")); // on définit le dossier de départ
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")); // on définit les extensions de fichiers acceptées
        File file = fileChooser.showOpenDialog(null); // on ouvre la fenêtre de sélection de fichier
        if (file != null) { // si un fichier a été sélectionné
            exportButton.setDisable(false); // on active le bouton d'export
            System.out.println("File selected : " + file.getName());
            imageView.setImage(new javafx.scene.image.Image(file.toURI().toString())); // on affiche l'image dans l'ImageView
        }
    }

    public void undo() { // fonction qui permet d'annuler une opération
        if (ImageUtils.hasHistory()) { // si l'historique n'est pas vide
            imageView.setImage(ImageUtils.toImage(ImageUtils.popHistory())); // on récupère l'image précédente et on l'affiche dans l'ImageView
        }
    }

    public void invertX() {
        processImage(ImageUtils::flipVertical);
    }

    public void invertY() {
        processImage(ImageUtils::flipHorizontal);
    }

    public void gbr() {
        processImage(ImageUtils::RGBtoGBR);
    }

    public void nb() {
        processImage(ImageUtils::blackAndWhite);
    }

    public void sepia() {
        processImage(ImageUtils::sepia);
    }

    public void sobel() {
        processImage(ImageUtils::sobel);
    }

    public void gallery() throws IOException { // fonction qui permet d'ouvrir la fenêtre de la galerie
        dialog = new Stage(); // on crée une nouvelle fenêtre
        Parent root = FXMLLoader.load(Main.class.getResource("gallery.fxml")); // on charge le FXML de la fenêtre
        dialog.initModality(Modality.APPLICATION_MODAL); // on définit le type de fenêtre
        dialog.initOwner(primaryStage); // on définit la fenêtre parente
        Scene dialogScene = new Scene(root, 800, 500); // on définit la scène de la fenêtre ainsi que sa taille
        dialog.setResizable(false); // on définit si la fenêtre est redimensionnable
        dialog.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png"))); // on définit l'icône de la fenêtre
        dialog.setTitle("Gallery"); // on définit le titre de la fenêtre
        dialog.setScene(dialogScene); // on définit la scène de la fenêtre
        dialog.show(); // on affiche la fenêtre
    }

    public void export() throws IOException { // fonction qui permet d'exporter l'image
        dialog = new Stage(); // on crée une nouvelle fenêtre
        Parent root = FXMLLoader.load(Main.class.getResource("export.fxml")); // on charge le FXML de la fenêtre

        dialog.initModality(Modality.APPLICATION_MODAL); // on définit le type de fenêtre
        dialog.initOwner(primaryStage); // on définit la fenêtre parente
        Scene dialogScene = new Scene(root, 425, 225); // on définit la scène de la fenêtre ainsi que sa taille
        dialog.setResizable(false); // on définit si la fenêtre est redimensionnable
        dialog.setTitle("Export"); // on définit le titre de la fenêtre
        JFXTextField name = (JFXTextField) dialogScene.lookup("#nameField"); // on récupère le champ de texte du nom
        JFXTextField tag = (JFXTextField) dialogScene.lookup("#tagsField"); // on récupère le champ de texte des tags
        JFXButton buttonAccept = (JFXButton) dialogScene.lookup("#save"); // on récupère le bouton d'export

        buttonAccept.setOnMouseClicked(event -> { // on définit l'action du bouton d'export
            System.out.println("Exporting image");
            String imgName = UUID.randomUUID().toString().substring(0, 8); // on génère un nom aléatoire pour l'image
            ImageUtils.saveImage(ImageUtils.toBufferedImage(imageView.getImage()), "images/" + imgName); // on sauvegarde l'image dans le dossier images
            ImageData exportImage = new ImageData(name.getText(), imgName); // on crée une nouvelle ImageData
            ImageManager.getInstance().addImage(exportImage); // on ajoute l'image à la liste des images
            if (!tag.getText().isEmpty()) { // si le champ de texte des tags n'est pas vide
                exportImage.getTags().addAll(Arrays.asList(tag.getText().split(" "))); // on ajoute les tags à l'image
            }
            ImageManager.getInstance().save(); // on sauvegarde la liste des images
            ImageManager.getInstance().displayDatabaseContent(); //afichage DB
            dialog.close(); // on ferme la fenêtre
        });

        dialog.setScene(dialogScene); // on définit la scène de la fenêtre
        dialog.show(); // on affiche la fenêtre
    }
}

