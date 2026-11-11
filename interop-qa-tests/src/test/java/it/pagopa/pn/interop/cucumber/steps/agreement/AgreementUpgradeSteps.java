package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

public class AgreementUpgradeSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;

    public AgreementUpgradeSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente richiede un'operazione di upgrade di quella richiesta di fruizione")
    public void requireAgreementUpgrade() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        requireAgreementUpgradeImpl();
    }

    @When("{string} richiede un'operazione di upgrade di quella richiesta di fruizione")
    public void requireAgreementUpgradeByUser(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        requireAgreementUpgradeImpl();
    }

    @When("{string} richiede un'operazione di upgrade di quella richiesta di fruizione con successo")
    public void successfullyRequireAgreementUpgradeByUser(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        requireAgreementUpgradeImpl();
        if(sharedStepsContext.getHttpCallExecutor().getResponseStatus().isError()) {
            throw new IllegalStateException("L'aggiornamento della richiesta di fruizione alla nuova versione dell'e-service non è andata a buon fine");
        }
    }

    private void requireAgreementUpgradeImpl() {
        sharedStepsContext.getHttpCallExecutor().performCall(
            () -> clientTokenConfigurator.getAgreementClient().upgradeAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId())
        );
        if (sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful()) {
            Agreement agreement = ((Agreement) sharedStepsContext.getHttpCallExecutor().getResponse());
            sharedStepsContext.getAgreementCommonContext().setResponseAgreementId(agreement.getId());
        }
    }

    @Given("{string} ha già pubblicato una nuova versione per quell'e-service richiedendo gli stessi attributi certificati")
    public void publishNewEserviceVersionWithSameCertifiedAttribute(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID descriptorId = dataPreparationService.createNextDraftDescriptor(
                sharedStepsContext.getEServicesCommonContext().getEserviceId());
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(descriptorId);

        dataPreparationService.updateDraftDescriptor(sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                descriptorId, new UpdateEServiceDescriptorSeed().attributes(new DescriptorAttributesSeed()
                        .certified(List.of(List.of(new DescriptorAttributeSeed()
                                .id(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                                .explicitAttributeVerification(true)
                        )))
                ));
        dataPreparationService.bringDescriptorToGivenState(sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                descriptorId, EServiceDescriptorState.PUBLISHED, false);
    }

    @Given("{string} ha già pubblicato una nuova versione per quell'e-service che richiede un attributo {string} che {string} non possiede")
    public void publishNewEServiceVersionWithNewAttribute(String tenantType, String kind, String consumer) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID attributeId = dataPreparationService.createAttribute(AttributeKind.valueOf(kind), null).getId();

        List<List<DescriptorAttributeSeed>> seed = List.of(List.of(new DescriptorAttributeSeed().id(attributeId).explicitAttributeVerification(true)));

        UUID descriptorId = dataPreparationService.createNextDraftDescriptor(sharedStepsContext.getEServicesCommonContext().getEserviceId());
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(descriptorId);

        dataPreparationService.updateDraftDescriptor(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                new UpdateEServiceDescriptorSeed().attributes(
                        new DescriptorAttributesSeed()
                                .certified(kind.equals(AttributeKind.CERTIFIED.getValue()) ? seed : List.of())
                                .declared(kind.equals(AttributeKind.DECLARED.getValue()) ? seed : List.of())
                                .verified(kind.equals(AttributeKind.VERIFIED.getValue()) ? seed : List.of())
                )
        );
        dataPreparationService.bringDescriptorToGivenState(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                EServiceDescriptorState.PUBLISHED,
                false
        );
    }

    @Then("si ottiene status code {int} ed è stata creata una nuova richiesta di fruizione in DRAFT")
    public void verifyStatusCodeAndAgreementStatus(int statusCode) {
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        Assertions.assertEquals(statusCode, httpCallExecutor.getResponseStatus().value());

        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getAgreementClient().getAgreementById(
                                sharedStepsContext.getAgreementCommonContext().getResponseAgreementId())),
                res -> httpCallExecutor.getResponseStatus() != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the agreement by id!"
        );

        Agreement createdAgreement = clientTokenConfigurator.getAgreementClient().getAgreementById(
                sharedStepsContext.getAgreementCommonContext().getResponseAgreementId());
        Assertions.assertEquals(AgreementState.DRAFT, createdAgreement.getState());
    }

    @Then("si ottiene status code 200 e la nuova richiesta di fruizione è associata alla versione 3 dell'eservice")
    public void verifyStatusCodeAndAssociatedEServiceVersion() {
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        Assertions.assertEquals(200, httpCallExecutor.getResponseStatus().value());

        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getAgreementClient().getAgreementById(sharedStepsContext.getAgreementCommonContext().getResponseAgreementId())),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the agreement by id!"
        );

        Agreement createdAgreement = clientTokenConfigurator.getAgreementClient().getAgreementById(sharedStepsContext.getAgreementCommonContext().getResponseAgreementId());
        Assertions.assertEquals(sharedStepsContext.getEServicesCommonContext().getDescriptorId(), createdAgreement.getDescriptorId());
    }
}
