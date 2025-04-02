package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.config.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebUserAttributesClient;
import it.pagopa.pn.client.b2b.pa.service.impl.*;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.RequestNewApiKey;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.ResponseNewApiKey;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.CourtesyDigitalAddress;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalAndUnverifiedDigitalAddress;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalChannelType;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.UserAddresses;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.*;
import it.pagopa.pn.cucumber.utils.DataTest;
import it.pagopa.pn.cucumber.utils.EventId;
import it.pagopa.pn.cucumber.utils.GroupPosition;
import it.pagopa.pn.cucumber.utils.TimelineEventId;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AssertionFailureBuilder;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Costanti.*;
import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion.V24;
import static it.pagopa.pn.cucumber.utils.FiscalCodeGenerator.generateCF;
import static it.pagopa.pn.cucumber.utils.NotificationValue.TAX_ID;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;


@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class SharedSteps {

    @Getter
    private final IPnPaB2bClient b2bClient;

    @Getter
    private final IPnWebPaClient webPaClient;

    @Getter
    private final PnGPDClientImpl pnGPDClientImpl;

    @Getter
    private final PnPaymentInfoClientImpl pnPaymentInfoClientImpl;

    @Getter
    private final IPnTosPrivacyClientImpl iPnTosPrivacyClientImpl;

    @Getter
    private final PnPaB2bUtils b2bUtils;

    @Getter
    private final PnPollingFactory pollingFactory;

    @Getter
    private final PnExternalServiceClientImpl pnExternalServiceClient;

    @Getter
    private final PnServiceDeskClientImpl serviceDeskClient;

    @Getter
    @Setter
    private IPnWebRecipientClient webRecipientClient;

    @Getter
    @Setter
    private HttpStatusCodeException notificationError;

    @Getter
    @Setter
    private OffsetDateTime notificationCreationDate;

    @Getter
    @Setter
    private RequestNewApiKey requestNewApiKey;

    @Getter
    @Setter
    private ResponseNewApiKey responseNewApiKey;

    @Getter
    @Setter
    private String errorCode;

    @Getter
    @Value("${pn.external.bearer-token-pg1.id}")
    private String idOrganizationGherkinSrl;

    @Getter
    @Value("${pn.external.bearer-token-pg2.id}")
    private String idOrganizationCucumberSpa;

    @Getter
    private final DataTableTypeUtil dataTableTypeUtil;

    @Getter
    private final HashMap<String, String> mapAllegatiNotificaSha256 = new HashMap<>();

    private IPnWebUserAttributesClient iPnWebUserAttributesClient;

    private boolean groupToSet = true;

    private final ApplicationContext context;

    private final List<String> iuvGPD;

    private final SecureRandom secureRandom;

    private final PnB2bClientTimingConfigs timingConfigs;

    private final ObjectMapper objMapper;

    /**
     * Campo chiave della classe, rappresentante lo IUN della notifica creata,
     * da cui poi recuperare la FullSentNotification (di qualsivoglia versione) tramite chiamata al B2B
     */
    @Getter
    @Setter
    private String notificationIun;

    /**
     * Rappresenta la versione con cui è stata generata una notifica. Viene impostata al momento di preparazione della request.
     * Va da sè che gli step successivi (aggiunta di destinatari, invio, etc) dovranno anch'essi utilizzare tale versione, salvo diversamente specificato.
     */
    @Getter
    @Setter
    private NotificationVersion versionUsed;

    /**
     * Mappa contenente le varie istanze di NotificationStepsInterface.
     */
    @Getter
    private final Map<NotificationVersion, NotificationStepsInterface> mapOfVersionSteps = NotificationVersion.getMapOfNotificationSteps(this);

    @Before("@useB2B")
    public void beforeMethod() {
        if (!(webRecipientClient instanceof B2BRecipientExternalClientImpl)) {
            this.webRecipientClient = context.getBean(B2BRecipientExternalClientImpl.class);
        }
        this.iPnWebUserAttributesClient = context.getBean(B2BUserAttributesExternalClientImpl.class);
    }

    @Autowired
    public SharedSteps(ApplicationContext context, DataTableTypeUtil dataTableTypeUtil, IPnPaB2bClient b2bClient,
                       PnPaB2bUtils b2bUtils, PnWebRecipientExternalClientImpl webRecipientClient,
                       PnExternalServiceClientImpl pnExternalServiceClient,
                       PnWebUserAttributesExternalClientImpl iPnWebUserAttributesClient, IPnWebPaClient webPaClient,
                       PnServiceDeskClientImpl serviceDeskClient,
                       PnGPDClientImpl pnGPDClientImpl,
                       PnPaymentInfoClientImpl pnPaymentInfoClientImpl, PnB2bClientTimingConfigs timingConfigs,
                       PnPollingFactory pollingFactory, IPnTosPrivacyClientImpl iPnTosPrivacyClientImpl) {
        this.context = context;
        this.dataTableTypeUtil = dataTableTypeUtil;
        this.b2bClient = b2bClient;
        this.webPaClient = webPaClient;
        this.b2bUtils = b2bUtils;
        this.webRecipientClient = webRecipientClient;
        this.pnExternalServiceClient = pnExternalServiceClient;
        this.iPnWebUserAttributesClient = iPnWebUserAttributesClient;
        this.serviceDeskClient = serviceDeskClient;
        this.pnGPDClientImpl = pnGPDClientImpl;
        this.pnPaymentInfoClientImpl = pnPaymentInfoClientImpl;
        this.iuvGPD = new ArrayList<>();
        this.timingConfigs = timingConfigs;
        this.pollingFactory = pollingFactory;
        this.iPnTosPrivacyClientImpl = iPnTosPrivacyClientImpl;
        this.objMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        this.secureRandom = new SecureRandom();
    }

    @BeforeAll
    public static void before_all() {
        log.debug("SHARED_GLUE START");
        //only for class activation
    }

    @Before
    public void injectScenarioNameInsideSfl4jMdc(Scenario scenario) {
        String scenarioName = scenario.getName();
        MDC.put(RestTemplateConfiguration.CUCUMBER_SCENARIO_NAME_MDC_ENTRY, scenarioName);
    }

    @Before("@integrationTest")
    public void doSomethingAfter() {
        this.groupToSet = false;
    }

    /**
     * Restituisce lo FullSentNotification aggiornata all'ultima versione (quella maggiormente utilizzata a codice)
     * TODO: se e quando verrà introdotta una nuova versione, ri-fattorizzare il tipo di oggetto ritornato e cambiare i punti di codice che richiamano questo metodo
     */
    public FullSentNotificationV26 getSentNotificationLastVersion() {
        if (notificationIun != null) {
            return b2bClient.getSentNotificationV26(notificationIun);
        }
        throw new RuntimeException("Lo IUN non è valorizzato, qualcosa è andato storto nei passaggi precedenti");
    }

    public NotificationVersion getNotificationVersion(String version) {
        if (version.trim().equalsIgnoreCase(MOST_RECENT)) {
            return NotificationVersion.V24;//TODO: modificare questo valore ogni volta che viene aggiunta una versione più recente
        }
        return NotificationVersion.valueOf(version.trim().toUpperCase());
    }

    private NotificationStepsInterface getNotificationStepInterface() {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        return getNotificationStepInterface(notificationVersion);
    }

    private NotificationStepsInterface getNotificationStepInterface(NotificationVersion notificationVersion) {
        return mapOfVersionSteps.get(notificationVersion);
    }

    /**
     * Per test di utilità generale, che non si prefiggono di testare qualcosa legato a una versione specifica, usare questo step
     */
    @Given("viene generata una nuova notifica")
    public void prepareNotificationRequest(Map<String, String> data) {
        prepareNotificationRequestWithVersion(MOST_RECENT, data);
    }

    /**
     * Per test che si prefiggono di testare qualcosa legato a una versione specifica, usare questo step
     */
    @Given("viene generata una nuova notifica con la versione {string}")
    public void prepareNotificationRequestWithVersion(String version, Map<String, String> data) {
        NotificationVersion notificationVersion = getNotificationVersion(version);
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        notificationStepsInterface.prepareNotificationRequest(data);
    }

    @And("destinatario")
    public void addDestinatario(Map<String, String> data) {
        getNotificationStepInterface().addRecipientToNotification(null, data);
    }

    @And("destinatario {destinatario}")
    public void addDestinatario(Destinatario destinatario) {
        getNotificationStepInterface().addRecipientToNotification(destinatario, new HashMap<>());
    }

    @And("destinatario {destinatario} e:")
    public void addDestinatarioWithParams(Destinatario destinatario, Map<String, String> data) {
        getNotificationStepInterface().addRecipientToNotification(destinatario, data);
    }

    @And("al destinatario viene associato lo iuv creato mediante partita debitoria alla posizione {int}")
    public void destinatarioAddIuvGPD(Integer posizione) {
        String iuvGPD = getIuvGPD(posizione);
        getNotificationStepInterface().setIuvToRecipient(posizione, iuvGPD);
    }

    /**
     * Invio massivo di notifiche irreperibili utili per i test radd
     * TODO MATTEO -> questo metodo va assolutamente re-fattorizzato, magari anche riscrivendo gli step
     */
    //TODO MATTEO TEST REFACTOR
    @Given("vengono inviate {int} notifiche per l'utente {destinatario} con il {string} e si aspetta fino allo stato COMPLETELY_UNREACHABLE")
    public void sendManyNotificationsForUserAndWaitUntilCompletelyUnreachable(int numberOfNotification, Destinatario destinatario, String paName) {

        String taxId = destinatario.equals(Destinatario.DESTINATARIO_SIGNOR_CASUALE) ? generateCF(System.nanoTime()) : destinatario.getTaxId();

        Map<String, String> notificationRequestMap = Map.ofEntries(
                Map.entry(SUBJECT.key, "notificaAnalogica con Cucumber"),
                Map.entry(SENDER_DENOMINATION.key, "Comune di Palermo"),
                Map.entry(PHYSICAL_COMMUNICATION_TYPE.key, "AR_REGISTERED_LETTER"));

        Map<String, String> notificationRecipientMap = Map.ofEntries(
                Map.entry(DENOMINATION.key, destinatario.getDenomination()),
                Map.entry(TAX_ID.key, taxId),
                Map.entry(DIGITAL_DOMICILE.key, "NULL"),
                Map.entry(PHYSICAL_ADDRESS_ADDRESS.key, "Via NationalRegistries @fail-Irreperibile_AR"));

        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface();
        notificationStepsInterface.prepareNotificationRequest(notificationRequestMap);
        notificationStepsInterface.addRecipientToNotification(Destinatario.DESTINATARIO_SIGNOR_CASUALE, notificationRecipientMap);
        setPaAndSenderTaxId(paName);

        List<Thread> threadList = new LinkedList<>();
        AtomicInteger notificationsCounter = new AtomicInteger();
        for (int i = 0; i < numberOfNotification; i++) {
            Thread t = new Thread(() -> {
                String newNotificationIun = notificationStepsInterface.sendNotification(getWorkFlowWait(), NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS);
                notificationStepsInterface.waitForTimelineElement(newNotificationIun, COMPLETELY_UNREACHABLE, 33);
                notificationsCounter.getAndIncrement();
            });
            threadList.add(t);
            t.start();
        }

        int attempts = 0;
        boolean completed = false;

        while (attempts < 50) {
            threadWait(getWorkFlowWait());
            int counter = 0;
            for (Thread thread : threadList) {
                if (!thread.isAlive()) counter++;
            }
            if (counter == threadList.size()) {
                completed = true;
                break;
            } else {
                attempts++;
            }
        }
        Assertions.assertTrue(completed);
        Assertions.assertEquals(numberOfNotification, notificationsCounter.get());
    }

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e codice avviso {isUguale}")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreAndCodiceAvvisoVariabile(boolean isCodiceAvvisoUguale) {
        getNotificationStepInterface().prepareNotificationRequestSimileAllaPrecedente(
                true, isCodiceAvvisoUguale, false, null);
    }

    @And("viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken {string}")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualePaProtocolNumberEIdempotenceToken(String idempotenceToken) {
        getNotificationStepInterface().prepareNotificationRequestSimileAllaPrecedente(
                false, false, true, idempotenceToken);
    }

    @And("viene generata una nuova notifica con uguale paProtocolNumber")
    public void vieneGenerataUnaNuovaNotificaConUgualePaProtocolNumber() {
        getNotificationStepInterface().prepareNotificationRequestSimileAllaPrecedente(
                false, false, true, null);
    }

    @And("destinatario {destinatario} con codice avviso uguale a quello del destinatario numero {int}")
    public void destinatarioConUgualeCodiceAvvisoDelDestinatarioN(Destinatario destinatario, int recipientIndex, Map<String, String> data) {
        getNotificationStepInterface().addRecipientToNotificationSpecialCondition(destinatario, data, "SAME_IUV_AS_RECIPIENT_INDEX", recipientIndex);
    }

    @And("aggiungo {int} numero allegati")
    public void aggiungoNumeroAllegati(int numAllegati) {
        getNotificationStepInterface().addDocumentItems(numAllegati);
    }

    @When("la notifica viene inviata tramite api b2b e si attende che lo stato diventi {string}")
    public void sendNotificationWithoutSettingPa(String status) {
        sendNotification(null, status);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi {string}")
    public void sendNotification(String paName, String status) {
        if (status.equalsIgnoreCase("HTTP_ERROR")) {
            sendNotificationHttpError(paName);
        } else {
            NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
            String versionString = notificationVersion.name();
            sendNotificationWithVersion(versionString, paName, status);
        }
    }

    @When("la notifica viene inviata tramite api b2b con la versione {string} dal {string} e si attende che lo stato diventi {string}")
    public void sendNotificationWithVersion(String version, String paName, String status) {
        if (!version.equalsIgnoreCase(versionUsed.name())) {
            throw new RuntimeException("Impossibile inviare con la " + version + " una notifica creata con la " + versionUsed.name());
        }
        NotificationVersion notificationVersion = getNotificationVersion(version);
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        if (paName != null) {
            setPaAndSenderTaxId(paName);
        }
        //TODO MATTEO: un tempo lo stato era sempre ACCEPTED, ora che è parametrico forse la pollingStrategy andrebbe desunta con qualche metodo che si basa sullo stato
        notificationStepsInterface.sendNotification(getWorkFlowWait(), status, VALIDATION_STATUS);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED per controllo GPD")
    public void laNotificaVieneInviataOkGPD(String paName) {
        setPaAndSenderTaxId(paName);
        getNotificationStepInterface().sendNotification(WAITING_GPD, NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS_ACCEPTATION_SHORT);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si controlla con check rapidi che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataOkRapidCheck(String paName) {
        setPaAndSenderTaxId(paName);
        getNotificationStepInterface().sendNotification(100, NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS_ACCEPTATION_SHORT);
    }

    @When("verifica che la notifica inviata tramite api b2b dal {string} non diventi ACCEPTED")
    public void laNotificaVieneInviataNoAccept(String paName) {
        setPaAndSenderTaxId(paName);
        //TODO MATTEO: prima richiamava waitForRequestNoAcceptation in b2bUtils. Ma è corretto che prenda "ACCEPTED" anche se non viene accettata ?
        getNotificationStepInterface().sendNotification(getWorkFlowWait(), NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS_NO_ACCEPTATION);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED e successivamente annullata")
    public void laNotificaVieneInviataOkAndCancelled(String paName) {
        setPaAndSenderTaxId(paName);
        getNotificationStepInterface().sendNotification(WAIT_EXTRA_RAPID, NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS);
        String iun = getNotificationIun();
        Assertions.assertDoesNotThrow(() -> {
            RequestStatus resp = Assertions.assertDoesNotThrow(() -> b2bClient.notificationCancellation(iun));
            Assertions.assertNotNull(resp);
            Assertions.assertNotNull(resp.getDetails());
            Assertions.assertFalse(resp.getDetails().isEmpty());
            Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));
        });
    }

    @When("la notifica viene inviata tramite api b2b dal {string} con allegato uguale all'allegato di pagamento")
    public void laNotificaVieneInviataAllegatiUgualeAlPagamento(String paName) {
        setPaAndSenderTaxId(paName);
        try {
            getNotificationStepInterface().uploadNotificationAllegatiUgualiPagamento();
        } catch (HttpStatusCodeException | IOException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    @And("la notifica {string} essere annullata dal sistema tramite codice IUN dal comune {string}")
    public void notificationCanBeCanceledWithIunByComune(String annullabile, String paName) {
        setPA(paName);
        String iun = getNotificationIun();
        if (annullabile.equalsIgnoreCase("può")) {
            Assertions.assertDoesNotThrow(() -> {
                RequestStatus response = b2bClient.notificationCancellation(iun);
                Assertions.assertNotNull(response);
                Assertions.assertNotNull(response.getDetails());
                Assertions.assertFalse(response.getDetails().isEmpty());
                Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(response.getDetails().get(0).getCode()));
            });
        } else {
            try {
                b2bClient.notificationCancellation(iun);
            } catch (HttpStatusCodeException exception) {
                this.notificationError = exception;
            }
        }
    }

    //TODO MATTEO: ho riscritto il metodo in modo che funzioni con ogni versione, ma:
    // 1) si potrebbe cancellare (il test che lo invoca non fa parte di nessuna suite)
    // 2) era scritto male: lo step precedente non valorizza in alcun modo request o response, quindi il requestId del log sarà sempre null
    // (e comunque il requestId potrebbe recuperarselo dal parametro).
    // In sostanza, se ne potrebbe fare tranquillamente a meno
    @And("viene effettuato recupero stato della notifica dal comune {string} con la versione {string}")
    public void getNotificationRequestStatus(String paName, String version) {
        setPaAndSenderTaxId(paName);
        String requestId = Base64Utils.encodeToString(getNotificationIun().getBytes());
        NotificationVersion notificationVersion = getNotificationVersion(version);
        getNotificationStepInterface(notificationVersion).getNotificationRequestStatus(requestId);
    }

    @Then("l'operazione di annullamento ha prodotto un errore con status code {string}")
    public void cancellationProducedErrorWithStatusCode(String statusCode) {
        Assertions.assertTrue(this.notificationError != null && this.notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode));
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si annulla prima che lo stato diventi REFUSED")
    public void laNotificaVieneInviataRefusedAndCancelled(String paName) {
        setPaAndSenderTaxId(paName);
        getNotificationStepInterface().sendNotification(1000, NOTIFICATION_STATUS_NOT_REFUSED, VALIDATION_STATUS);
    }

    //Per test normalizzatore
    //NOTA: il metodo riceve un parametro da scenario Outline, per quello sembra non venga richiamato (AddressValidation.feature)
    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi HTTP_ERROR")
    public void sendNotificationHttpError(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationWithError();
        Assertions.assertNotNull(this.notificationError);
        Assertions.assertEquals(400, this.notificationError.getStatusCode().value());
    }

    //TODO MATTEO: è identico al metodo sotto...perché? Procedere con la cancellazione?
    @When("la notifica viene inviata tramite api b2b senza preload allegato dal {string}")
    public void laNotificaVieneInviataTramiteApiB2bSenzaPreloadAllegato(String pa) {
        setPaAndSenderTaxId(pa);
        sendNotificationRefusedDueToError("NOT_FOUND_ALLEGATO", false);
    }

    @When("la notifica viene inviata tramite api b2b senza preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataSenzaPreloadAllegato(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError("NOT_FOUND_ALLEGATO", false);
    }

    @When("la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataTramiteApiBBEffettuandoLaPreloadMaSenzaCaricareNessunAllegatoDalESiAttendeCheLoStatoDiventiREFUSED(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError("NOT_FOUND_ALLEGATO", true);
    }

    @When("la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato json dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataTramiteApiBBEffettuandoLaPreloadMaSenzaCaricareNessunAllegatoJsonDalESiAttendeCheLoStatoDiventiREFUSED(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError("NOT_FOUND_ALLEGATO_JSON", true);
    }

    @When("la notifica viene inviata tramite api b2b con sha256 differente dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConShaDifferente(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError("NOT_EQUAL_SHA", null);
    }

    @When("la notifica viene inviata tramite api b2b con sha256 Json differente dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConShaJsonDifferente(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError("NOT_EQUAL_SHA_JSON", null);
    }

    @When("la notifica viene inviata tramite api b2b con estensione errata dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConEstensioneErrata(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError("WRONG_EXTENSION", null);
    }

    //Non viene richiamato da nessuno step: rimuovere?
    @When("la notifica viene inviata tramite api b2b oversize preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoOverSize(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError("OVERSIZE_ALLEGATO", null);
    }

    //Non viene richiamato da nessuno step: rimuovere?
    @When("la notifica viene inviata tramite api b2b injection preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoInjection(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError("NOTIFICATION_INJECTION_ALLEGATO", null);
    }

    //Non viene richiamato da nessuno step: rimuovere?
    @When("la notifica viene inviata tramite api b2b over 15 preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoOver15(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError("OVER_15_ALLEGATO", null);
    }

    @When("la notifica viene inviata dal {string}")
    public void laNotificaVieneInviataDallaPA(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationWithError();
    }

    @When("la notifica viene inviata tramite api b2b")
    public void laNotificaVieneInviataTramiteApiB2b() {
        sendNotificationWithError();
    }

    private void sendNotificationWithError() {
        try {
            getNotificationStepInterface().uploadNotification();
        } catch (HttpStatusCodeException | IOException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    @And("al destinatario viene associato lo iuv creato mediante partita debitoria per {string} alla posizione {int}")
    public void destinatarioAddIuvGPD(String denominazione, Integer posizioneDebitoria) {
        getNotificationStepInterface().addIuvGdpToDestinatario(denominazione, getIuvGPD(posizioneDebitoria), posizioneDebitoria);
    }

    @And("al destinatario viene associato lo iuv creato mediante partita debitoria per {string} per la posizione debitoria {int} del pagamento {int}")
    public void destinatarioAddIuvGPDperUtente(String denominazione, Integer posizioneDebitoria, Integer paymentIndex) {
        getNotificationStepInterface().addIuvGdpToDestinatario(denominazione, getIuvGPD(posizioneDebitoria), paymentIndex);
    }

    //Spostato da InvioNotificheB2bSteps
    @And("viene controllato la presenza del taxonomyCode")
    public void checkTaxonomyCode() {
        getNotificationStepInterface().checkTaxonomyCode();
    }

    //Spostato da InvioNotificheB2bSteps
    @And("vengono prodotte le evidenze: metadati e requestID")
    public void produceEvidence() {
        getNotificationStepInterface().produceEvidence();
    }

    //Spostato da InvioNotificheB2bSteps
    @Then("si verifica la corretta acquisizione della richiesta di invio notifica")
    public void correctAcquisitionRequest() {
        getNotificationStepInterface().verifyCorrectAcquisition();
    }

    //Spostato da InvioNotificheB2bSteps
    @Then("viene verificato lo stato di accettazione con idempotenceToken e paProtocolNumber")
    public void vieneVerificatoLoStatoDiAccettazioneConIdempotenceTokenEPaProtocolNumber() {
        getNotificationStepInterface().verifyStatus(false, true, true);

    }

    //Spostato da InvioNotificheB2bSteps
    @Then("viene verificato lo stato di accettazione con requestID")
    public void vieneVerificatoLoStatoDiAccettazioneConRequestID() {
        getNotificationStepInterface().verifyStatus(true, false, false);
    }

    //Spostato da InvioNotificheB2bSteps
    @Then("viene verificato lo stato di accettazione con paProtocolNumber")
    public void vieneVerificatoLoStatoDiAccettazioneConPaProtocolNumber() {
        getNotificationStepInterface().verifyStatus(false, true, false);
    }

    @And("viene rimossa se presente la pec di piattaforma di {string}")
    public void vieneRimossaSePresenteLaPecDiPiattaformaDi(String user) {
        selectUser(user);
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
                this.iPnWebUserAttributesClient.deleteRecipientLegalAddress("default", LegalChannelType.PEC);
                log.info("PEC FOUND AND DELETED");
            }
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
                log.info("PEC NOT FOUND");
            } else {
                throw httpStatusCodeException;
            }
        }
    }

    @And("viene verificata la presenza di pec inserite per l'utente {string}")
    public void viewedPecDiPiattaformaDi(String user) {
        selectUser(user);
        try {
            this.iPnWebUserAttributesClient.getLegalAddressByRecipient().stream()
                    .filter(address -> LegalChannelType.PEC.equals(address.getChannelType()))
                    .findAny()
                    .orElseThrow(() -> AssertionFailureBuilder.assertionFailure().message("PEC NOT FOUND!").build());
        } catch (Exception exc) {
            log.error("Si è verificato un errore durante la verifica di pec inserite: {}", exc.getMessage());
            throw exc;
        }
    }

    @And("viene verificata la presenza di {int} recapiti di cortesia inseriti per l'utente {string}")
    public void viewedCourtesyAddress(int expectedItems, String user) {
        selectUser(user);
        List<CourtesyDigitalAddress> courtesyAddressByRecipient = this.iPnWebUserAttributesClient.getCourtesyAddressByRecipient();
        Assertions.assertEquals(expectedItems, courtesyAddressByRecipient.size(), "Error retrieving the courtesy addresses!");
    }

    @And("viene verificata la presenza di qualunque tipo di recapito inserito per l'utente {string}")
    public void viewedAllAddress(String user) {
        selectUser(user);
        UserAddresses addressesByRecipient = this.iPnWebUserAttributesClient.getAddressesByRecipient();
        Assertions.assertTrue(
                (addressesByRecipient.getCourtesy() != null && !addressesByRecipient.getCourtesy().isEmpty())
                        || (addressesByRecipient.getLegal() != null && !addressesByRecipient.getLegal().isEmpty())
        );
    }

    @Then("stampa log dello IUN della notifica {string} con allegato {string} su comune {string}")
    public void stampaLogDelloIUNDellaNotificaConAllegatoSuComune(String notificationType, String attachment, String municipality) {
        log.info("notifica STAMPA COLORI IUN: {}, notifica: {}, allegato: {}, comune: {}", getNotificationIun(), notificationType, attachment, municipality);
    }

    @Then("si verifica che la notifica non viene accettata causa {string}")
    public void verificaNotificaNoAccept(String cause) {
        String expectedErrorCode = switch (cause) {
            case ALLEGATO -> FILE_NOTFOUND;
            case EXTENSION, FILE_PDF_INVALID_ERROR -> FILE_PDF_INVALID_ERROR;
            case SHA_256 -> FILE_SHA_ERROR;
            case Costanti.TAX_ID -> TAXID_NOT_VALID;
            case ADDRESS, NOT_VALID_ADDRESS -> NOT_VALID_ADDRESS;
            case INVALID_PARAMETER_MAX_ATTACHMENT -> INVALID_PARAMETER_MAX_ATTACHMENT;
            default -> throw new IllegalArgumentException("Invalid failure cause: " + cause);
        };
        Assertions.assertTrue(expectedErrorCode.equalsIgnoreCase(errorCode));
    }

    // TODO MATTEO TEST: 8 vecchi metodi sono stati unificati in questo. Il prossimo step sarebbe capire meglio cosa fanno quei metodi di
    //  utility richiamati nello switch e rimuoverli da B2bUtils (dove c'azzeccano poco, non sono vere utils se vengono richiamate solo qua).
    //  Alcuni di questi non vengono nemmeno mai richiamati da nessun file feature
    //  Altra possibile miglioria: sostituire le stringhe delle tipologie d'errore con costanti all'interno della classe Costanti
    private void sendNotificationRefusedDueToError(String errorType, Boolean noUpload) {
        AtomicReference<NewNotificationResponse> newResponse = new AtomicReference<>();
        //TODO MATTEO IMPORTANTE: al momento è progettato per funzionare solo con la V24.
        // Questi metodi sono l'ultimo scoglio da superare per avere un codice in grado di runnare con qualsiasi versione
        NewNotificationRequestV24 notificationRequest = ((NotificationStepsV24) mapOfVersionSteps.get(V24)).getNotificationRequest();
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                switch (errorType.toUpperCase()) {
                    case "NOT_FOUND_ALLEGATO" ->
                            newResponse.set(b2bUtils.uploadNotificationNotFindAllegato(notificationRequest, noUpload));
                    case "NOT_FOUND_ALLEGATO_JSON" ->
                            newResponse.set(b2bUtils.uploadNotificationNotFindAllegatoJson(notificationRequest, true));
                    case "NOT_EQUAL_SHA" ->
                            newResponse.set(b2bUtils.uploadNotificationNotEqualSha(notificationRequest));
                    case "NOT_EQUAL_SHA_JSON" ->
                            newResponse.set(b2bUtils.uploadNotificationNotEqualShaJson(notificationRequest));
                    case "WRONG_EXTENSION" ->
                            newResponse.set(b2bUtils.uploadNotificationWrongExtension(notificationRequest));
                    case "OVERSIZE_ALLEGATO" ->
                            newResponse.set(b2bUtils.uploadNotificationOverSizeAllegato(notificationRequest));
                    case "NOTIFICATION_INJECTION_ALLEGATO" ->
                            newResponse.set(b2bUtils.uploadNotificationInjectionAllegato(notificationRequest));
                    case "OVER_15_ALLEGATO" ->
                            newResponse.set(b2bUtils.uploadNotificationOver15Allegato(notificationRequest));
                }
                errorCode = b2bUtils.waitForRequestRefusedV25(newResponse.get());
            });
            threadWait(getWorkFlowWait());
            Assertions.assertNotNull(errorCode);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newResponse.get() == null ? "NULL" : newResponse.get().getNotificationRequestId()) + " }";
            if (errorType.equalsIgnoreCase("OVER_15_ALLEGATO")) {
                Assertions.assertTrue(message.contains("400") && message.contains("Max attachment count reached"));
                errorCode = "INVALID_PARAMETER_MAX_ATTACHMENT";
            } else {
                throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
            }
        }
    }

    public int getRecipientsSize() {
        return getNotificationStepInterface().getRecipientsSize();
    }

    public String getRecipientNoticeCode(int recipientIndex, int paymentIndex) {
        return getNotificationStepInterface().getRecipientNoticeCode(recipientIndex, paymentIndex);
    }

    public String getRecipientCreditorTaxId(int recipientIndex, int paymentIndex) {
        return getNotificationStepInterface().getRecipientCreditorTaxId(recipientIndex, paymentIndex);
    }

    public void resetNotificationRequest() {
        getNotificationStepInterface().resetNotificationRequest();
    }

    public HttpStatusCodeException consumeNotificationError() {
        HttpStatusCodeException value = notificationError;
        this.notificationError = null;
        return value;
    }

    private void setPaAndSenderTaxId(String paName) {
        setPA(paName);
        setSenderTaxIdAndGroup(paName);
    }

    public void setPA(String paName) {
        switch (paName) {
            case COMUNE_1 -> {
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_1);
                this.pollingFactory.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_1);
                this.webPaClient.setBearerToken(SettableBearerToken.BearerTokenType.MVP_1);
            }
            case COMUNE_2 -> {
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_2);
                this.pollingFactory.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_2);
                this.webPaClient.setBearerToken(SettableBearerToken.BearerTokenType.MVP_2);
            }
            case COMUNE_MULTI -> {
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.GA);
                this.pollingFactory.setApiKeys(IPnPaB2bClient.ApiKeyType.GA);
                this.webPaClient.setBearerToken(SettableBearerToken.BearerTokenType.GA);
            }
            case COMUNE_SON -> {
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.SON);
                this.pollingFactory.setApiKeys(IPnPaB2bClient.ApiKeyType.SON);
                this.webPaClient.setBearerToken(SettableBearerToken.BearerTokenType.SON);
            }
            case COMUNE_ROOT -> {
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.ROOT);
                this.pollingFactory.setApiKeys(IPnPaB2bClient.ApiKeyType.ROOT);
                this.webPaClient.setBearerToken(SettableBearerToken.BearerTokenType.ROOT);
            }
            default -> throw new IllegalArgumentException("Invalid paName: " + paName);
        }
        this.b2bUtils.setClient(b2bClient, pollingFactory);
    }

    private void setSenderTaxIdAndGroup(String pa) {
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface();
        switch (pa) {
            case COMUNE_1 -> {
                notificationStepsInterface.setSenderTaxId(COMUNE_1_TAX_ID);
                setGroup(SettableApiKey.ApiKeyType.MVP_1);
            }
            case COMUNE_2 -> {
                notificationStepsInterface.setSenderTaxId(COMUNE_2_TAX_ID);
                setGroup(SettableApiKey.ApiKeyType.MVP_2);
            }
            case COMUNE_MULTI -> {
                notificationStepsInterface.setSenderTaxId(COMUNE_MULTI_TAX_ID);
                setGroup(SettableApiKey.ApiKeyType.GA);
            }
            case COMUNE_SON -> {
                notificationStepsInterface.setSenderTaxId(COMUNE_SON_TAX_ID);
                setGroup(SettableApiKey.ApiKeyType.SON);
            }
            case COMUNE_ROOT -> {
                notificationStepsInterface.setSenderTaxId(COMUNE_ROOT_TAX_ID);
                setGroup(SettableApiKey.ApiKeyType.ROOT);
            }
        }
    }

    private void setGroup(SettableApiKey.ApiKeyType apiKeyType) {
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface();
        if (groupToSet && notificationStepsInterface.getNotificationRequestGroup() == null) {
            List<HashMap<String, String>> hashMapsList = pnExternalServiceClient.paGroupInfo(apiKeyType);
            if (hashMapsList == null || hashMapsList.isEmpty()) return;
            String id = null;
            for (HashMap<String, String> elem : hashMapsList) {
                if (elem.get("status").equalsIgnoreCase("ACTIVE")) {
                    id = elem.get("id");
                    break;
                }
            }
            if (id == null) return;
            notificationStepsInterface.setNotificationRequestGroup(id);
        }
    }

    public void setSenderTaxId(String senderTaxId) {
        getNotificationStepInterface().setSenderTaxId(senderTaxId);
    }

    public void setGroup(String group) {
        getNotificationStepInterface().setNotificationRequestGroup(group);
    }

    public void selectUser(String recipient) {
        switch (recipient.trim()) {
            case MARIO_CUCUMBER, ETTORE_FIERAMOSCA -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
            }
            case MARIO_GHERKIN, CRISTOFORO_COLOMBO -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
            }
            case GHERKIN_SRL -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            }
            case CUCUMBER_SPA, LUCIO_ANNEO_SENECA -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            }
            case ALDA_MERINI -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_3);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_3);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.PG_3);
            }
            case LEONARDO_DA_VINCI -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
            }
            case DINO_SAURO -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
            }
            case MARIO_CREDENZIALI_SCADUTE -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
            }
            case GALILEO_GALILEI -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
            }
            default -> throw new IllegalArgumentException("Invalid recipient name: " + recipient);
        }
    }

    public void throwAssertionErrorWithIUN(AssertionError assertionError) {
        String message = decorateErrorMsg(assertionError.getMessage());
        if (assertionError instanceof AssertionFailedError afe) {
            throw new AssertionFailedError(message, afe.getExpected(), afe.getActual(), afe.getCause());
        }
        throw new AssertionError(message, assertionError.getCause());
    }

    private String decorateErrorMsg(String originalMessage) {
        return originalMessage +
                "{VERSION: " + versionUsed + ", " +
                "IUN: " + Optional.ofNullable(getNotificationIun()).orElse("not found") + " }";
    }

    public void throwAssertionFailedErrorWithAmountGDPAndIUN(AssertionFailedError assertionFailedError, Integer amountGDP) {
        String message = assertionFailedError.getMessage() +
                "{IUN: " + getNotificationIun() + ", amountGDP " + (amountGDP == null ? "NULL" : amountGDP.toString()) + "}";
        throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
    }

    public <T> T deepCopy(Object obj, Class<T> toClass) {
        try {
            String json = objMapper.writeValueAsString(obj);
            return objMapper.readValue(json, toClass);
        } catch (JsonProcessingException exc) {
            throw new RuntimeException(exc);
        }
    }

    public Integer getWorkFlowWait() {
        if (timingConfigs.getWorkflowWaitMillis() == null) {
            return WORKFLOW_WAIT_DEFAULT + secureRandom.nextInt(WORKFLOW_WAIT_UPPER_BOUND);
        }
        return timingConfigs.getWorkflowWaitMillis() + secureRandom.nextInt(WORKFLOW_WAIT_UPPER_BOUND);
    }

    public Integer getWait() {
        if (timingConfigs.getWaitMillis() == null) {
            return WAIT_DEFAULT + secureRandom.nextInt(WAIT_UPPER_BOUND);
        }
        return timingConfigs.getWaitMillis() + secureRandom.nextInt(WAIT_UPPER_BOUND);
    }

    public Duration getSchedulingDaysSuccessDigitalRefinement() {
        if (timingConfigs.getSchedulingDaysSuccessDigitalRefinement() == null) {
            return DURATION_DIGITAL_REFINEMENT_DEFAULT_SUCCESS;
        }
        return timingConfigs.getSchedulingDaysSuccessDigitalRefinement();
    }

    public Duration getSchedulingDaysFailureDigitalRefinement() {
        if (timingConfigs.getSchedulingDaysFailureDigitalRefinement() == null) {
            return DURATION_DIGITAL_REFINEMENT_DEFAULT_FAILURE;
        }
        return timingConfigs.getSchedulingDaysFailureDigitalRefinement();
    }

    public Duration getSchedulingDaysSuccessAnalogRefinement() {
        if (timingConfigs.getSchedulingDaysSuccessAnalogRefinement() == null) {
            return DURATION_ANALOG_REFINEMENT_DEFAULT_SUCCESS;
        }
        return timingConfigs.getSchedulingDaysSuccessAnalogRefinement();
    }

    public Duration getSchedulingDaysFailureAnalogRefinement() {
        if (timingConfigs.getSchedulingDaysFailureAnalogRefinement() == null) {
            return DURATION_ANALOG_REFINEMENT_DEFAULT_FAILURE;
        }
        return timingConfigs.getSchedulingDaysFailureAnalogRefinement();
    }

    public Duration getTimeToAddInNonVisibilityTimeCase() {
        if (timingConfigs.getNonVisibilityTime() == null) {
            return DURATION_TIME_TO_ADD_IN_NON_VISIBILITY_TIME_CASE_DEFAULT;
        }
        return timingConfigs.getNonVisibilityTime();
    }

    public Duration getSecondNotificationWorkflowWaitingTime() {
        if (timingConfigs.getSecondNotificationWorkflowWaitingTime() == null) {
            return DURATION_SECOND_NOTIFICATION_WORKFLOW_WAITING_TIME_DEFAULT;
        }
        return timingConfigs.getSecondNotificationWorkflowWaitingTime();
    }

    public Duration getWaitingForReadCourtesyMessage() {
        if (timingConfigs.getWaitingForReadCourtesyMessage() == null) {
            return DURATION_WAIT_READ_COURTESY_MESSAGE_DEFAULT;
        }
        return timingConfigs.getWaitingForReadCourtesyMessage();
    }

    private List<HashMap<String, String>> getGroupsByPa(String paName) {
        List<HashMap<String, String>> hashMapsList = switch (paName) {
            case COMUNE_1 -> this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.MVP_1);
            case COMUNE_2 -> this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.MVP_2);
            case COMUNE_MULTI -> this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.GA);
            case COMUNE_SON -> this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.SON);
            case COMUNE_ROOT -> this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.ROOT);
            default -> throw new IllegalArgumentException("Invalid paName: " + paName);
        };
        Assertions.assertNotNull(hashMapsList);
        Assertions.assertFalse(hashMapsList.isEmpty());
        return hashMapsList;
    }

    public String getGroupIdByPa(String paName, GroupPosition position) {
        List<HashMap<String, String>> hashMapsList = getGroupsByPa(paName);
        String id = null;
        int count = 0;
        for (HashMap<String, String> elem : hashMapsList) {
            if (elem.get("status").equalsIgnoreCase("ACTIVE")) {
                id = elem.get("id");
                count++;
                if (GroupPosition.FIRST.equals(position)) {
                    break;
                }
            }
        }
        Assertions.assertNotNull(id);
        if (!GroupPosition.FIRST.equals(position)) {
            Assertions.assertTrue(count >= 2);
        }
        return id;
    }

    public List<String> getGroupAllActiveByPa(String paName) {
        List<HashMap<String, String>> hashMapsList = getGroupsByPa(paName);
        List<String> groups = new ArrayList<>();
        String id;
        int count = 0;
        for (HashMap<String, String> elem : hashMapsList) {
            if (elem.get("status").equalsIgnoreCase("ACTIVE")) {
                id = elem.get("id");
                count++;
                groups.add(id);
            }
        }
        Assertions.assertTrue(count >= 2);
        return groups;
    }

    public String getTimelineEventId(String timelineEventCategory, String iun, DataTest dataFromTest) {
        EventId event = getEventId(iun, dataFromTest);
        return switch (timelineEventCategory) {
            case SEND_COURTESY_MESSAGE -> TimelineEventId.SEND_COURTESY_MESSAGE.buildEventId(event);
            case REQUEST_REFUSED -> TimelineEventId.REQUEST_REFUSED.buildEventId(event);
            case AAR_GENERATION -> TimelineEventId.AAR_GENERATION.buildEventId(event);
            case REQUEST_ACCEPTED -> TimelineEventId.REQUEST_ACCEPTED.buildEventId(event);
            case SEND_DIGITAL_DOMICILE -> TimelineEventId.SEND_DIGITAL_DOMICILE.buildEventId(event);
            case SEND_DIGITAL_FEEDBACK -> TimelineEventId.SEND_DIGITAL_FEEDBACK.buildEventId(event);
            case GET_ADDRESS -> TimelineEventId.GET_ADDRESS.buildEventId(event);
            case DIGITAL_SUCCESS_WORKFLOW -> TimelineEventId.DIGITAL_SUCCESS_WORKFLOW.buildEventId(event);
            case SCHEDULE_REFINEMENT -> TimelineEventId.SCHEDULE_REFINEMENT_WORKFLOW.buildEventId(event);
            case REFINEMENT -> TimelineEventId.REFINEMENT.buildEventId(event);
            case ANALOG_SUCCESS_WORKFLOW -> TimelineEventId.ANALOG_SUCCESS_WORKFLOW.buildEventId(event);
            case DIGITAL_FAILURE_WORKFLOW -> TimelineEventId.DIGITAL_FAILURE_WORKFLOW.buildEventId(event);
            case SEND_ANALOG_FEEDBACK -> TimelineEventId.SEND_ANALOG_FEEDBACK.buildEventId(event);
            case SEND_SIMPLE_REGISTERED_LETTER_PROGRESS ->
                    TimelineEventId.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS.buildEventId(event);
            case SEND_ANALOG_PROGRESS -> TimelineEventId.SEND_ANALOG_PROGRESS.buildEventId(event);
            case ANALOG_FAILURE_WORKFLOW -> TimelineEventId.ANALOG_FAILURE_WORKFLOW.buildEventId(event);
            case PREPARE_ANALOG_DOMICILE -> TimelineEventId.PREPARE_ANALOG_DOMICILE.buildEventId(event);
            case SCHEDULE_ANALOG_WORKFLOW -> TimelineEventId.SCHEDULE_ANALOG_WORKFLOW.buildEventId(event);
            case SEND_ANALOG_DOMICILE -> TimelineEventId.SEND_ANALOG_DOMICILE.buildEventId(event);
            case SEND_SIMPLE_REGISTERED_LETTER -> TimelineEventId.SEND_SIMPLE_REGISTERED_LETTER.buildEventId(event);
            case PREPARE_SIMPLE_REGISTERED_LETTER ->
                    TimelineEventId.PREPARE_SIMPLE_REGISTERED_LETTER.buildEventId(event);
            case NOTIFICATION_VIEWED -> TimelineEventId.NOTIFICATION_VIEWED.buildEventId(event);
            case COMPLETELY_UNREACHABLE -> TimelineEventId.COMPLETELY_UNREACHABLE.buildEventId(event);
            case DIGITAL_DELIVERY_CREATION_REQUEST ->
                    TimelineEventId.DIGITAL_DELIVERY_CREATION_REQUEST.buildEventId(event);
            case ANALOG_WORKFLOW_RECIPIENT_DECEASED ->
                    TimelineEventId.ANALOG_WORKFLOW_RECIPIENT_DECEASED.buildEventId(event);
            default -> null;
        };
    }

    private static EventId getEventId(String iun, DataTest dataFromTest) {
        TimelineElementV23 timelineElement = dataFromTest.getTimelineElement();
        TimelineElementDetailsV23 timelineElementDetails = timelineElement.getDetails();
        DigitalAddress digitalAddress = timelineElementDetails == null ? null : timelineElementDetails.getDigitalAddress();
        DigitalAddressSource digitalAddressSource = timelineElementDetails == null ? null : timelineElementDetails.getDigitalAddressSource();

        EventId event = new EventId();
        event.setIun(iun);
        event.setRecIndex(timelineElementDetails == null ? null : timelineElementDetails.getRecIndex());
        event.setCourtesyAddressType(digitalAddress == null ? null : digitalAddress.getType());
        event.setSource(digitalAddressSource == null ? null : digitalAddressSource.getValue());
        event.setIsFirstSendRetry(dataFromTest.getIsFirstSendRetry());
        event.setSentAttemptMade(timelineElementDetails == null ? null : timelineElementDetails.getSentAttemptMade());
        event.setProgressIndex(dataFromTest.getProgressIndex());
        return event;
    }

    /**
     * Get all timeline elements that match the given event category and data from test
     *
     * @param timelineEventCategory the category of the timeline event
     * @param dataFromTest          the data filters
     * @return a list of timeline elements that match the given event category and data from test
     */
    public List<TimelineElementV26> getTimelineElementsByEventId(String timelineEventCategory, DataTest dataFromTest) {
        FullSentNotificationV26 fullSentNotification = getSentNotificationLastVersion();
        List<TimelineElementV26> timelineElementList = fullSentNotification.getTimeline();
        String iun = getIun(timelineEventCategory);
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            // get timeline event id
            String timelineEventId = getTimelineEventId(timelineEventCategory, iun, dataFromTest);
            if (timelineEventCategory.equals(TimelineElementCategoryV26.SEND_ANALOG_PROGRESS.getValue())
                    || timelineEventCategory.equals(TimelineElementCategoryV26.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS.getValue())) {
                TimelineElementV23 timelineElementFromTest = dataFromTest.getTimelineElement();
                TimelineElementDetailsV23 timelineElementDetails = timelineElementFromTest.getDetails();
                return timelineElementList.stream()
                        .filter(
                                elem -> Objects.requireNonNull(elem.getElementId()).startsWith(timelineEventId)
                                        && Objects.equals(Objects.requireNonNull(elem.getDetails()).getDeliveryDetailCode(), Objects.requireNonNull(timelineElementDetails).getDeliveryDetailCode()))
                        .toList();
            }
            return timelineElementList.stream().filter(elem -> Objects.requireNonNull(elem.getElementId()).contains(timelineEventId)).toList();
        }
        return timelineElementList.stream().filter(elem -> Objects.requireNonNull(elem.getCategory()).getValue().equals(timelineEventCategory)).toList();
    }

    /**
     * Get all timeline elements having attempt index less or equal to the given one
     *
     * @param attemptIndex the index of the attempt (starting from 0)
     * @return a list of timeline elements that match the given event category and data from test
     */
    public List<TimelineElementV26> getTimelineElementsToAttempt(int attemptIndex) {
        FullSentNotificationV26 fullSentNotification = getSentNotificationLastVersion();
        List<TimelineElementV26> timelineElementList = fullSentNotification.getTimeline();
        return timelineElementList.stream()
                .filter(elem -> nonNull(elem.getDetails()))
                //TODO: ignorare Sonar che dice che questo nonNull è inutile in quanto sempre true, non è vero
                .filter(elem -> nonNull(elem.getDetails().getSentAttemptMade()))
                .filter(elem -> elem.getDetails().getSentAttemptMade() <= attemptIndex)
                .toList();
    }

    public TimelineElementV26 getTimelineElementByEventId(String timelineEventCategory, DataTest dataFromTest) {
        return getTimelineElementsByEventId(timelineEventCategory, dataFromTest).stream()
                .findAny()
                .orElse(null);
    }

    public String getNotificationRequestId() {
        return getNotificationStepInterface().getNotificationRequestId();
    }

    private String getIun(String timelineEventCategory) {
        String iun;
        if (timelineEventCategory.equals(REQUEST_REFUSED)) {
            String requestId = getNotificationRequestId();
            byte[] decodedBytes = Base64.getDecoder().decode(requestId);
            iun = new String(decodedBytes);
        } else {
            // proceed with default flux
            iun = getNotificationIun();
        }
        return iun;
    }

    public Integer getSchedulingDelta() {
        if (timingConfigs.getSchedulingDeltaMillis() == null) {
            return SCHEDULING_DELTA_DEFAULT;
        }
        return timingConfigs.getSchedulingDeltaMillis();
    }

    public void addIuvGPD(String iuvGPD) {
        this.iuvGPD.add("3" + iuvGPD);
    }

    private String getIuvGPD(int posizione) {
        return this.iuvGPD.get(posizione);
    }

    public List<String> getDatiPagamentoVersionamento(Integer destinatario, Integer pagamento) {
        return getNotificationStepInterface().getDatiPagamento(notificationIun, destinatario, pagamento);
    }

    public static void threadWait(int wait) {
        try {
            await().atMost(wait, TimeUnit.MILLISECONDS);
        } catch (RuntimeException exception) {
            log.error("Await error exception");
            throw exception;
        }
    }
}