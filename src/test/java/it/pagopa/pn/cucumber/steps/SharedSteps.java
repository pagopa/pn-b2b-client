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
import org.springframework.boot.convert.DurationStyle;
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
import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Costanti.TAX_ID;
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
    private NewNotificationResponse newNotificationResponse;

    @Getter
    @Setter
    private NewNotificationRequestV24 notificationRequest;

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
    private TimelineElementV26 timelineElement;

    @Getter
    @Setter
    @Value("${pn.external.bearer-token-pg1.id}")
    private String idOrganizationGherkinSrl;

    @Getter
    @Setter
    @Value("${pn.external.bearer-token-pg2.id}")
    private String idOrganizationCucumberSpa;

    @Getter
    @Setter
    private List<ProgressResponseElement> progressResponseElementList = null;

    @Getter
    @Setter
    private List<ProgressResponseElementV23> progressResponseElementListV23 = null;

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
    private final ApplicationContext context;
    @Getter
    private final DataTableTypeUtil dataTableTypeUtil;
    private final List<String> iuvGPD;
    private IPnWebUserAttributesClient iPnWebUserAttributesClient;

    @Getter
    @Setter
    private String errorCode;

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

    //    private String settedPa = COMUNE_1;
    private boolean groupToSet = true;

    private final SecureRandom secureRandom = new SecureRandom();
    private final PnB2bClientTimingConfigs timingConfigs;
    private final Duration schedulingDaysSuccessDigitalRefinementDefault = DurationStyle.detectAndParse("6m");
    private final Duration schedulingDaysFailureDigitalRefinementDefault = DurationStyle.detectAndParse("6m");
    private final Duration schedulingDaysSuccessAnalogRefinementDefault = DurationStyle.detectAndParse("2m");
    private final Duration schedulingDaysFailureAnalogRefinementDefault = DurationStyle.detectAndParse("4m");
    private final Duration timeToAddInNonVisibilityTimeCaseDefault = DurationStyle.detectAndParse("10m");
    private final Duration secondNotificationWorkflowWaitingTimeDefault = DurationStyle.detectAndParse("6m");
    private final Duration waitingForReadCourtesyMessageDefault = DurationStyle.detectAndParse("5m");
    private final ObjectMapper objMapper;


    //    private final String gherkinSrltaxId = "12666810299";
//    private final String gherkinSpaTaxID = "12666810299";
//    private final String cucumberSpataxId = "20517490320";
//    private final String cucumberSrlTaxID = "20517490320";
//    private final String cucumberSocietyTaxID = "20517490320";
//    private final String gherkinIrreperibileTaxID = "00749900049";
//    private static final String gherkinAnalogicTaxID = "05722930657";
//    private static final String cucumberAnalogicTaxID = "LBPHLS94A56C826R";
//    private static final String defaultDigitalAddress = "testpagopa3@pec.pagopa.it";

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


    public HashMap<String, String> getMapAllegatiNotificaSha256() {
        return mapAllegatiNotificaSha256;
    }

    //TODO: nessun utilizzo a codice. Rimuovere ?
