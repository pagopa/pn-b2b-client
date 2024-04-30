package it.pagopa.pn.client.b2b.pa.polling.design;

import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingFactory implements SettableApiKey {
    private final ApplicationContext context;
    private String apiKey;
    private ApiKeyType apiKeyType;


    public PnPollingFactory(ApplicationContext context) {
        this.context = context;
    }

    public IPnPollingService<?> getPollingService(String pollingType) {
        try{
            IPnPollingService<?> iPnPollingService = context.getBean(pollingType, IPnPollingService.class);
            if(apiKeyType != null){
                iPnPollingService.setApiKeys(apiKeyType);
            }else if(apiKey != null){
                iPnPollingService.setApiKey(apiKey);
            }

            return iPnPollingService;
        }catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException){
            throw new PnPollingException("Unsupported IPnPollingService type");
        }
    }

    public void execute(String pollingType, String iun, PnPollingParameter pnPollingParameter) {
        IPnPollingService<?> iPnPollingService = getPollingService(pollingType);
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