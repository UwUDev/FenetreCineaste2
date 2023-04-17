package quoi.feur.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import quoi.feur.struct.ImageData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ImageManager {
    INSTANCE;

    public static ImageManager getInstance() { // pour ma satisfaction personnelle
        return INSTANCE;
    }
    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create(); // comme ça c'est beau

    @Getter
    private final List<ImageData> images = new ArrayList<>();

    public void addImage(ImageData imageData) {
        images.add(imageData);
    }

    @SneakyThrows // bonjour, la flemme de faire des try/catch
    public void save() {
        FileWriter writer = new FileWriter("images.json");
        writer.write(gson.toJson(images));
        writer.close();
    }

    @SneakyThrows // bonjour, la flemme de faire des try/catch
    public void load() {
        images.clear();
        images.addAll(Arrays.asList(gson.fromJson(new FileReader("images.json"), ImageData[].class)));
    }


}
