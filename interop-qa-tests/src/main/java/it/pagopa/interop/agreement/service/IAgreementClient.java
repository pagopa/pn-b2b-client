package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;

import java.io.File;
import java.util.List;
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

    // nuovi metodi
    Agreements getAgreements(
            Integer offset,
            Integer limit,
            List<AgreementState> states,
            List<UUID> producerIds,
            List<UUID> consumerIds,
            List<UUID> eserviceIds
    );

}
