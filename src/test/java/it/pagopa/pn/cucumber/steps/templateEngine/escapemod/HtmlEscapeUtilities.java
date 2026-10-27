package it.pagopa.pn.cucumber.steps.templateEngine.escapemod;

import it.pagopa.common.util.PDFUtility;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class HtmlEscapeUtilities {
    public static String normalizeRendered(String rendered, String format) {
        return switch (format) {
            case "html" -> normalizeHtmlRendered(rendered);
            case "pdf", "text" -> toSingleLine(rendered);
            default -> throw new IllegalArgumentException(
                    "Formato non supportato: " + format
            );
        };
    }

    public static String normalizeHtmlRendered(String value) {
        if (value == null) {
            return null;
        }

        return toSingleLine(value)
                .replace("&#xE0;", "à")
                .replace("&#xE8;", "è")
                .replace("&#xEC;", "ì")
                .replace("&#x20AC;", "€");
    }

    public static String toSingleLine(String value) {
        if (value == null) {
            return null;
        }

        return value.replaceAll("\\R\\s*", "");
    }

    public static String normalize(String value) {
        return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    public static String decapitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    public static List<String> splitFields(String fields) {
        return Arrays.stream(fields.split(","))
                .map(String::trim)
                .filter(field -> !field.isBlank())
                .toList();
    }

    public static String extractPdfText(Resource resource, String endpoint) {
        try (InputStream inputStream = resource.getInputStream()) {
            return PDFUtility.extractText(inputStream.readAllBytes());
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException(
                    "Impossibile estrarre il testo del PDF per " + endpoint,
                    e
            );
        }
    }

    public static Object convertStringValue(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        }
        if (targetType.isEnum()) {
            return Arrays.stream(targetType.getEnumConstants())
                    .filter(constant -> ((Enum<?>) constant).name().equalsIgnoreCase(value)
                            || constant.toString().equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Valore enum non valido " + value + " per " + targetType.getName()));
        }
        throw new IllegalStateException("Il valore HTML e' previsto su campi testuali, trovato " + targetType.getName());
    }

}
