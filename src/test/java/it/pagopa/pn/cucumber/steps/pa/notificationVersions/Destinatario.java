package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.cucumber.utils.FiscalCodeGenerator;
import lombok.Getter;

import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.DestinatariUtils.*;

@Getter
public enum Destinatario {

    // Persone fisiche
    MARIO_GHERKIN("Mario Gherkin", MARIO_GHERKIN_TAX_ID, PF, PEC),
    MARIO_CUCUMBER("Mario Cucumber", MARIO_CUCUMBER_TAX_ID, PF, PEC),
    SIGNOR_CASUALE("Signor RaddCasuale", FiscalCodeGenerator.generateCF(System.currentTimeMillis()), PF, PEC),
    CRISTOFORO_COLOMBO("Cristoforo Colombo", MARIO_GHERKIN_TAX_ID, PF, null),
    // Spa
    GHERKIN_SPA("GherkinSpa", GHERKIN_SPA_TAX_ID, PG, PEC),
    CUCUMBER_SPA("CucumberSpa", CUCUMBER_SPA_TAX_ID, PG, PEC),
    // Srl
    GHERKIN_SRL("GherkinSrl", GHERKIN_SRL_TAX_ID, PG, PEC),
    CUCUMBER_SRL("CucumberSrl", CUCUMBER_SPA_TAX_ID, PG, PEC),
    // Analogiche
    GHERKIN_ANALOGIC("Gherkin Analogic", GHERKIN_ANALOGIC_TAX_ID, PG, PEC),
    CUCUMBER_ANALOGIC("Cucumber Analogic", CUCUMBER_ANALOGIC_TAX_ID, PG, PEC),
    // Altro
    GHERKIN_IRREPERIBILE("Gherkin Irreperibile", GHERKIN_IRREPERIBILE_TAX_ID, PG, null),
    CUCUMBER_SOCIETY("Cucumber Society", CUCUMBER_SOCIETY_TAX_ID, PG, PEC),
    SIGNOR_GENERATO("TODO MATTEO", null, null, null),
    NESSUNO("Nessuno", null, null, null);

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
        throw new IllegalArgumentException("Destinatario inesistente: " + name);
    }
}
