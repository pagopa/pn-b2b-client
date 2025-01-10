package it.pagopa.pn.interop.cucumber.steps.delegate;

import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationCreateStep.DelegationRole.DELEGATE;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationCreateStep.DelegationRole.DELEGATING;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantFeature;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class DelegationCreateStep {
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final ITenantsApi tenantsApi;
    private final IdentityService identityService;
    private final PollingService pollingService;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public enum DelegationRole {
        DELEGATE,
        DELEGATING
    }

    private final Map<DelegationCreateStep.DelegationRole, String> tenants = new EnumMap<>(DelegationCreateStep.DelegationRole.class);

    public DelegationCreateStep(IProducerDelegationsApiClient producerDelegationsApiClient,
                                IDelegationApiClient delegationApiClient,
                                ITenantsApi tenantsApi,
                                SharedStepsContext sharedStepsContext) {
        this.producerDelegationsApiClient = producerDelegationsApiClient;
        this.delegationApiClient = delegationApiClient;
        this.tenantsApi = tenantsApi;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("l'ente {delegationRole} {string}")
    public void givenDelegatingTenant(DelegationCreateStep.DelegationRole delegationRole, String tenant) {
        this.tenants.put(delegationRole, tenant);
    }

    @Given("un utente dell'ente {delegationRole} con ruolo {string}")
    public void givenUserWithRole(DelegationCreateStep.DelegationRole delegationRole, String iamRole) {
        String tenantType = tenants.get(delegationRole);
        String token = identityService.getToken(tenantType, iamRole);
        identityService.setBearerToken(token);
        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setTenantType(tenantType);
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega all'ente delegato")
    public void givenDelegatingTenantHasRequestedDelegation() {
        String delegatingTenantToken = identityService.getToken(tenants.get(DELEGATING), null);
        identityService.setBearerToken(delegatingTenantToken);
        createDelegate(tenants.get(DELEGATE));
    }

    @And("l'utente concede la disponibilità a ricevere le deleghe")
    public void userGrantsDelegationAvailability() {
        identityService.setBearerToken(sharedStepsContext.getUserToken());
        setDelegationAvailability(sharedStepsContext.getTenantType());
    }

    @And("l'ente {string} concede la disponibilità a ricevere deleghe")
    public void tenantGrantsDelegationAvailability(String tenantType) {
        identityService.setBearerToken(identityService.getToken(tenantType, null));
        setDelegationAvailability(tenantType);
    }

    private void setDelegationAvailability(String tenantType) {
        httpCallExecutor.performCall(tenantsApi::assignTenantDelegatedProducerFeature);
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK)
            pollingService.makePolling(() -> tenantsApi.getTenant(sharedStepsContext.getXCorrelationId(), identityService.getOrganizationId(tenantType)),
                res -> Optional.ofNullable(res.getFeatures())
                        .orElse(List.of())
                        .stream()
                        .map(TenantFeature::getDelegatedProducer)
                        .anyMatch(Objects::nonNull),
                "There was an error while providing the delegation availability!");
    }

    @And("l'ente {string} richiede la creazione di una delega per l'ente {string}")
    public void createDelegate(String delegatorTenantType, String tenantType) {
        identityService.setBearerToken(identityService.getToken(delegatorTenantType, null));
        createDelegate(tenantType);
    }

    @And("l'utente richiede la creazione di una delega per l'ente {string}")
    public void userRequestDelegationCreation(String tenantType) {
        identityService.setBearerToken(sharedStepsContext.getUserToken());
        createDelegate(tenantType);
    }

    @And("la delega è stata creata correttamente")
    public void delegationIsPresent() {
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId()))),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while creating the delegation!"
        );
    }

    private void createDelegate(String tenantType) {
        UUID organizationId = identityService.getOrganizationId(tenantType);
        httpCallExecutor.performCall(() -> producerDelegationsApiClient.createProducerDelegation(sharedStepsContext.getXCorrelationId(),
                new DelegationSeed().eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId()).delegateId(organizationId)));
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK) {
            sharedStepsContext.getDelegationCommonContext().setDelegationId(((CreatedResource) httpCallExecutor.getResponse()).getId());
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(),
                            String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId()))),
                    res -> res != HttpStatus.NOT_FOUND,
                    "There was an error while creating the delegation!"
            );
        }
    }

    @ParameterType("delegato|delegante")
    public DelegationCreateStep.DelegationRole delegationRole(String delegationRole) {
        return switch (delegationRole) {
            case "delegato" -> DELEGATE;
            case "delegante" -> DELEGATING;
            default ->
                    throw new IllegalArgumentException("Invalid delegation role: " + delegationRole);
        };
    }

}
