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
import org.springframework.context.annotation.*;
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

    // 1. Questo bean viene caricato SOLO con "extra-qa" e diventa il PRIMARIO
    @Bean(name = "tracingIdentityService")
    @Profile("extra-qa")
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdentityService tracingIdentityService(TracingTokenFactory tracingTokenFactory, ConfigFileReader configFileReader) {
        return new IdentityServiceSelfcareImpl(tracingTokenFactory, configFileReader);
    }

    // 2. Questo bean viene caricato con "extra-qa" ma NON è primario (lascia la precedenza a quello sopra)
    @Bean(name = "interopSelfcareIdentityService")
    @Profile("extra-qa")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdentityService interopSelfcareIdentityServiceQA(TracingTokenFactory tracingTokenFactory, ConfigFileReader configFileReader) {
        return new IdentityServiceSelfcareImpl(tracingTokenFactory, configFileReader);
    }

    // 3. Questo bean viene caricato se "extra-qa" NON è attivo, e fa da PRIMARIO per tutti gli altri casi
    @Bean(name = "interopSelfcareIdentityService")
    @Profile("!extra-qa")
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdentityService interopSelfcareIdentityServiceDefault(InteropTokenFactory tracingTokenFactory, ConfigFileReader configFileReader) {
        return new IdentityServiceSelfcareImpl(tracingTokenFactory, configFileReader);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public M2MTokenService m2mTokenService(
            IdentityService identityService,
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