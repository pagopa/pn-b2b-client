package it.pagopa.pn.interop.cucumber.steps.m2m.event.config;

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import it.pagopa.interop.event.domain.dto.M2MEvent;
import it.pagopa.interop.event.filter.EventFilter;
import it.pagopa.interop.event.filter.EventPredicate;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import it.pagopa.pn.interop.cucumber.steps.m2m.event.util.RequestMappingUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class EventRequestConfig {

    private static final boolean FAIL_FAST_UNKNOWN_FIELDS = true;
    private static final List<Supplier<M2MEvent>> eventSuppliers = List.of(
            M2MEvent::new
    );

    private final TokenResolver tokenResolver;

    public EventRequestConfig(SharedStepsContext sharedStepsContext) {
        this.tokenResolver = new TokenResolver(sharedStepsContext);
    }

    @ParameterType("non visualizza|visualizza")
    public Boolean visibilitaEvento(String testo) {
        return "visualizza".equals(testo);
    }

    @DataTableType
    public EventPredicate eventPredicate(Map<String, String> row) {
        Map<String, String> resolvedRow = resolveTokens(row);

        return new EventPredicate(event -> matchesOnlyProvidedFields(event, resolvedRow));
    }

    private Map<String, String> resolveTokens(Map<String, String> row) {
        return row.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> tokenResolver.resolve(e.getValue())
                ));
    }

    private boolean matchesOnlyProvidedFields(M2MEvent event, Map<String, String> expectedFields) {
        if (event == null) {
            return false;
        }

        for (Map.Entry<String, String> entry : expectedFields.entrySet()) {
            Object actualValue = readProperty(event, entry.getKey());
            Class<?> targetType = getPropertyType(event, entry.getKey());
            Object expectedValue = convertValue(entry.getValue(), targetType);

            if (!java.util.Objects.equals(actualValue, expectedValue)) {
                return false;
            }
        }

        return true;
    }

    private Object readProperty(Object target, String propertyName) {
        try {
            var beanInfo = java.beans.Introspector.getBeanInfo(target.getClass(), Object.class);

            for (var descriptor : beanInfo.getPropertyDescriptors()) {
                if (descriptor.getName().equals(propertyName)) {
                    var getter = descriptor.getReadMethod();
                    if (getter == null) {
                        throw new IllegalArgumentException("Campo non leggibile: " + propertyName);
                    }
                    return getter.invoke(target);
                }
            }

            throw new IllegalArgumentException(
                    "Campo non presente su " + target.getClass().getSimpleName() + ": " + propertyName
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Errore leggendo il campo " + propertyName, e);
        }
    }

    private Class<?> getPropertyType(Object target, String propertyName) {
        try {
            var beanInfo = java.beans.Introspector.getBeanInfo(target.getClass(), Object.class);

            for (var descriptor : beanInfo.getPropertyDescriptors()) {
                if (descriptor.getName().equals(propertyName)) {
                    return descriptor.getPropertyType();
                }
            }

            throw new IllegalArgumentException(
                    "Campo non presente su " + target.getClass().getSimpleName() + ": " + propertyName
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Errore leggendo il tipo del campo " + propertyName, e);
        }
    }

    private Object convertValue(String rawValue, Class<?> targetType) {
        if (rawValue == null) {
            return null;
        }

        if (String.class.equals(targetType)) {
            return rawValue;
        }

        if (java.util.UUID.class.equals(targetType)) {
            return java.util.UUID.fromString(rawValue);
        }

        if (targetType.isEnum()) {
            return convertEnumValue(rawValue, targetType);
        }

        return rawValue;
    }

    private Object convertEnumValue(String rawValue, Class<?> enumType) {
        try {
            var fromValue = enumType.getMethod("fromValue", String.class);
            return fromValue.invoke(null, rawValue);
        } catch (NoSuchMethodException ignored) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Object enumValue = Enum.valueOf((Class<? extends Enum>) enumType.asSubclass(Enum.class), rawValue);
            return enumValue;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Impossibile convertire il valore enum: " + rawValue, ex);
        }
    }

}
