package it.pagopa.pn.cucumber.steps.pa.b2bVersions;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationPriceResponseV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.PaymentEventsRequestPagoPa;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusRapidV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineSlowV21;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class B2bStepsV21 implements B2bStepsInterface {

    private FullSentNotificationV21 fullSentNotification;
    private TimelineElementV20 timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
    private final SharedSteps.NotificationVersion version;
    private final AvanzamentoNotificheB2bSteps b2bSteps;

    public B2bStepsV21(AvanzamentoNotificheB2bSteps b2bSteps) {
        version = SharedSteps.NotificationVersion.V21;
        this.b2bSteps = b2bSteps;
    }

    @Override
    public void readEventsUpToTimelineElement(String timelineEventCategory) {
        PnPollingServiceTimelineSlowV21 timelineSlow =
                (PnPollingServiceTimelineSlowV21) b2bSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.TIMELINE_SLOW_V21);
        PnPollingResponseV21 pnPollingResponse = timelineSlow.waitForEvent(
                b2bSteps.getSharedSteps().getNotificationIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());

        fullSentNotification = pnPollingResponse.getNotification();
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponse.getNotification().getTimeline());
        try {
            assertThat(pnPollingResponse.getResult())
                    .as("Il risultato del polling dovrebbe essere valorizzato, Primo controllo: Verificare che l'elemento sia presente in timeline e le tempistiche con cui viene prodotto")
                    .isTrue();
            assertThat(pnPollingResponse.getTimelineElement())
                    .as("L'elemento della timeline non dovrebbe essere nullo")
                    .isNotNull();
            timelineElement = pnPollingResponse.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
        } catch (AssertionError assertionError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Override
    public void readEventsUpToStatus(String status) {
        String iun = b2bSteps.getSharedSteps().getNotificationIun();
        PnPollingServiceStatusRapidV21 statusRapid = (PnPollingServiceStatusRapidV21) b2bSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.STATUS_RAPID_V21);
        PnPollingResponseV21 pnPollingResponse = statusRapid.waitForEvent(iun,
                PnPollingParameter.builder()
                        .value(status)
                        .build());

        fullSentNotification = pnPollingResponse.getNotification();
        log.info("NOTIFICATION_STATUS_HISTORY V21: " + pnPollingResponse.getNotification().getNotificationStatusHistory());
        try {
            assertThat(pnPollingResponse.getResult())
                    .as("Il risultato del polling deve essere valorizzato")
                    .isTrue();
            assertThat(pnPollingResponse.getNotificationStatusHistoryElement())
                    .as("L'elemento dello storico degli stati non dovrebbe essere nullo")
                    .isNotNull();
            notificationStatusHistoryElement = pnPollingResponse.getNotificationStatusHistoryElement();
            log.info("NOTIFICATION_STATUS_HISTORY_ELEMENT V21: " + notificationStatusHistoryElement);
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void checkNotificationCost(String cost) {
        Long notificationCost = timelineElement.getDetails().getNotificationCost();
        if (cost.equalsIgnoreCase("null")) {
            Assertions.assertNull(notificationCost);
        } else {
            Assertions.assertEquals(Long.parseLong(cost), notificationCost);
        }
    }

    @Override
    public void checkNormalizedAddress(DataTable table) {
        log.info("indirizzo: {}", timelineElement.getDetails().getOldAddress());
        log.info("indirizzo Normalizzato: {}", timelineElement.getDetails().getNormalizedAddress());
        try {
            Assertions.assertEquals(b2bSteps.mapValueFromTable(table, "physicalAddress_address"), timelineElement.getDetails().getNormalizedAddress().getAddress());
            Assertions.assertEquals(b2bSteps.mapValueFromTable(table, "at"), timelineElement.getDetails().getNormalizedAddress().getAt());
            Assertions.assertEquals(b2bSteps.mapValueFromTable(table, "physicalAddress_addressDetails"), timelineElement.getDetails().getNormalizedAddress().getAddressDetails());
            Assertions.assertEquals(b2bSteps.mapValueFromTable(table, "physicalAddress_zip"), timelineElement.getDetails().getNormalizedAddress().getZip());
            Assertions.assertEquals(b2bSteps.mapValueFromTable(table, "physicalAddress_municipality"), timelineElement.getDetails().getNormalizedAddress().getMunicipality());
            Assertions.assertEquals(b2bSteps.mapValueFromTable(table, "physicalAddress_municipalityDetails"), timelineElement.getDetails().getNormalizedAddress().getMunicipalityDetails());
            Assertions.assertEquals(b2bSteps.mapValueFromTable(table, "physicalAddress_province"), timelineElement.getDetails().getNormalizedAddress().getProvince());
            Assertions.assertEquals(b2bSteps.mapValueFromTable(table, "physicalAddress_State"), timelineElement.getDetails().getNormalizedAddress().getForeignState());

        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void checkEventPresenceForRecipient(int recipientIndex, String evento) {
        try {
            List<String> timelineElements = notificationStatusHistoryElement.getRelatedTimelineElements();
            boolean esiste = false;
            for (String tmpTimeline : timelineElements) {
                if (tmpTimeline.contains(evento) && tmpTimeline.contains("RECINDEX_" + recipientIndex)) {
                    esiste = true;
                    break;
                }
            }
            Assertions.assertTrue(esiste);
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void checkPriceForRecipient(int recipientIndex, String price) {
        String iun = b2bSteps.getSharedSteps().getNotificationIun();
        FullSentNotificationV21 fullSentNotification = b2bSteps.getB2bClient().getSentNotificationV21(iun);
        List<NotificationPaymentItem> listNotificationPaymentItem = fullSentNotification.getRecipients().get(recipientIndex).getPayments();
        if (listNotificationPaymentItem != null) {
            for (NotificationPaymentItem notificationPaymentItem : listNotificationPaymentItem) {
                NotificationPriceResponse notificationPrice = b2bSteps.getB2bClient().getNotificationPrice(
                        notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());
                try {
                    Assertions.assertEquals(notificationPrice.getIun(), iun);
                    if (price != null) {
                        log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), recipientIndex);
                        Assertions.assertEquals(Integer.parseInt(price), notificationPrice.getAmount());
                    }
                    if (notificationPrice.getRefinementDate() != null) {
                        Assertions.assertEquals(OffsetDateTime.now().toLocalDate(), notificationPrice.getRefinementDate().toLocalDate());
                    }
                } catch (AssertionFailedError assertionFailedError) {
                    b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
                }
            }
        }
    }

    @Override
    public void payAvvisoPagoPa(Integer paymentIndex, Integer recipientIndex) {
        String iun = b2bSteps.getSharedSteps().getNotificationIun();
        FullSentNotificationV21 fullSentNotification = b2bSteps.getB2bClient().getSentNotificationV21(iun);
        String creditorTaxId = fullSentNotification.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().getCreditorTaxId();
        String noticeCode = fullSentNotification.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().getNoticeCode();
        NotificationPriceResponseV23 notificationPrice = b2bSteps.getB2bClient().getNotificationPriceV23(creditorTaxId, noticeCode);

        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.PaymentEventPagoPa paymentEventPagoPa =
                new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.PaymentEventPagoPa();
        paymentEventPagoPa.setCreditorTaxId(creditorTaxId);
        paymentEventPagoPa.setNoticeCode(noticeCode);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(now()));
        paymentEventPagoPa.setAmount(notificationPrice.getTotalPrice());
        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);
        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bSteps.getB2bClient().paymentEventsRequestPagoPa(eventsRequestPagoPa);
    }
}
