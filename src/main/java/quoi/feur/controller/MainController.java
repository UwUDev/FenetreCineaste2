package quoi.feur.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import quoi.feur.Main;
import quoi.feur.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static quoi.feur.Main.primaryStage;

public class MainController implements Initializable {
    public static MainController instance;

    public ImageView imageView;
    public JFXTextField searchBar;
    public JFXButton exportButton;
    public Stage dialog = new Stage();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        System.out.println("Loaded :)");
    }

    public void importImage() {
        System.out.println("Importing image");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("images/"));
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            exportButton.setDisable(false);
            System.out.println("File selected : " + file.getName());
            imageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
        }
    }

    public void undo() {
        Image image = imageView.getImage();
        if (image == null)
            return;

        if (ImageUtils.hasHistory()) {
            imageView.setImage(ImageUtils.toImage(ImageUtils.popHistory()));
        }
    }

    public void encrypt() {
        // TODO: 17/04/2023 Encrypt
    }

    public void invertX() {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.flipVertical(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void invertY() {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.flipHorizontal(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void gbr() {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.RGBtoGBR(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void nb() {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.blackAndWhite(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void sepia() {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.sepia(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void sobel() {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.sobel(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    /*public void updateWindowsSize(int width, int height) {
        System.out.println("New x : " + width + " New y : " + height);
    }*/

    @SuppressWarnings("DataFlowIssue")
    public void gallery() throws IOException {
        dialog = new Stage();
        Parent root = FXMLLoader.load(Main.class.getResource("gallery.fxml"));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        Scene dialogScene = new Scene(root, 800, 500);
        dialog.setResizable(false);
        dialog.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        dialog.setTitle("Gallery");
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public void export() {
    }

    public void search(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER") || keyEvent.getCode().toString().equals("RETURN")) {
            System.out.println("Text : " + searchBar.getText());
        }
    }
}
