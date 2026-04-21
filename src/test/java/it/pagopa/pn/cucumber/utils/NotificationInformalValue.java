package it.pagopa.pn.cucumber.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;


public enum NotificationInformalValue {

    // New Message Informal Request
    PRIMARY_SUBJECT("primary_subject", "Sollecito di pagamento 2023", false),
    PRIMARY_LONG_BODY("primary_long_body", "Gentile cittadino, la informiamo che...", false),
    PRIMARY_SHORT_BODY("primary_short_body", "Sollecito: hai una nuova comunicazione.", false),
    PRIMARY_LANGUAGE("primary_language", "it", false),

    ADDITIONAL_SUBJECT("additional_subject", "Sollecito per..",false),
    ADDITIONAL_LONG_BODY("additional_long_body", "Gentile cittadino, la informiamo che... ", false),
    ADDITIONAL_SHORT_BODY("additional_short_body", "Le comunichiamo che..", false),
    ADDITIONAL_LANGUAGE("additional_language", "fr",false),

    // Informal Request
    SENDER_DENOMINATION("senderDenomination", "Comune di Palermo", false),
    SENDER_TAX_ID("senderTaxId", "80016350821", false),
    PA_PROTOCOL_NUMBER("paProtocolNumber", " ", false),
    IDEMPOTENCE_TOKEN("idempotenceToken", null, false),
    CAMPAIGN_ID("campaignId", "campaign-1", false),
    MESSAGE_ID("messageId", null, false),
    SUBJECT("subject", "Test notifica..", false),
    GROUP("group", null, false),

    RECIPIENT_TYPE("recipient_type", "PF", false),
    RECIPIENT_TAX_ID("recipient_tax_id", "FRMTTR76M06B715E", false),
    RECIPIENT_DENOMINATION("recipient_denomination", "Ettore Fieramosca", false),

    PEC_ADDRESS("pec_address", null, false),

    PAGOPA_NOTICE_CODE("pagopa_notice_code", null, false),
    PAGOPA_CREDITOR_TAX_ID("pagopa_creditor_tax_id", "77777777777", false),

    DOCUMENT_TITLE("document_title", null, false),
    DOCUMENT_DOCIDX("document_docidx", null, false);

    private static final String NULL_VALUE = "NULL";
    private static final Integer NOTICE_CODE_LENGTH = 18;

    public final String key;
    private final String defaultValue;
    private final boolean addCurrentTime;
    private static final ObjectMapper mapper = new ObjectMapper();


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

    //    public static String getValue(Map<String, String> data, String key) {
//        if (data.containsKey(key)) {
//            return data.get(key).equals(NULL_VALUE) ? null : (data.get(key).contains("_CHAR") ? getCharSeq(data.get(key)) : data.get(key));
//        } else {
//            return getDefaultValue(key);
//        }
//    }
}