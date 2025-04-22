package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementRejectionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;

import java.io.File;
import java.util.UUID;

public interface IAgreementClient extends SettableBearerToken {
    CreatedResource createAgreement(AgreementPayload agreementPayload);
    Agreement getAgreementById(UUID agreementId);
    Agreement activateAgreement(UUID agreementId);
    Agreement submitAgreement(UUID agreementId, AgreementSubmissionPayload agreementSubmissionPayload);
    Agreement suspendAgreement(UUID agreementId);
    void archiveAgreement(UUID agreementId);
    Agreement rejectAgreement(UUID agreementId, AgreementRejectionPayload agreementRejectionPayload);
    File addAgreementConsumerDocument(UUID agreementId, String name, String prettyName, org.springframework.core.io.Resource doc);

}
