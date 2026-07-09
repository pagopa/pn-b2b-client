package it.pagopa.pn.cucumber.steps.utilitySteps;

import io.cucumber.java.ParameterType;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.cucumber.utils.FiscalCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.*;

public class DestinatarioRegistry {

    public final Destinatario DESTINATARIO_MARIO_GHERKIN;
    public final Destinatario DESTINATARIO_MARIO_CUCUMBER;
    public final Destinatario DESTINATARIO_SIGNOR_CASUALE;
    public final Destinatario DESTINATARIO_CRISTOFORO_COLOMBO;
    public final Destinatario DESTINATARIO_ETTORE_FIERAMOSCA;
    public final Destinatario DESTINATARIO_LEONARDO_DA_VINCI;
    public final Destinatario DESTINATARIO_GALILEO_GALILEI;
    public final Destinatario DESTINATARIO_ALDA_MERINI;
    public final Destinatario DESTINATARIO_DINO_SAURO;
    public final Destinatario DESTINATARIO_GHERKIN_SPA;
    public final Destinatario DESTINATARIO_CUCUMBER_SPA;
    public final Destinatario DESTINATARIO_GHERKIN_SRL;
    public final Destinatario DESTINATARIO_CUCUMBER_SRL;
    public final Destinatario DESTINATARIO_GHERKIN_ANALOGIC;
    public final Destinatario DESTINATARIO_CUCUMBER_ANALOGIC;
    public final Destinatario DESTINATARIO_GHERKIN_IRREPERIBILE;
    public final Destinatario DESTINATARIO_CUCUMBER_SOCIETY;
    public final Destinatario DESTINATARIO_SIGNOR_GENERATO;
    public final Destinatario DESTINATARIO_NESSUNO;
    public final Destinatario DESTINATARIO_ERRORE_D01;
    public final Destinatario DESTINATARIO_INDIRIZZO_VALIDO_ANPR;

    private final List<Destinatario> all;
    private final RecipientConfig taxIds;

