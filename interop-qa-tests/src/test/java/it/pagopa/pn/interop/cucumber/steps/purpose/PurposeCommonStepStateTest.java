package it.pagopa.pn.interop.cucumber.steps.purpose;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

// TODO utile solo in fase di sviluppo. Considerarne rimozioni una volta implementati tutti i test di Evolutive Nuovi Operatori.
class PurposeCommonStepStateTest {
    private final ClientTokenConfigurator tokens = mock(ClientTokenConfigurator.class);
    private final IdentityService identities = mock(IdentityService.class);
    private final BFFDataPreparationService preparation = mock(BFFDataPreparationService.class);
    private final SharedStepsContext context = mock(SharedStepsContext.class, RETURNS_DEEP_STUBS);
    private final UUID purposeId = UUID.randomUUID();
    private final UUID versionId = UUID.randomUUID();

    private PurposeCommonStep steps() {
        when(identities.getToken("GSP", null)).thenReturn("owner-token");
        when(context.getPurposeCommonContext().getPurposeId()).thenReturn(purposeId.toString());
        when(context.getPurposeCommonContext().getVersionId()).thenReturn(versionId.toString());
        return new PurposeCommonStep(tokens, identities, preparation, context);
    }

    @Test
    void preservesArchivedTransition() {
        steps().moveDescriptorToState("GSP", "ARCHIVED");
        verify(tokens).setBearerToken("owner-token");
        verify(preparation).archivePurpose(purposeId, versionId);
        verifyNoMoreInteractions(preparation);
    }

    @Test
    void preservesSuspendedTransition() {
        steps().moveDescriptorToState("GSP", "SUSPENDED");
        verify(tokens).setBearerToken("owner-token");
        verify(preparation).suspendPurpose(purposeId, versionId, null);
        verifyNoMoreInteractions(preparation);
    }

    @Test
    void preservesUnsupportedStateFailure() {
        PurposeCommonStep steps = steps();
        assertThatThrownBy(() -> steps.moveDescriptorToState("GSP", "UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Passed wrong desired purpose state!");
        verify(tokens).setBearerToken("owner-token");
        verifyNoInteractions(preparation);
    }
}
