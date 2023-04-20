package it.pagopa.pn.client.b2b.pa.testclient;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebUserAttributesExternalClientImpl implements IPnWebUserAttributesClient {


    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final ObjectMapper objMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();


    private final ConsentsApi ConsentsApi;

    private final LegalApi legalApi;
    private final AllApi allApi;
    private final CourtesyApi courtesyApiAddressBook;

    private BearerTokenType bearerTokenSetted = BearerTokenType.USER_1;

    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;

    private final String userAgent;
    private final String basePath;

    public PnWebUserAttributesExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.webapi.external.base-url}") String basePath,
            @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
            @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
            @Value("${pn.webapi.external.user-agent}") String userAgent
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.ConsentsApi = new ConsentsApi(newConsentsApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));

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
                this.ConsentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));

                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));

                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
                break;
            case USER_2:
                this.ConsentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));

                this.legalApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.allApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.courtesyApiAddressBook.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));

                this.bearerTokenSetted = BearerTokenType.USER_2;
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
        this.ConsentsApi.consentAction(consentType, version, consentAction);
    }

    public Consent getConsentByType(ConsentType consentType, String version) throws RestClientException {
        return this.ConsentsApi.getConsentByType(consentType, version);
    }


    public List<Consent> getConsents() throws RestClientException {
        return this.ConsentsApi.getConsents();
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
