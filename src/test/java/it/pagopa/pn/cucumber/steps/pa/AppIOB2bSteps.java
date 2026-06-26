package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.RequestCheckQrMandateDto;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ResponseCheckQrMandateDto;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyAttachment;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyMessage;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDocument;
import it.pagopa.pn.client.b2b.pa.service.IPnAppIOB2bClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.CUCUMBER_SPA;
import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.MARIO_CUCUMBER;
import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.MARIO_GHERKIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;


@Slf4j
public class AppIOB2bSteps {
    private final IPnAppIOB2bClient iPnAppIOB2bClient;
    private final SharedSteps sharedSteps;
    private HttpStatusCodeException notificationServerError;
    private String sha256DocumentDownload;
    private ResponseCheckQrMandateDto responseCheckAarMandateDto;

    @Value("${pn.appIO.checkQrCode-bodyUrl}")
    private String qrCodeBodyUrl;
    @Value("${pn.appIO.checkQrCodeV2-bodyUrl}")
    private String qrCodeBodyUrlV2;
    private String qrCode;
    @Value("${pn.iun.60gg.fieramosca}")
    private String iun60gg;

    @Autowired
    public AppIOB2bSteps(IPnAppIOB2bClient iPnAppIOB2bClient, SharedSteps sharedSteps) {
        this.iPnAppIOB2bClient = iPnAppIOB2bClient;
        this.sharedSteps = sharedSteps;
    }

