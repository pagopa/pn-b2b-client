package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DescriptorUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public DescriptorUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                     SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente aggiorna alcuni parametri di quel descrittore")
    public void updateSomeDescriptorParams() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UpdateEServiceDescriptorSeed seed = new UpdateEServiceDescriptorSeed()
                .description("Questo è un e-service di test")
                .audience(List.of("api/v1"))
                .voucherLifespan(60)
                .dailyCallsPerConsumer(50)
                .dailyCallsTotal(2000)
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
                .attributes(new DescriptorAttributesSeed());

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDraftDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                        seed
                )
        );
    }

    @When("l'utente aggiorna la durata del voucher e le soglie di carico di quel descrittore")
    public void updateVoucherLifespanAndCallsLimit() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UpdateEServiceDescriptorQuotas seed = new UpdateEServiceDescriptorQuotas()
                .voucherLifespan(60)
                .dailyCallsPerConsumer(50)
                .dailyCallsTotal(2000);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                        seed
                )
        );
    }

    @When("l'utente modifica dailyCallsPerConsumer con {int} per l'attributo certificato appena creato")
    public void updateLastCreatedDailyCallsPerConsumer(int dailyCallsPerConsumer) {
        updateLastCreatedDailyCallsPerConsumer(dailyCallsPerConsumer, null);
    }

    @When("l'utente modifica dailyCallsPerConsumer con {int} per l'{int}-esimo attributo certificato creato")
    public void updateLastCreatedDailyCallsPerConsumer(int dailyCallsPerConsumer, Integer attributeIndex) {

        List<List<UUID>> requiredCertifiedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes();
        List<List<UUID>> requiredVerifiedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredVerifiedAttributes();
        List<List<UUID>> requiredDeclaredAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredDeclaredAttributes();

        UUID attributeId = (attributeIndex != null) ?
            requiredCertifiedAttributes.get(attributeIndex).get(0) :
            requiredCertifiedAttributes.get(requiredCertifiedAttributes.size() - 1).get(0);

        // TODO Threshold: gestire anche attributi verificati e dichiarati

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        var eServiceDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId);

        List<List<DescriptorAttributeSeed>> certifiedAttributes = sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getCertified());
        for (List<DescriptorAttributeSeed> group : certifiedAttributes) {
            for (DescriptorAttributeSeed attr : group) {
                if (attr.getId().equals(attributeId)) {
                    // TODO Threshold
                    log.info("Setting dailyCallsPerConsumer to {} for attribute ID {}", dailyCallsPerConsumer, attributeId);
                    // attr.setDailyCallsPerConsumer(dailyCallsPerConsumer);
                }
            }
        }

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed()
            .certified(certifiedAttributes)
            .declared(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getDeclared()))
            .verified(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getVerified()));





        // TODO THRESHOLD try / catch --> 400

        if (eServiceDescriptor.getState() == EServiceDescriptorState.PUBLISHED) {
            clientTokenConfigurator.getEServiceClient().updateDescriptorAttributes(
                    eServiceId,
                    descriptorId,
                    attributesSeed
            );
        } else {
            UpdateEServiceDescriptorSeed seed = new UpdateEServiceDescriptorSeed()
                    .description(eServiceDescriptor.getDescription())
                    .audience(eServiceDescriptor.getAudience())
                    .voucherLifespan(eServiceDescriptor.getVoucherLifespan())
                    .dailyCallsPerConsumer(eServiceDescriptor.getDailyCallsPerConsumer())
                    .dailyCallsTotal(eServiceDescriptor.getDailyCallsTotal())
                    .agreementApprovalPolicy(eServiceDescriptor.getAgreementApprovalPolicy())
                    .attributes(attributesSeed);

            clientTokenConfigurator.getEServiceClient().updateDraftDescriptor(eServiceId, descriptorId, seed);
        }

        Optional<DescriptorAttribute> certAttr = getDescriptorAttribute(eServiceId, descriptorId, attributeId);

        Assertions.assertTrue(certAttr.isPresent());
        Assertions.assertEquals(attributeId, certAttr.get().getId());
        // TODO Threshold
        // Assertions.assertEquals(certAttr.get().getDailyCallsPerConsumer(), dailyCallsPerConsumer);
    }

    @Nonnull
    private Optional<DescriptorAttribute> getDescriptorAttribute(UUID eServiceId, UUID descriptorId, UUID attributeId) {

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
