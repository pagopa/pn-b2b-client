package it.pagopa.pn.interop.cucumber.steps.purpose;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext.ReviewerPayloadOwner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Dinieghi di assegnazione: CDT 1.4, 1.5, 7.5 e sola scrittura 7.7. */
public class PurposeRiskAnalysisAssignmentDeniedSteps {
    private final SharedStepsContext context;
    private final ClientTokenConfigurator tokens;
    private final ObjectMapper mapper = new ObjectMapper();
    private UUID initialPurposeId;
    private JsonNode initialAssignment;

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
        initialAssignment = assignmentData(tokens.getPurposeApiClient().getPurpose(initialPurposeId));
    }

    @Then("l'assegnazione e i dati dell'analisi del rischio sono rimasti invariati")
    public void verifyAssignmentUnchanged() {
        assertThat(initialAssignment).as("Lo snapshot della baseline deve essere disponibile").isNotNull();
        assertThat(UUID.fromString(context.getPurposeCommonContext().getPurposeId())).isEqualTo(initialPurposeId);
        // Nel Then la feature ripristina esplicitamente l'admin del proprietario.
        tokens.setBearerToken(context.getUserToken());
        context.getPollingService().makePolling(
                () -> tokens.getPurposeApiClient().getPurpose(initialPurposeId),
                purpose -> initialAssignment.equals(assignmentData(purpose)),
                "Il tentativo rifiutato ha modificato assegnazione, stato o dati RA"
        );
    }

    private JsonNode assignmentData(Purpose purpose) {
        ObjectNode data = mapper.valueToTree(purpose);
        // Include stato/firmatario/reviewer nel workflow e stato finalità nelle versioni.
        data.retain(List.of("riskAnalysisReviewMode", "reviewerWorkflow", "riskAnalysisForm",
                "metadataVersion", "currentVersion", "versions", "waitingForApprovalVersion",
                "rejectedVersion"));
        return data;
    }
}

