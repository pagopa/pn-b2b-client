package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.apache.commons.lang3.RandomStringUtils;

import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;

import java.time.OffsetDateTime;
import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.nullableBoolean;

public class EServiceUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final CatalogResolver catalogResolver;

    public EServiceUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
    }

    @When("l'utente aggiorna quell'e-service")
    public void userUpdateEService() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        userUpdateEServiceImpl();
    }

    @When("{string} aggiorna quell'e-service")
    public void userUpdateEService(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        userUpdateEServiceImpl();
    }

    @When("{string} aggiorna quell'e-service con:")
    public void userUpdateEService(String tenantType, UpdateEServiceSeed updateEServiceSeed) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        userUpdateEServiceImpl(updateEServiceSeed);
    }

    //60 è il limite di caratteri da rispettare per il nome di un e-service
    @And("l'utente aggiorna il nome dell'e-service con un valore di lunghezza 60 caratteri")
    public void eServiceNameUpdate() {
        final String prefix = "e-service-";
        final int totalLength = 60;
        String nameToUpdate = prefix + RandomStringUtils.insecure().nextNumeric(totalLength - prefix.length());

        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateEServiceName(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        new EServiceNameUpdateSeed().name(nameToUpdate)
                )
        );

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getPollingService().makePolling(
                    () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDetails(
                            sharedStepsContext.getEServicesCommonContext().getEserviceId()
                    ),
                    res -> res != null && nameToUpdate.equals(res.getName()),
                    "Il nome dell'e-service non è stato aggiornato correttamente"
            );

            sharedStepsContext.getEServicesCommonContext().setOldName(
                    sharedStepsContext.getEServicesCommonContext().getName()
            );
            sharedStepsContext.getEServicesCommonContext().setName(nameToUpdate);
            sharedStepsContext.getEServicesCommonContext().setEServiceEditTimestamp(OffsetDateTime.now());
        }
    }

    @When("l'utente imposta la delega amministrativa come {string} e la delega tecnica come {string} per la fruizione dell'e-service {string}")
    public void updateEServiceDelegationAvailability(String consumerDelegationAction, String clientAccessDelegationAction, String eServiceId) {

        Boolean isConsumerDelegable = nullableBoolean(consumerDelegationAction);
        Boolean isClientAccessDelegable = nullableBoolean(clientAccessDelegationAction);

        UUID eServiceUuid = catalogResolver.resolveEServiceId(eServiceId);

        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateEServiceDelegationFlags(
                        eServiceUuid,
                        new EServiceDelegationFlagsUpdateSeed()
                                .isConsumerDelegable(isConsumerDelegable)
                                .isClientAccessDelegable(isClientAccessDelegable)
                )
        );

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            httpCallExecutor.snapshot();
            sharedStepsContext.getPollingService().makePolling(
                    () -> httpCallExecutor.performCall(
                            () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDetails(
                                    sharedStepsContext.getEServicesCommonContext().getEserviceId()
                            )
                    ),
                    res -> {
                        ProducerEServiceDetails eServiceDetails = (ProducerEServiceDetails) httpCallExecutor.getResponse();
                        return eServiceDetails.getIsConsumerDelegable() != null && eServiceDetails.getIsConsumerDelegable().equals(isConsumerDelegable) &&
                                eServiceDetails.getIsClientAccessDelegable() != null && eServiceDetails.getIsClientAccessDelegable().equals(isClientAccessDelegable);
                    },
                    "Impossibile aggiornare i flag di delega dell'e-service"
            );
            httpCallExecutor.resetFormSnapshot();

            sharedStepsContext.getEServicesCommonContext().setIsConsumerDelegable(isConsumerDelegable);
            sharedStepsContext.getEServicesCommonContext().setIsClientAccessDelegable(isClientAccessDelegable);
        }
    }

    @And("le flag di delega dell'e-service non hanno subito modifiche")
    public void verifyEServiceDelegationFlagsUnchanged() {
        Boolean isConsumerDelegable = sharedStepsContext.getEServicesCommonContext().getIsConsumerDelegable();
        Boolean isClientAccessDelegable = sharedStepsContext.getEServicesCommonContext().getIsClientAccessDelegable();

        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        httpCallExecutor.snapshot();
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDetails(
                                sharedStepsContext.getEServicesCommonContext().getEserviceId()
                        )
                ),
                res -> {
                    ProducerEServiceDetails eServiceDetails = (ProducerEServiceDetails) httpCallExecutor.getResponse();
                    return eServiceDetails.getIsConsumerDelegable() != null && eServiceDetails.getIsConsumerDelegable().equals(isConsumerDelegable) &&
                            eServiceDetails.getIsClientAccessDelegable() != null && eServiceDetails.getIsClientAccessDelegable().equals(isClientAccessDelegable);
                },
                "Impossibile aggiornare i flag di delega dell'e-service"
        );

        httpCallExecutor.resetFormSnapshot();
    }

    private void userUpdateEServiceImpl() {
        this.userUpdateEServiceImpl(new UpdateEServiceSeed()
                .name(String.format("e-service - %d", sharedStepsContext.getTestSeed()))
                .description("Nuova descrizione")
                .mode(EServiceMode.DELIVER)
                .technology(EServiceTechnology.SOAP)
        );
    }

    private void userUpdateEServiceImpl(UpdateEServiceSeed updateEServiceSeed) {
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateEServiceById(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        updateEServiceSeed
                )
        );
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext().setEServiceEditTimestamp(OffsetDateTime.now());
        }
    }
}
