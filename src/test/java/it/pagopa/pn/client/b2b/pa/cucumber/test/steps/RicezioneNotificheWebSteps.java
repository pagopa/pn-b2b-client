package it.pagopa.pn.client.b2b.pa.cucumber.test.steps;



import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.PnWebRecipientExternalClientImpl;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.FullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;


public class RicezioneNotificheWebSteps {

    @Autowired
    private PnWebRecipientExternalClientImpl pnWebRecipientExternalClient;

    @Autowired
    private PnPaB2bUtils b2bUtils;

    @Autowired
    private DataTableTypeUtil dataTableTypeUtil;

    private NewNotificationRequest notificationRequest;
    private FullSentNotification notificationResponseComplete;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());



    @Given("viene generata una notifica per il test di ricezione")
    public void vieneGenerataUnaNotificaPerIlTestDiRicezione(@Transpose NewNotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
    }

    @And("destinatario Cristoforo Colombo")
    public void destinatarioCristoforoColombo() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<String,String>())
                        .denomination("Cristoforo Colombo")
                        .taxId("CLMCST42R12D969Z")
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC )
                                .address("CLMCST42R12D969Z@pnpagopa.postecert.local")));
    }

    @And("destinatario Cristoforo Colombo and:")
    public void destinatarioCristoforoColomboParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Cristoforo Colombo")
                        .taxId("CLMCST42R12D969Z")
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC )
                                .address("CLMCST42R12D969Z@pnpagopa.postecert.local")));
    }

    @When("la notifica viene inviata e si riceve il relativo codice IUN valorizzato")
    public void laNotificaVieneInviataESiRiceveIlRelativoCodiceIUNValorizzato() {
        Assertions.assertDoesNotThrow(() -> {
            NewNotificationResponse newNotificationRequest = b2bUtils.uploadNotification(notificationRequest);
            notificationResponseComplete = b2bUtils.waitForRequestAcceptation( newNotificationRequest );
        });
        try {
            Thread.sleep( 10 * 1000);
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
    }

    @Then("la notifica può essere correttamente recuperata dal destinatario")
    public void laNotificaPuòEssereCorrettamenteRecuperataDalDestinatario() {
        Assertions.assertDoesNotThrow(() -> {
            FullReceivedNotification receivedNotification =
                    pnWebRecipientExternalClient.getReceivedNotification(notificationResponseComplete.getIun(), null);
        });
    }


    @Then("il documento notificato può essere correttamente recuperato")
    public void ilDocumentoNotificatoPuòEssereCorrettamenteRecuperato() {
        NotificationAttachmentDownloadMetadataResponse downloadResponse = pnWebRecipientExternalClient.getReceivedNotificationDocument(
                notificationResponseComplete.getIun(),
                new BigDecimal(notificationResponseComplete.getDocuments().get(0).getDocIdx()),
                null
        );
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(),downloadResponse.getSha256());
    }


    @Then("l'allegato di pagamento può essere correttamente recuperato")
    public void lAllegatoDiPagamentoPuòEssereCorrettamenteRecuperato() {
        NotificationAttachmentDownloadMetadataResponse downloadResponse = pnWebRecipientExternalClient.getReceivedNotificationAttachment(
                notificationResponseComplete.getIun(),
                "PAGOPA",
                null);
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(),downloadResponse.getSha256());
    }
}
