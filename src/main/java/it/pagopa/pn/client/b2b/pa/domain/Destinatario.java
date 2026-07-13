package it.pagopa.pn.client.b2b.pa.domain;

import lombok.Getter;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.*;

@Getter
public class Destinatario {

    private final String denomination;
    private final String taxId;
    private final String uid;
    private final String recipientType;
    private final String digitalDomicileType;
    private final boolean useB2BApi;

    public Destinatario(String denomination, String taxId, String uid,
                        String recipientType, String digitalDomicileType) {
        this.denomination = denomination;
        this.taxId = taxId;
        this.uid = uid;
        this.recipientType = recipientType;
        this.digitalDomicileType = digitalDomicileType;
        this.useB2BApi = false;
    }

    public Destinatario(String denomination, String taxId, String uid,
                        String recipientType, String digitalDomicileType, boolean useB2BApi) {
        this.denomination = denomination;
        this.taxId = taxId;
        this.uid = uid;
        this.recipientType = recipientType;
        this.digitalDomicileType = digitalDomicileType;
        this.useB2BApi = useB2BApi;
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
