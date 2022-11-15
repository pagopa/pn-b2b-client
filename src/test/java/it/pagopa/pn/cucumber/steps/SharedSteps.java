package it.pagopa.pn.cucumber.steps;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SharedSteps {

    private final DataTableTypeUtil dataTableTypeUtil;
    private final IPnPaB2bClient b2bClient;
    private final PnPaB2bUtils b2bUtils;

    private NewNotificationResponse newNotificationResponse;
    private NewNotificationRequest notificationRequest;
    private FullSentNotification notificationResponseComplete;
    public static final String DEFAULT_PA = "MVP_1";
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public SharedSteps(DataTableTypeUtil dataTableTypeUtil, IPnPaB2bClient b2bClient, PnPaB2bUtils b2bUtils) {
        this.dataTableTypeUtil = dataTableTypeUtil;
        this.b2bClient = b2bClient;
        this.b2bUtils = b2bUtils;
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

    @And("destinatario Cristoforo Colombo")
    public void destinatarioCristoforoColombo() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Cristoforo Colombo")
                        .taxId("CLMCST42R12D969Z")
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC )
                                .address("CLMCST42R12D969Z@pnpagopa.postecert.local")));
    }

    @And("destinatario Cristoforo Colombo e:")
    public void destinatarioCristoforoColomboParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Cristoforo Colombo")
                        .taxId("CLMCST42R12D969Z")
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC )
                                .address("CLMCST42R12D969Z@pnpagopa.postecert.local")));
    }


    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEDiversoCodiceAvviso() {
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();

        generateNewNotification();

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
    }

    @And("destinatario con uguale codice avviso del destinario numero {int}")
    public void destinatarioConUgualeCodiceAvvisoDelDestinarioN(int recipientNumber, @Transpose NotificationRecipient recipient) {
        Assertions.assertDoesNotThrow(()->notificationRequest.getRecipients().get(recipientNumber-1).getPayment());
        String noticeCode = notificationRequest.getRecipients().get(recipientNumber-1).getPayment().getNoticeCode();

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

    @When("la notifica viene inviata tramite api b2b dalla PA {string} e si attende che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataOk(String paType) {
        selectPA(paType);
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
        selectPA(DEFAULT_PA);
    }

    @When("la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataOk() {
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
            case "MVP_1":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_1);
                break;
            case "MVP_2":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_2);
                break;
            case "GA":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.GA);
                break;
            default:
                throw new IllegalArgumentException();
        }
        this.b2bUtils.setClient(b2bClient);
    }

    public IPnPaB2bClient getB2bClient() {
        return b2bClient;
    }

    public PnPaB2bUtils getB2bUtils() {
        return b2bUtils;
    }

}
