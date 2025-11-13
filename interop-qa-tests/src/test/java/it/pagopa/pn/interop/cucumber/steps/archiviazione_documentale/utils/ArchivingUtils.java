package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ArchivingUtils {
    public static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC);

    public static String now(){
        return TS_FORMAT.format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    public static Instant parse(String formattedTimestamp){
       return LocalDateTime.parse(formattedTimestamp, TS_FORMAT).toInstant(ZoneOffset.UTC);
    }
}
