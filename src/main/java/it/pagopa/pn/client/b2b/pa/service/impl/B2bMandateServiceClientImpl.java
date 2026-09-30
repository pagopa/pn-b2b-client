package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.AcceptRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.CxTypeAuthFleet;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.MandateCountsDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.MandateDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.UpdateRequestDto;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Router tra le due implementazioni di IPnWebMandateClient disponibili nel flusso @useB2B:
 * le utenze PG dedicate con token _B2B (PG_B2B_1/PG_B2B_2) usano {@link MandateB2BExternalClientImpl}
 * (openapi pn-mandate-b2b-pg-external); ogni altra utenza (PF o PG classica) usa
 * {@link MandateInternalClientImpl} (openapi pn-mandate-internal). La scelta avviene una sola
 * volta in {@link #setBearerToken(BearerTokenType)}, cosi' i metodi di business si limitano a
 * inoltrare la chiamata all'implementazione attiva.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class B2bMandateServiceClientImpl implements IPnWebMandateClient {
    private final MandateInternalClientImpl internalClient;
    private final MandateB2BExternalClientImpl b2bExternalClient;
    private IPnWebMandateClient activeClient;

    public B2bMandateServiceClientImpl(MandateInternalClientImpl internalClient, MandateB2BExternalClientImpl b2bExternalClient) {
        this.internalClient = internalClient;
        this.b2bExternalClient = b2bExternalClient;
        this.activeClient = internalClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        this.activeClient = isB2BUtenza(bearerToken) ? b2bExternalClient : internalClient;
        return activeClient.setBearerToken(bearerToken);
    }

    private static boolean isB2BUtenza(BearerTokenType bearerToken) {
        return bearerToken == BearerTokenType.PG_B2B_1 || bearerToken == BearerTokenType.PG_B2B_2;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return activeClient.getBearerTokenSetted();
    }

    @Override
    public void acceptMandate(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException {
        activeClient.acceptMandate(mandateId, acceptRequestDto);
    }

    @Override
    public MandateCountsDto countMandatesByDelegate(String status) throws RestClientException {
        return activeClient.countMandatesByDelegate(status);
    }

    @Override
    public void createMandate(MandateDto mandateDto) throws RestClientException {
        activeClient.createMandate(mandateDto);
    }

    @Override
    public void updateMandate(String mandateId, UpdateRequestDto updateRequestDto) throws RestClientException {
        activeClient.updateMandate(mandateId, updateRequestDto);
    }

    @Override
    public void updateMandate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, String mandateId, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, UpdateRequestDto updateRequestDto) throws RestClientException {
        activeClient.updateMandate(xPagopaPnCxId, xPagopaPnCxType, mandateId, xPagopaPnCxGroups, xPagopaPnCxRole, updateRequestDto);
    }

    @Override
    public List<MandateDto> listMandatesByDelegate1(String status) throws RestClientException {
        return activeClient.listMandatesByDelegate1(status);
    }

    @Override
    public List<MandateDto> listMandatesByDelegator1() throws RestClientException {
        return activeClient.listMandatesByDelegator1();
    }

    @Override
    public void rejectMandate(String mandateId) throws RestClientException {
        activeClient.rejectMandate(mandateId);
    }

    @Override
    public void revokeMandate(String mandateId) throws RestClientException {
        activeClient.revokeMandate(mandateId);
    }

    @Override
    public List<MandateDto> searchMandatesByDelegate(String taxId, List<String> groups) throws RestClientException {
        return activeClient.searchMandatesByDelegate(taxId, groups);
    }

    @Override
    public List<MandateDto> searchMandatesByDelegateStatusFilter(String taxId, List<String> status, List<String> groups) throws RestClientException {
        return activeClient.searchMandatesByDelegateStatusFilter(taxId, status, groups);
    }
}
