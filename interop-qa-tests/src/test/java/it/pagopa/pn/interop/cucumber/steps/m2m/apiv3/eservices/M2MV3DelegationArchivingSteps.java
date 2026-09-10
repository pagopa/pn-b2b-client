package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.eservices;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.eservice.service.IM2MV3EserviceClient;
import it.pagopa.interop.eservice.service.IM2MV3EserviceClient.DelegatedEServiceArchivingRequest;
import it.pagopa.interop.eservice.service.IM2MV3EserviceDescriptorClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;

public class M2MV3DelegationArchivingSteps {
    private final IHttpExecutor httpExecutor;
    private final IM2MV3EserviceClient eServiceClient;
    private final IM2MV3EserviceDescriptorClient descriptorClient;
    private final CatalogResolver catalogResolver;

    public M2MV3DelegationArchivingSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServiceClient = clientTokenConfigurator.getM2mV3EserviceClient();
        this.descriptorClient = clientTokenConfigurator.getM2mV3EserviceDescriptorClient();
        this.eServiceClient.setHttpCallExecutor(httpExecutor);
        this.descriptorClient.setHttpCallExecutor(httpExecutor);
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
    }

    @When("l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione dell'e-service {string} specificando la motivazione {string} e {gracePeriodDays} giorni di preavviso")
    @When("l'utente invia via M2M v3 al delegante una richiesta di archiviazione dell'e-service {string} specificando la motivazione {string} e {gracePeriodDays} giorni di preavviso")
    public void submitDelegatedEServiceArchiving(
        String eServiceId,
        String archivingReason,
        GracePeriodDays gracePeriodDays
    ) {
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedArchivingReason = catalogResolver.resolveArchivingReason(archivingReason);

        DelegatedEServiceArchivingRequest request = DelegatedEServiceArchivingRequest.builder()
            .archivingReason(resolvedArchivingReason)
            .gracePeriodDays(gracePeriodDays == null ? null : gracePeriodDays.getValue())
            .build();

        httpExecutor.performCall(
            () -> eServiceClient.submitDelegatedEServiceArchiving(resolvedEServiceId, request));
    }

    @When("l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione dell'e-service {string} specificando una motivazione di {int} caratteri e {gracePeriodDays} giorni di preavviso")
    public void submitDelegatedEServiceArchivingWithReasonLength(
        String eServiceId,
        int archivingReasonLength,
        GracePeriodDays gracePeriodDays
    ) {
        String archivingReason = RandomStringUtils.insecure().nextAlphanumeric(archivingReasonLength);
        submitDelegatedEServiceArchiving(eServiceId, archivingReason, gracePeriodDays);
    }

    @When("l'utente delegato annulla via M2M v3 la richiesta di archiviazione dell'e-service {string}")
    public void cancelDelegatedEServiceArchiving(String eServiceId) {
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpExecutor.performCall(
            () -> eServiceClient.cancelDelegatedEServiceArchiving(resolvedEServiceId));
    }

    @When("l'utente delegante accetta via M2M v3 la richiesta di archiviazione relativa all'e-service {string}")
    @When("l'utente accetta via M2M v3 la richiesta di archiviazione relativa all'e-service {string}")
    public void approveDelegatedEServiceArchiving(String eServiceId) {
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpExecutor.performCall(
            () -> eServiceClient.approveDelegatedEServiceArchiving(resolvedEServiceId));
    }

    @When("l'utente delegante rifiuta via M2M v3 la richiesta di archiviazione delegata dell'e-service {string} con motivazione {string}")
    @When("l'utente rifiuta via M2M v3 la richiesta di archiviazione delegata dell'e-service {string} con motivazione {string}")
    public void rejectDelegatedEServiceArchiving(String eServiceId, String rejectionReason) {
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedRejectionReason = catalogResolver.resolveArchivingReason(rejectionReason);

        httpExecutor.performCall(
            () -> eServiceClient.rejectDelegatedEServiceArchiving(
                resolvedEServiceId,
                resolvedRejectionReason
            ));
    }

    @When("l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string} impostando {gracePeriodDays} giorni di preavviso")
    @When("l'utente invia via M2M v3 al delegante una richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string} impostando {gracePeriodDays} giorni di preavviso")
    public void submitDelegatedDescriptorArchiving(
        String descriptorId,
        String eServiceId,
        GracePeriodDays gracePeriodDays
    ) {
        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpExecutor.performCall(
            () -> descriptorClient.submitDelegatedDescriptorArchiving(
                resolvedEServiceId,
                resolvedDescriptorId,
                gracePeriodDays == null ? null : gracePeriodDays.getValue()
            ));
    }

    @When("l'utente delegato annulla via M2M v3 la richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string}")
    public void cancelDelegatedDescriptorArchiving(String descriptorId, String eServiceId) {
        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpExecutor.performCall(
            () -> descriptorClient.cancelDelegatedDescriptorArchiving(
                resolvedEServiceId,
                resolvedDescriptorId
            ));
    }

    @When("l'utente delegante accetta via M2M v3 la richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string}")
    @When("l'utente accetta via M2M v3 la richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string}")
    public void approveDelegatedDescriptorArchiving(String descriptorId, String eServiceId) {
        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpExecutor.performCall(
            () -> descriptorClient.approveDelegatedDescriptorArchiving(
                resolvedEServiceId,
                resolvedDescriptorId
            ));
    }

    @When("l'utente delegante rifiuta via M2M v3 la richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string} con motivazione {string}")
    @When("l'utente rifiuta via M2M v3 la richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string} con motivazione {string}")
    public void rejectDelegatedDescriptorArchiving(
        String descriptorId,
        String eServiceId,
        String rejectionReason
    ) {
        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedRejectionReason = catalogResolver.resolveArchivingReason(rejectionReason);

        httpExecutor.performCall(
            () -> descriptorClient.rejectDelegatedDescriptorArchiving(
                resolvedEServiceId,
                resolvedDescriptorId,
                resolvedRejectionReason
            ));
    }
}