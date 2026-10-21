package it.pagopa.pn.interop.cucumber.steps.purpose;

import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

// TODO utile in fase di sviluppo, potenzialmente superfluo a regime. Considerare rimozione una colta concluso Evolutive nuovi operatori.
class PurposeRiskAnalysisAssignmentDeniedStepsTest {
    private final SharedStepsContext context = mock(SharedStepsContext.class, RETURNS_DEEP_STUBS);
    private final ClientTokenConfigurator configurator = mock(ClientTokenConfigurator.class);
    private final IPurposeApiClient client = mock(IPurposeApiClient.class);
    private final UUID purposeId = UUID.randomUUID();

    private PurposeRiskAnalysisAssignmentDeniedSteps baseline() {
        when(context.getTenantType()).thenReturn("GSP");
        when(context.getPurposeCommonContext().getPurposeId()).thenReturn(purposeId.toString());
        when(configurator.getPurposeApiClient()).thenReturn(client);
        when(client.getPurpose(purposeId)).thenReturn(new Purpose().id(purposeId).title("Originale"));
        when(client.getPurposeMetadataVersion(purposeId)).thenReturn(7);
        PurposeRiskAnalysisAssignmentDeniedSteps steps = new PurposeRiskAnalysisAssignmentDeniedSteps(context, configurator);
        steps.rememberAssignment();
        return steps;
    }

    @Test
    void acceptsEntirePurposeAndMetadataUnchanged() {
        PurposeRiskAnalysisAssignmentDeniedSteps steps = baseline();
        when(client.getPurpose(purposeId)).thenReturn(new Purpose().id(purposeId).title("Originale"));
        assertThatCode(steps::verifyAssignmentUnchanged).doesNotThrowAnyException();
    }

    @Test
    void detectsChangesOutsideRiskAnalysisFields() {
        PurposeRiskAnalysisAssignmentDeniedSteps steps = baseline();
        when(client.getPurpose(purposeId)).thenReturn(new Purpose().id(purposeId).title("Modificata"));
        assertThatThrownBy(steps::verifyAssignmentUnchanged).isInstanceOf(AssertionError.class);
    }

    @Test
    void detectsMetadataVersionChange() {
        PurposeRiskAnalysisAssignmentDeniedSteps steps = baseline();
        when(client.getPurposeMetadataVersion(purposeId)).thenReturn(8);
        assertThatThrownBy(steps::verifyAssignmentUnchanged).isInstanceOf(AssertionError.class);
    }
}
