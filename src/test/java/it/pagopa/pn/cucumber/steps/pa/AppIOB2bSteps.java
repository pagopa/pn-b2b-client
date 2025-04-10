package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyAttachment;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyMessage;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDocument;
import it.pagopa.pn.client.b2b.pa.service.IPnAppIOB2bClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Costanti.*;


@Slf4j
public class AppIOB2bSteps {
    private final IPnAppIOB2bClient iPnAppIOB2bClient;
    private final SharedSteps sharedSteps;
    private final PnPaB2bUtils b2bUtils;
    private HttpStatusCodeException notificationServerError;
    private String sha256DocumentDownload;


    @Autowired
    public AppIOB2bSteps(IPnAppIOB2bClient iPnAppIOB2bClient, SharedSteps sharedSteps) {
        this.iPnAppIOB2bClient = iPnAppIOB2bClient;
        this.sharedSteps = sharedSteps;
        this.b2bUtils = sharedSteps.getB2bUtils();
    }

    @Then("la notifica può essere recuperata tramite AppIO")
    public void notificationCanBeRetrievedAppIO() {
        AtomicReference<ThirdPartyMessage> notificationByIun = new AtomicReference<>();
        try {
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(this.iPnAppIOB2bClient.getReceivedNotification(fullSentNotification.getIun(), fullSentNotification.getRecipients().get(0).getTaxId())));
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Then("il documento notificato può essere recuperata tramite AppIO")
    public void notifiedDocumentCanBeRetrievedAppIO() {
        FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<NotificationDocument> documents = fullSentNotification.getDocuments();
        it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                iPnAppIOB2bClient.getSentNotificationDocument(fullSentNotification.getIun(), Integer.parseInt(documents.get(0).getDocIdx()),
                        fullSentNotification.getRecipients().get(0).getTaxId());
        try {
            byte[] bytes = Assertions.assertDoesNotThrow(() -> b2bUtils.downloadFile(sentNotificationDocument.getUrl()));
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, sentNotificationDocument.getSha256());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Then("il documento di pagamento {string} può essere recuperata tramite AppIO")
    public void notifiedDocumentPaymentCanBeRetrievedAppIO(String typeDocument) {
        FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<NotificationDocument> documents = fullSentNotification.getDocuments();
        it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                iPnAppIOB2bClient.getReceivedNotificationAttachment(fullSentNotification.getIun(), typeDocument, fullSentNotification.getRecipients().get(0).getTaxId(), Integer.parseInt(documents.get(0).getDocIdx()));
        try {
            byte[] bytes = Assertions.assertDoesNotThrow(() -> b2bUtils.downloadFile(sentNotificationDocument.getUrl()));
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, sentNotificationDocument.getSha256());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Then("il documento di pagamento {string} può essere recuperata tramite AppIO da {string}")
    public void paymentDocumentCanBeRetrievedAppIO(String typeDocument, String recipient) {
        if ("F24".equalsIgnoreCase(typeDocument)) {
            downloadF24AppIo(typeDocument, recipient);
        } else if ("PAGOPA".equalsIgnoreCase(typeDocument)) {
            downloadPAGOPAAppIo(typeDocument, recipient);
        }
    }

    public void downloadPAGOPAAppIo(String typeDocument, String recipient) {
        try {
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            List<NotificationDocument> documents = fullSentNotification.getDocuments();
            it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse downloadResponse =
                    iPnAppIOB2bClient.getReceivedNotificationAttachment(fullSentNotification.getIun(), typeDocument, selectTaxIdUser(recipient), Integer.parseInt(documents.get(0).getDocIdx()));

            if ((downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0)) {
                try {
                    System.out.println("SECONDO TENTATIVO");
                    Thread.sleep(downloadResponse.getRetryAfter() * 3);
                    downloadResponse = iPnAppIOB2bClient.getReceivedNotificationAttachment(fullSentNotification.getIun(), typeDocument, selectTaxIdUser(recipient), Integer.parseInt(documents.get(0).getDocIdx()));

                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
            }
            System.out.println(downloadResponse.toString());

            it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse downloadResponseFinal = downloadResponse;
            byte[] bytes = Assertions.assertDoesNotThrow(() -> b2bUtils.downloadFile(downloadResponseFinal.getUrl()));
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, downloadResponse.getSha256());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            this.notificationServerError = e;
        }
    }

    public void downloadF24AppIo(String typeDocument, String recipient) {

        AtomicReference<ThirdPartyMessage> notificationByIun = new AtomicReference<>();
        try {
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(this.iPnAppIOB2bClient.getReceivedNotification(fullSentNotification.getIun(), fullSentNotification.getRecipients().get(0).getTaxId())));
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
        String url = null;
        for (ThirdPartyAttachment tmpAtt : notificationByIun.get().getAttachments()) {
            if (typeDocument.equalsIgnoreCase(tmpAtt.getCategory().getValue())) {
                url = tmpAtt.getUrl();
                System.out.println(notificationByIun.get().getAttachments().get(0).getUrl());
                break;
            }
        }

        Assertions.assertNotNull(url);

        try {
            it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse downloadResponse =
                    iPnAppIOB2bClient.getReceivedNotificationAttachmentByUrl(url, selectTaxIdUser(recipient));

            if ((downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0)) {
                try {
                    System.out.println("SECONDO TENTATIVO");
                    Thread.sleep(downloadResponse.getRetryAfter() * 3);
                    downloadResponse = iPnAppIOB2bClient.getReceivedNotificationAttachmentByUrl(url, selectTaxIdUser(recipient));

                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
            }
            System.out.println(downloadResponse.toString());

            it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse downloadResponseFinal = downloadResponse;
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponseFinal.getUrl()));
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            if (!"F24".equalsIgnoreCase(typeDocument)) {
                Assertions.assertEquals(this.sha256DocumentDownload, downloadResponse.getSha256());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            this.notificationServerError = e;
        }
    }


    @Then("il documento notificato può essere recuperata tramite AppIO da {string}")
    public void notifiedDocumentCanBeRetrievedAppIO(String recipient) {
        try {
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            List<NotificationDocument> documents = fullSentNotification.getDocuments();
            it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                    iPnAppIOB2bClient.getSentNotificationDocument(fullSentNotification.getIun(), Integer.parseInt(documents.get(0).getDocIdx()), selectTaxIdUser(recipient));

            byte[] bytes = Assertions.assertDoesNotThrow(() -> b2bUtils.downloadFile(sentNotificationDocument.getUrl()));
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, sentNotificationDocument.getSha256());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            this.notificationServerError = e;
        }
    }

    @And("{string} tenta il recupero della notifica tramite AppIO")
    public void attemptsNotificationRetrievalAppIO(String recipient) {
        try {
            this.iPnAppIOB2bClient.getReceivedNotification(sharedSteps.getNotificationIun(), selectTaxIdUser(recipient));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            this.notificationServerError = e;
        }
    }

    @Then("il tentativo di recupero con appIO ha prodotto un errore con status code {string}")
    public void retrievalAttemptWithAppIOProducedAnErrorWithStatusCode(String statusCode) {
        Assertions.assertTrue((this.notificationServerError != null) &&
                (this.notificationServerError.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    private String selectTaxIdUser(String recipient) {
        if (recipient.trim().equalsIgnoreCase(MARIO_CUCUMBER)) {
            return MARIO_CUCUMBER_TAX_ID;
        } else if (recipient.trim().equalsIgnoreCase(MARIO_GHERKIN)) {
            return MARIO_GHERKIN_TAX_ID;
        } else {
            throw new IllegalArgumentException("Invalid recipient name: " + recipient);
        }
    }


    @Then("{string} recupera la notifica tramite AppIO")
    public void recuperaLaNotificaTramiteAppIO(String recipient) {
        AtomicReference<ThirdPartyMessage> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(this.iPnAppIOB2bClient.getReceivedNotification(sharedSteps.getNotificationIun(), selectTaxIdUser(recipient))));
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Then("{string} recupera il documento notificato tramite AppIO")
    public void recuperaIlDocumentoNotificatoTramiteAppIO(String recipient) {
        FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<NotificationDocument> documents = fullSentNotification.getDocuments();
        it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                iPnAppIOB2bClient.getSentNotificationDocument(fullSentNotification.getIun(), Integer.parseInt(documents.get(0).getDocIdx()),
                        selectTaxIdUser(recipient));
        try {
            byte[] bytes = Assertions.assertDoesNotThrow(() -> b2bUtils.downloadFile(sentNotificationDocument.getUrl()));
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, sentNotificationDocument.getSha256());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }


}
