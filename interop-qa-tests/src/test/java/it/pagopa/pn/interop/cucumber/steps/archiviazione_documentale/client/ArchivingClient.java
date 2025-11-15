package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.polling.S3Polling;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.FileMatcher;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.strategy.FileMatchingStrategy;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

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

    private final FileMatcher fileMatcher = new FileMatcher();
    private final SharedStepsContext sharedStepsContext;

    public ArchivedFile findS3FileInInterval(SearchFileSeed seed) {

        AtomicReference<ArchivedFile> file = new AtomicReference<>();
        FileType fileType = seed.getType();

        // Inizializzo la finestra di ricerca
        Instant center = ArchivingUtils.parse(seed.getCenterTimestamp());
        Instant start = center.minusSeconds(seed.getDeltaSeconds());
        Instant end = center.plusSeconds(seed.getDeltaSeconds());

        // Inizializzo il polling
        S3BucketInfo bucketInfo = seed.getBucketInfo();
        Set<String> checkedKeys = new HashSet<>();

        S3Polling polling = new S3Polling(Region.EU_CENTRAL_1, s3 -> {

            ListObjectsV2Response res = s3.listObjectsV2(
                    ListObjectsV2Request.builder()
                            .bucket(bucketInfo.bucket())
                            .prefix(bucketInfo.prefix())
                            .build()
            );

            List<String> matchingFiles = res.contents().stream()
                    .map(S3Object::key)
                    // Skip oggetti gia controllati
                    .filter(key -> {
                        if (checkedKeys.contains(key)) return false;
                        checkedKeys.add(key);
                        return true;
                    })
                    // Filtro per baseName ed estensione
                    .filter(key -> {
                        String filename = ArchivingUtils.extractFilenameFromS3Key(key);
                        return ArchivingUtils.matchesBaseName(filename, fileType);
                    })
                    // Filtro per timestamp
                    .filter(key -> ArchivingUtils.extractTimestampFromS3Key(key)
                            .map(fileTs -> !fileTs.isBefore(start) && !fileTs.isAfter(end))
                            .orElse(false)
                    )
                    .toList();

            if (!matchingFiles.isEmpty()) {
                for (String key : matchingFiles) {
                    try {
                        FileMatchingStrategy.MatchingStrategySeed strategySeed =
                                new FileMatchingStrategy.MatchingStrategySeed(s3, fileType, bucketInfo, sharedStepsContext);

                        boolean match = fileMatcher.match(strategySeed);

                        if (match) {
                            file.set(buildArchivedDocument(s3, bucketInfo));
                            return true;
                        }

                    } catch (IOException e) {
                        throw new RuntimeException("Errore handler " + key, e);
                    }
                }
            }

            return false;
        });

        // Polling
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
