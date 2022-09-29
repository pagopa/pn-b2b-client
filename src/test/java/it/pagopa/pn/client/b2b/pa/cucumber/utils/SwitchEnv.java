package it.pagopa.pn.client.b2b.pa.cucumber.utils;


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
    private String apiKeys;

    @Value("${pn.external.bearer-token-CristoforoC.pagopa-svil}")
    private String bearerTokenCristoforoC;

    @Value("${pn.external.bearer-token-FieramoscaE.pagopa-svil}")
    private String bearerTokenFieramoscaE;

    @Autowired
    ApplicationContext applicationContext;


    private static boolean setted = false;

    @Before
    public void before_all() {
        if((executionType!= null && executionType.trim().equalsIgnoreCase("local")) && !setted){
            System.setProperty("pn.external.base-url","https://api.svil.pn.pagopa.it");
            System.setProperty("pn.external.api-key", apiKeys);
            System.setProperty("pn.webapi.external.base-url","https://webapi.svil.pn.pagopa.it");
            System.setProperty("pn.external.bearer-token-CristoforoC.pagopa",bearerTokenCristoforoC);
            System.setProperty("pn.external.bearer-token-FieramoscaE.pagopa",bearerTokenFieramoscaE);


            BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();

            beanRegistry.removeBeanDefinition("pnWebhookB2bExternalClientImpl");
            beanRegistry.registerBeanDefinition("pnWebhookB2bExternalClientImpl", BeanDefinitionBuilder.rootBeanDefinition(PnWebhookB2bExternalClientImpl.class).getBeanDefinition());

            beanRegistry.removeBeanDefinition("pnWebMandateExternalClientImpl");
            beanRegistry.registerBeanDefinition("pnWebMandateExternalClientImpl", BeanDefinitionBuilder.rootBeanDefinition(PnWebMandateExternalClientImpl.class).getBeanDefinition());

            beanRegistry.removeBeanDefinition("pnWebRecipientExternalClientImpl");
            beanRegistry.registerBeanDefinition("pnWebRecipientExternalClientImpl", BeanDefinitionBuilder.rootBeanDefinition(PnWebRecipientExternalClientImpl.class).getBeanDefinition());

            beanRegistry.removeBeanDefinition("pnPaB2bExternalClientImpl");
            beanRegistry.registerBeanDefinition("pnPaB2bExternalClientImpl", BeanDefinitionBuilder.rootBeanDefinition(PnPaB2bExternalClientImpl.class).getBeanDefinition());

            setted = true;
        }

    }


}
