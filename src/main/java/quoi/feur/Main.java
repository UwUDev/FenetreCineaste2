package quoi.feur;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import quoi.feur.controller.MainController;
import quoi.feur.manager.ImageManager;

import java.io.IOException;

@SuppressWarnings("DataFlowIssue") // pas de warning inutile = développeur heureux
public class Main extends Application {
    public static Stage primaryStage; // on stocke la fenêtre principale pour pouvoir y accéder depuis d'autres classes
    public static void main(String[] args) { // fonction principale
        ImageManager.getInstance().load(); // on charge les images
        launch(args); // on lance l'application
    }

    @Override
    public void start(Stage stage) throws IOException { // fonction appelée au lancement de l'application
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml")); // on charge le FXML
        stage.setTitle("Fenêtre Cinéaste 2.0"); // on définit le titre de la fenêtre
        stage.setScene(new Scene(root, 950, 550)); // on définit la scène de la fenêtre et sa taille
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png"))); // on définit l'icône de la fenêtre
        stage.setMinWidth(925); // on définit la taille minimale de la fenêtre (largeur)
        stage.setMinHeight(555); // on définit la taille minimale de la fenêtre (hauteur)

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> { // on définit un listener qui permet de redimensionner l'image en fonction de la taille de la fenêtre
            if (MainController.instance != null) { // on vérifie que l'instance de MainController existe
                ImageView imageView = MainController.instance.imageView; // on récupère l'ImageView
                if (imageView != null) { // on vérifie que l'ImageView existe
                    imageView.setX(145);  // on définit la position de l'ImageView (x)
                    imageView.setFitHeight(stage.getHeight() - 20 - 105); // on définit la taille de l'ImageView (hauteur)
                    imageView.setFitWidth(stage.getWidth() - 160); // on définit la taille de l'ImageView (largeur)
                }
            }
        };

        stage.widthProperty().addListener(stageSizeListener); // on ajoute le listener de la largeur à la fenêtre
        stage.heightProperty().addListener(stageSizeListener); // on ajoute le listener de la hauteur à la fenêtre

        stage.show(); // on affiche la fenêtre
        stage.resizableProperty().set(true); // on autorise le redimensionnement de la fenêtre
        Main.primaryStage = stage; // on stocke la fenêtre principale
    }
}
