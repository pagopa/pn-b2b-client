package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.ArchivedFileMatched;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketRole;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketUrl;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.polling.S3Polling;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.*;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.FileProcessor;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.FileCandidate;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils;
import it.pagopa.pn.interop.cucumber.utility.S3Utils;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Slf4j
public class ArchivingClient {

    @Data
    @Builder
    public static class PollingSpecification {

        @NonNull
        private FileInfo fileInfo;

        @Builder.Default
        private BucketRole bucketRole = BucketRole.STANDARD;

        @Builder.Default
        private String centerTimestamp = null;

        @Builder.Default
        private int deltaSeconds = 30;

        @Builder.Default
        private long timeoutMs = 10_000;

        @Builder.Default
        private long pollIntervalMs = 1_000;

        public boolean hasTimestamp() {
            return centerTimestamp != null;
        }
    }

    private final FileProcessor fileProcessor = new FileProcessor();

    public ArchivedFileMatched findS3FileInInterval(PollingSpecification spec) {

        FileLocation location = resolveLocation(spec);
        BucketUrl bucket = location.bucketUrl();
        boolean useTimestamp = useTimestampFilter(location, spec);

        Set<String> checkedKeys = new HashSet<>();
        AtomicInteger windowEnlargement = new AtomicInteger();
        AtomicReference<ArchivedFileMatched> match = new AtomicReference<>();

        S3Polling polling = new S3Polling(Region.EU_SOUTH_1, s3 -> {

            log.info("Ricerco il file all'interno del bucket: {}", bucket.fullPath());
            List<String> candidateKeys = getLatestNObjects(s3, bucket, 50).stream()
                    .map(S3Object::key)
                    .filter(key -> isNotAlreadyChecked(key, checkedKeys))
                    .filter(key -> isCandidateKey(key, spec, useTimestamp, windowEnlargement.get()))
                    .toList();

            for (String key : candidateKeys) {
                ArchivedFileMatched result = tryMatchFile(s3, bucket, key, spec.fileInfo, checkedKeys);
                if (result != null) {
                    match.set(result);
                    return true;
                }
            }

            if (candidateKeys.isEmpty() && useTimestamp) {
                int newWindow = windowEnlargement.addAndGet(300);
                logTimeWindow(spec, newWindow);
            }

            return false;
        });

        polling.executePolling(
                maxAttempts(spec),
                spec.getPollIntervalMs()
        );

        return match.get();
    }

