package it.pagopa.pn.cucumber.steps.templateEngine.escapemod;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlEscapeValidator {
    public static void assertPdfValueEscapingDisabled(
            HtmlEscapeExecution execution,
            String field,
            String rawValue,
            String rendered
    ) {

        String renderedWithoutEscaping = rawValue
                .replace("<b>", "")
                .replace("</b>", "");

        assertThat(rendered)
                .as(
                        "Con escaping disabilitato il PDF dell'endpoint %s "
                                + "non contiene il valore renderizzato del campo %s. Atteso: %s",
                        execution.getEndpoint(),
                        field,
                        renderedWithoutEscaping
                )
                .contains(renderedWithoutEscaping);

        assertThat(rendered)
                .as(
                        "Con escaping disabilitato il markup del campo %s "
                                + "non dovrebbe essere mostrato letteralmente nel PDF dell'endpoint %s",
                        field,
                        execution.getEndpoint()
                )
                .doesNotContain(rawValue);
    }

    public static void assertHtmlValueEscapingDisabled(
            HtmlEscapeExecution execution,
            String field,
            String rawValue,
            String rendered
    ) {

        assertThat(rendered)
                .as("Con escaping disabilitato il valore raw del campo %s non è presente nell'HTML dell'endpoint %s",
                        field,
                        execution.getEndpoint()
                )
                .contains(rawValue);

        String escapedValue = HtmlEscapeUtilities.toSingleLine(escapeExpected(rawValue));

        assertThat(rendered)
                .as("Con escaping disabilitato il campo % risulta ancora escapato nell'HTML dell'endpoint %s",
                        field,
                        execution.getEndpoint()
                )
                .doesNotContain(escapedValue);
    }

    public static void assertPdfValue(HtmlEscapeExecution execution, String field, String rawValue, String rendered) {
        assertThat(rendered)
                .as(
                        "Il PDF dell'endpoint %s non contiene il valore letterale del campo %s. Atteso: %s",
                        execution.getEndpoint(),
                        field,
                        rawValue
                )
                .contains(rawValue);

        String escapedValue = HtmlEscapeUtilities.toSingleLine(escapeExpected(rawValue));

        if (!escapedValue.equals(rawValue)) {
            assertThat(rendered)
                    .as(
                            "Il PDF dell'endpoint %s contiene entity HTML non renderizzate per il campo %s",
                            execution.getEndpoint(),
                            field
                    )
                    .doesNotContain(escapedValue);
        }
    }

    public static void assertHtmlValue(HtmlEscapeExecution execution, String field, String rawValue, String rendered) {
        String escapedValue = HtmlEscapeUtilities.toSingleLine(escapeExpected(rawValue));
        assertThat(rendered)
                .as(
                        "Escaping HTML errato per endpoint %s, campo %s. Atteso: %s",
                        execution.getEndpoint(),
                        field,
                        escapedValue
                )
                .contains(escapedValue);

        if (isUniqueFuzzValue(rawValue)) {
            assertThat(rendered)
                    .as(
                            "Il valore non escapato del campo %s e' presente nell'HTML dell'endpoint %s",
                            field,
                            execution.getEndpoint()
                    )
                    .doesNotContain(rawValue);
        }
    }

    public static void assertTextValue(
            HtmlEscapeExecution execution,
            String field,
            String rawValue,
            String rendered
    ) {

        assertThat(rendered)
                .as(
                        "Il template testuale dell'endpoint %s non contiene il valore del campo %s. Atteso: %s",
                        execution.getEndpoint(),
                        field,
                        rawValue
                )
                .contains(rawValue);
    }

    private static String escapeExpected(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    private static boolean isUniqueFuzzValue(String value) {
        return value != null
                && (value.startsWith("FZ_") || value.startsWith("FUZZ_"));
    }
}
