package it.pagopa.interop.authorization.service.utils;

import java.util.Map;

/** @deprecated Frutto di un'idea precedente all'introduzione di
 * {@link it.pagopa.interop.authorization.domain.Auth} e {@link it.pagopa.interop.common.client.AbstractDPoPClient}
 */
@Deprecated
public interface SettableHeaders {
    @Deprecated
    void setHeaders(Map<String, String> headers);
}