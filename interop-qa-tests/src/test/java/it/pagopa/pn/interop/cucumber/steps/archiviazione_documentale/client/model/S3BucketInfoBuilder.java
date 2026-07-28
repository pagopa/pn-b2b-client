package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Builder intelligente per {@link BucketUrl}.
 * Accetta un path completo (es. base/prefix/file) e calcola automaticamente base, prefix e key.
 */
public class S3BucketInfoBuilder {

    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM");

    private String fullPath;

    private S3BucketInfoBuilder() {}

    public static S3BucketInfoBuilder builder() {
        return new S3BucketInfoBuilder();
    }

    public S3BucketInfoBuilder fullPath(String fullPath) {
        this.fullPath = fullPath;
        return this;
    }

    public BucketUrl build() {
        if (fullPath == null || fullPath.isBlank()) {
            throw new IllegalStateException("Il percorso completo S3 non può essere nullo o vuoto.");
        }

        // Normalizzo il path (tolgo slash multipli o finali)
        String normalized = replaceDateTokens(fullPath.trim());
        while (normalized.contains("//")) {
            normalized = normalized.replace("//", "/");
        }
        if (normalized.endsWith("/") && normalized.length() > 1) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        String[] parts = normalized.split("/");

        if (parts.length == 0) {
            throw new IllegalArgumentException("Percorso non valido: " + fullPath);
        }

        String bucket = parts[0];
        String prefix = "";
        String key = "";

        if (parts.length > 1) {
            String rest = String.join("/", java.util.Arrays.copyOfRange(parts, 1, parts.length));
            boolean endsWithFile = rest.matches(".*\\.[A-Za-z0-9]{2,6}$");

            if (endsWithFile) {
                int lastSlash = rest.lastIndexOf('/');
                prefix = (lastSlash > 0) ? rest.substring(0, lastSlash + 1) : "";
                key = rest;
            } else {
                prefix = rest.endsWith("/") ? rest : rest + "/";
                key = prefix;
            }
        }

        return new BucketUrl(bucket, prefix, key);
    }

    private String replaceDateTokens(String input) {
        LocalDate today = LocalDate.now();
        return input
                .replace(":year", String.valueOf(today.getYear()))
                .replace(":onlyMonth", today.format(MONTH_FORMAT))
                .replace(":onlyDay", today.format(DAY_FORMAT));
    }
}
