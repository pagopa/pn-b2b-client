package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDigitalAddress;
import lombok.Getter;

import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.DestinatariUtils.MARIO_CUCUMBER_TAX_ID;
import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.DestinatariUtils.MARIO_GHERKIN_TAX_ID;

@Getter
public enum Destinatario {


    MARIO_GHERKIN("Mario Gherkin", MARIO_GHERKIN_TAX_ID, "PF", null),
    MARIO_CUCUMBER("Mario Cucumber", MARIO_CUCUMBER_TAX_ID, "PF", null);

    final String denomination;
    final String taxId;
    final String recipientType;
    final NotificationDigitalAddress digitalDomicile;

//    //    @Value("${pn.bearer-token.user1.taxID}")
//    public static final String MARIO_CUCUMBER_TAX_ID = "FRMTTR76M06B715E";
//    //    @Value("${pn.bearer-token.user2.taxID}")
//    public static final String MARIO_GHERKIN_TAX_ID = "CLMCST42R12D969Z";
//    //    @Value("${pn.bearer-token.user4.taxID}")
//    public static final String GALILEO_GALILEI_TAX_ID = "GLLGLL64B15G702I";
//    public static final String CUCUMBER_SRL_TAX_ID = "20517490320";
//    public static final String GHERKIN_SRL_TAX_ID = "12666810299";
//    public static final String CUCUMBER_SPA_TAX_ID = "20517490320";
//    public static final String GHERKIN_SPA_TAX_ID = "12666810299";
//    public static final String CUCUMBER_ANALOGIC_TAX_ID = "LBPHLS94A56C826R";
//    public static final String GHERKIN_ANALOGIC_TAX_ID = "05722930657";
//    public static final String CUCUMBER_SOCIETY_TAX_ID = "20517490320";
//    public static final String GHERKIN_IRREPERIBILE_TAX_ID = "00749900049";

    Destinatario(String name, String taxId, String recipientType, NotificationDigitalAddress digitalAddress) {
        this.denomination = name;
        this.taxId = taxId;
        this.recipientType = recipientType;
        this.digitalDomicile = digitalAddress;
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
