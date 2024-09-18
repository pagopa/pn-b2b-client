package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyBody;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyConsent;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

public interface IPnTosPrivacyClient {

    void acceptTosPrivacyV1(BffTosPrivacyBody bffTosPrivacyBody) throws RestClientException;

    BffTosPrivacyConsent getTosPrivacyV1() throws RestClientException;

    void setBearerToken(SettableBearerToken.BearerTokenType bearerToken);
}
