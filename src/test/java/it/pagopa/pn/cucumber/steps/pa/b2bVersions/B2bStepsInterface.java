package it.pagopa.pn.cucumber.steps.pa.b2bVersions;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.cucumber.steps.utilitySteps.WaitForEventPredicateFilters;

public interface B2bStepsInterface {

    /**
     * Ogni classe che implementa B2bStepsInterface nella propria implementazione dovrà restituire
     * la fullSentNotification relativa alla propria versione ottenuta chiamando il b2b client.
     * Vi dovrà poi essere un metodo PRIVATO getFullSentNotificationVersioned che restituisce l'oggetto
     * castato alla classe corrispondente alla versione in uso.
     */
    Object getFullSentNotification();

    void readEventsUpToTimelineElement(String timelineEventCategory);

    void readEventsUpToStatus(String status);

    void checkNotificationCost(String cost);

    void checkNormalizedAddress(DataTable table);

    void checkEventPresenceForRecipient(int recipientIndex, String evento);

    void checkPriceForRecipient(int recipientIndex, String price);

    void payAvvisoPagoPa(Integer paymentIndex, Integer recipientIndex);

    void checkForNoDuplicatedTimelineElements(String timelineElementCategory);

    void checkIfLastAttemptIndexMatch(int index);

    void verifyPriceAndWeightInvioCartaceo(Integer price, Integer weight);

    void waitForEvent(String pollingStrategy, String timelineEventCategory, WaitForEventPredicateFilters filters);
}
