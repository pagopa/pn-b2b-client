package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.CxLanguage;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.AddressVerification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.CourtesyDigitalAddress;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.Consent;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.ConsentType;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.b2b.pa.wrapper.LegalCourtesyAddressWrapper;
import it.pagopa.pn.client.b2b.pa.wrapper.RecipientWrapper;
import org.springframework.web.client.RestClientException;

import java.util.List;


public interface IPnWebUserAttributesClient extends SettableBearerToken {
    Consent getConsentByType(ConsentType consentType, String version) throws RestClientException;

    RecipientWrapper getAddressesByRecipient() throws RestClientException;

    void deleteRecipientLegalAddress(String senderId, LegalCourtesyAddressWrapper.ChannelType channelType) throws RestClientException;

    List<LegalCourtesyAddressWrapper> getLegalAddressByRecipient() throws RestClientException;

    void postRecipientLegalAddress(String senderId, LegalCourtesyAddressWrapper.ChannelType channelType, AddressVerification addressVerification, CxLanguage xPagopaPnLanguage) throws RestClientException;

    void deleteRecipientCourtesyAddress(String senderId, LegalCourtesyAddressWrapper.ChannelType channelType) throws RestClientException;

    List<CourtesyDigitalAddress> getCourtesyAddressByRecipient() throws RestClientException;

    void postRecipientCourtesyAddress(String senderId, LegalCourtesyAddressWrapper.ChannelType channelType, AddressVerification addressVerification, CxLanguage xPagopaPnLanguageCxLanguage) throws RestClientException;
}