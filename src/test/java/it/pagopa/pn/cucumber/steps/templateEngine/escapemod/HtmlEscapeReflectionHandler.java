package it.pagopa.pn.cucumber.steps.templateEngine.escapemod;


import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import org.springframework.core.io.Resource;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

public class HtmlEscapeReflectionHandler {
    public static Object createLanguageValue(Class<?> languageType, String language) {
        String enumValue = switch (language.toUpperCase()) {
            case "ITALIANA" -> "IT";
            case "TEDESCA" -> "DE";
            case "SLOVENA" -> "SL";
            case "FRANCESE" -> "FR";
            case "INGLESE" -> "EN";
            default -> throw new IllegalArgumentException("Lingua non valida: " + language);
        };

        if (languageType == String.class) {
            return enumValue;
        }
        if (languageType.isEnum()) {
            return Arrays.stream(languageType.getEnumConstants())
                    .filter(value -> ((Enum<?>) value).name().equalsIgnoreCase(enumValue)
                            || value.toString().equalsIgnoreCase(enumValue))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Valore lingua " + enumValue + " non disponibile su " + languageType.getName()));
        }
        throw new IllegalStateException("Tipo lingua non supportato: " + languageType.getName());
    }

    public static TemplateEngineResult toTemplateEngineResult(Object response) throws ReflectiveOperationException {
        if (response instanceof Resource resource) {
            return new TemplateEngineResult(resource);
        }
        if (response instanceof String text) {
            return new TemplateEngineResult(text);
        }
        if (response == null) {
            return null;
        }

        // Compatibilita' con eventuali wrapper tipo ResponseEntity senza introdurre dipendenze nuove.
        try {
            Method getBody = response.getClass().getMethod("getBody");
            return toTemplateEngineResult(getBody.invoke(response));
        } catch (NoSuchMethodException ignored) {
            throw new IllegalStateException("Tipo risposta non gestito: " + response.getClass().getName());
        }
    }

    public static Object createDefaultModel(Class<?> type, int depth, Set<Class<?>> branch) throws ReflectiveOperationException {
        if (depth > 5 || branch.contains(type)) {
            return null;
        }

        Object simpleValue = defaultSimpleValue(type, type.getSimpleName());
        if (simpleValue != null) {
            return simpleValue;
        }

        Object model = type.getDeclaredConstructor().newInstance();
        Set<Class<?>> nextBranch = new LinkedHashSet<>(branch);
        nextBranch.add(type);

        for (Method setter : type.getMethods()) {
            if (!setter.getName().startsWith("set") || setter.getParameterCount() != 1) {
                continue;
            }

            String property = HtmlEscapeUtilities.decapitalize(setter.getName().substring(3));
            if ("additionalProperties".equals(property)) {
                continue;
            }

            Object value = defaultValueForProperty(setter.getParameterTypes()[0],
                    setter.getGenericParameterTypes()[0], property, depth + 1, nextBranch);
            if (value != null) {
                setter.invoke(model, value);
            }
        }
        return model;
    }

    public static Object defaultValueForProperty(Class<?> type, Type genericType, String property,
                                           int depth, Set<Class<?>> branch) throws ReflectiveOperationException {
        if (type == String.class) {
            return defaultStringValue(property);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.TRUE;
        }
        if (type == int.class || type == Integer.class) {
            return 1;
        }
        if (type == long.class || type == Long.class) {
            return 1L;
        }
        if (type == double.class || type == Double.class) {
            return 1D;
        }
        if (type == float.class || type == Float.class) {
            return 1F;
        }
        if (type == BigDecimal.class) {
            return BigDecimal.ONE;
        }
        if (type == BigInteger.class) {
            return BigInteger.ONE;
        }
        if (type == URI.class) {
            return URI.create("https://example.org");
        }
        if (type == OffsetDateTime.class) {
            return OffsetDateTime.parse("2025-05-20T10:15:30Z");
        }
        if (type == LocalDateTime.class) {
            return LocalDateTime.parse("2025-05-20T10:15:30");
        }
        if (type == LocalDate.class) {
            return LocalDate.parse("2025-05-20");
        }
        if (type == Instant.class) {
            return Instant.parse("2025-05-20T10:15:30Z");
        }
        if (type == UUID.class) {
            return UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        }
        if (type.isEnum()) {
            return selectEnumDefault(type, property);
        }
        if (Collection.class.isAssignableFrom(type)) {
            Object item = createCollectionItem(genericType, property, depth, branch);
            if (item == null) {
                return null;
            }
            if (Set.class.isAssignableFrom(type)) {
                return Set.of(item);
            }
            return List.of(item);
        }
        if (Map.class.isAssignableFrom(type)) {
            return Map.of();
        }

        return createDefaultModel(type, depth, branch);
    }

