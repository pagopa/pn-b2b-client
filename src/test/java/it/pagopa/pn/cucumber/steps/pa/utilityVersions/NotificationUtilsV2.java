package it.pagopa.pn.cucumber.steps.pa.utilityVersions;

import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.*;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationStepsV2;
import it.pagopa.pn.cucumber.utils.NotificationValue;

import java.io.IOException;
import java.util.Map;

import static it.pagopa.pn.client.b2b.pa.PnPaB2bUtils.APPLICATION_PDF;
import static it.pagopa.pn.client.b2b.pa.PnPaB2bUtils.LOAD_TO_PRESIGNED;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

public class NotificationUtilsV2 extends AbstractNotificationUtils {

    private NotificationStepsV2 notificationStep;

    public NotificationUtilsV2(NotificationStepsV2 notificationStep) {
        this.notificationStep = notificationStep;
    }

    @Override
    public PnPaB2bUtils getB2bUtils() {
        return notificationStep.getSharedSteps().getB2bUtils();
    }

    public synchronized NewNotificationRequest convertNotificationRequest(Map<String, String> data) {
        NewNotificationRequest notificationRequest = (new NewNotificationRequest()
                .subject(getValue(data, SUBJECT.key))
                .cancelledIun(getValue(data, CANCELLED_IUN.key))
                .group(getValue(data, GROUP.key))
                .idempotenceToken(getValue(data, IDEMPOTENCE_TOKEN.key))
                ._abstract(getValue(data, ABSTRACT.key))
                .senderDenomination(getValue(data, SENDER_DENOMINATION.key))
                .senderTaxId(getValue(data, SENDER_TAX_ID.key))
                .paProtocolNumber(getValue(data, PA_PROTOCOL_NUMBER.key))
                .taxonomyCode(getValue(data, TAXONOMY_CODE.key))
                .amount(getValue(data, AMOUNT.key) == null ? null : Integer.parseInt(getValue(data, AMOUNT.key)))
                .paymentExpirationDate(getValue(data, PAYMENT_EXPIRATION_DATE.key) == null ? null : getValue(data, PAYMENT_EXPIRATION_DATE.key))
                .notificationFeePolicy(
                        (getValue(data, NOTIFICATION_FEE_POLICY.key) == null ? null :
                                (getValue(data, NOTIFICATION_FEE_POLICY.key).equalsIgnoreCase("FLAT_RATE") ?
                                        NotificationFeePolicy.FLAT_RATE : NotificationFeePolicy.DELIVERY_MODE)))
                .physicalCommunicationType(
                        (getValue(data, PHYSICAL_COMMUNICATION_TYPE.key) == null ? null :
                                (getValue(data, PHYSICAL_COMMUNICATION_TYPE.key).equalsIgnoreCase("REGISTERED_LETTER_890") ?
                                        NewNotificationRequest.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        NewNotificationRequest.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
                .addDocumentsItem(getValue(data, DOCUMENT.key) == null ? null : newDocument(getDefaultValue(DOCUMENT.key)))
                .pagoPaIntMode(
                        (getValue(data, PAGOPAINTMODE.key) == null ? null :
                                (getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("SYNC") ?
                                        NewNotificationRequest.PagoPaIntModeEnum.SYNC : NewNotificationRequest.PagoPaIntModeEnum.NONE))));
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequest;
    }

    public synchronized NotificationRecipient convertNotificationRecipient(Map<String, String> data) {
        NotificationRecipient notificationRecipient = (new NotificationRecipient()
                .denomination(getValue(data, DENOMINATION.key))
                .taxId(getValue(data, TAX_ID.key))
                //.internalId(getValue(data,INTERNAL_ID.key))
                .digitalDomicile(getValue(data, DIGITAL_DOMICILE.key) == null ? null : (new NotificationDigitalAddress()
                        .type((getValue(data, DIGITAL_DOMICILE_TYPE.key) == null ? null : NotificationDigitalAddress.TypeEnum.PEC))
                        .address(getValue(data, DIGITAL_DOMICILE_ADDRESS.key)))
                )
                .physicalAddress(getValue(data, PHYSICAL_ADDRES.key) == null ? null : new NotificationPhysicalAddress()
                        .address(getValue(data, PHYSICAL_ADDRESS_ADDRESS.key))
                        .addressDetails(getValue(data, PHYSICAL_ADDRESS_DETAILS.key))
                        .municipality(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITY.key))
                        .at(getValue(data, PHYSICAL_ADDRESS_AT.key))
                        .municipalityDetails(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITYDETAILS.key))
                        .province(getValue(data, PHYSICAL_ADDRESS_PROVINCE.key))
                        .foreignState(getValue(data, PHYSICAL_ADDRESS_STATE.key))
                        .zip(getValue(data, PHYSICAL_ADDRESS_ZIP.key))
                )
                .recipientType((getValue(data, RECIPIENT_TYPE.key) == null ? null :
                        (getValue(data, RECIPIENT_TYPE.key).equalsIgnoreCase("PF") ?
                                NotificationRecipient.RecipientTypeEnum.PF : NotificationRecipient.RecipientTypeEnum.PG)))
                .payment(getValue(data, NotificationValue.PAYMENT.key) == null ? null : new NotificationPaymentInfo()
                                .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                                .noticeCodeAlternative(getValue(data, PAYMENT_NOTICE_CODE_OPTIONAL.key).equalsIgnoreCase("SI") ? getDefaultValue(PAYMENT_NOTICE_CODE_OPTIONAL.key) : null)
                                .pagoPaForm(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ?
                                        null : newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))
                        //                  .f24flatRate(getValue(data, PAYMENT_F24_FLAT.key) == null ? null :
                        //                  (getValue(data, PAYMENT_F24_FLAT.key).equalsIgnoreCase("SI")?
                        //                                  utils.newAttachment(getDefaultValue(PAYMENT_F24_FLAT.key)):null))
                        //
                        //                    .f24standard(getValue(data, PAYMENT_F24_STANDARD.key) == null ? null :
                        //                           (getValue(data, PAYMENT_F24_STANDARD.key).equalsIgnoreCase("SI")?
                        //                                  utils.newAttachment(getDefaultValue(PAYMENT_F24_STANDARD.key)):null))
                )
        );
        /* TEST
        if(getValue(data,DIGITAL_DOMICILE.key) != null && !getValue(data,DIGITAL_DOMICILE.key).equalsIgnoreCase(EXCLUDE_VALUE)){
            notificationRecipient = notificationRecipient.digitalDomicile(getValue(data,DIGITAL_DOMICILE.key) == null? null : (new NotificationDigitalAddress()
                    .type((getValue(data,DIGITAL_DOMICILE_TYPE.key) == null?
                            null : NotificationDigitalAddress.TypeEnum.PEC ))
                    .address( getValue(data,DIGITAL_DOMICILE_ADDRESS.key)))
            );
        }

         */
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRecipient;
    }

