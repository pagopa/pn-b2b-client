package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.*;

import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationCreateStep.DelegationRole.DELEGATE;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationCreateStep.DelegationRole.DELEGATING;

@Slf4j
public class DelegationCreateStep {
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final ITenantsApi tenantsApi;
    private final CommonUtils commonUtils;
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
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("l'ente {delegationRole} {string}")
    public void givenDelegatingTenant(DelegationCreateStep.DelegationRole delegationRole, String tenant) {
        this.tenants.put(delegationRole, tenant);
    }

    @Given("un utente dell'ente {delegationRole} con ruolo {string}")
    public void givenUserWithRole(DelegationCreateStep.DelegationRole delegationRole, String iamRole) {
        String tenantType = tenants.get(delegationRole);
        String token = commonUtils.getToken(tenantType, iamRole);
        commonUtils.setBearerToken(token);
        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setTenantType(tenantType);
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega all'ente delegato")
    public void givenDelegatingTenantHasRequestedDelegation() {
        String delegatingTenantToken = commonUtils.getToken(tenants.get(DELEGATING), null);
        commonUtils.setBearerToken(delegatingTenantToken);
        createDelegate(tenants.get(DELEGATE));
    }

    @And("l'utente concede la disponibilità a ricevere le deleghe")
    public void userGrantsDelegationAvailability() {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        setDelegationAvailability(sharedStepsContext.getTenantType());
    }

    @And("l'ente {string} concede la disponibilità a ricevere deleghe")
    public void tenantGrantsDelegationAvailability(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        setDelegationAvailability(tenantType);
    }

    private void setDelegationAvailability(String tenantType) {
        httpCallExecutor.performCall(() -> tenantsApi.assignTenantDelegatedProducerFeature());
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK)
            commonUtils.makePolling(() -> tenantsApi.getTenant(sharedStepsContext.getXCorrelationId(), commonUtils.getOrganizationId(tenantType)),
                res -> Optional.ofNullable(res.getFeatures())
                        .orElse(List.of())
                        .stream()
                        .map(TenantFeature::getDelegatedProducer)
                        .anyMatch(Objects::nonNull),
                "There was an error while providing the delegation availability!");
    }

    @And("l'ente {string} richiede la creazione di una delega per l'ente {string}")
    public void createDelegate(String delegatorTenantType, String tenantType) throws InterruptedException {
        commonUtils.setBearerToken(commonUtils.getToken(delegatorTenantType, null));
        createDelegate(tenantType);
    }

    @And("l'utente richiede la creazione di una delega per l'ente {string}")
    public void userRequestDelegationCreation(String tenantType) {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        createDelegate(tenantType);
    }

    @And("la delega è stata creata correttamente")
    public void delegationIsPresent() {
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId()))),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while creating the delegation!"
        );
    }

    private void createDelegate(String tenantType) {
        UUID organizationId = commonUtils.getOrganizationId(tenantType);
        httpCallExecutor.performCall(() -> producerDelegationsApiClient.createProducerDelegation(sharedStepsContext.getXCorrelationId(),
                new DelegationSeed().eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId()).delegateId(organizationId)));
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK) {
            sharedStepsContext.getDelegationCommonContext().setDelegationId(((CreatedResource) httpCallExecutor.getResponse()).getId());
            commonUtils.makePolling(
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
