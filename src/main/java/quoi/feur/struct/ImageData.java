package quoi.feur.struct;

import lombok.Data; // bonjour, la flemme de faire des getters/setters
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public @Data class ImageData {
    private final String name;
    @NonNull
    private String filename;
    private final List<String> tags = new ArrayList<>();
    private String hashedPassword;

    /*public ImageData(String name, String filename) {
        this.name = name;
        this.filename = filename;
    }*/

    public boolean isLocked() {
        return hashedPassword != null;
    }
}
