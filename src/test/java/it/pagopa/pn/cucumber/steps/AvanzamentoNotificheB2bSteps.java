package it.pagopa.pn.cucumber.steps;

import io.cucumber.java.After;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.NotificationStatus;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.TimelineElementCategory;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.*;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;


public class AvanzamentoNotificheB2bSteps  {

    @Autowired
    IPnWebhookB2bClient webhookB2bClient;

    @Autowired
    IPnPaB2bClient b2bClient;

    @Autowired
    private IPnWebRecipientClient webRecipientClient;

    @Autowired
    private PnPaB2bUtils b2bUtils;

    @Autowired
    private IPnAppIOB2bClient appIOB2bClient;

    private List<StreamCreationRequest> streamCreationRequestList;
    private List<StreamMetadataResponse> eventStreamList;
    private Integer requestNumber;
    private HttpClientErrorException clientError;
    private NewNotificationRequest notificationRequest;
    private NewNotificationResponse newNotificationRequest;
    private FullSentNotification notificationResponseComplete;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Given("si predispo(ngono)(ne) {int} nuov(i)(o) stream denominat(i)(o) {string} con eventType {string}")
    public void vengonoPredispostiNuoviStreamDenominatiConEventType(int number, String title, String eventType) {
        this.streamCreationRequestList = new LinkedList<>();
        this.requestNumber = number;
        for(int i = 0; i<number; i++){
            StreamCreationRequest streamRequest = new StreamCreationRequest();
            streamRequest.title(title);
            //STATUS, TIMELINE
            streamRequest.eventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequest.EventTypeEnum.STATUS : StreamCreationRequest.EventTypeEnum.TIMELINE);
            streamCreationRequestList.add(streamRequest);
        }
    }

    @When("si crea(no) i(l) nuov(o)(i) stream")
    public void vieneCreatoUnNuovoStreamDiNotifica() {
        this.eventStreamList = new LinkedList<>();
        for(StreamCreationRequest request: streamCreationRequestList){
            try{
            StreamMetadataResponse eventStream = webhookB2bClient.createEventStream(request);
            this.eventStreamList.add(eventStream);
            }catch (HttpClientErrorException | HttpServerErrorException e) {
                if (e instanceof HttpClientErrorException) {
                    this.clientError = (HttpClientErrorException)e;
                }
            }
        }
    }

    @And("si cancella(no) (lo)(gli) stream creat(o)(i)")
    public void vieneCancellatoLoStreamCreato() {
        for(StreamMetadataResponse eventStream: eventStreamList){
            webhookB2bClient.deleteEventStream(eventStream.getStreamId());
        }

    }

    @And("viene verificata la corretta cancellazione")
    public void vieneVerificataAlCorrettaCancellazione() {
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        for(StreamMetadataResponse eventStream: eventStreamList){
            StreamListElement streamListElement = streamListElements.stream().filter(elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElement);
        }
    }


    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id")
    public void laStreamEStatoCreatoEVieneCorrettamenteRecuperatoDalSistema() {
        Assertions.assertDoesNotThrow(() -> {
            StreamMetadataResponse eventStream = webhookB2bClient.getEventStream(this.eventStreamList.get(0).getStreamId());
        });
    }


    @And("vengono letti gli eventi dello stream")
    public void vengonoLettiGliEventiDelloStream() {
        Assertions.assertDoesNotThrow(() -> {
            List<ProgressResponseElement> progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null);
            logger.info("EventProgress: " + progressResponseElements);
        });
    }

    @Given("viene generata una nuova notifica")
    public void vieneGenerataUnaNuovaNotifica(@Transpose NewNotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
    }

    @And("destinatario della notifica")
    public void destinatarioDellaNotifica(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(recipient);
    }


    @When("la notifica viene inviata e si attende che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataESiAttendeCheLoStatoDiventiACCEPTED() {
        Assertions.assertDoesNotThrow(() -> {
            NewNotificationResponse newNotificationRequest = b2bUtils.uploadNotification(notificationRequest);
            notificationResponseComplete = b2bUtils.waitForRequestAcceptation(newNotificationRequest);
        });
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
    }


    @And("vengono letti gli eventi dello stream fino allo stato {string}")
    public void vengonoLettiGliEventiDelloStreamFinoAlloStato(String status) {
        NotificationStatus notificationStatus;
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus;
        switch (status) {
            case "ACCEPTED":
                notificationStatus = NotificationStatus.ACCEPTED;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.ACCEPTED;
                break;
            case "DELIVERING":
                notificationStatus = NotificationStatus.DELIVERING;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.DELIVERING;
                break;
            case "DELIVERED":
                notificationStatus = NotificationStatus.DELIVERED;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.DELIVERED;
                break;
            case "CANCELLED":
                notificationStatus = NotificationStatus.CANCELLED;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.CANCELLED;
                break;
            case "EFFECTIVE_DATE":
                notificationStatus = NotificationStatus.EFFECTIVE_DATE;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.EFFECTIVE_DATE;
                break;
            default:
                throw new IllegalArgumentException();
        }
        List<ProgressResponseElement> progressResponseElements;
        ProgressResponseElement progressResponseElement = null;
        int wait = 48;
        boolean finded = false;
        for (int i = 0; i < wait; i++) {
            progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null);

            progressResponseElement = progressResponseElements.stream().filter(elem -> (elem.getIun().equals(notificationResponseComplete.getIun()) && elem.getNewStatus().equals(notificationStatus))).findAny().orElse(null);
            notificationResponseComplete = b2bClient.getSentNotification(notificationResponseComplete.getIun());
            logger.info("IUN: " + notificationResponseComplete.getIun());
            logger.info("*******************************************" + '\n');
            logger.info("EventProgress: " + progressResponseElements);
            logger.info("*******************************************" + '\n');
            logger.info("NOTIFICATION_STATUS_HISTORY: " + notificationResponseComplete.getNotificationStatusHistory());
            NotificationStatusHistoryElement notificationStatusHistoryElement = notificationResponseComplete.getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);
            if (notificationStatusHistoryElement != null && !finded) {
                wait = i + 2;
                finded = true;
            }
            if (progressResponseElement != null) {
                break;
            }
            try {
                Thread.sleep(10 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        Assertions.assertNotNull(progressResponseElement);
        logger.info("EventProgress: " + progressResponseElement);
    }


    @Then("vengono letti gli eventi dello stream fino all'elemento di timeline {string}")
    public void vengonoLettiGliEventiDelloStreamFinoAllElementoDiTimeline(String timelineEventCategory) {
        TimelineElementCategory timelineElementCategory;
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory timelineElementInternalCategory;
        switch (timelineEventCategory) {
            case "REQUEST_ACCEPTED":
                timelineElementCategory = TimelineElementCategory.REQUEST_ACCEPTED;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.REQUEST_ACCEPTED;
                break;
            case "AAR_GENERATION":
                timelineElementCategory = TimelineElementCategory.AAR_GENERATION;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.AAR_GENERATION;
                break;
            case "GET_ADDRESS":
                timelineElementCategory = TimelineElementCategory.GET_ADDRESS;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.GET_ADDRESS;
                break;
            case "SEND_DIGITAL_DOMICILE":
                timelineElementCategory = TimelineElementCategory.SEND_DIGITAL_DOMICILE;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.SEND_DIGITAL_DOMICILE;
                break;
            case "NOTIFICATION_VIEWED":
                timelineElementCategory = TimelineElementCategory.NOTIFICATION_VIEWED;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.NOTIFICATION_VIEWED;
                break;
            case "SEND_COURTESY_MESSAGE":
                timelineElementCategory = TimelineElementCategory.SEND_COURTESY_MESSAGE;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.SEND_COURTESY_MESSAGE;
                break;
            default:
                throw new IllegalArgumentException();
        }
        List<ProgressResponseElement> progressResponseElements;
        ProgressResponseElement progressResponseElement = null;
        int wait = 48;
        boolean finded = false;
        for (int i = 0; i < wait; i++) {
            ResponseEntity<List<ProgressResponseElement>> listResponseEntity = webhookB2bClient.consumeEventStreamHttp(this.eventStreamList.get(0).getStreamId(), null);
            String retryAfter = listResponseEntity.getHeaders().get("retry-after").get(0);
            System.out.println("ReTRY-AFTER: "+retryAfter);
            progressResponseElements = listResponseEntity.getBody();
            progressResponseElement = progressResponseElements.stream().filter(elem -> (elem.getIun().equals(notificationResponseComplete.getIun()) && elem.getTimelineEventCategory().equals(timelineElementCategory))).findAny().orElse(null);
            notificationResponseComplete = b2bClient.getSentNotification(notificationResponseComplete.getIun());
            logger.info("IUN: " + notificationResponseComplete.getIun());
            logger.info("*******************************************" + '\n');
            logger.info("EventProgress: " + progressResponseElements);
            logger.info("*******************************************" + '\n');
            logger.info("NOTIFICATION_TIMELINE: " + notificationResponseComplete.getTimeline());
            TimelineElement timelineElement = notificationResponseComplete.getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null && !finded) {
                wait = i + 2;
                finded = true;
            }

            if (progressResponseElement != null) {
                break;
            }
            try {
                Thread.sleep(10 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        Assertions.assertNotNull(progressResponseElement);
        logger.info("EventProgress: " + progressResponseElement);
    }

    @And("il destinatario legge la notifica")
    public void ilDestinatarioLeggeLaNotifica() {
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(notificationResponseComplete.getIun(), null);
        });
        try {
            Thread.sleep(50 * 1000L);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string}")
    public void vengonoLettiGliEventiDelloStreamFinoAlloStatoDellaNotifica(String status) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus;
        switch (status) {
            case "ACCEPTED":
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.ACCEPTED;
                break;
            case "DELIVERING":
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.DELIVERING;
                break;
            case "DELIVERED":
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.DELIVERED;
                break;
            case "CANCELLED":
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.CANCELLED;
                break;
            case "EFFECTIVE_DATE":
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.EFFECTIVE_DATE;
                break;
            default:
                throw new IllegalArgumentException();
        }

        NotificationStatusHistoryElement notificationStatusHistoryElement = null;

        for (int i = 0; i < 20; i++) {
            notificationResponseComplete = b2bClient.getSentNotification(notificationResponseComplete.getIun());

            logger.info("NOTIFICATION_STATUS_HISTORY: " + notificationResponseComplete.getNotificationStatusHistory());

            notificationStatusHistoryElement = notificationResponseComplete.getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            if (notificationStatusHistoryElement != null) {
                break;
            }
            try {
                Thread.sleep(10 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        Assertions.assertNotNull(notificationStatusHistoryElement);

    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string}")
    public void vengonoLettiGliEventiDelloStreamFinoAllElementoDiTimelineDellaNotifica(String timelineEventCategory) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory timelineElementInternalCategory;
        switch (timelineEventCategory) {
            case "REQUEST_ACCEPTED":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.REQUEST_ACCEPTED;
                break;
            case "AAR_GENERATION":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.AAR_GENERATION;
                break;
            case "GET_ADDRESS":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.GET_ADDRESS;
                break;
            case "SEND_DIGITAL_DOMICILE":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.SEND_DIGITAL_DOMICILE;
                break;
            case "NOTIFICATION_VIEWED":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.NOTIFICATION_VIEWED;
                break;
            case "SEND_COURTESY_MESSAGE":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.SEND_COURTESY_MESSAGE;
                break;
            case "DIGITAL_SUCCESS_WORKFLOW":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW;
                break;
            case "SEND_DIGITAL_PROGRESS":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.SEND_DIGITAL_PROGRESS;
                break;
            default:
                throw new IllegalArgumentException();
        }
        TimelineElement timelineElement = null;

        for (int i = 0; i < 20; i++) {
            notificationResponseComplete = b2bClient.getSentNotification(notificationResponseComplete.getIun());

            logger.info("NOTIFICATION_TIMELINE: " + notificationResponseComplete.getTimeline());

            timelineElement = notificationResponseComplete.getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
            try {
                Thread.sleep(10 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        Assertions.assertNotNull(timelineElement);
    }

    @Then("la PA richiede il download dell'attestazione opponibile {string}")
    public void vieneRichiestoIlDownloadDellAttestazioneOpponibile(String legalFactCategory) {
        downloadLegalFact(legalFactCategory,true,false);
    }


    @Then("viene richiesto tramite appIO il download dell'attestazione opponibile {string}")
    public void ilDestinatarioRichiedeTramiteAppIOIlDownloadDellAttestazioneOpponibile(String legalFactCategory) {
        downloadLegalFact(legalFactCategory,false,true);
    }


    private void downloadLegalFact(String legalFactCategory,boolean pa, boolean appIO){
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory timelineElementInternalCategory;
        TimelineElement timelineElement;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.REQUEST_ACCEPTED;
                timelineElement = notificationResponseComplete.getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.SENDER_ACK;
                break;
            case "RECIPIENT_ACCESS":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.NOTIFICATION_VIEWED;
                timelineElement = notificationResponseComplete.getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.RECIPIENT_ACCESS;
                break;
            case "PEC_RECEIPT":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.SEND_DIGITAL_PROGRESS;
                timelineElement = notificationResponseComplete.getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.PEC_RECEIPT;
                break;
            case "DIGITAL_DELIVERY":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW;
                timelineElement = notificationResponseComplete.getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.DIGITAL_DELIVERY;
                break;
            default:
                throw new IllegalArgumentException();
        }
        Assertions.assertNotNull(timelineElement.getLegalFactsIds());
        Assertions.assertEquals(category,timelineElement.getLegalFactsIds().get(0).getCategory());
        LegalFactCategory categorySearch = timelineElement.getLegalFactsIds().get(0).getCategory();
        String key = timelineElement.getLegalFactsIds().get(0).getKey();
        String keySearch = key.substring(key.indexOf("PN_LEGAL_FACTS"));
        if(pa){
            Assertions.assertDoesNotThrow(()->this.b2bClient.getLegalFact(notificationResponseComplete.getIun(),categorySearch , keySearch));
        }
        if(appIO){
            Assertions.assertDoesNotThrow(()->this.appIOB2bClient.getLegalFact(notificationResponseComplete.getIun(),categorySearch.toString(), keySearch,notificationResponseComplete.getRecipients().get(0).getTaxId()));
        }
    }

    @Then("si verifica che la notifica abbia lo stato VIEWED")
    public void siVerificaCheLaNotificaAbbiaLoStatoVIEWED() {
        notificationResponseComplete = b2bClient.getSentNotification(notificationResponseComplete.getIun());
        Assertions.assertNotNull(notificationResponseComplete.getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.VIEWED)).findAny().orElse(null));
    }


    @Then("si verifica nello stream che la notifica abbia lo stato VIEWED")
    public void siVerificaNelloStreamCheLaNotificaAbbiaLoStatoVIEWED() {
        List<ProgressResponseElement> progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null);
        Assertions.assertNotNull(progressResponseElements.stream().filter(elem -> (elem.getIun().equals(notificationResponseComplete.getIun()) && elem.getNewStatus().equals(NotificationStatus.VIEWED))).findAny().orElse(null));
    }



    @After("@clean")
    public void doSomethingAfter() {
        for(StreamMetadataResponse eventStream: eventStreamList){
            webhookB2bClient.deleteEventStream(eventStream.getStreamId());
            List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
            StreamListElement streamListElement = streamListElements.stream().filter(elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            logger.info("STREAM SIZE: " + streamListElements.size());
            logger.info("UUID DELETED: " + this.eventStreamList.get(0).getStreamId());
            Assertions.assertNull(streamListElement);
        }
    }

    @Then("l'ultima creazione ha prodotto un errore con status code {string}")
    public void lUltimaCreazioneHaProdottoUnErroreConStatusCode(String statusCode) {
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        System.out.println("streamListElements: "+streamListElements.size());
        System.out.println("eventStreamList: "+eventStreamList.size());
        System.out.println("requestNumber: "+requestNumber);
        Assertions.assertTrue((this.clientError != null) &&
                (this.clientError.getStatusCode().toString().substring(0,3).equals(statusCode)) && (eventStreamList.size() == (requestNumber-1)));
    }


    @Given("vengono cancellati tutti gli stream presenti")
    public void eliminaTutto() {
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        for(StreamListElement elem: streamListElements){
            webhookB2bClient.deleteEventStream(elem.getStreamId());
        }
    }




    @Then("vengono verificati costo = {string} e data di perfezionamento della notifica")
    public void vengonoVerificatiCostoEDataDiPerfezionamentoDellaNotifica(String price) {
        priceVerification(price,"");
    }


    @Then("viene verificato il costo = {string} della notifica")
    public void vieneVerificatoIlCostoDellaNotifica(String price) {
        priceVerification(price,null);
    }


    private void priceVerification(String price, String date){
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(notificationResponseComplete.getRecipients().get(0).getPayment().getCreditorTaxId(), notificationResponseComplete.getRecipients().get(0).getPayment().getNoticeCode());
        Assertions.assertEquals(notificationPrice.getIun(),notificationResponseComplete.getIun());
        if(price != null){
            Assertions.assertEquals(notificationPrice.getAmount(),price);
        }
        if(date != null){
            Assertions.assertNotNull(notificationPrice.getEffectiveDate());
        }
    }



}