    //documents

    public NotificationDocument newDocument(String resourcePath) {
        return new NotificationDocument().contentType(APPLICATION_PDF).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationDocument preloadDocument(NotificationDocument document) throws IOException {
        PnPaB2bUtils.Pair<String, String> preloadDocument = getB2bUtils().preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
        documentSetKey(document, preloadDocument.getValue1());
        documentSetVersionToken(document, "v1");
        documentSetDigests(document, preloadDocument.getValue2());
        return document;
    }

    private void documentSetKey(NotificationDocument notificationDocument, String key) {
        notificationDocument.getRef().setKey(key);
    }

    private void documentSetVersionToken(NotificationDocument notificationDocument, String version) {
        notificationDocument.getRef().setVersionToken(version);
    }

    private void documentSetDigests(NotificationDocument notificationDocument, String sha256) {
        notificationDocument.digests(new NotificationAttachmentDigests().sha256(sha256));
    }

    //attachments
    public NotificationPaymentAttachment newAttachment(String resourcePath) {
        return new NotificationPaymentAttachment().contentType(APPLICATION_PDF).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationPaymentAttachment preloadAttachment(NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            PnPaB2bUtils.Pair<String, String> preloadAttachment = getB2bUtils().preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED);
            attachmentSetKey(attachment, preloadAttachment.getValue1());
            attachmentSetVersionToken(attachment, "v1");
            attachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    private void attachmentSetKey(NotificationPaymentAttachment notificationPaymentAttachment, String key) {
        notificationPaymentAttachment.getRef().setKey(key);
    }

    private void attachmentSetVersionToken(NotificationPaymentAttachment notificationPaymentAttachment, String version) {
        notificationPaymentAttachment.getRef().setVersionToken(version);
    }

    private void attachmentSetDigests(NotificationPaymentAttachment notificationPaymentAttachment, String sha256) {
        notificationPaymentAttachment.digests(new NotificationAttachmentDigests().sha256(sha256));
    }
}
