package it.pagopa.pn.cucumber.steps.recipient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyMessage;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.CIEValidationData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateDto;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.service.IPnAppIOB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnMandateAppIoClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnMandateAppIoClientImpl;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.AcceptRequestDto;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.steps.utilitySteps.CieGeneratorTool;
import it.pagopa.pn.cucumber.steps.utilitySteps.Costanti;
import it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario;
import it.pagopa.pn.cucumber.steps.utilitySteps.LollipopHeader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import static it.pagopa.pn.cucumber.steps.utilitySteps.LollipopHeader.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Slf4j
@Data
public class DelegheTemporaneeSteps {

    private final SharedSteps sharedSteps;

    private final RicezioneNotificheWebDelegheSteps ricezioneNotificheWebDelegheSteps;

    private final IPnMandateAppIoClient mandateAppIoClient;

    private final IPnAppIOB2bClient appIOB2bClient;

    private final CieGeneratorTool cieGeneratorTool;

    private final String bucketS3;

    private final String appIoApiKey;

    private final String lollipopUserId;

    private String qrCode;

    private MandateCreationResponse mandateCreationResponse;

    private MandateDto mandateDtoB2b;

    private List<it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto> mandatesByDelegate;

    private List<it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto> mandatesByDelegator;

    private HttpStatusCodeException error;

    //qrCode valido, ma relativo a hotfix, per dare errore quando la suite gira in DEV/TEST/UAT
    private static final String VALID_QRCODE_404 = "?aar=S05EQS1OUEFHLVZBTkEtMjAyNTAyLUotMV9QRi00MmQ5ODJlZi0yNTc4LTQ3ODUtOTg0Yy04YzE5ZjM3NTZlNzlfMWY2NzVlNWQtYjcyNi00NzNkLWJlZTQtZDIxZjk5ZGQwN2Jm";

    @Autowired
    public DelegheTemporaneeSteps(SharedSteps sharedSteps,
                                  RicezioneNotificheWebDelegheSteps ricezioneNotificheWebDelegheSteps,
                                  PnMandateAppIoClientImpl mandateAppIoClient,
                                  IPnAppIOB2bClient appIOB2bClient,
                                  CieGeneratorTool cieGeneratorTool,
                                  @Value("${pn-deleghe-temporanee-bucket-s3}") String bucketS3,
                                  @Value("${pn.external.appio.api-key}") String appIoApiKey,
                                  @Value("${pn-lollipop-user-id}") String lollipopUserId) {
        this.sharedSteps = sharedSteps;
        this.mandateAppIoClient = mandateAppIoClient;
        this.ricezioneNotificheWebDelegheSteps = ricezioneNotificheWebDelegheSteps;
        this.appIOB2bClient = appIOB2bClient;
        this.cieGeneratorTool = cieGeneratorTool;
        this.bucketS3 = bucketS3;
        this.appIoApiKey = appIoApiKey;
        this.lollipopUserId = lollipopUserId;
    }

    //metodo di background
    @Given("vengono settati i parametri per il tool CIE")
    public void setToolCieParameter() {
        log.info("Inizio il setting dei parametri");
        System.setProperty("cie.generator.bucket", bucketS3);
        System.setProperty("cie.generator.file-key", "pn-mandate/csca-masterlist/catest.zip");
        log.info("Parametri settati");
        System.getenv().entrySet().forEach(x -> log.info("PARAM : " + x));
    }

