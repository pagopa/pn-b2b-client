package it.pagopa.pari.merchant.service;

import it.pagopa.pari.generated.openapi.clients.merchant.root.api.PointOfSalesApi;
import it.pagopa.pari.generated.openapi.clients.merchant.root.model.ListPointOfSaleDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.api.PortalConsentApi;

public interface IMerchantOperationClient {

    ListPointOfSaleDTO getPointOfSales(String merchantId, String type, String city, String address, String contactName, Integer page, Integer size, String sort);

}
