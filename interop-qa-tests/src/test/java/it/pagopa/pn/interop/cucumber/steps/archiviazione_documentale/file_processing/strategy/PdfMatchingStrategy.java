package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.strategy;

import java.io.IOException;

public class PdfMatchingStrategy implements FileMatchingStrategy {
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
//        try (ResponseInputStream<GetObjectResponse> s3is = s3.getObject(req)) {
//            long size = s3is.response().contentLength();
//            System.out.println("[PDF] trovato file PDF: " + key + " (dimensione: " + size + " bytes)");
//            return true;
//        }
//
//    }
}
