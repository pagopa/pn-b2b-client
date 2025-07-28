package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.agreement.service.IM2MAgreementClient;
import it.pagopa.interop.agreement.service.IM2MClientsClient;
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
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.eservice_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.tenant.service.ITenantsApi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
@ScenarioScope
@AllArgsConstructor
public class ClientTokenConfigurator {
    private IAuthorizationClient authorizationClient;
    private IAgreementClient agreementClient;
    private IAttributeApiClient attributeApiClient;
    private ITenantsApi tenantsApi;
    private IEServiceClient eServiceClient;
    private IEServiceTemplateClient eServiceTemplateClient;
    private IProducerClient producerClient;
    private IPurposeApiClient purposeApiClient;
    private IProducerDelegationsApiClient producerDelegationsApiClient;
    private IConsumerDelegationsApiClient consumerDelegationsApiClient;
    private IDelegationApiClient delegationApiClient;
    private IM2MAgreementClient m2mAgreementClient;
    private IM2MCertifiedAttributeClient m2mCertifiedAttributeClient;
    private IM2MDeclaredAttributeClient m2mDeclaredAttributeClient;
    private IM2MVerifiedAttributeClient m2mVerifiedAttributeClient;
    private IM2MEserviceClient m2meServiceClient;
    private IM2MPurposeClient m2mPurposeClient;
    private IM2MEServiceTemplateClient m2mEServiceTemplateClient;
    private IM2MEserviceDescriptorClient m2mEServiceDescriptorClient;
    private IM2MDelegationClient m2mDelegationClient;
    private IM2MClientsClient m2MClientsClient;

    public void setBearerToken(String token) {
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
    }

}
