package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.testclient.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.testclient.SettableBearerToken;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SharedSteps {


    private final DataTableTypeUtil dataTableTypeUtil;
    private final IPnPaB2bClient b2bClient;
    private final PnPaB2bUtils b2bUtils;
    private final IPnWebRecipientClient webRecipientClient;
    private final PnExternalServiceClientImpl pnExternalServiceClient;

    private NewNotificationResponse newNotificationResponse;
    private NewNotificationRequest notificationRequest;
    private FullSentNotification notificationResponseComplete;
    private HttpStatusCodeException notificationError;
    public static final String DEFAULT_PA = "Comune_1";
    private String settedPa = "Comune_1";
    private final ObjectMapper objMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private boolean groutToSet = true;

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

    private String gherkinSpaTaxID = "15376371009";
    private String cucumberSocietyTaxID = "CUPSOS66D44A199F";

    @Autowired
    public SharedSteps(DataTableTypeUtil dataTableTypeUtil, IPnPaB2bClient b2bClient,
                       PnPaB2bUtils b2bUtils, IPnWebRecipientClient webRecipientClient,
                       PnExternalServiceClientImpl pnExternalServiceClient) {
        this.dataTableTypeUtil = dataTableTypeUtil;
        this.b2bClient = b2bClient;
        this.b2bUtils = b2bUtils;
        this.webRecipientClient = webRecipientClient;
        this.pnExternalServiceClient = pnExternalServiceClient;
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
                                .type(NotificationDigitalAddress.TypeEnum.PEC )
                                .address("testpagopa1@pnpagopa.postecert.local")));
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
                                .type(NotificationDigitalAddress.TypeEnum.PEC )
                                .address("testpagopa1@pnpagopa.postecert.local")));
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
                                .type(NotificationDigitalAddress.TypeEnum.PEC )
                                .address("testpagopa1@pnpagopa.postecert.local")));
    }

    @And("destinatario Gherkin spa e:")
    public void destinatarioGherkinSpaParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Gherkin_spa")
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .taxId(gherkinSpaTaxID));
    }


    @And("destinatario Cucumber Society")
    public void destinatarioCucumberSociety() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Cucumber_Society")
                        .taxId(cucumberSocietyTaxID)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC )
                                .address("testpagopa1@pnpagopa.postecert.local")));
    }

    @And("destinatario Cucumber Society e:")
    public void destinatarioCucumberSocietyParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Cucumber_Society")
                        .taxId(cucumberSocietyTaxID)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG));
    }


    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEDiversoCodiceAvviso() {
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();

        generateNewNotification();

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
    }

    @And("destinatario {string} con uguale codice avviso del destinario numero {int}")
    public void destinatarioConUgualeCodiceAvvisoDelDestinarioN(String recipientName,int recipientNumber, @Transpose NotificationRecipient recipient) {
        Assertions.assertDoesNotThrow(()->notificationRequest.getRecipients().get(recipientNumber-1).getPayment());
        String noticeCode = notificationRequest.getRecipients().get(recipientNumber-1).getPayment().getNoticeCode();

        if(recipientName.trim().equalsIgnoreCase("mario cucumber")){
            recipient = (recipient.denomination("Mario Cucumber")
                    .taxId(marioCucumberTaxID));
        } else if (recipientName.trim().equalsIgnoreCase("mario gherkin")){
            recipient = (recipient.denomination("Mario Gherkin")
                    .taxId(marioGherkinTaxID));

        }else{
            throw new IllegalArgumentException();
        }

        recipient.getPayment().setNoticeCode(noticeCode);
        this.notificationRequest.addRecipientsItem(recipient);
    }

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEUgualeCodiceAvviso() {
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();
        String noticeCode = notificationRequest.getRecipients().get(0).getPayment().getNoticeCode();

        generateNewNotification();

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
        this.notificationRequest.getRecipients().get(0).getPayment().setNoticeCode(noticeCode);
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

    private void sendNotification(){
        try{
            Assertions.assertDoesNotThrow(() -> {
                newNotificationResponse = b2bUtils.uploadNotification(notificationRequest);
                notificationResponseComplete = b2bUtils.waitForRequestAcceptation( newNotificationResponse );
            });

            try {
                Thread.sleep( 10 * 1000);
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(notificationResponseComplete);

        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    "{RequestID: "+ (newNotificationResponse == null ? "NULL": newNotificationResponse.getNotificationRequestId()) +" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }

    private void sendNotificationWithError(){
        try {
            this.newNotificationResponse = b2bUtils.uploadNotification(notificationRequest);
        } catch (HttpStatusCodeException | IOException e) {
            if(e instanceof HttpStatusCodeException){
                this.notificationError = (HttpStatusCodeException)e;
            }
        }
    }


    private void generateNewNotification(){
        assert this.notificationRequest.getRecipients().get(0).getPayment() != null;
        this.notificationRequest = (dataTableTypeUtil.convertNotificationRequest(new HashMap<>())
                .subject(notificationRequest.getSubject())
                .senderDenomination(notificationRequest.getSenderDenomination())
                .addRecipientsItem(dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination(notificationRequest.getRecipients().get(0).getDenomination())
                        .taxId(notificationRequest.getRecipients().get(0).getTaxId())));
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
        HttpStatusCodeException value =  notificationError;
        this.notificationError = null;
        return value;
    }

    public void setNotificationError(HttpStatusCodeException notificationError) {
        this.notificationError = notificationError;
    }

    public void setSenderTaxIdFromProperties() {
        switch (settedPa){
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

    private void setGrup(SettableApiKey.ApiKeyType apiKeyType){
        if(groutToSet && this.notificationRequest.getGroup() == null){
            List<HashMap<String, String>> hashMaps = pnExternalServiceClient.paGroupInfo(apiKeyType);
            if(hashMaps == null || hashMaps.size() == 0)return;
            String id = hashMaps.get(0).get("id");
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
        switch (apiKey){
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

    public void selectUser(String recipient){
        if(recipient.trim().equalsIgnoreCase("mario cucumber")){
            webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
        } else if (recipient.trim().equalsIgnoreCase("mario gherkin")){
            webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
        }else{
            throw new IllegalArgumentException();
        }
    }

    public IPnPaB2bClient getB2bClient() {
        return b2bClient;
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

    public void throwAssertFailerWithIUN(AssertionFailedError assertionFailedError){
        String message = assertionFailedError.getMessage()+
                "{IUN: "+notificationResponseComplete.getIun() +" }";
        throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
    }

    public  <T> T deepCopy( Object obj, Class<T> toClass) {
        try {
            String json = objMapper.writeValueAsString( obj );
            return objMapper.readValue( json, toClass );
        } catch (JsonProcessingException exc ) {
            throw new RuntimeException( exc );
        }
    }


    @Before("@integrationTest")
    public void doSomethingAfter() {
       this.groutToSet = false;
    }

}
