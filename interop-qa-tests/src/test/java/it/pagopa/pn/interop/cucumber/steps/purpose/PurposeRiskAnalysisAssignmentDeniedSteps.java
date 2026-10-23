package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext.ReviewerPayloadOwner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Dinieghi di assegnazione: CDT 1.4, 1.5, 7.5, 7.6 e sola scrittura 7.7. */
public class PurposeRiskAnalysisAssignmentDeniedSteps {
    private final SharedStepsContext context;
    private final ClientTokenConfigurator tokens;
    private UUID initialPurposeId;
    private Purpose initialAssignment;
    private Integer initialMetadataVersion;

    public PurposeRiskAnalysisAssignmentDeniedSteps(SharedStepsContext context, ClientTokenConfigurator tokens) {
        this.context = context;
        this.tokens = tokens;
    }

    @Given("si memorizzano l'assegnazione e i dati dell'analisi del rischio della finalità")
    public void rememberAssignment() {
        String ownerTenant = context.getTenantType();
        initialPurposeId = UUID.fromString(context.getPurposeCommonContext().getPurposeId());
        context.getRiskAnalysisCommonContext().setReviewerPayloadOwner(new ReviewerPayloadOwner(initialPurposeId, ownerTenant));
        tokens.setBearerToken(context.getUserToken());
        initialAssignment = tokens.getPurposeApiClient().getPurpose(initialPurposeId);
        initialMetadataVersion = tokens.getPurposeApiClient().getPurposeMetadataVersion(initialPurposeId);
    }

    @Then("l'assegnazione e i dati dell'analisi del rischio sono rimasti invariati")
    public void verifyAssignmentUnchanged() {
        assertThat(initialAssignment).as("Lo snapshot della baseline deve essere disponibile").isNotNull();
        assertThat(UUID.fromString(context.getPurposeCommonContext().getPurposeId())).isEqualTo(initialPurposeId);
        // Nel Then la feature ripristina esplicitamente l'admin del proprietario.
        tokens.setBearerToken(context.getUserToken());
        Purpose actualAssignment = tokens.getPurposeApiClient().getPurpose(initialPurposeId);
        Integer actualMetadataVersion = tokens.getPurposeApiClient().getPurposeMetadataVersion(initialPurposeId);
        int expectedMetadataVersion = initialMetadataVersion;
        if (isAsyncRiskAnalysisDocumentGeneratedAfterSnapshot(initialAssignment, actualAssignment)) {
            clearRiskAnalysisDocument(actualAssignment);
            expectedMetadataVersion = initialMetadataVersion + 1;
        }
        assertThat(actualAssignment)
                .as("Il diniego non deve modificare alcun campo della Purpose").isEqualTo(initialAssignment);
        assertThat(actualMetadataVersion)
                .as("Il diniego non deve modificare la versione dei metadati").isEqualTo(expectedMetadataVersion);
    }

    private boolean isAsyncRiskAnalysisDocumentGeneratedAfterSnapshot(Purpose initial, Purpose actual) {
        PurposeVersion initialCurrent = initial != null ? initial.getCurrentVersion() : null;
        PurposeVersion actualCurrent = actual != null ? actual.getCurrentVersion() : null;
        return initialCurrent != null
                && actualCurrent != null
                && initialCurrent.getState() == PurposeVersionState.ACTIVE
                && actualCurrent.getState() == PurposeVersionState.ACTIVE
                && initialCurrent.getRiskAnalysisDocument() == null
                && actualCurrent.getRiskAnalysisDocument() != null;
    }

    private void clearRiskAnalysisDocument(Purpose purpose) {
        if (purpose.getCurrentVersion() != null) {
            purpose.getCurrentVersion().setRiskAnalysisDocument(null);
        }
        if (purpose.getVersions() != null) {
            purpose.getVersions().forEach(v -> v.setRiskAnalysisDocument(null));
        }
    }
}

