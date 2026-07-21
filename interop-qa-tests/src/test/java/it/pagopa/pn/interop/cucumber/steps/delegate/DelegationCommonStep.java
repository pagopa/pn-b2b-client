package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantFeature;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.CatalogCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.catalog.DescriptorPublicationSteps;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;
import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationCreateStep.DelegationAvailabilityStrategy.producerStrategyUsing;
import static org.apache.commons.lang3.ObjectUtils.allNull;

@Slf4j
public class DelegationCommonStep {
    private final PollingService pollingService;
    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;
    private final ITenantsApi tenantsApi;
    private final BFFDataPreparationService dataPreparationService;
    private final DescriptorPublicationSteps descriptorPublicationSteps;

    public DelegationCommonStep(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext,
                                PollingService pollingService,
                                BFFDataPreparationService dataPreparationService,
                                DescriptorPublicationSteps descriptorPublicationSteps) {
        this.sharedStepsContext = sharedStepsContext;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.tenantsApi = clientTokenConfigurator.getTenantsApi();
        this.pollingService = pollingService;
        this.dataPreparationService = dataPreparationService;
        this.descriptorPublicationSteps = descriptorPublicationSteps;
    }

    @Given("l'ente {string} rimuove la disponibilità a ricevere deleghe")
    public void tenantRemoveDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        try {
            tenantsApi.updateTenantDelegatedFeatures(false, false);
            pollingService.makePolling(
                    () -> tenantsApi.getTenant(identityService.getOrganizationId(tenantType)),
                    res -> res.getFeatures()
                            .stream()
                            .allMatch(feature -> allNull(feature.getDelegatedConsumer(), feature.getDelegatedProducer())),
                    "L'ente non dovrebbe risultare disponibile a ricevere deleghe, ma risulta altrimenti. Visionare logs per maggiori dettagli.");
        } catch (HttpClientErrorException.Conflict e) {
            log.info("No delegation availability defined for the given tenant!");
        } catch (Exception e) {
            log.error("Error while removing delegation availability", e);
        }
    }

    @Given("l'ente {string} rimuove la disponibilità a ricevere deleghe in fruizione")
    public void tenantRemoveConsumerDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID tenantId = this.identityService.getOrganizationId(tenantType);
        try {
            tenantsApi.updateTenantDelegatedFeatures(false, false);
            pollingService.makePolling(
                    () -> tenantsApi.getTenant(tenantId),
                    result -> result.getFeatures().stream()
                            .map(TenantFeature::getDelegatedConsumer)
                            .allMatch(Objects::isNull),
                    "An error occured when trying to remove consumer delegation for tenant %s".formatted(tenantType)
            );
        } catch (HttpClientErrorException.Conflict e) {
            log.info("No delegation availability defined for the given tenant!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Questo step è un condensato di molti degli step del test [TC_CAPOFILA_PUB_1] */
    @Given("{string} ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando {string} come delegato")
    public void createWFAEService(String producer, String delegate) {
        buildWFAEService(producer, delegate, true);
    }

    @Given("{string} porta il descrittore dell'e-service in stato WAITING_FOR_APPROVAL usando {string} come delegato")
    public void bringDescriptorToStateWFA(String producer, String delegate) {
        buildWFAEService(producer, delegate, false);
    }

    private void buildWFAEService(String producer, String delegate, boolean createEService) {
        // Il delegato dà la disponibilità a ricevere deleghe in erogazione
        clientTokenConfigurator.setBearerToken(identityService.getToken(delegate, null));
        DelegationCreateStep.setDelegationAvailability(
            delegate,
            producerStrategyUsing(tenantsApi),
            true,
            true,
            false,
            identityService,
            httpCallExecutor,
            tenantsApi,
            pollingService);

        clientTokenConfigurator.setBearerToken(identityService.getToken(producer, null));

        if (createEService) {
            // Il delegante crea l'e-service e vi associa un'interfaccia
            CatalogCommonSteps.createEServiceWithDescriptor(
                "DRAFT",
                dataPreparationService,
                sharedStepsContext.getEServicesCommonContext(),
                new EServiceSeed(), new UpdateEServiceDescriptorSeed());
        }

        // Il associa un'interfaccia all'e-service
        dataPreparationService.addInterfaceToDescriptor(
            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
            sharedStepsContext.getEServicesCommonContext().getDescriptorId());

        // Il delegante inoltra la richiesta di delega all'ente delegato
        DelegationCreateStep.createDelegate(
            producer,
            delegate,
            clientTokenConfigurator.getProducerDelegationsApiClient()::createProducerDelegation,
            DelegationProxy.ofMainDelegation(sharedStepsContext.getDelegationCommonContext()),
            identityService,
            httpCallExecutor,
            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
            pollingService,
            clientTokenConfigurator.getDelegationApiClient()
        );

        // Il delegato accetta la delega
        clientTokenConfigurator.setBearerToken(identityService.getToken(delegate, null));
        DelegationAcceptStep.approveProducerDelegation(
            httpCallExecutor,
            clientTokenConfigurator.getProducerDelegationsApiClient(),
            clientTokenConfigurator.getDelegationApiClient(),
            sharedStepsContext.getDelegationCommonContext(),
            pollingService
        );

        // Il delegato pubblica l'e-service
        descriptorPublicationSteps.publishDescriptor(
            httpCallExecutor,
            clientTokenConfigurator.getEServiceClient(),
            sharedStepsContext.getEServicesCommonContext());

        // A questo punto l'e-service sarà in stato WAITING_FOR_APPROVAL
        // Si attende attivamente che l'e-service entri in stato WAITING_FOR_APPROVAL
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        pollingService.makePolling(
            () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eserviceId, descriptorId),
            res -> res.getState().equals(EServiceDescriptorState.WAITING_FOR_APPROVAL),
            "Non è avvenuta la transizione di stato in " + EServiceDescriptorState.WAITING_FOR_APPROVAL);
    }

    @Then("si ottiene lo status code {int}")
    public void thenStatusCodeIs(int statusCode) {
        int actualStatusCode = httpCallExecutor.getResponseStatus().value();
        if (isSuccessful(statusCode)) Assertions.assertEquals(200, actualStatusCode);
        else Assertions.assertEquals(statusCode, actualStatusCode);
    }

    @Then("la response ha status code {int}")
    public void checkStatusCoe(int statusCode) {
        int actualStatusCode = httpCallExecutor.getResponseStatus().value();
        Assertions.assertEquals(statusCode, actualStatusCode);
    }

    boolean isSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

}
