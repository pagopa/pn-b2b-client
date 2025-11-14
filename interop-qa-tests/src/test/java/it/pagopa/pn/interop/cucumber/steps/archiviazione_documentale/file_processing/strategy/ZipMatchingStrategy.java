package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.strategy;

import java.io.IOException;

public class ZipMatchingStrategy implements FileMatchingStrategy {
    @Override
    public boolean match(MatchingStrategySeed seed) throws IOException {
        return false;
    }
//    @Override
//    public boolean match(S3Client s3, String bucketName, String key) throws IOException {
//        GetObjectRequest req = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build();
//
//        try (ResponseInputStream<GetObjectResponse> s3is = s3.getObject(req);
//             ZipInputStream zip = new ZipInputStream(s3is)) {
//
//            ZipEntry entry;
//            while ((entry = zip.getNextEntry()) != null) {
//                System.out.println("[ZIP] " + key + " contiene: " + entry.getName());
//            }
//        }
//
//        return true;
//    }
}
