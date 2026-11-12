package it.pagopa.pn.cucumber.steps.fatturazioneComBo.model;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * Lettura dei numeri dagli item DynamoDB. La lambda di fatturazione delle legali riscrive l'elemento di timeline dopo
 * averlo convertito in JSON: nei suoi record i numeri dei details arrivano come stringhe (es. notificationCost "100").
 * In pn-Timelines e nei record delle bonarie (invoicingManager scrive l'elemento così com'è) sono numeri.
 */
final class DynamoValues {

    private DynamoValues() {
    }

    static Integer integer(AttributeValue value) {
        if (value == null) {
            return null;
        }
        String text = value.n() != null ? value.n() : value.s();
        return text == null || text.isBlank() ? null : Integer.valueOf(text.trim());
    }

    static Long longValue(AttributeValue value) {
        if (value == null) {
            return null;
        }
        String text = value.n() != null ? value.n() : value.s();
        return text == null || text.isBlank() ? null : Long.valueOf(text.trim());
    }
}
