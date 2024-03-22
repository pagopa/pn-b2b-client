package it.pagopa.pn.client.b2b.pa.polling.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
public class PnPollingMapper {
    private final ModelMapper modelMapper;

    public PnPollingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}