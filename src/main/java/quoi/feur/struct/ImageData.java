package quoi.feur.struct;

import lombok.Data; // bonjour, la flemme de faire des getters/setters

import java.util.ArrayList;
import java.util.List;

public @Data class ImageData {
    private final String name;
    private String filename;
    private final List<String> tags = new ArrayList<>();
    private String hashedPassword;

    public boolean isLocked() {
        return hashedPassword != null;
    }
}
