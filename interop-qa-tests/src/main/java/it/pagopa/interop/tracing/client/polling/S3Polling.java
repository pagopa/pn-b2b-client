package it.pagopa.interop.tracing.client.polling;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.function.Predicate;


public class S3Polling extends AbstractPolling<S3Client> {

    public S3Polling(Region region, Predicate<S3Client> condition) {
        super(S3Client.builder()
                .region(region)
                // Per lanciare i test Tracing in locale serve aggiungere i token di accesso per Extra QA
                // nel file .aws/credentials sotto il profilo [extra-qa] e non sotto [default] che è dedicato a QA.
                // Decommentare in locale il metodo per leggere il profilo extra-qa.
                .credentialsProvider(DefaultCredentialsProvider
                        .builder()
                        //.profileName("extra-qa")
                        .build())
                .build(),
                condition
        );
    }
}
