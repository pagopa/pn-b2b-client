package it.pagopa.pn.cucumber.steps.gestioneCosti.domain;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import it.pagopa.pn.cucumber.steps.gestioneCosti.converter.ShipmentCalculateRequestConverter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateRequestParameter {
    @CsvBindByPosition(position = 0)
    private String geokey;
    @CsvCustomBindByPosition(position = 1, converter = ShipmentCalculateRequestConverter.class)
    private ShipmentCalculateRequest.ProductEnum product;
    @CsvBindByPosition(position = 2)
    private String tenderId;
    @CsvBindByPosition(position = 3)
    private Integer pageWeight;
    @CsvBindByPosition(position = 4)
    private Integer pageNumber;
    @CsvBindByPosition(position = 5)
    private Integer numSides;
    @CsvBindByPosition(position = 6)
    private Boolean isReversePrinter;
    @CsvBindByPosition(position = 7)
    private String cost;
    @CsvBindByPosition(position = 8)
    private String costPlusEuroDigital;
    @CsvBindByPosition(position = 9)
    private Integer expectedResult;

}
