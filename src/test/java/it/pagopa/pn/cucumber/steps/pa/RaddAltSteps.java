package it.pagopa.pn.cucumber.steps.pa;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddAlternativeClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.RaddOperator;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableAuthTokenRadd;
import it.pagopa.pn.client.b2b.pa.utils.DataPreparationRaddVpceService;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.cucumber.utils.Compress;
import it.pagopa.pn.cucumber.utils.FiscalCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static it.pagopa.pn.cucumber.utils.NotificationValue.generateRandomNumber;

@Slf4j
public class RaddAltSteps {
    private IPnRaddAlternativeClient raddClient;
    private final PnExternalServiceClientImpl externalServiceClient;
    private final SharedSteps sharedSteps;
    private String qrCode;
    private String iun;
    private String fileZip;
    private String currentUserCf;
    private String recipientType;
    @Value("${pn.iun.120gg.fieramosca}")
    private String iunFieramosca120gg;
    @Value("${pn.iun.120gg.lucio}")
    private String iunLucio120gg;
    @Value("${pn.iun.120gg.gherkin}")
    private String iunGherkin120gg;
    @Value("${pn.external.bearer-token-radd-1}")
    private String raddista1;
    @Value("${pn.radd.alt.external.max-print-request}")
    private int maxPrintRequest;
    @Value("${pn.radd-vpc.base-url}")
    private String baseUrl;
    private String operationId;
    private String versionToken = null;
    private String fileKey = null;
    private ActInquiryResponse actInquiryResponse;
    private StartTransactionResponse startTransactionResponse;
    private StartTransactionResponse aorStartTransactionResponse;
    private AORInquiryResponse aorInquiryResponse;
    private final String uid = "1234556";
    private CompleteTransactionResponse completeTransactionResponse;
    private B2bUtils.Pair<String, String> documentUploadResponse;
    private AbortTransactionResponse abortActTransaction;
    private HttpStatusCodeException documentUploadError;
    private HttpStatusCodeException expectedStartTransactionException;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private DataPreparationRaddVpceService dataPreparationService;

    @Autowired
    public RaddAltSteps(PnRaddAlternativeClientImpl raddAltClient, PnExternalServiceClientImpl externalServiceClient, SharedSteps sharedSteps, DataPreparationRaddVpceService dataPreparationService) {
        this.raddClient = raddAltClient;
        this.externalServiceClient = externalServiceClient;
        this.sharedSteps = sharedSteps;
        this.dataPreparationService = dataPreparationService;
    }

    public void setRaddClient(IPnRaddAlternativeClient raddClient) {
        this.raddClient = raddClient;
    }


    /**
     * Salva i dati principali della notifica corrente su file, associandoli a una chiave.
     * il file è salvato in locale sotto l'url "\IdeaProjects\pn-b2b-client\target\output\"
     * Questo step serve per "memorizzare" i dati prodotti in uno scenario (IUN, CF, RecipientType, qrCode)
     * per poterli riutilizzare successivamente in altri scenari.
     * - La chiave deve essere univoca (tipicamente il nome dello scenario o un identificativo)
     * - Il campo qrCode viene salvato solo se presente
     * - Il file è condiviso tra più test, quindi è gestito per evitare conflitti in esecuzione parallela
     * il file va poi spostato in "src/main/resources/output/data-preparation.json" per essere utilizzato
     * lo step che utilizza il file è ("carico i dati della notifica con chiave {string}")
     */
    @And("salvo i dati della notifica con chiave {string}")
    public void salvaDatiConChiave(String key) throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("iun", sharedSteps.getNotificationIun());
        data.put("recipientType", this.recipientType);
        data.put("pa", "Comune_Multi");
        data.put("cf", this.currentUserCf);
        data.put("qrCode", this.qrCode);

        log.info("Dati da salvare per chiave {}:", key);
        data.forEach((k, v) -> log.info("  {} = {}", k, v));

