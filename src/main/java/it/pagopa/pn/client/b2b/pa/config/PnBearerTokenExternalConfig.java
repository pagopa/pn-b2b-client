package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pn.external.bearer-token-", ignoreUnknownFields = false)
@Data
public class PnBearerTokenExternalConfig {

    private String pa1;
    private String pa2;
    private String paSON;
    private String paROOT;
    private String paGA;
    private String radd1;
    private String radd2;
    private String radd3;
    private String nonCensito;
    private String datiErrati;
    private String jwtScaduto;
    private String kidDiverso;
    private String audErratto;
    private String privateKeyDiverso;
    private String over50KB;
}
