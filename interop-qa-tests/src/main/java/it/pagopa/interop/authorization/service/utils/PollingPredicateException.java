package it.pagopa.interop.authorization.service.utils;

import lombok.experimental.StandardException;

/* DEV. NOTE 28/02/2025: questa classe viene usata dove prima veniva usata "IllegalArgumentException";
 * questa classe eredita quindi "IllegalArgumentException" per mantenere compatibilità con l'assetto
 * preesistente */
@StandardException
public class PollingPredicateException extends IllegalArgumentException {
}
