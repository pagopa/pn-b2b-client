package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffConsent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyActionBody;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.ConsentType;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IPnTosPrivacyClient {

    public void acceptTosPrivacyV1(List<BffTosPrivacyActionBody> bffTosPrivacyBody) throws RestClientException;

    List<BffConsent> getTosPrivacyV1(List<ConsentType> consentTypes) throws RestClientException;

    void acceptTosPrivacyV2(List<BffTosPrivacyActionBody> bffTosPrivacyBody) throws RestClientException;

    List<BffConsent> getTosPrivacyV2(List<ConsentType> consentTypes) throws RestClientException;

    void setBearerToken(SettableBearerToken.BearerTokenType bearerToken);
}
