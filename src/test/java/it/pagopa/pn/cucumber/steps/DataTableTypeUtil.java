package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.DataTableType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.cucumber.utils.CalculateRequestParameter;
import it.pagopa.pn.cucumber.utils.DataTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static it.pagopa.pn.cucumber.utils.NotificationValue.*;
import static java.util.Optional.ofNullable;


public class DataTableTypeUtil {

    @Autowired
    private PnPaB2bUtils utils;

    @DataTableType
    public synchronized NewNotificationRequestV23 convertNotificationRequest(Map<String, String> data){
        NewNotificationRequestV23 notificationRequest = (new NewNotificationRequestV23()
                .subject(getValue(data,SUBJECT.key))
                .cancelledIun(getValue(data,CANCELLED_IUN.key))
                .group(getValue(data,GROUP.key))
                .idempotenceToken(getValue(data,IDEMPOTENCE_TOKEN.key))
                ._abstract(getValue(data,ABSTRACT.key))
                .senderDenomination(getValue(data,SENDER_DENOMINATION.key))
                .senderTaxId(getValue(data,SENDER_TAX_ID.key))
                .paProtocolNumber(getValue(data,PA_PROTOCOL_NUMBER.key))
                .taxonomyCode(getValue(data,TAXONOMY_CODE.key))
                .amount(getValue(data, AMOUNT.key) == null ?  null : Integer.parseInt(getValue(data, AMOUNT.key)))
                .paymentExpirationDate(getValue(data, PAYMENT_EXPIRATION_DATE.key) == null ? null : getValue(data, PAYMENT_EXPIRATION_DATE.key))
                .notificationFeePolicy(
                        (getValue(data,NOTIFICATION_FEE_POLICY.key) == null? null :
                                (getValue(data,NOTIFICATION_FEE_POLICY.key).equalsIgnoreCase("FLAT_RATE")?
                                        NotificationFeePolicy.FLAT_RATE :
                                        NotificationFeePolicy.DELIVERY_MODE)))
                .physicalCommunicationType(
                        (getValue(data,PHYSICAL_COMMUNICATION_TYPE.key) == null? null :
                                (getValue(data,PHYSICAL_COMMUNICATION_TYPE.key).equalsIgnoreCase("REGISTERED_LETTER_890")?
                                        NewNotificationRequestV23.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        NewNotificationRequestV23.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
                .paFee(getValue(data, PA_FEE.key) == null ?  null : Integer.parseInt(getValue(data, PA_FEE.key)))
                .vat(getValue(data, VAT.key) == null ?  null : Integer.parseInt(getValue(data, VAT.key)))
                .pagoPaIntMode(
                        (getValue(data,PAGOPAINTMODE.key).equalsIgnoreCase("SYNC")?
                                NewNotificationRequestV23.PagoPaIntModeEnum.SYNC :
                                (getValue(data,PAGOPAINTMODE.key).equalsIgnoreCase("ASYNC")?
                                        NewNotificationRequestV23.PagoPaIntModeEnum.ASYNC:
                                        getValue(data,PAGOPAINTMODE.key).equalsIgnoreCase("NONE")?
                                                NewNotificationRequestV23.PagoPaIntModeEnum.NONE: null
                                ))));

        notificationRequest = addDocument(notificationRequest,data);

        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequest;
    }

    private NewNotificationRequestV23 addDocument(NewNotificationRequestV23 notificationRequest, Map<String, String> data) {
        String documentsToAdd = getValue(data,DOCUMENT.key);
        if( documentsToAdd == null){
            return notificationRequest.addDocumentsItem(null);
        }

        if(documentsToAdd.contains(";")){
            for(String documentElem : documentsToAdd.split(";")){
                notificationRequest = notificationRequest.addDocumentsItem(getNotificationDocument(documentElem));
            }
        }else{
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
            default ->  document = getDefaultValue(DOCUMENT.key);
        }

        return utils.newDocument(document);
    }

    @DataTableType
    public synchronized it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest convertNotificationRequestV1(Map<String, String> data){
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest notificationRequestV1 = (new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest()
                .subject(getValue(data,SUBJECT.key))
                .cancelledIun(getValue(data,CANCELLED_IUN.key))
                .group(getValue(data,GROUP.key))
                .idempotenceToken(getValue(data,IDEMPOTENCE_TOKEN.key))
                ._abstract(getValue(data,ABSTRACT.key))
                .senderDenomination(getValue(data,SENDER_DENOMINATION.key))
                .senderTaxId(getValue(data,SENDER_TAX_ID.key))
                .paProtocolNumber(getValue(data,PA_PROTOCOL_NUMBER.key))
                .taxonomyCode(getValue(data,TAXONOMY_CODE.key))
                .amount(getValue(data, AMOUNT.key) == null ?  null : Integer.parseInt(getValue(data, AMOUNT.key)))
                .paymentExpirationDate(getValue(data, PAYMENT_EXPIRATION_DATE.key) == null ? null : getValue(data, PAYMENT_EXPIRATION_DATE.key))
                .notificationFeePolicy(
                        (getValue(data,NOTIFICATION_FEE_POLICY.key) == null? null :
                                (getValue(data,NOTIFICATION_FEE_POLICY.key).equalsIgnoreCase("FLAT_RATE")?
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationFeePolicy.FLAT_RATE :
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationFeePolicy.DELIVERY_MODE)))
                .physicalCommunicationType(
                        (getValue(data,PHYSICAL_COMMUNICATION_TYPE.key) == null? null :
                                (getValue(data,PHYSICAL_COMMUNICATION_TYPE.key).equalsIgnoreCase("REGISTERED_LETTER_890")?
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
                .addDocumentsItem( getValue(data,DOCUMENT.key) == null ? null : utils.newDocumentV1(getDefaultValue(DOCUMENT.key)))
                .pagoPaIntMode(
                        (getValue(data,PAGOPAINTMODE.key) == null ? null :
                                (getValue(data,PAGOPAINTMODE.key).equalsIgnoreCase("SYNC")?
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest.PagoPaIntModeEnum.SYNC :
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest.PagoPaIntModeEnum.NONE )))
        );
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequestV1;
    }

    @DataTableType
    public synchronized it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest convertNotificationRequestV2(Map<String, String> data){
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest notificationRequestV2 = (new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest()
                .subject(getValue(data,SUBJECT.key))
                .cancelledIun(getValue(data,CANCELLED_IUN.key))
                .group(getValue(data,GROUP.key))
                .idempotenceToken(getValue(data,IDEMPOTENCE_TOKEN.key))
                ._abstract(getValue(data,ABSTRACT.key))
                .senderDenomination(getValue(data,SENDER_DENOMINATION.key))
                .senderTaxId(getValue(data,SENDER_TAX_ID.key))
                .paProtocolNumber(getValue(data,PA_PROTOCOL_NUMBER.key))
                .taxonomyCode(getValue(data,TAXONOMY_CODE.key))
                .amount(getValue(data, AMOUNT.key) == null ?  null : Integer.parseInt(getValue(data, AMOUNT.key)))
                .paymentExpirationDate(getValue(data, PAYMENT_EXPIRATION_DATE.key) == null ? null : getValue(data, PAYMENT_EXPIRATION_DATE.key))
                .notificationFeePolicy(
                        (getValue(data,NOTIFICATION_FEE_POLICY.key) == null? null :
                                (getValue(data,NOTIFICATION_FEE_POLICY.key).equalsIgnoreCase("FLAT_RATE")?
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationFeePolicy.FLAT_RATE :
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationFeePolicy.DELIVERY_MODE)))
                .physicalCommunicationType(
                        (getValue(data,PHYSICAL_COMMUNICATION_TYPE.key) == null? null :
                                (getValue(data,PHYSICAL_COMMUNICATION_TYPE.key).equalsIgnoreCase("REGISTERED_LETTER_890")?
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
                .addDocumentsItem( getValue(data,DOCUMENT.key) == null ? null : utils.newDocumentV2(getDefaultValue(DOCUMENT.key)))
                .pagoPaIntMode(
                        (getValue(data,PAGOPAINTMODE.key) == null ? null :
                                (getValue(data,PAGOPAINTMODE.key).equalsIgnoreCase("SYNC")?
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest.PagoPaIntModeEnum.SYNC :
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest.PagoPaIntModeEnum.NONE )))
        );
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequestV2;
    }

    @DataTableType
    public synchronized it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21 convertNotificationRequestV21(Map<String, String> data){
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21 notificationRequest = (new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21()
                .subject(getValue(data,SUBJECT.key))
                .cancelledIun(getValue(data,CANCELLED_IUN.key))
                .group(getValue(data,GROUP.key))
                .idempotenceToken(getValue(data,IDEMPOTENCE_TOKEN.key))
                ._abstract(getValue(data,ABSTRACT.key))
                .senderDenomination(getValue(data,SENDER_DENOMINATION.key))
                .senderTaxId(getValue(data,SENDER_TAX_ID.key))
                .paProtocolNumber(getValue(data,PA_PROTOCOL_NUMBER.key))
                .taxonomyCode(getValue(data,TAXONOMY_CODE.key))
                .amount(getValue(data, AMOUNT.key) == null ?  null : Integer.parseInt(getValue(data, AMOUNT.key)))
                .paymentExpirationDate(getValue(data, PAYMENT_EXPIRATION_DATE.key) == null ? null : getValue(data, PAYMENT_EXPIRATION_DATE.key))
                .notificationFeePolicy(
                        (getValue(data,NOTIFICATION_FEE_POLICY.key) == null? null :
                                (getValue(data,NOTIFICATION_FEE_POLICY.key).equalsIgnoreCase("FLAT_RATE")?
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationFeePolicy.FLAT_RATE :
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationFeePolicy.DELIVERY_MODE)))
                .physicalCommunicationType(
                        (getValue(data,PHYSICAL_COMMUNICATION_TYPE.key) == null? null :
                                (getValue(data,PHYSICAL_COMMUNICATION_TYPE.key).equalsIgnoreCase("REGISTERED_LETTER_890")?
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))

                .paFee(getValue(data, PA_FEE.key) == null ?  null : Integer.parseInt(getValue(data, PA_FEE.key)))
                .vat(getValue(data, VAT.key) == null ?  null : Integer.parseInt(getValue(data, VAT.key)))
                .addDocumentsItem( getValue(data,DOCUMENT.key) == null ? null : utils.newDocumentV21(getDefaultValue(DOCUMENT.key)))
                .pagoPaIntMode(
                        (getValue(data,PAGOPAINTMODE.key).equalsIgnoreCase("SYNC")?
                                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21.PagoPaIntModeEnum.SYNC :
                                (getValue(data,PAGOPAINTMODE.key).equalsIgnoreCase("ASYNC")?
                                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21.PagoPaIntModeEnum.ASYNC:null
                                ))));

        //.vat(getValue(data, VAT.key) == null ?  null : Integer.parseInt(getValue(data, VAT.key)))


        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequest;
    }


    @DataTableType
    public synchronized NotificationRecipientV23 convertNotificationRecipient(Map<String, String> data) {

        List<NotificationPaymentItem> listPayment;

        NotificationRecipientV23 notificationRecipient =  (new NotificationRecipientV23()
                .denomination(getValue(data,DENOMINATION.key))
                .taxId(getValue(data,TAX_ID.key))
                //.internalId(getValue(data,INTERNAL_ID.key))
                .digitalDomicile(getValue(data,DIGITAL_DOMICILE.key) == null ? null : (new NotificationDigitalAddress()
                        .type((getValue(data,DIGITAL_DOMICILE_TYPE.key) == null ?
                                null : NotificationDigitalAddress.TypeEnum.PEC ))
                        .address( getValue(data,DIGITAL_DOMICILE_ADDRESS.key)))
                )
                .physicalAddress(getValue(data,PHYSICAL_ADDRES.key) == null? null: new NotificationPhysicalAddress()
                        .address(getValue(data,PHYSICAL_ADDRESS_ADDRESS.key))
                        .addressDetails(getValue(data,PHYSICAL_ADDRESS_DETAILS.key))
                        .municipality(getValue(data,PHYSICAL_ADDRESS_MUNICIPALITY.key))
                        .at(getValue(data,PHYSICAL_ADDRESS_AT.key))
                        .municipalityDetails(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITYDETAILS.key))
                        .province(getValue(data,PHYSICAL_ADDRESS_PROVINCE.key))
                        .foreignState(getValue(data,PHYSICAL_ADDRESS_STATE.key))
                        .zip(getValue(data,PHYSICAL_ADDRESS_ZIP.key))
                )
                .recipientType((getValue(data,RECIPIENT_TYPE.key) == null? null :
                        (getValue(data,RECIPIENT_TYPE.key).equalsIgnoreCase("PF")?
                                NotificationRecipientV23.RecipientTypeEnum.PF :
                                NotificationRecipientV23.RecipientTypeEnum.PG)))

                //GESTIONE ISTANZE DI PAGAMENTI

        );

        //N PAGAMENTI
        if (getValue(data,PAYMENT.key)!= null  && getValue(data,PAYMENT_MULTY_NUMBER.key)!= null  && !getValue(data,PAYMENT_MULTY_NUMBER.key).isEmpty()){
            listPayment = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(getValue(data, PAYMENT_MULTY_NUMBER.key)); i++) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
                NotificationPaymentItem addPaymentsItem = new NotificationPaymentItem();
                addPaymentsItem.pagoPa(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ? null :
                        (getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NO") ?
                                null :
                                new PagoPaPayment()
                                        .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key)== null? null: getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                        .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key)== null? null: getValue(data, PAYMENT_NOTICE_CODE.key))
                                        .applyCost(getValue(data, PAYMENT_APPLY_COST_PAGOPA.key) == null ? null:
                                                getValue(data, PAYMENT_APPLY_COST_PAGOPA.key).equalsIgnoreCase("SI"))
                                        .attachment(getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NOALLEGATO") ? null : utils.newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))));

                //LOAD METADATI F24
                if (getValue(data,PAYMENT_F24.key)!= null  && !getValue(data,PAYMENT_F24.key).isEmpty()) {
                    setMetadatiF24(data, addPaymentsItem, i);

                } else if (getValue(data,PAYMENT_F24_X.key)!= null  && !getValue(data,PAYMENT_F24_X.key).isEmpty()) {
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

    private void setMetadatiF24(Map<String, String> data, NotificationPaymentItem addPaymentsItem, int i) {

        if(!Objects.equals(getValue(data, PAYMENT_F24.key), null)) {
            addPaymentsItem.f24(
                    new F24Payment()
                            .title(getValue(data, TITLE_PAYMENT.key)!=null? getValue(data, TITLE_PAYMENT.key) + "_" + i : null)
                            .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key)==null? null: getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI"))
                            .metadataAttachment(getValue(data, PAYMENT_F24.key).equalsIgnoreCase("NO_METADATA_ATTACHMENT")? null:
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

    private NotificationMetadataAttachment getNotificationMetadataAttachment(String metadataAttachment) {
        String metadati;

        switch (metadataAttachment.toUpperCase().trim()) {
            case "PAYMENT_F24_STANDARD_NO_VALID_FORMAT" -> metadati = "classpath:/METADATA_CORRETTO_NO_VALID_FORMAT.json";
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
            case "PAYMENT_F24_STANDARD_INPS_DEBIT_CREDIT" -> metadati = "classpath:/f24_delivery_standard_inps_debit_credit.json";
            case "PAYMENT_F24_STANDARD_INPS_DEBIT_CREDIT_1" -> metadati = "classpath:/f24_delivery_standard_inps_debit_credit_1.json";
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
            case "PAYMENT_F24_STANDARD_TREASURY_AE_FLAT" -> metadati = "classpath:/f24_flat_delivery_standard_treasury_ae.json";
            case "PAYMENT_F24_STANDARD_TREASURY_AE_ERR_FLAT" -> metadati = "classpath:/f24_flat_delivery_standard_treasury_ae_err.json";
            case "PAYMENT_F24_STANDARD_SOCIAL_FLAT" -> metadati = "classpath:/f24_flat_standard_social.json";
            case "PAYMENT_F24_STANDARD_INPS_ERR" -> metadati = "classpath:/f24_delivery_standard_inps_err.json";
            case "PAYMENT_F24_STANDARD_INPS_ERR_1" -> metadati = "classpath:/f24_delivery_standard_inps_err1.json";
            case "PAYMENT_F24_DELIVERY_STANDARD_LOCAL_TEFA" -> metadati = "classpath:/f24_delivery_standard_local_tefa.json";
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

            default ->  metadati = getDefaultValue(PAYMENT_F24.key);
        }

        return utils.newMetadataAttachment(metadati);
    }


    @DataTableType
    public synchronized it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient convertNotificationRecipientV1(Map<String, String> data){
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient notificationRecipient =  (new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient()
                .denomination(getValue(data,DENOMINATION.key))
                .taxId(getValue(data,TAX_ID.key))
                //.internalId(getValue(data,INTERNAL_ID.key))
                .digitalDomicile(getValue(data,DIGITAL_DOMICILE.key) == null? null : (new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDigitalAddress()
                        .type((getValue(data,DIGITAL_DOMICILE_TYPE.key) == null?
                                null : it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDigitalAddress.TypeEnum.PEC ))
                        .address( getValue(data,DIGITAL_DOMICILE_ADDRESS.key)))
                )
                .physicalAddress(getValue(data,PHYSICAL_ADDRES.key) == null? null: new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPhysicalAddress()
                        .address(getValue(data,PHYSICAL_ADDRESS_ADDRESS.key))
                        .addressDetails(getValue(data,PHYSICAL_ADDRESS_DETAILS.key))
                        .municipality(getValue(data,PHYSICAL_ADDRESS_MUNICIPALITY.key))
                        .at(getValue(data,PHYSICAL_ADDRESS_AT.key))
                        .municipalityDetails(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITYDETAILS.key))
                        .province(getValue(data,PHYSICAL_ADDRESS_PROVINCE.key))
                        .foreignState(getValue(data,PHYSICAL_ADDRESS_STATE.key))
                        .zip(getValue(data,PHYSICAL_ADDRESS_ZIP.key))
                )
                .recipientType((getValue(data,RECIPIENT_TYPE.key) == null? null :
                        (getValue(data,RECIPIENT_TYPE.key).equalsIgnoreCase("PF")?
                                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient.RecipientTypeEnum.PF :
                                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient.RecipientTypeEnum.PG)))
                .payment(getValue(data,PAYMENT.key)== null? null : new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentInfo()
                                .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                                .noticeCodeAlternative(getValue(data, PAYMENT_NOTICE_CODE_OPTIONAL.key).equalsIgnoreCase("SI")? getDefaultValue(PAYMENT_NOTICE_CODE_OPTIONAL.key) : null)

                                .pagoPaForm(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ?
                                        null : utils.newAttachmentV1(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))
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

    @DataTableType
    public synchronized it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient convertNotificationRecipientV2(Map<String, String> data){
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient notificationRecipientV2 =  (new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient()
                .denomination(getValue(data,DENOMINATION.key))
                .taxId(getValue(data,TAX_ID.key))
                //.internalId(getValue(data,INTERNAL_ID.key))
                .digitalDomicile(getValue(data,DIGITAL_DOMICILE.key) == null? null : (new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDigitalAddress()
                        .type((getValue(data,DIGITAL_DOMICILE_TYPE.key) == null?
                                null : it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDigitalAddress.TypeEnum.PEC ))
                        .address( getValue(data,DIGITAL_DOMICILE_ADDRESS.key)))
                )
                .physicalAddress(getValue(data,PHYSICAL_ADDRES.key) == null? null: new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPhysicalAddress()
                        .address(getValue(data,PHYSICAL_ADDRESS_ADDRESS.key))
                        .addressDetails(getValue(data,PHYSICAL_ADDRESS_DETAILS.key))
                        .municipality(getValue(data,PHYSICAL_ADDRESS_MUNICIPALITY.key))
                        .at(getValue(data,PHYSICAL_ADDRESS_AT.key))
                        .municipalityDetails(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITYDETAILS.key))
                        .province(getValue(data,PHYSICAL_ADDRESS_PROVINCE.key))
                        .foreignState(getValue(data,PHYSICAL_ADDRESS_STATE.key))
                        .zip(getValue(data,PHYSICAL_ADDRESS_ZIP.key))
                )
                .recipientType((getValue(data,RECIPIENT_TYPE.key) == null? null :
                        (getValue(data,RECIPIENT_TYPE.key).equalsIgnoreCase("PF")?
                                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient.RecipientTypeEnum.PF :
                                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient.RecipientTypeEnum.PG)))
                .payment(getValue(data,PAYMENT.key)== null? null : new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentInfo()
                                .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                                .noticeCodeAlternative(getValue(data, PAYMENT_NOTICE_CODE_OPTIONAL.key).equalsIgnoreCase("SI")? getDefaultValue(PAYMENT_NOTICE_CODE_OPTIONAL.key) : null)

                                .pagoPaForm(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ?
                                        null : utils.newAttachmentV2(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))
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
        return notificationRecipientV2;
    }

    @DataTableType
    public synchronized it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21 convertNotificationRecipientV21(Map<String, String> data) {

        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentItem> listPayment;

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21 notificationRecipient =  (new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21()
                .denomination(getValue(data,DENOMINATION.key))
                .taxId(getValue(data,TAX_ID.key))
                //.internalId(getValue(data,INTERNAL_ID.key))
                .digitalDomicile(getValue(data,DIGITAL_DOMICILE.key) == null? null : (new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDigitalAddress()
                        .type((getValue(data,DIGITAL_DOMICILE_TYPE.key) == null?
                                null : it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDigitalAddress.TypeEnum.PEC ))
                        .address( getValue(data,DIGITAL_DOMICILE_ADDRESS.key)))
                )
                .physicalAddress(getValue(data,PHYSICAL_ADDRES.key) == null? null: new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPhysicalAddress()
                        .address(getValue(data,PHYSICAL_ADDRESS_ADDRESS.key))
                        .addressDetails(getValue(data,PHYSICAL_ADDRESS_DETAILS.key))
                        .municipality(getValue(data,PHYSICAL_ADDRESS_MUNICIPALITY.key))
                        .at(getValue(data,PHYSICAL_ADDRESS_AT.key))
                        .municipalityDetails(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITYDETAILS.key))
                        .province(getValue(data,PHYSICAL_ADDRESS_PROVINCE.key))
                        .foreignState(getValue(data,PHYSICAL_ADDRESS_STATE.key))
                        .zip(getValue(data,PHYSICAL_ADDRESS_ZIP.key))
                )
                .recipientType((getValue(data,RECIPIENT_TYPE.key) == null? null :
                        (getValue(data,RECIPIENT_TYPE.key).equalsIgnoreCase("PF")?
                                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21.RecipientTypeEnum.PF :
                                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21.RecipientTypeEnum.PG)))

                //GESTIONE ISTANZE DI PAGAMENTI

        );

        //N PAGAMENTI
        if (getValue(data,PAYMENT.key)!= null  && getValue(data,PAYMENT_MULTY_NUMBER.key)!= null  && !getValue(data,PAYMENT_MULTY_NUMBER.key).isEmpty()){
            listPayment = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(getValue(data, PAYMENT_MULTY_NUMBER.key)); i++) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentItem addPaymentsItem = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentItem();
                addPaymentsItem.pagoPa(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ? null :
                        (getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NO") ?
                                null :
                                new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.PagoPaPayment()
                                        .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                        .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                                        .applyCost(getValue(data, PAYMENT_APPLY_COST_PAGOPA.key).equalsIgnoreCase("SI"))
                                        .attachment(getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NOALLEGATO") ? null : utils.newAttachmentV21(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))));

                //LOAD METADATI F24
                if (getValue(data,PAYMENT_F24.key)!= null && getValue(data,PAYMENT_F24.key).equalsIgnoreCase("PAYMENT_F24_FLAT")) {
                    addPaymentsItem.f24(
                            new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.F24Payment()
                                    .title(getValue(data, TITLE_PAYMENT.key) + "_" + i)
                                    .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI"))
                                    .metadataAttachment(utils.newMetadataAttachmentV21("classpath:/METADATA_CORRETTO_FLAT.json")));

                } else if (getValue(data,PAYMENT_F24.key)!= null && getValue(data,PAYMENT_F24.key).equalsIgnoreCase("PAYMENT_F24_STANDARD_0")) {
                    addPaymentsItem.f24(
                            new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.F24Payment()
                                    .title(getValue(data, TITLE_PAYMENT.key) + "_" + i)
                                    .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI"))
                                    .metadataAttachment(utils.newMetadataAttachmentV21("classpath:/METADATA_CORRETTO_0.json")));

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

    @DataTableType
    public synchronized DataTest convertTimelineElement(Map<String, String> data) throws JsonProcessingException {
        String recIndex = getValue(data, DETAILS_REC_INDEX.key);
        String sentAttemptMade = getValue(data, DETAILS_SENT_ATTEMPT_MADE.key);
        String retryNumber = getValue(data, DETAILS_RETRY_NUMBER.key);
        String responseStatus = getValue(data, DETAILS_RESPONSE_STATUS.key);
        String digitalAddressSource = getValue(data, DETAILS_DIGITAL_ADDRESS_SOURCE.key);
        String isAvailable = getValue(data, DETAILS_IS_AVAILABLE.key);
        String isFirstRetry = getValue(data, IS_FIRST_SEND_RETRY.key);
        String progressIndex = getValue(data, PROGRESS_INDEX.key);
        String analogCost = getValue(data, DETAILS_ANALOG_COST.key);
        String pollingTime = getValue(data, POLLING_TIME.key);
        String numCheck = getValue(data, NUM_CHECK.key);
        String pollingType = getValue(data, POLLING_Type.key);
        String loadTimeline = getValue(data, LOAD_TIMELINE.key);

        if (data.size() == 1 && data.get("NULL") != null) {
            return null;
        }

        DataTest dataTest = new DataTest();
        TimelineElementV23 timelineElement = new TimelineElementV23()
                .legalFactsIds(getListValue(LegalFactsId.class, data, LEGAL_FACT_IDS.key))
                .details(getValue(data, DETAILS.key) == null ? null : new TimelineElementDetailsV23()
                        .recIndex(recIndex != null ? Integer.parseInt(recIndex) : null)
                        .digitalAddress(getObjValue(DigitalAddress.class, data, DETAILS_DIGITAL_ADDRESS.key))
                        .refusalReasons(getListValue(NotificationRefusedErrorV23.class, data, DETAILS_REFUSAL_REASONS.key))
                        .generatedAarUrl(getValue(data, DETAILS_GENERATED_AAR_URL.key))
                        .responseStatus(responseStatus != null ? ResponseStatus.valueOf(responseStatus) : null)
                        .digitalAddressSource(digitalAddressSource != null ? DigitalAddressSource.valueOf(digitalAddressSource) : null)
                        .sentAttemptMade(sentAttemptMade != null ? Integer.parseInt(sentAttemptMade) : null)
                        .retryNumber(retryNumber != null ? Integer.parseInt(retryNumber) : null)
                        .sendingReceipts(getListValue(SendingReceipt.class, data, DETAILS_SENDING_RECEIPT.key))
                        .isAvailable(isAvailable != null ? Boolean.valueOf(getValue(data, DETAILS_IS_AVAILABLE.key)) : null)
                        .deliveryDetailCode(getValue(data, DETAILS_DELIVERY_DETAIL_CODE.key))
                        .deliveryFailureCause(getValue(data, DETAILS_DELIVERY_FAILURE_CAUSE.key))
                        .attachments(getListValue(AttachmentDetails.class, data, DETAILS_ATTACHMENTS.key))
                        .physicalAddress(getObjValue(PhysicalAddress.class, data, DETAILS_PHYSICALADDRESS.key))
                        .analogCost(analogCost != null ? Integer.parseInt(analogCost) : null)
                        .delegateInfo(getObjValue(DelegateInfo.class, data, DETAILS_DELEGATE_INFO.key))
                );

        // IMPORTANT: no empty data check; enrich with new checks if it is needed
        if (timelineElement.getDetails() != null || timelineElement.getLegalFactsIds() != null) {
            dataTest.setTimelineElement(timelineElement);
        }
        dataTest.setFirstSendRetry(isFirstRetry != null ? Boolean.valueOf(isFirstRetry) : null);
        dataTest.setProgressIndex(progressIndex != null ? Integer.parseInt(progressIndex) : null);
        dataTest.setPollingTime(pollingTime != null ? Integer.parseInt(pollingTime) : null);
        dataTest.setPollingType(pollingType);
        dataTest.setNumCheck(numCheck != null ? Integer.parseInt(numCheck) : null);
        dataTest.setLoadTimeline(loadTimeline != null ? Boolean.valueOf(loadTimeline) : null);

        return dataTest;
    }

    @DataTableType
    public synchronized CalculateRequestParameter convertShipmentCalculateRequetElement(Map<String, String> data) throws JsonProcessingException {
        CalculateRequestParameter requestParameter = new CalculateRequestParameter();
        requestParameter.setGeokey(getValue(data, "geokey"));
        requestParameter.setProduct(ShipmentCalculateRequest.ProductEnum.fromValue(getValue(data, "product")));
        requestParameter.setTenderId(getValue(data, "tenderId"));
        requestParameter.setNumPages(ofNullable(getValue(data, "numPages")).map(Integer::valueOf).orElse(null));
        requestParameter.setIsReversePrinter(ofNullable(getValue(data, "isReversePrinter")).map(Boolean::valueOf).orElse(null));
        requestParameter.setPageWeight(ofNullable(getValue(data, "pageWeight")).map(Integer::valueOf).orElse(null));
        return requestParameter;
    }


}