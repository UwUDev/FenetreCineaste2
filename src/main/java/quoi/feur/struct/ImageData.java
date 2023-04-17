package quoi.feur.struct;

import lombok.Data; // bonjour, la flemme de faire des getters/setters
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public @Data class ImageData {
    private final String name; // nom de l'image
    @NonNull
    private String filename; // nom du fichier
    private final List<String> tags = new ArrayList<>(); // liste des tags de l'image
    private String hashedPassword; // mot de passe hashé de l'image

    public boolean isLocked() {
        return hashedPassword != null; // si le mot de passe est null, l'image n'est pas verrouillée
    }
}
