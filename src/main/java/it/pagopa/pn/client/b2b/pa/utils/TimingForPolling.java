package it.pagopa.pn.client.b2b.pa.utils;

import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


//TODO: Usare ovunque è necessario il timing e verificare se parametrizzare da propertiesFile

@Component
public class TimingForPolling {
    private final PnB2bClientTimingConfigs timingConfigs;
    public record TimingResult(int numCheck, int waiting) { }


    @Autowired
    public TimingForPolling(PnB2bClientTimingConfigs timingConfigs){
        this.timingConfigs = timingConfigs;
    }

    public TimingResult getTimingForElement(String element, boolean isSlow, boolean isWebhook, boolean isExtraRapid){
        element = element.trim().toUpperCase();
        Element findedElement = Element.valueOf(element);
        int waiting;
        if (isExtraRapid){
            waiting = timingConfigs.getWorkflowWaitExtraRapidMillis();
        }else {
            waiting = timingConfigs.getWorkflowWaitMillis();
        }

        int waitingMultiplier = findedElement.getWaitingMultiplier();

        if(!isWebhook && waitingMultiplier > 1) {
            waiting = waiting * waitingMultiplier;
        }

        //CASO WAITING MULTIPLIER NEGATIVO DA GESTIRE IN FUTURO

        if(isSlow) {
            return new TimingResult(findedElement.getNumCheck(), waiting * timingConfigs.getWaitingTimingSlowMultiplier());
        }

        return new TimingResult(findedElement.getNumCheck(), waiting);
    }

    public TimingResult getTimingForElement(String element){
        return getTimingForElement(element, false, false,false);
    }

    public TimingResult getTimingForElement(String element, boolean isWebhook, boolean isExtraRapid){
        return getTimingForElement(element, false, isWebhook, isExtraRapid);
    }

    public TimingResult getTimingForStatusValidation(String element){
        element = element.trim().toUpperCase();
        Element findedElement = Element.valueOf(element);
        int waiting = timingConfigs.getWorkflowWaitAcceptedMillis();

        if (element.equalsIgnoreCase(Element.ACCEPTED_SHORT_VALIDATION.toString())){
            waiting = timingConfigs.getWaitMillisShort();
        } else if (element.equalsIgnoreCase(Element.ACCEPTED_EXTRA_RAPID_VALIDATION.toString())) {
            waiting = timingConfigs.getWaitMillisExtraRapid();
        }

        return new TimingResult(findedElement.getNumCheck(), waiting);
    }

    @Getter
    private enum Element {
        //TIMELINE ELEMENT UPDATE TO V2.3
        SENDER_ACK_CREATION_REQUEST(2,1),
        VALIDATE_NORMALIZE_ADDRESSES_REQUEST(2,1),
        NORMALIZED_ADDRESS(2,1),
        REQUEST_ACCEPTED(2,1),
        SEND_COURTESY_MESSAGE(11,1),
        GET_ADDRESS(2,2),
        PUBLIC_REGISTRY_CALL(2,4),
        PUBLIC_REGISTRY_RESPONSE(2,4),
        SCHEDULE_ANALOG_WORKFLOW(2,3),
        SCHEDULE_DIGITAL_WORKFLOW(3,2),
        PREPARE_DIGITAL_DOMICILE(3,2),
        SEND_DIGITAL_DOMICILE(2,2),
        SEND_DIGITAL_PROGRESS(6,3),
        SEND_DIGITAL_FEEDBACK(6,3),
        REFINEMENT(15,1),
        SCHEDULE_REFINEMENT(15,1),
        DIGITAL_DELIVERY_CREATION_REQUEST(15,1),
        DIGITAL_SUCCESS_WORKFLOW(8,3),
        DIGITAL_FAILURE_WORKFLOW(9,1),
        ANALOG_SUCCESS_WORKFLOW(15,1),
        ANALOG_FAILURE_WORKFLOW(14,1),
        PREPARE_SIMPLE_REGISTERED_LETTER(14,1),
        SEND_SIMPLE_REGISTERED_LETTER(14,1),
        SEND_SIMPLE_REGISTERED_LETTER_PROGRESS(15,1),
        NOTIFICATION_VIEWED_CREATION_REQUEST(2,2),
        NOTIFICATION_VIEWED(2,2),
        PREPARE_ANALOG_DOMICILE(9,1),
        SEND_ANALOG_DOMICILE(18,1),
        SEND_ANALOG_PROGRESS(16,2),
        SEND_ANALOG_FEEDBACK(11,1),
        PAYMENT(9,1),
        COMPLETELY_UNREACHABLE(13,1),
        COMPLETELY_UNREACHABLE_CREATION_REQUEST(11,1),
        REQUEST_REFUSED(15,1),
        AAR_CREATION_REQUEST(2,2),
        AAR_GENERATION(2,2),
        NOT_HANDLED(9,1),
        PROBABLE_SCHEDULING_ANALOG_DATE(15,1),
        NOTIFICATION_CANCELLATION_REQUEST(9,1),
        NOTIFICATION_CANCELLED(9,1),
        PREPARE_ANALOG_DOMICILE_FAILURE(15,1),
        NOTIFICATION_RADD_RETRIEVED(15,1),

        //NOTIFICATION STATUS UPDATE TO V2.3
        IN_VALIDATION(2,1),
        ACCEPTED(2,1),
        REFUSED(11,1),
        ACCEPTED_VALIDATION(15,1),
        NO_ACCEPTED_VALIDATION(8,1),
        ACCEPTED_SHORT_VALIDATION(231,1),
        ACCEPTED_EXTRA_RAPID_VALIDATION(450,1),
        REFUSED_VALIDATION(11,1),
        DELIVERING(2,4),
        DELIVERED(8,4),
        VIEWED(5,1),
        EFFECTIVE_DATE(11,1),
        PAID(5,1),
        UNREACHABLE(11,1),
        CANCELLED(11,1),

        //TIMING FOR WEBHOOK
        WEBHOOK(150000, 500);

        private final int numCheck;
        private final int waitingMultiplier;

        Element(int numCheck, int waitingMultiplier){
            this.numCheck = numCheck;
            this.waitingMultiplier = waitingMultiplier;
        }
    }
}