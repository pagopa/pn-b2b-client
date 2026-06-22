package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "pn.tax-id")
public class TaxIdConfig {
    private String marioCucumber;
    private String marioGherkin;
    private String cucumberSrl;
    private String cucumberSpa;
    private String gherkinSrl;
    private String gherkinSpa;
    private String cucumberAnalogic;
    private String gherkinAnalogic;
    private String cucumberSociety;
    private String gherkinIrreperibile;
    private String leonardoDaVinci;
    private String galileoGalilei;
    private String comune1;
    private String comune2;
    private String comuneMulti;
    private String comuneSon;
    private String comuneRoot;
    private String userIndirizzoNonValidoD01;
    private String userIndirizzoValidoAnpr;
}
