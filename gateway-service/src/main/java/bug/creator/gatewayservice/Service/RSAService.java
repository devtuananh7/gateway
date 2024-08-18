package bug.creator.gatewayservice.Service;


import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/*
 * RSAService class provides methods to generate RSA key pair, encrypt and decrypt data using RSA algorithm.
 * RSA algorithm is used for secure data transmission and data integrity verification.
 * @author bugCreator
 */

@Slf4j
public class RSAService {

    public static KeyPair keyGen(int keySize) {
        KeyPair result = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);
            result = keyPairGenerator.generateKeyPair();
            log.info("RSA key pair generated successfully");
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while generating RSA key pair", e);
        }
        return result;
    }


    /**
     *     -------- FLOW 1: Encrypt with public key, decrypt with private key --------
     *     Encrypt data with public key is for transferring data securely to the server.
     *     Decrypt data with private key is for verifying the data integrity.
     */
    public static String encryptWithPublicKey(String data, String publicKeyStr) {
        String result = null;
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedData = cipher.doFinal(data.getBytes());
            result = Base64.getEncoder().encodeToString(encryptedData);

            log.info("Data encrypted successfully");
        } catch (Exception e) {
            log.error("Error while encrypting data", e);
        }
        return result;
    }

    public static String decryptWithPrivateKey(String data, String privateKeyStr) {
        String result = null;
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            byte[] encryptedData = Base64.getDecoder().decode(data);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = cipher.doFinal(encryptedData);
            result = new String(decryptedData);
            log.info("Data decrypted successfully");
        } catch (Exception e) {
            log.error("Error while decrypting data", e);
        }
        return result;
    }
    /*
     *               -------- END FLOW 1 --------
     */

    /**
     *    -------- FLOW 2: Encrypt with private key, decrypt with public key --------
     *    Encrypt data with private key is for signing the data.
     *    Decrypt data with public key is for verifying the data integrity.
     */
    public static String encryptWithPrivateKey(String data, String privateKeyStr) {
        String result = null;
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            byte[] encryptedData = cipher.doFinal(data.getBytes());
            result = Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            log.error("Error while encrypting data", e);
        }
        return result;
    }

    public static String decryptWithPublicKey(String data, String publicKeyStr) {
        String result = null;
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            byte[] encryptedData = Base64.getDecoder().decode(data);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedData = cipher.doFinal(encryptedData);
            result = new String(decryptedData);
        } catch (Exception e) {
            log.error("Error while decrypting data", e);
        }
        return result;
    }
    /*
     *               -------- END FLOW 2 --------
     */

}
