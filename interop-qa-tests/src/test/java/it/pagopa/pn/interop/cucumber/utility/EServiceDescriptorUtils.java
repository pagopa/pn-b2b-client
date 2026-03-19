package it.pagopa.pn.interop.cucumber.utility;

import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import org.springframework.http.HttpStatus;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class EServiceDescriptorUtils {
    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IHttpExecutor httpCallExecutor;

    public EServiceDescriptorUtils(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    public void updateEServiceDescriptor(ProducerEServiceDescriptor eServiceDescriptor, DescriptorAttributesSeed attributesSeed) {

        if (eServiceDescriptor.getState() == EServiceDescriptorState.PUBLISHED) {
            httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDescriptorAttributes(
                    eServiceDescriptor.getEservice().getId(),
                    eServiceDescriptor.getId(),
                    attributesSeed
                )
            );
        } else if (eServiceDescriptor.getState() == EServiceDescriptorState.DRAFT) {
            UpdateEServiceDescriptorSeed seed = new UpdateEServiceDescriptorSeed()
                .description(eServiceDescriptor.getDescription())
                .audience(eServiceDescriptor.getAudience())
                .voucherLifespan(eServiceDescriptor.getVoucherLifespan())
                .dailyCallsPerConsumer(eServiceDescriptor.getDailyCallsPerConsumer())
                .dailyCallsTotal(eServiceDescriptor.getDailyCallsTotal())
                .agreementApprovalPolicy(eServiceDescriptor.getAgreementApprovalPolicy())
                .attributes(attributesSeed);
            httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDraftDescriptor(
                    eServiceDescriptor.getEservice().getId(),
                    eServiceDescriptor.getId(),
                    seed
                )
            );
        } else {
            throw new IllegalStateException("Stato dell'e-service non gestito: " + eServiceDescriptor.getState());
        }
    }

    @Nonnull
    public Optional<DescriptorAttribute> getDescriptorAttribute(UUID eServiceId, UUID descriptorId, UUID attributeId) {

        httpCallExecutor.snapshot();

        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(() -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId)),
                res -> res != HttpStatus.NOT_FOUND,
                BFFDataPreparationService.ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
        ProducerEServiceDescriptor producerEServiceDescriptor = (ProducerEServiceDescriptor) httpCallExecutor.getResponse();

        httpCallExecutor.resetFormSnapshot();

        return producerEServiceDescriptor.getAttributes()
                .getCertified()
                .stream()
                .filter(attrList -> attrList.stream().anyMatch(attr -> attr.getId().equals(attributeId)))
                .map(attrList -> attrList.get(0))
                .findFirst();
    }
}
