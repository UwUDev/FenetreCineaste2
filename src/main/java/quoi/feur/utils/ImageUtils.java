package quoi.feur.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    public static Image toImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static BufferedImage invert(BufferedImage image) {
        BufferedImage modified = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);
                color = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
                modified.setRGB(x, y, color.getRGB());
            }
        }
        return modified;
    }

    public static BufferedImage flipVertical(BufferedImage image) {
        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                flipped.setRGB(x, y, image.getRGB(x, image.getHeight() - y - 1));
            }
        }
        return flipped;
    }

    public static BufferedImage flipHorizontal(BufferedImage image) {
        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                flipped.setRGB(x, y, image.getRGB(image.getWidth() - x - 1, y));
            }
        }
        return flipped;
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
        return modified;
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
                modified.setRGB(x, y, color.getRGB());
            }
        }
        return modified;
    }

    public static BufferedImage sobel(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][] gx = new int[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] gy = new int[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        BufferedImage sobelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {

                int pixelX = (
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

                int pixelY = (
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
                int magnitude = (int) Math.sqrt(pixelX * pixelX + pixelY * pixelY);

                sobelImage.setRGB(x, y, getRGB(magnitude, magnitude/2, (int) (magnitude/1.8f)));
            }
        }
        return sobelImage;
    }

    private static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    private static int getRGB(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    public static boolean hasHistory() {
        return !history.isEmpty();
    }

    public static void addToHistory(BufferedImage image) {
        if (history.size() == 10) {
            history.remove(0);
        }
        history.add(image);
    }

    public static BufferedImage popHistory() {
        return history.remove(history.size() - 1);
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
}
