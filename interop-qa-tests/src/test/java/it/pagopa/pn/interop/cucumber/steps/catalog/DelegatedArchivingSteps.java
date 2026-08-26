package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class DelegatedArchivingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final CatalogResolver catalogResolver;

    public DelegatedArchivingSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
    }

    @When("l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string} impostando {gracePeriodDays} giorni di preavviso")
    public void submitDelegatedDescriptorArchiving(String descriptorId, String eServiceId, GracePeriodDays gracePeriodDays) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().submitDelegatedDescriptorArchiving(
                        resolvedEServiceId,
                        resolvedDescriptorId,
                        gracePeriodDays
                ),
                ResponseEntity::getStatusCode
        );
    }

    @When("l'utente delegante accetta la richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string}")
    public void approveDelegatedDescriptorArchiving(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().approveDelegatedDescriptorArchiving(
                        resolvedEServiceId,
                        resolvedDescriptorId
                ),
                ResponseEntity::getStatusCode
        );
    }

}