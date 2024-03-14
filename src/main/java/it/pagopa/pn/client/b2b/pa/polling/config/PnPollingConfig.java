package it.pagopa.pn.client.b2b.pa.polling.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PnPollingConfig {

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }
}