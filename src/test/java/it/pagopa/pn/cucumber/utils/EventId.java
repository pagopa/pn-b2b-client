package it.pagopa.pn.cucumber.utils;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventId {
    private String iun;
    private Integer recIndex;
    private String courtesyAddressType;
    private String source;
    private Boolean isFirstSendRetry;
    private Integer sentAttemptMade;
    private Integer progressIndex;

    /*
    private ContactPhaseInt contactPhase;
    private DeliveryModeInt deliveryMode;
    private DocumentCreationTypeInt documentCreationType;
    private CourtesyDigitalAddressInt.COURTESY_DIGITAL_ADDRESS_TYPE_INT courtesyAddressType;
    private String creditorTaxId;
    private String noticeCode;
    private String idF24;

    private String relatedTimelineId;
     */
}