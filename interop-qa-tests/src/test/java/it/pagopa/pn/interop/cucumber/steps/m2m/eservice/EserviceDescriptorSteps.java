package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;


import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EServiceDescriptorPatchRequest;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EServiceDescriptorQuotasPatchRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.assistant.EServiceDescriptorPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.assistant.EServiceDescriptorQuotasPatchOperationsAssistant;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EserviceDescriptorSteps extends AbstractCommonSteps<EServiceDescriptor, Pair<UUID, UUID>> {
    private final IM2MEserviceDescriptorClient client;
    private final SharedStepsContext sharedStepsContext;
    private final CatalogResolver catalogResolver;

    private final EServiceDescriptorPatchOperationsAssistant eServiceDescriptorPatchAssistant;
    private final EServiceDescriptorQuotasPatchOperationsAssistant quotasPatchAssistant;

    public EserviceDescriptorSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator,
        EServiceDescriptorPatchOperationsAssistant eServiceDescriptorPatchAssistant,
        EServiceDescriptorQuotasPatchOperationsAssistant eServiceDescriptorQuotasPatchAssistant
    ) {
        super("descriptor", clientTokenConfigurator.getM2mEServiceDescriptorClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mEServiceDescriptorClient();
        this.sharedStepsContext = sharedStepsContext;
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.eServiceDescriptorPatchAssistant = eServiceDescriptorPatchAssistant;
        this.quotasPatchAssistant = eServiceDescriptorQuotasPatchAssistant;
    }


    @When("l'utente tenta di recuperare la lista di descriptor usando l'eserviceId creato")
    @Override
    public void getAll() {
        var eserviceContext = this.sharedStepsContext.getEServicesCommonContext();
        List<it.pagopa.interop.agreement.domain.EServiceDescriptor> eservices = eserviceContext.getPublishedEservicesIds();

        Assertions.assertThat(eservices)
                .as("Verifica che ci sia un solo e-service pubblicato")
                .hasSize(1);

        UUID eserviceId = eservices.get(0).getEServiceId();
        retrieveDescriptors(eserviceId);
    }

    @When("l'utente tenta di recuperare la lista di descriptor con un eserviceId {entityIdType}")
    public void getAll(EntityIdType entityIdType) {
        UUID eserviceId = this.generateId(entityIdType).getLeft();
        retrieveDescriptors(eserviceId);
    }

    @When("l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service")
    public void patchEServiceDescription() {
        eServiceDescriptorPatchAssistant.patchResource();
    }

    @When("{string} con ruolo {m2mRole} tenta di effettuare la modifica parziale del descriptor dell'e-service")
    public void patchEServiceDescriptorNotOwned(String tenant, M2MRole m2mRole) {
        eServiceDescriptorPatchAssistant.patchResource(tenant, m2mRole);
    }

    @When("l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service con token non valido")
    public void patchEServiceDescriptorWithNotValidToken() {
        eServiceDescriptorPatchAssistant.patchResourceWithInvalidToken();
    }

    @When("l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service specificando un sottoinsieme di informazioni")
    public void patchEServiceDescriptorSubset() {
        EServiceDescriptorPatchRequest request = EServiceDescriptorPatchRequest.builder()
                .voucherLifespan(200)
                .build();

        eServiceDescriptorPatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale del descriptor di un e-service inesistente")
    public void patchNonExistentEServiceDescriptor() {
        eServiceDescriptorPatchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service senza apportare cambiamenti")
    public void patchEServiceWithSameDescriptor() {
        eServiceDescriptorPatchAssistant.patchResourceWithSameInfo();
    }

    @Then("l'e-service descriptor è stato parzialmente modificato correttamente")
    public void verificaPatchedEService() {
        eServiceDescriptorPatchAssistant.checkPatchedResource();
    }

    @Then("l'e-service descriptor non ha subito modifiche")
    public void verificaUnpatchedEService() {
        eServiceDescriptorPatchAssistant.checkUnpatchedResource();
    }

    @Then("l'e-service descriptor restituito è coerente con le modifiche effettuate")
    public void verificaRisultatoPatch() {
        eServiceDescriptorPatchAssistant.checkPatchOperationResult();
    }

    @When("l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service")
    public void patchEServiceDescriptorQuotas() {
        quotasPatchAssistant.patchResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service con token non valido")
    public void patchEServiceDescriptorQuotasWithNotValidToken() {
        quotasPatchAssistant.patchResourceWithInvalidToken();
    }

    @When("l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service specificando un sottoinsieme di informazioni")
    public void patchEServiceDescriptorQuotasSubset() {
        EServiceDescriptorQuotasPatchRequest request = EServiceDescriptorQuotasPatchRequest.builder()
            .dailyCallsPerConsumer(7)
            .build();

        quotasPatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale delle quote di un descriptor di un e-service inesistente")
    public void patchNonExistentEServiceDescriptorQuotas() {
        quotasPatchAssistant.patchNonExistentResource();
    }

    @When("{string} con ruolo {m2mRole} tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service")
    public void patchEServiceDescriptorQuotasNotOwned(String tenant, M2MRole m2mRole) {
        quotasPatchAssistant.patchResource(tenant, m2mRole);
    }

    @When("l'utente avvia l'archiviazione della vecchia versione {string} dell'e-service {string} prevedendo {gracePeriodDays} giorni di preavviso")
    public void scheduleArchiveOldDescriptor(String descriptorId, String eServiceId, GracePeriodDays gracePeriodDays) {
        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        registerDescriptorArchivingRequestTimestamp(gracePeriodDays);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> client.scheduleArchiveEServiceDescriptor(resolvedEServiceId, resolvedDescriptorId, gracePeriodDays.getValue()));
    }

    @When("l'utente avvia l'archiviazione della versione più recente dell'e-service prevedendo {gracePeriodDays} giorni di preavviso")
    public void archiveLatestDescriptor(GracePeriodDays gracePeriodDays) {
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();

        registerDescriptorArchivingRequestTimestamp(gracePeriodDays);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> client.scheduleArchiveEServiceDescriptor(eServiceId, descriptorId, gracePeriodDays.getValue()));
    }

    @When("l'utente tenta di annullare il processo di archiviazione della vecchia versione con id {string} dell'e-service con id {string}")
    public void cancelOldDescriptorArchiving(String descriptorId, String eServiceId) {
        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        sharedStepsContext.getHttpCallExecutor().performCall(
            () -> client.cancelEServiceDescriptorArchiving(resolvedEServiceId, resolvedDescriptorId));
    }

    @Override
    public void bindActual(SharedStepsContext context, List<EServiceDescriptor> actualEntities) {
        var eserviceContext = this.sharedStepsContext.getEServicesCommonContext();
        eserviceContext.setRetrievedEservicesIds(new ArrayList<>(actualEntities));
    }

    @Override
    public List<EServiceDescriptor> bindExpected(SharedStepsContext context) {
        return new ArrayList<>(context.getEServicesCommonContext().getPublishedEservicesIds());
    }

    @Override
    protected boolean isEqual(EServiceDescriptor a, EServiceDescriptor b) {
        return a.getDescriptorId().equals(b.getDescriptorId());
    }

    private void retrieveDescriptors(UUID eserviceId) {
        List<EServiceDescriptor> descriptors = client.getAll(eserviceId);
        List<EServiceDescriptor> actualDescriptors = descriptors != null ? descriptors : List.of();

        this.actualEntities.clear();
        this.actualEntities.addAll(actualDescriptors);
        this.bindActual(sharedStepsContext, actualDescriptors);
    }

    private void registerDescriptorArchivingRequestTimestamp(GracePeriodDays gracePeriodDays) {
        sharedStepsContext.getEServicesCommonContext()
                .setDescriptorArchivingRequestTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        sharedStepsContext.getEServicesCommonContext()
                .setDescriptorArchivingGracePeriodDays(gracePeriodDays);
    }
}
