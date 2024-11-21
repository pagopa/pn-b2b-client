package it.pagopa.pn.cucumber.steps.templateEngine;

import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateType;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TemplateConfiguration {

    @Autowired
    private ITemplateEngineClient templateEngineClient;

    @Bean
    public Map<TemplateType, ITemplateEngineStrategy> templateEngineStrategy() {
        Map<TemplateType, ITemplateEngineStrategy> map = new HashMap<>();
        map.put(TemplateType.AAR_PRESA_IN_CARICO, new NotificationReceiverLegalFactStrategy(templateEngineClient));
        map.put(TemplateType.AAR_NOTIFICA_DIGITALE, new PecDeliveryWorkflowLegalFactStrategy(templateEngineClient));
        map.put(TemplateType.AAR_AVVENUTO_ACCESSO, new NotificationViewedLegalFactStrategy(templateEngineClient));
        map.put(TemplateType.AAR_FUNZIONAMENTO_RIPRISTINO, new LegalFactMalfunctionStrategy(templateEngineClient));
        map.put(TemplateType.AAR_ANNULLAMENTO_NOTIFICA, new NotificationCancelledLegalFactStrategy(templateEngineClient));
        map.put(TemplateType.DEPOSITO_AVVENUTA_RICEZIONE, new AnalogDeliveryWorkflowFailureLegalFactStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_AVVENUTA_RICEZIONE, new NotificationAARStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_AVVENUTA_RICEZIONE_RADD, new NotificationAARRADDaltStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_CORTESIA_EMAIL, new NotificationAARForEMAILStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_CORTESIA_PEC, new NotificationAARForPECStrategy(templateEngineClient));
        map.put(TemplateType.OTP_CONFERMA_EMAIL, new ConfirmEmailBodyStrategy(templateEngineClient));
        map.put(TemplateType.OTP_CONFERMA_PEC, new ConfirmPecBodyStrategy(templateEngineClient));
        map.put(TemplateType.PEC_VALIDA, new ValidPecBodyStrategy(templateEngineClient));
        map.put(TemplateType.PEC_NON_VALIDA, new PecBodyRejectStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_CORTESIA_SMS, new NotificationAARForSMSStrategy(templateEngineClient));
        map.put(TemplateType.OTP_CONFERMA_SMS, new ConfirmSmsBodyStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_CORTESIA_SMS_OBJECT, new NotificationAARSubjectStrategy(templateEngineClient));
        map.put(TemplateType.OTP_CONFERMA_EMAIL_OBJECT, new ConfirmEmailBodyObjectStrategy(templateEngineClient));
        map.put(TemplateType.OTP_CONFERMA_PEC_OBJECT, new ConfirmPecBodyObjectStrategy(templateEngineClient));
        map.put(TemplateType.PEC_VALIDA_OBJECT, new ValidPecBodyObjectStrategy(templateEngineClient));
        map.put(TemplateType.PEC_NON_VALIDA_OBJECT, new PecBodyRejectObjectStrategy(templateEngineClient));
        return map;
    }
}
