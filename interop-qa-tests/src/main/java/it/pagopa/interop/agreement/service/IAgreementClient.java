package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;

import java.io.File;
import java.util.UUID;

public interface IAgreementClient extends SettableBearerToken {
    CreatedResource createAgreement(String xCorrelationId, AgreementPayload agreementPayload);
    Agreement getAgreementById(String xCorrelationId, UUID agreementId);
    Agreement activateAgreement(String xCorrelationId, UUID agreementId);
    Agreement submitAgreement(String xCorrelationId, UUID agreementId, AgreementSubmissionPayload agreementSubmissionPayload);
    Agreement suspendAgreement(String xCorrelationId, UUID agreementId);
    void archiveAgreement(String xCorrelationId, UUID agreementId);
    Agreement rejectAgreement(String xCorrelationId, UUID agreementId, AgreementRejectionPayload agreementRejectionPayload);
    File addAgreementConsumerDocument(String xCorrelationId, UUID agreementId, String name, String prettyName, org.springframework.core.io.Resource doc);

}
