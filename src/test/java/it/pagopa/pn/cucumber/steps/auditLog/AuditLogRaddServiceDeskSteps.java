package it.pagopa.pn.cucumber.steps.auditLog;

import io.cucumber.java.en.Then;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.util.CloudWatchLogsService;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step per PN-21832: verificano che l'identita' di un operatore RADD che invoca un'API pubblica di pn-radd-alt
 * (flusso ACT: actInquiry, startTransaction, completeTransaction) raggiunga effettivamente gli audit log di
 * pn-delivery/pn-delivery-push prodotti dalle chiamate interne che quell'API pubblica scatena, cosi' come
 * introdotto dalla PR #388 di pn-radd-alt. Le chiamate pubbliche avvengono tramite i client/step gia' esistenti
 * per il flusso reale (non tramite il client ad-hoc {@code AuditLogPrivateApiClient} usato da
 * {@link AuditLogOptionalHeadersSteps} per QA-19752, che bypassa il gateway e chiama direttamente le API private):
 * per pn-radd-alt l'identita' (cx_id/cx_type) deriva dal bearer token JWT usato, quindi qui si verifica solo che
 * sia stata propagata (presenza dei campi), non un valore specifico.
 */
@Slf4j
public class AuditLogRaddServiceDeskSteps {

    private final SharedSteps sharedSteps;
    private final CloudWatchLogsService cloudWatchLogsService;

    @Autowired
    public AuditLogRaddServiceDeskSteps(SharedSteps sharedSteps, CloudWatchLogsService cloudWatchLogsService) {
        this.sharedSteps = sharedSteps;
        this.cloudWatchLogsService = cloudWatchLogsService;
    }

    /**
     * Verifica che l'audit log riporti l'identita' di chi ha effettuato la chiamata (cx_id, cx_type, uid tutti
     * valorizzati), senza pretendere un valore specifico: per un operatore RADD l'identita' deriva dal bearer
     * token JWT usato per autenticarsi, non da un header impostato esplicitamente dal test.
     */
    @Then("verifico che l'audit log con tag {string} su {string} riporti l'identità del chiamante, ossia i campi cx_id, cx_type e uid valorizzati")
    public void verificaAuditLogRiportaIdentitaChiamante(String tag, String logGroup) {
        List<String> requiredFields = List.of("cx_id", "cx_type", "uid");
        try {
            Poller.pollUntil(
                    () -> hasFieldsPresentIgnoringTransientErrors(logGroup, tag, requiredFields),
                    found -> {
                        assertThat(found).as("Nessun audit log %s su %s riporta cx_id/cx_type/uid tutti valorizzati", tag, logGroup).isTrue();
                    },
                    PollingConfig.of(Duration.ofMinutes(2), Duration.ofSeconds(10)));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /** Ignora errori transitori di CloudWatch (es. throttling) riprovando al giro di polling successivo, invece di far fallire subito il check. */
    private boolean hasFieldsPresentIgnoringTransientErrors(String logGroup, String tag, List<String> requiredFields) {
        try {
            return cloudWatchLogsService.hasFieldsPresent(logGroup, "\"" + tag + "\"", 20, requiredFields);
        } catch (RuntimeException e) {
            log.warn("Errore transitorio durante la ricerca su CloudWatch, nuovo tentativo al prossimo giro di polling", e);
            return false;
        }
    }
}
