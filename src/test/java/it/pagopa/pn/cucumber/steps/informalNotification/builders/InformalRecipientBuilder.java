package it.pagopa.pn.cucumber.steps.informalNotification.builders;

import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.InformalPayment;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.InformalPhysicalAddress;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.InformalRecipient;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.RecipientType;
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

import static it.pagopa.pn.cucumber.steps.informalNotification.model.NotificationInformalValue.*;

@Component
@RequiredArgsConstructor
public class InformalRecipientBuilder {

    private final InformalNotificationRequestMapper mapper;
    private final InformalMessageProvider messageProvider;

    public InformalRecipient build(Map<String, String> data, String currentCxId) {

        InformalRecipient recipient = new InformalRecipient();

        // =========================
        // BASE
        // =========================
        String recipientType = getValue(data, RECIPIENT_TYPE.key);
        if (recipientType != null) {
            recipient.setRecipientType(RecipientType.fromValue(recipientType));
        }
        recipient.setMessageId(resolveMessageId(data, currentCxId));
        recipient.setTaxId(getValue(data, RECIPIENT_TAX_ID.key));
        recipient.setDenomination(getValue(data, RECIPIENT_DENOMINATION.key));

        recipient.setAdditionalLanguages(buildAdditionalLanguages(data));

        // =========================
        // DIGITAL
        // =========================
        String digitalDomicile = getValue(data, DIGITAL_DOMICILE.key);
        recipient.setDigitalDomicile(digitalDomicile);

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
    private InformalPhysicalAddress buildPhysicalAddress(Map<String, String> data) {

        String physicalAddressValue = data.get("physical_address");

        boolean isNull = "${PHYSICAL_ADDRESS_NULL}".equalsIgnoreCase(physicalAddressValue);

        if (isNull) {
            return null;
        }
        return InformalPhysicalAddress.builder()
                .address(getValue(data, PHYSICAL_ADDRESS_ADDRESS.key))
                .addressDetails(getValue(data, PHYSICAL_ADDRESS_DETAILS.key))
                .zip(getValue(data, PHYSICAL_ADDRESS_ZIP.key))
                .municipality(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITY.key))
                .province(getValue(data, PHYSICAL_ADDRESS_PROVINCE.key))
                .foreignState(getValue(data, PHYSICAL_ADDRESS_STATE.key))
                .at(getValue(data, PHYSICAL_ADDRESS_AT.key))
                .build();
    }

    // =========================================================
    // PAYMENTS
    // =========================================================
    private List<InformalPayment> buildPayments(Map<String, String> data) {

        String multyNumberStr = getValue(data, PAYMENT_MULTY_NUMBER.key);
        if (multyNumberStr == null) {
            return new ArrayList<>();
        }
        int paymentNumber = Integer.parseInt(multyNumberStr);
        List<InformalPayment> payments = new ArrayList<>();

        for (int i = 0; i < paymentNumber; i++) {
            String amount = getValue(data, PAYMENT_AMOUNT.key);
            String dueDate = getValue(data, PAYMENT_DUE_DATE.key);

            InformalPayment payment = InformalPayment.builder()
                    .noticeCode(generateNoticeCode(getValue(data, PAYMENT_NOTICE_CODE.key), i))
                    .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                    .amount(amount != null ? Integer.valueOf(amount) : null)
                    .dueDate(dueDate != null ? parseDueDate(dueDate) : null)
                    .attachmentKey(getValue(data, ATTACHMENT_KEY.key))
                    .attachmentContentType(getValue(data, ATTACHMENT_CONTENT_TYPE.key))
                    .build();
            payments.add(payment);
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
            case "${NEW-IT-EN}" -> UUID.fromString(messageProvider.createAndSaveMessageITEN(currentCxId));
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
