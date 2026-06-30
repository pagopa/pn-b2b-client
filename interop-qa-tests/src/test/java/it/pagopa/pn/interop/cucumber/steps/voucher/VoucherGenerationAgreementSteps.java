package it.pagopa.pn.interop.cucumber.steps.voucher;

import io.cucumber.java.en.Given;
import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.List;
import java.util.UUID;

public class VoucherGenerationAgreementSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;

    public VoucherGenerationAgreementSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("{string} ha già attivato nuovamente quella richiesta di fruizione come {clientType}")
    public void activateAgreement(String tenantType, ClientType clientType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.activateAgreement(
            sharedStepsContext.getAgreementCommonContext().getAgreementId(),
            clientType, null
        );
    }

    @Given("{string} ha già (assegnato)(dichiarato) nuovamente quell'attributo {string} a {string}")
    public void assignOrDeclareAttribute(
        String assignerTenant,
        String attributeKind,
        String destTenant
    ) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(assignerTenant, null));
        UUID idDestinatario = identityService.getOrganizationId(destTenant);
        UUID idAssegnatore = identityService.getOrganizationId(assignerTenant);
        switch (attributeKind) {
            case "CERTIFIED":
                dataPreparationService.assignCertifiedAttributeToTenant(
                    idDestinatario,
                    sharedStepsContext.getAttributeCommonContext().getAttributeId()
                );
                break;
            case "VERIFIED":
                dataPreparationService.assignVerifiedAttributeToTenant(
                    idDestinatario,
                    idAssegnatore,
                    sharedStepsContext.getAttributeCommonContext().getAttributeId(),
                    sharedStepsContext.getAgreementCommonContext().getAgreementId(),
                    null);
                break;
            case "DECLARED":
                dataPreparationService.declareDeclaredAttribute(
                    idDestinatario,
                    sharedStepsContext.getAttributeCommonContext().getAttributeId()
                );
                break;
            default:
                handleUnknownAttributeKind(attributeKind);
        }
    }

    @Given("{string} ha già revocato quell'attributo {string} a {string}")
    public void revokeAttribute(
        String revokerTenant,
        String attributeKind,
        String dstTenant
    ) {
        var revokerId = identityService.getOrganizationId(revokerTenant);
        var dstId = identityService.getOrganizationId(dstTenant);
        clientTokenConfigurator.setBearerToken(identityService.getToken(revokerTenant, null));
        switch (attributeKind) {
            case "CERTIFIED":
                dataPreparationService.revokeCertifiedAttributeToTenant(
                    dstId,
                    sharedStepsContext.getAttributeCommonContext().getAttributeId()
                );
                break;
            case "VERIFIED":
                dataPreparationService.revokeVerifiedAttributeToTenant(
                    dstId,
                    sharedStepsContext.getAttributeCommonContext().getAttributeId(),
                    sharedStepsContext.getAgreementCommonContext().getAgreementId(),
                    revokerId
                );
                break;
            case "DECLARED":
                dataPreparationService.revokeDeclaredAttributeToTenant(
                    dstId,
                    sharedStepsContext.getAttributeCommonContext().getAttributeId());
                break;
            default:
                handleUnknownAttributeKind(attributeKind);
        }

    }

    @Given("{string} ha già aggiornato la richiesta di fruizione all'ultima versione dell'eservice")
    public void upgradeAgreement(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.upgradeAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId());
    }

    @Given("{string} ha già richiesto la pubblicazione della richiesta aggiornata che và in stato PENDING")
    public void requestAgreementPublication(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.submitAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), AgreementState.PENDING);
    }

    @Given("{string} ha già archiviato quella richiesta di fruizione")
    public void archiveAgreement(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.archiveAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId());
    }

    @Given("{string} ha già pubblicato una nuova versione per quell'e-service che richiede quell'attributo verificato")
    public void publishNewVersion(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = dataPreparationService.createNextDraftDescriptor(
            eserviceId);
        UUID attributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();

        dataPreparationService.updateDraftDescriptor(
            eserviceId,
            descriptorId,
            new UpdateEServiceDescriptorSeed()
                .attributes(new DescriptorAttributesSeed()
                    .addVerifiedItem(
                        List.of(new DescriptorAttributeSeed()
                            .id(attributeId)
                            .explicitAttributeVerification(true)
                        )
                    )
                )
        );

        dataPreparationService.bringDescriptorToGivenState(
            eserviceId,
            descriptorId,
            EServiceDescriptorState.PUBLISHED,
            false);
    }

    @Given("{string} approva quella richiesta di fruizione")
    public void approveAgreement(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.activateAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), null, null);
    }

    private static void handleUnknownAttributeKind(String attributeKind) {
        throw new IllegalArgumentException("Unknown attribute kind: " + attributeKind);
    }
}
