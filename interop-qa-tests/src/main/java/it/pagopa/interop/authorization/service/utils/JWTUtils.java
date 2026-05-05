package it.pagopa.interop.authorization.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
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

    public static void setClaim(JwtBuilder builder, String name, Object value) {
        // 1. Gestione del "not found": se il valore è null o stringa vuota, non aggiungiamo il claim
        if (value == null || (value instanceof String s && s.isBlank())) {
            return;
        }

        // 2. Trasformazione del valore: passiamo il valore attraverso parseMaybeUuid
        // Questo permette di mappare la stringa "INVALID_UUID" (dalla tabella) a "not-a-uuid"
        // o di lasciare "not-a-uuid" se passato direttamente.
        Object finalValue = value;
        if (value instanceof String s) {
            finalValue = parseMaybeUuid(s);
        }

        // 3. Impostazione del claim sul builder
        builder.claim(name, finalValue);
    }

    public static void setHeader(JwtBuilder builder, String name, String value) {
        if (value == null || value.isBlank()) return;
        builder.header().add(name, value).and();
    }

    public static Object parseAud(String raw) {
        if (raw == null || raw.isBlank()) return null;
        // supporta singolo o multiplo separato da |
        if (raw.contains("|")) return java.util.List.of(raw.split("\\|"));
        return raw;
    }

    public static Long parseEpoch(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String value = raw.trim();
        java.time.Instant now = java.time.Instant.now();

        if ("now".equals(value)) {
            return now.getEpochSecond();
        }

        if (value.startsWith("now+") || value.startsWith("now-")) {
            char operator = value.charAt(3);
            String secondsPart = value.substring(4).trim();

            if (secondsPart.isEmpty()) {
                throw new IllegalArgumentException("Offset temporale mancante in: " + raw);
            }

            long seconds;
            try {
                seconds = Long.parseLong(secondsPart);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Offset temporale non valido in '" + raw + "'. Atteso formato now+<secondi> o now-<secondi>", e
                );
            }

            if (operator == '+') {
                return now.plusSeconds(seconds).getEpochSecond();
            } else {
                return now.minusSeconds(seconds).getEpochSecond();
            }
        }

        return Long.parseLong(value);
    }

    public static Object parseMaybeUuid(String raw) {
        if (raw == null || raw.isBlank()) return null;
        if ("INVALID_UUID".equals(raw)) return "not-a-uuid";
        return raw;
    }

    public static void removeClaim(JwtBuilder builder, String claimName) {
        builder.claim(claimName, null);
    }

    public static void setRawPayload(JwtBuilder builder, String raw) {
        if (raw == null) return;

        // Rimuove tutti i claims impostati finora per evitare l'IllegalStateException
        // Nelle versioni recenti, passare una mappa nulla o vuota resetta i claims interni
        builder.setClaims(new java.util.HashMap<>());

        // Ora puoi impostare il contenuto grezzo senza conflitti
        builder.content(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
