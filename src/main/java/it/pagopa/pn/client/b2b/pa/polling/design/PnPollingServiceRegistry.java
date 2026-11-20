package it.pagopa.pn.client.b2b.pa.polling.design;

import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Associa ogni {@link PnPollingType} al bean del service annotato con {@link PnPollingService}.
 */
@Component
public class PnPollingServiceRegistry {
    private final Map<PnPollingType, String> beanNames;

    public PnPollingServiceRegistry(ListableBeanFactory beanFactory) {
        this.beanNames = Collections.unmodifiableMap(indexBeanNames(beanFactory));
    }

    public Optional<String> getBeanName(PnPollingType pollingType) {
        return Optional.ofNullable(beanNames.get(pollingType));
    }

    private static Map<PnPollingType, String> indexBeanNames(ListableBeanFactory beanFactory) {
        Map<PnPollingType, String> index = new EnumMap<>(PnPollingType.class);
        for (String beanName : beanFactory.getBeanNamesForType(IPnPollingService.class, true, false)) {
            PnPollingService pollingService = beanFactory.findAnnotationOnBean(beanName, PnPollingService.class);
            if (pollingService == null) {
                throw new IllegalStateException("Service di polling senza @PnPollingService: " + beanName);
            }
            String previous = index.putIfAbsent(pollingService.value(), beanName);
            if (previous != null) {
                throw new IllegalStateException("PnPollingType " + pollingService.value()
                        + " dichiarato sia da " + previous + " sia da " + beanName);
            }
        }
        return index;
    }
}
