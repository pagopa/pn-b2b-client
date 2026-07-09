package it.pagopa.pn.cucumber.steps.informalNotification.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.FullSentInformalNotificationV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementDetailsV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementV1;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    }

    public static List<InformalTimelineElementV1> getTimelineElementsByCategory(FullSentInformalNotificationV1 notification, String category) {

        if (notification == null || notification.getTimeline() == null) {
            return Collections.emptyList();
        }

        return notification.getTimeline().stream().filter(t -> t.getCategory() != null && category.equals(t.getCategory().getValue())).toList();
    }

    public static List<InformalTimelineElementV1> waitForTimelineElementsByCategory(Supplier<FullSentInformalNotificationV1> notificationSupplier, String category, Duration timeout, Duration pollInterval) {

        AtomicReference<List<InformalTimelineElementV1>> foundElements = new AtomicReference<>(Collections.emptyList());

        await().atMost(timeout).pollInterval(pollInterval).until(() -> {

            FullSentInformalNotificationV1 notification = notificationSupplier.get();

            List<InformalTimelineElementV1> elements = getTimelineElementsByCategory(notification, category);

            foundElements.set(elements);

            return !elements.isEmpty();
        });

        return foundElements.get();
    }
}
