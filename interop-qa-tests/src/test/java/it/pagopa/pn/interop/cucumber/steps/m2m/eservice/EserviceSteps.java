package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.eservice.service.enums.EServiceCheckMode;
import it.pagopa.interop.eservice.service.enums.EserviceEntityType;
import it.pagopa.interop.eservice.service.enums.EserviceRequestType;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class EserviceSteps {
    private final SharedStepsContext sharedStepsContext;
    private final M2MDataPreparationService dataPreparationService;

    public EserviceSteps(SharedStepsContext sharedStepsContext,
                         M2MDataPreparationService dataPreparationService) {
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente tenta di recuperare {entityType} con un id {descriptorRequestType}")
    public void retrieveEntityById(EserviceEntityType entity, EserviceRequestType requestType) {
        retrieveEntity(requestType, entity);
    }

    @When("l'utente tenta di recuperare {entityType}")
    public void retrieveEntityList(EserviceEntityType entity) {
        retrieveEntity(EserviceRequestType.VALID, entity);
    }

    @Then("{entityType} {word} restituito")
    public void verifyEntityList(EserviceEntityType type, String presence) {
        EServiceCheckMode mode = switch (presence.toLowerCase()) {
            case "viene" -> EServiceCheckMode.PRESENT;
            case "non" -> EServiceCheckMode.NONE;
            default -> throw new IllegalArgumentException("Unsupported presence: " + presence);
        };
        assertExpectedEntity(type, mode);
    }

    @Then("{entityType} è presente solo se lo status code è {int}")
    public void verifyEntityListConditionally(EserviceEntityType type, int expectedStatusCode) {
        int actualStatusCode = sharedStepsContext.getHttpCallExecutor().getClientResponse().value();

        if (actualStatusCode == expectedStatusCode) {
            assertThat(sharedStepsContext.getHttpCallExecutor().getResponse())
                    .as("Il body della response dovrebbe essere valorizzato per status code %s".formatted(expectedStatusCode))
                    .isNotNull();

            assertExpectedEntity(type, EServiceCheckMode.PRESENT);
        } else {
            assertExpectedEntity(type, EServiceCheckMode.NONE);
        }
    }

    // --- SUPPORT ---
    private void retrieveEntity(EserviceRequestType requestType, EserviceEntityType entityType) {
        Pair<UUID, UUID> idPair = resolveIds(requestType);
        UUID eserviceId = idPair.getLeft();
        UUID descriptorId = idPair.getRight();

        Optional<?> response;

        switch (entityType) {
            case ESERVICE -> response = dataPreparationService.getEService(eserviceId);

            case DESCRIPTORS -> response = dataPreparationService.getEserviceDescriptors(
                    IM2MEserviceClient.EserviceDescriptorsListRequest.builder().eserviceId(eserviceId).offset(0).limit(30).build());

            case ESERVICES -> response = dataPreparationService.getEServices(
                    IM2MEserviceClient.EserviceListRequest.builder().offset(0).limit(30).build());

            case DESCRIPTOR -> response = dataPreparationService.getEServiceDescriptor(eserviceId, descriptorId);
            default -> throw new UnsupportedOperationException("Tipo non supportato: " + entityType);
        }

        List<EServiceDescriptor> result = response
                .map(entityType.geteServiceDescriptorMapper())
                .orElseGet(Collections::emptyList);

        sharedStepsContext.getEServicesCommonContext().setRetrievedEservicesIds(result);
    }

    private Pair<UUID,UUID> resolveIds(EserviceRequestType type) {
        return switch (type) {
            case VALID -> {
                List<EServiceDescriptor> list = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds();
                assertThat(list).hasSize(1);

                EServiceDescriptor descriptor = list.get(0);
                yield Pair.of(descriptor.getEServiceId(), descriptor.getDescriptorId());
            }
            case NULL_ID -> Pair.of(null, null) ;
            case NON_EXISTENT_ID -> Pair.of(UUID.randomUUID(), UUID.randomUUID());
        };
    }

    private void assertExpectedEntity(EserviceEntityType entity, EServiceCheckMode mode) {
        List<EServiceDescriptor> retrieved = sharedStepsContext.getEServicesCommonContext().getRetrievedEservicesIds();
        List<EServiceDescriptor> published = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds();
        Function<EServiceDescriptor, Object> extractor = null;

        switch (entity) {
            case ESERVICE, ESERVICES -> extractor = b -> b.getEServiceId();
            case DESCRIPTORS -> extractor = b -> b.getDescriptorId();
            case DESCRIPTOR -> extractor = b -> b;
            default -> throw new UnsupportedOperationException("Tipo non supportato: " + entity);
        }

        switch (mode) {
            case NONE -> assertThat(retrieved)
                    .as("La lista dei %s dovrebbe essere vuota".formatted(entity.getLabel()))
                    .isNullOrEmpty();

            case PRESENT -> assertThat(retrieved)
                    .as("La lista dei %s dovrebbe essere presente".formatted(entity.getLabel()))
                    .isNotEmpty();

            case PRESENT_AND_MATCHING -> {
                assertThat(retrieved)
                        .as("La lista dei %s dovrebbe essere presente".formatted(entity.getLabel()))
                        .isNotEmpty();

                // Verifica che abbiano gli stessi eserviceId (e/o descriptorId in base ad extractor) di quelli generati
                assertThat(retrieved)
                        .extracting(extractor)
                        .as("I %s restituiti dovrebbero corrispondere a quelli pubblicati".formatted(entity.getLabel()))
                        .containsAll(published.stream().map(extractor).toList());
            }
        }
    }

}
