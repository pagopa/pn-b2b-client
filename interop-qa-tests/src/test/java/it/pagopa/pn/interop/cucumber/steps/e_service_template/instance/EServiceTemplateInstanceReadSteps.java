package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import static org.apache.commons.collections4.IterableUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstance;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.assertj.core.api.Condition;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving quotas of E-service templates */
@Data
public class EServiceTemplateInstanceReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final IEServiceClient eServiceClient;

    public EServiceTemplateInstanceReadSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
    }

    @When("l'utente tenta la visualizzazione dell'elenco di tutte le istanze dell'e-service template")
    public void getEServiceTemplateInstances() {
        getEserviceTemplateInstances(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id());
    }

    @When("l'utente tenta la visualizzazione dell'elenco di tutte le istanze di un e-service template inesistente")
    public void getNotExistentEServiceTemplateInstances() {
        getEserviceTemplateInstances(UUID.randomUUID());
    }

    @Then("sono state visualizzate {int} istanza in stato DRAFT, {int} in stato PUBLISHED e {int} in stato SUSPENDED")
    public void checkEServiceTemplateInstancesCount(int draftCount, int publishedCount, int suspendedCount) {
        List<EServiceTemplateInstance> response = ((ResponseEntity<EServiceTemplateInstances>) httpCallExecutor.getResponse()).getBody().getResults();
        assertSoftly(softly -> {
            softly.assertThat(response)
                .areExactly(
                    draftCount,
                    instanceInState(EServiceDescriptorState.DRAFT));
            softly.assertThat(response)
                .areExactly(
                    publishedCount,
                    instanceInState(EServiceDescriptorState.PUBLISHED));
            softly.assertThat(response)
                .areExactly(
                    suspendedCount,
                    instanceInState(EServiceDescriptorState.SUSPENDED));
        });
    }

    @Then("l'elenco delle istanze dell'e-service template è vuoto")
    public void checkEmptyEServiceTemplateInstances() {
        List<EServiceTemplateInstance> response = ((ResponseEntity<EServiceTemplateInstances>) httpCallExecutor.getResponse()).getBody().getResults();
        assertThat(response).isEmpty();
    }

    private void getEserviceTemplateInstances(UUID templateId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                templateId
            ),
            ResponseEntity::getStatusCode);
    }

    // 28/03/2025 Versione precedente
    /*private Condition<EServiceTemplateInstance> instanceInState(EServiceDescriptorState state) {
        if (state == EServiceDescriptorState.DRAFT) {
            return new Condition<>(
                instance -> isEmpty(instance.getDescriptors()),
                "non-empty descriptors for expected DRAFT e-services", state);
        } else {
            return new Condition<>(
                instance -> instance.getLatestDescriptor().getState().equals(state),
                "instances in state %s", state);
        }
    }*/

    // Versione precedente
    /*private Condition<EServiceTemplateInstance> instanceInState(EServiceDescriptorState state) {
        return new Condition<>(
            instance -> (isEmpty(instance.getDescriptors()) && state == EServiceDescriptorState.DRAFT) ||
            instance.getLatestDescriptor().getState().equals(state),
            "instances in state %s", state);
    }*/

    private Condition<EServiceTemplateInstance> instanceInState(EServiceDescriptorState state) {
        return new Condition<>(
            instance -> {
                if (isEmpty(instance.getDescriptors())) {
                    return state == EServiceDescriptorState.DRAFT;
                } else {
                    return instance.getLatestDescriptor().getState().equals(state);
                }
            },
            "instances in state %s", state);
    }
}
