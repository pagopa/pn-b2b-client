package it.pagopa.pn.cucumber.steps.pa;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableAuthTokenRadd;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.Compress;
import it.pagopa.pn.client.b2b.pa.service.utils.RaddOperator;
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
import java.util.stream.IntStream;

import static it.pagopa.pn.cucumber.utils.FiscalCodeGenerator.generateCF;
import static it.pagopa.pn.cucumber.utils.NotificationValue.generateRandomNumber;

@Slf4j
public class RaddAltSteps {
    private final PnRaddAlternativeClientImpl raddAltClient;
    private final PnExternalServiceClientImpl externalServiceClient;
    private final SharedSteps sharedSteps;
    private final PnPaB2bUtils pnPaB2bUtils;
    private String qrCode;
    private String iun;
    private String fileZip;
    private String currentUserCf;
    private String recipientType;
    @Value("${pn.iun.120gg.fieramosca}")
    private String iunFieramosca120gg;
    @Value("${pn.iun.120gg.lucio}")
    private String iunLucio120gg;
    @Value("${pn.radd.alt.external.max-print-request}")
    private int maxPrintRequest;
    @Value("${pn.radd.alt.external.operatore-uploader-uid}")
    private String uidRaddUploaderOperator;
    @Value("${pn.radd.alt.external.operatore-standard-uid}")
    private String uidRaddStandardOperator;
    private String operationid;
    private ActInquiryResponse actInquiryResponse;
    private StartTransactionResponse startTransactionResponse;
    private StartTransactionResponse aorStartTransactionResponse;
    private AORInquiryResponse aorInquiryResponse;
    private final String uid = "1234556";
    private CompleteTransactionResponse completeTransactionResponse;
    private PnPaB2bUtils.Pair<String, String> documentUploadResponse;
    private AbortTransactionResponse abortActTransaction;
    private HttpStatusCodeException documentUploadError;
    private HttpStatusCodeException expectedStartTransactionException;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


    @Autowired
    public RaddAltSteps(PnRaddAlternativeClientImpl raddAltClient, PnExternalServiceClientImpl externalServiceClient,
                        PnPaB2bUtils pnPaB2bUtils, SharedSteps sharedSteps) {
        this.raddAltClient = raddAltClient;
        this.externalServiceClient = externalServiceClient;
        this.sharedSteps = sharedSteps;
        this.pnPaB2bUtils = pnPaB2bUtils;
    }

