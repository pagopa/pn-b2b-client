package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.pagopa.pn.interop.cucumber.utility.PollingUtils;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RequiredArgsConstructor
public class S3Client {

    private final S3Client s3Client;
    private final String bucketName;
    private static final DateTimeFormatter ZIP_TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC);

    public boolean verifyEventZipNdjsonInInterval(String centerTimestamp, int deltaSeconds, long timeoutMs, long pollIntervalMs, Map<String, String> filterFields) {
        Instant center = LocalDateTime.parse(centerTimestamp, ZIP_TS_FORMAT).toInstant(ZoneOffset.UTC);
        Instant start = center.minusSeconds(deltaSeconds);
        Instant end = center.plusSeconds(deltaSeconds);

        BooleanSupplier condition = () -> {
            List<String> candidateZipNames = findZipFilesInInterval(start, end);
            for (String zipName : candidateZipNames) {
                if (ndjsonContainsMatchingRecord(zipName, filterFields)) {
                    System.out.println("Matching record found in: " + zipName);
                    return true;
                }
            }
            return false;
        };

        boolean found = PollingUtils.pollUntil(condition, timeoutMs, pollIntervalMs);
        if (!found) {
            System.err.println("No matching NDJSON record found in ZIPs in interval.");
        }
        return found;
    }

    private List<String> findZipFilesInInterval(Instant start, Instant end) {
        List<String> matching = new ArrayList<>();
        ListObjectsV2Request req = ListObjectsV2Request.builder().bucket(bucketName).build();
        for (S3Object obj : s3Client.listObjectsV2(req).contents()) {
            String key = obj.key();
            if (key.startsWith("events_") && key.endsWith(".zip")) {
                String tsPart = key.substring("events_".length(), key.length() - ".zip".length());
                try {
                    Instant ts = LocalDateTime.parse(tsPart, ZIP_TS_FORMAT).toInstant(ZoneOffset.UTC);
                    if (!ts.isBefore(start) && !ts.isAfter(end)) {
                        matching.add(key);
                    }
                } catch (Exception ignore) {}
            }
        }
        return matching;
    }


    private boolean ndjsonContainsMatchingRecord(String zipFileName, Map<String, String> filterFields) {
        try (InputStream s3Stream = s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(zipFileName).build());
             ZipInputStream zipInputStream = new ZipInputStream(s3Stream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().endsWith(".ndjson")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8));
                    String line;
                    Gson gson = new Gson();
                    while ((line = reader.readLine()) != null) {
                        try {
                            JsonObject obj = gson.fromJson(line, JsonObject.class);
                            boolean allMatch = filterFields.entrySet().stream()
                                    .allMatch(e -> obj.has(e.getKey()) && e.getValue().equals(obj.get(e.getKey()).getAsString()));
                            if (allMatch) {
                                return true;
                            }
                        } catch (Exception ignore) {}
                    }
                }
                zipInputStream.closeEntry();
            }
        } catch (Exception ex) {
            System.err.println("Error reading ZIP from S3: " + ex.getMessage());
        }
        return false;
    }
}