//    public void setMapAllegatiNotificaSha256(HashMap<String, String> mapAllegatiNotificaSha256) {
//        this.mapAllegatiNotificaSha256 = mapAllegatiNotificaSha256;
//    }

    private final HashMap<String, String> mapAllegatiNotificaSha256 = new HashMap<>();

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

    private NotificationVersion getNotificationVersion(String version) {
        if (version.trim().equalsIgnoreCase(MOST_RECENT)) {
            return NotificationVersion.V24;//TODO: modificare questo valore ogni volta che viene aggiunta una versione più recente
        }
        return NotificationVersion.valueOf(version.trim().toUpperCase());
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

    //TODO MATTEO TEST
    @Given("viene generata una nuova notifica")
    public void prepareNotificationRequest(Map<String, String> data) {
        prepareNotificationRequestWithVersion(data, MOST_RECENT);
    }

    //TODO MATTEO TEST
    @Given("viene generata una nuova notifica con la versione {string}")
    public void prepareNotificationRequestWithVersion(Map<String, String> data, String version) {
        NotificationVersion notificationVersion = getNotificationVersion(version);
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        notificationStepsInterface.setNotificationRequest(data);
    }

    //TODO MATTEO TEST
    @And("destinatario")
    public void addDestinatario(Map<String, String> data) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        notificationStepsInterface.addRecipitentToNotification(null, data);
    }

    //TODO MATTEO TEST
    @And("destinatario {string}")
    public void addDestinatario(String destinatario) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        notificationStepsInterface.addRecipitentToNotification(destinatario, new HashMap<>());
    }

    //TODO MATTEO TEST
    @And("destinatario {string} e:")
    public void addDestinatarioWithParams(String destinatario, Map<String, String> data) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        notificationStepsInterface.addRecipitentToNotification(destinatario, data);
    }

    @And("senza destinatario")
    public void senzaDestinatario() {
//        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
//        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
//        notificationStepsInterface.addRecipitentToNotification("nessuno", new HashMap<>());
        //TODO MATTEO: TEST SE IL COMPORTAMENTO DI SOPRA (NUOVO) E' IDENTICO A QUELLO DI SOTTO (VECCHIO)
        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
        addRecipientToNotification(this.notificationRequest, notificationRecipientV23, new HashMap<>());
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

    //    @Given("viene generata una nuova notificaOLD")
//    public void vieneGenerataUnaNotifica(@Transpose NewNotificationRequestV24 notificationRequest) {
//        this.notificationRequest = notificationRequest;
//    }

//    @Given("viene generata una nuova notifica V1")
//    public void vieneGenerataUnaNotificaV1(@Transpose it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest notificationRequestV1) {
//        this.notificationRequestV1 = notificationRequestV1;
//    }

//    @Given("viene generata una nuova notifica V2")
//    public void vieneGenerataUnaNotificaV2(@Transpose it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest notificationRequestV2) {
//        this.notificationRequestV2 = notificationRequestV2;
//    }

//    @Given("viene generata una nuova notifica V21")
//    public void vieneGenerataUnaNotificaV21(@Transpose it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21 notificationRequestV21) {
//        this.notificationRequestV21 = notificationRequestV21;
//    }
//
    //TODO MATTEO RIMUOVERE (è diventato un metodo di DestinatariUtils)
//    public String getDigitalAddressValue() {
//        if (digitalAddress == null || digitalAddress.equalsIgnoreCase("${pn.external.digitalDomicile.address}"))
//            return defaultDigitalAddress;
//        return digitalAddress;
//    }

//    @And("destinatario")
//    public void destinatario(Map<String, String> data) {
//        addRecipientToNotification(this.notificationRequest, dataTableTypeUtil.convertNotificationRecipient(data), data);
//    }

//    @And("destinatario V1")
//    public void destinatario(@Transpose it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient recipient) {
//        this.notificationRequestV1.addRecipientsItem(recipient);
//    }

//    @And("destinatario V2")
//    public void destinatario(@Transpose it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient recipient) {
//        this.notificationRequestV2.addRecipientsItem(recipient);
//    }

//    @And("destinatario Mario Cucumber")
//    public void destinatarioMarioCucumber() {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "Mario Cucumber",
//                        marioCucumberTaxID,
//                        null,
//                        new NotificationDigitalAddress()
//                                .type(NotificationDigitalAddress.TypeEnum.PEC)
//                                .address(getDigitalAddressValue())
//                )
//                , new HashMap<>());
//    }

//    @And("destinatario Mario Cucumber e:")
//    public void destinatarioMarioCucumberParam(Map<String, String> data) {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "Mario Cucumber",
//                        marioCucumberTaxID,
//                        null,
//                        null),
//                data);
//    }

//    @And("destinatario Mario Cucumber V1")
//    public void destinatarioMarioCucumberV1() {
//        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient notificationRecipient = dataTableTypeUtil.convertNotificationRecipientV1(new HashMap<>());
//        this.notificationRequestV1.addRecipientsItem(
//                updateNotificationRecipient(notificationRecipient,
//                        "Mario Cucumber",
//                        marioCucumberTaxID,
//                        null,
//                        new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDigitalAddress()
//                                .type(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDigitalAddress.TypeEnum.PEC)
//                                .address(getDigitalAddressValue())));
//    }

//    @And("destinatario Mario Cucumber V2")
//    public void destinatarioMarioCucumberV2() {
//        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient notificationRecipient = dataTableTypeUtil.convertNotificationRecipientV2(new HashMap<>());
//        this.notificationRequestV2.addRecipientsItem(
//                updateNotificationRecipient(notificationRecipient,
//                        "Mario Cucumber",
//                        marioCucumberTaxID,
//                        null,
//                        new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDigitalAddress()
//                                .type(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDigitalAddress.TypeEnum.PEC)
//                                .address(getDigitalAddressValue())));
//    }

//    @And("destinatario Mario Gherkin")
//    public void destinatarioMarioGherkin() {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "Mario Gherkin",
//                        marioGherkinTaxID,
//                        null,
//                        new NotificationDigitalAddress()
//                                .type(NotificationDigitalAddress.TypeEnum.PEC)
//                                .address(getDigitalAddressValue()))
//                , new HashMap<>());
//    }

//    @And("destinatario Mario Gherkin e:")
//    public void destinatarioMarioGherkinParam(Map<String, String> data) {
//        this.notificationRequest = this.notificationStepsV24.getNotificationRequest();//TODO MATTEO
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "Mario Gherkin",
//                        marioGherkinTaxID,
//                        null,
//                        null)
//                , data);
//    }

//    @And("destinatario Mario Gherkin V1 e:")
//    public void destinatarioMarioGherkinParam(@Transpose it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient recipient) {
//        this.notificationRequestV1.addRecipientsItem(
//                updateNotificationRecipient(recipient,
//                        "Mario Gherkin",
//                        marioGherkinTaxID,
//                        null,
//                        null));
//    }

//    @And("destinatario Mario Gherkin V2 e:")
//    public void destinatarioMarioGherkinParam(@Transpose it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient recipient) {
//        this.notificationRequestV2.addRecipientsItem(
//                updateNotificationRecipient(recipient,
//                        "Mario Gherkin",
//                        marioGherkinTaxID,
//                        null,
//                        null));
//    }

//    @And("destinatario Mario Gherkin V21 e:")
//    public void destinatarioMarioGherkinParam(@Transpose it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21 recipient) {
//        this.notificationRequestV21.addRecipientsItem(
//                updateNotificationRecipient(recipient,
//                        "Mario Gherkin",
//                        marioGherkinTaxID,
//                        null,
//                        null));
//    }

//    @And("destinatario Gherkin spa")
//    public void destinatarioGherkinSpa() {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "GherkinSpa",
//                        gherkinSpaTaxID,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        new NotificationDigitalAddress()
//                                .type(NotificationDigitalAddress.TypeEnum.PEC)
//                                .address(getDigitalAddressValue())),
//                new HashMap<>());
//    }

//    @And("destinatario Gherkin spa e:")
//    public void destinatarioGherkinSpaParam(Map<String, String> data) {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "GherkinSpa",
//                        gherkinSpaTaxID,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        null)
//                , data);
//    }

//    @And("destinatario GherkinSrl")
//    public void destinatarioPg1() {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "GherkinSrl",
//                        gherkinSrltaxId,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        new NotificationDigitalAddress()
//                                .type(NotificationDigitalAddress.TypeEnum.PEC)
//                                .address(getDigitalAddressValue()))
//                , new HashMap<>());
//    }

//    @And("destinatario GherkinSrl e:")
//    public void destinatarioPg1param(Map<String, String> data) {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "GherkinSrl",
//                        gherkinSrltaxId,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        null)
//                , data);
//    }

//    @And("destinatario CucumberSpa")
//    public void destinatarioPg2() {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "CucumberSpa",
//                        cucumberSpataxId,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        new NotificationDigitalAddress()
//                                .type(NotificationDigitalAddress.TypeEnum.PEC)
//                                .address(getDigitalAddressValue()))
//                , new HashMap<>());
//    }

//    @And("destinatario CucumberSpa e:")
//    public void destinatarioPg2param(Map<String, String> data) {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "CucumberSpa",
//                        cucumberSpataxId,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        null)
//                , data);
//    }

//    @And("destinatario Cucumber srl")
//    public void destinatarioCucumberSrl() {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "CucumberSrl",
//                        cucumberSrlTaxID,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        new NotificationDigitalAddress()
//                                .type(NotificationDigitalAddress.TypeEnum.PEC)
//                                .address(getDigitalAddressValue()))
//                , new HashMap<>());
//    }

//    @And("destinatario Cucumber srl e:")
//    public void destinatarioCucumberSrlParam(Map<String, String> data) {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "CucumberSrl",
//                        cucumberSrlTaxID,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        null)
//                , data);
//    }

//    @And("destinatario Cucumber Society")
//    public void destinatarioCucumberSociety() {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "Cucumber_Society",
//                        cucumberSocietyTaxID,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        new NotificationDigitalAddress()
//                                .type(NotificationDigitalAddress.TypeEnum.PEC)
//                                .address(getDigitalAddressValue()))
//                , new HashMap<>());
//    }

//    @And("destinatario Cucumber Society e:")
//    public void destinatarioCucumberSocietyParam(Map<String, String> data) {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "Cucumber_Society",
//                        cucumberSocietyTaxID,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        null)
//                , data);
//    }

//    @And("destinatario Signor casuale")
//    public void destinatarioSignorCasuale() {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "signor RaddCasuale",
//                        generateCF(System.currentTimeMillis()),
//                        NotificationRecipientV23.RecipientTypeEnum.PF,
//                        new NotificationDigitalAddress()
//                                .type(NotificationDigitalAddress.TypeEnum.PEC)
//                                .address(getDigitalAddressValue()))
//                , new HashMap<>());
//    }

//    @And("destinatario Signor casuale e:")
//    public void destinatarioSignorCasualeMap(Map<String, String> data) {
//
//        threadWait(new Random().nextInt(500));
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "signor RaddCasuale",
//                        generateCF(System.currentTimeMillis()),
//                        NotificationRecipientV23.RecipientTypeEnum.PF,
//                        null)
//                , data);
//    }

//    @And("destinatario Gherkin Analogic e:")
//    public void destinatarioGherkinAnalogicParam(Map<String, String> data) {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "Gherkin Analogic",
//                        gherkinAnalogicTaxID,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        null)
//                , data);
//    }

//    @And("destinatario Gherkin Irreperibile e:")
//    public void destinatarioGherkinIrreperibileParam(Map<String, String> data) {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "Gherkin Irreperibile",
//                        gherkinIrreperibileTaxID,
//                        NotificationRecipientV23.RecipientTypeEnum.PG,
//                        null)
//                , data);
//    }

//    @And("destinatario Cucumber Analogic e:")
//    public void destinatarioCucumberAnalogicParam(Map<String, String> data) {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(data);
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "Cucumber Analogic",
//                        cucumberAnalogicTaxID,
//                        null,
//                        null)
//                , data);
//    }

//    @And("destinatario Cristoforo Colombo")
//    public void destinatarioCristoforoColombo() {
//        NotificationRecipientV23 notificationRecipientV23 = dataTableTypeUtil.convertNotificationRecipient(new HashMap<>());
//        addRecipientToNotification(this.notificationRequest,
//                updateNotificationRecipient(notificationRecipientV23,
//                        "Cristoforo Colombo",
//                        "CLMCST42R12D969Z",
//                        null,
//                        null)
//                , new HashMap<>());
//    }

//    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi REFUSED")
//    public void laNotificaVieneInviataRefused(String paName) {
//        setPaAndSenderTaxId(paName, null);
//        sendNotificationRefused(getWorkFlowWait());
//    }

//    private void sendNotificationRefused(int wait) {
//        try {
//            Assertions.assertDoesNotThrow(() -> {
//                notificationCreationDate = OffsetDateTime.now();
//                newNotificationResponse = b2bUtils.uploadNotificationV24(notificationRequest);
//                errorCode = b2bUtils.waitForRequestRefusedV25(newNotificationResponse);
//            });
//            threadWait(wait);
//            Assertions.assertFalse(errorCode.isEmpty());
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
//    }

    @And("al destinatario viene associato lo iuv creato mediante partita debitoria alla posizione {int}")
    public void destinatarioAddIuvGPD(Integer posizione) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        String iuvGPD = getIuvGPD(posizione);
        notificationStepsInterface.setIuvToRecipient(posizione, iuvGPD);
    }

    /*
    Invio massivo di notifiche irreperibili utili per i test radd
    TODO: migliorare e rendere di utilità generale
     */
    @Given("vengono inviate {int} notifiche per l'utente Signor casuale con il {string} e si aspetta fino allo stato COMPLETELY_UNREACHABLE")
    public void sendNotificationForUserSignorCasualeAndWaitUntilCompletelyUnreacheable(int numberOfNotification, String pa) {
        List<NewNotificationRequestV24> notificationRequests = new LinkedList<>();
        String generatedFiscalCode = generateCF(System.currentTimeMillis());
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
                            "signor RaddCasuale",
                            generatedFiscalCode,
                            NotificationRecipientV23.RecipientTypeEnum.PF,
                            null
                    ),
                    notificationRecipientMap);


            this.notificationRequest = newNotificationRequest;
            setPaAndSenderTaxId(pa, null);
            notificationRequests.add(newNotificationRequest);
        }

        List<Thread> threadList = new LinkedList<>();
        ConcurrentLinkedQueue<FullSentNotificationV26> sentNotifications = new ConcurrentLinkedQueue<>();

        for (NewNotificationRequestV24 notification : notificationRequests) {
            Thread t = new Thread(() -> {
                //INVIO NOTIFICA ED ATTESA ACCEPTED
                NewNotificationResponse internalNotificationResponse = Assertions.assertDoesNotThrow(() -> b2bUtils.uploadNotificationV24(notification));
                threadWait(getWait());
                FullSentNotificationV26 fullSentNotificationV26 = b2bUtils.waitForRequestAcceptationV26(internalNotificationResponse);
                Assertions.assertNotNull(fullSentNotificationV26);

                //ATTESA ELEMENTO DI TIMELINE
                TimelineElementV26 timelineElement = null;
                for (int i = 0; i < 33; i++) {
                    threadWait(getWorkFlowWait());
                    fullSentNotificationV26 = b2bClient.getSentNotification(fullSentNotificationV26.getIun());
                    log.info("NOTIFICATION_TIMELINE: " + fullSentNotificationV26.getTimeline());
                    timelineElement = fullSentNotificationV26.getTimeline().stream().filter(
                            elem -> Objects.requireNonNull(elem.getCategory().getValue())
                                    .equals(TimelineElementCategoryV23.COMPLETELY_UNREACHABLE.getValue())).findAny().orElse(null);
                    if (timelineElement != null) {
                        break;
                    }
                }
                Assertions.assertNotNull(timelineElement);
                sentNotifications.add(fullSentNotificationV26);
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
        if (recipientName.trim().equalsIgnoreCase("mario cucumber")) {
            updateNotificationRecipient(recipient, "Mario Cucumber", marioCucumberTaxID, null, null);
        } else if (recipientName.trim().equalsIgnoreCase("mario gherkin")) {
            updateNotificationRecipient(recipient, "Mario Gherkin", marioGherkinTaxID, null, null);
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

    //TODO MATTEO TEST
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
            setPaAndSenderTaxId(paName, notificationStepsInterface);
        }
        //TODO MATTEO: un tempo lo stato era sempre ACCEPTED, ora che è parametrico forse la pollingStrategy andrebbe desunta con qualche metodo che si basa sullo stato
        notificationStepsInterface.sendNotification(getWorkFlowWait(), status, VALIDATION_STATUS);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED per controllo GPD")
    public void laNotificaVieneInviataOkGPD(String paName) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        setPaAndSenderTaxId(paName, notificationStepsInterface);
        notificationStepsInterface.sendNotification(WAITING_GPD, NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS_ACCEPTATION_SHORT);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si controlla con check rapidi che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataOkRapidCheck(String paName) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        setPaAndSenderTaxId(paName, notificationStepsInterface);
        notificationStepsInterface.sendNotification(100, NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS_ACCEPTATION_SHORT);
    }

    @When("verifica che la notifica inviata tramite api b2b dal {string} non diventi ACCEPTED")
    public void laNotificaVieneInviataNoAccept(String paName) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        setPaAndSenderTaxId(paName, notificationStepsInterface);
        //TODO MATTEO: prima richiamava waitForRequestNoAcceptation in b2bUtils. Ma è corretto che prenda "ACCEPTED" ?
        notificationStepsInterface.sendNotification(getWorkFlowWait(), NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS_NO_ACCEPTATION);
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED e successivamente annullata")
    public void laNotificaVieneInviataOkAndCancelled(String paName) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        setPaAndSenderTaxId(paName, notificationStepsInterface);
        notificationStepsInterface.sendNotification(WAIT_EXTRA_RAPID, NOTIFICATION_STATUS_ACCEPTED, VALIDATION_STATUS);
        String iun = notificationStepsInterface.getNotificationSentIun();
        Assertions.assertDoesNotThrow(() -> {
            RequestStatus resp = Assertions.assertDoesNotThrow(() ->
                    b2bClient.notificationCancellation(iun));
            Assertions.assertNotNull(resp);
            Assertions.assertNotNull(resp.getDetails());
            Assertions.assertFalse(resp.getDetails().isEmpty());
            Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));
        });
    }

    //TODO MATTEO: creare metodo apposito nell'interfaccia
    @When("la notifica viene inviata tramite api b2b dal {string} con allegato uguale all'allegato di pagamento")
    public void laNotificaVieneInviataAllegatiUgualeAlPagamento(String paName) {
        setPaAndSenderTaxId(paName, null);
        try {
            newNotificationResponse = b2bUtils.uploadNotificationAllegatiUgualiPagamento(notificationRequest);
        } catch (HttpStatusCodeException | IOException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    @And("la notifica può essere annullata dal sistema tramite codice IUN dal comune {string}")
    public void notificationCanBeCanceledWithIunByComune(String paName) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        setPA(paName);
        String iun = notificationStepsInterface.getNotificationSentIun();
        Assertions.assertDoesNotThrow(() -> {
            RequestStatus response = b2bClient.notificationCancellation(iun);
            Assertions.assertNotNull(response);
            Assertions.assertNotNull(response.getDetails());
            Assertions.assertFalse(response.getDetails().isEmpty());
            Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(response.getDetails().get(0).getCode()));
        });
    }

    @And("la notifica non può essere annullata dal sistema tramite codice IUN dal comune {string}")
    public void notificationCanNotBeCanceledWithIunByComune(String paName) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        setPA(paName);
        String iun = notificationStepsInterface.getNotificationSentIun();
        try {
            b2bClient.notificationCancellation(iun);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }


    //TODO MATTEO RIMUOVERE
