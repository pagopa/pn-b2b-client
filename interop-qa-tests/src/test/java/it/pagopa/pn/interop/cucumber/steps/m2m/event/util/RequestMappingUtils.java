package it.pagopa.pn.interop.cucumber.steps.m2m.event.util;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RequestMappingUtils {

    private static final Map<Class<?>, Map<String, Method>> SETTER_CACHE = new ConcurrentHashMap<>();

    private RequestMappingUtils() {
    }

    public static <T> T mapToRequest(T request, Map<String, String> customData, boolean failFastUnknownFields) {
        try {
            Map<String, Method> setterByName = SETTER_CACHE.computeIfAbsent(
                    request.getClass(),
                    RequestMappingUtils::extractSetterByName
            );

            for (Map.Entry<String, String> entry : customData.entrySet()) {
                Method setter = setterByName.get(entry.getKey());
                if (setter == null) {
                    if (failFastUnknownFields) {
                        throw new IllegalArgumentException(
                                "Campo non mappabile per " + request.getClass().getSimpleName() + ": " + entry.getKey());
                    }
                    continue;
                }

                Class<?> targetType = setter.getParameterTypes()[0];
                Object convertedValue = convertValue(entry.getValue(), targetType);
                setter.invoke(request, convertedValue);
            }

            return request;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Errore durante il mapping automatico dei campi custom", ex);
        }
    }

    private static Map<String, Method> extractSetterByName(Class<?> requestType) {
        try {
            var writableProps = Introspector.getBeanInfo(requestType, Object.class).getPropertyDescriptors();
            Map<String, Method> setterByName = new HashMap<>();

            for (var prop : writableProps) {
                if (prop.getWriteMethod() != null) {
                    setterByName.put(prop.getName(), prop.getWriteMethod());
                }
            }

            return setterByName;
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Errore durante l'analisi dei setter di " + requestType.getSimpleName(), ex);
        }
    }

    private static Object convertValue(String rawValue, Class<?> targetType) {
        if (rawValue == null) {
            return null;
        }

        if (String.class.equals(targetType)) {
            return rawValue;
        }

        if (UUID.class.equals(targetType)) {
            return UUID.fromString(rawValue);
        }

        if (targetType.isEnum()) {
            return convertEnumValue(rawValue, targetType);
        }

        return rawValue;
    }

    private static Object convertEnumValue(String rawValue, Class<?> enumType) {
        try {
            Method fromValue = enumType.getMethod("fromValue", String.class);
            return fromValue.invoke(null, rawValue);
        } catch (NoSuchMethodException ignored) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object enumValue = Enum.valueOf((Class<? extends Enum>) enumType.asSubclass(Enum.class), rawValue);
            return enumValue;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Impossibile convertire il valore enum: " + rawValue, ex);
        }
    }
}

