package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.cucumber.utils.FiscalCodeGenerator;
import lombok.Getter;

import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Costanti.*;

@Getter
public enum Destinatario {

    // Persone fisiche
    DESTINATARIO_MARIO_GHERKIN(MARIO_GHERKIN, MARIO_GHERKIN_TAX_ID, PF, PEC),
    DESTINATARIO_MARIO_CUCUMBER(MARIO_CUCUMBER, MARIO_CUCUMBER_TAX_ID, PF, PEC),
    DESTINATARIO_SIGNOR_CASUALE(SIGNOR_CASUALE, FiscalCodeGenerator.generateCF(System.currentTimeMillis()), PF, PEC),
    DESTINATARIO_CRISTOFORO_COLOMBO(CRISTOFORO_COLOMBO, MARIO_GHERKIN_TAX_ID, PF, null),
    DESTINATARIO_LEONARDO_DA_VINCI(LEONARDO_DA_VINCI, LEONARDO_DA_VINCI_TAX_ID, PF, null),
    // Spa
    DESTINATARIO_GHERKIN_SPA(GHERKIN_SPA, GHERKIN_SPA_TAX_ID, PG, PEC),
    DESTINATARIO_CUCUMBER_SPA(CUCUMBER_SPA, CUCUMBER_SPA_TAX_ID, PG, PEC),
    // Srl
    DESTINATARIO_GHERKIN_SRL(GHERKIN_SRL, GHERKIN_SRL_TAX_ID, PG, PEC),
    DESTINATARIO_CUCUMBER_SRL(CUCUMBER_SRL, CUCUMBER_SPA_TAX_ID, PG, PEC),
    // Analogiche
    DESTINATARIO_GHERKIN_ANALOGIC(GHERKIN_ANALOGIC, GHERKIN_ANALOGIC_TAX_ID, PG, PEC),
    DESTINATARIO_CUCUMBER_ANALOGIC(CUCUMBER_ANALOGIC, CUCUMBER_ANALOGIC_TAX_ID, PF, PEC),
    // Altro
    DESTINATARIO_GHERKIN_IRREPERIBILE(GHERKIN_IRREPERIBILE, GHERKIN_IRREPERIBILE_TAX_ID, PG, null),
    DESTINATARIO_CUCUMBER_SOCIETY(CUCUMBER_SOCIETY, CUCUMBER_SOCIETY_TAX_ID, PG, PEC),
    DESTINATARIO_SIGNOR_GENERATO(SIGNOR_GENERATO, FiscalCodeGenerator.generateCF(System.currentTimeMillis()), PF, PEC),
    DESTINATARIO_NESSUNO(NESSUNO, null, null, null);

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
