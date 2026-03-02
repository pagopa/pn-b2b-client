package it.pagopa.interop.authorization.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.JsonParseException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public final class JWTUtils {
    @Data
    @AllArgsConstructor(staticName = "of")
    public static class JWTPojo {
        private Map<String, Object> header;
        private Map<String, Object> payload;
        private String signature;
    }

    private JWTUtils() {
        throw new AssertionError("Can't instantiate utility class");
    }

    public static Map<String, Object> decodeJwtPayload(String jwt) {
        return decodeJwt(jwt).getPayload();
    }

    private static Map<String, Object> decodeJwtPart(String part) {
        try {
            byte[] decodedJsonPayload = Base64.getUrlDecoder().decode(part);
            return new ObjectMapper().readValue(decodedJsonPayload, Map.class);
        } catch (IOException e) {
            throw new JsonParseException("Errore durante la decodifica del token JWT", e);
        }
    }

    public static JWTPojo decodeJwt(String jwt) {
        String[] parts = jwt.split("\\.");
        return JWTPojo.of(
            decodeJwtPart(parts[0]),
            decodeJwtPart(parts[1]),
            parts[2]
        );
    }

    /* TODO 08/07/2025: codifica non ancora funzionante. Il risultato atteso è che "encodeJwt" sia
    *   l'operazione inversa di "decodeJwt"; tuttavia, dato in input un token JWT, la sequenza
    *   decodifica con decodeJwt --> ricodifica con encodeJwt non produce ancora un risultato
    *   identico all'input.*/
    //public static String encodeJwt(JWTPojo jwt) {
    private static String encodeJwt(JWTPojo jwt) {
        return String.format("%s.%s.%s",
            encodeJwtPart(jwt.getHeader()),
            encodeJwtPart(jwt.getPayload()),
            jwt.getSignature());
    }

    private static String encodeJwtPart(Map<String, Object> part) {
        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(part);

            /*ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ObjectMapper().writeValue(out, part);
            byte[] bytes = out.t*/

            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            //return new String(encoded, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            throw new RuntimeException("Errore durante la codifica del token JWT", e);
        }
    }
    /* ********************************************************************************/
}
