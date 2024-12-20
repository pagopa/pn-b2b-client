package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.tenant.service.ITenantsApi;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientTokenConfigurator {
    private IAuthorizationClient authorizationClient;
    private IAgreementClient agreementClient;
    private IAttributeApiClient attributeApiClient;
    private ITenantsApi tenantsApi;
    private IEServiceClient eServiceClient;
    private IProducerClient producerClient;
    private IPurposeApiClient purposeApiClient;
    private IProducerDelegationsApiClient delegationsApiClient;
    private IDelegationApiClient delegationApiClient;

    public void setBearerToken(String token) {
        authorizationClient.setBearerToken(token);
        agreementClient.setBearerToken(token);
        attributeApiClient.setBearerToken(token);
        tenantsApi.setBearerToken(token);
        eServiceClient.setBearerToken(token);
        producerClient.setBearerToken(token);
        purposeApiClient.setBearerToken(token);
        delegationsApiClient.setBearerToken(token);
        delegationApiClient.setBearerToken(token);
    }

}
