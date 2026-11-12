package it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Avviso pagoPA identificato come su pn-PaymentInfo: pk = creditorTaxId##noticeCode.
 */
public record PaymentNotice(String creditorTaxId, String noticeCode, int amount) {

    private static final String PK_SEPARATOR = "##";
    private static final String QA_CREDITOR_TAX_ID = "77777777777";
    private static final int NOTICE_CODE_LENGTH = 18;
    private static final int UNKNOWN_NOTICE_AMOUNT = 1000;

    public String paymentInfoPk() {
        return creditorTaxId + PK_SEPARATOR + noticeCode;
    }

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
        StringBuilder noticeCode = new StringBuilder("3");
        while (noticeCode.length() < NOTICE_CODE_LENGTH) {
            noticeCode.append(ThreadLocalRandom.current().nextInt(10));
        }
        return new PaymentNotice(QA_CREDITOR_TAX_ID, noticeCode.toString(), UNKNOWN_NOTICE_AMOUNT);
    }
}
