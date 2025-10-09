package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.CIEValidationData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationResponse;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

public interface IPnMandateAppIoClient extends SettableBearerToken {

    MandateCreationResponse createIOMandate(String xPagopaCxTaxid, String xPagopaLollipopOriginalUrl, String xPagopaLollipopOriginalMethod, String xPagopaLollipopPublicKey, String xPagopaLollipopAssertionRef, String xPagopaLollipopAssertionType, String xPagopaLollipopAuthJwt, String xPagopaLollipopUserId, String signatureInput, String signature, MandateCreationRequest mandateCreationRequest) throws RestClientException;

    void acceptIOMandate(String xPagopaCxTaxid, String mandateId, String xPagopaLollipopOriginalUrl, String xPagopaLollipopOriginalMethod, String xPagopaLollipopPublicKey, String xPagopaLollipopAssertionRef, String xPagopaLollipopAssertionType, String xPagopaLollipopAuthJwt, String xPagopaLollipopUserId, String signatureInput, String signature, CIEValidationData ciEValidationData) throws RestClientException;
}