//    private void sendNotification(int wait) {
//        try {
//            Assertions.assertDoesNotThrow(() -> {
//                notificationCreationDate = OffsetDateTime.now();
//                newNotificationResponse = b2bUtils.uploadNotificationV24(notificationRequest);
//                threadWait(wait);
//                notificationResponseCompleteV26 = b2bUtils.waitForRequestAcceptationV26(newNotificationResponse);
//            });
//            threadWait(wait);
//            Assertions.assertNotNull(notificationResponseCompleteV26);
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
//    }

    //TODO MATTEO: Questi 3 metodi devono essere rimossi e inglobati dal metodo sendNotificationWithVersion
    //TODO 1 (non più usato)
//    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED")
//    public void laNotificaVieneInviataOk(String paName) {
//        setPaAndSenderTaxId(paName, null);
//        sendNotification(getWorkFlowWait());
//    }

    //TODO 2 (non più usato)
//    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED con la versione {string}")
//    public void laNotificaVieneInviataOkVersioning(String paName, String version) {
//        configureAndSendNotification(paName, version);
//    }

//    //TODO 3 (non più usato)
//    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED {string}")
//    public void laNotificaVieneInviataOkV21(String paName, String version) {
//        configureAndSendNotification(paName, version);
//    }

    @And("viene effettuato recupero stato della notifica con la V1 dal comune {string}")
    public void retrieveStateNotification(String paName) {
        setPaAndSenderTaxId(paName, getNotificationStepInterface(V1));
//        this.notificationRequestV1 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest();
        searchNotificationV1(Base64Utils.encodeToString(fullSentNotificationV26.getIun().getBytes()));
    }

    @Then("l'operazione di annullamento ha prodotto un errore con status code {string}")
    public void cancellationProducedErrorWithStatusCode(String statusCode) {
        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si annulla prima che lo stato diventi REFUSED")
    public void laNotificaVieneInviataRefusedAndCancelled(String paName) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        setPaAndSenderTaxId(paName, notificationStepsInterface);
        notificationStepsInterface.sendNotification(1000, NOTIFICATION_STATUS_NOT_REFUSED, VALIDATION_STATUS);
        //TODO MATTEO: C'ERANO UN SACCO DI METODI PRIVATI INUTILI CHE HO RIMOSSO E CONDENSATO NEL CODICE SOTTOSTANTE, RIASSUNTO DALLA RIGA SOPRA
//        int wait = 1000;
//        try {
//            Assertions.assertDoesNotThrow(() -> {
//                notificationCreationDate = OffsetDateTime.now();
//                newNotificationResponse = b2bUtils.uploadNotificationV24(notificationRequest);
//                RequestStatus resp = Assertions.assertDoesNotThrow(() ->
//                        b2bClient.notificationCancellation(new String(Base64Utils.decodeFromString(newNotificationResponse.getNotificationRequestId()))));
//                Assertions.assertNotNull(resp);
//                Assertions.assertNotNull(resp.getDetails());
//                Assertions.assertFalse(resp.getDetails().isEmpty());
//                Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));
//            });
//            boolean rifiutata = b2bUtils.waitForRequestNotRefusedV25(newNotificationResponse);
//            threadWait(wait);
//            Assertions.assertFalse(rifiutata);
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
    }

