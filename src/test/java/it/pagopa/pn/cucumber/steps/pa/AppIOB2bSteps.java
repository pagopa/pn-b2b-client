package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.IOReceivedNotification;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyMessage;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDocument;
import it.pagopa.pn.client.b2b.pa.testclient.IPnAppIOB2bClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AppIOB2bSteps {


    private final IPnAppIOB2bClient iPnAppIOB2bClient;
    private final SharedSteps sharedSteps;
    private final PnPaB2bUtils b2bUtils;

    private HttpStatusCodeException notficationServerError;
    private String sha256DocumentDownload;
    private final String marioCucumberTaxID;
    private final String marioGherkinTaxID;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public AppIOB2bSteps(IPnAppIOB2bClient iPnAppIOB2bClient, SharedSteps sharedSteps) {
        this.iPnAppIOB2bClient = iPnAppIOB2bClient;
        this.sharedSteps = sharedSteps;
        this.b2bUtils = sharedSteps.getB2bUtils();

        this.marioCucumberTaxID = sharedSteps.getMarioCucumberTaxID();
        this.marioGherkinTaxID = sharedSteps.getMarioGherkinTaxID();
    }


    @Then("la notifica può essere recuperata tramite AppIO")
    public void notificationCanBeRetrievedAppIO() {
        AtomicReference<ThirdPartyMessage> notificationByIun = new AtomicReference<>();
        try{
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(this.iPnAppIOB2bClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(),
                            sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()))
            );
            Assertions.assertNotNull(notificationByIun.get());
        }catch(AssertionFailedError assertionFailedError){
                sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }

    }

    @Then("il documento notificato può essere recuperata tramite AppIO")
    public void notifiedDocumentCanBeRetrievedAppIO() {
        List<NotificationDocument> documents = sharedSteps.getSentNotification().getDocuments();
        it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                iPnAppIOB2bClient.getSentNotificationDocument(sharedSteps.getSentNotification().getIun(), Integer.parseInt(documents.get(0).getDocIdx()),
                        sharedSteps.getSentNotification().getRecipients().get(0).getTaxId());
        try {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(sentNotificationDocument.getUrl()));
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, sentNotificationDocument.getSha256());
        }catch (AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("{string} tenta il recupero della notifica tramite AppIO")
    public void attemptsNotificationRetrievalAppIO(String recipient) {
        try {
            this.iPnAppIOB2bClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), selectTaxIdUser(recipient));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notficationServerError = e;
            }
        }
    }

    @Then("il tentativo di recupero con appIO ha prodotto un errore con status code {string}")
    public void retrievalAttemptWithAppIOProducedAnErrorWithStatusCode(String statusCode) {
        Assertions.assertTrue((this.notficationServerError != null) &&
                (this.notficationServerError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }

    private String selectTaxIdUser(String recipient){
        if(recipient.trim().equalsIgnoreCase("mario cucumber")){
            return this.marioCucumberTaxID;
        } else if (recipient.trim().equalsIgnoreCase("mario gherkin")){
            return this.marioGherkinTaxID;
        }else{
            throw new IllegalArgumentException();
        }
    }


    @Then("{string} recupera la notifica tramite AppIO")
    public void recuperaLaNotificaTramiteAppIO(String recipient) {
        AtomicReference<ThirdPartyMessage> notificationByIun = new AtomicReference<>();
        try{
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(this.iPnAppIOB2bClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(),
                            selectTaxIdUser(recipient)))
            );
            Assertions.assertNotNull(notificationByIun.get());
        }catch(AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("{string} recupera il documento notificato tramite AppIO")
    public void recuperaIlDocumentoNotificatoTramiteAppIO(String recipient) {
        List<NotificationDocument> documents = sharedSteps.getSentNotification().getDocuments();
        it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                iPnAppIOB2bClient.getSentNotificationDocument(sharedSteps.getSentNotification().getIun(), Integer.parseInt(documents.get(0).getDocIdx()),
                        selectTaxIdUser(recipient));
        try {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(sentNotificationDocument.getUrl()));
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, sentNotificationDocument.getSha256());
        }catch (AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }
}
