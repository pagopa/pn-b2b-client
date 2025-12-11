package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.validation_strategy;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileValidator;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils.isValidIsoTimestamp;

@RequiredArgsConstructor
public class ConsumerDelegationApprovedEventValidator implements IFileValidator {

    @Override
    public void validate(ValidatorStrategySeed seed) throws IOException {

        String expectedDelegationId = seed.getTokenResolver().resolve(":consumerDelegationId");

        InputStream s3Stream = seed.getFile().getContent();
        GZIPInputStream gis = new GZIPInputStream(s3Stream);

        FileUtils.validateNdjson(
                gis,

                // SELEZIONE DELLE RIGHE CANDIDATE
                json ->
                        json.has("event_name") &&
                                json.get("event_name").asText().equals("ConsumerDelegationApproved") &&
                                json.has("id") &&
                                json.get("id").asText().equals(expectedDelegationId),

                // VALIDAZIONE APPROFONDITA PER UNA RIGA
                json -> {
                    List<String> errors = new ArrayList<>();

                    // state
                    String expectedState = "Active";
                    String actualState = json.has("state") ? json.get("state").asText() : "<missing>";

                    if (!json.has("state")) {
                        errors.add("Campo 'state' mancante. Expected: '" + expectedState + "'");
                    } else if (!expectedState.equals(actualState)) {
                        errors.add("Campo 'state' non valido. Expected: '" + expectedState + "', actual: '" + actualState + "'");
                    }

                    // timestamp
                    String timestamp = json.has("timestamp") ? json.get("timestamp").asText() : "<missing>";

                    if (!json.has("timestamp")) {
                        errors.add("Campo 'timestamp' mancante.");
                    } else if (!isValidIsoTimestamp(timestamp)) {
                        errors.add("Campo 'timestamp' non valido. Expected formato yyyyMMddHHmmss, actual: '" + timestamp + "'");
                    }

                    return new ValidationResult(errors.isEmpty(), errors, json.toString());
                },

                // MESSAGGIO SE NESSUNA RIGA CANDIDATA È STATA TROVATA
                String.format("File %s associato all'evento ConsumerDelegationApproved non presenta l'id atteso: %s", seed.getFile().getFilename(), expectedDelegationId)
        );
    }
}

