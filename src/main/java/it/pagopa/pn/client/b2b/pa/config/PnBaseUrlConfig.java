package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PnBaseUrlConfig {

    @Value("${pn.delivery.base-url}")
    private String deliveryBaseUrl;
    @Value("${pn.safeStorage.base-url}")
    private String safeStorageBaseUrl;
    @Value("${pn.external.base-url}")
    private String externalBaseUrl;
    @Value("${pn.webapi.external.base-url}")
    private String webApiExternalBaseUrl;
    @Value("${pn.external.dest.base-url}")
    private String externalDestBaseUrl;
    @Value("${pn.dataVault.base-url}")
    private String dataVaultBaseUrl;
    @Value("${pn.interop.base-url}")
    private String interopBaseUrl;
    @Value("${pn.interop.tracing.base-url}")
    private String interopTracingBaseUrl;
    @Value("${pn.internal.delivery-base-url}")
    private String internalDeliveryBaseUrl;
    @Value("${pn.internal.delivery-push-base-url}")
    private String internalDeliveryPushBaseUrl;
    @Value("${pn.internal.gpd-base-url}")
    private String internalGdpBaseUrl;
    @Value("${pn.radd.base-url}")
    private String raddBaseUrl;
    @Value("${pn.appio.externa.base-url}")
    private String appIoExternalBaseUrl;
    @Value("${pn.externalChannels.base-url}")
    private String externalChannelsBaseUrl;
    @Value("${pn.radd.alt.external.base-url}")
    private String raddAltExternalBaseUrl;
    @Value("${pn.OpenSearch.base-url}")
    private String openSearchBaseUrl;
}
