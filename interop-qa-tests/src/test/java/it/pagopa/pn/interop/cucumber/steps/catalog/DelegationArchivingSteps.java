package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.DelegatedArchivingRequestVerifier;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class DelegationArchivingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final CatalogResolver catalogResolver;
    private final DelegatedArchivingRequestVerifier delegatedArchivingRequestVerifier;

    public DelegationArchivingSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
        this.delegatedArchivingRequestVerifier = new DelegatedArchivingRequestVerifier(
                clientTokenConfigurator,
                sharedStepsContext
        );
    }

    @Given("l'utente ha già inviato la richiesta di archiviazione per l'e-service {string} specificando la motivazione {string} e {gracePeriodDays} giorni di preavviso")
    public void delegatedEServiceArchivingRequestAlreadySubmitted(
            String eServiceId,
            String archivingReason,
            GracePeriodDays gracePeriodDays
    ) {
        submitDelegatedEServiceArchiving(eServiceId, archivingReason, gracePeriodDays);
        assertDelegatedArchivingRequestSubmitted();
        eServiceDelegatedArchivingRequestIsPending();
    }

        @Given("l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore {string} dell'e-service {string} specificando {gracePeriodDays} giorni di preavviso")
        public void delegatedOldDescriptorArchivingRequestAlreadySubmitted(
                        String descriptorId,
                        String eServiceId,
                        GracePeriodDays gracePeriodDays
        ) {
                submitDelegatedDescriptorArchiving(descriptorId, eServiceId, gracePeriodDays);
                assertDelegatedArchivingRequestSubmitted();
                oldDescriptorDelegatedArchivingRequestIsPending();
        }

        @Given("l'utente ha già rifiutato la richiesta di archiviazione per l'e-service {string} con motivazione {string}")
        public void delegatedEServiceArchivingRequestAlreadyRejected(String eServiceId, String rejectionReason) {
                rejectDelegatedEServiceArchiving(eServiceId, rejectionReason);
                assertDelegatedArchivingRequestRejected();
                eServiceDelegatedArchivingRequestIsRejected();
        }

        @Given("l'utente ha già rifiutato la richiesta di archiviazione per il vecchio descrittore {string} dell'e-service {string} con motivazione {string}")
        public void delegatedOldDescriptorArchivingRequestAlreadyRejected(
                        String descriptorId,
                        String eServiceId,
                        String rejectionReason
        ) {
                rejectDelegatedDescriptorArchiving(descriptorId, eServiceId, rejectionReason);
                assertDelegatedArchivingRequestRejected();
                oldDescriptorDelegatedArchivingRequestIsRejected();
        }

    @When("l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string} impostando {gracePeriodDays} giorni di preavviso")
    @When("l'utente invia al delegante una richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string} impostando {gracePeriodDays} giorni di preavviso")
    public void submitDelegatedDescriptorArchiving(String descriptorId, String eServiceId, GracePeriodDays gracePeriodDays) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        delegatedArchivingRequestVerifier.registerDescriptorArchivingRequest(
                resolvedDescriptorId,
                gracePeriodDays
        );

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().submitDelegatedDescriptorArchiving(
                        resolvedEServiceId,
                        resolvedDescriptorId,
                        gracePeriodDays
                ),
                ResponseEntity::getStatusCode
        );
    }

    @When("l'utente delegato annulla la richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string}")
    public void cancelDelegatedDescriptorArchivingRequest(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().cancelDelegatedDescriptorArchivingRequest(
                        resolvedEServiceId,
                        resolvedDescriptorId
                ),
                ResponseEntity::getStatusCode
        );
    }

    @When("l'utente delegante accetta la richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string}")
    @When("l'utente accetta la richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string}")
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

    @When("l'utente delegante rifiuta la richiesta di archiviazione della vecchia versione identificata da {string} per l'e-service {string} con motivazione {string}")
    public void rejectDelegatedDescriptorArchiving(String descriptorId, String eServiceId, String rejectionReason) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedRejectionReason = catalogResolver.resolveArchivingReason(rejectionReason);

        delegatedArchivingRequestVerifier.registerArchivingRequestRejection(resolvedRejectionReason);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().rejectDelegatedDescriptorArchiving(
                        resolvedEServiceId,
                        resolvedDescriptorId,
                        resolvedRejectionReason
                ),
                ResponseEntity::getStatusCode
        );
    }

    @When("l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service {string} specificando la motivazione {string} e {gracePeriodDays} giorni di preavviso")
    @When("l'utente invia al delegante una richiesta di archiviazione dell'e-service {string} specificando la motivazione {string} e {gracePeriodDays} giorni di preavviso")
    public void submitDelegatedEServiceArchiving(String eServiceId, String archivingReason, GracePeriodDays gracePeriodDays) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedArchivingReason = catalogResolver.resolveArchivingReason(archivingReason);

        delegatedArchivingRequestVerifier.registerEServiceArchivingRequest(
                gracePeriodDays,
                resolvedArchivingReason
        );

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().submitDelegatedEServiceArchiving(
                        resolvedEServiceId,
                        new EServiceArchivingSeed()
                                .archivingReason(resolvedArchivingReason)
                                .gracePeriodDays(gracePeriodDays)
                ),
                ResponseEntity::getStatusCode
        );
    }

        @When("l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service {string} specificando una motivazione di {int} caratteri e {gracePeriodDays} giorni di preavviso")
        public void submitDelegatedEServiceArchivingWithReasonLength(
                        String eServiceId,
                        int archivingReasonLength,
                        GracePeriodDays gracePeriodDays
        ) {
                String archivingReason = RandomStringUtils.insecure().nextAlphanumeric(archivingReasonLength);
                submitDelegatedEServiceArchiving(eServiceId, archivingReason, gracePeriodDays);
        }

    @Then("la richiesta di archiviazione delegata dell'e-service è in stato pending")
    public void eServiceDelegatedArchivingRequestIsPending() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        delegatedArchivingRequestVerifier.pollPendingEServiceArchivingRequest(eServiceId);
    }

        @Then("la richiesta di archiviazione delegata del vecchio descrittore è in stato pending")
        public void oldDescriptorDelegatedArchivingRequestIsPending() {
                clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

                UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
                delegatedArchivingRequestVerifier.pollPendingDescriptorArchivingRequest(eServiceId);
        }

        @Then("la richiesta di archiviazione delegata dell'e-service è stata rifiutata con successo")
        public void eServiceDelegatedArchivingRequestIsRejected() {
                clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

                UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
                delegatedArchivingRequestVerifier.pollRejectedEServiceArchivingRequest(eServiceId);
        }

        @Then("la richiesta di archiviazione delegata del vecchio descrittore è stata rifiutata con successo")
        public void oldDescriptorDelegatedArchivingRequestIsRejected() {
                clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

                UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
                delegatedArchivingRequestVerifier.pollRejectedDescriptorArchivingRequest(eServiceId);
        }

        @Then("la richiesta di archiviazione pendente dell'e-service è stata annullata con successo")
        public void pendingEServiceArchivingRequestIsCancelled() {
                clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

                UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
                delegatedArchivingRequestVerifier.pollWithoutPendingEServiceArchivingRequest(eServiceId);
        }

        @Then("la richiesta di archiviazione pendente del vecchio descrittore è stata annullata con successo")
        public void pendingOldDescriptorArchivingRequestIsCancelled() {
                clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

                UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
                delegatedArchivingRequestVerifier.pollWithoutPendingDescriptorArchivingRequest(eServiceId);
        }

        private void assertDelegatedArchivingRequestSubmitted() {
                if (httpCallExecutor.getResponseStatus() == null || !httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                        throw new IllegalStateException("L'invio della richiesta di archiviazione delegata non ha avuto successo");
                }
        }

        private void assertDelegatedArchivingRequestRejected() {
                if (httpCallExecutor.getResponseStatus() == null || !httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                        throw new IllegalStateException("Il rifiuto della richiesta di archiviazione delegata non ha avuto successo");
                }
        }

    @When("l'utente delegato annulla la richiesta di archiviazione dell'e-service {string}")
    public void cancelDelegatedEServiceArchivingRequest(String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .cancelDelegatedEServiceArchivingRequest(resolvedEServiceId),
                ResponseEntity::getStatusCode
        );
    }

    @When("l'utente delegante accetta la richiesta di archiviazione relativa all'e-service {string}")
    @When("l'utente accetta la richiesta di archiviazione relativa all'e-service {string}")
    public void approveDelegatedEServiceArchiving(String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().approveDelegatedEServiceArchiving(resolvedEServiceId),
                ResponseEntity::getStatusCode
        );
    }

    @When("l'utente delegante rifiuta la richiesta di archiviazione delegata dell'e-service {string} con motivazione {string}")
    @When("l'utente rifiuta la richiesta di archiviazione delegata dell'e-service {string} con motivazione {string}")
    public void rejectDelegatedEServiceArchiving(String eServiceId, String rejectionReason) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedRejectionReason = catalogResolver.resolveArchivingReason(rejectionReason);

        delegatedArchivingRequestVerifier.registerArchivingRequestRejection(resolvedRejectionReason);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().rejectDelegatedEServiceArchiving(
                        resolvedEServiceId,
                        resolvedRejectionReason
                ),
                ResponseEntity::getStatusCode
        );
    }
}
