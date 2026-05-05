package it.pagopa.pn.cucumber.steps.dataTable;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.*;
import it.pagopa.pn.cucumber.utils.NotificationInformalValue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.pn.cucumber.utils.NotificationInformalValue.*;

@Component
public class InformalNotificationRequestMapper {

    // New message
    public NewMessageRequest buildNewMessageRequest(Map<String, String> data) {
        NewMessageRequestPrimaryMessage primaryMessage = buildMessageContent(
                getValue(data, NotificationInformalValue.PRIMARY_SUBJECT.key),
                getValue(data, NotificationInformalValue.PRIMARY_LONG_BODY.key),
                getValue(data, NotificationInformalValue.PRIMARY_SHORT_BODY.key),
                getValue(data, NotificationInformalValue.PRIMARY_LANGUAGE.key)
        );
        NewMessageRequestAdditionalMessage additionalMessage = buildAdditionalMessageContent(
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

        String notifLang = getValue(data, NOTIFICATION_ADDITIONAL_LANGUAGE.key);
        if (notifLang != null) {
            request.setAdditionalLanguages(List.of(notifLang));
        }

        //request.setRecipients(List.of(buildRecipient(data)));

        //  NESSUN DESTINATARIO DI DEFAULT
        request.setRecipients(new ArrayList<>());

        request.setDocuments(List.of(buildDocument(data)));
        return request;
    }
    // builder
    private NewMessageRequestPrimaryMessage buildMessageContent(
            String subject,
            String longBody,
            String shortBody,
            String language) {

        if (subject == null && longBody == null && shortBody == null) {
            return null;
        }
        NewMessageRequestPrimaryMessage content = new NewMessageRequestPrimaryMessage();
        content.setSubject(subject);
        content.setLongBody(longBody);
        content.setShortBody(shortBody);
        content.setLanguage(language);
        return content;
    }
    private NewMessageRequestAdditionalMessage buildAdditionalMessageContent(
            String subject,
            String longBody,
            String shortBody,
            String language) {

        if (subject == null && longBody == null && shortBody == null) {
            return null;
        }
        NewMessageRequestAdditionalMessage content = new NewMessageRequestAdditionalMessage();
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

        String messageId = getValue(data, MESSAGE_ID.key);
        if (messageId != null) {
            informalNotificationRecipient.setMessageId(UUID.fromString(messageId));
        }
        if (getValue(data, PEC_ADDRESS.key) != null) {
            informalNotificationRecipient.setDigitalDomicile(new NotificationDigitalAddress()
                    .type(NotificationDigitalAddress.TypeEnum.PEC)
                    .address(getValue(data, PEC_ADDRESS.key))
            );
        }
        // Payments
        informalNotificationRecipient.setPayments(List.of(buildPaymentItem(data)));

        int paymentCount = Integer.parseInt(
                getValue(data, PAYMENT_COUNT.key)
        );
        List<InformalNotificationPaymentItem> payments =
                new ArrayList<>();

        for (int i = 0; i < paymentCount; i++) {
            payments.add(buildPaymentItem(data));
        }
        return informalNotificationRecipient;
    }

//    private InformalNotificationPaymentItem buildPaymentItem(Map<String, String> data) {
//        PagoPaPayment pagoPa = new PagoPaPayment()
//                .noticeCode(getValue(data, PAGOPA_NOTICE_CODE.key))
//                .creditorTaxId(getValue(data, PAGOPA_CREDITOR_TAX_ID.key))
//                .applyCost(true);
//
//        InformalNotificationPaymentItem item = new InformalNotificationPaymentItem();
//        item.setPagoPa(pagoPa);
//        return item;
//    }

//    private NotificationDocument buildDocument(Map<String, String> data) {
//        NotificationDocument d = new NotificationDocument();
//        d.setTitle(getValue(data, DOCUMENT_TITLE.key));
//        d.setDocIdx(getValue(data, DOCUMENT_DOCIDX.key));
//        d.setContentType("application/pdf");
//        return d;
//    }

    public NotificationPaymentAttachment buildPaymentAttachment(
            Map<String, String> data) {

        // Digests
        NotificationAttachmentDigests digests =
                new NotificationAttachmentDigests();
        digests.setSha256(
                getValue(data, "attachment_sha256")
        );

        // Ref
        NotificationAttachmentBodyRef ref =
                new NotificationAttachmentBodyRef();
        ref.setKey(
                getValue(data, "attachment_key")
        );
        ref.setVersionToken(
                getValue(data, "attachment_version_token")
        );

        // Attachment
        NotificationPaymentAttachment attachment =
                new NotificationPaymentAttachment();
        attachment.setDigests(digests);
        attachment.setContentType("application/pdf");
        attachment.setRef(ref);

        return attachment;
    }

    private InformalNotificationPaymentItem buildPaymentItem(
            Map<String, String> data) {

        PagoPaPaymentBase pagoPaBase = new PagoPaPaymentBase()
                .noticeCode(
                        getValue(data, PAGOPA_NOTICE_CODE.key)
                )
                .creditorTaxId(
                        getValue(data, PAGOPA_CREDITOR_TAX_ID.key)
                )
                //.applyCost(false)
                .attachment(
                        buildPaymentAttachment(data)
                );

        InformalNotificationPaymentItem item =
                new InformalNotificationPaymentItem();
        item.setPagoPa(pagoPaBase);

        return item;
    }

    private NotificationDocument buildDocument(Map<String, String> data) {

        NotificationAttachmentDigests digests =
                new NotificationAttachmentDigests();
        digests.setSha256(
                getValue(data, "document_sha256")
        );

        NotificationAttachmentBodyRef ref =
                new NotificationAttachmentBodyRef();
        ref.setKey(
                getValue(data, "document_key")
        );
        ref.setVersionToken(
                getValue(data, "document_version_token")
        );

        NotificationDocument document =
                new NotificationDocument();
        document.setDigests(digests);
        document.setContentType("application/pdf");
        document.setRef(ref);

        document.setTitle(getValue(data, DOCUMENT_TITLE.key));
        document.setDocIdx(getValue(data, DOCUMENT_DOCIDX.key));

        return document;
    }
}





