package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.model.Consent;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.model.ConsentAction;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.model.ConsentType;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IPnWebUserAttributesClient extends SettableBearerToken {

    void consentAction(ConsentType consentType, ConsentAction consentAction, String version) throws RestClientException;

    Consent getConsentByType(ConsentType consentType, String version) throws RestClientException;

    List<Consent> getConsents() throws RestClientException;

}
