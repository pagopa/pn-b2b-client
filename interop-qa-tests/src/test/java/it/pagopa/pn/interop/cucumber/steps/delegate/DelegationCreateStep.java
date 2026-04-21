package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.delegate.service.IConsumerDelegationsApiClient;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationAcceptStep.approveProducerDelegation;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationCreateStep.DelegationAvailabilityStrategy.consumerStrategyUsing;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationCreateStep.DelegationAvailabilityStrategy.producerStrategyUsing;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole.DELEGATE;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole.DELEGATING;

@Slf4j
public class DelegationCreateStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final IConsumerDelegationsApiClient consumerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final ITenantsApi tenantsApi;
    private final IdentityService identityService;
    private final PollingService pollingService;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private boolean isEServiceTemplateInstance;

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DelegationAvailabilityStrategy<U> {
        BiConsumer<Boolean, Boolean> delegationAvailabilityDeclarer;
        Function<TenantFeature, U> featureExtractor;

        public static DelegationAvailabilityStrategy<DelegatedProducer> producerStrategyUsing(ITenantsApi apiSet) {
            return new DelegationAvailabilityStrategy<>(apiSet::updateTenantDelegatedFeatures, TenantFeature::getDelegatedProducer);
        }

        public static DelegationAvailabilityStrategy<DelegatedConsumer> consumerStrategyUsing(ITenantsApi apiSet) {
            return new DelegationAvailabilityStrategy<>(apiSet::updateTenantDelegatedFeatures, TenantFeature::getDelegatedConsumer);
        }
    }

    public DelegationCreateStep(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.producerDelegationsApiClient = clientTokenConfigurator.getProducerDelegationsApiClient();
        this.consumerDelegationsApiClient = clientTokenConfigurator.getConsumerDelegationsApiClient();
        this.delegationApiClient = clientTokenConfigurator.getDelegationApiClient();
        this.tenantsApi = clientTokenConfigurator.getTenantsApi();
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("l'ente {delegationRole} {string}")
    public void givenDelegatingTenant(DelegationRole delegationRole, String tenant) {
        final UUID organizationId = identityService.getOrganizationId(tenant);
        switch (delegationRole) {
            case DELEGATE -> {
                sharedStepsContext.getDelegationCommonContext().setDelegateTenant(tenant);
                sharedStepsContext.getDelegationCommonContext().setDelegateId(organizationId);
            }
            case DELEGATING -> {
                sharedStepsContext.getDelegationCommonContext().setDelegatorTenant(tenant);
                sharedStepsContext.getDelegationCommonContext().setDelegatorId(organizationId);
            }
            default -> throw new IllegalArgumentException("Invalid delegation role");
        }
    }

    @Given("un utente dell'ente {delegationRole} con ruolo {string}")
    public void givenUserWithRole(DelegationRole delegationRole, String iamRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        String token = identityService.getToken(tenantType, iamRole);
        clientTokenConfigurator.setBearerToken(token);
        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setTenantType(tenantType);
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega all'ente delegato")
    public void givenDelegatingTenantHasRequestedDelegation() {
        String delegatingTenantToken = identityService.getToken(sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATING), null);
        clientTokenConfigurator.setBearerToken(delegatingTenantToken);

        String delegator = sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATING);
        String delegate = sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATE);
        createDelegate(delegator, delegate, producerDelegationsApiClient::createProducerDelegation);
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo")
    public void givenDelegatingTenantHasSuccessfullyRequestedDelegation() {
        givenDelegatingTenantHasRequestedDelegation();
        checkDelegation();
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato")
    public void givenConsumerDelegatingTenantHasRequestedDelegation() {
        String delegatorTenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATING);
        String delegateTenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATE);
        givenConsumerDelegatingTenantHasRequestedDelegation(delegatorTenant, delegateTenant);
    }

    @Given("l'ente delegante con ruolo {string} ha inoltrato una richiesta di delega in fruizione all'ente delegato")
    public void givenConsumerDelegatingTenantHasRequestedDelegationWithRole(String delegatorRole) {
        String delegatorTenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATING);
        String delegateTenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATE);
        agreementWithConsumerDelegation(delegatorTenant, delegatorRole, delegateTenant);
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato con successo")
    public void givenConsumerDelegatingTenantHasRequestedDelegationSuccessfully() {
        givenConsumerDelegatingTenantHasRequestedDelegation();
        checkDelegation();
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente {string}")
    public void givenConsumerDelegatingTenantHasRequestedDelegation(String delegateTenant) {
        String delegatorTenant = sharedStepsContext.getDelegationCommonContext()
            .getTenantBy(DELEGATING);
        givenConsumerDelegatingTenantHasRequestedDelegation(delegatorTenant, delegateTenant);
    }

    @Given("l'ente {string} ha inoltrato una richiesta di delega in fruizione all'ente {string}")
    public void givenConsumerDelegatingTenantHasRequestedDelegation(String delegatorTenant, String delegateTenant) {
        agreementWithConsumerDelegation(delegatorTenant, null, delegateTenant);
    }

    private void agreementWithConsumerDelegation(String delegatorTenant, @Nullable String delegatorRole, String delegateTenant) {
        String lastToken = sharedStepsContext.getUserToken();
        String delegatorToken = identityService.getToken(delegatorTenant, delegatorRole);
        sharedStepsContext.setUserToken(delegatorToken);
        clientTokenConfigurator.setBearerToken(delegatorToken);
        authAndConsumerDelegation(delegatorTenant, delegateTenant, DelegationProxy.ofMainDelegation(sharedStepsContext.getDelegationCommonContext()));
        sharedStepsContext.setUserToken(lastToken);
        clientTokenConfigurator.setBearerToken(lastToken);
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente terzo {string}")
    public void givenConsumerDelegatingTenantHasRequestedAuxDelegation(String delegateTenant) {
        String delegatorTenant = sharedStepsContext.getDelegationCommonContext()
            .getTenantBy(DELEGATING);
        givenConsumerDelegatingTenantHasRequestedAuxDelegation(delegatorTenant, delegateTenant);
    }

    @Given("l'ente {string} ha inoltrato una richiesta di delega in fruizione all'ente terzo {string}")
    public void givenConsumerDelegatingTenantHasRequestedAuxDelegation(String delegatorTenant, String delegateTenant) {
        authAndConsumerDelegation(delegatorTenant, delegateTenant, DelegationProxy.ofAuxDelegation(sharedStepsContext.getDelegationCommonContext()));
    }

    private void authAndConsumerDelegation(String delegatorTenant, String delegateTenant, DelegationProxy delegationProxy) {
        String delegatingTenantToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(delegatingTenantToken);
        createDelegate(delegatorTenant, delegateTenant, consumerDelegationsApiClient::createConsumerDelegation, delegationProxy);
    }

    @And("l'utente concede la disponibilità a ricevere le deleghe")
    @And("l'utente concede la disponibilità a ricevere le deleghe in erogazione")
    public void userGrantsProducerDelegationAvailability() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        setDelegationAvailability(sharedStepsContext.getTenantType(), producerStrategyUsing(tenantsApi), true, false);
    }

    @And("l'ente {string} concede la disponibilità a ricevere deleghe")
    @And("l'ente {string} concede la disponibilità a ricevere deleghe in erogazione")
    public void tenantGrantsProducerDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        setDelegationAvailability(tenantType, producerStrategyUsing(tenantsApi), true, false);
    }

    @And("l'ente {string} tenta di concedere la disponibilità a ricevere deleghe in erogazione")
    public void tenantTryGrantsProducerDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        setDelegationAvailability(tenantType, producerStrategyUsing(tenantsApi), false, true, false);
    }

    @And("l'ente {string} concede la disponibilità a ricevere deleghe in fruizione")
    public void tenantGrantsConsumerDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        setDelegationAvailability(tenantType, consumerStrategyUsing(tenantsApi), false, true);
    }

    @And("l'ente {string} tenta di concedere la disponibilità a ricevere deleghe in fruizione")
    public void tenantTryGrantsConsumerDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        setDelegationAvailability(tenantType, consumerStrategyUsing(tenantsApi), false, false, true);
    }

    @And("l'ente {delegationRole} concede la disponibilità a ricevere deleghe in fruizione")
    public void tenantGrantsConsumerDelegationAvailability(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        tenantGrantsConsumerDelegationAvailability(tenantType);
    }

    @Given("l'ente {string} tenta di rimuovere la disponibilità a ricevere deleghe")
    public void tenantTryRemoveConsumerDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        setDelegationAvailability(tenantType, consumerStrategyUsing(tenantsApi), false, false, false);
    }

    private <U> void setDelegationAvailability(
            String tenantType, DelegationAvailabilityStrategy<U> delegationStrategy, boolean pollingActive, Boolean isDelegatedProducer, Boolean isDelegatedConsumer) {
        setDelegationAvailability(
                tenantType,
                delegationStrategy,
                pollingActive,
                isDelegatedProducer,
                isDelegatedConsumer,
                identityService,
                httpCallExecutor,
                tenantsApi,
                pollingService
        );
    }

    private <U> void setDelegationAvailability(
        String tenantType, DelegationAvailabilityStrategy<U> delegationStrategy, Boolean isDelegatedProducer, Boolean isDelegatedConsumer) {
        setDelegationAvailability(
            tenantType,
            delegationStrategy,
            true,
            isDelegatedProducer,
            isDelegatedConsumer,
            identityService,
            httpCallExecutor,
            tenantsApi,
            pollingService
        );
    }

    public static <U> void setDelegationAvailability(
        String tenantType,
        DelegationAvailabilityStrategy<U> delegationStrategy,
        boolean pollingActive,
        Boolean isDelegatedProducer,
        Boolean isDelegatedConsumer,
        IdentityService identityService,
        IHttpExecutor httpExecutor,
        ITenantsApi client,
        PollingService pollingService
    ) {
        httpExecutor.performCall(() -> delegationStrategy.getDelegationAvailabilityDeclarer().accept(isDelegatedProducer, isDelegatedConsumer));
        if (pollingActive && httpExecutor.getResponseStatus() == HttpStatus.OK)
            pollingService.makePolling(() -> client.getTenant(identityService.getOrganizationId(tenantType)),
                res -> Optional.ofNullable(res.getFeatures())
                    .orElse(List.of())
                    .stream()
                    .map(delegationStrategy.getFeatureExtractor())
                    .anyMatch(Objects::nonNull),
                "There was an error while providing the delegation availability!");
    }

    @And("l'ente {string} richiede la creazione di una delega per l'ente {string}")
    public void createDelegate(String delegatorTenantType, String tenantType) {
        createDelegateImpl(delegatorTenantType, tenantType);
    }

    @And("l'ente {string} richiede la creazione di una delega per l'ente {string} con successo")
    @And("l'ente {string} richiede la creazione di una delega in erogazione per l'ente {string} con successo")
    public void createDelegateSuccessfully(String delegatorTenantType, String tenantType) {
        createDelegateImpl(delegatorTenantType, tenantType);
        checkDelegation();
    }

    @And("l'ente {string} ha una delega attiva verso l'ente {string} per l'istanza dell'e-service template")
    @And("l'ente {string} ha una delega in erogazione attiva verso l'ente {string} per l'istanza dell'e-service template")
    public void createAndActivateDelegateForInstanceSuccessfully(String delegatorTenantType, String tenantType) {
        this.isEServiceTemplateInstance = true;
        tenantGrantsProducerDelegationAvailability(tenantType);
        createDelegateSuccessfully(delegatorTenantType, tenantType);

        String lastToken = clientTokenConfigurator.getLastToken();
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        approveProducerDelegation(
                httpCallExecutor,
                producerDelegationsApiClient,
                delegationApiClient,
                sharedStepsContext.getDelegationCommonContext(),
                pollingService
        );
        clientTokenConfigurator.setBearerToken(lastToken);
    }

    private void checkDelegation() {
        if (httpCallExecutor.getResponseStatus().isError()) {
            throw new IllegalStateException("La richiesta di delega non è stata eseguita correttamente: " + httpCallExecutor.getErrorMessage());
        }
    }

    private void createDelegateImpl(String delegatorTenantType, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(delegatorTenantType, null));
        createDelegate(delegatorTenantType, tenantType, producerDelegationsApiClient::createProducerDelegation);
    }

    @And("l'utente richiede la creazione di una delega per l'ente {string}")
    @And("l'utente richiede la creazione di una delega in erogazione per l'ente {string}")
    public void userRequestDelegationCreation(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String delegatorTenant = sharedStepsContext.getTenantType();
        createDelegate(delegatorTenant, tenantType, producerDelegationsApiClient::createProducerDelegation);
    }

    @And("la delega è stata creata correttamente")
    public void delegationIsPresent() {
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> delegationApiClient.getDelegation(
                        sharedStepsContext.getDelegationCommonContext().getDelegationId())),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while creating the delegation!"
        );
    }

    private void createDelegate(
        String delegatorTenantType,
        String delegateTenantType,
        Function<DelegationSeed, CreatedResource> delegationCreator) {
        this.createDelegate(delegatorTenantType, delegateTenantType, delegationCreator, DelegationProxy.ofMainDelegation(sharedStepsContext.getDelegationCommonContext()));
    }

    private void createDelegate(
        String delegatorTenantType,
        String delegateTenantType,
        Function<DelegationSeed, CreatedResource> delegationCreator,
        DelegationProxy delegationProxy) {
        UUID eServiceId = this.isEServiceTemplateInstance
                ? sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate()
                : sharedStepsContext.getEServicesCommonContext().getEserviceId();
        createDelegate(
            delegatorTenantType,
            delegateTenantType,
            delegationCreator,
            delegationProxy,
            identityService,
            httpCallExecutor,
            eServiceId,
            pollingService,
            delegationApiClient
        );
    }

    public static void createDelegate(
        String delegatorTenantType,
        String delegateTenantType,
        Function<DelegationSeed, CreatedResource> delegationCreator,
        DelegationProxy delegationProxy,
        IdentityService identityService,
        IHttpExecutor httpExecutor,
        UUID eServiceId,
        PollingService pollingService,
        IDelegationApiClient client
    ) {
        UUID delegateOrganizationId = identityService.getOrganizationId(delegateTenantType);
        UUID delegatorOrganizationId = identityService.getOrganizationId(delegatorTenantType);

        httpExecutor.performCall(() -> delegationCreator.apply(
            new DelegationSeed().eserviceId(eServiceId).delegateId(delegateOrganizationId)));
        if (httpExecutor.getResponseStatus() == HttpStatus.OK) {
            delegationProxy.setDelegateId(delegateOrganizationId);
            delegationProxy.setDelegatorId(delegatorOrganizationId);
            delegationProxy.setDelegationId(((CreatedResource) httpExecutor.getResponse()).getId());
            delegationProxy.setCreatedAt(OffsetDateTime.now());
            pollingService.makePolling(
                () -> httpExecutor.performCall(() -> client.getDelegation(
                    delegationProxy.getDelegationId())),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while creating the delegation!"
            );
        }
    }

}
