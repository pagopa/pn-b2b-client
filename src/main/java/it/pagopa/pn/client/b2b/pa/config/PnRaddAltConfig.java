package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PnRaddAltConfig {

    @Value("${pn.iun.120gg.fieramosca}")
    private String iunFieramosca120gg;
    @Value("${pn.iun.120gg.lucio}")
    private String iunLucio120gg;
    @Value("${pn.radd.alt.external.max-print-request}")
    private int maxPrintRequest;
}
