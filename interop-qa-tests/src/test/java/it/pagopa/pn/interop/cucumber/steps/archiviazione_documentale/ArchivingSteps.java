package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale;

import io.cucumber.java.en.Then;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.ArchivingClient;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context.ArchivingContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.FileMatcher;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfo;
import org.assertj.core.api.Assertions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.within;

public class ArchivingSteps {

    private final ArchivingContext context;
    private final ArchivingClient client;

    public ArchivingSteps(SharedStepsContext sharedStepsContext, ArchivingContext context) {
        this.context = context;
        context.setSharedStepsContext(sharedStepsContext);
        this.client = new ArchivingClient(sharedStepsContext);
    }

    @Then("verifica nel bucket S3 {bucketType} l'esistenza del file {documentType}")
    public void checkS3Bucket(boolean isSigned, FileType fileType){
        S3BucketInfo bucketInfo = isSigned ? context.getWormBuckets().get(fileType) : context.getBuckets().get(fileType);
        ArchivingClient.SearchFileSeed seed =
                ArchivingClient.SearchFileSeed.builder()
                        .bucketInfo(bucketInfo).type(fileType).isSigned(isSigned).build();

        ArchivedFile file = client.findS3FileInInterval(seed);
        Assertions.assertThat(file)
                .as("Atteso file %s nel bucket %s ma non è stato trovato", bucketInfo.getKey(), bucketInfo.getBucket())
                .isNotNull();
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

    //TODO: stampa del context di sharedStep quando ci sono assertions che falliscono (cosi posso passarlo di pacco ai dev)
}