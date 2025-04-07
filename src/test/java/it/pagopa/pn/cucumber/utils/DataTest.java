package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class DataTest {
    //TODO: al rilascio di un nuova versione di TimelineElement, aggiornare il campo sottostante alla versione più recente
    private TimelineElementV26 timelineElement;
    private boolean isFirstSendRetry;
    private Integer progressIndex;
    private Integer pollingTime;
    private String pollingType;
    private Integer numCheck;
    private boolean loadTimeline;

    public boolean getIsFirstSendRetry() {
        return isFirstSendRetry;
    }

    public void setFirstSendRetry(boolean firstSendRetry) {
        isFirstSendRetry = firstSendRetry;
    }

    public boolean getLoadTimeline() {
        return loadTimeline;
    }
}