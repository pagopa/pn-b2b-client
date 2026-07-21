package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffRequestNewApiKey;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffResponseNewApiKey;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffLegalNotificationSearchRow;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffLegalNotificationsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.digitaladdresses.BffUserAddress;
import it.pagopa.pn.client.b2b.pa.cache.CacheManager;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.config.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.domain.Costanti;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.DigitalAddress;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.DigitalAddressSource;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.RequestStatus;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementDetailsV28;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.CourtesyDigitalAddress;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.provider.DestinatarioRegistry;
import it.pagopa.pn.client.b2b.pa.provider.SenderInfoProvider;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebUserAttributesClient;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BUserAttributesExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.IPnTosPrivacyClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnGPDClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaymentInfoClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnServiceDeskClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebUserAttributesInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.b2b.pa.wrapper.LegalCourtesyAddressWrapper;
import it.pagopa.pn.client.b2b.pa.wrapper.RecipientWrapper;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationStepsInterface;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.utils.DataTest;
import it.pagopa.pn.cucumber.utils.EventId;
import it.pagopa.pn.cucumber.utils.GroupPosition;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.AssertionFailureBuilder;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.ADDRESS;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.ALDA_MERINI;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.ALLEGATO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_1;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_2;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_MULTI;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_ROOT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_SON;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.CRISTOFORO_COLOMBO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.CUCUMBER_SPA;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.CUCUMBER_SPA_B2B;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.DINO_SAURO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.DURATION_ANALOG_REFINEMENT_DEFAULT_FAILURE;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.DURATION_ANALOG_REFINEMENT_DEFAULT_SUCCESS;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.DURATION_DIGITAL_REFINEMENT_DEFAULT_FAILURE;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.DURATION_DIGITAL_REFINEMENT_DEFAULT_SUCCESS;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.DURATION_SECOND_NOTIFICATION_WORKFLOW_WAITING_TIME_DEFAULT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.DURATION_TIME_TO_ADD_IN_NON_VISIBILITY_TIME_CASE_DEFAULT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.DURATION_WAIT_READ_COURTESY_MESSAGE_DEFAULT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.ETTORE_FIERAMOSCA;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.EXTENSION;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.FILE_NOTFOUND;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.FILE_PDF_INVALID_ERROR;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.FILE_SHA_ERROR;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.GALILEO_GALILEI;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.GHERKIN_SRL;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.GHERKIN_SRL_B2B;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.INVALID_PARAMETER_MAX_ATTACHMENT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.LEONARDO_DA_VINCI;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.LUCIO_ANNEO_SENECA;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_CREDENZIALI_SCADUTE;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_CUCUMBER;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_GHERKIN;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MOST_RECENT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOTIFICATION_INJECTION_ALLEGATO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOTIFICATION_STATUS_ACCEPTED;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOTIFICATION_STATUS_CANCELLED;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOT_EQUAL_SHA;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOT_EQUAL_SHA_JSON;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOT_FOUND_ALLEGATO_JSON;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOT_FOUND_NO_PRELOAD;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOT_FOUND_ON_SAFE_STORAGE;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOT_VALID_ADDRESS;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.OVERSIZE_ALLEGATO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.OVER_15_ALLEGATO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.SCHEDULING_DELTA_DEFAULT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.SEND_ANALOG_PROGRESS;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.SHA_256;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.TAXID_NOT_VALID;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.VALIDATION_STATUS;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.VALIDATION_STATUS_ACCEPTATION_SHORT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.VALIDATION_STATUS_NO_ACCEPTATION;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.WAITING_GPD;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.WAIT_DEFAULT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.WAIT_EXTRA_RAPID;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.WAIT_UPPER_BOUND;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.WORKFLOW_WAIT_DEFAULT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.WORKFLOW_WAIT_UPPER_BOUND;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.WRONG_EXTENSION;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class SharedSteps {

    private final SenderInfoProvider senderInfoProvider;

    @Getter
    private final DestinatarioRegistry destinatarioRegistry;

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

    @Getter
    private final ObjectMapper objMapper;

    private boolean checkAuditLogDisabled;

    private final DynamoDbService dynamoDbService;

    private final CacheManager<String, String> senderTaxIdCacheManager;

    private final SendSharedContext sendSharedContext;

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

    /**
     * Lo IUN delle notifiche che vengono create (nei rari casi di più notifiche create simultaneamente) viene salvato in questa variabile.
     * Tramite essi è poi possibile recuperare le FullSentNotification (di qualsivoglia versione) richiamando il B2B.
     */
    @Setter
    @Getter
    private List<String> notificationIunList = new ArrayList<>();

    @Getter
    @Setter
    private List<Destinatario> destinatariList = new ArrayList<>();

    /**
     * L'id dell'ultima delega (mandate) selezionata per la ricerca delle notifiche ricevute da un delegato.
     * Viene valorizzato dagli step che gestiscono le deleghe (es. {@code RicezioneNotificheWebDelegheSteps})
     * e serve a risolvere, tramite {@link it.pagopa.pn.cucumber.utils.token.TokenResolver}, i placeholder
     * usati nei feature file per riferirsi a un valore generato dinamicamente (es. {@code :mandateId}).
     */
    @Getter
    @Setter
    private String mandateId;

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
                       PnWebUserAttributesInternalClientImpl iPnWebUserAttributesClient,
                       PnServiceDeskClientImpl serviceDeskClient,
                       PnGPDClientImpl pnGPDClientImpl,
                       PnPaymentInfoClientImpl pnPaymentInfoClientImpl,
                       IPnTosPrivacyClientImpl iPnTosPrivacyClientImpl,
                       PnB2bClientTimingConfigs timingConfigs,
                       DynamoDbService dynamoDbService,
                       SenderInfoProvider senderInfoProvider,
                       @Qualifier("senderTaxIdCacheManager") CacheManager<String, String> senderTaxIdCacheManager,
                       DestinatarioRegistry destinatarioRegistry,
                       SendSharedContext sendSharedContext
    ) {
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
        this.dynamoDbService = dynamoDbService;
        this.senderInfoProvider = senderInfoProvider;
        versionUsed = getNotificationVersion(MOST_RECENT);
        this.senderTaxIdCacheManager = senderTaxIdCacheManager;
        this.destinatarioRegistry = destinatarioRegistry;
        this.sendSharedContext = sendSharedContext;
    }

    @BeforeAll
    public static void before_all() {
        log.debug("SHARED_GLUE START");
        //only for class activation
    }

    @After
    public void afterScenario() {
        MDC.clear();
    }

    @Before
    public void injectScenarioNameInsideSfl4jMdc(Scenario scenario) {
        String scenarioName = scenario.getName();
        MDC.put(RestTemplateConfiguration.CUCUMBER_SCENARIO_NAME_MDC_ENTRY, scenarioName);
        log.info("START SCENARIO: {}", scenarioName);
    }

    @Before("@integrationTest")
    public void doSomethingAfter() {
        this.groupToSet = false;
    }

    /**
     * Restituisce lo FullSentNotification aggiornata all'ultima versione (quella maggiormente utilizzata a codice)
     */
    //TODO: all'introduzione di una nuova versione, ri-fattorizzare il tipo di oggetto ritornato e cambiare i punti di codice che richiamano questo metodo
    public FullSentNotificationV29 getSentNotificationLastVersion() {
        return b2bClient.getSentNotificationV29(notificationIun);
    }

    /**
     * Restituisce lo FullSentNotification aggiornata all'ultima versione (quella maggiormente utilizzata a codice)
     * ma a differenza del metodo sopra anziché usare il notificationIun di SharedSteps usa uno IUN arbitrario.
     * Usato in un solo punto del codice
     */
    //TODO: all'introduzione di una nuova versione, ri-fattorizzare il tipo di oggetto ritornato e cambiare i punti di codice che richiamano questo metodo
    public FullSentNotificationV29 getSentNotificationLastVersionByIun(String iun) {
        return b2bClient.getSentNotificationV29(iun);
    }

    public NotificationVersion getNotificationVersion(String version) {
        if (version.trim().equalsIgnoreCase(MOST_RECENT)) {
            return NotificationVersion.V26;//TODO: modificare questo valore ogni volta che viene aggiunta una versione più recente
        }
        return NotificationVersion.valueOf(version.trim().toUpperCase());
    }

    public NotificationStepsInterface getNotificationStepInterface() {
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
        getNotificationStepInterface().addRecipientToNotification(destinatarioRegistry.DESTINATARIO_MARIO_CUCUMBER, new HashMap<>());
    }

    /**
     * Metodo a soli fine di debugging, da non essere utilizzato in nessuno scenario.
     * Se si hanno già pronte più notifiche, anziché crearla da zero, aspettare che arrivino in ACCEPTED, etc
     * si impostano gli IUN qua e la PA e si può procedere con il resto dei metodi.
     */
    @Given("imposto la pa a {string} e gli iun di SharedSteps")
    public void setMultipleIunAndPAForTestPurposes(String paName, Map<String, String> iunMap) {
        setPA(paName);
        notificationIunList = iunMap.values().stream().toList();
        /*Imposta la data di creazione a cinque giorni fa (sufficienti per testare) e crea una notification request con un destinatario
        e crea una request con destinatario Mario Cucumber (questi passaggi servono per poter recuperare anche le notifiche andate in REFUSED) */
        notificationCreationDate = OffsetDateTime.now(ZoneOffset.UTC).minusDays(5);
        getNotificationStepInterface().prepareNotificationRequest(Map.of(
                "subject", "MOCKED NOTIFICATION",
                "senderDenomination", "Comune di Palermo"));
        getNotificationStepInterface().addRecipientToNotification(destinatarioRegistry.DESTINATARIO_MARIO_CUCUMBER, new HashMap<>());
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
        sendSharedContext.getLegalNotificationContext().getRecipient().setDestinatario(destinatario);
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

    @And("vengono create {int} notifiche con destinatario {destinatario} per la pa {string} e si aspetta che raggiungano l'elemento di timeline della notifica {string}")
    public void creaNotifiche(int notificationNumber, Destinatario destinatario, String pa, String timelineEvent, Map<String, String> data) throws IOException, InterruptedException {
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface();
        for (int i = 0; i < notificationNumber; i++) {
            prepareNotificationRequestWithVersion(MOST_RECENT, data);
            setPaAndSenderTaxId(pa);
            getNotificationStepInterface().addRecipientToNotification(destinatario, data);
            getNotificationStepInterface().uploadNotification(null);
            String iun = getNotificationIun();
            notificationIunList.add(iun);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.MINUTES.sleep(5);
        List<String> remainingIuns = new ArrayList<>(notificationIunList); // lista di quelli da processare
        List<String> failedIuns = new ArrayList<>();

        while (!remainingIuns.isEmpty()) {
            failedIuns.clear();
            for (String iun : remainingIuns) {
                setNotificationIun(iun);
                try {
                    notificationStepsInterface.waitForTimelineElement(timelineEvent, 30);
                } catch (HttpClientErrorException.NotFound e) {
                    failedIuns.add(iun);
                }
            }
            // prepariamo la prossima iterazione solo con quelli che hanno fallito
            remainingIuns = new ArrayList<>(failedIuns);
        }
    }

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e codice avviso {isTheSame}")
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

    @When("la notifica viene inviata tramite api b2b dal {string} senza aspettare che diventi accepted")
    public void sendNotificationWithoutWaitingForAccepted(String paName) throws IOException {
        setPaAndSenderTaxId(paName);
        getNotificationStepInterface().uploadNotification(null);
        assertThat(notificationIun).as("Lo IUN della notifica inviata non dev'essere null").isNotNull();
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

    @Then("la timeline {contains} elementi con la stringa {string}")
    public void verifyPresenceOfTimelineElementsWithString(boolean contains, String searchString) {
        FullSentNotificationV29 fullSentNotification = getSentNotificationLastVersion();
        List<TimelineElementV28> timeline = fullSentNotification.getTimeline();
        List<TimelineElementV28> matchingElements = timeline.stream().filter(e -> e.getElementId() != null && e.getElementId().contains(searchString)).toList();

        if (!matchingElements.isEmpty()) {
            log.warn("Elementi di timeline contenenti '{}':", searchString);
            matchingElements.forEach(e -> log.warn(" - elementId: {}, timestamp: {}", e.getElementId(), e.getTimestamp()));
        } else {
            log.info("Nessun elemento di timeline contiene la stringa '{}'", searchString);
        }
        int matchingElementsSize = matchingElements.size();
        if (contains) {
            assertThat(matchingElementsSize).as("Attesa la presenza di elementi contenenti '%s' ma non ne sono stati trovati", searchString).isGreaterThan(0);
        } else {
            assertThat(matchingElementsSize).as("Non era attesa la presenza di elementi contenenti '%s' ma ne sono stati trovati %s", searchString, matchingElementsSize).isEqualTo(0);
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

    @When("la notifica viene inviata dal {string} con errore {string}")
    public void laNotificaVieneInviataDallaPAWithError(String paName, String errorType) {
        setPaAndSenderTaxId(paName);
        try {
            getNotificationStepInterface().uploadNotification(errorType);
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

    @And("al destinatario {int} viene associato lo iuv creato mediante partita debitoria alla posizione {int} per il suo pagamento alla posizione {int}")
    public void destinatarioAddIuvGPD(Integer recIndex, Integer posizioneDebitoria, Integer recipientPaymentIndex) {
        getNotificationStepInterface().addIuvGpdToDestinatario(recIndex, getIuvGPD(posizioneDebitoria), recipientPaymentIndex);
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
        RecipientWrapper addressesByRecipient = this.iPnWebUserAttributesClient.getAddressesByRecipient();

        if (webRecipientClient instanceof B2BRecipientExternalClientImpl) {
            Assertions.assertTrue(
                    (addressesByRecipient.getB2bUserAddress() != null &&
                            addressesByRecipient.getB2bUserAddress().getCourtesy() != null &&
                            !addressesByRecipient.getB2bUserAddress().getCourtesy().isEmpty())
                            ||
                            (addressesByRecipient.getB2bUserAddress() != null &&
                                    addressesByRecipient.getB2bUserAddress().getLegal() != null &&
                                    !addressesByRecipient.getB2bUserAddress().getLegal().isEmpty()),
                    "Non è presente alcun recapito LEGAL o COURTESY da b2bUserAddress per l'utente " + user
            );
        } else {
            Assertions.assertTrue(
                    addressesByRecipient.getBffUserAddress() != null &&
                            addressesByRecipient.getBffUserAddress()
                                    .stream()
                                    .map(BffUserAddress::getAddressType)
                                    .collect(Collectors.toSet())
                                    .containsAll(Arrays.asList("LEGAL", "COURTESY")),
                    "Non è presente alcun recapito LEGAL o COURTESY da BFF per l'utente " + user
            );
        }
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
        String senderTaxId = notificationStepsInterface.getSenderTaxId();

        // Recupera il senderTaxId da Dynamo solo se non passato da scenario
        if (senderTaxId == null) {
            // Recupera da cache o computa
            senderTaxId = senderTaxIdCacheManager.getOrCompute(
                    pa,  // Chiave: il nome della PA
                    () -> fetchSenderTaxIdFromDynamo(pa)  // Supplier che ritorna String
            );

            Assertions.assertNotNull(senderTaxId, "La sender tax id non è presente nel DB DynamoDb ONBOARD_INSTITUTIONS");
            notificationStepsInterface.setSenderTaxId(senderTaxId);
        }
        SettableApiKey.ApiKeyType apiKeyType = mapPaToApiKeyType(pa);
        setGroup(apiKeyType);
    }

    private String fetchSenderTaxIdFromDynamo(String pa) {
        String senderId = senderInfoProvider.getSenderId(pa);
        log.debug("Fetching sender tax ID for PA: {} with senderId: {}", pa, senderId);
        QueryResponse response = dynamoDbService.call(DynamoTableName.ONBOARD_INSTITUTIONS, Map.of(
                ":v_id", AttributeValue.builder().s(senderId).build()
        ));
        return response.items().stream()
                .findFirst()
                .flatMap(item -> Optional.ofNullable(item.get("taxCode"))
                        .map(AttributeValue::s))
                .orElse(null);
    }

    private SettableApiKey.ApiKeyType mapPaToApiKeyType(String pa) {
        return senderInfoProvider.getApiKeyType(pa);
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
            case CUCUMBER_SPA_B2B -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_B2B_2);
            }
            case GHERKIN_SRL_B2B -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_B2B_1);
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

    public String getGroupIdByPa(String paName, GroupPosition position, String status) {
        List<HashMap<String, String>> hashMapsList = getGroupsByPa(paName);
        String id = null;
        int count = 0;
        for (HashMap<String, String> elem : hashMapsList) {
            if (elem.get("status").equalsIgnoreCase(status)) {
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
        TimelineElementV28 timelineElement = dataFromTest.getTimelineElement();
        TimelineElementDetailsV28 timelineElementDetails = timelineElement.getDetails();
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
    public List<TimelineElementV28> getTimelineElementsByEventId(String timelineEventCategory, DataTest dataFromTest) {
        FullSentNotificationV29 fullSentNotification = getSentNotificationLastVersion();
        List<TimelineElementV28> timelineElementList = fullSentNotification.getTimeline();
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            // get timeline event id
            String timelineEventId = getTimelineEventId(timelineEventCategory, notificationIun, dataFromTest);
            if (timelineEventCategory.equals(SEND_ANALOG_PROGRESS)
                    || timelineEventCategory.equals(SEND_SIMPLE_REGISTERED_LETTER_PROGRESS)) {
                TimelineElementV28 timelineElementFromTest = dataFromTest.getTimelineElement();
                TimelineElementDetailsV28 timelineElementDetails = timelineElementFromTest.getDetails();
                return timelineElementList.stream().filter(elem ->
                                Objects.requireNonNull(elem.getElementId()).startsWith(timelineEventId)
                                        && Objects.equals(Objects.requireNonNull(elem.getDetails()).getDeliveryDetailCode(), Objects.requireNonNull(timelineElementDetails).getDeliveryDetailCode()))
                        .toList();
            }
            return timelineElementList.stream().filter(elem -> Objects.requireNonNull(elem.getElementId()).contains(timelineEventId)).toList();
        }
        return timelineElementList.stream().filter(elem -> Objects.requireNonNull(elem.getCategory()).getValue().equals(timelineEventCategory)).toList();
    }

    public TimelineElementV28 getTimelineElementByEventId(String timelineEventCategory, DataTest dataFromTest) {
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
            TimeUnit.MILLISECONDS.sleep(wait);
        } catch (RuntimeException exception) {
            log.error("Await error exception");
            throw exception;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String vieneRichiestoIlCodiceQRPerLoIUN(String iun, Integer destinatario) {
        HashMap<String, String> quickAccessLink = pnExternalServiceClient.getQuickAccessLink(iun);
        log.debug("quickAccessLink: {}", quickAccessLink.toString());
        String qrCode = quickAccessLink.get(quickAccessLink.keySet().toArray()[destinatario]);
        log.debug("qrCode: {}", qrCode);
        return qrCode;
    }

    @Given("vengono inviate {int} nuove notifiche tramite api b2b dal {string} con destinatario {destinatario} e si aspetta che raggiungano l'elemento di timeline {string}")
    public void sendMultipleNotifications(int notificationNumber, String paName, Destinatario destinatario, String timelineElement, Map<String, String> data) throws IOException, InterruptedException {
        if (notificationIunList.isEmpty()) {
            for (int i = 0; i < notificationNumber; i++) {
                prepareNotificationRequestWithVersion(MOST_RECENT, data);
                setPaAndSenderTaxId(paName);
                getNotificationStepInterface().addRecipientToNotification(destinatario, data);
                getNotificationStepInterface().uploadNotification(null);
                assertThat(notificationIun).as("Lo IUN della notifica inviata non dev'essere null").isNotNull();
                notificationIunList.add(notificationIun);
                threadWait(getWorkFlowWait());
                log.info("Notifica {} inviata", i + 1);
            }
        }
        log.info("Elenco IUN delle {} notifiche caricate: {}", notificationNumber, notificationIunList);
        TimeUnit.MINUTES.sleep(5);

        List<String> remainingIuns = new ArrayList<>(notificationIunList); // lista di quelli da processare
        List<String> failedIuns = new ArrayList<>();

        while (!remainingIuns.isEmpty()) {
            failedIuns.clear();
            for (String iun : remainingIuns) {
                setNotificationIun(iun);
                try {
                    getNotificationStepInterface().waitForTimelineElement(timelineElement, 33);
                } catch (HttpClientErrorException.NotFound e) {
                    failedIuns.add(iun);
                }
            }

            // prepariamo la prossima iterazione solo con quelli che hanno fallito
            remainingIuns = new ArrayList<>(failedIuns);

            // opzionale: piccolo ritardo tra i tentativi per non saturare il sistema
            if (!remainingIuns.isEmpty()) {
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * Step precedentemente adibito unicamente al recupero di una notifica vecchia 120 giorni, ora il range temporale è impostabile a piacimento
     */
    @And("{string} recupera lato web PA una notifica inviata tra {int} e {int} giorni fa con destinatario {destinatario}")
    public void retrieveNotification120DaysOldByIunWebPaSide(String paName, int limitA, int limitB, Destinatario recipient) {
        long upperLimit = Math.max(limitA, limitB);
        long lowerLimit = Math.min(limitB, limitA);
        setPA(paName);
        String recipientTaxId = recipient.getTaxId();
        OffsetDateTime todayDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
        BffLegalNotificationsResponse bffNotificationsResponse = webPaClient.searchSentNotification(
                todayDate.minusDays(upperLimit),
                todayDate.minusDays(lowerLimit),
                recipientTaxId, null, null, null, 50, null);
        AssertionsForClassTypes.assertThat(bffNotificationsResponse).as("La bffNotificationResponse non dev'essere null").isNotNull();
        AssertionsForInterfaceTypes.assertThat(bffNotificationsResponse.getResultsPage()).as("La lista di notifiche vecchie " + lowerLimit + " giorni non dev'essere null").isNotNull();
        AssertionsForInterfaceTypes.assertThat(bffNotificationsResponse.getResultsPage()).as("La lista di notifiche vecchie " + lowerLimit + " giorni non dev'essere vuota").isNotEmpty();
        BffLegalNotificationSearchRow result = bffNotificationsResponse.getResultsPage().stream().filter(
                        n -> n.getRecipients().size() == 1 && n.getRecipients().contains(recipientTaxId))
                .findFirst().orElse(null);
        AssertionsForClassTypes.assertThat(result).as("Nessuna notifica trovato con il solo destinatario " + recipientTaxId).isNotNull();
        FullSentNotificationV29 oldNotification = getSentNotificationLastVersionByIun(result.getIun());
        notificationIun = oldNotification.getIun();
        notificationIunList.add(oldNotification.getIun());
        log.info("RECIPIENTS OLDER {} GG: {}", lowerLimit, oldNotification.getRecipients().stream().map(r -> r.getTaxId()).toList());
        log.info("IUN OLDER {} GG: {}", lowerLimit, oldNotification.getIun());
    }

    @And("al destinatario {int} viene settato l'applyCost del pagamento PagoPa alla posizione {int} a false")
    public void setApplyCostFalse(int recIndex, int paymentIndex) {
        getNotificationStepInterface().setApplyCostFalse(recIndex, paymentIndex);
    }
}