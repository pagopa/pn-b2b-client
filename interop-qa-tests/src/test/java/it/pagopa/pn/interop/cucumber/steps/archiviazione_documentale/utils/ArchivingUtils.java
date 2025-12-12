package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.FileNameParts;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArchivingUtils {
    public static final String PDF_SIGNED_NAME_REGEX = "^(?:([0-9]{14})_)?INTEROP_([^.]*)\\.([^.]+)$";
    public static final String EVENT_SIGNED_NAME_REGEX = "^INTEROP_((?:[^.-]+)-[0-9a-fA-F]{32}-signed)\\.([^.]+(?:\\.[^.]+)*)$";
    public static final String PDF_NAME_REGEX = "(\\d{14})_([^.]+)\\.(.+)";
    public static final String EVENT_NAME_REGEX = "^events_(\\d{8})_(\\d{6})_([0-9a-fA-F-]+)(\\.[^.]+)+$";
    public static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC);

    public static String now(){
        return TS_FORMAT.format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    public static Instant parse(String formattedTimestamp){
       return LocalDateTime.parse(formattedTimestamp, TS_FORMAT).toInstant(ZoneOffset.UTC);
    }

    public static String extractFilenameFromS3Key(String key) {
        if (key == null || key.isEmpty()) return null;
        return key.substring(key.lastIndexOf('/') + 1);
    }

    public static FileNameParts applyFileFormatRegex(String filename, FileType type) {
        if (filename == null)
            throw new IllegalArgumentException("filename is null");
        
        String regex = type.getFormatRegex();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(filename);

        if (!m.matches()) return null;

        String timestamp = null;
        String baseName;
        String extension;

        switch (regex) {
            
            case PDF_SIGNED_NAME_REGEX, PDF_NAME_REGEX:
                timestamp = m.group(1); 
                baseName  = m.group(2);
                extension = m.group(3);
                break;

            case EVENT_SIGNED_NAME_REGEX:
                baseName  = m.group(1);

                // trova l'ultimo punto nel filename
                int lastDot = filename.lastIndexOf('.');

                // prendi l'estensione finale SENZA il punto
                extension = filename.substring(lastDot + 1); // "p7m"
                break;
                
            case EVENT_NAME_REGEX:
                String date = m.group(1);
                String time = m.group(2);
                String uuid = m.group(3);

                baseName = "events_" + date + "_" + time + "_" + uuid;
                timestamp = String.join("", date, time);

                // extension non è in un singolo gruppo → la ricostruisco
                int start = filename.indexOf(uuid) + uuid.length() + ".ndjson.".length();
                extension = filename.substring(start);
                break;
                
            default:
                throw new IllegalStateException("Regex non gestita: " + regex);
        }

        return new FileNameParts(timestamp, baseName, extension);
    }

    public static boolean isValidIsoTimestamp(String value) {

        // caso: formato compatto yyyyMMddHHmmss
        if (value.matches("\\d{14}")) {
            try {
                TS_FORMAT.parse(value);
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }

        // formato ISO-8601 completo
        try {
            OffsetDateTime.parse(value);
            return true;
        } catch (Exception ignored) {
            // not ISO either
        }

        return false;
    }


    public static boolean isValidUUID(String value) {
        try {
            java.util.UUID.fromString(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
