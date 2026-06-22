package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "pn.recipient")
public class RecipientConfig {
    private RecipientData marioCucumber = new RecipientData();
    private RecipientData marioGherkin = new RecipientData();
    private RecipientData cucumberSrl = new RecipientData();
    private RecipientData cucumberSpa = new RecipientData();
    private RecipientData gherkinSrl = new RecipientData();
    private RecipientData gherkinSpa = new RecipientData();
    private RecipientData cucumberAnalogic = new RecipientData();
    private RecipientData gherkinAnalogic = new RecipientData();
    private RecipientData cucumberSociety = new RecipientData();
    private RecipientData gherkinIrreperibile = new RecipientData();
    private RecipientData leonardoDaVinci = new RecipientData();
    private RecipientData galileoGalilei = new RecipientData();
    private RecipientData userIndirizzoNonValidoD01 = new RecipientData();
    private RecipientData userIndirizzoValidoAnpr = new RecipientData();

    // mittenti (PA) — solo taxId, uid non applicabile
    private RecipientData comune1    = new RecipientData();
    private RecipientData comune2    = new RecipientData();
    private RecipientData comuneMulti = new RecipientData();
    private RecipientData comuneSon  = new RecipientData();
    private RecipientData comuneRoot = new RecipientData();

    @Getter
    @Setter
    public static class RecipientData {
        private String taxId;
        private String uid;
    }
}
