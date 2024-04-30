package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddFsuClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import static it.pagopa.pn.cucumber.utils.FiscalCodeGenerator.generateCF;
import static it.pagopa.pn.cucumber.utils.NotificationValue.generateRandomNumber;


@Slf4j
public class RaddFsuSteps {
    private final IPnRaddFsuClient raddFsuClient;
    private final PnExternalServiceClientImpl externalServiceClient;
    private final SharedSteps sharedSteps;
    private final PnPaB2bUtils pnPaB2bUtils;
    private ActInquiryResponse actInquiryResponse;
    private String qrCode;
    private String currentUserCf;
    private String operationid;
    private StartTransactionResponse startTransactionResponse;
    private StartTransactionResponse aorStartTransactionResponse;
    private final String uid = "1234556";
    private AORInquiryResponse aorInquiryResponse;
    private CompleteTransactionResponse completeTransactionResponse;
    private PnPaB2bUtils.Pair<String,String> documentUploadResponse;
    private AbortTransactionResponse abortActTransaction;
    private HttpStatusCodeException documentUploadError;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


    @Autowired
    public RaddFsuSteps(IPnRaddFsuClient raddFsuClient, PnExternalServiceClientImpl externalServiceClient,
                        PnPaB2bUtils pnPaB2bUtils, SharedSteps sharedSteps) {
        this.raddFsuClient = raddFsuClient;
        this.externalServiceClient = externalServiceClient;
        this.sharedSteps = sharedSteps;
        this.pnPaB2bUtils = pnPaB2bUtils;
    }

    @Given("viene verificata la presenza di atti e\\/o attestazioni per l'utente {string}")
    public void vieneVerificataLaPresenzaDiAttiEOAttestazioniPerLUtente(String cf) {
        AORInquiryResponse pf = this.raddFsuClient.aorInquiry("reprehenderit culpa enim", cf, "PF");
        System.out.println(pf);
    }

    @Given("viene richiesto il codice QR per lo IUN {string}")
    public void vieneRichiestoIlCodiceQRPerLoIUN(String iun) {
        HashMap<String, String> quickAccessLink = externalServiceClient.getQuickAccessLink(iun);
        log.debug("quickAccessLink: {}",quickAccessLink.toString());
        this.qrCode = quickAccessLink.get(quickAccessLink.keySet().toArray()[0]);
        log.debug("qrCode: {}",qrCode);
    }

    @When("L'operatore scansione il qrCode per recuperare gli atti")
    public void lOperatoreScansioneIlQrCodePerRecuperariGliAtti() {
        ActInquiryResponse actInquiryResponse = raddFsuClient.actInquiry(uid,this.currentUserCf, "PF", qrCode);
        log.info("actInquiryResponse: {}",actInquiryResponse);
        this.actInquiryResponse = actInquiryResponse;
    }

    private void selectUser(String cf){
        switch (cf.toUpperCase()){
            case "MARIO CUCUMBER" -> this.currentUserCf = sharedSteps.getMarioCucumberTaxID();
            case "MARIO GHERKIN" -> this.currentUserCf = sharedSteps.getMarioGherkinTaxID();
            case "SIGNOR CASUALE" -> this.currentUserCf = sharedSteps.getSentNotification().getRecipients().get(0).getTaxId();
            case "SIGNOR GENERATO" -> this.currentUserCf = generateCF(System.nanoTime());
            default ->  this.currentUserCf = cf;
        }
    }

