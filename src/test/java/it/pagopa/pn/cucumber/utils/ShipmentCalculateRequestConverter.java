package it.pagopa.pn.cucumber.utils;

import com.opencsv.bean.AbstractBeanField;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;

public class ShipmentCalculateRequestConverter extends AbstractBeanField<ShipmentCalculateRequest.ProductEnum, String> {

    @Override
    protected Object convert(String value) {
        return ShipmentCalculateRequest.ProductEnum.fromValue(value);
    }

}
