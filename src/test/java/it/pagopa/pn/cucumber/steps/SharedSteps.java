package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.Transpose;
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
import it.pagopa.pn.cucumber.utils.*;
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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static it.pagopa.pn.cucumber.steps.SharedSteps.NotificationVersion.V1;
import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Costanti.*;
import static it.pagopa.pn.cucumber.utils.FiscalCodeGenerator.generateCF;
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
    @Setter
    private IPnWebRecipientClient webRecipientClient;

    @Getter
    private final PnExternalServiceClientImpl pnExternalServiceClient;

    @Getter
    private final PnServiceDeskClientImpl serviceDeskClient;

    @Setter
    @Getter
    private HttpStatusCodeException notificationError;

    @Getter
    private OffsetDateTime notificationCreationDate;

    @Getter
    private SettableApiKey.ApiKeyType apiKeyTypeSetted = SettableApiKey.ApiKeyType.MVP_1;

    @Getter
    private final PnPollingFactory pollingFactory;

    @Getter
    @Setter
    private RequestNewApiKey requestNewApiKey;

    @Getter
    @Setter
    private ResponseNewApiKey responseNewApiKey;

    @Getter
    @Setter
    @Value("${pn.external.bearer-token-pg1.id}")
    private String idOrganizationGherkinSrl;

    @Getter
    @Setter
    @Value("${pn.external.bearer-token-pg2.id}")
    private String idOrganizationCucumberSpa;

    @Value("${pn.interop.base-url}")
    private String interopBaseUrl;

    @Value("${pn.interop.token-oauth2.path}")
    private String tokenOauth2Path;

    @Value("${pn.interop.token-oauth2.client-assertion}")
    private String clientAssertion;

    @Value("${pn.external.utilized.pec:testpagopa3@pec.pagopa.it}")
    private String digitalAddress;

    @Value("${pn.external.api-key-taxID}")
    private String senderTaxId;

    @Value("${pn.external.api-key-2-taxID}")
    private String senderTaxIdTwo;

    @Value("${pn.external.api-key-GA-taxID}")
    private String senderTaxIdGa;

    @Value("${pn.external.api-key-SON-taxID}")
    private String senderTaxIdSON;

    @Value("${pn.external.api-key-ROOT-taxID}")
    private String senderTaxIdROOT;

    @Value("${pn.bearer-token.user1.taxID}")
    private String marioCucumberTaxID;

    @Value("${pn.bearer-token.user2.taxID}")
    private String marioGherkinTaxID;

    @Getter
    private final DataTableTypeUtil dataTableTypeUtil;

    @Getter
    private final HashMap<String, String> mapAllegatiNotificaSha256 = new HashMap<>();
    private IPnWebUserAttributesClient iPnWebUserAttributesClient;

    @Getter
    @Setter
    private String errorCode;
    private boolean groupToSet = true;
    private final ApplicationContext context;
    private final List<String> iuvGPD;
    private final SecureRandom secureRandom;
    private final PnB2bClientTimingConfigs timingConfigs;
    private final ObjectMapper objMapper;

    @Getter
    @Setter
    //Viene settato solo per l'ultima versione. Al rilascio di una nuova, sostituire con l'oggetto corrispondente
    private NewNotificationResponse newNotificationResponse;

    @Getter
    @Setter
    // Viene settato solo per l'ultima versione. Al rilascio di una nuova, sostituire con l'oggetto corrispondente
    private NewNotificationRequestV24 notificationRequest;

    @Getter
    @Setter
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification fullSentNotificationV1;

    @Getter
    @Setter
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 fullSentNotificationV20;

    @Getter
    @Setter
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.FullSentNotificationV21 fullSentNotificationV21;

    @Getter
    @Setter
    private FullSentNotificationV23 fullSentNotificationV23;

    @Getter
    @Setter
    private FullSentNotificationV24 fullSentNotificationV24;

    @Getter
    @Setter
    private FullSentNotificationV25 fullSentNotificationV25;

    @Getter
    @Setter
    private FullSentNotificationV26 fullSentNotificationV26;

    private final NotificationStepsV1 notificationStepsV1 = new NotificationStepsV1(this);
    private final NotificationStepsV2 notificationStepsV2 = new NotificationStepsV2(this);
    private final NotificationStepsV21 notificationStepsV21 = new NotificationStepsV21(this);
    private final NotificationStepsV23 notificationStepsV23 = new NotificationStepsV23(this);
    private final NotificationStepsV24 notificationStepsV24 = new NotificationStepsV24(this);

    @Getter
    @Setter
    private NotificationVersion versionUsed;

    public enum NotificationVersion {
        V1(1), V2(2), V21(21), V23(23), V24(24);

        /**
         * Scopo di questo campo è quello di poter comparare le versioni con < o >
         * In questo modo si possono aggiungere controlli nel codice per verificare
         * se un dato Notification Version è antecedente o successivo a un'altra versione
         */
        @Getter
        private final int value;

        NotificationVersion(int value) {
            this.value = value;
        }
    }

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
     * Restituisce lo IUN della notifica, a prescindere dalla versione con cui è stata creata
     */
    public String getIunVersionamento() {
        return getNotificationStepInterface().getNotificationSentIun();
    }

    /**
     * L'idea alla base di metodo sarebbe di far restituire l'oggetto FullSentNotification a prescindere dalla versione
     * con cui è stato creato. In tal modo si potrebbe alleggerire la classe di tutti gli N campi fullSentNotificationV xyz
     * Gli attuali metodi che adesso richiamano il getFullSentNotificationV xyz a priori dovrebbero eseguire il casting, ma sarebbe
     * un casting safe, in quanto sanno già di operare con la versione xyz
     */
    public Object getSentNotificationAnyVersion() {
        return getNotificationStepInterface().getSentNotificationAnyVersion();
    }


    private NotificationVersion getNotificationVersion(String version) {
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
        switch (notificationVersion) {
            case V1 -> {
                return notificationStepsV1;
            }
            case V2 -> {
                return notificationStepsV2;
            }
            case V21 -> {
                return notificationStepsV21;
            }
            case V23 -> {
                return notificationStepsV23;
            }
            case V24 -> {
                return notificationStepsV24;
            }
            default -> throw new IllegalArgumentException("Version not supported!: " + notificationVersion);
        }
    }

    @Given("viene generata una nuova notifica")
    public void prepareNotificationRequest(Map<String, String> data) {
        prepareNotificationRequestWithVersion(MOST_RECENT, data);
    }

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

    @And("destinatario {string}")
    public void addDestinatario(String destinatario) {
        getNotificationStepInterface().addRecipientToNotification(destinatario, new HashMap<>());
    }

    @And("destinatario {string} e:")
    public void addDestinatarioWithParams(String destinatario, Map<String, String> data) {
        getNotificationStepInterface().addRecipientToNotification(destinatario, data);
    }

    @And("al destinatario viene associato lo iuv creato mediante partita debitoria alla posizione {int}")
    public void destinatarioAddIuvGPD(Integer posizione) {
        String iuvGPD = getIuvGPD(posizione);
        getNotificationStepInterface().setIuvToRecipient(posizione, iuvGPD);
    }

    /*
    Invio massivo di notifiche irreperibili utili per i test radd
    TODO: migliorare e rendere di utilità generale
    //TODO MATTEO -> questo metodo va assolutamente rifattorizzato, magari anche riscrivendo gli step
     */
    @Given("vengono inviate {int} notifiche per l'utente Signor Casuale con il {string} e si aspetta fino allo stato COMPLETELY_UNREACHABLE")
    public void sendNotificationForUserSignorCasualeAndWaitUntilCompletelyUnreachable(int numberOfNotification, String pa) {
        List<NewNotificationRequestV24> notificationRequests = new LinkedList<>();
        String generatedFiscalCode = generateCF(System.nanoTime());
        for (int i = 0; i < numberOfNotification; i++) {
            NewNotificationRequestV24 newNotificationRequest = dataTableTypeUtil.convertNotificationRequestV24(new HashMap<>())
                    .subject("notifica analogica con cucumber")
                    .senderDenomination("Comune di palermo")
                    .physicalCommunicationType(NewNotificationRequestV24.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER);

            HashMap<String, String> notificationRecipientMap = new HashMap<>();
            notificationRecipientMap.put("digitalDomicile", "NULL");
            notificationRecipientMap.put("physicalAddress_address", "Via NationalRegistries @fail-Irreperibile_AR");
            NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(notificationRecipientMap);
            addRecipientToNotification(newNotificationRequest,
                    updateNotificationRecipient(notificationRecipientV23,
                            SIGNOR_CASUALE,
                            generatedFiscalCode,
                            NotificationRecipientV23.RecipientTypeEnum.PF,
                            null
                    ),
                    notificationRecipientMap);


            this.notificationRequest = newNotificationRequest;
            setPaAndSenderTaxId(pa);
            notificationRequests.add(newNotificationRequest);
        }
        List<Thread> threadList = new LinkedList<>();
        ConcurrentLinkedQueue<FullSentNotificationV26> sentNotifications = new ConcurrentLinkedQueue<>();

        for (NewNotificationRequestV24 notification : notificationRequests) {
            Thread t = new Thread(() -> {
                //INVIO NOTIFICA ED ATTESA ACCEPTED
                NewNotificationResponse internalNotificationResponse = Assertions.assertDoesNotThrow(() ->
                        b2bUtils.uploadNotificationV24(notification));
                threadWait(getWait());
                FullSentNotificationV26 fsn = b2bUtils.waitForRequestAcceptationV26(internalNotificationResponse);
                Assertions.assertNotNull(fsn);

                //ATTESA ELEMENTO DI TIMELINE
                TimelineElementV26 timelineElement = null;
                for (int i = 0; i < 33; i++) {
                    threadWait(getWorkFlowWait());
                    fsn = b2bClient.getSentNotification(fsn.getIun());
                    log.info("NOTIFICATION_TIMELINE: " + fsn.getTimeline());
                    timelineElement = fsn.getTimeline().stream().filter(
                            elem -> Objects.requireNonNull(elem.getCategory().getValue())
                                    .equals(TimelineElementCategoryV23.COMPLETELY_UNREACHABLE.getValue())).findAny().orElse(null);
                    if (timelineElement != null) {
                        break;
                    }
                }
                Assertions.assertNotNull(timelineElement);
                sentNotifications.add(fsn);
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
        Assertions.assertEquals(sentNotifications.size(), numberOfNotification);
        log.debug("NOTIFICATION LIST: {}", sentNotifications);
        log.debug("IUN: ");
        for (FullSentNotificationV26 fullSentNotification : sentNotifications) {
            log.info(fullSentNotification.getIun());
        }
        log.debug("End IUN list");
        //la prima notifica viene inserita
        this.fullSentNotificationV26 = sentNotifications.poll();
        log.debug("notificationResponseComplete: {}", this.fullSentNotificationV26);
    }

    private void addRecipientToNotification(NewNotificationRequestV24 notificationRequest, NotificationRecipientV23 notificationRecipient, Map<String, String> recipientData) {
        if (notificationRequest.getNotificationFeePolicy() == NotificationFeePolicy.DELIVERY_MODE
                && NotificationValue.getValue(recipientData, PAYMENT.key) != null) {
            String pagopaFormValue = getValue(recipientData, PAYMENT_PAGOPA_FORM.key);
            if (pagopaFormValue != null && !pagopaFormValue.equalsIgnoreCase("NO")) {
                for (NotificationPaymentItem payments : Objects.requireNonNull(notificationRecipient.getPayments())) {
                    Objects.requireNonNull(payments.getPagoPa()).setApplyCost(true);
                }
            }
        }
        notificationRequest.addRecipientsItem(notificationRecipient);
    }

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEDiversoCodiceAvviso() {
        String creditorTaxId = Objects.requireNonNull(Objects.requireNonNull(notificationRequest.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getCreditorTaxId();
        generateNewNotification();
        Objects.requireNonNull(Objects.requireNonNull(this.notificationRequest.getRecipients().get(0).getPayments()).get(0).getPagoPa()).setCreditorTaxId(creditorTaxId);
    }

    @And("destinatario {string} con uguale codice avviso del destinario numero {int}")
    public void destinatarioConUgualeCodiceAvvisoDelDestinarioN(String recipientName, int recipientNumber, @Transpose NotificationRecipientV23 recipient) {
        Assertions.assertDoesNotThrow(() -> Objects.requireNonNull(notificationRequest.getRecipients().get(recipientNumber - 1).getPayments()).get(0));
        String noticeCode = Objects.requireNonNull(Objects.requireNonNull(notificationRequest.getRecipients().get(recipientNumber - 1).getPayments()).get(0).getPagoPa()).getNoticeCode();
        if (recipientName.trim().equalsIgnoreCase(MARIO_CUCUMBER)) {
            updateNotificationRecipient(recipient, MARIO_CUCUMBER, marioCucumberTaxID, null, null);
        } else if (recipientName.trim().equalsIgnoreCase(MARIO_GHERKIN)) {
            updateNotificationRecipient(recipient, MARIO_GHERKIN, marioGherkinTaxID, null, null);
        } else {
            throw new IllegalArgumentException();
        }
        Objects.requireNonNull(Objects.requireNonNull(recipient.getPayments()).get(0).getPagoPa()).setNoticeCode(noticeCode);
        this.notificationRequest.addRecipientsItem(recipient);
    }

    @Then("viene generata una nuova notifica valida con uguale codice fiscale del creditore e uguale codice avviso")
    public void vieneGenerataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEUgualeCodiceAvvisoConTaxIdCorretto() {
        String creditorTaxId = Objects.requireNonNull(Objects.requireNonNull(notificationRequest.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getCreditorTaxId();
        String noticeCode = Objects.requireNonNull(Objects.requireNonNull(notificationRequest.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getNoticeCode();
        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
        this.notificationRequest = (dataTableTypeUtil.convertNotificationRequestV24(new HashMap<>())
                .addRecipientsItem(updateNotificationRecipient(notificationRecipientV23, null, marioCucumberTaxID, null, null)));

        Objects.requireNonNull(Objects.requireNonNull(this.notificationRequest.getRecipients().get(0).getPayments()).get(0).getPagoPa()).setCreditorTaxId(creditorTaxId);
        Objects.requireNonNull(Objects.requireNonNull(this.notificationRequest.getRecipients().get(0).getPayments()).get(0).getPagoPa()).setNoticeCode(noticeCode);
    }

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEUgualeCodiceAvviso() {
        String creditorTaxId = Objects.requireNonNull(Objects.requireNonNull(notificationRequest.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getCreditorTaxId();
        String noticeCode = Objects.requireNonNull(Objects.requireNonNull(notificationRequest.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getNoticeCode();
        generateNewNotification();
        Objects.requireNonNull(Objects.requireNonNull(this.notificationRequest.getRecipients().get(0).getPayments()).get(0).getPagoPa()).setCreditorTaxId(creditorTaxId);
        Objects.requireNonNull(Objects.requireNonNull(this.notificationRequest.getRecipients().get(0).getPayments()).get(0).getPagoPa()).setNoticeCode(noticeCode);
    }

    @And("viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken {string}")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualePaProtocolNumberEIdempotenceToken(String idempotenceToken) {
        String paProtocolNumber = notificationRequest.getPaProtocolNumber();
        generateNewNotification();
        this.notificationRequest.setIdempotenceToken(idempotenceToken);
        this.notificationRequest.setPaProtocolNumber(paProtocolNumber);
    }

    @And("viene generata una nuova notifica con uguale paProtocolNumber")
    public void vieneGenerataUnaNuovaNotificaConUgualePaProtocolNumber() {
        String paProtocolNumber = notificationRequest.getPaProtocolNumber();
        generateNewNotification();
        this.notificationRequest.setPaProtocolNumber(paProtocolNumber);
    }

    @And("aggiungo {int} numero allegati")
    public void aggiungoNumeroAllegati(int numAllegati) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        notificationStepsInterface.addDocumentItems(numAllegati);
    }

    @When("la notifica viene inviata tramite api b2b e si attende che lo stato diventi {string}")
    public void sendNotificationWithoutSettingPa(String status) {
        sendNotification(null, status);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi {string}")
    public void sendNotification(String paName, String status) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        String versionString = notificationVersion.name();
        if (status.equalsIgnoreCase("HTTP_ERROR")) {
            sendNotificationHttpError(paName);
        } else {
            sendNotificationWithVersion(versionString, paName, status);
        }
    }

    @When("la notifica viene inviata tramite api b2b con la versione {string} dal {string} e si attende che lo stato diventi {string}")
    public void sendNotificationWithVersion(String version, String paName, String status) {
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
        //TODO MATTEO: prima richiamava waitForRequestNoAcceptation in b2bUtils. Ma è corretto che prenda "ACCEPTED" ?
        getNotificationStepInterface().sendNotification(getWorkFlowWait(), NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS_NO_ACCEPTATION);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED e successivamente annullata")
    public void laNotificaVieneInviataOkAndCancelled(String paName) {
        setPaAndSenderTaxId(paName);
        getNotificationStepInterface().sendNotification(WAIT_EXTRA_RAPID, NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS);
        String iun = getIunVersionamento();
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
        String iun = getIunVersionamento();
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

    //TODO MATTEO: il test che richiama questo metodo non è utilizzato, cancellare ?
    @And("viene effettuato recupero stato della notifica con la V1 dal comune {string}")
    public void retrieveStateNotification(String paName) {
        versionUsed = V1;
        setPaAndSenderTaxId(paName);
        String requestId = Base64Utils.encodeToString(fullSentNotificationV26.getIun().getBytes());
        try {
            Assertions.assertDoesNotThrow(() -> b2bClient.getNotificationRequestStatusV1(requestId));
        } catch (AssertionFailedError assertionFailedError) {
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model_v1.NewNotificationResponse notificationResponse =
                    (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model_v1.NewNotificationResponse) notificationStepsV1.retrieveNotificationResponse();
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("l'operazione di annullamento ha prodotto un errore con status code {string}")
    public void cancellationProducedErrorWithStatusCode(String statusCode) {
        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si annulla prima che lo stato diventi REFUSED")
    public void laNotificaVieneInviataRefusedAndCancelled(String paName) {
        setPaAndSenderTaxId(paName);
        getNotificationStepInterface().sendNotification(1000, NOTIFICATION_STATUS_NOT_REFUSED, VALIDATION_STATUS);
    }

    //TODO: per test normalizzatore
    //NOTA: il metodo riceve un parametro da scenario Outline, per quello sembra non venga richiamato (AddressValidation.feature)
    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi HTTP_ERROR")
    public void sendNotificationHttpError(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationWithError(getNotificationStepInterface());
        Assertions.assertNotNull(this.notificationError);
        Assertions.assertEquals(400, this.notificationError.getStatusCode().value());
    }

    @When("la notifica viene inviata tramite api b2b senza preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataSenzaPreloadAllegato(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationWithErrorNotFindAllegato(false);
    }

    @When("la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataTramiteApiBBEffettuandoLaPreloadMaSenzaCaricareNessunAllegatoDalESiAttendeCheLoStatoDiventiREFUSED(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationWithErrorNotFindAllegato(true);
    }

    @When("la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato json dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataTramiteApiBBEffettuandoLaPreloadMaSenzaCaricareNessunAllegatoJsonDalESiAttendeCheLoStatoDiventiREFUSED(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationWithErrorNotFindAllegatoJson();
    }

    @When("la notifica viene inviata tramite api b2b con sha256 differente dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConShaDifferente(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationWithErrorSha();
    }

    @When("la notifica viene inviata tramite api b2b con sha256 Json differente dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConShaJsonDifferente(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationWithErrorShaJson();
    }

    @When("la notifica viene inviata tramite api b2b con estensione errata dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConEstensioneErrata(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationWithWrongExtension();
    }

    @When("la notifica viene inviata tramite api b2b oversize preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoOverSize(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedOverSizeAllegato();
    }

    @When("la notifica viene inviata tramite api b2b injection preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoInjection(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedInjectionAllegato();
    }

    @When("la notifica viene inviata tramite api b2b over 15 preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoOver15(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationRefusedOver15Allegato();
    }

    @When("la notifica viene inviata dal {string}")
    public void laNotificaVieneInviataDallaPA(String paName) {
        setPaAndSenderTaxId(paName);
        sendNotificationWithError(getNotificationStepInterface());
    }

    @When("la notifica viene inviata tramite api b2b")
    public void laNotificaVieneInviataTramiteApiB2b() {
        sendNotificationWithError(getNotificationStepInterface());
    }

    private void sendNotificationWithError(NotificationStepsInterface notificationStepsInterface) {
        notificationCreationDate = OffsetDateTime.now();
        try {
            notificationStepsInterface.uploadNotification();
        } catch (HttpStatusCodeException | IOException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    @When("la notifica viene inviata tramite api b2b senza preload allegato dal {string}")
    public void laNotificaVieneInviatatramiteApiB2bSenzaPreloadAllegato(String pa) {
        setPaAndSenderTaxId(pa);
        sendNotificationWithErrorNotFindAllegato(false);
    }

    @And("al destinatario viene associato lo iuv creato mediante partita debitoria per {string} alla posizione {int}")
    public void destinatarioAddIuvGPD(String denominazione, Integer posizione) {
        if (this.notificationRequest != null) {
            Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(this.notificationRequest.getRecipients().get(0).denomination(denominazione).getPayments())).get(posizione).getPagoPa()).setNoticeCode(getIuvGPD(posizione));
        } else {
            NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(NotificationVersion.V21);
            NewNotificationRequestV21 notificationRequestV21 = (NewNotificationRequestV21) notificationStepsInterface.retrieveNotificationRequest();
            if (notificationRequestV21 != null) {
                Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(notificationRequestV21.getRecipients().get(0).denomination(denominazione).getPayments())).get(posizione).getPagoPa()).setNoticeCode(getIuvGPD(posizione));
            }
        }
    }

    @And("al destinatario viene associato lo iuv creato mediante partita debitoria per {string} per la posizione debitoria {int} del pagamento {int}")
    public void destinatarioAddIuvGPDperUtente(String denominazione, Integer posizioneDebitoria, Integer pagamento) {
        for (NotificationRecipientV23 recipient : this.notificationRequest.getRecipients()) {
            if (recipient.getDenomination().equalsIgnoreCase(denominazione)) {
                Objects.requireNonNull(Objects.requireNonNull(recipient.getPayments()).get(pagamento).getPagoPa()).setNoticeCode(getIuvGPD(posizioneDebitoria));
            }
        }
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

    //TODO MATTEO TEST (editato in maniera più compatta)
    @Then("si verifica che la notifica non viene accettata causa {string}")
    public void verificaNotificaNoAccept(String causa) {
        String expectedErrorCode = switch (causa) {
            case ALLEGATO -> FILE_NOTFOUND;
            case EXTENSION, FILE_PDF_INVALID_ERROR -> FILE_PDF_INVALID_ERROR;
            case SHA_256 -> FILE_SHA_ERROR;
            case Costanti.TAX_ID -> TAXID_NOT_VALID;
            case ADDRESS, NOT_VALID_ADDRESS -> NOT_VALID_ADDRESS;
            case INVALID_PARAMETER_MAX_ATTACHMENT -> INVALID_PARAMETER_MAX_ATTACHMENT;
            default -> throw new IllegalArgumentException();
        };
        Assertions.assertTrue(expectedErrorCode.equalsIgnoreCase(errorCode));
    }

    private void sendNotificationWithErrorNotFindAllegato(boolean noUpload) {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                newNotificationResponse = b2bUtils.uploadNotificationNotFindAllegato(notificationRequest, noUpload);
                errorCode = b2bUtils.waitForRequestRefusedV25(newNotificationResponse);
            });
            threadWait(getWorkFlowWait());
            Assertions.assertNotNull(errorCode);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void sendNotificationWithErrorNotFindAllegatoJson() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                newNotificationResponse = b2bUtils.uploadNotificationNotFindAllegatoJson(notificationRequest, true);
                errorCode = b2bUtils.waitForRequestRefusedV25(newNotificationResponse);
            });

            threadWait(getWorkFlowWait());

            Assertions.assertNotNull(errorCode);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void sendNotificationWithErrorSha() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                newNotificationResponse = b2bUtils.uploadNotificationNotEqualSha(notificationRequest);
                errorCode = b2bUtils.waitForRequestRefusedV25(newNotificationResponse);
            });

            threadWait(getWorkFlowWait());

            Assertions.assertNotNull(errorCode);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void sendNotificationWithErrorShaJson() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                newNotificationResponse = b2bUtils.uploadNotificationNotEqualShaJson(notificationRequest);
                errorCode = b2bUtils.waitForRequestRefusedV25(newNotificationResponse);
            });

            threadWait(getWorkFlowWait());

            Assertions.assertFalse(errorCode.isEmpty());

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void sendNotificationWithWrongExtension() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                newNotificationResponse = b2bUtils.uploadNotificationWrongExtension(notificationRequest);

                errorCode = b2bUtils.waitForRequestRefusedV25(newNotificationResponse);
            });

            threadWait(getWorkFlowWait());

            Assertions.assertFalse(errorCode.isEmpty());

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void sendNotificationRefusedOverSizeAllegato() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                newNotificationResponse = b2bUtils.uploadNotificationOverSizeAllegato(notificationRequest);
                errorCode = b2bUtils.waitForRequestRefusedV25(newNotificationResponse);
            });

            threadWait(getWorkFlowWait());
            Assertions.assertFalse(errorCode.isEmpty());


        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void sendNotificationRefusedInjectionAllegato() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                newNotificationResponse = b2bUtils.uploadNotificationInjectionAllegato(notificationRequest);
                errorCode = b2bUtils.waitForRequestRefusedV25(newNotificationResponse);
            });

            threadWait(getWorkFlowWait());
            Assertions.assertFalse(errorCode.isEmpty());


        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void sendNotificationRefusedOver15Allegato() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                newNotificationResponse = b2bUtils.uploadNotificationOver15Allegato(notificationRequest);
                errorCode = b2bUtils.waitForRequestRefusedV25(newNotificationResponse);
            });

            threadWait(getWorkFlowWait());

            Assertions.assertFalse(errorCode.isEmpty());

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            Assertions.assertTrue(message.contains("400") && message.contains("Max attachment count reached"));
            errorCode = "INVALID_PARAMETER_MAX_ATTACHMENT";
        }
    }

    private void generateNewNotification() {
        assert this.notificationRequest.getRecipients().get(0).getPayments() != null;
        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
        this.notificationRequest = (dataTableTypeUtil.convertNotificationRequestV24(new HashMap<>())
                .subject(notificationRequest.getSubject())
                .senderDenomination(notificationRequest.getSenderDenomination())
                .addRecipientsItem(updateNotificationRecipient(notificationRecipientV23,
                        notificationRequest.getRecipients().get(0).getDenomination(),
                        notificationRequest.getRecipients().get(0).getTaxId(),
                        notificationRequest.getRecipients().get(0).getRecipientType(),
                        null)));
    }

    public HttpStatusCodeException consumeNotificationError() {
        HttpStatusCodeException value = notificationError;
        this.notificationError = null;
        return value;
    }

    private void setPaAndSenderTaxId(String paName) {
        setPA(paName);
        setSenderTaxId(paName);
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
            default -> throw new IllegalArgumentException();
        }
        this.b2bUtils.setClient(b2bClient, pollingFactory);
    }

    private void setSenderTaxId(String pa) {
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface();
        switch (pa) {
            case COMUNE_1 -> {
                notificationStepsInterface.setSenderTaxId(COMUNE_1_TAX_ID);
                setGroup(SettableApiKey.ApiKeyType.MVP_1, notificationStepsInterface);
                apiKeyTypeSetted = SettableApiKey.ApiKeyType.MVP_1;
            }
            case COMUNE_2 -> {
                notificationStepsInterface.setSenderTaxId(COMUNE_2_TAX_ID);
                setGroup(SettableApiKey.ApiKeyType.MVP_2, notificationStepsInterface);
                apiKeyTypeSetted = SettableApiKey.ApiKeyType.MVP_2;
            }
            case COMUNE_MULTI -> {
                notificationStepsInterface.setSenderTaxId(COMUNE_MULTI_TAX_ID);
                setGroup(SettableApiKey.ApiKeyType.GA, notificationStepsInterface);
                apiKeyTypeSetted = SettableApiKey.ApiKeyType.GA;
            }
            case COMUNE_SON -> {
                notificationStepsInterface.setSenderTaxId(COMUNE_SON_TAX_ID);
                setGroup(SettableApiKey.ApiKeyType.SON, notificationStepsInterface);
                apiKeyTypeSetted = SettableApiKey.ApiKeyType.SON;
            }
            case COMUNE_ROOT -> {
                notificationStepsInterface.setSenderTaxId(COMUNE_ROOT_TAX_ID);
                setGroup(SettableApiKey.ApiKeyType.ROOT, notificationStepsInterface);
                apiKeyTypeSetted = SettableApiKey.ApiKeyType.ROOT;
            }
        }
    }

    private void setGroup(SettableApiKey.ApiKeyType apiKeyType, NotificationStepsInterface notificationStepsInterface) {
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
            default -> throw new IllegalArgumentException();
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
                " {IUN: " + Optional.ofNullable(getIunVersionamento())
                .orElse("not found") + " }";
    }

    public void throwAssertionFailedErrorWithAmountGDPAndIUN(AssertionFailedError assertionFailedError, Integer amountGDP) {
        String message = assertionFailedError.getMessage() +
                "{IUN: " + fullSentNotificationV26.getIun() + ", amountGDP " + (amountGDP == null ? "NULL" : amountGDP.toString()) + "}";
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

    public List<HashMap<String, String>> getGroupsByPa(String paName) {
        List<HashMap<String, String>> hashMapsList = switch (paName) {
            case COMUNE_1 -> this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.MVP_1);
            case COMUNE_2 -> this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.MVP_2);
            case COMUNE_MULTI -> this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.GA);
            case COMUNE_SON -> this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.SON);
            case COMUNE_ROOT -> this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.ROOT);
            default -> throw new IllegalArgumentException();
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

    /**
     * Get all timeline elements that match the given event category and data from test
     *
     * @param timelineEventCategory the category of the timeline event
     * @param dataFromTest          the data filters
     * @return a list of timeline elements that match the given event category and data from test
     */
    public List<TimelineElementV26> getTimelineElementsByEventId(String timelineEventCategory, DataTest dataFromTest) {
        List<TimelineElementV26> timelineElementList = fullSentNotificationV26.getTimeline();
        String iun = getIun(timelineEventCategory);
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            // get timeline event id
            String timelineEventId = getTimelineEventId(timelineEventCategory, iun, dataFromTest);
            if (timelineEventCategory.equals(TimelineElementCategoryV26.SEND_ANALOG_PROGRESS.getValue()) || timelineEventCategory.equals(TimelineElementCategoryV26.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS.getValue())) {
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
        List<TimelineElementV26> timelineElementList = fullSentNotificationV26.getTimeline();
        return timelineElementList.stream()
                .filter(elem -> nonNull(elem.getDetails()))
                .filter(elem -> elem.getDetails().getSentAttemptMade() <= attemptIndex)
                .toList();
    }

    public TimelineElementV26 getTimelineElementByEventId(String timelineEventCategory, DataTest dataFromTest) {
        return getTimelineElementsByEventId(timelineEventCategory, dataFromTest).stream()
                .findAny()
                .orElse(null);
    }

    private String getIun(String timelineEventCategory) {
        String iun;
        if (timelineEventCategory.equals(TimelineElementCategoryV26.REQUEST_REFUSED.getValue())) {
            String requestId = newNotificationResponse.getNotificationRequestId();
            byte[] decodedBytes = Base64.getDecoder().decode(requestId);
            iun = new String(decodedBytes);
        } else {
            // proceed with default flux
            iun = fullSentNotificationV26.getIun();
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
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        //TODO MATTEO FINIRE

        List<String> datiPagamento = new ArrayList<>();
        if (fullSentNotificationV1 != null) {
            datiPagamento.add(Objects.requireNonNull(fullSentNotificationV1.getRecipients().get(destinatario).getPayment()).getCreditorTaxId());
            datiPagamento.add(Objects.requireNonNull(fullSentNotificationV1.getRecipients().get(destinatario).getPayment()).getNoticeCode());
        } else if (fullSentNotificationV20 != null) {
            datiPagamento.add(Objects.requireNonNull(fullSentNotificationV20.getRecipients().get(destinatario).getPayment()).getCreditorTaxId());
            datiPagamento.add(Objects.requireNonNull(fullSentNotificationV20.getRecipients().get(destinatario).getPayment()).getNoticeCode());
        } else if (fullSentNotificationV21 != null) {
            datiPagamento.add(Objects.requireNonNull(Objects.requireNonNull(fullSentNotificationV21.getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getCreditorTaxId());
            datiPagamento.add(Objects.requireNonNull(Objects.requireNonNull(fullSentNotificationV21.getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getNoticeCode());
        } else if (fullSentNotificationV26 != null) {
            datiPagamento.add(Objects.requireNonNull(Objects.requireNonNull(fullSentNotificationV26.getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getCreditorTaxId());
            datiPagamento.add(Objects.requireNonNull(Objects.requireNonNull(fullSentNotificationV26.getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getNoticeCode());
        }
        return datiPagamento;
    }

    public static void threadWait(int wait) {
        try {
            await().atMost(wait, TimeUnit.MILLISECONDS);
        } catch (RuntimeException exception) {
            log.error("Await error exception");
            throw exception;
        }
    }

    private <T, V, K> T updateNotificationRecipient(T notificationRecipient, String denomination, String taxId, V recipientType, K digitalDomicile) {

        if (notificationRecipient instanceof NotificationRecipientV23) {
            ((NotificationRecipientV23) notificationRecipient)
                    .denomination(denomination).taxId(taxId);
            if (recipientType != null) {
                ((NotificationRecipientV23) notificationRecipient)
                        .recipientType((NotificationRecipientV23.RecipientTypeEnum) recipientType);
            }
            if (digitalDomicile != null) {
                ((NotificationRecipientV23) notificationRecipient)
                        .digitalDomicile((NotificationDigitalAddress) digitalDomicile);
            }
        } else if (notificationRecipient instanceof it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient) {
            ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient) notificationRecipient)
                    .denomination(denomination).taxId(taxId);
            if (recipientType != null) {
                ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient) notificationRecipient)
                        .recipientType((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient.RecipientTypeEnum) recipientType);
            }
            if (digitalDomicile != null) {
                ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient) notificationRecipient)
                        .digitalDomicile((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDigitalAddress) digitalDomicile);
            }
        } else if (notificationRecipient instanceof it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient) {
            ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient) notificationRecipient)
                    .denomination(denomination).taxId(taxId);
            if (recipientType != null) {
                ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient) notificationRecipient)
                        .recipientType((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient.RecipientTypeEnum) recipientType);
            }
            if (digitalDomicile != null) {
                ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient) notificationRecipient)
                        .digitalDomicile((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDigitalAddress) digitalDomicile);
            }
        } else if (notificationRecipient instanceof it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21) {
            ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21) notificationRecipient)
                    .denomination(denomination).taxId(taxId);
            if (recipientType != null) {
                ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21) notificationRecipient)
                        .recipientType((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21.RecipientTypeEnum) recipientType);
            }
            if (digitalDomicile != null) {
                ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21) notificationRecipient)
                        .digitalDomicile((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDigitalAddress) digitalDomicile);
            }
        }
        return notificationRecipient;
    }

    //TODO MATTEO: spostato da AvanzamentoNotificheWebhookB2BSteps, dove non c'entrava nulla
    @Then("tra gli elementi di timeline versione {string} di categoria {string} nessuno contiene un legalFact con categoria {string}")
    public void checkTimelineElementVersionLegalFacts(String version, String timelineCategory, String legalFactCategory) {
        NotificationVersion notificationVersion = getNotificationVersion(version);
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        Object sentNotificationAnyVersion = notificationStepsInterface.getSentNotificationAnyVersion();
        Assertions.assertNotNull(sentNotificationAnyVersion);

        if (version.equalsIgnoreCase("V26") || version.equalsIgnoreCase("V27")) {
            TimelineElementV26 timelineElementWithTargetCategory = fullSentNotificationV26.getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertNotEquals(x.getCategory(), legalFactCategory));
        } else if (version.equalsIgnoreCase("V25")) {
            TimelineElementV25 timelineElementWithTargetCategory = fullSentNotificationV25.getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertNotEquals(x.getCategory(), legalFactCategory));
        } else if (version.equalsIgnoreCase("V24")) {
            TimelineElementV24 timelineElementWithTargetCategory = fullSentNotificationV24.getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertNotEquals(x.getCategory().getValue(), legalFactCategory));
        } else if (version.equalsIgnoreCase("V23")) {
            TimelineElementV23 timelineElementWithTargetCategory = fullSentNotificationV23.getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertNotEquals(x.getCategory().getValue(), legalFactCategory));
        }
    }

    @Then("stampa log dello IUN della notifica {string} con allegato {string} su comune {string}")
    public void stampaLogDelloIUNDellaNotificaConAllegatoSuComune(String notificationType, String attachment, String municipality) {
        log.info("notifica STAMPA COLORI IUN: {}, notifica: {}, allegato: {}, comune: {}", fullSentNotificationV26.getIun(), notificationType, attachment, municipality);
    }
}