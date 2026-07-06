package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.NewInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalPreLoadRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalPreLoadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
    public class PnPaB2bExternalInformalClientImpl {

        private final NewInformalNotificationApi newInformalNotificationApi;

        public PnPaB2bExternalInformalClientImpl(
                RestTemplate restTemplate,
                @Value("${pn.delivery.base-url}") String deliveryBasePath) {

            this.newInformalNotificationApi =
                    new NewInformalNotificationApi(
                            newExternalInformalApiClient(
                                    restTemplate,
                                    deliveryBasePath
                            )
                    );
        }

        private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient
        newExternalInformalApiClient(
                RestTemplate restTemplate,
                String basePath) {

            var client =
                    new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient(
                            restTemplate
                    );

            client.setBasePath(basePath);

            return client;
        }

        public List<InformalPreLoadResponse> informalPresignedUploadRequest(
                List<InformalPreLoadRequest> requests) {

            return newInformalNotificationApi
                    .informalPresignedUploadRequest(requests);
        }
    }

