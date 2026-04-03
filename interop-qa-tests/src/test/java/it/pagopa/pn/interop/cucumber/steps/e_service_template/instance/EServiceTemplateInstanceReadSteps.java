package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import static org.apache.commons.collections4.IterableUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstance;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.springframework.http.ResponseEntity;

/**
 * Cucumber steps involving quotas of E-service templates
 */
public class EServiceTemplateInstanceReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final IEServiceClient eServiceClient;
    private List<EServiceTemplateInstance> eServiceTemplateInstances;
    private final EServiceTemplateInstanceUtility eServiceTemplateInstanceUtility;

    public EServiceTemplateInstanceReadSteps(ClientTokenConfigurator clientTokenConfigurator,
                                             SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
        this.eServiceTemplateInstanceUtility = new EServiceTemplateInstanceUtility(sharedStepsContext);
    }

    @When("l'utente tenta la visualizzazione dell'elenco di tutte le istanze dell'e-service template")
    public void getEServiceTemplateInstances() {
        getEserviceTemplateInstances(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId());
    }

    @When("l'utente tenta la visualizzazione dell'elenco di tutte le istanze di un e-service template inesistente")
    public void getNotExistentEServiceTemplateInstances() {
        getEserviceTemplateInstances(UUID.randomUUID());
    }

    @When("l'utente tenta la visualizzazione dell'elenco delle istanze dell'e-service template filtrando per offset {int}, limit {int} e producerName {string}")
    public void getEServiceTemplateInstancesWithProducerNameFilter(int offset, int limit, String producerName) {
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        String tenantName = eServiceTemplateInstanceUtility.resolveTenantName(producerName);
        getEserviceTemplateInstancesWithFilters(templateId, offset, limit, tenantName, null);
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

    @Then("sono state visualizzate solo e soltanto {int} istanze, tutte in stato {eServiceDescriptorState}")
    public void checkEServiceTemplateInstancesCount(int instanceCount, EServiceDescriptorState expectedState) {
        List<EServiceTemplateInstance> response = ((ResponseEntity<EServiceTemplateInstances>) httpCallExecutor.getResponse()).getBody().getResults();
        assertThat(response)
                .hasSize(instanceCount)
                .are(instanceInState(expectedState));
    }

    @Then("l'elenco delle istanze dell'e-service template è vuoto")
    public void checkEmptyEServiceTemplateInstances() {
        List<EServiceTemplateInstance> response = ((ResponseEntity<EServiceTemplateInstances>) httpCallExecutor.getResponse()).getBody().getResults();
        assertThat(response).isEmpty();
    }

    @Then("l'elenco delle istanze e-service template restituite contiene l'ultimo e-service template istanziato")
    public void checkEServiceTemplateInstancesContainsLastCreated() {
        List<EServiceTemplateInstance> response = ((ResponseEntity<EServiceTemplateInstances>) httpCallExecutor.getResponse()).getBody().getResults();
        UUID lastCreatedId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceCreatedFromTemplate().getId();
        assertThat(response)
                .extracting(EServiceTemplateInstance::getId)
                .as(String.format("L'elenco delle istanze non contiene l'e-service template istanziato con id '%s'", lastCreatedId))
                .containsExactly(lastCreatedId);
    }

    @When("l'utente recupera le proprie istanze e-service template create dall'e-service template {string}")
    public void getMyEServiceTemplateInstances(String eServiceTemplateId) {
        UUID templateEServiceId = eServiceTemplateInstanceUtility.resolveEServiceTemplateId(eServiceTemplateId);
        httpCallExecutor.performCall(
                () -> eServiceClient.getMyEServiceTemplateInstancesWithHttpInfo(
                        templateEServiceId, 0, 50
                ),
                res -> {
                    if (res.getStatusCode().is2xxSuccessful()) {
                        this.eServiceTemplateInstances = res.getBody().getResults();
                    }
                    return res.getStatusCode();
                }
        );
    }

    @When("ottengo solo l'ultimo e-service creato dall'ente prodotti dall'e-service template")
    public void checkMyEServiceTemplateInstances() {
        assertThat(this.eServiceTemplateInstances).hasSize(1);
        UUID eServiceTemplateInstanceId = this.sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceCreatedFromTemplate().getId();
        Assertions.assertThat(this.eServiceTemplateInstances.get(0).getId()).isEqualTo(eServiceTemplateInstanceId);
    }

    private void getEserviceTemplateInstances(UUID templateId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
                () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                        templateId
                ),
                ResponseEntity::getStatusCode);
    }

    private void getEserviceTemplateInstancesWithFilters(UUID templateId, Integer offset, Integer limit, String producerName, List<EServiceDescriptorState> states) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
                () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                        templateId,
                        offset,
                        limit,
                        producerName,
                        states
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
