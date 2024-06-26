package it.pagopa.pn.cucumber.utils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Classe builder che permette di costruire un timelineEventId
 *
 * Il formato dello della stringa di input dovrà essere:
 * <CATEGORY_VALUE>;IUN_<IUN_VALUE>;RECINDEX_<RECINDEX_VALUE>...
 * tutti i value sono facoltativi, tranne il campo category.
 * Sarà responsabilità del builder concatenare ogni singolo value alla timelineEventId solo se non gli viene passato null.
 */
public class TimelineEventIdBuilder {

    public static final String DELIMITER = ".";

    private String iun = "";

    private String recIndex = "";

    private String category = "";

    private String source = "";

    private String sentAttemptMade = "";

    private String progressIndex = "";

    private String deliveryMode = "";

    private String contactPhase = "";

    private String correlationId = ""; // for national registries

    private String courtesyAddressType = "";

    private String paymentCode = "";

    private String isFirstSendRetry = "";

    public TimelineEventIdBuilder withIun(@Nullable String iun) {
        if(iun != null)
            this.iun = DELIMITER.concat("IUN_").concat(iun);
        return this;
    }

    public TimelineEventIdBuilder withRecIndex(@Nullable Integer recIndex) {
        if(recIndex != null)
            this.recIndex = DELIMITER.concat("RECINDEX_").concat(recIndex + "");
        return this;
    }

    public TimelineEventIdBuilder withCategory(@NotNull String category) {
        this.category = category;
        return this;
    }

    public TimelineEventIdBuilder withCourtesyAddressType(@Nullable String courtesyAddressType) {
        if(courtesyAddressType != null)
            this.courtesyAddressType = DELIMITER.concat("COURTESYADDRESSTYPE_").concat(courtesyAddressType);
        return this;
    }

    public TimelineEventIdBuilder withSource(@Nullable String source) {
        if(source != null)
            this.source = DELIMITER.concat("SOURCE_").concat(source);
        return this;
    }

    public TimelineEventIdBuilder withIsFirstSendRetry(@Nullable Boolean retry) {
        if(retry != null)
            this.isFirstSendRetry = DELIMITER.concat("REPEAT_").concat(retry.toString());
        return this;
    }

    public TimelineEventIdBuilder withSentAttemptMade(@Nullable Integer sentAttemptMade) {
        if(sentAttemptMade != null && sentAttemptMade >= 0)
            this.sentAttemptMade = DELIMITER.concat("ATTEMPT_").concat(sentAttemptMade + "");
        return this;
    }

    public TimelineEventIdBuilder withProgressIndex(@Nullable Integer progressIndex) {
        // se passo un progressindex negativo, è perchè non voglio che venga inserito nell'eventid. Usato per cercare con l'inizia per
        if(progressIndex != null && progressIndex >= 0)
            this.progressIndex = DELIMITER.concat("IDX_").concat(progressIndex + "");
        return this;
    }

    /*

    public TimelineEventIdBuilder withDeliveryMode(@Nullable DeliveryModeInt deliveryMode) {
        if(deliveryMode != null)
            this.deliveryMode = DELIMITER.concat("DELIVERYMODE_").concat(deliveryMode.getValue());
        return this;
    }

    public TimelineEventIdBuilder withContactPhase(@Nullable ContactPhaseInt contactPhase) {
        if(contactPhase != null)
            this.contactPhase = DELIMITER.concat("CONTACTPHASE_").concat(contactPhase.getValue());
        return this;
    }

    public TimelineEventIdBuilder withCorrelationId(@Nullable String correlationId) {
        if(correlationId != null)
            this.correlationId = DELIMITER.concat("CORRELATIONID_").concat(correlationId);
        return this;
    }

    // payment code per pagamenti PagoPa = PPANoticeNumberCreditorTaxId
    // payment code per pagamenti f24 = F24Idf24
    public TimelineEventIdBuilder withPaymentCode(@Nullable String paymentCode) {
        if(paymentCode != null)
            this.paymentCode = DELIMITER.concat("CODE_").concat(paymentCode);
        return this;
    }
     */

    public String build() {
        return category +
                iun +
                recIndex +
                courtesyAddressType +
                source +
                deliveryMode +
                contactPhase +
                isFirstSendRetry +
                sentAttemptMade +
                progressIndex +
                correlationId +
                paymentCode;
    }
}
