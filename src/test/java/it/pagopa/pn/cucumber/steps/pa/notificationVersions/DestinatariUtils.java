package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Getter
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DestinatariUtils {

    // Nomi Utenti
    public static final String MARIOGERKIN = "Mario Gherkin";
    public static final String MARIOCUCUMBER = "Mario Cucumber";
    public static final String GHERKINSPA = "GherkinSpa";
    public static final String CUCUMBERSPA = "CucumberSpa";
    public static final String GHERKINSRL = "GherkinSrl";
    public static final String CUCUMBERSRL = "CucumberSrl";
    public static final String GHERKINANALOGIC = "Gherkin Analogic";
    public static final String CUCUMBERANALOGIC = "Cucumber Analogic";
    public static final String GHERKINIRREPERIBILE = "Gherkin Irreperibile";
    public static final String CUCUMBERSOCIETY = "Cucumber Society";
    public static final String CRISTOFOROCOLOMBO = "Cristoforo Colombo";
    public static final String ETTOREFIERAMOSCA = "Ettore Fieramosca";
    public static final String GALILEOGALILEI = "Galileo Galilei";
    public static final String LEONARDODAVINCI = "Leonardo Da Vinci";
    public static final String DINOSAURO = "Dino Sauro";
    public static final String LUCIOANNEOSENECA = "Lucio Anneo Seneca";
    public static final String SIGNORCASUALE = "Signor RaddCasuale";
    public static final String ALDAMERINI = "Alda Merini";

    //TODO MATTEO: capire come riuscire a prendere i valori annotati con value
    // (non so perchè me li dà null quando commento il valore e scommento il @Value)

    @Value("${pn.external.utilized.pec:testpagopa3@pec.pagopa.it}")
    public static String DIGITAL_ADDRESS;
    public static final String DEFAULT_DIGITAL_ADDRESS = "testpagopa3@pec.pagopa.it";
    //    @Value("${pn.bearer-token.user1.taxID}")
    public static String MARIO_CUCUMBER_TAX_ID = "FRMTTR76M06B715E";
    //    @Value("${pn.bearer-token.user2.taxID}")
    public static String MARIO_GHERKIN_TAX_ID = "CLMCST42R12D969Z";
    //    @Value("${pn.bearer-token.user4.taxID}")

    // Tax ID
    public static final String CUCUMBER_SRL_TAX_ID = "20517490320";
    public static final String GHERKIN_SRL_TAX_ID = "12666810299";
    public static final String CUCUMBER_SPA_TAX_ID = "20517490320";
    public static final String GHERKIN_SPA_TAX_ID = "12666810299";
    public static final String CUCUMBER_ANALOGIC_TAX_ID = "LBPHLS94A56C826R";
    public static final String GHERKIN_ANALOGIC_TAX_ID = "05722930657";
    public static final String CUCUMBER_SOCIETY_TAX_ID = "20517490320";
    public static final String GHERKIN_IRREPERIBILE_TAX_ID = "00749900049";
    public static String GALILEO_GALILEI_TAX_ID = "GLLGLL64B15G702I";

    // Tipologie destinatario
    public static final String PF = "PF";
    public static final String PG = "PG";
    // Tipologie indirizzo
    public static final String PEC = "PEC";

    public static String getDigitalAddressValue() {
        if (DIGITAL_ADDRESS == null || DIGITAL_ADDRESS.equalsIgnoreCase("${pn.external.digitalDomicile.address}"))
            return DEFAULT_DIGITAL_ADDRESS;
        return DIGITAL_ADDRESS;
    }


}
