package it.pagopa.pn.cucumber.steps.informalNotification.adapter.v1;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.*;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.*;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.RecipientType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

// ReportingPolicy.ERROR: un campo del model V1 non mappato (es. aggiunto/rinominato) fa fallire la compilazione
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface InformalCommunicationV1Mapper {
    InformalCommunicationV1Mapper INSTANCE = Mappers.getMapper(InformalCommunicationV1Mapper.class);

    InformalNotificationRequestV1 toV1(InformalCommunication domain);

    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "digitalDomicile", source = "digitalDomicile", qualifiedByName = "mapDigitalDomicile")
    @Mapping(target = "recipientType", source = "recipientType", qualifiedByName = "mapRecipientType")
    InformalNotificationRecipientV1 toRecipientV1(InformalRecipient recipient);

    @Mapping(target = "municipalityDetails", ignore = true)
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
                    .ref(new NotificationAttachmentBodyRef().key(payment.getAttachmentKey()).versionToken(payment.getAttachmentVersionToken()));
            if (payment.getAttachmentSha256() != null) {
                attachment.setDigests(new NotificationAttachmentDigests().sha256(payment.getAttachmentSha256()));
            }
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
    default InformalNotificationRecipientV1.RecipientTypeEnum mapRecipientType(RecipientType recipientType) {
        if (recipientType == null) {
            return null;
        }
        return InformalNotificationRecipientV1.RecipientTypeEnum.fromValue(recipientType.name());
    }
}
