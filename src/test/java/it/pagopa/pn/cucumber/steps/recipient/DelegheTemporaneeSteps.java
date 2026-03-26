package it.pagopa.pn.cucumber.steps.recipient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import it.pagopa.pn.cucumber.steps.utilitySteps.LollipopHeaders;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Map;

import static it.pagopa.pn.cucumber.steps.utilitySteps.LollipopHeaders.*;
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
                                  @Value("${pn-deleghe-temporanee-bucket-s3}") String bucketS3) {
        this.sharedSteps = sharedSteps;
        this.mandateAppIoClient = mandateAppIoClient;
        this.ricezioneNotificheWebDelegheSteps = ricezioneNotificheWebDelegheSteps;
        this.appIOB2bClient = appIOB2bClient;
        this.cieGeneratorTool = cieGeneratorTool;
        this.bucketS3 = bucketS3;
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
    public void creaDelegaTemporaneaWithHeaders(String cfDelegato, String delegator, LollipopHeaders lollipopHeaderWithError) {
        qrCode = getQRPathEnvironmentBased() + "?aar=" +
                (sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0));

        MandateCreationRequest mandateCreationRequest = new MandateCreationRequest();
        mandateCreationRequest.setAarQrCodeValue(qrCode);

        String taxId = cfDelegato;

        Map<LollipopHeaders, String> lollipopHeaders = getLollipopHeaders(lollipopHeaderWithError);
        String xPagopaLollipopOriginalUrl = lollipopHeaders.get(LOLLIPOP_ORIGINAL_URL);
        String xPagopaLollipopOriginalMethod = lollipopHeaders.get(LOLLIPOP_ORIGINAL_METHOD);
        String xPagopaLollipopPublicKey = lollipopHeaders.get(LOLLIPOP_PUBLIC_KEY);
        String xPagopaLollipopAssertionRef = lollipopHeaders.get(LOLLIPOP_ASSERTION_REF);
        String xPagopaLollipopAssertionType = lollipopHeaders.get(LOLLIPOP_ASSERTION_TYPE);
        String xPagopaLollipopAuthJwt = lollipopHeaders.get(LOLLIPOP_AUTH_JWT);
        String xPagoPaLollipopUserId = lollipopHeaders.get(LOLLIPOP_USER_ID);
        String signatureInput = lollipopHeaders.get(LOLLIPOP_SIGNATURE_INPUT);
        String signature = lollipopHeaders.get(LOLLIPOP_SIGNATURE);
        if (!lollipopHeaderWithError.equals(LOLLIPOP_USER_ID)) {
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

    private Map<LollipopHeaders, String> getLollipopHeaders(LollipopHeaders lollipopHeaderWithError) {
        Map<LollipopHeaders, String> headersLollipop = new HashMap<>();
        headersLollipop.put(LOLLIPOP_ORIGINAL_URL, "mandate");
        headersLollipop.put(LOLLIPOP_ORIGINAL_METHOD, "GET");
        headersLollipop.put(LOLLIPOP_PUBLIC_KEY, "eyJ4IjoiQU9LVXhvUDlUdDdEL084WjlYWCtNaFJGaURKYVg3b1FlYmwvZEx5c3dRR20iLCJjcnYiOiJQLTI1NiIsInkiOiJFWldLNFI4TWx3TWxHcFVOcXBrU2krczhlUVBFOHgzN3lBWjI3ZHI2U0lNPSIsImt0eSI6IkVDIn0");
        headersLollipop.put(LOLLIPOP_ASSERTION_REF, "sha256-BjD7BkZRAZItSW9JKL-E-PopREBm0Ve5q5rS6M-c_YQ");
        headersLollipop.put(LOLLIPOP_ASSERTION_TYPE, "SAML");
        headersLollipop.put(LOLLIPOP_AUTH_JWT, "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhc3NlcnRpb25SZWYiOiJzaGEyNTYtQmpEN0JrWlJBWkl0U1c5SktMLUUtUG9wUkVCbTBWZTVxNXJTNk0tY19ZUSIsIm9wZXJhdGlvbklkIjoiYjdmNWUwYjQtNzIzZC00MmMyLWIxZDctN2M4OTA1OTAwMjAzIiwiaWF0IjoxNzc0NDQ3NzM2LCJleHAiOjE3NzQ0NDg2MzYsImlzcyI6ImFwaS5pby5wYWdvcGEuaXQiLCJqdGkiOiIwMUtNSk43REpTNThTMDE0SzdEVE5CM0dBRyJ9.Eq14IePo2q-kAPjx4Uf-xuC3ulY-5tMJLLZpjx5Rq-rUtdbN1YZRn42SkKKDv1_UE1E5AkyqPc9umGg9O0-PuP9--QsVPT3Pinl9-bOSy6E8ojLTSf6NgB7Ka9nsGngCt-23u2tsRSMo-FooXd9gA00TZq5G8wUQicrx9US2jXoyfxBzic2UQ_wbbS52p7bYef-98Wt5GFJTVbrGgFnW6ck_-4wsRpX7a2hQ9zlnav9zx3wbOfjHS3VnIvKxLkroBpTeT4LvKiw6e7RT3GRW4A8SCkim1oHOfh1eor3kqvOiKueRXTlJVtvWoh5Szjr6DLXV_KRFtlMLfZad7q8YUQ");
        headersLollipop.put(LOLLIPOP_SIGNATURE_INPUT, "sig1=(\"x-pagopa-lollipop-original-method\" \"x-pagopa-lollipop-original-url\");created=1774447735;nonce=\"b7f5e0b4-723d-42c2-b1d7-7c8905900203\";alg=\"ecdsa-p256-sha256\";keyid=\"BjD7BkZRAZItSW9JKL-E-PopREBm0Ve5q5rS6M-c_YQ\"");
        headersLollipop.put(LOLLIPOP_SIGNATURE, "sig1=:MEYCIQCnNETTQ1ZUb0ukBqBSl8+hORbMw0x1PCEejiqCucHzGQIhAOsoIH2I0hHmHLDkUYqsE+wr/YpMHOzJOaDPIx0RElrt:");
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
