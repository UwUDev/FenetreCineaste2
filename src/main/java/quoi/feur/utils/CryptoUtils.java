package quoi.feur.utils;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class CryptoUtils {
    private static final String leSel = "c'est un sel";

    @SneakyThrows
    public static String hashSha256(String password) { // fonction qui permet de hasher un mot de passe
        MessageDigest digest = MessageDigest.getInstance("SHA-256"); // on crée un MessageDigest en utilisant l'algorithme SHA-256
        byte[] hash = digest.digest(password.getBytes()); // on hash le mot de passe
        StringBuilder hexString = new StringBuilder(); // on crée une chaîne de caractères
        for (byte b : hash) { // on convertit le hash en chaîne de caractères
            String hex = Integer.toHexString(0xff & b); // on convertit le byte en hexadécimal
            if (hex.length() == 1) hexString.append('0'); // si le byte est inférieur à 16, on ajoute un 0
            hexString.append(hex); // on ajoute le byte au hash
        }
        return hexString.toString(); // on retourne le hash
    }

    public static boolean isValidPassword(String password, String hash) { // fonction qui permet de vérifier si un mot de passe est valide
        return hashSha256(password).equals(hash); // on vérifie si le hash du mot de passe est égal au hash du mot de passe
    }

    @SneakyThrows
    public static SecretKey getKeyFromPassword(String password) { // fonction qui permet de générer une clé à partir d'un mot de passe
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); // on crée un SecretKeyFactory en utilisant l'algorithme PBKDF2WithHmacSHA256 (PBKDF2 = Password-Based Key Derivation Function 2 et HMAC = Keyed-Hash Message Authentication Code)
        // on a préféré utiliser PBKDF2WithHmacSHA256 plutôt que PBKDF2WithHmacSHA1 car SHA1 est considéré comme obsolète et SHA256 est plus sécurisé
        KeySpec spec = new PBEKeySpec(password.toCharArray(), leSel.getBytes(), 65536, 256); // on crée un KeySpec en utilisant le mot de passe, le sel et 65536 itérations
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES"); // on retourne une SecretKeySpec en utilisant la clé générée par le SecretKeyFactory
    }

    public static IvParameterSpec generateIv() { // fonction qui permet de générer un vecteur d'initialisation
        byte[] iv = new byte[16]; // on crée un tableau de 16 octets, car AES utilise un bloc de 128 bits
        new SecureRandom().nextBytes(iv);  // on remplit le tableau avec des octets aléatoires
        return new IvParameterSpec(iv); // on retourne un IvParameterSpec en utilisant le tableau
    }

    @SneakyThrows
    public static void encryptFile(String password, File inputFile, File outputFile) { // fonction qui permet de chiffrer un fichier
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // on crée un Cipher en utilisant l'algorithme AES/CBC/PKCS5Padding
        IvParameterSpec ivSpec = generateIv(); // on génère un vecteur d'initialisation
        cipher.init(Cipher.ENCRYPT_MODE, getKeyFromPassword(password), ivSpec); // on initialise le Cipher en mode chiffrage, en utilisant la clé générée à partir du mot de passe et le vecteur d'initialisation

        try (FileInputStream inputStream = new FileInputStream(inputFile); // on crée un FileInputStream pour lire le fichier d'entrée
             FileOutputStream outputStream = new FileOutputStream(outputFile)) { // on crée un FileOutputStream pour écrire dans le fichier de sortie

            outputStream.write(ivSpec.getIV()); // on écrit le vecteur d'initialisation dans le fichier de sortie

            gloubiBoulga(cipher, inputStream, outputStream); // on chiffre le fichier
        }
    }

    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void decryptFile(String password, File inputFile, File outputFile) { // fonction qui permet de déchiffrer un fichier
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // on crée un Cipher en utilisant l'algorithme AES/CBC/PKCS5Padding

        try (FileInputStream inputStream = new FileInputStream(inputFile); // on crée un FileInputStream pour lire le fichier d'entrée
             FileOutputStream outputStream = new FileOutputStream(outputFile)) { // on crée un FileOutputStream pour écrire dans le fichier de sortie

            byte[] iv = new byte[16]; // on crée un tableau de 16 octets, car AES utilise un bloc de 128 bits
            inputStream.read(iv); // on lit le vecteur d'initialisation dans le fichier d'entrée
            IvParameterSpec ivSpec = new IvParameterSpec(iv); // on crée un IvParameterSpec en utilisant le tableau

            cipher.init(Cipher.DECRYPT_MODE, getKeyFromPassword(password), ivSpec); // on initialise le Cipher en mode déchiffrement, en utilisant la clé générée à partir du mot de passe et le vecteur d'initialisation

            gloubiBoulga(cipher, inputStream, outputStream); // on déchiffre le fichier
        }
    }

    @SneakyThrows
    private static void gloubiBoulga(Cipher cipher, FileInputStream inputStream, FileOutputStream outputStream) { // fonction qui permet de chiffrer ou déchiffrer un fichier
        byte[] buffer = new byte[64]; // on crée un tableau de 64 octets pour le buffer
        int bytesRead; // on crée une variable pour stocker le nombre d'octets lus
        while ((bytesRead = inputStream.read(buffer)) != -1) { // on lit le fichier d'entrée tant qu'on n'est pas à la fin du fichier
            byte[] output = cipher.update(buffer, 0, bytesRead); // on chiffre ou déchiffre le buffer
            if (output != null) { // si le buffer n'est pas vide
                outputStream.write(output); // on écrit le buffer dans le fichier de sortie
            }
        }
        byte[] outputBytes = cipher.doFinal(); // on chiffre ou déchiffre le dernier bloc
        if (outputBytes != null) { // si le dernier bloc n'est pas vide
            outputStream.write(outputBytes); // on écrit le dernier bloc dans le fichier de sortie
        }
    }
}