    @Autowired
    public DestinatarioRegistry(RecipientConfig recipients) {
        this.taxIds = recipients;

        DESTINATARIO_MARIO_GHERKIN = new Destinatario(
                MARIO_GHERKIN, recipients.getMarioGherkin().getTaxId(),
                recipients.getMarioGherkin().getUid(), PF, PEC);

        DESTINATARIO_MARIO_CUCUMBER = new Destinatario(
                MARIO_CUCUMBER, recipients.getMarioCucumber().getTaxId(),
                recipients.getMarioCucumber().getUid(), PF, PEC);

        DESTINATARIO_SIGNOR_CASUALE = new Destinatario(SIGNOR_CASUALE, null, null, PF, PEC);

        DESTINATARIO_CRISTOFORO_COLOMBO = new Destinatario(
                CRISTOFORO_COLOMBO, recipients.getMarioGherkin().getTaxId(),
                recipients.getMarioGherkin().getUid(), PF, PEC);

        DESTINATARIO_ETTORE_FIERAMOSCA = new Destinatario(
                ETTORE_FIERAMOSCA, recipients.getMarioCucumber().getTaxId(),
                recipients.getMarioCucumber().getUid(), PF, PEC);

        DESTINATARIO_LEONARDO_DA_VINCI = new Destinatario(
                LEONARDO_DA_VINCI, recipients.getLeonardoDaVinci().getTaxId(),
                recipients.getLeonardoDaVinci().getUid(), PF, PEC);

        DESTINATARIO_GALILEO_GALILEI = new Destinatario(
                GALILEO_GALILEI, recipients.getGalileoGalilei().getTaxId(),
                recipients.getGalileoGalilei().getUid(), PF, PEC);

        DESTINATARIO_ALDA_MERINI = new Destinatario(
                ALDA_MERINI, recipients.getAldaMerini().getTaxId(),
                recipients.getAldaMerini().getUid(), PG, PEC);

        DESTINATARIO_DINO_SAURO = new Destinatario(
                DINO_SAURO, recipients.getDinoSauro().getTaxId(),
                recipients.getDinoSauro().getUid(), PF, PEC);

        DESTINATARIO_GHERKIN_SPA = new Destinatario(
                GHERKIN_SPA, recipients.getGherkinSpa().getTaxId(),
                recipients.getGherkinSpa().getUid(), PG, PEC);

        DESTINATARIO_CUCUMBER_SPA = new Destinatario(
                CUCUMBER_SPA, recipients.getCucumberSpa().getTaxId(),
                recipients.getCucumberSpa().getUid(), PG, PEC);

        DESTINATARIO_GHERKIN_SRL = new Destinatario(
                GHERKIN_SRL, recipients.getGherkinSrl().getTaxId(),
                recipients.getGherkinSrl().getUid(), PG, PEC);

        DESTINATARIO_CUCUMBER_SRL = new Destinatario(
                CUCUMBER_SRL, recipients.getCucumberSrl().getTaxId(),
                recipients.getCucumberSrl().getUid(), PG, PEC);

        DESTINATARIO_GHERKIN_ANALOGIC = new Destinatario(
                GHERKIN_ANALOGIC, recipients.getGherkinAnalogic().getTaxId(),
                recipients.getGherkinAnalogic().getUid(), PG, PEC);

        DESTINATARIO_CUCUMBER_ANALOGIC = new Destinatario(
                CUCUMBER_ANALOGIC, recipients.getCucumberAnalogic().getTaxId(),
                recipients.getCucumberAnalogic().getUid(), PF, PEC);

        DESTINATARIO_GHERKIN_IRREPERIBILE = new Destinatario(
                GHERKIN_IRREPERIBILE, recipients.getGherkinIrreperibile().getTaxId(),
                recipients.getGherkinIrreperibile().getUid(), PG, PEC);

        DESTINATARIO_CUCUMBER_SOCIETY = new Destinatario(
                CUCUMBER_SOCIETY, recipients.getCucumberSociety().getTaxId(),
                recipients.getCucumberSociety().getUid(), PG, PEC);

        DESTINATARIO_SIGNOR_GENERATO = new Destinatario(
                SIGNOR_GENERATO, FiscalCodeGenerator.generateCF(System.nanoTime()),
                null, PF, PEC);

        DESTINATARIO_NESSUNO = new Destinatario(NESSUNO, null, null, null, null);

        DESTINATARIO_ERRORE_D01 = new Destinatario(
                UTENZA_CON_INDIRIZZO_NON_VALIDO, recipients.getUserIndirizzoNonValidoD01().getTaxId(),
                recipients.getUserIndirizzoNonValidoD01().getUid(), PF, PEC);
        DESTINATARIO_INDIRIZZO_VALIDO_ANPR = new Destinatario(UTENZA_CON_INDIRIZZO_VALIDO_ANPR, recipients.getUserIndirizzoValidoAnpr().getTaxId(),
                recipients.getUserIndirizzoValidoAnpr().getUid(), PF, PEC);

        all = List.of(
                DESTINATARIO_MARIO_GHERKIN, DESTINATARIO_MARIO_CUCUMBER, DESTINATARIO_SIGNOR_CASUALE,
                DESTINATARIO_CRISTOFORO_COLOMBO, DESTINATARIO_ETTORE_FIERAMOSCA, DESTINATARIO_LEONARDO_DA_VINCI,
                DESTINATARIO_GALILEO_GALILEI, DESTINATARIO_GHERKIN_SPA, DESTINATARIO_CUCUMBER_SPA,
                DESTINATARIO_GHERKIN_SRL, DESTINATARIO_CUCUMBER_SRL, DESTINATARIO_GHERKIN_ANALOGIC,
                DESTINATARIO_CUCUMBER_ANALOGIC, DESTINATARIO_GHERKIN_IRREPERIBILE, DESTINATARIO_CUCUMBER_SOCIETY,
                DESTINATARIO_SIGNOR_GENERATO, DESTINATARIO_NESSUNO, DESTINATARIO_ERRORE_D01, DESTINATARIO_INDIRIZZO_VALIDO_ANPR,
                DESTINATARIO_ALDA_MERINI, DESTINATARIO_DINO_SAURO
        );
    }

    public String getSenderTaxIdFromProperties(String paName) {
        return switch (paName) {
            case COMUNE_1 -> taxIds.getComune1().getTaxId();
            case COMUNE_2 -> taxIds.getComune2().getTaxId();
            case COMUNE_MULTI -> taxIds.getComuneMulti().getTaxId();
            case COMUNE_SON -> taxIds.getComuneSon().getTaxId();
            case COMUNE_ROOT -> taxIds.getComuneRoot().getTaxId();
            default -> throw new IllegalArgumentException("PA non riconosciuta: " + paName);
        };
    }

    @ParameterType(MARIO_GHERKIN + "|" +
            MARIO_CUCUMBER + "|" +
            SIGNOR_CASUALE + "|" +
            ETTORE_FIERAMOSCA + "|" +
            CRISTOFORO_COLOMBO + "|" +
            LEONARDO_DA_VINCI + "|" +
            GHERKIN_SPA + "|" +
            CUCUMBER_SPA + "|" +
            GHERKIN_SRL + "|" +
            CUCUMBER_SRL + "|" +
            GHERKIN_ANALOGIC + "|" +
            CUCUMBER_ANALOGIC + "|" +
            GHERKIN_IRREPERIBILE + "|" +
            CUCUMBER_SOCIETY + "|" +
            SIGNOR_GENERATO + "|" +
            GALILEO_GALILEI + "|" +
            NESSUNO + "|" +
            UTENZA_CON_INDIRIZZO_NON_VALIDO + "|" +
            UTENZA_CON_INDIRIZZO_VALIDO_ANPR + "|" +
            ALDA_MERINI + "|" +
            DINO_SAURO
    )
    public Destinatario destinatario(String name) {
        return all.stream()
                .filter(d -> d.getDenomination() != null && d.getDenomination().trim().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}