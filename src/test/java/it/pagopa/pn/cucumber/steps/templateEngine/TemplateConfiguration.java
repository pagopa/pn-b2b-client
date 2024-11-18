package it.pagopa.pn.cucumber.steps.templateEngine;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
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
    public Map<String, ITemplateEngineStrategy> templateEngineStrategy() {
        Map<String, ITemplateEngineStrategy> map = new HashMap<>();
        map.put("ATTESTAZIONE OPPONIBILE A TERZI DI NOTIFICA PRESA IN CARICO", new NotificationReceiverLegalFactStrategy(templateEngineClient));
        map.put("ATTESTAZIONE OPPONIBILE A TERZI DI NOTIFICA DIGITALE", new PecDeliveryWorkflowLegalFactStrategy(templateEngineClient));
        map.put("ATTESTAZIONE OPPONIBILE A TERZI DI AVVENUTO ACCESSO", new NotificationViewedLegalFactStrategy(templateEngineClient));
        map.put("ATTESTAZIONE OPPONIBILE A TERZI DI MALFUNZIONAMENTO E RIPRISTINO", new LegalFactMalfunctionStrategy(templateEngineClient));
        map.put("DICHIARAZIONE DI ANNULLAMENTO NOTIFICA", new NotificationCancelledLegalFactStrategy(templateEngineClient));
        map.put("DEPOSITO DI AVVENUTA RICEZIONE", new AnalogDeliveryWorkflowFailureLegalFactStrategy(templateEngineClient));
        map.put("AVVISO DI AVVENUTA RICEZIONE", new NotificationAARStrategy(templateEngineClient));
        map.put("AVVISO DI AVVENUTA RICEZIONE RADD", new NotificationAARRADDaltStrategy(templateEngineClient));
        map.put("AVVISO DI CORTESIA EMAIL",new NotificationAARForEMAILStrategy(templateEngineClient));
        map.put("AVVISO DI CORTESIA PEC", new NotificationAARForPECStrategy(templateEngineClient));
        map.put("OTP DI CONFERMA EMAIL", new EmailbodyStrategy(templateEngineClient));
        map.put("OTP DI CONFERMA PEC", new ConfirmPecBodyStrategy(templateEngineClient));
        map.put("PEC VALIDA", new ValidPecBodyStrategy(templateEngineClient));
        map.put("PEC NON VALIDA", new PecBodyRejectStrategy(templateEngineClient));
        map.put("AVVISO DI CORTESIA SMS", new NotificationAARForSMSStrategy(templateEngineClient));
        map.put("OTP DI CONFERMA SMS", new ConfirmSmsBodyStrategy(templateEngineClient));
        map.put("AVVISO DI CORTESIA PER L’SMS", new NotificationAARSubjectStrategy(templateEngineClient));
        return map;
    }
}