    @Given("Il cittadino {string} mostra il QRCode {string}")
    public void ilCittadinoMostraIlQRCode(String cf, String qrCodeType) {
        selectUser(cf);
        qrCodeType = qrCodeType.toLowerCase();
        switch (qrCodeType) {
            case "malformato" -> {
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getSentNotification().getIun());
                this.qrCode = this.qrCode+"MALF";
            }
            case "inesistente" -> {
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getSentNotification().getIun());
                char toReplace = this.qrCode.charAt(0);
                char replace = toReplace == 'B' ? 'C' : 'B';
                this.qrCode = this.qrCode.replace(toReplace,replace);
            }
            case "appartenente a terzo" -> {
                if(this.currentUserCf.equalsIgnoreCase(sharedSteps.getSentNotification().getRecipients().get(0).getTaxId())){
                    throw new IllegalArgumentException();
                }
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getSentNotification().getIun());
            }
            case "corretto" -> {
                vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getSentNotification().getIun());
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private ActInquiryResponseStatus.CodeEnum getErrorCode(int errorCode){
        switch (errorCode){
            case 0 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_0;
            }
            case 1 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_1;
            }
            case 2 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_2;
            }
            case 3 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_3;
            }
            case 99 -> {
                return ActInquiryResponseStatus.CodeEnum.NUMBER_99;
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @Then("Viene restituito un messaggio di errore {string} con codice di errore {int}")
    public void vieneRestituitoUnMessaggioDiErrore(String errorType, int errorCode) {
        errorType = errorType.toLowerCase();
        ActInquiryResponseStatus.CodeEnum error = getErrorCode(errorCode);
        switch (errorType) {
            case "qrcode non valido", "cf non valido" -> {
                Assertions.assertEquals(false, actInquiryResponse.getResult());
                Assertions.assertNotNull( actInquiryResponse.getStatus());
                Assertions.assertEquals(error, actInquiryResponse.getStatus().getCode());
            }
            case "stampa già eseguita" ->{
                Assertions.assertEquals(false, actInquiryResponse.getResult());
                Assertions.assertNotNull( actInquiryResponse.getStatus());
                Assertions.assertNotNull(actInquiryResponse.getStatus().getMessage());
                Assertions.assertEquals(errorType.toLowerCase(), actInquiryResponse.getStatus().getMessage().toLowerCase());
                Assertions.assertEquals(error, actInquiryResponse.getStatus().getCode());
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @And("la scansione si conclude correttamente")
    public void laScansioneSiConcludeCorrettamente() {
        log.debug("actInquiryResponse {}",actInquiryResponse.toString());
        Assertions.assertEquals(true, actInquiryResponse.getResult());
        Assertions.assertNotNull( actInquiryResponse.getStatus());
        Assertions.assertEquals(ActInquiryResponseStatus.CodeEnum.NUMBER_0, actInquiryResponse.getStatus().getCode());
    }

    @And("vengono caricati i documento di identità del cittadino")
    public void vengonoCaricatiIDocumentoDiIdentitaDelCittadino() {
        uploadDocument(true);
    }

    @And("si inizia il processo di caricamento dei documento di identità del cittadino ma non si porta a conclusione")
    public void siIniziaIlProcessoDiCaricamentoDeiDocumentoDiIdentitàDelCittadinoMaNonSiPortaAConclusione() {
        uploadDocument(false);
    }

    private void uploadDocument(boolean usePresignedUrl){
        try {
            PnPaB2bUtils.Pair<String, String> uploadResponse = pnPaB2bUtils.preloadRadFsuDocument("classpath:/sample.pdf",usePresignedUrl);
            Assertions.assertNotNull(uploadResponse);
            this.documentUploadResponse = uploadResponse;
            log.info("documentUploadResponse: {}",documentUploadResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR")
    public void vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAAR() {
        this.operationid = generateRandomNumber();
        startTransactionAct(this.operationid);
    }

    @And("Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR utilizzando il precedente operationId")
    public void vengonoVisualizzatiSiaGliAttiSiaLeAttestazioniOpponibiliRiferitiAllaNotificaAssociataAllAARUtilizzandoIlPrecedenteOperationId() {
        startTransactionAct(this.operationid);
    }

    private void startTransactionAct(String operationid){
        ActStartTransactionRequest actStartTransactionRequest =
                new ActStartTransactionRequest()
                        .qrCode(this.qrCode)
                        .versionToken("string")
                        .fileKey(this.documentUploadResponse.getValue1())
                        .operationId(operationid)
                        .recipientTaxId(this.currentUserCf)
                        .recipientType(ActStartTransactionRequest.RecipientTypeEnum.PF)
                        //.operationDate(OffsetDateTime.now()) TODO: controllare
                        .checksum(this.documentUploadResponse.getValue2());
        System.out.println("actStartTransactionRequest: "+actStartTransactionRequest);
        this.startTransactionResponse = raddFsuClient.startActTransaction(uid, actStartTransactionRequest);
        System.out.println("startTransactionResponse: "+startTransactionResponse);
    }

    @And("l'operazione di download degli atti si conclude correttamente")
    public void lOperazioneDiDownloadDegliAttiSiConcludeCorrettamente() {
        Assertions.assertNotNull(this.startTransactionResponse.getUrlList());
        Assertions.assertFalse(this.startTransactionResponse.getUrlList().isEmpty());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus());
        Assertions.assertEquals(StartTransactionResponseStatus.CodeEnum.NUMBER_0,this.startTransactionResponse.getStatus().getCode());
    }


    @And("l'operazione di download degli atti genera un errore {string} con codice {int}")
    public void lOperazioneDiDownloadDegliAttiGeneraUnErroreConCodice(String errorDescription, int erroCode) {
        StartTransactionResponseStatus.CodeEnum error = getErrorCodeStartTransaction(erroCode);
        Assertions.assertNull(this.startTransactionResponse.getUrlList());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus());
        Assertions.assertEquals(error,this.startTransactionResponse.getStatus().getCode());
        Assertions.assertNotNull(this.startTransactionResponse.getStatus().getMessage());
        Assertions.assertEquals(errorDescription.trim().toLowerCase(),this.startTransactionResponse.getStatus().getMessage().toLowerCase());
    }

    private StartTransactionResponseStatus.CodeEnum getErrorCodeStartTransaction(int errorCode){
        //return StartTransactionResponseStatus.CodeEnum.valueOf("NUMBER_"+errorCode);
        switch (errorCode){
            case 0 -> {
                return StartTransactionResponseStatus.CodeEnum.NUMBER_0;
            }
            case 2 -> {
                return StartTransactionResponseStatus.CodeEnum.NUMBER_2;
            }
            case 99 -> {
                return StartTransactionResponseStatus.CodeEnum.NUMBER_99;
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @And("viene conclusa la visualizzati di atti ed attestazioni della notifica")
    public void vieneConclusaLaVisualizzatiDiAttiEdAttestazioniDellaNotifica() {
        CompleteTransactionRequest completeTransactionRequest =
                new CompleteTransactionRequest()
                        .operationId(this.operationid)
                        .operationDate(dateTimeFormatter.format(OffsetDateTime.now()));
        this.completeTransactionResponse = raddFsuClient.completeActTransaction(this.uid, completeTransactionRequest);
        System.out.println(completeTransactionResponse);
        Assertions.assertNotNull(completeTransactionResponse);
    }


    @Given("Il cittadino {string} chiede di verificare la presenza di notifiche")
    public void ilCittadinoChiedeDiVerificareLaPresenzaDiNotifiche(String cf) {
        selectUser(cf);
        this.aorInquiryResponse = raddFsuClient.aorInquiry(uid, cf, "PF");
    }

    @When("Il cittadino Signor casuale chiede di verificare la presenza di notifiche")
    public void ilCittadinoSignorCasualeChiedeDiVerificareLaPresenzaDiNotifiche() {
        selectUser("Signor casuale");
        this.aorInquiryResponse = raddFsuClient.aorInquiry(uid, this.currentUserCf, "PF");
    }

    @When("La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente")
    public void laVerificaAorMostraCorrettamenteLeNotificheInStatoIrreperibile() {
        Assertions.assertNotNull(this.aorInquiryResponse);
        Assertions.assertEquals(Boolean.TRUE, this.aorInquiryResponse.getResult());
        Assertions.assertNotNull(this.aorInquiryResponse.getStatus());
        Assertions.assertEquals(ResponseStatus.CodeEnum.NUMBER_0,this.aorInquiryResponse.getStatus().getCode());
        log.info("aorInquiryResponse: {}",this.aorInquiryResponse);
    }

    @Then("Vengono recuperati gli aar delle notifiche in stato irreperibile")
    public void vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibile() {
        this.operationid = generateRandomNumber();
        AorStartTransactionRequest aorStartTransactionRequest =
                new AorStartTransactionRequest()
                        .versionToken("string")
                        .fileKey(this.documentUploadResponse.getValue1())
                        .operationId(this.operationid)
                        .recipientTaxId(this.currentUserCf)
                        .recipientType(AorStartTransactionRequest.RecipientTypeEnum.PF)
                        .operationDate(dateTimeFormatter.format(OffsetDateTime.now()))
                        //.delegateTaxId("")
                        .checksum(this.documentUploadResponse.getValue2());
        this.aorStartTransactionResponse = raddFsuClient.startAorTransaction(this.uid, aorStartTransactionRequest);
    }

    @And("il recupero degli aar in stato irreperibile si conclude correttamente")
    public void ilRecuperoDegliAttiInStatoIrreperibileSiConcludeCorrettamente() {
        log.info("aorStartTransactionResponse: {}",this.aorStartTransactionResponse);

        Assertions.assertNotNull(this.aorStartTransactionResponse.getUrlList());
        Assertions.assertFalse(this.aorStartTransactionResponse.getUrlList().isEmpty());
        Assertions.assertNotNull(this.aorStartTransactionResponse.getStatus());
        Assertions.assertEquals(StartTransactionResponseStatus.CodeEnum.NUMBER_0,this.aorStartTransactionResponse.getStatus().getCode());
    }

    @And("il recupero degli aar in stato irreperibile si conclude correttamente e vengono restituiti {int} aar")
    public void ilRecuperoDegliAarInStatoIrreperibileSiConcludeCorrettamenteEVengonoRestituitiTuttiEGliAar(int aarNumber) {
        log.info("aorStartTransactionResponse: {}",this.aorStartTransactionResponse);

        Assertions.assertNotNull(this.aorStartTransactionResponse.getUrlList());
        Assertions.assertEquals(this.aorStartTransactionResponse.getUrlList().size(),aarNumber);
        Assertions.assertFalse(this.aorStartTransactionResponse.getUrlList().isEmpty());
        Assertions.assertNotNull(this.aorStartTransactionResponse.getStatus());
        Assertions.assertEquals(StartTransactionResponseStatus.CodeEnum.NUMBER_0,this.aorStartTransactionResponse.getStatus().getCode());
    }

    @And("il recupero degli aar genera un errore {string} con codice {int}")
    public void ilRecuperoDegliAarGeneraUnErroreConCodice(String errorType, int errorCode) {
        log.info("aorStartTransactionResponse: {}",this.aorStartTransactionResponse);

        errorType = errorType.toLowerCase();
        StartTransactionResponseStatus.CodeEnum error = getErrorCodeStartTransaction(errorCode);

        Assertions.assertNull(this.aorStartTransactionResponse.getUrlList());
        Assertions.assertNotNull( this.aorStartTransactionResponse.getStatus());
        Assertions.assertEquals(error,this.aorStartTransactionResponse.getStatus().getCode());
        Assertions.assertNotNull(this.aorStartTransactionResponse.getStatus().getMessage());
        Assertions.assertEquals(errorType,this.aorStartTransactionResponse.getStatus().getMessage().toLowerCase());
    }

    @When("La verifica della presenza di notifiche in stato irreperibile genera un errore {string} con codice {int}")
    public void laVerificaDellaPresenzaDiNotificheInStatoIrreperibiGeneraUnErroreConCodice(String errorType, int errorCode) {
        errorType = errorType.toLowerCase();
        ResponseStatus.CodeEnum error = getAorErrorCode(errorCode);
        switch (errorType) {
            case "non ci sono notifiche non consegnate per questo codice fiscale" -> {
                Assertions.assertEquals(false, this.aorInquiryResponse.getResult());
                Assertions.assertNotNull( this.aorInquiryResponse.getStatus());
                Assertions.assertEquals(error, this.aorInquiryResponse.getStatus().getCode());
            }
            default -> throw new IllegalArgumentException();
        }

        log.info("aorInquiryResponse: {}",this.aorInquiryResponse);
    }

    private ResponseStatus.CodeEnum getAorErrorCode(int errorCode){
        switch (errorCode){
            case 0 -> {
                return ResponseStatus.CodeEnum.NUMBER_0;
            }
            case 99 -> {
                return ResponseStatus.CodeEnum.NUMBER_99;
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @And("viene chiusa la transazione per il recupero degli aar")
    public void vieneDichiarataCompletataLaTransazionePerIlRecuperoDegliAar() {
        CompleteTransactionRequest completeTransactionRequest =
                new CompleteTransactionRequest()
                        .operationId(this.operationid)
                        .operationDate(dateTimeFormatter.format(OffsetDateTime.now()));
        this.completeTransactionResponse = raddFsuClient.completeAorTransaction(this.uid, completeTransactionRequest);
        log.info("completeTransactionResponse: {}",completeTransactionResponse);
    }

    @And("la chiusura delle transazione per il recupero degli aar non genera errori")
    public void laChiusuraDelleTransazionePerIlRecuperoDegliAarNonGeneraErrori() {
        Assertions.assertNotNull(this.completeTransactionResponse);
        Assertions.assertNotNull(this.completeTransactionResponse.getStatus());
        Assertions.assertEquals(TransactionResponseStatus.CodeEnum.NUMBER_0,this.completeTransactionResponse.getStatus().getCode());
    }

    @And("la chiusura delle transazione per il recupero degli aar ha generato l'errore {string} con statusCode {int}")
    public void laChiusuraDelleTransazionePerIlRecuperoDegliAarNonGeneraErrori(String error, int statusCode) {
        Assertions.assertNotNull(this.completeTransactionResponse);
        Assertions.assertNotNull(this.completeTransactionResponse.getStatus());
        Assertions.assertNotNull(this.completeTransactionResponse.getStatus().getCode());
        Assertions.assertEquals(new BigDecimal(statusCode),this.completeTransactionResponse.getStatus().getCode().getValue());
        Assertions.assertEquals(error,this.completeTransactionResponse.getStatus().getMessage());
    }

    @Given("vengono caricati i documento di identità del cittadino senza {string}")
    public void vengonoCaricatiIDocumentoDiIdentitàDelCittadinoSenza(String without) {
        String sha256;
        try {
            sha256 = pnPaB2bUtils.computeSha256( "classpath:/sample.pdf" );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DocumentUploadRequest documentUploadRequest = new DocumentUploadRequest();
        documentUploadRequest = without.equalsIgnoreCase("bundleId") ? documentUploadRequest : documentUploadRequest.bundleId("TEST");
        documentUploadRequest = without.equalsIgnoreCase("contentType") ? documentUploadRequest : documentUploadRequest.checksum(sha256);
        documentUploadRequest = without.equalsIgnoreCase("checksum") ? documentUploadRequest : documentUploadRequest.contentType("application/pdf");

        try{
            DocumentUploadResponse documentUploadResponse = raddFsuClient.documentUpload("1234556", documentUploadRequest);
            log.debug("DocumentUploadResponse: {}",documentUploadResponse);
        }catch(HttpStatusCodeException httpStatusCodeException){
            log.debug("HttpStatusCodeException {}",httpStatusCodeException);
            this.documentUploadError = httpStatusCodeException;
        }
    }

    @Then("il caricamente ha prodotto une errore http {int}")
    public void ilCaricamenteHaProdottoUneErroreHttp(int httpError) {
        Assertions.assertNotNull(this.documentUploadError);
        Assertions.assertEquals(this.documentUploadError.getStatusCode().value(),httpError);
    }

    @Then("la transazione viene abortita")
    public void laTransazioneVieneAbortita() {
        this.abortActTransaction = this.raddFsuClient.abortActTransaction(this.uid,
                new AbortTransactionRequest()
                        .operationId(this.operationid)
                        .operationDate(dateTimeFormatter.format(OffsetDateTime.now()))
                        .reason("TEST"));
    }

    @And("l'operazione di abort genera un errore {string} con codice {int}")
    public void lOperazioneDiAbortGeneraUnErroreConCodice(String error, int statusCode) {
        Assertions.assertNotNull(this.abortActTransaction);
        Assertions.assertNotNull(this.abortActTransaction.getStatus());
        Assertions.assertNotNull(this.abortActTransaction.getStatus().getCode());
        Assertions.assertEquals(new BigDecimal(statusCode),this.abortActTransaction.getStatus().getCode().getValue());
        Assertions.assertEquals(error,this.abortActTransaction.getStatus().getMessage());
    }
}