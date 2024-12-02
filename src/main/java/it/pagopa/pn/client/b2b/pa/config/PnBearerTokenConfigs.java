package it.pagopa.pn.client.b2b.pa.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pn.bearer-token", ignoreUnknownFields = true)
@Data
public class PnBearerTokenConfigs {

    private String user1;       // Mario Cucumber token
    private String user2;       // Mario Gerkin token
    private String user3;       // Leonardo token
    private String user4;       // Galileo Galilei token
    private String user5;       // Dino Sauro token
    private String pg1;         // Gerkin Srl token
    private String pg2;         // Cucumber Spa token
    private String pg3;         // Alda Merini PG Token,
    private String pg4;         // Maria Montessori PG token
    private String pg5;         // Nilde Iotti PG token
    private String scaduto;     // Expired token
    private String payinfo;
    @Value("${pn.bearer-token-b2b.pg2}")
    private String b2bPg2;
    @Value("${pn.external.bearer-token-pg1.id}")
    private String idOrganizationGherkinSrl;
    @Value("${pn.external.bearer-token-pg2.id}")
    private String idOrganizationCucumberSpa;
    @Value("${pn.bearer-token.user1.taxID}")
    private String user1TaxID;
    @Value("${pn.bearer-token.user2.taxID}")
    private String user2TaxID;
}