package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.conf.api_profile.ApiProfile;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RiskAnalysisReviewer;
import it.pagopa.interop.purpose.service.RiskAnalysisReviewMode;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Verifica della lettura dell'assegnazione via M2M per il CDT 7.7. */
public class PurposeRiskAnalysisM2MReadSteps {
    private final SharedStepsContext context;
    private final ClientTokenConfigurator configurator;

    public PurposeRiskAnalysisM2MReadSteps(SharedStepsContext context, ClientTokenConfigurator configurator) {
        this.context = context;
        this.configurator = configurator;
    }

    @When("l'utente tenta di leggere la finalità tramite M2M V3")
    public void readPurpose() {
        assertThat(context.getApiSet()).isEqualTo(ApiProfile.ApiSet.M2M);
        context.getHttpCallExecutor().performCall(() -> configurator.getM2mV3PurposeClient().getPurposeV3(purposeId()));
    }

    /**
     * Verifica i dati di assegnazione (modalità e valutatori) restituiti da M2M V3.
     * NOTA 19/10/2026: al momento si controlla unicamente la modalità, poiché la GET M2M non espone ancora
     * i valutatori (blocco tracciato su ticket PIN-11189). Alla risoluzione del ticket andrà integrato
     * esclusivamente il corpo di questo metodo, verificando i reviewer restituiti contro initialReviewerActors.
     */
    @Then("la risposta M2M della finalità riporta la modalità {riskAnalysisReviewMode} e i valutatori assegnati")
    public void verifyAssignmentResponse(RiskAnalysisReviewMode mode) {
        assertThat(context.getHttpCallExecutor().getResponseStatus()).isNotNull();
        assertThat(context.getHttpCallExecutor().getResponseStatus().value()).isEqualTo(200);
        assertThat(context.getHttpCallExecutor().getResponse()).isInstanceOf(Purpose.class);
        Purpose purpose = (Purpose) context.getHttpCallExecutor().getResponse();
        assertThat(purpose.getId()).isEqualTo(purposeId());
        assertThat(purpose.getRiskAnalysisReviewMode()).isNotNull();
        assertThat(purpose.getRiskAnalysisReviewMode().getValue()).isEqualTo(mode.getValue());
        if (mode != RiskAnalysisReviewMode.ADMIN_WRITES_ADMIN_SIGNS) {
            assertThat(purpose.getReviewerWorkflow()).isNotNull();
            List<RiskAnalysisCommonContext.AssignedReviewerActorRef> initialReviewerActors = context.getRiskAnalysisCommonContext().getInitialReviewerActors();
            List<UUID> expectedReviewers = initialReviewerActors.stream()
                    .map(actor -> context.getIdentityService().getUserId(actor.tenantType(), actor.role(), actor.index()))
                    .toList();
            List<UUID> actualReviewers = purpose.getReviewerWorkflow().getReviewers() == null
                    ? Collections.emptyList()
                    : purpose.getReviewerWorkflow().getReviewers().stream()
                        .map(RiskAnalysisReviewer::getUserId)
                        .toList();
            assertThat(actualReviewers).containsExactlyInAnyOrderElementsOf(expectedReviewers);
        }
    }

    private UUID purposeId() {
        return UUID.fromString(context.getPurposeCommonContext().getPurposeId());
    }
}
