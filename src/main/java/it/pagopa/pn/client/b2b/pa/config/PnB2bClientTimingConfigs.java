package it.pagopa.pn.client.b2b.pa.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


import java.time.Duration;

@ConfigurationProperties( prefix = "pn.configuration", ignoreUnknownFields = false)
@Data
public class PnB2bClientTimingConfigs {

    private Integer workflowWaitAcceptedMillis;
    private Integer workflowWaitMillis;
    private Integer waitMillis;
    private Duration schedulingDaysSuccessDigitalRefinement;
    private Duration schedulingDaysFailureDigitalRefinement;
    private Duration schedulingDaysSuccessAnalogRefinement;
    private Duration schedulingDaysFailureAnalogRefinement;
    private Duration nonVisibilityTime;
    private Duration secondNotificationWorkflowWaitingTime;
    private Duration waitingForReadCourtesyMessage;
    private Integer schedulingDeltaMillis;




}
