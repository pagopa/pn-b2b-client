package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalAndUnverifiedDigitalAddress;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalChannelType;
import it.pagopa.pn.cucumber.utils.GroupPosition;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;

import static it.pagopa.pn.cucumber.utils.NotificationValue.PAYMENT_NOTICE_CODE_OPTIONAL;
import static it.pagopa.pn.cucumber.utils.NotificationValue.getValue;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SharedSteps {


    private final DataTableTypeUtil dataTableTypeUtil;
    private final IPnPaB2bClient b2bClient;
    private final IPnWebPaClient webClient;
    private final PnPaB2bUtils b2bUtils;
    private final IPnWebRecipientClient webRecipientClient;
    private final PnExternalServiceClientImpl pnExternalServiceClient;
    private final IPnWebUserAttributesClient iPnWebUserAttributesClient;

    private NewNotificationResponse newNotificationResponse;
    private NewNotificationRequest notificationRequest;
    private FullSentNotification notificationResponseComplete;
    private HttpStatusCodeException notificationError;
    public static final String DEFAULT_PA = "Comune_1";
    private String settedPa = "Comune_1";
    private final ObjectMapper objMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private boolean groupToSet = true;

    private String errorCode = null;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${pn.external.api-key-taxID}")
    private String senderTaxId;

    @Value("${pn.external.api-key-2-taxID}")
    private String senderTaxIdTwo;

    @Value("${pn.external.api-key-GA-taxID}")
    private String senderTaxIdGa;

    @Value("${pn.bearer-token.user1.taxID}")
    private String marioCucumberTaxID;

    @Value("${pn.bearer-token.user2.taxID}")
    private String marioGherkinTaxID;

    @Value("${pn.configuration.workflow.wait.millis:31000}")
    private Integer workFlowWait;

    @Value("${pn.configuration.wait.millis:10000}")
    private Integer wait;

    private final Integer workFlowWaitDefault = 31000;
    private final Integer waitDefault = 10000;

    private String gherkinSpaTaxID = "15376371009";
    private String cucumberSrlTaxID = "12345678903";
    private String cucumberSocietyTaxID = "MSSLGU51P10A087J";
    private String cucumberAnalogicTaxID = "SNCLNN65D19Z131V";
    private String gherkinSrltaxId = "CCRMCT06A03A433H";
    private String cucumberSpataxId = "20517490320";

    @Value("${pn.interop.base-url}")
    private String interopBaseUrl;
    @Value("${pn.interop.token-oauth2.path}")
    private String tokenOauth2Path;
    @Value("${pn.interop.token-oauth2.client-assertion}")
    private String clientAssertion;

    @Value("${interop.clientId}")
    private String interopClientId;

    @Value("${pn.external.bearer-token-pg1.id}")
    private String idOrganizationGherkinSrl;
    @Value("${pn.external.bearer-token-pg2.id}")
    private String idOrganizationCucumberSpa;
    private String enableInterop;

    private final PnInteropTokenOauth2Client pnInteropTokenOauth2Client;
    private  String bearerTokenInterop = null;

    @Autowired
    public SharedSteps(DataTableTypeUtil dataTableTypeUtil, IPnPaB2bClient b2bClient,
                       PnPaB2bUtils b2bUtils, IPnWebRecipientClient webRecipientClient,
                       PnExternalServiceClientImpl pnExternalServiceClient,
                       IPnWebUserAttributesClient iPnWebUserAttributesClient, IPnWebPaClient webClient,
                       @Value("${pn.interop.enable}") String enableInterop,
                       PnInteropTokenOauth2Client pnInteropTokenOauth2Client) {
        this.dataTableTypeUtil = dataTableTypeUtil;
        this.b2bClient = b2bClient;
        this.webClient = webClient;
        this.b2bUtils = b2bUtils;
        this.webRecipientClient = webRecipientClient;
        this.pnExternalServiceClient = pnExternalServiceClient;
        this.iPnWebUserAttributesClient = iPnWebUserAttributesClient;
        this.enableInterop = enableInterop;
        this.pnInteropTokenOauth2Client = pnInteropTokenOauth2Client;

        if ("true".equalsIgnoreCase(enableInterop)) {
            this.bearerTokenInterop = pnInteropTokenOauth2Client.getBearerToken();
        }
    }

    @BeforeAll
    public static void before_all() {
        logger.debug("SHARED_GLUE START");
        //only for class activation
    }

    @Given("viene generata una nuova notifica")
    public void vieneGenerataUnaNotifica(@Transpose NewNotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
    }


    @And("destinatario")
    public void destinatario(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(recipient);
    }


    @And("destinatario Mario Cucumber")
    public void destinatarioMarioCucumber() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Mario Cucumber")
                        .taxId(marioCucumberTaxID)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address("notifichedigitali-uat@pec.pagopa.it")));
    }

    @And("destinatario Mario Cucumber e:")
    public void destinatarioMarioCucumberParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Mario Cucumber")
                        .taxId(marioCucumberTaxID));
    }


    @And("destinatario Mario Gherkin")
    public void destinatarioMarioGherkin() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Mario Gherkin")
                        .taxId(marioGherkinTaxID)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address("notifichedigitali-uat@pec.pagopa.it")));
    }

    @And("destinatario Mario Gherkin e:")
    public void destinatarioMarioGherkinParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Mario Gherkin")
                        .taxId(marioGherkinTaxID));
    }

    @And("destinatario Gherkin spa")
    public void destinatarioGherkinSpa() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Gherkin_spa")
                        .taxId(gherkinSpaTaxID)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address("notifichedigitali-uat@pec.pagopa.it")));
    }

    @And("destinatario GherkinSrl")
    public void destinatarioPg1() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("GherkinSrl")
                        .taxId(gherkinSrltaxId)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address("notifichedigitali-uat@pec.pagopa.it")));
    }

    @And("destinatario GherkinSrl e:")
    public void destinatarioPg1param(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("GherkinSrl")
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .taxId(gherkinSrltaxId));
    }

    @And("destinatario CucumberSpa")
    public void destinatarioPg2() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("CucumberSpa")
                        .taxId(cucumberSpataxId)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address("notifichedigitali-uat@pec.pagopa.it")));
    }
    

    @And("destinatario Gherkin spa e:")
    public void destinatarioGherkinSpaParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("GherkinSpa")
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .taxId(gherkinSpaTaxID));
    }

    @And("destinatario Cucumber srl")
    public void destinatarioCucumberSrl() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("CucumberSrl")
                        .taxId(cucumberSrlTaxID)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address("notifichedigitali-uat@pec.pagopa.it")));
    }

    @And("destinatario Cucumber srl e:")
    public void destinatarioCucumberSrlParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("CucumberSrl")
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .taxId(cucumberSrlTaxID));
    }

    @And("destinatario Cucumber Society")
    public void destinatarioCucumberSociety() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Cucumber_Society")
                        .taxId(cucumberSocietyTaxID)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address("notifichedigitali-uat@pec.pagopa.it")));
    }

    @And("destinatario Cucumber Society e:")
    public void destinatarioCucumberSocietyParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Cucumber_Society")
                        .taxId(cucumberSocietyTaxID)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG));
    }

    @And("destinatario Cucumber Analogic e:")
    public void destinatarioCucumberAnalogicParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Cucumber Analogic")
                        .taxId(cucumberAnalogicTaxID));
    }


    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEDiversoCodiceAvviso() {
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();

        generateNewNotification();

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
    }

    @And("destinatario {string} con uguale codice avviso del destinario numero {int}")
    public void destinatarioConUgualeCodiceAvvisoDelDestinarioN(String recipientName, int recipientNumber, @Transpose NotificationRecipient recipient) {
        Assertions.assertDoesNotThrow(() -> notificationRequest.getRecipients().get(recipientNumber - 1).getPayment());
        String noticeCode = notificationRequest.getRecipients().get(recipientNumber - 1).getPayment().getNoticeCode();

        if (recipientName.trim().equalsIgnoreCase("mario cucumber")) {
            recipient = (recipient.denomination("Mario Cucumber")
                    .taxId(marioCucumberTaxID));
        } else if (recipientName.trim().equalsIgnoreCase("mario gherkin")) {
            recipient = (recipient.denomination("Mario Gherkin")
                    .taxId(marioGherkinTaxID));

        } else {
            throw new IllegalArgumentException();
        }

        recipient.getPayment().setNoticeCode(noticeCode);
        this.notificationRequest.addRecipientsItem(recipient);
    }

    @Then("viene generata una nuova notifica valida con uguale codice fiscale del creditore e uguale codice avviso")
    public void vieneGenerataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEUgualeCodiceAvvisoConTaxIdCorretto() {
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();
        String noticeCode = notificationRequest.getRecipients().get(0).getPayment().getNoticeCode();

        this.notificationRequest = (dataTableTypeUtil.convertNotificationRequest(new HashMap<>())
                .addRecipientsItem(dataTableTypeUtil.convertNotificationRecipient(new HashMap<>()).taxId(marioCucumberTaxID)));

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
        this.notificationRequest.getRecipients().get(0).getPayment().setNoticeCode(noticeCode);
    }

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEUgualeCodiceAvviso() {
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();
        String noticeCode = notificationRequest.getRecipients().get(0).getPayment().getNoticeCode();

        generateNewNotification();

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
        this.notificationRequest.getRecipients().get(0).getPayment().setNoticeCode(noticeCode);
    }

    @And("viene configurato noticeCodeAlternative uguale a noticeCode")
    public void vieneConfiguratoNoticeCodeAlternativeUgualeNoticeCode() {
        this.notificationRequest.getRecipients().get(0).getPayment().setNoticeCodeAlternative(this.notificationRequest.getRecipients().get(0).getPayment().getNoticeCode());
    }

    @And("viene configurato noticeCodeAlternative diversi a noticeCode")
    public void vieneConfiguratoNoticeCodeAlternativeDiversiNoticeCode() {
        this.notificationRequest.getRecipients().get(0).getPayment().setNoticeCodeAlternative(getValue(new HashMap<>(), PAYMENT_NOTICE_CODE_OPTIONAL.key));
    }

    @And("viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken {string}")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualePaProtocolNumberEIdempotenceToken(String idempotenceToken) {
        String paProtocolNumber = notificationRequest.getPaProtocolNumber();

        generateNewNotification();

        this.notificationRequest.setIdempotenceToken(idempotenceToken);
        this.notificationRequest.setPaProtocolNumber(paProtocolNumber);
    }

    @And("viene generata una nuova notifica con uguale paProtocolNumber")
    public void vieneGenerataUnaNuovaNotificaConUgualePaProtocolNumber() {
        String paProtocolNumber = notificationRequest.getPaProtocolNumber();

        generateNewNotification();

        this.notificationRequest.setPaProtocolNumber(paProtocolNumber);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataOk(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotification();
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataRefused(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationRefused();
    }

    @When("la notifica viene inviata tramite api b2b senza preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataSenzaPreloadAllegato(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationWithErrorNotFindAllegato();
    }


    @When("la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataOk() {
        sendNotification();
    }

    @When("la notifica viene inviata dal {string}")
    public void laNotificaVieneInviataDallaPA(String pa) {
        selectPA(pa);
        setSenderTaxIdFromProperties();
        sendNotificationWithError();
    }

    @When("la notifica viene inviata tramite api b2b")
    public void laNotificaVieneInviatatramiteApiB2b() {
        sendNotificationWithError();
    }

    @When("la notifica viene inviata tramite api b2b senza preload allegato dal {string}")
    public void laNotificaVieneInviatatramiteApiB2bSenzaPreloadAllegato(String pa) {
        selectPA(pa);
        setSenderTaxIdFromProperties();
        sendNotificationWithErrorNotFindAllegato();
    }

    private void sendNotification() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                newNotificationResponse = b2bUtils.uploadNotification(notificationRequest);
                notificationResponseComplete = b2bUtils.waitForRequestAcceptation(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(notificationResponseComplete);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void sendNotificationWithError() {
        try {
            this.newNotificationResponse = b2bUtils.uploadNotification(notificationRequest);
        } catch (HttpStatusCodeException | IOException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    private void sendNotificationWithErrorNotFindAllegato() {

        try {
            Assertions.assertDoesNotThrow(() -> {
                newNotificationResponse = b2bUtils.uploadNotificationNotFindAllegato(notificationRequest);
                errorCode = b2bUtils.waitForRequestRefused(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }

            Assertions.assertNotNull(errorCode);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    private void sendNotificationRefused() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                newNotificationResponse = b2bUtils.uploadNotification(notificationRequest);
                errorCode = b2bUtils.waitForRequestRefused(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(errorCode);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    private void generateNewNotification() {
        assert this.notificationRequest.getRecipients().get(0).getPayment() != null;
        this.notificationRequest = (dataTableTypeUtil.convertNotificationRequest(new HashMap<>())
                .subject(notificationRequest.getSubject())
                .senderDenomination(notificationRequest.getSenderDenomination())
                .addRecipientsItem(dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination(notificationRequest.getRecipients().get(0).getDenomination())
                        .taxId(notificationRequest.getRecipients().get(0).getTaxId())
                        .recipientType(notificationRequest.getRecipients().get(0).getRecipientType())));

    }


    public void setNewNotificationResponse(NewNotificationResponse newNotificationResponse) {
        this.newNotificationResponse = newNotificationResponse;
    }

    public NewNotificationResponse getNewNotificationResponse() {
        return newNotificationResponse;
    }

    public NewNotificationRequest getNotificationRequest() {
        return notificationRequest;
    }

    public HttpStatusCodeException consumeNotificationError() {
        HttpStatusCodeException value = notificationError;
        this.notificationError = null;
        return value;
    }

    public void setNotificationError(HttpStatusCodeException notificationError) {
        this.notificationError = notificationError;
    }

    public void setSenderTaxIdFromProperties() {
        switch (settedPa) {
            case "Comune_1":
                this.notificationRequest.setSenderTaxId(this.senderTaxId);
                setGrup(SettableApiKey.ApiKeyType.MVP_1);
                break;
            case "Comune_2":
                this.notificationRequest.setSenderTaxId(this.senderTaxIdTwo);
                setGrup(SettableApiKey.ApiKeyType.MVP_2);
                break;
            case "Comune_Multi":
                this.notificationRequest.setSenderTaxId(this.senderTaxIdGa);
                setGrup(SettableApiKey.ApiKeyType.GA);
                break;
        }

    }

    public String getSenderTaxIdFromProperties(String settedPa) {
        switch (settedPa) {
            case "Comune_1":
                return this.senderTaxId;
            case "Comune_2":
                return this.senderTaxIdTwo;
            case "Comune_Multi":
                return this.senderTaxIdGa;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void setGrup(SettableApiKey.ApiKeyType apiKeyType) {
        if (groupToSet && this.notificationRequest.getGroup() == null) {
            List<HashMap<String, String>> hashMapsList = pnExternalServiceClient.paGroupInfo(apiKeyType, bearerTokenInterop);
            if (hashMapsList == null || hashMapsList.size() == 0) return;
            String id = null;
            for (HashMap<String, String> elem : hashMapsList) {
                if (elem.get("status").equalsIgnoreCase("ACTIVE")) {
                    id = elem.get("id");
                    break;
                }
            }
            if (id == null) return;
            this.notificationRequest.setGroup(id);
        }
    }

    public FullSentNotification getSentNotification() {
        return notificationResponseComplete;
    }

    public void setNotificationRequest(NewNotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
    }

    public void setSentNotification(FullSentNotification notificationResponseComplete) {
        this.notificationResponseComplete = notificationResponseComplete;
    }

    public void selectPA(String apiKey) {
        switch (apiKey) {
            case "Comune_1":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_1);
                break;
            case "Comune_2":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_2);
                break;
            case "Comune_Multi":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.GA);
                break;
            default:
                throw new IllegalArgumentException();
        }
        this.b2bUtils.setClient(b2bClient);
        this.settedPa = apiKey;
    }

    public void selectUser(String recipient) {
        switch (recipient.trim().toLowerCase()){
            case "mario cucumber":
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                break;
            case "mario gherkin":
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                break;
            case "gherkinsrl":
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                break;
            case "cucumberspa":
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public IPnPaB2bClient getB2bClient() {
        return b2bClient;
    }

    public IPnWebPaClient getWebPaClient() {
        return webClient;
    }

    public PnPaB2bUtils getB2bUtils() {
        return b2bUtils;
    }

    public IPnWebRecipientClient getWebRecipientClient() {
        return webRecipientClient;
    }

    public String getMarioCucumberTaxID() {
        return marioCucumberTaxID;
    }

    public String getMarioGherkinTaxID() {
        return marioGherkinTaxID;
    }

    public String getGherkinSrltaxId() {
        return gherkinSrltaxId;
    }

    public String getCucumberSpataxId() {
        return cucumberSpataxId;
    }

    public PnExternalServiceClientImpl getPnExternalServiceClient() {
        return pnExternalServiceClient;
    }

    public void throwAssertFailerWithIUN(AssertionFailedError assertionFailedError) {
        String message = assertionFailedError.getMessage() +
                "{IUN: " + notificationResponseComplete.getIun() + " }";
        throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
    }

    public <T> T deepCopy(Object obj, Class<T> toClass) {
        try {
            String json = objMapper.writeValueAsString(obj);
            return objMapper.readValue(json, toClass);
        } catch (JsonProcessingException exc) {
            throw new RuntimeException(exc);
        }
    }


    public Integer getWorkFlowWait() {
        if (workFlowWait == null) return workFlowWaitDefault;
        return workFlowWait;
    }

    public Integer getWait() {
        if (wait == null) return waitDefault;
        return wait;
    }

    @Before("@integrationTest")
    public void doSomethingAfter() {
        this.groupToSet = false;
    }

    public List<HashMap<String, String>> getGroupsByPa(String settedPa) {
        List<HashMap<String, String>> hashMapsList = null;
        switch (settedPa) {
            case "Comune_1":
                hashMapsList = this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.MVP_1, bearerTokenInterop);
                break;
            case "Comune_2":
                hashMapsList = this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.MVP_2, bearerTokenInterop);
                break;
            case "Comune_Multi":
                hashMapsList = this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.GA, bearerTokenInterop);
                break;
            default:
                throw new IllegalArgumentException();
        }

        Assertions.assertNotNull(hashMapsList);
        Assertions.assertTrue(hashMapsList.size() > 0);
        return hashMapsList;
    }

    public String getGroupIdByPa(String settedPa, GroupPosition position) {
        List<HashMap<String, String>> hashMapsList = getGroupsByPa(settedPa);

        String id = null;
        Integer count = 0;
        for (HashMap<String, String> elem : hashMapsList) {
            if (elem.get("status").equalsIgnoreCase("ACTIVE")) {
                id = elem.get("id");
                count++;
                if (GroupPosition.FIRST.equals(position)) {
                    break;
                }
            }
        }

        Assertions.assertNotNull(id);
        if (!GroupPosition.FIRST.equals(position)) {
            Assertions.assertTrue(count >= 2);
        }

        return id;
    }

    @And("viene rimossa se presente la pec di piattaforma di {string}")
    public void vieneRimossaSePresenteLaPecDiPiattaformaDi(String user) {
        selectUser(user);
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
                this.iPnWebUserAttributesClient.deleteRecipientLegalAddress("default", LegalChannelType.PEC);
                logger.info("PEC FOUND AND DELETED");
            }
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
                logger.info("PEC NOT FOUND");
            } else {
                throw httpStatusCodeException;
            }
        }

    }

    @Then("si verifica che la notifica non viene accettata causa {string}")
    public void verificaNotificaNoAccept(String causa) {
        switch (causa) {
            case "ALLEGATO":
                Assertions.assertTrue("FILE_NOTFOUND".equalsIgnoreCase(errorCode));
                break;
            case "TAX_ID":
                Assertions.assertTrue("TAXID_NOT_VALID".equalsIgnoreCase(errorCode));
                break;
            default:
                throw new IllegalArgumentException();
        }

    }


    @Before
    public void injectScenarioNameInsideSfl4jMdc(Scenario scenario) {
        String scenarioName = scenario.getName();
        MDC.put( RestTemplateConfiguration.CUCUMBER_SCENARIO_NAME_MDC_ENTRY, scenarioName );
    }


    public String getIdOrganizationGherkinSrl() {
        return idOrganizationGherkinSrl;
    }

    public void setIdOrganizationGherkinSrl(String idOrganizationGherkinSrl) {
        this.idOrganizationGherkinSrl = idOrganizationGherkinSrl;
    }

    public String getIdOrganizationCucumberSpa() {
        return idOrganizationCucumberSpa;
    }

    public void setIdOrganizationCucumberSpa(String idOrganizationCucumberSpa) {
        this.idOrganizationCucumberSpa = idOrganizationCucumberSpa;
    }

}
