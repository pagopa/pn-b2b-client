package it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Avviso pagoPA identificato come su pn-PaymentInfo: pk = creditorTaxId##noticeCode.
 */
public record PaymentNotice(String creditorTaxId, String noticeCode, int amount) {

    private static final String PK_SEPARATOR = "##";
    private static final String QA_CREDITOR_TAX_ID = "77777777777";
    private static final String NOTICE_CODE_PREFIX = "3";
    private static final int NOTICE_CODE_LENGTH = 18;
    private static final int UNKNOWN_NOTICE_AMOUNT = 1000;

    /**
     * Chiave dell'avviso su pn-PaymentInfo.
     */
    public String paymentInfoPk() {
        return creditorTaxId + PK_SEPARATOR + noticeCode;
    }

    /**
     * Avviso ricavato dalla chiave di pn-PaymentInfo.
     */
    public static PaymentNotice fromPaymentInfoPk(String pk, int amount) {
        String[] parts = pk.split(PK_SEPARATOR);
        if (parts.length != 2) {
            throw new IllegalArgumentException("pk di pn-PaymentInfo non nel formato creditorTaxId##noticeCode: " + pk);
        }
        return new PaymentNotice(parts[0], parts[1], amount);
    }

    /**
     * Avviso con codice casuale, non censito su SEND.
     */
    public static PaymentNotice unknownToSend() {
        return new PaymentNotice(QA_CREDITOR_TAX_ID, randomNoticeCodeEndingWith(""), UNKNOWN_NOTICE_AMOUNT);
    }

    /**
     * Codice avviso casuale di 18 cifre che termina con {@code suffix}.
     */
    public static String randomNoticeCodeEndingWith(String suffix) {
        StringBuilder noticeCode = new StringBuilder(NOTICE_CODE_PREFIX);
        while (noticeCode.length() < NOTICE_CODE_LENGTH - suffix.length()) {
            noticeCode.append(ThreadLocalRandom.current().nextInt(10));
        }
        return noticeCode.append(suffix).toString();
    }
}
