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

import java.util.Objects;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingFactory implements SettableApiKey {
    private final ApplicationContext context;
    private String apiKey;
    private ApiKeyType apiKeyType;


    public PnPollingFactory(ApplicationContext context) {
        this.context = context;
    }

    public IPnPollingService<?> getPollingService(PnPollingType pollingType) {
        Objects.requireNonNull(pollingType, "pollingType is required");
        return resolve(pollingType.getServiceName());
    }

    /** @deprecated usare {@link #getPollingService(PnPollingType)} */
    @Deprecated
    public IPnPollingService<?> getPollingService(String pollingType) {
        return resolve(pollingType);
    }

    public void execute(PnPollingType pollingType, String iun, PnPollingParameter pnPollingParameter) {
        getPollingService(pollingType).waitForEvent(iun, pnPollingParameter);
    }

    /** @deprecated usare {@link #execute(PnPollingType, String, PnPollingParameter)} */
    @Deprecated
    public void execute(String pollingType, String iun, PnPollingParameter pnPollingParameter) {
        resolve(pollingType).waitForEvent(iun, pnPollingParameter);
    }

    private IPnPollingService<?> resolve(String serviceName) {
        try{
            IPnPollingService<?> iPnPollingService = context.getBean(serviceName, IPnPollingService.class);
            if(apiKeyType != null){
                iPnPollingService.setApiKeys(apiKeyType);
            }else if(apiKey != null){
                iPnPollingService.setApiKey(apiKey);
            }

            return iPnPollingService;
        }catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException){
            throw new PnPollingException("Unsupported IPnPollingService type: " + serviceName);
        }
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