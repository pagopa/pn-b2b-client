package it.pagopa.pn.interop.cucumber.steps.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.config.springconfig.springconfig.ApiProfile;
import it.pagopa.interop.notification.cache.NotificationCacheImpl;
import it.pagopa.pn.interop.cucumber.ApiProfileConfig;
import it.pagopa.pn.interop.cucumber.ApiProfileContext;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;
import it.pagopa.pn.interop.cucumber.steps.selfcare.model.TenantContext;
import it.pagopa.pn.interop.cucumber.utility.FeatureLifecycleManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CucumberScopedBeans {

    @Bean
    @ScenarioScope
    public ApiProfile apiProfile() {
        ApiProfileConfig config = ApiProfileContext.getRequired();
        return ApiProfile.from(
                config.getApiMode(),
                config.getApiM2mVersion(),
                config.getApiBffVersion(),
                config.getApiSet()
        );
    }

    @Bean
    public NotificationCacheImpl notificationCache() {
        return new NotificationCacheImpl();
    }

    @Bean
    public FeatureLifecycleManager notificationFeatureLifecycleManager() {
        return new FeatureLifecycleManager();
    }

    @Bean
    @ScenarioScope
    public ProducerKeychainsContext producerKeychainsContext() {
        return new ProducerKeychainsContext();
    }

    @Bean
    @ScenarioScope
    public TenantContext tenantContext() {
        return new TenantContext();
    }
}
