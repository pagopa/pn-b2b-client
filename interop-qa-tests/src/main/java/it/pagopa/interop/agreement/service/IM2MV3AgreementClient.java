package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;

import java.util.UUID;

public interface IM2MV3AgreementClient extends IM2MAgreementClient, SettableHeaders, Authenticable {
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Agreement archiveAgreement(UUID agreementId);
}
