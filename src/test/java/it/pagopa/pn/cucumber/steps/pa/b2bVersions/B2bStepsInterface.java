package it.pagopa.pn.cucumber.steps.pa.b2bVersions;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.cucumber.steps.utilitySteps.PollingType;
import it.pagopa.pn.cucumber.steps.utilitySteps.TimelineElementCheck;
import it.pagopa.pn.cucumber.steps.utilitySteps.TimelineElementCheckFilters;
import it.pagopa.pn.cucumber.steps.utilitySteps.WaitForEventPredicateFilters;

import java.util.Map;

public interface B2bStepsInterface {

    /**
     * Ogni classe che implementa B2bStepsInterface nella propria implementazione dovrà restituire
     * la fullSentNotification relativa alla propria versione ottenuta chiamando il b2b client.
     * Vi dovrà poi essere un metodo PRIVATO getFullSentNotificationVersioned che restituisce l'oggetto
     * castato alla classe corrispondente alla versione in uso.
     */
    Object getFullSentNotification();

    void readEventsUpToTimelineElement(String timelineEventCategory);

    void readEventsUpToStatus(String status, boolean exists);

    void checkNotificationCost(String cost);

    void checkNormalizedAddress(DataTable table);

    void checkEventPresenceForRecipient(int recipientIndex, String evento);

    void checkPriceForRecipient(int recipientIndex, String price);

    void payAvvisoPagoPa(Integer paymentIndex, Integer recipientIndex);

    void checkForNoDuplicatedTimelineElements(String timelineElementCategory);

    void checkIfLastAttemptMatchesIndex(int index);

    void verifyPriceAndWeightInvioCartaceo(Integer price, Integer weight);

    void waitForEventOrStatus(String pollingStrategy, PollingType pollingType, String timelineEventCategory, WaitForEventPredicateFilters filters);

    void verifyTimelineElementDoesNotExists(boolean mustLoadTimeline, String timelineEventCategory, Map<String, String> dataMap);

    /**
     * La lettura avviene dentro a waitForEventOrStatus, qua si limita a fare le assertions
     *
     * @param exists        se true, il codice assume che l'elemento di timeline atteso sia stato trovato, e viceversa
     * @param furtherChecks se != null, il codice procede con ulteriori assert, tramite uno switch a seconda del TimelineElementCheck
     * @param filterParams  oggetto contenente tutti i campi necessari per effettuare i controlli aggiuntivi (può essere null, se il
     *                      TimelineElementCheck passato come parametro non necessita di parametri specifici).
     */
    void checkIfTimelineElementExists(boolean exists, TimelineElementCheck furtherChecks, TimelineElementCheckFilters filterParams);

    void checkIfTimelineElementFromDataExists(boolean exists, String timelineEventCategory, Map<String, String> data);

    void checkIfStatusExists(boolean exists);

    void performFurtherChecks(TimelineElementCheck furtherChecks, TimelineElementCheckFilters filterParams);

    void searchCustomTimelineElementInTimeline(String eventId, String timelineEventCategory);

    //Lo step che lo implementa non è utilizzato, valuterei eliminazione (anche perchè si può inglobare in un altro dei metodi già esistenti)
    void verificaAssenzaPagamentiF24();

}
