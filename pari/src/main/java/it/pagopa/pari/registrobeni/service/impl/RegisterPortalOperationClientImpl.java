package it.pagopa.pari.registrobeni.service.impl;

import it.pagopa.pari.generated.openapi.clients.registro.beni.ApiClient;
import it.pagopa.pari.generated.openapi.clients.registro.beni.api.InstitutionsApi;
import it.pagopa.pari.generated.openapi.clients.registro.beni.api.PortalConsentApi;
import it.pagopa.pari.generated.openapi.clients.registro.beni.api.ProductsApi;
import it.pagopa.pari.generated.openapi.clients.registro.beni.api.ProductsUploadApi;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.CsvDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.InstitutionResponse;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.InstitutionsResponse;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.PortalConsentDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductListDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductStatus;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductsUpdateDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.RegisterUploadResponseDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.UpdateResponseDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.UploadsListDTO;
import it.pagopa.pari.registrobeni.domain.RdbRole;
import it.pagopa.pari.utils.RdBJWTProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RegisterPortalOperationClientImpl {
    private final RestTemplate restTemplate;
    private final RdBJWTProvider rdBJWTProvider;
    private final PortalConsentApi portalConsentApi;
    private final ProductsUploadApi productsUploadApi;
    private final ProductsApi productsApi;
    private final InstitutionsApi institutionsApi;
    private final String basePath;

    public RegisterPortalOperationClientImpl(RestTemplate restTemplate,
                                             RdBJWTProvider rdBJWTProvider,
                                             @Value("${rdb.base-url}") String basePath) {
        this.restTemplate = restTemplate;
        this.rdBJWTProvider = rdBJWTProvider;
        this.basePath = basePath;
        portalConsentApi = new PortalConsentApi(createApiClient("dummy"));
        productsUploadApi = new ProductsUploadApi(createApiClient("dummy"));
        productsApi = new ProductsApi(createApiClient("dummy"));
        institutionsApi = new InstitutionsApi(createApiClient("dummy"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return apiClient;
    }

    public PortalConsentDTO getConsent() {
        return portalConsentApi.getPortalConsent();
    }

    public void savePortalConsent(PortalConsentDTO portalConsentDTO) {
        portalConsentApi.savePortalConsent(portalConsentDTO);
    }

    public CsvDTO downloadErrorReport(String productFileId) {
        return productsUploadApi.downloadErrorReport(productFileId);
    }

    public List<Object> getBatchNameList(String xOrganizationSelected) {
        return productsUploadApi.getBatchNameList(xOrganizationSelected);
    }

    public UploadsListDTO getProductFilesList(Integer page, Integer size) {
        return productsUploadApi.getProductFilesList(page, size);
    }

    public RegisterUploadResponseDTO uploadProductList(org.springframework.core.io.Resource csv, String category) {
        return productsUploadApi.uploadProductList(csv, category);
    }

    public RegisterUploadResponseDTO verifyProductList(org.springframework.core.io.Resource csv, String category) {
        return productsUploadApi.verifyProductList(csv, category);
    }

    public ProductListDTO getProducts(Integer page, Integer size, String sort, String category, String brand, String model, String eprelCode, String gtinCode, String productFileId, String productName, String fullProductName, ProductStatus status, String organizationId) {
        return productsApi.getProducts(page, size, sort, category, brand, model, eprelCode, gtinCode, productFileId, productName, fullProductName, status, organizationId);
    }

    public InstitutionsResponse getInstitutionsList() {
        return institutionsApi.getInstitutionsList();
    }

    public InstitutionResponse retrieveInstitutionById(String institutionId) {
        return institutionsApi.retrieveInstitutionById(institutionId);
    }

    public UpdateResponseDTO updateProductStatusApproved(ProductsUpdateDTO productsUpdateDTO) {
        return productsApi.updateProductStatusApproved(productsUpdateDTO);
    }

    public UpdateResponseDTO updateProductStatusRejected(ProductsUpdateDTO productsUpdateDTO) {
        return productsApi.updateProductStatusRejected(productsUpdateDTO);
    }

    public UpdateResponseDTO updateProductStatusSupervised(ProductsUpdateDTO productsUpdateDTO) {
        return productsApi.updateProductStatusSupervised(productsUpdateDTO);
    }

    public UpdateResponseDTO updateProductStatusWaitApproved(ProductsUpdateDTO productsUpdateDTO) {
        return productsApi.updateProductStatusWaitApproved(productsUpdateDTO);
    }

    public UpdateResponseDTO updateProductStatusRestored(ProductsUpdateDTO productsUpdateDTO) {
        return productsApi.updateProductStatusRestored(productsUpdateDTO);
    }

    public void setBearerToken(RdbRole role) {
        String bearerToken = rdBJWTProvider.provideJWT(role);
        portalConsentApi.setApiClient(createApiClient(bearerToken));
        productsUploadApi.setApiClient(createApiClient(bearerToken));
        productsApi.setApiClient(createApiClient(bearerToken));
        institutionsApi.setApiClient(createApiClient(bearerToken));
    }
}
