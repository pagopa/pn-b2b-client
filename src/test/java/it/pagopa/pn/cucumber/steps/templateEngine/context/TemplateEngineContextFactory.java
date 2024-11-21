package it.pagopa.pn.cucumber.steps.templateEngine.context;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.*;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TemplateEngineContextFactory {

    public TemplateRequestContext createContext(Map<String, String> parameters) {
        String deliveryValue = parameters.get("delivery");
        String notificationValue = parameters.get("notification");
        String delegateValue = parameters.get("delegate");
        String recipientValue = parameters.get("recipient");
        TemplateRequestContext context = new TemplateRequestContext();
        String digest = getParameter(parameters, "context_digest");
        context.setNotification(notificationValue != null ? notificationValue.equals("null") ? null : getNotification(parameters) : getNotification(parameters));
        context.setSendDate(getParameter(parameters, "context_senddate"));
        context.setDigests(digest != null ? List.of(digest) : null);
        context.setSubject(getParameter(parameters, "context_subject"));
        context.setEndWorkflowDate(getParameter(parameters, "context_endWorkflowDate"));
        context.setEndWorkflowStatus(getParameter(parameters, "context_endWorkflowStatus"));
        context.setIun(getParameter(parameters, "context_iun"));
        context.setDeliveries(deliveryValue != null ? deliveryValue.equals("null") ? null : List.of(getDelivery(parameters)) : List.of(getDelivery(parameters)));
        context.setPhysicalAddressAndDenomination(getParameter(parameters, "context_physicalAddressAndDenomination"));
        context.setWhen(getParameter(parameters, "context_when"));
        context.setDelegate(delegateValue != null ? delegateValue.equals("null") ? null : getDelegate(parameters) : getDelegate(parameters));
        context.setRecipient(recipientValue != null ? recipientValue.equals("null") ? null : getRecipients(parameters) : getRecipients(parameters));
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

        return context;
    }

    private Notification getNotification(Map<String, String> parameters) {
        String recipientsValue = parameters.get("notification_recipient");
        String senderValue = parameters.get("sender");
        return new Notification()
                .iun(getParameter(parameters, "notification_iun"))
                .subject(getParameter(parameters, "notification_subject"))
                .recipients(recipientsValue != null ? recipientsValue.equals("null") ? null : List.of(getRecipients(parameters)) : List.of(getRecipients(parameters)))
                .sender(senderValue != null ? senderValue.equals("null") ? null : getNotificationSender(parameters) : getNotificationSender(parameters));
    }

    private Recipient getRecipients(Map<String, String> parameters) {
        return new Recipient()
                .recipientType(getParameter(parameters, "notification_recipient_recipientType"))
                .denomination(getParameter(parameters, "notification_recipient_denomination"))
                .taxId(getParameter(parameters, "notification_recipient_taxId"))
                .physicalAddress(getParameter(parameters, "notification_recipient_physicalAddress"))
                .digitalDomicile(getDigitalDomicile(parameters));
    }

    private DigitalDomicile getDigitalDomicile(Map<String, String> parameters) {
        return new DigitalDomicile()
                .address(getParameter(parameters, "notification_recipient_digitalDomicile_address"));
    }

    private NotificationSender getNotificationSender(Map<String, String> parameters) {
        return new NotificationSender()
                .paDenomination(getParameter(parameters, "notification_sender_paDenomination"))
                .paId(getParameter(parameters, "notification_sender_paId"))
                .paTaxId(getParameter(parameters, "notification_sender_paTaxId"));
    }

    private Delivery getDelivery(Map<String, String> parameters) {
        String okValue = parameters.get("delivery_ok");
        Delivery delivery =  new Delivery()
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

    private Delegate getDelegate(Map<String, String> parameters) {
        return new Delegate()
                .denomination(getParameter(parameters, "delegate_denomination"))
                .taxId(getParameter(parameters, "delegate_taxId"));
    }

    private String getParameter(Map<String, String> parameters, String key) {
        return parameters.get(key) == null ? "string" : parameters.get(key).equals("null") ? null : parameters.get(key);
    }
}
