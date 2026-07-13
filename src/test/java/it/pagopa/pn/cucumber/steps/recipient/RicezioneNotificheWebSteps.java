package it.pagopa.pn.cucumber.steps.recipient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchRow;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffLegalNotificationSearchRow;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffLegalNotificationsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffDocumentType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffNotificationDetailDocument;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffNotificationDetailTimeline;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.NotificationStatusV26;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffConsent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyActionBody;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.ConsentType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.selfcare.privateapi.FilteredPaIdsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.userattributesb2b.model.CxLanguage;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.AddressVerification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.CourtesyDigitalAddress;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.LegalChannelType;
import it.pagopa.pn.client.b2b.pa.mapper.NotificationSearchParamMapper;
import it.pagopa.pn.client.b2b.pa.provider.SenderInfoProvider;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPnBFFRecipientNotificationClient;
import it.pagopa.pn.client.b2b.pa.service.IPnTosPrivacyClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebUserAttributesClient;
import it.pagopa.pn.client.b2b.pa.service.impl.AooUoIdsClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BSenderReadClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BUserAttributesExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebUserAttributesInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.b2b.pa.wrapper.BundleFullReceivedNotification;
import it.pagopa.pn.client.b2b.pa.wrapper.LegalCourtesyAddressWrapper;
import it.pagopa.pn.client.web.generated.openapi.clients.informal.web.pa.model.InformalNotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.LegalNotificationSearchRow;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.utils.DataTest;
import it.pagopa.pn.cucumber.utils.notificationsearch.NotificationSearchCriteriaMapper;
import it.pagopa.pn.cucumber.utils.notificationsearch.NotificationSearchRowAssertions;
import it.pagopa.pn.cucumber.utils.token.TokenResolver;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.AAR_GENERATION;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.ALDA_MERINI;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_1;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_2;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_MULTI;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_ROOT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_SON;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_SON_2;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.CRISTOFORO_COLOMBO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.CUCUMBER_SPA;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.DINO_SAURO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.ETTORE_FIERAMOSCA;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.GALILEO_GALILEI;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.GHERKIN_SRL;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.LEONARDO_DA_VINCI;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.LUCIO_ANNEO_SENECA;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_CREDENZIALI_SCADUTE;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_CUCUMBER;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_GHERKIN;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.REFINEMENT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.SCHEDULE_REFINEMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;

@Slf4j
public class RicezioneNotificheWebSteps {
    private final ApplicationContext context;
    private IPnWebRecipientClient webRecipientClient;
    private IPnWebUserAttributesClient iPnWebUserAttributesClient;
    private AooUoIdsClientImpl aooUoIdsClient;
    private final PnExternalServiceClientImpl externalClient;
    private final SharedSteps sharedSteps;
    private final SendSharedContext sendSharedContext;
    private final IPnWebPaClient webPaClient;
    private final IPnBFFRecipientNotificationClient bffRecipientNotificationClient;
    private final B2BSenderReadClientImpl b2BSenderReadClient;
    private final IPnTosPrivacyClient iPnTosPrivacyClient;
    private final PnB2bClientTimingConfigs timingConfigs;
    private static final Integer WAIT_DEFAULT = 10000;
    private HttpStatusCodeException notificationError;
    private BundleFullReceivedNotification fullReceivedNotification;
    private BffFullNotificationV1 bffFullNotificationV1Recipient;
    private OtpCodeService otpCodeService;
    private final NotificationSearchParamMapper notificationSearchParamMapper;
    private final NotificationSearchCriteriaMapper notificationSearchCriteriaMapper;
    private final SenderInfoProvider senderInfoProvider;


    private final DynamoDbService dynamoDbService;
    private it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1 bffFullNotificationV1Sender;

    private static final String TOS_VERSION = "8";
    private static final String ACCEPT_TOS = "ACCETTA";
    private FilteredPaIdsResponse filteredPaIdsResponse;

    @Value("${pn.external.senderId}")
    private String senderId;
    @Value("${pn.external.senderId-2}")
    private String senderId2;
    @Value("${pn.external.senderId-GA}")
    private String senderIdGA;
    @Value("${pn.external.senderId-SON}")
    private String senderIdSON;
    @Value("${pn.external.senderId-SON-2}")
    private String senderIdSON2;
    @Value("${pn.external.senderId-ROOT}")
    private String senderIdROOT;

    @Before("@useB2B")
    public void beforeMethod() {
        this.iPnWebUserAttributesClient = context.getBean(B2BUserAttributesExternalClientImpl.class);
        if (!(webRecipientClient instanceof B2BRecipientExternalClientImpl)) {
            this.webRecipientClient = context.getBean(B2BRecipientExternalClientImpl.class);
            sharedSteps.setWebRecipientClient(webRecipientClient);
        }
    }

    @After("@cleanAddressBook")
    public void afterAndressBook() {
        cleanLegalAddressForUser();
        log.info("clean digital address from @After");
    }

    @After("@sercqF2")
    public void afterSercQ() {
        cleanLegalAddressForUser();
        log.info("clean digital address from @After");
    }

    @Autowired
    public RicezioneNotificheWebSteps(ApplicationContext context, SharedSteps sharedSteps, PnWebUserAttributesInternalClientImpl iPnWebUserAttributesClient,
                                      IPnBFFRecipientNotificationClient bffRecipientNotificationClient, IPnTosPrivacyClient iPnTosPrivacyClient, PnB2bClientTimingConfigs timingConfigs, DynamoDbService dynamoDbService,
                                      OtpCodeService otpCodeService, AooUoIdsClientImpl aooUoIdsClient, NotificationSearchParamMapper notificationSearchParamMapper,
                                      B2BSenderReadClientImpl b2BSenderReadClient, NotificationSearchCriteriaMapper notificationSearchCriteriaMapper,
                                      SenderInfoProvider senderInfoProvider, SendSharedContext sendSharedContext) {
        this.context = context;
        this.sharedSteps = sharedSteps;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.iPnWebUserAttributesClient = iPnWebUserAttributesClient;
        this.webPaClient = sharedSteps.getWebPaClient();
        this.externalClient = sharedSteps.getPnExternalServiceClient();
        this.bffRecipientNotificationClient = bffRecipientNotificationClient;
        this.iPnTosPrivacyClient = sharedSteps.getIPnTosPrivacyClientImpl();
        this.timingConfigs = timingConfigs;
        this.aooUoIdsClient = aooUoIdsClient;
        this.dynamoDbService = dynamoDbService;
        this.otpCodeService = otpCodeService;
        this.notificationSearchParamMapper = notificationSearchParamMapper;
        this.b2BSenderReadClient = b2BSenderReadClient;
        this.notificationSearchCriteriaMapper = notificationSearchCriteriaMapper;
        this.senderInfoProvider = senderInfoProvider;
        this.sendSharedContext = sendSharedContext;
    }

