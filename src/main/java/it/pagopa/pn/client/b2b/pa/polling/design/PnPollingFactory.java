package it.pagopa.pn.client.b2b.pa.polling.design;

import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponse;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.Map;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingFactory implements SettableApiKey {
    private final Map<String, IPnPollingService<?>> pollingServiceMap;
    private String apiKey;
    private ApiKeyType apiKeyType;

    public PnPollingFactory(Map<String, IPnPollingService<?>> pollingServiceMap) {
        this.pollingServiceMap = pollingServiceMap;
    }

    public IPnPollingService<?> getPollingService(String pollingType) {
        IPnPollingService<?> iPnPollingService = pollingServiceMap.get(pollingType);
        if (iPnPollingService == null) {
            throw new RuntimeException("Unsupported IPnPollingService type");
        }
        if(apiKeyType != null){
            iPnPollingService.setApiKeys(apiKeyType);
        }else if(apiKey != null){
            iPnPollingService.setApiKey(apiKey);
        }

        return iPnPollingService;
    }

    public void execute(String pollingType, String iun, PnPollingParameter pnPollingParameter) {
        IPnPollingService<?> iPnPollingService = pollingServiceMap.get(pollingType);
        if(apiKeyType != null){
            iPnPollingService.setApiKeys(apiKeyType);
        }else if(apiKey != null){
            iPnPollingService.setApiKey(apiKey);
        }

        iPnPollingService.waitForEvent(iun, pnPollingParameter);
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