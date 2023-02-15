package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.AddressVerification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalChannelType;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalDigitalAddress;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

public class AvanzamentoNotificheB2bSteps {


    private final IPnPaB2bClient b2bClient;
    private final SharedSteps sharedSteps;
    private final IPnAppIOB2bClient appIOB2bClient;
    private final IPnWebRecipientClient webRecipientClient;
    private final IPnWebUserAttributesClient webUserAttributesClient;
    private final IPnIoUserAttributerExternaClientImpl ioUserAttributerExternaClient;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public AvanzamentoNotificheB2bSteps(SharedSteps sharedSteps, IPnAppIOB2bClient appIOB2bClient,
                                        IPnWebUserAttributesClient webUserAttributesClient, IPnIoUserAttributerExternaClientImpl ioUserAttributerExternaClient) {
        this.sharedSteps = sharedSteps;
        this.appIOB2bClient = appIOB2bClient;
        this.b2bClient = sharedSteps.getB2bClient();
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.webUserAttributesClient = webUserAttributesClient;
        this.ioUserAttributerExternaClient = ioUserAttributerExternaClient;
    }


    @Then("vengono letti gli eventi fino allo stato della notifica {string} dalla PA {string}")
    public void readingEventsNotificationPA(String status, String pa) {
        sharedSteps.selectPA(pa);
        readingEventUpToTheStatusOfNotification(status);
        sharedSteps.selectPA(SharedSteps.DEFAULT_PA);
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string}")
    public void readingEventUpToTheStatusOfNotification(String status) {
        NotificationStatus notificationInternalStatus;
        switch (status) {
            case "ACCEPTED":
                notificationInternalStatus = NotificationStatus.ACCEPTED;
                break;
            case "DELIVERING":
                notificationInternalStatus = NotificationStatus.DELIVERING;
                break;
            case "DELIVERED":
                notificationInternalStatus = NotificationStatus.DELIVERED;
                break;
            case "CANCELLED":
                notificationInternalStatus = NotificationStatus.CANCELLED;
                break;
            case "EFFECTIVE_DATE":
                notificationInternalStatus = NotificationStatus.EFFECTIVE_DATE;
                break;
            case "COMPLETELY_UNREACHABLE":
                notificationInternalStatus = NotificationStatus.UNREACHABLE;
                break;
            default:
                throw new IllegalArgumentException();
        }

        NotificationStatusHistoryElement notificationStatusHistoryElement = null;

        for (int i = 0; i < 10; i++) {
            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_STATUS_HISTORY: " + sharedSteps.getSentNotification().getNotificationStatusHistory());

            notificationStatusHistoryElement = sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            if (notificationStatusHistoryElement != null) {
                break;
            }
            try {
                Thread.sleep(31 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        try{
            Assertions.assertNotNull(notificationStatusHistoryElement);
        }catch (AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }


    }

    private TimelineElementCategory getTimelineElementCategory(String timelineEventCategory){
        TimelineElementCategory timelineElementInternalCategory;
        switch (timelineEventCategory) {
            case "REQUEST_ACCEPTED":
                timelineElementInternalCategory = TimelineElementCategory.REQUEST_ACCEPTED;
                break;
            case "AAR_GENERATION":
                timelineElementInternalCategory = TimelineElementCategory.AAR_GENERATION;
                break;
            case "GET_ADDRESS":
                timelineElementInternalCategory = TimelineElementCategory.GET_ADDRESS;
                break;
            case "SEND_DIGITAL_DOMICILE":
                timelineElementInternalCategory = TimelineElementCategory.SEND_DIGITAL_DOMICILE;
                break;
            case "NOTIFICATION_VIEWED":
                timelineElementInternalCategory = TimelineElementCategory.NOTIFICATION_VIEWED;
                break;
            case "SEND_COURTESY_MESSAGE":
                timelineElementInternalCategory = TimelineElementCategory.SEND_COURTESY_MESSAGE;
                break;
            case "DIGITAL_SUCCESS_WORKFLOW":
                timelineElementInternalCategory = TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW;
                break;
            case "DIGITAL_FAILURE_WORKFLOW":
                timelineElementInternalCategory = TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW;
                break;
            case "NOT_HANDLED":
                timelineElementInternalCategory = TimelineElementCategory.NOT_HANDLED;
                break;
            case "SEND_DIGITAL_FEEDBACK":
                timelineElementInternalCategory = TimelineElementCategory.SEND_DIGITAL_FEEDBACK;
                break;
            case "SEND_DIGITAL_PROGRESS":
                timelineElementInternalCategory = TimelineElementCategory.SEND_DIGITAL_PROGRESS;
                break;
            case "PUBLIC_REGISTRY_CALL":
                timelineElementInternalCategory = TimelineElementCategory.PUBLIC_REGISTRY_CALL;
                break;
            case "PUBLIC_REGISTRY_RESPONSE":
                timelineElementInternalCategory = TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE;
                break;
            case "SCHEDULE_ANALOG_WORKFLOW":
                timelineElementInternalCategory = TimelineElementCategory.SCHEDULE_ANALOG_WORKFLOW;
                break;
            case "ANALOG_SUCCESS_WORKFLOW":
                timelineElementInternalCategory = TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW;
                break;
            case "SEND_ANALOG_DOMICILE":
                timelineElementInternalCategory = TimelineElementCategory.SEND_ANALOG_DOMICILE;
                break;
            case "SEND_ANALOG_FEEDBACK":
                timelineElementInternalCategory = TimelineElementCategory.SEND_ANALOG_FEEDBACK;
                break;
            case "SEND_ANALOG_PREPARE":
                timelineElementInternalCategory = TimelineElementCategory.SEND_ANALOG_PREPARE;
                break;
            case "PREPARE_SIMPLE_REGISTERED_LETTER":
                timelineElementInternalCategory = TimelineElementCategory.PREPARE_SIMPLE_REGISTERED_LETTER;
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER":
                timelineElementInternalCategory = TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER;
                break;
            case "PAYMENT":
                timelineElementInternalCategory = TimelineElementCategory.PAYMENT;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return timelineElementInternalCategory;
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string}")
    public void readingEventUpToTheTimelineElementOfNotification(String timelineEventCategory) {
        TimelineElementCategory timelineElementInternalCategory = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        for (int i = 0; i < 10; i++) {
            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
            try {
                Thread.sleep(31 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        try{
            Assertions.assertNotNull(timelineElement);
        }catch (AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }

    }

    @Then("la PA richiede il download dell'attestazione opponibile {string}")
    public void paRequiresDownloadOfLegalFact(String legalFactCategory) {
        downloadLegalFact(legalFactCategory,true,false,false);
    }


    @Then("viene richiesto tramite appIO il download dell'attestazione opponibile {string}")
    public void appIODownloadLegalFact(String legalFactCategory) {
        downloadLegalFact(legalFactCategory,false,true,false);
    }

    @Then("{string} richiede il download dell'attestazione opponibile {string}")
    public void userDownloadLegalFact(String user, String legalFactCategory) {
        sharedSteps.selectUser(user);
        downloadLegalFact(legalFactCategory,false,false,true);
    }

    private void downloadLegalFact(String legalFactCategory,boolean pa, boolean appIO, boolean webRecipient){
        try {
            Thread.sleep(10 * 1000L);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementCategory timelineElementInternalCategory;
        TimelineElement timelineElement;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK":
                timelineElementInternalCategory = TimelineElementCategory.REQUEST_ACCEPTED;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.SENDER_ACK;
                break;
            case "RECIPIENT_ACCESS":
                timelineElementInternalCategory = TimelineElementCategory.NOTIFICATION_VIEWED;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.RECIPIENT_ACCESS;
                break;
            case "PEC_RECEIPT":
                timelineElementInternalCategory = TimelineElementCategory.SEND_DIGITAL_PROGRESS;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.PEC_RECEIPT;
                break;
            case "DIGITAL_DELIVERY":
                timelineElementInternalCategory = TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.DIGITAL_DELIVERY;
                break;
            default:
                throw new IllegalArgumentException();
        }
        try {
            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            Assertions.assertEquals(category, timelineElement.getLegalFactsIds().get(0).getCategory());
            LegalFactCategory categorySearch = timelineElement.getLegalFactsIds().get(0).getCategory();
            String key = timelineElement.getLegalFactsIds().get(0).getKey();
            String keySearch = null;
            if (key.contains("PN_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_LEGAL_FACTS"));
            } else if (key.contains("PN_NOTIFICATION_ATTACHMENTS")) {
                keySearch = key.substring(key.indexOf("PN_NOTIFICATION_ATTACHMENTS"));
            } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
            }
            String finalKeySearch = keySearch;
            if (pa) {
                Assertions.assertDoesNotThrow(() -> this.b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch, finalKeySearch));
            }
            if (appIO) {
                Assertions.assertDoesNotThrow(() -> this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch.toString(), finalKeySearch,
                        sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));
            }
            if (webRecipient) {
                Assertions.assertDoesNotThrow(() -> this.webRecipientClient.getLegalFact(sharedSteps.getSentNotification().getIun(),
                        sharedSteps.deepCopy(categorySearch,
                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactCategory.class),
                        finalKeySearch
                ));
            }
        }catch (AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @Then("si verifica che la notifica abbia lo stato VIEWED")
    public void checksNotificationViewedStatus() {
        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        try {
            Assertions.assertNotNull(sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.VIEWED)).findAny().orElse(null));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono verificati costo = {string} e data di perfezionamento della notifica")
    public void notificationPriceAndDateVerification(String price) {
        priceVerification(price,"");
    }


    @Then("viene verificato il costo = {string} della notifica")
    public void notificationPriceVerification(String price) {
        priceVerification(price,null);
    }


    private void priceVerification(String price, String date){
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getNoticeCode());
        try {
            Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getSentNotification().getIun());
            if (price != null) {
                Assertions.assertEquals(notificationPrice.getAmount(), price);
            }
            if (date != null) {
                Assertions.assertNotNull(notificationPrice.getEffectiveDate());
            }
        }catch (AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("{string} legge la notifica ricevuta")
    public void userReadReceivedNotification(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
        try {
            Thread.sleep(50 * 1000L);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }


    @Then("viene verificato che la chiave dell'attestazione opponibile {string} è {string}")
    public void verifiedThatTheKeyOfTheLegalFactIs(String legalFactCategory, String key) {
        try {
            Thread.sleep(10 * 1000L);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementCategory timelineElementInternalCategory;
        TimelineElement timelineElement;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK":
                timelineElementInternalCategory = TimelineElementCategory.REQUEST_ACCEPTED;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.SENDER_ACK;
                break;
            case "RECIPIENT_ACCESS":
                timelineElementInternalCategory = TimelineElementCategory.NOTIFICATION_VIEWED;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.RECIPIENT_ACCESS;
                break;
            case "PEC_RECEIPT":
                timelineElementInternalCategory = TimelineElementCategory.SEND_DIGITAL_PROGRESS;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.PEC_RECEIPT;
                break;
            case "DIGITAL_DELIVERY":
                timelineElementInternalCategory = TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.DIGITAL_DELIVERY;
                break;
            default:
                throw new IllegalArgumentException();
        }
        try {
            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertEquals(category, timelineElement.getLegalFactsIds().get(0).getCategory());
            Assertions.assertTrue(timelineElement.getLegalFactsIds().get(0).getKey().contains(key));
        }catch (AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("l'avviso pagopa viene pagato correttamente")
    public void laNotificaVienePagata() {
        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
        paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getNoticeCode());
        paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getCreditorTaxId());
        paymentEventPagoPa.setPaymentDate(OffsetDateTime.now());

        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
    }

    @Then("il modello f24 viene pagato correttamente")
    public void ilModelloFVienePagatoCorrettamente() {
        PaymentEventsRequestF24 eventsRequestF24 = new PaymentEventsRequestF24();

        PaymentEventF24 paymentEventF24 = new PaymentEventF24();
        paymentEventF24.setIun(sharedSteps.getSentNotification().getIun());
        paymentEventF24.setRecipientTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getTaxId());
        paymentEventF24.setRecipientType(sharedSteps.getSentNotification().getRecipients().get(0).getRecipientType().equals(NotificationRecipient.RecipientTypeEnum.PF)? "PF":"PG");
        paymentEventF24.setPaymentDate(OffsetDateTime.now());

        List<PaymentEventF24> eventF24List = new LinkedList<>();
        eventF24List.add(paymentEventF24);

        eventsRequestF24.setEvents(eventF24List);

        b2bClient.paymentEventsRequestF24(eventsRequestF24);
    }

    @And("si verifica che il timelineId dell'elemento {string} corrisponda a quello di {string}")
    public void siVerificaCheLaDataDellElementoCorrispondaAQuelloDi(String timelineElementCategory, String timelineElementCategoryToCompare) {
        TimelineElementCategory timelineElementCatFirst = getTimelineElementCategory(timelineElementCategory);
        TimelineElementCategory timelineElementCatSecond = getTimelineElementCategory(timelineElementCategoryToCompare);

        TimelineElement timelineElementFirst = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementCatFirst)).findAny().orElse(null);
        TimelineElement timelineElementSecond = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementCatSecond)).findAny().orElse(null);
        System.out.println("timelineElementFirst");
        System.out.println(timelineElementFirst);
        System.out.println("timelineElementSecond");
        System.out.println(timelineElementSecond);
    }


       /*
    UTILE PER TEST 

    @Given("viene vista la pec per l'utente {string}")
    public void vieneRimossaLaPecPerLUtente(String arg0) {
        webUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
        List<LegalDigitalAddress> legalAddressByRecipient = webUserAttributesClient.getLegalAddressByRecipient();
        System.out.println(legalAddressByRecipient);
        webUserAttributesClient.deleteRecipientLegalAddress("default",LegalChannelType.PEC);
        webUserAttributesClient.postRecipientLegalAddress("default", LegalChannelType.PEC,
                (new AddressVerification().verificationCode("17947").value("test@fail.it")));
    }


    @Given("viene {string} l'app IO per {string}")
    public void vieneLAppIOPer(String onOff, String recipient) {
        webUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);

        //IoCourtesyDigitalAddressActivation ioCourtesyDigitalAddressActivation = new IoCourtesyDigitalAddressActivation();
        //ioCourtesyDigitalAddressActivation.setActivationStatus(onOff.equalsIgnoreCase("abilitata")?true:false);
        //ioUserAttributerExternaClient.setCourtesyAddressIo(selectTaxIdUser(recipient),ioCourtesyDigitalAddressActivation);
        System.out.println("STATUS IO: "+ioUserAttributerExternaClient.getCourtesyAddressIo(selectTaxIdUser(recipient)));
    }

     */
}
