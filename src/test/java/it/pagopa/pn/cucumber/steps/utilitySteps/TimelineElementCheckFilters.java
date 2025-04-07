package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Builder;
import lombok.Getter;

/**
 * Classe contenitore per tutti i parametri da passare al metodo performFurtherChecks
 * delle classi che implementano l'interfaccia {@link it.pagopa.pn.cucumber.steps.pa.b2bVersions.B2bStepsInterface}.
 * Se si necessita di creare una nuova tipologia di check con parametri, sarà sufficiente
 * aggiungere un nuovo valore all'Enum {@link it.pagopa.pn.cucumber.steps.utilitySteps.TimelineElementCheck}
 * e inserire qui eventuali parametri necessari per il check
 */
@Builder
@Getter
public class TimelineElementCheckFilters {

    //CHECK_RESPONSE_STATUS
    private String responseStatus;
    private String digitalAddressSource;
    private boolean withDeliveryDetailCode;
    private boolean withDeliveryFailureCause;
    //CHECK_SCHEDULING_DATE_DELAY
    private String tipoIncremento;
    //CHECK_SCHEDULING_DATE_DELAY + CHECK_NOTIFICATION_DATE_DELAY
    private Integer delay;
    //CHECK_ATTACHMENTS
    private boolean withAttempt;
    private String documentType;
    //CHECK_DELIVERY_FAILURE_CAUSE
    private String deliveryFailureCause;
    //CHECK_FAILURE_CAUSE
    private String failureCause;
    //CHECK_SERVICE_LEVEL
    private String serviceLevel;
    //CHECK_DIGITAL_ADDRESS
    private boolean withPlatformAddress;
    private String platformAddress;
    //CHECK_PHYSICAL_ADDRESS
    private String physicalAddressRegex;
    //CHECK_SCHEDULING_DATE_RISPETTO_A_EVENTO
    private String otherEventCategory;
    //CHECK_SCHEDULING_DATE_RISPETTO_A_EVENTO + CHECK_NOTIFICATION_COST_FOR_USER
    private Integer recipientIndex;
    //CHECK_NOTIFICATION_COST_FOR_USER
    private String notificationCost;
    private String timelineEventCategory;
    //CHECK_NUMBER_OF_PAGES_AAR
    private Integer numberOfPagesAAR;
    //CHECK_FIELD_MATCHES_REGEX
    private String fieldPath;
    private String fieldRegex;
    //CHECK_ATTESTAZIONI_OPPONIBILI
    private int numberOfAttestazioniOpponibili;
}
