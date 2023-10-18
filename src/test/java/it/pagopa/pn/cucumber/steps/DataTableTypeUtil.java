package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.DataTableType;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.cucumber.utils.DataTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static it.pagopa.pn.cucumber.utils.NotificationValue.*;


public class DataTableTypeUtil {

    @Autowired
    private PnPaB2bUtils utils;

    @DataTableType
    public synchronized NewNotificationRequestV21 convertNotificationRequest(Map<String, String> data){
        NewNotificationRequestV21 notificationRequest = (new NewNotificationRequestV21()
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
                                        NewNotificationRequestV21.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        NewNotificationRequestV21.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
                .addDocumentsItem( getValue(data,DOCUMENT.key) == null ? null : utils.newDocument(getDefaultValue(DOCUMENT.key)))

                .paFee(getValue(data, PA_FEE.key) == null ?  null : Integer.parseInt(getValue(data, PA_FEE.key)))

        );
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequest;
    }

    @DataTableType
    public synchronized NotificationRecipientV21 convertNotificationRecipient(Map<String, String> data) {

        List<NotificationPaymentItem> listPayment = new ArrayList<NotificationPaymentItem>();;

        NotificationRecipientV21 notificationRecipient =  (new NotificationRecipientV21()
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
                                NotificationRecipientV21.RecipientTypeEnum.PF :
                                NotificationRecipientV21.RecipientTypeEnum.PG)))

                //GESTIONE ISTANZE DI PAGAMENTI

        );

        //N PAGAMENTI
        if (getValue(data,PAYMENT.key)!= null && getValue(data,PAYMENT_MULTY_NUMBER.key)!= null && !getValue(data,PAYMENT_MULTY_NUMBER.key).isEmpty()){
                listPayment = new ArrayList<NotificationPaymentItem>();
                for (int i = 0; i < Integer.parseInt(getValue(data, PAYMENT_MULTY_NUMBER.key)); i++) {
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException exc) {
                        throw new RuntimeException(exc);
                    }
                    NotificationPaymentItem addPaymentsItem = new NotificationPaymentItem();
                    addPaymentsItem.pagoPa(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ? null :
                            (getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("SI") ?
                                    new PagoPaPayment()
                                            .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                            .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                                            .applyCost(getValue(data, PAYMENT_APPLY_COST_PAGOPA.key).equalsIgnoreCase("SI") ? true : false)
                                            .attachment(utils.newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key))) : null));

                    addPaymentsItem.f24(getValue(data, PAYMENT_F24_STANDARD.key) == null ? null :
                            (getValue(data, PAYMENT_F24_STANDARD.key).equalsIgnoreCase("SI") ?
                                    new F24Payment()
                                            .title(getValue(data, TITLE_PAYMENT.key))
                                            .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI") ? true : false)
                                            .metadataAttachment(utils.newMetadataAttachment(getDefaultValue(PAYMENT_F24_STANDARD.key))) : null));
                    if (getValue(data, PAYMENT_F24_STANDARD.key) == null) {
                        addPaymentsItem.f24(getValue(data, PAYMENT_F24_FLAT.key) == null ? null :
                                (getValue(data, PAYMENT_F24_FLAT.key).equalsIgnoreCase("SI") ?
                                        new F24Payment()
                                                .title(getValue(data, TITLE_PAYMENT.key))
                                                .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI") ? true : false)
                                                .metadataAttachment(utils.newMetadataAttachment(getDefaultValue(PAYMENT_F24_FLAT.key))) : null));
                    }

                    listPayment.add(addPaymentsItem);
                }

        }
            if (getValue(data,PAYMENT.key)!= null && (listPayment==null || (listPayment!= null && listPayment.isEmpty()))){
                listPayment = new ArrayList<NotificationPaymentItem>();
                NotificationPaymentItem addPaymentsItem = new NotificationPaymentItem();
                addPaymentsItem.pagoPa(
                        new PagoPaPayment()
                                .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                                .applyCost(false)
                                .attachment(utils.newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key))));
/**
 addPaymentsItem.f24(getValue(data, PAYMENT_F24_STANDARD.key) == null ? null :
 (getValue(data, PAYMENT_F24_STANDARD.key).equalsIgnoreCase("SI") ?
 new F24Payment()
 .title(getValue(data, TITLE_PAYMENT.key))
 .applyCost(false)
 .metadataAttachment(utils.newMetadataAttachment(getDefaultValue(PAYMENT_F24_STANDARD.key))) : null));
 **/

                listPayment.add(addPaymentsItem);
            }



        notificationRecipient.setPayments(listPayment);

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
                        .refusalReasons(getListValue(NotificationRefusedErrorV20.class, data, DETAILS_REFUSAL_REASONS.key))
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
