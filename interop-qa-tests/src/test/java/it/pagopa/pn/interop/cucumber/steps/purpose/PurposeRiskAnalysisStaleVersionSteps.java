package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.purpose.service.mapper.PurposeUpdateMapper;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.apache.commons.collections4.IterableUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

/** Mutazioni sequenziali che riproducono una versione obsoleta senza concorrenza temporale: CDT 3.2. */
public class PurposeRiskAnalysisStaleVersionSteps {
    public enum RiskAnalysisMutation { TITOLO, RISPOSTE }

    private final SharedStepsContext context;
    private final ClientTokenConfigurator configurator;
    private final PurposeUpdateMapper purposeMapper;
    private Integer versionBeforeMutation;
    private Integer versionAfterMutation;
    private Predicate<Purpose> mutationPersisted;
    private String mutationDescription;
    private UUID mutationPurposeId;
    private Purpose purposeAfterMutation;

    public PurposeRiskAnalysisStaleVersionSteps(SharedStepsContext context, ClientTokenConfigurator configurator,
            PurposeUpdateMapper purposeMapper) {
        this.context = context;
        this.configurator = configurator;
        this.purposeMapper = purposeMapper;
    }

    /** Seleziona il dato modificato dall'altro attore autorizzato. */
    @ParameterType("titolo|risposte")
    public static RiskAnalysisMutation riskAnalysisMutation(String value) {
        return RiskAnalysisMutation.valueOf(value.toUpperCase(Locale.ROOT));
    }

    /** Non firma e non aggiorna il seed di firma già acquisito dal primo reviewer. */
    @When("l'utente modifica {riskAnalysisMutation} per rendere obsoleta la versione acquisita")
    public void mutatePurpose(RiskAnalysisMutation mutation) {
        configurator.setBearerToken(context.getUserToken());
        var client = configurator.getPurposeApiClient();
        mutationPurposeId = UUID.fromString(context.getPurposeCommonContext().getPurposeId());
        var purpose = client.getPurpose(mutationPurposeId);
        versionBeforeMutation = client.getPurposeMetadataVersion(mutationPurposeId);
        if (mutation == RiskAnalysisMutation.TITOLO) {
            String expectedTitle = purpose.getTitle() + " - modifica concorrente";
            var payload = purposeMapper.toUpdateContent(purpose).title(expectedTitle);
            mutationPersisted = updated -> expectedTitle.equals(updated.getTitle());
            mutationDescription = "aggiornamento del titolo della finalità";
            context.getHttpCallExecutor().performCall(() -> client.updatePurpose(mutationPurposeId, payload));
        } else {
            var form = purpose.getRiskAnalysisForm();
            var answers = new LinkedHashMap<>(form.getAnswers());
            var previous = answers.get("institutionalPurpose");
            String value = IterableUtils.isEmpty(previous) ? "modifica concorrente"
                    : previous.get(0) + " - modifica concorrente";
            answers.put("institutionalPurpose", List.of(value));
            String expectedVersion = form.getVersion();
            var expectedAnswers = new LinkedHashMap<String, List<String>>();
            answers.forEach((key, values) -> expectedAnswers.put(key, values == null ? null : List.copyOf(values)));
            var payload = new RiskAnalysisFormSeed().version(expectedVersion).answers(answers);
            mutationPersisted = updated -> updated.getRiskAnalysisForm() != null
                    && Objects.equals(expectedVersion, updated.getRiskAnalysisForm().getVersion())
                    && expectedAnswers.equals(updated.getRiskAnalysisForm().getAnswers());
            mutationDescription = "aggiornamento delle risposte dell'analisi del rischio";
            context.getHttpCallExecutor().performCall(() -> client.compileRiskAnalysisForm(mutationPurposeId, payload));
        }
    }

    /** Attende dati aggiornati e versione superiore prima del tentativo con il vecchio seed. */
    @Then("la modifica concorrente è persistita e ha incrementato la versione dei metadati")
    public void verifyMutationPersisted() {
        assertThat(versionBeforeMutation).as("Versione prima della modifica").isNotNull();
        assertThat(mutationPersisted).as("Eseguire prima la modifica e definirne il controllo di persistenza").isNotNull();
        var client = configurator.getPurposeApiClient();
        context.getPollingService().makePolling(() -> client.getPurpose(mutationPurposeId), purpose -> {
            if (!mutationPersisted.test(purpose)) {
                return false;
            }
            Integer version = client.getPurposeMetadataVersion(mutationPurposeId);
            if (version == null || version <= versionBeforeMutation) {
                return false;
            }
            versionAfterMutation = version;
            purposeAfterMutation = purpose;
            return true;
        }, "La modifica concorrente non risulta persistita con una nuova versione metadati: " + mutationDescription);
    }

    /** La firma rifiutata non deve incrementare la versione creata dalla mutazione valida. */
    @Then("il tentativo con versione obsoleta lascia invariati finalità e versione dei metadati")
    public void verifyMetadataUnchanged() {
        assertThat(versionAfterMutation).as("Verificare prima la modifica concorrente").isNotNull();
        configurator.setBearerToken(context.getUserToken());
        assertThat(configurator.getPurposeApiClient().getPurpose(mutationPurposeId))
                .as("La Purpose aggiornata non deve essere modificata dalla firma rifiutata")
                .isEqualTo(purposeAfterMutation);
        assertThat(configurator.getPurposeApiClient().getPurposeMetadataVersion(mutationPurposeId))
                .isEqualTo(versionAfterMutation);
    }
}



