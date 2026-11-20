package it.pagopa.pn.client.b2b.pa.polling.design;

import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingFactory implements SettableApiKey {
    private final ApplicationContext context;
    private final PnPollingServiceRegistry registry;
    private String apiKey;
    private ApiKeyType apiKeyType;


    public PnPollingFactory(ApplicationContext context, PnPollingServiceRegistry registry) {
        this.context = context;
        this.registry = registry;
    }

    public IPnPollingService<?> getPollingService(PnPollingType pollingType) {
        Objects.requireNonNull(pollingType, "pollingType is required");
        String beanName = registry.getBeanName(pollingType)
                .orElseThrow(() -> new PnPollingException("Unsupported IPnPollingService type: " + pollingType));
        IPnPollingService<?> iPnPollingService = context.getBean(beanName, IPnPollingService.class);
        if(apiKeyType != null){
            iPnPollingService.setApiKeys(apiKeyType);
        }else if(apiKey != null){
            iPnPollingService.setApiKey(apiKey);
        }
        return iPnPollingService;
    }

    public void execute(PnPollingType pollingType, String iun, PnPollingParameter pnPollingParameter) {
        getPollingService(pollingType).waitForEvent(iun, pnPollingParameter);
    }


    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        this.apiKeyType = apiKey;
        return true;
    }

    @Override
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        //if null
        return apiKeyType;
    }
}