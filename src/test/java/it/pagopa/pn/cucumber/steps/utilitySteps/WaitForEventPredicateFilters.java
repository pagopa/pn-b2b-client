package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class WaitForEventPredicateFilters {

    /**
     * Attempt (molto generico) è stato rinominato withElementIdSuffix.
     * Il controllo sull'attempt veniva infatti utilizzato per verificare la presenza nel timelineElementId di una certa stringa.
     * Ora che lo stesso metodo è stato usato anche per controllare la presenza di elementi di timeline con "_REWORK", il cambio riflette meglio la natura generale del campo.
     * Sarebbe il caso di fare un refactor anche degli step, mettendo "con suffisso XXX" anzichè l'attuale "al tentativo XXX"
     * Valutare anche l'inserimento di un nuovo campo withoutElementIdSuffix per fare il check opposto.
     */
    private String withElementIdSuffix;
    private Integer recipientIndex;
    private String deliveryDetailCode;
    private String documentType;
    private String responseStatus;
    private boolean isF24;
    private boolean isLegalFactEmpty;
    private String legalFactIdCategory;
    private boolean isAttachmentEmpty;
    private List<String> failureCauses;
    private String statusHistory;
}
