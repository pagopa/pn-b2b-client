package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArchivingUtils {
    public static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC);
    public record FileNameParts(String timestamp, String baseName, String extension) {}

    public static String now(){
        return TS_FORMAT.format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    public static Instant parse(String formattedTimestamp){
       return LocalDateTime.parse(formattedTimestamp, TS_FORMAT).toInstant(ZoneOffset.UTC);
    }

    public static Optional<Instant> extractTimestampFromS3Key(String key) {
        if (key == null || key.isEmpty()) {
            return Optional.empty();
        }

        // Estraggo SOLO il nome del file
        String filename = key.substring(key.lastIndexOf('/') + 1);

        // Cerco timestamp numerico all'inizio del filename
        // Es: 20250312123045_documento.pdf
        // Es: 20250312123045-documento.pdf
        // Es: 20250312123045documento.pdf
        Pattern p = Pattern.compile("(\\d{14})_([^\\.]+)\\.(.+)");
        Matcher m = p.matcher(filename);

        if (m.find()) {
            String tsString = m.group(1);
            try {
                LocalDateTime ldt = LocalDateTime.parse(tsString, TS_FORMAT);
                return Optional.of(ldt.toInstant(ZoneOffset.UTC));
            } catch (Exception ignored) {
            }
        }

        return Optional.empty();
    }

    public static String extractFilenameFromS3Key(String key) {
        if (key == null || key.isEmpty()) return null;
        return key.substring(key.lastIndexOf('/') + 1);
    }

    public static Optional<FileNameParts> parseFileName(String filename, FileType type) {
        if (filename == null) return Optional.empty();
        String regex = "(\\d{14})_([^\\.]+)\\.(.+)";
        if (type.getExpectedBaseName().startsWith("%")) {
            regex = type.getExpectedBaseName().replaceAll("%", "");
        }

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(filename);

        if (!m.find()) return Optional.empty();

        return Optional.of(
                new FileNameParts(
                        m.group(1), // timestamp
                        m.group(2), // baseName
                        m.group(3)  // extension
                )
        );
    }

    public static boolean matchesBaseName(String filename, FileType type) {
        return parseFileName(filename, type)
                .map(parts -> {
                    if (!parts.extension().equalsIgnoreCase(type.getExtension())) {
                        return false;
                    }
                    if (type.getExpectedBaseName().startsWith("%")) {
                        return true;
                    } else {
                        return parts.baseName.equalsIgnoreCase(type.getExpectedBaseName());
                    }
                }
                )
                .orElse(false);
    }

}
