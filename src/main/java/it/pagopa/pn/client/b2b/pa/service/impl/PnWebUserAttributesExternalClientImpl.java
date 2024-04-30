package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnWebUserAttributesClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.api.AllApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.api.CourtesyApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.api.LegalApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.api.ConsentsApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.Consent;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.ConsentAction;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.ConsentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebUserAttributesExternalClientImpl implements IPnWebUserAttributesClient {
    private final RestTemplate restTemplate;
    private final ConsentsApi consentsApi;
    private final LegalApi legalApi;
    private final AllApi allApi;
    private final CourtesyApi courtesyApiAddressBook;
    private BearerTokenType bearerTokenSetted = BearerTokenType.USER_1;
    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String galileoBearerToken;
    private final String dinoBearerToken;
    private final String userBearerTokenScaduto;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final String userAgent;
    private final String basePath;


    public PnWebUserAttributesExternalClientImpl(RestTemplate restTemplate,
                                                 @Value("${pn.webapi.external.base-url}") String basePath,
                                                 @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                                 @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                                 @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                                 @Value("${pn.bearer-token.user4}") String galileoBearerToken,
                                                 @Value("${pn.bearer-token.user5}") String dinoBearerToken,
                                                 @Value("${pn.bearer-token.scaduto}") String userBearerTokenScaduto,
                                                 @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                                 @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
                                                 @Value("${pn.webapi.external.user-agent}") String userAgent) {
        this.restTemplate = restTemplate;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.galileoBearerToken = galileoBearerToken;
        this.dinoBearerToken = dinoBearerToken;
        this.userBearerTokenScaduto= userBearerTokenScaduto;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.consentsApi = new ConsentsApi(newConsentsApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
        this.legalApi = new LegalApi(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
        this.allApi = new AllApi(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
        this.courtesyApiAddressBook = new CourtesyApi(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
    }

    private static it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.ApiClient newConsentsApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.ApiClient newApiClient =
                new it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.ApiClient newAddressBookApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.ApiClient newApiClient =
                new it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken) {
            case USER_1:
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));

                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));

                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
                break;
            case USER_2:
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));

                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));

                this.bearerTokenSetted = BearerTokenType.USER_2;
                beenSet = true;
                break;
            case USER_3:
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));

                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));

                this.bearerTokenSetted = BearerTokenType.USER_3;
                beenSet = true;
                break;
            case USER_4:
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, galileoBearerToken, userAgent));

                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, galileoBearerToken, userAgent));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, galileoBearerToken, userAgent));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, galileoBearerToken, userAgent));

                this.bearerTokenSetted = BearerTokenType.USER_4;
                beenSet = true;
                break;
            case USER_5:
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, dinoBearerToken, userAgent));

                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, dinoBearerToken, userAgent));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, dinoBearerToken, userAgent));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, dinoBearerToken, userAgent));

                this.bearerTokenSetted = BearerTokenType.USER_5;
                beenSet = true;
                break;
            case PG_1:
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));

                this.bearerTokenSetted = BearerTokenType.PG_1;
                beenSet = true;
                break;
            case PG_2:
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));

                this.bearerTokenSetted = BearerTokenType.PG_2;
                beenSet = true;
                break;
            case USER_SCADUTO:
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, userBearerTokenScaduto, userAgent));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, userBearerTokenScaduto, userAgent));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, userBearerTokenScaduto, userAgent));

                this.bearerTokenSetted = BearerTokenType.USER_SCADUTO;
                beenSet = true;
                break;

        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    public void consentAction(ConsentType consentType, ConsentAction consentAction, String version) throws RestClientException {
        //TODO: problema da verificare
        /*
        consentAction(ConsentType consentType,
                          String consentAction,
                          ConsentAction version) ???
         */
        this.consentsApi.consentAction(consentType, version, consentAction);
    }

    public Consent getConsentByType(ConsentType consentType, String version) throws RestClientException {
        return this.consentsApi.getConsentByType(consentType, version);
    }


    public List<Consent> getConsents() throws RestClientException {
        return this.consentsApi.getConsents();
    }

    public UserAddresses getAddressesByRecipient() throws RestClientException {
        return allApi.getAddressesByRecipient();
    }


    public void deleteRecipientLegalAddress(String senderId, LegalChannelType channelType) throws RestClientException {
        legalApi.deleteRecipientLegalAddress(senderId, channelType);
    }


    public List<LegalAndUnverifiedDigitalAddress> getLegalAddressByRecipient() throws RestClientException {
        return legalApi.getLegalAddressByRecipient();
    }

    public void postRecipientLegalAddress(String senderId, LegalChannelType channelType, AddressVerification addressVerification) throws RestClientException {
        legalApi.postRecipientLegalAddress(senderId, channelType, addressVerification);
    }

    public void deleteRecipientCourtesyAddress(String senderId, CourtesyChannelType channelType) throws RestClientException {
        courtesyApiAddressBook.deleteRecipientCourtesyAddress(senderId, channelType);
    }

    public List<CourtesyDigitalAddress> getCourtesyAddressByRecipient() throws RestClientException {
        return courtesyApiAddressBook.getCourtesyAddressByRecipient();
    }

    public void postRecipientCourtesyAddress(String senderId, CourtesyChannelType channelType, AddressVerification addressVerification) throws RestClientException {
        courtesyApiAddressBook.postRecipientCourtesyAddress(senderId, channelType, addressVerification);
    }
}