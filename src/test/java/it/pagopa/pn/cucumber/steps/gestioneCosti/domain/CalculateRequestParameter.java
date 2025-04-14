package it.pagopa.pn.cucumber.steps.gestioneCosti.domain;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import io.cucumber.java.DataTableType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import it.pagopa.pn.cucumber.steps.gestioneCosti.converter.ShipmentCalculateRequestConverter;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static it.pagopa.pn.cucumber.utils.NotificationValue.getValue;
import static java.util.Optional.ofNullable;

@Getter
@Setter
public class CalculateRequestParameter {
    @CsvBindByPosition(position = 0)
    private String geokey;
    @CsvCustomBindByPosition(position = 1, converter = ShipmentCalculateRequestConverter.class)
    private ShipmentCalculateRequest.ProductEnum product;
    @CsvBindByPosition(position = 4)
    private Integer pageWeight;
    @CsvBindByPosition(position = 5)
    private Integer pageNumber;
    @CsvBindByPosition(position = 6)
    private Integer numSides;
    @CsvBindByPosition(position = 7)
    private Boolean isReversePrinter;
    @CsvBindByPosition(position = 8)
    private String cost;
    @CsvBindByPosition(position = 9)
    private String costPlusEuroDigital;
    @CsvBindByPosition(position = 10)
    private Integer expectedResult;

    @DataTableType
    public synchronized CalculateRequestParameter convertShipmentCalculateRequestElement(Map<String, String> data) {
        CalculateRequestParameter requestParameter = new CalculateRequestParameter();
        requestParameter.setGeokey(getValue(data, "geokey"));
        requestParameter.setProduct(ofNullable(getValue(data, "product")).map(ShipmentCalculateRequest.ProductEnum::fromValue).orElse(null));
        requestParameter.setNumSides(ofNullable(getValue(data, "numSides")).map(Integer::valueOf).orElse(null));
        requestParameter.setIsReversePrinter(ofNullable(getValue(data, "isReversePrinter")).map(Boolean::valueOf).orElse(null));
        requestParameter.setPageWeight(ofNullable(getValue(data, "pageWeight")).map(Integer::valueOf).orElse(null));
        return requestParameter;
    }
}
