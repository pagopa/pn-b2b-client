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

}