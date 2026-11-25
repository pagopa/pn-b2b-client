package it.pagopa.pn.cucumber.utils.notificationsearch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Legge il valore di un campo da una riga di risultato della ricerca notifiche tramite il getter
 * convenzionale ({@code get} + PascalCase), indipendentemente dal tipo concreto dell'oggetto
 * (es. {@code FullNotificationSearchRow} o {@code LegalNotificationSearchRow}). In questo modo le
 * verifiche sui criteri di ricerca non devono conoscere quale delle due risposte è stata restituita.
 * <p>
 * Se il campo non ha un getter diretto sulla riga (es. {@code viewed}/{@code delivered} sulle righe
 * delle notifiche bonarie, che le espongono annidate dentro {@code communicationOutcomes}), il reader
 * ritenta cercando lo stesso getter sull'oggetto restituito da {@code getCommunicationOutcomes()}.
 */
public final class NotificationRowFieldReader {

    private NotificationRowFieldReader() {
    }

    public static Object readField(Object row, String fieldName) {
        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            Method getter = row.getClass().getMethod(getterName);
            return getter.invoke(row);
        } catch (NoSuchMethodException e) {
            return readFieldFromCommunicationOutcomes(row, fieldName, getterName, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(
                    "Impossibile leggere il campo '" + fieldName + "' da " + row.getClass().getSimpleName(), e);
        }
    }

    private static Object readFieldFromCommunicationOutcomes(Object row, String fieldName, String getterName, NoSuchMethodException originalError) {
        try {
            Method outcomesGetter = row.getClass().getMethod("getCommunicationOutcomes");
            Object outcomes = outcomesGetter.invoke(row);
            if (outcomes == null) {
                return null;
            }
            Method nestedGetter = outcomes.getClass().getMethod(getterName);
            return nestedGetter.invoke(outcomes);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "Il campo '" + fieldName + "' non è presente su " + row.getClass().getSimpleName(), originalError);
        }
    }
}
