package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
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
        assertThat(tokens.getPurposeApiClient().getPurpose(initialPurposeId))
                .as("Il diniego non deve modificare alcun campo della Purpose").isEqualTo(initialAssignment);
        assertThat(tokens.getPurposeApiClient().getPurposeMetadataVersion(initialPurposeId))
                .as("Il diniego non deve modificare la versione dei metadati").isEqualTo(initialMetadataVersion);
    }
}

