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
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.CIEValidationData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateDto;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV27;
import it.pagopa.pn.client.b2b.pa.service.IPnMandateAppIoClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnMandateAppIoClientImpl;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.AcceptRequestDto;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.utilitySteps.CieGeneratorTool;
import it.pagopa.pn.cucumber.steps.utilitySteps.Costanti;
import it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Slf4j
@Data
public class DelegheTemporaneeSteps {

    private final SharedSteps sharedSteps;

    private final RicezioneNotificheWebDelegheSteps ricezioneNotificheWebDelegheSteps;

    private final IPnMandateAppIoClient mandateAppIoClient;

    private String qrCode;

    private MandateCreationResponse mandateCreationResponse;

    private MandateDto mandateDtoB2b;

    private List<it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto> mandatesByDelegate;

    private List<it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto> mandatesByDelegator;

    private HttpClientErrorException error;

    private final static String QRCODE_FROM_HOTFIX = "http://cittadini.notifichedigitali.it/io?aar=S05EQS1OUEFHLVZBTkEtMjAyNTAyLUotMV9QRi00MmQ5ODJlZi0yNTc4LTQ3ODUtOTg0Yy04YzE5ZjM3NTZlNzlfMWY2NzVlNWQtYjcyNi00NzNkLWJlZTQtZDIxZjk5ZGQwN2Jm";

    @Autowired
    public DelegheTemporaneeSteps(SharedSteps sharedSteps,
                                  RicezioneNotificheWebDelegheSteps ricezioneNotificheWebDelegheSteps,
                                  PnMandateAppIoClientImpl mandateAppIoClient) {
        this.sharedSteps = sharedSteps;
        this.mandateAppIoClient = mandateAppIoClient;
        this.ricezioneNotificheWebDelegheSteps = ricezioneNotificheWebDelegheSteps;
    }

    //TODO delegator superfluo come parametro, ma aiuta ai fini della leggibilità dello scenario
    @When("{destinatario} viene temporaneamente delegato da {string} passando {string}")
    public void creaDelegaTemporanea(Destinatario delegate, String delegator, String inputParamsType) {

        qrCode = "http://cittadini.notifichedigitali.it/io?aar=" +
                (sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0));
        MandateCreationRequest mandateCreationRequest = new MandateCreationRequest();
        mandateCreationRequest.setAarQrCodeValue(qrCode);
        String taxId = delegate.getTaxId();
        String lollipopUserId = delegate.getTaxId();

        switch (inputParamsType.toUpperCase()) {
            //qrCode valido, ma relativo a hotfix, per dare errore quando la suite gira in DEV/TEST/UAT
            case "QRCODE INESISTENTE" -> mandateCreationRequest.setAarQrCodeValue(QRCODE_FROM_HOTFIX);
            case "QRCODE NON VALIDO" -> qrCode = "invalid";
            case "TAXID NULL" -> taxId = null;
            case "EMPTY REQUEST BODY" -> mandateCreationRequest = null;
            case "CX TAX ID E LOLLIPOP USER ID NON COINCIDENTI" -> lollipopUserId = Costanti.GALILEO_GALILEI_TAX_ID;
        }

