package it.pagopa.pn.cucumber.steps.pa.utilityVersions;

import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationStepsV24;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static it.pagopa.pn.client.b2b.pa.PnPaB2bUtils.*;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

@Component
public class NotificationUtilsV24 extends AbstractNotificationUtils {

    private NotificationStepsV24 notificationStep;

    public NotificationUtilsV24(NotificationStepsV24 notificationStep) {
        this.notificationStep = notificationStep;
    }

    @Override
    public PnPaB2bUtils getB2bUtils() {
        return notificationStep.getSharedSteps().getB2bUtils();
    }

    public synchronized NewNotificationRequestV24 convertNotificationRequest(Map<String, String> data) {
        NewNotificationRequestV24 notificationRequest = (new NewNotificationRequestV24()
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
                .paymentExpirationDate(getValue(data, PAYMENT_EXPIRATION_DATE.key) == null ?
                        null : getValue(data, PAYMENT_EXPIRATION_DATE.key))
                .notificationFeePolicy((getValue(data, NOTIFICATION_FEE_POLICY.key) == null ?
                        null : (getValue(data, NOTIFICATION_FEE_POLICY.key).equalsIgnoreCase("FLAT_RATE") ?
                        NotificationFeePolicy.FLAT_RATE :
                        NotificationFeePolicy.DELIVERY_MODE)))
                .physicalCommunicationType((getValue(data, PHYSICAL_COMMUNICATION_TYPE.key) == null ?
                        null : (getValue(data, PHYSICAL_COMMUNICATION_TYPE.key).equalsIgnoreCase("REGISTERED_LETTER_890") ?
                        NewNotificationRequestV24.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                        NewNotificationRequestV24.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
                .paFee(getValue(data, PA_FEE.key) == null ? null : Integer.parseInt(getValue(data, PA_FEE.key)))
                .vat(getValue(data, VAT.key) == null ? null : Integer.parseInt(getValue(data, VAT.key)))
                .additionalLanguages(getValue(data, ADDITIONAL_LANGUAGES.key) == null ?
                        null : List.of(getValue(data, ADDITIONAL_LANGUAGES.key)))
                .pagoPaIntMode((getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("SYNC") ?
                        NewNotificationRequestV24.PagoPaIntModeEnum.SYNC :
                        (getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("ASYNC") ?
                                NewNotificationRequestV24.PagoPaIntModeEnum.ASYNC :
                                getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("NONE") ?
                                        NewNotificationRequestV24.PagoPaIntModeEnum.NONE : null))));

        notificationRequest = addDocument(notificationRequest, data);
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequest;
    }

    public synchronized NotificationRecipientV23 convertNotificationRecipient(Map<String, String> data) {
        List<NotificationPaymentItem> listPayment;

        NotificationRecipientV23 notificationRecipient = (new NotificationRecipientV23()
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
                                NotificationRecipientV23.RecipientTypeEnum.PF : NotificationRecipientV23.RecipientTypeEnum.PG)))
                //GESTIONE ISTANZE DI PAGAMENTI
        );
        //N PAGAMENTI
        if (getValue(data, PAYMENT.key) != null && getValue(data, PAYMENT_MULTY_NUMBER.key) != null && !getValue(data, PAYMENT_MULTY_NUMBER.key).isEmpty()) {
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
                                        .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key) == null ? null : getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                        .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key) == null ? null : getValue(data, PAYMENT_NOTICE_CODE.key))
                                        .applyCost(getValue(data, PAYMENT_APPLY_COST_PAGOPA.key) == null ? null :
                                                getValue(data, PAYMENT_APPLY_COST_PAGOPA.key).equalsIgnoreCase("SI"))
                                        .attachment(getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NOALLEGATO") ?
                                                null : newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))));

                //LOAD METADATI F24
                if (getValue(data, PAYMENT_F24.key) != null && !getValue(data, PAYMENT_F24.key).isEmpty()) {
                    setMetadatiF24(data, addPaymentsItem, i);

                } else if (getValue(data, PAYMENT_F24_X.key) != null && !getValue(data, PAYMENT_F24_X.key).isEmpty()) {
                    setMetadatiF24(data, addPaymentsItem, i);
                }

                listPayment.add(addPaymentsItem);
            }
            notificationRecipient.setPayments(listPayment);
        }
        /*
            if (getValue(data,PAYMENT.key)!= null && (listPayment==null || (listPayment!= null && listPayment.isEmpty()))){
                listPayment = new ArrayList<NotificationPaymentItem>();
                NotificationPaymentItem addPaymentsItem = new NotificationPaymentItem();
                addPaymentsItem.pagoPa(
                        new PagoPaPayment()
                                .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                                .applyCost(getValue(data, PAYMENT_APPLY_COST_PAGOPA.key).equalsIgnoreCase("SI") ? true : false)
                                .attachment(utils.newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key))));

                                 addPaymentsItem.f24(getValue(data, PAYMENT_F24_STANDARD.key) == null ? null :
                                 (getValue(data, PAYMENT_F24_STANDARD.key).equalsIgnoreCase("SI") ?
                                 new F24Payment()
                                 .title(getValue(data, TITLE_PAYMENT.key))
                                 .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI") ? true : false)
                                 .metadataAttachment(utils.newMetadataAttachment(getDefaultValue(PAYMENT_F24_STANDARD.key))) : null));



                listPayment.add(addPaymentsItem);
            }

         */


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

    private NewNotificationRequestV24 addDocument(NewNotificationRequestV24 notificationRequest, Map<String, String> data) {
        String documentsToAdd = getValue(data, DOCUMENT.key);
        if (documentsToAdd == null) {
            return notificationRequest.addDocumentsItem(null);
        }
        if (documentsToAdd.contains(";")) {
            for (String documentElem : documentsToAdd.split(";")) {
                notificationRequest = notificationRequest.addDocumentsItem(getNotificationDocument(documentElem));
            }
        } else {
            notificationRequest = notificationRequest.addDocumentsItem(getNotificationDocument(documentsToAdd));
        }
        return notificationRequest;
    }

    private NotificationDocument getNotificationDocument(String documentElem) {
        String document;

        switch (documentElem.toUpperCase().trim()) {
            case "DOC_1_PG" -> document = "classpath:/sample_1pg.pdf";
            case "DOC_2_PG" -> document = "classpath:/sample_2pg.pdf";
            case "DOC_3_PG" -> document = "classpath:/sample_3pg.pdf";
            case "DOC_4_PG" -> document = "classpath:/sample_4pg.pdf";
            case "DOC_5_PG" -> document = "classpath:/sample_5pg.pdf";
            case "DOC_6_PG" -> document = "classpath:/sample_6pg.pdf";
            case "DOC_7_PG" -> document = "classpath:/sample_7pg.pdf";
            case "DOC_8_PG" -> document = "classpath:/sample_8pg.pdf";
            case "DOC_50_PG" -> document = "classpath:/sample_50pg.pdf";
            case "DOC_100_PG" -> document = "classpath:/sample_100pg.pdf";
            case "DOC_300_PG" -> document = "classpath:/sample_300pg.pdf";
            case "DOC_BS" -> document = "classpath:/verbaleBs.pdf";
            case "DOC_BS2" -> document = "classpath:/Atto2BsRadd.pdf";
            case "DOC_30MB" -> document = "classpath:/allegato_30Mb.pdf";
            case "ALLEGATO_1_BN" -> document = "classpath:/Allegato1_BN.pdf";
            case "ALLEGATO_2_BN" -> document = "classpath:/Allegato2_BN.pdf";
            case "ALLEGATO_3_COLORI" -> document = "classpath:/Allegato3_COLORI.PDF";
            case "ALLEGATO_4_COLORI" -> document = "classpath:/Allegato4_COLORI.pdf";
            default -> document = getDefaultValue(DOCUMENT.key);
        }
        return newDocument(document);
    }

    public NotificationDocument preloadDocument(NotificationDocument document) throws IOException {
        PnPaB2bUtils.Pair<String, String> preloadDocument = getB2bUtils().preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
        documentSetKey(document, preloadDocument.getValue1());
        documentSetVersionToken(document, "v1");
        documentSetDigests(document, preloadDocument.getValue2());
        return document;
    }

    public void documentSetKey(NotificationDocument notificationDocument, String key) {
        notificationDocument.getRef().setKey(key);
    }

    public void documentSetVersionToken(NotificationDocument notificationDocument, String version) {
        notificationDocument.getRef().setVersionToken(version);
    }

    public void documentSetDigests(NotificationDocument notificationDocument, String sha256) {
        notificationDocument.digests(new NotificationAttachmentDigests().sha256(sha256));
    }

    //attachments
    public NotificationPaymentAttachment newAttachment(String resourcePath) {
        return new NotificationPaymentAttachment().contentType(APPLICATION_PDF).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    private NotificationPaymentAttachment preloadAttachment(NotificationPaymentAttachment attachment) throws IOException {
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

    //metadataAttachments
    public NotificationMetadataAttachment newMetadataAttachment(String resourcePath) {
        return new NotificationMetadataAttachment().contentType(APPLICATION_JSON).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    private NotificationMetadataAttachment getNotificationMetadataAttachment(String metadataAttachment) {
        String metadati;

        switch (metadataAttachment.toUpperCase().trim()) {
            case "PAYMENT_F24_STANDARD_NO_VALID_FORMAT" ->
                    metadati = "classpath:/METADATA_CORRETTO_NO_VALID_FORMAT.json";
            case "PAYMENT_F24_STANDARD_NO_VALID_LENGH" -> metadati = "classpath:/METADATA_CORRETTO_NO_VALID_LENGH.json";
            case "PAYMENT_F24_STANDARD_VALID_ANAG" -> metadati = "classpath:/METADATA_CORRETTO_VALID_103.json";
            case "PAYMENT_F24_STANDARD_NO_VALID_ANAG" -> metadati = "classpath:/METADATA_CORRETTO_VALID_104.json";
            case "PAYMENT_F24_STANDARD_NO_VALID_ANAG_1" -> metadati = "classpath:/METADATA_CORRETTO_VALID_105.json";
            case "PAYMENT_F24_STANDARD_NO_VALID_ANAG_2" -> metadati = "classpath:/METADATA_CORRETTO_VALID_106.json";
            case "PAYMENT_F24_STANDARD_NO_VALID_ANAG_3" -> metadati = "classpath:/METADATA_CORRETTO_VALID_107.json";
            case "PAYMENT_F24_STANDARD_NO_VALID_ANAG_4" -> metadati = "classpath:/METADATA_CORRETTO_VALID_108.json";
            case "PAYMENT_F24_STANDARD_NO_VALID_ANAG_5" -> metadati = "classpath:/test_invalid_metadata.json";
            case "PAYMENT_F24_STANDARD" -> metadati = "classpath:/METADATA_CORRETTO.json";
            case "PAYMENT_F24_STANDARD_PG" -> metadati = "classpath:/METADATA_CORRETTO_PG.json";
            case "PAYMENT_F24_STANDARD_EXCISE_PG" -> metadati = "classpath:/test_excise_pg.json";
            case "PAYMENT_F24_FLAT" -> metadati = "classpath:/METADATA_CORRETTO_FLAT.json";
            case "PAYMENT_F24_SIMPLIFIED" -> metadati = "classpath:/f24_delivery_simplified.json";
            case "PAYMENT_F24_SIMPLIFIED_PG" -> metadati = "classpath:/test_simplified_pg.json";
            case "PAYMENT_F24_SIMPLIFIED_1" -> metadati = "classpath:/f24filenuovo.json";
            case "PAYMENT_F24_SIMPLIFIED_ERR1" -> metadati = "classpath:/test01.json";
            case "PAYMENT_F24_SIMPLIFIED_ERR2" -> metadati = "classpath:/test02.json";
            case "PAYMENT_F24_SIMPLIFIED_ERR3" -> metadati = "classpath:/test03.json";
            case "PAYMENT_F24_STANDARD_INPS" -> metadati = "classpath:/f24_delivery_standard_inps.json";
            case "PAYMENT_F24_STANDARD_INPS_DEBIT_CREDIT" ->
                    metadati = "classpath:/f24_delivery_standard_inps_debit_credit.json";
            case "PAYMENT_F24_STANDARD_INPS_DEBIT_CREDIT_1" ->
                    metadati = "classpath:/f24_delivery_standard_inps_debit_credit_1.json";
            case "PAYMENT_F24_STANDARD_REGION" -> metadati = "classpath:/f24_delivery_standard_region.json";
            case "PAYMENT_F24_STANDARD_LOCAL" -> metadati = "classpath:/f24_delivery_standard_local.json";
            case "PAYMENT_F24_STANDARD_TREASURY" -> metadati = "classpath:/f24_delivery_standard_treasury.json";
            case "PAYMENT_F24_STANDARD_TREASURY_AE" -> metadati = "classpath:/f24_delivery_standard_treasury_ae.json";
            case "PAYMENT_F24_STANDARD_SOCIAL" -> metadati = "classpath:/f24_delivery_standard_social.json";
            case "PAYMENT_F24_SIMPLIFIED_FLAT" -> metadati = "classpath:/f24_flat_simplified.json";
            case "PAYMENT_F24_STANDARD_INPS_FLAT" -> metadati = "classpath:/f24_flat_standard_inps.json";
            case "PAYMENT_F24_STANDARD_REGION_FLAT" -> metadati = "classpath:/f24_flat_standard_region.json";
            case "PAYMENT_F24_STANDARD_LOCAL_FLAT" -> metadati = "classpath:/f24_flat_standard_local.json";
            case "PAYMENT_F24_STANDARD_TREASURY_FLAT" -> metadati = "classpath:/f24_flat_standard_treasury.json";
            case "PAYMENT_F24_STANDARD_TREASURY_AE_FLAT" ->
                    metadati = "classpath:/f24_flat_delivery_standard_treasury_ae.json";
            case "PAYMENT_F24_STANDARD_TREASURY_AE_ERR_FLAT" ->
                    metadati = "classpath:/f24_flat_delivery_standard_treasury_ae_err.json";
            case "PAYMENT_F24_STANDARD_SOCIAL_FLAT" -> metadati = "classpath:/f24_flat_standard_social.json";
            case "PAYMENT_F24_STANDARD_INPS_ERR" -> metadati = "classpath:/f24_delivery_standard_inps_err.json";
            case "PAYMENT_F24_STANDARD_INPS_ERR_1" -> metadati = "classpath:/f24_delivery_standard_inps_err1.json";
            case "PAYMENT_F24_DELIVERY_STANDARD_LOCAL_TEFA" ->
                    metadati = "classpath:/f24_delivery_standard_local_tefa.json";
            case "METADATO_CORRETTO_STAND_MINIMAL" -> metadati = "classpath:/METADATO_CORRETTO_STAND_MINIMAL.json";
            case "METADATO_CORRETTO_SIMPL_MINIMAL" -> metadati = "classpath:/METADATO_CORRETTO_SIMPL_MINIMAL.json";
            case "METADATO_CORRETTO_EXCISE_MINIMAL" -> metadati = "classpath:/METADATO_CORRETTO_EXCISE_MINIMAL.json";
            case "METADATO_CORRETTO_ELID_MINIMAL" -> metadati = "classpath:/METADATO_CORRETTO_ELID_MINIMAL.json";
            case "PAYMENT_F24_STANDARD_0" -> metadati = "classpath:/METADATA_CORRETTO_0.json";
            case "PAYMENT_F24_STANDARD_1" -> metadati = "classpath:/METADATA_CORRETTO_1.json";
            case "PAYMENT_F24_STANDARD_2" -> metadati = "classpath:/METADATA_CORRETTO_2.json";
            case "PAYMENT_F24_FLAT_0" -> metadati = "classpath:/METADATA_CORRETTO_FLAT_0.json";
            case "PAYMENT_F24_FLAT_1" -> metadati = "classpath:/METADATA_CORRETTO_FLAT_1.json";
            case "PAYMENT_F24_FLAT_2" -> metadati = "classpath:/METADATA_CORRETTO_FLAT_2.json";
            case "PAYMENT_F24_SIMPLIFIED_VALIDATION_OFF_1" ->
                    metadati = "classpath:/f24_delivery_simplified_validation_off_1.json";
            case "PAYMENT_F24_SIMPLIFIED_VALIDATION_OFF_2" ->
                    metadati = "classpath:/f24_delivery_simplified_validation_off_2.json";

            default -> metadati = getDefaultValue(PAYMENT_F24.key);
        }

        return newMetadataAttachment(metadati);
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
    public void preloadPayDocument(NewNotificationRequestV24 request) throws IOException {
        for (NotificationRecipientV23 recipient : request.getRecipients()) {
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

    private void setMetadatiF24(Map<String, String> data, NotificationPaymentItem addPaymentsItem, int i) {

        if (!Objects.equals(getValue(data, PAYMENT_F24.key), null)) {
            addPaymentsItem.f24(
                    new F24Payment()
                            .title(getValue(data, TITLE_PAYMENT.key) != null ? getValue(data, TITLE_PAYMENT.key) + "_" + i : null)
                            .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key) == null ? null : getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI"))
                            .metadataAttachment(getValue(data, PAYMENT_F24.key).equalsIgnoreCase("NO_METADATA_ATTACHMENT") ? null :
                                    getNotificationMetadataAttachment(getValue(data, PAYMENT_F24.key))));

        } else if (!Objects.equals(getValue(data, PAYMENT_F24_X.key), null)) {
            boolean applyCost = i != 2 || !getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI");
            if (getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("NO")) {
                applyCost = false;
            }
            addPaymentsItem.f24(
                    new F24Payment()
                            .title(getValue(data, TITLE_PAYMENT.key) + "_" + i)
                            .applyCost(applyCost)
                            .metadataAttachment(getNotificationMetadataAttachment(getValue(data, PAYMENT_F24_X.key) + "_" + i)));
        }

    }
}
