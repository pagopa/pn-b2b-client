package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Then;
import it.pagopa.interop.event.enums.InteropEvent;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.ArchivingClient;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.ArchivedFileMatched;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketRole;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context.ArchivingContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.InteropFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.FileInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.registry.FileInfoRegistry;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import it.pagopa.pn.interop.cucumber.steps.common.AuditTokenContext;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Slf4j
public class ArchivingSteps {

    private final ArchivingContext context;
    private final AuditTokenContext auditTokenContext;
    private final ArchivingClient client;
    private final FileInfoRegistry fileInfoRegistry;

    public ArchivingSteps(@Value("${s3.unsigned-document-base-path}") String documentBucketBase,
                          @Value("${s3.signed-document-base-path}") String documentWormBucketBase,
                          @Value("${s3.unsigned-event-base-path}") String eventBucketBase,
                          @Value("${s3.signed-event-base-path}") String eventWormBucketBase,
                          @Value("${s3.unsigned-jwt-details-path}") String jwtDetailsBucketBase,
                          @Value("${s3.signed-jwt-details-path}") String jwtDetailsSignedBucketBase,
                          @Value("${s3.unsigned-m2m-jwt-details-path}") String m2mJwtDetailsBucketBase,
                          @Value("${s3.signed-m2m-jwt-details-path}") String m2mJwtDetailsSignedBucketBase,
                          SharedStepsContext sharedStepsContext) {
        TokenResolver tokenResolver = new TokenResolver(sharedStepsContext);

        this.context = new ArchivingContext();
        this.auditTokenContext = sharedStepsContext.getAuditTokenContext();
        this.client = new ArchivingClient();
        this.fileInfoRegistry = new FileInfoRegistry(tokenResolver, documentBucketBase, documentWormBucketBase,
                eventBucketBase, eventWormBucketBase,
                jwtDetailsBucketBase, jwtDetailsSignedBucketBase,
                m2mJwtDetailsBucketBase, m2mJwtDetailsSignedBucketBase);
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

    @Then("verifica che le informazioni di audit sul bucket S3 {string} contengano i seguenti dati per il voucher generato:")
    public void checkFileInS3Bucket(String bucketType, List<Map<String, String>> rows) throws IOException {

        BucketRole bucketRole;
        InteropFile fileType;
        switch (bucketType.toUpperCase()) {
            case "PERSISTENZA" -> {
                bucketRole = BucketRole.STANDARD;
                fileType = InteropFile.AUDIT_JWT_EVENTS_LOG;
            }
            case "SIGNED" -> {
                bucketRole = BucketRole.SIGNED;
                fileType = InteropFile.AUDIT_JWT_EVENTS_LOG;
            }
            case "PERSISTENZA M2M" -> {
                bucketRole = BucketRole.STANDARD;
                fileType = InteropFile.AUDIT_JWT_M2M_EVENTS_LOG;
            }
            case "SIGNED M2M" -> {
                bucketRole = BucketRole.SIGNED;
                fileType = InteropFile.AUDIT_JWT_M2M_EVENTS_LOG;
            }
            default -> throw new IllegalArgumentException("Tipo di bucket non riconosciuto: " + bucketType);
        }

        FileInfo fileInfo = fileInfoRegistry.getFileInfo(fileType);

        ArchivingClient.PollingSpecification pollingSpecification =
                ArchivingClient.PollingSpecification.builder()
                        .centerTimestamp(context.getCenterTimestamp())
                        .timeoutMs(600_000)
                        .pollIntervalMs(5_000)
                        .deltaSeconds(300)
                        .fileInfo(fileInfo)
                        .bucketRole(bucketRole)
                        .build();

        ArchivedFileMatched archivedFile = client.findS3FileInInterval(pollingSpecification);

        Assertions.assertThat(archivedFile)
                .as("Atteso file %s nel bucket %s ma non è stato trovato", fileType, bucketRole)
                .isNotNull();
        context.setMatch(archivedFile);

        checkArchivedFile(archivedFile, rows, bucketRole);
    }

    private void checkArchivedFile(ArchivedFileMatched archivedFile, List<Map<String, String>> rows, BucketRole bucketRole) throws IOException {

        List<JsonNode> jsonNodes;

        if (bucketRole == BucketRole.SIGNED) {
            ProcessedFile processed = client.normalizeFile(archivedFile);
            jsonNodes = FileUtils.readNdjsonLines(processed.content());
        } else {
            jsonNodes = FileUtils.readNdjsonLines(archivedFile.file().getContent());
        }

        JsonNode jsonNode = jsonNodes.stream()
                .filter(node -> node.has("jwtId") && node.get("jwtId").asText().equals(auditTokenContext.getJwtId()))
                .findFirst()
                .orElse(null);

        Assertions.assertThat(jsonNode)
                .as("Impossibile trovare il nodo con jwtId: %s nel bucket: %s", auditTokenContext.getJwtId(), bucketRole)
                .isNotNull();

        assertSoftly(softly -> {
            for (Map<String, String> row : rows) {
                String position = row.get("position");
                String archivedField = row.get("element");
                String contextField = row.get("context");
                String auditField = (contextField == null || contextField.isBlank()) ? archivedField : contextField;

                Map<String, String> contextValues = switch (position) {
                    case "header" -> auditTokenContext.getHeaders();
                    case "payload" -> auditTokenContext.getPayload();
                    default -> throw new IllegalArgumentException("Invalid position: " + position);
                };

                Object actualValue = AuditTokenContext.resolveFieldValue(jsonNode, archivedField);
                String expectedValue = contextValues.get(auditField);

                softly.assertThat(actualValue)
                        .as("Il campo '%s' non è presente", auditField)
                        .isNotNull();
                if (actualValue != null) {
                    softly.assertThat(actualValue.toString())
                            .as("Il valore del campo '%s' non corrisponde a quello del file", auditField)
                            .isEqualTo(expectedValue);
                }
            }
        });
    }
}