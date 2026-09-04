package it.pagopa.pn.interop.cucumber.steps.m2m.attribute.mapper;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AttributeSeedMapper<T> {
    private static final Pattern SIZE_PLACEHOLDER_PATTERN = Pattern.compile("^\\$SIZE\\((\\d+)\\)$");
    private static final Pattern EMPTY_PLACEHOLDER_PATTERN = Pattern.compile("^\\$EMPTY\\(\\)$");

    private Function<AttributePrototype, T> prototypeMapper;

    /**
     * Questo metodo crea un oggetto specifico di tipo T utilizzando una funzione di mappatura configurabile.
     * Il metodo genera valori predefiniti per i campi mancanti o vuoti e supporta le macro $SIZE(...) e $EMPTY().
     *
     * @param entry una map containing the attribute details, where keys represent attribute properties
     *              (e.g., "name", "description", "code") and values represent their corresponding values.
     * @return una istanza di tipo T rappresentante l'oggetto attributo creato in base alla mappa di input.
     */
    public T mapAttributeSeed(Map<String, String> entry) {
        // Recupera e imposta il nome (generato automaticamente se assente)
        String name = entry.get("name");
        String actualName = resolveStringValue(name == null || name.isBlank()
                ? String.format("new_attribute_%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE))
                : name);

        // Imposta la descrizione: se assente, imposta una descrizione di default
        String description = entry.get("description");
        String actualDescription = (description == null || description.isBlank())
                ? "Descrizione automatica per attributo: " + actualName
                : description;

        // Recupera e imposta il codice (generato automaticamente se richiesto)
        String code = entry.get("code");
        String actualCode = (code == null || code.isBlank())
                ? generateUniqueAttributeCode()
                : code;
        AttributePrototype attributePrototype = AttributePrototype.of(actualName, actualDescription,
            actualCode);
        return this.prototypeMapper.apply(attributePrototype);
    }

    private String resolveStringValue(String name) {
        Matcher matcher = SIZE_PLACEHOLDER_PATTERN.matcher(name);
        if (matcher.matches()) {
            int size = Integer.parseInt(matcher.group(1));
            return generateRandomString(size, "new_attribute_");
        }
        matcher.usePattern(EMPTY_PLACEHOLDER_PATTERN);
        if (matcher.matches()) {
            return "";
        }
        return name;
    }

    private String generateRandomString(int length, String prefix) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(length);
        builder.append(prefix);
        if (builder.length() >= length) {
            return builder.substring(0, length);
        }
        for (int i = 0; i < length - prefix.length(); i++) {
            builder.append(chars.charAt(random.nextInt(chars.length())));
        }
        return builder.toString();
    }

    private String generateUniqueAttributeCode() {
        final String prefix = "unique_code";
        long timestamp = System.currentTimeMillis();
        return prefix + "_" + timestamp;
    }
}
