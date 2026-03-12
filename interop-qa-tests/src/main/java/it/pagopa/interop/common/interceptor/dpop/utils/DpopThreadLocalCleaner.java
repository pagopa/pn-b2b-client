package it.pagopa.interop.common.interceptor.dpop.utils;

import it.pagopa.interop.common.interceptor.dpop.DPoPAuthInterceptor;
import it.pagopa.interop.common.interceptor.dpop.DPoPTokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DpopThreadLocalCleaner {

    private final DPoPAccessTokenSupplier tokenSupplier;
    private final DPoPAuthInterceptor authInterceptor;
    private final DPoPTokenInterceptor tokenInterceptor;

    public void clear() {
        tokenSupplier.clear();
        authInterceptor.clear();
        tokenInterceptor.clear();
    }
}
