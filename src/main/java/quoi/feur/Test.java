package quoi.feur;

import quoi.feur.utils.CryptoUtils;

import java.io.File;
import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        File input = new File("images/pain.png");
        File output = new File("images/" + UUID.randomUUID().toString().substring(0, 8) + ".locked");
        String pass = "feur";

        CryptoUtils.encryptFile(pass, input, output);
        System.out.println("Hash: " + CryptoUtils.hashSha256(pass));
    }
}
