package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale;

import io.cucumber.java.en.Then;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.ArchivingClient;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.ArchivedFileMatched;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context.ArchivingContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketRole;
import it.pagopa.interop.event.enums.InteropEvent;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.InteropFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.FileInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.registry.FileInfoRegistry;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.within;

public class ArchivingSteps {

    private final ArchivingContext context;
    private final ArchivingClient client;
    private final FileInfoRegistry fileInfoRegistry;

    public ArchivingSteps(@Value("${s3.unsigned-document-base-path}") String documentBucketBase,
                          @Value("${s3.signed-document-base-path}") String documentWormBucketBase,
                          @Value("${s3.unsigned-event-base-path}") String eventBucketBase,
                          @Value("${s3.signed-event-base-path}") String eventWormBucketBase,
                          SharedStepsContext sharedStepsContext) {
        TokenResolver tokenResolver = new TokenResolver(sharedStepsContext);

        this.context = new ArchivingContext();
        this.client = new ArchivingClient();
        this.fileInfoRegistry = new FileInfoRegistry(tokenResolver, documentBucketBase, documentWormBucketBase, eventBucketBase, eventWormBucketBase);
    }

    @Then("verifica che a fronte dell'evento {interopEvent} venga generato nell'opportuno bucket S3 {bucketRole} un {interopFile}")
    public void checkFileInS3Bucket(InteropEvent event, BucketRole bucketRole, InteropFile fileType) {
        FileInfo fileInfo = fileInfoRegistry.getFileInfo(fileType);

        ArchivingClient.PollingSpecification pollingSpecification =
                ArchivingClient.PollingSpecification.builder()
                        .centerTimestamp(context.getCenterTimestamp())
                        .timeoutMs(600_000)
                        .pollIntervalMs(30_000)
                        .deltaSeconds(300)
                        .fileInfo(fileInfo)
                        .bucketRole(bucketRole)
                        .build();

        ArchivedFileMatched archivedFile = client.findS3FileInInterval(pollingSpecification);
        Assertions.assertThat(archivedFile)
                .as("Atteso file %s dopo l'evento %s nel bucket %s ma non è stato trovato", fileType, event.name(), bucketRole)
                .isNotNull();

        context.setMatch(archivedFile);
    }

    @Then("verifica che il file nel bucket WORM abbia la proprietà \"Retain until date\" pari a 10 anni dalla data di creazione")
    public void checkRetainUntilDate() {

        ArchivedFile archivedFile = context.getMatch().file();

        Instant creationDate = archivedFile.getCreationDate();
        Instant expectedRetainUntil = creationDate.atZone(ZoneOffset.UTC).plusYears(10).toInstant();
        Instant actualRetainUntilDate = archivedFile.getRetainUntilDate();

        // Tolleranza di 1 giorno
        Assertions.assertThat(actualRetainUntilDate)
                .as("La Retain until date non è 10 anni dopo la creation date. Creation=%s, Retain=%s", creationDate, actualRetainUntilDate)
                .isCloseTo(expectedRetainUntil, within(1, ChronoUnit.DAYS));
    }

    @Then("verifica che il file contenga le opportune informazioni")
    public void validateFile() {
        ArchivedFileMatched matched = context.getMatch();

        if (matched == null || matched.validation() == null) {
            throw new RuntimeException("Nessun file archiviato valido è stato trovato");
        }

        ValidationResult validation = matched.validation();

        Assertions.assertThat(validation)
                .as("Il risultato della validazione deve essere presente")
                .isNotNull();

        Assertions.assertThat(validation.missingRequired())
                .as("Le informazioni obbligatorie mancanti non sono zero")
                .isEmpty();

        Assertions.assertThat(validation.missingOptional())
                .as("Le informazioni opzionali mancanti non sono zero")
                .isEmpty();
    }
}