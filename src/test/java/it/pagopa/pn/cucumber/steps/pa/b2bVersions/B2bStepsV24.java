package it.pagopa.pn.cucumber.steps.pa.b2bVersions;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingPredicate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceStatusRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceTimelineSlowV26;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationStepsV24;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion;
import it.pagopa.pn.cucumber.steps.utilitySteps.WaitForEventPredicateFilters;
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
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Slf4j
public class B2bStepsV24 implements B2bStepsInterface {

    private TimelineElementV26 timelineElement;
    private NotificationStatusHistoryElementV26 notificationStatusHistoryElement;
    private PnPollingResponseV26 pollingResponse;
    private final NotificationVersion version;
    private final AvanzamentoNotificheB2bSteps b2bSteps;

    public B2bStepsV24(AvanzamentoNotificheB2bSteps b2bSteps) {
        version = NotificationVersion.V24;
        this.b2bSteps = b2bSteps;
    }

    @Override
    public Object getFullSentNotification() {
        return b2bSteps.getB2bClient().getSentNotificationV26(b2bSteps.getSharedSteps().getNotificationIun());
    }

    private FullSentNotificationV26 getFullSentNotificationVersioned() {
        return (FullSentNotificationV26) getFullSentNotification();
    }

    @Override
    public void readEventsUpToTimelineElement(String timelineEventCategory) {
        PnPollingServiceTimelineSlowV26 timelineSlow =
                (PnPollingServiceTimelineSlowV26) b2bSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.TIMELINE_SLOW_V26);
        PnPollingResponseV26 pnPollingResponse = timelineSlow.waitForEvent(
                b2bSteps.getSharedSteps().getNotificationIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());

//        fullSentNotification = pnPollingResponse.getNotification();//TODO MATTEO TEST
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponse.getNotification().getTimeline());
        try {
            assertThat(pnPollingResponse.getResult())
                    .as("Il risultato del polling dovrebbe essere valorizzato, Primo controllo: Verificare che l'elemento sia presente in timeline e le tempistiche con cui viene prodotto")
                    .isTrue();
            assertThat(pnPollingResponse.getTimelineElement())
                    .as("L'elemento della timeline non dovrebbe essere nullo")
                    .isNotNull();
            timelineElement = pnPollingResponse.getTimelineElement();
            b2bSteps.setTimelineElement(timelineElement);//TODO MATTEO: L'IDEALE SAREBBE RIMUOVERLO DA CAMPO DI B2B STEPS e prendere sempre quello restituito qua
            log.info("TIMELINE_ELEMENT: " + timelineElement);
        } catch (AssertionError assertionError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Override
    public void readEventsUpToStatus(String status) {
        String iun = b2bSteps.getSharedSteps().getNotificationIun();
        PnPollingServiceStatusRapidV26 statusRapid = (PnPollingServiceStatusRapidV26) b2bSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.STATUS_RAPID_V26);
        PnPollingResponseV26 pnPollingResponse = statusRapid.waitForEvent(iun,
                PnPollingParameter.builder()
                        .value(status)
                        .build());
//        fullSentNotification = pnPollingResponse.getNotification();//TODO MATTEO TEST
        log.info("NOTIFICATION_STATUS_HISTORY V26: " + pnPollingResponse.getNotification().getNotificationStatusHistory());
        try {
            assertThat(pnPollingResponse.getResult())
                    .as("Il risultato del polling deve essere valorizzato")
                    .isTrue();
            assertThat(pnPollingResponse.getNotificationStatusHistoryElement())
                    .as("L'elemento dello storico degli stati non dovrebbe essere nullo")
                    .isNotNull();
            notificationStatusHistoryElement = pnPollingResponse.getNotificationStatusHistoryElement();
            log.info("NOTIFICATION_STATUS_HISTORY_ELEMENT V26: " + notificationStatusHistoryElement);
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
        FullSentNotificationV26 fullSentNotification = b2bSteps.getB2bClient().getSentNotificationV26(iun);
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
        FullSentNotificationV26 fullSentNotification = b2bSteps.getB2bClient().getSentNotificationV26(iun);
        String creditorTaxId = fullSentNotification.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().getCreditorTaxId();
        String noticeCode = fullSentNotification.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().getNoticeCode();
        NotificationPriceResponseV23 notificationPrice = b2bSteps.getB2bClient().getNotificationPriceV23(creditorTaxId, noticeCode);

        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
        paymentEventPagoPa.setCreditorTaxId(creditorTaxId);
        paymentEventPagoPa.setNoticeCode(noticeCode);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(now()));
        paymentEventPagoPa.setAmount(notificationPrice.getTotalPrice());
        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
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
            List<TimelineElementV26> actualTimelineElements = getFullSentNotificationVersioned().getTimeline().stream()
                    .filter(elem -> nonNull(elem.getDetails()))
                    //TODO: ignorare Sonar che dice che questo nonNull è inutile in quanto sempre true, non è vero
                    .filter(elem -> nonNull(elem.getDetails().getSentAttemptMade()))
                    .filter(elem -> elem.getDetails().getSentAttemptMade() <= index)
                    .toList();
            List<Integer> actualAttemptsMade = actualTimelineElements.stream()
                    .map(TimelineElementV26::getDetails)
                    .filter(Objects::nonNull)
                    .map(TimelineElementDetailsV26::getSentAttemptMade)
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

//    @Override
//    public void waitForEvent(String pollingStrategy,
//                             String timelineEventCategory,
//                             Integer destinatario,
//                             String deliveryDetailCode,
//                             String attempt,
//                             String tipoDoc,
//                             String responseStatus,
//                             boolean isF24,
//                             boolean isLegalFactEmpty,
//                             String legalFactIdCategory,
//                             boolean isAttachmentEmpty,
//                             List<String> failureCauses) {
//
//        String strategy = NotificationStepsV24.getPollingStrategy(pollingStrategy, version);
//        IPnPollingService pollingService = b2bSteps.getSharedSteps().getB2bUtils().getPollingFactory().getPollingService(strategy);
//        PnPollingPredicate pollingPredicate = getPnPollingPredicateForTimeline(
//                timelineEventCategory,
//                destinatario,
//                deliveryDetailCode,
//                attempt,
//                tipoDoc,
//                responseStatus,
//                isF24,
//                isLegalFactEmpty,
//                legalFactIdCategory,
//                isAttachmentEmpty,
//                failureCauses);
//        PnPollingResponseV26 pnPollingResponse = (PnPollingResponseV26) pollingService.waitForEvent(
//                b2bSteps.getSharedSteps().getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(pollingPredicate)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponse.getNotification().getTimeline());
//    }

    @Override
    public void waitForEvent(String pollingStrategy, String timelineEventCategory, WaitForEventPredicateFilters filters) {
        String strategy = NotificationStepsV24.getPollingStrategy(pollingStrategy, version);
        IPnPollingService pollingService = b2bSteps.getSharedSteps().getB2bUtils().getPollingFactory().getPollingService(strategy);
        PnPollingPredicate pollingPredicate = getPnPollingPredicateForTimeline(timelineEventCategory, filters);
        pollingResponse = (PnPollingResponseV26) pollingService.waitForEvent(
                b2bSteps.getSharedSteps().getNotificationIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(pollingPredicate)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pollingResponse.getNotification().getTimeline());
    }

//    public PnPollingPredicate getPnPollingPredicateForTimeline(
//            String timelineEventCategory,
//            Integer recipientIndex,
//            String deliveryDetailCode,
//            String attempt,
//            String tipoDoc,
//            String responseStatus,
//            boolean isF24,
//            boolean isLegalFactEmpty,
//            String legalFactIdCategory,
//            boolean isAttachmentEmpty,
//            List<String> failureCauses) {
//
//        PnPollingPredicate pnPollingPredicate = new PnPollingPredicate();
//        pnPollingPredicate.setTimelineElementPredicateV26(timelineElement ->
//                timelineElement.getCategory() != null
//                        && (timelineEventCategory == null || Objects.requireNonNull(timelineElement.getCategory().getValue()).equals(timelineEventCategory))
//                        && (recipientIndex == null || Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getRecIndex()).equals(recipientIndex))
//                        && (deliveryDetailCode == null || Objects.equals(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryDetailCode(), deliveryDetailCode))
//                        && (attempt == null || Objects.requireNonNull(timelineElement.getElementId()).contains(attempt))
//                        && (tipoDoc == null || Objects.equals(Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments()).get(0).getDocumentType(), tipoDoc))
//                        && (responseStatus == null || Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getResponseStatus().getValue()).equals(responseStatus))
//                        && (!isF24 || Objects.requireNonNull(timelineElement.getDetails()).getIdF24() != null)
//                        && (!isLegalFactEmpty || Objects.nonNull(timelineElement.getLegalFactsIds()) && !timelineElement.getLegalFactsIds().isEmpty())
//                        && (legalFactIdCategory == null || Objects.requireNonNull(Objects.requireNonNull(timelineElement.getLegalFactsIds()).get(0)).getCategory().equals(legalFactIdCategory))
//                        && (!isAttachmentEmpty || Objects.nonNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments()) && !timelineElement.getDetails().getAttachments().isEmpty())
//                        && (legalFactIdCategory == null || failureCauses.contains(Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryFailureCause())))
//        );
//        return pnPollingPredicate;
//    }


    public PnPollingPredicate getPnPollingPredicateForTimeline(String timelineEventCategory, WaitForEventPredicateFilters filters) {
        PnPollingPredicate pnPollingPredicate = new PnPollingPredicate();
        pnPollingPredicate.setTimelineElementPredicateV26(timelineElement ->
                timelineElement.getCategory() != null
                        && (timelineEventCategory == null || Objects.requireNonNull(timelineElement.getCategory().getValue()).equals(timelineEventCategory))
                        && (filters.getRecipientIndex() == null || Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getRecIndex()).equals(filters.getRecipientIndex()))
                        && (filters.getDeliveryDetailCode() == null || Objects.equals(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryDetailCode(), filters.getDeliveryDetailCode()))
                        && (filters.getAttempt() == null || Objects.requireNonNull(timelineElement.getElementId()).contains(filters.getAttempt()))
                        && (filters.getDocumentType() == null || Objects.equals(Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments()).get(0).getDocumentType(), filters.getDocumentType()))
                        && (filters.getResponseStatus() == null || Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getResponseStatus().getValue()).equals(filters.getResponseStatus()))
                        && (!filters.isF24() || Objects.requireNonNull(timelineElement.getDetails()).getIdF24() != null)
                        && (!filters.isLegalFactEmpty() || Objects.nonNull(timelineElement.getLegalFactsIds()) && !timelineElement.getLegalFactsIds().isEmpty())
                        && (filters.getLegalFactIdCategory() == null || Objects.requireNonNull(Objects.requireNonNull(timelineElement.getLegalFactsIds()).get(0)).getCategory().equals(filters.getLegalFactIdCategory()))
                        && (!filters.isAttachmentEmpty() || Objects.nonNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments()) && !timelineElement.getDetails().getAttachments().isEmpty())
                        && (filters.getLegalFactIdCategory() == null || filters.getFailureCauses().contains(Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryFailureCause())))
        );
        return pnPollingPredicate;
    }

    @Override
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(boolean success) {
        String iun = b2bSteps.getSharedSteps().getNotificationIun() != null ? b2bSteps.getSharedSteps().getNotificationIun() : "UNKNOWN";
        try {
            if (success) {
                assertSoftly(softly -> {
                    softly.assertThat(pollingResponse.getResult())
                            .as("Verifica che il polling abbia avuto successo per IUN: " + iun)
                            .isTrue();
                    softly.assertThat(pollingResponse.getTimelineElement())
                            .as("Verifica che l'elemento di timeline esista per IUN: " + iun)
                            .isNotNull();
                });
                if (pollingResponse.getTimelineElement() != null) {
                    log.info("TIMELINE_ELEMENT: {}", pollingResponse.getTimelineElement());
                }
            } else {
                Assertions.assertTrue(pollingResponse.getResult(), "Polling failed. IUN: " + iun);
                Assertions.assertNotNull(pollingResponse.getTimelineElement(), "Timeline element not found. IUN: " + iun);
                log.info("TIMELINE_ELEMENT: {}", pollingResponse.getTimelineElement());
            }
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(int delay) {
        try {
            Assertions.assertTrue(pollingResponse.getResult());
            Assertions.assertNotNull(pollingResponse.getTimelineElement());
            TimelineElementV26 timelineElement = pollingResponse.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getNotificationDate().format(fmt), now().plusDays(delay).format(fmt));
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeWithoutSuccess() {
        try {
            Assertions.assertFalse(pollingResponse.getResult());
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifySchedulingDate(int delay, String tipoIncremento) {
        try {
            Assertions.assertTrue(pollingResponse.getResult());
            Assertions.assertNotNull(pollingResponse.getTimelineElement());
            TimelineElementV26 timelineElement = pollingResponse.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            OffsetDateTime digitalDeliveryCreationRequestDate = Objects.requireNonNull(timelineElement).getTimestamp();
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getSchedulingDate());
            Assertions.assertNotNull(tipoIncremento);
            if ("d".equalsIgnoreCase(tipoIncremento)) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate().format(fmt), Objects.requireNonNull(digitalDeliveryCreationRequestDate).plusDays(delay).format(fmt));
            } else if ("m".equalsIgnoreCase(tipoIncremento)) {
                DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate().format(fmt1), Objects.requireNonNull(digitalDeliveryCreationRequestDate).plusMinutes(delay).format(fmt1));
            }
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeVerifyTypeDoc(String documentType, boolean withAttempt) {
        try {
            Assertions.assertTrue(pollingResponse.getResult());
            Assertions.assertNotNull(pollingResponse.getTimelineElement());
            TimelineElementV26 timelineElement = pollingResponse.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments());
            Assertions.assertFalse(timelineElement.getDetails().getAttachments().isEmpty());
            Assertions.assertNotNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType());
            if (withAttempt) {
                Assertions.assertTrue(
                        Objects.equals(timelineElement.getDetails().getAttachments().get(0).getDocumentType(), documentType)
                                || Objects.equals(timelineElement.getDetails().getAttachments().get(0).getDocumentType(), "Indagine"));
            } else {
                Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType()), documentType);
            }
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeDeliveryFailureCause(String failureCause) {
        try {
            Assertions.assertTrue(pollingResponse.getResult());
            Assertions.assertNotNull(pollingResponse.getTimelineElement());
            TimelineElementV26 timelineElement = pollingResponse.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryFailureCause(), failureCause);
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void vieneVerificatoCampoSendRequestIdEventoTimeline() {
        try {
            Assertions.assertTrue(pollingResponse.getResult());
            Assertions.assertNotNull(pollingResponse.getTimelineElement());
            TimelineElementV26 timelineElement = pollingResponse.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails());
            Assertions.assertNotNull(timelineElement.getDetails().getSendRequestId());
            String sendRequestId = timelineElement.getDetails().getSendRequestId();
            TimelineElementV26 timelineElementRelative = pollingResponse
                    .getNotification()
                    .getTimeline()
                    .stream()
                    .filter(elem -> Objects.requireNonNull(elem.getElementId()).equals(sendRequestId))
                    .findAny()
                    .orElse(null);
            Assertions.assertNotNull(timelineElementRelative);
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void vieneVerificatoCampoServiceLevelEventoTimeline(String value) {
        try {
            Assertions.assertTrue(pollingResponse.getResult());
            ServiceLevel level = switch (value) {
                case "AR_REGISTERED_LETTER" -> ServiceLevel.AR_REGISTERED_LETTER;
                case "REGISTERED_LETTER_890" -> ServiceLevel.REGISTERED_LETTER_890;
                default -> throw new IllegalArgumentException();
            };
            Assertions.assertNotNull(pollingResponse.getTimelineElement());
            TimelineElementV26 timelineElement = pollingResponse.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails());
            Assertions.assertEquals(timelineElement.getDetails().getServiceLevel(), level);
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }
}
