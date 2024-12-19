package it.pagopa.pn.cucumber.steps.templateEngine.context;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TemplateEngineContextFactory {

    public TemplateRequestContext createContext(Map<String, String> parameters, String notificationTpe) {
        String deliveryValue = parameters.get("context_delivery");
        String notificationValue = parameters.get("context_notification");
        String delegateValue = parameters.get("context_delegate");
        String recipientValue = parameters.get("context_recipient");
        TemplateRequestContext context = new TemplateRequestContext();
        String digest = getParameter(parameters, "context_digest");
        context.setNotification(notificationValue != null ? notificationValue.equals("null") ? null : getNotification(parameters, notificationTpe) : getNotification(parameters, notificationTpe));
        context.setSendDate(getParameter(parameters, "context_senddate"));
        context.setDigests(createDigest(digest, notificationTpe));
        context.setSubject(getParameter(parameters, "context_subject"));
        context.setEndWorkflowDate(getParameter(parameters, "context_endWorkflowDate"));
        context.setEndWorkflowStatus(getParameter(parameters, "context_endWorkflowStatus"));
        context.setIun(getParameter(parameters, "context_iun"));
        context.setDeliveries(deliveryValue != null ? deliveryValue.equals("null") ? null : List.of(getDelivery(parameters)) : List.of(getDelivery(parameters)));
        context.setPhysicalAddressAndDenomination(getParameter(parameters, "context_physicalAddressAndDenomination"));
        context.setWhen(getParameter(parameters, "context_when"));
        context.setDelegate(delegateValue != null ? delegateValue.equals("null") ? null : getDelegate(parameters) : getDelegate(parameters));
        context.setRecipient(recipientValue != null ? recipientValue.equals("null") ? null : getRecipients(parameters, "") : getRecipients(parameters, ""));
        context.setEndDate(getParameter(parameters, "context_endDate"));
        context.setStartDate(getParameter(parameters, "context_startDate"));
        context.setEndWorkflowTime(getParameter(parameters, "context_endWorkflowTime"));
        context.setTimeReferenceStartDate(getParameter(parameters, "context_timeReferenceStartDate"));
        context.setTimeReferenceEndDate(getParameter(parameters, "context_timeReferenceEndDate"));
        context.setNotificationCancelledDate(getParameter(parameters, "context_NotificationCancelledDate"));
        context.setQrCodeQuickAccessLink(getParameter(parameters, "context_qrCodeQuickAccessLink"));
        context.setPiattaformaNotificheURL(getParameter(parameters, "context_piattaformaNotificheURL"));
        context.setPiattaformaNotificheURLLabel(getParameter(parameters, "context_piattaformaNotificheURLLabel"));
        context.setPerfezionamentoURL(getParameter(parameters, "context_perfezionamentoURL"));
        context.setPerfezionamentoURLLabel(getParameter(parameters, "context_perfezionamentoURLLabel"));
        context.setSendURL(getParameter(parameters, "context_sendURL"));
        context.setSendURLLAbel(getParameter(parameters, "context_sendURLLAbel"));
        context.setPnFaqSendURL(getParameter(parameters, "context_pnFaqSendURL"));
        context.setSendLogoLink(getParameter(parameters, "context_sendLogoLink"));
        context.setLogoBase64(getParameter(parameters, "context_logoBase64"));
        context.setRecipientType(getParameter(parameters, "context_recipientType"));
        context.setVerificationCode(getParameter(parameters, "context_verificationCode"));
        context.setRaddPhoneNumber(getParameter(parameters, "context_raddPhoneNumber"));
        context.setRecipients(getParameter(parameters, "context_recipients"));

        return context;
    }

    private static List<String> createDigest(String digest, String notificationTpe) {
        if (digest == null) return null;
        else {
            if (notificationTpe.equals("piu allegati")) {
                return List.of("TEST_digest_allegato", "TEST_digest_allegato");
            } else return List.of("TEST_digest_allegato");
        }
    }

    private TemplateNotification getNotification(Map<String, String> parameters, String notificationTpe) {
        String recipientsValue = parameters.get("notification_recipient");
        String senderValue = parameters.get("notification_sender");
        return new TemplateNotification()
                .iun(getParameter(parameters, "notification_iun"))
                .subject(getParameter(parameters, "notification_subject"))
                .recipients(getRecipientList(parameters, recipientsValue, notificationTpe))
                .sender(senderValue != null ? senderValue.equals("null") ? null : getNotificationSender(parameters) : getNotificationSender(parameters));
    }

    private List<TemplateRecipient> getRecipientList(Map<String, String> parameters, String recipientsValue, String notificationTpe) {
        List<TemplateRecipient> recipients;
        if (recipientsValue != null && recipientsValue.equals("null")) {
            return null;
        } else {
            if (notificationTpe.equals("multidestinatario")) {
                recipients = List.of(getRecipients(parameters, "notification_"), getRecipients(parameters, "notification_"));
            } else recipients = List.of(getRecipients(parameters, "notification_"));
        }
        /*if (recipientsValue != null) {
            if (recipientsValue.equals("null")) {
                recipients = null;
            } else recipients = List.of(getRecipients(parameters, "notification_"));
        } else recipients = List.of(getRecipients(parameters, "notification_"));*/
        return recipients;
    }

    private TemplateRecipient getRecipients(Map<String, String> parameters, String suffix) {
        String digitalDomicile = parameters.get("notification_recipient_digitalDomicile");
        return new TemplateRecipient()
                .recipientType(getParameter(parameters, suffix + "recipient_recipientType"))
                .denomination(getParameter(parameters, suffix + "recipient_denomination"))
                .taxId(getParameter(parameters, suffix + "recipient_taxId"))
                .physicalAddressAndDenomination(getParameter(parameters, suffix + "recipient_physicalAddress"))
                .digitalDomicile(digitalDomicile != null ? digitalDomicile.equals("null") ? null : getDigitalDomicile(parameters) : getDigitalDomicile(parameters));
    }

    private TemplateDigitalDomicile getDigitalDomicile(Map<String, String> parameters) {
        return new TemplateDigitalDomicile()
                .address(getParameter(parameters, "notification_recipient_digitalDomicile_address"));
    }

    private TemplateSender getNotificationSender(Map<String, String> parameters) {
        return new TemplateSender()
                .paDenomination(getParameter(parameters, "notification_sender_paDenomination"))
                //.paId(getParameter(parameters, "notification_sender_paId"))
                .paTaxId(getParameter(parameters, "notification_sender_paTaxId"));
    }

    private TemplateDelivery getDelivery(Map<String, String> parameters) {
        String okValue = parameters.get("delivery_ok");
        TemplateDelivery delivery =  new TemplateDelivery()
                .address(getParameter(parameters, "delivery_address"))
                .taxId(getParameter(parameters, "delivery_taxId"))
                .type(getParameter(parameters, "delivery_type"))
                .addressSource(getParameter(parameters, "delivery_addressSource"))
                .responseDate(getParameter(parameters, "delivery_responseDate"))
                .denomination(getParameter(parameters, "delivery_denomination"));
        if (okValue == null || !okValue.equals("null"))
            delivery.ok(true);
        return delivery;
    }

    private TemplateDelegate getDelegate(Map<String, String> parameters) {
        return new TemplateDelegate()
                .denomination(getParameter(parameters, "delegate_denomination"))
                .taxId(getParameter(parameters, "delegate_taxId"));
    }

    private String getParameter(Map<String, String> parameters, String key) {
        return parameters.get(key) == null ? "string" : parameters.get(key).equals("null") ? null : parameters.get(key);
    }
}
