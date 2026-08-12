package ru.bank.notification_service.infrastructure.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class SimpleDecrypt {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final byte[] KEY = "1234567890123456".getBytes(StandardCharsets.UTF_8); // 16 байт


    public static String decrypt(String encryptedPassword){
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedPassword);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ex){
            log.warn("Не удалось расшифровать пароль из Kafka");
            throw new RuntimeException("Не удалось расшифровать пароль из Kafka");
        }
    }

}