    @Given("{destinatario} rifiuta l'eventuale delega permanente da parte di {destinatario}")
    public void rejectPermanentMandateIfPresent(Destinatario delegate, Destinatario delegator) {
        ricezioneNotificheWebDelegheSteps.setBearerToken(delegate.getDenomination());
        String delegatorTaxId = delegator.getTaxId();

        List<it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto> mandateList = ricezioneNotificheWebDelegheSteps.getWebMandateClient().searchMandatesByDelegate(delegatorTaxId, null);

        it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto mandateDto = null;
        for (it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto mandate : mandateList) {
            log.debug("MANDATE-LIST: {}", mandateList);
            if (Objects.requireNonNull(mandate.getDelegator()).getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)) {
                mandateDto = mandate;
                break;
            }
        }
        if (mandateDto != null) {
            try {
                ricezioneNotificheWebDelegheSteps.getWebMandateClient().rejectMandate(mandateDto.getMandateId());
            } catch (HttpStatusCodeException exception) {
                if (exception.getRawStatusCode() == 404) {
                    log.info("L'esecuzione in parallelo di altri test ha causato il 404, niente di grave");
                }
            }
        }
    }

    //delegator superfluo come parametro, ma aiuta ai fini della leggibilità dello scenario
    @When("{destinatario} viene temporaneamente delegato da {destinatario} passando {string}")
    public void creaDelegaTemporanea(Destinatario delegate, Destinatario delegator, String inputParamsType) {
        setQrCode(inputParamsType);
        MandateCreationRequest mandateCreationRequest = new MandateCreationRequest();
        mandateCreationRequest.setAarQrCodeValue(qrCode);
        String taxId = delegate.getTaxId();
        String lollipopUserId = delegate.getTaxId();
        switch (inputParamsType.toUpperCase()) {
            //qrCode valido, ma relativo a hotfix, per dare errore quando la suite gira in DEV/TEST/UAT
            case "QRCODE INESISTENTE" ->
                    mandateCreationRequest.setAarQrCodeValue(getQRPathEnvironmentBased() + VALID_QRCODE_404);
            case "QRCODE NON VALIDO" -> mandateCreationRequest.setAarQrCodeValue("invalid");
            case "TAXID NULL" -> taxId = null;
            case "EMPTY REQUEST BODY" -> mandateCreationRequest = null;
            case "CX TAX ID E LOLLIPOP USER ID NON COINCIDENTI" -> lollipopUserId = Costanti.GALILEO_GALILEI_TAX_ID;
        }
        try {
            mandateCreationResponse = mandateAppIoClient.createIOMandate(
                    taxId, null, null, "PublicKey", null,
                    null, "AuthJwt", lollipopUserId, null, null,
                    mandateCreationRequest);
            checkMandateCreation();
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
    }

    private void setQrCode(String inputParamsType) {
        String environment = B2bUtils.getEnvironment(sharedSteps.getContext());
        String environmentPath;
        switch (environment) {
            case "dev" -> environmentPath = "http://cittadini.dev.notifichedigitali.it/io";
            case "test" -> environmentPath = "http://cittadini.test.notifichedigitali.it/io";
            case "uat" -> environmentPath = "https://cittadini.uat.notifichedigitali.it/io/";
            default -> throw new IllegalArgumentException("Invalid environment name: " + environment);
        }
        environmentPath += "?aar=";
        switch (inputParamsType.toUpperCase()) {
            case "QRCODE NON VALIDO" -> qrCode = "invalid";
            case "QRCODE INESISTENTE" -> qrCode = environmentPath + VALID_QRCODE_404;
            default ->
                    qrCode = environmentPath + sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0);
        }
        log.info("QR code settato: {}", qrCode);
    }

    @Then("la delega temporanea è stata correttamente creata")
    public void checkMandateCreation() {
        assertThat(mandateCreationResponse).as("La response di creazione delega non dev'essere null").isNotNull();
        assertThat(mandateCreationResponse.getMandate()).as("Il mandate della create response non dev'essere null").isNotNull();
        mandateDtoB2b = mandateCreationResponse.getMandate();
        log.info("CREATED MANDATE TEMP: " + mandateCreationResponse.getMandate());
    }

    @When("la delega temporanea di {destinatario} viene accettata da {destinatario} passando {string}")
    public void accettaDelegaTemporanea(Destinatario delegator, Destinatario delegate, String inputParamsType) {
        String taxId = delegate.getTaxId();
        String delegatorTaxId = delegator.getTaxId();
        String lollipopUserId = delegate.getTaxId();
        String mandateId = mandateDtoB2b.getMandateId();

        CIEValidationData cieValidationData = getCieValidationData(
                delegatorTaxId,
                inputParamsType.equalsIgnoreCase("SIGNED NONCE ERRATO") ? "00000" : mandateDtoB2b.getVerificationCode(),
                inputParamsType);

        switch (inputParamsType.toUpperCase()) {
            //mandateId valido ma inesistente preso da ambiente di hotfix destinato a dare 404 quando la suite gira in DEV/TEST/UAT
            case "MANDATE ID INESISTENTE" -> mandateId = "82e80ed2-d93b-4e85-9767-cc6ae150fb80";
            case "MANDATE ID NON VALIDO" -> mandateId += "invalid";
            case "MANDATE ID VUOTO" -> mandateId = null;
            case "NULL REQUEST" -> cieValidationData = null;
            case "EMPTY REQUEST BODY" -> {
                cieValidationData.setMrtdData(null);
                cieValidationData.setNisData(null);
                cieValidationData.setSignedNonce(null);
            }
            case "NIS DATA CIE ERRATO" -> {
                String nisDataPubKey = cieValidationData.getNisData().getPubKey();
                String firstChar = nisDataPubKey.substring(0, 1);
                String replacement = firstChar.equals("A") ? "B" : "A";
                nisDataPubKey = replacement + nisDataPubKey.substring(1);
                cieValidationData.getNisData().setPubKey(nisDataPubKey);
            }
            case "MRTD DATA CIE ERRATO" -> {
                String mrtdDataDg1 = cieValidationData.getMrtdData().getDg1();
                String firstChar = mrtdDataDg1.substring(0, 1);
                String replacement = firstChar.equals("A") ? "B" : "A";
                mrtdDataDg1 = replacement + mrtdDataDg1.substring(1);
                cieValidationData.getNisData().setPubKey(mrtdDataDg1);
            }
            case "CX TAX ID E LOLLIPOP USER ID NON COINCIDENTI" -> lollipopUserId = Costanti.GALILEO_GALILEI_TAX_ID;
        }
        try {
            mandateAppIoClient.acceptIOMandate(
                    taxId,
                    mandateId,
                    null,
                    null,
                    "PublicKey",
                    null,
                    null,
                    "AuthJwt",
                    lollipopUserId,
                    null,
                    null,
                    cieValidationData);
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
    }

    //TODO: importante, verificare che in tutti gli ambienti, la validità di una delega impostata sia sempre a 5 minuti(accettazione) e 10 minuti (validità delega)
    @Given("attendo {int} minuti affinché la {string} scada")
    public void wasteTime(int minutes, String operation) {
        log.info("Attendo " + minutes + " minuti affinché la " + operation + " scada");
        long delayInMilliseconds = minutes * 60000L;
        try {
            Thread.sleep(delayInMilliseconds);
            log.info("Sono trascorsi " + minutes + " minuti");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("L'attesa è stata interrotta: " + e.getMessage(), e);
        }
    }

    //il metodo viene richiamato da altri step del codice, ma lo step dev'essere invocato solo a fini di debug
    @And("DEBUGONLY test cie utente {string} nonce {string} con {string}")
    public CIEValidationData getCieValidationData(String delegatorTaxId, String nonce, String inputParamsType) {
        Path path = Path.of("lib/output");
        LocalDate expirationDate = LocalDate.now().plusYears(1L);
        String cieOwnerTaxId = delegatorTaxId;
        switch (inputParamsType.toUpperCase()) {
            case "DATI DI UNA CIE SCADUTA" -> expirationDate = LocalDate.now().minusYears(1L);
            case "DATI CIE DI UTENTE DIVERSO DAL DESTINATARIO" -> cieOwnerTaxId = Costanti.GALILEO_GALILEI_TAX_ID;
            case "SIGNED NONCE ERRATO" -> nonce = "00000";
        }
        return cieGeneratorTool.generateCieValidationData(path, delegatorTaxId, cieOwnerTaxId, expirationDate, nonce);
    }

    //Step importante in quanto va anche a settare il mandateId nella classe RicezioneNotificheWebDelegheSteps
    @Then("l'operazione non ha prodotto alcun errore")
    public void checkErrorIsNull() {
        assertThat(error).as("L'operazione non dovrebbe aver prodotto errori").isNull();
        //converto il mandate creato nel corrispettivo di mandateWeb e lo setto alla classe RicezioneNotificheWebDelegheSteps
        //in questo modo posso riciclare i metodi utilizzati per le deleghe permanenti, senza dover duplicare codice
        it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto mandateWeb = deepCopy(mandateDtoB2b, it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto.class);
        ricezioneNotificheWebDelegheSteps.setMandateToSearch(mandateWeb);
    }

    @When("{string} tenta di accettare la delega temporanea richiamando l'api b2b")
    public void acceptTemporaryMandateViaB2b(String user) {
        ricezioneNotificheWebDelegheSteps.setBearerToken(user);
        try {
            ricezioneNotificheWebDelegheSteps.getWebMandateClient().acceptMandate(
                    mandateDtoB2b.getMandateId(), new AcceptRequestDto().verificationCode(mandateDtoB2b.getVerificationCode()));
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
    }

    @Then("l'operazione restituisce codice {int}")
    public void checkErrorCode(Integer errorCode) {
        assertThat(error.getStatusCode().value()).as("Il codice di errore non coincide con quanto atteso").isEqualTo(errorCode);
        error = null; //una volta verificato l'ottenimento dell'errore atteso, resettarlo
    }

    @Then("la lista di deleghe del {isDelegate} {string} {contains} la delega temporanea creata")
    public void checkPresenceTemporaryMandateInList(boolean isDelegate, String user, boolean contains) {
        ricezioneNotificheWebDelegheSteps.setBearerToken(user);
        it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto mandateFound;
        try {
            if (isDelegate) {
                mandatesByDelegate = ricezioneNotificheWebDelegheSteps.getWebMandateClient().listMandatesByDelegate1(null);
                assertThat(mandatesByDelegate).as("La lista di deleghe del delegato non dev'essere null").isNotNull();
                mandateFound = mandatesByDelegate.stream().filter(m -> m.getMandateId().equals(mandateDtoB2b.getMandateId())).findFirst().orElse(null);
            } else {
                mandatesByDelegator = ricezioneNotificheWebDelegheSteps.getWebMandateClient().listMandatesByDelegator1();
                assertThat(mandatesByDelegator).as("La lista di deleghe del delegante non dev'essere null").isNotNull();
                mandateFound = mandatesByDelegator.stream().filter(m -> m.getMandateId().equals(mandateDtoB2b.getMandateId())).findFirst().orElse(null);
            }
            if (contains) {
                assertThat(mandateFound)
                        .as("La lista di deleghe del " + (isDelegate ? "delegato" : "delegante") + " deve contenere la delega temporanea")
                        .isNotNull();
            } else {
                assertThat(mandateFound)
                        .as("La lista di deleghe del " + (isDelegate ? "delegato" : "delegante") + "non deve contenere la delega temporanea")
                        .isNull();
            }
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
    }

    private <T> T deepCopy(Object obj, Class<T> toClass) {
        ObjectMapper objMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        try {
            String json = objMapper.writeValueAsString(obj);
            return objMapper.readValue(json, toClass);
        } catch (JsonProcessingException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }

    @Given("DEBUGONLY il mandate in uso è quello con id {string} e verificationCode {string}")
    public void mockMandate(String mandateId, String nonce) {
        mandateDtoB2b = new MandateDto();
        mandateDtoB2b.setMandateId(mandateId);
        mandateDtoB2b.setVerificationCode(nonce);
    }

    @Then("la notifica {canBe} essere correttamente letta tramite appIo dal delegato {destinatario}")
    public void delegateReadsNotificationWithAppIO(boolean canBeRead, Destinatario delegate) {
        ThirdPartyMessage thirdPartyMessage = null;
        try {
            thirdPartyMessage = appIOB2bClient.getReceivedNotification(sharedSteps.getNotificationIun(), delegate.getTaxId(), UUID.fromString(mandateDtoB2b.getMandateId()));
        } catch (HttpStatusCodeException exception) {
            error = exception;
        }
        if (canBeRead) {
            Assertions.assertThat(thirdPartyMessage).as("La notifica recuperata non dev'essere null").isNotNull();
            log.info("Notifica visualizzata con successo tramite appIO: \n" + thirdPartyMessage);
        } else {
            Assertions.assertThat(error).as("Il recupero della notifica deve produrre un errore").isNotNull();
            log.info("Errore in fase di visualizzazione notifica tramite appIO: \n" + error.getMessage());
        }
    }

    //delegator superfluo come parametro, ma aiuta ai fini della leggibilità dello scenario
    @When("{string} viene temporaneamente delegato da {string} passando headers lollipop {lollipopHeadersError}")
    public void creaDelegaTemporaneaWithHeaders(String cfDelegato, String delegator, LollipopHeader lollipopHeaderWithError) {
        qrCode = getQRPathEnvironmentBased() + "?aar=" +
                (sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0));

        MandateCreationRequest mandateCreationRequest = new MandateCreationRequest();
        mandateCreationRequest.setAarQrCodeValue(qrCode);

        String taxId = cfDelegato;

        Map<LollipopHeader, String> lollipopHeaders = getMandateLollipopHeadersValues(lollipopHeaderWithError);
        String xPagopaLollipopOriginalUrl = lollipopHeaders.get(LOLLIPOP_ORIGINAL_URL);
        String xPagopaLollipopOriginalMethod = lollipopHeaders.get(LOLLIPOP_ORIGINAL_METHOD);
        String xPagopaLollipopPublicKey = lollipopHeaders.get(LOLLIPOP_PUBLIC_KEY);
        String xPagopaLollipopAssertionRef = lollipopHeaders.get(LOLLIPOP_ASSERTION_REF);
        String xPagopaLollipopAssertionType = lollipopHeaders.get(LOLLIPOP_ASSERTION_TYPE);
        String xPagopaLollipopAuthJwt = lollipopHeaders.get(LOLLIPOP_AUTH_JWT);
        String xPagoPaLollipopUserId = lollipopHeaders.get(LOLLIPOP_USER_ID);
        String signatureInput = lollipopHeaders.get(LOLLIPOP_SIGNATURE_INPUT);
        String signature = lollipopHeaders.get(LOLLIPOP_SIGNATURE);
        if (lollipopHeaderWithError == null || !lollipopHeaderWithError.equals(LOLLIPOP_USER_ID)) {
            xPagoPaLollipopUserId = cfDelegato;
        }

        mandateCreationResponse = null;
        try {
            mandateCreationResponse = mandateAppIoClient.createIOMandate(
                    taxId,
                    xPagopaLollipopOriginalUrl,
                    xPagopaLollipopOriginalMethod,
                    xPagopaLollipopPublicKey,
                    xPagopaLollipopAssertionRef,
                    xPagopaLollipopAssertionType,
                    xPagopaLollipopAuthJwt,
                    xPagoPaLollipopUserId,
                    signatureInput,
                    signature,
                    mandateCreationRequest);
            checkMandateCreation();
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
    }

    @Given("viene invocata l'api per testare la lambda authorizer di lollipop con metodo {string} passando headers {lollipopHeadersError}")
    public void todo(String method, LollipopHeader lollipopHeaderWithError) throws IOException, InterruptedException {
        log.info("Tipo header lollipop: {}", lollipopHeaderWithError == null ? "tutti validi" : lollipopHeaderWithError + " errato");
        HttpClient client = HttpClient.newHttpClient();
        Map<LollipopHeader, String> lollipopHeadersMap = getPlaygroundLollipopHeadersValues(method, lollipopHeaderWithError);
        String requestBody = """
                {
                    "jsonBodyRequest": "testQA"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-io.uat.notifichedigitali.it/io-playground/lollipop-test"))
                .header("Content-Type", "application/json")
                .header("x-pagopa-lollipop-original-url", lollipopHeadersMap.get(LOLLIPOP_ORIGINAL_URL))
                .header("x-pagopa-lollipop-original-method", lollipopHeadersMap.get(LOLLIPOP_ORIGINAL_METHOD))
                .header("x-pagopa-lollipop-public-key", lollipopHeadersMap.get(LOLLIPOP_PUBLIC_KEY))
                .header("x-pagopa-lollipop-assertion-ref", lollipopHeadersMap.get(LOLLIPOP_ASSERTION_REF))
                .header("x-pagopa-lollipop-assertion-type", lollipopHeadersMap.get(LOLLIPOP_ASSERTION_TYPE))
                .header("x-pagopa-lollipop-auth-jwt", lollipopHeadersMap.get(LOLLIPOP_AUTH_JWT))//Vale per un anno circa
                .header("x-pagopa-lollipop-user-id", lollipopHeadersMap.get(LOLLIPOP_USER_ID))
                .header("signature", lollipopHeadersMap.get(LOLLIPOP_SIGNATURE))
                .header("signature-input", lollipopHeadersMap.get(LOLLIPOP_SIGNATURE_INPUT))
                .header("x-pagopa-cx-taxid", lollipopUserId)
                .header("x-api-key", appIoApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        log.info("Request per lambda authorizer test: {}", request);
        log.info("Body per lambda authorizer test:\n{}", requestBody);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        B2bUtils.logPrettyResponse(response.body());

        if (response.statusCode() == 200) {
            String resultCode = JsonPath.read(response.body(), "$.data.authorizerContext.resultCode");
            log.info("Lollipop ResultCode recuperato: {}", resultCode);
            if (lollipopHeaderWithError == null) {
                assertThat(resultCode).as("Il result code dev'essere VERIFICATION_SUCCESS_CODE").isEqualToIgnoringCase("VERIFICATION_SUCCESS_CODE");
            } else {
                switch (lollipopHeaderWithError) {
                    case LOLLIPOP_ORIGINAL_URL -> assertThat(resultCode).as("").contains("TODO 1");
                    case LOLLIPOP_ORIGINAL_METHOD -> assertThat(resultCode).as("").contains("TOD0 2");
                    case LOLLIPOP_PUBLIC_KEY -> assertThat(resultCode).as("").contains("TODO 3");
                    case LOLLIPOP_ASSERTION_REF -> assertThat(resultCode).as("").contains("TODO 4");
                    case LOLLIPOP_ASSERTION_TYPE -> assertThat(resultCode).as("").contains("TODO 5");
                    case LOLLIPOP_AUTH_JWT -> assertThat(resultCode).as("").contains("TODO 6");
                    case LOLLIPOP_USER_ID -> assertThat(resultCode).as("").contains("TODO 7");
                    case LOLLIPOP_SIGNATURE -> assertThat(resultCode).as("").contains("TODO 8");
                    case LOLLIPOP_SIGNATURE_INPUT -> assertThat(resultCode).as("").contains("TODO 9");
                }
            }
        } else {
            log.info("Errore in fase di chiamata: statusCode = {}", response.statusCode());
        }
    }

    private Map<LollipopHeader, String> getPlaygroundLollipopHeadersValues(String method, LollipopHeader lollipopHeaderWithError) {
        Map<LollipopHeader, String> headersLollipop = new HashMap<>();
        headersLollipop.put(LOLLIPOP_ORIGINAL_URL, "https://api-app.io.pagopa.it/api/com/v1/send/lollipop-check/test?isTest=true");
        headersLollipop.put(LOLLIPOP_ORIGINAL_METHOD, method);
        headersLollipop.put(LOLLIPOP_PUBLIC_KEY, "eyJ4IjoiQU9LVXhvUDlUdDdEL084WjlYWCtNaFJGaURKYVg3b1FlYmwvZEx5c3dRR20iLCJjcnYiOiJQLTI1NiIsInkiOiJFWldLNFI4TWx3TWxHcFVOcXBrU2krczhlUVBFOHgzN3lBWjI3ZHI2U0lNPSIsImt0eSI6IkVDIn0");
        headersLollipop.put(LOLLIPOP_ASSERTION_REF, "sha256-BjD7BkZRAZItSW9JKL-E-PopREBm0Ve5q5rS6M-c_YQ");
        headersLollipop.put(LOLLIPOP_ASSERTION_TYPE, "SAML");
        headersLollipop.put(LOLLIPOP_AUTH_JWT, method.equals("GET") ?
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhc3NlcnRpb25SZWYiOiJzaGEyNTYtQmpEN0JrWlJBWkl0U1c5SktMLUUtUG9wUkVCbTBWZTVxNXJTNk0tY19ZUSIsIm9wZXJhdGlvbklkIjoiN2QwOWMzMGItZWI4NS00ZjVjLTk5YTAtMTc1OGU5MDdmMmM2IiwiaWF0IjoxNzc0ODY4MzI1LCJleHAiOjE3NzQ4NjkyMjUsImlzcyI6ImFwaS5pby5wYWdvcGEuaXQiLCJqdGkiOiIwMUtNWjZBUzQ1VkozUUszWlg3RkUzQjZSMiJ9.WXBp7dVZ5xdHn_7IgnFLQicKrVAXMItQ_GXAwtfC8IWBuuB-PUzWBLa9JiHuotpqre-Qx6YD9tioFb1KxAtx8kTeUHhE80aQROL1C3TsNCQnxuO9v0Z1cYgIVWEuAKiTMz0GU2f1mBKA5N_oneUsDMjas0_32qtX45vr5r7RX1GuPGlVm0Ooqb6c3z0bccIcYD-b1-8JJN9NRvn5e0QBOddI_imZeBHoHXgAH4G2rkG4fgnaJwqS5h34n2lYxUkzhdZBCyy-agLfixnIrvNYnIGKkDehQ9gYqBcd8hwuo6FkTTqItsty79WQO5fXUm3RyesD_-ffHPwtITa9M4s0ww" :
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhc3NlcnRpb25SZWYiOiJzaGEyNTYtQmpEN0JrWlJBWkl0U1c5SktMLUUtUG9wUkVCbTBWZTVxNXJTNk0tY19ZUSIsIm9wZXJhdGlvbklkIjoiN2QwOWMzMGItZWI4NS00ZjVjLTk5YTAtMTc1OGU5MDdmMmM2IiwiaWF0IjoxNzc0ODY4NDAyLCJleHAiOjE3NzQ4NjkzMDIsImlzcyI6ImFwaS5pby5wYWdvcGEuaXQiLCJqdGkiOiIwMUtNWjZENDhFUDJBOVY3S1M4VFZXWkNSRyJ9.qAb1j3dddT2kVUQBSUlgvxvQ84aMZGFK4IVBrgB0dyL8C3EQPW11AtXzUJtm3bdl54rcTGKOmhJs1BY2LzX5dsnerNWuyqPRWoqa-lPNr0_bCmVxiTFbSva2WwuUrVx5HE3hR2cbeRoK1ogyEFJT5vaq8hhMXVKN3GQ4QrpU8wXsP_6hhfQ6GQxy6g-MET4vdt-BAU4qhWaSD0RLM2ldJo7xUKzEM8Ry7-fkQhYtZRfIZWomUNQKEBGasTk_YrcEKXDDyHtbMjbJzj5e8MXtraoP5WrDT3yvmj2UnjfXvNE26Z_2JWX3on2-44n3QrX1BRdbZaUPToT1L7St1coEHA");
        headersLollipop.put(LOLLIPOP_SIGNATURE_INPUT, "sig1=(\"x-pagopa-lollipop-original-method\" \"x-pagopa-lollipop-original-url\");created=1774868324;nonce=\"7d09c30b-eb85-4f5c-99a0-1758e907f2c6\";alg=\"ecdsa-p256-sha256\";keyid=\"BjD7BkZRAZItSW9JKL-E-PopREBm0Ve5q5rS6M-c_YQ\"");
        headersLollipop.put(LOLLIPOP_SIGNATURE, "sig1=:MEYCIQDYnlQGASeDnE9uaKuDe2HXyXAzsL7LxHhrscWbYLTOSAIhAI1YT9st0hu5G9G7L95JKP/IvUYg3zc3rRZtgGunJBoH:");
        headersLollipop.put(LOLLIPOP_USER_ID, lollipopUserId);
        if (lollipopHeaderWithError != null) {
            switch (lollipopHeaderWithError) {
                case LOLLIPOP_ORIGINAL_URL -> headersLollipop.put(LOLLIPOP_ORIGINAL_URL, "TODO_ERROR");
                case LOLLIPOP_ORIGINAL_METHOD -> headersLollipop.put(LOLLIPOP_ORIGINAL_METHOD, "DELETE");
                case LOLLIPOP_PUBLIC_KEY -> headersLollipop.put(LOLLIPOP_PUBLIC_KEY, "TODO_ERROR");
                case LOLLIPOP_ASSERTION_REF -> headersLollipop.put(LOLLIPOP_ASSERTION_REF, "TODO_ERROR");
                case LOLLIPOP_ASSERTION_TYPE -> headersLollipop.put(LOLLIPOP_ASSERTION_TYPE, "TODO_ERROR");
                case LOLLIPOP_AUTH_JWT -> headersLollipop.put(LOLLIPOP_AUTH_JWT, "TODO_ERROR");
                case LOLLIPOP_SIGNATURE_INPUT -> headersLollipop.put(LOLLIPOP_SIGNATURE_INPUT, "TODO_ERROR");
                case LOLLIPOP_SIGNATURE -> headersLollipop.put(LOLLIPOP_SIGNATURE, "TODO_ERROR");
                case LOLLIPOP_USER_ID -> headersLollipop.put(LOLLIPOP_USER_ID, Costanti.GALILEO_GALILEI_TAX_ID);
            }
        }
        return headersLollipop;
    }

    //TODO: sostituire con i valori necessari per mandate quando ci verranno forniti i valori corretti
    private Map<LollipopHeader, String> getMandateLollipopHeadersValues(LollipopHeader lollipopHeaderWithError) {
        Map<LollipopHeader, String> headersLollipop = new HashMap<>();
        headersLollipop.put(LOLLIPOP_ORIGINAL_URL, "https://api-app.io.pagopa.it/api/com/v1/send/lollipop-check/test?isTest=true");
        headersLollipop.put(LOLLIPOP_ORIGINAL_METHOD, "GET");
        headersLollipop.put(LOLLIPOP_PUBLIC_KEY, "eyJ4IjoiQU9LVXhvUDlUdDdEL084WjlYWCtNaFJGaURKYVg3b1FlYmwvZEx5c3dRR20iLCJjcnYiOiJQLTI1NiIsInkiOiJFWldLNFI4TWx3TWxHcFVOcXBrU2krczhlUVBFOHgzN3lBWjI3ZHI2U0lNPSIsImt0eSI6IkVDIn0");
        headersLollipop.put(LOLLIPOP_ASSERTION_REF, "sha256-BjD7BkZRAZItSW9JKL-E-PopREBm0Ve5q5rS6M-c_YQ");
        headersLollipop.put(LOLLIPOP_ASSERTION_TYPE, "SAML");
        headersLollipop.put(LOLLIPOP_AUTH_JWT, "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhc3NlcnRpb25SZWYiOiJzaGEyNTYtQmpEN0JrWlJBWkl0U1c5SktMLUUtUG9wUkVCbTBWZTVxNXJTNk0tY19ZUSIsIm9wZXJhdGlvbklkIjoiYjM1OTg3ZjYtYjgxYy00NWQyLTg1NTUtYjMxZTU5Njg0NjIyIiwiaWF0IjoxNzc0NjI1MTk2LCJleHAiOjE3NzQ2MjYwOTYsImlzcyI6ImFwaS5pby5wYWdvcGEuaXQiLCJqdGkiOiIwMUtNUVlGMksxUUJTRUNBNU5LRlhQWldXWSJ9.IQcugUl_6ry7zSfYCtOGMlHJ3xhMBK1oOZGRKAxT_XRO28xSF3xpw1nYLO5KheiuDI95RXei04kdo6HffH56jgS7VgV5IfFGXmaQLR08GdvJJ0AMkD9Z2mB6JQDkZOMj-z9jp3ZuTp9FpXJ_qR109Bd1IGnebYX9tOoPYhO5Q6OGZOiP75Xj4sYIne-ssFxMIjvumixw9hcOMmsTfKvWK3NoK3jRjfEvgGTfi87nBJRrCbzAbHhu809VoglJ8Oy0Pavx1pgG1pwOi23AwrP9_B9owpTcUz3_9e2yyjDrVFx0zGuKPQSwBzgKGyeD8TQkm7Rzd5BkFPxpGsUaDyhfsA");
        headersLollipop.put(LOLLIPOP_SIGNATURE_INPUT, "sig1=(\"x-pagopa-lollipop-original-method\" \"x-pagopa-lollipop-original-url\");created=1774625195;nonce=\"b35987f6-b81c-45d2-8555-b31e59684622\";alg=\"ecdsa-p256-sha256\";keyid=\"BjD7BkZRAZItSW9JKL-E-PopREBm0Ve5q5rS6M-c_YQ\"");
        headersLollipop.put(LOLLIPOP_SIGNATURE, "sig1=:MEYCIQCnNETTQ1ZUb0ukBqBSl8+hORbMw0x1PCEejiqCucHzGQIhAOsoIH2I0hHmHLDkUYqsE+wr/YpMHOzJOaDPIx0RElrt:");
        headersLollipop.put(LOLLIPOP_USER_ID, lollipopUserId);
        if (lollipopHeaderWithError != null) {
            switch (lollipopHeaderWithError) {
                case LOLLIPOP_ORIGINAL_URL -> headersLollipop.put(LOLLIPOP_ORIGINAL_URL, "TODO_ERROR");
                case LOLLIPOP_ORIGINAL_METHOD -> headersLollipop.put(LOLLIPOP_ORIGINAL_METHOD, "DELETE");
                case LOLLIPOP_PUBLIC_KEY -> headersLollipop.put(LOLLIPOP_PUBLIC_KEY, "TODO_ERROR");
                case LOLLIPOP_ASSERTION_REF -> headersLollipop.put(LOLLIPOP_ASSERTION_REF, "TODO_ERROR");
                case LOLLIPOP_ASSERTION_TYPE -> headersLollipop.put(LOLLIPOP_ASSERTION_TYPE, "TODO_ERROR");
                case LOLLIPOP_AUTH_JWT -> headersLollipop.put(LOLLIPOP_AUTH_JWT, "TODO_ERROR");
                case LOLLIPOP_SIGNATURE_INPUT -> headersLollipop.put(LOLLIPOP_SIGNATURE_INPUT, "TODO_ERROR");
                case LOLLIPOP_SIGNATURE -> headersLollipop.put(LOLLIPOP_SIGNATURE, "TODO_ERROR");
                case LOLLIPOP_USER_ID -> headersLollipop.put(LOLLIPOP_USER_ID, Costanti.GALILEO_GALILEI_TAX_ID);
            }
        }
        return headersLollipop;
    }

    @Given("genero la curl a partire dai log lambdaAuthorizer")
    public void generateCurl() {
        String logString = """
                {
                {
                                                      type: 'REQUEST',
                                                      methodArn: 'arn:aws:execute-api:eu-south-1:644374009812:k6tj47klpa/unique/POST/notifications/received/check-qr-code',
                                                      resource: '/notifications/received/check-qr-code',
                                                      path: '/delivery/notifications/received/check-qr-code',
                                                      httpMethod: 'POST',
                                                      headers: {
                                                        accept: '*/*',
                                                        'accept-encoding': 'br, gzip, deflate',
                                                        'accept-language': '*',
                                                        'content-digest': 'sha-256=:wje4k645j+rmj5rVXLkuYgOevU6q/XUQ/z0496F0Aos=:',
                                                        'Content-Length': '208',
                                                        'content-type': 'application/json',
                                                        Host: 'api-io.uat.notifichedigitali.it',
                                                        'sec-fetch-mode': 'cors',
                                                        signature: 'sig1=:MEYCIQDJ1QoqRDPZcotJZ+hk3Z88cBaBOhhk9NuuCy64FHUrOwIhAPC4CjaiIxXpvjOblsN0dfzoOhW27Ap1naP7B3L1B1gz:',
                                                        'signature-input': 'sig1=("x-pagopa-lollipop-original-method" "x-pagopa-lollipop-original-url");created=1775642225;nonce="f870855f-29b8-4495-8544-b2ef7c2ced12";alg="ecdsa-p256-sha256";keyid="ojT1fpcnQ6AGIPVLAEUIzTrNt19w5mM6zD1YPFmSCvY"',
                                                        'user-agent': 'node',
                                                        'X-Amzn-Trace-Id': 'Root=1-69d62671-6e74b4d521dd0cec32059f9e',
                                                        'x-api-key': 'q6XSeFPKh0a90ezR8BIeT8BdvsjNZnBBTaBQoN3b',
                                                        'X-Forwarded-For': '4.232.9.45',
                                                        'X-Forwarded-Port': '443',
                                                        'X-Forwarded-Proto': 'https',
                                                        'x-pagopa-cx-taxid': 'NNCMRC80H27A509T',
                                                        'x-pagopa-lollipop-assertion-ref': 'sha256-ojT1fpcnQ6AGIPVLAEUIzTrNt19w5mM6zD1YPFmSCvY',
                                                        'x-pagopa-lollipop-assertion-type': 'SAML',
                                                        'x-pagopa-lollipop-auth-jwt': 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhc3NlcnRpb25SZWYiOiJzaGEyNTYtb2pUMWZwY25RNkFHSVBWTEFFVUl6VHJOdDE5dzVtTTZ6RDFZUEZtU0N2WSIsIm9wZXJhdGlvbklkIjoiZjg3MDg1NWYtMjliOC00NDk1LTg1NDQtYjJlZjdjMmNlZDEyIiwiaWF0IjoxNzc1NjQyMjI1LCJleHAiOjE3NzU2NDMxMjUsImlzcyI6ImFwaS5pby5wYWdvcGEuaXQiLCJqdGkiOiIwMUtOUDhDQU03VDdUMFAwVFZBMTJQNkdDVCJ9.Z3MQHkL-vhLqiPHwNVXcRzM1hJXfrwZMyugCV1VuQ9HFFGNFnGEUha7YG7MN93hEebRVMeFTXdtCldeITVr9EM_zlJ8VkIXRURhi5kdAwbzS04pROijIh5zyhlpZvPfVF0AVOol1LUJaEbP03_dDkX2m0Z7-9pr0JAviQJmhtL7-AlMVhyytsASKz9uo7_oU-n5RcZnmXVMlWgJcPOFmKUBsW1NaMYeb7VNwvebuLyJvG2bJUHERrgqv_rmGkgWIYQVYsoX9vqFYL7BWy8lM4YYdwbg9mYhnteaTeZLsYyfcl_aBheC0pgCzPS9yNV0BvhymHDIYXxKvmSnd-O0KDg',
                                                        'x-pagopa-lollipop-original-method': 'POST',
                                                        'x-pagopa-lollipop-original-url': 'https://api-app.io.pagopa.it/api/com/v1/send/aar/qr-code-check?isTest=true',
                                                        'x-pagopa-lollipop-public-key': 'eyJ5IjoiMHoyUHJvSDJDTk9vTGZ5eXdPRTc3aTZBNnhOSGhBMVVxWTVQVVU2UjV4MD0iLCJrdHkiOiJFQyIsImNydiI6IlAtMjU2IiwieCI6InBsQ3YxWnF6aDU2TTBJbHo1VkE2WjltZE5XVml4VGhBTG1uZUdpeFJ3NkU9In0',
                                                        'x-pagopa-lollipop-user-id': 'NNCMRC80H27A509T'
                                                      }
                """;

        String[] keysToFind = {
                "x-pagopa-cx-taxid",
                "x-pagopa-lollipop-assertion-ref",
                "x-pagopa-lollipop-assertion-type",
                "x-pagopa-lollipop-auth-jwt",
                "x-pagopa-lollipop-original-method",
                "x-pagopa-lollipop-original-url",
                "x-pagopa-lollipop-public-key",
                "x-pagopa-lollipop-user-id",
                "x-api-key",
                "signature",
                "signature-input"
        };

        StringBuilder curl = new StringBuilder();
        curl.append("curl --location 'https://api-io.uat.notifichedigitali.it/mandate/api/v1/io/mandate' \\\n");
        curl.append("--header 'Content-Type: application/json' \\\n");

        String[] lines = logString.split("\n");
        for (String key : keysToFind) {
            for (String line : lines) {
                if (line.contains(key + ":") || line.contains("'" + key + "':")) {
                    int duePuntiIndex = line.indexOf(":");
                    String valueHeader = line.substring(duePuntiIndex + 3, line.length() - 1);
                    if (valueHeader.endsWith(",")) {
                        valueHeader = valueHeader.substring(0, valueHeader.length() - 1);
                    }
                    if (valueHeader.endsWith("'")) {
                        valueHeader = valueHeader.substring(0, valueHeader.length() - 1);
                    }
                    curl.append("--header '").append(key).append(": ").append(valueHeader).append("' \\\n");
                }
            }
        }

        String body = "{\"aarQrCodeValue\":\"https://cittadini.uat.notifichedigitali.it/io/?aar=TFJLUC1WTUpZLVdOWFktMjAyNjAxLUgtMV9QRi1hNTJlNzAxOS0yZTJiLTRlYmUtYTMyYy1iYzAwY2UzYTNjODdfOGYwYzcwYzktODEwMS00Mjc0LTlhYTctYzE0MjAwYzAzNWU3\"}";
        curl.append("--data '").append(body).append("'");

        log.info("\n" + curl);
    }

    private String getQRPathEnvironmentBased() {
        String environment = sharedSteps.getContext().getEnvironment().getActiveProfiles()[0];
        return switch (environment) {
            case "dev" -> "http://cittadini.dev.notifichedigitali.it/io";
            case "test" -> "http://cittadini.test.notifichedigitali.it/io";
            case "uat" -> "https://cittadini.uat.notifichedigitali.it/io/";
            default -> throw new IllegalArgumentException("Invalid environment name: " + environment);
        };
    }
}
