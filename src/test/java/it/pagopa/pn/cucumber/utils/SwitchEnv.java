package it.pagopa.pn.cucumber.utils;


import io.cucumber.java.Before;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.PnAppIOB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.PnWebMandateExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.PnWebRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.PnWebhookB2bExternalClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;



public class SwitchEnv {

    @Value("${pn.execution-type}")
    private String executionType;

    @Value("${pn.external.api-keys.pagopa-env}")
    private String apiKeysEnv;

    @Value("${pn.external.api-keys.pagopa-env-2}")
    private String apiKeysTwoEnv;

    @Value("${pn.external.api-keys.pagopa-GA-env}")
    private String apiKeysGAEnv;

    @Value("${pn.external.api.keys.appio.pagopa.env}")
    private String apiKeysAppIOEnv;

    @Value("${pn.external.bearer-token-user2.pagopa-env}")
    private String bearerTokenCristoforoCEnv;

    @Value("${pn.external.bearer-token-user1.pagopa-env}")
    private String bearerTokenFieramoscaEEnv;

    @Autowired
    ApplicationContext applicationContext;


    private static boolean setted = false;

    @Before
    public synchronized void before_all() {

        if(executionType!= null  && !setted && checkExecutionType()){
            synchronized (this){
                setted = true;
            }
            System.setProperty("pn.external.base-url","https://api."+executionType+".pn.pagopa.it");
            System.setProperty("pn.webapi.external.base-url","https://webapi."+executionType+".pn.pagopa.it");
            System.setProperty("pn.appio.externa.base-url","https://api-io."+executionType+".pn.pagopa.it");

            System.setProperty("pn.external.api-key", apiKeysEnv);
            System.setProperty("pn.external.api-key-2", apiKeysTwoEnv);
            System.setProperty("pn.external.api-key-GA", apiKeysGAEnv);
            System.setProperty("pn.external.appio.api-key", apiKeysAppIOEnv);
            System.setProperty("pn.external.bearer-token-user2.pagopa", bearerTokenCristoforoCEnv);
            System.setProperty("pn.external.bearer-token-user1.pagopa", bearerTokenFieramoscaEEnv);

            switchBean();

        }

    }

    private boolean checkExecutionType(){
        boolean checked = false;
        switch(executionType){
            case "svil":
                checked = true;
                break;
            case "coll":
                checked = true;
                break;
            case "hotfix":
                checked = true;
                break;
            default:
                return false;
        }
        return checked;
    }

    private void switchBean(){
        BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();

        beanRegistry.removeBeanDefinition("pnWebhookB2bExternalClientImpl");
        beanRegistry.registerBeanDefinition("pnWebhookB2bExternalClientImpl", BeanDefinitionBuilder.rootBeanDefinition(PnWebhookB2bExternalClientImpl.class).getBeanDefinition());

        beanRegistry.removeBeanDefinition("pnWebMandateExternalClientImpl");
        beanRegistry.registerBeanDefinition("pnWebMandateExternalClientImpl", BeanDefinitionBuilder.rootBeanDefinition(PnWebMandateExternalClientImpl.class).getBeanDefinition());

        beanRegistry.removeBeanDefinition("pnWebRecipientExternalClientImpl");
        beanRegistry.registerBeanDefinition("pnWebRecipientExternalClientImpl", BeanDefinitionBuilder.rootBeanDefinition(PnWebRecipientExternalClientImpl.class).getBeanDefinition());

        beanRegistry.removeBeanDefinition("pnPaB2bExternalClientImpl");
        beanRegistry.registerBeanDefinition("pnPaB2bExternalClientImpl", BeanDefinitionBuilder.rootBeanDefinition(PnPaB2bExternalClientImpl.class).getBeanDefinition());

        beanRegistry.removeBeanDefinition("pnAppIOB2bExternalClientImpl");
        beanRegistry.registerBeanDefinition("pnAppIOB2bExternalClientImpl", BeanDefinitionBuilder.rootBeanDefinition(PnAppIOB2bExternalClientImpl.class).getBeanDefinition());

        beanRegistry.removeBeanDefinition("pnPaB2bExternalClientImpl");
        beanRegistry.registerBeanDefinition("pnPaB2bExternalClientImpl", BeanDefinitionBuilder.rootBeanDefinition(PnPaB2bExternalClientImpl.class).getBeanDefinition());

    }


}
