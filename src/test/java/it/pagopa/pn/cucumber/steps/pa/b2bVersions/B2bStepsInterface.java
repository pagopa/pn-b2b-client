package it.pagopa.pn.cucumber.steps.pa.b2bVersions;

import io.cucumber.datatable.DataTable;

public interface B2bStepsInterface {

    void readEventsUpToTimelineElement(String timelineEventCategory);

    void readEventsUpToStatus(String status);

    void checkNotificationCost(String cost);

    void checkNormalizedAddress(DataTable table);

    void checkEventPresenceForRecipient(int recipientIndex, String evento);

    void checkPriceForRecipient(int recipientIndex, String price);

    void payAvvisoPagoPa(Integer paymentIndex, Integer recipientIndex);
}
