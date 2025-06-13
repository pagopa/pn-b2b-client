package it.pagopa.interop.config.springconfig.springconfig;

import it.pagopa.interop.authorization.service.DataPreparationService;
import it.pagopa.interop.authorization.service.M2MDPopTokenService;
import it.pagopa.interop.authorization.service.M2MTokenService;
import it.pagopa.interop.authorization.service.factory.InteropTokenFactory;
import it.pagopa.interop.authorization.service.factory.TracingTokenFactory;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.identity.IdentityServiceInteropImpl;
import it.pagopa.interop.authorization.service.identity.IdentityServiceSelfcareImpl;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.DpopProofService;
import it.pagopa.interop.authorization.service.utils.voucher.DPopVoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.conf.InteropClientConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JwtTokenServiceConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TracingTokenFactory tracingTokenFactory(InteropClientConfigs interopClientConfigs, ConfigFileReader configFileReader) {
        return new TracingTokenFactory(interopClientConfigs, configFileReader);
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public InteropTokenFactory interopTokenFactory(InteropClientConfigs interopClientConfigs, ConfigFileReader configFileReader) {
        return new InteropTokenFactory(interopClientConfigs, configFileReader);
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
    public DPopVoucherService dPopVoucherService(
            @Autowired RestTemplate restTemplate
    ) {
        return new DPopVoucherService(restTemplate);
    }


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DpopProofService dpopProofService() {
        return new DpopProofService();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public M2MDPopTokenService m2mDpopTokenService(
            @Qualifier("interopSelfcareIdentityService") IdentityService identityService,
            DataPreparationService dataPreparationService,
            DPopVoucherService dPopVoucherService,
            DpopProofService dpopProofService
    ) {
        return new M2MDPopTokenService(identityService, dataPreparationService, dPopVoucherService, dpopProofService);
    }


}