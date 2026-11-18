package it.pagopa.pn.cucumber.steps.fatturazioneComBo.model;

import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * Dominio della comunicazione: stabilisce quali eventi ne chiudono il percorso e in quale tabella viene fatturata.
 */
@Getter
@RequiredArgsConstructor
public enum CommunicationDomain {

    /**
     * Notifiche legali: eventi di chiusura dell'AS-IS (ANALOG_FAILURE_WORKFLOW_TIMEOUT non è prodotto).
     */
    LEGAL(DynamoTableName.TIMELINES_FOR_INVOICING, Set.of(
            "NOTIFICATION_CANCELLED",
            "NOTIFICATION_VIEWED",
            "REFINEMENT",
            "REQUEST_REFUSED",
            "ANALOG_WORKFLOW_RECIPIENT_DECEASED")),

    /**
     * Comunicazioni bonarie (SRS PN-21786, WI-1.1).
     */
    INFORMAL(DynamoTableName.INFORMAL_TIMELINES_FOR_INVOICING, Set.of(
            "REQUEST_REFUSED",
            "WORKFLOW_DONE_UNREACHED",
            "WORKFLOW_DONE_REACHED",
            "WORKFLOW_ENDED_UNDELIVERABLE",
            "WORKFLOW_ENDED_UNREACHED",
            "WORKFLOW_ENDED_REACHED"));

    public static final String REQUEST_REFUSED = "REQUEST_REFUSED";

    private final DynamoTableName invoicingTable;
    private final Set<String> closureCategories;

    public boolean isClosure(InternalTimelineElement element) {
        return closureCategories.contains(element.category());
    }

    public CommunicationDomain other() {
        return this == LEGAL ? INFORMAL : LEGAL;
    }
}