    @Then("la notifica può essere correttamente recuperata da {string}")
    public void recipientReadNotification(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            fullReceivedNotification = webRecipientClient.getFullReceivedNotification(sharedSteps.getNotificationIun(), null);
            log.info("timeline received: " + fullReceivedNotification.getTimeline());
        });
    }

    @And("{string} tenta il recupero della notifica")
    public void notificationCanBeCorrectlyReadBy(String recipient) {
        sharedSteps.selectUser(recipient);
        try {
            webRecipientClient.getFullReceivedNotification(sharedSteps.getNotificationIun(), null);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    @And("lato destinatario vengono letti i dettagli della notifica lato web dal destinatario {string}")
    public void latoDestinatarioVengonoLettiIDettagliDellaNotificaLatoWeb(String user) {
        selectUser(user);
        bffFullNotificationV1Recipient =
                Assertions.assertDoesNotThrow(() ->
                        bffRecipientNotificationClient.getReceivedNotificationV1WithHttpInfoForRecipient(sharedSteps.getNotificationIun())
                                .getBody());
        Assertions.assertNotNull(bffFullNotificationV1Recipient);
        log.info("FULL TIMELINE RECIPIENT: " + bffFullNotificationV1Recipient.getTimeline());
    }

    @And("lato destinatario viene recuperato AAR lato web dal destinatario {string}")
    public void retrieveNotificationAAROnRecipientSideForWeb(String user) {
        Assertions.assertNotNull(bffFullNotificationV1Recipient);
        selectUser(user);
        String documentId = Optional.ofNullable(bffFullNotificationV1Recipient.getOtherDocuments()).orElse(List.of()).stream()
                .filter(x -> x != null && x.getDocumentType().equals("AAR"))
                .map(BffNotificationDetailDocument::getDocumentId)
                .findFirst()
                .orElse(null);
        Assertions.assertDoesNotThrow(() ->
                bffRecipientNotificationClient.getReceivedNotificationDocumentV1(sharedSteps.getNotificationIun(), BffDocumentType.AAR, null, null, documentId));
    }

    @And("lato destinatario è possibile recuperare correttamente l'allegato {string} dal destinatario {string}")
    public void retrieveAttachmentOnRecipientSideForWeb(String attachmentName, String user) {
        Assertions.assertNotNull(bffFullNotificationV1Recipient);
        selectUser(user);
        Assertions.assertDoesNotThrow(() ->
                bffRecipientNotificationClient.getReceivedNotificationPaymentV1WithHttpInfo(sharedSteps.getNotificationIun(), attachmentName, null, 0));
    }


    @And("lato api l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è visibile")
    public void timelineEventWithCategoryAndDeliveryDetailCodeNotPresent(String category, String deliveryDetailCode) {
        Assertions.assertNull(getTimelineElement(category, deliveryDetailCode));
    }

    @And("lato api l'elemento di timeline della notifica {string} con deliveryDetailCode {string} è visibile")
    public void timelineEventWithCategoryAndDeliveryDetailCodePresent(String category, String deliveryDetailCode) {
        Assertions.assertNotNull(getTimelineElement(category, deliveryDetailCode));
    }

    private TimelineElementV28 getTimelineElement(String category, String deliveryDetailCode) {
        fullReceivedNotification.getTimeline().forEach(x -> log.info(x.toString()));
        return fullReceivedNotification.getTimeline().stream()
                .filter(x -> x.getCategory().getValue().equals(category) &&
                        x.getDetails() != null &&
                        x.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                .findFirst().orElse(null);
    }

    @And("lato api l'elemento di timeline della notifica {string} {is} visibile")
    public void timelineEventWithCategoryAndDeliveryDetailCodeNotPresent(String category, boolean isVisibile) {
        TimelineElementV28 timelineElement = getTimelineElement(category, null);
        if (isVisibile) {
            assertThat(timelineElement).as("Il timeline element " + category + " dovrebbe risultare visibile al destinatario").isNotNull();
        } else {
            assertThat(timelineElement).as("Il timeline element " + category + " non dovrebbe risultare visibile al destinatario").isNull();
        }
    }

    @And("lato destinatario dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} è visibile")
    public void latoDestinatarioDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeÈVisibile(String category, String deliveryDetailCode) {
        Optional<BffNotificationDetailTimeline> dato = getNotificationDetailTimeline(category, deliveryDetailCode);
        Assertions.assertFalse(dato.isEmpty());
        Assertions.assertFalse(dato.get().getHidden());
    }

    @And("lato destinatario dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è visibile")
    public void latoDestinatarioDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeNonÈVisibile(String category, String deliveryDetailCode) {
        Optional<BffNotificationDetailTimeline> dato = getNotificationDetailTimeline(category, deliveryDetailCode);
        Assertions.assertFalse(dato.isEmpty());
        Assertions.assertTrue(dato.get().getHidden());
    }

    @And("lato destinatario dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è presente")
    public void latoDestinatarioDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeNonÈPresente(String category, String deliveryDetailCode) {
        Optional<BffNotificationDetailTimeline> dato = getNotificationDetailTimeline(category, deliveryDetailCode);
        Assertions.assertTrue(dato.isEmpty());
    }

    private Optional<BffNotificationDetailTimeline> getNotificationDetailTimeline(String category, String deliveryDetailCode) {
        return bffFullNotificationV1Recipient
                .getTimeline()
                .stream()
                .filter(Objects::nonNull)
                .filter(data ->
                        data.getElementId().contains(category)
                                && data.getDetails().getDeliveryDetailCode() != null
                                && data.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                .findFirst();
    }

    private LegalNotificationSearchResponse notificationSearchResponse;
    private InformalNotificationSearchResponse informalNotificationSearchResponse;

    @And("vengono recuperate le notifiche inviate dal mittente {string}")
    public void vengonoRecuperateLeNotificheInviateDalMittente(String sender, @Transpose NotificationSearchParam searchParam) {
        selectPa(sender);
        try {
        notificationSearchResponse = b2BSenderReadClient.searchSentNotification(searchParam);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
        }
    }

    @And("vengono recuperate le notifiche bonarie inviate dal mittente {string}")
    public void vengonoRecuperateLeNotificheBonarieInviateDalMittente(String sender, @Transpose NotificationSearchParam searchParam) {
        try {
            informalNotificationSearchResponse = b2BSenderReadClient.searchInformalSentNotification(searchParam);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
        }
    }

    @And("lato mittente vengono letti i dettagli della notifica lato web {string}")
    public void latoMittenteVengonoLettiIDettagliDellaNotificaLatoWebDalDestinatario(String sender) {
        selectPa(sender);
        bffFullNotificationV1Sender = Assertions.assertDoesNotThrow(() ->
                bffRecipientNotificationClient.getSentNotificationV1WithHttpInfoForSender(sharedSteps.getNotificationIun())
                        .getBody());
        Assertions.assertNotNull(bffFullNotificationV1Sender);
        log.info("FULL TIMELINE SENDER: " + bffFullNotificationV1Sender.getTimeline());
    }

    @And("lato mittente dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} è visibile")
    public void latoMittenteDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeÈVisibile(String category, String deliveryDetailCode) {
        Optional<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationDetailTimeline> dato = getNotificationDetailTimelineSender(category, deliveryDetailCode);
        Assertions.assertFalse(dato.isEmpty());
        Assertions.assertFalse(dato.get().getHidden());
    }

    @And("lato mittente dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è visibile")
    public void latoMittenteDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeNonÈVisibile(String category, String deliveryDetailCode) {
        Optional<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationDetailTimeline> dato = getNotificationDetailTimelineSender(category, deliveryDetailCode);
        Assertions.assertFalse(dato.isEmpty());
        Assertions.assertTrue(dato.get().getHidden());
    }

    @And("lato mittente da api l'elemento di timeline della notifica {string} con deliveryDetailCode {string} è visibile")
    public void latoMittenteDaApiElementoDiTimeline(String category, String deliveryDetailCode) {
        Optional<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28> timelineElement = sharedSteps.getSentNotificationLastVersion()
                .getTimeline()
                .stream()
                .filter(Objects::nonNull)
                .filter(data ->
                        data.getElementId().contains(category) && data.getDetails() != null &&
                                data.getDetails().getDeliveryDetailCode() != null &&
                                data.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                .findFirst();
        Assertions.assertTrue(timelineElement.isPresent(), "The searched category is not present in the timeline!");
    }

    @And("lato mittente dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è presente")
    public void latoMittenteDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeNonÈPresente(String category, String deliveryDetailCode) {
        Optional<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationDetailTimeline> dato = getNotificationDetailTimelineSender(category, deliveryDetailCode);
        Assertions.assertTrue(dato.isEmpty());
    }

    private Optional<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationDetailTimeline> getNotificationDetailTimelineSender(String category, String deliveryDetailCode) {
        return bffFullNotificationV1Sender
                .getTimeline()
                .stream()
                .filter(Objects::nonNull)
                .filter(data ->
                        data.getElementId().contains(category)
                                && data.getDetails().getDeliveryDetailCode() != null
                                && data.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                .findFirst();
    }

    @Then("la notifica non può essere correttamente recuperata da {string}")
    public void notificationCanNotBeCorrectlyReadby(String recipient) {
        sharedSteps.selectUser(recipient);
        fullReceivedNotification = webRecipientClient.getFullReceivedNotification(sharedSteps.getNotificationIun(), null);
        Assertions.assertNull(fullReceivedNotification);
    }

    @Then("il documento notificato può essere correttamente recuperato da {string}")
    public void theDocumentCanBeProperlyRetrievedBy(String recipient) {
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = getReceivedNotificationDocument();
        if (downloadResponse.getSha256() != null) {
            AtomicReference<String> sha256 = new AtomicReference<>("");
            Assertions.assertDoesNotThrow(() -> {
                byte[] bytes = Assertions.assertDoesNotThrow(() ->
                        B2bUtils.downloadFile(downloadResponse.getUrl()));
                sha256.set(B2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
            });
            Assertions.assertEquals(sha256.get(), downloadResponse.getSha256());
        } else {
            Assertions.assertNotNull(downloadResponse.getFilename());
            Assertions.assertNotNull(downloadResponse.getContentLength());
            Assertions.assertNotNull(downloadResponse.getUrl());
        }
    }

    @Then("il documento notificato non può essere correttamente recuperato da {string}")
    public void theDocumentCanNotBeProperlyRetrievedBy(String recipient) {
        try {
            sharedSteps.selectUser(recipient);
            getReceivedNotificationDocument();
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    private NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument() {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        return webRecipientClient.getReceivedNotificationDocument(
                fullSentNotification.getIun(),
                Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(fullSentNotification).getDocuments()).get(0).getDocIdx())),
                null
        );
    }

    @Then("l'utente {string} controlla che la data di refinement sia corretta")
    public void theNotificationDateOfRefinementIsCorrectFromUser(String recipient) {
        sharedSteps.selectUser(recipient);

        String iun = sharedSteps.getNotificationIun();

        try {
            String scheduleDate = Objects.requireNonNull(webRecipientClient.getFullReceivedNotification(iun, null).getTimeline().stream().filter(
                    elem -> Objects.requireNonNull(elem.getCategory().getValue()).equals(SCHEDULE_REFINEMENT)).findAny().get().getDetails()).getSchedulingDate();
            String refinementDate = webRecipientClient.getFullReceivedNotification(iun, null).getTimeline().stream().filter(
                    elem -> Objects.requireNonNull(elem.getCategory().getValue()).equals(REFINEMENT)).findAny().get().getTimestamp();
            log.info("scheduleDate : {}", scheduleDate);
            log.info("refinementDate : {}", refinementDate);
            assertThat(refinementDate).as("La data di refinement non coincide con la scheduleDate").isEqualTo(scheduleDate);

        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("l'allegato {string} può essere correttamente recuperato da {string}")
    public void attachmentCanBeCorrectlyRetrievedBy(String attachmentName, String recipient) {
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = getReceivedNotificationAttachment(attachmentName);

        if (downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0) {
            try {
                await().atMost(downloadResponse.getRetryAfter() * 3L, TimeUnit.MILLISECONDS);
                downloadResponse = getReceivedNotificationAttachment(attachmentName);
            } catch (RuntimeException exc) {
                log.error("Await error exception: {}", exc.getMessage());
                throw exc;
            }
        }

        if (!"F24".equalsIgnoreCase(attachmentName) && downloadResponse.getSha256() != null) {
            AtomicReference<String> sha256 = new AtomicReference<>("");
            NotificationAttachmentDownloadMetadataResponse finalDownloadResponse = downloadResponse;
            Assertions.assertDoesNotThrow(() -> {
                byte[] bytes = Assertions.assertDoesNotThrow(() ->
                        B2bUtils.downloadFile(Objects.requireNonNull(finalDownloadResponse).getUrl()));
                sha256.set(B2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
            });
            Assertions.assertEquals(sha256.get(), Objects.requireNonNull(downloadResponse).getSha256());
        } else {
            NotificationAttachmentDownloadMetadataResponse finalDownloadResponse = downloadResponse;
            Assertions.assertDoesNotThrow(() ->
                    B2bUtils.downloadFile(Objects.requireNonNull(finalDownloadResponse).getUrl()));
        }
    }

    private NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String attachmentName) {

        return webRecipientClient.getReceivedNotificationAttachment(
                sharedSteps.getNotificationIun(),
                attachmentName,
                null, 0);
    }

    @And("{string} tenta il recupero dell'allegato {string}")
    public void attachmentRetrievedError(String recipient, String attachmentName) {
        this.notificationError = null;
        sharedSteps.selectUser(recipient);
        retrieveAttachment(attachmentName);
    }

    @And("{string} tenta il recupero dell'attestazione {string}")
    public void attachmentAttestazioneRetrievedError(String recipient, String attachmentName) {
        this.notificationError = null;
        sharedSteps.selectUser(recipient);
        retrieveAttachment(attachmentName);
    }

    private void retrieveAttachment(String attachmentName) {
        try {
            NotificationAttachmentDownloadMetadataResponse downloadResponse = getReceivedNotificationAttachment(attachmentName);

            if (downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0) {
                try {
                    await().atMost(downloadResponse.getRetryAfter() * 3L, TimeUnit.MILLISECONDS);
                    getReceivedNotificationAttachment(attachmentName);
                } catch (RuntimeException exc) {
                    log.error("Await error exception: {}", exc.getMessage());
                    throw exc;
                }
            }
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    @Then("(il download)(il recupero) ha prodotto un errore con status code {string}")
    public void operationProducedErrorWithStatusCode(String statusCode) {
        try {
            assertSoftly(softly -> {
                assertThat(notificationError)
                        .as("L'operazione non ha prodotto l'errore atteso")
                        .isNotNull();
                assertThat(notificationError.getStatusCode().toString().substring(0, 3))
                        .as("Il codice di errore non coincide con quanto atteso")
                        .isEqualTo(statusCode);
            });
        } catch (AssertionFailedError e) {
            sharedSteps.throwAssertionErrorWithIUN(e);
        }
    }

    @Then("(il download)(il recupero) non ha prodotto errori")
    public void operationProducedErrorWithStatusCode() {
        try {
            assertThat(notificationError)
                    .as("L'operazione non dovrebbe aver prodotto errori")
                    .isNull();
            //TODO: se abbiamo appurato che è null la consume è del tutto inutile
            Assertions.assertNull(sharedSteps.consumeNotificationError());
        } catch (AssertionFailedError e) {
            sharedSteps.throwAssertionErrorWithIUN(e);
        }
    }

    @Then("si verifica che sia stato restituito un errore di tipo {string}")
    public void verifyApiErrorType(String errorType) {
            assertThat(notificationError.getStatusCode().getReasonPhrase().toUpperCase())
                    .as("Il tipo di errore non coincide con quanto atteso")
                    .contains(errorType.toUpperCase());
    }

    @And("download attestazione opponibile AAR da parte {string}")
    public void downloadLegalFactIdAARByRecipient(String recipient) {
        sharedSteps.selectUser(recipient);
        this.notificationError = null;
        try {
            await().atMost(sharedSteps.getWait(), TimeUnit.MILLISECONDS);
        } catch (RuntimeException exc) {
            log.error("Await error exception: {}", exc.getMessage());
            throw exc;
        }

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28 timelineElement = null;

        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28 element : fullSentNotification.getTimeline()) {
            if (Objects.requireNonNull(element.getCategory().getValue()).equals(AAR_GENERATION)) {
                timelineElement = element;
                break;
            }
        }

        Assertions.assertNotNull(timelineElement);
        String keySearch = null;
        Objects.requireNonNull(timelineElement.getDetails());
        if (!timelineElement.getDetails().getGeneratedAarUrl().isEmpty()) {

            if (timelineElement.getDetails().getGeneratedAarUrl().contains("PN_AAR")) {
                keySearch = timelineElement.getDetails().getGeneratedAarUrl().substring(timelineElement.getDetails().getGeneratedAarUrl().indexOf("PN_AAR"));
            }

            String finalKeySearch = "safestorage://" + keySearch;
            try {
                this.webRecipientClient.getDocumentsWeb(sharedSteps.getNotificationIun(), finalKeySearch, null);
            } catch (HttpStatusCodeException e) {
                this.notificationError = e;
            }
        }
    }


    @Then("la notifica può essere correttamente recuperata con una ricerca da {string}")
    public void notificationCanBeCorrectlyReadWithResearch(String recipient, @Transpose NotificationSearchParam searchParam) {
        sharedSteps.selectUser(recipient);
        Destinatario recipientObj = sharedSteps.getDestinatarioRegistry().destinatario(recipient);
        try {
            Assertions.assertTrue(searchNotification(recipientObj, searchParam));
        } catch (HttpStatusCodeException e) {
            notificationError = e;
            log.info("Errore durante la ricerca della notifica: {}", e.getMessage());
        }
    }

    @Then("la notifica può essere correttamente recuperata con una ricerca da web PA {string}")
    public void notificationCanBeCorrectlyReadWithResearchWebPA(String paName, @Transpose NotificationSearchParamWebPA searchParam) {
        sharedSteps.setPA(paName);
        Assertions.assertTrue(searchNotificationWebPA(searchParam));
    }

    @Then("la notifica non viene recuperata con una ricerca da {string}")
    public void notificationCantBeCorrectlyReadWithResearch(String recipient, @Transpose NotificationSearchParam searchParam) {
        sharedSteps.selectUser(recipient);
        Destinatario destinatario = sharedSteps.getDestinatarioRegistry().destinatario(recipient);
        Assertions.assertFalse(searchNotification(destinatario, searchParam));
    }

//    @DataTableType
//    public NotificationSearchParam convertNotificationSearchParam(Map<String, String> data) {
//        NotificationSearchParam searchParam = new NotificationSearchParam();
//
//        B2bUtils.Pair<OffsetDateTime, OffsetDateTime> dates = getStartDateAndEndDate(data);
//
//        searchParam.startDate = dates.getValue1();
//        searchParam.endDate = dates.getValue2();
//        searchParam.subjectRegExp = data.getOrDefault("subjectRegExp", null);
//        String iun = data.getOrDefault("iunMatch", null);
//        if (data.containsKey("status")) {
//            searchParam.status = data.get("status");
//        }
//        searchParam.iunMatch = iun != null && iun.equalsIgnoreCase("ACTUAL") ? sharedSteps.getNotificationIun() : iun;
//        searchParam.size = Integer.parseInt(data.getOrDefault("size", "10"));
//        if (searchParam.size == -1) searchParam.size = null;
//        return searchParam;
//    }

    /**
     * Metodo di conversione dei parametri di ricerca delle notifiche da DataTable in oggetto NotificationSearchParam
     * @param data
     * @return
     */
    @DataTableType
    public NotificationSearchParam convertNotificationSearchParam(Map<String, String> data) {
        B2bUtils.Pair<OffsetDateTime, OffsetDateTime> dates = getStartDateAndEndDate(data);
        TokenResolver tokenResolver = new TokenResolver(sharedSteps, sendSharedContext);
        return notificationSearchParamMapper.build(data, dates.getValue1(), dates.getValue2(), sharedSteps.getNotificationIun(), tokenResolver::resolve);
    }

    @DataTableType
    public NotificationSearchParamWebPA convertNotificationSearchParamWebPA(Map<String, String> data) {
        NotificationSearchParamWebPA searchParam = new NotificationSearchParamWebPA();

        B2bUtils.Pair<OffsetDateTime, OffsetDateTime> dates = getStartDateAndEndDate(data);

        searchParam.startDate = dates.getValue1();
        searchParam.endDate = dates.getValue2();
        searchParam.subjectRegExp = data.getOrDefault("subjectRegExp", null);
        String iun = data.getOrDefault("iunMatch", null);
        searchParam.iunMatch = iun != null && iun.equalsIgnoreCase("ACTUAL") ? sharedSteps.getNotificationIun() : iun;
        searchParam.size = Integer.parseInt(data.getOrDefault("size", "10"));
        return searchParam;
    }

    private B2bUtils.Pair<OffsetDateTime, OffsetDateTime> getStartDateAndEndDate(Map<String, String> data) {
        String startDate = data.getOrDefault("startDate", LocalDate.now().minusDays(1).toString());
        String endDate = data.getOrDefault("endDate", LocalDate.now().plusDays(1).toString());
        return new B2bUtils.Pair<>(toStartOfDayUtc(startDate), toStartOfDayUtc(endDate));
    }

    private OffsetDateTime toStartOfDayUtc(String rawDate) {
        String resolvedDate = StringUtils.resolveValue(rawDate);
        if (resolvedDate == null) {
            return null;
        }
        return LocalDate.parse(resolvedDate).atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    private boolean searchNotification(Destinatario recipient, NotificationSearchParam searchParam) {
        boolean beenFound;
//        NotificationStatusV26 notificationStatus = searchParam.status != null ? NotificationStatusV26.valueOf(searchParam.status) : null;
        FullNotificationSearchResponse notificationSearchResponse = webRecipientClient.searchReceivedNotification(recipient, searchParam);
        List<FullNotificationSearchRow> resultsPage = notificationSearchResponse.getResultsPage();
        beenFound = Objects.requireNonNull(resultsPage).stream().filter(elem -> Objects.requireNonNull(elem.getIun()).equals(sharedSteps.getNotificationIun())).findAny().orElse(null) != null;
        if (!beenFound && Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
            while (Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
                List<String> nextPagesKey = notificationSearchResponse.getNextPagesKey();
                for (String pageKey : Objects.requireNonNull(nextPagesKey)) {
                    searchParam.setNextPagesKey(pageKey);
                    notificationSearchResponse = webRecipientClient.searchReceivedNotification(recipient, searchParam);
                    beenFound = resultsPage.stream().filter(elem -> Objects.requireNonNull(elem.getIun()).equals(sharedSteps.getNotificationIun())).findAny().orElse(null) != null;
                    if (beenFound) break;
                }
                if (beenFound) break;
            }
        }
        return beenFound;
    }


    private boolean searchNotificationWebPA(NotificationSearchParamWebPA searchParam) {
        boolean beenFound;
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationStatusV26 convertedStatus;
        convertedStatus = deepCopy(searchParam.status, it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationStatusV26.class);
        BffLegalNotificationsResponse notificationSearchResponse = webPaClient
                .searchSentNotification(
                        searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                        convertedStatus, searchParam.subjectRegExp,
                        searchParam.iunMatch, searchParam.size, null);
        List<BffLegalNotificationSearchRow> resultsPage = notificationSearchResponse.getResultsPage();
        beenFound = Objects.requireNonNull(resultsPage).stream().filter(elem -> Objects.requireNonNull(elem.getIun()).equals(sharedSteps.getNotificationIun())).findAny().orElse(null) != null;
        if (!beenFound && Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
            while (Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
                List<String> nextPagesKey = notificationSearchResponse.getNextPagesKey();
                for (String pageKey : Objects.requireNonNull(nextPagesKey)) {
                    notificationSearchResponse = webPaClient
                            .searchSentNotification(
                                    searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                                    convertedStatus, searchParam.subjectRegExp,
                                    searchParam.iunMatch, searchParam.size, pageKey);
                    beenFound = resultsPage.stream().filter(elem -> Objects.requireNonNull(elem.getIun()).equals(sharedSteps.getNotificationIun())).findAny().orElse(null) != null;
                    if (beenFound) break;
                }//for
                if (beenFound) break;
            }//while
        }//search cycle
        return beenFound;
    }

    @When("si predispone addressbook per l'utente {string}")
    public void siPredisponeAddressbook(String user) {
        switch (user) {
            case MARIO_CUCUMBER -> {
                this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                sharedSteps.setDestinatariList(List.of(sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_CUCUMBER));
            }
            case MARIO_GHERKIN -> {
                this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                sharedSteps.setDestinatariList(List.of(sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_GHERKIN));
            }
            case GALILEO_GALILEI -> {
                this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
                sharedSteps.setDestinatariList(List.of(sharedSteps.getDestinatarioRegistry().DESTINATARIO_GALILEO_GALILEI));
            }
            case LUCIO_ANNEO_SENECA, CUCUMBER_SPA -> {
                this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                sharedSteps.setDestinatariList(List.of(sharedSteps.getDestinatarioRegistry().DESTINATARIO_CUCUMBER_SPA));
            }
            case GHERKIN_SRL -> {
                this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                sharedSteps.setDestinatariList(List.of(sharedSteps.getDestinatarioRegistry().DESTINATARIO_GHERKIN_SRL));
            }
            case ALDA_MERINI -> {
                this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_3);
                sharedSteps.setDestinatariList(List.of(sharedSteps.getDestinatarioRegistry().DESTINATARIO_ALDA_MERINI));
            }
            case DINO_SAURO -> {
                this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
                sharedSteps.setDestinatariList(List.of(sharedSteps.getDestinatarioRegistry().DESTINATARIO_DINO_SAURO));
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @And("viene inserito un recapito legale {string} con verification code errato {string}")
    public void nuovoRecapitoLegale(String pec, String verificationCode) {
        postRecipientLegalAddressWrongCode("default", pec, verificationCode, CxLanguage.IT);
    }

    @When("viene richiesto l'inserimento della pec {string}")
    public void perLUtenteVieneSettatoLaPec(String pec) {
        postRecipientLegalAddress("default", pec, "00000", false, CxLanguage.IT);
    }

    @When("viene richiesto l'inserimento della pec {string}, e passo la lingua selezionata dal destinatario {string}")
    public void perLUtenteVieneSettatoLaPecELang(String pec, String language) {
        postRecipientLegalAddress("default", pec, "00000", true, CxLanguage.fromValue(language));
    }

    @When("viene richiesto l'inserimento del numero di telefono {string}")
    public void vieneRichiestoLInserimentoDelNumeroDiTelefono(String phone) {
        postRecipientCourtesyAddress("default", phone, LegalCourtesyAddressWrapper.ChannelType.SMS, "00000", false, CxLanguage.IT);
    }

    @When("viene richiesto l'inserimento del numero di telefono {string}, e passo la lingua selezionata dal destinatario {string}")
    public void vieneRichiestoLInserimentoDelNumeroDiTelefonoELang(String phone, String language) {
        postRecipientCourtesyAddress("default", phone, LegalCourtesyAddressWrapper.ChannelType.SMS, "00000", true, CxLanguage.fromValue(language));
    }

    @When("viene richiesto l'inserimento del email di cortesia {string}")
    public void vieneRichiestoLInserimentoDelEmailDiCortesia(String email) {
        postRecipientCourtesyAddress("default", email, LegalCourtesyAddressWrapper.ChannelType.EMAIL, "00000", false, CxLanguage.IT);
    }

    @When("viene richiesto l'inserimento del email di cortesia {string}, e passo la lingua selezionata dal destinatario {string}")
    public void vieneRichiestoLInserimentoDelEmailDiCortesiaeLang(String email, String language) {
        postRecipientCourtesyAddress("default", email, LegalCourtesyAddressWrapper.ChannelType.EMAIL, "00000", true, CxLanguage.fromValue(language));
    }

    @And("viene inserito un recapito legale {string} per il comune {string}")
    public void nuovoRecapitoLegaleDalComune(String pec, String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientLegalAddress(senderIdPa, pec, null, true, CxLanguage.IT);
    }

    @And("viene inserito un recapito legale {string} per il comune {string} con verification code errato {string}")
    public void nuovoRecapitoLegaleDalComuneConVerificationCodeErrato(String pec, String pa, String verificationCode) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientLegalAddressWrongCode(senderIdPa, pec, verificationCode, CxLanguage.IT);
    }

    @When("viene richiesto l'inserimento della pec {string} per il comune {string}")
    public void perLUtenteVieneSettatoLaPecPerIlComune(String pec, String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientLegalAddress(senderIdPa, pec, "00000", false, CxLanguage.IT);
    }

    @And("viene richiesto l'inserimento del email di cortesia {string} per il comune {string}")
    public void vieneRichiestoLInserimentoDelEmailDiCortesiaDalComune(String email, String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientCourtesyAddress(senderIdPa, email, LegalCourtesyAddressWrapper.ChannelType.EMAIL, "00000", false, CxLanguage.IT);
    }

    @When("viene invocata l'api di filtro pa di tipo Root passando le seguenti PA:")
    public void vieneInvocataLApiDiFiltroRootPassandoLeSeguentiPA(List<String> paList) {
        try {
            List<String> paIds = paList.stream()
                    .map(pa -> getSenderIdPa(pa.replace("\"", "").trim()))
                    .collect(Collectors.toList());
            this.filteredPaIdsResponse = this.aooUoIdsClient.getFilteredAooUoIdV2Private(paIds);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    @Then("si verifica che la risposta contenga gli id relativi alle seguenti PA:")
    public void siVerificaCheLaRispostaContengaGliIdRelativiAlleSeguentiPA(List<String> paList) {
        Assertions.assertNotNull(filteredPaIdsResponse);
        Assertions.assertNotNull(filteredPaIdsResponse.getIds());

        List<String> expectedIds = paList.stream()
                .map(pa -> getSenderIdPa(pa.replace("\"", "").trim()))
                .collect(Collectors.toList());

        // Verifica esclusiva: la risposta deve contenere esattamente gli id attesi
        assertThat(filteredPaIdsResponse.getIds())
                .containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Then("si verifica che la risposta non contenga id")
    public void siVerificaCheLaRispostaNonContengaId() {
        Assertions.assertTrue(
                filteredPaIdsResponse == null ||
                        filteredPaIdsResponse.getIds() == null ||
                        filteredPaIdsResponse.getIds().isEmpty()
        );
    }

    @And("viene inserita l'email di cortesia {string} per il comune {string}")
    public void vieneInseritaEmailDiCortesiaDalComune(String email, String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientCourtesyAddress(senderIdPa, email, LegalCourtesyAddressWrapper.ChannelType.EMAIL, null, true, CxLanguage.IT);
    }

    @When("viene richiesto l'inserimento del numero di telefono {string} per il comune {string}")
    public void vieneRichiestoLInserimentoDelNumeroDiTelefono(String phone, String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientCourtesyAddress(senderIdPa, phone, LegalCourtesyAddressWrapper.ChannelType.SMS, "00000", false, CxLanguage.IT);
    }

    private void postRecipientCourtesyAddress(String senderId, String addressVerification, LegalCourtesyAddressWrapper.ChannelType type, String verificationCode, boolean inserimento, CxLanguage xPagopaPnLanguageCxLanguage) {
        try {
            if (inserimento) {
                Destinatario destinatario = sharedSteps.getDestinatariList().get(0);
                // il service tiene traccia dell'ultimo OTP già restituito per questa pk+canale
                // e attende su Dynamo finché non ne compare uno nuovo dopo la POST
                this.iPnWebUserAttributesClient.postRecipientCourtesyAddress(senderId, type, (new AddressVerification().value(addressVerification)), xPagopaPnLanguageCxLanguage);
                verificationCode = otpCodeService.getNewOtp(destinatario, type);
            }
            this.iPnWebUserAttributesClient.postRecipientCourtesyAddress(senderId, type, (new AddressVerification().value(addressVerification).verificationCode(verificationCode)), xPagopaPnLanguageCxLanguage);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    private void postRecipientLegalAddress(String senderIdPa, String addressVerification, String verificationCode, boolean inserimento, CxLanguage xPagopaPnLanguage) {
        try {
            if (inserimento) {
                Destinatario destinatario = sharedSteps.getDestinatariList().get(0);
                this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalCourtesyAddressWrapper.ChannelType.PEC, (new AddressVerification().value(addressVerification)), xPagopaPnLanguage);
                verificationCode = otpCodeService.getNewOtp(destinatario, LegalCourtesyAddressWrapper.ChannelType.PEC);
            }
            this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalCourtesyAddressWrapper.ChannelType.PEC, (new AddressVerification().value(addressVerification).verificationCode(verificationCode)), xPagopaPnLanguage);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    private void postRecipientLegalAddressWrongCode(String senderIdPa, String addressVerification, String verificationCode, CxLanguage xPagopaPnLanguage) {
        String[] code = {verificationCode};
        AddressVerification verification = new AddressVerification()
                .value(addressVerification)
                .verificationCode(code[0]);

        Assertions.assertThrows(HttpStatusCodeException.class,
                () -> this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalCourtesyAddressWrapper.ChannelType.PEC, verification, xPagopaPnLanguage));
    }

    @And("viene cancellata l'email di cortesia per il comune {string}")
    public void vieneCancellataEmailDiCortesiaDalComune(String pa) {
        String senderIdPa = getSenderIdPa(pa);

        try {
            this.iPnWebUserAttributesClient.deleteRecipientCourtesyAddress(senderIdPa, LegalCourtesyAddressWrapper.ChannelType.EMAIL);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    private String getSenderIdPa(String pa) {
        return switch (pa) {
            case COMUNE_1 -> senderId;
            case COMUNE_2 -> senderId2;
            case COMUNE_MULTI -> senderIdGA;
            case COMUNE_SON -> senderIdSON;
            case COMUNE_SON_2 -> senderIdSON2;
            case COMUNE_ROOT -> senderIdROOT;
            default -> "default";
        };
    }

    @Then("l'inserimento ha prodotto un errore con status code {string}")
    public void lInserimentoHaProdottoUnErroreConStatusCode(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @Then("l'inserimento va a buon fine e NON ha prodotto un errore")
    public void lInserimentoNonHaProdottoErrore() {
        HttpStatusCodeException codeException = sharedSteps.consumeNotificationError();
        Assertions.assertNull(codeException);
    }

    @And("verifico che l'atto opponibile a terzi di {string} sia lo stesso")
    public void verificoAttoOpponibileSiaUguale(String timelineEventCategory, @Transpose DataTest dataFromTest) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28 timelineElement =
                sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        // get new timeline
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28 newTimelineElement =
                sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        // check legal fact key
        Assertions.assertEquals(Objects.requireNonNull(timelineElement.getLegalFactsIds()).size(), Objects.requireNonNull(newTimelineElement.getLegalFactsIds()).size());
        for (int i = 0; i < newTimelineElement.getLegalFactsIds().size(); i++) {
            Assertions.assertEquals(newTimelineElement.getLegalFactsIds().get(i).getKey(), timelineElement.getLegalFactsIds().get(i).getKey());
        }
    }

    @And("attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti")
    public void attendoCheGliElementiDiTimelineSEND_ANALOG_PROGRESSVenganoRicevutiTutti() {
        Integer waiting = timingConfigs.getWaitMillisForSendAnalogEvents() == null ? WAIT_DEFAULT : timingConfigs.getWaitMillisForSendAnalogEvents();
        waitState(waiting);
    }

//    public static class NotificationSearchParam {
//        OffsetDateTime startDate;
//        OffsetDateTime endDate;
//        String mandateId;
//        String senderId;
//        String status;
//        String subjectRegExp;
//        String iunMatch;
//        Integer size = 10;
//    }

    private static class NotificationSearchParamWebPA {
        OffsetDateTime startDate;
        OffsetDateTime endDate;
        String mandateId;
        NotificationStatusV26 status;
        String subjectRegExp;
        String iunMatch;
        Integer size = 10;
    }

    public void selectUser(String user) {
        switch (user.trim()) {
            case MARIO_CUCUMBER, ETTORE_FIERAMOSCA ->
                    bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.USER_1);
            case MARIO_GHERKIN, CRISTOFORO_COLOMBO ->
                    bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.USER_2);
            case GHERKIN_SRL ->
                    bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            case CUCUMBER_SPA, LUCIO_ANNEO_SENECA ->
                    bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            case LEONARDO_DA_VINCI ->
                    bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.USER_3);
            case DINO_SAURO ->
                    bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.USER_5);
            case MARIO_CREDENZIALI_SCADUTE ->
                    bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
            default -> throw new IllegalArgumentException();

        }

    }

    public void selectPa(String paName) {
        switch (paName) {
            case COMUNE_1 ->
                    this.bffRecipientNotificationClient.setSenderBearerToken(SettableBearerToken.BearerTokenType.MVP_1);
            case COMUNE_2 ->
                    this.bffRecipientNotificationClient.setSenderBearerToken(SettableBearerToken.BearerTokenType.MVP_2);
            case COMUNE_MULTI ->
                    this.bffRecipientNotificationClient.setSenderBearerToken(SettableBearerToken.BearerTokenType.GA);
            case COMUNE_SON ->
                    this.bffRecipientNotificationClient.setSenderBearerToken(SettableBearerToken.BearerTokenType.SON);
            case COMUNE_ROOT ->
                    this.bffRecipientNotificationClient.setSenderBearerToken(SettableBearerToken.BearerTokenType.ROOT);
            default -> throw new IllegalArgumentException();
        }
    }

    private static void waitState(Integer waitingStateCsv) {
        try {
            Thread.sleep(waitingStateCsv);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void postRecipientLegalAddressSercq(String senderIdPa, String address, CxLanguage xPagopaPnLanguage) {
        Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.postRecipientLegalAddress(
                senderIdPa, LegalCourtesyAddressWrapper.ChannelType.SERCQ_SEND, (new AddressVerification().value(address)), xPagopaPnLanguage));
    }

    private void postRecipientLegalAddressSercqError(String senderIdPa, String address, CxLanguage xPagopaPnLanguage) {
        Assertions.assertDoesNotThrow(() -> {
            try {
                this.iPnWebUserAttributesClient.postRecipientLegalAddress(
                        senderIdPa,
                        LegalCourtesyAddressWrapper.ChannelType.SERCQ_SEND,
                        (new AddressVerification().value(address)), xPagopaPnLanguage
                );
                log.info("Chiamata SERCQ SEND completata con successo. Grazie.");
            } catch (HttpStatusCodeException e) {
                log.error("Errore durante la chiamata SERCQ SEND", e);
                sharedSteps.setNotificationError(e);
            }
        });
    }

    @And("viene disabilitato il servizio SERCQ SEND (per la PA)(come indirizzo di)(per il comune) {string}")
    public void vieneDisabilitatoSercqPerEnte(String pa) {
        String senderId = getSenderIdPa(pa);
        Assertions.assertDoesNotThrow(() -> {
            List<LegalCourtesyAddressWrapper> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()
                    && legalAddressByRecipient.stream()
                    .anyMatch(x -> x.getSenderId().equals(senderId) && x.getChannelType() == LegalCourtesyAddressWrapper.ChannelType.SERCQ_SEND)) {
                this.iPnWebUserAttributesClient.deleteRecipientLegalAddress(senderId, LegalCourtesyAddressWrapper.ChannelType.SERCQ_SEND);
                log.info("SERCQ DISABLED");
            }
        });
    }

    @And("viene verificata l' assenza di pec inserite per l'utente")
    public void viewedPecDiPiattaformaDi() {


        List<LegalCourtesyAddressWrapper> legalAddressByRecipient = Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.getLegalAddressByRecipient());

        Assertions.assertNotNull(legalAddressByRecipient);

        boolean exists = legalAddressByRecipient.stream()
                .anyMatch(address -> LegalChannelType.PEC.getValue().equals(address.getChannelType().getValue()));
        Assertions.assertFalse(exists, "PEC IS PRESENT");

    }

    @And("viene verificata la presenza di pec inserite per il comune {string}")
    public void verifyPecIsPresentPerUserPerEnte(String pa) {
        String senderId = getSenderIdPa(pa);

        List<LegalCourtesyAddressWrapper> legalAddressByRecipient = Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.getLegalAddressByRecipient());
        boolean exists = false;
        if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
            exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> senderId.equals(address.getSenderId()));
        }
        Assertions.assertTrue(exists, "PEC NOT FOUND");
    }

    @And("viene verificato che Sercq sia {string} (per la PA)(come indirizzo di)(per il comune) {string}")
    public void viewedSercqPerEnte(String act, String pa) {
        String senderId = getSenderIdPa(pa);

        List<LegalCourtesyAddressWrapper> legalAddressByRecipient = Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.getLegalAddressByRecipient());

        boolean exists = Optional.ofNullable(legalAddressByRecipient)
                .filter(data -> !data.isEmpty())
                .map(data -> data.stream()
                        .anyMatch(address -> senderId.equals(address.getSenderId()) && address.getChannelType().equals(LegalCourtesyAddressWrapper.ChannelType.SERCQ_SEND)))
                .orElse(false);

        switch (act) {
            case "disabilitato" -> Assertions.assertFalse(exists, "Sercq risulta abilitato per il comune: " + pa);
            case "abilitato" -> Assertions.assertTrue(exists, "Sercq risulta disabilitato per il comune: " + pa);
            default ->
                    throw new IllegalArgumentException("Valore di 'act' non valido: " + act + ". I valori consentiti sono 'abilitato' o 'disabilitato'.");
        }
    }


    @And("viene verificata la presenza di Sercq attivo per l'utente {string} ")
    public void viewedSercqPerUtente(String user) {
        selectUser(user);

        List<LegalCourtesyAddressWrapper> legalAddressByRecipient = Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.getLegalAddressByRecipient());
        boolean exists = false;
        if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
            exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> address.getChannelType().getValue().contains(LegalChannelType.SERCQ.getValue()));
        }
        Assertions.assertTrue(exists, "SERCQ NOT FOUND");
    }


    @And("viene verificata l'assenza di indirizzi Pec per il comune {string}")
    public void viewedPecPerUtentePerEnte(String pa) {
        String senderId = getSenderIdPa(pa);
        Awaitility.await()
                .atMost(2, TimeUnit.MINUTES)
                .pollInterval(5, TimeUnit.SECONDS)
                .ignoreExceptions()
                .untilAsserted(() -> {
                    List<LegalCourtesyAddressWrapper> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
                    boolean exists = false;
                    if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
                        exists = legalAddressByRecipient.stream()
                                .anyMatch(address -> LegalChannelType.PEC.getValue().equals(address.getChannelType().getValue()) && senderId.equals(address.getSenderId()) && Boolean.TRUE.equals(address.getCodeValid()));
                    }
                    Assertions.assertFalse(exists, "PEC FOUND");
                });
    }

    //Come da SRS Abilitazione Domicilio Digitale, address è una stringa fissa "x-pagopa-pn-sercq:send-self:notification-already-delivered"
    @And("viene attivato il servizio SERCQ SEND per recapito principale")
    public void attivazioneSercqSend() {
        try {
            viewedSercqPerEnte("disabilitato", "default");
        } catch (AssertionFailedError failedError) {
            log.info("SERCQ già abilitato per la PA selezionata!");
            return;
        }
        postRecipientLegalAddressSercq("default", "x-pagopa-pn-sercq:send-self:notification-already-delivered", CxLanguage.IT);
    }

    @And("viene attivato il servizio SERCQ SEND per recapito {string} con errore")
    public void attivazioneSercqSendWithError(String pa) {
        postRecipientLegalAddressSercqError(pa, "x-pagopa-pn-sercq:send-self:notification-already-delivered", CxLanguage.IT);
    }

    //Come da SRS Abilitazione Domicilio Digitale, address è una stringa fissa "x-pagopa-pn-sercq:send-self:notification-already-delivered"
    @And("viene attivato il servizio SERCQ SEND (per la PA)(come indirizzo di)(per il comune) {string}")
    public void attivazioneSercqPerEnteSpecifico(String pa) {
        try {
            viewedSercqPerEnte("disabilitato", pa);
        } catch (AssertionFailedError failedError) {
            log.info("SERCQ già abilitato per la PA selezionata!");
            return;
        }
        String senderIdPa = getSenderIdPa(pa);
        postRecipientLegalAddressSercq(senderIdPa, "x-pagopa-pn-sercq:send-self:notification-already-delivered", CxLanguage.IT);
    }

    @And("viene inserito un recapito legale {string}")
    public void nuovoRecapitoLegale(String pec) {
        postRecipientLegalAddress("default", pec, null, true, CxLanguage.IT);
    }

    @And("viene controllato che siano presenti pec verificate inserite per il comune {string}")
    public void waitedAndViewedPecDiPiattaformaDi(String pa) {
        String senderId = getSenderIdPa(pa);

        Awaitility.await()
                .atMost(2, TimeUnit.MINUTES)
                .pollInterval(5, TimeUnit.SECONDS)
                .ignoreExceptions()
                .untilAsserted(() -> {
                    List<LegalCourtesyAddressWrapper> legalAddresses =
                            this.iPnWebUserAttributesClient.getLegalAddressByRecipient();

                    Assertions.assertNotNull(legalAddresses, "Lista indirizzi nulla");
                    Assertions.assertTrue(
                            legalAddresses.stream()
                                    .anyMatch(address ->
                                            LegalChannelType.PEC.getValue().equals(address.getChannelType().getValue()) &&
                                                    senderId.equals(address.getSenderId()) &&
                                                    Boolean.TRUE.equals(address.getPecValid())),
                            "PEC NOT FOUND"
                    );
                });
    }

    @And("viene rimossa se presente la pec per il comune {string}")
    public void vieneRimossaSePresenteLaPecDiPiattaformaDi(String pa) {
        String senderId = getSenderIdPa(pa);
        Assertions.assertDoesNotThrow(() -> {
            this.iPnWebUserAttributesClient.deleteRecipientLegalAddress(senderId, LegalCourtesyAddressWrapper.ChannelType.PEC);
            log.info("PEC FOUND AND DELETED");
        }, "PEC NOT FOUND");
    }

    @And("vengono rimossi eventuali recapiti presenti per l'utente")
    public void cleanLegalAddressForUser() {
        // Rimuovo tutti gli indirizzi legali presenti per l'utente
        try {
            List<LegalCourtesyAddressWrapper> legalAddresses = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            if (legalAddresses != null && !legalAddresses.isEmpty()) {
                legalAddresses.forEach(address -> {
                    try {
                        this.iPnWebUserAttributesClient.deleteRecipientLegalAddress(
                                address.getSenderId(),
                                LegalCourtesyAddressWrapper.ChannelType.valueOf(address.getChannelType().getValue())
                        );
                        log.info("Cancellato indirizzo di tipo {} per il comune {}", address.getChannelType(), address.getSenderId());
                    } catch (Exception e) {
                        log.error("Errore nella rimozione indirizzo legale tipo={} senderId={}: {}",
                                address.getChannelType(), address.getSenderId(), e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli indirizzi legali: {}", e.getMessage());
        }

        // Rimuovo tutti gli indirizzi di cortesia presenti per l'utente
        try {
            List<CourtesyDigitalAddress> courtesyDigitalAddresses = this.iPnWebUserAttributesClient.getCourtesyAddressByRecipient();
            if (courtesyDigitalAddresses != null && !courtesyDigitalAddresses.isEmpty()) {
                courtesyDigitalAddresses.forEach(address -> {
                    try {
                        this.iPnWebUserAttributesClient.deleteRecipientCourtesyAddress(
                                address.getSenderId(),
                                LegalCourtesyAddressWrapper.ChannelType.valueOf(address.getChannelType().getValue())
                        );
                        log.info("Cancellato indirizzo di cortesia di tipo {} per il comune {}", address.getChannelType(), address.getSenderId());
                    } catch (Exception e) {
                        log.error("Errore nella rimozione indirizzo di cortesia tipo={} senderId={}: {}",
                                address.getChannelType(), address.getSenderId(), e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli indirizzi di cortesia: {}", e.getMessage());
        }

        // warm-up: gli OTP eventualmente già presenti su Dynamo (residui di run/iterazioni
        // precedenti) vengono marcati come "già visti", così il primo getNewOtp dello
        // scenario attenderà un codice effettivamente nuovo dopo la POST
        Destinatario destinatario = sharedSteps.getDestinatariList().get(0);
        otpCodeService.markExistingOtpAsSeen(destinatario);
    }


    @And("Viene verificato che non sia arrivato un evento di {string}")
    public void verificaAssenzaElementoTimeline(String categoryToFind) {
        try {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            boolean isPresent = fullSentNotification.getTimeline()
                    .stream()
                    .map(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28::getCategory)
                    .filter(Objects::nonNull)
                    .map(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV28::toString)
                    .anyMatch(category -> category.equals(categoryToFind));
            assertThat(isPresent).as("La ricerca non doveva restituire nessun elemento %s", categoryToFind).isFalse();
        } catch (AssertionError ae) {
            sharedSteps.throwAssertionErrorWithIUN(ae);
        }
    }

    @Given("l'utente {string} {string} i tos per sercq")
    public void lUtenteAccettaITos(String user, String operation) {
        sharedSteps.selectUser(user);
        BffTosPrivacyActionBody.ActionEnum actionEnum = operation.equals(ACCEPT_TOS) ? BffTosPrivacyActionBody.ActionEnum.ACCEPT : BffTosPrivacyActionBody.ActionEnum.DECLINE;
        BffTosPrivacyActionBody bffTosPrivacyBody = new BffTosPrivacyActionBody().action(actionEnum).version(TOS_VERSION).type(ConsentType.TOS_SERCQ);
        Assertions.assertDoesNotThrow(() -> iPnTosPrivacyClient.acceptTosPrivacyV1(List.of(bffTosPrivacyBody)));
    }

    @Given("l'utente {string} {string} i termini di servizio di tipo: {tosConsentType}")
    public void userAcceptConsensOfType(String user, String action, ConsentType consentType) {
        sharedSteps.selectUser(user);
        BffTosPrivacyActionBody.ActionEnum actionEnum = action.equals(ACCEPT_TOS) ? BffTosPrivacyActionBody.ActionEnum.ACCEPT : BffTosPrivacyActionBody.ActionEnum.DECLINE;
        BffTosPrivacyActionBody bffTosPrivacyBody = new BffTosPrivacyActionBody().action(actionEnum).version(TOS_VERSION).type(consentType);
        Assertions.assertDoesNotThrow(() -> iPnTosPrivacyClient.acceptTosPrivacyV2(List.of(bffTosPrivacyBody)));
    }

    @ParameterType("TOS|TOS_SERCQ|TOS_DEST_B2B|DATAPRIVACY_SERCQ|DATAPRIVACY")
    public ConsentType tosConsentType(String consentType) {
        return switch (consentType) {
            case "TOS" -> ConsentType.TOS;
            case "TOS_SERCQ" -> ConsentType.TOS_SERCQ;
            case "TOS_DEST_B2B" -> ConsentType.TOS_DEST_B2B;
            case "DATAPRIVACY_SERCQ" -> ConsentType.DATAPRIVACY_SERCQ;
            case "DATAPRIVACY" -> ConsentType.DATAPRIVACY;
            default -> throw new IllegalArgumentException("Invalid consent type: " + consentType);
        };
    }

    @Given("l'utente {string} controlla l'accettazione {string} dei tos per sercq")
    public void lUtenteControllaAccettazioneDeiTos(String user, String tosStatus) {
        sharedSteps.selectUser(user);
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.ConsentType consentType = ConsentType.TOS_SERCQ;
        List<BffConsent> privacyConsentV1 = Assertions.assertDoesNotThrow(() -> iPnTosPrivacyClient.getTosPrivacyV1(List.of(consentType)));
        Assertions.assertNotNull(privacyConsentV1);
        Assertions.assertFalse(privacyConsentV1.isEmpty());
        privacyConsentV1.forEach(data -> {
            Assertions.assertNotNull(data.getConsentType());
            Assertions.assertEquals(ConsentType.TOS_SERCQ, data.getConsentType());
            Assertions.assertEquals(data.getAccepted(), tosStatus.equalsIgnoreCase("positiva"));
            Assertions.assertNotEquals(ConsentType.DATAPRIVACY_SERCQ, data.getConsentType());
        });
    }

    @Given("l'utente {string} controlla l'accettazione {string} dei tos per sercq v2")
    public void lUtenteControllaAccettazioneDeiTosv2(String user, String tosStatus) {
        sharedSteps.selectUser(user);
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.ConsentType consentType = ConsentType.TOS_SERCQ;
        List<BffConsent> privacyConsentV2 = Assertions.assertDoesNotThrow(() -> iPnTosPrivacyClient.getTosPrivacyV2(List.of(consentType)));
        Assertions.assertNotNull(privacyConsentV2);
        Assertions.assertFalse(privacyConsentV2.isEmpty());
        privacyConsentV2.forEach(data -> {
            Assertions.assertNotNull(data.getConsentType());
            Assertions.assertEquals(ConsentType.TOS_SERCQ, data.getConsentType());
            Assertions.assertEquals(data.getAccepted(), tosStatus.equalsIgnoreCase("positiva"));
        });
    }

    private <T> T deepCopy(Object obj, Class<T> toClass) {
        ObjectMapper objMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        try {
            String json = objMapper.writeValueAsString(obj);
            return objMapper.readValue(json, toClass);
        } catch (JsonProcessingException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }

    @And("l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:")
    public void verifySenderNotificationSearchResponse(Map<String, String> criteria) {
        List<LegalNotificationSearchRow> resultsPage = notificationSearchResponse.getResultsPage();
        Map<String, List<String>> resolvedCriteria = notificationSearchCriteriaMapper.build(criteria, new TokenResolver(sharedSteps, sendSharedContext));
        NotificationSearchRowAssertions.assertAllRowsMatchCriteria(resultsPage, resolvedCriteria);
    }

}




