package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonAuthClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IPnLegalPersonAuthClientImpl implements IPnLegalPersonAuthClient {

    @Override
    public PublicKeyResponse getPublicKeys(Integer limit, String lastKey, String createdAt, Boolean showPublicKey) {
        return null;
    }
}
