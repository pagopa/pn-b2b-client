package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementRejectionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementUpdatePayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServicesLight;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import java.util.List;
import java.util.UUID;

import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef;
import org.springframework.http.ResponseEntity;

public interface IAgreementClient extends SettableBearerToken {
    CreatedResource createAgreement(AgreementPayload agreementPayload);
    Agreement getAgreementById(UUID agreementId);
    ResponseEntity<Void> getAgreementContract(UUID agreementId);
    Agreement activateAgreement(UUID agreementId, DelegationRef delegationRef);
    Agreement activateAgreement(UUID agreementId);
    Agreement submitAgreement(UUID agreementId, AgreementSubmissionPayload agreementSubmissionPayload);
    Agreement suspendAgreement(UUID agreementId, DelegationRef delegationRef);
    Agreement suspendAgreement(UUID agreementId);
    Agreement unsuspendAgreement(UUID agreementId);
    Agreement unsuspendAgreement(UUID agreementId, UUID delegationId);
    Agreement updateAgreement(UUID agreementId, AgreementUpdatePayload agreementUpdatePayload);
    Agreement upgradeAgreement(UUID agreementId);
    void archiveAgreement(UUID agreementId);
    Agreement rejectAgreement(UUID agreementId, AgreementRejectionPayload agreementRejectionPayload);
    ResponseEntity<Void> addAgreementConsumerDocument(UUID agreementId, String name, String prettyName, org.springframework.core.io.Resource doc);
    CreatedResource cloneAgreement(UUID agreementId);
    ResponseEntity<CompactOrganizations> getAgreementConsumers(Integer offset, Integer limit, String q);
    ResponseEntity<CompactOrganizations> getAgreementProducers(Integer offset, Integer limit, String q);
    ResponseEntity<Void> getAgreementConsumerDocument(UUID agreementId, UUID documentId);
    void deleteAgreement(UUID agreementId);
    void removeAgreementConsumerDocument(UUID agreementId, UUID documentId);
    ResponseEntity<CompactEServicesLight> getAgreementEServiceConsumers(Integer offset, Integer limit, String q);
    ResponseEntity<CompactEServicesLight> getAgreementEServiceProducers(Integer offset, Integer limit, String q);
    ResponseEntity<it.pagopa.interop.generated.openapi.clients.bff.model.Agreements> getConsumerAgreements(Integer offset, Integer limit, List<UUID> eservicesIds, List<UUID> producersIds, List<AgreementState> states, Boolean showOnlyUpgradeable);
    ResponseEntity<it.pagopa.interop.generated.openapi.clients.bff.model.Agreements> getProducerAgreements(Integer offset, Integer limit, List<UUID> eservicesIds, List<UUID> consumersIds, List<AgreementState> states, Boolean showOnlyUpgradeable);

}
