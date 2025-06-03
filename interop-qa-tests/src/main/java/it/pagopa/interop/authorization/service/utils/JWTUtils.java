package it.pagopa.interop.authorization.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public final class JWTUtils {

    public JWTUtils() {
        throw new AssertionError("Can't instantiate utility class");
    }

    public static Map<String, Object> decodeJwtPayload(String jwt) {
        try {
            String jsonPayload = jwt.split("\\.")[1];
            byte[] decodedJsonPayload = Base64.getUrlDecoder().decode(jsonPayload);
            return new ObjectMapper().readValue(decodedJsonPayload, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Errore durante la decodifica del token JWT", e);
        }
    }
}
