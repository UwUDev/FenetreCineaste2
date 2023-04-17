package quoi.feur.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import quoi.feur.struct.ImageData;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ImageManager {
    INSTANCE; // on utilise un singleton pour pouvoir accéder à l'instance de la classe depuis n'importe quelle classe et celle ne peut entre instanciée qu'une seule fois

    public static ImageManager getInstance() { // pour ma satisfaction personnelle
        return INSTANCE;
    }
    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create(); // comme ça c'est beau

    @Getter
    private final List<ImageData> images = new ArrayList<>(); // liste des informations des images

    public void addImage(ImageData imageData) { // fonction qui permet d'ajouter une image
        images.add(imageData);
    }

    @SneakyThrows // bonjour, la flemme de faire des try/catch
    public void save() { // fonction qui permet de sauvegarder les images
        FileWriter writer = new FileWriter("images.json"); // on crée un FileWriter
        writer.write(gson.toJson(images)); // on écrit les images dans le fichier
        writer.close(); // on ferme le FileWriter
    }

    @SneakyThrows // bonjour, la flemme de faire des try/catch
    public void load() { // fonction qui permet de charger les informations des images
        images.clear(); // on vide la liste des images
        images.addAll(Arrays.asList(gson.fromJson(new FileReader("images.json"), ImageData[].class))); // on ajoute les images à la liste
    }
}
