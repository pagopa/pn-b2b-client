package it.pagopa.pn.cucumber.steps.dataTable;

import io.cucumber.java.DataTableType;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.Address;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.CreateRegistryRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.GeoLocation;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.UpdateRegistryRequest;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.pagopa.pn.cucumber.utils.RaddAltValue.*;


public class DataTableTypeRaddAlt {

    @Autowired
    private PnPaB2bUtils utils;

    @DataTableType
    public synchronized CreateRegistryRequest convertRegistryRequestData(Map<String, String> data){
        CreateRegistryRequest sportelloRadd= new CreateRegistryRequest()
                .address(getValue(data,ADDRESS_RADD.key)==null?null:
                        new Address()
                                .addressRow(getValue(data,ADDRESS_RADD_ROW.key)==null? null:
                                        getValue(data,ADDRESS_RADD_ROW.key).equalsIgnoreCase("random")? generateRandomNumber() : getValue(data,ADDRESS_RADD_ROW.key))
                                .cap(getValue(data,ADDRESS_RADD_CAP.key)==null? null: getValue(data,ADDRESS_RADD_CAP.key))
                                .city(getValue(data,ADDRESS_RADD_CITY.key)==null? null: getValue(data,ADDRESS_RADD_CITY.key))
                                .pr(getValue(data,ADDRESS_RADD_PROVINCE.key)==null? null: getValue(data,ADDRESS_RADD_PROVINCE.key))
                                .country(getValue(data,ADDRESS_RADD_COUNTRY.key)==null? null: getValue(data,ADDRESS_RADD_COUNTRY.key)))
                .description(getValue(data,RADD_DESCRIPTION.key)==null? null: getValue(data,RADD_DESCRIPTION.key))
                .phoneNumber(getValue(data,RADD_PHONE_NUMBER.key)==null? null:getValue(data,RADD_PHONE_NUMBER.key))
                .geoLocation(getValue(data,RADD_GEO_LOCATION.key)==null? null: new GeoLocation()
                        .latitude(getValue(data,RADD_GEO_LOCATION_LATITUDINE.key)==null? null:getValue(data,RADD_GEO_LOCATION_LATITUDINE.key))
                        .longitude(getValue(data,RADD_GEO_LOCATION_LONGITUDINE.key)==null? null:getValue(data,RADD_GEO_LOCATION_LONGITUDINE.key)))
                .openingTime(getValue(data,RADD_OPENING_TIME.key)==null? null:getValue(data,RADD_OPENING_TIME.key))
                .startValidity(getValue(data,RADD_START_VALIDITY.key)==null? null:setData(getValue(data,RADD_START_VALIDITY.key)))
                .endValidity(getValue(data,RADD_END_VALIDITY.key)==null? null:setData(getValue(data,RADD_END_VALIDITY.key)))
                .externalCode(getValue(data, RADD_EXTERNAL_CODE.key)==null?null:getValue(data, RADD_EXTERNAL_CODE.key))
                .capacity(getValue(data, RADD_CAPACITY.key)==null?null:getValue(data, RADD_CAPACITY.key));

        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sportelloRadd;
    }



    public synchronized List<CreateRegistryRequest> convertToListRegistryRequestData(List<Map<String, String>> dataCsv) {

        List<CreateRegistryRequest> listaSportelli=new ArrayList<>();

        for (Map<String, String>data:dataCsv) {

        CreateRegistryRequest sportelloRadd= new CreateRegistryRequest()
                .address(getValue(data,ADDRESS_RADD.key)==null?null:
                        new Address()
                                .addressRow(getValue(data,ADDRESS_RADD_ROW.key)==null? null:
                                        getValue(data,ADDRESS_RADD_ROW.key).equalsIgnoreCase("random")? generateRandomNumber() : getValue(data,ADDRESS_RADD_ROW.key))
                                .cap(getValue(data,ADDRESS_RADD_CAP.key)==null? null: getValue(data,ADDRESS_RADD_CAP.key))
                                .city(getValue(data,ADDRESS_RADD_CITY.key)==null? null: getValue(data,ADDRESS_RADD_CITY.key))
                                .pr(getValue(data,ADDRESS_RADD_PROVINCE.key)==null? null: getValue(data,ADDRESS_RADD_PROVINCE.key))
                                .country(getValue(data,ADDRESS_RADD_COUNTRY.key)==null? null: getValue(data,ADDRESS_RADD_COUNTRY.key)))
                .description(getValue(data,RADD_DESCRIPTION.key)==null? null: getValue(data,RADD_DESCRIPTION.key))
                .phoneNumber(getValue(data,RADD_PHONE_NUMBER.key)==null? null:getValue(data,RADD_PHONE_NUMBER.key))
                .geoLocation(getValue(data,RADD_GEO_LOCATION.key)==null? null: new GeoLocation()
                        .latitude(getValue(data,RADD_GEO_LOCATION_LATITUDINE.key)==null? null:getValue(data,RADD_GEO_LOCATION_LATITUDINE.key))
                        .longitude(getValue(data,RADD_GEO_LOCATION_LONGITUDINE.key)==null? null:getValue(data,RADD_GEO_LOCATION_LONGITUDINE.key)))
                .openingTime(getValue(data,RADD_OPENING_TIME.key)==null? null:getValue(data,RADD_OPENING_TIME.key))
                .startValidity(getValue(data,RADD_START_VALIDITY.key)==null? null: setData(getValue(data,RADD_START_VALIDITY.key)))
                .endValidity(getValue(data,RADD_END_VALIDITY.key)==null? null:setData(getValue(data,RADD_END_VALIDITY.key)))
                .externalCode(getValue(data, RADD_EXTERNAL_CODE.key)==null?null:getValue(data, RADD_EXTERNAL_CODE.key))
                .capacity(getValue(data, RADD_CAPACITY.key)==null?null:getValue(data, RADD_CAPACITY.key));

        listaSportelli.add(sportelloRadd);

        }


        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return listaSportelli;
    }
    
    @DataTableType
    public synchronized UpdateRegistryRequest convertUpdateRegistryRequest(Map<String, String> data){
        UpdateRegistryRequest sportelloAggiornatoRadd= new UpdateRegistryRequest()
                .description(getValue(data,RADD_DESCRIPTION.key)==null? null: getValue(data,RADD_DESCRIPTION.key))
                .phoneNumber(getValue(data,RADD_PHONE_NUMBER.key)==null? null:getValue(data,RADD_PHONE_NUMBER.key))
                .openingTime(getValue(data,RADD_OPENING_TIME.key)==null? null:getValue(data,RADD_OPENING_TIME.key));

        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sportelloAggiornatoRadd;
    }

    public String setData(String data) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String dataNumber = data.replaceAll("[\\\\+|g|\\\\-]", "");

        String dataString = data;

        if (data.toLowerCase().contains("+")) {
            dataString = dateTimeFormatter.format(OffsetDateTime.now().plusDays(Long.parseLong(dataNumber)));
        } else if (data.toLowerCase().contains("-")) {
            dataString = dateTimeFormatter.format(OffsetDateTime.now().minusDays(Long.parseLong(dataNumber)));
        } else if (data.equalsIgnoreCase("now")) {
            dataString = dateTimeFormatter.format(OffsetDateTime.now());
        } else if (data.equalsIgnoreCase("formato errato")) {
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            dataString = dateTimeFormatter.format(OffsetDateTime.now());
        }


        return dataString;
    }
}