package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.validation_strategy;

import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileValidator;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils.isValidIsoTimestamp;

@RequiredArgsConstructor
public class AgreementActivatedEventSignedValidator implements IFileValidator {

    @Override
    public void validate(ValidatorStrategySeed seed) throws IOException {

        String expectedDelegationId = seed.getTokenResolver().resolve(":agreementId");
        byte[] originalBytes = extractSignedContent(seed);

        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(originalBytes))) {

            // Validazione NDJSON generica
            FileUtils.validateNdjson(
                    gis,

                    // Righe candidate
                    json ->
                            json.has("event_name") &&
                                    "AgreementActivated".equals(json.get("event_name").asText()) &&
                                    json.has("id") &&
                                    expectedDelegationId.equals(json.get("id").asText()),

                    // Validazione campi della riga
                    this::validateRow,

                    // Messaggio se nessuna riga candidata trovata
                    "File %s associato all'evento AgreementActivated non presenta l'id atteso: %s"
                            .formatted(seed.getFile().getFilename(), expectedDelegationId)
            );
        }
    }

    /**
     * Estrae il contenuto firmato da un file P7M.
     */
    private byte[] extractSignedContent(ValidatorStrategySeed seed) {
        try {
            CMSSignedData signedData = new CMSSignedData(seed.getFile().getContent());
            CMSProcessable signedContent = signedData.getSignedContent();
            return (byte[]) signedContent.getContent();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Errore durante l'estrazione del contenuto firmato del file " + seed.getFile().getFilename(),
                    e
            );
        }
    }

    /**
     * Validazione dei campi per una singola riga NDJSON.
     */
    private ValidationResult validateRow(JsonNode json) {
        List<String> errors = new ArrayList<>();

        // state
        String expectedState = "Active";
        String actualState = json.has("state") ? json.get("state").asText() : "<missing>";

        if (!json.has("state")) {
            errors.add("Campo 'state' mancante. Expected: '%s'".formatted(expectedState));
        } else if (!expectedState.equals(actualState)) {
            errors.add("Campo 'state' non valido. Expected: '%s', actual: '%s'"
                    .formatted(expectedState, actualState));
        }

        // timestamp
        String timestamp = json.has("timestamp") ? json.get("timestamp").asText() : "<missing>";

        if (!json.has("timestamp")) {
            errors.add("Campo 'timestamp' mancante.");
        } else if (!isValidIsoTimestamp(timestamp)) {
            errors.add("Campo 'timestamp' non valido. Atteso formato yyyyMMddHHmmss, actual: '%s'"
                    .formatted(timestamp));
        }

        return new ValidationResult(errors.isEmpty(), errors, json.toString());
    }
}


