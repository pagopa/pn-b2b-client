package it.pagopa.pn.cucumber.utils;


import io.cucumber.java.Before;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
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

    @Value("${pn.external.api-keys.pagopa-svil}")
    private String apiKeysSvil;

    @Value("${pn.external.api-keys.pagopa-svil-2}")
    private String apiKeysTwoSvil;

    @Value("${pn.external.api.keys.appio.pagopa.svil}")
    private String apiKeysAppIOSvil;

    @Value("${pn.external.bearer-token-user2.pagopa-svil}")
    private String bearerTokenCristoforoCSvil;

    @Value("${pn.external.bearer-token-user1.pagopa-svil}")
    private String bearerTokenFieramoscaESvil;



    @Value("${pn.external.api-keys.pagopa-coll}")
    private String apiKeysColl;

    @Value("${pn.external.api-keys.pagopa-coll-2}")
    private String apiKeysTwoColl;

    @Value("${pn.external.api-keys.pagopa-GA-coll}")
    private String apiKeysGAColl;

    @Value("${pn.external.api.keys.appio.pagopa.coll}")
    private String apiKeysAppIOColl;

    @Value("${pn.external.bearer-token-user2.pagopa-coll}")
    private String bearerTokenCristoforoCColl;

    @Value("${pn.external.bearer-token-user1.pagopa-coll}")
    private String bearerTokenFieramoscaEColl;

    @Autowired
    ApplicationContext applicationContext;


    private static boolean setted = false;

    @Before
    public void before_all() {
        if(executionType!= null  && !setted){
            if(executionType.trim().equalsIgnoreCase("svil")){
                System.setProperty("pn.external.base-url","https://api.svil.pn.pagopa.it");
                System.setProperty("pn.webapi.external.base-url","https://webapi.svil.pn.pagopa.it");
                System.setProperty("pn.appio.externa.base-url","https://api-io.svil.pn.pagopa.it");

                System.setProperty("pn.external.api-key", apiKeysSvil);
                System.setProperty("pn.external.api-key-2", apiKeysTwoSvil);
                System.setProperty("pn.external.appio.api-key", apiKeysAppIOSvil);
                System.setProperty("pn.external.bearer-token-user2.pagopa", bearerTokenCristoforoCSvil);
                System.setProperty("pn.external.bearer-token-user1.pagopa", bearerTokenFieramoscaESvil);

                switchBean();
                setted = true;
            }
            if(executionType.trim().equalsIgnoreCase("coll")){
                System.setProperty("pn.external.base-url","https://api.coll.pn.pagopa.it");
                System.setProperty("pn.webapi.external.base-url","https://webapi.coll.pn.pagopa.it");
                System.setProperty("pn.appio.externa.base-url","https://api-io.coll.pn.pagopa.it");

                System.setProperty("pn.external.api-key", apiKeysColl);
                System.setProperty("pn.external.api-key-2", apiKeysTwoColl);
                System.setProperty("pn.external.api-key-GA", apiKeysGAColl);
                System.setProperty("pn.external.appio.api-key", apiKeysAppIOColl);
                System.setProperty("pn.external.bearer-token-user2.pagopa", bearerTokenCristoforoCColl);
                System.setProperty("pn.external.bearer-token-user1.pagopa", bearerTokenFieramoscaEColl);

                switchBean();
                setted = true;
            }
        }
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

    }


}
