package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.ArchivedFileMatched;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketRole;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketUrl;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.S3BucketInfoBuilder;
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

import java.io.IOException;
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

        AtomicInteger windowEnlargement = new AtomicInteger();
        Instant center = ArchivingUtils.parse(spec.getCenterTimestamp());
        Instant start = center.minusSeconds(spec.getDeltaSeconds() + windowEnlargement.get());
        Instant end   = center.plusSeconds(spec.getDeltaSeconds() + windowEnlargement.get());

        Set<String> checkedKeys = new HashSet<>();
        AtomicReference<ArchivedFileMatched> match = new AtomicReference<>();

        S3Polling polling = new S3Polling(Region.EU_SOUTH_1, s3 -> {

            log.info("Ricerco il file all'interno del bucket: {}", bucket.fullPath());
            List<S3Object> candidates = getLatestNObjects(s3, bucket, 50, start, end).stream()
                    .filter(obj -> isNotAlreadyChecked(obj.key(), checkedKeys))
                    .toList();

            for (S3Object obj : candidates) {
                ArchivedFileMatched result = tryMatchFile(s3, bucket, obj.key(), spec.fileInfo, checkedKeys);
                if (result != null) {
                    match.set(result);
                    return true;
                }
            }

            int newWindow = windowEnlargement.addAndGet(300);
            logTimeWindow(spec, newWindow);

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

    private boolean isNotAlreadyChecked(String key, Set<String> checkedKeys) {
       return !checkedKeys.contains(key);
    }

    private void addKeyToChecked(String key, Set<String> checkedKeys){
        checkedKeys.add(key);
    }

    private int maxAttempts(PollingSpecification spec) {return (int) ((spec.getTimeoutMs() / spec.getPollIntervalMs()) + 1);}

    private List<S3Object> getLatestNObjects(S3Client s3, BucketUrl bucketInfo, int limit, Instant start, Instant end) {

        Comparator<S3Object> minHeapComparator = Comparator.comparing(S3Object::lastModified);
        PriorityQueue<S3Object> minHeap = new PriorityQueue<>(limit, minHeapComparator);
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

                if (ts.isBefore(start) || ts.isAfter(end)) {
                    continue;
                }

                if (minHeap.size() < limit) {
                    minHeap.offer(obj);
                } else if (ts.isAfter(minHeap.peek().lastModified())) {
                    minHeap.poll();
                    minHeap.offer(obj);
                }
            }

            continuationToken = page.nextContinuationToken();

        } while (continuationToken != null);

        List<S3Object> result = new ArrayList<>(minHeap);

        // Ordina per lastModified DESC (più recenti prima)
        result.sort(Comparator.comparing(S3Object::lastModified).reversed());

        return result;
    }

    public ProcessedFile normalizeFile(ArchivedFileMatched archivedFile) throws IOException {
        FileCandidate candidate = new FileCandidate(
                archivedFile.file().getContent(),
                archivedFile.file().getFilename(),
                ContentType.fromExtension(
                        archivedFile.file().getFilename().substring(archivedFile.file().getFilename().lastIndexOf(".") + 1)
                )
        );
        return fileProcessor.normalize(candidate);
    }

    private ArchivedFileMatched tryMatchFile(S3Client s3, BucketUrl bucket, String key, FileInfo fileInfo, Set<String> checkedKeys) {
        this.addKeyToChecked(key, checkedKeys);

        String filename = ArchivingUtils.extractFilenameFromS3Key(key);
        FileNameParts parts = FileNameParts.parse(filename);
        BucketUrl fileUrl = S3BucketInfoBuilder.builder().fullPath(String.join("/", bucket.base(),key)).build();

        if (parts == null || parts.extension() == null) {
            throw new IllegalStateException("FileNameParts non valido per " + filename);
        }

        ContentType contentType = ContentType.fromExtension(parts.extension());

        FileCandidate candidate = new FileCandidate(
                S3Utils.getFileStream(s3, fileUrl),
                filename,
                contentType
        );

        ProcessedFile processed = fileProcessor.normalize(candidate);
        ValidationResult validation = fileInfo.validation().validate(processed);

        log.info("Controllata la key {}", key);

        if (!validation.hasAllRequired()) {
            return null;
        }

        ArchivedFile archivedFile = buildArchivedDocument(s3, fileUrl);
        return new ArchivedFileMatched(archivedFile, validation);
    }

    private ArchivedFile buildArchivedDocument(S3Client s3, BucketUrl bucketInfo) {
        ArchivedFile.ArchivedFileBuilder builder = ArchivedFile.builder();
        String key = bucketInfo.key();

        // Bucket info
        builder.bucketInfo(bucketInfo);

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
