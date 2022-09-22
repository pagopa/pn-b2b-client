package it.pagopa.pn.client.b2b.pa.cucumber.test.steps;

import io.cucumber.java.DataTableType;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

import static it.pagopa.pn.client.b2b.pa.cucumber.utils.NotificationValue.*;


public class DataTableTypeUtil {

    @Autowired
    private PnPaB2bUtils utils;

    @DataTableType
    public NewNotificationRequest convertNotificationRequest(Map<String, String> data){
        return (new NewNotificationRequest()
                .subject(getValue(data,SUBJECT.key))
                .cancelledIun(getValue(data,CANCELLED_IUN.key))
                .group(getValue(data,GROUP.key))
                .idempotenceToken(getValue(data,IDEMPOTENCE_TOKEN.key))
                ._abstract(getValue(data,ABSTRACT.key))
                .senderDenomination(getValue(data,SENDER_DENOMINATION.key))
                .senderTaxId(getValue(data,SENDER_TAX_ID.key))
                .paProtocolNumber(getValue(data,PA_PROTOCOL_NUMBER.key))
                .notificationFeePolicy(
                        (getValue(data,NOTIFICATION_FEE_POLICY.key) == null? null :
                                (getValue(data,NOTIFICATION_FEE_POLICY.key).equalsIgnoreCase("FLAT_RATE")?
                                        NewNotificationRequest.NotificationFeePolicyEnum.FLAT_RATE :
                                        NewNotificationRequest.NotificationFeePolicyEnum.DELIVERY_MODE)))
                .physicalCommunicationType(
                        (getValue(data,PHYSICAL_COMMUNICATION_TYPE.key) == null? null :
                                (getValue(data,PHYSICAL_COMMUNICATION_TYPE.key).equalsIgnoreCase("REGISTERED_LETTER_890")?
                                        NewNotificationRequest.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        NewNotificationRequest.PhysicalCommunicationTypeEnum.SIMPLE_REGISTERED_LETTER)))
                .addDocumentsItem( getValue(data,DOCUMENT.key) == null ? null : utils.newDocument(getDefaultValue(DOCUMENT.key)))
        );
    }

    @DataTableType
    public NotificationRecipient convertNotificationRecipient(Map<String, String> data){
        return (new NotificationRecipient()
                .denomination(getValue(data,DENOMINATION.key))
                .taxId(getValue(data,TAX_ID.key))
                .internalId(getValue(data,INTERNAL_ID.key))
                .digitalDomicile( new NotificationDigitalAddress()
                        .type((getValue(data,DIGITAL_DOMICILE_TYPE.key) == null?
                                null : NotificationDigitalAddress.TypeEnum.PEC ))
                        .address( getValue(data,DIGITAL_DOMICILE_ADDRESS.key))
                )
                .physicalAddress(getValue(data,PHYSICAL_ADDRES.key) == null? null: new NotificationPhysicalAddress()
                        .address(getValue(data,PHYSICAL_ADDRESS_ADDRESS.key))
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
                        .noticeCodeOptional(getValue(data, PAYMENT_NOTICE_CODE_OPTIONAL.key))

                        .pagoPaForm(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ?
                                null : utils.newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))

                        .f24flatRate(getValue(data, PAYMENT_F24_FLAT.key) == null ? null :
                                (getValue(data, PAYMENT_F24_FLAT.key).equalsIgnoreCase("SI")?
                                        utils.newAttachment(getDefaultValue(PAYMENT_F24_FLAT.key)):null))

                        .f24standard(getValue(data, PAYMENT_F24_STANDARD.key) == null ? null :
                                (getValue(data, PAYMENT_F24_STANDARD.key).equalsIgnoreCase("SI")?
                                        utils.newAttachment(getDefaultValue(PAYMENT_F24_STANDARD.key)):null))
                )
        );
    }
}
