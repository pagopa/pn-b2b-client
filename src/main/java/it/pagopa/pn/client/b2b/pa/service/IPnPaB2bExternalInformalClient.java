package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalPreLoadRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalPreLoadResponse;

import java.util.List;

    public interface IPnPaB2bExternalInformalClient {

        List<InformalPreLoadResponse> informalPresignedUploadRequest(String apiKey, List<InformalPreLoadRequest> requests);
    }