        dataPreparationService.save(key, data);
        log.info("Salvati dati per chiave {}", key);
    }

    @Given("carico i dati della notifica con chiave {string}")
    public void caricoDatiConChiave(String key) throws IOException {
        Map<String, String> data = dataPreparationService.load(key);

        sharedSteps.impostoIunAndPaForTestPurposes(
                data.get("iun"),
                data.get("pa")
        );
        this.recipientType = data.get("recipientType");
        this.currentUserCf = data.get("cf");
        this.qrCode = Optional.ofNullable(data.get("qrCode"))
                .filter(s -> !s.isBlank())
                .orElse(null);
        log.info("Caricati dati per chiave {} -> {}", key, data);
    }

    @When("L'operatore scansione il qrCode per recuperare gli atti di {destinatario}")
    public void lOperatoreScansioneIlQrCodePerRecuperareGliAttiAlternative(Destinatario destinatario) {
        selectUserRaddAlternative(destinatario);
        ActInquiryResponse actInquiryResponse = raddClient.actInquiry(uid, this.currentUserCf, this.recipientType, qrCode, null);
        log.info("actInquiryResponse: {}", actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    @When("L'operatore usa lo IUN {string} per recuperare gli atti di {destinatario}")
    public void lOperatoreUsoIUNPerRecuperareGliAtti(String tipologiaIun, Destinatario destinatario) {
        selectUserRaddAlternative(destinatario);
        ActInquiryResponse actInquiryResponse = raddClient.actInquiry(uid, this.currentUserCf, this.recipientType, null, tipologiaIun.equalsIgnoreCase("corretto") ? this.iun = sharedSteps.getNotificationIun() : tipologiaIun.equalsIgnoreCase("errato") ? this.iun = "GLDZ-MGZD-AGAR-202402-Y-1" : null);

        log.info("actInquiryResponse: {}", actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    @When("L'operatore usa lo IUN {string} per recuperare gli atti di {destinatario} da issuer {string}")
    public void lOperatoreUsoIUNPerRecuperareGliAttiDaIssuer(String tipologiaIun, Destinatario destinatario, String issuer) {
        changeRaddista(issuer);
        selectUserRaddAlternative(destinatario);
        ActInquiryResponse actInquiryResponse = null;
        try {
            actInquiryResponse = raddClient.actInquiry(uid, this.currentUserCf, this.recipientType, null, tipologiaIun.equalsIgnoreCase("corretto") ? this.iun = sharedSteps.getNotificationIun() : tipologiaIun.equalsIgnoreCase("errato") ? this.iun = "GLDZ-MGZD-AGAR-202402-Y-1" : null);
        } catch (HttpStatusCodeException exception) {
            sharedSteps.setNotificationError(exception);
        }
        log.info("actInquiryResponse: {}", actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    @When("L'operatore usa lo IUN {string} per recuperare gli atti di {destinatario} con restituzione errore")
    public void lOperatoreUsoIUNPerRecuperareGliAttiWithError(String iun, Destinatario destinatario) {
        selectUserRaddAlternative(destinatario);
        ActInquiryResponse actInquiryResponse = null;
        try {
            actInquiryResponse = raddClient.actInquiry(uid, this.currentUserCf, this.recipientType, null, iun.equalsIgnoreCase("corretto") ? this.iun = sharedSteps.getNotificationIun() : iun.equalsIgnoreCase("errato") ? this.iun = "GLDZ-MGZD-AGAR-202402-Y-1" : null);
        } catch (HttpStatusCodeException exception) {
            sharedSteps.setNotificationError(exception);
        }
        log.info("actInquiryResponse: {}", actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    private ActInquiryResponseStatus.CodeEnum getErrorCodeRaddAlternative(int errorCode) {
        switch (errorCode) {
            case 0 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_0;
            }
            case 2 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_2;
            }
            case 3 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_3;
            }
            case 4 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_4;
            }
            case 10 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_10;
            }
            case 80 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_80;
            }
            case 99 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_99;
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @Then("Viene restituito un messaggio di errore {string} con codice di errore {int} su radd alternative")
    public void vieneRestituitoUnMessaggioDiErrore(String errorType, int errorCode) {
        errorType = errorType.toLowerCase();
        ActInquiryResponseStatus.CodeEnum error = getErrorCodeRaddAlternative(errorCode);
        switch (errorType) {
            case "qrcode non valido", "cf non valido" -> {
                Assertions.assertEquals(false, actInquiryResponse.getResult());
                Assertions.assertNotNull(actInquiryResponse.getStatus());
                Assertions.assertEquals(error, actInquiryResponse.getStatus().getCode());
            }
            case "stampa già eseguita", "questa notifica è stata annullata dall’ente mittente",
                 "documenti non più disponibili", "ko generico", "input non valido", "limite di 10 stampe superato" -> {
                Assertions.assertEquals(false, actInquiryResponse.getResult());
                Assertions.assertNotNull(actInquiryResponse.getStatus());
                Assertions.assertNotNull(actInquiryResponse.getStatus().getMessage());
                Assertions.assertEquals(errorType.toLowerCase(), actInquiryResponse.getStatus().getMessage().toLowerCase());
                Assertions.assertEquals(error, actInquiryResponse.getStatus().getCode());
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @And("la (scansione)(lettura) si conclude correttamente su radd alternative")
    public void laScansioneSiConcludeCorrettamenteAlternative() {
        log.debug("actInquiryResponse {}", actInquiryResponse.toString());
        Assertions.assertEquals(true, actInquiryResponse.getResult());
        Assertions.assertNotNull(actInquiryResponse.getStatus());
        Assertions.assertEquals(ActInquiryResponseStatus.CodeEnum.NUMBER_0, actInquiryResponse.getStatus().getCode());
    }

    @And("vengono caricati i documento di identità del cittadino su radd alternative")
    public void vengonoCaricatiIDocumentoDiIdentitaDelCittadino() {
        this.versionToken = "string";
        this.operationId = generateRandomNumber();
        uploadDocumentRaddAlternative(true);
        this.fileKey = this.documentUploadResponse != null ? this.documentUploadResponse.getValue1() : null;
    }

    @And("vengono caricati i documento di identità del cittadino su radd alternative per errore")
    public void vengonoCaricatiIDocumentoDiIdentitaDelCittadinoPerErrore() {
        this.operationId = generateRandomNumber();
        uploadDocumentRaddOperatorAlternative(true, RaddOperator.UPLOADER);
    }

    @And("vengono caricati i documento di identità del cittadino su radd alternative dall'operatore RADD {string}")
    public void vengonoCaricatiIDocumentoDiIdentitaDelCittadinoSuRaddAlternativeDallOperatoreRADD(String raddOperatorType) {
        RaddOperator raddOperator = setOperatorRaddJWT(raddOperatorType);
        this.versionToken = raddOperatorType.equalsIgnoreCase("UPLOADER") ? "string" : null;
        this.operationId = generateRandomNumber();
        Assertions.assertDoesNotThrow(() -> uploadDocumentRaddOperatorAlternative(true, raddOperator));
    }

    @And("l'operatore {string} tenta di caricare i documento di identità del cittadino su radd alternative senza successo")
    public void lOperatoreTentaDiCaricareIDocumentoDiIdentitaDelCittadinoSuRaddAlternativeSenzaSuccesso(String raddOperatorType) {
        RaddOperator raddOperator = setOperatorRaddJWT(raddOperatorType);
        this.operationId = generateRandomNumber();
        documentUploadError = Assertions.assertThrows(HttpStatusCodeException.class, () -> uploadDocumentRaddOperatorAlternative(true, raddOperator));
    }

    @And("si inizia il processo di caricamento dei documento di identità del cittadino ma non si porta a conclusione su radd alternative")
    public void siIniziaIlProcessoDiCaricamentoDeiDocumentoDiIdentitaDelCittadinoMaNonSiPortaAConclusione() {
        this.operationId = generateRandomNumber();
        this.versionToken = "string";
        uploadDocumentRaddAlternative(false);
    }

    @And("si inizia il processo di caricamento per radd {string} dei documento di identità del cittadino ma non si porta a conclusione su radd alternative")
    public void siIniziaIlProcessoDiCaricamentoPerRaddStandardDeiDocumentoDiIdentitaDelCittadinoMaNonSiPortaAConclusione(String raddOperatorType) {
        RaddOperator raddOperator = setOperatorRaddJWT(raddOperatorType);
        this.operationId = generateRandomNumber();
        uploadDocumentRaddOperatorAlternative(false, raddOperator);
    }

    private void uploadDocumentRaddAlternative(boolean usePresignedUrl) {
        try {
            creazioneZip();
            PnRaddAlternativeClientImpl altClient = (PnRaddAlternativeClientImpl) raddClient;
            B2bUtils.Pair<String, String> uploadResponse = B2bUtils.preloadRaddAlternativeDocument(sharedSteps.getContext(), altClient, null, "classpath:/" + this.fileZip, usePresignedUrl, this.operationId);
            Assertions.assertNotNull(uploadResponse);
            this.documentUploadResponse = uploadResponse;
            log.info("documentUploadResponse: {}", documentUploadResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void uploadDocumentRaddOperatorAlternative(boolean usePresignedUrl, RaddOperator raddOperator) {
        try {
            creazioneZip();
            PnRaddAlternativeClientImpl altClient = (PnRaddAlternativeClientImpl) raddClient;
            B2bUtils.Pair<String, String> uploadResponse = B2bUtils.preloadRaddAlternativeDocument(sharedSteps.getContext(), altClient, raddOperator, "classpath:/" + this.fileZip, usePresignedUrl, this.operationId);
            Assertions.assertNotNull(uploadResponse);
            this.documentUploadResponse = uploadResponse;
            log.info("documentUploadResponse: {}", documentUploadResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative")
    public void vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAAR() {
        this.operationId = this.operationId == null ? generateRandomNumber() : this.operationId;
        startTransactionActRaddAlternative(this.operationId, true);
    }

    @And("l'operazione di download degli atti restituisce {int} documenti e si conclude con errore {string} e codice {int} su radd alternative")
    public void lOperazioneDiDownloadDegliAttiSiConcludeCorrettamente(Integer documenti, String errorDescription, int erroCode) {
        StartTransactionResponseStatus.CodeEnum error = getErrorCodeStartTransaction(erroCode);
        Assertions.assertNotNull(this.startTransactionResponse.getDownloadUrlList());
        Assertions.assertFalse(this.startTransactionResponse.getDownloadUrlList().isEmpty());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus());
        Assertions.assertEquals(error, this.startTransactionResponse.getStatus().getCode());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus().getMessage());
        Assertions.assertEquals(errorDescription.trim().toLowerCase(), this.startTransactionResponse.getStatus().getMessage().toLowerCase());
        Assertions.assertEquals(documenti, this.startTransactionResponse.getDownloadUrlList().size());
    }

    @And("l'operazione di download non restituisce atti, generando un errore {string} con codice {int} su radd alternative")
    public void lOperazioneDiDownloadNonRestituisceAttiGeneraUnErroreConCodice(String errorDescription, int erroCode) {
        StartTransactionResponseStatus.CodeEnum error = getErrorCodeStartTransaction(erroCode);
        Assertions.assertNotNull(this.startTransactionResponse.getDownloadUrlList());
        Assertions.assertEquals(0, this.startTransactionResponse.getDownloadUrlList().size());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus());
        Assertions.assertEquals(error, this.startTransactionResponse.getStatus().getCode());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus().getMessage());
        Assertions.assertEquals(errorDescription.trim().toLowerCase(), this.startTransactionResponse.getStatus().getMessage().toLowerCase());
    }

    @Then("Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative per operatore {string}")
    public void vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARDaRaddAlternativePerOperatoreStandard(String raddOperatorType) {
        RaddOperator raddOperator = RaddOperator.valueOf(raddOperatorType);
        this.operationId = this.operationId == null ? generateRandomNumber() : this.operationId;
        this.fileKey = this.fileKey != null && (this.fileKey.isEmpty() || this.fileKey.equals("null")) ? setFileKeyValue(this.fileKey) : this.documentUploadResponse != null ? this.documentUploadResponse.getValue1() : null;
        raddClient.setAuthTokenRadd(raddOperator.getIssuerType());
        startTransactionActRaddAlternativeForOperator(this.operationId, true, raddOperator.getUid());
    }

    @Then("Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative per operatore {string} con fileKey {string}")
    public void vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARDaRaddAlternativePerOperatoreStandard(String raddOperatorType, String fileKey) {
        this.fileKey = fileKey;
        vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARDaRaddAlternativePerOperatoreStandard(raddOperatorType);
    }

    @When("tentativo di recuperare gli atti delle notifiche associata all'AAR da radd alternative per operatore {string} con versionToken errato")
    public void tentativoDiRecuperareGliAttiDelleNotificheAssociataAllAARDaRaddAlternativePerOperatoreConVersionTokenErrato(String raddOperatorType) {
        this.versionToken = raddOperatorType.equalsIgnoreCase("UPLOADER") ? null : "string";
        this.expectedStartTransactionException = Assertions.assertThrows(HttpClientErrorException.class, () -> vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARDaRaddAlternativePerOperatoreStandard(raddOperatorType));
    }

    @When("tentativo di recuperare gli atti delle notifiche associata all'AAR da radd alternative per operatore {string} senza successo")
    public void tentativoDiRecuperareGliAttiDelleNotificheAssociataAllAARDaRaddAlternativePerOperatoreSenzaSuccesso(String raddOperatorType) {
        this.expectedStartTransactionException = Assertions.assertThrows(HttpClientErrorException.class, () -> vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARDaRaddAlternativePerOperatoreStandard(raddOperatorType));
    }

    @When("tentativo di recuperare gli atti delle notifiche associata all'AAR da radd alternative per operatore {string} senza successo con file key {string}")
    public void tentativoDiRecuperareGliAttiDelleNotificheAssociataAllAARDaRaddAlternativePerOperatoreConFileKey(String raddOperatorType, String fileKey) {
        this.fileKey = fileKey.equals("null") ? this.fileKey : fileKey;
        this.expectedStartTransactionException = Assertions.assertThrows(HttpClientErrorException.class, () -> vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARDaRaddAlternativePerOperatoreStandard(raddOperatorType));
    }

    @Then("Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative senza ritentativi")
    public void vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARSenzaRetry() {
        startTransactionActRaddAlternative(this.operationId, false);
    }

    protected void startTransactionActRaddAlternative(String operationId, boolean retry) {
        ActStartTransactionRequest actStartTransactionRequest = createActStartTransactionRequest(operationId);
        System.out.println("actStartTransactionRequest: " + actStartTransactionRequest);
        int attempts = 5;
        for (int i = 0; i < attempts; i++) {
            this.startTransactionResponse = raddClient.startActTransaction(uid, actStartTransactionRequest);
            if (this.startTransactionResponse.getStatus().getCode().equals(StartTransactionResponseStatus.CodeEnum.NUMBER_2) && retry) {
                waitFor(this.startTransactionResponse.getStatus().getRetryAfter().longValue());
            } else break;
        }
        System.out.println("startTransactionResponse: " + startTransactionResponse);
    }

    private void startTransactionActRaddAlternativeForOperator(String operationId, boolean retry, String uidRaddOperator) {
        ActStartTransactionRequest actStartTransactionRequest = createActStartTransactionRequest(operationId);
        System.out.println("actStartTransactionRequest: " + actStartTransactionRequest);
        this.startTransactionResponse = raddClient.startActTransaction(uidRaddOperator, actStartTransactionRequest);

        if (this.startTransactionResponse.getStatus().getCode().equals(StartTransactionResponseStatus.CodeEnum.NUMBER_2) && retry) {
            waitFor(this.startTransactionResponse.getStatus().getRetryAfter().longValue());
            this.startTransactionResponse = raddClient.startActTransaction(uid, actStartTransactionRequest);
        }
        System.out.println("startTransactionResponse: " + startTransactionResponse);
    }

    @And("l'operazione di download degli atti si conclude correttamente su radd alternative")
    public void lOperazioneDiDownloadDegliAttiSiConcludeCorrettamente() {
        Assertions.assertNotNull(this.startTransactionResponse.getDownloadUrlList());
        Assertions.assertFalse(this.startTransactionResponse.getDownloadUrlList().isEmpty());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus());
        Assertions.assertEquals(StartTransactionResponseStatus.CodeEnum.NUMBER_0, this.startTransactionResponse.getStatus().getCode());
    }

    @And("l'operazione di download restituisce {int} documenti")
    public void downloadOperationReturnsTotDocuments(Integer documenti) {
        Assertions.assertNotNull(this.startTransactionResponse.getDownloadUrlList());
        Assertions.assertFalse(this.startTransactionResponse.getDownloadUrlList().isEmpty());
        Assertions.assertEquals(documenti, this.startTransactionResponse.getDownloadUrlList().size());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus());
        Assertions.assertEquals(StartTransactionResponseStatus.CodeEnum.NUMBER_0, this.startTransactionResponse.getStatus().getCode());
    }

    @And("si verifica se il file richiede l'autenticazione")
    public void siVerificaSeIlFileRichiedeLAutenticazione() {
        Assertions.assertNotNull(this.startTransactionResponse.getDownloadUrlList());
        for (DownloadUrl download : this.startTransactionResponse.getDownloadUrlList()) {
            log.info("downloadData: {}", download);
            Assertions.assertNotNull(download.getUrl());
            Assertions.assertNotNull(download.getNeedAuthentication());
        }
    }

    @And("l'operazione di download degli atti genera un errore {string} con codice {int} su radd alternative")
    public void lOperazioneDiDownloadDegliAttiGeneraUnErroreConCodice(String errorDescription, int erroCode) {
        StartTransactionResponseStatus.CodeEnum error = getErrorCodeStartTransaction(erroCode);
        Assertions.assertNull(this.startTransactionResponse.getDownloadUrlList());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus());
        Assertions.assertEquals(error, this.startTransactionResponse.getStatus().getCode());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus().getMessage());
        Assertions.assertEquals(errorDescription.trim().toLowerCase(), this.startTransactionResponse.getStatus().getMessage().toLowerCase());
    }

    private StartTransactionResponseStatus.CodeEnum getErrorCodeStartTransaction(int errorCode) {
        switch (errorCode) {
            case 0 -> {
                return StartTransactionResponseStatus.CodeEnum.NUMBER_0;
            }
            case 2 -> {
                return StartTransactionResponseStatus.CodeEnum.NUMBER_2;
            }
            case 4 -> {
                return StartTransactionResponseStatus.CodeEnum.NUMBER_4;
            }
            case 5 -> {
                return StartTransactionResponseStatus.CodeEnum.NUMBER_5;
            }
            case 99 -> {
                return StartTransactionResponseStatus.CodeEnum.NUMBER_99;
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @And("viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative")
    public void vieneConclusaLaVisualizzatiDiAttiEdAttestazioniDellaNotifica() {
        CompleteTransactionRequest completeTransactionRequest = new CompleteTransactionRequest().operationId(this.operationId).operationDate(dateTimeFormatter.format(OffsetDateTime.now()));
        this.completeTransactionResponse = raddClient.completeActTransaction(this.uid, completeTransactionRequest);
        System.out.println(completeTransactionResponse);
        Assertions.assertNotNull(completeTransactionResponse);
    }

    @Given("la persona (fisica)(giuridica) {destinatario} chiede di verificare la presenza di notifiche")
    public void ilCittadinoChiedeDiVerificareLaPresenzaDiNotifiche(Destinatario destinatario) {
        selectUserRaddAlternative(destinatario);
        this.versionToken = "string";
        this.aorInquiryResponse = raddClient.aorInquiry(uid, this.currentUserCf, this.recipientType);
    }

    @And("la persona fisica {destinatario} chiede di verificare ad operatore radd {string} la presenza di notifiche")
    public void laPersonaFisicaChiedeDiVerificareAdOperatoreRaddLaPresenzaDiNotifiche(Destinatario destinatario, String raddOperatorType) {
        RaddOperator raddOperator = RaddOperator.valueOf(raddOperatorType);
        selectUserRaddAlternative(destinatario);
        this.versionToken = raddOperatorType.equalsIgnoreCase("UPLOADER") ? "string" : null;
        this.aorInquiryResponse = raddClient.aorInquiry(raddOperator.getUid(), this.currentUserCf, this.recipientType);
    }

    @When("La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative")
    public void laVerificaAorMostraCorrettamenteLeNotificheInStatoIrreperibile() {
        Assertions.assertNotNull(this.aorInquiryResponse);
        Assertions.assertEquals(Boolean.TRUE, this.aorInquiryResponse.getResult());
        Assertions.assertNotNull(this.aorInquiryResponse.getStatus());
        Assertions.assertEquals(ResponseStatus.CodeEnum.NUMBER_0, this.aorInquiryResponse.getStatus().getCode());
        log.info("aorInquiryResponse: {}", this.aorInquiryResponse);
    }


    @Then("Vengono recuperati gli aar delle notifiche in stato irreperibile della persona (fisica)(giuridica) su radd alternative")
    public void vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibile() {
        AorStartTransactionRequest aorStartTransactionRequest = new AorStartTransactionRequest().versionToken("string").fileKey(this.documentUploadResponse.getValue1()).operationId(this.operationId).recipientTaxId(this.currentUserCf).recipientType(this.recipientType.equalsIgnoreCase("PF") ? AorStartTransactionRequest.RecipientTypeEnum.PF : AorStartTransactionRequest.RecipientTypeEnum.PG).operationDate(dateTimeFormatter.format(OffsetDateTime.now()))
                //.delegateTaxId("")
                .checksum(this.documentUploadResponse.getValue2());
        this.aorStartTransactionResponse = raddClient.startAorTransaction(this.uid, aorStartTransactionRequest);
    }

    @Then("Vengono recuperati gli aar delle notifiche in stato irreperibile della persona (fisica)(giuridica) su radd vpce")
    public void vengonoRecuperatiGliAttiAorVpce() {
        this.operationId = this.operationId == null ? generateRandomNumber() : this.operationId;
        AorStartTransactionRequest request = new AorStartTransactionRequest().operationId(this.operationId).recipientTaxId(this.currentUserCf).recipientType(this.recipientType.equalsIgnoreCase("PF") ? AorStartTransactionRequest.RecipientTypeEnum.PF : AorStartTransactionRequest.RecipientTypeEnum.PG).operationDate(dateTimeFormatter.format(OffsetDateTime.now()));

        log.info("AOR VPCE REQUEST: {}", request);

        this.aorStartTransactionResponse = raddClient.startAorTransaction(this.uid, request);

        log.info("AOR VPCE RESPONSE: {}", aorStartTransactionResponse);
    }

    @Then("Vengono recuperati gli aar delle notifiche in stato irreperibile della persona (fisica)(giuridica) 2 volte su radd alternative")
    public void vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibile2volte() {
        AorStartTransactionRequest aorStartTransactionRequest = createAorStartTransactionRequest();
        this.aorStartTransactionResponse = raddClient.startAorTransaction(this.uid, aorStartTransactionRequest);
        this.aorStartTransactionResponse = raddClient.startAorTransaction(this.uid, aorStartTransactionRequest);
    }

    @Then("Vengono recuperati gli aar delle notifiche in stato irreperibile della persona (fisica)(giuridica) con lo stesso operationId dal raddista {string}")
    public void vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileStessoOperationId(String organizzazione) {
        changeRaddista(organizzazione);
        AorStartTransactionRequest aorStartTransactionRequest = createAorStartTransactionRequest();
        this.aorStartTransactionResponse = raddClient.startAorTransaction(this.uid, aorStartTransactionRequest);
    }

    @Then("Vengono recuperati gli aar delle notifiche in stato irreperibile della persona (fisica)(giuridica) su radd alternative da operatore radd {string}")
    public void vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileDaOperatoreRaddType(String raddOperatorType) {
        RaddOperator raddOperator = setOperatorRaddJWT(raddOperatorType);
        this.fileKey = this.fileKey != null && (this.fileKey.isEmpty() || this.fileKey.equals("null")) ? setFileKeyValue(this.fileKey) : this.documentUploadResponse != null ? this.documentUploadResponse.getValue1() : null;
        AorStartTransactionRequest aorStartTransactionRequest = createAorStartTransactionRequest();
        this.aorStartTransactionResponse = raddClient.startAorTransaction(raddOperator.getUid(), aorStartTransactionRequest);
    }

    @Then("Vengono recuperati gli aar delle notifiche in stato irreperibile della persona (fisica)(giuridica) su radd alternative da operatore radd {string} con file key {string}")
    public void vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileDaOperatoreRaddType(String raddOperatorType, String fileKey) {
        this.fileKey = fileKey;
        vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileDaOperatoreRaddType(raddOperatorType);
    }

    private String setFileKeyValue(String fileKey) {
        return this.fileKey.equals("null") ? null : fileKey;
    }

    @And("il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative")
    public void ilRecuperoDegliAttiInStatoIrreperibileSiConcludeCorrettamente() {
        log.info("aorStartTransactionResponse: {}", this.aorStartTransactionResponse);

        Assertions.assertNotNull(this.aorStartTransactionResponse.getDownloadUrlList());
        Assertions.assertFalse(this.aorStartTransactionResponse.getDownloadUrlList().isEmpty());
        Assertions.assertNotNull(this.aorStartTransactionResponse.getStatus());
        Assertions.assertEquals(StartTransactionResponseStatus.CodeEnum.NUMBER_0, this.aorStartTransactionResponse.getStatus().getCode());
    }

    @And("il recupero degli aar in stato irreperibile si conclude correttamente e vengono restituiti {int} aar su radd alternative")
    public void ilRecuperoDegliAarInStatoIrreperibileSiConcludeCorrettamenteEVengonoRestituitiTuttiEGliAar(int aarNumber) {
        log.info("aorStartTransactionResponse: {}", this.aorStartTransactionResponse);

        Assertions.assertNotNull(this.aorStartTransactionResponse.getDownloadUrlList());
        Assertions.assertEquals(this.aorStartTransactionResponse.getDownloadUrlList().size(), aarNumber);
        Assertions.assertFalse(this.aorStartTransactionResponse.getDownloadUrlList().isEmpty());
        Assertions.assertNotNull(this.aorStartTransactionResponse.getStatus());
        Assertions.assertEquals(StartTransactionResponseStatus.CodeEnum.NUMBER_0, this.aorStartTransactionResponse.getStatus().getCode());
    }

    @And("il recupero degli aar genera un errore {string} con codice {int} su radd alternative")
    public void ilRecuperoDegliAarGeneraUnErroreConCodice(String errorType, int errorCode) {
        log.info("aorStartTransactionResponse: {}", this.aorStartTransactionResponse);

        errorType = errorType.toLowerCase();
        StartTransactionResponseStatus.CodeEnum error = getErrorCodeStartTransaction(errorCode);
        Assertions.assertNull(this.aorStartTransactionResponse.getDownloadUrlList());
        Assertions.assertNotNull(this.aorStartTransactionResponse.getStatus());
        Assertions.assertEquals(error, this.aorStartTransactionResponse.getStatus().getCode());
        Assertions.assertNotNull(this.aorStartTransactionResponse.getStatus().getMessage());
        Assertions.assertEquals(errorType, this.aorStartTransactionResponse.getStatus().getMessage().toLowerCase());
    }

    @When("La verifica della presenza di notifiche in stato irreperibile genera un errore {string} con codice {int} su radd alternative")
    public void laVerificaDellaPresenzaDiNotificheInStatoIrreperibileGeneraUnErroreConCodice(String errorType, int errorCode) {
        errorType = errorType.toLowerCase();
        ResponseStatus.CodeEnum error = getAorErrorCode(errorCode);
        if (errorType.equals("non ci sono notifiche non consegnate per questo codice fiscale")) {
            Assertions.assertEquals(false, this.aorInquiryResponse.getResult());
            Assertions.assertNotNull(this.aorInquiryResponse.getStatus());
            Assertions.assertEquals(error, this.aorInquiryResponse.getStatus().getCode());
        } else {
            throw new IllegalArgumentException();
        }
        log.info("aorInquiryResponse: {}", this.aorInquiryResponse);
    }

    private ResponseStatus.CodeEnum getAorErrorCode(int errorCode) {
        switch (errorCode) {
            case 0 -> {
                return ResponseStatus.CodeEnum.NUMBER_0;
            }
            case 99 -> {
                return ResponseStatus.CodeEnum.NUMBER_99;
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @When("tentativo di recuperare gli aar delle notifiche in stato irreperibile da operatore radd {string} senza successo")
    public void siEsegueUnTentativoDiRecuperareGliAarDelleNotificheInStatoIrreperibileDaOperatoreRaddSenzaSuccesso(String raddOperatorType) {
        this.expectedStartTransactionException = Assertions.assertThrows(HttpClientErrorException.class, () -> vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileDaOperatoreRaddType(raddOperatorType));
    }

    @When("tentativo di recuperare gli aar delle notifiche in stato irreperibile da operatore radd {string} senza successo con file key {string}")
    public void siEsegueUnTentativoDiRecuperareGliAarDelleNotificheInStatoIrreperibileDaOperatoreRaddSenzaSuccessoConFileKey(String raddOperatorType, String fileKey) {
        this.fileKey = fileKey.equals("null") ? this.fileKey : fileKey;
        this.expectedStartTransactionException = Assertions.assertThrows(HttpClientErrorException.class, () -> vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileDaOperatoreRaddType(raddOperatorType));
    }

    @When("tentativo di recuperare gli aar delle notifiche in stato irreperibile da operatore radd {string} con versionToken errato")
    public void siEsegueUnTentativoDiRecuperareGliAarDelleNotificheInStatoIrreperibileDaOperatoreRaddSenzaVersionToken(String raddOperatorType) {
        this.versionToken = raddOperatorType.equalsIgnoreCase("UPLOADER") ? null : "string";
        this.expectedStartTransactionException = Assertions.assertThrows(HttpClientErrorException.class, () -> vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileDaOperatoreRaddType(raddOperatorType));
    }

    @And("il tentativo genera un errore {int} {string} con il messaggio {string}")
    public void lErroreDelRecuperoDegliAarDelleNotificheGenereUnErroreConIlMessaggio(int errorCode, String errorType, String errorMessage) {
        Assertions.assertNotNull(expectedStartTransactionException);
        Assertions.assertNotNull(expectedStartTransactionException.getStatusCode());
        Assertions.assertNotNull(expectedStartTransactionException.getMessage());
        Assertions.assertEquals(errorCode, expectedStartTransactionException.getStatusCode().value());
        Assertions.assertEquals(errorType, expectedStartTransactionException.getStatusText());
        Assertions.assertTrue(expectedStartTransactionException.getMessage().contains(errorMessage), "the message is: " + expectedStartTransactionException.getMessage());
    }

    @And("viene chiusa la transazione per il recupero degli aar su radd alternative")
    public void vieneDichiarataCompletataLaTransazionePerIlRecuperoDegliAar() {
        CompleteTransactionRequest completeTransactionRequest = new CompleteTransactionRequest().operationId(this.operationId).operationDate(dateTimeFormatter.format(OffsetDateTime.now()));
        this.completeTransactionResponse = raddClient.completeAorTransaction(this.uid, completeTransactionRequest);
        log.info("completeTransactionResponse: {}", completeTransactionResponse);
    }

    @And("la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative")
    public void laChiusuraDelleTransazionePerIlRecuperoDegliAarNonGeneraErrori() {
        Assertions.assertNotNull(this.completeTransactionResponse);
        Assertions.assertNotNull(this.completeTransactionResponse.getStatus());
        Assertions.assertEquals(TransactionResponseStatus.CodeEnum.NUMBER_0, this.completeTransactionResponse.getStatus().getCode());
    }

    @And("la chiusura delle transazione per il recupero degli aar ha generato l'errore {string} con statusCode {int} su radd alternative")
    public void laChiusuraDelleTransazionePerIlRecuperoDegliAarNonGeneraErrori(String error, int statusCode) {
        Assertions.assertNotNull(this.completeTransactionResponse);
        Assertions.assertNotNull(this.completeTransactionResponse.getStatus());
        Assertions.assertNotNull(this.completeTransactionResponse.getStatus().getCode());
        Assertions.assertEquals(new BigDecimal(statusCode), this.completeTransactionResponse.getStatus().getCode().getValue());
        Assertions.assertEquals(error, this.completeTransactionResponse.getStatus().getMessage());
    }

    @Given("vengono caricati i documento di identità del cittadino senza {string} su radd alternative ")
    public void vengonoCaricatiIDocumentoDiIdentitaDelCittadinoSenza(String without) {
        String sha256;
        try {
            sha256 = B2bUtils.computeSha256(sharedSteps.getContext(), "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DocumentUploadRequest documentUploadRequest = new DocumentUploadRequest();
        documentUploadRequest = without.equalsIgnoreCase("contentType") ? documentUploadRequest : documentUploadRequest.checksum(sha256);

        try {
            DocumentUploadResponse documentUploadResponse = raddClient.documentUpload(this.uid, documentUploadRequest);
            log.debug("DocumentUploadResponse: {}", documentUploadResponse);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            this.documentUploadError = httpStatusCodeException;
        }
    }

    @Then("il caricamento ha prodotto une errore http {int} su radd alternative")
    public void uploadProducedAnHttpError(int httpError) {
        Assertions.assertNotNull(this.documentUploadError);
        Assertions.assertEquals(this.documentUploadError.getStatusCode().value(), httpError);
    }

    @Then("la transazione viene abortita per gli {string}")
    public void laTransazioneVieneAbortitaAor(String tipologia) {
        switch (tipologia.toLowerCase()) {
            case "aor" ->
                    this.abortActTransaction = this.raddClient.abortAorTransaction(this.uid, new AbortTransactionRequest().operationId(this.operationId).operationDate(dateTimeFormatter.format(OffsetDateTime.now())).reason("TEST"));
            case "act" ->
                    this.abortActTransaction = this.raddClient.abortActTransaction(this.uid, new AbortTransactionRequest().operationId(this.operationId).operationDate(dateTimeFormatter.format(OffsetDateTime.now())).reason("TEST"));
            default -> throw new IllegalArgumentException();
        }
    }

    @And("l'operazione di abort genera un errore {string} con codice {int} su radd alternative")
    public void lOperazioneDiAbortGeneraUnErroreConCodice(String error, int statusCode) {
        Assertions.assertNotNull(this.abortActTransaction);
        Assertions.assertNotNull(this.abortActTransaction.getStatus());
        Assertions.assertNotNull(this.abortActTransaction.getStatus().getCode());
        Assertions.assertEquals(new BigDecimal(statusCode), this.abortActTransaction.getStatus().getCode().getValue());
        Assertions.assertEquals(error, this.abortActTransaction.getStatus().getMessage());
    }

    protected void selectUserRaddAlternative(Destinatario destinatario) {
        this.currentUserCf = destinatario.isSignorCasuale() ? getRecipientZeroTaxId() :
                destinatario.isSignorGenerato() ? FiscalCodeGenerator.generateCF(System.nanoTime()) : destinatario.getTaxId();
        this.recipientType = destinatario.getRecipientType();
    }

    //TODO, c'è un metodo ad hoc per il taxId in SharedSteps
    private String getRecipientZeroTaxId() {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        return fullSentNotification.getRecipients().get(0).getTaxId();
    }

    @Given("Il cittadino {destinatario} come destinatario {int} mostra il QRCode {string}")
    public void ilCittadinoMostraIlQRCodeRaddAlternative(Destinatario destinatario, Integer recipientIndex, String qrCodeType) {
        selectUserRaddAlternative(destinatario);
        qrCodeType = qrCodeType.toLowerCase();
        switch (qrCodeType) {
            case "malformato" -> {
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), recipientIndex);
                this.qrCode = this.qrCode + "MALF";
            }
            case "inesistente" -> {
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), recipientIndex);
                char toReplace = this.qrCode.charAt(0);
                char replace = toReplace == 'B' ? 'C' : 'B';
                this.qrCode = this.qrCode.replace(toReplace, replace);
            }
            case "appartenente a terzo" -> {
                if (this.currentUserCf.equalsIgnoreCase(getRecipientZeroTaxId())) {
                    throw new IllegalArgumentException();
                }
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), recipientIndex);
            }
            case "corretto" -> vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), recipientIndex);
            case "dopo 120gg" -> {
                if (this.currentUserCf.equalsIgnoreCase(sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_CUCUMBER.getTaxId())) {
                    vieneRichiestoIlCodiceQRPerLoIUN(this.iunFieramosca120gg, recipientIndex);
                } else if (this.currentUserCf.equalsIgnoreCase(sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_GHERKIN.getTaxId())) {
                    vieneRichiestoIlCodiceQRPerLoIUN(this.iunGherkin120gg, recipientIndex);
                } else if (this.currentUserCf.equalsIgnoreCase(sharedSteps.getDestinatarioRegistry().DESTINATARIO_CUCUMBER_SPA.getTaxId())) {
                    vieneRichiestoIlCodiceQRPerLoIUN(this.iunLucio120gg, recipientIndex);
                } else {
                    throw new IllegalArgumentException();
                }
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @Given("viene richiesto il codice QR per lo IUN {string} per il destinatario {int} su radd alternative")
    public void vieneRichiestoIlCodiceQRPerLoIUN(String iun, Integer destinatario) {
        this.qrCode = sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(iun, destinatario);
        //log.info("********  qrCode  *******/: {}", qrCode);
    }

    @When("L'operatore scansione il qrCode per recuperare gli atti da radd alternative")
    public void lOperatoreScansioneIlQrCodePerRecuperareGliAtti() {
        ActInquiryResponse actInquiryResponse = raddClient.actInquiry(uid, this.currentUserCf, this.recipientType, qrCode, iun);
        log.info("actInquiryResponse: {}", actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    @When("Imposto il cf {string} e recipient type {string} e qrCode {string}")
    public void setDateFromPreparationWithQrCode(String cf, String recipientType, String qrCode) {
        this.currentUserCf = cf;
        this.recipientType = recipientType;
        this.qrCode = qrCode;
    }

    @When("Imposto il cf {string} e recipient type {string}")
    public void setDateFromPreparation(String cf, String recipientType) {
        this.currentUserCf = cf;
        this.recipientType = recipientType;
        this.qrCode = qrCode;
    }

    @When("L'operatore {string} scansione il qrCode per recuperare gli atti da radd alternative")
    public void lOperatoreUploaderScansioneIlQrCodePerRecuperareGliAtti(String raddOperatorType) {
        RaddOperator raddOperator = setOperatorRaddJWT(raddOperatorType);
        this.versionToken = raddOperatorType.equalsIgnoreCase("UPLOADER") ? "string" : null;
        ActInquiryResponse actInquiryResponse = raddClient.actInquiry(raddOperator.getUid(), this.currentUserCf, this.recipientType, qrCode, null);
        log.info("actInquiryResponse: {}", actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    @Given("L'operatore esegue il download del frontespizio del operazione {string}")
    public void lOperatoreEsegueDownloadFrontespizio(String operationType) {

        try {
            downloadFrontespizio(operationType.toUpperCase(), this.operationId, null);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + " {OperationId: " + this.operationId + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Given("L'operatore esegue il download del frontespizio del operazione {string} con attachmentId {string}")
    public void lOperatoreEsegueDownloadFrontespizioAttachmentIdNonEsistente(String operationType, String attachmentId) {
        try {
            downloadFrontespizio(operationType.toUpperCase(), this.operationId, attachmentId);
        } catch (HttpStatusCodeException exception) {
            sharedSteps.setNotificationError(exception);
        }
    }

    @When("L'operatore scansiona il qrCode e stampa gli atti per {int} volte senza errori")
    public void lOperatoreScansionaIlQrCodeEStampaGliAttiPerIntVolteSenzaErrori(int iteration) {
        IntStream.range(0, iteration).forEach(i -> {
            lOperatoreScansioneIlQrCodePerRecuperareGliAtti();
            laScansioneSiConcludeCorrettamenteAlternative();
            vengonoCaricatiIDocumentoDiIdentitaDelCittadinoSuRaddAlternativeDallOperatoreRADD("UPLOADER");
            vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAAR();
            lOperazioneDiDownloadDegliAttiSiConcludeCorrettamente();
            vieneConclusaLaVisualizzatiDiAttiEdAttestazioniDellaNotifica();
            laChiusuraDelleTransazionePerIlRecuperoDegliAarNonGeneraErrori();
        });
    }


    @When("L'operatore scansiona il qrCode e stampa gli atti per il numero di volte consentito")
    public void lOperatoreScansionaIlQrCodeEStampaGliAttiPerIlNumeroDiVolteConsentito() {
        IntStream.range(0, maxPrintRequest).forEach(i -> {
            lOperatoreScansioneIlQrCodePerRecuperareGliAtti();
            laScansioneSiConcludeCorrettamenteAlternative();
            vengonoCaricatiIDocumentoDiIdentitaDelCittadino();
            vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAAR();
            lOperazioneDiDownloadDegliAttiSiConcludeCorrettamente();
            vieneConclusaLaVisualizzatiDiAttiEdAttestazioniDellaNotifica();
            laChiusuraDelleTransazionePerIlRecuperoDegliAarNonGeneraErrori();
        });
    }

    private void downloadFrontespizio(String operationType, String operationId, String attachmentId) {
        byte[] download = raddClient.documentDownload(operationType, operationId, attachmentId);
        Assertions.assertNotNull(download);
        B2bUtils.stampaPdfTramiteByte(download, "target/classes/frontespizio" + generateRandomNumber() + ".pdf");
    }

    public void creazioneZip() throws IOException {
        String[] files = {};
        if (this.recipientType.equalsIgnoreCase("PG")) {
            files = new String[]{"target/classes/sample.pdf"};
        }

        InputStream[] filesJson = {creazioneJSON()};
        String fileDestination = "file" + generateRandomNumber() + ".zip";
        Compress c = new Compress(filesJson, files, "target/classes/" + fileDestination);
        c.zip();
        this.fileZip = fileDestination;
    }

    public InputStream creazioneJSON() {
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("operationId", this.operationId);
        jsonMap.put("docType", "Carta d'identità");
        jsonMap.put("docNumber", generateRandomNumber());
        jsonMap.put("docIssuer", generateRandomNumber());
        jsonMap.put("issueDate", dateTimeFormatter.format(OffsetDateTime.now()));
        jsonMap.put("expireDate", dateTimeFormatter.format(OffsetDateTime.now().plusDays(10)));
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonString = objectMapper.writeValueAsString(jsonMap);
            System.out.println(jsonString);

            byte[] jsonBytes = jsonString.getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonBytes);

            InputStreamSource inputStreamSource = new InputStreamResource(inputStream);

            return inputStreamSource.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @After("@raddAlt")
    public void deleteZip() {
        if (fileZip != null) {
            URI zipDisk = URI.create("target/classes/" + this.fileZip);
            File file = new File(zipDisk.getPath());
            boolean deleted = file.delete();
            System.out.println("delete " + deleted);
        }
    }

    public void changeRaddista(String raddista) {
        switch (raddista.toLowerCase()) {
            case "issuer_1" -> raddClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_1);
            case "issuer_2" -> raddClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_2);
            case "issuer_non_censito" ->
                    raddClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_NON_CENSITO);
            case "issuer_dati_errati" ->
                    raddClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.DATI_ERRATI);
            case "issuer_scaduto" ->
                    raddClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_SCADUTO);
            case "issuer_aud_errata" -> raddClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.AUD_ERRATA);
            case "issuer_kid_diverso" ->
                    raddClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.KID_DIVERSO);
            case "issuer_private_diverso" ->
                    raddClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.PRIVATE_DIVERSO);
            case "issuer_header_errato" ->
                    raddClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.HEADER_ERRATO);
            case "issuer_over_50kb" -> raddClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.OVER_50KB);
            default -> throw new IllegalArgumentException();
        }
    }

    private ActStartTransactionRequest createActStartTransactionRequest(String operationId) {
        return new ActStartTransactionRequest().qrCode(this.qrCode).versionToken(this.versionToken).fileKey(this.documentUploadResponse != null ? this.documentUploadResponse.getValue1() : null).operationId(operationId).recipientTaxId(this.currentUserCf).recipientType(this.recipientType.equalsIgnoreCase("PF") ? ActStartTransactionRequest.RecipientTypeEnum.PF : ActStartTransactionRequest.RecipientTypeEnum.PG).iun(this.iun).operationDate(dateTimeFormatter.format(OffsetDateTime.now())).checksum(this.documentUploadResponse != null ? this.documentUploadResponse.getValue2() : null);
    }

    private AorStartTransactionRequest createAorStartTransactionRequest() {
        return new AorStartTransactionRequest().versionToken(this.versionToken).fileKey(this.fileKey).operationId(this.operationId == null ? generateRandomNumber() : this.operationId).recipientTaxId(this.currentUserCf).recipientType(this.recipientType.equalsIgnoreCase("PF") ? AorStartTransactionRequest.RecipientTypeEnum.PF : AorStartTransactionRequest.RecipientTypeEnum.PG).operationDate(dateTimeFormatter.format(OffsetDateTime.now())).checksum(this.documentUploadResponse != null ? this.documentUploadResponse.getValue2() : null);
    }

    private RaddOperator setOperatorRaddJWT(String raddOperatorType) {
        RaddOperator raddOperator = RaddOperator.valueOf(raddOperatorType);
        raddClient.setAuthTokenRadd(raddOperator.getIssuerType());
        return raddOperator;
    }

    private void waitFor(Long waitingTime) {
        try {
            Thread.sleep(waitingTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}