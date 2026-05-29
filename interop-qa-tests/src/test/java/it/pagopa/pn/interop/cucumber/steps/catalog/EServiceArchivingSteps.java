package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingReasonSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class EServiceArchivingSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final CatalogResolver catalogResolver;

    public EServiceArchivingSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
    }

    @When("l'utente avvia il processo di archiviazione dell'e-service con id {string} e specificando la motivazione {string}")
    public void scheduleEServiceArchiving(String eServiceId, String archivingReason) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedArchivingReason = catalogResolver.resolveArchivingReason(archivingReason);

        scheduleArchiveEService(resolvedEServiceId, resolvedArchivingReason);
    }

    @When("l'utente avvia il processo di archiviazione dell'e-service con id {string} e specificando la motivazione composta da {int} caratteri")
    public void scheduleEServiceArchivingWithReasonLength(String eServiceId, int archivingReasonLength) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String archivingReason = RandomStringUtils.insecure().nextAlphanumeric(archivingReasonLength);

        scheduleArchiveEService(resolvedEServiceId, archivingReason);
    }

    private void scheduleArchiveEService(UUID eServiceId, String archivingReason) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .scheduleArchiveEService(
                                eServiceId,
                                new EServiceArchivingReasonSeed().archivingReason(archivingReason)
                        ),
                ResponseEntity::getStatusCode
        );
    }
}
