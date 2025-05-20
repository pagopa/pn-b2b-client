package it.pagopa.pn.cucumber.utils.datatestVersions;

import lombok.Data;

import java.util.Map;

/**
 * Superclasse per le varie versioni di DataTest.
 * Si differenziano tra loro unicamente per la versione del campo
 * timelineElement
 */
@Data
public abstract class AbstractDataTest {

    private boolean isFirstSendRetry;
    private Integer progressIndex;
    private Integer pollingTime;
    private String pollingType;
    private Integer numCheck;
    private boolean loadTimeline;
    private String loadTimelineFrom;
    private Map<String, String> inputData; //solo a fini di logging, per mostrare il tipo di dato che ci si attende

}
