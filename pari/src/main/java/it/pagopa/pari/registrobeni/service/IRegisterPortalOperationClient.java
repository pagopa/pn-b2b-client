package it.pagopa.pari.registrobeni.service;

import it.pagopa.pari.generated.openapi.clients.registro.beni.model.CsvDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.InstitutionResponse;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.InstitutionsResponse;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.PortalConsentDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductListDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.RegisterUploadResponseDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.UploadsListDTO;

import java.util.List;

public interface IRegisterPortalOperationClient {
    PortalConsentDTO getConsent();
    void savePortalConsent(PortalConsentDTO portalConsentDTO);
    CsvDTO downloadErrorReport(String productFileId);
    List<Object> getBatchNameList(String xOrganizationSelected);
    UploadsListDTO getProductFilesList(Integer page, Integer size, String sort);
    RegisterUploadResponseDTO uploadProductList(String category, org.springframework.core.io.Resource csv);
    RegisterUploadResponseDTO verifyProductList(String category, org.springframework.core.io.Resource csv);
    ProductListDTO getProducts(String xOrganizationSelected, Integer page, Integer size, String sort, String category, String eprelCode, String gtinCode, String productCode, String productFileId);
    InstitutionsResponse getInstitutionsList();
    InstitutionResponse retrieveInstitutionById(String institutionId);
}