    @Given("viene generato il QR Code {string} per la notifica appena creata")
    public void vieneGeneratoIlCodiceQRPerLaNotificaCreata(String qrCodeType) {
        qrCode = switch (qrCodeType.toLowerCase()) {
            case "corretto" -> sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0);
            case "malformato" ->
                    sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0) + "MALF";
            case "esteso" ->
                    sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0) + "&utm_campaign=<XXXX>&utm_source=<YYYYY>&utm_medium=<ZZZZZ>";
            default -> throw new IllegalArgumentException("Valore passato come qrCodeType non valido: " + qrCodeType);
        };
    }

    @Given("viene generato il QR Code {string} per la notifica di 60 giorni")
    public void vieneGeneratoIlCodiceQRPerLaNotificaOld(String qrCodeType) {
        sharedSteps.setNotificationIun(iun60gg);
        qrCode = switch (qrCodeType.toLowerCase()) {
            case "corretto" -> sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0);
            case "malformato" ->
                    sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0) + "MALF";
            default -> throw new IllegalArgumentException("Valore passato come qrCodeType non valido: " + qrCodeType);
        };
    }

    @When("l'utente {destinatario} scansiona il QR Code per recuperare i dettagli della notifica con versione {string}")
    public void userScanQRCodeWithoutLollipopHeader(Destinatario user, String qrCodeBodyUrlVersion) {
        userScanQrCode(user, user.getTaxId(), qrCodeBodyUrlVersion);
    }

    @When("l'utente {destinatario} scansiona il QR Code per recuperare i dettagli della notifica e viene passato l'header lollipop")
    public void userScanQRCodeWithLollipopHeader(Destinatario user) {
        userScanQrCode(user, "CLMCST42R12D969Z", "0.9");
    }

    private void userScanQrCode(Destinatario user, String xPagopaLollipopUserId, String qrCodeBodyUrlVersion) {

        String qrCodeBodyUrlTmp = qrCodeBodyUrlVersion.equalsIgnoreCase("1.0")
                ? qrCodeBodyUrlV2
                : qrCodeBodyUrl;

        RequestCheckQrMandateDto requestCheckAarMandateDto = new RequestCheckQrMandateDto().aarQrCodeValue(qrCodeBodyUrlTmp + qrCode);
        try {
            responseCheckAarMandateDto = iPnAppIOB2bClient.checkAarQrCodeIO(user.getTaxId(), xPagopaLollipopUserId, requestCheckAarMandateDto);
        } catch (HttpStatusCodeException ex) {
            notificationServerError = ex;
        }
    }

    @When("l'operazione non ha prodotti errori")
    public void checkExeption() {
        assertNull(notificationServerError,
                "notificationServerError non deve essere valorizzato, ma vale: " + notificationServerError);
    }

    @When("viene chiamato l'endpoint {string} con i seguenti params:")
    public void callScanQRCodeWithParams(String endpoint, DataTable dataTable) {
        Map<String, String> inputParams = dataTable.asMap();
        try {
            switch (endpoint) {
                case "checkQRCode" ->
                        iPnAppIOB2bClient.checkAarQrCodeIO(inputParams.get("taxId"), new RequestCheckQrMandateDto().aarQrCodeValue(inputParams.get("aarQrCodeValue")));
                case "getReceivedNotification" ->
                        iPnAppIOB2bClient.getReceivedNotification(inputParams.get("iun"), inputParams.get("taxId"), null);
                case "getSentNotificationDocument" ->
                        iPnAppIOB2bClient.getSentNotificationDocument(inputParams.get("iun"),
                                inputParams.get("docIdx") == null ? null : Integer.parseInt(inputParams.get("docIdx")), inputParams.get("taxId"), null);
                case "getReceivedNotificationAttachment" ->
                        iPnAppIOB2bClient.getReceivedNotificationAttachment(inputParams.get("iun"), inputParams.get("attachmentName"),
                                inputParams.get("taxId"), Integer.parseInt(inputParams.get("attachmentIdx")), null);
            }
        } catch (HttpStatusCodeException ex) {
            notificationServerError = ex;
        }
    }

    @Then("si verifica che la chiamata abbia ritornato uno status code: {int}")
    public void verifyStatusCodeResponse(int statusCode) {
        Assertions.assertEquals(statusCode, notificationServerError.getStatusCode().value());
    }

    @And("a seguito della scansione del QR Code, la notifica può essere recuperata da: {destinatario} tramite AppIO passando un header src non valido")
    public void attemptsNotificationRetrievalAppIOWithInvalidHeader(Destinatario user) {
        try {
            this.iPnAppIOB2bClient.getReceivedNotification(sharedSteps.getNotificationIun(), user.getTaxId(), null, "TEST");
        } catch (HttpStatusCodeException e) {
            this.notificationServerError = e;
        }
    }

    @Then("a seguito della scansione del QR Code, la notifica può essere recuperata da: {destinatario} tramite AppIO")
    public void notificationCanBeRetrievedFromAppIOAfterQRCodeScan(Destinatario user) {
        assertNotificationCanBeRetrievedFromAppIO(responseCheckAarMandateDto.getIun(), user.getTaxId(), null);
    }

    @Then("a seguito della scansione del QR Code, la notifica non può essere recuperata da: {destinatario} tramite AppIO senza passare l'id della delega")
    public void notificationCanBeRetrievedFromAppIOAfterQRCodeScanWithoutMandateId(Destinatario user) {
        Assertions.assertThrows(HttpClientErrorException.class, () -> iPnAppIOB2bClient.getReceivedNotification(responseCheckAarMandateDto.getIun(), user.getTaxId(), null));
    }

    @Then("a seguito della scansione del QR Code, la notifica può essere recuperata tramite AppIO dal delegato: {destinatario}")
    public void delegateRetrievesNotificationFromAppIOAfterQRCodeScan(Destinatario user) {
        Assertions.assertNotNull(responseCheckAarMandateDto.getMandateId(), "MandateId cannot be null!");
        assertNotificationCanBeRetrievedFromAppIO(responseCheckAarMandateDto.getIun(), user.getTaxId(), UUID.fromString(responseCheckAarMandateDto.getMandateId()));
    }

    @Then("la notifica può essere recuperata tramite AppIO")
    public void notificationCanBeRetrievedAppIO() {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        assertNotificationCanBeRetrievedFromAppIO(fullSentNotification.getIun(), fullSentNotification.getRecipients().get(0).getTaxId(), null);
    }

    private void assertNotificationCanBeRetrievedFromAppIO(String iun, String taxId, UUID mandateId) {
        AtomicReference<ThirdPartyMessage> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(this.iPnAppIOB2bClient.getReceivedNotification(iun, taxId, mandateId)));
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("a seguito della scansione del QR Code, il documento notificato può essere recuperata tramite AppIO")
    public void notifiedDocumentCanBeRetrievedAppIOAfterQRCodeScan() {
        assertNotificationDocumentCanBeRetrievedFromAppIO(responseCheckAarMandateDto.getIun(), 0, responseCheckAarMandateDto.getRecipientInfo().getTaxId());
    }

    @Then("il documento notificato può essere recuperata tramite AppIO")
    public void notifiedDocumentCanBeRetrievedAppIO() {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<NotificationDocument> documents = fullSentNotification.getDocuments();
        assertNotificationDocumentCanBeRetrievedFromAppIO(fullSentNotification.getIun(), Integer.parseInt(documents.get(0).getDocIdx()),
                fullSentNotification.getRecipients().get(0).getTaxId());
    }

    private void assertNotificationDocumentCanBeRetrievedFromAppIO(String iun, Integer docIdx, String taxId) {
        NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                iPnAppIOB2bClient.getSentNotificationDocument(iun, docIdx, taxId, null);
        try {
            byte[] bytes = Assertions.assertDoesNotThrow(() -> B2bUtils.downloadFile(sentNotificationDocument.getUrl()));
            this.sha256DocumentDownload = B2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, sentNotificationDocument.getSha256());
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("il documento di pagamento {string} può essere recuperata tramite AppIO")
    public void notifiedDocumentPaymentCanBeRetrievedAppIO(String typeDocument) {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<NotificationDocument> documents = fullSentNotification.getDocuments();
        NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                iPnAppIOB2bClient.getReceivedNotificationAttachment(fullSentNotification.getIun(), typeDocument, fullSentNotification.getRecipients().get(0).getTaxId(),
                        Integer.parseInt(documents.get(0).getDocIdx()), null);
        try {
            byte[] bytes = Assertions.assertDoesNotThrow(() -> B2bUtils.downloadFile(sentNotificationDocument.getUrl()));
            this.sha256DocumentDownload = B2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, sentNotificationDocument.getSha256());
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("a seguito della scansione del QR Code, il documento di pagamento {string} può essere recuperata tramite AppIO")
    public void paymentDocumentCanBeRetrievedAppIOAfterQRCodeScan(String typeDocument) {
        downloadPaymentDocument(typeDocument + "_FROM_QR", responseCheckAarMandateDto.getRecipientInfo().getTaxId(), null);
    }

    @Then("a seguito della scansione del QR Code, il documento di pagamento {string} può essere recuperata tramite AppIO dal delegato: {destinatario}")
    public void paymentDocumentCanBeRetrievedAppIOAfterQRCodeScanFromDelegatee(String typeDocument, Destinatario user) {
        Assertions.assertNotNull(responseCheckAarMandateDto);
        Assertions.assertNotNull(responseCheckAarMandateDto.getMandateId());
        downloadPaymentDocument(typeDocument + "_FROM_QR", user.getTaxId(), UUID.fromString(responseCheckAarMandateDto.getMandateId()));
    }

    @Then("il documento di pagamento {string} può essere recuperata tramite AppIO da {string}")
    public void paymentDocumentCanBeRetrievedAppIO(String typeDocument, String recipient) {
        downloadPaymentDocument(typeDocument, recipient, null);
    }

    private void downloadPaymentDocument(String typeDocument, String recipient, UUID mandateId) {
        switch (typeDocument.toUpperCase()) {
            case "F24_FROM_QR" ->
                    downloadF24AppIoByAttachmentName(responseCheckAarMandateDto.getIun(), "F24", recipient, mandateId);
            case "PAGOPA_FROM_QR" ->
                    downloadPAGOPAAppIo(responseCheckAarMandateDto.getIun(), recipient, "PAGOPA", "0", mandateId);
            case "F24" -> downloadF24AppIoByUrl("F24", recipient, mandateId);
            case "PAGOPA" -> {
                FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
                downloadPAGOPAAppIo(fullSentNotification.getIun(), selectTaxIdUser(recipient), "PAGOPA", fullSentNotification.getDocuments().get(0).getDocIdx(), mandateId);
            }
        }
    }


    private void downloadAndVerifyAttachment(Supplier<NotificationAttachmentDownloadMetadataResponse> downloadSupplier, boolean verifySha256) {
        try {
            NotificationAttachmentDownloadMetadataResponse downloadResponse = downloadSupplier.get();
            if (downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0) {
                try {
                    System.out.println("SECONDO TENTATIVO");
                    Thread.sleep(downloadResponse.getRetryAfter() * 3L);
                    downloadResponse = downloadSupplier.get();
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
            }
            System.out.println(downloadResponse);

            assertThat(downloadResponse).as("La risposta di download non deve essere null").isNotNull();

            byte[] bytes = B2bUtils.downloadFile(downloadResponse.getUrl());

            assertThat(bytes).as("Il file scaricato non deve essere vuoto").isNotEmpty();

            this.sha256DocumentDownload = B2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            if (verifySha256)
                assertThat(this.sha256DocumentDownload).as("SHA256 scaricato deve combaciare con quello dichiarato").isEqualTo(downloadResponse.getSha256());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            this.notificationServerError = e;
        }
    }

    public void downloadPAGOPAAppIo(String iun, String taxId, String typeDocument, String docIdx, UUID mandateId) {
        downloadAndVerifyAttachment(
                () -> iPnAppIOB2bClient.getReceivedNotificationAttachment(iun, typeDocument, taxId, Integer.parseInt(docIdx), mandateId),
                true
        );
    }

    public void downloadF24AppIoByUrl(String typeDocument, String recipient, UUID mandateId) {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        ThirdPartyMessage notificationByIun = iPnAppIOB2bClient.getReceivedNotification(fullSentNotification.getIun(), fullSentNotification.getRecipients().get(0).getTaxId(), mandateId);

        assertThat(notificationByIun).as("La notifica deve essere recuperata da AppIO").isNotNull();

        String url = notificationByIun.getAttachments().stream()
                .filter(att -> typeDocument.equalsIgnoreCase(att.getCategory().getValue()))
                .map(ThirdPartyAttachment::getUrl)
                .findFirst()
                .orElse(null);

        assertThat(url).as("URL allegato per documento %s deve esistere", typeDocument).isNotNull();

        downloadAndVerifyAttachment(
                () -> iPnAppIOB2bClient.getReceivedNotificationAttachmentByUrl(url, selectTaxIdUser(recipient)), false);
    }

    public void downloadF24AppIoByAttachmentName(String iun, String attachmentName, String taxId, UUID mandateId) {
        downloadAndVerifyAttachment(
                () -> iPnAppIOB2bClient.getReceivedNotificationAttachment(iun, attachmentName, taxId, 0, mandateId), false);
    }

    @Then("il documento notificato può essere recuperata tramite AppIO da {string}")
    public void notifiedDocumentCanBeRetrievedAppIO(String recipient) {
        try {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            List<NotificationDocument> documents = fullSentNotification.getDocuments();
            NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                    iPnAppIOB2bClient.getSentNotificationDocument(fullSentNotification.getIun(), Integer.parseInt(documents.get(0).getDocIdx()), selectTaxIdUser(recipient), null);

            byte[] bytes = Assertions.assertDoesNotThrow(() -> B2bUtils.downloadFile(sentNotificationDocument.getUrl()));
            this.sha256DocumentDownload = B2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, sentNotificationDocument.getSha256());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            this.notificationServerError = e;
        }
    }

    @And("{string} tenta il recupero della notifica tramite AppIO")
    public void attemptsNotificationRetrievalAppIO(String recipient) {
        try {
            this.iPnAppIOB2bClient.getReceivedNotification(sharedSteps.getNotificationIun(), selectTaxIdUser(recipient), null);
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
        return switch (recipient.trim()) {
            case MARIO_CUCUMBER -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_CUCUMBER.getTaxId();
            case MARIO_GHERKIN -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_GHERKIN.getTaxId();
            case CUCUMBER_SPA -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_CUCUMBER_SPA.getTaxId();
            default -> throw new IllegalStateException("Unexpected value: " + recipient.trim());
        };
    }


    @Then("{string} recupera la notifica tramite AppIO")
    public void recuperaLaNotificaTramiteAppIO(String recipient) {
        AtomicReference<ThirdPartyMessage> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(this.iPnAppIOB2bClient.getReceivedNotification(sharedSteps.getNotificationIun(), selectTaxIdUser(recipient), null)));
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("{string} recupera il documento notificato tramite AppIO")
    public void recuperaIlDocumentoNotificatoTramiteAppIO(String recipient) {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<NotificationDocument> documents = fullSentNotification.getDocuments();
        NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                iPnAppIOB2bClient.getSentNotificationDocument(fullSentNotification.getIun(), Integer.parseInt(documents.get(0).getDocIdx()),
                        selectTaxIdUser(recipient), null);
        try {
            byte[] bytes = Assertions.assertDoesNotThrow(() -> B2bUtils.downloadFile(sentNotificationDocument.getUrl()));
            this.sha256DocumentDownload = B2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            Assertions.assertEquals(this.sha256DocumentDownload, sentNotificationDocument.getSha256());
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }
}
