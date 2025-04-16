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
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface IAgreementClient extends SettableBearerToken {
    CreatedResource createAgreement(AgreementPayload agreementPayload);
    Agreement getAgreementById(String xCorrelationId, UUID agreementId);
    ResponseEntity<File> getAgreementContract(String xCorrelationId, UUID agreementId);
    Agreement activateAgreement(String xCorrelationId, UUID agreementId);
    Agreement submitAgreement(String xCorrelationId, UUID agreementId, AgreementSubmissionPayload agreementSubmissionPayload);
    Agreement suspendAgreement(String xCorrelationId, UUID agreementId);
    Agreement updateAgreement(String xCorrelationId, UUID agreementId, AgreementUpdatePayload agreementUpdatePayload);
    Agreement upgradeAgreement(String xCorrelationId, UUID agreementId);
    void archiveAgreement(String xCorrelationId, UUID agreementId);
    Agreement rejectAgreement(String xCorrelationId, UUID agreementId, AgreementRejectionPayload agreementRejectionPayload);
    File addAgreementConsumerDocument(String xCorrelationId, UUID agreementId, String name, String prettyName, org.springframework.core.io.Resource doc);
    CreatedResource cloneAgreement(String xCorrelationId, UUID agreementId);
    ResponseEntity<CompactOrganizations> getAgreementConsumers(String xCorrelationId, Integer offset, Integer limit, String q);
    ResponseEntity<CompactOrganizations> getAgreementProducers(String xCorrelationId, Integer offset, Integer limit, String q);
    File getAgreementConsumerDocument(String xCorrelationId, UUID agreementId, UUID documentId);
    void deleteAgreement(String xCorrelationId, UUID agreementId);
    void removeAgreementConsumerDocument(String xCorrelationId, UUID agreementId, UUID documentId);
    ResponseEntity<CompactEServicesLight> getAgreementEServiceConsumers(String xCorrelationId, Integer offset, Integer limit, String q);
    ResponseEntity<CompactEServicesLight> getAgreementEServiceProducers(String xCorrelationId, Integer offset, Integer limit, String q, List<AgreementState> states);



}