    public static Object createCollectionItem(Type genericType, String property,
                                        int depth, Set<Class<?>> branch) throws ReflectiveOperationException {
        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            return null;
        }
        Type itemType = parameterizedType.getActualTypeArguments()[0];
        if (!(itemType instanceof Class<?> itemClass)) {
            return null;
        }
        return defaultValueForProperty(itemClass, itemClass, property, depth, branch);
    }

    public static Object defaultSimpleValue(Class<?> type, String property) {
        if (type == String.class) {
            return defaultStringValue(property);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.TRUE;
        }
        if (type == int.class || type == Integer.class) {
            return 1;
        }
        if (type == long.class || type == Long.class) {
            return 1L;
        }
        return null;
    }

    public static Object selectEnumDefault(Class<?> enumType, String property) {
        List<String> preferredNames = new ArrayList<>();
        String normalizedProperty = HtmlEscapeUtilities.normalize(property);
        if (normalizedProperty.contains("recipienttype")) {
            preferredNames.add("PF");
        }
        if (normalizedProperty.contains("status")) {
            preferredNames.add("SUCCESS");
        }
        if (normalizedProperty.equals("type")) {
            preferredNames.add("PEC");
            preferredNames.add("DIGITAL");
        }
        preferredNames.add("IT");

        Object[] constants = enumType.getEnumConstants();
        for (String preferredName : preferredNames) {
            for (Object constant : constants) {
                if (((Enum<?>) constant).name().equalsIgnoreCase(preferredName)
                        || constant.toString().equalsIgnoreCase(preferredName)) {
                    return constant;
                }
            }
        }
        return constants.length == 0 ? null : constants[0];
    }

    public static String defaultStringValue(String property) {
        String normalizedProperty = HtmlEscapeUtilities.normalize(property);
        if (normalizedProperty.contains("iun")) {
            return "UTGP-ZRHR-XDNQ-202505-Q-1";
        }
        if (normalizedProperty.contains("taxid")) {
            return "RSSMRA80A01H501U";
        }
        if (normalizedProperty.contains("url") || normalizedProperty.contains("link")) {
            return "https://example.org";
        }
        if (normalizedProperty.contains("date") || normalizedProperty.contains("time")
                || normalizedProperty.equals("when")) {
            return "2025-05-20T10:15:30Z";
        }
        if (normalizedProperty.contains("digest")) {
            return "TEST_digest_allegato";
        }
        if (normalizedProperty.contains("recipienttype")) {
            return "PF";
        }
        if (normalizedProperty.equals("type")) {
            return "PEC";
        }
        if (normalizedProperty.contains("status")) {
            return "SUCCESS";
        }
        if (normalizedProperty.contains("phone")) {
            return "0612345678";
        }
        if (normalizedProperty.contains("verificationcode")) {
            return "12345";
        }
        return property + "_value";
    }

    public static void setNestedProperty(Object current, String[] path, int index, String value)
            throws ReflectiveOperationException {
        if (current == null) {
            throw new IllegalStateException("Impossibile valorizzare il path " + String.join(".", path));
        }

        if (current instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                throw new IllegalStateException("Collection vuota nel path " + String.join(".", path));
            }
            for (Object item : collection) {
                setNestedProperty(item, path, index, value);
            }
            return;
        }

        String property = path[index];
        if (index == path.length - 1) {
            Method setter = findSetter(current.getClass(), property);
            setter.invoke(current, HtmlEscapeUtilities.convertStringValue(value, setter.getParameterTypes()[0]));
            return;
        }

        Method getter = findGetter(current.getClass(), property);
        Object nested = getter.invoke(current);
        if (nested == null) {
            Method setter = findSetter(current.getClass(), property);
            nested = defaultValueForProperty(setter.getParameterTypes()[0], setter.getGenericParameterTypes()[0],
                    property, 1, new LinkedHashSet<>());
            setter.invoke(current, nested);
        }
        setNestedProperty(nested, path, index + 1, value);
    }

    public static Method findGetter(Class<?> type, String property) {
        String normalizedProperty = HtmlEscapeUtilities.normalize(property);
        return Arrays.stream(type.getMethods())
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> method.getName().startsWith("get") || method.getName().startsWith("is"))
                .filter(method -> {
                    String name = method.getName().startsWith("get")
                            ? method.getName().substring(3)
                            : method.getName().substring(2);
                    return HtmlEscapeUtilities.normalize(name).equals(normalizedProperty);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Getter non trovato per " + type.getSimpleName() + "." + property));
    }

    public static Method findSetter(Class<?> type, String property) {
        String normalizedProperty = HtmlEscapeUtilities.normalize(property);
        return Arrays.stream(type.getMethods())
                .filter(method -> method.getName().startsWith("set") && method.getParameterCount() == 1)
                .filter(method -> HtmlEscapeUtilities.normalize(method.getName().substring(3)).equals(normalizedProperty))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Setter non trovato per " + type.getSimpleName() + "." + property));
    }

}
