package it.pagopa.pn.interop.cucumber.steps.delegate;

import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationCreateStep.DelegationAvailabilityStrategy.consumerStrategyUsing;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationCreateStep.DelegationAvailabilityStrategy.producerStrategyUsing;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole.DELEGATE;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole.DELEGATING;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.delegate.service.IConsumerDelegationsApiClient;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegatedConsumer;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegatedProducer;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantFeature;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

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
    private final HttpCallExecutor httpCallExecutor;

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DelegationAvailabilityStrategy<T, U> {
        Consumer<T> delegationAvailabilityDeclarer;
        Function<TenantFeature, U> featureExtractor;

        public static DelegationAvailabilityStrategy<String, DelegatedProducer> producerStrategyUsing(ITenantsApi apiSet) {
            return new DelegationAvailabilityStrategy<>(in -> apiSet.assignTenantDelegatedProducerFeature(), TenantFeature::getDelegatedProducer);
        }

        public static DelegationAvailabilityStrategy<String, DelegatedConsumer> consumerStrategyUsing(ITenantsApi apiSet) {
            return new DelegationAvailabilityStrategy<>(apiSet::assignTenantDelegatedConsumerFeature, TenantFeature::getDelegatedConsumer);
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
        switch (delegationRole) {
            case DELEGATE -> sharedStepsContext.getDelegationCommonContext().setDelegateTenant(tenant);
            case DELEGATING -> sharedStepsContext.getDelegationCommonContext().setDelegatorTenant(tenant);
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
        createDelegate(sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATE), producerDelegationsApiClient::createProducerDelegation);
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato")
    public void givenConsumerDelegatingTenantHasRequestedDelegation() {
        String delegatorTenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATING);
        String delegateTenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATE);
        givenConsumerDelegatingTenantHasRequestedDelegation(delegatorTenant, delegateTenant);
    }

    @Given("l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente {string}")
    public void givenConsumerDelegatingTenantHasRequestedDelegation(String delegateTenant) {
        String delegatorTenant = sharedStepsContext.getDelegationCommonContext()
            .getTenantBy(DELEGATING);
        givenConsumerDelegatingTenantHasRequestedDelegation(delegatorTenant, delegateTenant);
    }

    @Given("l'ente {string} ha inoltrato una richiesta di delega in fruizione all'ente {string}")
    public void givenConsumerDelegatingTenantHasRequestedDelegation(String delegatorTenant, String delegateTenant) {
        authAndConsumerDelegation(delegatorTenant, delegateTenant, DelegationProxy.ofMainDelegation(sharedStepsContext.getDelegationCommonContext()));
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
        createDelegate(delegateTenant, consumerDelegationsApiClient::createConsumerDelegation, delegationProxy);
    }

    @And("l'utente concede la disponibilità a ricevere le deleghe")
    public void userGrantsProducerDelegationAvailability() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        setDelegationAvailability(sharedStepsContext.getTenantType(), producerStrategyUsing(tenantsApi), null);
    }

    @And("l'ente {string} concede la disponibilità a ricevere deleghe")
    public void tenantGrantsProducerDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        setDelegationAvailability(tenantType, producerStrategyUsing(tenantsApi), null);
    }

    @And("l'ente {string} concede la disponibilità a ricevere deleghe in fruizione")
    public void tenantGrantsConsumerDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        setDelegationAvailability(tenantType, consumerStrategyUsing(tenantsApi), sharedStepsContext.getXCorrelationId());
    }

    @And("l'ente {delegationRole} concede la disponibilità a ricevere deleghe in fruizione")
    public void tenantGrantsConsumerDelegationAvailability(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        tenantGrantsConsumerDelegationAvailability(tenantType);
    }

    private <T, U> void setDelegationAvailability(
        String tenantType, DelegationAvailabilityStrategy<T, U> delegationStrategy, T delegationApiInput) {
        httpCallExecutor.performCall(() -> delegationStrategy.getDelegationAvailabilityDeclarer().accept(delegationApiInput));
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK)
            pollingService.makePolling(() -> tenantsApi.getTenant(sharedStepsContext.getXCorrelationId(), identityService.getOrganizationId(tenantType)),
                res -> Optional.ofNullable(res.getFeatures())
                        .orElse(List.of())
                        .stream()
                        .map(delegationStrategy.getFeatureExtractor())
                        .anyMatch(Objects::nonNull),
                "There was an error while providing the delegation availability!");
    }

    @And("l'ente {string} richiede la creazione di una delega per l'ente {string}")
    public void createDelegate(String delegatorTenantType, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(delegatorTenantType, null));
        createDelegate(tenantType, producerDelegationsApiClient::createProducerDelegation);
    }

    @And("l'utente richiede la creazione di una delega per l'ente {string}")
    public void userRequestDelegationCreation(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        createDelegate(tenantType,  producerDelegationsApiClient::createProducerDelegation);
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

    private void createDelegate(
        String tenantType,
        BiFunction<String, DelegationSeed, CreatedResource> delegationCreator) {
        this.createDelegate(tenantType, delegationCreator, DelegationProxy.ofMainDelegation(sharedStepsContext.getDelegationCommonContext()));
    }

    private void createDelegate(
        String tenantType,
        BiFunction<String, DelegationSeed, CreatedResource> delegationCreator,
        DelegationProxy delegationProxy) {
        UUID organizationId = identityService.getOrganizationId(tenantType);
        httpCallExecutor.performCall(() -> delegationCreator.apply(sharedStepsContext.getXCorrelationId(),
                new DelegationSeed().eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId()).delegateId(organizationId)));
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK) {
            delegationProxy.setDelegationId(((CreatedResource) httpCallExecutor.getResponse()).getId());
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(),
                            String.valueOf(delegationProxy.getDelegationId()))),
                    res -> res != HttpStatus.NOT_FOUND,
                    "There was an error while creating the delegation!"
            );
        }
    }

}
