package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.io.model.IoCourtesyDigitalAddressActivation;
import org.springframework.web.client.RestClientException;

public interface IPnIoUserAttributerExternaClient {

    IoCourtesyDigitalAddressActivation getCourtesyAddressIo(String xPagopaCxTaxid) throws RestClientException;

    void setCourtesyAddressIo(String xPagopaCxTaxid, IoCourtesyDigitalAddressActivation ioCourtesyDigitalAddressActivation) throws RestClientException;
}
