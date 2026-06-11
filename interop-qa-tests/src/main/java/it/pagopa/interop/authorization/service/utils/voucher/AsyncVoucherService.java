package it.pagopa.interop.authorization.service.utils.voucher;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.generated.openapi.clients.auth.api.AsyncAuthApi;
import it.pagopa.interop.generated.openapi.clients.auth.model.ClientCredentialsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AsyncVoucherService extends AbstractClient {

    private final AsyncAuthApi asyncAuthApi;

    public ClientCredentialsResponse requestVoucher(String clientAssertion, String clientAssertionType, String grantType,
                                                    String dpoP, UUID clientId) {
        return performOperation(
                () -> this.asyncAuthApi.createAsyncTokenWithHttpInfo(clientAssertion, clientAssertionType, grantType, dpoP, clientId)
        ).orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }
}
