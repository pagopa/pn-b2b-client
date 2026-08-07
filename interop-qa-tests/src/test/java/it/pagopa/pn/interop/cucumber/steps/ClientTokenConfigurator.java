package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.APIUnavailableException;
import it.pagopa.interop.agreement.service.*;
import it.pagopa.interop.attribute.service.*;
import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.conf.api_profile.ApiProfile;
import it.pagopa.interop.conf.api_profile.ApiProfile.ApiM2MVersion;
import it.pagopa.interop.conf.api_profile.ApiProfile.ApiMode;
import it.pagopa.interop.delegate.service.*;
import it.pagopa.interop.dev_tools.service.IDevToolsClient;
import it.pagopa.interop.e_service_template.*;
import it.pagopa.interop.eservice.service.*;
import it.pagopa.interop.event.service.IM2MEventClient;
import it.pagopa.interop.event.service.IM2MV3EventClient;
import it.pagopa.interop.notification.INotificationClient;
import it.pagopa.interop.notification.INotificationConfigClient;
import it.pagopa.interop.producer_keychains.IM2MV3ProducerKeychainsClient;
import it.pagopa.interop.producerkeychain.ProducerKeychainClient;
import it.pagopa.interop.purpose.service.*;
import it.pagopa.interop.selfcare.service.ISelfcareClient;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.tenant.service.ITenantsProcessApi;
import it.pagopa.interop.users.IM2MV3UsersClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

@Getter
@Component
@ScenarioScope
@RequiredArgsConstructor
public class ClientTokenConfigurator {
    private String lastToken;

    // DEV. NOTE al momento non si pone il campo "final" per non interferire con
    // @RequiredArgsConstructor, che lo inserirebbe nei parametri del costruttore
    private Map<Class<?>, Object> proxies = new HashMap<>();

    private final ApiProfile apiProfile;

    private final IAuthorizationClient authorizationClient;
    private final IAgreementClient agreementClient;
    private final IAttributeApiClient attributeApiClient;
    private final ITenantsApi tenantsApi;
    private final ITenantsProcessApi tenantsProcessApi;
    private final IEServiceClient eServiceClient;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IProducerClient producerClient;
    private final IPurposeApiClient purposeApiClient;
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final IConsumerDelegationsApiClient consumerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final IM2MAgreementClient m2mAgreementClient;
    private final IM2MCertifiedAttributeClient m2mCertifiedAttributeClient;
    private final IM2MDeclaredAttributeClient m2mDeclaredAttributeClient;
    private final IM2MVerifiedAttributeClient m2mVerifiedAttributeClient;
    private final IM2MEserviceClient m2meServiceClient;
    private final IM2MPurposeClient m2mPurposeClient;
    private final IM2MEServiceTemplateClient m2mEServiceTemplateClient;
    private final IM2MEserviceDescriptorClient m2mEServiceDescriptorClient;
    private final IM2MDelegationClient m2mDelegationClient;
    private final IM2MClientsClient m2MClientsClient;
    private final IM2MTenantClient m2mTenantClient;
    private final IM2MEServiceAttributeClient m2mEServiceAttributeClient;
    private final IM2MEServiceTemplateAttributeClient m2mEServiceTemplateAttributeClient;
    private final IM2MEventClient m2mEventClient;
    private final IM2MPurposeTemplateClient m2mPurposeTemplateClient;
    private final ISelfcareClient iSelfcareClient;
    private final IPurposeTemplateClient purposeTemplateClient;
    private final INotificationClient notificationClient;
    private final INotificationConfigClient notificationConfigClient;
    private final ProducerKeychainClient producerKeychainClient;
    private final IDevToolsClient devToolsClient;

    // Clients M2M API v3
    private final IM2MV3AgreementClient m2mV3AgreementClient;
    private final IM2MV3ClientsClient m2mV3ClientsClient;
    private final IM2MV3TenantClient m2mV3TenantClient;
    private final IM2MV3CertifiedAttributeClient m2mV3CertifiedAttributeClient;
    private final IM2MV3CertifiedDiscreteAttributeClient m2mV3CertifiedDiscreteAttributeClient;
    private final IM2MV3DeclaredAttributeClient m2mV3DeclaredAttributeClient;
    private final IM2MV3VerifiedAttributeClient m2mV3VerifiedAttributeClient;
    private final IM2MV3DelegationClient m2mV3DelegationClient;
    private final IM2MV3EServiceTemplateAttributeClient m2mV3EServiceTemplateAttributeClient;
    private final IM2MV3EServiceTemplateClient m2mV3EServiceTemplateClient;
    private final IM2MV3EServiceAttributeClient m2mV3EServiceAttributeClient;
    private final IM2MV3EserviceClient m2mV3EserviceClient;
    private final IM2MV3EserviceDescriptorClient m2mV3EserviceDescriptorClient;
    private final IM2MV3EventClient m2mV3EventClient;
    private final IM2MV3ProducerKeychainsClient m2mV3ProducerKeychainsClient;
    private final IM2MV3PurposeClient m2mV3PurposeClient;
    private final IM2MV3PurposeTemplateClient m2mV3PurposeTemplateClient;
    private final IM2MV3UsersClient m2mV3UsersClient;

