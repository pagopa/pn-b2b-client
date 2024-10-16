package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.api.AllApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.api.CourtesyApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.api.LegalApi;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebUserAttributesClient;
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
public class B2BUserAttributesExternalClientImpl implements IPnWebUserAttributesClient {
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
    private final String basePath;


    public B2BUserAttributesExternalClientImpl(RestTemplate restTemplate,
                                               @Value("${pn.external.dest.base-url}") String basePath,
                                               @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                               @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                               @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                               @Value("${pn.bearer-token.user4}") String galileoBearerToken,
                                               @Value("${pn.bearer-token.user5}") String dinoBearerToken,
                                               @Value("${pn.bearer-token.scaduto}") String userBearerTokenScaduto,
                                               @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                               @Value("${pn.bearer-token-b2b.pg2}") String cucumberSpaBearerToken) {
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
        this.consentsApi = new ConsentsApi(newConsentsApiClient(restTemplate, basePath, marioCucumberBearerToken));
        this.legalApi = new LegalApi(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken));
        this.allApi = new AllApi(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken));
        this.courtesyApiAddressBook = new CourtesyApi(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken));
    }

    private static it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.ApiClient newConsentsApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.ApiClient newApiClient =
                new it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.ApiClient newAddressBookApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.ApiClient newApiClient =
                new it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case USER_1 -> {
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, marioCucumberBearerToken));
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_1;
            }
            case USER_2 -> {
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, marioGherkinBearerToken));
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioGherkinBearerToken));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioGherkinBearerToken));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioGherkinBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_2;
            }
            case USER_3 -> {
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, leonardoBearerToken));
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, leonardoBearerToken));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, leonardoBearerToken));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, leonardoBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_3;
            }
            case USER_4 -> {
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, galileoBearerToken));
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, galileoBearerToken));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, galileoBearerToken));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, galileoBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_4;
            }
            case USER_5 -> {
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, dinoBearerToken));
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, dinoBearerToken));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, dinoBearerToken));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, dinoBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_5;
            }
            case PG_1 -> {
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
            }
            case PG_2 -> {
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_2;
            }
            case USER_SCADUTO -> {
                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, userBearerTokenScaduto));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, userBearerTokenScaduto));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, userBearerTokenScaduto));
                this.bearerTokenSetted = BearerTokenType.USER_SCADUTO;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    public void consentAction(ConsentType consentType, ConsentAction consentAction, String version) throws RestClientException {
        this.consentsApi.consentAction(consentType, version, consentAction);
    }

    public Consent getConsentByType(ConsentType consentType, String version) throws RestClientException {
        return this.consentsApi.getConsentByType(consentType, version);
    }


    public List<Consent> getConsents() throws RestClientException {
        return this.consentsApi.getConsents();
    }

    public UserAddresses getAddressesByRecipient() throws RestClientException {
        return deepCopy(allApi.getAddressesByRecipient(), UserAddresses.class) ;
    }


    public void deleteRecipientLegalAddress(String senderId, LegalChannelType channelType) throws RestClientException {
        legalApi.deleteRecipientLegalAddress(senderId, deepCopy(channelType, it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.LegalChannelType.class));
    }


    public List<LegalAndUnverifiedDigitalAddress> getLegalAddressByRecipient() throws RestClientException {
        return legalApi.getLegalAddressByRecipient()
                .stream()
                .map(x -> deepCopy(x, LegalAndUnverifiedDigitalAddress.class))
                .toList();
    }

    public void postRecipientLegalAddress(String senderId, LegalChannelType channelType, AddressVerification addressVerification) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.LegalChannelType legalChannelType = deepCopy(channelType, it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.LegalChannelType.class);
        it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.AddressVerification address = deepCopy(addressVerification, it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.AddressVerification.class);
        legalApi.postRecipientLegalAddress(senderId, legalChannelType, address);
    }

    public void deleteRecipientCourtesyAddress(String senderId, CourtesyChannelType channelType) throws RestClientException {
        courtesyApiAddressBook.deleteRecipientCourtesyAddress(senderId, deepCopy(channelType, it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.CourtesyChannelType.class));
    }

    public List<CourtesyDigitalAddress> getCourtesyAddressByRecipient() throws RestClientException {
        return courtesyApiAddressBook.getCourtesyAddressByRecipient()
                .stream()
                .map(x -> deepCopy(x, CourtesyDigitalAddress.class))
                .toList();
    }

    public void postRecipientCourtesyAddress(String senderId, CourtesyChannelType channelType, AddressVerification addressVerification) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.CourtesyChannelType courtesyChannelType = deepCopy(channelType, it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.CourtesyChannelType.class);
        it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.AddressVerification address = deepCopy(addressVerification, it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.AddressVerification.class);
        courtesyApiAddressBook.postRecipientCourtesyAddress(senderId, courtesyChannelType, address);
    }

    private <T> T deepCopy( Object obj, Class<T> toClass) {
        ObjectMapper objMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        try {
            String json = objMapper.writeValueAsString( obj );
            return objMapper.readValue( json, toClass );
        } catch (JsonProcessingException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }
}