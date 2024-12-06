package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PnAvanzamentoNotificheConfig {

    @Value("${pn.external.costo_base_notifica}")
    private Integer costoBaseNotifica;
    @Value("${pn.external.allowed.future.offset.duration}")
    private String offsetDuration;
    @Value("${pn.consolidatore.requestId}")
    private String consolidatoreRequestId;

}
