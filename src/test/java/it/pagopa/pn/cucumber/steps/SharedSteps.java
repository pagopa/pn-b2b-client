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
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalAndUnverifiedDigitalAddress;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalChannelType;
import it.pagopa.pn.cucumber.utils.DataTest;
import it.pagopa.pn.cucumber.utils.EventId;
import it.pagopa.pn.cucumber.utils.GroupPosition;
import it.pagopa.pn.cucumber.utils.TimelineEventId;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SharedSteps {


    private final DataTableTypeUtil dataTableTypeUtil;
    private final IPnPaB2bClient b2bClient;
    private final IPnWebPaClient webClient;
    private final PnPaB2bUtils b2bUtils;
    private final IPnWebRecipientClient webRecipientClient;
    private final PnExternalServiceClientImpl pnExternalServiceClient;
    private final IPnWebUserAttributesClient iPnWebUserAttributesClient;

    private NewNotificationResponse newNotificationResponse;
    private NewNotificationRequest notificationRequest;
    private FullSentNotificationV20 notificationResponseComplete;
    private HttpStatusCodeException notificationError;
    private OffsetDateTime notificationCreationDate;
    public static final String DEFAULT_PA = "Comune_1";
    private String settedPa = "Comune_1";
    private final ObjectMapper objMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private boolean groupToSet = true;

    private String errorCode = null;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${pn.external.api-key-taxID}")
    private String senderTaxId;

    @Value("${pn.external.api-key-2-taxID}")
    private String senderTaxIdTwo;

    @Value("${pn.external.api-key-GA-taxID}")
    private String senderTaxIdGa;

    @Value("${pn.bearer-token.user1.taxID}")
    private String marioCucumberTaxID;

    @Value("${pn.bearer-token.user2.taxID}")
    private String marioGherkinTaxID;

    @Value("${pn.configuration.workflow.wait.millis:31000}")
    private Integer workFlowWait;

    @Value("${pn.configuration.wait.millis:10000}")
    private Integer wait;

    @Value("${pn.configuration.scheduling.days.success.digital.refinement:6m}")
    private Duration schedulingDaysSuccessDigitalRefinement;

    @Value("${pn.configuration.scheduling.days.failure.digital.refinement:6m}")
    private Duration schedulingDaysFailureDigitalRefinement;

    @Value("${pn.configuration.scheduling.days.success.analog.refinement:2m}")
    private Duration schedulingDaysSuccessAnalogRefinement;

    @Value("${pn.configuration.scheduling.days.failure.analog.refinement:2m}")
    private Duration schedulingDaysFailureAnalogRefinement;

    @Value("${pn.configuration.second.notification.workflow.waiting.time:6m}")
    private Duration secondNotificationWorkflowWaitingTime;

    @Value("${pn.configuration.non.visibility.time:10m}")
    private Duration timeToAddInNonVisibilityTimeCase;

    @Value("${pn.configuration.waiting.for.read.courtesy.message:5m}")
    private Duration waitingForReadCourtesyMessage;

    @Value("${pn.configuration.scheduling.days.success.digital.refinement:6m}")
    private String schedulingDaysSuccessDigitalRefinementString;

    @Value("${pn.configuration.scheduling.days.failure.digital.refinement:6m}")
    private String schedulingDaysFailureDigitalRefinementString;

    private final Integer workFlowWaitDefault = 31000;
    private final Integer waitDefault = 10000;

    private final String schedulingDaysSuccessDigitalRefinementDefaultString = "6m";
    private final String schedulingDaysFailureDigitalRefinementDefaultString = "6m";
    private final Duration schedulingDaysSuccessDigitalRefinementDefault = DurationStyle.detectAndParse("6m");
    private final Duration schedulingDaysFailureDigitalRefinementDefault = DurationStyle.detectAndParse("6m");
    private final Duration schedulingDaysSuccessAnalogRefinementDefault = DurationStyle.detectAndParse("2m");
    private final Duration schedulingDaysFailureAnalogRefinementDefault = DurationStyle.detectAndParse("4m");
    private final Duration timeToAddInNonVisibilityTimeCaseDefault = DurationStyle.detectAndParse("10m");
    private final Duration secondNotificationWorkflowWaitingTimeDefault = DurationStyle.detectAndParse("6m");
    private final Duration waitingForReadCourtesyMessageDefault = DurationStyle.detectAndParse("5m");


    private List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElement> progressResponseElements = null;
    public static Integer lastEventID = 0;

    private String gherkinSpaTaxID = "12666810299";
  //  private String cucumberSrlTaxID = "SCTPTR04A01C352E";
    private String cucumberSrlTaxID = "20517490320";

    private String cucumberSocietyTaxID = "20517490320" ;// "DNNGRL83A01C352D";
    private String cucumberAnalogicTaxID = "SNCLNN65D19Z131V";
   // private String gherkinSrltaxId = "CCRMCT06A03A433H";

    private String gherkinSrltaxId = "12666810299";
    private String cucumberSpataxId = "20517490320"; //

    @Value("${pn.interop.base-url}")
    private String interopBaseUrl;
    @Value("${pn.interop.token-oauth2.path}")
    private String tokenOauth2Path;
    @Value("${pn.interop.token-oauth2.client-assertion}")
    private String clientAssertion;

    @Value("${interop.clientId}")
    private String interopClientId;

    @Value("${pn.external.bearer-token-pg1.id}")
    private String idOrganizationGherkinSrl;
    @Value("${pn.external.bearer-token-pg2.id}")
    private String idOrganizationCucumberSpa;

    @Value("${pn.external.utilized.pec:testpagopa3@pec.pagopa.it}")
    private String digitalAddress;

    private String defaultDigitalAddress = "testpagopa3@pec.pagopa.it";



    @Autowired
    public SharedSteps(DataTableTypeUtil dataTableTypeUtil, IPnPaB2bClient b2bClient,
                       PnPaB2bUtils b2bUtils, IPnWebRecipientClient webRecipientClient,
                       PnExternalServiceClientImpl pnExternalServiceClient,
                       IPnWebUserAttributesClient iPnWebUserAttributesClient, IPnWebPaClient webClient) {
        this.dataTableTypeUtil = dataTableTypeUtil;
        this.b2bClient = b2bClient;
        this.webClient = webClient;
        this.b2bUtils = b2bUtils;
        this.webRecipientClient = webRecipientClient;
        this.pnExternalServiceClient = pnExternalServiceClient;
        this.iPnWebUserAttributesClient = iPnWebUserAttributesClient;

    }

    @BeforeAll
    public static void before_all() {
        logger.debug("SHARED_GLUE START");
        //only for class activation
    }

    @Given("viene generata una nuova notifica")
    public void vieneGenerataUnaNotifica(@Transpose NewNotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
    }


    @And("destinatario")
    public void destinatario(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(recipient);
    }


    @And("destinatario Mario Cucumber")
    public void destinatarioMarioCucumber() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Mario Cucumber")
                        .taxId(marioCucumberTaxID)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address(getDigitalAddressValue())));
    }

    @And("destinatario Mario Cucumber e:")
    public void destinatarioMarioCucumberParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Mario Cucumber")
                        .taxId(marioCucumberTaxID));
    }


    @And("destinatario Mario Gherkin")
    public void destinatarioMarioGherkin() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Mario Gherkin")
                        .taxId(marioGherkinTaxID)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address(getDigitalAddressValue())));
    }

    @And("destinatario Mario Gherkin e:")
    public void destinatarioMarioGherkinParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Mario Gherkin")
                        .taxId(marioGherkinTaxID));
    }

    @And("destinatario Gherkin spa")
    public void destinatarioGherkinSpa() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Gherkin_spa")
                        .taxId(gherkinSpaTaxID)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address(getDigitalAddressValue())));
    }

    @And("destinatario GherkinSrl")
    public void destinatarioPg1() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("GherkinSrl")
                        .taxId(gherkinSrltaxId)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address(getDigitalAddressValue())));
    }

    @And("destinatario GherkinSrl e:")
    public void destinatarioPg1param(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("GherkinSrl")
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .taxId(gherkinSrltaxId));
    }

    @And("destinatario CucumberSpa")
    public void destinatarioPg2() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("CucumberSpa")
                        .taxId(cucumberSpataxId)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address(getDigitalAddressValue())));
    }


    @And("destinatario Gherkin spa e:")
    public void destinatarioGherkinSpaParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("GherkinSpa")
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .taxId(gherkinSpaTaxID));
    }

    @And("destinatario Cucumber srl")
    public void destinatarioCucumberSrl() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("CucumberSrl")
                        .taxId(cucumberSrlTaxID)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address(getDigitalAddressValue())));
    }

    @And("destinatario Cucumber srl e:")
    public void destinatarioCucumberSrlParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("CucumberSrl")
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .taxId(cucumberSrlTaxID));
    }

    @And("destinatario Cucumber Society")
    public void destinatarioCucumberSociety() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Cucumber_Society")
                        .taxId(cucumberSocietyTaxID)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG)
                        .digitalDomicile(new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address(getDigitalAddressValue())));
    }

    @And("destinatario Cucumber Society e:")
    public void destinatarioCucumberSocietyParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Cucumber_Society")
                        .taxId(cucumberSocietyTaxID)
                        .recipientType(NotificationRecipient.RecipientTypeEnum.PG));
    }

    @And("destinatario Cucumber Analogic e:")
    public void destinatarioCucumberAnalogicParam(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(
                recipient
                        .denomination("Cucumber Analogic")
                        .taxId(cucumberAnalogicTaxID));
    }

    @And("destinatario Cristoforo Colombo")
    public void destinatarioCristoforoColombo() {
        this.notificationRequest.addRecipientsItem(
                dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination("Cristoforo Colombo")
                        .taxId("CLMCST42R12D969Z")
                        .digitalDomicile(null));
    }

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEDiversoCodiceAvviso() {
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();

        generateNewNotification();

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
    }

    @And("destinatario {string} con uguale codice avviso del destinario numero {int}")
    public void destinatarioConUgualeCodiceAvvisoDelDestinarioN(String recipientName, int recipientNumber, @Transpose NotificationRecipient recipient) {
        Assertions.assertDoesNotThrow(() -> notificationRequest.getRecipients().get(recipientNumber - 1).getPayment());
        String noticeCode = notificationRequest.getRecipients().get(recipientNumber - 1).getPayment().getNoticeCode();

        if (recipientName.trim().equalsIgnoreCase("mario cucumber")) {
            recipient = (recipient.denomination("Mario Cucumber")
                    .taxId(marioCucumberTaxID));
        } else if (recipientName.trim().equalsIgnoreCase("mario gherkin")) {
            recipient = (recipient.denomination("Mario Gherkin")
                    .taxId(marioGherkinTaxID));

        } else {
            throw new IllegalArgumentException();
        }

        recipient.getPayment().setNoticeCode(noticeCode);
        this.notificationRequest.addRecipientsItem(recipient);
    }

    @Then("viene generata una nuova notifica valida con uguale codice fiscale del creditore e uguale codice avviso")
    public void vieneGenerataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEUgualeCodiceAvvisoConTaxIdCorretto() {
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();
        String noticeCode = notificationRequest.getRecipients().get(0).getPayment().getNoticeCode();

        this.notificationRequest = (dataTableTypeUtil.convertNotificationRequest(new HashMap<>())
                .addRecipientsItem(dataTableTypeUtil.convertNotificationRecipient(new HashMap<>()).taxId(marioCucumberTaxID)));

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
        this.notificationRequest.getRecipients().get(0).getPayment().setNoticeCode(noticeCode);
    }

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEUgualeCodiceAvviso() {
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();
        String noticeCode = notificationRequest.getRecipients().get(0).getPayment().getNoticeCode();

        generateNewNotification();

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
        this.notificationRequest.getRecipients().get(0).getPayment().setNoticeCode(noticeCode);
    }

    @And("viene configurato noticeCodeAlternative uguale a noticeCode")
    public void vieneConfiguratoNoticeCodeAlternativeUgualeNoticeCode() {
        this.notificationRequest.getRecipients().get(0).getPayment().setNoticeCodeAlternative(this.notificationRequest.getRecipients().get(0).getPayment().getNoticeCode());
    }

    @And("viene configurato noticeCodeAlternative diversi a noticeCode")
    public void vieneConfiguratoNoticeCodeAlternativeDiversiNoticeCode() {
        this.notificationRequest.getRecipients().get(0).getPayment().setNoticeCodeAlternative(getValue(new HashMap<>(), PAYMENT_NOTICE_CODE_OPTIONAL.key));
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
        int i = 0;
        while ( i < numAllegati) {
            this.notificationRequest.addDocumentsItem( b2bUtils.newDocument(getDefaultValue(DOCUMENT.key)) );
            i++;
        }
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataOk(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotification();
    }

    @And("la notifica può essere annullata dal sistema tramite codice IUN dal comune {string}")
    public void notificationCanBeCanceledWithIUNByComune(String paType) {
        selectPA(paType);
        Assertions.assertDoesNotThrow(() -> {
            RequestStatus resp =  Assertions.assertDoesNotThrow(() ->
                    this.b2bClient.notificationCancellation(getSentNotification().getIun()));

            Assertions.assertNotNull(resp);
            Assertions.assertNotNull(resp.getDetails());
            Assertions.assertTrue(resp.getDetails().size()>0);
            Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));

        });

    }

    @And("la notifica non può essere annullata dal sistema tramite codice IUN dal comune {string}")
    public void notificationCaNotBeCanceledWithIUNByComune(String paType) {
        selectPA(paType);
        try {
            this.b2bClient.notificationCancellation(getSentNotification().getIun());
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    @Then("l'operazione di annullamento ha prodotto un errore con status code {string}")
    public void operationProducedErrorWithStatusCode(String statusCode) {
        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi ACCEPTED e successivamente annullata")
    public void laNotificaVieneInviataOkAndCancelled(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationAndCancell();
    }

    @When("la notifica viene inviata tramite api b2b dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataRefused(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationRefused();
    }

    @When("la notifica viene inviata tramite api b2b senza preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataSenzaPreloadAllegato(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationWithErrorNotFindAllegato(false);
    }

    @When("la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataTramiteApiBBEffettuandoLaPreloadMaSenzaCaricareNessunAllegatoDalESiAttendeCheLoStatoDiventiREFUSED(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationWithErrorNotFindAllegato(true);
    }

    @When("la notifica viene inviata tramite api b2b con sha256 differente dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConShaDifferente(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationWithErrorSha();
    }

    @When("la notifica viene inviata tramite api b2b con estensione errata dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataConEstensioneErrata(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationWithWrongExtension();
    }

    @When("la notifica viene inviata tramite api b2b oversize preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoOverSize(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationRefusedOverSizeAllegato();
    }

    @When("la notifica viene inviata tramite api b2b injection preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoInjection(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationRefusedInjectionAllegato();
    }

    @When("la notifica viene inviata tramite api b2b over 15 preload allegato dal {string} e si attende che lo stato diventi REFUSED")
    public void laNotificaVieneInviataPreloadAllegatoOver15(String paType) {
        selectPA(paType);
        setSenderTaxIdFromProperties();
        sendNotificationRefusedOver15Allegato();
    }


    @When("la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataOk() {
        sendNotification();
    }

    @When("la notifica viene inviata dal {string}")
    public void laNotificaVieneInviataDallaPA(String pa) {
        selectPA(pa);
        setSenderTaxIdFromProperties();
        sendNotificationWithError();
    }

    @When("la notifica viene inviata tramite api b2b")
    public void laNotificaVieneInviatatramiteApiB2b() {
        sendNotificationWithError();
    }

    @When("la notifica viene inviata tramite api b2b senza preload allegato dal {string}")
    public void laNotificaVieneInviatatramiteApiB2bSenzaPreloadAllegato(String pa) {
        selectPA(pa);
        setSenderTaxIdFromProperties();
        sendNotificationWithErrorNotFindAllegato(false);
    }

    private void sendNotification() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                newNotificationResponse = b2bUtils.uploadNotification(notificationRequest);

                try {
                    Thread.sleep(getWorkFlowWait());
                } catch (InterruptedException e) {
                    logger.error("Thread.sleep error retry");
                    throw new RuntimeException(e);
                }

                notificationResponseComplete = b2bUtils.waitForRequestAcceptation(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(notificationResponseComplete);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    private void sendNotificationAndCancell() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                newNotificationResponse = b2bUtils.uploadNotification(notificationRequest);

                try {
                    Thread.sleep(getWorkFlowWait());
                } catch (InterruptedException e) {
                    logger.error("Thread.sleep error retry");
                    throw new RuntimeException(e);
                }

                notificationResponseComplete = b2bUtils.waitForRequestAcceptation(newNotificationResponse);

            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(notificationResponseComplete);

            Assertions.assertDoesNotThrow(() -> {
                RequestStatus resp =  Assertions.assertDoesNotThrow(() ->
                        b2bClient.notificationCancellation(notificationResponseComplete.getIun()));

                Assertions.assertNotNull(resp);
                Assertions.assertNotNull(resp.getDetails());
                Assertions.assertTrue(resp.getDetails().size()>0);
                Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));

            });

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void sendNotificationWithError() {
        try {
            notificationCreationDate = OffsetDateTime.now();
            this.newNotificationResponse = b2bUtils.uploadNotification(notificationRequest);
        } catch (HttpStatusCodeException | IOException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    private void sendNotificationWithErrorNotFindAllegato(boolean noUpload) {

        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                newNotificationResponse = b2bUtils.uploadNotificationNotFindAllegato(notificationRequest,noUpload);
                errorCode = b2bUtils.waitForRequestRefused(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }

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
                errorCode = b2bUtils.waitForRequestRefused(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }

            Assertions.assertNotNull(errorCode);

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

                errorCode = b2bUtils.waitForRequestRefused(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }

            Assertions.assertNotNull(errorCode);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void sendNotificationRefused() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                newNotificationResponse = b2bUtils.uploadNotification(notificationRequest);
                errorCode = b2bUtils.waitForRequestRefused(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(errorCode);

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
                errorCode = b2bUtils.waitForRequestRefused(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(errorCode);

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
                errorCode = b2bUtils.waitForRequestRefused(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(errorCode);

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
                errorCode = b2bUtils.waitForRequestRefused(newNotificationResponse);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(errorCode);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (newNotificationResponse == null ? "NULL" : newNotificationResponse.getNotificationRequestId()) + " }";
            Assertions.assertTrue((message != null) && (message.contains("400") && message.contains("Max attachment count reached")));
            errorCode = "INVALID_PARAMETER_MAX_ATTACHMENT";


        }
    }


    private void generateNewNotification() {
        assert this.notificationRequest.getRecipients().get(0).getPayment() != null;
        this.notificationRequest = (dataTableTypeUtil.convertNotificationRequest(new HashMap<>())
                .subject(notificationRequest.getSubject())
                .senderDenomination(notificationRequest.getSenderDenomination())
                .addRecipientsItem(dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination(notificationRequest.getRecipients().get(0).getDenomination())
                        .taxId(notificationRequest.getRecipients().get(0).getTaxId())
                        .recipientType(notificationRequest.getRecipients().get(0).getRecipientType())));

    }


    public void setNewNotificationResponse(NewNotificationResponse newNotificationResponse) {
        this.newNotificationResponse = newNotificationResponse;
    }

    public NewNotificationResponse getNewNotificationResponse() {
        return newNotificationResponse;
    }

    public NewNotificationRequest getNotificationRequest() {
        return notificationRequest;
    }

    public HttpStatusCodeException consumeNotificationError() {
        HttpStatusCodeException value = notificationError;
        this.notificationError = null;
        return value;
    }

    public void setNotificationError(HttpStatusCodeException notificationError) {
        this.notificationError = notificationError;
    }

    public void setSenderTaxIdFromProperties() {
        switch (settedPa) {
            case "Comune_1":
                this.notificationRequest.setSenderTaxId(this.senderTaxId);
                setGrup(SettableApiKey.ApiKeyType.MVP_1);
                break;
            case "Comune_2":
                this.notificationRequest.setSenderTaxId(this.senderTaxIdTwo);
                setGrup(SettableApiKey.ApiKeyType.MVP_2);
                break;
            case "Comune_Multi":
                this.notificationRequest.setSenderTaxId(this.senderTaxIdGa);
                setGrup(SettableApiKey.ApiKeyType.GA);
                break;
        }

    }

    public String getSenderTaxIdFromProperties(String settedPa) {
        switch (settedPa) {
            case "Comune_1":
                return this.senderTaxId;
            case "Comune_2":
                return this.senderTaxIdTwo;
            case "Comune_Multi":
                return this.senderTaxIdGa;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void setGrup(SettableApiKey.ApiKeyType apiKeyType) {
        if (groupToSet && this.notificationRequest.getGroup() == null) {
            List<HashMap<String, String>> hashMapsList = pnExternalServiceClient.paGroupInfo(apiKeyType);
            if (hashMapsList == null || hashMapsList.size() == 0) return;
            String id = null;
            for (HashMap<String, String> elem : hashMapsList) {
                if (elem.get("status").equalsIgnoreCase("ACTIVE")) {
                    id = elem.get("id");
                    break;
                }
            }
            if (id == null) return;
            this.notificationRequest.setGroup(id);
        }
    }

    public FullSentNotificationV20 getSentNotification() {
        return notificationResponseComplete;
    }

    public OffsetDateTime getNotificationCreationDate() {
        return notificationCreationDate;
    }



    public void setNotificationRequest(NewNotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
    }

    public void setSentNotification(FullSentNotificationV20 notificationResponseComplete) {
        this.notificationResponseComplete = notificationResponseComplete;
    }

    public void selectPA(String apiKey) {
        switch (apiKey) {
            case "Comune_1":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_1);
                break;
            case "Comune_2":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_2);
                break;
            case "Comune_Multi":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.GA);
                break;
            default:
                throw new IllegalArgumentException();
        }
        this.b2bUtils.setClient(b2bClient);
        this.settedPa = apiKey;
    }

    public void selectUser(String recipient) {
        switch (recipient.trim().toLowerCase()){
            case "mario cucumber":
            case "ettore fieramosca":
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                break;
            case "mario gherkin":
            case "cristoforo colombo":
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                break;
            case "gherkinsrl":
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                break;
            case "cucumberspa":
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                break;
            case "leonardo da vinci":
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_3);
                break;
            case "dino sauro":
                webRecipientClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
                iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_5);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public IPnPaB2bClient getB2bClient() {
        return b2bClient;
    }


    public IPnWebPaClient getWebPaClient() {
        return webClient;
    }

    public PnPaB2bUtils getB2bUtils() {
        return b2bUtils;
    }

    public IPnWebRecipientClient getWebRecipientClient() {
        return webRecipientClient;
    }

    public String getMarioCucumberTaxID() {
        return marioCucumberTaxID;
    }

    public String getMarioGherkinTaxID() {
        return marioGherkinTaxID;
    }

    public String getGherkinSrltaxId() {
        return gherkinSrltaxId;
    }

    public String getCucumberSpataxId() {
        return cucumberSpataxId;
    }

    public PnExternalServiceClientImpl getPnExternalServiceClient() {
        return pnExternalServiceClient;
    }

    public void throwAssertFailerWithIUN(AssertionFailedError assertionFailedError) {
        String message = assertionFailedError.getMessage() +
                "{IUN: " + notificationResponseComplete.getIun() + " }";
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
        if (workFlowWait == null) return workFlowWaitDefault;
        return workFlowWait;
    }

    public Integer getWait() {
        if (wait == null) return waitDefault;
        return wait;
    }

    public String getDigitalAddressValue() {
        if (digitalAddress == null || digitalAddress.equalsIgnoreCase("${pn.external.digitalDomicile.address}")) return defaultDigitalAddress;
        return digitalAddress;
    }

    public Duration getSchedulingDaysSuccessDigitalRefinement() {
        if (schedulingDaysSuccessDigitalRefinement == null) return schedulingDaysSuccessDigitalRefinementDefault;
        return schedulingDaysSuccessDigitalRefinement;
    }

    public Duration getSchedulingDaysFailureDigitalRefinement() {
        if (schedulingDaysFailureDigitalRefinement == null) return schedulingDaysFailureDigitalRefinementDefault;
        return schedulingDaysFailureDigitalRefinement;
    }

    public Duration getSchedulingDaysSuccessAnalogRefinement() {
        if (schedulingDaysSuccessAnalogRefinement == null) return schedulingDaysSuccessAnalogRefinementDefault;
        return schedulingDaysSuccessAnalogRefinement;
    }

    public Duration getSchedulingDaysFailureAnalogRefinement() {
        if (schedulingDaysSuccessAnalogRefinement == null) return schedulingDaysFailureAnalogRefinementDefault;
        return schedulingDaysFailureAnalogRefinement;
    }

    public Duration getTimeToAddInNonVisibilityTimeCase() {
        if (schedulingDaysSuccessDigitalRefinement == null) return timeToAddInNonVisibilityTimeCaseDefault;
        return timeToAddInNonVisibilityTimeCase;
    }

    public Duration getSecondNotificationWorkflowWaitingTime() {
        if (secondNotificationWorkflowWaitingTime == null) return secondNotificationWorkflowWaitingTimeDefault;
        return secondNotificationWorkflowWaitingTime;
    }

    public Duration getWaitingForReadCourtesyMessage() {
        if (waitingForReadCourtesyMessage == null) return waitingForReadCourtesyMessageDefault;
        return waitingForReadCourtesyMessage;
    }

    @Before("@integrationTest")
    public void doSomethingAfter() {
        this.groupToSet = false;
    }

    public List<HashMap<String, String>> getGroupsByPa(String settedPa) {
        List<HashMap<String, String>> hashMapsList = null;
        switch (settedPa) {
            case "Comune_1":
                hashMapsList = this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.MVP_1);
                break;
            case "Comune_2":
                hashMapsList = this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.MVP_2);
                break;
            case "Comune_Multi":
                hashMapsList = this.pnExternalServiceClient.paGroupInfo(SettableApiKey.ApiKeyType.GA);
                break;
            default:
                throw new IllegalArgumentException();
        }

        Assertions.assertNotNull(hashMapsList);
        Assertions.assertTrue(hashMapsList.size() > 0);
        return hashMapsList;
    }

    public String getGroupIdByPa(String settedPa, GroupPosition position) {
        List<HashMap<String, String>> hashMapsList = getGroupsByPa(settedPa);

        String id = null;
        Integer count = 0;
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

    @And("viene rimossa se presente la pec di piattaforma di {string}")
    public void vieneRimossaSePresenteLaPecDiPiattaformaDi(String user) {
        selectUser(user);
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
                this.iPnWebUserAttributesClient.deleteRecipientLegalAddress("default", LegalChannelType.PEC);
                logger.info("PEC FOUND AND DELETED");
            }
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
                logger.info("PEC NOT FOUND");
            } else {
                throw httpStatusCodeException;
            }
        }

    }

    @Then("si verifica che la notifica non viene accettata causa {string}")
    public void verificaNotificaNoAccept(String causa) {
        switch (causa) {
            case "ALLEGATO":
                Assertions.assertTrue("FILE_NOTFOUND".equalsIgnoreCase(errorCode));
                break;
            case "EXTENSION":
                Assertions.assertTrue("FILE_PDF_INVALID_ERROR".equalsIgnoreCase(errorCode));
                break;
            case "SHA_256":
                Assertions.assertTrue("FILE_SHA_ERROR".equalsIgnoreCase(errorCode));
                break;
            case "TAX_ID":
                Assertions.assertTrue("TAXID_NOT_VALID".equalsIgnoreCase(errorCode));
                break;
            case "ADDRESS":
                Assertions.assertTrue("NOT_VALID_ADDRESS".equalsIgnoreCase(errorCode));
            case "INVALID_PARAMETER_MAX_ATTACHMENT":
                Assertions.assertTrue("INVALID_PARAMETER_MAX_ATTACHMENT".equalsIgnoreCase(errorCode));
                break;
            case "FILE_PDF_INVALID_ERROR":
                Assertions.assertTrue("FILE_PDF_INVALID_ERROR".equalsIgnoreCase(errorCode));
                break;
            case "NOT_VALID_ADDRESS":
                Assertions.assertTrue("NOT_VALID_ADDRESS".equalsIgnoreCase(errorCode));
                break;
            default:
                throw new IllegalArgumentException();
        }

    }


    @Before
    public void injectScenarioNameInsideSfl4jMdc(Scenario scenario) {
        String scenarioName = scenario.getName();
        MDC.put( RestTemplateConfiguration.CUCUMBER_SCENARIO_NAME_MDC_ENTRY, scenarioName );
    }


    public String getIdOrganizationGherkinSrl() {
        return idOrganizationGherkinSrl;
    }

    public void setIdOrganizationGherkinSrl(String idOrganizationGherkinSrl) {
        this.idOrganizationGherkinSrl = idOrganizationGherkinSrl;
    }

    public String getIdOrganizationCucumberSpa() {
        return idOrganizationCucumberSpa;
    }

    public void setIdOrganizationCucumberSpa(String idOrganizationCucumberSpa) {
        this.idOrganizationCucumberSpa = idOrganizationCucumberSpa;
    }

    public String getTimelineEventId(String timelineEventCategory, String iun, DataTest dataFromTest) {
        TimelineElementV20 timelineElement = dataFromTest.getTimelineElement();
        TimelineElementDetailsV20 timelineElementDetails = timelineElement.getDetails();
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

        switch (timelineEventCategory) {
            case "SEND_COURTESY_MESSAGE":
                return TimelineEventId.SEND_COURTESY_MESSAGE.buildEventId(event);
            case "REQUEST_REFUSED":
                return TimelineEventId.REQUEST_REFUSED.buildEventId(event);
            case "AAR_GENERATION":
                return TimelineEventId.AAR_GENERATION.buildEventId(event);
            case "REQUEST_ACCEPTED":
                return TimelineEventId.REQUEST_ACCEPTED.buildEventId(event);
            case "SEND_DIGITAL_DOMICILE":
                return TimelineEventId.SEND_DIGITAL_DOMICILE.buildEventId(event);
            case "SEND_DIGITAL_FEEDBACK":
                return TimelineEventId.SEND_DIGITAL_FEEDBACK.buildEventId(event);
            case "GET_ADDRESS":
                return TimelineEventId.GET_ADDRESS.buildEventId(event);
            case "DIGITAL_SUCCESS_WORKFLOW":
                return TimelineEventId.DIGITAL_SUCCESS_WORKFLOW.buildEventId(event);
            case "SCHEDULE_REFINEMENT":
                return TimelineEventId.SCHEDULE_REFINEMENT_WORKFLOW.buildEventId(event);
            case "REFINEMENT":
                return TimelineEventId.REFINEMENT.buildEventId(event);
            case "ANALOG_SUCCESS_WORKFLOW":
                return TimelineEventId.ANALOG_SUCCESS_WORKFLOW.buildEventId(event);
            case "DIGITAL_FAILURE_WORKFLOW":
                return TimelineEventId.DIGITAL_FAILURE_WORKFLOW.buildEventId(event);
            case "SEND_ANALOG_FEEDBACK":
                return TimelineEventId.SEND_ANALOG_FEEDBACK.buildEventId(event);
            case "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS":
                return TimelineEventId.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS.buildEventId(event);
            case "SEND_ANALOG_PROGRESS":
                return TimelineEventId.SEND_ANALOG_PROGRESS.buildEventId(event);
            case "ANALOG_FAILURE_WORKFLOW":
                return TimelineEventId.ANALOG_FAILURE_WORKFLOW.buildEventId(event);
            case "PREPARE_ANALOG_DOMICILE":
                return TimelineEventId.PREPARE_ANALOG_DOMICILE.buildEventId(event);
            case "SCHEDULE_ANALOG_WORKFLOW":
                return TimelineEventId.SCHEDULE_ANALOG_WORKFLOW.buildEventId(event);
            case "SEND_ANALOG_DOMICILE":
                return TimelineEventId.SEND_ANALOG_DOMICILE.buildEventId(event);
            case "SEND_SIMPLE_REGISTERED_LETTER":
                return TimelineEventId.SEND_SIMPLE_REGISTERED_LETTER.buildEventId(event);
            case "PREPARE_SIMPLE_REGISTERED_LETTER":
                return TimelineEventId.PREPARE_SIMPLE_REGISTERED_LETTER.buildEventId(event);
            case "NOTIFICATION_VIEWED":
                return TimelineEventId.NOTIFICATION_VIEWED.buildEventId(event);
            case "COMPLETELY_UNREACHABLE":
                return TimelineEventId.COMPLETELY_UNREACHABLE.buildEventId(event);
            case "DIGITAL_DELIVERY_CREATION_REQUEST":
                return TimelineEventId.DIGITAL_DELIVERY_CREATION_REQUEST.buildEventId(event);

        }
        return null;
    }

    public TimelineElementV20 getTimelineElementByEventId (String timelineEventCategory, DataTest dataFromTest) {
        List<TimelineElementV20> timelineElementList = notificationResponseComplete.getTimeline();
        String iun;
        if (timelineEventCategory.equals(TimelineElementCategoryV20.REQUEST_REFUSED.getValue())) {
            String requestId = newNotificationResponse.getNotificationRequestId();
            byte[] decodedBytes = Base64.getDecoder().decode(requestId);
            iun = new String(decodedBytes);
        } else {
            // proceed with default flux
            iun = notificationResponseComplete.getIun();
        }
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            // get timeline event id
            String timelineEventId = getTimelineEventId(timelineEventCategory, iun, dataFromTest);
            if (timelineEventCategory.equals(TimelineElementCategoryV20.SEND_ANALOG_PROGRESS.getValue()) || timelineEventCategory.equals(TimelineElementCategoryV20.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS.getValue())) {
                TimelineElementV20 timelineElementFromTest = dataFromTest.getTimelineElement();
                TimelineElementDetailsV20 timelineElementDetails = timelineElementFromTest.getDetails();
                return timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId) && elem.getDetails().getDeliveryDetailCode().equals(timelineElementDetails.getDeliveryDetailCode())).findAny().orElse(null);
            }
            return timelineElementList.stream().filter(elem -> elem.getElementId().equals(timelineEventId)).findAny().orElse(null);
        }
        return timelineElementList.stream().filter(elem -> elem.getCategory().getValue().equals(timelineEventCategory)).findAny().orElse(null);
    }

    public List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElement> getProgressResponseElements() {
        return progressResponseElements;
    }

    public void setProgressResponseElements(List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElement> progressResponseElements) {
        this.progressResponseElements = progressResponseElements;
    }

    public String getSchedulingDaysFailureDigitalRefinementString() {
        if (schedulingDaysFailureDigitalRefinementString == null) return schedulingDaysFailureDigitalRefinementDefaultString;
        return schedulingDaysFailureDigitalRefinementString;
    }
    public String getSchedulingDaysSuccessDigitalRefinementString() {
        if (schedulingDaysSuccessDigitalRefinementString == null) return schedulingDaysSuccessDigitalRefinementDefaultString;
        return schedulingDaysSuccessDigitalRefinementString;
    }



}
