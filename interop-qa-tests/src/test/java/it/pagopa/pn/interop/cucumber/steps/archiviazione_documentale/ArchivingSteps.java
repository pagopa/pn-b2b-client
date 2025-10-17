package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale;

import io.cucumber.java.en.Then;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.ArchivingClient;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context.FileContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileTypes;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.FileMatcher;
import org.assertj.core.api.Assertions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.within;

public class ArchivingSteps {

    private final SharedStepsContext sharedStepsContext;
    private final ArchivingClient client;
    private final FileMatcher fileMatcher;

    public ArchivingSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
        this.fileMatcher = new FileMatcher();
        this.client = new ArchivingClient(fileMatcher);
    }

    @Then("verifica nel bucket S3 {string} l'esistenza del file unsigned {string} con estensione {string}")
    @Then("verifica nel bucket S3 WORM {string} l'esistenza del file signed {string} con estensione {string}")
    public void checkS3Bucket(String bucketName, String regex, FileTypes fileType){
        boolean finded = client.matchS3FileInInterval(bucketName, fileType, regex, "", 100,100,100 );
        Assertions.assertThat(finded)
                .as("Atteso file %s nel bucket %s con pattern %s ma non è stato trovato", fileType, bucketName, regex)
                .isTrue();
    }

    @Then("verifica che il file nel bucket WORM abbia la proprietà \"Retain until date\" pari a 10 anni dalla data di creazione")
    public void checkRetainUntilDate() {
        FileContext ctx = fileMatcher.getContext();

        Instant creationDate = ctx.getCreationDate();
        Instant expectedRetainUntil = creationDate.plus(10, ChronoUnit.YEARS);

        // Tolleranza di 1 giorno
        Assertions.assertThat(ctx.getRetainUntilDate())
                .as("La Retain until date non è 10 anni dopo la creation date. Creation=%s, Retain=%s", creationDate, ctx.getRetainUntilDate())
                .isCloseTo(expectedRetainUntil, within(1, ChronoUnit.DAYS));
    }

    //TODO: stampa del context di sharedStep quando ci sono assertions che falliscono (cosi posso passarlo di pacco ai dev)
}