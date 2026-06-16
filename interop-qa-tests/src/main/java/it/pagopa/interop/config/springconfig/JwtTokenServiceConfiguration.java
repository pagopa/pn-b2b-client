package it.pagopa.interop.config.springconfig;

import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.DataPreparationService;
import it.pagopa.interop.authorization.service.M2MTokenService;
import it.pagopa.interop.authorization.service.factory.InteropTokenFactory;
import it.pagopa.interop.authorization.service.factory.TracingTokenFactory;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.identity.IdentityServiceInteropImpl;
import it.pagopa.interop.authorization.service.identity.IdentityServiceSelfcareImpl;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.DpopProofService;
import it.pagopa.interop.authorization.service.utils.voucher.DPoPVoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.auth.ApiClient;
import it.pagopa.interop.generated.openapi.clients.auth.api.AsyncAuthApi;
import it.pagopa.interop.utils.HttpCallExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.kms.KmsClient;

@Configuration
public class JwtTokenServiceConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public KmsClient kmsClient() {
        return KmsClient.create();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TracingTokenFactory tracingTokenFactory(
        InteropClientConfigs interopClientConfigs,
        ConfigFileReader configFileReader,
        KmsClient kmsClient
    ) {
        return new TracingTokenFactory(interopClientConfigs, configFileReader, kmsClient);
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public InteropTokenFactory interopTokenFactory(
        InteropClientConfigs interopClientConfigs,
        ConfigFileReader configFileReader,
        KmsClient kmsClient
    ) {
        return new InteropTokenFactory(interopClientConfigs, configFileReader, kmsClient);
    }

    @Bean(name = "tracingIdentityService")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdentityService tracingIdentityService(TracingTokenFactory tracingTokenFactory, ConfigFileReader configFileReader) {
        return new IdentityServiceSelfcareImpl(tracingTokenFactory, configFileReader);
    }

    @Bean(name = "interopSelfcareIdentityService")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdentityService interopSelfcareIdentityService(InteropTokenFactory tracingTokenFactory, ConfigFileReader configFileReader) {
        return new IdentityServiceSelfcareImpl(tracingTokenFactory, configFileReader);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public M2MTokenService m2mTokenService(
            @Qualifier("interopSelfcareIdentityService") @Autowired IdentityService identityService,
            DataPreparationService dataPreparationService,
            VoucherService voucherService
    ) {
        return new M2MTokenService(identityService, dataPreparationService, voucherService);
    }

    @Bean(name = "interopIdentityService")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdentityService interopIdentityService(
            @Qualifier("interopSelfcareIdentityService") IdentityService identityService,
            M2MTokenService m2mService
    ) {
        return new IdentityServiceInteropImpl(identityService, m2mService);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DPoPVoucherService dPopVoucherService(
            @Autowired RestTemplate restTemplate
    ) {
        return new DPoPVoucherService(restTemplate);
    }


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DpopProofService dpopProofService() {
        return new DpopProofService();
    }

    @Bean
    public AsyncAuthApi asyncAuthApi(
            RestTemplate restTemplate,
            @Value("${authorization.server.token-async.creation.url}")
            String basePath
    ) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return new AsyncAuthApi(apiClient);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DPoPTokenService m2mDpopTokenService(
            @Qualifier("interopSelfcareIdentityService") IdentityService identityService,
            DPoPVoucherService dPopVoucherService,
            DpopProofService dpopProofService,
            HttpCallExecutor httpCallExecutor
    ) {
        return new DPoPTokenService(identityService, dPopVoucherService, dpopProofService, httpCallExecutor);
    }

}