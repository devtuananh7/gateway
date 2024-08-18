package bug.creator.simservice1.Service;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

@Slf4j
public class AESService {

    public static String keyGen(int keySize) {
        String keyStr = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize);
            SecretKey key = keyGenerator.generateKey();
            keyStr = Base64.getEncoder().encodeToString(key.getEncoded());
            log.info("AES key generated successfully");
        } catch (Exception e) {
            log.error("Error while generating AES key", e);
        }
        return keyStr;
    }

    public static String encrypt(String data, String secretKeyStr) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secretKeyStr);
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());

            String encryptedDataStr = Base64.getEncoder().encodeToString(encryptedData);

            log.info("Encrypted data: {}", encryptedDataStr);
            return encryptedDataStr;
        } catch (Exception e) {
            log.error("Error while encrypting data", e);
        }
        return null;
    }

    public static String decrypt(String data, String secretKeyStr) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secretKeyStr);
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            byte[] encryptedData = Base64.getDecoder().decode(data);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedData = cipher.doFinal(encryptedData);

            String decryptedDataStr = new String(decryptedData, "UTF-8");
            log.info("Decrypted data: {}", decryptedDataStr);
            return decryptedDataStr;
        } catch (Exception e) {
            log.error("Error while decrypting data", e);
        }
        return null;
    }

    public static void main(String[] args) {
        String data = """
                {
                        "field1": "dinh",
                        "field2": "tuan anh"
                    }""";
        String secretKey = keyGen(256);
        log.info("AES key: {}", secretKey);

        String encryptedData = encrypt(data, secretKey);
        log.info("AES Encrypted data: {}", encryptedData);
        String decryptedData = decrypt(encryptedData, secretKey);
        log.info("AES Decrypted data: {}", decryptedData);

        KeyPair keyPair = RSAService.keyGen(2048);

        byte[] privateBytes = keyPair.getPrivate().getEncoded();
        String base64PrivateKey = Base64.getEncoder().encodeToString(privateBytes);

        byte[] publicBytes = keyPair.getPublic().getEncoded();
        String base64PublicKey = Base64.getEncoder().encodeToString(publicBytes);

        String encryptedAESData = RSAService.encryptWithPublicKey(secretKey, base64PublicKey);
        log.info("RSA Flow 1: Encrypted AES key: {}", encryptedAESData);
        log.info("----------------------------------------------------------------");
        String decryptedAESData = RSAService.decryptWithPrivateKey(encryptedAESData, base64PrivateKey);
        log.info("RSA Flow1: Decrypted AES key: {}", decryptedAESData);

        encryptedAESData = RSAService.encryptWithPrivateKey(secretKey, base64PrivateKey);
        log.info("RSA Flow 2: Encrypted AES key: {}", encryptedAESData);
        log.info("----------------------------------------------------------------");
        decryptedAESData = RSAService.decryptWithPublicKey(encryptedAESData, base64PublicKey);
        log.info("RSA Flow 2: Decrypted AES key: {}", decryptedAESData);
    }
}
