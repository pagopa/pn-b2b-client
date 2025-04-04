package it.pagopa.pn.cucumber.steps.pa.b2bVersions;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceStatusRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceTimelineSlowV23;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.time.OffsetDateTime.now;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class B2bStepsV23 implements B2bStepsInterface {
    private TimelineElementV23 timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
    private final NotificationVersion version;
    private final AvanzamentoNotificheB2bSteps b2bSteps;

    public B2bStepsV23(AvanzamentoNotificheB2bSteps b2bSteps) {
        version = NotificationVersion.V23;
        this.b2bSteps = b2bSteps;
    }

    @Override
    public Object getFullSentNotification() {
        return b2bSteps.getB2bClient().getSentNotificationV23(b2bSteps.getSharedSteps().getNotificationIun());
    }

    private FullSentNotificationV23 getFullSentNotificationVersioned() {
        return (FullSentNotificationV23) getFullSentNotification();
    }

    @Override
    public void readEventsUpToTimelineElement(String timelineEventCategory) {
        PnPollingServiceTimelineSlowV23 timelineSlow =
                (PnPollingServiceTimelineSlowV23) b2bSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.TIMELINE_SLOW_V23);
        PnPollingResponseV23 pnPollingResponse = timelineSlow.waitForEvent(
                b2bSteps.getSharedSteps().getNotificationIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
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
        PnPollingServiceStatusRapidV23 statusRapid = (PnPollingServiceStatusRapidV23) b2bSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.STATUS_RAPID_V23);
        PnPollingResponseV23 pnPollingResponse = statusRapid.waitForEvent(iun,
                PnPollingParameter.builder()
                        .value(status)
                        .build());
        log.info("NOTIFICATION_STATUS_HISTORY V23: " + pnPollingResponse.getNotification().getNotificationStatusHistory());
        try {
            assertThat(pnPollingResponse.getResult())
                    .as("Il risultato del polling deve essere valorizzato")
                    .isTrue();
            assertThat(pnPollingResponse.getNotificationStatusHistoryElement())
                    .as("L'elemento dello storico degli stati non dovrebbe essere nullo")
                    .isNotNull();
            notificationStatusHistoryElement = pnPollingResponse.getNotificationStatusHistoryElement();
            log.info("NOTIFICATION_STATUS_HISTORY_ELEMENT V23: " + notificationStatusHistoryElement);
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
        FullSentNotificationV23 fullSentNotification = b2bSteps.getB2bClient().getSentNotificationV23(iun);
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
        FullSentNotificationV23 fullSentNotification = b2bSteps.getB2bClient().getSentNotificationV23(iun);
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

    @Override
    public void checkForNoDuplicatedTimelineElements(String timelineEventCategory) {
        int counter = (int) getFullSentNotificationVersioned().getTimeline().stream().filter(te ->
                te.getCategory().getValue().equals(timelineEventCategory)).count();
        try {
            assertThat(counter <= 1)
                    .as("L'elemento di timeline " + timelineEventCategory + " dovrebbe essere presente massimo una volta, invece risulta ve ne siano " + counter)
                    .isTrue();
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void checkIfLastAttemptIndexMatch(int index) {
        try {
            List<TimelineElementV23> actualTimelineElements = getFullSentNotificationVersioned().getTimeline().stream()
                    .filter(elem -> nonNull(elem.getDetails()))
                    //TODO: ignorare Sonar che dice che questo nonNull è inutile in quanto sempre true, non è vero
                    .filter(elem -> nonNull(elem.getDetails().getSentAttemptMade()))
                    .filter(elem -> elem.getDetails().getSentAttemptMade() <= index)
                    .toList();
            List<Integer> actualAttemptsMade = actualTimelineElements.stream()
                    .map(TimelineElementV23::getDetails)
                    .filter(Objects::nonNull)
                    .map(TimelineElementDetailsV23::getSentAttemptMade)
                    .distinct()
                    .toList();
            List<Integer> expectedAttemptsMade = IntStream.range(0, index + 1)
                    .boxed()
                    .toList();
            assertThat(actualAttemptsMade)
                    .as("Non è stato trovato alcun elemento di timeline corrispondente a un tentativo di indice minore o uguale a '%d'.".formatted(index))
                    .isNotEmpty()
                    .as("I tentativi effettuati non corrispondono a quelli attesi.")
                    .hasSameElementsAs(expectedAttemptsMade);
        } catch (AssertionError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void verifyPriceAndWeightInvioCartaceo(Integer price, Integer weight) {
        try {
            if (price != null) {
                assertThat(timelineElement.getDetails().getAnalogCost())
                        .as("Il costo differisce da quanto previsto (expected: + " + price + "actual:" + timelineElement.getDetails().getAnalogCost())
                        .isEqualTo(price);
            }
            if (weight != null) {
                assertThat(timelineElement.getDetails().getEnvelopeWeight())
                        .as("Il peso differisce da quanto previsto (expected: + " + weight + "actual:" + timelineElement.getDetails().getEnvelopeWeight())
                        .isEqualTo(price);
            }
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }
}