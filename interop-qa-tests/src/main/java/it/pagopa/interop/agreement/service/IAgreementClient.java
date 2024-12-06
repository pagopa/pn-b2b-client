package it.pagopa.interop.agreement.service;

import it.pagopa.interop.generated.openapi.clients.bff.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;

import java.io.File;
import java.util.UUID;

public interface IAgreementClient {
    CreatedResource createAgreement(String xCorrelationId, AgreementPayload agreementPayload);
    Agreement getAgreementById(String xCorrelationId, UUID agreementId);
    Agreement submitAgreement(String xCorrelationId, UUID agreementId, AgreementSubmissionPayload agreementSubmissionPayload);
    Agreement suspendAgreement(String xCorrelationId, UUID agreementId);
    void archiveAgreement(String xCorrelationId, UUID agreementId);
    File addAgreementConsumerDocument(String xCorrelationId, UUID agreementId, String name, String prettyName, org.springframework.core.io.Resource doc);

}