    @When("L'operatore scansione il qrCode per recuperare gli atti di {string}")
    public void lOperatoreScansioneIlQrCodePerRecuperariGliAttiAlternative(String cf) {
        selectUserRaddAlternative(cf);
        ActInquiryResponse actInquiryResponse = raddAltClient.actInquiry(uid,
                this.currentUserCf,
                this.recipientType,
                qrCode,
                null);

        log.info("actInquiryResponse: {}", actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }


    @When("L'operatore usa lo IUN {string} per recuperare gli atti di {string}")
    public void lOperatoreUsoIUNPerRecuperariGliAtti(String tipologiaIun, String cf) {
        selectUserRaddAlternative(cf);
            ActInquiryResponse actInquiryResponse = raddAltClient.actInquiry(uid,
                    this.currentUserCf,
                    this.recipientType,
                    null,
                    tipologiaIun.equalsIgnoreCase("corretto") ? this.iun= sharedSteps.getIunVersionamento() :
                            tipologiaIun.equalsIgnoreCase("errato") ? this.iun= "GLDZ-MGZD-AGAR-202402-Y-1" : null);

        log.info("actInquiryResponse: {}", actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    @When("L'operatore usa lo IUN {string} per recuperare gli atti di {string} da issuer {string}")
    public void lOperatoreUsoIUNPerRecuperariGliAttiDaIssuer(String tipologiaIun, String cf, String issuer) {
        changeRaddista(issuer);
        selectUserRaddAlternative(cf);
        ActInquiryResponse actInquiryResponse = null;
        try {
            actInquiryResponse = raddAltClient.actInquiry(uid,
                    this.currentUserCf,
                    this.recipientType,
                    null,
                    tipologiaIun.equalsIgnoreCase("corretto") ? this.iun = sharedSteps.getIunVersionamento() :
                            tipologiaIun.equalsIgnoreCase("errato") ? this.iun = "GLDZ-MGZD-AGAR-202402-Y-1" : null);
        } catch (HttpStatusCodeException exception) {
            sharedSteps.setNotificationError(exception);
        }
        log.info("actInquiryResponse: {}", actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    @When("L'operatore usa lo IUN {string} per recuperare gli atti di {string} con restituzione errore")
    public void lOperatoreUsoIUNPerRecuperariGliAttiWithError(String iun,String cf) {
        selectUserRaddAlternative(cf);
        ActInquiryResponse actInquiryResponse =null;
        try {
            actInquiryResponse = raddAltClient.actInquiry(uid,
                    this.currentUserCf,
                    this.recipientType,
                    null,
                    iun.equalsIgnoreCase("corretto") ? this.iun= sharedSteps.getIunVersionamento() :
                            iun.equalsIgnoreCase("errato") ? this.iun= "GLDZ-MGZD-AGAR-202402-Y-1" : null);
        }catch (HttpStatusCodeException exception){
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
            case "qrcode non valido",
                 "cf non valido" -> {
                Assertions.assertEquals(false, actInquiryResponse.getResult());
                Assertions.assertNotNull(actInquiryResponse.getStatus());
                Assertions.assertEquals(error, actInquiryResponse.getStatus().getCode());
            }
            case "stampa già eseguita",
                 "questa notifica è stata annullata dall’ente mittente",
                 "documenti non più disponibili",
                 "ko generico",
                 "input non valido",
                 "limite di 10 stampe superato" -> {
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
        this.operationid = generateRandomNumber();
        uploadDocumentRaddAlternative(true);
    }

    @And("vengono caricati i documento di identità del cittadino su radd alternative per errore")
    public void vengonoCaricatiIDocumentoDiIdentitaDelCittadinoPerErrore() {
        this.operationid = generateRandomNumber();
        uploadDocumentRaddOperatorAlternative(true, RaddOperator.UPLOADER);
    }

    @And("vengono caricati i documento di identità del cittadino su radd alternative dall'operatore RADD {string}")
    public void vengonoCaricatiIDocumentoDiIdentitàDelCittadinoSuRaddAlternativeDallOperatoreRADD(String raddOperatorType) {
        RaddOperator raddOperator = setOperatorRaddJWT(raddOperatorType);
        this.operationid = generateRandomNumber();
        Assertions.assertDoesNotThrow(()->
                uploadDocumentRaddOperatorAlternative(true, raddOperator));
    }

    @And("l'operatore {string} tenta di caricare i documento di identità del cittadino su radd alternative senza successo")
    public void lOperatoreTentaDiCaricareIDocumentoDiIdentitàDelCittadinoSuRaddAlternativeSenzaSuccesso(String raddOperatorType) {
        RaddOperator raddOperator = setOperatorRaddJWT(raddOperatorType);
        this.operationid = generateRandomNumber();
        documentUploadError = Assertions.assertThrows(HttpStatusCodeException.class,
                () ->  uploadDocumentRaddOperatorAlternative(true, raddOperator));
    }

    @And("si inizia il processo di caricamento dei documento di identità del cittadino ma non si porta a conclusione su radd alternative")
    public void siIniziaIlProcessoDiCaricamentoDeiDocumentoDiIdentitàDelCittadinoMaNonSiPortaAConclusione() {
        this.operationid = generateRandomNumber();
        uploadDocumentRaddAlternative(false);
    }

    @And("si inizia il processo di caricamento per radd {string} dei documento di identità del cittadino ma non si porta a conclusione su radd alternative")
    public void siIniziaIlProcessoDiCaricamentoPerRaddStandardDeiDocumentoDiIdentitàDelCittadinoMaNonSiPortaAConclusione(String raddOperatorType) {
        RaddOperator raddOperator = setOperatorRaddJWT(raddOperatorType);
        this.operationid = generateRandomNumber();
        uploadDocumentRaddOperatorAlternative(false, raddOperator);
    }

    private void uploadDocumentRaddAlternative(boolean usePresignedUrl) {
        try {
            creazioneZip();
            PnPaB2bUtils.Pair<String, String> uploadResponse = pnPaB2bUtils.preloadRaddAlternativeDocument("classpath:/"+this.fileZip, usePresignedUrl,this.operationid);
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
            PnPaB2bUtils.Pair<String, String> uploadResponse = pnPaB2bUtils.preloadRaddOperatoreAlternativeDocument("classpath:/"+this.fileZip, usePresignedUrl, this.operationid, raddOperator);
            Assertions.assertNotNull(uploadResponse);
            this.documentUploadResponse = uploadResponse;
            log.info("documentUploadResponse: {}", documentUploadResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative")
    public void vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAAR() {
        startTransactionActRaddAlternative(this.operationid,true);
    }

    @Then("Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative per operatore {string}")
    public void vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARDaRaddAlternativePerOperatoreStandard(String raddOperatorType) {
        RaddOperator raddOperator = RaddOperator.valueOf(raddOperatorType);
        this.operationid = this.operationid == null ? generateRandomNumber() : this.operationid;
        raddAltClient.setAuthTokenRadd(raddOperator.getIssuerType());
        startTransactionActRaddAlternativeForOperator(this.operationid,true, raddOperator.getUid());
    }


    @When("tentativo di recuperare gli atti delle notifiche associata all'AAR da radd alternative per operatore {string} senza successo")
    public void tentativoDiRecuperareGliAttiDelleNotificheAssociataAllAARDaRaddAlternativePerOperatoreSenzaSuccesso(String raddOperatorType) {
        this.expectedStartTransactionException = Assertions.assertThrows(HttpClientErrorException.class,
                () -> vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARDaRaddAlternativePerOperatoreStandard(raddOperatorType));
    }

    @And("Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR con lo stesso operationId dal raddista {string}")
    public void vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARUtilizzandoIlPrecedenteOperationIdOrganizzazioneDiversa(String raddista ) {
        changeRaddista(raddista);
        startTransactionActRaddAlternative(this.operationid, true);
    }

    @Then("Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative senza ritentativi")
    public void vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARSenzaRetry() {
        startTransactionActRaddAlternative(this.operationid, false);
    }

    protected void startTransactionActRaddAlternative(String operationid, boolean retry) {
        ActStartTransactionRequest actStartTransactionRequest = createActStartTransactionRequest(operationid);
        System.out.println("actStartTransactionRequest: " + actStartTransactionRequest);
        this.startTransactionResponse = raddAltClient.startActTransaction(uid, actStartTransactionRequest);

        if(this.startTransactionResponse.getStatus().getCode().equals(StartTransactionResponseStatus.CodeEnum.NUMBER_2) && retry){
            waitFor(this.startTransactionResponse.getStatus().getRetryAfter().longValue());
            this.startTransactionResponse = raddAltClient.startActTransaction(uid, actStartTransactionRequest);
        }
        System.out.println("startTransactionResponse: " + startTransactionResponse);
    }

    private void startTransactionActRaddAlternativeForOperator(String operationid, boolean retry, String uidRaddOperator) {
        ActStartTransactionRequest actStartTransactionRequest = createActStartTransactionRequest(operationid);

        System.out.println("actStartTransactionRequest: " + actStartTransactionRequest);
        this.startTransactionResponse = raddAltClient.startActTransaction(uidRaddOperator, actStartTransactionRequest);

        if(this.startTransactionResponse.getStatus().getCode().equals(StartTransactionResponseStatus.CodeEnum.NUMBER_2) && retry){
            waitFor(this.startTransactionResponse.getStatus().getRetryAfter().longValue());
            this.startTransactionResponse = raddAltClient.startActTransaction(uid, actStartTransactionRequest);
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
    public void loperazioneDiDownloadRestituisceTotDocumenti(Integer documenti) {
        Assertions.assertNotNull(this.startTransactionResponse.getDownloadUrlList());
        Assertions.assertFalse(this.startTransactionResponse.getDownloadUrlList().isEmpty());
        Assertions.assertEquals(documenti, this.startTransactionResponse.getDownloadUrlList().size());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus());
        Assertions.assertEquals(StartTransactionResponseStatus.CodeEnum.NUMBER_0, this.startTransactionResponse.getStatus().getCode());
    }

    @And("si verifica se il file richiede l'autenticazione")
    public void siVerificaSeIlFileRichiedeLAutenticazione() {
        Assertions.assertNotNull(this.startTransactionResponse.getDownloadUrlList());
        for (DownloadUrl download : this.startTransactionResponse.getDownloadUrlList() ) {
            log.info("downloadData: {}",download);
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
        //return StartTransactionResponseStatus.CodeEnum.valueOf("NUMBER_"+errorCode);
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
        CompleteTransactionRequest completeTransactionRequest =
                new CompleteTransactionRequest()
                        .operationId(this.operationid)
                        .operationDate(dateTimeFormatter.format(OffsetDateTime.now()));
        this.completeTransactionResponse = raddAltClient.completeActTransaction(this.uid, completeTransactionRequest);
        System.out.println(completeTransactionResponse);
        Assertions.assertNotNull(completeTransactionResponse);
    }

    @Given("la persona (fisica)(giuridica) {string} chiede di verificare la presenza di notifiche")
    public void ilCittadinoChiedeDiVerificareLaPresenzaDiNotifiche(String cf) {
        selectUserRaddAlternative(cf);
        this.aorInquiryResponse = raddAltClient.aorInquiry(uid,
                this.currentUserCf,
                this.recipientType);
    }

    @And("la persona fisica {string} chiede di verificare ad operatore radd {string} la presenza di notifiche")
    public void laPersonaFisicaChiedeDiVerificareAdOperatoreRaddLaPresenzaDiNotifiche(String citizen, String raddOperatorType) {
        RaddOperator raddOperator = RaddOperator.valueOf(raddOperatorType);
        selectUserRaddAlternative(citizen);
        this.aorInquiryResponse = raddAltClient.aorInquiry(raddOperator.getUid(),
                this.currentUserCf,
                this.recipientType);
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
        AorStartTransactionRequest aorStartTransactionRequest =
                new AorStartTransactionRequest()
                        .versionToken("string")
                        .fileKey(this.documentUploadResponse.getValue1())
                        .operationId(this.operationid)
                        .recipientTaxId(this.currentUserCf)
                        .recipientType(this.recipientType.equalsIgnoreCase("PF")?AorStartTransactionRequest.RecipientTypeEnum.PF:
                                AorStartTransactionRequest.RecipientTypeEnum.PG)
                        .operationDate(dateTimeFormatter.format(OffsetDateTime.now()))
                        //.delegateTaxId("")
                        .checksum(this.documentUploadResponse.getValue2());
        this.aorStartTransactionResponse = raddAltClient.startAorTransaction(this.uid, aorStartTransactionRequest);
    }

    @Then("Vengono recuperati gli aar delle notifiche in stato irreperibile della persona (fisica)(giuridica) 2 volte su radd alternative")
    public void vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibile2volte() {
        AorStartTransactionRequest aorStartTransactionRequest = createAorStartTransactionResponse();
        this.aorStartTransactionResponse = raddAltClient.startAorTransaction(this.uid, aorStartTransactionRequest);
        this.aorStartTransactionResponse = raddAltClient.startAorTransaction(this.uid, aorStartTransactionRequest);
    }

    @Then("Vengono recuperati gli aar delle notifiche in stato irreperibile della persona (fisica)(giuridica) con lo stesso operationId dal raddista {string}")
    public void vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileStessoOperationId(String organizzazione) {
        changeRaddista(organizzazione);
        AorStartTransactionRequest aorStartTransactionRequest = createAorStartTransactionResponse();
        this.aorStartTransactionResponse = raddAltClient.startAorTransaction(this.uid,
                aorStartTransactionRequest);
    }

    @Then("Vengono recuperati gli aar delle notifiche in stato irreperibile della persona (fisica)(giuridica) su radd alternative da operatore radd {string}")
    public void vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileDaOperatoreRaddType(String raddOperatorType) {
        RaddOperator raddOperator = setOperatorRaddJWT(raddOperatorType);
        AorStartTransactionRequest aorStartTransactionRequest = createAorStartTransactionResponse();
        this.aorStartTransactionResponse = raddAltClient.startAorTransaction(raddOperator.getUid(), aorStartTransactionRequest);
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
    public void laVerificaDellaPresenzaDiNotificheInStatoIrreperibiGeneraUnErroreConCodice(String errorType, int errorCode) {
        errorType = errorType.toLowerCase();
        ResponseStatus.CodeEnum error = getAorErrorCode(errorCode);
        switch (errorType) {
            case "non ci sono notifiche non consegnate per questo codice fiscale" -> {
                Assertions.assertEquals(false, this.aorInquiryResponse.getResult());
                Assertions.assertNotNull(this.aorInquiryResponse.getStatus());
                Assertions.assertEquals(error, this.aorInquiryResponse.getStatus().getCode());
            }
            default -> throw new IllegalArgumentException();
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
        this.expectedStartTransactionException = Assertions.assertThrows(HttpClientErrorException.class,
                () -> vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileDaOperatoreRaddType(raddOperatorType));
    }

    @And("il tentativo genera un errore {int} {string} con il messaggio {string}")
    public void lErroreDelRecuperoDegliAarDelleNotificheGenereUnErroreConIlMessaggio(int errorCode, String errorType, String errorMessage) {
        Assertions.assertNotNull(expectedStartTransactionException);
        Assertions.assertNotNull(expectedStartTransactionException.getStatusCode());
        Assertions.assertNotNull(expectedStartTransactionException.getMessage());
        Assertions.assertEquals(errorCode, expectedStartTransactionException.getStatusCode().value());
        Assertions.assertEquals(errorType, expectedStartTransactionException.getStatusText());
        Assertions.assertTrue(expectedStartTransactionException.getMessage().contains(errorMessage));
    }

    @And("viene chiusa la transazione per il recupero degli aar su radd alternative")
    public void vieneDichiarataCompletataLaTransazionePerIlRecuperoDegliAar() {
        CompleteTransactionRequest completeTransactionRequest =
                new CompleteTransactionRequest()
                        .operationId(this.operationid)
                        .operationDate(dateTimeFormatter.format(OffsetDateTime.now()));
        this.completeTransactionResponse = raddAltClient.completeAorTransaction(this.uid, completeTransactionRequest);
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
    public void vengonoCaricatiIDocumentoDiIdentitàDelCittadinoSenza(String without) {
        String sha256;
        try {
            sha256 = pnPaB2bUtils.computeSha256("");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DocumentUploadRequest documentUploadRequest = new DocumentUploadRequest();
        documentUploadRequest = without.equalsIgnoreCase("contentType") ? documentUploadRequest : documentUploadRequest.checksum(sha256);

        try {
            DocumentUploadResponse documentUploadResponse = raddAltClient.documentUpload(this.uid,documentUploadRequest);
            log.debug("DocumentUploadResponse: {}", documentUploadResponse);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            this.documentUploadError = httpStatusCodeException;
        }
    }

    @Then("il caricamente ha prodotto une errore http {int} su radd alternative")
    public void ilCaricamenteHaProdottoUneErroreHttp(int httpError) {
        Assertions.assertNotNull(this.documentUploadError);
        Assertions.assertEquals(this.documentUploadError.getStatusCode().value(), httpError);
    }

    @Then("la transazione viene abortita per gli {string}")
    public void laTransazioneVieneAbortitaAor(String tipologia) {
        switch (tipologia.toLowerCase()) {
            case "aor" -> this.abortActTransaction = this.raddAltClient.abortAorTransaction(this.uid,
                    new AbortTransactionRequest()
                            .operationId(this.operationid)
                            .operationDate(dateTimeFormatter.format(OffsetDateTime.now()))
                            .reason("TEST"));
            case "act" -> this.abortActTransaction = this.raddAltClient.abortActTransaction(this.uid,
                    new AbortTransactionRequest()
                            .operationId(this.operationid)
                            .operationDate(dateTimeFormatter.format(OffsetDateTime.now()))
                            .reason("TEST"));
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

    protected void selectUserRaddAlternative(String cf) {
        switch (cf.toUpperCase()) {
            case "MARIO CUCUMBER" -> {
                this.currentUserCf = sharedSteps.getMarioCucumberTaxID();
                this.recipientType="PF";
            }
            case "MARIO GHERKIN" -> {
                this.currentUserCf = sharedSteps.getMarioGherkinTaxID();
                this.recipientType="PF";
            }
            case "LEONARDO DA VINCI" -> {
                this.currentUserCf = "DVNLRD52D15M059P";
                this.recipientType="PF";
            }
            case "CUCUMBERSPA" -> {
                this.currentUserCf = sharedSteps.getCucumberSpataxId();
                this.recipientType="PG";
            }
            case "SIGNOR CASUALE" -> {
                this.currentUserCf = sharedSteps.getSentNotification().getRecipients().get(0).getTaxId();
                this.recipientType="PF";
            }
            case "SIGNOR GENERATO" -> {
                this.currentUserCf = generateCF(System.nanoTime());
                this.recipientType="PF";
            }
            case "GHERKIN IRREPERIBILE" -> {
                this.currentUserCf = sharedSteps.getGherkinIrreperibileTaxId();
                this.recipientType="PG";
            }
            default -> this.currentUserCf = cf;
        }
    }

    @Given("Il cittadino {string} come destinatario {int} mostra il QRCode {string}")
    public void ilCittadinoMostraIlQRCodeRaddAlternative(String cf,Integer destinatario, String qrCodeType) {
        selectUserRaddAlternative(cf);
        qrCodeType = qrCodeType.toLowerCase();
        switch (qrCodeType) {
            case "malformato" -> {
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getSentNotification().getIun(), destinatario);
                this.qrCode = this.qrCode+"MALF";
            }
            case "inesistente" -> {
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getSentNotification().getIun(), destinatario);
                char toReplace = this.qrCode.charAt(0);
                char replace = toReplace == 'B' ? 'C' : 'B';
                this.qrCode = this.qrCode.replace(toReplace,replace);
            }
            case "appartenente a terzo" -> {
                if(this.currentUserCf.equalsIgnoreCase(sharedSteps.getSentNotification().getRecipients().get(0).getTaxId())){
                    throw new IllegalArgumentException();
                }
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getSentNotification().getIun(), destinatario);
            }
            case "corretto" -> {
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getSentNotification().getIun(), destinatario);
            }
            case "dopo 120gg" -> {
                    if (this.currentUserCf.equalsIgnoreCase(sharedSteps.getMarioCucumberTaxID())) {
                        vieneRichiestoIlCodiceQRPerLoIUN(this.iunFieramosca120gg, destinatario);
                    }else if (this.currentUserCf.equalsIgnoreCase(sharedSteps.getCucumberSpataxId())) {
                        vieneRichiestoIlCodiceQRPerLoIUN(this.iunLucio120gg, destinatario);
                    }else{
                        throw new IllegalArgumentException();
                    }
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @Given("viene richiesto il codice QR per lo IUN {string} per il destinatario {int} su radd alternative")
    public void vieneRichiestoIlCodiceQRPerLoIUN(String iun, Integer destinatario) {
        HashMap<String, String> quickAccessLink = externalServiceClient.getQuickAccessLink(iun);
        log.debug("quickAccessLink: {}",quickAccessLink.toString());
        this.qrCode = quickAccessLink.get(quickAccessLink.keySet().toArray()[destinatario]);
        log.debug("qrCode: {}",qrCode);
    }

    @When("L'operatore scansione il qrCode per recuperare gli atti da radd alternative")
    public void lOperatoreScansioneIlQrCodePerRecuperariGliAtti() {
        ActInquiryResponse actInquiryResponse = raddAltClient.actInquiry(uid, this.currentUserCf, this.recipientType, qrCode, iun);
        log.info("actInquiryResponse: {}",actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    @When("L'operatore {string} scansione il qrCode per recuperare gli atti da radd alternative")
    public void lOperatoreUploaderScansioneIlQrCodePerRecuperariGliAtti(String raddOperatorType) {
        RaddOperator raddOperator = setOperatorRaddJWT(raddOperatorType);
        ActInquiryResponse actInquiryResponse = raddAltClient.actInquiry(raddOperator.getUid(), this.currentUserCf, this.recipientType, qrCode, null);
        log.info("actInquiryResponse: {}",actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    @Given("L'operatore esegue il download del frontespizio del operazione {string}")
    public void lOperatoreEsegueDownloadFrontespizio(String operationType) {
        try {
        downloadFrontespizio(operationType.toUpperCase(),this.operationid,null);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    " {OperatiodId: "+this.operationid +" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }

    @Given("L'operatore esegue il download del frontespizio del operazione {string} con attachmentId {string}")
    public void lOperatoreEsegueDownloadFrontespizioAttachmentIdNonEsistente(String operationType,String attachmentId) {
        try {
            downloadFrontespizio(operationType.toUpperCase(), this.operationid, attachmentId);
        }catch (HttpStatusCodeException exception){
            sharedSteps.setNotificationError(exception);
        }
    }

    @When("L'operatore scansiona il qrCode e stampa gli atti per {int} volte senza errori")
    public void lOperatoreScansionaIlQrCodeEStampaGliAttiPerIntVolteSenzaErrori(int iteration) {
        IntStream.range(0, iteration).forEach(i -> {
            lOperatoreScansioneIlQrCodePerRecuperariGliAtti();
            laScansioneSiConcludeCorrettamenteAlternative();
            vengonoCaricatiIDocumentoDiIdentitàDelCittadinoSuRaddAlternativeDallOperatoreRADD("UPLOADER");
            vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAAR();
            lOperazioneDiDownloadDegliAttiSiConcludeCorrettamente();
            vieneConclusaLaVisualizzatiDiAttiEdAttestazioniDellaNotifica();
            laChiusuraDelleTransazionePerIlRecuperoDegliAarNonGeneraErrori();
        });
    }


    @When("L'operatore scansiona il qrCode e stampa gli atti per il numero di volte consentito")
    public void lOperatoreScansionaIlQrCodeEStampaGliAttiPerIlNumeroDiVolteConsentito() {
        IntStream.range(0, maxPrintRequest).forEach(i -> {
            lOperatoreScansioneIlQrCodePerRecuperariGliAtti();
            laScansioneSiConcludeCorrettamenteAlternative();
            vengonoCaricatiIDocumentoDiIdentitaDelCittadino();
            //vengonoCaricatiIDocumentoDiIdentitàDelCittadinoSuRaddAlternativeDallOperatoreRADD("STANDARD");
            vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAAR();
            lOperazioneDiDownloadDegliAttiSiConcludeCorrettamente();
            vieneConclusaLaVisualizzatiDiAttiEdAttestazioniDellaNotifica();
            laChiusuraDelleTransazionePerIlRecuperoDegliAarNonGeneraErrori();
        });
    }

    private void downloadFrontespizio(String operationType,String operationid,String attachmentId) {
        byte[] download = raddAltClient.documentDownload(operationType,
                operationid,
                attachmentId);
        Assertions.assertNotNull(download);
        pnPaB2bUtils.stampaPdfTramiteByte(download, "target/classes/frontespizio" + generateRandomNumber() + ".pdf");
    }

    public void creazioneZip() throws IOException {
        String[] files = {};
        if(this.recipientType.equalsIgnoreCase("PG")) {
            files = new String[]{"target/classes/sample.pdf"};
        }

        InputStream[] filesJson = {creazioneJSON()};
        String fileDestination="file"+generateRandomNumber()+".zip";
        Compress c = new Compress(filesJson,files, "target/classes/"+fileDestination);
        c.zip();
        this.fileZip = fileDestination;
    }

    public InputStream creazioneJSON(){
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("operationId", this.operationid);
        jsonMap.put("docType", "Carta d'indentità");
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
            URI zip_disk = URI.create("target/classes/"+this.fileZip);
            File file = new File(zip_disk.getPath());
            boolean deleted = file.delete();
            System.out.println("delete "+deleted);
            }
    }

    public void changeRaddista(String raddista) {
        switch (raddista.toLowerCase()) {
            case "issuer_1" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_1);
            case "issuer_2" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_2);
            case "issuer_non_censito" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_NON_CENSITO);
            case "issuer_dati_errati" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.DATI_ERRATI);
            case "issuer_scaduto" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_SCADUTO);
            case "issuer_aud_errata" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.AUD_ERRATA);
            case "issuer_kid_diverso" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.KID_DIVERSO);
            case "issuer_private_diverso" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.PRIVATE_DIVERSO);
            case "issuer_header_errato" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.HEADER_ERRATO);
            case "issuer_over_50kb" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.OVER_50KB);
            default -> throw new IllegalArgumentException();
        }
    }

    private ActStartTransactionRequest createActStartTransactionRequest(String operationid) {
        return new ActStartTransactionRequest()
                .qrCode(this.qrCode)
                .versionToken("string")
                .fileKey(this.documentUploadResponse != null ? this.documentUploadResponse.getValue1() : null)
                .operationId(operationid)
                .recipientTaxId(this.currentUserCf)
                .recipientType(this.recipientType.equalsIgnoreCase("PF") ? ActStartTransactionRequest.RecipientTypeEnum.PF :
                        ActStartTransactionRequest.RecipientTypeEnum.PG)
                .iun(this.iun)
                .operationDate(dateTimeFormatter.format(OffsetDateTime.now()))
                .checksum(this.documentUploadResponse != null ? this.documentUploadResponse.getValue2() : null);
    }

    private AorStartTransactionRequest createAorStartTransactionResponse() {
        return new AorStartTransactionRequest()
                .versionToken("string")
                .fileKey(this.documentUploadResponse != null ? this.documentUploadResponse.getValue1() : null)
                .operationId(this.operationid == null ? generateRandomNumber() : this.operationid)
                .recipientTaxId(this.currentUserCf)
                .recipientType(this.recipientType.equalsIgnoreCase("PF") ? AorStartTransactionRequest.RecipientTypeEnum.PF :
                        AorStartTransactionRequest.RecipientTypeEnum.PG)
                .operationDate(dateTimeFormatter.format(OffsetDateTime.now()))
                //.delegateTaxId("")
                .checksum(this.documentUploadResponse != null ? this.documentUploadResponse.getValue2() : null);
    }

    private RaddOperator setOperatorRaddJWT(String raddOperatorType) {
        RaddOperator raddOperator = RaddOperator.valueOf(raddOperatorType);
        raddAltClient.setAuthTokenRadd(raddOperator.getIssuerType());
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