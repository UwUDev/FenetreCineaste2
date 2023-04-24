package quoi.feur.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import quoi.feur.struct.ImageData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ImageManager {
    INSTANCE; // on utilise un singleton pour pouvoir accéder à l'instance de la classe depuis n'importe quelle classe et celle ne peut entre instanciée qu'une seule fois

    public static ImageManager getInstance() { // pour ma satisfaction personnelle
        return INSTANCE;
    }

    @Getter
    private final List<ImageData> images = new ArrayList<>(); // liste des informations des images

    public void addImage(ImageData imageData) { // fonction qui permet d'ajouter une image
        images.add(imageData);
    }

    /**
     * Établit une connexion à la base de données Derby en utilisant la chaîne de connexion 'dbURL'
     * contenant le nom de la base de données et le paramètre 'create=true' pour la créer si elle n'existe pas.
     * Renvoie la connexion à la base de données. Si une exception de type SQLException est levée,
     */
    private Connection getConnection() throws SQLException {
        String dbURL = "jdbc:derby:imagesDB;create=true";
        return DriverManager.getConnection(dbURL);
    }

    /**
     * Initialise la base de données en créant une table "images" si elle n'existe pas déjà.
     * Utilise une connexion à la base de données obtenue à partir de la méthode getConnection().
     * Si une exception de type SQLException est levée, elle est affichée en utilisant la méthode printStackTrace().
     */
    private void initializeDatabase() {
        try (Connection connection = getConnection()) {
            // Récupère les métadonnées de la base de données
            DatabaseMetaData metaData = connection.getMetaData();

            // Récupère les tables de la base de données avec le nom "IMAGES"
            ResultSet resultSet = metaData.getTables(null, null, "IMAGES", null);

            // Si la table "images" n'existe pas, la crée avec les champs nécessaires
            if (!resultSet.next()) {
                String createTableQuery = "CREATE TABLE images ("
                        + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                        + "name VARCHAR(255) NOT NULL, "
                        + "filename VARCHAR(255) NOT NULL, "
                        + "tags CLOB, "
                        + "hashedPassword VARCHAR(255), "
                        + "PRIMARY KEY (id))";

                try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllImages() { // fonction qui vide la DB en supprimant la table images
        String deleteAllImagesQuery = "DELETE FROM images";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(deleteAllImagesQuery)) {

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteImage(String imageName) { // permet de supprimer l'image portant le filename dans la DB sachant que le filename est aleatoire
        String deleteImageQuery = "DELETE FROM images WHERE filename = ?";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(deleteImageQuery)) {
            pstmt.setString(1, imageName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enregistre les données de la liste d'images "images" dans la base de données.
     * Utilise une connexion à la base de données obtenue à partir de la méthode getConnection().
     * Pour chaque image de la liste, la fonction exécute une requête SELECT pour vérifier si une image similaire existe déjà
     * (même nom de fichier ou même hachage de fichier). Si une telle image n'existe pas, elle est ajoutée à la base de données
     * en exécutant une requête INSERT. Si une telle image existe, elle peut être mise à jour en fonction de ce que vous voulez faire.
     * Si une exception de type SQLException est levée, elle est affichée en utilisant la méthode printStackTrace().
     */
    public void save() {
        try (Connection connection = getConnection()) {
            for (ImageData imageData : images) {
                String selectSql = "SELECT name, filename, hashedPassword FROM images WHERE filename = ? OR hashedPassword = ?";
                try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                    selectStmt.setString(1, imageData.getFilename());
                    selectStmt.setString(2, imageData.getHashedPassword());
                    ResultSet resultSet = selectStmt.executeQuery();
                    if (!resultSet.next()) {
                        String insertSql = "INSERT INTO images (name, filename, tags, hashedPassword) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                            insertStmt.setString(1, imageData.getName());
                            insertStmt.setString(2, imageData.getFilename());
                            insertStmt.setString(3, String.join(",", imageData.getTags()));
                            insertStmt.setString(4, imageData.getHashedPassword());
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge les données de la base de données dans la liste d'images "images".
     * Utilise une connexion à la base de données obtenue à partir de la méthode getConnection().
     * Les données sont récupérées en exécutant une requête SELECT sur la table "images".
     * Si une exception de type SQLException est levée, elle est affichée en utilisant la méthode printStackTrace().
     */
    public void load() {
        try (Connection connection = getConnection()) {
            images.clear();

            //deleteAllImages(); permet de vider la DB lors des tests

            // Crée un objet Statement pour exécuter une requête SELECT sur la table "images"
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM images");

            // Parcours les résultats de la requête et ajoute chaque image à la liste d'images "images"
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String filename = resultSet.getString("filename");
                String tagsString = resultSet.getString("tags");
                List<String> tags = Arrays.asList(tagsString.split(","));
                String hashedPassword = resultSet.getString("hashedPassword");

                ImageData imageData = new ImageData(name, filename);
                imageData.getTags().addAll(tags);
                imageData.setHashedPassword(hashedPassword);
                images.add(imageData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayDatabaseContent() { // permet l'affichage de la DB pour les tests
        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM images");

            System.out.println("ID\tName\tFilename\tTags\t\tHashedPassword");
            System.out.println("---------------------------------------------------------------");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String filename = resultSet.getString("filename");
                String tags = resultSet.getString("tags");
                String hashedPassword = resultSet.getString("hashedPassword");

                System.out.printf("%d\t%s\t%s\t%s\t%s\n", id, name, filename, tags, hashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ImageManager() {
        initializeDatabase();
    }
}
