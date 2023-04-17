package quoi.feur.utils;

import lombok.SneakyThrows;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class CryptoUtils {
    private static final String leSel = "c'est un sel";

    @SneakyThrows
    public static String hashSha256(String password) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static boolean isValidPassword(String password, String hash) {
        return hashSha256(password).equals(hash);
    }
    @SneakyThrows
    public static SecretKey getKeyFromPassword(String password) {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), leSel.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    @SneakyThrows
    public static void encryptFile(String password, File inputFile, File outputFile) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = generateIv();
        cipher.init(Cipher.ENCRYPT_MODE, getKeyFromPassword(password), ivSpec);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            // Écrire l'IV au début du fichier chiffré
            outputStream.write(ivSpec.getIV());

            gloubiBoulga(cipher, inputStream, outputStream);
        }
    }

    @SneakyThrows
    public static void decryptFile(String password, File inputFile, File outputFile) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            // Lire l'IV à partir du fichier chiffré
            byte[] iv = new byte[16];
            //noinspection ResultOfMethodCallIgnored
            inputStream.read(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, getKeyFromPassword(password), ivSpec);

            gloubiBoulga(cipher, inputStream, outputStream);
        }
    }

    private static void gloubiBoulga(Cipher cipher, FileInputStream inputStream, FileOutputStream outputStream) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
    }
}