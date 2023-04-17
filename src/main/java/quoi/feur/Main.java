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
    public static Stage primaryStage;
    public static void main(String[] args) {
        ImageManager.getInstance().load();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(getClass().getResource("main.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage.setTitle("Fenêtre Cinéaste 2.0");
        stage.setScene(new Scene(root, 950, 550));
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        stage.setMinWidth(925);
        stage.setMinHeight(555);

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            if (MainController.instance != null) {
                ImageView imageView = MainController.instance.imageView;
                if (imageView != null) {
                    imageView.setX(145);
                    imageView.setFitHeight(stage.getHeight() - 20 - 105);
                    imageView.setFitWidth(stage.getWidth() - 160);
                }
            }
        };

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);

        stage.show();
        stage.resizableProperty().set(true);
        Main.primaryStage = stage;
    }
}
