package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public abstract class AbstractS3Polling {

    protected final S3Client s3;
    protected final String bucketName;

    public AbstractS3Polling(String bucketName, Region region) {
        this.s3 = S3Client.builder().region(region).build();
        this.bucketName = bucketName;
    }

    // Template Method: scheletro del polling
    public final void executePolling(int maxAttempts, long intervalMillis) {
        for (int i = 0; i < maxAttempts; i++) {
            System.out.println("Tentativo " + (i+1));

            if (checkCondition(s3, bucketName)) {
                System.out.println("Condizione soddisfatta, stop polling.");
                return;
            }

            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        System.out.println("Condizione NON soddisfatta dopo " + maxAttempts + " tentativi.");
    }

    // Hook da definire nelle sottoclassi o da iniettare come lambda
    protected abstract boolean checkCondition(S3Client s3, String bucketName);
}