//    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED e successivamente annullata {string}")
//    public void laNotificaVieneInviataOkAndCancelledV2(String paName, String versione) {
//        setPaAndSenderTaxId(paName, getNotificationStepInterface(getNotificationVersion(versione)));
//        sendNotificationAndCancelV2();
//    }
//
//    private void sendNotificationAndCancelV2() {
//        sendNotificationV2();
//        Assertions.assertDoesNotThrow(() -> {
//            RequestStatus resp = Assertions.assertDoesNotThrow(() ->
//                    b2bClient.notificationCancellation(notificationResponseCompleteV20.getIun()));
//            Assertions.assertNotNull(resp);
//            Assertions.assertNotNull(resp.getDetails());
//            Assertions.assertFalse(resp.getDetails().isEmpty());
//            Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));
//        });
//    }
//
//    private void sendNotificationV2() {
//        try {
//            Assertions.assertDoesNotThrow(() -> {
//                notificationCreationDate = OffsetDateTime.now();
//                newNotificationResponseV2 = b2bUtils.uploadNotificationV2(notificationRequestV2);
//                threadWait(getWorkFlowWait());
//                notificationResponseCompleteV20 = b2bUtils.waitForRequestAcceptationV2(newNotificationResponseV2);
//            });
//            threadWait(getWorkFlowWait());
//            Assertions.assertNotNull(notificationResponseCompleteV20);
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    "{RequestID: " + (newNotificationResponseV2 == null ? "NULL" : newNotificationResponseV2.getNotificationRequestId()) + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
//    }


    //TODO: per test normalizzatore
    //TODO MATTEO: il metodo riceveva un parametro da scenario Outline, per quello sembra non venisse richiamato (AddressValidation.feature)
    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi HTTP_ERROR")
    public void sendNotificationHttpError(String paName) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        setPaAndSenderTaxId(paName, notificationStepsInterface);
        sendNotificationWithError(notificationStepsInterface);
        Assertions.assertNotNull(this.notificationError);
        Assertions.assertEquals(400, this.notificationError.getStatusCode().value());
    }

    @When("la notifica viene inviata tramite api b2b senza preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataSenzaPreloadAllegato(String paName) {
        setPaAndSenderTaxId(paName, null);
        sendNotificationWithErrorNotFindAllegato(false);
    }

    @When("la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataTramiteApiBBEffettuandoLaPreloadMaSenzaCaricareNessunAllegatoDalESiAttendeCheLoStatoDiventiREFUSED(String paName) {
        setPaAndSenderTaxId(paName, null);
        sendNotificationWithErrorNotFindAllegato(true);
    }

    @When("la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato json dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataTramiteApiBBEffettuandoLaPreloadMaSenzaCaricareNessunAllegatoJsonDalESiAttendeCheLoStatoDiventiREFUSED(String paName) {
        setPaAndSenderTaxId(paName, null);
        sendNotificationWithErrorNotFindAllegatoJson();
    }

    @When("la notifica viene inviata tramite api b2b con sha256 differente dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConShaDifferente(String paName) {
        setPaAndSenderTaxId(paName, null);
        sendNotificationWithErrorSha();
    }

    @When("la notifica viene inviata tramite api b2b con sha256 Json differente dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConShaJsonDifferente(String paName) {
        setPaAndSenderTaxId(paName, null);
        sendNotificationWithErrorShaJson();
    }

    @When("la notifica viene inviata tramite api b2b con estensione errata dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConEstensioneErrata(String paName) {
        setPaAndSenderTaxId(paName, null);
        sendNotificationWithWrongExtension();
    }

    @When("la notifica viene inviata tramite api b2b oversize preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoOverSize(String paName) {
        setPaAndSenderTaxId(paName, null);
        sendNotificationRefusedOverSizeAllegato();
    }

    @When("la notifica viene inviata tramite api b2b injection preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoInjection(String paName) {
        setPaAndSenderTaxId(paName, null);
        sendNotificationRefusedInjectionAllegato();
    }

    @When("la notifica viene inviata tramite api b2b over 15 preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoOver15(String paName) {
        setPaAndSenderTaxId(paName, null);
        sendNotificationRefusedOver15Allegato();
    }

    @When("la notifica viene inviata dal {string}")
    public void laNotificaVieneInviataDallaPA(String paName) {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        setPaAndSenderTaxId(paName, notificationStepsInterface);
        sendNotificationWithError(notificationStepsInterface);
    }

    @When("la notifica viene inviata tramite api b2b")
    public void laNotificaVieneInviataTramiteApiB2b() {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        sendNotificationWithError(notificationStepsInterface);
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
        setPaAndSenderTaxId(pa, null);
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
            case TAX_ID -> TAXID_NOT_VALID;
            case ADDRESS, NOT_VALID_ADDRESS -> NOT_VALID_ADDRESS;
            case INVALID_PARAMETER_MAX_ATTACHMENT -> INVALID_PARAMETER_MAX_ATTACHMENT;
            default -> throw new IllegalArgumentException();
        };
        Assertions.assertTrue(expectedErrorCode.equalsIgnoreCase(errorCode));
    }

