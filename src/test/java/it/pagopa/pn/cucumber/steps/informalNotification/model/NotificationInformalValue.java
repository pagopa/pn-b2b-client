package it.pagopa.pn.cucumber.steps.informalNotification.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;


public enum NotificationInformalValue {

    // New Message Informal Request
    PRIMARY_SUBJECT("primary_subject", "È stata emessa una nuova fattura per te", false),
    PRIMARY_LONG_BODY("primary_long_body", "Riscuoti S.p.a. ti informa che è stata emessa una fattura per l'utenza n.182140 relativa al periodo 23 dicembre 2025/31 marzo 2026. Di seguito trovi le informazioni principali per il pagamento: Importo:60,68€ Scadenza 26 maggio 2026", false),
    PRIMARY_SHORT_BODY("primary_short_body", "SEND, ti informa che hai ricevuto una comunicazione da Riscuoti S.p.A.", false),
    PRIMARY_LANGUAGE("primary_language", "IT", false),

    ADDITIONAL_SUBJECT("additional_subject", null,false),
    ADDITIONAL_LONG_BODY("additional_long_body", null, false),
    ADDITIONAL_SHORT_BODY("additional_short_body", null, false),
    ADDITIONAL_LANGUAGE("additional_language", null,false),

    // Informal Request
    SENDER_DENOMINATION("senderDenomination", "Comune di Palermo", false),
    SENDER_TAX_ID("senderTaxId", "80016350821", false),
    PA_PROTOCOL_NUMBER("paProtocolNumber", "", true),
    IDEMPOTENCE_TOKEN("idempotenceToken", null, false),
    CAMPAIGN_ID("campaignId", "campaign-1", false),
    MESSAGE_ID("messageId", null, false),
    SUBJECT("subject", "Test notifica..", true),
    GROUP("group", null, false),

    RECIPIENT_TYPE("recipientType", "PF", false),
    RECIPIENT_TAX_ID("taxId", "FRMTTR76M06B715E", false),
    RECIPIENT_DENOMINATION("denomination", "Ettore Fieramosca", false),

    PEC_ADDRESS("pec_address", null, false),

    DOCUMENT_TITLE("document_title", null, false),
    DOCUMENT_DOCIDX("document_docidx", null, false),

    RECIPIENT_ADDITIONAL_LANGUAGES("additionalLanguages", null, false),

    PAYMENT_AMOUNT("payment_amount", "100", false),
    PAYMENT_DUE_DATE("payment_due_date", null, false),

    ATTACHMENT_CONTENT_TYPE("attachment_contentType", "application/pdf", false),
    ATTACHMENT_KEY("attachment_key", "classpath:/AvvisoPagoPA.pdf", false),
    DOCUMENT_KEY("document_key", "classpath:/sample_1pg.pdf", false),
    DOCUMENT("document", null, false),

    PAYMENT_MULTY_NUMBER("payment_multy_number", "1", false),
    PAYMENT_CREDITOR_TAX_ID("payment_creditorTaxId", "77777777777", false),
    PAYMENT_NOTICE_CODE("payment_noticeCode", null, true),

    PHONE_NUMBER("phone_number", null, false),
    EMAIL("email", null, false),
    DIGITAL_DOMICILE("digitalDomicile", null, false),

    PHYSICAL_ADDRESS("physicalAddress", "", false),
    PHYSICAL_ADDRESS_ADDRESS("physical_address_address", "via Roma", false),
    PHYSICAL_ADDRESS_DETAILS("physical_address_details", "Strega", false),
    PHYSICAL_ADDRESS_ZIP("physical_address_zip", "82100", false),
    PHYSICAL_ADDRESS_MUNICIPALITY("physical_address_municipality", "Benevento", false),
    PHYSICAL_ADDRESS_PROVINCE("physical_address_province", "BN", false),
    PHYSICAL_ADDRESS_AT("physical_address_at", "presso", false),
    PHYSICAL_ADDRESS_MUNICIPALITY_DETAILS("physical_address_municipality_details", null, false),
    PHYSICAL_ADDRESS_STATE("physical_address_state", "ITALIA", false);

    private static final String NULL_VALUE = "NULL";
    private static final Integer NOTICE_CODE_LENGTH = 18;

    public final String key;
    private final String defaultValue;
    private final boolean addCurrentTime;

    NotificationInformalValue(String key, String defaultValue, boolean addCurrentTime) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.addCurrentTime = addCurrentTime;
    }

    public static String getValue(Map<String, String> data, String key) {
        if (data.containsKey(key)) {
            String value = data.get(key);

            // NULL esplicito → null nel JSON
            if (NULL_VALUE.equals(value)) {
                return null;
            }
            // stringa vuota o solo spazi → DEFAULT
            if (value == null || value.trim().isEmpty()) {
                return getDefaultValue(key);
            }
            // _CHAR → generatore
            if (value.contains("_CHAR")) {
                return getCharSeq(value);
            }
            // valore reale
            return value;
        } else {
            // chiave assente → DEFAULT
            return getDefaultValue(key);
        }
    }


    public static String getCharSeq(String request) {
        StringBuilder result = new StringBuilder();
        int number = Integer.parseInt(request.substring(0, request.indexOf("_")));
        result.append("a".repeat(Math.max(0, number)));
        return result.toString();
    }

    public static String getDefaultValue(String key) {
        NotificationInformalValue notificationValue =
                Arrays.stream(NotificationInformalValue.values()).filter(value -> value.key.equals(key)).findFirst().orElse(null);
        return (notificationValue == null ? null : (notificationValue.addCurrentTime ? (notificationValue.defaultValue + generateRandomNumber()) : notificationValue.defaultValue));
    }

    public static String generateRandomNumber() {
        String threadNumber = (String.valueOf(Thread.currentThread().getId()));
        String numberOfThread = threadNumber.length() < 2 ? "0" + threadNumber : threadNumber.substring(0, 2);
        String timeNano = String.valueOf(System.nanoTime());
        String randomClassePagamento = String.valueOf(new Random().nextInt(14));
        randomClassePagamento = randomClassePagamento.length() < 2 ? "0" + randomClassePagamento : randomClassePagamento;
        String finalNumber = "302" + randomClassePagamento + numberOfThread + timeNano.substring(0, timeNano.length() - 4);
        if (finalNumber.length() > NOTICE_CODE_LENGTH) {
            finalNumber = finalNumber.substring(0, NOTICE_CODE_LENGTH);
        } else {
            int remainingLength = NOTICE_CODE_LENGTH - finalNumber.length();
            String paddingString = String.valueOf(new Random().nextInt(9)).repeat(remainingLength);
            finalNumber = finalNumber + paddingString;
        }
        return finalNumber;
    }
}