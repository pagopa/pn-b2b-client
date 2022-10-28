package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPnAppIOB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebRecipientClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;

public class AvanzamentoNotificheB2b {

    @Autowired
    IPnPaB2bClient b2bClient;

    @Autowired
    private SharedSteps sharedSteps;

    @Autowired
    private IPnAppIOB2bClient appIOB2bClient;

    @Autowired
    private IPnWebRecipientClient webRecipientClient;


    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Then("vengono letti gli eventi fino allo stato della notifica {string}")
    public void vengonoLettiGliEventiDelloStreamFinoAlloStatoDellaNotifica(String status) {
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
            default:
                throw new IllegalArgumentException();
        }

        NotificationStatusHistoryElement notificationStatusHistoryElement = null;

        for (int i = 0; i < 20; i++) {
            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_STATUS_HISTORY: " + sharedSteps.getSentNotification().getNotificationStatusHistory());

            notificationStatusHistoryElement = sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

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
            case "SEND_DIGITAL_PROGRESS":
                timelineElementInternalCategory = TimelineElementCategory.SEND_DIGITAL_PROGRESS;
                break;
            default:
                throw new IllegalArgumentException();
        }
        TimelineElement timelineElement = null;

        for (int i = 0; i < 20; i++) {
            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
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
        Assertions.assertNotNull(timelineElement.getLegalFactsIds());
        Assertions.assertEquals(category,timelineElement.getLegalFactsIds().get(0).getCategory());
        LegalFactCategory categorySearch = timelineElement.getLegalFactsIds().get(0).getCategory();
        String key = timelineElement.getLegalFactsIds().get(0).getKey();
        String keySearch = key.substring(key.indexOf("PN_LEGAL_FACTS"));
        if(pa){
            Assertions.assertDoesNotThrow(()->this.b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(),categorySearch , keySearch));
        }
        if(appIO){
            Assertions.assertDoesNotThrow(()->this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(),categorySearch.toString(), keySearch,
                    sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));
        }
    }

    @Then("si verifica che la notifica abbia lo stato VIEWED")
    public void siVerificaCheLaNotificaAbbiaLoStatoVIEWED() {
        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        Assertions.assertNotNull(sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.VIEWED)).findAny().orElse(null));
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
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getNoticeCode());
        Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getSentNotification().getIun());
        if(price != null){
            Assertions.assertEquals(notificationPrice.getAmount(),price);
        }
        if(date != null){
            Assertions.assertNotNull(notificationPrice.getEffectiveDate());
        }
    }

    @And("il destinatario legge la notifica ricevuta")
    public void ilDestinatarioLeggeLaNotificaRicevuta() {
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
        try {
            Thread.sleep(50 * 1000L);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }
}
