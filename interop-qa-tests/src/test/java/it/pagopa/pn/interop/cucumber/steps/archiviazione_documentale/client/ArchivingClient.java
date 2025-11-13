package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.polling.S3Polling;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.FileMatcher;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.FileMatchingStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils;
import it.pagopa.pn.interop.cucumber.utility.S3Utils;
import lombok.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils.TS_FORMAT;

@RequiredArgsConstructor
public class ArchivingClient {

    @Data
    @Builder
    public static class SearchFileSeed {

        @NonNull
        private FileType type;

        @NonNull
        private S3BucketInfo bucketInfo;

        @Builder.Default
        private boolean isSigned = false;

        @Builder.Default
        private String centerTimestamp = null;

        @Builder.Default
        private int deltaSeconds = 30;

        @Builder.Default
        private long timeoutMs = 10_000;

        @Builder.Default
        private long pollIntervalMs = 1_000;
    }

    private final FileMatcher fileMatcher;

    public ArchivedFile findS3FileInInterval(SearchFileSeed seed) {

        AtomicReference<ArchivedFile> file = new AtomicReference<>();
        FileType fileType = seed.getType();

        // Inizializzo la finestra di ricerca
        Instant center = ArchivingUtils.parse(seed.getCenterTimestamp());
        Instant start = center.minusSeconds(seed.getDeltaSeconds());
        Instant end = center.plusSeconds(seed.getDeltaSeconds());
        Pattern timestampPattern = Pattern.compile("(\\d{14})");

        // Inizializzo il polling
        S3BucketInfo bucketInfo = seed.getBucketInfo();

        S3Polling polling = new S3Polling(Region.EU_CENTRAL_1, s3 -> {
            ListObjectsV2Response res = s3.listObjectsV2(
                    ListObjectsV2Request.builder()
                            .bucket(bucketInfo.getBucket())
                            .prefix(bucketInfo.getPrefix())
                            .build()
            );

            List<String> matchingFiles = res.contents().stream()
                    .map(S3Object::key)
                    // filtro per estensione
                    .filter(key -> key.endsWith(fileType.getExtension()))
                    // filtro per timestamp
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
                        FileMatchingStrategy.MatchingStrategySeed strategySeed = new FileMatchingStrategy.MatchingStrategySeed(s3, fileType, bucketInfo);
                        boolean match = fileMatcher.match(strategySeed);
                        if (match) file.set(buildArchivedDocument(s3, bucketInfo));

                        return match;
                    } catch (IOException e) {
                        throw new RuntimeException("Errore handler " + key, e);
                    }
                }
            }

            return false;
        });
        polling.executePolling(5, 2000);

        long maxAttempts = seed.getTimeoutMs() / seed.getPollIntervalMs();
        polling.executePolling((int) maxAttempts, seed.getPollIntervalMs());

        return file.get();
    }

    private ArchivedFile buildArchivedDocument(S3Client s3, S3BucketInfo bucketInfo) {
        ArchivedFile.ArchivedFileBuilder builder = ArchivedFile.builder();

        // Recupero i metadati classici via HeadObject
        HeadObjectResponse headResp = S3Utils.getHeader(s3, bucketInfo);

        builder.contentLength(headResp.contentLength());
        builder.contentType(headResp.contentType());
        builder.creationDate(headResp.lastModified());

        // Recupero retention info (se Object Lock attivo)
        GetObjectRetentionResponse retentionResp = S3Utils.getRetentionInfo(s3, bucketInfo);

        if (retentionResp != null && retentionResp.retention() != null) {
            builder.retentionMode(retentionResp.retention().modeAsString());
            builder.retainUntilDate(retentionResp.retention().retainUntilDate());
        }

        // Recupero legal hold (se presente)
        GetObjectLegalHoldResponse legalHoldInfo = S3Utils.getLegalHoldInfo(s3, bucketInfo);

        if (legalHoldInfo != null && legalHoldInfo.legalHold() != null) {
            builder.legalHoldEnabled(legalHoldInfo.legalHold().statusAsString().equals("ON"));
        }

        // Recupero il contenuto
        InputStream content = S3Utils.getFileStream(s3, bucketInfo);
        builder.content(content);

        return builder.build();
    }
}
