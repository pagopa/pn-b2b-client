package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.agreement.service.IM2MAgreementClient;
import it.pagopa.interop.agreement.service.IM2MClientsClient;
import it.pagopa.interop.agreement.service.IM2MTenantClient;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.attribute.service.IM2MCertifiedAttributeClient;
import it.pagopa.interop.attribute.service.IM2MDeclaredAttributeClient;
import it.pagopa.interop.attribute.service.IM2MVerifiedAttributeClient;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.delegate.service.IConsumerDelegationsApiClient;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IM2MDelegationClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateAttributeClient;
import it.pagopa.interop.eservice.service.IM2MEServiceAttributeClient;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.event.service.IM2MEventClient;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import it.pagopa.interop.purpose.service.IM2MPurposeTemplateClient;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import it.pagopa.interop.selfcare.service.ISelfcareClient;
import it.pagopa.interop.tenant.service.ITenantsApi;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@ScenarioScope
@RequiredArgsConstructor
public class ClientTokenConfigurator {
    private String lastToken;

    private final IAuthorizationClient authorizationClient;
    private final IAgreementClient agreementClient;
    private final IAttributeApiClient attributeApiClient;
    private final ITenantsApi tenantsApi;
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
    private final ISelfcareClient iSelfcareClient;
    private final IPurposeTemplateClient purposeTemplateClient;
    private final IM2MPurposeTemplateClient m2mPurposeTemplateClient;

    public void setBearerToken(String token) {
        this.lastToken = token;

        authorizationClient.setBearerToken(token);
        agreementClient.setBearerToken(token);
        attributeApiClient.setBearerToken(token);
        tenantsApi.setBearerToken(token);
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
    }

}
