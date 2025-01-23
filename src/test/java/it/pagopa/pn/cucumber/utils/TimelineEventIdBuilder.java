package it.pagopa.pn.cucumber.utils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Classe builder che permette di costruire un timelineEventId
 * Il formato dello della stringa in input dovrà essere:
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

    private String courtesyAddressType = "";

    private String isFirstSendRetry = "";

    public TimelineEventIdBuilder withIun(@Nullable String iun) {
        if (iun != null)
            this.iun = DELIMITER.concat("IUN_").concat(iun);
        return this;
    }

    public TimelineEventIdBuilder withRecIndex(@Nullable Integer recIndex) {
        if (recIndex != null)
            this.recIndex = DELIMITER.concat("RECINDEX_").concat(String.valueOf(recIndex));
        return this;
    }

    public TimelineEventIdBuilder withCategory(@NotNull String category) {
        this.category = category;
        return this;
    }

    public TimelineEventIdBuilder withCourtesyAddressType(@Nullable String courtesyAddressType) {
        if (courtesyAddressType != null)
            this.courtesyAddressType = DELIMITER.concat("COURTESYADDRESSTYPE_").concat(courtesyAddressType);
        return this;
    }

    public TimelineEventIdBuilder withSource(@Nullable String source) {
        if (source != null)
            this.source = DELIMITER.concat("SOURCE_").concat(source);
        return this;
    }

    public TimelineEventIdBuilder withIsFirstSendRetry(@Nullable Boolean retry) {
        if (retry != null)
            this.isFirstSendRetry = DELIMITER.concat("REPEAT_").concat(retry.toString());
        return this;
    }

    public TimelineEventIdBuilder withSentAttemptMade(@Nullable Integer sentAttemptMade) {
        if (sentAttemptMade != null && sentAttemptMade >= 0)
            this.sentAttemptMade = DELIMITER.concat("ATTEMPT_").concat(String.valueOf(sentAttemptMade));
        return this;
    }

    public TimelineEventIdBuilder withProgressIndex(@Nullable Integer progressIndex) {
        // Se passo un progressIndex negativo, è perché non voglio che venga inserito nell'eventId. Usato per cercare con l'inizia per
        if (progressIndex != null && progressIndex >= 0)
            this.progressIndex = DELIMITER.concat("IDX_").concat(String.valueOf(progressIndex));
        return this;
    }

    public String build() {
        String paymentCode = "";
        String deliveryMode = "";
        String contactPhase = "";
        // for national registries
        String correlationId = "";
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