    @PostConstruct
    public void init() {
        // Area Agreement & Tenant
        registerProxy(IM2MAgreementClient.class, m2mAgreementClient, m2mV3AgreementClient);
        registerProxy(IM2MTenantClient.class, m2mTenantClient, m2mV3TenantClient);
        registerProxy(IM2MDelegationClient.class, m2mDelegationClient, m2mV3DelegationClient);

        // Area Attributes
        registerProxy(IM2MCertifiedAttributeClient.class, m2mCertifiedAttributeClient, m2mV3CertifiedAttributeClient);
        registerProxy(IM2MV3CertifiedDiscreteAttributeClient.class, null, m2mV3CertifiedDiscreteAttributeClient);
        registerProxy(IM2MDeclaredAttributeClient.class, m2mDeclaredAttributeClient, m2mV3DeclaredAttributeClient);
        registerProxy(IM2MVerifiedAttributeClient.class, m2mVerifiedAttributeClient, m2mV3VerifiedAttributeClient);
        registerProxy(IM2MEServiceAttributeClient.class, m2mEServiceAttributeClient, m2mV3EServiceAttributeClient);
        registerProxy(IM2MEServiceTemplateAttributeClient.class, m2mEServiceTemplateAttributeClient, m2mV3EServiceTemplateAttributeClient);

        // Area E-Service & Descriptor
        registerProxy(IM2MEserviceClient.class, m2meServiceClient, m2mV3EserviceClient);
        registerProxy(IM2MEserviceDescriptorClient.class, m2mEServiceDescriptorClient, m2mV3EserviceDescriptorClient);
        registerProxy(IM2MEServiceTemplateClient.class, m2mEServiceTemplateClient, m2mV3EServiceTemplateClient);

        // Area Purpose, Clients & Events
        registerProxy(IM2MPurposeClient.class, m2mPurposeClient, m2mV3PurposeClient);
        registerProxy(IM2MPurposeTemplateClient.class, m2mPurposeTemplateClient, m2mV3PurposeTemplateClient);
        registerProxy(IM2MClientsClient.class, m2MClientsClient, m2mV3ClientsClient);
        registerProxy(IM2MEventClient.class, m2mEventClient, m2mV3EventClient);
    }

    public IM2MAgreementClient getM2mAgreementClient() {
        return getProxy(IM2MAgreementClient.class);
    }

    public IM2MTenantClient getM2mTenantClient() {
        return getProxy(IM2MTenantClient.class);
    }

    public IM2MDelegationClient getM2mDelegationClient() {
        return getProxy(IM2MDelegationClient.class);
    }

    public IM2MCertifiedAttributeClient getM2mCertifiedAttributeClient() {
        return getProxy(IM2MCertifiedAttributeClient.class);
    }

    public IM2MDeclaredAttributeClient getM2mDeclaredAttributeClient() {
        return getProxy(IM2MDeclaredAttributeClient.class);
    }

    public IM2MVerifiedAttributeClient getM2mVerifiedAttributeClient() {
        return getProxy(IM2MVerifiedAttributeClient.class);
    }

    public IM2MEServiceAttributeClient getM2mEServiceAttributeClient() {
        return getProxy(IM2MEServiceAttributeClient.class);
    }

    public IM2MEServiceTemplateAttributeClient getM2mEServiceTemplateAttributeClient() {
        return getProxy(IM2MEServiceTemplateAttributeClient.class);
    }

    public IM2MEserviceClient getM2meServiceClient() {
        return getProxy(IM2MEserviceClient.class);
    }

    public IM2MEserviceDescriptorClient getM2mEServiceDescriptorClient() {
        return getProxy(IM2MEserviceDescriptorClient.class);
    }

    public IM2MEServiceTemplateClient getM2mEServiceTemplateClient() {
        return getProxy(IM2MEServiceTemplateClient.class);
    }

    public IM2MPurposeClient getM2mPurposeClient() {
        return getProxy(IM2MPurposeClient.class);
    }

    public IM2MPurposeTemplateClient getM2mPurposeTemplateClient() {
        return getProxy(IM2MPurposeTemplateClient.class);
    }

    public IM2MClientsClient getM2MClientsClient() {
        return getProxy(IM2MClientsClient.class);
    }

    public IM2MEventClient getM2mEventClient() {
        return getProxy(IM2MEventClient.class);
    }