    private FileLocation resolveLocation(PollingSpecification spec) {
        return spec.fileInfo
                .locationFor(spec.getBucketRole())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Nessuna location trovata per %s e %s"
                                        .formatted(spec.fileInfo, spec.getBucketRole())
                        ));
    }

    private boolean isCandidateKey(String key, PollingSpecification spec, boolean useTimestamp, int windowEnlargement) {
        String filename = ArchivingUtils.extractFilenameFromS3Key(key);
        FileNameParts parts = FileNameParts.parse(filename);

        if (parts == null) return false;

        if (!useTimestamp) return true;

        if (parts.timestamp() == null) {
            log.error("Previsto polling per timestamp ma il file {} non lo contiene", filename);
            return false;
        }

        Instant fileTs = ArchivingUtils.parse(parts.timestamp());
        Instant center = ArchivingUtils.parse(spec.getCenterTimestamp());

        Instant start = center.minusSeconds(spec.getDeltaSeconds() + windowEnlargement);
        Instant end = center.plusSeconds(spec.getDeltaSeconds() + windowEnlargement);

        return !fileTs.isBefore(start) && !fileTs.isAfter(end);
    }

    private boolean useTimestampFilter(FileLocation location, PollingSpecification spec) {return location.filenameFormat().hasTimestamp() && spec.hasTimestamp();}

    private boolean isNotAlreadyChecked(String key, Set<String> checkedKeys) {
       return checkedKeys.contains(key);
    }

    private void addKeyToChecked(String key, Set<String> checkedKeys){
        checkedKeys.add(key);
    }

    private int maxAttempts(PollingSpecification spec) {return (int) ((spec.getTimeoutMs() / spec.getPollIntervalMs()) + 1);}

    private List<S3Object> getLatestNObjects(S3Client s3, BucketUrl bucketInfo, int limit) {

        Comparator<S3Object> safeComparator = (o1, o2) -> {
            Instant t1 = o1.lastModified();
            Instant t2 = o2.lastModified();

            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return -1;
            if (t2 == null) return 1;

            return t1.compareTo(t2); // ASC (min-heap)
        };

        PriorityQueue<S3Object> minHeap = new PriorityQueue<>(limit, safeComparator);

        String continuationToken = null;

        do {
            ListObjectsV2Request.Builder req = ListObjectsV2Request.builder()
                    .bucket(bucketInfo.base())
                    .prefix(bucketInfo.prefix());

            if (continuationToken != null) {
                req.continuationToken(continuationToken);
            }

            ListObjectsV2Response page = s3.listObjectsV2(req.build());

            for (S3Object obj : page.contents()) {
                Instant ts = obj.lastModified();
                if (ts == null) continue;

                if (minHeap.size() < limit) {
                    minHeap.offer(obj);
                } else {
                    Instant oldest = minHeap.peek().lastModified();
                    if (oldest == null || ts.isAfter(oldest)) {
                        minHeap.poll();
                        minHeap.offer(obj);
                    }
                }
            }

            continuationToken = page.nextContinuationToken();

        } while (continuationToken != null);

        List<S3Object> result = new ArrayList<>(minHeap);

        // Ordina per lastModified DESC
        result.sort((o1, o2) -> {
            Instant t1 = o1.lastModified();
            Instant t2 = o2.lastModified();

            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return -1;
            if (t2 == null) return 1;

            return t2.compareTo(t1); // DESC
        });

        return result;
    }

    private ArchivedFileMatched tryMatchFile(S3Client s3, BucketUrl bucket, String key, FileInfo fileInfo, Set<String> checkedKeys) {
        String filename = ArchivingUtils.extractFilenameFromS3Key(key);
        this.addKeyToChecked(key, checkedKeys);
        FileNameParts parts = FileNameParts.parse(filename);

        if (parts == null || parts.extension() == null) {
            throw new IllegalStateException("FileNameParts non valido per " + filename);
        }

        ContentType contentType = ContentType.fromExtension(parts.extension());

        FileCandidate candidate = new FileCandidate(
                S3Utils.getFileStream(s3, bucket),
                filename,
                contentType
        );

        ProcessedFile processed = fileProcessor.normalize(candidate);
        ValidationResult validation = fileInfo.validation().validate(processed);

        log.info("Controllata la key {}", key);

        if (!validation.hasAllRequired()) {
            return null;
        }

        ArchivedFile archivedFile = buildArchivedDocument(s3, bucket);
        return new ArchivedFileMatched(archivedFile, validation);
    }

    private ArchivedFile buildArchivedDocument(S3Client s3, BucketUrl bucketInfo) {
        ArchivedFile.ArchivedFileBuilder builder = ArchivedFile.builder();
        String key = bucketInfo.key();

        // Estrai il nome file
        String filename = ArchivingUtils.extractFilenameFromS3Key(key);
        builder.filename(filename);

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

    private void logTimeWindow(PollingSpecification spec, long windowEnlargementSeconds) {
        var LOG_TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);
        Instant center = ArchivingUtils.parse(spec.getCenterTimestamp());

        long totalDelta = spec.getDeltaSeconds() + windowEnlargementSeconds;

        Instant start = center.minusSeconds(totalDelta);
        Instant end = center.plusSeconds(totalDelta);

        log.info(
                """
                S3 polling window enlarged (UTC)
                  interval : {} → {}
                  center   : {}
                  window   : ±{} min ({} s)
                """,
                LOG_TS_FORMAT.format(start),
                LOG_TS_FORMAT.format(end),
                LOG_TS_FORMAT.format(center),
                totalDelta / 60,
                totalDelta
        );
    }


}
