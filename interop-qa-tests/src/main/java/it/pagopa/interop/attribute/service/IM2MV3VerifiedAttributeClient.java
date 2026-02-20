package it.pagopa.interop.attribute.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;

public interface IM2MV3VerifiedAttributeClient extends IM2MVerifiedAttributeClient,
    SettableHeaders, Authenticable {
}
