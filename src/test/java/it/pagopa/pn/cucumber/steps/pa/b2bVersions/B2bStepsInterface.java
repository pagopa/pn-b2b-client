package it.pagopa.pn.cucumber.steps.pa.b2bVersions;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.cucumber.steps.utilitySteps.PollingType;
import it.pagopa.pn.cucumber.steps.utilitySteps.WaitForEventPredicateFilters;
import it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement.TimelineElementCheck;
import it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement.TimelineElementCheckFilters;
import it.pagopa.pn.cucumber.utils.datatestVersions.AbstractDataTest;

import java.time.temporal.ChronoUnit;
import java.util.Map;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.STATUS_RAPID;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.TIMELINE_SLOW;
import static it.pagopa.pn.cucumber.steps.utilitySteps.PollingType.STATUS;
import static it.pagopa.pn.cucumber.steps.utilitySteps.PollingType.TIMELINE;

public interface B2bStepsInterface {

    /**
     * Ogni classe che implementa B2bStepsInterface nella propria implementazione dovrà restituire
     * la fullSentNotification relativa alla propria versione ottenuta chiamando il b2b client.
     * Vi dovrà poi essere un metodo PRIVATO getFullSentNotificationVersioned che restituisce l'oggetto
     * castato alla classe corrispondente alla versione in uso.
     */
    Object getFullSentNotification();

    void verifyTestCompatibilityWithVersion(String eventCategoryOrStatus, boolean isEventCategory);

    void checkFullSentNotificationWithVersion(boolean isPresent, String timelineEventCategory);

    void checkFullSentNotificationRelatedElementWithVersion(String timelineEventCategory);

    /**
     * Per notifiche andate in ACCEPTED usare questo metodo (lettura timeline da b2b)
     */
    default void readEventsUpToTimelineElement(String timelineEventCategory) {
        verifyTestCompatibilityWithVersion(timelineEventCategory, true);
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        checkIfTimelineElementExists(timelineEventCategory, true, null, null);
    }

    /**
     * Per notifiche andate in REFUSED usare questo metodo (lettura timeline da delivery-push)
     */
    void readEventsUpToTimelineElementFromDeliveryPush(String timelineEventCategory, AbstractDataTest dataTest, boolean existCheck);

    default void readEventsUpToStatus(String status, boolean exists) {
        if (exists) {
            verifyTestCompatibilityWithVersion(status, false);
        }
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .statusHistory(status)
                .build();
        waitForEventOrStatus(STATUS_RAPID, STATUS, status, filters);
        checkIfStatusExists(exists);
    }

    void checkNotificationCost(String cost);

    void checkNormalizedAddress(DataTable table);

    void checkEventPresenceForRecipient(int recipientIndex, String evento);

    void checkPriceForRecipient(int recipientIndex, String price);

    /**
     * Paga gli avvisi di pagamento relativi a un certo destinatario
     *
     * @param recipientIndex il destinatario che deve eseguire i pagamenti
     * @param paymentIndex   se passato null, il metodo procede con il pagamento di tutti gli avvisi relativi al recipient
     */
    void payAvvisoPagoPa(Integer recipientIndex, Integer paymentIndex);

    void checkForNoDuplicatedTimelineElements(String timelineElementCategory);

    void checkIfLastAttemptMatchesIndex(int index);

    void verifyPriceAndWeightInvioCartaceo(Integer price, Integer weight);

    void waitForEventOrStatus(String pollingStrategy, PollingType pollingType, String timelineEventCategory, WaitForEventPredicateFilters filters);

    /**
     * La lettura avviene dentro a waitForEventOrStatus, qua si limita a fare le assertions
     *
     * @param category      unicamente per fini di log
     * @param exists        se true, il codice assume che l'elemento di timeline atteso sia stato trovato, e viceversa
     * @param furtherChecks se != null, il codice procede con ulteriori assert, tramite uno switch a seconda del TimelineElementCheck
     * @param filterParams  oggetto contenente tutti i campi necessari per effettuare i controlli aggiuntivi (può essere null, se il
     *                      TimelineElementCheck passato come parametro è null o non necessita di parametri specifici).
     */
    void checkIfTimelineElementExists(String category, boolean exists, TimelineElementCheck furtherChecks, TimelineElementCheckFilters filterParams);

    void checkIfTimelineElementExistsFromData(boolean exists, String timelineEventCategory, Map<String, String> dataMap);

    void checkIfStatusExists(boolean exists);

    void performFurtherChecks(TimelineElementCheck furtherChecks, TimelineElementCheckFilters filterParams);

    void searchCustomTimelineElementInTimeline(String eventId, String timelineEventCategory);

    //Lo step che lo implementa non è utilizzato, valuterei eliminazione (anche perché si può inglobare in un altro dei metodi già esistenti)
    void verificaAssenzaPagamentiF24();

    void checkNumberOfTimelineElements(String timelineEventCategory, Integer size);

    void checkNumberOfTimelineElementsFromData(String timelineEventCategory, Integer size, Map<String, String> data);

    void waitForScheduleRefinement(Map<String, String> dataMap) throws InterruptedException;

    void waitForSecondAttempt(String timelineEventCategory, Map<String, String> dataMap) throws InterruptedException;

    void checkOrdineEventiUnivoci(String category1, Boolean isSuccessivo, String category2);

    void vieneSchedulatoIlPerfezionamento(String timelineEventCategory, Map<String, String> dataMap);

    void checkScartoTemporaleTraDueDeliveryDetailCode(String code1, String code2, Boolean isSuperiore, int timeQuantity, ChronoUnit unitaTemporale);

    void checkReworkTimelineWithVersion();


}
