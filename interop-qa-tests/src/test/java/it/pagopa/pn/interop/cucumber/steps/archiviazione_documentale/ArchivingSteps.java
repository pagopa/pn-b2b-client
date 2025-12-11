package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale;

import io.cucumber.java.en.Then;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.ArchivingClient;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context.ArchivingContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfoBuilder;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.within;

public class ArchivingSteps {

    private final ArchivingContext context;
    private final ArchivingClient client;

    public ArchivingSteps( @Value("${s3.unsigned-document-base-path}") String unsignedDocumentBasePath,
                           @Value("${s3.signed-document-base-path}") String signedDocumentBasePath,
                           SharedStepsContext sharedStepsContext ) {
        this.context = new ArchivingContext(unsignedDocumentBasePath, signedDocumentBasePath, sharedStepsContext);
        this.client = new ArchivingClient(sharedStepsContext);
    }

    @Then("verifica nel bucket S3 {bucketType} l'esistenza del file {documentType}")
    public void checkS3Bucket(boolean isSigned, FileType fileType){
        S3BucketInfo bucketInfo = context.getBucket(isSigned, fileType);
        boolean hasFileTimestamp = !(fileType.getExtension().equals("pdf") && isSigned);

        ArchivingClient.SearchFileSeed seed =
                ArchivingClient.SearchFileSeed.builder()
                        .centerTimestamp(hasFileTimestamp ? context.getCenterTimestamp() : null)
                        .timeoutMs(600_000)
                        .pollIntervalMs(30_000)
                        .deltaSeconds(300)
                        .bucketInfo(bucketInfo)
                        .type(fileType)
                        .isSigned(isSigned)
                        .build();

        ArchivedFile file = client.findS3FileInInterval(seed);
        Assertions.assertThat(file)
                .as("Atteso file %s nel bucket %s ma non è stato trovato", bucketInfo.key(), bucketInfo.bucket())
                .isNotNull();

        context.setCurrentFile(file);
    }

    @Then("verifica che il file nel bucket SIGNED abbia la proprietà \"Retain until date\" pari a 10 anni dalla data di creazione")
    @Then("verifica che il file nel bucket WORM abbia la proprietà \"Retain until date\" pari a 10 anni dalla data di creazione")
    public void checkRetainUntilDate() {

        Instant creationDate = context.getCurrentFile().getCreationDate();
        Instant expectedRetainUntil = creationDate.plus(10, ChronoUnit.YEARS);
        Instant actualRetainUntilDate = context.getCurrentFile().getRetainUntilDate();

        // Tolleranza di 1 giorno
        Assertions.assertThat(actualRetainUntilDate)
                .as("La Retain until date non è 10 anni dopo la creation date. Creation=%s, Retain=%s", creationDate, actualRetainUntilDate)
                .isCloseTo(expectedRetainUntil, within(1, ChronoUnit.DAYS));
    }

    @Then("recupera gli ultimi {int} file nel bucket {string}")
    public void getAllFile(int limit, String bucket) {
        S3BucketInfo bucketInfo = S3BucketInfoBuilder.builder().fullPath(bucket).build();
        ArchivingClient.SearchFileSeed seed = ArchivingClient.SearchFileSeed.builder().bucketInfo(bucketInfo).build();
        List<ArchivedFile> files = client.getAllFilesInS3(seed, limit);

        System.out.println("=== FILE TROVATI (" + files.size() + ") ===");

        files.forEach(f -> {
            System.out.println("\n------------------------------------------");
            System.out.println(f.toString());   // 👈 stampa tutta la struttura
            System.out.println("------------------------------------------");
        });

        System.out.println("\n=== FINE LISTA ===");
    }

    //TODO: stampa del context di sharedStep quando ci sono assertions che falliscono (cosi posso passarlo di pacco ai dev)
}