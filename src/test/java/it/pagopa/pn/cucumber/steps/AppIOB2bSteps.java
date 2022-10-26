package it.pagopa.pn.cucumber.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.FullReceivedNotification;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDocument;
import it.pagopa.pn.client.b2b.pa.testclient.IPnAppIOB2bClient;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AppIOB2bSteps {

    @Autowired
    private IPnAppIOB2bClient iPnAppIOB2bClient;

    @Autowired
    private GenerazioneInvioNotificaB2bSteps notificationGlue;

    @Autowired
    private PnPaB2bUtils b2bUtils;

    private HttpServerErrorException notficationServerError;
    private String sha256DocumentDownload;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Then("la notifica può essere recuperata tramite AppIO")
    public void laNotificaPuòEssereRecuperataTramiteAppIO() {
        AtomicReference<FullReceivedNotification> notificationByIun = new AtomicReference<>();

        Assertions.assertDoesNotThrow(() ->
                notificationByIun.set(this.iPnAppIOB2bClient.getReceivedNotification(notificationGlue.getSentNotification().getIun(),
                        notificationGlue.getSentNotification().getRecipients().get(0).getTaxId()))
        );
        Assertions.assertNotNull(notificationByIun.get());
    }

    @Then("il documento notificato può essere recuperata tramite AppIO")
    public void ilDocumentoNotificatoPuòEssereRecuperataTramiteAppIO() {
        List<NotificationDocument> documents = notificationGlue.getSentNotification().getDocuments();
        it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                iPnAppIOB2bClient.getSentNotificationDocument(notificationGlue.getSentNotification().getIun(), Integer.parseInt(documents.get(0).getDocIdx()),
                        notificationGlue.getSentNotification().getRecipients().get(0).getTaxId());

        byte[] bytes = Assertions.assertDoesNotThrow(() ->
                b2bUtils.downloadFile(sentNotificationDocument.getUrl()));
        this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

        Assertions.assertEquals(this.sha256DocumentDownload,sentNotificationDocument.getSha256());
    }

    @And("si tenta il recupero della notifica tramite AppIO")
    public void siTentaIlRecuperoDellaNotificaTramiteAppIO() {
        try {
            this.iPnAppIOB2bClient.getReceivedNotification(notificationGlue.getSentNotification().getIun(), "FRMTTR76M06B715E");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e instanceof HttpServerErrorException) {
                this.notficationServerError = (HttpServerErrorException) e;
            }
        }
    }

    @Then("il tentativo di recupero con appIO ha prodotto un errore con status code {string}")
    public void ilTentativoDiRecuperoHaProdottoUnErroreConStatusCode(String statusCode) {
        Assertions.assertTrue((this.notficationServerError != null) &&
                (this.notficationServerError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }
}
