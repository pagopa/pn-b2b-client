package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.digitaladdresses.AddressesApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.tos.privacy.UserConsentsApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.digitaladdresses.BffAddressType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.digitaladdresses.BffAddressVerificationRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.digitaladdresses.BffChannelType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.digitaladdresses.BffUserAddress;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffConsent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyActionBody;
import it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.CxLanguage;
import it.pagopa.pn.client.b2b.pa.exception.IllegalConfigurationException;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.api.CourtesyApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.api.LegalApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.AddressVerification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.CourtesyDigitalAddress;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.Consent;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.ConsentAction;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.ConsentType;
import it.pagopa.pn.client.b2b.pa.service.IPnWebUserAttributesClient;
import it.pagopa.pn.client.b2b.pa.wrapper.LegalCourtesyAddressWrapper;
import it.pagopa.pn.client.b2b.pa.wrapper.RecipientWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebUserAttributesInternalClientImpl implements IPnWebUserAttributesClient {
    private final RestTemplate restTemplate;
    private final UserConsentsApi consentsApi;
    private final AddressesApi addressesApi;
    private BearerTokenType bearerTokenSetted = BearerTokenType.USER_1;
    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String galileoBearerToken;
    private final String dinoBearerToken;
    private final String userBearerTokenScaduto;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;

    private final String aldaMeriniBearerToken;
    private final String mariaMontessoriBearerToken;
    private final String userAgent;
    private final String basePath;

    private final LegalApi legalApi;

    private final CourtesyApi courtesyApiAddressBook;


    public PnWebUserAttributesInternalClientImpl(RestTemplate restTemplate,
                                                 @Value("${pn.webapi.external.base-url}") String basePath,
                                                 @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                                 @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                                 @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                                 @Value("${pn.bearer-token.user4}") String galileoBearerToken,
                                                 @Value("${pn.bearer-token.user5}") String dinoBearerToken,
                                                 @Value("${pn.bearer-token.scaduto}") String userBearerTokenScaduto,
                                                 @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                                 @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
                                                 @Value("${pn.bearer-token.pg3}") String aldaMeriniBearerToken,
                                                 @Value("${pn.bearer-token.pg4}") String mariaMontessoriBearerToken,
                                                 @Value("${pn.webapi.external.user-agent}") String userAgent) {
        this.restTemplate = restTemplate;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.galileoBearerToken = galileoBearerToken;
        this.dinoBearerToken = dinoBearerToken;
        this.userBearerTokenScaduto = userBearerTokenScaduto;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.aldaMeriniBearerToken = aldaMeriniBearerToken;
        this.mariaMontessoriBearerToken = mariaMontessoriBearerToken;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.consentsApi = new UserConsentsApi(newConsentsApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
        this.addressesApi = new AddressesApi(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
        this.legalApi = new LegalApi(newAddressBookInternalApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
        this.courtesyApiAddressBook = new CourtesyApi(newAddressBookInternalApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.tos.ApiClient newConsentsApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.tos.ApiClient newApiClient =
                new it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.tos.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    private static ApiClient newAddressBookApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.ApiClient newAddressBookInternalApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken) {
            case USER_1:
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.addressesApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
                break;
            case USER_2:
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.addressesApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_2;
                beenSet = true;
                break;
            case USER_3:
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.addressesApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_3;
                beenSet = true;
                break;
            case USER_4:
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, galileoBearerToken, userAgent));
                this.addressesApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, galileoBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_4;
                beenSet = true;
                break;
            case USER_5:
                this.consentsApi.setApiClient(newConsentsApiClient(restTemplate, basePath, dinoBearerToken, userAgent));
                this.addressesApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, dinoBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_5;
                beenSet = true;
                break;
            case PG_1:
                this.addressesApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_1;
                beenSet = true;
                break;
            case PG_2:
                this.addressesApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_2;
                beenSet = true;
                break;
            case PG_3:
                this.addressesApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, aldaMeriniBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_3;
                beenSet = true;
                break;
            case PG_4:
                this.addressesApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, mariaMontessoriBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_4;
                beenSet = true;
                break;
            case USER_SCADUTO:
                this.addressesApi.setApiClient(newAddressBookApiClient(restTemplate, basePath, userBearerTokenScaduto, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_SCADUTO;
                beenSet = true;
                break;
            default:
                throw new IllegalConfigurationException("Invalid token: " + bearerToken);
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
        this.consentsApi.acceptTosPrivacyV2(List.of(new BffTosPrivacyActionBody()
                .action(BffTosPrivacyActionBody.ActionEnum.fromValue(consentAction.getAction().getValue()))
                .type(it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.ConsentType.fromValue(consentAction.getAction().getValue()))
                .version(version)));
    }

    public Consent getConsentByType(ConsentType consentType, String version) throws RestClientException {
        List<BffConsent> bffConsents = this.consentsApi.getTosPrivacyV2(List.of(
                it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.ConsentType.fromValue(consentType.getValue()))
        );
        return deepCopy(bffConsents.get(0), Consent.class);
    }

    public RecipientWrapper getAddressesByRecipient() throws RestClientException {

        List<BffUserAddress> bffUserAddress = addressesApi.getAddressesV1();
        RecipientWrapper recipientWrapper = new RecipientWrapper();
        recipientWrapper.setBffUserAddress(bffUserAddress);

        return recipientWrapper;
    }


    public void deleteRecipientLegalAddress(String senderId, LegalCourtesyAddressWrapper.ChannelType channelType) throws RestClientException {
        addressesApi.deleteAddressV1(BffAddressType.LEGAL, senderId, BffChannelType.fromValue(channelType.getValue()));
    }

    public List<LegalCourtesyAddressWrapper> getLegalAddressByRecipient() throws RestClientException {
        return addressesApi.getAddressesV1().stream()
                .filter(item -> "LEGAL".equals(item.getAddressType()))
                .map(item -> deepCopy(item, LegalCourtesyAddressWrapper.class))
                .toList();
    }

    public void postRecipientLegalAddress(String senderId, LegalCourtesyAddressWrapper.ChannelType channelType, AddressVerification addressVerification, CxLanguage xPagopaPnLanguage) throws RestClientException {
        BffAddressVerificationRequest bffAddressVerificationRequest = new BffAddressVerificationRequest().requestId(addressVerification.getRequestId())
                .verificationCode(addressVerification.getVerificationCode()).value(addressVerification.getValue());
        addressesApi.createOrUpdateAddressV1(BffAddressType.LEGAL, senderId, BffChannelType.fromValue(channelType.getValue()), bffAddressVerificationRequest, deepCopy(xPagopaPnLanguage,  it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.digitaladdresses.CxLanguage.class));
        //legalApi.postRecipientLegalAddress("pn-test", CxTypeAuthFleet.PF, senderId, deepCopy(channelType, LegalChannelType.class), addressVerification, null, null, deepCopy(xPagopaPnLanguage, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.CxLanguage.class));
    }

    public void deleteRecipientCourtesyAddress(String senderId, LegalCourtesyAddressWrapper.ChannelType channelType) throws RestClientException {
        addressesApi.deleteAddressV1(BffAddressType.COURTESY, senderId, BffChannelType.fromValue(channelType.getValue()));
    }

    public List<CourtesyDigitalAddress> getCourtesyAddressByRecipient() throws RestClientException {
        return addressesApi.getAddressesV1().stream()
                .filter(item -> "COURTESY".equals(item.getAddressType()))
                .map(item -> deepCopy(item, CourtesyDigitalAddress.class))
                .toList();
    }

    public void postRecipientCourtesyAddress(String senderId, LegalCourtesyAddressWrapper.ChannelType channelType, AddressVerification addressVerification, CxLanguage xPagopaPnLanguageCxLanguage) throws RestClientException {
        BffAddressVerificationRequest bffAddressVerificationRequest = new BffAddressVerificationRequest().requestId(addressVerification.getRequestId())
                .verificationCode(addressVerification.getVerificationCode()).value(addressVerification.getValue());
        addressesApi.createOrUpdateAddressV1(BffAddressType.COURTESY, senderId, BffChannelType.fromValue(channelType.getValue()), bffAddressVerificationRequest, deepCopy(xPagopaPnLanguageCxLanguage,  it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.digitaladdresses.CxLanguage.class));
        //AddressVerificationResponse response = courtesyApiAddressBook.postRecipientCourtesyAddress("pn-test", CxTypeAuthFleet.PF, senderId, deepCopy(channelType, CourtesyChannelType.class), addressVerification, null, null, deepCopy(xPagopaPnLanguageCxLanguage, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.CxLanguage.class));
    }

    private <T> T deepCopy(Object obj, Class<T> toClass) {
        ObjectMapper objMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        try {
            String json = objMapper.writeValueAsString(obj);
            return objMapper.readValue(json, toClass);
        } catch (JsonProcessingException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }

}