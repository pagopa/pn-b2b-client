package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.delegate.service.IDelegationsApiClient;
import it.pagopa.interop.purpose.RiskAnalysisDataInitializer;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.tenant.service.ITenantsApi;

public class ClientTokenConfigurator {
    private IAuthorizationClient authorizationClient;
    private IAgreementClient agreementClient;
    private IAttributeApiClient attributeApiClient;
    private ITenantsApi tenantsApi;
    private IEServiceClient eServiceClient;
    private IProducerClient producerClient;
    private IPurposeApiClient purposeApiClient;
    private IDelegationsApiClient delegationsApiClient;

    public ClientTokenConfigurator(IAuthorizationClient authorizationClient,
                                   IAgreementClient agreementClient,
                                   IAttributeApiClient attributeApiClient,
                                   ITenantsApi tenantsApi,
                                   IEServiceClient eServiceClient,
                                   IProducerClient producerClient,
                                   IPurposeApiClient purposeApiClient,
                                   IDelegationsApiClient delegationsApiClient) {
        this.authorizationClient = authorizationClient;
        this.agreementClient = agreementClient;
        this.attributeApiClient = attributeApiClient;
        this.tenantsApi = tenantsApi;
        this.eServiceClient = eServiceClient;
        this.producerClient = producerClient;
        this.purposeApiClient = purposeApiClient;
        this.delegationsApiClient = delegationsApiClient;
    }

    public void setBearerToken(String token) {
        authorizationClient.setBearerToken(token);
        agreementClient.setBearerToken(token);
        attributeApiClient.setBearerToken(token);
        tenantsApi.setBearerToken(token);
        eServiceClient.setBearerToken(token);
        producerClient.setBearerToken(token);
        purposeApiClient.setBearerToken(token);
        delegationsApiClient.setBearerToken(token);
    }

}
