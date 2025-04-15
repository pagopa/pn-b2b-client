package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementRejectionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.UUID;

public interface IAgreementClient extends SettableBearerToken {
    CreatedResource createAgreement(AgreementPayload agreementPayload);
    Agreement getAgreementById(String xCorrelationId, UUID agreementId);
    Agreement activateAgreement(String xCorrelationId, UUID agreementId);
    Agreement submitAgreement(String xCorrelationId, UUID agreementId, AgreementSubmissionPayload agreementSubmissionPayload);
    Agreement suspendAgreement(String xCorrelationId, UUID agreementId);
    void archiveAgreement(String xCorrelationId, UUID agreementId);
    Agreement rejectAgreement(String xCorrelationId, UUID agreementId, AgreementRejectionPayload agreementRejectionPayload);
    File addAgreementConsumerDocument(String xCorrelationId, UUID agreementId, String name, String prettyName, org.springframework.core.io.Resource doc);
    CreatedResource cloneAgreement(String xCorrelationId, UUID agreementId);
    ResponseEntity<CompactOrganizations> getAgreementConsumers(String xCorrelationId, Integer offset, Integer limit, String q);
    void deleteAgreement(String xCorrelationId, UUID agreementId);

}