//    private void sendNotificationExtraRapid(int wait) {
//        try {
//            Assertions.assertDoesNotThrow(() -> {
//                notificationCreationDate = OffsetDateTime.now();
//                newNotificationResponse = b2bUtils.uploadNotificationV24(notificationRequest);
//                threadSleep(wait);
//                notificationResponseCompleteV26 = b2bUtils.waitForRequestAcceptationExtraRapid(newNotificationResponse);
//            });
//            threadSleep(wait);
//            Assertions.assertNotNull(notificationResponseCompleteV26);
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
//    }

//    private void sendNotificationV1() {
//        try {
//            Assertions.assertDoesNotThrow(() -> {
//                notificationCreationDate = OffsetDateTime.now();
//                newNotificationResponseV1 = b2bUtils.uploadNotificationV1(notificationRequestV1);
//
//                threadWait(getWorkFlowWait());
//
//                notificationResponseCompleteV1 = b2bUtils.waitForRequestAcceptationV1(newNotificationResponseV1);
//            });
//
//            threadWait(getWorkFlowWait());
//            Assertions.assertNotNull(notificationResponseCompleteV1);
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    "{RequestID: " + (newNotificationResponseV1 == null ? "NULL" : newNotificationResponseV1.getNotificationRequestId()) + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
//    }
//    private void sendNotificationV21() {
//        try {
//            Assertions.assertDoesNotThrow(() -> {
//                notificationCreationDate = OffsetDateTime.now();
//                newNotificationResponseV21 = b2bUtils.uploadNotificationV21(notificationRequestV21);
//
//                threadWait(getWorkFlowWait());
//
//                notificationResponseCompleteV21 = b2bUtils.waitForRequestAcceptationV21(newNotificationResponseV21);
//            });
//
//            threadWait(getWorkFlowWait());
//
//            Assertions.assertNotNull(notificationResponseCompleteV21);
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    "{RequestID: " + (newNotificationResponseV21 == null ? "NULL" : newNotificationResponseV21.getNotificationRequestId()) + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
//    }

    private void searchNotificationV1(String requestId) {
        try {
            Assertions.assertDoesNotThrow(() -> b2bClient.getNotificationRequestStatusV1(requestId));
        } catch (AssertionFailedError assertionFailedError) {
            NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(V1);
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model_v1.NewNotificationResponse notificationResponse =
                    (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model_v1.NewNotificationResponse) notificationStepsInterface.retrieveNotificationResponse();

            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
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

    //TODO MATTEO TEST anche con le vecchie versioni
    private void setPaAndSenderTaxId(String paName, NotificationStepsInterface notificationStepsInterface) {
        if (notificationStepsInterface == null) {
            NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
            notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        }
        setPA(paName, notificationStepsInterface);
        setSenderTaxId(paName, notificationStepsInterface);
    }

    //TODO MATTEO TEST
    private void setPA(String paName, NotificationStepsInterface notificationStepsInterface) {
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
        notificationStepsInterface.setSelectedPA(paName);//TODO MATTEO, CREDO SIA INUTILE, rimuovere qua, nell'interfaccia e nei campi delle impl
    }

    //TODO MATTEO TEST
    private void setSenderTaxId(String pa, NotificationStepsInterface notificationStepsInterface) {
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

    //TODO MATTEO TEST
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

    //TODO MATTEO QUESTI METODI NON SARANNO PIU' NECESSARI. ALCUNI PROPRIO INUTILI, ALTRI RIMODERNATI. SI CHIAMANO A CATENA
    //TODO 1 --> chiama 2
//    private void configureAndSendNotification(String paName, String version) {
//        selectPaAndSenderTaxId(paName, version);
//        switch (version.toLowerCase()) {
//            case "v1" -> sendNotificationV1();
//            case "v2" -> sendNotificationV2();
//            case "v21" -> sendNotificationV21();
//        }
//    }
//    //TODO 2 --> chiama 3
//    private void selectPaAndSenderTaxId(String paName, String version) {
//        setPA(paName);
//        setSenderTaxIdFromProperties(version);
//    }
//    //TODO 3 --> chiama 4.1 e 4.2 + 5
//    private void setSenderTaxIdFromProperties(String version) {
//        switch (settedPa) {
//            case "Comune_1" -> {
//                if (version != null) {
//                    setSenderTaxIdVersioning(version);
//                    setGrupVersioning(SettableApiKey.ApiKeyType.MVP_1, version);
//                } else {
//                    this.notificationRequest.setSenderTaxId(this.senderTaxId);
//                    setGrup(SettableApiKey.ApiKeyType.MVP_1);
//                }
//                apiKeyTypeSetted = SettableApiKey.ApiKeyType.MVP_1;
//            }
//            case "Comune_2" -> {
//                if (version != null) {
//                    setSenderTaxIdVersioning(version);
//                    setGrupVersioning(SettableApiKey.ApiKeyType.MVP_2, version);
//                } else {
//                    this.notificationRequest.setSenderTaxId(this.senderTaxIdTwo);
//                    setGrup(SettableApiKey.ApiKeyType.MVP_2);
//                }
//                apiKeyTypeSetted = SettableApiKey.ApiKeyType.MVP_2;
//            }
//            case "Comune_Multi" -> {
//                if (version != null) {
//                    setSenderTaxIdVersioning(version);
//                    setGrupVersioning(SettableApiKey.ApiKeyType.GA, version);
//                } else {
//                    this.notificationRequest.setSenderTaxId(this.senderTaxIdGa);
//                    setGrup(SettableApiKey.ApiKeyType.GA);
//                }
//                apiKeyTypeSetted = SettableApiKey.ApiKeyType.GA;
//            }
//            case "Comune_Son" -> {
//                this.notificationRequest.setSenderTaxId(this.senderTaxIdSON);
//                setGrup(SettableApiKey.ApiKeyType.SON);
//                apiKeyTypeSetted = SettableApiKey.ApiKeyType.SON;
//            }
//            case "Comune_Root" -> {
//                this.notificationRequest.setSenderTaxId(this.senderTaxIdROOT);
//                setGrup(SettableApiKey.ApiKeyType.ROOT);
//                apiKeyTypeSetted = SettableApiKey.ApiKeyType.ROOT;
//            }
//        }
//    }
//    //TODO 4.1
//    private void setGrup(SettableApiKey.ApiKeyType apiKeyType) {
//        if (groupToSet && this.notificationRequest.getGroup() == null) {
//            List<HashMap<String, String>> hashMapsList = pnExternalServiceClient.paGroupInfo(apiKeyType);
//            if (hashMapsList == null || hashMapsList.isEmpty()) return;
//            String id = null;
//            for (HashMap<String, String> elem : hashMapsList) {
//                if (elem.get("status").equalsIgnoreCase("ACTIVE")) {
//                    id = elem.get("id");
//                    break;
//                }
//            }
//            if (id == null) return;
//            this.notificationRequest.setGroup(id);
//        }
//    }
//    //TODO 4.2
//    private void setGrupVersioning(SettableApiKey.ApiKeyType apiKeyType, String version) {
//        String group = null;
//        switch (version.toLowerCase()) {
//            case "v1" -> group = this.notificationRequestV1.getGroup();
//            case "v2" -> group = this.notificationRequestV2.getGroup();
//            case "v21" -> group = this.notificationRequestV21.getGroup();
//        }
//        if (groupToSet && group == null) {
//            List<HashMap<String, String>> hashMapsList = pnExternalServiceClient.paGroupInfo(apiKeyType);
//            if (hashMapsList == null || hashMapsList.isEmpty()) return;
//            String id = null;
//            for (HashMap<String, String> elem : hashMapsList) {
//                if (elem.get("status").equalsIgnoreCase("ACTIVE")) {
//                    id = elem.get("id");
//                    break;
//                }
//            }
//            if (id == null) return;
//            switch (version.toLowerCase()) {
//                case "v1" -> this.notificationRequestV1.setGroup(id);
//                case "v2" -> this.notificationRequestV2.setGroup(id);
//                case "v21" -> this.notificationRequestV21.setGroup(id);
//            }
//        }
//    }
//
//    //TODO 5 --> chiama il metodo sotto
//    private void setSenderTaxIdVersioning(String version) {
//        switch (version.toLowerCase()) {
//            case "v1" -> this.notificationRequestV1.setSenderTaxId(getSenderTaxIdFromProperties(settedPa));
//            case "v2" -> this.notificationRequestV2.setSenderTaxId(getSenderTaxIdFromProperties(settedPa));
//            case "v21" -> this.notificationRequestV21.setSenderTaxId(getSenderTaxIdFromProperties(settedPa));
//        }
//    }

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
//        this.settedPa = paName;
    }

    public void selectUser(String recipient) {
        switch (recipient.trim()) {
            case MARIOCUCUMBER, ETTOREFIERAMOSCA -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);

            }
            case MARIOGHERKIN, CRISTOFOROCOLOMBO -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
            }
            case GHERKINSRL -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            }
            case CUCUMBERSPA, LUCIOANNEOSENECA -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            }
            case ALDAMERINI -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_3);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_3);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.PG_3);
            }
            case LEONARDODAVINCI -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
            }
            case DINOSAURO -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
            }
            //TODO MATTEO: occhio, qua era previsto toLowerCase()
            case "mario cucumber con credenziali non valide" -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
            }
            case GALILEOGALILEI -> {
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
                iPnTosPrivacyClientImpl.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
            }
            default -> throw new IllegalArgumentException();
        }
    }

