package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.cucumber.utils.FiscalCodeGenerator;
import lombok.Getter;

import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Costanti.*;

@Getter
public enum Destinatario {

    // Persone fisiche
    MARIO_GHERKIN(MARIOGHERKIN, MARIO_GHERKIN_TAX_ID, PF, PEC),
    MARIO_CUCUMBER(MARIOCUCUMBER, MARIO_CUCUMBER_TAX_ID, PF, PEC),
    SIGNOR_CASUALE("Signor RaddCasuale", FiscalCodeGenerator.generateCF(System.currentTimeMillis()), PF, PEC),
    CRISTOFORO_COLOMBO(CRISTOFOROCOLOMBO, MARIO_GHERKIN_TAX_ID, PF, null),
    LEONARDO_DA_VINCI(LEONARDODAVINCI, LEONARDO_DA_VINCI_TAX_ID, PF, null),
    // Spa
    GHERKIN_SPA(GHERKINSPA, GHERKIN_SPA_TAX_ID, PG, PEC),
    CUCUMBER_SPA(CUCUMBERSPA, CUCUMBER_SPA_TAX_ID, PG, PEC),
    // Srl
    GHERKIN_SRL(GHERKINSRL, GHERKIN_SRL_TAX_ID, PG, PEC),
    CUCUMBER_SRL(CUCUMBERSRL, CUCUMBER_SPA_TAX_ID, PG, PEC),
    // Analogiche
    GHERKIN_ANALOGIC(GHERKINANALOGIC, GHERKIN_ANALOGIC_TAX_ID, PG, PEC),
    CUCUMBER_ANALOGIC(CUCUMBERANALOGIC, CUCUMBER_ANALOGIC_TAX_ID, PG, PEC),
    // Altro
    GHERKIN_IRREPERIBILE(GHERKINIRREPERIBILE, GHERKIN_IRREPERIBILE_TAX_ID, PG, null),
    CUCUMBER_SOCIETY(CUCUMBERSOCIETY, CUCUMBER_SOCIETY_TAX_ID, PG, PEC),
    SIGNOR_GENERATO("Signor Generato", FiscalCodeGenerator.generateCF(System.currentTimeMillis()), PF, PEC),
    NESSUNO("Nessuno", null, null, null);//TODO MATTEO finire

    private final String denomination;
    private final String taxId;
    private final String recipientType;
    private final String digitalDomicileType;

    Destinatario(String denomination, String taxId, String recipientType, String digitalDomicileType) {
        this.denomination = denomination;
        this.taxId = taxId;
        this.recipientType = recipientType;
        this.digitalDomicileType = digitalDomicileType;
    }

    public static Destinatario getByName(String name) {
        for (Destinatario destinatario : values()) {
            if (destinatario.denomination.trim().equalsIgnoreCase(name)) {
                return destinatario;
            }
        }
        return null;
    }
}
