package it.pagopa.pn.interop.cucumber.steps.m2m.event.config;

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import it.pagopa.interop.event.domain.dto.M2MEvent;
import it.pagopa.interop.event.filter.EventPredicate;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EventRequestConfig {

    private final TokenResolver tokenResolver;

    private static final Map<Class<?>, Map<String, PropertyDescriptor>> PROPERTY_CACHE =
            new ConcurrentHashMap<>();

    public EventRequestConfig(SharedStepsContext sharedStepsContext) {
        this.tokenResolver = new TokenResolver(sharedStepsContext);
    }

    @ParameterType("non visualizza|visualizza")
    public Boolean visibilitaEvento(String testo) {
        return "visualizza".equals(testo);
    }

    @DataTableType
    public EventPredicate eventPredicate(Map<String, String> row) {
        String propertyName = row.get("field");
        String rawValue = row.get("value");

        if (propertyName == null || propertyName.isBlank()) {
            throw new IllegalArgumentException("Il campo 'field' è obbligatorio");
        }

        String resolvedValue = tokenResolver.resolve(rawValue);

        return event -> matchesField(event, propertyName, resolvedValue);
    }

    private boolean matchesField(M2MEvent event, String propertyName, String expectedRawValue) {
        if (event == null) {
            return false;
        }

        PropertyDescriptor descriptor = getPropertyDescriptor(event.getClass(), propertyName);
        Method getter = descriptor.getReadMethod();

        if (getter == null) {
            throw new IllegalArgumentException(
                    "Campo non leggibile su " + event.getClass().getSimpleName() + ": " + propertyName
            );
        }

        Object actualValue = invokeGetter(getter, event);
        Object expectedValue = convertValue(expectedRawValue, descriptor.getPropertyType());

        return Objects.equals(actualValue, expectedValue);
    }

    private static PropertyDescriptor getPropertyDescriptor(Class<?> targetClass, String propertyName) {
        Map<String, PropertyDescriptor> descriptors =
                PROPERTY_CACHE.computeIfAbsent(targetClass, EventRequestConfig::introspectProperties);

        PropertyDescriptor descriptor = descriptors.get(propertyName);
        if (descriptor == null) {
            throw new IllegalArgumentException(
                    "Campo non presente su " + targetClass.getSimpleName() + ": " + propertyName
            );
        }

        return descriptor;
    }

    private static Map<String, PropertyDescriptor> introspectProperties(Class<?> targetClass) {
        try {
            PropertyDescriptor[] propertyDescriptors =
                    Introspector.getBeanInfo(targetClass, Object.class).getPropertyDescriptors();

            Map<String, PropertyDescriptor> result = new HashMap<>();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                result.put(descriptor.getName(), descriptor);
            }
            return result;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Errore durante introspezione della classe " + targetClass.getSimpleName(), e
            );
        }
    }

    private static Object invokeGetter(Method getter, Object target) {
        try {
            return getter.invoke(target);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Errore leggendo il campo tramite getter " + getter.getName(), e
            );
        }
    }

    private Object convertValue(String rawValue, Class<?> targetType) {
        if (rawValue == null) {
            return null;
        }

        if (String.class.equals(targetType)) {
            return rawValue;
        }

        if (UUID.class.equals(targetType)) {
            return UUID.fromString(rawValue);
        }

        if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            return Integer.valueOf(rawValue);
        }

        if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            return Long.valueOf(rawValue);
        }

        if (Boolean.class.equals(targetType) || boolean.class.equals(targetType)) {
            return Boolean.valueOf(rawValue);
        }

        if (targetType.isEnum()) {
            return convertEnumValue(rawValue, targetType);
        }

        return rawValue;
    }

    private Object convertEnumValue(String rawValue, Class<?> enumType) {
        try {
            Method fromValue = enumType.getMethod("fromValue", String.class);
            return fromValue.invoke(null, rawValue);
        } catch (NoSuchMethodException ignored) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Object enumValue = Enum.valueOf((Class<? extends Enum>) enumType.asSubclass(Enum.class), rawValue);
            return enumValue;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Impossibile convertire il valore enum '" + rawValue + "' nel tipo " + enumType.getSimpleName(),
                    e
            );
        }
    }
}