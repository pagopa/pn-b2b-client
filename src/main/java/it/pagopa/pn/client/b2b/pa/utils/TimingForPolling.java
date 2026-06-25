package it.pagopa.pn.client.b2b.pa.utils;

import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import org.springframework.stereotype.Component;

//TODO: Usare ovunque è necessario il timing e verificare se parametrizzare da propertiesFile

@Component
public class TimingForPolling {
    private final PnB2bClientTimingConfigs timingConfigs;

    public record TimingResult(int numCheck, int waiting) {
    }



    public TimingForPolling(PnB2bClientTimingConfigs timingConfigs) {
        this.timingConfigs = timingConfigs;
    }

    public TimingResult getTimingForElement(String element, boolean isSlow, boolean isWebhook, boolean isExtraRapid) {
        element = element.trim().toUpperCase();
        int waiting;
        if (isExtraRapid) {
            waiting = timingConfigs.getWorkflowWaitExtraRapidMillis();
        } else {
            waiting = timingConfigs.getWorkflowWaitMillis();
        }

        int waitingMultiplier = timingConfigs.calculateWaitingMultiplierValue(element);

        if (!isWebhook && waitingMultiplier > 1) {
            waiting = waiting * waitingMultiplier;
        }

        //CASO WAITING MULTIPLIER NEGATIVO DA GESTIRE IN FUTURO

        if (isSlow) {
            return new TimingResult(timingConfigs.calculateNumCheckValue(element),  waiting * timingConfigs.getWaitingTimingSlowMultiplier());
        }

        return new TimingResult(timingConfigs.calculateNumCheckValue(element), waiting);
    }

    public TimingResult getTimingForElement(String element) {
        return getTimingForElement(element, false, false, false);
    }

    public TimingResult getTimingForElement(String element, boolean isWebhook, boolean isExtraRapid) {
        return getTimingForElement(element, false, isWebhook, isExtraRapid);
    }

    public TimingResult getTimingForStatusValidation(String element) {
        element = element.trim().toUpperCase();
        int waiting = timingConfigs.getWorkflowWaitAcceptedMillis();

        if (element.equalsIgnoreCase(PnB2bClientTimingConfigs.DefaultElementTimeValue.ACCEPTED_SHORT_VALIDATION.toString())) {
            waiting = timingConfigs.getWaitMillisShort();
        } else if (element.equalsIgnoreCase(PnB2bClientTimingConfigs.DefaultElementTimeValue.ACCEPTED_EXTRA_RAPID_VALIDATION.toString())) {
            waiting = timingConfigs.getWaitMillisExtraRapid();
        }

        return new TimingResult(timingConfigs.calculateNumCheckValue(element), waiting);
    }

}