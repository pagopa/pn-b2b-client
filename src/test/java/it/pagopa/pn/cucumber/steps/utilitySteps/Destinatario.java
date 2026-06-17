package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Getter;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;

@Getter
public class Destinatario {

    private final String denomination;
    private final String taxId;
    private final String recipientType;
    private final String digitalDomicileType;

    public Destinatario(String denomination, String taxId, String recipientType, String digitalDomicileType) {
        this.denomination = denomination;
        this.taxId = taxId;
        this.recipientType = recipientType;
        this.digitalDomicileType = digitalDomicileType;
    }

    public boolean isNessuno() {
        return NESSUNO.equals(denomination);
    }

    public boolean isSignorCasuale() {
        return SIGNOR_CASUALE.equals(denomination);
    }

    public boolean isSignorGenerato() {
        return SIGNOR_GENERATO.equals(denomination);
    }
}
