package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.polling.S3Polling;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.FileMatcher;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileMatcher;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.FileNameParts;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import it.pagopa.pn.interop.cucumber.utility.S3Utils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils.applyFileFormatRegex;

@RequiredArgsConstructor
@Slf4j
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
    private final TokenResolver tokenResolver;

    public ArchivedFile findS3FileInInterval(SearchFileSeed seed) {

        AtomicReference<ArchivedFile> file = new AtomicReference<>();
        FileType fileType = seed.getType();
        AtomicInteger windowEnlargement = new AtomicInteger();
        boolean hasTimestamp = seed.centerTimestamp == null;

        // Inizializzo il polling
        S3BucketInfo bucketInfo = seed.getBucketInfo();
        Set<String> checkedKeys = new HashSet<>();

        S3Polling polling = new S3Polling(Region.EU_SOUTH_1, s3 -> {

            List<S3Object> latestObjects = getLatestNObjects(s3, bucketInfo, 50);

            List<String> matchingFiles = latestObjects.stream()
                    // Converte in key
                    .map(S3Object::key)

                    // Skip oggetti gia controllati
                    .filter(key -> {
                        if (checkedKeys.contains(key)) return false;
                        checkedKeys.add(key);
                        return true;
                    })

                    // Filtro per formato e timestamp
                    .filter(key -> {
                        String filename = ArchivingUtils.extractFilenameFromS3Key(key);
                        FileNameParts fileNameParts = applyFileFormatRegex(filename, fileType);

                        if (fileNameParts == null) return false;

                        boolean inInterval = true;
                        if(seed.centerTimestamp != null) {
                            Instant fileTs = Instant.parse(fileNameParts.timestamp());

                            Instant center = ArchivingUtils.parse(seed.getCenterTimestamp());
                            Instant start = center.minusSeconds(seed.getDeltaSeconds() + windowEnlargement.get());
                            Instant end = center.plusSeconds(seed.getDeltaSeconds() + windowEnlargement.get());

                            inInterval = !fileTs.isBefore(start) && !fileTs.isAfter(end);
                        }

                        return inInterval && fileNameParts.extension().equals(fileType.getFormatRegex());
                    })

                    .toList();

            if (!matchingFiles.isEmpty()) {
                for (String key : matchingFiles) {
                    try {
                        S3BucketInfo s3BucketInfo = new S3BucketInfo(bucketInfo.bucket(), bucketInfo.prefix(), key);
                        IFileMatcher.MatchingStrategySeed strategySeed = new IFileMatcher.MatchingStrategySeed(s3, fileType, s3BucketInfo, sharedStepsContext, tokenResolver);

                        log.info("Viene controllata la key: {}", key);
                        boolean match = fileMatcher.match(strategySeed);

                        if (match) {
                            file.set(buildArchivedDocument(s3, s3BucketInfo));
                            return true;
                        }

                    } catch (IOException e) {
                        throw new RuntimeException("Errore handler " + key, e);
                    }
                }
            }
            else if(hasTimestamp)
                windowEnlargement.addAndGet(300);

            return false;
        });

        // Polling
        long maxAttempts = (seed.getTimeoutMs() / seed.getPollIntervalMs()) + 1;
        polling.executePolling((int) maxAttempts, seed.getPollIntervalMs());

        return file.get();
    }

    private List<S3Object> getLatestNObjects(S3Client s3, S3BucketInfo bucketInfo, int limit) {

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
                    .bucket(bucketInfo.bucket())
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

    public List<ArchivedFile> getAllFilesInS3(SearchFileSeed seed, int limit) {

        S3BucketInfo bucketInfo = seed.getBucketInfo();
        S3Client s3 = S3Client.builder()
                .region(Region.EU_SOUTH_1)
                .build();

        List<S3Object> allObjects = new java.util.ArrayList<>();

        String continuationToken = null;

        // --- PAGINA FINCHÉ CI SONO ALTRI OGGETTI ---
        do {
            ListObjectsV2Request.Builder reqBuilder = ListObjectsV2Request.builder()
                    .bucket(bucketInfo.bucket())
                    .prefix(bucketInfo.prefix());

            if (continuationToken != null) {
                reqBuilder.continuationToken(continuationToken);
            }

            ListObjectsV2Response resp = s3.listObjectsV2(reqBuilder.build());

            if (resp.contents() != null) {
                allObjects.addAll(resp.contents());
            }

            continuationToken = resp.nextContinuationToken();

        } while (continuationToken != null);


        // --- ORDINA PER DATA (dal più recente al meno recente) ---
        allObjects.sort((o1, o2) -> o2.lastModified().compareTo(o1.lastModified()));

        // Limita ai primi N
        List<S3Object> topN = allObjects.stream()
                .limit(limit)
                .toList();

        // --- COSTRUISCI I RISULTATI COME ArchivedFile ---
        List<ArchivedFile> result = new java.util.ArrayList<>();

        for (S3Object obj : topN) {

            S3BucketInfo info = new S3BucketInfo(
                    bucketInfo.bucket(),
                    bucketInfo.prefix(),
                    obj.key()
            );

            ArchivedFile archived = buildArchivedDocument(s3, info);
            result.add(archived);
        }

        return result;
    }

    private ArchivedFile buildArchivedDocument(S3Client s3, S3BucketInfo bucketInfo) {
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
}
