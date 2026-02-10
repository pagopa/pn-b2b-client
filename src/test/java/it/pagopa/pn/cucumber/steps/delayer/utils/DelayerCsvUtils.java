package it.pagopa.pn.cucumber.steps.delayer.utils;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DelayerCsvUtils {

//    public void replaceCsvContent(Path path, String placeholder, String replaceWith) {
//        try {
//            String content = Files.readString(path, StandardCharsets.UTF_8);
//            content = content.replace(placeholder, replaceWith);
//            Files.writeString(path, content, StandardCharsets.UTF_8);
//            uploadCsvToS3(content, "", path.getFileName().toString());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void uploadCsvToS3(String csvContent, String bucket, String key) {
//        S3Client s3Client = S3Client.builder()
//                .region(Region.EU_SOUTH_1)
//                .credentialsProvider(ProfileCredentialsProvider.create("ROLE_test_core"))
//                .build();
//
//        s3Client.putObject(
//                PutObjectRequest.builder()
//                        .bucket("")
//                        .key(key)
//                        .contentType("text/csv")
//                        .serverSideEncryption(ServerSideEncryption.AWS_KMS)
//                        .build(),
//                RequestBody.fromString(csvContent, StandardCharsets.UTF_8)
//        );
//    }
}
