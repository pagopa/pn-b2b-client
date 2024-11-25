package it.pagopa.pn.cucumber.steps.templateEngine;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.Delivery;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateType;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public Map<TemplateType, List<String>> templateEngineObjectFields(List<String> notificationReceiverLegalFactFields, List<String> pecDeliveryWorkflowLegalFactFields,
                                                                      List<String> notificationViewedLegalFactFields) {
        Map<TemplateType, List<String>> map = new HashMap<>();
        map.put(TemplateType.AAR_PRESA_IN_CARICO, notificationReceiverLegalFactFields);
        map.put(TemplateType.AAR_NOTIFICA_DIGITALE, pecDeliveryWorkflowLegalFactFields);
        map.put(TemplateType.AAR_AVVENUTO_ACCESSO, notificationViewedLegalFactFields);
        return map;
    }

    @Bean
    public List<String> notificationReceiverLegalFactFields() {
        List list = new ArrayList();
        list.add("context_physicalAddressAndDenomination");
        list.add("context_senddate");
        list.add("context_subject");
        list.add("notification_iun");
        list.add("notification_subject");
        list.add("notification_recipient_recipientType");
        list.add("notification_recipient_recipientType");
        list.add("notification_recipient_denomination");
        list.add("notification_recipient_taxId");
        list.add("notification_recipient_physicalAddress");
        list.add("notification_sender_paDenomination");
        list.add("notification_sender_paId");
        list.add("notification_sender_paTaxId");
        list.add("context_digest");
        return list;
    }

    @Bean
    public List<String> pecDeliveryWorkflowLegalFactFields() {
        List list = new ArrayList();
        list.add("context_iun");
        list.add("context_endWorkflowDate");
        list.add("context_endWorkflowStatus");
        list.add("delivery_address");
        list.add("delivery_ok");
        list.add("delivery_type");
        list.add("delivery_addressSource");
        list.add("delivery_responseDate");
        list.add("delivery_denomination");
        return list;
    }

    @Bean
    public List<String> notificationViewedLegalFactFields() {
        List list = new ArrayList();
        list.add("context_iun");
        list.add("context_when");
        list.add("delegate_denomination");
        list.add("delegate_taxId");
        list.add("notification_recipient_recipientType");
        list.add("notification_recipient_denomination");
        list.add("notification_recipient_taxId");
        list.add("notification_recipient_physicalAddress");
        list.add("notification_recipient_digitalDomicile_address");
        return list;
    }



}
