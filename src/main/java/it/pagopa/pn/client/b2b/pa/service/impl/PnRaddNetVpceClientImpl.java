package it.pagopa.pn.client.b2b.pa.service.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.invoker.ApiClient;
//import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.ActOperationsApi;
//import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.AorOperationsApi;
//import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.DocumentOperationsApi;
//import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.NotificationInquiryApi;
//import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.ImportApi;
//import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.RegistryApi;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnRaddNetVpceClientImpl {

//    protected final ActOperationsApi actOperationsApi;
//    protected final AorOperationsApi aorOperationsApi;
//    protected final DocumentOperationsApi documentOperationsApi;
//    protected final NotificationInquiryApi notificationInquiryApi;
//    protected final ImportApi importApi;
//    protected final RegistryApi registryApi;

    public PnRaddNetVpceClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.radd.vpce.base-url}") String basePath
    ) {
//        ApiClient apiClient = new ApiClient(restTemplate);
//        apiClient.setBasePath(basePath);

//        this.actOperationsApi = new ActOperationsApi(apiClient);
//        this.aorOperationsApi = new AorOperationsApi(apiClient);
//        this.documentOperationsApi = new DocumentOperationsApi(apiClient);
//        this.notificationInquiryApi = new NotificationInquiryApi(apiClient);
//        this.importApi = new ImportApi(apiClient);
//        this.registryApi = new RegistryApi(apiClient);
    }
}