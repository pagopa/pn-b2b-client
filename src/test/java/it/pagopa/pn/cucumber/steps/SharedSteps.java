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
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffRequestNewApiKey;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffResponseNewApiKey;
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
import it.pagopa.pn.client.b2b.pa.wrapper.LegalCourtesyAddressWrapper;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.CourtesyDigitalAddress;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.UserAddresses;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationStepsInterface;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.steps.utilitySteps.Costanti;
import it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario;
import it.pagopa.pn.cucumber.utils.DataTest;
import it.pagopa.pn.cucumber.utils.EventId;
import it.pagopa.pn.cucumber.utils.GroupPosition;
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
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static it.pagopa.pn.cucumber.utils.FiscalCodeGenerator.generateCF;
import static it.pagopa.pn.cucumber.utils.NotificationValue.TAX_ID;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;


@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class SharedSteps {

    @Getter
    private final ApplicationContext context;

    @Getter
    private final IPnPaB2bClient b2bClient;

    @Getter
    private final PnPollingFactory pollingFactory;

    @Getter
    private final IPnWebPaClient webPaClient;

    @Getter
    private final PnGPDClientImpl pnGPDClientImpl;

    @Getter
    private final PnPaymentInfoClientImpl pnPaymentInfoClientImpl;

    @Getter
    private final IPnTosPrivacyClientImpl iPnTosPrivacyClientImpl;

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
    private BffRequestNewApiKey requestNewApiKey;

    @Getter
    @Setter
    private BffResponseNewApiKey responseNewApiKey;

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
    private final HashMap<String, String> mapAllegatiNotificaSha256 = new HashMap<>();

    private IPnWebUserAttributesClient iPnWebUserAttributesClient;

    private boolean groupToSet = true;

    private final List<String> iuvGPD;

    private final SecureRandom secureRandom;

    private final PnB2bClientTimingConfigs timingConfigs;

    private final ObjectMapper objMapper;

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
    private final Map<NotificationVersion, NotificationStepsInterface> mapOfVersionSteps = new HashMap<>();

    /**
     * Lo IUN della notifica che viene creata (dell'ultima, nei rari casi di più notifiche create simultaneamente) e
     * viene salvato in questa variabile.
     * Tramite esso è poi possibile recuperare le FullSentNotification (di qualsivoglia versione) richiamando il B2B.
     * Qualora venga inviata una notifica che andrà in stato REFUSED (o la si cancelli prima che raggiunga lo stato REFUSED),
     * lo IUN settato viene impostato decodificando in Base64 il requestId
     */
    @Setter
    @Getter
    private String notificationIun;

    @Before("@useB2B")
    public void beforeMethod() {
        if (!(webRecipientClient instanceof B2BRecipientExternalClientImpl)) {
            this.webRecipientClient = context.getBean(B2BRecipientExternalClientImpl.class);
        }
        this.iPnWebUserAttributesClient = context.getBean(B2BUserAttributesExternalClientImpl.class);
    }

    @Autowired
    public SharedSteps(ApplicationContext context,
                       IPnPaB2bClient b2bClient,
                       PnPollingFactory pollingFactory,
                       IPnWebPaClient webPaClient,
                       PnWebRecipientExternalClientImpl webRecipientClient,
                       PnExternalServiceClientImpl pnExternalServiceClient,
                       PnWebUserAttributesExternalClientImpl iPnWebUserAttributesClient,
                       PnServiceDeskClientImpl serviceDeskClient,
                       PnGPDClientImpl pnGPDClientImpl,
                       PnPaymentInfoClientImpl pnPaymentInfoClientImpl,
                       IPnTosPrivacyClientImpl iPnTosPrivacyClientImpl,
                       PnB2bClientTimingConfigs timingConfigs) {
        this.context = context;
        this.b2bClient = b2bClient;
        this.pollingFactory = pollingFactory;
        this.webPaClient = webPaClient;
        this.webRecipientClient = webRecipientClient;
        this.pnExternalServiceClient = pnExternalServiceClient;
        this.iPnWebUserAttributesClient = iPnWebUserAttributesClient;
        this.serviceDeskClient = serviceDeskClient;
        this.pnGPDClientImpl = pnGPDClientImpl;
        this.pnPaymentInfoClientImpl = pnPaymentInfoClientImpl;
        this.iPnTosPrivacyClientImpl = iPnTosPrivacyClientImpl;
        this.timingConfigs = timingConfigs;
        this.iuvGPD = new ArrayList<>();
        this.objMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        this.secureRandom = new SecureRandom();
        versionUsed = getNotificationVersion(MOST_RECENT);
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
     */
    //TODO: all'introduzione di una nuova versione, ri-fattorizzare il tipo di oggetto ritornato e cambiare i punti di codice che richiamano questo metodo
    public FullSentNotificationV27 getSentNotificationLastVersion() {
        return b2bClient.getSentNotificationV27(notificationIun);
    }

    /**
     * Restituisce lo FullSentNotification aggiornata all'ultima versione (quella maggiormente utilizzata a codice)
     * ma a differenza del metodo sopra anziché usare il notificationIun di SharedSteps usa uno IUN arbitrario.
     * Usato in un solo punto del codice
     */
    //TODO: all'introduzione di una nuova versione, ri-fattorizzare il tipo di oggetto ritornato e cambiare i punti di codice che richiamano questo metodo
    public FullSentNotificationV27 getSentNotificationLastVersionByIun(String iun) {
        return b2bClient.getSentNotificationV27(iun);
    }

    public NotificationVersion getNotificationVersion(String version) {
        if (version.trim().equalsIgnoreCase(MOST_RECENT)) {
            return NotificationVersion.V25;//TODO: modificare questo valore ogni volta che viene aggiunta una versione più recente
        }
        return NotificationVersion.valueOf(version.trim().toUpperCase());
    }

    private NotificationStepsInterface getNotificationStepInterface() {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        return getNotificationStepInterface(notificationVersion);
    }

    private NotificationStepsInterface getNotificationStepInterface(NotificationVersion notificationVersion) {
        if (mapOfVersionSteps.get(notificationVersion) == null) {
            mapOfVersionSteps.put(notificationVersion, NotificationVersion.createNotificationStep(notificationVersion, this));
        }
        return mapOfVersionSteps.get(notificationVersion);
    }

    /**
     * Metodo a soli fine di debugging, da non essere utilizzato in nessuno scenario.
     * Se si ha già pronta una notifica e si vogliono testare dei metodi che riguardano la timeline,
     * anziché crearla da zero, aspettare che arrivi in ACCEPTED, etc si imposta lo IUN qua e la PA e
     * si può procedere con il resto dei metodi.
     */
    @Given("imposto lo iun di SharedSteps a {string} e la pa a {string}")
    public void impostoIunAndPaForTestPurposes(String iun, String paName) {
        notificationIun = iun;
        setPA(paName);
        /*Imposta la data di creazione a cinque giorni fa (sufficienti per testare) e crea una notification request con un destinatario
        e crea una request con destinatario Mario Cucumber (questi passaggi servono per poter recuperare anche le notifiche andate in REFUSED) */
        notificationCreationDate = OffsetDateTime.now(ZoneOffset.UTC).minusDays(5);
        getNotificationStepInterface().prepareNotificationRequest(Map.of(
                "subject", "MOCKED NOTIFICATION",
                "senderDenomination", "Comune di Palermo"));
        getNotificationStepInterface().addRecipientToNotification(Destinatario.DESTINATARIO_MARIO_CUCUMBER, new HashMap<>());
    }

    /**
     * Effettua un controllo sulla versione che si sta utilizzando, per verificare se è pari o superiore
     * a quella in uso.
     * Sarebbe buona prassi iniziare tutti gli scenari futuri con questo step, in modo che se mai
     * si decidesse per qualche motivo di runnare un NRT con una versione precedente, i test coinvolti
     * verrebbero skippati senza essere conteggiati come fail.
     */
    @Given("il test è effettuabile con API versione {string} o superiore")
    public void checkApiVersion(String version) {
        NotificationVersion notificationVersion = getNotificationVersion(version);
        assumeThat(versionUsed.getValue())
                .as("Test skipped: la versione" + versionUsed + " non supporta questo test pensato per la " + version + " o superiore")
                .isGreaterThanOrEqualTo(notificationVersion.getValue());
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
     * TODO -> test refattorizzato per poter essere eseguito con qualsiasi versione, ma comunque ampiamente migliorabile, magari anche riscrivendo gli step
     */
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
                notificationStepsInterface.sendNotification(getWorkFlowWait(), NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS);
                notificationStepsInterface.waitForTimelineElement(COMPLETELY_UNREACHABLE, 33);
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
            if (paName != null) {
                setPaAndSenderTaxId(paName);
            }
            /*TODO: un tempo lo stato era sempre ACCEPTED, ora che è parametrico, qualora vengano aggiunti nuovi status oltre ad
               ACCEPTED, REFUSED, CANCELLED (che usano tutti la pollingStrategy VALIDATION_STATUS) si dovrebbe valutare
               la creazione di un metodo privato che prenda uno status in input e restituisca la pollingStrategy corrispondente*/
            getNotificationStepInterface().sendNotification(getWorkFlowWait(), status, VALIDATION_STATUS);
        }
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
        getNotificationStepInterface().sendNotification(getWorkFlowWait(), NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS_NO_ACCEPTATION);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED e successivamente annullata")
    public void laNotificaVieneInviataOkAndCancelled(String paName) {
        setPaAndSenderTaxId(paName);
        getNotificationStepInterface().sendNotification(WAIT_EXTRA_RAPID, NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS);
        Assertions.assertDoesNotThrow(() -> {
            RequestStatus resp = Assertions.assertDoesNotThrow(() -> b2bClient.notificationCancellation(notificationIun));

            assertThat(resp).as("La response non dev'essere null").isNotNull();
            assertThat(resp.getDetails()).as("I details della response non devono essere null").isNotNull();
            assertThat(resp.getDetails()).as("I details della response non devono essere vuoti").isNotEmpty();
            assertThat("NOTIFICATION_CANCELLATION_ACCEPTED")
                    .as("Il codice della response non coincide con quanto atteso")
                    .isEqualToIgnoringCase(resp.getDetails().get(0).getCode());
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
        if (annullabile.equalsIgnoreCase("può")) {
            Assertions.assertDoesNotThrow(() -> {
                RequestStatus response = b2bClient.notificationCancellation(notificationIun);
                Assertions.assertNotNull(response);
                Assertions.assertNotNull(response.getDetails());
                Assertions.assertFalse(response.getDetails().isEmpty());
                Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(response.getDetails().get(0).getCode()));
            });
        } else {
            try {
                b2bClient.notificationCancellation(notificationIun);
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
        String requestId = Base64Utils.encodeToString(notificationIun.getBytes());
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
        getNotificationStepInterface().sendNotification(1000, NOTIFICATION_STATUS_CANCELLED, VALIDATION_STATUS);
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

    @When("la notifica viene inviata tramite api b2b senza preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataSenzaPreloadAllegato(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError(NOT_FOUND_NO_PRELOAD);
    }

    @When("la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataTramiteApiBBEffettuandoLaPreloadMaSenzaCaricareNessunAllegatoDalESiAttendeCheLoStatoDiventiREFUSED(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError(NOT_FOUND_ON_SAFE_STORAGE);
    }

    @When("la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato json dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataTramiteApiBBEffettuandoLaPreloadMaSenzaCaricareNessunAllegatoJsonDalESiAttendeCheLoStatoDiventiREFUSED(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError(NOT_FOUND_ALLEGATO_JSON);
    }

    @When("la notifica viene inviata tramite api b2b con sha256 differente dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConShaDifferente(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError(NOT_EQUAL_SHA);
    }

    @When("la notifica viene inviata tramite api b2b con sha256 Json differente dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConShaJsonDifferente(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError(NOT_EQUAL_SHA_JSON);
    }

    @When("la notifica viene inviata tramite api b2b con estensione errata dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConEstensioneErrata(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError(WRONG_EXTENSION);
    }

    //Non viene richiamato da nessuno step: rimuovere?
    @When("la notifica viene inviata tramite api b2b oversize preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoOverSize(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError(OVERSIZE_ALLEGATO);
    }

    //Non viene richiamato da nessuno step: rimuovere?
    @When("la notifica viene inviata tramite api b2b injection preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoInjection(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError(NOTIFICATION_INJECTION_ALLEGATO);
    }

    //Non viene richiamato da nessuno step: rimuovere?
    @When("la notifica viene inviata tramite api b2b over 15 preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoOver15(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedDueToError(OVER_15_ALLEGATO);
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
            getNotificationStepInterface().uploadNotification(null);
        } catch (HttpStatusCodeException | IOException e) {
            if (e instanceof HttpStatusCodeException httpError) {
                this.notificationError = httpError;
            }
        }
    }

    // Spostato da AvanzamentoNotificheB2bSteps, ha più senso qua
    //Annullamento Notifica
    @And("la notifica può essere annullata dal sistema tramite codice IUN")
    public void notificationCanBeCanceledWithIUN() {
        Assertions.assertNotNull(notificationIun);
        Assertions.assertDoesNotThrow(() -> {
            RequestStatus response = Assertions.assertDoesNotThrow(() -> b2bClient.notificationCancellation(notificationIun));
            Assertions.assertNotNull(response);
            Assertions.assertNotNull(response.getDetails());
            Assertions.assertTrue(response.getDetails().size() > 0);
            Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(response.getDetails().get(0).getCode()));
        });
    }

    @And("al destinatario viene associato lo iuv creato mediante partita debitoria per {string} alla posizione {int}")
    public void destinatarioAddIuvGPD(String denominazione, Integer posizioneDebitoria) {
        getNotificationStepInterface().addIuvGpdToDestinatario(denominazione, getIuvGPD(posizioneDebitoria), posizioneDebitoria);
    }

    @And("al destinatario viene associato lo iuv creato mediante partita debitoria per {string} per la posizione debitoria {int} del pagamento {int}")
    public void destinatarioAddIuvGpdPerUtente(String denominazione, Integer posizioneDebitoria, Integer paymentIndex) {
        getNotificationStepInterface().addIuvGpdToDestinatario(denominazione, getIuvGPD(posizioneDebitoria), paymentIndex);
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
            List<LegalCourtesyAddressWrapper> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
                this.iPnWebUserAttributesClient.deleteRecipientLegalAddress("default", LegalCourtesyAddressWrapper.ChannelType.PEC);
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
                    .filter(address -> LegalCourtesyAddressWrapper.ChannelType.PEC.getValue().equals(address.getChannelType().getValue()))
                    .findAny()
                    .orElseThrow(() -> AssertionFailureBuilder.assertionFailure().message("PEC NOT FOUND!").build());
        } catch (Exception exc) {
            log.error("Si è verificato un errore durante la verifica di pec inserite: {}", exc.getMessage());
            throw exc;
        }
    }

    @And("viene verificata la presenza di {int} recapit(o)(i) di cortesia inserit(o)(i) per l'utente {string}")
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

    @Then("si verifica la corretta acquisizione della notifica")
    public void correctAcquisitionNotification() {
        String version = versionUsed.toString();
        assertThatCode(() -> verifyNotification(version))
                .as("La verifica della versione della notifica non deve generare eccezioni per la versione " + version)
                .doesNotThrowAnyException();
    }

    @Then("si verifica lo scarto dell' acquisizione della notifica")
    public void correctAcquisitionNotificationError() {
        String version = versionUsed.toString();
        verifyNotification(version);
    }

    private void verifyNotification(String version) {
        NotificationVersion notificationVersion = getNotificationVersion(version);
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        try {
            notificationStepsInterface.verifyNotification(notificationIun);
        } catch (AssertionFailedError assertionFailedError) {
            log.info("Errore di acquisizione notifica");
        }
    }

    @Then("stampa log dello IUN della notifica {string} con allegato {string} su comune {string}")
    public void stampaLogDelloIUNDellaNotificaConAllegatoSuComune(String notificationType, String attachment, String municipality) {
        log.info("notifica STAMPA COLORI IUN: {}, notifica: {}, allegato: {}, comune: {}", notificationIun, notificationType, attachment, municipality);
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

    /* Sono stati unificati 8 vecchi metodi in questo (alcuni di questi non vengono nemmeno mai richiamati da nessun file feature).
      È stato refattorizzato tutto quanto, in modo che possa runnare con qualsiasi versione
      (dalla 21 in su, in quanto i metadati non sono presenti in versioni precedenti)
     */
    private void sendNotificationRefusedDueToError(String errorType) {
        assumeThat(versionUsed.getValue())
                .as("Test skipped: metodo pensato per funzionare con versioni dalla 21 in poi")
                .isGreaterThanOrEqualTo(NotificationVersion.V21.getValue());

        try {
            getNotificationStepInterface().createAndSendNotificationRequestWithError(errorType);
            threadWait(getWorkFlowWait());
            assertThat(errorCode).as("Il messaggio di errore non dev'essere null").isNotNull();
        } catch (AssertionFailedError assertionFailedError) {
            String message = decorateErrorMsg(assertionFailedError.getMessage());
            if (errorType.equalsIgnoreCase("OVER_15_ALLEGATO")) {
                assertSoftly(softly -> {
                    assertThat(message).as("Il codice di errore non coincide con quanto atteso").contains("400");
                    assertThat(message).as("Il messaggio di errore non coincide con quanto atteso").contains("Max attachment count reached");
                });
                errorCode = "INVALID_PARAMETER_MAX_ATTACHMENT";
            } else {
                throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
            }
        }
    }

    public String getCreditorTaxId(int recipientIndex) {
        return getNotificationStepInterface().getCreditorTaxId(recipientIndex);
    }

    public String getNoticeCode(int recipientIndex) {
        return getNotificationStepInterface().getNoticeCode(recipientIndex);
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
        HttpStatusCodeException value = this.notificationError;
        this.notificationError = null;

        if (value != null) {
            log.info("Consuming HttpStatusCodeException: Status={}, Message={}, ResponseBody={}",
                    value.getStatusCode(),
                    value.getMessage(),
                    value.getResponseBodyAsString()
            );
        }

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
            case LEONARDO_DA_VINCI -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
            }
            case GALILEO_GALILEI -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
            }
            case DINO_SAURO -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
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
            case MARIO_CREDENZIALI_SCADUTE -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
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
                "IUN: " + Optional.ofNullable(notificationIun).orElse("not found") + " }";
    }

    public void throwAssertionFailedErrorWithAmountGPDAndIUN(AssertionFailedError assertionFailedError, Integer amountGPD) {
        String message = assertionFailedError.getMessage() +
                "{IUN: " + notificationIun + ", amountGPD " + (amountGPD == null ? "NULL" : amountGPD.toString()) + "}";
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

    /**
     * Usati solo dalle classi che implementano B2bStepsInterface (prima in AvanzamentoNotificheB2bSteps)
     * TODO: si potrebbero estrapolare da SharedSteps e mettere in una classe di utility a parte
     */

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
        return B2bUtils.getTimelineEventId(event, timelineEventCategory);
    }

    private static EventId getEventId(String iun, DataTest dataFromTest) {
        TimelineElementV27 timelineElement = dataFromTest.getTimelineElement();
        TimelineElementDetailsV27 timelineElementDetails = timelineElement.getDetails();
        DigitalAddress digitalAddress = timelineElementDetails == null ? null : timelineElementDetails.getDigitalAddress();
        DigitalAddressSource digitalAddressSource = timelineElementDetails == null ? null : timelineElementDetails.getDigitalAddressSource();

        EventId event = new EventId();
        event.setIun(iun);
        event.setRecIndex(timelineElementDetails == null ? null : timelineElementDetails.getRecIndex());
        event.setCourtesyAddressType(digitalAddress == null ? null : digitalAddress.getType());
        event.setSource(digitalAddressSource == null ? null : digitalAddressSource.getValue());
        event.setIsFirstSendRetry(dataFromTest.isFirstSendRetry());
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
    public List<TimelineElementV27> getTimelineElementsByEventId(String timelineEventCategory, DataTest dataFromTest) {
        FullSentNotificationV27 fullSentNotification = getSentNotificationLastVersion();
        List<TimelineElementV27> timelineElementList = fullSentNotification.getTimeline();
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            // get timeline event id
            String timelineEventId = getTimelineEventId(timelineEventCategory, notificationIun, dataFromTest);
            if (timelineEventCategory.equals(SEND_ANALOG_PROGRESS)
                    || timelineEventCategory.equals(SEND_SIMPLE_REGISTERED_LETTER_PROGRESS)) {
                TimelineElementV27 timelineElementFromTest = dataFromTest.getTimelineElement();
                TimelineElementDetailsV27 timelineElementDetails = timelineElementFromTest.getDetails();
                return timelineElementList.stream().filter(elem ->
                                Objects.requireNonNull(elem.getElementId()).startsWith(timelineEventId)
                                        && Objects.equals(Objects.requireNonNull(elem.getDetails()).getDeliveryDetailCode(), Objects.requireNonNull(timelineElementDetails).getDeliveryDetailCode()))
                        .toList();
            }
            return timelineElementList.stream().filter(elem -> Objects.requireNonNull(elem.getElementId()).contains(timelineEventId)).toList();
        }
        return timelineElementList.stream().filter(elem -> Objects.requireNonNull(elem.getCategory()).getValue().equals(timelineEventCategory)).toList();
    }

    public TimelineElementV27 getTimelineElementByEventId(String timelineEventCategory, DataTest dataFromTest) {
        return getTimelineElementsByEventId(timelineEventCategory, dataFromTest).stream()
                .findAny()
                .orElse(null);
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
        return getNotificationStepInterface().getDatiPagamento(destinatario, pagamento);
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