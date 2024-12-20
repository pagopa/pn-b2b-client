package it.pagopa.pn.interop.cucumber.steps.delegate;

import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationDenyStep.DelegationRole.DELEGATE;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationDenyStep.DelegationRole.DELEGATING;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

// FIXME 19/12/2024: buona parte dell'attuale contenuto della classe sarà riformulato o rimosso per conciliarsi con quanto fornito dalle altre classi *Step.java
@Slf4j
public class DelegationDenyStep {
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final ITenantsApi tenantsApi;
    private final CommonUtils commonUtils;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    private final DataPreparationService dataPreparationService;

    public enum DelegationRole {
        DELEGATE,
        DELEGATING
    }

    private final Map<DelegationRole, String> tenants = new EnumMap<>(DelegationRole.class);

    public DelegationDenyStep(IProducerDelegationsApiClient producerDelegationsApiClient,
                              IDelegationApiClient delegationApiClient,
                              ITenantsApi tenantsApi,
                              CommonUtils commonUtils,
                              SharedStepsContext sharedStepsContext,
                              HttpCallExecutor httpCallExecutor,
                              DataPreparationService dataPreparationService) {
        this.producerDelegationsApiClient = producerDelegationsApiClient;
        this.delegationApiClient = delegationApiClient;
        this.tenantsApi = tenantsApi;
        this.commonUtils = commonUtils;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = httpCallExecutor;

        this.dataPreparationService = dataPreparationService;
    }

    @Given("l'ente {delegationRole} {string}")
    public void givenDelegatingTenant(DelegationRole delegationRole, String tenant) {
        this.tenants.put(delegationRole, tenant);
    }

    @Given("un utente dell'ente {delegationRole} con ruolo {string}")
    public void givenUserWithRole(DelegationRole delegationRole, String iamRole) {
        String tenantType = tenants.get(delegationRole);

        String token = commonUtils.getToken(tenantType, iamRole);
        commonUtils.setBearerToken(token);
        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setTenantType(tenantType);
    }

    @Given("l'ente delegante ha già creato e pubblicato {int} e-service")
    public void givenDelegatingTenantHasPublishedEService(int totalEservices) {
        String tenantType = tenants.get(DELEGATING);
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        // Create e-services and publish descriptors
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        for (int i = 0; i < totalEservices; i++) {
            // Create e-service and descriptor
            int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            int TEST_SEED = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            String eserviceName = String.format("eservice-%d-%d-%d", i, TEST_SEED, randomInt);
            EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed().name(eserviceName), new UpdateEServiceDescriptorSeed());
            // Set the descriptor to "PUBLISHED" state
            dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(),
                eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.PUBLISHED, false);
            // Add the e-service to the list of published ones
            eServicesCommonContext.getPublishedEservicesIds().add(eServiceDescriptor);
        }
        // Set the first e-service and descriptor
        if (!eServicesCommonContext.getPublishedEservicesIds().isEmpty()) {
            EServiceDescriptor firstDescriptor = eServicesCommonContext.getPublishedEservicesIds().get(0);
            eServicesCommonContext.setEserviceId(firstDescriptor.getEServiceId());
            eServicesCommonContext.setDescriptorId(firstDescriptor.getDescriptorId());
        }
    }

    @Given("l'ente delegato concede la disponibilità a ricevere deleghe")
    public void givenDelegateTenantGrantsDelegationAvailability() throws InterruptedException {
        String tenantType = tenants.get(DELEGATE);
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        httpCallExecutor.performCall(() -> tenantsApi.assignTenantDelegatedProducerFeature());
        Thread.sleep(4000); // FIXME 19/12/2024: sostituire con verifica diretta della concessa delega
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega all'ente delegato")
    public void givenDelegatingTenantHasRequestedDelegation() {
        String delegatingTenantToken = commonUtils.getToken(tenants.get(DELEGATING), null);
        commonUtils.setBearerToken(delegatingTenantToken);
        createDelegate(tenants.get(DELEGATE));
    }

    @Given("l'ente delegato ha accettato la delega")
    public void givenDelegateTenantHasAcceptedDelegation() {
        String tenantType = tenants.get(DELEGATE);

        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        httpCallExecutor.performCall(
            () -> producerDelegationsApiClient.approveDelegation(sharedStepsContext.getXCorrelationId(),
                sharedStepsContext.getDelegationCommonContext().getDelegationId()));

        // wait until delegation is correctly approved
        commonUtils.makePolling(
            () -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(),
                String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId())),
            res ->  res.getState().equals(DelegationState.ACTIVE),
            "There was an error while accepting the delegation!"
        );
    }

    @When("l'utente rifiuta la delega")
    public void whenUserRejectsDelegation() {
        String authToken = sharedStepsContext.getUserToken();
        commonUtils.setBearerToken(authToken);
        httpCallExecutor.performCall(
            () -> producerDelegationsApiClient.rejectDelegation(sharedStepsContext.getXCorrelationId(),
                sharedStepsContext.getDelegationCommonContext().getDelegationId(),
                new RejectDelegationPayload().rejectionReason("Missing all required data!")));
    }

    @Then("si ottiene lo status code {int}")
    public void thenStatusCodeIs(int statusCode) {
        int actualStatusCode = httpCallExecutor.getClientResponse().value();
        if (isSuccessful(statusCode)) Assertions.assertEquals(200, actualStatusCode);
        else Assertions.assertEquals(statusCode, actualStatusCode);
    }

    // solution from https://stackoverflow.com/questions/34718083/is-it-possible-to-pass-java-enum-as-argument-from-cucumber-feature-file
    @ParameterType("delegato|delegante")
    public DelegationRole delegationRole(String delegationRole) {
        return switch (delegationRole) {
            case "delegato" -> DELEGATE;
            case "delegante" -> DELEGATING;
            default ->
                throw new IllegalArgumentException("Invalid delegation role: " + delegationRole);
        };
    }

    private void createDelegate(String tenantType) {
        UUID organizationId = commonUtils.getOrganizationId(tenantType);
        httpCallExecutor.performCall(() -> producerDelegationsApiClient.createProducerDelegation(sharedStepsContext.getXCorrelationId(),
            new DelegationSeed().eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId()).delegateId(organizationId)));
        sharedStepsContext.getDelegationCommonContext().setDelegationId(((CreatedResource) httpCallExecutor.getResponse()).getId());

    }

    boolean isSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }
}
