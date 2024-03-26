package it.pagopa.pn.client.b2b.pa.utils;

import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//TODO: Usare ovunque è necessario il timing e verificare se parametrizzare da propertiesFile

@Component
public class TimingForTimeline {
    private final PnB2bClientTimingConfigs timingConfigs;
    public record TimingResult(int numCheck, int waiting) { }

    @Autowired
    public TimingForTimeline(PnB2bClientTimingConfigs timingConfigs){
        this.timingConfigs = timingConfigs;
    }


    public TimingResult getTimingForElement(String element, boolean isSlow){
        element = element.trim().toUpperCase();
        Element findedElement = Element.valueOf(element);
        int waiting = timingConfigs.getWorkflowWaitMillis();
        int waitingMultiplier = findedElement.getWaitingMultiplier();

        if(waitingMultiplier > 0){
            waiting = waiting * waitingMultiplier;
        }
        //CASO WAITING MULTIPLIER NEGATIVO DA GESTIRE IN FUTURO

        if(isSlow) {
            return new TimingResult(findedElement.getNumCheck(), waiting * timingConfigs.getWaitingTimingSlowMultiplier());
        }
        return new TimingResult(findedElement.getNumCheck(), waiting);
    }

    public TimingResult getTimingForElement(String element){
        return getTimingForElement(element, false);
    }

    @Getter
    private enum Element{
        //TIMELINE ELEMENT UPDATE TO V2.3
        SENDER_ACK_CREATION_REQUEST(3,0),
        VALIDATE_NORMALIZE_ADDRESSES_REQUEST(3,0),
        NORMALIZED_ADDRESS(3,0),
        REQUEST_ACCEPTED(3,0),
        SEND_COURTESY_MESSAGE(11,0),
        GET_ADDRESS(3,2),
        PUBLIC_REGISTRY_CALL(3,4),
        PUBLIC_REGISTRY_RESPONSE(3,4),
        SCHEDULE_ANALOG_WORKFLOW(3,3),
        SCHEDULE_DIGITAL_WORKFLOW(4,2),
        PREPARE_DIGITAL_DOMICILE(4,2),
        SEND_DIGITAL_DOMICILE(3,2),
        SEND_DIGITAL_PROGRESS(3,3),
        SEND_DIGITAL_FEEDBACK(3,3),
        REFINEMENT(16,0),
        SCHEDULE_REFINEMENT(16,0),
        DIGITAL_DELIVERY_CREATION_REQUEST(16,0),
        DIGITAL_SUCCESS_WORKFLOW(3,3),
        DIGITAL_FAILURE_WORKFLOW(10,0),
        ANALOG_SUCCESS_WORKFLOW(15,0),
        ANALOG_FAILURE_WORKFLOW(15,0),
        PREPARE_SIMPLE_REGISTERED_LETTER(15,0),
        SEND_SIMPLE_REGISTERED_LETTER(15,0),
        SEND_SIMPLE_REGISTERED_LETTER_PROGRESS(16,0),
        NOTIFICATION_VIEWED_CREATION_REQUEST(3,2),
        NOTIFICATION_VIEWED(3,2),
        PREPARE_ANALOG_DOMICILE(10,0),
        SEND_ANALOG_DOMICILE(10,0),
        SEND_ANALOG_PROGRESS(9,0),
        SEND_ANALOG_FEEDBACK(11,0),
        PAYMENT(10,0),
        COMPLETELY_UNREACHABLE(11,0),
        COMPLETELY_UNREACHABLE_CREATION_REQUEST(11,0),
        REQUEST_REFUSED(16,0),
        AAR_CREATION_REQUEST(3,2),
        AAR_GENERATION(3,2),
        NOT_HANDLED(10,0),
        PROBABLE_SCHEDULING_ANALOG_DATE(16,0),
        NOTIFICATION_CANCELLATION_REQUEST(10,0),
        NOTIFICATION_CANCELLED(10,0),
        PREPARE_ANALOG_DOMICILE_FAILURE(16,0),
        NOTIFICATION_RADD_RETRIEVED(16, 0),

        //NOTIFICATION STATUS UPDATE TO V2.3
        IN_VALIDATION(2,0),
        ACCEPTED(2,0),
        REFUSED(10,0),
        DELIVERING(2,4),
        DELIVERED(3,4),
        VIEWED(5,0),
        EFFECTIVE_DATE(10,0),
        PAID(5,0),
        UNREACHABLE(10,0),
        CANCELLED(10,0);


        private int numCheck,waitingMultiplier;

        Element(int numCheck, int waitingMultiplier){
            this.numCheck = numCheck;
            this.waitingMultiplier = waitingMultiplier;
        }
    }
}