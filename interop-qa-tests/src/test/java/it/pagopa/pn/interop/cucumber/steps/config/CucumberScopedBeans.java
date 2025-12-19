package it.pagopa.pn.interop.cucumber.steps.config;

import it.pagopa.interop.notification.cache.NotificationCacheImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CucumberScopedBeans {

    @Bean
    public NotificationCacheImpl notificationCacheImpl() {
        return new NotificationCacheImpl();
    }
}
