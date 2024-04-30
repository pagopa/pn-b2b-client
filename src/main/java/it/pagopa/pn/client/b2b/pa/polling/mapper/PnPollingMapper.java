package it.pagopa.pn.client.b2b.pa.polling.mapper;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Getter
@Component
public class PnPollingMapper {
    private final ModelMapper modelMapper;

    public PnPollingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}