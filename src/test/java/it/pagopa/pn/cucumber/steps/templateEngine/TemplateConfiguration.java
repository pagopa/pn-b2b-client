package it.pagopa.pn.cucumber.steps.templateEngine;

import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateType;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public List<String> notificationReceiverLegalFactFields() {
        return List.of("context_senddate", "context_subject", "context_digest", "notification_iun", "context_notification",
                "notification_sender", "notification_recipient", "notification_sender_paDenomination", "notification_sender_paTaxId",
                 "notification_recipient_denomination", "notification_recipient_taxId", "notification_recipient_digitalDomicile");
    }

    @Bean
    public List<String> pecDeliveryWorkflowLegalFactFields() {
        return List.of("context_iun", "context_endWorkflowStatus", "context_delivery", "delivery_ok", "delivery_taxId",
                 "delivery_responseDate", "delivery_denomination");
    }

    @Bean
    public List<String> notificationViewedLegalFactFields() {
        return List.of("context_iun", "context_when", "context_recipient", "delegate_denomination", "delegate_taxId",
                 "recipient_denomination", "recipient_taxId");
    }

    @Bean
    public List<String> legalFactMalfunctionFields() {
        return List.of("context_endDate", "context_startDate", "context_timeReferenceStartDate", "context_timeReferenceEndDate");
    }

    @Bean
    public List<String> notificationCancelledLegalFactFields() {
        return List.of("context_notification", "notification_iun", "notification_recipient_denomination", "notification_recipient_taxId", "notification_sender",
                "notification_sender_paDenomination", "context_NotificationCancelledDate", "notification_recipient");
    }

    @Bean
    public List<String> analogDeliveryWorkflowFailureLegalFactFields() {
        return List.of("context_recipient", "context_iun", "recipient_denomination", "recipient_taxId", "context_endWorkflowDate", "context_endWorkflowTime");
    }

    @Bean
    public List<String> notificationAARFields() {
        return List.of("context_recipient", "recipient_taxId", "recipient_recipientType", "context_notification", "notification_iun", "notification_subject", "notification_sender",
                "notification_sender_paDenomination", "context_qrCodeQuickAccessLink", "context_piattaformaNotificheURLLabel", "context_piattaformaNotificheURL",
                "context_perfezionamentoURLLabel", "context_perfezionamentoURL");
    }

    @Bean
    public List<String> notificationAARRADDaltFields() {
        return List.of("context_recipient", "recipient_taxId", "recipient_recipientType", "recipient_denomination", "context_notification", "notification_iun", "notification_subject", "notification_sender",
                "notification_sender_paDenomination", "context_qrCodeQuickAccessLink", "context_piattaformaNotificheURLLabel", "context_piattaformaNotificheURL",
                "context_perfezionamentoURLLabel", "context_perfezionamentoURL", "context_sendURL", "context_sendURLLAbel", "context_raddPhoneNumber");
    }

    @Bean
    public List<String> notificationAARForEMAILFields() {
        return List.of("context_notification", "notification_iun", "notification_sender", "notification_sender_paDenomination", "context_qrCodeQuickAccessLink",
                "context_piattaformaNotificheURL", "context_perfezionamentoURL", "context_pnFaqSendURL");
    }

    @Bean
    public List<String> notificationAARForPECFields() {
        return List.of("context_notification", "context_recipient", "notification_iun", "notification_subject", "notification_sender",
                "notification_sender_paDenomination", "context_qrCodeQuickAccessLink", "context_recipientType",
                "context_piattaformaNotificheURL", "context_perfezionamentoURL", "context_pnFaqSendURL", "recipient_taxId");
    }

    @Bean
    public List<String> confirmBodyFields() {
        return List.of("context_verificationCode");
    }

    @Bean
    public List<String> notificationAARForSMSFields() {
        return List.of("context_notification", "notification_iun", "notification_sender", "notification_sender_paDenomination");
    }

    @Bean
    public List<String> notificationAARSubjectFields() {
        return List.of("context_notification", "notification_sender", "notification_sender_paDenomination");
    }

    @Bean
    public Map<TemplateType, List<String>> templateEngineObjectFields(List<String> notificationReceiverLegalFactFields, List<String> pecDeliveryWorkflowLegalFactFields,
                                                                      List<String> notificationViewedLegalFactFields, List<String> legalFactMalfunctionFields, List<String> notificationCancelledLegalFactFields,
                                                                      List<String> analogDeliveryWorkflowFailureLegalFactFields, List<String> notificationAARFields, List<String> notificationAARRADDaltFields,
                                                                      List<String> notificationAARForEMAILFields, List<String> notificationAARForPECFields, List<String> notificationAARForSMSFields,
                                                                      List<String> notificationAARSubjectFields, List<String> confirmBodyFields) {
        Map<TemplateType, List<String>> map = new HashMap<>();
        map.put(TemplateType.AAR_PRESA_IN_CARICO, notificationReceiverLegalFactFields);
        map.put(TemplateType.AAR_NOTIFICA_DIGITALE, pecDeliveryWorkflowLegalFactFields);
        map.put(TemplateType.AAR_AVVENUTO_ACCESSO, notificationViewedLegalFactFields);
        map.put(TemplateType.AAR_FUNZIONAMENTO_RIPRISTINO, legalFactMalfunctionFields);
        map.put(TemplateType.AAR_ANNULLAMENTO_NOTIFICA, notificationCancelledLegalFactFields);
        map.put(TemplateType.DEPOSITO_AVVENUTA_RICEZIONE, analogDeliveryWorkflowFailureLegalFactFields);
        map.put(TemplateType.AVVISO_AVVENUTA_RICEZIONE, notificationAARFields);
        map.put(TemplateType.AVVISO_AVVENUTA_RICEZIONE_RADD, notificationAARRADDaltFields);
        map.put(TemplateType.AVVISO_CORTESIA_EMAIL, notificationAARForEMAILFields);
        map.put(TemplateType.AVVISO_CORTESIA_PEC, notificationAARForPECFields);
        map.put(TemplateType.OTP_CONFERMA_EMAIL, confirmBodyFields);
        map.put(TemplateType.AVVISO_CORTESIA_SMS, notificationAARForSMSFields);
        map.put(TemplateType.AVVISO_CORTESIA_SMS_OBJECT, notificationAARSubjectFields);
        map.put(TemplateType.OTP_CONFERMA_PEC, confirmBodyFields);

        return map;
    }
}
