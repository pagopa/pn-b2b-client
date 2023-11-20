package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.DataTableType;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.cucumber.utils.DataTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static it.pagopa.pn.cucumber.utils.NotificationValue.*;


public class DataTableTypeUtil {

    @Autowired
    private PnPaB2bUtils utils;

    @DataTableType
    public synchronized NewNotificationRequest convertNotificationRequest(Map<String, String> data){
        NewNotificationRequest notificationRequest = (new NewNotificationRequest()
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
                                        NewNotificationRequest.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        NewNotificationRequest.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
                .addDocumentsItem( getValue(data,DOCUMENT.key) == null ? null : utils.newDocument(getDefaultValue(DOCUMENT.key)))
        );
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequest;
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
    public synchronized NotificationRecipient convertNotificationRecipient(Map<String, String> data){
        NotificationRecipient notificationRecipient =  (new NotificationRecipient()
                .denomination(getValue(data,DENOMINATION.key))
                .taxId(getValue(data,TAX_ID.key))
                //.internalId(getValue(data,INTERNAL_ID.key))
                .digitalDomicile(getValue(data,DIGITAL_DOMICILE.key) == null? null : (new NotificationDigitalAddress()
                .type((getValue(data,DIGITAL_DOMICILE_TYPE.key) == null?
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
                                NotificationRecipient.RecipientTypeEnum.PF :
                                NotificationRecipient.RecipientTypeEnum.PG)))
                .payment(getValue(data,PAYMENT.key)== null? null : new NotificationPaymentInfo()
                        .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                        .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                        //.noticeCodeAlternative(getValue(data, PAYMENT_NOTICE_CODE_OPTIONAL.key))

                        .pagoPaForm(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ?
                                null : utils.newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))
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
                                //.noticeCodeAlternative(getValue(data, PAYMENT_NOTICE_CODE_OPTIONAL.key))

                                .pagoPaForm(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ?
                                        null : utils.newAttachmentV1(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))
                )
        );

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
        String loadTimeline = getValue(data, LOAD_TIMELINE.key);

        if (data.size() == 1 && data.get("NULL") != null) {
            return null;
        }

        DataTest dataTest = new DataTest();
        TimelineElementV20 timelineElement = new TimelineElementV20()
                .legalFactsIds(getListValue(LegalFactsId.class, data, LEGAL_FACT_IDS.key))
                .details(getValue(data, DETAILS.key) == null ? null : new TimelineElementDetailsV20()
                        .recIndex(recIndex != null ? Integer.parseInt(recIndex) : null)
                        .digitalAddress(getObjValue(DigitalAddress.class, data, DETAILS_DIGITAL_ADDRESS.key))
                        .refusalReasons(getListValue(NotificationRefusedError.class, data, DETAILS_REFUSAL_REASONS.key))
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
        dataTest.setNumCheck(numCheck != null ? Integer.parseInt(numCheck) : null);
        dataTest.setLoadTimeline(loadTimeline != null ? Boolean.valueOf(loadTimeline) : null);

        return dataTest;
    }


}
