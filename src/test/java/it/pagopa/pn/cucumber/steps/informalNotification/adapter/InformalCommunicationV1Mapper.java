package it.pagopa.pn.cucumber.steps.informalNotification.adapter;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.*;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InformalCommunicationV1Mapper {
    InformalCommunicationV1Mapper INSTANCE = Mappers.getMapper(InformalCommunicationV1Mapper.class);

    InformalNotificationRequestV1 toV1(InformalCommunication domain);

    @Mapping(target = "digitalDomicile", source = "digitalDomicile", qualifiedByName = "mapDigitalDomicile")
    @Mapping(target = "physicalAddress", source = "physicalAddress")
    @Mapping(target = "payments", source = "payments")
    @Mapping(target = "recipientType", source = "recipientType", qualifiedByName = "mapRecipientType")
    InformalNotificationRecipientV1 toRecipientV1(InformalRecipient recipient);

    NotificationPhysicalAddress toPhysicalAddressV1(InformalPhysicalAddress address);

    default InformalNotificationPaymentItem toPaymentItemV1(InformalPayment payment) {
        if (payment == null) {
            return null;
        }
        PagoPaPaymentBase pagoPa = new PagoPaPaymentBase()
                .noticeCode(payment.getNoticeCode())
                .creditorTaxId(payment.getCreditorTaxId())
                .amount(payment.getAmount())
                .dueDate(payment.getDueDate());

        if (payment.getAttachmentKey() != null || payment.getAttachmentContentType() != null) {
            NotificationPaymentAttachment attachment = new NotificationPaymentAttachment()
                    .contentType(payment.getAttachmentContentType())
                    .ref(new NotificationAttachmentBodyRef().key(payment.getAttachmentKey()));
            pagoPa.setAttachment(attachment);
        }

        return new InformalNotificationPaymentItem().pagoPa(pagoPa);
    }

    default NotificationDocument toDocumentV1(InformalDocument doc) {
        if (doc == null) {
            return null;
        }
        NotificationDocument document = new NotificationDocument()
                .contentType(doc.getContentType())
                .ref(new NotificationAttachmentBodyRef().key(doc.getKey()).versionToken(doc.getVersionToken()));
        if (doc.getSha256() != null) {
            document.setDigests(new NotificationAttachmentDigests().sha256(doc.getSha256()));
        }
        return document;
    }

    @Named("mapDigitalDomicile")
    default NotificationDigitalAddress mapDigitalDomicile(String digitalDomicile) {
        if (digitalDomicile == null || digitalDomicile.isBlank() || "NULL".equalsIgnoreCase(digitalDomicile)) {
            return null;
        }
        return new NotificationDigitalAddress()
                .type(NotificationDigitalAddress.TypeEnum.PEC)
                .address(digitalDomicile);
    }

    @Named("mapRecipientType")
    default InformalNotificationRecipientV1.RecipientTypeEnum mapRecipientType(it.pagopa.pn.cucumber.steps.informalNotification.domain.model.RecipientType recipientType) {
        if (recipientType == null) {
            return null;
        }
        return InformalNotificationRecipientV1.RecipientTypeEnum.fromValue(recipientType.name());
    }
}
