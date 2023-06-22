package it.pagopa.pn.cucumber.utils;

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
    public String getIun() {
        return iun;
    }

    public void setIun(String iun) {
        this.iun = iun;
    }

    public Integer getRecIndex() {
        return recIndex;
    }

    public void setRecIndex(Integer recIndex) {
        this.recIndex = recIndex;
    }

    public String getCourtesyAddressType() {
        return courtesyAddressType;
    }

    public void setCourtesyAddressType(String courtesyAddressType) {
        this.courtesyAddressType = courtesyAddressType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getIsFirstSendRetry() {
        return isFirstSendRetry;
    }

    public void setIsFirstSendRetry(Boolean firstSendRetry) {
        isFirstSendRetry = firstSendRetry;
    }

    public Integer getSentAttemptMade() {
        return sentAttemptMade;
    }

    public void setSentAttemptMade(Integer sentAttemptMade) {
        this.sentAttemptMade = sentAttemptMade;
    }

    public Integer getProgressIndex() {
        return progressIndex;
    }

    public void setProgressIndex(Integer progressIndex) {
        this.progressIndex = progressIndex;
    }
}
