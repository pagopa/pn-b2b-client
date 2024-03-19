package it.pagopa.pn.client.b2b.pa.utils;

import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//TODO: Usare ovunque è necessario il timing e verificare se parametrizzare da propertiesFile

@Component
public class TimingForTimeline {
    private final PnB2bClientTimingConfigs timingConfigs;
    public record TimingResult(int numCheck, int waiting) { }

    @Value("${pn.config.timing.timeline.tuning-value}")
    private Integer tuningValue;

    @Autowired
    public TimingForTimeline(PnB2bClientTimingConfigs timingConfigs){
        this.timingConfigs = timingConfigs;
    }


    public TimingResult getTimingForElement(String element, boolean isSlow){
        element = element.trim().toUpperCase();
        Element findedElement = Element.valueOf(element);
        int waiting = timingConfigs.getWorkflowWaitMillis();
        int waitingMultiplier = findedElement.getWaitingMultiplier();

        if( waitingMultiplier > 0){
            waiting = waiting * waitingMultiplier;
        }else if(waitingMultiplier < 0){
            //CASO MULTIPLIER NEGATIVO ?
            waiting = waiting / waitingMultiplier;
        }

        if(isSlow) {
            return new TimingResult(findedElement.getNumCheck(), waiting * tuningValue);
        }
        return new TimingResult(findedElement.getNumCheck(), waiting);
    }

    public TimingResult getTimingForElement(String element){
        element = element.trim().toUpperCase();
        Element findedElement = Element.valueOf(element);
        int waiting = timingConfigs.getWorkflowWaitMillis();
        int waitingMultiplier = findedElement.getWaitingMultiplier();
        if( waitingMultiplier > 0){
            waiting = waiting * waitingMultiplier;
        }else if(waitingMultiplier < 0){
            waiting = waiting / waitingMultiplier;
        }
        return new TimingResult(findedElement.getNumCheck(), waiting);
    }

    @Getter
    private enum Element{
        //TIMELINE ELEMENT UPDATE TO V2.3
        SENDER_ACK_CREATION_REQUEST(2,0),
        VALIDATE_NORMALIZE_ADDRESSES_REQUEST(2,0),
        NORMALIZED_ADDRESS(2,0),
        REQUEST_ACCEPTED(2,0),
        SEND_COURTESY_MESSAGE(10,0),
        GET_ADDRESS(2,2),
        PUBLIC_REGISTRY_CALL(2,4),
        PUBLIC_REGISTRY_RESPONSE(2,4),
        SCHEDULE_ANALOG_WORKFLOW(2,3),
        SCHEDULE_DIGITAL_WORKFLOW(3,2),
        PREPARE_DIGITAL_DOMICILE(3,2),
        SEND_DIGITAL_DOMICILE(2,2),
        SEND_DIGITAL_PROGRESS(2,3),
        SEND_DIGITAL_FEEDBACK(2,3),
        REFINEMENT(15,0),
        SCHEDULE_REFINEMENT(15,0),
        DIGITAL_DELIVERY_CREATION_REQUEST(15,0),
        DIGITAL_SUCCESS_WORKFLOW(2,3),
        DIGITAL_FAILURE_WORKFLOW(9,0),
        ANALOG_SUCCESS_WORKFLOW(14,0),
        ANALOG_FAILURE_WORKFLOW(14,0),
        PREPARE_SIMPLE_REGISTERED_LETTER(14,0),
        SEND_SIMPLE_REGISTERED_LETTER(14,0),
        SEND_SIMPLE_REGISTERED_LETTER_PROGRESS(15,0),
        NOTIFICATION_VIEWED_CREATION_REQUEST(2,2),
        NOTIFICATION_VIEWED(2,2),
        PREPARE_ANALOG_DOMICILE(9,0),
        SEND_ANALOG_DOMICILE(9,0),
        SEND_ANALOG_PROGRESS(8,0),
        SEND_ANALOG_FEEDBACK(10,0),
        PAYMENT(9,0),
        COMPLETELY_UNREACHABLE(10,0),
        COMPLETELY_UNREACHABLE_CREATION_REQUEST(10,0),
        REQUEST_REFUSED(15,0),
        AAR_CREATION_REQUEST(2,2),
        AAR_GENERATION(2,2),
        NOT_HANDLED(9,0),
        PROBABLE_SCHEDULING_ANALOG_DATE(15,0),
        NOTIFICATION_CANCELLATION_REQUEST(9,0),
        NOTIFICATION_CANCELLED(9,0),
        PREPARE_ANALOG_DOMICILE_FAILURE(15,0),
        NOTIFICATION_RADD_RETRIEVED(15,0),

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