package it.pagopa.pn.cucumber.steps.informalNotification.mapper;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.*;
import it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.*;

@Component
public class InformalNotificationRequestMapper {

    // New message
    public NewMessageRequest buildNewMessageRequest(Map<String, String> data) {
        NewMessageRequestPrimaryMessage primaryMessage = buildMessageContent(getValue(data, NotificationInformalValue.PRIMARY_SUBJECT.key), getValue(data, NotificationInformalValue.PRIMARY_LONG_BODY.key), getValue(data, NotificationInformalValue.PRIMARY_SHORT_BODY.key), getValue(data, NotificationInformalValue.PRIMARY_LANGUAGE.key));
        NewMessageRequestAdditionalMessage additionalMessage = buildAdditionalMessageContent(getValue(data, NotificationInformalValue.ADDITIONAL_SUBJECT.key), getValue(data, NotificationInformalValue.ADDITIONAL_LONG_BODY.key), getValue(data, NotificationInformalValue.ADDITIONAL_SHORT_BODY.key), getValue(data, NotificationInformalValue.ADDITIONAL_LANGUAGE.key));
        return new NewMessageRequest().primaryMessage(primaryMessage).additionalMessage(additionalMessage);
    }

    // New Informal Notification
    public InformalNotificationRequestV1 buildInformalNotificationRequest(Map<String, String> data) {
        InformalNotificationRequestV1 request = new InformalNotificationRequestV1();

        request.setSenderDenomination(getValue(data, SENDER_DENOMINATION.key));
        request.setPaProtocolNumber(getValue(data, PA_PROTOCOL_NUMBER.key));
        request.setIdempotenceToken(getValue(data, IDEMPOTENCE_TOKEN.key));
        request.setCampaignId(getValue(data, CAMPAIGN_ID.key));
        request.setSubject(getValue(data, SUBJECT.key));
        request.setGroup(getValue(data, GROUP.key));

//        String notifLang = getValue(data, NOTIFICATION_ADDITIONAL_LANGUAGE.key);
//        if (notifLang != null) {
//            request.setAdditionalLanguages(List.of(notifLang));
//        }
        request.setGroup(getValue(data, GROUP.key));
        //  NESSUN DESTINATARIO DI DEFAULT
        request.setRecipients(new ArrayList<>());
        request.setDocuments(buildDocuments(data));
        return request;
    }

    // builder
    private NewMessageRequestPrimaryMessage buildMessageContent(String subject, String longBody, String shortBody, String language) {

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

    private NewMessageRequestAdditionalMessage buildAdditionalMessageContent(String subject, String longBody, String shortBody, String language) {

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

    private List<NotificationDocument> buildDocuments(Map<String, String> data) {

        String documentsToAdd = getValue(data, DOCUMENT.key);
        List<NotificationDocument> result = new ArrayList<>();

        if (documentsToAdd == null) {
            result.add(buildDefaultDocument(data));
            return result;
        }
        for (String doc : documentsToAdd.split(";")) {

            String path = getDocumentPath(doc);
            NotificationDocument document = new NotificationDocument().contentType("application/pdf").ref(new NotificationAttachmentBodyRef().key(path));
            result.add(document);
        }
        return result;
    }

    private String getDocumentPath(String documentElem) {

        return switch (documentElem.toUpperCase().trim()) {
            case "DOC_1_PG" -> "classpath:/sample_1pg.pdf";
            case "DOC_2_PG" -> "classpath:/sample_2pg.pdf";
            case "DOC_3_PG" -> "classpath:/sample_3pg.pdf";
            case "DOC_4_PG" -> "classpath:/sample_4pg.pdf";
            case "DOC_30MB" -> "classpath:/allegato_30Mb.pdf";
            case "ALLEGATO_1_BN" -> "classpath:/Allegato1_BN.pdf";
            case "ALLEGATO_2_BN" -> "classpath:/Allegato2_BN.pdf";

            default -> throw new IllegalArgumentException("Documento non supportato: " + documentElem);
        };
    }

    // fallback
    private NotificationDocument buildDefaultDocument(Map<String, String> data) {
        return new NotificationDocument().contentType("application/pdf").ref(new NotificationAttachmentBodyRef().key("classpath:/sample_1pg.pdf"));
    }

    // =========================
    // ATTACHMENT PAGAMENTO
    // =========================

    public NotificationPaymentAttachment buildPaymentAttachment(Map<String, String> data) {

        NotificationAttachmentBodyRef ref = new NotificationAttachmentBodyRef();
        ref.setKey(getValue(data, ATTACHMENT_KEY.key));

        NotificationPaymentAttachment attachment = new NotificationPaymentAttachment();
        attachment.setContentType(getValue(data, ATTACHMENT_CONTENT_TYPE.key));
        attachment.setRef(ref);
        return attachment;
    }
}





