package it.pagopa.pn.interop.cucumber.steps.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.conf.api_profile.ApiProfile;
import it.pagopa.interop.notification.cache.NotificationCacheImpl;
import it.pagopa.interop.conf.api_profile.ApiProfileConfig;
import it.pagopa.interop.conf.api_profile.ApiProfileContext;
import it.pagopa.pn.interop.cucumber.steps.authorization.model.VoucherContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.event.model.EventContext;
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
        return ApiProfile.from(config);
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

    @Bean
    @ScenarioScope
    public EventContext eventContext() {
        return new EventContext();
    }

    @Bean
    @ScenarioScope
    public VoucherContext voucherContext() {
        return new VoucherContext();
    }
}