    public void setBearerToken(String token) {
        this.lastToken = token;

        authorizationClient.setBearerToken(token);
        agreementClient.setBearerToken(token);
        attributeApiClient.setBearerToken(token);
        tenantsApi.setBearerToken(token);
        tenantsProcessApi.setBearerToken(token);
        eServiceClient.setBearerToken(token);
        eServiceTemplateClient.setBearerToken(token);
        producerClient.setBearerToken(token);
        purposeApiClient.setBearerToken(token);
        producerDelegationsApiClient.setBearerToken(token);
        consumerDelegationsApiClient.setBearerToken(token);
        delegationApiClient.setBearerToken(token);
        m2mAgreementClient.setBearerToken(token);
        m2mCertifiedAttributeClient.setBearerToken(token);
        m2mDeclaredAttributeClient.setBearerToken(token);
        m2mVerifiedAttributeClient.setBearerToken(token);
        m2meServiceClient.setBearerToken(token);
        m2mPurposeClient.setBearerToken(token);
        m2mEServiceTemplateClient.setBearerToken(token);
        m2mEServiceDescriptorClient.setBearerToken(token);
        m2mDelegationClient.setBearerToken(token);
        m2MClientsClient.setBearerToken(token);
        m2mTenantClient.setBearerToken(token);
        m2mEServiceAttributeClient.setBearerToken(token);
        m2mEServiceTemplateAttributeClient.setBearerToken(token);
        m2mEventClient.setBearerToken(token);
        iSelfcareClient.setBearerToken(token);
        purposeTemplateClient.setBearerToken(token);
        m2mPurposeTemplateClient.setBearerToken(token);
        notificationClient.setBearerToken(token);
        notificationConfigClient.setBearerToken(token);
        producerKeychainClient.setBearerToken(token);
        devToolsClient.setBearerToken(token);
    }

    public void setAuth(Auth auth) {
        m2mV3ProducerKeychainsClient.setAuth(auth);
        m2mV3AgreementClient.setAuth(auth);
        m2mV3TenantClient.setAuth(auth);
        m2mV3DelegationClient.setAuth(auth);
        m2mV3CertifiedAttributeClient.setAuth(auth);
        m2mV3CertifiedDiscreteAttributeClient.setAuth(auth);
        m2mV3DeclaredAttributeClient.setAuth(auth);
        m2mV3VerifiedAttributeClient.setAuth(auth);
        m2mV3EServiceAttributeClient.setAuth(auth);
        m2mV3EServiceTemplateAttributeClient.setAuth(auth);
        m2mV3EserviceClient.setAuth(auth);
        m2mV3EserviceDescriptorClient.setAuth(auth);
        m2mV3EServiceTemplateClient.setAuth(auth);
        m2mV3PurposeClient.setAuth(auth);
        m2mV3PurposeTemplateClient.setAuth(auth);
        m2mV3ClientsClient.setAuth(auth);
        m2mV3EventClient.setAuth(auth);
        m2mV3ProducerKeychainsClient.setAuth(auth);
        m2mV3UsersClient.setAuth(auth);
    }

    @SuppressWarnings("unchecked")
    private <T, U extends T, V extends T> T makeClientProxy(Class<T> clientInterface, U clientV2, V clientV3) {
        InvocationHandler handler = (proxy, method, args) -> {
            ApiM2MVersion targetVersion = apiProfile.getApiM2MVersion();
            boolean isBestFit = apiProfile.getApiMode() == ApiMode.BEST_FIT;

            T primary = (targetVersion == ApiProfile.ApiM2MVersion.V2) ? clientV2 : clientV3;
            T secondary = (targetVersion == ApiProfile.ApiM2MVersion.V2) ? clientV3 : clientV2;

            try {
                return invokeUnwrapped(method, primary, args);
            } catch (APIUnavailableException e) {
                if (isBestFit) {
                    return invokeUnwrapped(method, secondary, args);
                }
                throw e;
            }
        };

        return (T) Proxy.newProxyInstance(
                clientInterface.getClassLoader(),
                new Class<?>[]{clientInterface},
                handler
        );
    }

    private Object invokeUnwrapped(Method method, Object target, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ite) {
            // rilancia la vera eccezione del target
            throw ite.getCause();
        }
    }

    /**
     * Helper per la registrazione dei proxy nella mappa
     */
    private <T, U extends T, V extends T> void registerProxy(Class<T> interfaceClass, U v2, V v3) {
        proxies.put(interfaceClass, makeClientProxy(interfaceClass, v2, v3));
    }

    /**
     * Helper per il recupero tipizzato dei proxy
     */
    @SuppressWarnings("unchecked")
    private <T> T getProxy(Class<T> interfaceClass) {
        return (T) proxies.get(interfaceClass);
    }

}
