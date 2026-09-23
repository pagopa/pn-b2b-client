package it.pagopa.pn.cucumber.steps.informalNotification.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.FullSentInformalNotificationV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementDetailsV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewInformalNotificationRequestStatusResponseV1;
import it.pagopa.pn.cucumber.steps.informalNotification.datatest.InformalStatusPollingConfig;
import it.pagopa.pn.cucumber.steps.informalNotification.datatest.InformalTimelinePollingConfig;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
//Per aggiungere nuovi campi aggiornare anche la classe: InformalDataTestV1
public class NotificationInformalUtilsWorkFlowV1 {

    public static void checkTimelineElement(InformalTimelineElementV1 actual, InformalTimelineElementV1 expected) {

        InformalTimelineElementDetailsV1 actualDetails = actual.getDetails();
        InformalTimelineElementDetailsV1 expectedDetails = expected.getDetails();

        if (expectedDetails.getRecIndex() != null) {
            assertEquals(expectedDetails.getRecIndex(), actualDetails.getRecIndex());
        }
        if (expectedDetails.getResponseStatus() != null) {
            assertEquals(expectedDetails.getResponseStatus(), actualDetails.getResponseStatus());
        }
        if (expectedDetails.getDigitalAddressSource() != null) {
            assertEquals(expectedDetails.getDigitalAddressSource(), actualDetails.getDigitalAddressSource());
        }
        if (expectedDetails.getSentAttemptMade() != null) {
            assertEquals(expectedDetails.getSentAttemptMade(), actualDetails.getSentAttemptMade());
        }
        if (expectedDetails.getDigitalAddress() != null) {
            assertEquals(expectedDetails.getDigitalAddress(), actualDetails.getDigitalAddress());
        }
        if (expectedDetails.getSourceElementId() != null) {
            assertEquals(expectedDetails.getSourceElementId(), actualDetails.getSourceElementId());
        }
        if (expectedDetails.getChannel() != null) {
            assertEquals(expectedDetails.getChannel(), actualDetails.getChannel());
        }
        if (expectedDetails.getDeliveryDetail() != null && expectedDetails.getDeliveryDetail().getFailureCause() != null) {
            assertEquals(expectedDetails.getDeliveryDetail().getFailureCause(), actualDetails.getDeliveryDetail().getFailureCause());
        }
    }

    public static List<InformalTimelineElementV1> getTimelineElementsByCategory(FullSentInformalNotificationV1 notification, String category) {
        if (notification == null || notification.getTimeline() == null) {
            return Collections.emptyList();
        }
        return notification.getTimeline().stream().filter(t -> t.getCategory() != null && category.equals(t.getCategory().getValue())).toList();
    }

    public static List<InformalTimelineElementV1> waitForTimelineElementsByCategory(Supplier<FullSentInformalNotificationV1> notificationSupplier, String category) {
        Duration timeout = getTimeout(category);
        Duration pollInterval = getPollingInterval(category);

        AtomicReference<List<InformalTimelineElementV1>> foundElements = new AtomicReference<>(Collections.emptyList());
        await().atMost(timeout).pollInterval(pollInterval).until(() -> {
            FullSentInformalNotificationV1 notification = notificationSupplier.get();
            List<InformalTimelineElementV1> elements = getTimelineElementsByCategory(notification, category);
            foundElements.set(elements);
            return !elements.isEmpty();
        });
        return foundElements.get();
    }

    public static Duration getTimeout(String category) {
        InformalTimelinePollingConfig.DefaultElementTimeValue config = getPollingConfig(category);
        return Duration.ofSeconds(config.getNumCheck() * config.getWaitingMultiplier());
    }

    public static Duration getPollingInterval(String category) {
        InformalTimelinePollingConfig.DefaultElementTimeValue config = getPollingConfig(category);
        return Duration.ofSeconds(config.getWaitingMultiplier());
    }

    public static InformalTimelinePollingConfig.DefaultElementTimeValue getPollingConfig(String category) {

        return InformalTimelinePollingConfig.DefaultElementTimeValue.valueOf(category);
    }

    public static InformalTimelineElementV1 waitForTimelineElement(Supplier<FullSentInformalNotificationV1> notificationSupplier, String category, InformalTimelineElementV1 expected) {

        Duration timeout = getTimeout(category);
        Duration pollInterval = getPollingInterval(category);

        AtomicReference<InformalTimelineElementV1> foundElement = new AtomicReference<>();
        AtomicReference<FullSentInformalNotificationV1> lastNotification = new AtomicReference<>();
        try {

            await().atMost(timeout).pollInterval(pollInterval).until(() -> {

                FullSentInformalNotificationV1 notification = notificationSupplier.get();
                lastNotification.set(notification);
                List<InformalTimelineElementV1> elements = getTimelineElementsByCategory(notification, category);

                if (elements.isEmpty()) {
                    return false;
                }
                // Nessun dettaglio atteso: basta che esista almeno un elemento
                if (expected == null || expected.getDetails() == null) {
                    foundElement.set(elements.get(0));
                    return true;
                }
                for (InformalTimelineElementV1 actual : elements) {
                    try {
                        checkTimelineElement(actual, expected);
                        foundElement.set(actual);
                        return true;

                    } catch (AssertionError ignored) {
                    }
                }
                return false;
            });

        } catch (Exception e) {

            FullSentInformalNotificationV1 notification = lastNotification.get();

            throw new AssertionError("""
                        Elemento timeline non trovato. Categoria attesa: %s Ultima FullSentInformalNotificationV1: %s """.formatted(category, notification), e);
        }
        return foundElement.get();
    }

    public static NewInformalNotificationRequestStatusResponseV1 waitForStatus(Supplier<NewInformalNotificationRequestStatusResponseV1> statusSupplier, String expectedStatus) {

        AtomicReference<NewInformalNotificationRequestStatusResponseV1> responseRef = new AtomicReference<>();
        AtomicReference<String> stopStatusReached = new AtomicReference<>();
        AtomicReference<String> lastStatus = new AtomicReference<>();

        InformalStatusPollingConfig.DefaultStatusValue config = InformalStatusPollingConfig.DefaultStatusValue.valueOf(expectedStatus);

        await().atMost(Duration.ofMinutes(12)).pollInterval(Duration.ofSeconds(5)).until(() -> {
            NewInformalNotificationRequestStatusResponseV1 response = statusSupplier.get();

            responseRef.set(response);

            if (response == null) {
                return false;
            }
            String actualStatus = response.getNotificationRequestStatus();

            lastStatus.set(actualStatus);

            if (config.getStopStatuses().contains(actualStatus)) {
                stopStatusReached.set(actualStatus);
                return true;
            }
            return expectedStatus.equals(actualStatus);
        });

        if (stopStatusReached.get() != null) {
            fail("Atteso stato " + expectedStatus + " ma raggiunto lo stato " + stopStatusReached.get());
        }

        if (responseRef.get() == null) {
            fail("Nessuna response disponibile. " + "Atteso stato: " + expectedStatus);
        }
        return responseRef.get();
    }

}
