package pe.edu.sajv.sajv_qos.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec secretKey;

    public CryptoService(
            @Value("${sajv.qos.encryption-key}") String keyBase64) {

        byte[] key = Base64.getDecoder().decode(keyBase64);

        if (key.length != 32) {
            throw new IllegalArgumentException(
                "La clave AES debe tener exactamente 32 bytes."
            );
        }

        this.secretKey = new SecretKeySpec(key, "AES");
    }

    public String encrypt(String plainText) {

        if (plainText == null || plainText.isBlank()) {
            return null;
        }

        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey,
                new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] encrypted = cipher.doFinal(
                plainText.getBytes(StandardCharsets.UTF_8)
            );

            ByteBuffer buffer = ByteBuffer.allocate(
                iv.length + encrypted.length
            );

            buffer.put(iv);
            buffer.put(encrypted);

            return Base64.getEncoder().encodeToString(buffer.array());

        } catch (Exception e) {
            throw new IllegalStateException("Error al cifrar credencial", e);
        }
    }

    public String decrypt(String encryptedText) {

        if (encryptedText == null || encryptedText.isBlank()) {
            return null;
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);

            if (decoded.length <= IV_LENGTH) {
                throw new IllegalArgumentException("Texto cifrado inválido.");
            }

            ByteBuffer buffer = ByteBuffer.wrap(decoded);

            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);

            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey,
                new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new IllegalStateException(
                "No fue posible descifrar la credencial.", e
            );
        }
    }
}