        mandateCreationResponse = null;
        try {
            mandateCreationResponse = mandateAppIoClient.createIOMandate(
                    taxId, null, null, null, null,
                    null, null, lollipopUserId, null, null,
                    mandateCreationRequest);
            checkMandateCreation();
        } catch (HttpClientErrorException e) {
            this.error = e;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    @Then("la delega temporanea è stata correttamente creata")
    public void checkMandateCreation() {
        assertThat(mandateCreationResponse).as("La response di creazione delega non dev'essere null").isNotNull();
        assertThat(mandateCreationResponse.getMandate()).as("Il mandate della create response non dev'essere null").isNotNull();
        mandateDtoB2b = mandateCreationResponse.getMandate();
    }

    @When("la delega temporanea di {destinatario} viene accettata da {destinatario} passando {string}")
    public void accettaDelegaTemporanea(Destinatario delegator, Destinatario delegate, String inputParamsType) {
        String taxId = delegate.getTaxId();
        String delegatorTaxId = delegator.getTaxId();
        String lollipopUserId = delegate.getTaxId();
        String mandateId = mandateDtoB2b.getMandateId();
        CIEValidationData cieValidationData = getCieValidationData(delegatorTaxId, inputParamsType);

        switch (inputParamsType.toUpperCase()) {
            //TODO:
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
            case "SIGNED NONCE ERRATO" -> cieValidationData.setSignedNonce("00000");
            case "NIS DATA CIE ERRATO" -> cieValidationData.setNisData(null);
            case "CX TAX ID E LOLLIPOP USER ID NON COINCIDENTI" -> lollipopUserId = Costanti.GALILEO_GALILEI_TAX_ID;
        }
        try {
            mandateAppIoClient.acceptIOMandate(
                    taxId,
                    mandateId,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    lollipopUserId,
                    null,
                    null,
                    cieValidationData);
        } catch (HttpClientErrorException e) {
            this.error = e;
        }
    }

    //TODO: importante, verificare che in tutti gli ambienti, la validità di una delega impostata sia sempre 7 minuti
    @Given("la delega viene fatta scadere")
    public void wasteTime() {
        log.info("Attendo 7 minuti per far scadere la delega");
        long delayInMilliseconds = 420000L;
        try {
            Thread.sleep(delayInMilliseconds);
            log.info("Sono trascorsi 7 minuti, la delega ormai è scaduta");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("L'attesa è stata interrotta: " + e.getMessage(), e);
        }
    }

    private CIEValidationData getCieValidationData(String delegatorTaxId, String inputParamsType) {
        Path path = null;
        LocalDate expirationDate = LocalDate.now().plusYears(1L);
        String nonce = null;
        switch (inputParamsType.toUpperCase()) {
            case "DATI DI UNA CIE SCADUTA" -> expirationDate = LocalDate.now().minusYears(1L);
            case "DATI CIE DI UTENTE DIVERSO DAL DESTINATARIO" -> delegatorTaxId = Costanti.GALILEO_GALILEI_TAX_ID;
            case "SIGNED NONCE ERRATO" -> nonce = "00000";
        }
        return CieGeneratorTool.generateCieValidationData(path, delegatorTaxId, expirationDate, nonce);
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
        } catch (HttpClientErrorException e) {
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
        } catch (HttpClientErrorException e) {
            this.error = e;
        }
    }

    @And("{string} recupera lato web PA una notifica vecchia 120 o più giorni inviata a {destinatario}")
    public void retrieveNotification120DaysOldByIunWebPaSide(String paName, Destinatario recipient) {
        sharedSteps.setPA(paName);
        String recipientTaxId = null;//recipient.getTaxId();
        OffsetDateTime todayDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
        BffNotificationsResponse bffNotificationsResponse = sharedSteps.getWebPaClient().searchSentNotification(
                todayDate.minusDays(130),
                todayDate.minusDays(120),
                recipientTaxId, null, null, null, 50, null);
        assertThat(bffNotificationsResponse).as("La bffNotificationResponse non dev'essere null").isNotNull();
        assertThat(bffNotificationsResponse.getResultsPage()).as("La lista di notifiche vecchie 120 giorni non dev'essere vuota").isNotNull();
        assertThat(bffNotificationsResponse.getResultsPage()).as("La lista di notifiche vecchie 120 giorni non dev'essere vuota").isNotEmpty();
        FullSentNotificationV27 notifica120 = sharedSteps.getSentNotificationLastVersionByIun(bffNotificationsResponse.getResultsPage().get(0).getIun());
        sharedSteps.setNotificationIun(notifica120.getIun());
        log.info("IUN OLDER 120 GG: " + notifica120.getIun());
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

    //TODO MATTEO: solo per debug, per evitare di aspettare ogni volta che una notifica vada in accepted
    //rimuovere una volta verificato che i test funzionano
    @Given("il mandate in uso è quello con id {string}")
    public void mockMandate(String mandateId) {
        mandateDtoB2b = new MandateDto();
        mandateDtoB2b.setMandateId(mandateId);
    }

    @Given("calcolo il qrCode dello notifica con iun {string}")
    public void calcoloIlQrCodeDelloNotificaConIun(String iun) {
        qrCode = "http://cittadini.notifichedigitali.it/io?aar=" +
                (sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(iun, 0));
        log.info("QRCODE = " + qrCode);
    }
}
