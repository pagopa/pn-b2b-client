package it.pagopa.pn.cucumber.steps.dataTable;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.*;
import it.pagopa.pn.cucumber.utils.NotificationInformalValue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.pn.cucumber.utils.NotificationInformalValue.*;

@Component
public class InformalNotificationRequestMapper {


    // New message
    public NewMessageRequest buildNewMessageRequest(Map<String, String> data) {
        LocalizedContent primaryMessage = buildMessageContent(
                getValue(data, NotificationInformalValue.PRIMARY_SUBJECT.key),
                getValue(data, NotificationInformalValue.PRIMARY_LONG_BODY.key),
                getValue(data, NotificationInformalValue.PRIMARY_SHORT_BODY.key),
                getValue(data, NotificationInformalValue.PRIMARY_LANGUAGE.key)
        );
        LocalizedContent additionalMessage = buildMessageContent(
                getValue(data, NotificationInformalValue.ADDITIONAL_SUBJECT.key),
                getValue(data, NotificationInformalValue.ADDITIONAL_LONG_BODY.key),
                getValue(data, NotificationInformalValue.ADDITIONAL_SHORT_BODY.key),
                getValue(data, NotificationInformalValue.ADDITIONAL_LANGUAGE.key)
        );
        return new NewMessageRequest()
                .primaryMessage(primaryMessage)
                .additionalMessage(additionalMessage);
    }

    // New Informal Notification
    public InformalNotificationRequestV1 buildInformalNotificationRequest(Map<String, String> data) {
        InformalNotificationRequestV1 request = new InformalNotificationRequestV1();

        request.setSenderDenomination(getValue(data, SENDER_DENOMINATION.key));
        request.setSenderTaxId(getValue(data, SENDER_TAX_ID.key));
        request.setPaProtocolNumber(getValue(data, PA_PROTOCOL_NUMBER.key));
        request.setIdempotenceToken(getValue(data, IDEMPOTENCE_TOKEN.key));
        request.setCampaignId(getValue(data, CAMPAIGN_ID.key));
        request.setSubject(getValue(data, SUBJECT.key));
        request.setGroup(getValue(data, GROUP.key));

        String messageId = getValue(data, MESSAGE_ID.key);
        if (messageId != null) {
            request.setMessageId(UUID.fromString(messageId));
        }
        String lang = getValue(data, ADDITIONAL_LANGUAGE.key);
        if (lang != null) {
            request.setAdditionalLanguages(List.of(lang));
        }
        request.setRecipients(List.of(buildRecipient(data)));
        request.setDocuments(List.of(buildDocument(data)));
        return request;
    }

    // builder

    private LocalizedContent buildMessageContent(
            String subject,
            String longBody,
            String shortBody,
            String language) {

        if (subject == null && longBody == null && shortBody == null) {
            return null;
        }
        LocalizedContent content = new LocalizedContent();
        content.setSubject(subject);
        content.setLongBody(longBody);
        content.setShortBody(shortBody);
        content.setLanguage(language);
        return content;
    }

    private InformalNotificationRecipientV1 buildRecipient(Map<String, String> data) {
        InformalNotificationRecipientV1 informalNotificationRecipient = new InformalNotificationRecipientV1();

        informalNotificationRecipient.setRecipientType(
                InformalNotificationRecipientV1.RecipientTypeEnum
                        .fromValue(getValue(data, RECIPIENT_TYPE.key))
        );
        informalNotificationRecipient.setTaxId(getValue(data, RECIPIENT_TAX_ID.key));
        informalNotificationRecipient.setDenomination(getValue(data, RECIPIENT_DENOMINATION.key));

        if (getValue(data, PEC_ADDRESS.key) != null) {
            informalNotificationRecipient.setDigitalDomicile(new NotificationDigitalAddress()
                    .type(NotificationDigitalAddress.TypeEnum.PEC)
                    .address(getValue(data, PEC_ADDRESS.key))
            );
        }
        // Payments
        informalNotificationRecipient.setPayments(List.of(buildPaymentItem(data)));
        return informalNotificationRecipient;
    }

    private InformalNotificationPaymentItem buildPaymentItem(Map<String, String> data) {
        PagoPaPayment pagoPa = new PagoPaPayment()
                .noticeCode(getValue(data, PAGOPA_NOTICE_CODE.key))
                .creditorTaxId(getValue(data, PAGOPA_CREDITOR_TAX_ID.key))
                .applyCost(true);

        InformalNotificationPaymentItem item = new InformalNotificationPaymentItem();
        item.setPagoPa(pagoPa);
        return item;
    }

    private NotificationDocument buildDocument(Map<String, String> data) {
        NotificationDocument d = new NotificationDocument();
        d.setTitle(getValue(data, DOCUMENT_TITLE.key));
        d.setDocIdx(getValue(data, DOCUMENT_DOCIDX.key));
        d.setContentType("application/pdf");
        return d;
    }
}





