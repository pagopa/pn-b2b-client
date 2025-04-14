package it.pagopa.pn.cucumber.steps.pa.utilityVersions;

import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.*;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationStepsV21;
import it.pagopa.pn.cucumber.utils.NotificationValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.pagopa.pn.client.b2b.pa.PnPaB2bUtils.*;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

public class NotificationUtilsV21 extends AbstractNotificationUtils {

    private NotificationStepsV21 notificationStep;

    public NotificationUtilsV21(NotificationStepsV21 notificationStep) {
        this.notificationStep = notificationStep;
    }

    @Override
    public PnPaB2bUtils getB2bUtils() {
        return notificationStep.getSharedSteps().getB2bUtils();
    }

    public synchronized NewNotificationRequestV21 convertNotificationRequest(Map<String, String> data) {
        NewNotificationRequestV21 notificationRequest = (new NewNotificationRequestV21()
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
                                        NewNotificationRequestV21.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        NewNotificationRequestV21.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
                .paFee(getValue(data, PA_FEE.key) == null ? null : Integer.parseInt(getValue(data, PA_FEE.key)))
                .vat(getValue(data, VAT.key) == null ? null : Integer.parseInt(getValue(data, VAT.key)))
                .addDocumentsItem(getValue(data, DOCUMENT.key) == null ? null : newDocument(getDefaultValue(DOCUMENT.key)))
                .pagoPaIntMode(
                        (getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("SYNC") ?
                                NewNotificationRequestV21.PagoPaIntModeEnum.SYNC :
                                (getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("ASYNC") ?
                                        NewNotificationRequestV21.PagoPaIntModeEnum.ASYNC : null))));
        //.vat(getValue(data, VAT.key) == null ?  null : Integer.parseInt(getValue(data, VAT.key)))
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequest;
    }

    public synchronized NotificationRecipientV21 convertNotificationRecipient(Map<String, String> data) {
        List<NotificationPaymentItem> listPayment;

        NotificationRecipientV21 notificationRecipient = (new NotificationRecipientV21()
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
                                NotificationRecipientV21.RecipientTypeEnum.PF : NotificationRecipientV21.RecipientTypeEnum.PG)))
                //GESTIONE ISTANZE DI PAGAMENTI
        );
        //N PAGAMENTI
        if (getValue(data, NotificationValue.PAYMENT.key) != null && getValue(data, PAYMENT_MULTY_NUMBER.key) != null && !getValue(data, PAYMENT_MULTY_NUMBER.key).isEmpty()) {
            listPayment = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(getValue(data, PAYMENT_MULTY_NUMBER.key)); i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
                NotificationPaymentItem addPaymentsItem = new NotificationPaymentItem();
                addPaymentsItem.pagoPa(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ? null :
                        (getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NO") ?
                                null :
                                new PagoPaPayment()
                                        .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                        .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                                        .applyCost(getValue(data, PAYMENT_APPLY_COST_PAGOPA.key).equalsIgnoreCase("SI"))
                                        .attachment(getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NOALLEGATO") ?
                                                null : newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))));

                //LOAD METADATI F24
                if (getValue(data, PAYMENT_F24.key) != null && getValue(data, PAYMENT_F24.key).equalsIgnoreCase("PAYMENT_F24_FLAT")) {
                    addPaymentsItem.f24(
                            new F24Payment()
                                    .title(getValue(data, TITLE_PAYMENT.key) + "_" + i)
                                    .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI"))
                                    .metadataAttachment(newMetadataAttachment("classpath:/METADATA_CORRETTO_FLAT.json")));

                } else if (getValue(data, PAYMENT_F24.key) != null && getValue(data, PAYMENT_F24.key).equalsIgnoreCase("PAYMENT_F24_STANDARD_0")) {
                    addPaymentsItem.f24(
                            new F24Payment()
                                    .title(getValue(data, TITLE_PAYMENT.key) + "_" + i)
                                    .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI"))
                                    .metadataAttachment(newMetadataAttachment("classpath:/METADATA_CORRETTO_0.json")));

                }
                listPayment.add(addPaymentsItem);
            }
            notificationRecipient.setPayments(listPayment);
        }

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
        Pair<String, String> preloadDocument = getB2bUtils().preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
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
            Pair<String, String> preloadAttachment = getB2bUtils().preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED);
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

    //metadataAttachments
    public NotificationMetadataAttachment newMetadataAttachment(String resourcePath) {
        return new NotificationMetadataAttachment().contentType(APPLICATION_JSON).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationMetadataAttachment preloadMetadataAttachment(NotificationMetadataAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = getB2bUtils().preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED_METADATI);
            metadataAttachmentSetKey(attachment, preloadAttachment.getValue1());
            metadataAttachmentSetVersionToken(attachment, "v1");
            metadataAttachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    private void metadataAttachmentSetKey(NotificationMetadataAttachment notificationMetadataAttachment, String key) {
        notificationMetadataAttachment.getRef().setKey(key);
    }

    private void metadataAttachmentSetVersionToken(NotificationMetadataAttachment notificationMetadataAttachment, String version) {
        notificationMetadataAttachment.getRef().setVersionToken(version);
    }

    private void metadataAttachmentSetDigests(NotificationMetadataAttachment notificationMetadataAttachment, String sha256) {
        notificationMetadataAttachment.digests(new NotificationAttachmentDigests().sha256(sha256));
    }

    //payDocument
    public void preloadPayDocument(NewNotificationRequestV21 request) throws IOException {
        for (NotificationRecipientV21 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if (paymentList != null) {
                setAttachmentWithSleep(paymentList);
            }
        }
    }

    private void setAttachmentWithSleep(List<NotificationPaymentItem> paymentList) throws IOException {
        for (NotificationPaymentItem paymentInfo : paymentList) {
            try {
                Thread.sleep(getB2bUtils().getRandom().nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }
            if (paymentInfo.getPagoPa() != null) {
                paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
            }
            if (paymentInfo.getF24() != null) {
                paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
            }
        }
    }
}
