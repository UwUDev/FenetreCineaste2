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

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println(getClass().getResource("main.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Fenêtre Cinéaste 2.0");
        primaryStage.setScene(new Scene(root, 950, 550));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        primaryStage.setMinWidth(925);
        primaryStage.setMinHeight(555);

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            if (MainController.instance != null) {
                ImageView imageView = MainController.instance.imageView;
                if (imageView != null) {
                    imageView.setX(145);
                    imageView.setFitHeight(primaryStage.getHeight() - 20 - 105);
                    imageView.setFitWidth(primaryStage.getWidth() - 160);
                }
            }
        };

        primaryStage.widthProperty().addListener(stageSizeListener);
        primaryStage.heightProperty().addListener(stageSizeListener);

        primaryStage.show();
        primaryStage.resizableProperty().set(true);
    }
}
