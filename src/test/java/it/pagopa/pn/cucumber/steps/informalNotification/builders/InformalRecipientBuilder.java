package it.pagopa.pn.cucumber.steps.informalNotification.builders;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationPaymentItem;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationRecipientV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NotificationDigitalAddress;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NotificationPaymentAttachment;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NotificationPhysicalAddress;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.PagoPaPaymentBase;
import it.pagopa.pn.cucumber.steps.informalNotification.mapper.InformalNotificationRequestMapper;
import it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue;
import it.pagopa.pn.cucumber.steps.informalNotification.provider.InformalMessageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.DIGITAL_DOMICILE;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.EMAIL;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.MESSAGE_ID;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PAYMENT_AMOUNT;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PAYMENT_CREDITOR_TAX_ID;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PAYMENT_DUE_DATE;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PAYMENT_MULTY_NUMBER;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PAYMENT_NOTICE_CODE;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PHONE_NUMBER;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PHYSICAL_ADDRESS_ADDRESS;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PHYSICAL_ADDRESS_AT;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PHYSICAL_ADDRESS_DETAILS;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PHYSICAL_ADDRESS_MUNICIPALITY;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PHYSICAL_ADDRESS_PROVINCE;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PHYSICAL_ADDRESS_STATE;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.PHYSICAL_ADDRESS_ZIP;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.RECIPIENT_ADDITIONAL_LANGUAGES;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.RECIPIENT_DENOMINATION;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.RECIPIENT_TAX_ID;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.RECIPIENT_TYPE;
import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.getValue;

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

        recipient.setAdditionalLanguages(buildAdditionalLanguages(data));


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

    // =========================
    // ADDITIONAL LANGUAGES
    // =========================
    private List<String> buildAdditionalLanguages(Map<String, String> data) {

        String languages = getValue(data, RECIPIENT_ADDITIONAL_LANGUAGES.key);

        if (languages == null) {
            return null;
        }
        return Arrays.stream(languages.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
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

            String amount = getValue(data, PAYMENT_AMOUNT.key);
            if (amount != null) {
                pagoPa.setAmount(Integer.valueOf(amount));
            }
            String dueDate = getValue(data, PAYMENT_DUE_DATE.key);
            if (dueDate != null) {
                pagoPa.setDueDate(parseDueDate(dueDate));
            }
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

            case "${IT}" -> UUID.fromString(messageProvider.getOrCreateMessageIT(currentCxId));
            case "${IT-FR}" -> UUID.fromString(messageProvider.getOrCreateMessageITFR(currentCxId));
            case "${NEW-IT}" -> UUID.fromString(messageProvider.createAndSaveMessageIT(currentCxId));
            case "${NEW-IT-FR}" -> UUID.fromString(messageProvider.createAndSaveMessageITFR(currentCxId));
            case "${NEW-IT-DE}" -> UUID.fromString(messageProvider.createAndSaveMessageITDE(currentCxId));
            case "${NEW-IT-SL}" -> UUID.fromString(messageProvider.createAndSaveMessageITSL(currentCxId));
            case "${SAVED-IT}" -> UUID.fromString(messageProvider.getSavedMessageIT());
            case "${SAVED-IT-FR}" -> UUID.fromString(messageProvider.getSavedMessageITFR());

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

    private OffsetDateTime parseDueDate(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return value.length() == 10 ? LocalDate.parse(value).atStartOfDay().atOffset(ZoneOffset.UTC) : OffsetDateTime.parse(value);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid payment dueDate: " + value, e);
        }
    }
}

