package it.pagopa.pn.cucumber.utils;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateRequestParameter {
    @CsvCustomBindByPosition(position = 1, converter = ShipmentCalculateRequestConverter.class)
    private ShipmentCalculateRequest.ProductEnum product;
    @CsvBindByPosition(position = 0)
    private String geokey;
    @CsvBindByPosition(position = 3)
    private Integer numPages;
    @CsvBindByPosition(position = 5)
    private Boolean isReversePrinter;
    @CsvBindByPosition(position = 2)
    private Integer weight;
    @CsvBindByPosition(position = 4)
    private String expectedResult;

}
