package it.pagopa.pari.merchant.service.impl;

import it.pagopa.pari.generated.openapi.clients.merchant.root.ApiClient;
import it.pagopa.pari.generated.openapi.clients.merchant.root.api.PointOfSalesApi;
import it.pagopa.pari.generated.openapi.clients.merchant.root.model.ListPointOfSaleDTO;
import it.pagopa.pari.generated.openapi.clients.merchant.root.model.PointOfSaleDTO;
import it.pagopa.pari.merchant.service.IMerchantOperationClient;
import it.pagopa.pari.registrobeni.domain.RdbRole;
import it.pagopa.pari.utils.RdBJWTProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class MerchantOperationClientImpl implements IMerchantOperationClient {
    private final RestTemplate restTemplate;
    private final PointOfSalesApi pointOfSalesApi;
    private final RdBJWTProvider rdBJWTProvider;
    private final String basePath;



    public MerchantOperationClientImpl(RestTemplate restTemplate,
                                       RdBJWTProvider rdBJWTProvider,
                                       @Value("${merchant.base-url}") String basePath) {
        this.restTemplate = restTemplate;
        this.basePath = basePath + "/portal/";
        this.rdBJWTProvider = rdBJWTProvider;
        this.pointOfSalesApi = new PointOfSalesApi(createApiClient("dummy"));

    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return apiClient;
    }

    @Override
    public ListPointOfSaleDTO getPointOfSales(String merchantId, String type, String city, String address, String contactName, Integer page, Integer size, String sort) {
        return pointOfSalesApi.getPointOfSales(merchantId, type, city, address, contactName, page, size, sort);
    }

    public PointOfSaleDTO getPointOfSale(String merchantId, String pointOfSaleId) {
        return pointOfSalesApi.getPointOfSale(merchantId, pointOfSaleId);
    }

    public void putPointOfSales(String merchantId, List<PointOfSaleDTO> pointOfSaleDTO) {
        pointOfSalesApi.putPointOfSales(merchantId, pointOfSaleDTO);
    }

    public void setBearerToken(RdbRole role) {
        String bearerToken = rdBJWTProvider.provideJWT(role);
        pointOfSalesApi.setApiClient(createApiClient(bearerToken));
    }


}
