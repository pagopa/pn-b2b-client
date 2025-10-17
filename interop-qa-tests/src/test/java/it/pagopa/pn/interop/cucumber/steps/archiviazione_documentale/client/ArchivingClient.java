package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.polling.S3Polling;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileTypes;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.FileMatcher;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class ArchivingClient {

    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC);
    private final FileMatcher fileMatcher;

    public boolean matchS3FileInInterval(String bucketName, FileTypes fileType, String regex, String centerTimestamp, int deltaSeconds, long timeoutMs, long pollIntervalMs) {

        AtomicBoolean finded = new AtomicBoolean(false);

        Instant center = LocalDateTime.parse(centerTimestamp, TS_FORMAT).toInstant(ZoneOffset.UTC);
        Instant start = center.minusSeconds(deltaSeconds);
        Instant end = center.plusSeconds(deltaSeconds);
        Pattern timestampPattern = Pattern.compile("(\\d{14})");

        S3Polling polling = new S3Polling(Region.EU_CENTRAL_1, s3 -> {
            ListObjectsV2Response res = s3.listObjectsV2(
                    ListObjectsV2Request.builder()
                            .bucket(bucketName)
                            .build()
            );

            List<String> matchingFiles = res.contents().stream()
                    .map(S3Object::key)
                    .filter(key -> key.endsWith(fileType.getExtension()))
                    .filter(key -> {
                        Matcher m = timestampPattern.matcher(key);
                        if (m.find()) {
                            String tsString = m.group(1);
                            try {
                                LocalDateTime ldt = LocalDateTime.parse(tsString, TS_FORMAT);
                                Instant fileTs = ldt.toInstant(ZoneOffset.UTC);
                                return !fileTs.isBefore(start) && !fileTs.isAfter(end);
                            } catch (Exception e) {
                                return false;
                            }
                        }
                        return false;
                    })
                    .toList();

            if (!matchingFiles.isEmpty()) {
                for (String key : matchingFiles) {
                    try {
                        boolean match = fileMatcher.match(s3, bucketName, key, regex);
                        finded.set(match);
                        return match;
                    } catch (IOException e) {
                        throw new RuntimeException("Errore handler " + key, e);
                    }
                }
            }

            return false;
        });

        polling.executePolling(5, 2000);


        long maxAttempts = timeoutMs / pollIntervalMs;
        polling.executePolling((int) maxAttempts, pollIntervalMs);

        return finded.get();
    }

}
