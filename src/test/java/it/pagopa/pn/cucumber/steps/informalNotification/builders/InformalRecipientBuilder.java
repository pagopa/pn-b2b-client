package it.pagopa.pn.cucumber.steps.informalNotification.builders;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.*;
import it.pagopa.pn.cucumber.steps.informalNotification.mapper.InformalNotificationRequestMapper;
import it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue;
import it.pagopa.pn.cucumber.steps.informalNotification.provider.InformalMessageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.*;

@Component
@RequiredArgsConstructor
public class InformalRecipientBuilder {

    private final InformalNotificationRequestMapper mapper;
    private final InformalMessageProvider messageProvider;

    public InformalNotificationRecipientV1 build(Map<String, String> data, String currentCxId) {

        InformalNotificationRecipientV1 recipient = new InformalNotificationRecipientV1();

        // =========================
        // BASE
        // =========================
        String recipientType = getValue(data, RECIPIENT_TYPE.key);
        if (recipientType != null) {
            recipient.setRecipientType(InformalNotificationRecipientV1.RecipientTypeEnum.fromValue(recipientType));
        }
        recipient.setMessageId(resolveMessageId(data, currentCxId));
        recipient.setTaxId(getValue(data, RECIPIENT_TAX_ID.key));
        recipient.setDenomination(getValue(data, RECIPIENT_DENOMINATION.key));

        // =========================
        // DIGITAL
        // =========================
        String digitalDomicile = getValue(data, DIGITAL_DOMICILE.key);

        if (digitalDomicile != null) {
            recipient.setDigitalDomicile(new NotificationDigitalAddress().type(NotificationDigitalAddress.TypeEnum.PEC).address(digitalDomicile));
        } else {
            recipient.setDigitalDomicile(null);
        }

        // =========================
        // CONTACTS
        // =========================
        recipient.setPhoneNumber(getValue(data, PHONE_NUMBER.key));
        recipient.setEmail(getValue(data, EMAIL.key));

        // =========================
        // PHYSICAL ADDRESS
        // =========================
        recipient.setPhysicalAddress(buildPhysicalAddress(data));

        // =========================
        // PAYMENTS
        // =========================
        recipient.setPayments(buildPayments(data));
        return recipient;
    }

    // =========================================================
    // PHYSICAL ADDRESS
    // =========================================================
    private NotificationPhysicalAddress buildPhysicalAddress(Map<String, String> data) {

        String physicalAddressValue = data.get("physical_address");

        boolean isNull = "${PHYSICAL_ADDRESS_NULL}".equalsIgnoreCase(physicalAddressValue);

        if (isNull) {
            return null;
        }
        NotificationPhysicalAddress pa = new NotificationPhysicalAddress();

        pa.setAddress(getValue(data, PHYSICAL_ADDRESS_ADDRESS.key));
        pa.setAddressDetails(getValue(data, PHYSICAL_ADDRESS_DETAILS.key));
        pa.setZip(getValue(data, PHYSICAL_ADDRESS_ZIP.key));
        pa.setMunicipality(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITY.key));
        pa.setProvince(getValue(data, PHYSICAL_ADDRESS_PROVINCE.key));
        pa.setForeignState(getValue(data, PHYSICAL_ADDRESS_STATE.key));
        pa.setAt(getValue(data, PHYSICAL_ADDRESS_AT.key));

        return pa;
    }

    // =========================================================
    // PAYMENTS
    // =========================================================
    private List<InformalNotificationPaymentItem> buildPayments(Map<String, String> data) {

        int paymentNumber = Integer.parseInt(getValue(data, PAYMENT_MULTY_NUMBER.key));
        List<InformalNotificationPaymentItem> payments = new ArrayList<>();

        for (int i = 0; i < paymentNumber; i++) {

            NotificationPaymentAttachment attachment = mapper.buildPaymentAttachment(data);

            PagoPaPaymentBase pagoPa = new PagoPaPaymentBase().noticeCode(generateNoticeCode(getValue(data, PAYMENT_NOTICE_CODE.key), i)).creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key)).attachment(attachment);

            InformalNotificationPaymentItem item = new InformalNotificationPaymentItem();
            item.setPagoPa(pagoPa);
            payments.add(item);
        }
        return payments;
    }

    // =========================================================
    // MESSAGE ID
    // =========================================================
    private UUID resolveMessageId(Map<String, String> data, String currentCxId) {

        String value = getValue(data, MESSAGE_ID.key);

        if (value == null) {
            return null;
        }
        return switch (value) {
            case "${IT}" -> UUID.fromString(messageProvider.getMessageIT(currentCxId));

            case "${IT-FR}" -> UUID.fromString(messageProvider.getMessageITFR(currentCxId));

            default -> UUID.fromString(value);
        };
    }

    // =========================================================
    // NOTICE CODE
    // =========================================================
    private String generateNoticeCode(String base, int index) {

        if (base == null) {
            return NotificationInformalValue.generateRandomNumber();
        }
        String cleaned = base.trim().replaceAll("\\D", "");

        if (cleaned.isEmpty()) {
            return NotificationInformalValue.generateRandomNumber();
        }
        if (cleaned.length() >= 18) {
            String prefix = cleaned.substring(0, 16);
            String suffix = String.format("%02d", index % 100);
            return prefix + suffix;
        }
        if (cleaned.length() < 18) {
            return cleaned + "0".repeat(18 - cleaned.length());
        }
        return NotificationInformalValue.generateRandomNumber();
    }
}

