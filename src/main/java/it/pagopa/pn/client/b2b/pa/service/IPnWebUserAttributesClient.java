package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.Consent;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.ConsentAction;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.ConsentType;
import org.springframework.web.client.RestClientException;
import java.util.List;


public interface IPnWebUserAttributesClient extends SettableBearerToken {
    void consentAction(ConsentType consentType, ConsentAction consentAction, String version) throws RestClientException;
    Consent getConsentByType(ConsentType consentType, String version) throws RestClientException;
    List<Consent> getConsents() throws RestClientException;
    UserAddresses getAddressesByRecipient() throws RestClientException;
    void deleteRecipientLegalAddress(String senderId, LegalChannelType channelType) throws RestClientException;
    List<LegalAndUnverifiedDigitalAddress> getLegalAddressByRecipient() throws RestClientException;
    void postRecipientLegalAddress(String senderId, LegalChannelType channelType, AddressVerification addressVerification) throws RestClientException;
    void deleteRecipientCourtesyAddress(String senderId, CourtesyChannelType channelType) throws RestClientException;
    List<CourtesyDigitalAddress> getCourtesyAddressByRecipient() throws RestClientException;
    void postRecipientCourtesyAddress(String senderId, CourtesyChannelType channelType, AddressVerification addressVerification) throws RestClientException;
}