package it.pagopa.pn.client.b2b.pa.polling.design;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tipo di polling gestito dal service annotato, usato da {@link PnPollingServiceRegistry}.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PnPollingService {

    PnPollingType value();
}
