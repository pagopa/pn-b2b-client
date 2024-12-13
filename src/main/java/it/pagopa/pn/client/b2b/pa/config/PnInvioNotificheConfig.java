package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PnInvioNotificheConfig {

    @Value("${pn.retention.time.preload}")
    private Integer retentionTimePreload;
    @Value("${pn.retention.time.load}")
    private Integer retentionTimeLoad;
    @Value("${b2b.sender.mail}")
    private String senderMail;
}
