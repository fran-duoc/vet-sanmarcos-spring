package com.veterinaria.microservices.security;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "VeterinariaSanMarcosSecretKeyForJwtAuthenticationBearerTokens2026";
    private static final long EXPIRATION_TIME = 86400000L; // 24 horas

    public String generarToken(String username, String rol) {
        long now = System.currentTimeMillis();
        long exp = now + EXPIRATION_TIME;

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = String.format("{\"sub\":\"%s\",\"rol\":\"%s\",\"iat\":%d,\"exp\":%d}",
                username, rol, now / 1000, exp / 1000);

        String encodedHeader = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

        String dataToSign = encodedHeader + "." + encodedPayload;
        String signature = hmacSha256(dataToSign, SECRET_KEY);

        return dataToSign + "." + signature;
    }

    public boolean validarToken(String token) {
        if (token == null || !token.contains(".")) {
            return false;
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }

        String dataToSign = parts[0] + "." + parts[1];
        String expectedSignature = hmacSha256(dataToSign, SECRET_KEY);

        return MessageDigest.isEqual(parts[2].getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8));
    }

    private static String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error firmando JWT", e);
        }
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