//    public String getGherkinIrreperibileTaxId() {
//        return gherkinIrreperibileTaxID;
//    }

    public void throwAssertFailerWithIUN(AssertionFailedError assertionFailedError) {
        String message = decorateErrorMsg(assertionFailedError.getMessage());
        throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
    }

    public void throwAssertFailerWithIUN(AssertionError assertionError) {
        String message = decorateErrorMsg(assertionError.getMessage());
        throw new AssertionError(message, assertionError.getCause());
    }

    private String decorateErrorMsg(String originalMessage) {
        return originalMessage +
                " {IUN: " + Optional.ofNullable(getIunVersionamento())
                .orElse("not found") + " }";
    }

    public void throwAssertFailerWithAmountGDPAndIUN(AssertionFailedError assertionFailedError, Integer amountGDP) {
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
        if (timingConfigs.getSchedulingDaysSuccessDigitalRefinement() == null)
            return schedulingDaysSuccessDigitalRefinementDefault;
        return timingConfigs.getSchedulingDaysSuccessDigitalRefinement();
    }

    public Duration getSchedulingDaysFailureDigitalRefinement() {
        if (timingConfigs.getSchedulingDaysFailureDigitalRefinement() == null)
            return schedulingDaysFailureDigitalRefinementDefault;
        return timingConfigs.getSchedulingDaysFailureDigitalRefinement();
    }

    public Duration getSchedulingDaysSuccessAnalogRefinement() {
        if (timingConfigs.getSchedulingDaysSuccessAnalogRefinement() == null)
            return schedulingDaysSuccessAnalogRefinementDefault;
        return timingConfigs.getSchedulingDaysSuccessAnalogRefinement();
    }

    public Duration getSchedulingDaysFailureAnalogRefinement() {
        if (timingConfigs.getSchedulingDaysFailureAnalogRefinement() == null)
            return schedulingDaysFailureAnalogRefinementDefault;
        return timingConfigs.getSchedulingDaysFailureAnalogRefinement();
    }

    public Duration getTimeToAddInNonVisibilityTimeCase() {
        if (timingConfigs.getNonVisibilityTime() == null) return timeToAddInNonVisibilityTimeCaseDefault;
        return timingConfigs.getNonVisibilityTime();
    }

    public Duration getSecondNotificationWorkflowWaitingTime() {
        if (timingConfigs.getSecondNotificationWorkflowWaitingTime() == null)
            return secondNotificationWorkflowWaitingTimeDefault;
        return timingConfigs.getSecondNotificationWorkflowWaitingTime();
    }

    public Duration getWaitingForReadCourtesyMessage() {
        if (timingConfigs.getWaitingForReadCourtesyMessage() == null) return waitingForReadCourtesyMessageDefault;
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

    //TODO MATTEO IMPORTANTISSIMO
    public String getIunVersionamento() {
        NotificationVersion notificationVersion = versionUsed == null ? getNotificationVersion(MOST_RECENT) : versionUsed;
        NotificationStepsInterface notificationStepsInterface = getNotificationStepInterface(notificationVersion);
        return notificationStepsInterface.getNotificationSentIun();
//        if (getSentNotificationV1() != null) {
//            return getSentNotificationV1().getIun();
//        } else if (getSentNotificationV2() != null) {
//            return getSentNotificationV2().getIun();
//        } else if (getSentNotificationV21() != null) {
//            return getSentNotificationV21().getIun();
//        } else if (getSentNotification() != null) {
//            return getSentNotification().getIun();
//        } else if (getSentNotificationV25() != null) {
//            return getSentNotificationV25().getIun();
//        } else if (getSentNotificationV23() != null) {
//            return getSentNotificationV23().getIun();
//        } else {
//            return null;
//        }
    }

    public List<String> getDatiPagamentoVersionamento(Integer destinatario, Integer pagamento) {
        List<String> DatiPagamento = new ArrayList<>();
        if (getFullSentNotificationV1() != null) {
            DatiPagamento.add(Objects.requireNonNull(getFullSentNotificationV1().getRecipients().get(destinatario).getPayment()).getCreditorTaxId());
            DatiPagamento.add(Objects.requireNonNull(getFullSentNotificationV1().getRecipients().get(destinatario).getPayment()).getNoticeCode());
        } else if (getFullSentNotificationV20() != null) {
            DatiPagamento.add(Objects.requireNonNull(getFullSentNotificationV20().getRecipients().get(destinatario).getPayment()).getCreditorTaxId());
            DatiPagamento.add(Objects.requireNonNull(getFullSentNotificationV20().getRecipients().get(destinatario).getPayment()).getNoticeCode());
        } else if (getFullSentNotificationV21() != null) {
            DatiPagamento.add(Objects.requireNonNull(Objects.requireNonNull(getFullSentNotificationV21().getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getCreditorTaxId());
            DatiPagamento.add(Objects.requireNonNull(Objects.requireNonNull(getFullSentNotificationV21().getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getNoticeCode());
        } else if (getFullSentNotificationV26() != null) {
            DatiPagamento.add(Objects.requireNonNull(Objects.requireNonNull(getFullSentNotificationV26().getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getCreditorTaxId());
            DatiPagamento.add(Objects.requireNonNull(Objects.requireNonNull(getFullSentNotificationV26().getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getNoticeCode());
        }
        return DatiPagamento;
    }

    public static void threadWait(int wait) {
        try {
            await().atMost(wait, TimeUnit.MILLISECONDS);
        } catch (RuntimeException exception) {
            log.error("await error exeption");
            throw exception;
        }
    }

    //TODO MATTEO: usato solo da SendNotificationExtraRapid? Ci sono differenze col metodo sopra?
    // Se no, cancellarlo e sostituirlo con threadWait
    public static void threadSleep(int wait) {
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            log.error("Thread.sleep error retry");
            throw new RuntimeException(e);
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
        if (version.equalsIgnoreCase("V26") || version.equalsIgnoreCase("V27")) {
            Assertions.assertNotNull(fullSentNotificationV26);
            TimelineElementV26 timelineElementWithTargetCategory = fullSentNotificationV26.getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertNotEquals(x.getCategory(), legalFactCategory));
        } else if (version.equalsIgnoreCase("V25")) {
            Assertions.assertNotNull(fullSentNotificationV25);
            TimelineElementV25 timelineElementWithTargetCategory = fullSentNotificationV25.getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertNotEquals(x.getCategory(), legalFactCategory));
        } else if (version.equalsIgnoreCase("V24")) {
            Assertions.assertNotNull(fullSentNotificationV24);
            TimelineElementV24 timelineElementWithTargetCategory = fullSentNotificationV24.getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertNotEquals(x.getCategory().getValue(), legalFactCategory));
        } else if (version.equalsIgnoreCase("V23")) {
            Assertions.assertNotNull(fullSentNotificationV23);
            TimelineElementV23 timelineElementWithTargetCategory = fullSentNotificationV23.getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertNotEquals(x.getCategory().getValue(), legalFactCategory));
        }
    }
}