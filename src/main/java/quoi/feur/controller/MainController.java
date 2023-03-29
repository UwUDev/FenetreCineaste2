package quoi.feur.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import quoi.feur.Main;
import quoi.feur.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public static MainController instance;

    public ImageView imageView;
    public JFXTextField searchBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        System.out.println("Loaded :)");
    }

    public void importImage(MouseEvent mouseEvent) {
        System.out.println("Importing image");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("images/"));
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            System.out.println("File selected : " + file.getName());
            imageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
        }
    }

    public void undo(MouseEvent mouseEvent) {
        Image image = imageView.getImage();
        if (image == null)
            return;

        if (ImageUtils.hasHistory()) {
            imageView.setImage(ImageUtils.toImage(ImageUtils.popHistory()));
        }
    }

    public void encrypt(MouseEvent mouseEvent) {
    }

    public void invertX(MouseEvent mouseEvent) {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.flipVertical(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void invertY(MouseEvent mouseEvent) {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.flipHorizontal(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void gbr(MouseEvent mouseEvent) {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.RGBtoGBR(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void nb(MouseEvent mouseEvent) {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.blackAndWhite(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void sepia(MouseEvent mouseEvent) {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.sepia(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void sobel(MouseEvent mouseEvent) {
        Image image = imageView.getImage();
        if (image == null)
            return;

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageUtils.addToHistory(bufferedImage);
        bufferedImage = ImageUtils.sobel(bufferedImage);
        imageView.setImage(ImageUtils.toImage(bufferedImage));
    }

    public void updateWindowsSize(int width, int height) {
        System.out.println("New x : " + width + " New y : " + height);
    }

    public void export(MouseEvent mouseEvent) {
    }

    public void search(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER") || keyEvent.getCode().toString().equals("RETURN")) {
            System.out.println("Text : " + searchBar.getText());
        }
    }
}
