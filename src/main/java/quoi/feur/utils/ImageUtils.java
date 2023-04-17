package quoi.feur.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    private static final List<BufferedImage> history = new ArrayList<>();
    public static BufferedImage toBufferedImage(javafx.scene.image.Image image) {
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        BufferedImage bufImageARGB = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        SwingFXUtils.fromFXImage(image, bufImageARGB);
        return bufImageARGB;
    }

    public static Image toImage(BufferedImage bufferedImage) { // fonction qui permet de convertir une BufferedImage en Image
        return SwingFXUtils.toFXImage(bufferedImage, null); // on convertit l'image
    }

    public static BufferedImage flipVertical(BufferedImage image) {
        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                flipped.setRGB(x, y, image.getRGB(x, image.getHeight() - y - 1));
            }
        }
        return flipped; // on retourne l'image retournée verticalement
    }

    public static BufferedImage flipHorizontal(BufferedImage image) {
        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                flipped.setRGB(x, y, image.getRGB(image.getWidth() - x - 1, y));
            }
        }
        return flipped; // on retourne l'image retournée horizontalement
    }

    public static BufferedImage RGBtoGBR(BufferedImage image) {
        BufferedImage modified = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);
                color = new Color(color.getGreen(), color.getBlue(), color.getRed());
                modified.setRGB(x, y, color.getRGB());
            }
        }
        return modified; // on retourne l'image convertie
    }

    public static BufferedImage sepia(BufferedImage image) {
        BufferedImage modified = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);
                color = new Color(
                        Math.min(255, (int) (0.393 * color.getRed() + 0.769 * color.getGreen() + 0.189 * color.getBlue())),
                        Math.min(255, (int) (0.349 * color.getRed() + 0.686 * color.getGreen() + 0.168 * color.getBlue())),
                        Math.min(255, (int) (0.272 * color.getRed() + 0.134 * color.getGreen() + 0.131 * color.getBlue()))
                );
                modified.setRGB(x, y, color.getRGB()); // on applique la nouvelle couleur à l'image
            }
        }
        return modified; // on retourne l'image convertie
    }

    @SuppressWarnings("DuplicatedCode")
    //sources: https://fr.wikipedia.org/wiki/Filtre_de_Sobel et https://www.youtube.com/watch?v=QHvV4m389XI
    public static BufferedImage sobel(BufferedImage image) { // fonction qui permet d'appliquer un filtre de Sobel à une BufferedImage
        int width = image.getWidth(); // on récupère la largeur de l'image
        int height = image.getHeight(); // on récupère la hauteur de l'image

        int[][] gx = new int[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}}; // on crée les matrices de convolution pour X
        int[][] gy = new int[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}}; // on crée les matrices de convolution pour Y

        BufferedImage sobelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // on crée une nouvelle BufferedImage de la même taille et du même type que l'image d'origine

        for (int x = 1; x < width - 1; x++) { // on parcourt l'image en largeur
            for (int y = 1; y < height - 1; y++) { // on parcourt l'image en hauteur

                int pixelX = ( // on applique la formule de convolution pour X
                        (gx[0][0] * getRed(image.getRGB(x - 1, y - 1))) +
                                (gx[0][1] * getRed(image.getRGB(x, y - 1))) +
                                (gx[0][2] * getRed(image.getRGB(x + 1, y - 1))) +
                                (gx[1][0] * getRed(image.getRGB(x - 1, y))) +
                                (gx[1][1] * getRed(image.getRGB(x, y))) +
                                (gx[1][2] * getRed(image.getRGB(x + 1, y))) +
                                (gx[2][0] * getRed(image.getRGB(x - 1, y + 1))) +
                                (gx[2][1] * getRed(image.getRGB(x, y + 1))) +
                                (gx[2][2] * getRed(image.getRGB(x + 1, y + 1)))
                );

                int pixelY = ( // on applique la formule de convolution pour Y
                        (gy[0][0] * getRed(image.getRGB(x - 1, y - 1))) +
                                (gy[0][1] * getRed(image.getRGB(x, y - 1))) +
                                (gy[0][2] * getRed(image.getRGB(x + 1, y - 1))) +
                                (gy[1][0] * getRed(image.getRGB(x - 1, y))) +
                                (gy[1][1] * getRed(image.getRGB(x, y))) +
                                (gy[1][2] * getRed(image.getRGB(x + 1, y))) +
                                (gy[2][0] * getRed(image.getRGB(x - 1, y + 1))) +
                                (gy[2][1] * getRed(image.getRGB(x, y + 1))) +
                                (gy[2][2] * getRed(image.getRGB(x + 1, y + 1)))
                );

                int magnitude = (int) Math.sqrt(pixelX * pixelX + pixelY * pixelY); // on calcule la magnitude

                sobelImage.setRGB(x, y, getRGB(magnitude, magnitude/2, (int) (magnitude/1.8f))); // on applique la nouvelle couleur à l'image
            }
        }
        return sobelImage; // on retourne l'image convertie
    }

    public static BufferedImage blackAndWhite(BufferedImage bufferedImage) { // fonction qui permet de convertir une image en noir et blanc
        int width = bufferedImage.getWidth(); // on récupère la largeur de l'image
        int height = bufferedImage.getHeight(); // on récupère la hauteur de l'image
        BufferedImage modified = new BufferedImage(width, height, bufferedImage.getType()); // on crée une nouvelle BufferedImage de la même taille et du même type que l'image d'origine
        for (int x = 0; x < width; x++) { // on parcourt l'image en largeur
            for (int y = 0; y < height; y++) { // on parcourt l'image en hauteur
                int rgb = bufferedImage.getRGB(x, y); // on récupère la couleur du pixel
                Color color = new Color(rgb, true); // on crée un objet Color à partir de la couleur du pixel
                int gray = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()); // on calcule la valeur du gris à partir de la couleur du pixel grâce à la formule de luminance
                color = new Color(gray, gray, gray); // on crée un objet Color à partir de la valeur du gris
                modified.setRGB(x, y, color.getRGB()); // on applique la nouvelle couleur à l'image
            }
        }
        return modified; // on retourne l'image convertie
    }

    private static int getRed(int rgb) { // fonction qui permet de récupérer la valeur du rouge d'une couleur
        return (rgb >> 16) & 0xFF; // on décale les bits de la couleur de 16 bits vers la droite et on applique un masque binaire pour récupérer les 8 bits de poids fort
    }

    private static int getRGB(int r, int g, int b) { // fonction qui permet de créer une couleur à partir de ses valeurs de rouge, vert et bleu
        return (r << 16) | (g << 8) | b; // on décale les bits de la couleur de 16 bits vers la gauche et on applique un masque binaire pour récupérer les 8 bits de poids fort
    }

    public static boolean hasHistory() { // fonction qui permet de savoir si l'historique est vide ou non
        return !history.isEmpty(); // on retourne le résultat de la fonction isEmpty() de la liste
    }

    public static void addToHistory(BufferedImage image) { // fonction qui permet d'ajouter une image à l'historique
        if (history.size() == 10) { // si l'historique contient déjà 10 images
            history.remove(0); // on supprime la première image de l'historique
        }
        history.add(image); // on ajoute l'image à l'historique
    }

    public static BufferedImage popHistory() { // fonction qui permet de récupérer la dernière image de l'historique
        return history.remove(history.size() - 1); // on supprime la dernière image de l'historique et on la retourne
    }


    public static BufferedImage blackAndWhite(BufferedImage bufferedImage) {
        BufferedImage modified = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int rgb = bufferedImage.getRGB(x, y);
                Color color = new Color(rgb, true);
                int gray = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
                color = new Color(gray, gray, gray);
                modified.setRGB(x, y, color.getRGB());
            }
        }
        return modified;
    }

    public static void saveImage(BufferedImage image, String path) { // fonction qui permet de sauvegarder une image
        try {
            ImageIO.write(image, "png", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
